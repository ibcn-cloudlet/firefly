package be.iminds.iot.thing.rule.endpoint;

import java.util.UUID;

import be.iminds.iot.thing.rule.factory.SimpleCondition.Operator;

public class SimpleConditionDTO {

	public UUID thingId;
	public String type;
	public String variable;
	public Operator operator;
	public String value;
	
}
