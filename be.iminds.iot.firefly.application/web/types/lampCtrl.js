
(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly').controller('lampCtrl', function ($scope, $modalInstance, actions, thing) {
		  
		  $scope.thing = thing;
		  $scope.thing.color = "#0000FF";
		 
		  
		  $scope.$watch('thing.color', function(newValue, oldValue) {
			  console.log("COLOR! "+newValue);
			  if(newValue!== undefined)
				  actions.action($scope.thing.id, $scope.thing.type, "color", $scope.thing.color);
		  });
		  
		  $scope.$watch('thing.level', function(newValue, oldValue) {
			  console.log("LEVEL!");
			  if(newValue!== undefined)
				  actions.action($scope.thing.id, $scope.thing.type, "level", $scope.thing.level);
		  });
		  
		  $scope.toggle = function(){
			  console.log("TOGGLE!");
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