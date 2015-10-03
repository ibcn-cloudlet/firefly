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
package be.iminds.iot.firefly.dashboard.actions;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import be.iminds.iot.firefly.dashboard.Actions;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.api.lamp.Lamp;

@Component(property={"aiolos.proxy=false"})
public class LampActions implements Actions {

	private Map<UUID, Lamp> lamps = Collections.synchronizedMap(new HashMap<UUID, Lamp>());
	
	@Override
	public String getType() {
		return "lamp";
	}

	@Override
	public void action(UUID id, String... params) {
		Lamp lamp = lamps.get(id);
		if(lamp!=null){
			if(params.length==0){
				// default action - switch on/off
				lamp.toggle();
			} else {
				switch(params[0]){
				case "level":
					lamp.setLevel(Integer.parseInt(params[1]));
					break;
				case "color":
					Color c = Color.decode(params[1]);
					lamp.setColor(c);
					break;
				}
			}
		}
	}

	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addLamp(Lamp l, Map<String, Object> properties){
		UUID id = UUID.fromString((String)properties.get(Thing.ID));
		lamps.put(id, l);
	}
	
	public void removeLamp(Lamp l, Map<String, Object> properties){
		UUID id = UUID.fromString((String)properties.get(Thing.ID));
		lamps.remove(id);
	}

	
}
