package be.iminds.iot.thing.rule.factory;

import java.util.UUID;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.rule.api.Change;
import be.iminds.iot.things.rule.api.Condition;

public class SimpleCondition implements Condition {

	public enum Operator {BECOMES,IS,IS_NOT,IS_GREATER,IS_LESS};
	
	private final UUID id;
	private final String type;
	private final String variable;
	private final Operator operator;
	private final Object value;
	
	private Object currentValue;
	
	public SimpleCondition(
			UUID id,
			String type, 
			String variable, 
			Operator operator, 
			Object value) {
		this.id = id;
		this.type = type;
		this.variable = variable;
		this.operator = operator;
		this.value = value;
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setThing(Thing thing) {
		// ignore
	}

	@Override
	public boolean trigger(Change change) {
		boolean changed = false;
		if(change.thingId.equals(id)
			&& change.stateVariable.equals(variable)){
			currentValue = change.value;
			changed = true;
		}
		
		switch(operator) {
		case BECOMES:
			return changed && currentValue.equals(value);
		case IS:
			return currentValue.equals(value);
		case IS_NOT:
			return !currentValue.equals(value);
		case IS_GREATER:
			return ((Comparable)currentValue).compareTo(value) > 0;
		case IS_LESS:
			return ((Comparable)currentValue).compareTo(value) < 0;
		}
		return false;
	}

	public SimpleCondition clone(UUID id){
		return new SimpleCondition(id, type, variable, operator, value);
	}
}
