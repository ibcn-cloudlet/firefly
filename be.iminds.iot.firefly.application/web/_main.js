
(function(){

	'use strict';
	
	function error(msg) {
		alert(msg);
	}
	
	var FIREFLY = angular.module('be.iminds.iot.firefly', ['ui.bootstrap','ngRoute','enJsonrpc','enEasse']);
	
	FIREFLY.config(function($routeProvider, en$jsonrpcProvider) {
		en$jsonrpcProvider.setNotification({
			error : error
		});
	});
	
	// introduce onLongclick and onClick directives
	FIREFLY.directive('onLongclick', function($timeout) {
		return {
			restrict: 'A',
			link: function($scope, $elm, $attrs) {
				$elm.bind('mousedown', function(evt) {
					// Locally scoped variable that will keep track of the long press
					$scope.long = true;
	 
					// We'll set a timeout for 600 ms for a long press
					$timeout(function() {
						if ($scope.long) {
							$scope.long = false;
							// If the touchend event hasn't fired,
							// apply the function given in on the element's ng-longclick attribute
							$scope.$apply(function() {
								$scope.$eval($attrs.onLongclick)
							});
						}
					}, 600);
				});
	 
				$elm.bind('mouseup', function(evt) {
					// If no long click, then fire click
					if($scope.long){
						if ($attrs.onClick) {
							$scope.$apply(function() {
								$scope.$eval($attrs.onClick)
							});
						}
					}
					$scope.long = false;
				});
			}
		};
	});
	
	
	FIREFLY.controller('ThingsCtrl', function ($rootScope, $scope, $modal, en$easse, en$jsonrpc) {
		  var repository;
		  en$jsonrpc.endpoint("be.iminds.iot.things.repository").then(
				function(r){
					repository = r;
					r.listThings().then(function(t){$scope.things = t});
				}
		  );
		  
		  
		  $scope.things = {};
//		  $scope.things['0'] = 
//		                   {
//		                	    'id': '0',
//		                	 	'name': 'Button',
//		                	 	'type': 'button',
//		                     	'room': 'Kitchen',
//		                     	'state': 'PRESSED',
//		                    	'iconclass':'icon-button'
//		                   };
//		  $scope.things['1'] =
//		                   {
//		                	    'id': '1',
//		                	    'name': 'Lamp',
//		                	    'type': 'lamp',
//			                 	'room': 'Kitchen',
//			                 	'state': 'ON',
//			                 	'iconclass':'icon-lamp'
//			                };	                  
//		  $scope.things['2'] = {
//			                	'id': '2',
//			                	'name': 'Temperature',
//			                	'type': 'temperature',
//			                	'room': 'Kitchen',
//			                    'state': '20 C',
//			                    'iconclass':'icon-temperature'
//			                };
		  
		  
		  // listeners for events
		  $scope.online = function(event) {
				// TODO lookup info from server
				var thing = {};
				thing.id = event['be.iminds.iot.thing.id'];
				thing.name = event['be.iminds.iot.thing.service'];
				thing.type = 'test';
				thing.iconclass = 'icon-button';
				
				$scope.things[thing.id] = thing;
				$scope.$apply();
		  };	
			
		  $scope.offline = function(event) {
			   delete $scope.things[event['be.iminds.iot.thing.id']];
			   $scope.$apply();
		  };
		  
		  $scope.change = function(event) {
			  // TODO which state variable to show?
			  $scope.things[event['be.iminds.iot.thing.id']].state = event['be.iminds.iot.thing.state.value'];
			  $scope.$apply();
		  };
			
		  en$easse.handle("be/iminds/iot/thing/online/*", $scope.online, error);
		  en$easse.handle("be/iminds/iot/thing/offline/*", $scope.offline, error);
		  en$easse.handle("be/iminds/iot/thing/change/*", $scope.change, error);

		  
		  $scope.action = function(id){
			  console.log("ACTION "+id);
			  var thing = $scope.things[id];
			  // TODO send action request to server
	 		  // window[thing.type+"_action"](thing);
		  };
		  
		  
		  $scope.dialog = function(id){
			  console.log("DIALOG "+id);
			  var thing = $scope.things[id];
		
			  var modalInstance = $modal.open({
			      templateUrl: thing.type+'Content.html',
			      controller: thing.type+'Ctrl',
			      size: 'lg',
			      resolve: {
			        thing: function () {
			          return thing;
			        }
			      }
			    });
		  };
	});

})();