

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('rulesDialogCtrl', function ($scope, $modal, $modalInstance, things) {
		  $scope.rules = [];
		
		  $scope.rules.push({'description':'this is a test rule','source':['button1'],'destination':['lamp1']});
		  $scope.rules.push({'description':'this is a second rule','source':['button2'],'destination':['camera']});
		  
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
					$scope.rules.push(rule);
					
					var ruleDTO = {};
					ruleDTO.sourceTypes = [rule.sourceType];
					ruleDTO.destinationTypes = [rule.destinationType];
					ruleDTO.sources = [rule.source.id];
					ruleDTO.destinations = [rule.destination.id];
					ruleDTO.description = rule.description;
					ruleDTO.type = rule.type;
					// TODO send new rule to server
					console.log("Send rule to server "+JSON.stringify(ruleDTO));
			  });
		  }
		  
		  $scope.remove = function(index){
			  // TODO send delete to server
			  console.log("Delete rule "+index+" on server");
			  
			  $scope.rules.splice(index, 1);
		  }
	});

})();