package org.okbqa.rocknrole.main;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class StartWebService {

  public static void main(String[]args) throws IllegalArgumentException, Exception {
      
        Component component = new Component();

        component.getServers().add(Protocol.HTTP,1555);
	component.getDefaultHost().attach("/templategeneration/rocknrole",new RestletApp());  
        component.start();
  }

}