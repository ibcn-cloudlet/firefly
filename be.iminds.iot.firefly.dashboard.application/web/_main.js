/*******************************************************************************
 *  Copyright (c) 2015, Tim Verbelen
 *  Internet Based Communication Networks and Services research group (IBCN),
 *  Department of Information Technology (INTEC), Ghent University - iMinds.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of Ghent University - iMinds, nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

(function(){

	'use strict';
	
	function error(msg) {
		console.error(msg);
	}
	
	var MODULE = angular.module('be.iminds.iot.firefly.dashboard', 
			['ui.bootstrap',
			 'ngRoute',
			 'ngResource',
			 'enJsonrpc',
			 'enEasse',
			 'be.iminds.iot.repository',
			 'be.iminds.iot.rule.engine',
			 'be.iminds.iot.rule.templates',
			 'be.iminds.iot.rule.simple',
			 'be.iminds.iot.firefly.dashboard.actions']);
	
	MODULE.config(function($routeProvider, en$jsonrpcProvider) {
		en$jsonrpcProvider.setNotification({
			error : error
		});
	});
	
	// this allows filtering on map values in ng-repeat
	MODULE.filter('mapFilter', function($filter) {
		  var filter = $filter('filter');

		  return function(map, expression, comparator) {
		    if (! expression) return map;

		    var result = {};
		    angular.forEach(map, function(data, index) {
		      if (filter([data], expression, comparator).length)
		        result[index] = data;          
		    });

		    return result;
		  }
	});
	
	// introduce onLongclick directive, also offers onDoubleclick
	MODULE.directive('onLongclick', function($timeout) {
		return {
			restrict: 'A',
			link: function($scope, $elm, $attrs) {
				$elm.bind('mousedown', function(evt) {
					// Bookkeeping
					if($scope.single){
						$scope.double = true;
						$scope.single = false;
					} else {
						$scope.single = true;
					}
					$scope.long = true;
					
					// Timeout of 600 ms for long click
					$timeout(function() {
						if ($scope.long) {
							$scope.long = false;
							$scope.$apply(function() {
								$scope.$eval($attrs.onLongclick)
							});
						}
					}, 600);
					
					// Timeout of 200 ms for double click
					$timeout(function() {
						if(!$scope.long && $scope.single){
							$scope.$apply(function() {
								$scope.$eval($attrs.onClick)
							});
						}
						$scope.single = false;
					}, 200);
			
				});
	 
				$elm.bind('mouseup', function(evt) {
					if($scope.long){
						if($scope.double){
							$scope.double = false;
							$scope.$apply(function() {
								$scope.$eval($attrs.onDoubleclick)
							});
						}
					}
					$scope.long = false;
				});
			}
		};
	});
	
	
	MODULE.controller('ThingsCtrl', function ($rootScope, $scope, $modal, en$easse, en$jsonrpc, repository, actions) {
		// fill things map using repository REST endpoint
		$scope.things = {};
		$scope.locations = {};
		$scope.filters = {};
		
		repository.query(function(things){
			for(var i=0; i<things.length; i++){
				$scope.things[things[i].id] = things[i];
				$scope.locations[things[i].location] = things[i].location;
				
				// initialize highlight
				for(var name in things[i].state){
					if(name==='state' || Object.keys(things[i].state).length==1){
						$scope.things[things[i].id].highlight = $scope.formatState(things[i].state[name]);
					}
				}
			}
		});

		// listeners for events
		$scope.online = function(event) {
			repository.get({ id: event['thing.id'] }, function(thing) {
				if(angular.equals({}, thing)){
					thing.id = event['thing.id'];
					thing.name = event['thing.service'];
					thing.type = event['thing.type'];
					thing.gateway = event['thing.gateway'];
				} 
				$scope.things[thing.id] = thing;
				$scope.locations[thing.location] = thing.location;
				$scope.$apply();
			});
		};	
			
		$scope.offline = function(event) {
			delete $scope.things[event['thing.id']];
			$scope.$apply();
		};
		  
		$scope.change = function(event) {
			// TODO which state variable to show?
			if( $scope.things[event['thing.id']] != undefined){
				if($scope.things[event['thing.id']].state == null){
					$scope.things[event['thing.id']].state = {};
				}
			
				var name = event['state.variable'];
				var val = event['state.value'];
				$scope.things[event['thing.id']].state[name] = val;
				
				// set highlight to "state" variabele, or anything in case only 1 state element
				if(name==='state' || Object.keys($scope.things[event['thing.id']].state).length==1){
					$scope.things[event['thing.id']].highlight = $scope.formatState($scope.things[event['thing.id']].state[name]);
				}
				
				$scope.$apply();
			}
		};
			
		// easse callbacks
		en$easse.handle("be/iminds/iot/thing/online/*", $scope.online, error);
		en$easse.handle("be/iminds/iot/thing/offline/*", $scope.offline, error);
		en$easse.handle("be/iminds/iot/thing/change/*", $scope.change, error);

		// action callback
		$scope.action = function(id){
			actions.action(id, $scope.things[id].type);
		};
		
		$scope.update = function(id){
			$scope.updateThing($scope.things[id]);
		};
		  
		// thing details dialog
		$scope.dialog = function(id){
			var thing = $scope.things[id];
		
			var modalInstance = $modal.open({
				templateUrl: 'types/'+thing.type+'Content.html',
				controller: thing.type+'Ctrl',
				size: 'lg',
				resolve: {
					thing: function () {
						return thing;
					}
				}
			});
		};
		  
		// update thing dialog
		$scope.updateThing = function(thing){
			var modalInstance = $modal.open({
				templateUrl: 'thingDetails.html',
				controller: 'thingDetailsCtrl',
				resolve: {
					thing: function(){
						return thing;
					}
				}
			});
			modalInstance.result.then(function(thing){
				$scope.things[thing.id] = thing;
				$scope.locations[thing.location] = thing.location;
			});
		};
		
		// format state value
		$scope.formatState = function(val){
			var formatted;
			if(angular.isString(val)){
				formatted = val;
			} else if(angular.isNumber(val)) {
				if(val !== (val|0)) {
					// float, format nicely
					formatted = parseFloat(val).toFixed(2)+" "+val.unit;
				} else {
					// int
					formatted = val;
				}
			} else {
				// sensor value, format nicely
				formatted = parseFloat(val.value).toFixed(2)+" "+val.unit;
			}
			return formatted;
		}
		
		$scope.showRulesDialog = function(){
			var modalInstance = $modal.open({
				templateUrl: 'rules/rules.html',
				controller: 'rulesCtrl',
				size: 'lg',
				resolve: {
					things: function(){
						return $scope.things;
					}
				}
			});
		}
	});

})();