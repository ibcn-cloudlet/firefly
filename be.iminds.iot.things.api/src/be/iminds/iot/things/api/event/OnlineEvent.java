package be.iminds.iot.things.api.event;

import java.util.UUID;

public class OnlineEvent {
	public UUID thingId;
	public UUID gatewayId;
	public String service;
	public String device;
	public String type;
	public long timestamp;
	
}
