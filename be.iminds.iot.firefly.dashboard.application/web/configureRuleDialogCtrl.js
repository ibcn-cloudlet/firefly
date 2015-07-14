

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('configureRuleDialogCtrl', function ($scope, $modalInstance, rule) {
		$scope.rule = rule;	
			
		$scope.ok = function () {
			console.log("rule updated!");
			  
			  
			$modalInstance.close();
		};
	
		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	});

})();