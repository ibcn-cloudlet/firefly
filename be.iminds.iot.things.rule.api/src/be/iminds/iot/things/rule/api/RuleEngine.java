package be.iminds.iot.things.rule.api;

import java.util.List;

public interface RuleEngine {

	public void addRule(Rule rule);
	
	public void removeRule(int index);
	
	public List<Rule> getRules();
	
}
