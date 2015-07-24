package be.iminds.iot.thing.rule.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.rest.api.REST;
import be.iminds.iot.thing.rule.factory.SimpleAction;
import be.iminds.iot.thing.rule.factory.SimpleCondition;
import be.iminds.iot.thing.rule.factory.SimpleRule;
import be.iminds.iot.things.api.Things;
import be.iminds.iot.things.rule.api.RuleEngine;

@Component()
public class SimpleRuleRestEndpoint implements REST {

	private Things things;
	private RuleEngine engine;
	
	public Collection<String> getTypes(){
		return things.getTypes();
	}
	
	public Collection<String> getMethods(String type){
		System.out.println("GET METHODS");
		return things.getMethods(type);
	}
	
	public Collection<String> getVariables(String type){
		System.out.println("GET VARIABLES");
		return things.getStateVariables(type);
	}

	public void putSimpleRule(SimpleRuleDTO dto){
		System.out.println("ADD NEW SIMPLE RULE!");
		List<SimpleCondition> conditions = new ArrayList<SimpleCondition>();
		for(SimpleConditionDTO c : dto.conditions){
			conditions.add(new SimpleCondition(c.thingId, c.type, c.variable, c.operator, c.value));
		}
		
		List<SimpleAction> actions = new ArrayList<SimpleAction>();
		for(SimpleActionDTO a : dto.actions){
			actions.add(new SimpleAction(a.thingId, a.type, a.method, a.args));
		}
		
		SimpleRule rule = new SimpleRule(dto.type, dto.description, conditions, actions);
		engine.addRule(rule);
	}
	
	@Reference
	public void setThings(Things things){
		this.things = things;
	}
	
	@Reference
	public void setRuleEngine(RuleEngine engine){
		this.engine = engine;
	}
}
