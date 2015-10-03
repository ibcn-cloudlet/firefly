package be.iminds.iot.things.dummy;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.sensor.contact.ContactSensor;
import be.iminds.iot.things.api.sensor.light.LightLevel;
import be.iminds.iot.things.api.sensor.light.LightSensor;

@Component(name="be.iminds.iot.things.dummy.LightSensor",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={LightSensor.class, Thing.class},
		property={Thing.TYPE+"=light"})
public class DummyLightSensor extends DummyThing implements LightSensor {

	private double level = 222;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "light";
		
		publishOnline();
		publishChange(LightSensor.LIGHTLEVEL, new LightLevel(level));
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}

	@Override
	public LightLevel getLightLevel() {
		return new LightLevel(level);
	}
	
	void setLightLevel(double level){
		this.level = level;
		publishChange(LightSensor.LIGHTLEVEL, new LightLevel(level));
	}

	@Override
	void randomEvent() {
		double l = (random.nextDouble()-0.5)*10;
		setLightLevel(level+l);
	}
}
