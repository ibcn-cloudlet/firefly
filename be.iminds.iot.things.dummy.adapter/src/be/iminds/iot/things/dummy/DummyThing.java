package be.iminds.iot.things.dummy;

import java.util.Random;
import java.util.UUID;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;

import osgi.enroute.dto.api.DTOs;
import osgi.enroute.scheduler.api.Scheduler;
import be.iminds.iot.things.api.event.ChangeEvent;
import be.iminds.iot.things.api.event.OfflineEvent;
import be.iminds.iot.things.api.event.OnlineEvent;

public abstract class DummyThing {

	private int max = 60;
	protected Random random = new Random(System.currentTimeMillis());
	protected Scheduler scheduler;
	
	protected EventAdmin ea;
	protected DTOs dtos;
	
	protected UUID id;
	protected UUID gatewayId;
	protected String type;
	protected String device;
	protected String service;
	
	@Reference
	void setEventAdmin(EventAdmin ea){
		this.ea = ea;
	}
	
	@Reference
	void setDTOs(DTOs dtos){
		this.dtos = dtos;
	}
	
	@Reference
	void setScheduler(Scheduler scheduler){
		this.scheduler = scheduler;
		
		int next = random.nextInt(max)*1000;
		scheduler.after(() -> event(), next);
	}
	
	void publishOnline(){
		OnlineEvent e = new OnlineEvent();
		e.thingId = id;
		e.gatewayId = gatewayId;
		e.device = device;
		e.service = service;
		e.type = type;
		e.timestamp = System.currentTimeMillis();
		
		try {
			final String topic = "be/iminds/iot/thing/online/"+id;
			ea.sendEvent(new org.osgi.service.event.Event(topic, dtos.asMap(e)));
		} catch(Exception ex){
			System.err.println("Failed to send event "+e);
		}
	}
	
	void publishOffline(){
		OfflineEvent e = new OfflineEvent();
		e.thingId = id;
		e.gatewayId = gatewayId;
		e.timestamp = System.currentTimeMillis();
		
		try {
			final String topic = "be/iminds/iot/thing/offline/"+id;
			ea.sendEvent(new org.osgi.service.event.Event(topic, dtos.asMap(e)));
		} catch(Exception ex){
			System.err.println("Failed to send event "+e);
		}
	}
	
	void publishChange(String variable, Object value){
		ChangeEvent e = new ChangeEvent();
		e.thingId = id;
		e.gatewayId = gatewayId;
		e.stateVariable = variable;
		e.stateValue = value;
		e.timestamp = System.currentTimeMillis();
		
		try {
			final String topic = "be/iminds/iot/thing/change/"+id;
			ea.sendEvent(new org.osgi.service.event.Event(topic, dtos.asMap(e)));
		} catch(Exception ex){
			System.err.println("Failed to send event "+e);
		}	
	}
	
	abstract void randomEvent();
	
	void event(){
		randomEvent();
		int next = random.nextInt(max)*1000;
		scheduler.after(() -> event(), next);
	}
}
