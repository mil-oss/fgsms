/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oasis_open.docs.wsn.client;

import javax.xml.ws.WebServiceFeature;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.oasis_open.docs.wsn.brw_2.CreatePullPoint;

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


     @Test
     public void testGetCreatePullPointBindingPort() {
	  System.out.println("getCreatePullPointBindingPort");
	  CreatePullPointService instance = new CreatePullPointService();
	  instance.getCreatePullPointBindingPort();
     }
     
     @Test
     public void testNotificationService() {
	  System.out.println("NotificationService");
	  NotificationService instance = new NotificationService();
	  instance.getNotificationPort();
     }
     
     @Test
     public void testPausableSubscriptionManagerService() {
	  System.out.println("PausableSubscriptionManagerService");
	  PausableSubscriptionManagerService instance = new PausableSubscriptionManagerService();
	  instance.getPausableSubscriptionManagerPort();
     }
     
     @Test
     public void testPublisherRegistrationManagerService() {
	  System.out.println("PublisherRegistrationManagerService");
	  PublisherRegistrationManagerService instance = new PublisherRegistrationManagerService();
	  instance.getPublisherRegistrationManagerPort();
     }
     
     @Test
     public void testPullPointService() {
	  System.out.println("PullPointService");
	  PullPointService instance = new PullPointService();
	  instance.getPullPointBindingPort();
     }

     
}
