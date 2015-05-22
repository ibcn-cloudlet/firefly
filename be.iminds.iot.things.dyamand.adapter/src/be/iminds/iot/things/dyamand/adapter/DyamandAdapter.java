package be.iminds.iot.things.dyamand.adapter;

import org.dyamand.event.EventListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;

/**
 * 
 */
@Component(name = "be.iminds.iot.things.dyamand", immediate=true)
public class DyamandAdapter implements EventListener {

	// Hack to have bridge to Dyamand plugin system...
	static DyamandAdapter instance = null;
	
	private EventAdmin ea;
	
	@Activate
	public void activate(){
		instance = this;
	}
	
	@Deactivate
	public void deactivate(){
		instance = null;
	}
	
	@Override
	public void onEvent(org.dyamand.event.Event event) {
		System.out.println("EVENT "+event.toString());
	}

	@Reference
	void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
}
