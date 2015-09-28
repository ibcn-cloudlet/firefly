package be.iminds.iot.things.api.event;

import java.util.UUID;

public class ChangeEvent {
	public UUID thingId;
	public UUID gatewayId;
	public String stateVariable;
	public Object stateValue;
	public long timestamp;
}
