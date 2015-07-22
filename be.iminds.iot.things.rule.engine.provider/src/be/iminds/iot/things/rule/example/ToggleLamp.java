package be.iminds.iot.things.rule.example;

import java.util.UUID;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.lamp.Lamp;
import be.iminds.iot.things.rule.api.Action;

public class ToggleLamp implements Action {

	private UUID id;
	private Lamp lamp;
	
	public ToggleLamp(UUID id) {
		this.id = id;
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getType() {
		return "lamp";
	}

	@Override
	public void setThing(Thing thing) {
		lamp = (Lamp) thing;
	}

	@Override
	public void execute() {
		if(lamp!=null)
			lamp.toggle();
	}

	
	
}
