package be.iminds.iot.things.rule.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import be.iminds.iot.things.rule.api.Action;
import be.iminds.iot.things.rule.api.Condition;
import be.iminds.iot.things.rule.api.ConditionActionRule;

public class SimpleRule extends ConditionActionRule {

	public SimpleRule(String type, String description,
			List<SimpleCondition> conditions, List<SimpleAction> actions) {
		super(type, description, conditions, actions);
	}

	public SimpleRule clone(String description, UUID[] sources, UUID[] destinations) throws Exception {
		// TODO check whether sources.length and destinations.length is ok?
		
		List<SimpleCondition> clonedConditions = new ArrayList<SimpleCondition>(sources.length);
		int i = 0;
		for(Condition c : conditions){
			clonedConditions.add(((SimpleCondition)c).clone(sources[i++]));
		}
		
		List<SimpleAction> clonedActions = new ArrayList<SimpleAction>(destinations.length);
		i = 0;
		for(Action a : actions){
			clonedActions.add(((SimpleAction)a).clone(destinations[i++]));
		}
		
		SimpleRule clone = new SimpleRule(type, description, clonedConditions, clonedActions);
		return clone;
	}
}
