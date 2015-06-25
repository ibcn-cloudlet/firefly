package be.iminds.iot.things.dyamand.adapters;

import org.dyamand.sensors.environment.LightSensorServiceType;
import org.osgi.service.component.annotations.Component;

import be.iminds.iot.things.api.sensor.light.LightLevel;
import be.iminds.iot.things.api.sensor.light.LightSensor;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

@Component(property={"aiolos.proxy=false"})
public class LightSensorAdapter implements ServiceAdapter {

	@Override
	public String getType(){
		return "light";
	}
	
	@Override
	public String[] getTargets() {
		return new String[] {
				be.iminds.iot.things.api.Thing.class.getName(),
				be.iminds.iot.things.api.sensor.light.LightSensor.class
						.getName() };
	}

	@Override
	public Object getServiceObject(final Object source) throws Exception {
		if (!(source instanceof org.dyamand.sensors.environment.LightSensor)) {
			throw new Exception("Cannot translate object!");
		}
		final LightSensor sensor = new LightSensor() {

			@Override
			public LightLevel getLightLevel() {
				final org.dyamand.sensors.environment.LightLevel l = ((org.dyamand.sensors.environment.LightSensor) source)
						.getLightLevel();
				return new LightLevel(l.getLux());
			}
		};

		return sensor;
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		final StateVariable translated;
		if (variable.equals(LightSensorServiceType.LIGHT_LEVEL_STATE_VAR.toString())) {
			final org.dyamand.sensors.environment.LightLevel l = (org.dyamand.sensors.environment.LightLevel) value;
			final LightLevel translatedValue = new LightLevel(l.getLux());
			translated = new StateVariable(LightSensor.LIGHT, translatedValue);
		} else {
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}
}
