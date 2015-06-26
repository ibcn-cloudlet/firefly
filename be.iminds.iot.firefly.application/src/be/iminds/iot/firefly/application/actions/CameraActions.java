package be.iminds.iot.firefly.application.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import be.iminds.iot.firefly.application.Actions;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.camera.Camera;

@Component(property={"aiolos.proxy=false"})
public class CameraActions implements Actions {

	private Map<UUID, Camera> cameras = Collections.synchronizedMap(new HashMap<UUID, Camera>());
	
	@Override
	public String getType() {
		return "camera";
	}

	@Override
	public void action(UUID id, String... params) {
		Camera camera = cameras.get(id);
		if(camera!=null){
			if(params.length==0){
				// default action - switch camera on/off
				if(camera.isOn()){
					camera.stop();
				} else {
					camera.start();
				}
			}
		}
	}

	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addCamera(Camera c, Map<String, Object> properties){
		UUID id = (UUID) properties.get(Thing.ID);
		cameras.put(id, c);
	}
	
	public void removeCamera(Camera c, Map<String, Object> properties){
		UUID id = (UUID) properties.get(Thing.ID);
		cameras.remove(id);
	}

}
