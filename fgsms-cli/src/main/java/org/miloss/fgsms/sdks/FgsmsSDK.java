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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.sdks;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Constants.AuthMode;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService_Service;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.*;

import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.reportingservice.ArrayOfReportTypeContainer;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportCSVDataRequestMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataRequestMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToCSVResponseMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTMLResponse;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTMLResponseMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService_Service;
import org.miloss.fgsms.services.interfaces.status.GetStatusRequestMsg;
import org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg;
import org.miloss.fgsms.services.interfaces.status.StatusService;
import org.miloss.fgsms.services.interfaces.status.StatusServiceService;
import org.apache.log4j.*;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * This class is an over simplified SDK, it basically automates creating web
 * service proxies for all of the FGSMS service endpoints from a configuration
 * file and provides some wrappers for some of the mundane tasks that you may
 * find yourself tackling.
 *
 * @author AO
 */
public class FgsmsSDK {

    private DataAccessService das;
    private PCS pcs;
    private StatusService ss;
    private ReportingService rs;
    private AutomatedReportingService ars;

    final static Logger log = Logger.getLogger("fgsms.SDK");
    public static final String USERNAME_PROPERTY = BindingProvider.USERNAME_PROPERTY;
    public static final String PASSWORD_PROPERTY = BindingProvider.PASSWORD_PROPERTY;
    public static final String DAS_ENDPOINT = "org.miloss.fgsms.dasEndpoint";
    public static final String ARS_ENDPOINT = "org.miloss.fgsms.arsEndpoint";
    public static final String PCS_ENDPOINT = "org.miloss.fgsms.pcsEndpoint";
    public static final String SS_ENDPOINT = "org.miloss.fgsms.ssEndpoint";
    public static final String RS_ENDPOINT = "org.miloss.fgsms.rsEndpoint";
    public static final String AUTH_MODE = "org.miloss.fgsms.authMode";
    public static final String CLASSIFICATION_LEVEL = "org.miloss.fgsms.classification";
    public static final String CLASSIFICATION_CAVEAT = "org.miloss.fgsms.caveat";
    private SecurityWrapper sc = new SecurityWrapper(ClassificationType.U, "");

    public FgsmsSDK(Map<String, Object> context) throws Exception {
        AuthMode mode = (AuthMode) context.get(AUTH_MODE);
        if (mode == null) {
            throw new Exception("Auth Mode is not defined");
        }

        DataAccessService_Service svc = new DataAccessService_Service();
        das = svc.getDASPort();
        BindingProvider bp = (BindingProvider) das;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, context.get(DAS_ENDPOINT));

