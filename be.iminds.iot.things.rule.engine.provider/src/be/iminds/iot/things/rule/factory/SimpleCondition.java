package be.iminds.iot.things.rule.factory;

import java.util.UUID;

import aQute.lib.converter.Converter;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.rule.api.Change;
import be.iminds.iot.things.rule.api.Condition;

public class SimpleCondition implements Condition {

	public enum Operator {BECOMES,IS,IS_NOT,IS_GREATER,IS_LESS};
	
	private final UUID id;
	private final String type;
	private final String variable;
	private Operator operator;
	private Object value; 
	
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
		
		// Try to convert in case of non matching classes
		// This is to attempt to handle any String values coming from web interface
		if(!currentValue.getClass().equals(value.getClass())){
			try {
				value = Converter.cnv(currentValue.getClass(), value);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
