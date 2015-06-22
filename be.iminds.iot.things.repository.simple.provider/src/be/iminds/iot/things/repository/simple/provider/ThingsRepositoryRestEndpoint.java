package be.iminds.iot.things.repository.simple.provider;

import java.util.Collection;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import be.iminds.iot.things.repository.api.Repository;
import be.iminds.iot.things.repository.api.ThingDTO;

@Component()
public class ThingsRepositoryRestEndpoint implements REST{

	private Repository repository;

	@Reference()
	public void setRepository(Repository r){
		this.repository = r;
	}
	
	public ThingDTO getThing(RESTRequest rq, UUID id) {
		return repository.getThing(id);
	}

	public Collection<ThingDTO> getThing(RESTRequest rq) {
		return repository.getThings();
	}

	public void putThing(RESTRequest rq, ThingDTO thing) {
		repository.putThing(thing);
	}
	
}
