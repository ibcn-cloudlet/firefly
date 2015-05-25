package be.iminds.iot.things.api.sensor.temperature;

import be.iminds.iot.things.api.Thing;

public interface TemperatureSensor extends Thing {

    public final static String TEMPERATURE = "be.iminds.iot.thing.sensor.temperature";

    public Temperature getTemperature();
    
}
