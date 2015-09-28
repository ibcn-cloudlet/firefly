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
package be.iminds.iot.things.rule.command;

import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.things.rule.api.Rule;
import be.iminds.iot.things.rule.api.RuleDTO;
import be.iminds.iot.things.rule.api.RuleEngine;
import be.iminds.iot.things.rule.api.RuleFactory;
import be.iminds.iot.things.rule.engine.SimpleRuleEngine;
import be.iminds.iot.things.rule.factory.SimpleAction;

@Component(
		service=Object.class,
		property={"osgi.command.scope=rules",
				  "osgi.command.function=rules",
				  "osgi.command.function=templates",
				  "osgi.command.function=add",
				  "osgi.command.function=remove",
				  "osgi.command.function=trigger",
				  "osgi.command.function=load",
				  "osgi.command.function=save"},
		immediate=true)
public class RuleCommands {

	private RuleEngine engine;
	private RuleFactory factory;
	
	public void rules(){
		int i=0;
		for(Rule r : engine.getRules()){
			System.out.println("["+(i++)+"] "+r.getDescription());
		}
	}
	
	public void templates(){
		for(RuleDTO template : factory.getTemplates()){
			System.out.println("* "+template.type+" - "+template.description);
		}
	}
	
	// only supports simple one-to-one rules for now
	public void add(String type, String sourceId, String destinationId) throws Exception {
		RuleDTO template = new RuleDTO();
		template.type = type;
		template.description = type+" "+sourceId+"->"+destinationId;
		template.sources = new UUID[]{UUID.fromString(sourceId)};
		template.destinations = new UUID[]{UUID.fromString(destinationId)};
		Rule rule = factory.createRule(template);
		engine.addRule(rule);
	}
	
	public void remove(int index){
		engine.removeRule(index);
	}
	
	public void trigger(String id, String type, String method, String... args){
		SimpleAction a = new SimpleAction(UUID.fromString(id), type, method, args);
		a.execute();
	}
	
	
	public void load(){
		load("rules.data");
	}
	
	public void load(String file){
		((SimpleRuleEngine)engine).load(file);
	}
	
	public void save(){
		save("rules.data");
	}
	
	public void save(String file){
		((SimpleRuleEngine)engine).save(file);
	}
	
	@Reference
	public void setRuleEngine(RuleEngine re){
		this.engine = re;
	}
	
	@Reference
	public void setRuleFactory(RuleFactory f){
		this.factory = f;
	}
}
