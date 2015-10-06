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
package be.iminds.iot.things.api;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * Utility class to query all available things, 
 * their state variables and actions using reflection.
 * 
 * @author tverbele
 *
 */
@Component(immediate=true, service=Things.class)
public class Things {

	private Map<String, Class> types = new HashMap<String, Class>();
	
	@Activate
	public void activate(BundleContext context){
		BundleWiring wiring = context.getBundle().adapt(BundleWiring.class);
		Collection<String> clazzes = wiring.listResources("be/iminds/iot/things/api", "*.class", BundleWiring.LISTRESOURCES_RECURSE);
		for(String s : clazzes){
			String className = s.substring(0, s.length()-6).replaceAll("/", ".");
			try {
				Class clazz = wiring.getClassLoader().loadClass(className);
				if(Thing.class.isAssignableFrom(clazz)
						&& Thing.class!=clazz){
					// We have a Thing
					String[] packageParts = className.split("\\.");
					String name = packageParts[packageParts.length-2];
					types.put(name, clazz);
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public List<String> getTypes(){
		List<String> t = new ArrayList<String>();
		t.addAll(types.keySet());
		return t;
	}
	
	public Class getClass(String type){
		return types.get(type);
	}
	
	public Collection<String> getStateVariables(String type){
		Set<String> variables = new HashSet<String>();
		Class c = types.get(type);
		if(c!=null){
			for(Method m : c.getMethods()){
				if(m.getDeclaringClass()!=c)
					continue;
				
				if(!Modifier.isPublic(m.getModifiers()))
					continue;
				
				String name = m.getName();
				if(name.startsWith("get")){
					String var = name.substring(3).toLowerCase();
					variables.add(var);
				}
			}
		}
		return variables;
	}
	
	public Collection<String> getStateValues(String type, String variable){
		Class c = types.get(type);
		if(c!=null){
			for(Method m : c.getMethods()){
				if(!m.getName().toLowerCase().equals("get"+variable)){
					continue;
				}
				if(m.getReturnType().isEnum()){
					ArrayList<String> values = new ArrayList<>();
					for(Object val : m.getReturnType().getEnumConstants()){
						values.add(val.toString());
					}
					return values;
				}
			}
		}
		return null;
	}
	
	public Collection<String> getMethods(String type){
		Set<String> methods = new HashSet<String>();
		Class c = types.get(type);
		if(c!=null){
			for(Method m : c.getMethods()){
				if(m.getDeclaringClass()!=c)
					continue;
				
				if(!Modifier.isPublic(m.getModifiers()))
					continue;
				
				String name = m.getName();
				if(name.startsWith("get")
						|| name.startsWith("is")){
					continue;
				}
				
				methods.add(name);
			}
		}
		return methods;
	}
}
