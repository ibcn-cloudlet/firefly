package be.iminds.iot.things.repository.simple.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import osgi.enroute.jsonrpc.api.JSONRPC;
import be.iminds.iot.things.repository.api.Repository;
import be.iminds.iot.things.repository.api.ThingDTO;

/**
 * 
 */
@Component(property = JSONRPC.ENDPOINT + "=be.iminds.iot.things.repository",
service={Repository.class, JSONRPC.class})
public class ThingsRepository implements Repository, JSONRPC {

	private Map<UUID, ThingDTO> things = new HashMap<>();
	
	@Activate
	public void activate(BundleContext context){
		// TODO read from file?
		ThingDTO button = new ThingDTO();
		button.device = "test device";
		button.service = "button service";
		button.id = UUID.nameUUIDFromBytes((button.device+button.service).getBytes());
		button.gateway = UUID.fromString(context.getProperty(Constants.FRAMEWORK_UUID)); // get gateway id
		
		button.name = "Button 1";
		button.type = "button";
		button.location = "Kitchen";
		
		things.put(button.id, button);
		
		// 
		
	}
	
	@Override
	public ThingDTO getThing(UUID id) {
		System.out.println("GET "+id);
		return things.get(id);
	}

	@Override
	public Collection<ThingDTO> listThings() {
		System.out.println("LIST");
		return Collections.unmodifiableCollection(new ArrayList(things.values()));
	}

	@Override
	public void putThing(ThingDTO thing) {
		System.out.println("PUT");
		things.put(thing.id, thing);
	}

	@Override
	public Object getDescriptor() throws Exception {
		// TODO What should this return?
		return null;
	}

}
