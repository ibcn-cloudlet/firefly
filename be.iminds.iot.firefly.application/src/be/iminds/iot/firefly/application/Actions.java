package be.iminds.iot.firefly.application;

import java.util.UUID;

public interface Actions {

	public String getType();
	
	public void action(UUID id, String ... params);
	
}
