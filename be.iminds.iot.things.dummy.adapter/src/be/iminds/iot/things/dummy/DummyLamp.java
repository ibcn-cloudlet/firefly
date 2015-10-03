package be.iminds.iot.things.dummy;

import java.awt.Color;
import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.lamp.Lamp;
import be.iminds.iot.things.api.sensor.contact.ContactSensor;

@Component(name="be.iminds.iot.things.dummy.Lamp",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={Lamp.class, Thing.class},
		property={Thing.TYPE+"=lamp"})
public class DummyLamp extends DummyThing implements Lamp {

	private State state = State.OFF;
	private int level = 100;
	private Color color = Color.RED;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "lamp";
		
		publishOnline();
		publishChange(Lamp.STATE, getState());
		publishChange(Lamp.LEVEL, getLevel());
		publishChange(Lamp.COLOR, getColor());
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}
	
	@Override
	public State getState() {
		return state;
	}

	void setState(Lamp.State s){
		if(state!=s){
			state = s;
			publishChange(Lamp.STATE, getState());
		}
	}
	
	@Override
	public void on() {
		setState(State.ON);
	}

	@Override
	public void off() {
		setState(State.OFF);
	}

	@Override
	public void toggle() {
		setState(state==State.ON ? State.OFF : State.ON);
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
		publishChange(Lamp.LEVEL, this.level);
	}

	@Override
	public void incrementLevel(int increment) {
		setLevel(level + increment);
	}

	@Override
	public void decrementLevel(int decrement) {
		setLevel(level - decrement);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color c) {
		this.color = c;
		publishChange(Lamp.COLOR, color);
	}

	@Override
	void randomEvent() {
		// don't do anything with Lamp - no sensor
	}

}
