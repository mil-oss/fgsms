/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * <p>
 * If it is not possible or desirable to put the notice in a particular file,
 * then You may include the notice in a location (such as a LICENSE file in a
 * relevant directory) where a recipient would be likely to look for such a
 * notice.
 * <p>
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.DataCollectorService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.AccessDeniedException;
import org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServiceUnavailableException;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;

/**
 * The general idea here is to add tons of records to stress out the user
 * interface, hopefully not causing excessive delays.
 *
 * Although this class has existed for some time, it was never really given any
 * attention until post public release, jan 2017, when it was expanded to
 * primarily benchmark FGSMS Agent Core's {@link MessageProcessor} which handles
 * the majority of web service transaction logs. Performance of the
 * {@link MessageProcessor} has always been a struggle between memory usage and
 * thread management. This class exercises it under varying conditions and
 * tracks it's ability to transmit data. As a side effect, it also stresses the
 * FGSMS {@link DataCollectorService}, certain UI elements, the statistics
 * aggregator and more.
 *
 * @author AO
 * @since 7.0.0
 * @see MessageProcessor
 */
public class ManualLoadTests {
    private static boolean moreOutput = false;

    /**
     * main entry point
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        System.out.println("Must run this as a user with global admin rights");
        // create Options object
        Options options = new Options();
        options.addOption("makePolicy", true, "Create N service policies");
        options.addOption("deletePolicy", true, "Delete N service policies");
        options.addOption("makePerformanceData", true, "Creates N service policies and X performance records each");
        options.addOption("records", true, "X performance records");
        options.addOption("pcs", true, "PCS Url override");
        options.addOption("username", true, "username override");
        options.addOption("password", true, "password override");
        options.addOption("highOutput", false, "prints additional information std out");

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);
        ManualLoadTests obj = new ManualLoadTests();

        obj.init(line.getOptionValue("pcs"), line.getOptionValue("username"), line.getOptionValue("password"), line.hasOption("highOutput"));
        try {
            if (line.hasOption("makePolicy")) {
                obj.createServicePolicies(Integer.parseInt(line.getOptionValue("makePolicy")));
            } else if (line.hasOption("makePerformanceData")) {
                obj.outputSystemProperties();

                obj.createPerfData(Integer.parseInt(line.getOptionValue("makePerformanceData")), Integer.parseInt(line.getOptionValue("records")), "Run 1");
                //run it again but with smaller numbers to make sure the threads don't die
                obj.createPerfData(Integer.parseInt(line.getOptionValue("makePerformanceData")), Integer.parseInt(line.getOptionValue("records")), "Run 2");
                obj.close();
            } else if (line.hasOption("deletePolicy")) {
                obj.killPolicies(Integer.parseInt(line.getOptionValue("deletePolicy")));
            } else {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("fgsms.ManualLoadTests.jar", options);
            }
        } catch (OutOfMemoryError oom) {
            System.out.println("Out of memory! remaining items are aborted, will pause until the FGSMS agent queue is emptied");
        }
        while (MessageProcessor.getSingletonObject().outboundQueueSize() > 0) {
            Thread.sleep(1000);
            printQueueStats();
        }
    }

    private static void printQueueStats() {
        System.out.print("Outbound queue:" + MessageProcessor.getSingletonObject().outboundQueueSize());
        if (moreOutput){
            System.out.print(" Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory() + " " + percentFreeMemory());
        }
        else System.out.println();
    }

    private static double percentFreeMemory() {
        return (1d - ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() + 0d) / Runtime.getRuntime().totalMemory())) * 100d;
    }

    BindingProvider bp;
    /**
     * agent config
     */
    ConfigLoader cfg = null;
    /**
     * endpoint to the PCS service, needed since
     */
    PCS pcsport = null;

    /**
     * the url prefix that will be associated with all this stuff we will
     * generate
     */
    String baseurl = IpAddressUtility.modifyURL("http://localhost:9999/LoadTestServiceX", false);
    BufferedWriter outputReport;

