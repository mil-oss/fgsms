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
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.sdks;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import org.miloss.fgsms.common.Constants.AuthMode;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * This is a very basic console application that provides access to basic
 * functionality of fgsms services. Note: it does not currently support PKI/CAC
 * authentication
 *
 * @author AO
 */
public class ConsoleAdapter {

    public static void main(String[] args) throws Exception {
        new ConsoleAdapter().start();
    }
    FgsmsSDK sdk;
    List<ServiceType> GetListOfMonitoredServices;
    Map<String, Object> context;

    /**
     * processes a command from the console and routes the control to the
     * appropriate action
     *
     * @param command
     * @return
     * @throws Exception
     */
    private boolean process(String command) throws Exception {
        if (command.toLowerCase().startsWith("help")) {
            printCommands();
        } else if (command.toLowerCase().startsWith("list services")) {
            printServiceList();
        } else if (command.toLowerCase().startsWith("ls")) {
            printServiceList();
        } else if (command.toLowerCase().startsWith("list brokers")) {
            printServiceList(PolicyType.STATISTICAL);
        } else if (command.toLowerCase().startsWith("list machine")) {
            printServiceList(PolicyType.MACHINE);
        } else if (command.toLowerCase().startsWith("list process")) {
            printServiceList(PolicyType.PROCESS);
        } else if (command.toLowerCase().startsWith("list status")) {
            printServiceList(PolicyType.STATUS);
        } else if (command.toLowerCase().startsWith("list ws")) {
            printServiceList(PolicyType.TRANSACTIONAL);
        } else if (command.toLowerCase().startsWith("refresh")) {
            getServiceList();
        } else if (command.toLowerCase().startsWith("agg")) {
            getAggregatedStats(command);
        } else if (command.toLowerCase().startsWith("status-all")) {
            getStatusAll();
        } else if (command.toLowerCase().startsWith("status")) {
            getStatus();
        } else if (command.toLowerCase().startsWith("perf-broker")) {
            getBrokerPerf();
        } else if (command.toLowerCase().startsWith("perf-proc")) {
            getProcessPerf();
        } else if (command.toLowerCase().startsWith("perf-machine")) {
            getMachinePerf();
        } else if (command.toLowerCase().startsWith("perf-wslogs-faulting")) {
            getWSLogsFault();
        } else if (command.toLowerCase().startsWith("perf-wslogs-detail")) {
            GetWSLogsDetails();
        } else if (command.toLowerCase().startsWith("perf-wslogs-sla")) {
            getWSLogsSLA();
        } else if (command.toLowerCase().startsWith("perf-wslogs")) {
            getWSLogs();
        } else if (command.toLowerCase().startsWith("gen-settings-get")) {
            GetGenSettings();
        } else if (command.toLowerCase().startsWith("gen-settings-add")) {
            addGenSetting();
        } else if (command.toLowerCase().startsWith("gen-settings-remove")) {
            removeGenSetting();
        } else if (command.toLowerCase().startsWith("email-settings-get")) {
            getEmail();
        } else if (command.toLowerCase().startsWith("policy-get-basic")) {
            printPolicy(getPolicy());

        } else if (command.toLowerCase().startsWith("machine-net-bulk")) {
            getServiceList();
            machineNetBulk(command);
        } else if (command.toLowerCase().startsWith("machine-net-loop")) {
            while (true) {
                getServiceList();
                machineNetBulk(command);
                Thread.sleep(1000);
            }
        } //
        else if (command.toLowerCase().startsWith("report-drone-nic")) {
            reportsDroneNic(command);
        } else if (command.toLowerCase().startsWith("export-drone-nic")) {
            exportsDroneNic(command);
        } else if (command.toLowerCase().startsWith("machine-del-bulk")) {
            machineDelBulk(command);
        } else if (command.toLowerCase().startsWith("machine-info")) {
            printMachineInformation();
        } else if (command.toLowerCase().startsWith("machine-add")) {
            addAProcessToMonitor();
        } else if (command.toLowerCase().startsWith("policy-get-xml")) {
            getPolicyXml(getPolicy());
        } else if (command.toLowerCase().startsWith("policy-del")) {
            removePolicy();
        } else if (command.toLowerCase().startsWith("dump")) {
            dumpProperties();
        } else if (command.toLowerCase().startsWith("exit") || command.toLowerCase().startsWith("quit")) {
            System.exit(0);
        } else {
            return false;
        }
        return true;
    }

