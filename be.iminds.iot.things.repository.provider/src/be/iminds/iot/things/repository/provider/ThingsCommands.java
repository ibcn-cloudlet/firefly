package be.iminds.iot.things.repository.provider;

import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.things.repository.api.ThingsRepository;
import be.iminds.iot.things.repository.api.ThingDTO;

@Component(
		service=Object.class,
		property={"osgi.command.scope=things",
				  "osgi.command.function=things",
				  "osgi.command.function=thing",
				  "osgi.command.function=save",
				  "osgi.command.function=load"},
		immediate=true)
public class ThingsCommands {

	private ThingsRepository repository;
	
	public void things(){
		for(ThingDTO t : repository.getThings()){
			System.out.println(" * "+t.id+"\t"+t.name+" "+t.type);
		}
	}
	
	public void thing(String id){
		UUID uuid = UUID.fromString(id);
		ThingDTO t = repository.getThing(uuid);
		if(t!=null){
			System.out.println(t.name);
			System.out.println("---------");
			System.out.println(" id: "+t.id);
			System.out.println(" gateway: "+t.gateway);
			System.out.println(" type: "+t.type);
			System.out.println(" device: "+t.device);
			System.out.println(" service: "+t.service);
			System.out.println(" location: "+t.location);
			System.out.println(" state variables:");
			for(String s : t.state.keySet()){
				System.out.println("   "+s+": "+t.state.get(s));
			}
		}
	}
	
	public void save(){
		save("things.txt");
	}
	
	public void load(){
		load("things.txt");
	}
	
	public void save(String f){
		((ThingsRepositoryImpl)repository).save(f);
	}
	
	public void load(String f){
		((ThingsRepositoryImpl)repository).load(f);
	}
	
	
	@Reference()
	void setRepository(ThingsRepository r){
		this.repository = r;
	}
	
}
