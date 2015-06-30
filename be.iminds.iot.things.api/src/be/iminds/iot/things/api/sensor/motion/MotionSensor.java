package be.iminds.iot.things.api.sensor.motion;

import be.iminds.iot.things.api.Thing;

public interface MotionSensor extends Thing {

	public final static String STATE = "state";

	public static enum State {
		MOTION, NO_MOTION;
	}

	public State getState();
}
