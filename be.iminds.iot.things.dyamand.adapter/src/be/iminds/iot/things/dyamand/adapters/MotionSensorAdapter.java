package be.iminds.iot.things.dyamand.adapters;

import org.dyamand.sensors.occupancy.MotionSensorServiceType;

import be.iminds.iot.things.api.sensor.motion.MotionSensor;
import be.iminds.iot.things.api.sensor.motion.MotionSensor.State;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

public class MotionSensorAdapter implements ServiceAdapter {

	@Override
	public String[] getTargets() {
		return new String[] {
				be.iminds.iot.things.api.Thing.class.getName(),
				be.iminds.iot.things.api.sensor.motion.MotionSensor.class
						.getName() };
	}

	@Override
	public Object getServiceObject(final Object source) throws Exception {
		if (!(source instanceof org.dyamand.sensors.occupancy.MotionSensor)) {
			throw new Exception("Cannot translate object!");
		}

		final MotionSensor sensor = new MotionSensor() {
			@Override
			public State getState() {
				final org.dyamand.sensors.occupancy.MotionSensor m = (org.dyamand.sensors.occupancy.MotionSensor) source;
				return m.isMotionDetected() ? State.MOTION : State.NO_MOTION;
			}
		};
		return sensor;
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		StateVariable translated;
		if (variable
				.equals(MotionSensorServiceType.MOTION_STATE_VAR.toString())) {
			final boolean motion = (Boolean) value;
			final State translatedValue = motion ? State.MOTION
					: State.NO_MOTION;
			translated = new StateVariable(MotionSensor.STATE, translatedValue);
		} else {
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}

}
