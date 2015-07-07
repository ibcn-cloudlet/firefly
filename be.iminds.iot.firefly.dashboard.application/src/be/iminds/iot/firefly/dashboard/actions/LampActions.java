package be.iminds.iot.firefly.dashboard.actions;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import be.iminds.iot.firefly.dashboard.Actions;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.lamp.Lamp;

@Component(property={"aiolos.proxy=false"})
public class LampActions implements Actions {

	private Map<UUID, Lamp> lamps = Collections.synchronizedMap(new HashMap<UUID, Lamp>());
	
	@Override
	public String getType() {
		return "lamp";
	}

	@Override
	public void action(UUID id, String... params) {
		Lamp lamp = lamps.get(id);
		if(lamp!=null){
			if(params.length==0){
				// default action - switch camera on/off
				lamp.toggle();
			} else {
				switch(params[0]){
				case "level":
					lamp.setLevel(Integer.parseInt(params[1]));
					break;
				case "color":
					Color c = Color.decode(params[1]);
					lamp.setColor(c);
					break;
				}
			}
		}
	}

	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addLamp(Lamp l, Map<String, Object> properties){
		UUID id = (UUID) properties.get(Thing.ID);
		lamps.put(id, l);
	}
	
	public void removeLamp(Lamp l, Map<String, Object> properties){
		UUID id = (UUID) properties.get(Thing.ID);
		lamps.remove(id);
	}

	
}
