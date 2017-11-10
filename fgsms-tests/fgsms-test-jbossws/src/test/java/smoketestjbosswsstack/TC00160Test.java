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
package smoketestjbosswsstack;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.tempuri.IService1;
import org.tempuri.Service1;

/**
 *
 * @author AO
 */
public class TC00160Test {
    
        static Properties prop;

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
        prop = new Properties();
        prop.load(new FileInputStream(new File("../test.properties")));
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    /**
     * TC00160 JAXWS Monitored Client to JAXWS Monitored Service, Jboss hosted using Jbossws soap stack
     * @throws Exception 
     */
    @org.junit.Test
    public void TC00160_1_MonitoredJaxWSClientToMonitorwsJaxWSServiceJbossHosted() throws Exception {
        TC00160_dowork(false, false);
    }

    @org.junit.Test
    public void TC00160_2_MonitoredJaxWSClientToMonitorwsJaxWSServiceJbossHosted() throws Exception {
        TC00160_dowork(true, false);
    }

    @org.junit.Test
    public void TC00160_3_MonitoredJaxWSClientToMonitorwsJaxWSServiceJbossHosted() throws Exception {
        TC00160_dowork(false, true);
    }

    @org.junit.Test
    public void TC00160_4_MonitoredJaxWSClientToMonitorwsJaxWSServiceJbossHosted() throws Exception {
        TC00160_dowork(true, true);
    }

    void TC00160_dowork(boolean request, boolean response) throws Exception {
        
        RemoveService(firsthop_url);
        MessageProcessor mp = MessageProcessor.getSingletonObject();
        MessageProcessor.PurgeMessageMap();;
        MessageProcessor.PurgeOutboundQueue();
        MessageProcessor.PurgePolicyCache();
        SetServicePolicy(firsthop_url, request, response);
        
        Service1 client = new Service1();
        IService1 port = client.getBasicHttpBindingIService1();
        BindingProvider bp = (BindingProvider) port;
        //firsthop_url
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, firsthop_url);
        List<Handler> hsc = new ArrayList<Handler>();
        hsc.add(new org.miloss.fgsms.agents.JbossWSClientAgent());
        bp.getBinding().setHandlerChain(hsc);
        String workingGetData = port.workingGetData(5);
        Thread.sleep(5000);
        
        String err = VerifyMessagePayloads(firsthop_url, 2, request, response);
        RemoveService(firsthop_url);
        if (!Utility.stringIsNullOrEmpty(err)) {
            fail(err);
        }
    }
    
    
    String firsthop_url="http://" + Utility.GetHostName() + ":8180/TempUriService1/Service1Impl";
    long threshold = 60*1000*5;
    private void RemoveService(String firsthop_url) throws Exception {
        /*URL url = Thread.currentThread().getContextClassLoader().getResource("/WEB-INF/PCS8.wsdl");
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/PCS8.wsdl");
        }*/
        PolicyConfigurationService c = new PolicyConfigurationService();
        PCS pcsPort = c.getPCSPort();
        BindingProvider bp = (BindingProvider) pcsPort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (String) prop.get("pcsurl"));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, (String) prop.get("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, (String) prop.get("fgsmsadminpass"));
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setDeletePerformanceData(true);
        req.setURL(firsthop_url);
        pcsPort.deleteServicePolicy(req);
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
        req.setRecords(4);
        GetMessageLogsResponseMsg recentMessageLogs = dasPort.getRecentMessageLogs(req);
        if (recentMessageLogs == null || recentMessageLogs.getLogs() == null || recentMessageLogs.getLogs() == null || recentMessageLogs.getLogs().getTransactionLog().isEmpty()) {
            return "no records found";
        }
        String ret = "";
        for (int i=0; i < recentMessageLogs.getLogs().getTransactionLog().size() ; i++)
        {
            if (recentMessageLogs.getLogs().getTransactionLog().get(i).getTimestamp()==null)
                ret += recentMessageLogs.getLogs().getTransactionLog().get(i).getTransactionId() + " didn't have a time stamp";
            else 
            {
                if (System.currentTimeMillis() - recentMessageLogs.getLogs().getTransactionLog().get(i).getTimestamp().getTimeInMillis() > threshold)
                    ret += recentMessageLogs.getLogs().getTransactionLog().get(i).getTransactionId() + "'s time stamp was of a greater difference that expected";
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

    private void SetServicePolicy(String firsthop_url, boolean request, boolean response) throws Exception {
        /*URL url = Thread.currentThread().getContextClassLoader().getResource("/WEB-INF/PCS8.wsdl");
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/PCS8.wsdl");
        }*/
        PolicyConfigurationService c = new PolicyConfigurationService();
        PCS pcsPort = c.getPCSPort();
        BindingProvider bp = (BindingProvider) pcsPort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (String) prop.get("pcsurl"));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, (String) prop.get("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, (String) prop.get("fgsmsadminpass"));

        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(firsthop_url);
        TransactionalWebServicePolicy tp = new TransactionalWebServicePolicy();
        tp.setRecordFaultsOnly(true);
        tp.setRecordRequestMessage(request);
        tp.setRecordResponseMessage(response);
        tp.setBuellerEnabled(false);
        tp.setHealthStatusEnabled(false);
        tp.setURL(firsthop_url);
        tp.setDomainName("UNSPECIFIED");
        tp.setMachineName(Utility.GetHostName());
        tp.setPolicyType(PolicyType.TRANSACTIONAL);
        req.setPolicy(tp);
        req.setURL(firsthop_url);
        pcsPort.setServicePolicy(req);
    }
}
