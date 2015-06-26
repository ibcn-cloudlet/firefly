
(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly').controller('cameraCtrl', function ($scope, $modalInstance, thing) {
		
		  
		  $scope.thing = thing;
	
		  $scope.ok = function () {
		    $modalInstance.close();
		  };
	
		  $scope.cancel = function () {
		    $modalInstance.dismiss('cancel');
		  };
	});

})();