package be.iminds.iot.things.dyamand.adapters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.dyamand.v4l2.api.V4L2CameraFormat;
import org.dyamand.v4l2.api.V4L2CameraListener;
import org.dyamand.v4l2.api.V4L2CameraServicePOJO;
import org.dyamand.v4l2.api.V4L2CameraServiceType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import be.iminds.iot.things.api.camera.Camera;
import be.iminds.iot.things.api.camera.CameraListener;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

public class V4L2CameraAdapter implements ServiceAdapter {

	private final Map<UUID, V4L2CameraServicePOJO> cameras = Collections.synchronizedMap(new HashMap<UUID, V4L2CameraServicePOJO>());
	private final Map<CameraListener, V4L2CameraListener> listeners = Collections.synchronizedMap(new HashMap<>());
	
	public V4L2CameraAdapter(BundleContext context){
		final ServiceTracker<CameraListener, CameraListener> tracker = new ServiceTracker<CameraListener, CameraListener>(
				context, CameraListener.class,
				new ServiceTrackerCustomizer<CameraListener, CameraListener>() {

				    @Override
				    public CameraListener addingService(
					    final ServiceReference<CameraListener> reference) {
						final CameraListener listener = context.getService(reference);
						if (listener != null) {
							V4L2CameraListener l = new V4L2CameraListener() {
								@Override
								public void nextFrame(int f, byte[] data) {
									listener.nextFrame(Camera.Format.values()[f], data);
								}
							};
							listeners.put(listener, l);
							
							// check target, else add as listener for all cameras
						    final String target = (String) reference
							    .getProperty(CameraListener.CAMERA_ID);
						    if(target!=null){
							    final UUID id = UUID.fromString(target);
								final V4L2CameraServicePOJO c = V4L2CameraAdapter.this.cameras.get(id);
							    if (c != null) {
									c.addListener(l);
							    }
						    } else {
						    	for (final V4L2CameraServicePOJO c : V4L2CameraAdapter.this.cameras
										.values()) {
									    c.addListener(l);
								}
						    }
						}
						return listener;
				    }

				    @Override
				    public void modifiedService(
					    final ServiceReference<CameraListener> reference,
					    final CameraListener listener) {
					// do nothing
				    }

				    @Override
				    public void removedService(
					    final ServiceReference<CameraListener> reference,
					    final CameraListener listener) {
				    	
				    	V4L2CameraListener l = listeners.remove(listener);
				    	
						final String target = (String) reference
							.getProperty(CameraListener.CAMERA_ID);
						if (target != null) {
						    final UUID id = UUID.fromString(target);
						    final V4L2CameraServicePOJO c = V4L2CameraAdapter.this.cameras.get(id);
						    if (c != null) {
						    	c.removeListener(l);
						    }
						} else {
						    for (final V4L2CameraServicePOJO c : V4L2CameraAdapter.this.cameras.values()) {
						    	c.removeListener(l);
						    }
						}
				    }
				});
			tracker.open();
		
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
		System.out.println("ADAPT TO CAMERA? "+ source.getClass().getName());
		if (!(source instanceof org.dyamand.v4l2.api.V4L2CameraServicePOJO)) {
			throw new Exception("Cannot translate object!");
		}
		final org.dyamand.v4l2.api.V4L2CameraServicePOJO pojo = (org.dyamand.v4l2.api.V4L2CameraServicePOJO) source;
		this.cameras.put(pojo.getService().getOriginalDevice().getId(), pojo);
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
				return pojo.isOn() ? State.ON : State.OFF;
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

		};
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		StateVariable translated;
		if (variable.equals(V4L2CameraServiceType.CAMERA_STATE.toString())) {
			final boolean on = (Boolean) value;
			final Camera.State translatedValue = on ? Camera.State.ON
					: Camera.State.OFF;
			translated = new StateVariable(Camera.STATE, translatedValue);
		} else {
			// TODO also translate width/height state changes
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}

}
