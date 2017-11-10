/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.wsn.broker;

import java.util.Random;
import java.util.UUID;
import javax.xml.ws.Endpoint;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author la.alex.oree
 */
public class CreatePullPointServiceTest {
     
     public CreatePullPointServiceTest() {
     }
     
     @BeforeClass
     public static void setUpClass() {
     }
     
     @AfterClass
     public static void tearDownClass() {
     }
     
     @Before
     public void setUp() {
     }
     
     @After
     public void tearDown() {
     }
     
     final Random rand  =new Random();

     final int baseport=1024;
     /**
      * Test of createPullPoint method, of class CreatePullPointService.
      */
     @Test
     public void testCreatePullPoint() throws Exception {
	  System.out.println("createPullPoint");
	  
	  
	  CreatePullPointService instance = new CreatePullPointService();
	  Endpoint ep = Endpoint.publish("http://localhost:" + (baseport + rand.nextInt(1024)) + "/" + UUID.randomUUID().toString(), instance);
	  
	  ep.stop();
	  
	  
     }
     
     @Test
     public void testPullPoint() throws Exception {
	  System.out.println("PullPoint");
	  
	  PullPointService instance = new PullPointService();
	  Endpoint ep = Endpoint.publish("http://localhost:" + (baseport + rand.nextInt(1024)) + "/" + UUID.randomUUID().toString(), instance);
	  
	  ep.stop();
	  
	  
     }
     
     
         
     @Test
     public void testPausableSubscriptionManagerService() throws Exception {
	  System.out.println("PausableSubscriptionManagerService");
	  
	  PausableSubscriptionManagerService instance = new PausableSubscriptionManagerService();
	  Endpoint ep = Endpoint.publish("http://localhost:" + (baseport + rand.nextInt(1024)) + "/" + UUID.randomUUID().toString(), instance);
	  
	  ep.stop();
	  
	  
     }
     
     
             
     @Test
     public void testPublisherRegistrationManagerService() throws Exception {
	  System.out.println("PublisherRegistrationManagerService");
	  
	  PublisherRegistrationManagerService instance = new PublisherRegistrationManagerService();
	  Endpoint ep = Endpoint.publish("http://localhost:" + (baseport + rand.nextInt(1024)) + "/" + UUID.randomUUID().toString(), instance);
	  
	  ep.stop();
	  
	  
     }
     
     
       @Test
     public void testWSNotificationBroker() throws Exception {
	  System.out.println("WSNotificationBroker");
	  
	  WSNotificationBroker instance = new WSNotificationBroker();
	  Endpoint ep = Endpoint.publish("http://localhost:" + (baseport + rand.nextInt(1024)) + "/" + UUID.randomUUID().toString(), instance);
	  
	  ep.stop();
	  
	  
     }
     
}
