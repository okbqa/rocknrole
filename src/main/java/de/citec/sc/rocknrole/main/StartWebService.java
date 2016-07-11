package de.citec.sc.rocknrole.main;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class StartWebService {

  public static void main(String[]args) throws IllegalArgumentException, Exception {
      
        Component component = new Component();

        component.getServers().add(Protocol.HTTP,1555);
	component.getDefaultHost().attach("/templategeneration/templator",new RestletApp());  
        component.start();
  }

}