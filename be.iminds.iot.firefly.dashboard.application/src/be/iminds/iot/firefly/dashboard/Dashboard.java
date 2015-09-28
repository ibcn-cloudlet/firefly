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
package be.iminds.iot.firefly.dashboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.eventadminserversentevents.capabilities.RequireEventAdminServerSentEventsWebResource;
import osgi.enroute.github.angular_ui.capabilities.RequireAngularUIWebResource;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.jsonrpc.api.JSONRPC;
import osgi.enroute.jsonrpc.api.RequireJsonrpcWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireAngularUIWebResource(resource="ui-bootstrap-tpls.js")
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireEventAdminServerSentEventsWebResource(resource={"easse.js","polyfill/eventsource.js"})
@RequireJsonrpcWebResource(resource={"jsonrpc.js"})
@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="be.iminds.iot.firefly.dashboard",property=JSONRPC.ENDPOINT+"=be.iminds.iot.firefly.dashboard")
public class Dashboard implements JSONRPC {

	private Map<String, Actions> dispatchers = Collections.synchronizedMap(new HashMap<String, Actions>());
	
	public void action(UUID thingId, String type, String... params){
		Actions dispatcher = dispatchers.get(type);
		if(dispatcher!=null){
			dispatcher.action(thingId, params);
		}
	}

	@Override
	public Object getDescriptor() throws Exception {
		// JSONRPC getDescriptor() ... not used atm
		return null;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,
			policy=ReferencePolicy.DYNAMIC)
	public void addActions(Actions a){
		dispatchers.put(a.getType(), a);
	}
	
	public void removeActions(Actions a){
		Actions old = dispatchers.get(a.getType());
		if(old==a){
			dispatchers.remove(a);
		}
	}

}
