package be.iminds.iot.things.dyamand.adapter;

import java.util.ArrayList;
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
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventProperties;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.dyamand.adapters.ButtonAdapter;
import be.iminds.iot.things.dyamand.adapters.ContactSensorAdapter;
import be.iminds.iot.things.dyamand.adapters.LightAdapter;
import be.iminds.iot.things.dyamand.adapters.LightSensorAdapter;
import be.iminds.iot.things.dyamand.adapters.MotionSensorAdapter;
import be.iminds.iot.things.dyamand.adapters.TemperatureSensorAdapter;

/**
 * 
 */
@Component(name = "be.iminds.iot.things.dyamand", immediate=true)
public class DyamandAdapter implements EventListener {

	// Hack to have bridge to Dyamand plugin system...
	static DyamandAdapter instance = null;
	
	private BundleContext context;
	private EventAdmin ea;
	
    private final Map<Object, ServiceRegistration> services = new HashMap<>();
	private final List<ServiceAdapter> adapters = new ArrayList<>();
	
	@Activate
	public void activate(BundleContext ctx){
		instance = this;
		context = ctx;
		
		// for now fix code all adapters... use service mechanism for this?
		adapters.add(new ButtonAdapter());
		adapters.add(new ContactSensorAdapter());
		adapters.add(new LightAdapter());
		adapters.add(new LightSensorAdapter());
		adapters.add(new MotionSensorAdapter());
		adapters.add(new TemperatureSensorAdapter());
		
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
		for(ServiceAdapter adapter : adapters){
			try {
			    // ADAPT!
			    final Object service = adapter.getServiceObject(servicePOJO);

				final UUID deviceId = servicePOJO.getService().getOriginalDevice().getId();
				final String serviceName = servicePOJO.getService().getName().toString().replaceAll("\\.", "_");

				final Dictionary<String, Object> properties = new Hashtable<String, Object>();
				properties.put(Thing.ID, deviceId.toString());
				properties.put(Thing.SERVICE, serviceName);
			    // Add some AIOLOS stuff
			    properties.put("aiolos.instance.id", servicePOJO
				    .getService().getId().toString());
			    properties.put("aiolos.combine", "*");

			    final ServiceRegistration registration = this.context
				    .registerService(adapter.getTargets(), service,
					    properties);
			    this.services.put(servicePOJO, registration);
			    
			    final String topic = "be/iminds/iot/thing/online/"+deviceId+"/"+serviceName;
				this.notifyStateChangeListeners(deviceId.toString(),
						serviceName, null, null, topic);
			} catch (final Exception e) {
			}
		}
    }

	private void servicePojoOffline(final ServicePOJO servicePOJO) {
		final ServiceRegistration registration = this.services
				.remove(servicePOJO);
		if (registration != null) {
			registration.unregister();
			
			final UUID deviceId = servicePOJO.getService().getOriginalDevice().getId();
			final String serviceName = servicePOJO.getService().getName().toString().replaceAll("\\.", "_");;
		    final String topic = "be/iminds/iot/thing/offline/"+deviceId+"/"+serviceName;
			this.notifyStateChangeListeners(deviceId.toString(),
					serviceName, null, null, topic);
		}
	}

	private void processStateChange(
			final org.dyamand.service.StateChange stateChange) {
		final UUID deviceId = stateChange.getService().getOriginalDevice()
				.getId();
		final String serviceName = stateChange.getService().getName()
				.toString().replaceAll("\\.", "_");;
		final String stateVariable = stateChange.getStateVariable().toString();
		final Object value = stateChange.getValue();

		for (final ServiceAdapter adapter : this.adapters) {
			try {
				final StateVariable translated = adapter
						.translateStateVariable(stateVariable, value);
				// TODO which TOPIC namespaces to use?
				final String topic = "be/iminds/iot/thing/change/"+deviceId+"/"+serviceName;
				this.notifyStateChangeListeners(deviceId.toString(),
						serviceName, translated.getName(),
						translated.getValue(), topic);
			} catch (final Exception e) {
			}
		}
	}

	private void notifyStateChangeListeners(final String deviceId,
			final String serviceName, final String stateVariable,
			final Object stateValue, final String topic) {
		final HashMap<String, Object> p = new HashMap<>();
		p.put(Thing.ID, deviceId);
		p.put(Thing.SERVICE, serviceName);
		p.put(Thing.STATE_VAR, stateVariable);
		p.put(Thing.STATE_VAL, stateValue);
		p.put("timestamp", System.currentTimeMillis());
		final EventProperties e = new EventProperties(p);
		ea.postEvent(new org.osgi.service.event.Event(topic, e));
	}

	@Reference
	void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
}
