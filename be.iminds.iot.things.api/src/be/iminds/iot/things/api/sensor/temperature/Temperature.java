package be.iminds.iot.things.api.sensor.temperature;

import be.iminds.iot.things.api.sensor.SensorValue;


/**
 * Class representing a temperature value in degrees Celcius.
 */
public final class Temperature extends SensorValue {

	public Temperature(double value){
		super(value, "\u00b0C");
	}
}
