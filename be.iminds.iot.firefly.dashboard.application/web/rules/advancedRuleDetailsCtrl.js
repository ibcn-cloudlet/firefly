

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('advancedRuleDetailsCtrl', function ($scope, $modalInstance, rule, things, templates) {
		
		$scope.things = things;
		$scope.rule = rule;
		
		$scope.description;
		
		$scope.conditions = [];
		$scope.conditions.push({});
		
		$scope.actions = [];
		$scope.actions.push({});
		
		$scope.ok = function () {
			$scope.rule.description = $scope.description;
			$scope.rule.sources = $scope.sources;
			$scope.rule.destinations = $scope.destinations;
			$modalInstance.close($scope.rule);
		};
	
		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
		
		$scope.addAction = function(){
			$scope.actions.push({});
		};
		
		$scope.removeAction = function(index){
			$scope.actions.splice(index, 1);
		};
		
		$scope.addCondition = function(){
			$scope.conditions.push({});
		};
		
		$scope.removeCondition = function(index){
			$scope.conditions.splice(index, 1);
		};
	});

})();