

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('rulesCtrl', function ($scope, $modal, $modalInstance, things, rules) {
		  $scope.rules = [];
		
		  rules.query(function(rules){
				$scope.rules = rules;
			});
		  
		  $scope.create = function () {
			  var ruleModal = $modal.open({
					templateUrl: 'rules/ruleDetails.html',
					controller: 'ruleDetailsCtrl',
					size: 'lg',
					resolve: {
						things: function(){
							return things;
						}
					}
			  });
			  ruleModal.result.then(function(rule){
				  $scope.rules.push(rule);
			  });
		  };
	
		  $scope.cancel = function () {
			  $modalInstance.dismiss('cancel');
		  };
		  
		  $scope.remove = function(index){
			  rules.remove({'index': index}, function success(){
				  $scope.rules.splice(index, 1);
			  });
		  }
	});

})();