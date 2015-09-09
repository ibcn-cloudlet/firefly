

(function(){

	'use strict';

	angular.module('be.iminds.iot.firefly.dashboard').controller('advancedRuleDetailsCtrl', function ($scope, $modalInstance, simplerules, things) {
		// for now hard coded - should check whether actions are available
		$scope.destinationFilter = function(thing, index, array ){
			if(thing.type === 'camera' || thing.type==='lamp'){
				return true;
			}
			return false;
		};
		
		$scope.simplerules = simplerules;
		
		$scope.things = things;
		
		$scope.description;
		
		$scope.conditions = [];
		$scope.conditions.push({});
		
		$scope.actions = [];
		$scope.actions.push({});
		
		
		
		$scope.ok = function () {
			var simpleRuleDTO = {};
			simpleRuleDTO.type = "simple";
			simpleRuleDTO.description = $scope.description;
			
			simpleRuleDTO.conditions = [];
			for(var c=0; c<$scope.conditions.length;c++){
				var conditionDTO = {};
				conditionDTO.thingId = $scope.conditions[c].thing.id;
				conditionDTO.type = $scope.conditions[c].thing.type;
				conditionDTO.variable = $scope.conditions[c].variable;
				conditionDTO.operator = $scope.conditions[c].operator;
				conditionDTO.value = $scope.conditions[c].value;
				
				simpleRuleDTO.conditions.push(conditionDTO);
			}
			
			simpleRuleDTO.actions = [];
			for(var a=0; a<$scope.actions.length;a++){
				var actionDTO = {};
				actionDTO.thingId = $scope.actions[a].thing.id;
				actionDTO.type = $scope.actions[a].thing.type;
				actionDTO.method = $scope.actions[a].method;
				actionDTO.args = $scope.actions[a].arguments===undefined ? [] : $scope.actions[a].arguments.split(',');
				
				simpleRuleDTO.actions.push(actionDTO);
			}
			
			simplerules.add(simpleRuleDTO, function success(){
				$modalInstance.close(simpleRuleDTO);
			}, function error(){
				$modalInstance.dismiss('cancel');
			});
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
		
		$scope.conditionThingChange = function(condition){
			$scope.simplerules.variables({type: condition.thing.type},
					function success(variables){
				  		condition.variables = variables;
					});
		};
		
		$scope.actionThingChange = function(action){
			$scope.simplerules.methods({type: action.thing.type},
					function success(methods){
				  		action.methods = methods;
					});
		};
	});

})();