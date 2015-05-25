package be.iminds.iot.things.dyamand.adapter;

/**
 * Helper object for translating state
 */
public class StateVariable {

	public final String name;
	public final Object value;

	public StateVariable(final String n, final Object v) {
		this.name = n;
		this.value = v;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}
}
