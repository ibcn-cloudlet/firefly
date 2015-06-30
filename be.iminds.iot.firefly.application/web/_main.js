
(function(){

	'use strict';
	
	function error(msg) {
		console.error(msg);
	}
	
	var FIREFLY = angular.module('be.iminds.iot.firefly', 
			['ui.bootstrap',
			 'ngRoute',
			 'ngResource',
			 'enJsonrpc',
			 'enEasse',
			 'be.iminds.iot.repository',
			 'be.iminds.iot.firefly.actions']);
	
	FIREFLY.config(function($routeProvider, en$jsonrpcProvider) {
		en$jsonrpcProvider.setNotification({
			error : error
		});
	});
	
	// this allows filtering on map values in ng-repeat
	FIREFLY.filter('mapFilter', function($filter) {
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
	FIREFLY.directive('onLongclick', function($timeout) {
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
	
	
	FIREFLY.controller('ThingsCtrl', function ($rootScope, $scope, $modal, en$easse, en$jsonrpc, repository, actions) {
		// fill things map using repository REST endpoint
		$scope.things = {};
		$scope.locations = {};
		$scope.filters = { location : ''};
		
		repository.query(function(things){
			for(var i in things){
				console.log(JSON.stringify(things[i]));
				$scope.things[things[i].id] = things[i];
				$scope.locations[things[i].location] = things[i].location;
				
				// initialize highlight
				for(name in things[i].state){
					if(name==='state' || Object.keys(things[i].state).length==1){
						$scope.things[things[i].id].highlight = things[i].state[name];
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
				if($scope.things[event['thing.id']].state === undefined){
					$scope.things[event['thing.id']].state = {};
				}
			
				var val = event['state.value'];
				var name = event['state.variable'];

				if(angular.isString(val)){
					$scope.things[event['thing.id']].state[name] = val;
				} else if(angular.isNumber(val)) {
					if(val !== (val|0)) {
						// float, format nicely
						var formatted = parseFloat(val).toFixed(2)+" "+val.unit;
						$scope.things[event['thing.id']].state[name] = formatted;
					} else {
						// int
						$scope.things[event['thing.id']].state[name] = val;
					}
				} else {
					// sensor value, format nicely
					var formatted = parseFloat(val.value).toFixed(2)+" "+val.unit;
					$scope.things[event['thing.id']].state[name] = formatted;
				}
				
				// set highlight to "state" variabele, or anything in case only 1 state element
				if(name==='state' || Object.keys($scope.things[event['thing.id']].state).length==1){
					$scope.things[event['thing.id']].highlight = $scope.things[event['thing.id']].state[name];
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
			console.log("ACTION "+id)
			actions.action(id, $scope.things[id].type);
		};
		
		$scope.update = function(id){
			console.log("UPDATE "+id);
			$scope.updateThing($scope.things[id]);
		};
		  
		// thing details dialog
		$scope.dialog = function(id){
			console.log("DIALOG "+id);
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
				templateUrl: 'updateThingContent.html',
				controller: 'updateThingCtrl',
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
	});

})();