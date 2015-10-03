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

import osgi.enroute.dto.api.DTOs;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.event.ChangeEvent;
import be.iminds.iot.things.api.event.OfflineEvent;
import be.iminds.iot.things.api.event.OnlineEvent;

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
	private DTOs dtos;
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
    	if(this.services.get(servicePOJO)!=null){
    		System.err.println("ServicePOJO "+servicePOJO+" already online?!");
    		return;
    	}
    	
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
					properties.put(Thing.ID, thingId.toString());
					properties.put(Thing.DEVICE, device);
					properties.put(Thing.SERVICE, service);
					properties.put(Thing.GATEWAY, gatewayId.toString());
					properties.put(Thing.TYPE, adapter.getType());

				    // Add some AIOLOS stuff
				    properties.put("aiolos.instance.id", thingId.toString());
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
			final String device = servicePOJO.getService().getOriginalDevice().getName().toString();
			final String service = servicePOJO.getService().getName().toString();
			
			final UUID thingId = UUID.nameUUIDFromBytes((device+service).getBytes());
		    this.notifyOffline(thingId);
			
			registration.unregister();
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
		try {
			OnlineEvent e = new OnlineEvent();
			e.thingId = thingId;
			e.gatewayId = gatewayId;
			e.service = service;
			e.device = device;
			e.type = type;
			e.timestamp = System.currentTimeMillis();
	
			final String topic = "be/iminds/iot/thing/online/"+thingId;
			ea.sendEvent(new org.osgi.service.event.Event(topic, dtos.asMap(e)));
		} catch(Exception e){
			System.err.println("Error sending online event "+e);
		}
	}
	
	private void notifyOffline(
			final UUID thingId){	    
		try {
			OfflineEvent e = new OfflineEvent();
			e.thingId = thingId;
			e.gatewayId = gatewayId;
			e.timestamp = System.currentTimeMillis();
	
		    final String topic = "be/iminds/iot/thing/offline/"+thingId;
			ea.sendEvent(new org.osgi.service.event.Event(topic, dtos.asMap(e)));
		} catch(Exception e){
			System.err.println("Error sending offline event "+e);
		}
	}
	
	private void notifiyStateChange(
			final UUID thingId, 
			final String stateVariable, 
			final Object stateValue){
		try {
			ChangeEvent e = new ChangeEvent();
			e.thingId = thingId;
			e.gatewayId = gatewayId;
			e.stateVariable = stateVariable;
			e.stateValue = stateValue;
			e.timestamp = System.currentTimeMillis();
	
			final String topic = "be/iminds/iot/thing/change/"+thingId;
			ea.postEvent(new org.osgi.service.event.Event(topic, dtos.asMap(e)));
		} catch(Exception e){
			System.err.println("Error sending change event "+e);
		}
	}
	
	@Reference
	void setDTOs(DTOs dtos){
		this.dtos = dtos;
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
