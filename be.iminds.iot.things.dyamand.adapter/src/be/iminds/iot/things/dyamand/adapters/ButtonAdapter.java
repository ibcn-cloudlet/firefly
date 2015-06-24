package be.iminds.iot.things.dyamand.adapters;

import org.dyamand.sensors.button.ButtonServiceType;

import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

public class ButtonAdapter implements ServiceAdapter {

	@Override
	public String getType(){
		return "button";
	}
	
	@Override
	public String[] getTargets() {
		return new String[] { be.iminds.iot.things.api.Thing.class.getName(),
				be.iminds.iot.things.api.button.Button.class.getName() };
	}

	@Override
	public Object getServiceObject(final Object source) throws Exception {
		if (!(source instanceof org.dyamand.sensors.button.Button)) {
			throw new Exception("Cannot translate object!");
		}
		final Button button = new Button() {
			@Override
			public State[] getPossibleStates() {
				final org.dyamand.sensors.button.Button.State[] ss = ((org.dyamand.sensors.button.Button) source)
						.getPossibleStates();
				final State[] states = new State[ss.length];
				for (int i = 0; i < ss.length; i++) {
					states[i] = State.values()[ss[i].ordinal()];
				}
				return states;
			}

			@Override
			public State getState() {
				final org.dyamand.sensors.button.Button.State s = ((org.dyamand.sensors.button.Button) source)
						.getCurrentState();
				return State.values()[s.ordinal()];
			}

		};
		return button;
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		StateVariable translated;
		if (variable.equals(ButtonServiceType.BUTTON_STATE.toString())) {
			final Button.State translatedValue = Button.State.values()[((org.dyamand.sensors.button.Button.State) value)
					.ordinal()];
			translated = new StateVariable(Button.STATE, translatedValue);
		} else {
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}

}
