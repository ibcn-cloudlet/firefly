

//function button_action(thing){
//	
//	console.log("Pressed button "+thing.id);
//}
//
//function button_dialog(thing){
//	
//	console.log("Show dialog for button "+thing.id);
//}

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly').controller('buttonCtrl', function ($scope, $modalInstance, thing) {
		
		  
		  $scope.thing = thing;
	
		  $scope.ok = function () {
		    $modalInstance.close();
		  };
	
		  $scope.cancel = function () {
		    $modalInstance.dismiss('cancel');
		  };
	});

})();