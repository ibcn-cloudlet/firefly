
(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly').controller('cameraCtrl', function ($scope, $modalInstance, thing) {
		  
		  $scope.thing = thing;
	
		  $scope.configure = function(){
			  console.log("configure!");
		  };
		  
		  $scope.ok = function () {
			  $modalInstance.close();
		  };
	
		  $scope.cancel = function () {
			  $modalInstance.dismiss('cancel');
		  };
	});

})();