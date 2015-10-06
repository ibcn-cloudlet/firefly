package be.iminds.iot.things.api.event;

import java.util.UUID;

/**
 * Represents an event notifying a thing comes online.
 *
 * @author tverbele
 *
 */
public class OnlineEvent {
	
	public UUID thingId; /* id of the thing */
	public UUID gatewayId; /* gateway that published the event */
	public String service; /* service of the thing */
	public String device; /* device name of the thing */
	public String type; /* thing type */
	public long timestamp; /* timestamp the event was generated */
	
}
