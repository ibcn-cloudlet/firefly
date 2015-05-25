package be.iminds.iot.things.api.button;

import be.iminds.iot.things.api.Thing;

public interface Button extends Thing {

    public final static String STATE = "be.iminds.iot.thing.button.state";

    public static enum State {
    	PRESSED, RELEASED, UP, DOWN
    }

    public State[] getPossibleStates();

    public State getState();

}
