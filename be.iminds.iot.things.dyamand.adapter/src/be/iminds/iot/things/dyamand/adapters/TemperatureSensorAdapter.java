package be.iminds.iot.things.dyamand.adapters;

import org.dyamand.sensors.environment.TemperatureSensorServiceType;

import be.iminds.iot.things.api.sensor.temperature.Temperature;
import be.iminds.iot.things.api.sensor.temperature.TemperatureSensor;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

public class TemperatureSensorAdapter implements ServiceAdapter {

	@Override
	public String getType(){
		return "temperature";
	}
	
    @Override
    public String[] getTargets() {
    	return new String[]{
			be.iminds.iot.things.api.Thing.class.getName(),
			be.iminds.iot.things.api.sensor.temperature.TemperatureSensor.class.getName()};
    }

	@Override
	public Object getServiceObject(final Object source) throws Exception {
		if (!(source instanceof org.dyamand.sensors.environment.TemperatureSensor)) {
			throw new Exception("Cannot translate object!");
		}
		final TemperatureSensor sensor = new TemperatureSensor() {

			@Override
			public Temperature getTemperature() {
				final org.dyamand.sensors.environment.Temperature t = ((org.dyamand.sensors.environment.TemperatureSensor) source)
						.getTemperature();
				if(t.getScale().equals(org.dyamand.sensors.environment.Temperature.Scale.CELCIUS)){
					final Temperature temp = new Temperature(t.getValue());
					return temp;
				} else {
					System.err.println("For now we only support sensor values in Celcius");
					return null;
				}
			}

		};

		return sensor;
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		final StateVariable translated;
		if (variable.equals(TemperatureSensorServiceType.TEMPERATURE_STATE_VAR
				.toString())) {
			final org.dyamand.sensors.environment.Temperature temp = (org.dyamand.sensors.environment.Temperature) value;
			if(temp.getScale().equals(org.dyamand.sensors.environment.Temperature.Scale.CELCIUS)){
				final Temperature translatedValue = new Temperature(
						temp.getValue());
				translated = new StateVariable(TemperatureSensor.TEMPERATURE,
						translatedValue);
			} else {
				throw new Exception("For nwo only support sensor values in Celcius");
			}
		} else {
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}

}
