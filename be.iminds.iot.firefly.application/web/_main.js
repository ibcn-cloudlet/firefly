
(function(){

	'use strict';
	
	angular.module('be.iminds.iot.firefly', ['ui.bootstrap','enEasse']);
	
	// introduce onLongclick and onClick directives
	angular.module('be.iminds.iot.firefly').directive('onLongclick', function($timeout) {
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
	
	angular.module('be.iminds.iot.firefly').controller('ThingsCtrl', function ($scope, $modal, en$easse) {
		  $scope.things = {};
		  $scope.things['0'] = 
		                   {
		                	    'id': '0',
		                	 	'name': 'Button',
		                	 	'type': 'button',
		                     	'room': 'Kitchen',
		                     	'state': 'PRESSED',
		                    	'iconclass':'icon-button'
		                   };
		  $scope.things['1'] =
		                   {
		                	    'id': '1',
		                	    'name': 'Lamp',
		                	    'type': 'lamp',
			                 	'room': 'Kitchen',
			                 	'state': 'ON',
			                 	'iconclass':'icon-lamp'
			                };	                  
		  $scope.things['2'] = {
			                	'id': '2',
			                	'name': 'Temperature',
			                	'type': 'temperature',
			                	'room': 'Kitchen',
			                    'state': '20 C',
			                    'iconclass':'icon-temperature'
			                };
		  
		  
		  // listeners for events
		  $scope.online = function(event) {
				console.log("ONLINE! "+event);
				
				
				//$scope.$apply();
		  };	
			
		  $scope.offline = function(event) {
			  console.log("OFFLINE! "+event);
				
				
				//$scope.$apply();
		  };
		  
		  $scope.change = function(event) {
			  console.log("CHANGE! "+event);
				
				
				//$scope.$apply();
		  };
			
		  $scope.error = function(e){
				console.log("Error... "+e);
		  };
			
		  en$easse.handle("be/iminds/iot/thing/online/*", $scope.online, $scope.error);
		  en$easse.handle("be/iminds/iot/thing/offline/*", $scope.offline, $scope.error);
		  en$easse.handle("be/iminds/iot/thing/change/*", $scope.change, $scope.error);

		  
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