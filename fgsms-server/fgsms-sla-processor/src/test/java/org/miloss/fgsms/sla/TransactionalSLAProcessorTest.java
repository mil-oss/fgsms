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

/**
 *
 * @author AO
 */
public class TransactionalSLAProcessorTest {

    public TransactionalSLAProcessorTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
        //TODO insert a service policy with an SLA rule record set to true, insert a single record
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
        //TODO delete service policy, transaction records, and sla faults
    }

    /**
     * Test of ProcessNewTransaction method, of class TransactionalSLAProcessor.
     */
    @org.junit.Test
    public void testProcessNewTransaction() {
     /*   System.out.println("ProcessNewTransaction");
        AddDataRequestMsg req = null;
        String transactionid = "";
        TransactionalSLAProcessor instance = new TransactionalSLAProcessor();
        instance.ProcessNewTransaction(req, transactionid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of ProcessNonTransactionalSLA method, of class TransactionalSLAProcessor.
     */
    @org.junit.Test
    public void testProcessNonTransactionalSLA() {
     /*   System.out.println("ProcessNonTransactionalSLA");
        TransactionalSLAProcessor instance = new TransactionalSLAProcessor();
        instance.ProcessNonTransactionalSLA();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of LoadPolicy method, of class TransactionalSLAProcessor.
     */
    @org.junit.Test
    public void testLoadPolicy() {
     /*   System.out.println("LoadPolicy");
        String uRI = "";
        TransactionalSLAProcessor instance = new TransactionalSLAProcessor();
        ServicePolicy expResult = null;
        ServicePolicy result = instance.LoadPolicy(uRI);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of ListContainsNontransactionSLA method, of class TransactionalSLAProcessor.
    
    @org.junit.Test
    public void testListContainsNontransactionSLATest2() {
        System.out.println("ListContainsNontransactionSLA");
        ResponseTimeGreaterThan r = new ResponseTimeGreaterThan();
        r.setParameter(100);
        List<SLA> sla = new ArrayList<SLA>();
        SLA a = new SLA();
        a.setRule(r);
        a.setGuid(UUID.randomUUID().toString());
       
        
        boolean expResult = false;
        boolean result = SLACommon.ListContainsNontransactionSLA(sla);
        assertEquals(expResult, result);
    } */
    
    
    
    
}
