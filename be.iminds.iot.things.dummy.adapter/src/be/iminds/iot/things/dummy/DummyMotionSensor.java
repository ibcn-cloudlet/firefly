package be.iminds.iot.things.dummy;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.api.sensor.contact.ContactSensor;
import be.iminds.iot.things.api.sensor.motion.MotionSensor;

@Component(name="be.iminds.iot.things.dummy.MotionSensor",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={MotionSensor.class, Thing.class},
		property={Thing.TYPE+"=motion"})
public class DummyMotionSensor extends DummyThing implements MotionSensor {

	MotionSensor.State state = MotionSensor.State.NO_MOTION;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "motion";
		
		publishOnline();
		publishChange(MotionSensor.STATE, getState());
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}

	@Override
	public State getState() {
		return state;
	}
	
	void setState(MotionSensor.State s){
		if(this.state != s){
			this.state = s;
			publishChange(Button.STATE, getState());
		}
	}
	
	@Override
	void randomEvent() {
		if(random.nextFloat() > 0.5f){
			setState(MotionSensor.State.MOTION);
		} else {
			setState(MotionSensor.State.NO_MOTION);
		}
	}

}
