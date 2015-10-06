package be.iminds.iot.things.api.event;

import java.util.UUID;

/**
 * Represents an event notifying the change of a thing's state variable.
 * 
 * @author tverbele
 *
 */
public class ChangeEvent {
	
	public UUID thingId; /* id of the thing */
	public UUID gatewayId; /* gateway that published the event */
	public String stateVariable; /* state variable that changed */
	public Object stateValue; /* new value of the state variable */
	public long timestamp; /* timestamp the event was generated */
	
}
