/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.smoke.jms;

import java.io.FileInputStream;
import java.util.Properties;
import net.timewalker.ffmq3.FFMQCoreSettings;
import net.timewalker.ffmq3.FFMQServer;
import net.timewalker.ffmq3.listeners.ClientListener;
import net.timewalker.ffmq3.listeners.tcp.io.TcpListener;
import net.timewalker.ffmq3.local.FFMQEngine;
import net.timewalker.ffmq3.management.destination.definition.QueueDefinition;
import net.timewalker.ffmq3.utils.Settings;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author AO
 */
public class JMSAlertSmokeTransactionalTest {
 
 
    @BeforeClass
    public static void before() throws Exception{
          System.out.println("Starting HornetQ server ...");
        
           HornetBroker.start();
    }

     @AfterClass
    public static void after() throws Exception{
         HornetBroker.stop();
        
            // Stopping the listener
            System.out.println("Stopping HornetQ server ...");
            
            System.out.println("Done.");
    }
    
     private Settings createEngineSettings()
    {
        // Various ways of creating engine settings
        
        // 1 - From a properties file
        Properties externalProperties = new Properties();
        try
        {
            FileInputStream in = new FileInputStream("../conf/ffmq-server.properties");
            externalProperties.load(in);
            in.close();
        }
        catch (Exception e)
        {
            //throw new RuntimeException("Cannot load external properties",e);
        }
        Settings settings = new Settings(externalProperties);
        
        // 2 - Explicit Java code

        
        settings.setStringProperty(FFMQCoreSettings.DESTINATION_DEFINITIONS_DIR, "./target/");
        settings.setStringProperty(FFMQCoreSettings.BRIDGE_DEFINITIONS_DIR, "./target/");
        settings.setStringProperty(FFMQCoreSettings.TEMPLATES_DIR, "./target/");
        settings.setStringProperty(FFMQCoreSettings.DEFAULT_DATA_DIR, "./target/");
        settings.setBooleanProperty(FFMQCoreSettings.AUTO_CREATE_QUEUES, true);
        settings.setBooleanProperty(FFMQCoreSettings.AUTO_CREATE_TOPICS, true);
        settings.setBooleanProperty(FFMQCoreSettings.JMX_AGENT_ENABLED, true);
        settings.setIntProperty(FFMQCoreSettings.JMX_AGENT_JNDI_RMI_PORT, 1099);

        
        return settings;
    }
    
    /**
     * Test of main method, of class JMSAlertSmokeTest.
     */
    @org.junit.Test
    public void testMain() throws Exception {
        System.out.println("JMS Alert Transactional");
        JMSAlertSmokeNonTransactionalTestTest.configure();
        String[] args = null;
        boolean runtest = JMSAlertSmokeTest.runtest();
        JMSAlertSmokeNonTransactionalTestTest.destroy();
        assertTrue(runtest);
    }
    
        @org.junit.Test
    public void testMainNT() throws Exception {
        System.out.println("JMS Alert NT Transactional");
        String[] args = null;
        JMSAlertSmokeNonTransactionalTestTest.configure();
        boolean runtest = JMSAlertSmokeTestNT.runtest();
        JMSAlertSmokeNonTransactionalTestTest.destroy();
        assertTrue(runtest);
    }

   
}
