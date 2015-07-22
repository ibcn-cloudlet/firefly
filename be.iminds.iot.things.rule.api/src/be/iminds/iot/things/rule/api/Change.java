package be.iminds.iot.things.rule.api;

import java.util.UUID;

/**
 * Represents the change of a Thing's state variable
 * @author tverbele
 *
 */
public class Change {

	public Change(UUID thingId, String stateVariable, Object value){
		this.thingId = thingId;
		this.stateVariable = stateVariable;
		this.value = value;
	}
	
	public UUID thingId;
	public String stateVariable;
	public Object value;
}
