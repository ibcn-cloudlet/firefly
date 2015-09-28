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
package be.iminds.iot.things.rule.api;

import java.util.List;
import java.util.UUID;

import be.iminds.iot.things.api.Thing;

/**
 * A Rule implementation that triggers Actions when a number of Conditions are met.
 * 
 * @author tverbele
 *
 */
public class ConditionActionRule implements Rule {

	protected final String type;
	protected final String description;
	protected final List<? extends Condition> conditions;
	protected final List<? extends Action> actions;
	
	public ConditionActionRule(String type, String description, 
			List<? extends Condition> conditions,
			List<? extends Action> actions){
		
		this.type = type;
		this.description = description;
		this.conditions = conditions;
		this.actions = actions;
	}
	
	@Override
	public boolean evaluate(Change change) {
		boolean fire = true;
		for(Condition c : conditions){
			fire = fire && c.trigger(change);
		}
		if(fire){
			for(Action a : actions){
				a.execute();
			}
		}
		return fire;
	}

	@Override
	public String getType(){
		return type;
	}
	
	@Override
	public String getDescription(){
		return description;
	}
	
	@Override
	public RuleDTO getDTO() {
		RuleDTO dto = new RuleDTO();
		dto.description = description;
		dto.type = type;
		
		int size = conditions.size();
		dto.sourceTypes = new String[size];
		dto.sources = new UUID[size];
		int i = 0;
		for(Condition c : conditions){
			dto.sourceTypes[i] = c.getType();
			dto.sources[i] = c.getId();
			i++;
		}
		
		size = actions.size();
		dto.destinationTypes = new String[size];
		dto.destinations = new UUID[size];
		i = 0;
		for(Action a : actions){
			dto.destinationTypes[i] = a.getType();
			dto.destinations[i] = a.getId();
			i++;
		}
		
		return dto;
	}

	@Override
	public void setThing(UUID id, Thing thing) {
		for(Condition c : conditions){
			if(id.equals(c.getId())){
				c.setThing(thing);
			}
		}
		for(Action a : actions){
			if(id.equals(a.getId())){
				a.setThing(thing);
			}
		}
	}
}
