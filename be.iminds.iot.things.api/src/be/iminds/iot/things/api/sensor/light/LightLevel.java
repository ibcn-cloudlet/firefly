package be.iminds.iot.things.api.sensor.light;

import be.iminds.iot.things.api.sensor.SensorValue;

// represents light level in lux
public class LightLevel extends SensorValue {

	public LightLevel(double v) {
		super(v, "lx");
	}

}
