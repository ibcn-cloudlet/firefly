package be.iminds.iot.things.api.sensor.light;

import be.iminds.iot.things.api.Thing;

public interface LightSensor extends Thing {

    public final static String LIGHT = "be.iminds.iot.thing.sensor.light";

    public LightLevel getLightLevel();

}
