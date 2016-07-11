package de.citec.sc.rocknrole.main;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.CharacterSet;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

/**
 *
 * @author cunger
 */
public class RestletApp extends Application {

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attachDefault(ProcessRequest.class);
		return router;
        }
}