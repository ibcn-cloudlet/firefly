package be.iminds.iot.things.rule.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.api.lamp.Lamp;
import be.iminds.iot.things.api.sensor.contact.ContactSensor;
import be.iminds.iot.things.api.sensor.motion.MotionSensor;
import be.iminds.iot.things.rule.api.Rule;
import be.iminds.iot.things.rule.api.RuleDTO;
import be.iminds.iot.things.rule.api.RuleFactory;
import be.iminds.iot.things.rule.factory.SimpleCondition.Operator;

@Component
public class SimpleRuleFactory implements RuleFactory {

	private Map<String, SimpleRule> templates = new HashMap<String, SimpleRule>();
	
	@Activate
	public void activate(){
		// create SimpleRule instances with null ids to act as templates
		
		// toggle lamp on button
		createTemplate(new SimpleRule("ToggleLampFromButtonPress", 
								"Toggle {{destination.name}} when {{source.name}} becomes pressed", 
								Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.BECOMES, Button.State.PRESSED)), 
								Collections.singletonList(new SimpleAction(null, "lamp", "toggle"))));
		createTemplate(new SimpleRule("ToggleLampFromButtonChange", 
				"Toggle {{destination.name}} when {{source.name}} state changes", 
				Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.CHANGES, null)), 
				Collections.singletonList(new SimpleAction(null, "lamp", "toggle"))));
		createTemplate(new SimpleRule("ToggleLampFromButtonDown", 
				"Toggle {{destination.name}} when {{source.name}} state becomes down", 
				Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.BECOMES, Button.State.DOWN)), 
				Collections.singletonList(new SimpleAction(null, "lamp", "toggle"))));
		
		// toggle lamp on lamp
		createTemplate(new SimpleRule("ToggleLampFromLamp", 
				"Toggle {{destination.name}} when {{source.name}} state changes", 
				Collections.singletonList(new SimpleCondition(null, "lamp", Lamp.STATE, Operator.CHANGES, null)), 
				Collections.singletonList(new SimpleAction(null, "lamp", "toggle"))));
		
		// turn brightness up or down on press
		createTemplate(new SimpleRule("DimLampFromButtonPress", 
				"Decrease {{destination.name}} brightness when {{source.name}} becomes pressed", 
				Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.BECOMES, Button.State.PRESSED)), 
				Collections.singletonList(new SimpleAction(null, "lamp", "decrementLevel", 20))));
		
		createTemplate(new SimpleRule("BrightenLampFromButtonPress", 
				"Increase {{destination.name}} brightness when {{source.name}} becomes pressed", 
				Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.BECOMES, Button.State.PRESSED)), 
				Collections.singletonList(new SimpleAction(null, "lamp", "incrementLevel", 20))));
		
		// toggle camera on button
		createTemplate(new SimpleRule("ToggleCameraFromButtonPress", 
				"Toggle {{destination.name}} when {{source.name}} becomes pressed", 
				Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.BECOMES, Button.State.PRESSED)), 
				Collections.singletonList(new SimpleAction(null, "camera", "toggle"))));
		createTemplate(new SimpleRule("ToggleCameraFromButtonChange", 
				"Toggle {{destination.name}} when {{source.name}} state changes", 
				Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.CHANGES, null)), 
				Collections.singletonList(new SimpleAction(null, "camera", "toggle"))));
		createTemplate(new SimpleRule("ToggleCameraFromButtonDown", 
				"Toggle {{destination.name}} when {{source.name}} state becomes down", 
				Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.BECOMES, Button.State.DOWN)), 
				Collections.singletonList(new SimpleAction(null, "camera", "toggle"))));	
		
		// toggle lamp on door
		createTemplate(new SimpleRule("ToggleLampFromContactChange", 
				"Toggle {{destination.name}} when {{source.name}} state changes", 
				Collections.singletonList(new SimpleCondition(null, "contact", ContactSensor.STATE, Operator.CHANGES, null)), 
				Collections.singletonList(new SimpleAction(null, "lamp", "toggle"))));
		
		// toggle camera on door
		createTemplate(new SimpleRule("ToggleCameraFromContactChange", 
				"Toggle {{destination.name}} when {{source.name}} state changes", 
				Collections.singletonList(new SimpleCondition(null, "contact", ContactSensor.STATE, Operator.CHANGES, null)), 
				Collections.singletonList(new SimpleAction(null, "camera", "toggle"))));
		
		// toggle lamp on motion
		createTemplate(new SimpleRule("ToggleLampFromMotionChange", 
				"Toggle {{destination.name}} when {{source.name}} state changes", 
				Collections.singletonList(new SimpleCondition(null, "motion", MotionSensor.STATE, Operator.CHANGES, null)), 
				Collections.singletonList(new SimpleAction(null, "lamp", "toggle"))));
		
		// toggle camera on motion
		createTemplate(new SimpleRule("ToggleCameraFromMotionChange", 
				"Toggle {{destination.name}} when {{source.name}} state changes", 
				Collections.singletonList(new SimpleCondition(null, "motion", MotionSensor.STATE, Operator.CHANGES, null)), 
				Collections.singletonList(new SimpleAction(null, "camera", "toggle"))));
		
	}
	
	@Override
	public Rule createRule(RuleDTO dto)
			throws Exception {
		Rule rule = null;
		
		// use the cloning mechanism of SimpleRules to create new ones
		SimpleRule template = templates.get(dto.type);
		if(template!=null){
			rule = template.clone(dto.description, dto.sources, dto.destinations);
		}
		return rule;
	}

	@Override
	public Collection<RuleDTO> getTemplates() {
		return Collections.unmodifiableCollection(
				templates.values().stream().map(r -> r.getDTO()).collect(Collectors.toList()));
	}

	public void createTemplate(SimpleRule rule){
		templates.put(rule.getType(), rule);
	}
	
}
