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
package org.miloss.fgsms.smoke.cxf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.message.Message;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.miloss.fgsms.agentcore.IMessageProcessor;

/**
 * self hosted rest service with monitor
 * @author AO
 */
public class TC00155_JAXRS_CXFServiceTest {

    static Properties prop;

    static Server s;

    static long timeout = 1000 * 60 * 10;
    static String firsthop_url = "http://" + Utility.getHostName() + ":9155/TC00155_JAXRS_CXFServiceTest";
    static String jettyurl = "http://localhost:9155/TC00155_JAXRS_CXFServiceTest";
    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
        prop = new Properties();
        prop.load(new FileInputStream(new File("../test.properties")));
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(CustomerService.class);
        sf.setResourceProvider(CustomerService.class,
                new SingletonResourceProvider(new CustomerService()));
        sf.setAddress(jettyurl);//"http://localhost:9000/restTest");
        List<Interceptor<? extends Message>> inInterceptors = sf.getInInterceptors();
        if (inInterceptors == null) {
            inInterceptors = new ArrayList<Interceptor<? extends Message>>();
        }

        inInterceptors.add(new org.miloss.fgsms.agents.CXFInterceptorInService());
        List<Interceptor<? extends Message>> outInterceptors = sf.getOutInterceptors();
        if (outInterceptors == null) {
            outInterceptors = new ArrayList<Interceptor<? extends Message>>();
        }
        outInterceptors.add(new org.miloss.fgsms.agents.CXFInterceptorOutService());

        s = sf.create();
        
    }
    
    
    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
        s.stop();
        s.destroy();

    }
    
    private static String getStringFromInputStream(InputStream in) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }
    long threshold = 60 * 1000 * 5;

    public void TC00155_dowork(boolean request, boolean response) throws Exception {

        RemoveService(firsthop_url);
        IMessageProcessor mp = MessageProcessor.getSingletonObject();
        mp.purgeMessageMap();;
        mp.purgeOutboundQueue();
        mp.purgePolicyCache();
        SetServicePolicy(firsthop_url, request, response);
        System.out.println("Starting up");
      

        
        //do some work
        LaunchBrowser1(jettyurl + "/customerservice/customers/123");
        //LaunchBrowser2(firsthop_url + "/customerservice");
        Thread.sleep(5000);

        
        String err = VerifyMessagePayloads(firsthop_url, 1, request, response);
        TransactionalWebServicePolicy tp = mp.getPolicyIfAvailable(firsthop_url);
        TransactionalWebServicePolicy tp2 = mp.getPolicyIfAvailable(firsthop_url + "/customerservice/customers/123");
        TransactionalWebServicePolicy tp3 = mp.getPolicyIfAvailable(firsthop_url + "/customers/123");
        if (tp == null) {
            err += " the policy was not cached, meaning the transaction was never recorded";
        }
        if (tp2 != null || tp3 != null) {
            err += "something went wrong with url detection, only one policy should have been created for this service";
        }


        RemoveService(firsthop_url);
        RemoveService(firsthop_url + "/customers/");
        RemoveService(firsthop_url + "/customers/123");
        if (!Utility.stringIsNullOrEmpty(err)) {
            fail(err);
        }
    }

    @org.junit.Test
    public void TC00155_1_MonitoredJaxRSServiceSelfHosted() throws Exception {
        TC00155_dowork(false, false);
    }

    
    @org.junit.Test
    public void TC00155_2_MonitoredJaxRSServiceSelfHostedRecordPayloads() throws Exception {
        TC00155_dowork(true, true);
    }

    private void RemoveService(String firsthop_url) throws Exception {
     
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
        for (int i = 0; i < recentMessageLogs.getLogs().getTransactionLog().size(); i++) {
            if (recentMessageLogs.getLogs().getTransactionLog().get(i).getTimestamp() == null) {
                ret += recentMessageLogs.getLogs().getTransactionLog().get(i).getTransactionId() + " didn't have a time stamp";
            } else {
                if (System.currentTimeMillis() - recentMessageLogs.getLogs().getTransactionLog().get(i).getTimestamp().getTimeInMillis() > threshold) {
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
        if (expectedrecords == 1 && recentMessageLogs.getLogs().getTransactionLog().size() == expectedrecords) {
            GetMessageTransactionLogDetailsMsg req1 = new GetMessageTransactionLogDetailsMsg();
            req1.setClassification(new SecurityWrapper());
            req1.setTransactionID(recentMessageLogs.getLogs().getTransactionLog().get(0).getTransactionId());
            GetMessageTransactionLogDetailsResponseMsg t1 = dasPort.getMessageTransactionLogDetails(req1);




            if (Utility.stringIsNullOrEmpty(t1.getXmlRequestMessage()) && request) {
                ret += "transaction request payload not recorded";
            }



            if (Utility.stringIsNullOrEmpty(t1.getXmlResponseMessage()) && response) {
                ret += "transaction response payload not recorded";
            }



        }
        return ret;

    }

    private void SetServicePolicy(String firsthop_url, boolean request, boolean response) throws Exception {
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
        tp.setRecordHeaders(true);
        tp.setURL(firsthop_url);
        tp.setRecordedMessageCap(1024000);
        tp.setRecordHeaders(true);
        tp.setDomainName("UNSPECIFIED");
        tp.setMachineName(Utility.getHostName());
        tp.setPolicyType(PolicyType.TRANSACTIONAL);
        req.setPolicy(tp);
        req.setURL(firsthop_url);
        pcsPort.setServicePolicy(req);
    }

    private void LaunchBrowser1(String firsthop_url) throws Exception {
        System.out.println("Sent HTTP GET request to query customer info");
        URL url = new URL(firsthop_url);
        InputStream in = url.openStream();
        System.out.println(getStringFromInputStream(in));
    }

}
