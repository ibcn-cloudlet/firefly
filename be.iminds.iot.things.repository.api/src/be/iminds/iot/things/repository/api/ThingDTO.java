package be.iminds.iot.things.repository.api;

import java.util.Map;
import java.util.UUID;

public class ThingDTO {

	// These are provided by the system (hardware)
	public UUID id; 		/* unique thing id - based on device+service combination */
	public String device; 	/* device name */
	public String service; 	/* service name */
	public UUID gateway; 	/* id of the gateway that provides this thing */
	
	// These are (optionally) provided by the user
	public String name;		/* user defined name of the thing */
	public String location;	/* user defined location the thing is located */
	
	// Type represents the functionality (interface) of this thing
	public String type;		/* type of the device */
	
	public Map<String, Object> state; /* last known state variables */
	
	// TODO do we need to extend this further or have separate DTOs to
	// - keep stuff about the state? online/offline/lastonline/...?
	// - keep stuff about the state variables in this thing?
	// - keep history of thing events?
}
