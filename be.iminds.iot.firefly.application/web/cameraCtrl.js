
(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly').controller('cameraCtrl', function ($scope, $modalInstance, actions, thing) {
		  
		  $scope.thing = thing;
	
		  $scope.configure = function(){
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