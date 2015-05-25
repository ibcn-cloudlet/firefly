package be.iminds.iot.things.api.sensor.light;

// represents light level in lux
public class LightLevel {

	private final int value;

	public LightLevel(final int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.value + " lx";
	}
}
