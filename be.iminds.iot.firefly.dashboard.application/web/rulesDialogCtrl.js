

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('rulesDialogCtrl', function ($scope, $modal, $modalInstance, things) {
		  $scope.rules = {};
		
		  $scope.rules["0"] = {'description':'this is a test rule','source':['button1'],'destination':['lamp1']};
		  $scope.rules["1"] = {'description':'this is a second rule','source':['button2'],'destination':['camera']};
		  
		  $scope.create = function () {
			  $scope.configure();
		  };
	
		  $scope.cancel = function () {
			  $modalInstance.dismiss('cancel');
		  };
		  
		  $scope.configure = function(id){
			  var configureRuleModal = $modal.open({
					templateUrl: 'configureRuleDialogContent.html',
					controller: 'configureRuleDialogCtrl',
					size: 'lg',
					resolve: {
						rule: function(){
							return $scope.rules[id]===undefined? {} : $scope.rules[id];
						},
						things: function(){
							return things;
						}
					}
			  });
			  configureRuleModal.result.then(function(rule){
					$scope.rules[Object.keys($scope.rules).length] = rule;
					
					// TODO send new rule to server
			  });
		  }
		  
		  $scope.remove = function(id){
			  delete $scope.rules[id];
			  
			  // TODO send delete to server
			  
		  }
	});

})();