package be.iminds.iot.thing.rule.factory;

import java.lang.reflect.Method;
import java.util.UUID;

import be.iminds.iot.things.api.Thing;
import be.iminds.iot.things.rule.api.Action;

public class SimpleAction implements Action {

	private final UUID id;
	private Object thing = null;
	
	private final String type;
	private final String method;
	private final Object[] args;
	
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
	}

	@Override
	public void execute() {
		if(thing!=null){
			Class[] parameterTypes = new Class[args.length];
			for(int i=0;i<parameterTypes.length;i++){
				parameterTypes[i] = args[i].getClass();
			}
			try {
				Method m = thing.getClass().getMethod(method, parameterTypes);
				m.setAccessible(true);
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
}
