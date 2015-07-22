package be.iminds.iot.things.rule.api;

import java.util.List;
import java.util.UUID;

import be.iminds.iot.things.api.Thing;

/**
 * A Rule implementation that triggers Actions when a number of Conditions are met.
 * 
 * @author tverbele
 *
 */
public class ConditionActionRule implements Rule {

	protected final String type;
	protected final String description;
	protected final List<? extends Condition> conditions;
	protected final List<? extends Action> actions;
	
	public ConditionActionRule(String type, String description, 
			List<? extends Condition> conditions,
			List<? extends Action> actions){
		
		this.type = type;
		this.description = description;
		this.conditions = conditions;
		this.actions = actions;
	}
	
	@Override
	public boolean evaluate(Change change) {
		boolean fire = true;
		for(Condition c : conditions){
			fire = fire && c.trigger(change);
		}
		if(fire){
			for(Action a : actions){
				a.execute();
			}
		}
		return fire;
	}

	@Override
	public String getType(){
		return type;
	}
	
	@Override
	public String getDescription(){
		return description;
	}
	
	@Override
	public RuleDTO getDTO() {
		RuleDTO dto = new RuleDTO();
		dto.description = description;
		dto.type = type;
		
		int size = conditions.size();
		dto.sourceTypes = new String[size];
		dto.sources = new UUID[size];
		int i = 0;
		for(Condition c : conditions){
			dto.sourceTypes[i] = c.getType();
			dto.sources[i] = c.getId();
			i++;
		}
		
		size = actions.size();
		dto.destinationTypes = new String[size];
		dto.destinations = new UUID[size];
		i = 0;
		for(Action a : actions){
			dto.destinationTypes[i] = a.getType();
			dto.destinations[i] = a.getId();
			i++;
		}
		
		return dto;
	}

	@Override
	public void setThing(UUID id, Thing thing) {
		for(Condition c : conditions){
			if(id.equals(c.getId())){
				c.setThing(thing);
			}
		}
		for(Action a : actions){
			if(id.equals(a.getId())){
				a.setThing(thing);
			}
		}
	}
}
