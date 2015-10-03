package be.iminds.iot.things.dummy;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.button.Button;

@Component(name="be.iminds.iot.things.dummy.Button",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={Button.class, Thing.class},
		property={Thing.TYPE+"=button"})
public class DummyButton extends DummyThing implements Button {

	boolean isSwitch = true;
	Button.State state = Button.State.UP;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "button";
		
		isSwitch = random.nextDouble() > 0.5;
		if(isSwitch){
			state = Button.State.UP;
		} else {
			state = Button.State.RELEASED;
		}
		
		publishOnline();
		publishChange(Button.STATE, getState());
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}

	@Override
	public State getState() {
		return state;
	}
	
	void setState(Button.State s){
		if(this.state!=s){
			this.state = s;
			publishChange(Button.STATE, getState());
		}
	}

	@Override
	void randomEvent() {
		if(isSwitch){
			if(random.nextFloat() > 0.5f){
				setState(Button.State.UP);
			} else {
				setState(Button.State.DOWN);
			}
		} else {
			setState(Button.State.PRESSED);
			scheduler.after(()-> setState(Button.State.RELEASED), (long)(100+random.nextDouble()*1000));
		}
	}
}
