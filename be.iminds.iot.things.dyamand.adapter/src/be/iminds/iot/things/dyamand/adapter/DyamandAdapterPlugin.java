package be.iminds.iot.things.dyamand.adapter;

import java.util.Collections;

import org.dyamand.DyamandException;
import org.dyamand.plugin.Plugin;
import org.dyamand.plugin.PluginContext;

public class DyamandAdapterPlugin implements Plugin {

	private PluginContext pluginContext;
	
	@Override
	public void start(PluginContext context) throws DyamandException {
		pluginContext = context;
		// TODO what if DyamandAdapter not yet activated - can this happen?
		synchronized(DyamandAdapter.sync){
			if(DyamandAdapter.instance==null){
				try {
					DyamandAdapter.sync.wait();
				} catch (InterruptedException e) {
				}
			}
			context.register(DyamandAdapter.instance, Collections.EMPTY_LIST);
		}
		
	}

	@Override
	public void stop() throws DyamandException {
		this.pluginContext.unregister(DyamandAdapter.instance);
	}

}
