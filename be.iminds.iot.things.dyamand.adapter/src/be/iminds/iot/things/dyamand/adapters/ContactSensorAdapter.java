/*******************************************************************************
 *  Copyright (c) 2015, Tim Verbelen
 *  Internet Based Communication Networks and Services research group (IBCN),
 *  Department of Information Technology (INTEC), Ghent University - iMinds.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of Ghent University - iMinds, nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package be.iminds.iot.things.dyamand.adapters;

import org.dyamand.sensors.security.DoorWindowContactServiceType;
import org.osgi.service.component.annotations.Component;

import be.iminds.iot.things.api.sensor.contact.ContactSensor;
import be.iminds.iot.things.api.sensor.contact.ContactSensor.State;
import be.iminds.iot.things.dyamand.adapter.ServiceAdapter;
import be.iminds.iot.things.dyamand.adapter.StateVariable;

@Component(property={"aiolos.proxy=false"})
public class ContactSensorAdapter implements ServiceAdapter {

	@Override
	public String getType(){
		return "contact";
	}
	
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
