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
package be.iminds.iot.things.rule.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.rest.api.REST;
import be.iminds.iot.things.api.Things;
import be.iminds.iot.things.rule.api.RuleEngine;
import be.iminds.iot.things.rule.factory.SimpleAction;
import be.iminds.iot.things.rule.factory.SimpleCondition;
import be.iminds.iot.things.rule.factory.SimpleRule;

@Component()
public class SimpleRuleRestEndpoint implements REST {

	private Things things;
	private RuleEngine engine;
	
	public Collection<String> getTypes(){
		return things.getTypes();
	}
	
	public Collection<String> getMethods(String type){
		return things.getMethods(type);
	}
	
	public Collection<String> getVariables(String type){
		return things.getStateVariables(type);
	}
	
	public Collection<String> getValues(String type, String variable){
		return things.getStateValues(type, variable);
	}

	public void putSimpleRule(SimpleRuleDTO dto){
		List<SimpleCondition> conditions = new ArrayList<SimpleCondition>();
		for(SimpleConditionDTO c : dto.conditions){
			conditions.add(new SimpleCondition(c.thingId, c.type, c.variable, c.operator, c.value));
		}
		
		List<SimpleAction> actions = new ArrayList<SimpleAction>();
		for(SimpleActionDTO a : dto.actions){
			actions.add(new SimpleAction(a.thingId, a.type, a.method, a.args));
		}
		
		SimpleRule rule = new SimpleRule(dto.type, dto.description, conditions, actions);
		engine.addRule(rule);
	}
	
	@Reference
	public void setThings(Things things){
		this.things = things;
	}
	
	@Reference
	public void setRuleEngine(RuleEngine engine){
		this.engine = engine;
	}
}
