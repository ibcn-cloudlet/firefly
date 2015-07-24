package be.iminds.iot.things.repository.simple.provider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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

import aQute.lib.converter.TypeReference;
import aQute.lib.json.JSONCodec;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.repository.api.Repository;
import be.iminds.iot.things.repository.api.ThingDTO;

/**
 * 
 */
@Component(property={"event.topics=be/iminds/iot/thing/*"})
public class ThingsRepository implements Repository, EventHandler {
	
	private final static JSONCodec json = new JSONCodec();
	
	private Map<UUID, ThingDTO> things = Collections.synchronizedMap(new HashMap<>());
	private Set<UUID> online = Collections.synchronizedSet(new HashSet<>());
	
	private Writer logger;
	
	@Activate
	public void activate(BundleContext context){
		// load thing dtos from file
		try {
			things = (Map<UUID, ThingDTO>) json.dec().from(new File("things.txt")).get(new TypeReference<Map<UUID,ThingDTO>>(){});
		} catch(Exception e){
			System.err.println("Failed to load thing descriptions from file");
		}
		
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
		try {
			logger.close();
		} catch (IOException ioe) {
			// ignore
		}
		
		// write thing dtos to file
		try {
			json.enc().indent("\t").to(new File("things.txt")).put(things).close();
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
				result.add(things.get(id));
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
		UUID id = (UUID) event.getProperty(Thing.ID);
		ThingDTO thing;
		synchronized(things){
			thing = things.get(id);
			if(thing==null){
				if(event.getTopic().startsWith("be/iminds/iot/thing/online/")){
					thing = new ThingDTO();
					thing.id = id;
					thing.gateway = (UUID) event.getProperty(Thing.GATEWAY);
					thing.device = (String) event.getProperty(Thing.DEVICE);
					thing.service = (String) event.getProperty(Thing.SERVICE);
					thing.type = (String) event.getProperty(Thing.TYPE);
					thing.name = thing.service;
					
					
					things.put(id, thing);
				}
			} else {
				// update gateway - could be changed
				thing.gateway = (UUID) event.getProperty(Thing.GATEWAY);
			}
		}
		
		if(thing!=null){
			if(event.getTopic().startsWith("be/iminds/iot/thing/online/")){
				online.add(thing.id);
			} else if(event.getTopic().startsWith("be/iminds/iot/thing/offline/")){
				online.remove(thing);
			} else if(event.getTopic().startsWith("be/iminds/iot/thing/change/")){
				online.add(thing.id);
				
				String name = (String) event.getProperty(Thing.STATE_VAR);
				Object val = event.getProperty(Thing.STATE_VAL);
				
				if(thing.state == null){
					thing.state = new HashMap<>();
				}
				thing.state.put(name, val);
			}
		}

		logEvent(event);
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addThing(Thing t, Map<String, Object> properties){
		// mark online
		UUID id = (UUID) properties.get(Thing.ID);
		
		// also init here in case of missed online event
		ThingDTO thing;
		synchronized(things){
			thing = things.get(id);
			if(thing==null){
				thing.gateway = (UUID) properties.get(Thing.GATEWAY);
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

		online.add(id);
		
		// This does not update UI, also send service online thing event?
		// FIXME ? This could lead to many duplicates though 
		ea.postEvent(new Event("be/iminds/iot/thing/online/"+id, properties));
	}
	
	public void removeThing(Thing t, Map<String, Object> properties){
		// mark offline
		UUID id = (UUID) properties.get(Thing.ID);
		online.remove(id);
		
		// This does not update UI - as no event will be sent when the gateway
		// is just stopped, we send an event of this service on our own...
		ea.postEvent(new Event("be/iminds/iot/thing/offline/"+id, properties));
	}
	
	private EventAdmin ea;
	
	@Reference
	public void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
	
	private void logEvent(Event event){
		String type = "change";
		if(event.getTopic().startsWith("be/iminds/iot/thing/online/")){
			type = "online";
		} else if(event.getTopic().startsWith("be/iminds/iot/thing/offline/")){
			type = "offline";
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(event.getProperty("timestamp"));
		builder.append("\t");
		builder.append(event.getProperty(Thing.ID));
		builder.append("\t");
		builder.append(event.getProperty(Thing.GATEWAY));
		builder.append("\t");
		builder.append(type);

		if(type.equals("online")){
			builder.append("\t");
			builder.append(event.getProperty(Thing.DEVICE));
			builder.append("\t");
			builder.append(event.getProperty(Thing.SERVICE));
			builder.append("\t");
			builder.append(event.getProperty(Thing.TYPE));
		} else if(type.equals("change")){
			builder.append("\t");
			builder.append(event.getProperty(Thing.STATE_VAR));
			builder.append("\t");
			builder.append(event.getProperty(Thing.STATE_VAL));
		}

		try {
			builder.append("\n");
			logger.write(builder.toString());
			logger.flush();
		} catch(IOException e){
			// ignore
		}
	}
}
