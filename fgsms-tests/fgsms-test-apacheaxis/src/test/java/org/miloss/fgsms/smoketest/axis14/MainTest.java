/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * You may add additional accurate notices of copyright ownership.
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.smoketest.axis14;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.xml.ws.BindingProvider;
import org.junit.Test;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogDetailsMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogDetailsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetRecentMessageLogsRequestMsg;
import org.miloss.fgsms.smoketest.common.Tools;
import samples.message.TestMsg;

/**
 *
 * @author AO
 */
public class MainTest {
 static Properties prop;

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {

        prop = new Properties();
        prop.load(new FileInputStream(new File("../test.properties")));
        Tools.waitForServer("http://localhost:8888/fgsmsApacheAxis/services/MessageService");
        
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
       
            }

    @Test
    public void runTest() throws Exception{
        System.out.println("sending message");
        TestMsg.main(null);
        System.out.println("verifying message was recorded");
        VerifyMessagePayloads("http://" + Utility.getHostName() + ":8888/fgsmsApacheAxis/services/MessageService", 1, false, false);
        
    }
    
    
    private String VerifyMessagePayloads(String firsthop_url, int expectedrecords, boolean request, boolean response) throws Exception {
	  /*URL url = Thread.currentThread().getContextClassLoader().getResource("/WEB-INF/DASv8.wsdl");
	   if (url == null) {
	   url = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/DASv8.wsdl");
	   }*/
        DataAccessService_Service c = new DataAccessService_Service();
        DataAccessService dasPort = c.getDASPort();
        BindingProvider bp = (BindingProvider) dasPort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (String) prop.get("dasurl"));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, (String) prop.get("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, (String) prop.get("fgsmsadminpass"));
        GetRecentMessageLogsRequestMsg req = new GetRecentMessageLogsRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(firsthop_url);
        req.setRecords(expectedrecords);
        GetMessageLogsResponseMsg recentMessageLogs = dasPort.getRecentMessageLogs(req);
        if (recentMessageLogs == null || recentMessageLogs.getLogs() == null || recentMessageLogs.getLogs() == null || recentMessageLogs.getLogs().getTransactionLog().isEmpty()) {
            return "no records found";
        }
        String ret = "";
        for (int i = 0; i < recentMessageLogs.getLogs().getTransactionLog().size(); i++) {
            if (recentMessageLogs.getLogs().getTransactionLog().get(i).getTimestamp() == null) {
                ret += recentMessageLogs.getLogs().getTransactionLog().get(i).getTransactionId() + " didn't have a time stamp";
            } else {
                if (System.currentTimeMillis() - recentMessageLogs.getLogs().getTransactionLog().get(i).getTimestamp().getTimeInMillis() > 30000) {
                    ret += recentMessageLogs.getLogs().getTransactionLog().get(i).getTransactionId() + "'s time stamp was of a greater difference that expected";
                }
            }

        }
        if (recentMessageLogs.getLogs().getTransactionLog().size() != expectedrecords) {
            ret += recentMessageLogs.getLogs().getTransactionLog().size() + " records returned instead of " + expectedrecords;
        }
        if (recentMessageLogs.getTotalRecords() != expectedrecords) {
            ret += "total records return was not the expected value of " + expectedrecords + "vs " + recentMessageLogs.getTotalRecords();
        }
        if (expectedrecords == 2 && recentMessageLogs.getLogs().getTransactionLog().size() == expectedrecords) {
            GetMessageTransactionLogDetailsMsg req1 = new GetMessageTransactionLogDetailsMsg();
            req1.setClassification(new SecurityWrapper());
            req1.setTransactionID(recentMessageLogs.getLogs().getTransactionLog().get(0).getTransactionId());
            GetMessageTransactionLogDetailsResponseMsg t1 = dasPort.getMessageTransactionLogDetails(req1);
            req1 = new GetMessageTransactionLogDetailsMsg();
            req1.setClassification(new SecurityWrapper());
            req1.setTransactionID(recentMessageLogs.getLogs().getTransactionLog().get(1).getTransactionId());
            GetMessageTransactionLogDetailsResponseMsg t2 = dasPort.getMessageTransactionLogDetails(req1);

            if (Utility.stringIsNullOrEmpty(t1.getRelatedTransactionID())) {
                ret += "related tranaction is null for " + t1.getAgentType() + t1.getTransactionId();
            } else if (!t1.getRelatedTransactionID().equalsIgnoreCase(t2.getTransactionId())) {
                ret += "related tranactions ids do not match";
            }

            if (Utility.stringIsNullOrEmpty(t2.getRelatedTransactionID())) {
                ret += "related tranaction is null for " + t2.getAgentType() + t2.getTransactionId();
            } else if (!t2.getRelatedTransactionID().equalsIgnoreCase(t1.getTransactionId())) {
                ret += "related tranactions ids do not match";
            }

            if (!t1.getTransactionthreadId().equalsIgnoreCase(t2.getTransactionthreadId())) {
                ret += "thread ids do not match";
            }

        }
        return ret;

    }
}
