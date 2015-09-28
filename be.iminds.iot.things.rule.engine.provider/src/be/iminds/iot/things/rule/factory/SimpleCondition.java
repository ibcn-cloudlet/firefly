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

import java.util.UUID;

import aQute.lib.converter.Converter;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.rule.api.Change;
import be.iminds.iot.things.rule.api.Condition;

public class SimpleCondition implements Condition {

	public enum Operator {BECOMES,IS,IS_NOT,IS_GREATER,IS_LESS,CHANGES};
	
	private final UUID id;
	private final String type;
	private final String variable;
	private Operator operator;
	private Object value; 
	
	private Object currentValue;
	
	public SimpleCondition(
			UUID id,
			String type, 
			String variable, 
			Operator operator, 
			Object value) {
		this.id = id;
		this.type = type;
		this.variable = variable;
		this.operator = operator;
		this.value = value;
	}
	
	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setThing(Thing thing) {
		// ignore
	}

	@Override
	public boolean trigger(Change change) {
		boolean changed = false;
		if(change.thingId.equals(id)
			&& change.stateVariable.equals(variable)
			&& (currentValue==null || !change.value.equals(currentValue))){
			currentValue = change.value;
			changed = true;
			
			// Try to convert in case of non matching classes
			// This is to attempt to handle any String values coming from web interface
			if(value!=null && !currentValue.getClass().equals(value.getClass())){
				try {
					value = Converter.cnv(currentValue.getClass(), value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		switch(operator) {
		case BECOMES:
			return changed && currentValue.equals(value);
		case IS:
			return currentValue.equals(value);
		case IS_NOT:
			return !currentValue.equals(value);
		case IS_GREATER:
			return ((Comparable)currentValue).compareTo(value) > 0;
		case IS_LESS:
			return ((Comparable)currentValue).compareTo(value) < 0;
		case CHANGES:
			return changed;
		}
		return false;
	}

	public SimpleCondition clone(UUID id){
		return new SimpleCondition(id, type, variable, operator, value);
	}
}
