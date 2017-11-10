/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.agents.esb;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogDetailsMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogDetailsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetRecentMessageLogsRequestMsg;
import org.miloss.fgsms.smoketest.common.Tools;

/**
 *
 * @author AO
 */
public class Main {

    static long threshold =30000;

    static Properties prop = new Properties();

    public static void main(String[] args) throws Exception{
        if (args == null || args.length == 0) {
            printUsage();
        }
        if (args[0].equalsIgnoreCase("waitForJbossESB")) {
            if (Tools.waitForServer("http://localhost:8080")) {
                System.exit(0);
            } else {
                System.err.println("timeout waiting for jbossesb to start up");
                System.exit(1);
            }

        } else if (args[0].equalsIgnoreCase("verifyCaptures")) {

            prop.load(new FileInputStream(new File("../test.properties")));
            String url="http://" + Utility.getHostName() + ":8080/Quickstart_webservice_proxy_basic/http/Proxy_Basic/Proxy";
            String msg=VerifyMessagePayloads(url, 1, false,false);
            if (msg.length()==0)
                return;
            else {
                System.err.println("verification failed: " + msg);
                System.exit(1);
            }
        } else {
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("java -jar (this library) (task)");
        System.out.println("tasks: ");
        System.out.println("\twaitForJbossESB");
        System.out.println("\tverifyCaptures");
    }

    private static String VerifyMessagePayloads(String firsthop_url, int expectedrecords, boolean request, boolean response) throws Exception {
        /*URL url = Thread.currentThread().getContextClassLoader().getResource("/WEB-INF/DASv8.wsdl");
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/DASv8.wsdl");
        }*/
        System.out.println("checking " + firsthop_url);
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
}
