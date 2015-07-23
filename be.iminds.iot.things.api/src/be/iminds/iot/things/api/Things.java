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
 * Utility class to query all available things, their state variables and actions
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
	
	// Use API instead of reflection?
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
