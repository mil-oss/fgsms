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

package org.miloss.fgsms.services.status.impl;

import org.miloss.fgsms.services.status.impl.StatusServiceImpl;
import java.util.List;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.status.GetStatusRequestMsg;
import org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg;
import org.miloss.fgsms.services.interfaces.status.RemoveStatusRequestMsg;
import org.miloss.fgsms.services.interfaces.status.RemoveStatusResponseMsg;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;
import org.miloss.fgsms.services.interfaces.status.SetStatusResponseMsg;
import org.miloss.fgsms.test.WebServiceBaseTests;
import static org.junit.Assert.*;

/**
 *
 * @author AO
 */
public class StatusServiceTest extends WebServiceBaseTests{

    public StatusServiceTest() throws Exception {
        super();
        url  = "http://localhost:8180/jUnitTestCaseSS";
        Init();
      
    }
 
    
    
    
    /**
     * Test of setStatus method, of class StatusServiceImpl.
     */
    @org.junit.Test
    public void testSetStatusNullMsg() throws Exception {
        System.out.println("testSetStatusNullMsg");
        SetStatusRequestMsg req = null;
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            SetStatusResponseMsg result = instance.setStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testSetStatusNullClassMsg() throws Exception {
        System.out.println("testSetStatusNullClassMsg");
        SetStatusRequestMsg req = new SetStatusRequestMsg();
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            SetStatusResponseMsg result = instance.setStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testSetStatusEmptyMsg() throws Exception {
        System.out.println("testSetStatusEmptyMsg");
        SetStatusRequestMsg req = new SetStatusRequestMsg();
        req.setClassification(new SecurityWrapper());
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            SetStatusResponseMsg result = instance.setStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }
    
    @org.junit.Test
    public void testSetStatusValidMsg() throws Exception {
        System.out.println("testSetStatusValidMsg");
        SetStatusRequestMsg req = new SetStatusRequestMsg();
        req.setClassification(new SecurityWrapper());
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            req.setURI(url);
            SetStatusResponseMsg result = instance.setStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of getStatus method, of class StatusServiceImpl.
     */
    @org.junit.Test
    public void testGetStatus() throws Exception {
        System.out.println("getStatus");
        GetStatusRequestMsg req = null;
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);

        try {
            GetStatusResponseMsg result = instance.getStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of getAllStatus method, of class StatusServiceImpl.
     */
    @org.junit.Test
    public void testGetAllStatus() throws Exception {
        System.out.println("getAllStatus");
        GetStatusRequestMsg req = null;
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            List result = instance.getAllStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of removeStatus method, of class StatusServiceImpl.
     */
    @org.junit.Test
    public void testRemoveStatusNullMsg() throws Exception {
        System.out.println("removeSttestRemoveStatusNullMsgatus");
        RemoveStatusRequestMsg req = null;
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            RemoveStatusResponseMsg result = instance.removeStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }
    
    @org.junit.Test
    public void testRemoveStatusClassNullMsg() throws Exception {
        System.out.println("testRemoveStatusClassNullMsg");
        RemoveStatusRequestMsg req = new RemoveStatusRequestMsg();
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            RemoveStatusResponseMsg result = instance.removeStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }
    
     
 @org.junit.Test
    public void testRemoveStatusEmptyMsg() throws Exception {
        System.out.println("testRemoveStatusEmptyMsg");
        RemoveStatusRequestMsg req = new RemoveStatusRequestMsg();
        req.setClassification(new SecurityWrapper());
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            RemoveStatusResponseMsg result = instance.removeStatus(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }
    
 @org.junit.Test
    public void testRemoveStatusValidMsgAdmin() throws Exception {
        System.out.println("testRemoveStatusValidMsgAdmin");
        RemoveStatusRequestMsg req = new RemoveStatusRequestMsg();
        req.setClassification(new SecurityWrapper());
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            req.getURI().add(url);
            RemoveStatusResponseMsg result = instance.removeStatus(req);
            assertNotNull(result);
            assertNotNull(result.getClassification());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected fault");
        }
    }
 
 
 @org.junit.Test
    public void testRemoveStatusValidMsgAgent() throws Exception {
        System.out.println("testRemoveStatusValidMsgAgent");
        RemoveStatusRequestMsg req = new RemoveStatusRequestMsg();
        req.setClassification(new SecurityWrapper());
        StatusServiceImpl instance = new StatusServiceImpl(agentctx);
        try {
            req.getURI().add(url);
            RemoveStatusResponseMsg result = instance.removeStatus(req);
              fail("unexpected success");
       
              
        } catch (Exception ex) {

          
        }
    }
 
 
 
 
 @org.junit.Test
    public void testRemoveStatusValidMsgUser() throws Exception {
        System.out.println("testRemoveStatusValidMsgUser");
        RemoveStatusRequestMsg req = new RemoveStatusRequestMsg();
        req.setClassification(new SecurityWrapper());
        StatusServiceImpl instance = new StatusServiceImpl(userbobctx);
        try {
            req.getURI().add(url);
            RemoveStatusResponseMsg result = instance.removeStatus(req);
              fail("unexpected success");
        } catch (Exception ex) {
            
        }
    }
    
    

    /**
     * Test of setMoreStatus method, of class StatusServiceImpl.
     */
    @org.junit.Test
    public void testSetMoreStatusNullMsg() throws Exception {
        System.out.println("testSetMoreStatusNullMsg");
        List<SetStatusRequestMsg> reqs = null;
        StatusServiceImpl instance = new StatusServiceImpl(adminctx);
        try {
            SetStatusResponseMsg result = instance.setMoreStatus(reqs);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }
}
