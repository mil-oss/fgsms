/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.smoke.jms;

import java.io.File;
import org.hornetq.core.config.impl.FileConfiguration;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.jms.server.JMSServerManager;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;

/**
 *
 * @author alex
 */
public class HornetBroker {

     private static HornetQServer server=null;
     private static JMSServerManager jmsServerManager=null;
     
     public static void start() throws Exception {// Step 1. Create HornetQ core configuration, and set the properties accordingly
          System.setProperty("hornetq.remoting.netty.host","localhost");
          System.setProperty("hornetq.remoting.netty.port","5445");
          
          //Load the file configuration first of all
			FileConfiguration configuration = new FileConfiguration();
			configuration.setConfigurationUrl(new File("hornetq-configuration.xml").toURI().toString());
			configuration.start();

			//Create a new instance of hornetq server
			server = HornetQServers.newHornetQServer(configuration);
			
			//Wrap inside a JMS server
			 jmsServerManager = new JMSServerManagerImpl(
					server, new File("hornetq-jms.xml").toURI().toString());
			
			// if you want to use JNDI, simple inject a context here or don't
			// call this method and make sure the JNDI parameters are set.
			jmsServerManager.setContext(null);
			
			//Start the server
			jmsServerManager.start();
               
         
          
           
     }
     
      public static void stop() throws Exception {// Step 1. Create HornetQ core configuration, and set the properties accordingly
          jmsServerManager.stop();
          server.stop(Boolean.TRUE);
          
           
     }
}
