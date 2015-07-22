package be.iminds.iot.things.rule.example;

import java.util.UUID;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.rule.api.Change;
import be.iminds.iot.things.rule.api.Condition;

public class ButtonPressed implements Condition {

	private final UUID id;
	
	public ButtonPressed(UUID id){
		this.id = id;
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getType() {
		return "button";
	}

	@Override
	public void setThing(Thing thing) {
		// thing object not needed
	}

	@Override
	public boolean trigger(Change change) {
		if(!change.thingId.equals(id))
			return false;
		
		if(!change.stateVariable.equals(Button.STATE))
			return false;
		
		if(change.value.equals(Button.State.PRESSED))
			return true;
		
		return false;
	}

}
