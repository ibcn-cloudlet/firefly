package be.iminds.iot.things.dummy;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
@interface DummyThingConfig {
	String thing_id();
	String thing_gateway();
	String thing_device();
	String thing_service();
}

