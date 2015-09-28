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
package be.iminds.iot.things.rule.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import be.iminds.iot.things.rule.api.Action;
import be.iminds.iot.things.rule.api.Condition;
import be.iminds.iot.things.rule.api.ConditionActionRule;

public class SimpleRule extends ConditionActionRule {

	public SimpleRule(String type, String description,
			List<SimpleCondition> conditions, List<SimpleAction> actions) {
		super(type, description, conditions, actions);
	}

	public SimpleRule clone(String description, UUID[] sources, UUID[] destinations) throws Exception {
		// TODO check whether sources.length and destinations.length is ok?
		
		List<SimpleCondition> clonedConditions = new ArrayList<SimpleCondition>(sources.length);
		int i = 0;
		for(Condition c : conditions){
			clonedConditions.add(((SimpleCondition)c).clone(sources[i++]));
		}
		
		List<SimpleAction> clonedActions = new ArrayList<SimpleAction>(destinations.length);
		i = 0;
		for(Action a : actions){
			clonedActions.add(((SimpleAction)a).clone(destinations[i++]));
		}
		
		SimpleRule clone = new SimpleRule(type, description, clonedConditions, clonedActions);
		return clone;
	}
}