    public ManualLoadTests() throws ConfigurationException {
        if (cfg == null) {
            cfg = new ConfigLoader();
        }
        if (pcsport == null) {
            pcsport = cfg.getPcsport();
        }
        bp = (BindingProvider) pcsport;
        //initialize the output files
        File siteDir = new File("../../src/site/markdown/");
        if (siteDir.exists() && siteDir.isDirectory()) {
            //got it
            siteDir = new File(siteDir, "MessageProcessor-Java-Benchmark.md");
            open(siteDir);
            outputReportHeader();
            return;
        }
        siteDir = new File("../src/site/markdown");
        if (siteDir.exists() && siteDir.isDirectory()) {
            siteDir = new File(siteDir, "MessageProcessor-Java-Benchmark.md");
            open(siteDir);
            //got it
            outputReportHeader();
            return;
        }
        siteDir = new File("Benchmark.md");
        //give up and just use the current working directory
        open(siteDir);
        outputReportHeader();

    }

    public void outputSystemProperties() throws Exception {
        if (outputReport != null) {

            outputReport.write("## Test System Information: \n\n");
            outputReport.write("| Field | Value | \n");
            outputReport.write("| --- | --- | \n");
            outputReport.write("| CPU Cores | " + Runtime.getRuntime().availableProcessors() + " | \n");
            outputReport.write("| Max memory | " + Runtime.getRuntime().maxMemory() + " | \n");
            outputReport.write("| java.runtime.version | " + System.getProperty("java.runtime.version") + " | \n");
            outputReport.write("| os.name | " + System.getProperty("os.name") + " | \n");

            outputReport.write("\n");
        }
    }

    public float createPerfData(final int policyCount, final int iterations, final String header) throws Exception {

        if (outputReport != null) {

            outputReport.write("## " + header + "\n\n");
            outputReport.write("Input parameters: \n\n");
            outputReport.write("| Field | Value | \n");
            outputReport.write("| --- | --- | \n");
            outputReport.write("| Create X policies  | " + policyCount + " |\n");
            outputReport.write("| Create X transactions per policy | " + iterations + " | \n");
            outputReport.write("| DTG | " + Utility.formatDateTime(new Date()) + " |\n");
            outputReport.write("\n");
        }

        //assuming baseurl[0-n]
        long start = System.currentTimeMillis();
        createServicePolicies((policyCount));
        printQueueStats();
        if (outputReport != null) {
            outputReport.write("### Results \n\n");
            outputReport.write("| Test | Results | \n");
            outputReport.write("| --- | --- | \n");
            outputReport.write("| Policies created | " + (policyCount) + "|\n");
            outputReport.write("| Policy creation time | " + (System.currentTimeMillis() - start) + "ms |\n");
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < policyCount; i++) {
            final int urlcount = i;
            System.out.println("Creating " + baseurl + urlcount);
            for (int k = 0; k < iterations; k++) {
                makeTransaction(baseurl + urlcount, k);
            }
            //TODO throttling based on JVM memory?
            printQueueStats();
        }
        if (outputReport != null) {
            outputReport.write("| Performances Records created | " + (iterations * policyCount) + "|\n");
            outputReport.write("| Policy creation time | " + (System.currentTimeMillis() - start) + "ms |\n");
        }

        long delta = System.currentTimeMillis() - start;
        System.out.println("Message generation complete in " + (delta));
        System.out.println(iterations + " " + policyCount + " " + delta);
        try {
            System.out.println("Message generation complete in " + (((iterations * policyCount)) / (delta / 1000)) + " msg/sec");
        } catch (Exception ex) {
        }
        while (MessageProcessor.getSingletonObject().outboundQueueSize() > 0) {
            Thread.sleep(1000);

            printQueueStats();
            //total to send - what's left = total sent
            try {
                System.out.println("Current rate " + ((((iterations * policyCount)) - MessageProcessor.getSingletonObject().outboundQueueSize()) / ((System.currentTimeMillis() - start) / 1000f)) + " msg/sec");
            } catch (Exception ex) {
            }
        }
        delta = System.currentTimeMillis() - start;
        System.out.println();

        double bottom = delta / 1000d;
        if (outputReport != null) {
            outputReport.write("| Data transmission time | " + delta + " ms | \n");
            if (bottom != 0d) {
                outputReport.write("| Data transmission rate | " + (((iterations * policyCount)) / bottom) + " msg/sec\n\n");
            } else {
                outputReport.write("| Data transmission rate | NaN\n\n");
            }
        }
        if (bottom != 0d) {
            return (float)(((iterations * policyCount)) / bottom);
        } else {
            return -1;
        }
    }

