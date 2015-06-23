

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly').controller('newThingCtrl', function ($scope, repository, $modalInstance, thing) {
		
		  $scope.thing = thing;
	
		  $scope.ok = function () {
			  repository.update($scope.thing);
			  $modalInstance.close($scope.thing);
		  };
	
		  $scope.cancel = function () {
			  $modalInstance.dismiss('cancel');
		  };
	});

})();