package be.iminds.iot.things.repository.simple.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.event.Event;
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
	
	private Map<UUID, ThingDTO> things = new HashMap<>();
	private Set<ThingDTO> online = new HashSet<>();
	
	
	@Activate
	public void activate(BundleContext context){
		try {
			things = (Map<UUID, ThingDTO>) json.dec().from(new File("things.txt")).get(new TypeReference<Map<UUID,ThingDTO>>(){});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Deactivate
	public void deactivate(){
		try {
			json.enc().indent("\t").to(new File("things.txt")).put(things).close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public ThingDTO getThing(UUID id) {
		System.out.println("GET "+id);
		return things.get(id);
	}

	@Override
	public Collection<ThingDTO> getThings() {
		// only return online things
		System.out.println("LIST "+things.values().size());
		return Collections.unmodifiableCollection(new ArrayList(online));
		//return Collections.unmodifiableCollection(new ArrayList(things.values()));
		
	}

	@Override
	public void putThing(ThingDTO thing) {
		System.out.println("PUT");
		things.put(thing.id, thing);
	}

	@Override
	public void handleEvent(Event event) {
		UUID id = (UUID) event.getProperty(Thing.ID);
		ThingDTO thing = things.get(id);
		if(thing==null){
			thing = new ThingDTO();
			thing.id = id;
			thing.gateway = (UUID) event.getProperty(Thing.GATEWAY);
			thing.device = (String) event.getProperty(Thing.DEVICE);
			thing.service = (String) event.getProperty(Thing.SERVICE);
			thing.type = (String) event.getProperty(Thing.TYPE);
			thing.name = thing.service;
			
			things.put(id, thing);
		}
		
		if(event.getTopic().startsWith("be/iminds/iot/thing/online/")){
			online.add(thing);
		} else if(event.getTopic().startsWith("be/iminds/iot/thing/offline/")){
			online.remove(thing);
		} else if(event.getTopic().startsWith("be/iminds/iot/thing/change/")){
			
			String name = (String) event.getProperty(Thing.STATE_VAR);
			Object val = event.getProperty(Thing.STATE_VAL);
			
			if(thing.state == null){
				thing.state = new HashMap<>();
			}
			thing.state.put(name, val);
		}

		// TODO log all events
	}

}