        if (mode == AuthMode.UsernamePassword) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, context.get(USERNAME_PROPERTY));
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, context.get(PASSWORD_PROPERTY));
        }

        PolicyConfigurationService svc2 = new PolicyConfigurationService();
        pcs = svc2.getPCSPort();
        bp = (BindingProvider) pcs;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, context.get(PCS_ENDPOINT));

        if (mode == AuthMode.UsernamePassword) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, context.get(USERNAME_PROPERTY));
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, context.get(PASSWORD_PROPERTY));
        }

        ReportingService_Service svc3 = new ReportingService_Service();
        rs = svc3.getReportingServicePort();
        bp = (BindingProvider) rs;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, context.get(RS_ENDPOINT));

        if (mode == AuthMode.UsernamePassword) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, context.get(USERNAME_PROPERTY));
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, context.get(PASSWORD_PROPERTY));
        }

        AutomatedReportingService_Service svc4 = new AutomatedReportingService_Service();
        ars = svc4.getAutomatedReportingServicePort();
        bp = (BindingProvider) ars;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, context.get(ARS_ENDPOINT));

        if (mode == AuthMode.UsernamePassword) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, context.get(USERNAME_PROPERTY));
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, context.get(PASSWORD_PROPERTY));
        }

        StatusServiceService svc5 = new StatusServiceService();
        ss = svc5.getStatusServicePort();
        bp = (BindingProvider) ss;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, context.get(SS_ENDPOINT));

        if (mode == AuthMode.UsernamePassword) {
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, context.get(USERNAME_PROPERTY));
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, context.get(PASSWORD_PROPERTY));
        }

        if (context.get(CLASSIFICATION_LEVEL) != null) {
            sc.setClassification((ClassificationType) context.get(CLASSIFICATION_LEVEL));
        }
        if (context.get(CLASSIFICATION_CAVEAT) != null) {
            sc.setCaveats((String) context.get(CLASSIFICATION_CAVEAT));
        }

    }

    public List<ServiceType> GetListOfMonitoredServices() throws Exception {
        GetMonitoredServiceListRequestMsg req = new GetMonitoredServiceListRequestMsg();
        req.setClassification(sc);
        GetMonitoredServiceListResponseMsg monitoredServiceList = das.getMonitoredServiceList(req);
        if (monitoredServiceList != null && monitoredServiceList.getServiceList() != null && monitoredServiceList.getServiceList() != null) {
            return monitoredServiceList.getServiceList().getServiceType();
        }
        return null;
    }

    public ServicePolicy GetServicePolicy(String url) throws Exception {
        ServicePolicyRequestMsg req = new ServicePolicyRequestMsg();
        req.setClassification(sc);
        req.setURI(url);
        ServicePolicyResponseMsg servicePolicy = pcs.getServicePolicy(req);
        return servicePolicy.getPolicy();
    }

    public GetStatusResponseMsg GetStatus(String url) throws Exception {
        GetStatusRequestMsg req = new GetStatusRequestMsg();
        req.setURI(url);
        req.setClassification(sc);
        GetStatusResponseMsg status = ss.getStatus(req);
        return status;
    }

    public GetQuickStatsResponseMsg GetAggregatedStatistics(String url) throws Exception {
        GetQuickStatsRequestMsg req = new GetQuickStatsRequestMsg();
        req.setClassification(sc);
        req.setUri(url);
        GetQuickStatsResponseMsg quickStats = das.getQuickStats(req);
        return quickStats;
    }

    public GetMostRecentMachineDataResponseMsg GetMachineStatistics(String url) throws Exception {
        GetMostRecentMachineDataRequestMsg req = new GetMostRecentMachineDataRequestMsg();
        req.setUri(url);
        req.setClassification(sc);
        GetMostRecentMachineDataResponseMsg mostRecentMachineData = das.getMostRecentMachineData(req);
        return mostRecentMachineData;
    }

    public GetMostRecentProcessDataResponseMsg GetProcessStatistics(String url) throws Exception {
        GetMostRecentProcessDataRequestMsg req = new GetMostRecentProcessDataRequestMsg();
        req.setUri(url);
        req.setClassification(sc);
        GetMostRecentProcessDataResponseMsg mostRecentMachineData = das.getMostRecentProcessData(req);
        return mostRecentMachineData;
    }

    public GetCurrentBrokerDetailsResponseMsg GetBrokerStatistics(String url) throws Exception {
        GetCurrentBrokerDetailsRequestMsg req = new GetCurrentBrokerDetailsRequestMsg();
        req.setClassification(sc);
        req.setUrl(url);
        GetCurrentBrokerDetailsResponseMsg currentBrokerDetails = das.getCurrentBrokerDetails(req);
        return currentBrokerDetails;
    }

    public GetGlobalPolicyResponseMsg GetGlobalPolicy() throws Exception {
        GetGlobalPolicyRequestMsg req = new GetGlobalPolicyRequestMsg();
        req.setClassification(sc);
        GetGlobalPolicyResponseMsg globalPolicy = pcs.getGlobalPolicy(req);
        sc = globalPolicy.getClassification();
        return globalPolicy;
    }

    public GetMessageTransactionLogDetailsResponseMsg GetMessagePayload(String uuid) throws Exception {
        GetMessageTransactionLogDetailsMsg req = new GetMessageTransactionLogDetailsMsg();
        req.setTransactionID(uuid);
        req.setClassification(sc);
        GetMessageTransactionLogDetailsResponseMsg res = das.getMessageTransactionLogDetails(req);
        return res;
    }

    List<GetStatusResponseMsg> GetStatusAll() throws Exception {
        GetStatusRequestMsg req = new GetStatusRequestMsg();
        req.setClassification(sc);
        List<GetStatusResponseMsg> allStatus = ss.getAllStatus(req);
        return allStatus;
    }

    void RemoveServicePolicyAndData(String url) throws Exception {
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(sc);
        req.setURL(url);
        req.setDeletePerformanceData(true);
        pcs.deleteServicePolicy(req);
    }

    GetGeneralSettingsResponseMsg getGeneralSettings() throws Exception {
        GetGeneralSettingsRequestMsg req = new GetGeneralSettingsRequestMsg();
        req.setClassification(sc);
        return pcs.getGeneralSettings(req);
    }

    void removeGeneralSetting(KeyNameValue get) throws Exception {
        RemoveGeneralSettingsRequestMsg req = new RemoveGeneralSettingsRequestMsg();
        req.setClassification(sc);
        req.getKeyNameValue().add(get);
        pcs.removeGeneralSettings(req);
    }

    void addGeneralSetting(String key, String name, String value, boolean shouldEncrypt) throws Exception {
        SetGeneralSettingsRequestMsg req = new SetGeneralSettingsRequestMsg();
        req.setClassification(sc);
        req.getKeyNameValueEnc().add(new KeyNameValueEnc());
        req.getKeyNameValueEnc().get(0).setKeyNameValue(new KeyNameValue());
        req.getKeyNameValueEnc().get(0).getKeyNameValue().setPropertyKey(key);
        req.getKeyNameValueEnc().get(0).getKeyNameValue().setPropertyName(name);
        req.getKeyNameValueEnc().get(0).getKeyNameValue().setPropertyValue(value);
        req.getKeyNameValueEnc().get(0).setShouldEncrypt(shouldEncrypt);
        pcs.setGeneralSettings(req);
    }

    GetProcessesListByMachineResponseMsg getMachineInformation(ServiceType get) throws Exception {

        GetProcessesListByMachineRequestMsg req = new GetProcessesListByMachineRequestMsg();
        req.setHostname(get.getHostname());
        req.setClassification(sc);
        GetProcessesListByMachineResponseMsg res = pcs.getProcessesListByMachine(req);
        return res;
    }

    GetMailSettingsResponseMsg getEmailsettings() throws Exception {
        GetMailSettingsRequestMsg req = new GetMailSettingsRequestMsg();
        req.setClassification(sc);
        GetMailSettingsResponseMsg mailSettings = pcs.getMailSettings(req);
        return mailSettings;
    }

    SetServicePolicyResponseMsg StartProcessMonitor(String process, String machine, String domain, String displayname, String machineurl, long datattl, List<SLA> slas) throws Exception {
        SetServicePolicyRequestMsg preq = new SetServicePolicyRequestMsg();
        preq.setClassification(sc);
        preq.setURL("urn:" + machine.toLowerCase() + ":" + process);
        ProcessPolicy pp = new ProcessPolicy();
        pp.setMachineName(machine.toLowerCase());
        pp.setDomainName(domain);
        pp.setDisplayName(displayname);
        pp.setPolicyType(PolicyType.PROCESS);
        pp.setAgentsEnabled(true);
        pp.setURL("urn:" + machine.toLowerCase() + ":" + process);
        pp.setParentObject(machineurl);
        pp.setDataTTL(DatatypeFactory.newInstance().newDuration(datattl));
        //30L * 24L * 60L * 60L * 1000L));
        Utility.addStatusChangeSLA(pp);
        if (slas != null) {
            pp.getServiceLevelAggrements().getSLA().addAll(slas);
        }
        preq.setPolicy(pp);
        return pcs.setServicePolicy(preq);
    }

    List<TransactionLog> getTransactionLogs(String url, boolean faultsonly, boolean slaonly, int records, int offset) throws Exception {
        GetRecentMessageLogsRequestMsg req = new GetRecentMessageLogsRequestMsg();
        req.setClassification(sc);
        req.setFaultsOnly(faultsonly);
        req.setSlaViolationsOnly(slaonly);
        req.setURL(url);
        req.setOffset(offset);
        req.setRecords(records);
        GetMessageLogsResponseMsg recentMessageLogs = das.getRecentMessageLogs(req);
        return recentMessageLogs.getLogs().getTransactionLog();
    }

    void SetServicePolicies(List<ServicePolicy> pols) throws Exception {
        for (int i = 0; i < pols.size(); i++) {
            SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
            req.setClassification(sc);
            req.setURL(pols.get(i).getURL());
            req.setPolicy(pols.get(i));
            pcs.setServicePolicy(req);
        }

    }

    void DeleteServicePolicies(List<String> urls) throws Exception {
        for (int i = 0; i < urls.size(); i++) {
            DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
            req.setClassification(sc);
            req.setURL(urls.get(i));
            req.setDeletePerformanceData(true);
            pcs.deleteServicePolicy(req);
        }
    }

    public void GetCSVExportNIC(List<String> urls, String NICID, long start, long end, String filename) throws Exception {
        ExportCSVDataRequestMsg req = new ExportCSVDataRequestMsg();
        req.setClassification(sc);
        req.setAllServices(false);
        req.setExportType(ExportRecordsEnum.MACHINE);
        req.setRange(new TimeRange());
        req.getRange().setStart(CreateXmlGreg(start));
        req.getRange().setEnd(CreateXmlGreg(end));
        req.getURLs().addAll(urls);
        ExportDataToCSVResponseMsg exportDataToCSV = this.rs.exportDataToCSV(req);
        WriteBytes(exportDataToCSV.getZipFile(), filename);
    }
    static DatatypeFactory df = null;

    private Calendar CreateXmlGreg(long time) throws DatatypeConfigurationException {
        if (df == null) {
            df = DatatypeFactory.newInstance();
        }
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(time);
        return (gcal);
    }

    private void WriteBytes(byte[] zipFile, String filename) throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(zipFile);
        fos.flush();
        fos.close();
    }

    String getReportClassname(String type) throws Exception {
        GetPluginList r = new GetPluginList();
        r.setRequest(new GetPluginListRequestMsg());
        r.getRequest().setClassification(sc);
        r.getRequest().setPlugintype("REPORTING");
        try {
            GetPluginListResponse pluginList = pcs.getPluginList(r);
            for (int i = 0; i < pluginList.getResponse().getPlugins().size(); i++) {
                if (pluginList.getResponse().getPlugins().get(i).getClassname().endsWith(type)) {
                    return type;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        throw new IllegalArgumentException(type);

    }

    void GetHtmlReportNIC(List<String> urls, String NICID, long start, long end, String filename) throws Exception {
        ExportDataRequestMsg req = new ExportDataRequestMsg();
        req.setClassification(sc);
        req.setAllServices(false);
        req.setReportTypes(new ArrayOfReportTypeContainer());

        ReportTypeContainer r = new ReportTypeContainer();
        r.setType(getReportClassname("CpuUsageReport"));
        req.getReportTypes().getReportTypeContainer().add(r);
        r = new ReportTypeContainer();
        r.setType(getReportClassname("NetworkIOReport"));
        req.getReportTypes().getReportTypeContainer().add(r);

        r = new ReportTypeContainer();
        r.setType(getReportClassname("MemoryUsageReport"));
        req.getReportTypes().getReportTypeContainer().add(r);

        req.setRange(new TimeRange());
        req.getRange().setStart(CreateXmlGreg(start));
        req.getRange().setEnd(CreateXmlGreg(end));
        req.getURLs().addAll(urls);
        ExportDataToHTMLResponseMsg exportDataToCSV = this.rs.exportDataToHTML(req);
        WriteBytes(exportDataToCSV.getZipFile(), filename);
    }
}
