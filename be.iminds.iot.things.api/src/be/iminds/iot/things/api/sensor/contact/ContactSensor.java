package be.iminds.iot.things.api.sensor.contact;

import be.iminds.iot.things.api.Thing;

public interface ContactSensor extends Thing {

    public final static String STATE = "be.iminds.iot.thing.sensor.contact";

    public static enum State {
    	OPEN, CLOSED;
    }

    public State getState();
}
