package be.iminds.iot.things.rule.api;

import java.util.Arrays;
import java.util.UUID;

/**
 * Represents a Rule instance
 * @author tverbele
 *
 */
public class RuleDTO {

	// Types to match the sources
	public String[] sourceTypes;
	// Types to match the destinations
	public String[] destinationTypes;
	
	// Thing UUIDs this rule listens to
	public UUID[] sources;
	// Thing UUIDs this rules actions upon
	public UUID[] destinations;
	
	// Rule type string identifier - from this type a factory can create a rule
	public String type;
	// Description describing this rule
	public String description;
	
	public RuleDTO clone(){
		RuleDTO clone = new RuleDTO();
		clone.sourceTypes = Arrays.copyOf(sourceTypes, sourceTypes.length);
		clone.destinationTypes = Arrays.copyOf(destinationTypes, destinationTypes.length);;
		clone.sources = Arrays.copyOf(sources, sources.length);;
		clone.destinations = Arrays.copyOf(destinations, destinations.length);;
		clone.type = type;
		clone.description = description;
		return clone;
	}
}
