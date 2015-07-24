(function() {

	'use strict';

	angular.module('be.iminds.iot.rule.simple', []).factory('simplerules',
			function($resource) {
				return $resource('/rest/:a/:type',{}, {
					add : {
						method : 'PUT',
						params : {a: 'simplerule', type: ""}
					},
					methods : {
						method: 'GET',
						params : {a: 'methods', type: '@type'},
						isArray: true
					},
					variables : {
						method: 'GET',
						params : {a: 'variables', type: '@type'},
						isArray: true
					}
				});
			});

})();
	