    private void printCommands() {
        System.out.println("---------[Listing]----------");
        System.out.println("list services\t\tRetrieves a list of ALL services that you have access to");
        System.out.println("list brokers\t\tRetrieves a list of services that you have access to");
        System.out.println("list machine\t\tRetrieves a list of services that you have access to");
        System.out.println("list process\t\tRetrieves a list of services that you have access to");
        System.out.println("list ws\t\t\tRetrieves a list of services that you have access to");
        System.out.println("list status\t\tRetrieves a list of services that you have access to");
        System.out.println("---------[Performance]----------");
        System.out.println("agg\t\t\tRetrieves a list of aggregated web service data");
        // System.out.println("perf-wslogs-search\t\tSearch for transactions by URL and timestamp");
        System.out.println("perf-wslogs-detail\tGet the payloads and headers for a transaction");
        System.out.println("perf-broker\t\tCurrent Broker data");
        System.out.println("perf-proc\t\tCurrent Process data");
        System.out.println("perf-machine\t\tCurrent Process data");
        System.out.println("perf-wslogs\t\tGets recent transaction logs for a web service");
        System.out.println("perf-wslogs-faulting\tGets recent failing transaction logs for a web service");
        System.out.println("perf-wslogs-sla\t\tGets recent SLA fault transaction logs for a web service");
        System.out.println("---------[Status]----------");
        System.out.println("status-all\t\tRetrieves the status of all avaiable services");
        System.out.println("status\t\t\tRetrieves the status of a specific");
        System.out.println("---------[Configuration]----------");
        System.out.println("dump\t\t\tDumps the configuration of this application to console");
        System.out.println("machine-info\t\tRetrieves all availalble configuration info on a machine (OS Agent)");
        System.out.println("machine-net-bulk\tMonitors network ETH0 on a group of machines (OS Agent)");
        System.out.println("machine-net-loop\tMonitors network ETH0 on a group of machines (OS Agent) looping");
        System.out.println("machine-del-bulk\tDeletes a group of machines (OS Agent)");
        System.out.println("machine-add\t\tMonitor another process on a machine");
        System.out.println("gen-settings-get\tRetrieves all general settings from fgsms");
        System.out.println("gen-setting-add\t\tAdds a general settings from fgsms");
        System.out.println("gen-setting-remove\tRemoves a general settings from fgsms");
        System.out.println("email-settings-get\tRetrieves all email settings from fgsms");
        System.out.println("policy-get-basic\tRetrieves a service policy from fgsms");
        System.out.println("policy-get-xml\t\tRetrieves a service policy writes the raw xml to screen");
        System.out.println("policy-delete\t\tRemoves a service policy from fgsms and deletes all data");
        System.out.println("refresh\t\t\tRetrieves a new copy of the list of monitored services");
        System.out.println("---------[Reporting]----------");
        System.out.println("export-drone-nic\t(prefix) (startTime) (endTime) (nic) returns reports for the given time period");
        System.out.println("report-drone-nic\t(prefix) (startTime) (endTime) (nic) returns reports for the given time period");
        System.out.println("exit\t\t\tExits the program");

    }

    private void printServiceList() {
        for (int i = 0; i < GetListOfMonitoredServices.size(); i++) {
            System.out.println("=======================================");
            System.out.println("URL: " + GetListOfMonitoredServices.get(i).getURL());
            System.out.println("Display Name: " + GetListOfMonitoredServices.get(i).getDisplayName());
            System.out.println("Type: " + GetListOfMonitoredServices.get(i).getPolicyType().value());
            System.out.println("Domain: " + GetListOfMonitoredServices.get(i).getDomainname());
            System.out.println("Hostname: " + GetListOfMonitoredServices.get(i).getHostname());
            System.out.println("Pavrent: " + GetListOfMonitoredServices.get(i).getParentobject());

        }
    }

    private void printServiceList(PolicyType t) {
        for (int i = 0; i < GetListOfMonitoredServices.size(); i++) {
            if (GetListOfMonitoredServices.get(i).getPolicyType().equals(t)) {
                System.out.println("=======================================");
                System.out.println("URL: " + GetListOfMonitoredServices.get(i).getURL());
                System.out.println("Display Name: " + GetListOfMonitoredServices.get(i).getDisplayName());
                System.out.println("Type: " + GetListOfMonitoredServices.get(i).getPolicyType().value());
                System.out.println("Domain: " + GetListOfMonitoredServices.get(i).getDomainname());
                System.out.println("Hostname: " + GetListOfMonitoredServices.get(i).getHostname());
                System.out.println("Parent: " + GetListOfMonitoredServices.get(i).getParentobject());
            }
        }
    }

    private int getServiceFromSelection(PolicyType t) {
        int count = 0;
        for (int i = 0; i < GetListOfMonitoredServices.size(); i++) {
            if (GetListOfMonitoredServices.get(i).getPolicyType().equals(t)) {
                System.out.println("[" + i + "] " + GetListOfMonitoredServices.get(i).getURL());
                count++;
            }
        }
        int k = -1;
        while (k < 0 || k > GetListOfMonitoredServices.size()) {
            System.out.print("Enter Selection: ");
            String s = System.console().readLine();
            try {
                k = Integer.parseInt(s);
            } catch (Exception ex) {
                System.out.println("operating canceled");
                return -1;
            }
        }
        return k;
    }

    private int getServiceFromSelection() {
        for (int i = 0; i < GetListOfMonitoredServices.size(); i++) {
            System.out.println("[" + i + "] " + GetListOfMonitoredServices.get(i).getURL());
        }
        int k = -1;
        while (k < 0 || k > GetListOfMonitoredServices.size()) {
            System.out.print("Enter Selection: ");
            String s = System.console().readLine();
            k = Integer.parseInt(s);
        }
        return k;
    }

