

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
	
	angular.module('be.iminds.iot.firefly.dashboard').controller('ruleDetailsCtrl', function ($scope, $modalInstance, rule, things, templates) {
		
		$scope.things = things;
		$scope.rule = rule;
		
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
			$scope.rule.description = $scope.description;
			$scope.rule.source = $scope.source;
			$scope.rule.destination = $scope.destination;
			$scope.rule.type = $scope.template[0].type;
			$modalInstance.close($scope.rule);
		};
	
		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
	});

})();