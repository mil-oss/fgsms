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

package org.miloss.fgsms.sla;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.miloss.fgsms.common.SLAUtils;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.miloss.fgsms.sla.actions.WSNotificationAlerter;
import org.oasis_open.docs.wsdm.muws1_2.ManagementEventType;
import org.oasis_open.docs.wsdm.muws2_2.AvailabilitySituation;
import org.w3c.dom.Element;

/**
 *
 * @author AO
 */
public class WSNotificationAlerterTest {
    
    public WSNotificationAlerterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    

    /**
     * Test of StripXmlHeader method, of class WSNotificationAlerter.
     */
    @Test
    public void testStripXmlHeader() {
        System.out.println("StripXmlHeader");
        String xmlalert = "<?xml version=\"1.0\" encoding=\"utf-8\"?><something>test</something>";

        String expResult = "<something>test</something>";
        String result = WSNotificationAlerter.StripXmlHeader(xmlalert);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
   //     fail("The test case is a prototype.");
    }
    
    
    
    /**
     * Test of serialization of WSDM alerts method
     */
    @Test
    public void testserializationofwsdm() {
      
        AlertContainer alert = new AlertContainer("xyz", "hello world", "http://localhost/text", "1", System.currentTimeMillis(), "1", false, false, new SLAAction(), "1", new TransactionalWebServicePolicy(), new AvailabilitySituation());
        ManagementEventType CreateWSDMEvent = SLAUtils.createWSDMEvent(alert);
        assertNotNull(CreateWSDMEvent);
        Element WSDMAlertToDomElement = SLAUtils.WSDMAlertToDomElement(CreateWSDMEvent);
        assertNotNull(WSDMAlertToDomElement);
    }
}
