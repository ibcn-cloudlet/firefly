package be.iminds.iot.things.dyamand.adapters;

import java.awt.Color;

import org.dyamand.sensors.lighting.LightServiceType;

import be.iminds.iot.things.api.light.Light;
import be.iminds.iot.things.api.light.Light.State;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

public class LightAdapter implements ServiceAdapter {

	@Override
	public String[] getTargets() {
		return new String[] { be.iminds.iot.things.api.Thing.class.getName(),
				be.iminds.iot.things.api.light.Light.class.getName() };
	}

	@Override
	public Object getServiceObject(final Object source) throws Exception {
		if (!(source instanceof org.dyamand.sensors.lighting.Light)) {
			throw new Exception("Cannot translate object!");
		}
		final Light light = new AdaptedLight(
				(org.dyamand.sensors.lighting.Light) source);
		return light;
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		StateVariable translated;
		if (variable.equals(LightServiceType.STATE.toString())) {
			final boolean on = (Boolean) value;
			final State translatedValue = on ? State.ON
					: State.OFF;
			translated = new StateVariable(Light.STATE, translatedValue);
		} else if (variable.equals(LightServiceType.LEVEL.toString())) {
			final int level = (Integer) value;
			translated = new StateVariable(Light.LEVEL, level);
		} else if (variable.equals(LightServiceType.COLOR.toString())) {
			final Color color = (Color) value;
			translated = new StateVariable(Light.COLOR, color);
		}
		else {
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}

	private class AdaptedLight implements Light {

		private final org.dyamand.sensors.lighting.Light source;

		public AdaptedLight(final org.dyamand.sensors.lighting.Light l) {
			this.source = l;
		}

		@Override
		public int getLevel() {
			return this.source.getLevel();
		}

		@Override
		public void setLevel(final int level) {
			this.source.setLevel(level);
		}

		@Override
		public void incrementLevel(final int increment) {
			this.source.incrementLevel(increment);
		}

		@Override
		public void decrementLevel(final int decrement) {
			this.source.decrementLevel(decrement);
		}

		@Override
		public State getState() {
			final boolean on = this.source.isOn();
			return on ? State.ON : State.OFF;
		}

		@Override
		public void on() {
			if (!this.source.isOn()) {
				this.source.toggleState();
			}
		}

		@Override
		public void off() {
			if (this.source.isOn()) {
				this.source.toggleState();
			}
		}

		@Override
		public void toggle() {
			this.source.toggleState();
		}

		@Override
		public Color getColor() {
			//return this.source.getColor();
			return this.source.getService().getState(LightServiceType.COLOR);
		}

		@Override
		public void setColor(final Color c) {
			this.source.setColor(c);
		}

	}
}