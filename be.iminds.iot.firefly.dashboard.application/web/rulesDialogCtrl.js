

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('rulesDialogCtrl', function ($scope, $modal, $modalInstance, things, rules) {
		  $scope.rules = [];
		
		  rules.query(function(rules){
				$scope.rules = rules;
			});
		  
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
				  // for now only addition is supported, no reconfiguration
					
				  var ruleDTO = {};
				  ruleDTO.sourceTypes = [rule.source.type];
				  ruleDTO.destinationTypes = [rule.destination.type];
				  ruleDTO.sources = [rule.source.id];
				  ruleDTO.destinations = [rule.destination.id];
				  ruleDTO.description = rule.description;
				  ruleDTO.type = rule.type;
					
				  rules.add(ruleDTO, function success(){
					  $scope.rules.push(rule);
				  });
			  });
		  }
		  
		  $scope.remove = function(index){
			  rules.remove({'index': index}, function success(){
				  $scope.rules.splice(index, 1);
			  });
		  }
	});

})();