(function() {

	'use strict';

	angular.module('be.iminds.iot.firefly.actions', []).factory('actions',
			function(en$jsonrpc) {
				var actions = {};
				actions.endpoint = en$jsonrpc.endpoint("be.iminds.iot.firefly");
				
				actions.action = function(){
					var params = arguments;
					actions.endpoint.then(function(a){
						a.action.apply(this, params);
					});
				}
				
				return actions;
			});

})();
	