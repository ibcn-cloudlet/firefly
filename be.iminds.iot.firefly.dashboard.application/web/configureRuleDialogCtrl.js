

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').filter('ruleFilter', function() {
			return function(templates, source, destination) {
			    return templates.filter(function(template){
			    	return template.sourceType.indexOf(source.type) > -1 
			    			&& template.destinationType.indexOf(destination.type) > -1;
			    });
			};
	});
	
	angular.module('be.iminds.iot.firefly.dashboard').controller('configureRuleDialogCtrl', function ($scope, $modalInstance, rule, things) {
		
		$scope.things = things;
		$scope.rule = rule;
		
		$scope.description;
		$scope.source = {};
		$scope.destination = {};
		$scope.template = {};
		
		
		$scope.templates = [
		     {'description':'this is a test rule button->lamp','sourceType':['button'],'destinationType':['lamp']},
		     {'description':'this is a second rule button->camera','sourceType':['button'],'destinationType':['camera']},
		     {'description':'this is a third rule camera->camera','sourceType':['camera'],'destinationType':['camera']}
		];
			
		$scope.ok = function () {
			console.log("rule updated!");
			$scope.rule.description = $scope.description;
			$scope.rule.sourceType = $scope.template[0].sourceType;
			$scope.rule.destinationType = $scope.template[0].destinationType;
			$scope.rule.source = $scope.source;
			$scope.rule.destination = $scope.destination;
			$modalInstance.close($scope.rule);
		};
	
		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	});

})();