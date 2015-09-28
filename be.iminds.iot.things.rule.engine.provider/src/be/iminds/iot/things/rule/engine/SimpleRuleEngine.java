/*******************************************************************************
 *  Copyright (c) 2015, Tim Verbelen
 *  Internet Based Communication Networks and Services research group (IBCN),
 *  Department of Information Technology (INTEC), Ghent University - iMinds.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of Ghent University - iMinds, nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
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

import osgi.enroute.dto.api.DTOs;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.event.ChangeEvent;
import be.iminds.iot.things.api.event.EventUtil;
import be.iminds.iot.things.rule.api.Change;
import be.iminds.iot.things.rule.api.Rule;
import be.iminds.iot.things.rule.api.RuleDTO;
import be.iminds.iot.things.rule.api.RuleEngine;

@Component(property={"event.topics=be/iminds/iot/thing/change/*"})
public class SimpleRuleEngine implements RuleEngine, EventHandler {

	private DTOs dtos;
	
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
		try {
			ChangeEvent e = EventUtil.toChangeEvent(event, dtos);
			
			Change change = new Change(e.thingId, e.stateVariable, e.stateValue);
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
		} catch(Exception e){
			System.err.println("Error handling event "+event);
		}
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
	
	@Reference
	void setDTOs(DTOs dtos){
		this.dtos = dtos;
	}
}
