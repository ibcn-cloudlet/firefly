package be.iminds.iot.firefly.application;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import osgi.enroute.capabilities.AngularUIWebResource;
import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.EasseWebResource;
import osgi.enroute.capabilities.EventAdminSSEEndpoint;
import osgi.enroute.capabilities.JsonrpcWebResource;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.jsonrpc.api.JSONRPC;

@AngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@AngularUIWebResource(resource="ui-bootstrap-tpls.js")
@BootstrapWebResource(resource="css/bootstrap.css")
@EasseWebResource(resource={"easse.js","polyfill/eventsource.js"})
@EventAdminSSEEndpoint
@JsonrpcWebResource(resource={"jsonrpc.js"})
@WebServerExtender
@ConfigurerExtender
@Component(name="be.iminds.iot.firefly",property=JSONRPC.ENDPOINT+"=be.iminds.iot.firefly")
public class FireflyApplication implements JSONRPC {

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
