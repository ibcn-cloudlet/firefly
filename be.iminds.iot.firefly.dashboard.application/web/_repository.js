(function() {

	'use strict';

	angular.module('be.iminds.iot.repository', []).factory('repository',
			function($resource) {
				return $resource('/rest/thing','{}', {
					update : {
						method : 'PUT'
					}
				});
			});

})();
	