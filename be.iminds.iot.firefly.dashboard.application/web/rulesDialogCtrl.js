

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('rulesDialogCtrl', function ($scope, $modal, $modalInstance) {
		  $scope.rules = {};
		
		  $scope.rules["1"] = {'description':'this is a test rule','source':['button1'],'destination':['lamp1','lamp2']};
		  $scope.rules["2"] = {'description':'this is a second rule','source':['button2'],'destination':['camera']};
		  
		  $scope.create = function () {
			  console.log("rules updated!");
			  
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
							return $scope.rules[id];
						}
					}
			  });
			  configureRuleModal.result.then(function(rule){
					$scope.rules[rule.id] = rule;
			  });
		  }
		  
		  $scope.remove = function(id){
			  delete $scope.rules[id];
			  
			  console.log("delete rule "+id);
		  }
	});

})();