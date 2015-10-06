package be.iminds.iot.things.api.event;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.osgi.service.event.Event;

import osgi.enroute.dto.api.DTOs;

/**
 * Utility class to convert OSGi Event classes to our typed Event classes.
 * 
 * @author tverbele
 *
 */
public class EventUtil {

	public static ChangeEvent toChangeEvent(Event event, DTOs dtos) throws Exception {
		ChangeEvent e = new ChangeEvent();
		for ( Field f : e.getClass().getFields() ) {
			if ( Modifier.isStatic(f.getModifiers()))
				continue;
			
			Object rawValue = event.getProperty(f.getName());
			Object value = dtos.convert(rawValue).to(f.getGenericType());
			f.set(e, value);
		}
		return e;
	}
	
	public static OnlineEvent toOnlineEvent(Event event, DTOs dtos) throws Exception {
		OnlineEvent e = new OnlineEvent();
		for ( Field f : e.getClass().getFields() ) {
			if ( Modifier.isStatic(f.getModifiers()))
				continue;
			
			Object rawValue = event.getProperty(f.getName());
			Object value = dtos.convert(rawValue).to(f.getGenericType());
			f.set(e, value);
		}
		return e;
	}
	
	public static OfflineEvent toOfflineEvent(Event event, DTOs dtos) throws Exception {
		OfflineEvent e = new OfflineEvent();
		for ( Field f : e.getClass().getFields() ) {
			if ( Modifier.isStatic(f.getModifiers()))
				continue;
			
			Object rawValue = event.getProperty(f.getName());
			Object value = dtos.convert(rawValue).to(f.getGenericType());
			f.set(e, value);
		}
		return e;
	}
}
