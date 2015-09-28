package be.iminds.iot.things.api.event;

import java.util.UUID;

public class OfflineEvent {
	public UUID thingId;
	public UUID gatewayId;
	public long timestamp;
}
