package be.iminds.iot.thing.rule.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.rest.api.REST;
import be.iminds.iot.things.rule.api.Rule;
import be.iminds.iot.things.rule.api.RuleDTO;
import be.iminds.iot.things.rule.api.RuleEngine;
import be.iminds.iot.things.rule.api.RuleFactory;

@Component()
public class RulesRestEndpoint implements REST {

	private RuleEngine engine;
	private RuleFactory factory;
	
	public List<RuleDTO> getRules(){
		System.out.println("GET RULES");
		List<Rule> rules = engine.getRules();
		List<RuleDTO> dtos = new ArrayList<RuleDTO>(rules.size());
		for(Rule r : rules){
			dtos.add(r.getDTO());
		}
		return dtos;
	}
	
	public void putRules(RuleDTO template){
		try {
			Rule r = factory.createRule(template);
			engine.addRule(r);
		} catch(Exception e){
			System.out.println("Failed to create rule "+template);
		}
	}
	
	public void deleteRules(int index){
		System.out.println("DELETE "+index);
		engine.removeRule(index);
	}
	
	public Collection<RuleDTO> getTemplates(){
		return factory.getTemplates();
	}
	
	@Reference
	public void setRuleEngine(RuleEngine re){
		this.engine = re;
	}
	
	@Reference
	public void setRuleFactory(RuleFactory f){
		this.factory = f;
	}
}
