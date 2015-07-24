
(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('lampCtrl', function ($scope, $modalInstance, actions, thing) {
		  
		  $scope.thing = thing;
		  $scope.color = $scope.thing.state.color;
		  $scope.level = $scope.thing.state.level;
		  
		  // should not happen?
		  if($scope.color === undefined)
			  $scope.color = "#FFFFFF";
		  if($scope.level === undefined)
			  $scope.level = "100";
		  
		  $scope.$watch('color', function(newValue, oldValue) {
			  if(newValue!== undefined)
				  actions.action($scope.thing.id, $scope.thing.type, "color", $scope.color);
		  });
		  
		  $scope.$watch('level', function(newValue, oldValue) {
			  if(newValue!== undefined)
				  actions.action($scope.thing.id, $scope.thing.type, "level", $scope.level);
		  });
		  
		  $scope.toggle = function(){
			  actions.action($scope.thing.id, $scope.thing.type);
		  };
		  
		  $scope.ok = function () {
			  $modalInstance.close();
		  };
	
		  $scope.cancel = function () {
			  $modalInstance.dismiss('cancel');
		  };
	});

})();