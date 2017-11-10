/*
 * Copyright 2015 CERDEC Command Power and Integration.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tempuri;

import java.util.Random;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceFeature;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author la.alex.oree
 */
public class Service1Test {
     
     public Service1Test() {
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

    /**
      * Test of getBasicHttpBindingIService1 method, of class Service1.
      */
     @Test
     public void testGetBasicHttpBindingIService1() {
	  System.out.println("getBasicHttpBindingIService1");
	  Service1 instance = new Service1();
	  
     }

     /**
      * Test of getBasicHttpBindingIService1 method, of class Service1.
      */
     @Test
     public void testGetBasicHttpBindingIService1_WebServiceFeatureArr() {
	  System.out.println("getBasicHttpBindingIService1");
	  
	  Service1 instance = new Service1();
	  IService1 basicHttpBindingIService1 = instance.getBasicHttpBindingIService1();
	  Random r = new Random();
	  int port=r.nextInt(1024)+1024;
	  String url="http://localhost:" + port + "/service";
	  Service1Impl impl = new Service1Impl();
	  Endpoint publish = Endpoint.publish(url, impl);
	  
	  ((BindingProvider)basicHttpBindingIService1).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
	  String ret=basicHttpBindingIService1.workingGetData(123);
	  
	  
	  
	  publish.stop();
	  assertEquals(ret,"123");
     }
     
     
}