    private void getServiceList() throws Exception {
        GetListOfMonitoredServices = sdk.GetListOfMonitoredServices();
        System.out.println("Refreshed..." + GetListOfMonitoredServices.size() + " items returned");
    }

    private void enterLoop() throws Exception {
        while (true) {
            System.out.print(">");
            String command = System.console().readLine();
            if (!process(command)) {
                System.out.println("Unknown command, try 'help' for a list");
            }
        }
    }

    private void start() throws Exception {
        System.out.println("Starting up");
        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config.properties");
            p.load(fis);
        } catch (Exception ex) {
            System.err.println("problem reading from config.properties, check to make sure it is present and readable");
            try {
                fis.close();
            } catch (Exception e) {
            }
            return;
        } finally {
            try {
                fis.close();
            } catch (Exception ex) {
            }
        }
        context = new HashMap<String, Object>();
        context.put(FgsmsSDK.ARS_ENDPOINT, p.get(FgsmsSDK.ARS_ENDPOINT));
        context.put(FgsmsSDK.DAS_ENDPOINT, p.get(FgsmsSDK.DAS_ENDPOINT));
        context.put(FgsmsSDK.PCS_ENDPOINT, p.get(FgsmsSDK.PCS_ENDPOINT));
        context.put(FgsmsSDK.SS_ENDPOINT, p.get(FgsmsSDK.SS_ENDPOINT));
        context.put(FgsmsSDK.RS_ENDPOINT, p.get(FgsmsSDK.RS_ENDPOINT));

        context.put(FgsmsSDK.CLASSIFICATION_LEVEL, ClassificationType.fromValue((String) p.get(FgsmsSDK.CLASSIFICATION_LEVEL)));
        context.put(FgsmsSDK.CLASSIFICATION_CAVEAT, p.get(FgsmsSDK.CLASSIFICATION_CAVEAT));
        AuthMode auth = org.miloss.fgsms.common.Constants.AuthMode.valueOf((String) p.get(FgsmsSDK.AUTH_MODE));
        context.put(FgsmsSDK.AUTH_MODE, auth);
        if (auth == AuthMode.UsernamePassword) {
            String username = System.getenv("USERNAME");
            String pass = System.getenv("PASSWORD");
            if (Utility.stringIsNullOrEmpty(pass) || Utility.stringIsNullOrEmpty(username)) {
                System.out.println("You can also specify the username and password via environment variables USERNAME and PASSWORD");
                System.out.print("Username: ");
                username = System.console().readLine();
                System.out.print("Password: ");
                pass = new String(System.console().readPassword());
            }
            context.put(FgsmsSDK.USERNAME_PROPERTY, username);
            context.put(FgsmsSDK.PASSWORD_PROPERTY, pass);
        }
        sdk = new FgsmsSDK(context);
        System.out.println("Configuration appears to be ok...");

        System.out.println("Logging in...");
        GetGlobalPolicyResponseMsg GetGlobalPolicy = sdk.GetGlobalPolicy();
        System.out.println("Current classification level is " + GetGlobalPolicy.getClassification().getClassification().value() + " " + GetGlobalPolicy.getClassification().getCaveats());
        if (GetGlobalPolicy.getPolicy().isAgentsEnabled()) {
            System.out.println("Web Service Agents are enabled");
        } else {
            System.out.println("Web Service Agents are disabled!");
        }

