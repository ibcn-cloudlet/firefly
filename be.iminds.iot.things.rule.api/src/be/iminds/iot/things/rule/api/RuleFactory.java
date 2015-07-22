package be.iminds.iot.things.rule.api;

import java.util.Collection;

public interface RuleFactory {

	public Rule createRule(RuleDTO template) throws Exception;
	
	public Collection<RuleDTO> getTemplates();
}
