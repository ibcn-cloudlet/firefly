package be.iminds.iot.things.rule.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.api.camera.Camera;
import be.iminds.iot.things.rule.api.Rule;
import be.iminds.iot.things.rule.api.RuleDTO;
import be.iminds.iot.things.rule.api.RuleFactory;
import be.iminds.iot.things.rule.factory.SimpleCondition.Operator;

@Component
public class SimpleRuleFactory implements RuleFactory {

	private Map<String, SimpleRule> templates = new HashMap<String, SimpleRule>();
	
	@Activate
	public void activate(){
		// create SimpleRule instances with null ids to act as templates
		createTemplate(new SimpleRule("ToggleLampFromButton", 
								"Toggle {{destination.name}} when {{source.name}} is pressed", 
								Collections.singletonList(new SimpleCondition(null, "button", Button.STATE, Operator.BECOMES, Button.State.PRESSED)), 
								Collections.singletonList(new SimpleAction(null, "lamp", "toggle"))));
		
	}
	
	@Override
	public Rule createRule(RuleDTO dto)
			throws Exception {
		Rule rule = null;
		
		// use the cloning mechanism of SimpleRules to create new ones
		SimpleRule template = templates.get(dto.type);
		if(template!=null){
			rule = template.clone(dto.description, dto.sources, dto.destinations);
		}
		return rule;
	}

	@Override
	public Collection<RuleDTO> getTemplates() {
		List<RuleDTO> t = new ArrayList<RuleDTO>();
		for(SimpleRule r : templates.values()){
			t.add(r.getDTO());
		}
		return Collections.unmodifiableCollection(t);
	}

	private void createTemplate(SimpleRule rule){
		templates.put(rule.getType(), rule);
	}
}
