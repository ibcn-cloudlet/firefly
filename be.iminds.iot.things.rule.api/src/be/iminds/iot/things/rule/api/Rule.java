package be.iminds.iot.things.rule.api;

import java.io.Serializable;
import java.util.UUID;

import be.iminds.iot.things.api.Thing;

/**
 * Generic Rule interface
 * 
 * @author tverbele
 *
 */
public interface Rule extends Serializable {

	/**
	 * Evaluates the rule, returns whether it is triggered or not
	 * 
	 * @param change the change that can cause the rule to trigger
	 */
	public boolean evaluate(Change change);
	
	/**
	 * Get the type identifier of this Rule
	 */
	public String getType();
	
	/**
	 * Get the custom human readable description of this Rule
	 */
	public String getDescription();
	
	/**
	 * Get the DTO representing this Rule
	 */
	public RuleDTO getDTO();
	
	/**
	 * Notifies when a Thing that is applicable for this rule is online/offline
	 * In case of online, the Thing object is set, in case of offline, null is set
	 * 
	 * @param id uuid of the Thing
	 * @param thing the Thing object
	 */
	public void setThing(UUID id, Thing thing);
}
