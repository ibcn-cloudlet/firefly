package be.iminds.iot.things.api.sensor.contact;

import be.iminds.iot.things.api.Thing;

public interface ContactSensor extends Thing {

    public final static String STATE = "state";

    public static enum State {
    	OPEN, CLOSED;
    }

    public State getState();
}
