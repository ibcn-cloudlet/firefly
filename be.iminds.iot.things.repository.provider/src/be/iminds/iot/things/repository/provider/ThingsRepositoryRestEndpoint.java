package be.iminds.iot.things.repository.provider;

import java.util.Collection;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RequireRestImplementation;
import be.iminds.iot.things.repository.api.ThingsRepository;
import be.iminds.iot.things.repository.api.ThingDTO;

@RequireRestImplementation
@Component()
public class ThingsRepositoryRestEndpoint implements REST{

	private ThingsRepository repository;

	public ThingDTO getThing(UUID id) {
		return repository.getThing(id);
	}

	public Collection<ThingDTO> getThing() {
		return repository.getThings();
	}

	public void putThing(ThingDTO thing, UUID id) {
		// TODO only works with our patched rest provider 
		repository.putThing(thing);
	}
	
	@Reference()
	void setRepository(ThingsRepository r){
		this.repository = r;
	}
	
}
