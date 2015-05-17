package be.iminds.iot.firefly.application;

import org.osgi.service.component.annotations.Component;

import osgi.enroute.capabilities.AngularUIWebResource;
import osgi.enroute.capabilities.AngularWebResource;
import osgi.enroute.capabilities.BootstrapWebResource;
import osgi.enroute.capabilities.ConfigurerExtender;
import osgi.enroute.capabilities.WebServerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;

@AngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@AngularUIWebResource(resource="ui-bootstrap-tpls.js")
@BootstrapWebResource(resource="css/bootstrap.css")
@WebServerExtender
@ConfigurerExtender
@Component(name="be.iminds.iot.firefly")
public class FireflyApplication implements REST {

	public String getUpper(RESTRequest rq, String string) {
		return string.toUpperCase();
	}

}
