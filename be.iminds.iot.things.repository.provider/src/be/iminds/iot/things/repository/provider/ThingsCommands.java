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
package be.iminds.iot.things.repository.provider;

import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.things.repository.api.ThingsRepository;
import be.iminds.iot.things.repository.api.ThingDTO;

@Component(
		service=Object.class,
		property={"osgi.command.scope=things",
				  "osgi.command.function=things",
				  "osgi.command.function=thing",
				  "osgi.command.function=save",
				  "osgi.command.function=load"},
		immediate=true)
public class ThingsCommands {

	private ThingsRepository repository;
	
	public void things(){
		for(ThingDTO t : repository.getThings()){
			System.out.println(" * "+t.id+"\t"+t.name+" "+t.type);
		}
	}
	
	public void thing(String id){
		UUID uuid = UUID.fromString(id);
		ThingDTO t = repository.getThing(uuid);
		if(t!=null){
			System.out.println(t.name);
			System.out.println("---------");
			System.out.println(" id: "+t.id);
			System.out.println(" gateway: "+t.gateway);
			System.out.println(" type: "+t.type);
			System.out.println(" device: "+t.device);
			System.out.println(" service: "+t.service);
			System.out.println(" location: "+t.location);
			System.out.println(" state variables:");
			for(String s : t.state.keySet()){
				System.out.println("   "+s+": "+t.state.get(s));
			}
		}
	}
	
	public void save(){
		save("things.txt");
	}
	
	public void load(){
		load("things.txt");
	}
	
	public void save(String f){
		((ThingsRepositoryImpl)repository).save(f);
	}
	
	public void load(String f){
		((ThingsRepositoryImpl)repository).load(f);
	}
	
	
	@Reference()
	void setRepository(ThingsRepository r){
		this.repository = r;
	}
	
}
