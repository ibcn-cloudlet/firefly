

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').filter('ruleFilter', function() {
			return function(templates, source, destination) {
			    var filtered =  templates.filter(function(template){
			    	return template.sourceTypes.indexOf(source.type) > -1 
			    			&& template.destinationTypes.indexOf(destination.type) > -1;
			    });
			    // manually replace the source.name and destination.name in description
			    // requires to refresh $scope.templates everytime a new thing is selected though
			    for(var i=0;i<filtered.length;i++){
			    	filtered[i].description = filtered[i].description
			    								.replace('{{source.name}}',source.name)
			    								.replace('{{destination.name}}',destination.name);
			    }
			    return filtered;
			};
	});
	
	angular.module('be.iminds.iot.firefly.dashboard').controller('ruleDetailsCtrl', function ($scope, $compile, $modal, $modalInstance, rules, things, templates) {
		// for now hard coded - should check whether actions are available
		$scope.destinationFilter = function(thing, index, array ){
			if(thing.type === 'camera' || thing.type==='lamp'){ 
				return true;
			}
			return false;
		};
		$scope.things = things;

		$scope.source = {};
		$scope.destination = {};
		$scope.template = {};
		
		$scope.origTemplates = [];
		$scope.templates = [];
		
		templates.query(function(t){
			$scope.origTemplates = t;
			$scope.refresh();
		});
			
		$scope.ok = function () {
			var ruleDTO = {};
			ruleDTO.sourceTypes = $scope.template[0].sourceTypes;
			ruleDTO.destinationTypes = $scope.template[0].destinationTypes;
			ruleDTO.sources = [$scope.source.id];
			ruleDTO.destinations = [$scope.destination.id];
			ruleDTO.description = $scope.template[0].description;
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
		
		$scope.refresh = function(){
			// called when another thing is selected, use this to 
			// refresh the templates after replacements in filter
			$scope.templates = [];
			angular.copy($scope.origTemplates, $scope.templates);
		}
	});

})();