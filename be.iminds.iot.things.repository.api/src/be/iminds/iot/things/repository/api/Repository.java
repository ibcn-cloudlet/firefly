package be.iminds.iot.things.repository.api;

import java.util.Collection;
import java.util.UUID;

/**
 * Things repository - persists all info about things
 */
public interface Repository {
	
	public ThingDTO getThing(UUID id);
	
	public Collection<ThingDTO> listThings();
	
	public void putThing(ThingDTO thing);
	
}