    public void makeTransaction(String url, int currentIteration) {
        MessageProcessor.getSingletonObject();
        AddDataRequestMsg msg = new AddDataRequestMsg();
        msg.setAction("testAction");
        msg.setAgentType("manualLoadTest");
        msg.setClassification(new SecurityWrapper());
        msg.setRecordedat(new GregorianCalendar());
        msg.setSuccess(true);
        msg.setRequestSize(100);
        msg.setResponseSize(100);
        msg.setRequestURI(url);
        msg.setServiceHost(Utility.getHostName());
        msg.setTransactionID(UUID.randomUUID().toString());
        msg.setURI(url);
        msg.setXmlRequest("testmessage");
        msg.setXmlResponse("testmessage");
        msg.setResponseTime((int) ((Math.random() * 1000)));

        MessageProcessor.getSingletonObject().processPreppedMessage(msg);

    }

    public void createServicePolicies(int count) throws Exception {
        long totaltime = 0;
        printQueueStats();
        for (int i = 0; i < count; i++) {
            System.out.println("Creating " + baseurl + i);
            long now = System.currentTimeMillis();
            setPolicy(baseurl + i);
            totaltime += System.currentTimeMillis() - now;
        }
        printQueueStats();
        System.out.println("took a total of " + totaltime + "ms or about " + ((double) totaltime / (double) count) + " ms on average");

    }

    public void setPolicy(String url) throws ConfigurationException, ServiceUnavailableException, AccessDeniedException, DatatypeConfigurationException {
        System.out.println("Setting up policy");
        TransactionalWebServicePolicy pol = new TransactionalWebServicePolicy();
        pol.setAgentsEnabled(true);
        pol.setBuellerEnabled(false);
        pol.setDataTTL(DatatypeFactory.newInstance().newDuration(600000));
        pol.setHealthStatusEnabled(false);
        pol.setMachineName(Utility.getHostName());
        pol.setPolicyType(PolicyType.TRANSACTIONAL);
        pol.setURL(url);
        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(pol.getURL());
        req.setPolicy(pol);
        pcsport.setServicePolicy(req);
    }

    public void killPolicies(int count) throws Exception {

        if (outputReport != null) {

            outputReport.write("### Removing policies and data\n\n");
            outputReport.write("Input parameters: \n\n");
            outputReport.write("| Field | Value | \n");
            outputReport.write("| --- | --- | \n");
            outputReport.write("| Create X policies  | " + count + " |\n");
            outputReport.write("| DTG | " + Utility.formatDateTime(new Date()) + " |\n");

        }
        long start = System.currentTimeMillis();
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setDeletePerformanceData(true);
        for (int i = 0; i < count; i++) {
            System.out.println("deleting " + baseurl + i);
            req.setURL(baseurl + i);
            pcsport.deleteServicePolicy(req);
        }
        start = System.currentTimeMillis() - start;
        if (outputReport != null) {
            outputReport.write("| Deletion time | " + start + " ms | \n");
            outputReport.write("| Deletion rate | " + (count / (start / 1000f)) + " policy+data/sec\n");
            outputReport.write("\n");
        }
    }

    private void open(File siteDir) {
        try {
            outputReport = new BufferedWriter(new FileWriter(siteDir));
        } catch (Exception ex) {

            outputReport = null;
        }
    }

    public void close() {
        if (outputReport != null) {
            try {
                outputReport.write("\n");
                outputReport.flush();
                outputReport.close();
            } catch (Exception ex) {
            }
        }
        outputReport = null;

    }

    private void outputReportHeader() {
        if (outputReport != null) {
            try {
                outputReport.write("# FGSMS Java Benchmark Report\n\n");
                outputReport.write("This covers load testing agains the following components\n\n");
                outputReport.write(" - Java Agent Core MessageProcess\n");
                outputReport.write(" - Java Server - Data Collector Service\n\n");
            } catch (IOException ex) {

            }
        }
    }

    public void init(String url, String username, String password, boolean moreOutput) {
        this.moreOutput = moreOutput;
        if (url != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        } else {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getPCS_URLS().get(0));
        }

        if (username != null) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
        } else {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, cfg.getUsername());
        }

        if (password != null) {
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        } else {
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.getPassword()));
        }

    }
}
