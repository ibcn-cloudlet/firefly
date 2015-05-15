'use strict';

var fireflyApp = angular.module('be.iminds.iot.firefly', []);

fireflyApp.controller('ThingsCtrl', function ($scope) {
	  $scope.things = [
	                   {
	                	    'id': '0',
	                	 	'name': 'Button',
	                	 	'type': 'button',
	                     	'room': 'Kitchen',
	                     	'state': 'PRESSED',
	                    	'iconclass':'icon-button'
	                   },
	                   {
	                	    'id': '1',
	                	    'name': 'Lamp',
	                	    'type': 'lamp',
		                 	'room': 'Kitchen',
		                 	'state': 'ON',
		                 	'iconclass':'icon-lamp'
		                },	                  
		                {
		                	'id': '2',
		                	'name': 'Temperature',
		                	'type': 'temperature',
		                	'room': 'Kitchen',
		                    'state': '20 C',
		                    'iconclass':'icon-temperature'
		                }                
		              ];
});
