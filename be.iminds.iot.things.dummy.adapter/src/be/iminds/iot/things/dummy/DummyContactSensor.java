package be.iminds.iot.things.dummy;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.api.sensor.contact.ContactSensor;

@Component(name="be.iminds.iot.things.dummy.ContactSensor",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={ContactSensor.class, Thing.class},
		property={Thing.TYPE+"=contact"})
public class DummyContactSensor extends DummyThing implements ContactSensor {

	ContactSensor.State state = ContactSensor.State.CLOSED;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "contact";
		
		publishOnline();
		publishChange(ContactSensor.STATE, getState());
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}

	@Override
	public State getState() {
		return state;
	}
	
	void setState(ContactSensor.State s){
		if(this.state!= s){
			this.state = s;
			publishChange(Button.STATE, getState());
		}
	}

	@Override
	void randomEvent() {
		if(random.nextFloat() > 0.5f){
			setState(ContactSensor.State.CLOSED);
		} else {
			setState(ContactSensor.State.OPEN);
		}
	}
	

}
