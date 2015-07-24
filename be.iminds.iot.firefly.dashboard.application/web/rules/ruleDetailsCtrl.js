

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').filter('ruleFilter', function() {
			return function(templates, source, destination) {
			    return templates.filter(function(template){
			    	return template.sourceTypes.indexOf(source.type) > -1 
			    			&& template.destinationTypes.indexOf(destination.type) > -1;
			    });
			};
	});
	
	angular.module('be.iminds.iot.firefly.dashboard').controller('ruleDetailsCtrl', function ($scope, $modal, $modalInstance, rules, things, templates) {
		
		$scope.things = things;

		$scope.description;
		$scope.source = {};
		$scope.destination = {};
		$scope.template = {};
		
		
		$scope.templates;
		
		// this raises errors in the console lot ... but does work :-/
		templates.query(function(templates){
			$scope.templates = templates;
		});
			
		$scope.ok = function () {
			var ruleDTO = {};
			ruleDTO.sourceTypes = $scope.template[0].sourceTypes;
			ruleDTO.destinationTypes = $scope.template[0].destinationTypes;
			ruleDTO.sources = [$scope.source.id];
			ruleDTO.destinations = [$scope.destination.id];
			ruleDTO.description = $scope.description;
			ruleDTO.type = $scope.template[0].type;
				
			rules.add(ruleDTO, function success(){
				$modalInstance.close(ruleDTO);
			}, function error(){
				$modalInstance.dismiss('cancel');
			});
		};
	
		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
		
		$scope.advanced = function () {
			var advancedRuleModal = $modal.open({
					templateUrl: 'rules/advancedRuleDetails.html',
					controller: 'advancedRuleDetailsCtrl',
					size: 'lg',
					resolve: {
						things: function(){
							return $scope.things;
						}
					}
			});
			advancedRuleModal.result.then(function(rule){
				$modalInstance.close(rule);
			});
		}
	});

})();