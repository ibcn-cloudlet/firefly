(function() {

	'use strict';

	angular.module('be.iminds.iot.rule.engine', []).factory('rules',
			function($resource) {
				return $resource('/rest/rules/:index', {index:'@index'}, {
					add : {
						method : 'PUT'
					},
					remove : {
						method: 'DELETE'
					}
				});
			});
	
	angular.module('be.iminds.iot.rule.templates', []).factory('templates',
			function($resource) {
				return $resource('/rest/templates/');
			});

})();
	