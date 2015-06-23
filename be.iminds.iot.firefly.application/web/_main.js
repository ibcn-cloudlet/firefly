
(function(){

	'use strict';
	
	function error(msg) {
		alert(msg);
	}
	
	var FIREFLY = angular.module('be.iminds.iot.firefly', ['ui.bootstrap','ngRoute','ngResource','enJsonrpc','enEasse','be.iminds.iot.repository']);
	
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
	
	
	FIREFLY.controller('ThingsCtrl', function ($rootScope, $scope, $modal, en$easse, en$jsonrpc, repository) {
		// fill things map using repository REST endpoint
		$scope.things = {};
		$scope.locations = {};
		
		repository.query(function(things){
			for(var i in things){
				console.log(things[i].id);
				$scope.things[things[i].id] = things[i];
				$scope.locations[things[i].location] = things[i].location;
			}
		});

		// connect to firefly jsonrpc endpoint for actions
		$scope.ff = {};
		en$jsonrpc.endpoint("be.iminds.iot.firefly").then(
				function(ff){
					$scope.ff = ff;
				}
		);
		
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
			  	repository.get({ id: event['be.iminds.iot.thing.id'] }, function(thing) {
				    console.log("ONLINE "+JSON.stringify(thing));
				    if(angular.equals({}, thing)){
						thing.id = event['be.iminds.iot.thing.id'];
						thing.name = event['be.iminds.iot.thing.service'];
						thing.type = 'button';
						$scope.newThing(thing);
				  		
				  	} else {
						$scope.things[thing.id] = thing;
						$scope.locations[thing.location] = thing.location;
						$scope.$apply();
				  	}
			  	});
		  };	
			
		  $scope.offline = function(event) {
			   delete $scope.things[event['be.iminds.iot.thing.id']];
			   $scope.$apply();
		  };
		  
		  $scope.change = function(event) {
			  // TODO which state variable to show?
			  if( $scope.things[event['be.iminds.iot.thing.id']] != undefined){
				  $scope.things[event['be.iminds.iot.thing.id']].state = event['be.iminds.iot.thing.state.value'];
				  $scope.$apply();
			  }
		  };
			
		  // easse callbacks
		  en$easse.handle("be/iminds/iot/thing/online/*", $scope.online, error);
		  en$easse.handle("be/iminds/iot/thing/offline/*", $scope.offline, error);
		  en$easse.handle("be/iminds/iot/thing/change/*", $scope.change, error);

		  // action callback
		  $scope.action = function(id){
			  console.log("ACTION "+id);	
			  $scope.ff.action(id);
		  };
		  
		  // thing details dialog
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
		  
		  // new thing dialog
		  $scope.newThing = function(thing){
			  var modalInstance = $modal.open({
			      templateUrl: 'newThingContent.html',
			      controller: 'newThingCtrl',
			      resolve: {
			    	  thing: function(){
			    		  return thing;
			    	  }
			      }
			  });
			  modalInstance.result.then(function(thing){
				  $scope.things[thing.id] = thing;
				  $scope.locations[thing.location] = thing.location;
				  //$scope.$apply();
			  });
		  };
	});

})();