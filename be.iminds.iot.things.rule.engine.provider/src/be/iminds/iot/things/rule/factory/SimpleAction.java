package be.iminds.iot.things.rule.factory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import aQute.lib.converter.Converter;
import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.rule.api.Action;

public class SimpleAction implements Action {

	private final UUID id;
	private Object thing = null;
	private Method m;
	
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
					newArgs[i] = Converter.cnv(m.getParameterTypes()[i], args[i]);
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
