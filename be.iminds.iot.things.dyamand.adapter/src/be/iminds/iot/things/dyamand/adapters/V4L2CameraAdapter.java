package be.iminds.iot.things.dyamand.adapters;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.dyamand.v4l2.api.V4L2CameraFormat;
import org.dyamand.v4l2.api.V4L2CameraListener;
import org.dyamand.v4l2.api.V4L2CameraServicePOJO;
import org.dyamand.v4l2.api.V4L2CameraServiceType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import aQute.lib.collections.MultiMap;
import be.iminds.iot.things.api.camera.Camera;
import be.iminds.iot.things.api.camera.CameraListener;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

@Component(property={"aiolos.proxy=false"})
public class V4L2CameraAdapter implements ServiceAdapter {

	private final Map<UUID, V4L2CameraServicePOJO> cameras = Collections.synchronizedMap(new HashMap<UUID, V4L2CameraServicePOJO>());
	private final MultiMap<CameraListener, V4L2CameraListener> v4l2listeners = new MultiMap<>(); // map cameralistener to v4l2listeners
	private final Map<CameraListener, UUID> listeners = new HashMap<>(); // keep all cameralisteners and their target UUID
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addCameraListener(CameraListener listener, Map<String, Object> properties){
		UUID target = null;
	    String s = (String) properties.get(CameraListener.CAMERA_ID);
	    if(s!=null){
	    	target = UUID.fromString(s);
	    }
	    listeners.put(listener, target);
	    
	    if(target!=null){
	    	// add only to target camera
	    	addV4L2CameraListener(listener, target);
	    } else {
	    	// add to all cameras
	    	for(UUID cameraId : this.cameras.keySet()){
		    	addV4L2CameraListener(listener, cameraId);
	    	}
	    }
	}
	
	private void addV4L2CameraListener(final CameraListener listener, final UUID cameraId){
		V4L2CameraServicePOJO c = cameras.get(cameraId);
		if(c!=null){
			V4L2CameraListener l = new V4L2CameraListener() {
				@Override
				public void nextFrame(int f, byte[] data) {
					listener.nextFrame(cameraId, Camera.Format.values()[f], data);
				}
			};
			v4l2listeners.add(listener, l);
			
			c.addListener(l);
		}
	}
	
	public void removeCameraListener(CameraListener listener, Map<String, Object> properties){
		List<V4L2CameraListener> ls = v4l2listeners.remove(listener);
		
	    for(Entry<UUID, V4L2CameraServicePOJO> e : V4L2CameraAdapter.this.cameras.entrySet()){
			final V4L2CameraServicePOJO c = e.getValue();
			for(V4L2CameraListener l : ls)
				c.removeListener(l);
	    }
	    
		listeners.remove(listener);
	}
	
	@Override
	public String getType() {
		return "camera";
	}

	@Override
	public String[] getTargets() {
		return new String[] { be.iminds.iot.things.api.Thing.class.getName(),
				be.iminds.iot.things.api.camera.Camera.class.getName() };
	}

	@Override
	public Object getServiceObject(final Object source) throws Exception {
		if (!(source instanceof org.dyamand.v4l2.api.V4L2CameraServicePOJO)) {
			throw new Exception("Cannot translate object!");
		}
		final org.dyamand.v4l2.api.V4L2CameraServicePOJO pojo = (org.dyamand.v4l2.api.V4L2CameraServicePOJO) source;
		// generate same UUID as the adapter does to match listener target UUIDs
		final String device = pojo.getService().getOriginalDevice().getName().toString();
		final String service = pojo.getService().getName().toString();
		final UUID id = UUID.nameUUIDFromBytes((device+service).getBytes());
		this.cameras.put(id, pojo);

		// add all existing listeners that match the new camera
		for(Entry<CameraListener, UUID> e : listeners.entrySet()){
			if(e.getValue()!=null){
				// check if this is target
				if(e.getValue().equals(id)){
					addV4L2CameraListener(e.getKey(), id);
				}
			} else {
				addV4L2CameraListener(e.getKey(), id);
			}
		}
		
		return new Camera() {

			@Override
			public void stop() {
				pojo.stop();
			}

			@Override
			public void start(final int width, final int height, Camera.Format format) {
				pojo.start(width, height, format.ordinal());
			}

			@Override
			public void start() {
				pojo.start(800, 600, V4L2CameraFormat.RGB);
			}

			@Override
			public int getWidth() {
				return pojo.getWidth();
			}

			@Override
			public int getHeight() {
				return pojo.getHeight();
			}

			@Override
			public State getState() {
				return pojo.isOn() ? State.RECORDING : State.OFF;
			}
			
			@Override
			public boolean isOn(){
				return pojo.isOn();
			}

			@Override
			public Format[] getSupportedFormats() {
				// TODO actually query the device?
				return new Format[] { Format.YUV, Format.RGB, Format.GRAYSCALE, Format.MJPEG};
			}

			@Override
			public Format getFormat() {
				return Format.values()[pojo.getFormat()];
			}

			@Override
			public byte[] getFrame() {
				return pojo.getFrame();
			}

			@Override
			public float getFramerate() {
				return pojo.getFramerate();
			}
			
			@Override
			public void setFramerate(float f) {
				pojo.setFramerate(f);
			}

		};
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		StateVariable translated;
		if (variable.equals(V4L2CameraServiceType.CAMERA_STATE.toString())) {
			final boolean on = (Boolean) value;
			final Camera.State translatedValue = on ? Camera.State.RECORDING
					: Camera.State.OFF;
			translated = new StateVariable(Camera.STATE, translatedValue);
		} else {
			// TODO also translate width/height/fps state changes
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}

}
