package be.iminds.iot.firefly.dashboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.github.angular_ui.capabilities.RequireAngularUIWebResource;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.jsonrpc.api.JSONRPC;
import osgi.enroute.jsonrpc.api.RequireJsonrpcWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireAngularUIWebResource(resource="ui-bootstrap-tpls.js")
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireEventAdminServerSentEventsWebResource(resource={"easse.js","polyfill/eventsource.js"})
@RequireJsonrpcWebResource(resource={"jsonrpc.js"})
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="be.iminds.iot.firefly.dashboard",property=JSONRPC.ENDPOINT+"=be.iminds.iot.firefly.dashboard")
public class Dashboard implements JSONRPC {

	private Map<String, Actions> dispatchers = Collections.synchronizedMap(new HashMap<String, Actions>());
	
	public void action(UUID thingId, String type, String... params){
		Actions dispatcher = dispatchers.get(type);
		if(dispatcher!=null){
			dispatcher.action(thingId, params);
		}
	}

	@Override
	public Object getDescriptor() throws Exception {
		// JSONRPC getDescriptor() ... not used atm
		return null;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addActions(Actions a){
		dispatchers.put(a.getType(), a);
	}
	
	public void removeActions(Actions a){
		Actions old = dispatchers.get(a.getType());
		if(old==a){
			dispatchers.remove(a);
		}
	}

}
