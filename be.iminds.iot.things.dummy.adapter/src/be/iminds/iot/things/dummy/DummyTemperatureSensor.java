package be.iminds.iot.things.dummy;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.api.sensor.contact.ContactSensor;
import be.iminds.iot.things.api.sensor.temperature.Temperature;
import be.iminds.iot.things.api.sensor.temperature.TemperatureSensor;

@Component(name="be.iminds.iot.things.dummy.TemperatureSensor",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={TemperatureSensor.class, Thing.class},
		property={Thing.TYPE+"=temperature"})
public class DummyTemperatureSensor extends DummyThing implements TemperatureSensor {

	private double temperature = 20.2;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "temperature";
		
		publishOnline();
		publishChange(TemperatureSensor.TEMPERATURE, new Temperature(temperature));
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}

	@Override
	public Temperature getTemperature() {
		return new Temperature(temperature);
	}

	void setTemperature(double temp){
		this.temperature = temp;
		publishChange(TemperatureSensor.TEMPERATURE, new Temperature(temperature));
	}
	
	@Override
	void randomEvent() {
		double t = (random.nextDouble()-0.5)*10;
		setTemperature(temperature+t);
	}
}
