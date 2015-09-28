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