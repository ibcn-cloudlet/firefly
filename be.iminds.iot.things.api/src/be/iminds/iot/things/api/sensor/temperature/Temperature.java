package be.iminds.iot.things.api.sensor.temperature;

import java.text.DecimalFormat;

/**
 * Class representing a temperature value.
 */
public final class Temperature {
	/** Temperature value. */
	private final double value;
	/** Scale of the temperature. */
	private final Scale scale;

	/**
	 * Construct a temperature given the value and the scale.
	 *
	 * @param value
	 *            temperature value
	 * @param scale
	 *            temperature scale
	 * @see Scale
	 */
	public Temperature(final double value, final Scale scale) {
		this.value = value;
		this.scale = scale;
	}

	/**
	 * Get raw value.
	 *
	 * @return value as specified at construction time
	 */
	public double getValue() {
		return this.value;
	}

	/**
	 * Get temperature value in the scale of choice.
	 *
	 * @param targetScale
	 *            temperature scale
	 * @return temperature value in specified scale
	 */
	public double getValue(final Scale targetScale) {
		if (this.scale == targetScale) {
			return this.value;
		} else {
			if (this.scale == Scale.CELCIUS) {
				if (targetScale == Scale.FAHRENHEIT) {
					// Celsius to Fahrenheit
					return 9 * this.value / 5 + 32;
				} else if (targetScale == Scale.KELVIN) {
					// Celsius to Kelvin
					return this.value + 273.15;
				}
			} else if (this.scale == Scale.FAHRENHEIT) {
				if (targetScale == Scale.CELCIUS) {
					// Fahrenheit to Celcius
					return (this.value - 32) * 5 / 9;
				} else if (targetScale == Scale.KELVIN) {
					// Fahrenheit to Kelvin
					return (this.value + 459.67) * 5 / 9;
				}
			} else if (this.scale == Scale.KELVIN) {
				if (targetScale == Scale.CELCIUS) {
					// Kelvin to Celcius
					return this.value - 273.15;
				} else if (targetScale == Scale.FAHRENHEIT) {
					// Kelvin to Fahrenheit
					return this.value * 9 / 5 - 459.67;
				}
			}
		}
		return -1;
	}

	/**
	 * Get the temperature scale.
	 *
	 * @return {@link Scale}
	 */
	public Scale getScale() {
		return this.scale;
	}

	/**
	 * Temperature scale.
	 */
	public static enum Scale {
		/** Celcius scale. */
		CELCIUS,
		/** Fahrenheit scale. */
		FAHRENHEIT,
		/** Kelvin scale. */
		KELVIN
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		final DecimalFormat format = new DecimalFormat("#.00");
		builder.append(format.format(this.value));
		switch (this.scale) {
		case CELCIUS:
			builder.append(" &deg;C");
			break;
		case FAHRENHEIT:
			builder.append(" &deg;F");
			break;
		case KELVIN:
			builder.append(" K");
			break;
		}
		return builder.toString();
	}
}
