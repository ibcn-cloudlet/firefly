package be.iminds.iot.things.api.lamp;

import java.awt.Color;

import be.iminds.iot.things.api.Thing;

public interface Lamp extends Thing {

    public final static String STATE = "be.iminds.iot.thing.lamp.state";
    public final static String LEVEL = "be.iminds.iot.thing.lamp.level";
    public final static String COLOR = "be.iminds.iot.thing.lamp.color";

    
    public static enum State {
    	OFF, ON;
    }

    public State getState();

    public void on();

    public void off();

    public void toggle();

    public int getLevel();

    public void setLevel(final int level);

    public void incrementLevel(final int increment);

    public void decrementLevel(final int decrement);

    public Color getColor();

    public void setColor(Color c);

}
