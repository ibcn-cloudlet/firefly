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
package be.iminds.iot.things.repository.provider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.dto.api.TypeReference;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.event.ChangeEvent;
import be.iminds.iot.things.api.event.EventUtil;
import be.iminds.iot.things.api.event.OfflineEvent;
import be.iminds.iot.things.api.event.OnlineEvent;
import be.iminds.iot.things.repository.api.ThingDTO;
import be.iminds.iot.things.repository.api.ThingsRepository;

/**
 * 
 */
@Component(property={"event.topics=be/iminds/iot/thing/*"})
public class ThingsRepositoryImpl implements ThingsRepository, EventHandler {
	
	private DTOs dtos;
	
	private Map<UUID, ThingDTO> things = Collections.synchronizedMap(new HashMap<>());
	private Set<UUID> online = Collections.synchronizedSet(new HashSet<>());
	
	private PrintWriter logger;
	
	@Activate
	public void activate(BundleContext context){
		// load thing dtos from file
		load("things.txt");
		
		// open file output to log events
		try {
			logger = new PrintWriter(new BufferedWriter(new FileWriter(new File("log.txt"), true)));
			logger.write(">> System online "+new Date()+"\n");
			logger.flush();
		} catch (IOException e) {
		}
	}
	
	@Deactivate
	public void deactivate(){
		// close event logging file
		logger.close();
	
		
		save("things.txt");
	}
	
	void load(String file){
		try {
			dtos.decoder(new TypeReference<Map<UUID,ThingDTO>>(){}).get(new FileInputStream(file));
		} catch(Exception e){
			System.err.println("Failed to load thing descriptions from file");
		}
	}
	
	void save(String file){
		// write thing dtos to file
		try {
			dtos.encoder(things).put(new FileOutputStream(file));
		} catch(Exception e){
			System.err.println("Failed to write thing descriptions to file");
		}
	}
	
	@Override
	public ThingDTO getThing(UUID id) {
		return things.get(id);
	}

	@Override
	public Collection<ThingDTO> getThings() {
		// only return online things
		ArrayList<ThingDTO> result = new ArrayList<>();
		synchronized(online){
			for(UUID id : online){
				ThingDTO t = things.get(id);
				if(t!=null){
					result.add(t);
				} else {
					// should not happen
					System.err.println(id+" online but no ThingDTO!");
				}
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	@Override
	public void putThing(ThingDTO thing) {
		things.put(thing.id, thing);
	}

	@Override
	public void handleEvent(Event event) {
		try {
			if(event.getTopic().startsWith("be/iminds/iot/thing/online/")){
				OnlineEvent e = EventUtil.toOnlineEvent(event, dtos);
				handleOnlineEvent(e);
				logEvent(e);
			} else if(event.getTopic().startsWith("be/iminds/iot/thing/offline/")){
				OfflineEvent e = EventUtil.toOfflineEvent(event, dtos);
				handleOfflineEvent(e);
				logEvent(e);
			} else if(event.getTopic().startsWith("be/iminds/iot/thing/change/")){
				ChangeEvent e = EventUtil.toChangeEvent(event, dtos);
				handleChangeEvent(e);
				logEvent(e);
			}
		} catch(Exception e){
			System.err.println("Error handling event "+event);
		}
	}
	
	private void handleOnlineEvent(OnlineEvent e){
		// add thing to repository
		ThingDTO thing;
		synchronized(things){
			thing = things.get(e.thingId);
			if(thing==null){
				thing = new ThingDTO();
				thing.id = e.thingId;
				thing.gateway = e.gatewayId;
				thing.device = e.device;
				thing.service = e.service;
				thing.type = e.type;
				thing.name = thing.service;

				things.put(thing.id, thing);
			} 
		}
		
		// mark online
		online.add(thing.id);
	}
	
	private void handleOfflineEvent(OfflineEvent e){
		// mark offline
		online.remove(e.thingId);
	}
	
	private void handleChangeEvent(ChangeEvent e){
		ThingDTO thing = things.get(e.thingId);
		if(thing!=null){
			// update state
			if(thing.state == null){
				thing.state = new HashMap<>();
			}
			thing.state.put(e.stateVariable, e.stateValue);
			
			// mark online
			online.add(thing.id);
		}
	}
	
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addThing(Thing t, Map<String, Object> properties){
		UUID id = UUID.fromString((String)properties.get(Thing.ID));
		// also init here in case of missed online event
		ThingDTO thing;
		synchronized(things){
			thing = things.get(id);
			if(thing==null){
				thing = new ThingDTO();
				thing.id = id;
				thing.gateway = UUID.fromString((String)properties.get(Thing.GATEWAY));
				thing.device = (String) properties.get(Thing.DEVICE);
				thing.service = (String) properties.get(Thing.SERVICE);
				thing.type = (String) properties.get(Thing.TYPE);
				thing.name = thing.service;

				things.put(id, thing);
			} else {
				// update gateway - could be changed
				thing.gateway = (UUID) properties.get(Thing.GATEWAY);
			}
		}
	
		// mark online
		online.add(id);
	}
	
	public void removeThing(Thing t, Map<String, Object> properties){
		// mark offline
		UUID id = UUID.fromString((String)properties.get(Thing.ID));
		UUID gateway = UUID.fromString((String)properties.get(Thing.GATEWAY));
		
		if(online.remove(id)){
			// When this is caused by the gateway losing connectivity, the (remote) service
			// goes offline but no OfflineEvent was sent by the gateway.
			// Send an event through eventadmin to update the UI.
			try {
				OfflineEvent e = new OfflineEvent();
				e.thingId = id;
				e.gatewayId = gateway;
				e.timestamp = System.currentTimeMillis();
				ea.postEvent(new Event("be/iminds/iot/thing/offline/"+id, dtos.asMap(e)));
			} catch(Exception e){
				System.err.println("Error sending online event "+e);
			}
		}
	}
	
	private EventAdmin ea;
	
	@Reference
	public void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
	
	private void logEvent(OnlineEvent event){
		StringBuilder builder = new StringBuilder();
		builder.append("ONLINE");
		builder.append("\t");
		builder.append(event.timestamp);
		builder.append("\t");
		builder.append(event.thingId);
		builder.append("\t");
		builder.append(event.gatewayId);
		builder.append("\t");
		builder.append(event.device);
		builder.append("\t");
		builder.append(event.service);
		builder.append("\t");
		builder.append(event.type);
		logger.println(builder.toString());
		logger.flush();
	}
	
	private void logEvent(OfflineEvent event){
		StringBuilder builder = new StringBuilder();
		builder.append("OFFLINE");
		builder.append("\t");
		builder.append(event.timestamp);
		builder.append("\t");
		builder.append(event.thingId);
		builder.append("\t");
		builder.append(event.gatewayId);
		logger.println(builder.toString());
		logger.flush();
	}
	
	
	private void logEvent(ChangeEvent event){
		StringBuilder builder = new StringBuilder();
		builder.append("CHANGE");
		builder.append("\t");
		builder.append(event.timestamp);
		builder.append("\t");
		builder.append(event.thingId);
		builder.append("\t");
		builder.append(event.gatewayId);
		builder.append("\t");
		builder.append(event.stateVariable);
		builder.append("\t");
		builder.append(event.stateValue);
		logger.println(builder.toString());
		logger.flush();
	}
	
	
	@Reference
	public void setDTOs(DTOs dtos){
		this.dtos = dtos;
	}
}
