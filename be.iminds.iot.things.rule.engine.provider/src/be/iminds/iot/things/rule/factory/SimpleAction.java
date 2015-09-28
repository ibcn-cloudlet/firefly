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

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import aQute.lib.converter.Converter;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.rule.api.Action;

public class SimpleAction implements Action {

	private final UUID id;
	private transient Object thing = null;
	private transient Method m;
	
	private final String type;
	private final String method;
	private Object[] args;
	
	public SimpleAction(
			UUID id,
			String type, 
			String method,  
			Object... args){
		this.id = id;
		this.type = type;
		this.method = method;
		this.args = args;
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
		this.thing = thing;
		
		if(thing!=null){
			this.m = findMatchingMethod();
			if(this.m!=null){
				this.m.setAccessible(true);
			} else {
				System.err.println("Did not find a matching method!");
			}
		} else {
			this.m = null;
		}
	}

	@Override
	public void execute() {
		if(m!=null){
			try {
				m.invoke(thing, args);
			} catch(Exception e){
				System.err.println("Error invoking "+method+" on thing "+id);
				e.printStackTrace();
			}
		}
	}

	public SimpleAction clone(UUID id){
		return new SimpleAction(id, type, method, args);
	}
	
	private Method findMatchingMethod(){
		List<Method> candidates = new ArrayList<Method>();
		for(Method m : thing.getClass().getMethods()){
			if(!m.getName().equals(method)){
				continue;
			}
			if(m.getParameterTypes().length!=args.length){
				continue;
			}
			
			candidates.add(m);

			// check if it is perfect
			boolean perfect = true;
			for(int i=0;i<args.length;i++){
				if(!m.getParameterTypes()[i].isAssignableFrom(args[i].getClass())){
					perfect = false;
					break;
				}
			}
			
			if(perfect){
				return m;
			}
		}
		
		// try to convert the args to types we need in the candidates
		// this is to handle string args that come from web interface ... we just do our best
		Object[] newArgs = new Object[args.length];
		for(Method m : candidates){
			try {
				for(int i=0;i<args.length;i++){
					// hard coded check for Color class for Lamps
					if(m.getParameterTypes()[i].equals(Color.class)){
						newArgs[i] = Color.decode(args[i].toString());
					} else {
						newArgs[i] = Converter.cnv(m.getParameterTypes()[i], args[i]);
					}
				}
				args = newArgs;
				return m;
			} catch(Exception e){
				// failed
			}
		}
		
		return null;
	}
}
