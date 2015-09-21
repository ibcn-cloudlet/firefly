package be.iminds.iot.things.rule.engine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.rule.api.Change;
import be.iminds.iot.things.rule.api.Rule;
import be.iminds.iot.things.rule.api.RuleDTO;
import be.iminds.iot.things.rule.api.RuleEngine;

@Component(property={"event.topics=be/iminds/iot/thing/change/*"})
public class SimpleRuleEngine implements RuleEngine, EventHandler {

	// keep rules and things
	private List<Rule> rules = Collections.synchronizedList(new ArrayList<Rule>());
	private Map<UUID, Thing> things = Collections.synchronizedMap(new HashMap<UUID, Thing>());
	
	// execute on separate thread
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Activate
	public void activate(){
		load("rules.data");
	}
	
	@Deactivate
	public void deactivate(){
		save("rules.data");
	}
	
	public void load(String file){
		try {
			ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(file))));
			rules = (List<Rule>)input.readObject();
			input.close();
		} catch(Exception e){
			System.err.println("Failed to load rules from file");
			e.printStackTrace();
		}
	}
	
	public void save(String file){
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(file)));
			output.writeObject(rules);
			output.flush();
			output.close();
		} catch(Exception e){
			System.err.println("Failed to write rules to file");
			e.printStackTrace();
		}
	}
	
	@Override
	public void addRule(Rule rule) {
		// set references of known things
		RuleDTO dto =  rule.getDTO();
		for(UUID source : dto.sources){
			rule.setThing(source, things.get(source));
		}
		for(UUID destination : dto.destinations){
			rule.setThing(destination, things.get(destination));
		}
		
		rules.add(rule);
	}

	@Override
	public void removeRule(int index) {
		rules.remove(index);
	}

	@Override
	public List<Rule> getRules() {
		List<Rule> copy = new ArrayList<Rule>(rules.size());
		synchronized(rules){
			copy.addAll(rules);
		}
		return copy;
	}

	@Override
	public void handleEvent(Event event) {
		UUID id = (UUID) event.getProperty(Thing.ID);
		String name = (String) event.getProperty(Thing.STATE_VAR);
		Object val = event.getProperty(Thing.STATE_VAL);
		
		Change change = new Change(id, name, val);
		executor.execute(() -> {
			synchronized(rules){
				// TODO only notify rules that actually wait for events of this Thing?
				for(Rule r : rules){
					if(r.evaluate(change)){
						// TODO notify event when rule is fired?
						System.out.println("Triggered rule: "+r.getDescription());
					}
				}
			}
		});
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addThing(Thing thing, Map<String, Object> properties){
		UUID id = (UUID) properties.get(Thing.ID);
		things.put(id, thing);
		
		// for now just iterate all rules - TODO optimize?
		synchronized(rules){
			for(Rule r : rules){
				r.setThing(id, thing);
			}
		}
	}
	
	public void removeThing(Thing thing, Map<String, Object> properties){
		UUID id = (UUID) properties.get(Thing.ID);
		things.remove(id);
		
		// for now just iterate all rules - TODO optimize?
		synchronized(rules){
			for(Rule r : rules){
				r.setThing(id, null);
			}
		}
	}
}
