package be.iminds.iot.things.dyamand.adapters;

import org.dyamand.sensors.security.DoorWindowContactServiceType;

import be.iminds.iot.things.api.sensor.contact.ContactSensor;
import be.iminds.iot.things.api.sensor.contact.ContactSensor.State;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

public class ContactSensorAdapter implements ServiceAdapter {

	@Override
	public String[] getTargets() {
		return new String[] {
				be.iminds.iot.things.api.Thing.class.getName(),
				be.iminds.iot.things.api.sensor.contact.ContactSensor.class
						.getName() };
	}

	@Override
	public Object getServiceObject(final Object source) throws Exception {
		if (!(source instanceof org.dyamand.sensors.security.DoorWindowContact)) {
			throw new Exception("Cannot translate object!");
		}
		final ContactSensor sensor = new ContactSensor() {

			@Override
			public State getState() {
				final org.dyamand.sensors.security.DoorWindowContact c = (org.dyamand.sensors.security.DoorWindowContact) source;
				return c.isOpen() ? State.OPEN : State.CLOSED;
			}
		};
		return sensor;
	}

	@Override
	public StateVariable translateStateVariable(final String variable,
			final Object value) throws Exception {
		StateVariable translated;
		if (variable.equals(DoorWindowContactServiceType.OPEN_STATE_VAR_TYPE
				.toString())) {
			final boolean open = (Boolean) value;
			final ContactSensor.State translatedValue = open ? ContactSensor.State.OPEN
					: State.CLOSED;
			translated = new StateVariable(ContactSensor.STATE, translatedValue);
		} else {
			throw new Exception("Could not translate state variable!");
		}
		return translated;
	}

}
