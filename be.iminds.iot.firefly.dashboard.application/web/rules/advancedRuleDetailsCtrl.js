/*******************************************************************************
 *  Copyright (c) 2015, Tim Verbelen
 *  Internet Based Communication Networks and Services research group (IBCN),
 *  Department of Information Technology (INTEC), Ghent University - iMinds.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of Ghent University - iMinds, nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/


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