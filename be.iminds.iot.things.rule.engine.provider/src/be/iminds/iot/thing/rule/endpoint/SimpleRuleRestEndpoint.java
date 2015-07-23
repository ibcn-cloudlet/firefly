package be.iminds.iot.thing.rule.endpoint;

import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.rest.api.REST;
import be.iminds.iot.things.api.Things;

@Component()
public class SimpleRuleRestEndpoint implements REST {

	private Things things;
	
	public Collection<String> getTypes(){
		return things.getTypes();
	}
	
	public Collection<String> getMethods(String type){
		return things.getMethods(type);
	}
	
	public Collection<String> getVariables(String type){
		return things.getStateVariables(type);
	}
	
	@Reference
	public void setThings(Things things){
		this.things = things;
	}
}
