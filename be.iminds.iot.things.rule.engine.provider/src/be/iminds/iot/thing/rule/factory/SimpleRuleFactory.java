package be.iminds.iot.thing.rule.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import be.iminds.iot.things.rule.api.Rule;
import be.iminds.iot.things.rule.api.RuleDTO;
import be.iminds.iot.things.rule.api.RuleFactory;
import be.iminds.iot.things.rule.example.ToggleLampOnButtonPress;

@Component
public class SimpleRuleFactory implements RuleFactory {

	private Collection<RuleDTO> templates = new ArrayList<RuleDTO>();
	
	@Activate
	public void activate(){
		// FIXME for now hard coded with simple rule
		templates.add(new ToggleLampOnButtonPress("Toggle a lamp triggerd by a button press.", null, null).getDTO());
	}
	
	@Override
	public Rule createRule(RuleDTO template)
			throws Exception {
		Rule rule = null;
		switch(template.type){
			// FIXME for now hard coded ... should change
			case "ToggleLampOnButtonPress":{
				UUID buttonId = template.sources[0];
				UUID lampId = template.destinations[0];
				rule = new ToggleLampOnButtonPress(template.description, buttonId, lampId);
				break;
			}
			default:
				System.out.println("Don't know how to create a rule of type "+template.type);
		}
		return rule;
	}

	@Override
	public Collection<RuleDTO> getTemplates() {
		return templates;
	}

}
