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
