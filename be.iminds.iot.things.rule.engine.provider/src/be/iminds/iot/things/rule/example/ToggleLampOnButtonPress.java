package be.iminds.iot.things.rule.example;

import java.util.Collections;
import java.util.UUID;

import be.iminds.iot.things.rule.api.ConditionActionRule;

public class ToggleLampOnButtonPress extends ConditionActionRule {

	public ToggleLampOnButtonPress(String description, UUID buttonId, UUID lampId) {
		super("ToggleLampOnButtonPress", 
				description, 
				Collections.singletonList(new ButtonPressed(buttonId)),
				Collections.singletonList(new ToggleLamp(lampId)));
		
	}

}
