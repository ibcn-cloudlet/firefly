package be.iminds.iot.things.dyamand.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dyamand.event.Event;
import org.dyamand.event.EventListener;
import org.dyamand.event.ServicePOJOOfflineEvent;
import org.dyamand.event.ServicePOJOOnlineEvent;
import org.dyamand.event.StateChangedEvent;
import org.dyamand.service.ServicePOJO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventProperties;

import be.iminds.iot.things.api.Thing;

/**
 * 
 */
@Component(name = "be.iminds.iot.things.dyamand", immediate=true)
public class DyamandAdapter implements EventListener {

	// Hack to have bridge to Dyamand plugin system...
	static Object sync = new Object();
	static DyamandAdapter instance = null;
	
	private BundleContext context;
	private UUID gatewayId;
	private EventAdmin ea;
	
    private final Map<Object, ServiceRegistration> services = new HashMap<>();
	private final List<ServiceAdapter> adapters = Collections.synchronizedList(new ArrayList<>());
	
	@Activate
	public void activate(BundleContext ctx){
		synchronized(sync){
			instance = this;
			sync.notifyAll();
		}
		context = ctx;
		gatewayId = UUID.fromString(context.getProperty(Constants.FRAMEWORK_UUID)); // get frameworkId
	}
	
	@Deactivate
	public void deactivate(){
		instance = null;
	}
	
	
    @Override
	public void onEvent(final Event event) {
		if (event instanceof ServicePOJOOnlineEvent) {
			final ServicePOJO servicePOJO = ((ServicePOJOOnlineEvent) event)
					.getServicePOJO();
			this.servicePojoOnline(servicePOJO);
		} else if (event instanceof ServicePOJOOfflineEvent) {
			final ServicePOJO servicePOJO = ((ServicePOJOOfflineEvent) event)
					.getServicePOJO();
			this.servicePojoOffline(servicePOJO);
		} else if (event instanceof StateChangedEvent) {
			final org.dyamand.service.StateChange stateChange = ((StateChangedEvent) event)
					.getStateChange();
			this.processStateChange(stateChange);
		} 
	}

    private void servicePojoOnline(final ServicePOJO servicePOJO) {
    	// translate sensor to IoT types
    	synchronized(adapters){
			for(ServiceAdapter adapter : adapters){
				try {
				    // ADAPT!
				    final Object so = adapter.getServiceObject(servicePOJO);
	
					final String device = servicePOJO.getService().getOriginalDevice().getName().toString();
					final String service = servicePOJO.getService().getName().toString();
					final UUID thingId = UUID.nameUUIDFromBytes((device+service).getBytes());
					
					final Dictionary<String, Object> properties = new Hashtable<String, Object>();
					properties.put(Thing.ID, thingId);
					properties.put(Thing.DEVICE, device);
					properties.put(Thing.SERVICE, service);
					properties.put(Thing.GATEWAY, gatewayId);
					properties.put(Thing.TYPE, adapter.getType());

				    // Add some AIOLOS stuff
				    properties.put("aiolos.instance.id", servicePOJO
					    .getService().getId().toString());
				    properties.put("aiolos.combine", "*");
	
				    final ServiceRegistration registration = this.context
					    .registerService(adapter.getTargets(), so,
						    properties);
				    this.services.put(servicePOJO, registration);
				    
				    this.notifyOnline(thingId, device, service, adapter.getType());
				} catch (final Exception e) {
				}
			}
    	}
    }

	private void servicePojoOffline(final ServicePOJO servicePOJO) {
		final ServiceRegistration registration = this.services
				.remove(servicePOJO);
		if (registration != null) {
			registration.unregister();
			
			final String device = servicePOJO.getService().getOriginalDevice().getName().toString();
			final String service = servicePOJO.getService().getName().toString();
			final UUID thingId = UUID.nameUUIDFromBytes((device+service).getBytes());
			
		    this.notifyOffline(thingId);
		}
	}

	private void processStateChange(
			final org.dyamand.service.StateChange stateChange) {
		
		final String device = stateChange.getService().getOriginalDevice().getName().toString();
		final String service = stateChange.getService().getName().toString();
		final UUID thingId = UUID.nameUUIDFromBytes((device+service).getBytes());
		
		final String stateVariable = stateChange.getStateVariable().toString();
		final Object value = stateChange.getValue();

		synchronized(adapters){
			for (final ServiceAdapter adapter : this.adapters) {
				try {
					final StateVariable translated = adapter
							.translateStateVariable(stateVariable, value);
					this.notifiyStateChange(thingId, translated.getName(), translated.getValue());
					
				} catch (final Exception e) {
				}
			}
		}
	}
	
	private void notifyOnline(
			final UUID thingId,
			final String device,
			final String service,
			final String type){
		final HashMap<String, Object> p = new HashMap<>();
		p.put(Thing.ID, thingId);
		p.put(Thing.GATEWAY, gatewayId);		
		p.put(Thing.SERVICE, service);
		p.put(Thing.DEVICE, device);
		p.put(Thing.TYPE, type);
		
		final String topic = "be/iminds/iot/thing/online/"+thingId;
		notifyListeners(topic, p);
	}
	
	private void notifyOffline(
			final UUID thingId){
		final HashMap<String, Object> p = new HashMap<>();
		p.put(Thing.ID, thingId);
		p.put(Thing.GATEWAY, gatewayId);
		
	    final String topic = "be/iminds/iot/thing/offline/"+thingId;
	    notifyListeners(topic, p);
	}
	
	private void notifiyStateChange(
			final UUID thingId, 
			final String stateVariable, 
			final Object stateValue){
		final HashMap<String, Object> p = new HashMap<>();
		p.put(Thing.ID, thingId);
		p.put(Thing.GATEWAY, gatewayId);
		p.put(Thing.STATE_VAR, stateVariable);
		p.put(Thing.STATE_VAL, stateValue);
		
		final String topic = "be/iminds/iot/thing/change/"+thingId;
		notifyListeners(topic, p);
	}
	
	private void notifyListeners(final String topic, final Map<String, Object> properties){
		properties.put("timestamp", System.currentTimeMillis());
		final EventProperties e = new EventProperties(properties);
		// use synchrounous delivery in order not to hang before sending an event on single threaded device like Pi B+
		ea.sendEvent(new org.osgi.service.event.Event(topic, properties));
	}

	@Reference
	void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addServiceAdapter(ServiceAdapter sa){
		adapters.add(sa);
	}
	
	public void removeServiceAdapter(ServiceAdapter sa){
		adapters.remove(sa);
	}
}
