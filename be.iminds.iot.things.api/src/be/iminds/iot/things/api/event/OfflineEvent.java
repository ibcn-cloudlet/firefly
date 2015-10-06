package be.iminds.iot.things.api.event;

import java.util.UUID;

/**
 * Represents an event notifying a thing goes offline.
 *
 * @author tverbele
 *
 */
public class OfflineEvent {
	
	public UUID thingId; /* id of the thing */
	public UUID gatewayId; /* gateway that published the event */
	public long timestamp; /* timestamp the event was generated */
	
}