        System.out.println("Retrieving the list of monitored services");
        GetListOfMonitoredServices = sdk.GetListOfMonitoredServices();
        System.out.println("done. For a list of commands, enter 'help' and press enter");
        enterLoop();
    }

    private void getAggregatedStats(String command) throws Exception {
        int x = getServiceFromSelection(PolicyType.TRANSACTIONAL);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        GetQuickStatsResponseMsg agg = sdk.GetAggregatedStatistics(GetListOfMonitoredServices.get(x).getURL());
        DatatypeFactory df = DatatypeFactory.newInstance();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());

        for (int i = 0; i < agg.getQuickStatWrapper().size(); i++) {
            System.out.println("Action: " + agg.getQuickStatWrapper().get(i).getAction() + " Uptime " + Utility.durationToString(agg.getQuickStatWrapper().get(i).getUptime()));
            System.out.println("Range\tAvail\tAvgResTime\tSuccess\tFaults\tSLA\tMTBF\tMaxReq\tMaxRes\tMaxResTime\tAge");
            for (int k = 0; k < agg.getQuickStatWrapper().get(i).getQuickStatData().size(); k++) {
                System.out.println(agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getTimeInMinutes().intValue() + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getAvailabilityPercentage() + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getAverageResponseTime() + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getSuccessCount() + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getFailureCount() + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getSLAViolationCount() + "\t"
                        + Utility.durationToString(agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getMTBF()) + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getMaximumRequestSize() + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getMaximumResponseSize() + "\t"
                        + agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getMaximumResponseTime() + "\t"
                        + Utility.durationLargestUnitToString(df.newDuration(System.currentTimeMillis() - agg.getQuickStatWrapper().get(i).getQuickStatData().get(k).getUpdatedAt().getTimeInMillis())));

            }
            System.out.println("Uptime: " + Utility.durationToString(agg.getQuickStatWrapper().get(i).getUptime()));

            System.out.println("-----------------------------");
        }
    }

    private void getStatusAll() throws Exception {
        List<GetStatusResponseMsg> GetStatusAll = sdk.GetStatusAll();
        for (int x = 0; x < GetStatusAll.size(); x++) {
            System.out.print(GetStatusAll.get(x).getURI());
            if (GetStatusAll.get(x).isOperational()) {
                System.out.print(" OK ");
            } else {
                System.out.print(" NG ");
            }
            System.out.println(GetStatusAll.get(x).getMessage() + " as of "
                    + GetStatusAll.get(x).getTimeStamp().getTime() + " last status change was at " + GetStatusAll.get(x).getLastStatusChangeTimeStamp().getTime());
        }
    }

    private void getStatus() throws Exception {
        int x = getServiceFromSelection();
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        GetStatusResponseMsg s = sdk.GetStatus(GetListOfMonitoredServices.get(x).getURL());
        if (s.isOperational()) {
            System.out.print("OK ");
        } else {
            System.out.print("NG ");
        }
        if (s.getTimeStamp() == null) {
            System.out.println(s.getMessage() + " as last status change was at " + (s.getLastStatusChangeTimeStamp() != null ? s.getLastStatusChangeTimeStamp().getTime() : " never"));
        } else {
            System.out.println(s.getMessage() + " as of " + s.getTimeStamp().toString() + " last status change was at " + (s.getLastStatusChangeTimeStamp() != null ? s.getLastStatusChangeTimeStamp().getTime() : " never"));
        }
    }

    private void getEmail() throws Exception {
        GetMailSettingsResponseMsg emailsettings = sdk.getEmailsettings();
        for (int i = 0; i < emailsettings.getPropertiesList().size(); i++) {
            System.out.println(emailsettings.getPropertiesList().get(i).getPropertyName() + " = " + emailsettings.getPropertiesList().get(i).getPropertyValue());

        }
    }

    private void getBrokerPerf() throws Exception {
        int x = getServiceFromSelection(PolicyType.STATISTICAL);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        GetCurrentBrokerDetailsResponseMsg GetBrokerStatistics = sdk.GetBrokerStatistics(GetListOfMonitoredServices.get(x).getURL());
        System.out.println("URL: " + GetBrokerStatistics.getUri());
        System.out.println("Name: " + GetBrokerStatistics.getDisplayName());
        if (GetBrokerStatistics.isOperational()) {
            System.out.println("ONLINE");
        } else {
            System.out.println("OFFLINE");
        }
        System.out.println("Name\tType\tConsumers\tMsgCnt\tMsgDrp\tQueueDepth\tTimestamp");
        for (int i = 0; i < GetBrokerStatistics.getQueueORtopicDetails().size(); i++) {
            System.out.println(GetBrokerStatistics.getQueueORtopicDetails().get(i).getCanonicalname() + "\t"
                    + GetBrokerStatistics.getQueueORtopicDetails().get(i).getItemtype() + "\t"
                    + GetBrokerStatistics.getQueueORtopicDetails().get(i).getActiveconsumercount() + "\t"
                    + GetBrokerStatistics.getQueueORtopicDetails().get(i).getMessagecount() + "\t"
                    + GetBrokerStatistics.getQueueORtopicDetails().get(i).getMessagesdropped() + "\t"
                    + GetBrokerStatistics.getQueueORtopicDetails().get(i).getQueueDepth() + "\t"
                    + GetBrokerStatistics.getQueueORtopicDetails().get(i).getTimestamp().toString());
        }
    }

    private void getProcessPerf() throws Exception {
        int x = getServiceFromSelection(PolicyType.PROCESS);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        GetMostRecentProcessDataResponseMsg GetProcessStatistics = sdk.GetProcessStatistics(GetListOfMonitoredServices.get(x).getURL());
        System.out.println("URL: " + GetProcessStatistics.getPerformanceData().getUri());
        System.out.println("Status Message: " + GetProcessStatistics.getPerformanceData().getStatusmessage());
        System.out.println("Status: " + GetProcessStatistics.getPerformanceData().isOperationalstatus());
        System.out.println("Threads: " + GetProcessStatistics.getPerformanceData().getNumberofActiveThreads());
        System.out.println("CPU%: " + GetProcessStatistics.getPerformanceData().getPercentusedCPU());
        System.out.println("Open Files: " + GetProcessStatistics.getPerformanceData().getOpenFileHandles());
        System.out.println("Memory: " + GetProcessStatistics.getPerformanceData().getBytesusedMemory());
        System.out.println("Started at: " + GetProcessStatistics.getPerformanceData().getStartedAt());
        System.out.println("Updated at: " + GetProcessStatistics.getPerformanceData().getTimestamp());

    }

    private void getMachinePerf() throws Exception {
        int x = getServiceFromSelection(PolicyType.MACHINE);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        GetMostRecentMachineDataResponseMsg GetMachineStatistics = sdk.GetMachineStatistics(GetListOfMonitoredServices.get(x).getURL());
        System.out.println("Domain: " + GetMachineStatistics.getMachineData().getDomainName());
        System.out.println("Hostname: " + GetMachineStatistics.getMachineData().getHostname());
        System.out.println("Status Message: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getStatusmessage());
        System.out.println("Status: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().isOperationalstatus());
        System.out.println("Memory: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getBytesusedMemory());
        System.out.println("Threads: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getNumberofActiveThreads());
        System.out.println("CPU%: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getPercentusedCPU());
        System.out.println("ID: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getId());
        System.out.println("Updated at: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getTimestamp());
        System.out.println("Started at: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getStartedAt());
        for (int i = 0; i < GetMachineStatistics.getMachineData().getMachinePerformanceData().getDriveInformation().size(); i++) {
            System.out.println("Partition: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getDriveInformation().get(i).getPartition());
            System.out.println("\tStatus: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getDriveInformation().get(i).getOperationalstatus());
            System.out.println("\tFreespace: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getDriveInformation().get(i).getFreespace());
            System.out.println("\tRead: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getDriveInformation().get(i).getKilobytespersecondDiskRead());
            System.out.println("\tWrite: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getDriveInformation().get(i).getKilobytespersecondDiskWrite());
        }
        for (int i = 0; i < GetMachineStatistics.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().size(); i++) {
            System.out.println("NIC: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(i).getAdapterName());
            System.out.println("\tStatus: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkReceive());
            System.out.println("\tFreespace: " + GetMachineStatistics.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkTransmit());
        }

    }

    private void getWSLogs() throws Exception {
        int x = getServiceFromSelection(PolicyType.TRANSACTIONAL);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        String url = GetListOfMonitoredServices.get(x).getURL();
        int records = getInt("Records: ", 1, 10000);
        int offset = getInt("Offset: ", 0, 1000000);
        long now = System.currentTimeMillis();
        List<TransactionLog> transactionLogs = sdk.getTransactionLogs(url, false, false, records, offset);
        now = System.currentTimeMillis() - now;
        System.out.println(transactionLogs.size() + " logs fetched in " + now + " ms");
        printWSLogs(transactionLogs);
    }

    private void printWSLogs(List<TransactionLog> transactionLogs) {
        System.out.println("Timestamp\tID\tRequest\tResponse\tResTime\tFault\tSLA");
        for (int i = 0; i < transactionLogs.size(); i++) {
            System.out.print(transactionLogs.get(i).getTimestamp().toString() + "\t" + transactionLogs.get(i).getTransactionId() + "\t" + transactionLogs.get(i).getRequestSize()
                    + "\t" + transactionLogs.get(i).getResponseSize() + "\t" + transactionLogs.get(i).getResponseTime() + "\t");
            if (!transactionLogs.get(i).isIsFault()) {
                System.out.print("yes");
            } else {
                System.out.print("no");
            }
            System.out.print("\t");
            if (transactionLogs.get(i).isIsSLAFault()) {
                System.out.print("yes");
            } else {
                System.out.print("no");
            }
            System.out.println();
        }
    }

    private void getWSLogsFault() throws Exception {
        int x = getServiceFromSelection(PolicyType.TRANSACTIONAL);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        String url = GetListOfMonitoredServices.get(x).getURL();
        int records = getInt("Records: ", 1, 1000);
        int offset = getInt("Offset: ", 0, 1000000);
        long now = System.currentTimeMillis();
        List<TransactionLog> transactionLogs = sdk.getTransactionLogs(url, true, false, records, offset);
        now = System.currentTimeMillis() - now;
        System.out.println(transactionLogs.size() + " logs fetched in " + now + " ms");
        printWSLogs(transactionLogs);
    }

    private void getWSLogsSLA() throws Exception {
        int x = getServiceFromSelection(PolicyType.TRANSACTIONAL);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        String url = GetListOfMonitoredServices.get(x).getURL();
        int records = getInt("Records: ", 1, 10000);
        int offset = getInt("Offset: ", 0, 1000000);
        long now = System.currentTimeMillis();
        List<TransactionLog> transactionLogs = sdk.getTransactionLogs(url, false, true, records, offset);
        now = System.currentTimeMillis() - now;
        System.out.println(transactionLogs.size() + " logs fetched in " + now + " ms");
        printWSLogs(transactionLogs);
    }

    private void GetGenSettings() throws Exception {
        GetGeneralSettingsResponseMsg generalSettings = sdk.getGeneralSettings();
        System.out.println("Key\tName\tValue");
        for (int i = 0; i < generalSettings.getKeyNameValue().size(); i++) {
            System.out.println(generalSettings.getKeyNameValue().get(i).getPropertyKey() + "\t"
                    + generalSettings.getKeyNameValue().get(i).getPropertyName() + "\t"
                    + generalSettings.getKeyNameValue().get(i).getPropertyValue());

        }
    }

    private void addGenSetting() throws Exception {
        System.out.print("Key = ");
        String key = System.console().readLine();

        System.out.print("Name = ");
        String name = System.console().readLine();

        System.out.print("Value = ");
        String value = System.console().readLine();
        sdk.addGeneralSetting(key, name, value, false);
        System.out.println("Settings added");
    }

    private void removeGenSetting() throws Exception {
        GetGeneralSettingsResponseMsg generalSettings = sdk.getGeneralSettings();
        System.out.println("Key\tName\tValue");
        for (int i = 0; i < generalSettings.getKeyNameValue().size(); i++) {
            System.out.println("[" + i + "]" + generalSettings.getKeyNameValue().get(i).getPropertyKey() + "\t"
                    + generalSettings.getKeyNameValue().get(i).getPropertyName() + "\t"
                    + generalSettings.getKeyNameValue().get(i).getPropertyValue());
        }
        int k = -1;
        while (k < 0 || k > generalSettings.getKeyNameValue().size()) {
            System.out.print("Enter Selection: ");
            String s = System.console().readLine();
            try {
                k = Integer.parseInt(s);
            } catch (Exception ex) {
                System.out.println("operating canceled");
                return;
            }
        }
        sdk.removeGeneralSetting(generalSettings.getKeyNameValue().get(k));
    }

    private ServicePolicy getPolicy() throws Exception {
        return sdk.GetServicePolicy(GetListOfMonitoredServices.get(getServiceFromSelection()).getURL());
    }

    private void removePolicy() throws Exception {
        int x = getServiceFromSelection();
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        sdk.RemoveServicePolicyAndData(GetListOfMonitoredServices.get(x).getURL());
        System.out.println("Removed");
    }

    private void printPolicy(ServicePolicy p) {
        System.out.println("URL: " + p.getURL());
        System.out.println("Parent: " + p.getParentObject());
        System.out.println("Display name: " + p.getDisplayName());
        System.out.println("Description: " + p.getDescription());
        System.out.println("Domain: " + p.getDomainName());
        System.out.println("Machine: " + p.getMachineName());
        System.out.println("POC: " + p.getPOC());
        System.out.println("Type: " + p.getPolicyType().value());
        System.out.println("External URL: " + p.getExternalURL());
        System.out.println("Data TTL: " + Utility.durationToString(p.getDataTTL()));
        if (p.getServiceLevelAggrements() != null && p.getServiceLevelAggrements() != null
                && !p.getServiceLevelAggrements().getSLA().isEmpty()) {
            System.out.println("SLA Defined: " + p.getServiceLevelAggrements().getSLA().size());
        }
        if (p.getFederationPolicyCollection() != null && !p.getFederationPolicyCollection().getFederationPolicy().isEmpty()) {
            System.out.println("Federation Defined: " + p.getFederationPolicyCollection().getFederationPolicy().size());
        }
    }

    private void getPolicyXml(ServicePolicy GetPolicy) throws JAXBException {
        JAXBContext GetSerializationContext = Utility.getSerializationContext();
        Marshaller createMarshaller = GetSerializationContext.createMarshaller();
        createMarshaller.marshal(GetPolicy, System.out);
        System.out.println();
    }

    private void printMachineInformation() throws Exception {
        int x = getServiceFromSelection(PolicyType.MACHINE);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        GetProcessesListByMachineResponseMsg machineInformation = sdk.getMachineInformation(GetListOfMonitoredServices.get(x));
        System.out.println("Cores: " + machineInformation.getMachineInformation().getCpucorecount());
        System.out.println("OS: " + machineInformation.getMachineInformation().getOperatingsystem());
        System.out.println("Memory: " + machineInformation.getMachineInformation().getMemoryinstalled());
        System.out.println("Last updated at: " + machineInformation.getLastupdateat());
        for (int i = 0; i < machineInformation.getMachineInformation().getPropertyPair().size(); i++) {
            System.out.println(machineInformation.getMachineInformation().getPropertyPair().get(i).getPropertyname() + " = " + machineInformation.getMachineInformation().getPropertyPair().get(i).getPropertyvalue());
        }
        System.out.println("-----Processes-----");
        for (int i = 0; i < machineInformation.getProcessName().size(); i++) {
            System.out.println(machineInformation.getProcessName().get(i));
        }
        for (int i = 0; i < machineInformation.getMachineInformation().getAddresses().size(); i++) {
            System.out.println("NIC: " + machineInformation.getMachineInformation().getAddresses().get(i).getAdapterName());
            System.out.println("\tIPs: " + Utility.listStringtoString(machineInformation.getMachineInformation().getAddresses().get(i).getIp()));
            System.out.println("\tDNS: " + Utility.listStringtoString(machineInformation.getMachineInformation().getAddresses().get(i).getDns()));
            System.out.println("\tDescription: " + machineInformation.getMachineInformation().getAddresses().get(i).getAdapterDescription());
            System.out.println("\tName: " + machineInformation.getMachineInformation().getAddresses().get(i).getAdapterName());
            System.out.println("\tGateway: " + machineInformation.getMachineInformation().getAddresses().get(i).getDefaultGateway());
            System.out.println("\tMAC: " + machineInformation.getMachineInformation().getAddresses().get(i).getMac());
            System.out.println("\tSubnet: " + machineInformation.getMachineInformation().getAddresses().get(i).getSubnetMask());
        }
        for (int i = 0; i < machineInformation.getMachineInformation().getDriveInformation().size(); i++) {
            System.out.println("Drive: " + machineInformation.getMachineInformation().getDriveInformation().get(i).getPartition());
            System.out.println("\tID: " + machineInformation.getMachineInformation().getDriveInformation().get(i).getId());
            System.out.println("\tStatus: " + machineInformation.getMachineInformation().getDriveInformation().get(i).getOperationalstatus());
            System.out.println("\tSystem ID: " + machineInformation.getMachineInformation().getDriveInformation().get(i).getSystemid());
            System.out.println("\tType: " + machineInformation.getMachineInformation().getDriveInformation().get(i).getType());
            System.out.println("\tTotal space: " + machineInformation.getMachineInformation().getDriveInformation().get(i).getTotalspace());

        }
    }

    private void addAProcessToMonitor() throws Exception {
        int x = getServiceFromSelection(PolicyType.MACHINE);
        if (x == -1 || x < 0 || x >= this.GetListOfMonitoredServices.size()) {
            return;
        }
        GetProcessesListByMachineResponseMsg machineInformation = sdk.getMachineInformation(GetListOfMonitoredServices.get(x));

        for (int i = 0; i < machineInformation.getProcessName().size(); i++) {
            System.out.println("[" + i + "] " + machineInformation.getProcessName().get(i));
        }
        int k = -1;
        while (k < 0 || k > machineInformation.getProcessName().size()) {
            System.out.print("Enter Selection: ");
            String s = System.console().readLine();
            try {
                k = Integer.parseInt(s);
            } catch (Exception ex) {
                System.out.println("operating canceled");
                return;
            }
        }
        sdk.StartProcessMonitor(machineInformation.getProcessName().get(k),
                machineInformation.getMachineInformation().getHostname(),
                machineInformation.getMachineInformation().getDomain(),
                null, //display name
                machineInformation.getMachineInformation().getUri(), 30L * 24L * 60L * 60L * 1000L,
                null //additional SLAs other than status change, send an email
        );
        System.out.println("Process Policy Created. It may take 30 seconds or more to start recieving performance data.");
    }

    private void dumpProperties() {
        Iterator<Entry<String, Object>> iterator = context.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Object> next = iterator.next();
            if (next.getKey() == FgsmsSDK.PASSWORD_PROPERTY) {
                System.out.println(next.getKey() + " = *********");
            } else {
                System.out.println(next.getKey() + " = " + next.getValue().toString());
            }
        }
    }

    private int getInt(String msg, int min, int max) {
        int k = -1;
        do {
            System.out.print(msg);
            try {
                String s = System.console().readLine();
                k = Integer.parseInt(s);
            } catch (Exception ex) {
            }
        } while (k > max || k < min);
        return k;
    }

    private void GetWSLogsDetails() throws Exception {
        String x = "";
        while (Utility.stringIsNullOrEmpty(x)) {
            System.out.print("Transaction ID = ");
            x = System.console().readLine();
        }

        long now = System.currentTimeMillis();
        GetMessageTransactionLogDetailsResponseMsg GetMessagePayload = sdk.GetMessagePayload(x);
        now = System.currentTimeMillis() - now;
        System.out.println("Details fetched in " + now + " ms");
        printWSDetails(GetMessagePayload);
    }

    private void printWSDetails(GetMessageTransactionLogDetailsResponseMsg pl) {
        System.out.println("Corrected URL = " + pl.getCorrectedURL());
        System.out.println("Requested URL = " + pl.getOriginalRequestURL());
        System.out.println("Action = " + pl.getAction());

        System.out.println("Monitored at = " + pl.getMonitorHostname());
        System.out.println("Service hosted at = " + pl.getServiceHostname());
        System.out.println("Thread id = " + pl.getTransactionthreadId());
        System.out.println("SLA fault = " + pl.getSlaFaultMsg());
        System.out.println("Agent memo = " + pl.getAgentMemo());
        System.out.println("Agent Type = " + pl.getAgentType());
        System.out.println("Related transaction = " + pl.getRelatedTransactionID());
        System.out.println("Thread id = " + pl.getTransactionthreadId());
        System.out.println("Requestor = " + Utility.listStringtoString(pl.getIdentity()));
        System.out.println("=============== Request");
        for (int i = 0; i < pl.getHeadersRequest().size(); i++) {
            System.out.println(pl.getHeadersRequest().get(i).getName() + " = " + Utility.listStringtoString(pl.getHeadersRequest().get(i).getValue()));
        }
        System.out.println(pl.getXmlRequestMessage());
        System.out.println("=============== Response");
        for (int i = 0; i < pl.getHeadersResponse().size(); i++) {
            System.out.println(pl.getHeadersResponse().get(i).getName() + " = " + Utility.listStringtoString(pl.getHeadersResponse().get(i).getValue()));
        }
        System.out.println(pl.getXmlResponseMessage());
    }

    private void machineNetBulk(String command) throws Exception {

        String split = command.toLowerCase().replace("machine-net-bulk", "");
        split = split.toLowerCase().replace("machine-net-loop", "");
        split = split.trim();
        String[] inputs = split.split("\\s");
        if (inputs.length != 2) {
            System.out.println("need to enter: (hostname prefix) (nic id)");
            return;
        }
        System.out.println("Parsed as " + inputs[0].trim() + " and " + inputs[1].trim());
        List<ServiceType> data = new ArrayList<ServiceType>();
        for (int i = 0; i < this.GetListOfMonitoredServices.size(); i++) {
            if (this.GetListOfMonitoredServices.get(i).getPolicyType() == PolicyType.MACHINE
                    && this.GetListOfMonitoredServices.get(i).getHostname().toLowerCase().startsWith(inputs[0])) {
                data.add(this.GetListOfMonitoredServices.get(i));
            }
        }

        List<ServicePolicy> pols = new ArrayList<ServicePolicy>();
        for (int i = 0; i < data.size(); i++) {
            ServicePolicy policy = sdk.GetServicePolicy(data.get(i).getURL());
            MachinePolicy mp = (MachinePolicy) policy;
            boolean ok = false;
            for (int k = 0; k < mp.getRecordNetworkUsage().size(); k++) {
                if (mp.getRecordNetworkUsage().get(k).equalsIgnoreCase(inputs[1])) {
                    ok = true;
                }
            }
            if (!ok) {
                mp.getRecordNetworkUsage().add(inputs[1]);
                pols.add(mp);
            }
        }
        System.out.println(pols.size() + " to be altered");
        sdk.SetServicePolicies(pols);
        System.out.println("success");

    }

    private void machineDelBulk(String command) throws Exception {

        String split = command.toLowerCase().replace("machine-del-bulk", "");
        split = split.trim();

        if (split.length() == 0) {
            System.out.println("need to enter: (prefix) ");
            return;
        }

        System.out.println("Parsed as " + split);
        List<String> urls = new ArrayList<String>();

        for (int i = 0; i < this.GetListOfMonitoredServices.size(); i++) {
            if (this.GetListOfMonitoredServices.get(i).getPolicyType() == PolicyType.MACHINE
                    && this.GetListOfMonitoredServices.get(i).getHostname().toLowerCase().startsWith(split)) {
                urls.add(this.GetListOfMonitoredServices.get(i).getURL());
            }
        }

        System.out.println(urls.size() + " to be altered");

        sdk.DeleteServicePolicies(urls);
        System.out.println("success");

    }

    private void reportsDroneNic(String command) throws Exception {

        String split = command.toLowerCase().replace("report-drone-nic", "");
        split = split.trim();

        if (split.length() == 0) {
            System.out.println("need to enter: (prefix) (startTime) (endTime) (nic)");
            return;
        }

        System.out.println("Parsed as " + split);
        String[] items = split.split(" ");

        List<String> urls = new ArrayList<String>();

        for (int i = 0; i < this.GetListOfMonitoredServices.size(); i++) {
            if (this.GetListOfMonitoredServices.get(i).getPolicyType() == PolicyType.MACHINE
                    && this.GetListOfMonitoredServices.get(i).getHostname().toLowerCase().startsWith(items[0])) {
                urls.add(this.GetListOfMonitoredServices.get(i).getURL());
            }
        }

        System.out.println(urls.size() + " to be fetched");
        String filename = System.currentTimeMillis() + "CSVExport.zip";
        sdk.GetHtmlReportNIC(urls, items[3], Long.parseLong(items[1]), Long.parseLong(items[2]), filename);

        System.out.println("success, written to " + new File(filename).getAbsolutePath());

    }

    private void exportsDroneNic(String command) throws Exception {

        String split = command.toLowerCase().replace("export-drone-nic", "");
        split = split.trim();

        if (split.length() == 0) {
            System.out.println("need to enter: (prefix) (startTime) (endTime) (nic)");
            return;
        }

        System.out.println("Parsed as " + split);
        String[] items = split.split(" ");

        List<String> urls = new ArrayList<String>();

        for (int i = 0; i < this.GetListOfMonitoredServices.size(); i++) {
            if (this.GetListOfMonitoredServices.get(i).getPolicyType() == PolicyType.MACHINE
                    && this.GetListOfMonitoredServices.get(i).getHostname().toLowerCase().startsWith(items[0])) {
                urls.add(this.GetListOfMonitoredServices.get(i).getURL());
            }
        }

        System.out.println(urls.size() + " to be fetched");
        String filename = System.currentTimeMillis() + "CSVExport.zip";
        sdk.GetCSVExportNIC(urls, items[3], Long.parseLong(items[1]), Long.parseLong(items[2]), filename);

        System.out.println("success, written to " + new File(filename).getAbsolutePath());

    }
}
