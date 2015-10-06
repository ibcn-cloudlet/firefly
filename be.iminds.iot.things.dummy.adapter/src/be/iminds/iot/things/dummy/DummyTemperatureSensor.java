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
import be.iminds.iot.things.api.sensor.temperature.Temperature;
import be.iminds.iot.things.api.sensor.temperature.TemperatureSensor;

@Component(name="be.iminds.iot.things.dummy.TemperatureSensor",
		configurationPolicy=ConfigurationPolicy.REQUIRE,
		service={TemperatureSensor.class, Thing.class},
		property={Thing.TYPE+"=temperature"})
public class DummyTemperatureSensor extends DummyThing implements TemperatureSensor {

	private double temperature = 20.2;
	
	@Activate
	void activate(DummyThingConfig config){
		id = UUID.fromString(config.thing_id());
		gatewayId = UUID.fromString(config.thing_gateway());
		device = config.thing_device();
		service = config.thing_service();
		type = "temperature";
		
		publishOnline();
		publishChange(TemperatureSensor.TEMPERATURE, new Temperature(temperature));
	}
	
	@Deactivate
	void deactivate(){
		publishOffline();
	}

	@Override
	public Temperature getTemperature() {
		return new Temperature(temperature);
	}

	void setTemperature(double temp){
		this.temperature = temp;
		publishChange(TemperatureSensor.TEMPERATURE, new Temperature(temperature));
	}
	
	@Override
	void randomEvent() {
		double t = (random.nextDouble()-0.5)*10;
		setTemperature(temperature+t);
	}
}
