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
package be.iminds.iot.things.dummy;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.button.Button;
import be.iminds.iot.things.api.sensor.contact.ContactSensor;

@Component(name="be.iminds.iot.things.dummy.ContactSensor",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={ContactSensor.class, Thing.class},
		property={Thing.TYPE+"=contact"})
public class DummyContactSensor extends DummyThing implements ContactSensor {

	ContactSensor.State state = ContactSensor.State.CLOSED;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "contact";
		
		publishOnline();
		publishChange(ContactSensor.STATE, getState());
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}

	@Override
	public State getState() {
		return state;
	}
	
	void setState(ContactSensor.State s){
		if(this.state!= s){
			this.state = s;
			publishChange(Button.STATE, getState());
		}
	}

	@Override
	void randomEvent() {
		if(random.nextFloat() > 0.5f){
			setState(ContactSensor.State.CLOSED);
		} else {
			setState(ContactSensor.State.OPEN);
		}
	}
	

}
