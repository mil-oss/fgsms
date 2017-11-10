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
package org.miloss.fgsms.services.ars.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.UUID;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.*;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AccessDeniedException;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ServiceUnavailableException;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.plugins.reporting.ReportGeneratorPlugin;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * Reporting Service Port Type This service basically reads and writes to the
 * database to set/get/fetch/delete reports and report jobs
 *
 * @since 6.2
 */


@WebService(name = "automatedReportingService", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService"
//, wsdlLocation = "ARSv1.wsdl"
)
@XmlSeeAlso({
    org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory.class,
    us.gov.ic.ism.v2.ObjectFactory.class,
    org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory.class,
    org.miloss.fgsms.services.interfaces.common.ObjectFactory.class,
    org.miloss.fgsms.services.interfaces.faults.ObjectFactory.class,
    org.miloss.fgsms.services.interfaces.automatedreportingservice.ObjectFactory.class
})
public class AutomatedReportingServiceImpl implements AutomatedReportingService, org.miloss.fgsms.services.interfaces.automatedreportingservice.OpStatusService {

    private static final String name = "fgsms.AutomatedReportingService";
    private static DatatypeFactory df = null;
    private static Calendar started = null;
    final static Logger log = Logger.getLogger(name);
    private static final String OK = "OK";

    private static void validatePluginRegistered(String type) throws Exception {
        Connection configurationDBConnection = Utility.getConfigurationDBConnection();
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            cmd = configurationDBConnection.prepareStatement("select * from plugins where classname=? and appliesto='REPORTING'");
            cmd.setString(1, type);
            ResultSet executeQuery = cmd.executeQuery();
            if (executeQuery.next()) {
                //we are ok
            } else {
                throw new IllegalArgumentException("Plugin '" + type + "' not registered");
            }
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
            throw ex;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(configurationDBConnection);
        }
    }

    public AutomatedReportingServiceImpl() throws DatatypeConfigurationException {

        Init();
    }

    /**
     * constructor used for unit testing, do not remove
     *
     * @param w
     */
    public AutomatedReportingServiceImpl(WebServiceContext w) throws DatatypeConfigurationException {
        ctx = w;
        Init();
    }
    @Resource
    private WebServiceContext ctx;

    private synchronized void Init() throws DatatypeConfigurationException {
        if (df == null) {
            df = DatatypeFactory.newInstance();
        }
        if (started == null) {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            started = (gcal);
        }
    }

    /**
     *     * Gets a list of jobs owned by the current user and a list of recently
     * generated reports
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.automatedreportingservice.GetMyScheduledReportsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMyScheduledReports", action = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService/GetMyScheduledReports")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService")
    @RequestWrapper(localName = "GetMyScheduledReports", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.GetMyScheduledReports")
    @ResponseWrapper(localName = "GetMyScheduledReportsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.GetMyScheduledReportsResponse")
    public GetMyScheduledReportsResponseMsg getMyScheduledReports(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService") GetMyScheduledReportsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getMyScheduledReports", currentUser, "", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getMyScheduledReports", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        //log.log(Level.INFO, name + "exportDataToHTML" + currentUser);
        //no authorization is necessary here

        if (request.getRecordlimit() == 0) {
            request.setRecordlimit(100);
        }
        //when reading the database by sure to use the column to override the lastupdated field
        GetMyScheduledReportsResponseMsg ret = new GetMyScheduledReportsResponseMsg();
        Connection con = null;
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            ret.setClassification(getCurrentOperatingClassificationLevel());
            JAXBContext GetARSSerializationContext = Utility.getARSSerializationContext();
            Unmarshaller u = GetARSSerializationContext.createUnmarshaller();
            con = Utility.getPerformanceDBConnection();
            cmd = con.prepareStatement("select * from arsjobs where owninguser=?;");
            cmd.setString(1, currentUser);
            rs = cmd.executeQuery();
            while (rs.next()) {
                ExistingReportDefitions ed = new ExistingReportDefitions();
                byte[] s = rs.getBytes("reportdef");
                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                JAXBElement<ReportDefinition> foo = (JAXBElement<ReportDefinition>) u.unmarshal(r, ReportDefinition.class);
                if (foo != null && foo.getValue() != null) {
                    ed.setJob(foo.getValue());
                }
                ed.getJob().setLastRanAt(ConvertToXmlGreg(rs.getLong("lastranat")));
                ret.getCompletedJobs().add(ed);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        con = null;

        try {
            con = Utility.getPerformanceDBConnection();

            //check for completed jobs
            for (int i = 0; i < ret.getCompletedJobs().size(); i++) {

                cmd = con.prepareStatement("select * from arsreports where jobid=? limit  ? offset ?");
                cmd.setString(1, ret.getCompletedJobs().get(i).getJob().getJobId());
                cmd.setInt(2, request.getRecordlimit());
                cmd.setInt(3, request.getOffset());
                rs = cmd.executeQuery();
                while (rs.next()) {
                    CompletedJobs cj = new CompletedJobs();
                    cj.setReportId(rs.getString("reportid"));
                    cj.setTimestamp(ConvertToXmlGreg(rs.getLong("datetime")));
                    ret.getCompletedJobs().get(i).getReports().add(cj);
                }

                rs.close();
                cmd.close();
            }
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw f;

    }

    private ReportDefinition loadReportDef(String job) throws Exception {
        Connection con = null;
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            ReportDefinition ret = null;
            JAXBContext GetARSSerializationContext = Utility.getARSSerializationContext();
            Unmarshaller u = GetARSSerializationContext.createUnmarshaller();
            con = Utility.getPerformanceDBConnection();
            cmd = con.prepareStatement("select * from arsjobs where jobid=?;");
            cmd.setString(1, job);
            rs = cmd.executeQuery();
            if (rs.next()) {

                byte[] s = rs.getBytes("reportdef");
                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                JAXBElement<ReportDefinition> foo = (JAXBElement<ReportDefinition>) u.unmarshal(r, ReportDefinition.class);
                if (foo != null && foo.getValue() != null) {
                    ret = foo.getValue();
                }

            }

            return ret;
        } catch (Exception ex) {
            log.log(Level.WARN, "loadReportDef error", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        throw new IllegalArgumentException("job not found");
    }

    private SecurityWrapper getCurrentOperatingClassificationLevel() {
        try {
            SecurityWrapper t = getGlobalPolicyFromDB().getClassification();
            log.log(Level.INFO, "PCS, current security classification is " + Utility.ICMClassificationToString(t.getClassification()) + " " + t.getCaveats());
            return t;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to determine current classification level. Is the database available?", ex);
        }
        throw new IllegalAccessError();
    }

    private GetGlobalPolicyResponseMsg getGlobalPolicyFromDB() throws org.miloss.fgsms.services.interfaces.policyconfiguration.ServiceUnavailableException, ServiceUnavailableException {

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        boolean foundPolicy = false;
        GetGlobalPolicyResponseMsg response = new GetGlobalPolicyResponseMsg();
        try {
            response.setPolicy(new GlobalPolicy());
            //we don't care about the request in this case

            comm = con.prepareStatement("Select * from GlobalPolicies;");

            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            results = comm.executeQuery();
            //       int count = 0;

            if (results.next()) {
                response.getPolicy().setPolicyRefreshRate(df.newDuration(results.getLong("PolicyRefreshRate")));
                response.getPolicy().setRecordedMessageCap(results.getInt("RecordedMessageCap"));
                SecurityWrapper wrap = new SecurityWrapper(ClassificationType.fromValue(results.getString("classification")),
                        results.getString("caveat"));
                response.setClassification(wrap);
                response.getPolicy().setAgentsEnabled(results.getBoolean("agentsenable"));
                foundPolicy = true;
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting global policy", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        try {
            if (!foundPolicy) {
                try {
                    comm = con.prepareStatement("INSERT INTO GlobalPolicies (PolicyRefreshRate, RecordedMessageCap, classification, agentsenable, caveat) "
                            + " VALUES (?, ?, ?, true, ?);");
                    comm.setLong(1, 30 * 60 * 100);
                    comm.setLong(2, 1024000);
                    comm.setString(3, "U");
                    comm.setString(4, "");
                    comm.execute();
                    response.getPolicy().setRecordedMessageCap(1024000);
                    response.getPolicy().setClassification(new SecurityWrapper(ClassificationType.U, ""));
                    response.getPolicy().setAgentsEnabled(true);
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "error setting global policy", ex);
                }
            }
            KeyNameValueEnc d = DBSettingsLoader.GetPropertiesFromDB(true, "UddiPublisher", "Interval");
            if (d != null && d.getKeyNameValue() != null) {
                try {
                    response.getPolicy().setUDDIPublishRate(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                } catch (Exception ex) {
                    response.getPolicy().setUDDIPublishRate(df.newDuration(30 * 60 * 100));
                }
            }
            //response.setClassification(getCurrentOperatingClassificationLevel());
            return response;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting global policy", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    }

    /**
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.automatedreportingservice.AddOrUpdateScheduledReportResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "AddOrUpdateScheduledReport", action = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService/AddOrUpdateScheduledReport")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService")
    @RequestWrapper(localName = "AddOrUpdateScheduledReport", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.AddOrUpdateScheduledReport")
    @ResponseWrapper(localName = "AddOrUpdateScheduledReportResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.AddOrUpdateScheduledReportResponse")
    public AddOrUpdateScheduledReportResponseMsg addOrUpdateScheduledReport(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService") AddOrUpdateScheduledReportRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "addOrUpdateScheduledReport", currentUser, "", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        if (request.getJobs().isEmpty()) {
            throw new IllegalArgumentException("at least one report must be specified for updating");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "addOrUpdateScheduledReport", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        //validate request
        //loop through all jobs, add job ids if not present, authorize the request
        validateRequest(request, currentUser, ctx);

        //     if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "addOrUpdateScheduledReport", request.getClassification(), ctx))
        //TODO admins?
        Connection con = Utility.getPerformanceDBConnection();
        for (int i = 0; i < request.getJobs().size(); i++) {
            PreparedStatement cmd = null;
            try {

                StringWriter sw = new StringWriter();

                if (!Utility.stringIsNullOrEmpty(request.getJobs().get(i).getJobId())) {
                    cmd = con.prepareStatement("UPDATE arsjobs SET  reportdef=?, hasextrapermissions=?, enabled=? WHERE jobid=?;");
                    //cmd.setBytes(1, bits);
                    cmd.setBoolean(2, !request.getJobs().get(i).getAdditionalReaders().isEmpty());
                    cmd.setBoolean(3, request.getJobs().get(i).isEnabled());
                    cmd.setString(4, request.getJobs().get(i).getJobId());
                } else {
                    request.getJobs().get(i).setJobId(UUID.randomUUID().toString());
                    cmd = con.prepareStatement("INSERT INTO arsjobs (reportdef, hasextrapermissions,  enabled, lastranat, jobid, owninguser) values (?,?,?,?,?,?);");

                    cmd.setBoolean(2, !request.getJobs().get(i).getAdditionalReaders().isEmpty());

                    cmd.setBoolean(3, request.getJobs().get(i).isEnabled());
                    cmd.setLong(4, 0);
                    cmd.setString(5, request.getJobs().get(i).getJobId());
                    cmd.setString(6, currentUser);
                }
                Utility.getARSSerializationContext().createMarshaller().marshal(request.getJobs().get(i), sw);
                byte[] bits = sw.toString().getBytes(Constants.CHARSET);
                cmd.setBytes(1, bits);
                cmd.executeUpdate();
                cmd.close();
            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(cmd);
            }
        }
        if (con != null) {
            DBUtils.safeClose(con);
        }
        AddOrUpdateScheduledReportResponseMsg r = new AddOrUpdateScheduledReportResponseMsg();
        r.setClassification(getCurrentOperatingClassificationLevel());
        r.getJobs().addAll(request.getJobs());
        r.setSuccess(true);
        r.setMessage(OK);
        return r;

    }

    /**
     ** This will delete the job AND all reports associated with the job
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.automatedreportingservice.DeleteScheduledReportResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "DeleteScheduledReport", action = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService/DeleteScheduledReport")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService")
    @RequestWrapper(localName = "DeleteScheduledReport", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.DeleteScheduledReport")
    @ResponseWrapper(localName = "DeleteScheduledReportResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.DeleteScheduledReportResponse")
    public DeleteScheduledReportResponseMsg deleteScheduledReport(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService") DeleteScheduledReportRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "deleteScheduledReport", currentUser, "", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        if (Utility.stringIsNullOrEmpty(request.getJobId())) {
            throw new IllegalArgumentException("a job id must be specified");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "deleteScheduledReport", currentUser, request.getJobId(), (request.getClassification()), ctx.getMessageContext());

        Connection con = null;
        PreparedStatement cmd = null;
        try {

            con = Utility.getPerformanceDBConnection();

            if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "deleteScheduledReport", request.getClassification(), ctx)) {
                cmd = con.prepareStatement("delete from arsjobs where jobid=? ");
                cmd.setString(1, request.getJobId());
            } else {
                cmd = con.prepareStatement("delete from arsjobs where owninguser=? and jobid=? ");
                cmd.setString(1, currentUser);
                cmd.setString(2, request.getJobId());
            }
            int jobsdeleted = cmd.executeUpdate();
            cmd.close();
            if (jobsdeleted == 0) {
                con.close();
                throw new AccessDeniedException("either the job doesn't exist or you don't own it", new org.miloss.fgsms.services.interfaces.faults.AccessDeniedException());
            }

            cmd = con.prepareStatement("delete from arsreports where jobid=? ");
            cmd.setString(1, request.getJobId());
            cmd.executeUpdate();

            DeleteScheduledReportResponseMsg r = new DeleteScheduledReportResponseMsg();
            r.setClassification(getCurrentOperatingClassificationLevel());
            r.setSuccess(true);
            r.setMessage(OK);
            return r;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);

            ServiceUnavailableException f = new ServiceUnavailableException("", null);

            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
    }

    /**
     * Gets a specific generated report
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.automatedreportingservice.GetReportResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetReport", action = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService/GetReport")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService")
    @RequestWrapper(localName = "GetReport", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.GetReport")
    @ResponseWrapper(localName = "GetReportResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.GetReportResponse")
    public GetReportResponseMsg getReport(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService") GetReportRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getReport", currentUser, "", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        if (Utility.stringIsNullOrEmpty(request.getReportId())) {
            throw new IllegalArgumentException("a report id must be specified");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getReport", currentUser, request.getReportId(), (request.getClassification()), ctx.getMessageContext());
        GetReportResponseMsg r = new GetReportResponseMsg();
        r.setClassification(getCurrentOperatingClassificationLevel());
        r.setReportId(request.getReportId());
        r.setZippedReport(get_a_Report(request.getReportId(), currentUser, request.getClassification()));
        if (r.getZippedReport() == null) {
            throw new IllegalArgumentException("report not found");
        }
        return r;
    }

    protected byte[] get_a_Report(final String id, final String currentUser, SecurityWrapper wrapper) throws AccessDeniedException {
        String job = null;
        boolean access = false;
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement cmd = null;
        try {
            con = Utility.getPerformanceDBConnection();
            cmd = con.prepareStatement("select jobid from arsreports where reportid=?");
            cmd.setString(1, id);
            rs = cmd.executeQuery();
            if (rs.next()) {
                job = rs.getString(1);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            if (ex instanceof AccessDeniedException) {
                throw (AccessDeniedException) ex;
            }
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        try {

            if (!Utility.stringIsNullOrEmpty(job)) {
                ReportDefinition rd = loadReportDef(job);
                if (rd.getOwner().equalsIgnoreCase(currentUser)) {
                    access = true;
                } else {
                    for (int i = 0; i < rd.getAdditionalReaders().size(); i++) {
                        if (currentUser.equalsIgnoreCase(rd.getAdditionalReaders().get(i))) {
                            access = true;
                        }
                    }
                }
                if (access) {
                    //get the report
                    con = Utility.getPerformanceDBConnection();
                    cmd = con.prepareStatement("select reportcontents from arsreports where reportid=?");
                    cmd.setString(1, id);
                    rs = cmd.executeQuery();
                    byte[] bits = null;
                    if (rs.next()) {
                        bits = rs.getBytes(1);
                    }
                    if (bits != null) {
                        return bits;
                    }
                } else {
                    throw new AccessDeniedException("", new org.miloss.fgsms.services.interfaces.faults.AccessDeniedException());
                }
            } else {
                //report not found
                return null;
            }
        } catch (Exception ex) {

            if (ex instanceof AccessDeniedException) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "getReport", currentUser, "FAILURE, attempt to access ARS report " + id, wrapper, ctx.getMessageContext());
                throw (AccessDeniedException) ex;
            }
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        return null;
    }

    /**
     ** Deletes a generated report
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.automatedreportingservice.DeleteReportResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "DeleteReport", action = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService/DeleteReport")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService")
    @RequestWrapper(localName = "DeleteReport", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.DeleteReport")
    @ResponseWrapper(localName = "DeleteReportResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService", className = "org.miloss.fgsms.services.interfaces.automatedreportingservice.DeleteReportResponse")
    public DeleteReportResponseMsg deleteReport(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService") DeleteReportRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "deleteReport", currentUser, "", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        if (Utility.stringIsNullOrEmpty(request.getReportId())) {
            throw new IllegalArgumentException("a report id must be specified");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "deleteReport", currentUser, request.getReportId(), (request.getClassification()), ctx.getMessageContext());
        boolean proceed = false;
        Connection con = null;
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            con = Utility.getPerformanceDBConnection();
            cmd = con.prepareStatement("select owninguser from arsjobs, arsreports where arsjobs.jobid=arsreports.jobid and arsreports.reportid=?;");
            cmd.setString(1, request.getReportId());
            rs = cmd.executeQuery();
            if (rs.next()) {
                String owner = rs.getString(1);
                if (!Utility.stringIsNullOrEmpty(owner)) {
                    if (currentUser.equalsIgnoreCase(owner)) {
                        proceed = true;
                    }
                }
            }

        } catch (Exception ex) {
            //record not found
            log.log(Level.ERROR, "error getting global policy", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }

        if (proceed) {
            try {
                con = Utility.getPerformanceDBConnection();
                cmd = con.prepareStatement("delete from arsreports where arsreports.reportid=?;");
                cmd.setString(1, request.getReportId());
                int k = cmd.executeUpdate();

                if (k != 1) {
                    ServiceUnavailableException f = new ServiceUnavailableException("Deletion failed", null);
                    f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                    throw f;
                }
                DeleteReportResponseMsg ret = new DeleteReportResponseMsg();
                ret.setSuccess(true);
                ret.setMessage(OK);
                ret.setClassification(getCurrentOperatingClassificationLevel());
                return ret;
            } catch (Exception ex) {

                log.log(Level.ERROR, "error removing reporting", ex);
                ServiceUnavailableException f = new ServiceUnavailableException("", null);
                f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
                throw f;
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(cmd);
                DBUtils.safeClose(con);

            }
        } else //access denied you don't own this job or it doesn't exist
        {
            log.log(Level.ERROR, "cannot remove report, either the job or report doesn't exist or the current user doesn't own it " + currentUser);
            AccessDeniedException f = new AccessDeniedException("", null);
            throw f;
        }
    }

    private Calendar ConvertToXmlGreg(long aLong) {
        if (aLong == 0) {
            return null;
        }
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(aLong);
        return (gcal);
    }

    private static boolean IsReportJobOwner(String currentUser, String jobId) {
        Connection con = null;
        boolean ok = false;
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            con = Utility.getPerformanceDBConnection();
            cmd = con.prepareStatement("select * from arsjobs where jobid=? and owninguser=?");
            cmd.setString(1, jobId);
            cmd.setString(2, currentUser);
            rs = cmd.executeQuery();
            if (rs.next()) {
                ok = true;
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "error caught searching for a report job", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        return ok;
    }

    private static void assertNotNull(Object exportType) {
        if (exportType == null) {
            throw new IllegalArgumentException("a required value is null");
        }
    }

    private static void assertFalse(boolean empty) {
        if (empty) {
            throw new IllegalArgumentException("The array is empty");
        }
    }

    private static void validateRange(TimeRangeDiff range) {
        if (range == null) {
            throw new IllegalArgumentException("The time range diff is empty");
        }
        if (range.getStart() == null) {
            throw new IllegalArgumentException("The time range diff start is empty");
        }
        if (range.getEnd() == null) {
            throw new IllegalArgumentException("The time range diff endis empty");
        }
        if (range.getStart().equals(range.getEnd()) || range.getStart().isShorterThan(range.getEnd())) {
            throw new IllegalArgumentException("The time range diff is invalid, start should be a longer duration that end");
        }
    }

    private static void validReportDefinition(ReportDefinition get) {
        if (get == null) {
            throw new IllegalArgumentException("The report def is empty");
        }
        if (get.getSchedule() == null) {
            throw new IllegalArgumentException("The schedule is empty");
        }
        if (get.getSchedule().getTriggers().isEmpty()) {
            throw new IllegalArgumentException("The report def has no triggers defined.");
        }

        for (int i = 0; i < get.getSchedule().getTriggers().size(); i++) {
            if (get.getSchedule().getTriggers().get(i).getStartingAt() == null) {
                throw new IllegalArgumentException("The report def has invalid triggers.");
            }

            if (get.getSchedule().getTriggers().get(i) instanceof DailySchedule) {
                DailySchedule ds = (DailySchedule) get.getSchedule().getTriggers().get(i);
                if (ds.getReoccurs() == null || ds.getReoccurs().intValue() < 1) {
                    throw new IllegalArgumentException("The report def has invalid value for reoccuring.");
                }
            } else if (get.getSchedule().getTriggers().get(i) instanceof WeeklySchedule) {
                WeeklySchedule ds = (WeeklySchedule) get.getSchedule().getTriggers().get(i);
                if (ds.getDayOfTheWeekIs().isEmpty()) {
                    throw new IllegalArgumentException("The report def has invalid weekly schedule for day of the weeks.");
                }
                if (ds.getReoccurs() == null || ds.getReoccurs().intValue() < 1) {
                    throw new IllegalArgumentException("The report def has invalid value for reoccuring.");
                }
            } else if (get.getSchedule().getTriggers().get(i) instanceof MonthlySchedule) {
                MonthlySchedule ds = (MonthlySchedule) get.getSchedule().getTriggers().get(i);
                if (ds.getDayOfTheMonthIs().isEmpty()) {
                    throw new IllegalArgumentException("The report def has invalid weekly schedule for day of the month.");
                }
                if (ds.getMonthNameIs().isEmpty()) {
                    throw new IllegalArgumentException("The report def has invalid weekly schedule for month.");
                }
            } else if (get.getSchedule().getTriggers().get(i) instanceof OneTimeSchedule) {
                //TODO
            } else if (get.getSchedule().getTriggers().get(i) instanceof ImmediateSchedule) {
                //TODO
            } else {
                throw new IllegalArgumentException("The report def has a trigger that is not intrepretable.");
            }
        }

        if (get.getExportCSVDataRequestMsg() != null) {
            Utility.validateClassification(get.getExportCSVDataRequestMsg().getClassification());
        }
        if (get.getExportDataRequestMsg() != null) {
            Utility.validateClassification(get.getExportDataRequestMsg().getClassification());
        }
    }

    private static void validateRequest(AddOrUpdateScheduledReportRequestMsg request, String currentUser, WebServiceContext ctx) throws AccessDeniedException {
        for (int i = 0; i < request.getJobs().size(); i++) {
            request.getJobs().get(i).setOwner(currentUser);
            if (Utility.stringIsNullOrEmpty(request.getJobs().get(i).getJobId())) {
                //its a new job
                //request.getJobs().get(i).setJobId(UUID.randomUUID().toString());
            } else {
                //existing job
                //prevent users from overriding existing reports that are owned by others
                if (!IsReportJobOwner(currentUser, request.getJobs().get(i).getJobId())) {
                    AccessDeniedException f = new AccessDeniedException("the report job " + request.getJobs().get(i).getJobId() + " is not owned by you", null);
                    throw f;
                }
            }
            if (request.getJobs().get(i).getExportCSVDataRequestMsg() == null && request.getJobs().get(i).getExportDataRequestMsg() == null) {
                throw new IllegalArgumentException("one of ExportData or ExportCSV must be specified");
            }
            if (request.getJobs().get(i).getExportCSVDataRequestMsg() != null && request.getJobs().get(i).getExportDataRequestMsg() != null) {
                throw new IllegalArgumentException("both ExportData and ExportCSV cannot be specified on the same report definition");
            }

            validReportDefinition(request.getJobs().get(i));
            validateReportAlerts(request.getJobs().get(i));
            if (request.getJobs().get(i).getExportCSVDataRequestMsg() != null) {
                if (request.getJobs().get(i).getExportCSVDataRequestMsg().getExportType() != ExportRecordsEnum.AUDIT_LOGS
                        && request.getJobs().get(i).getExportCSVDataRequestMsg().getURLs().isEmpty()) {
                    throw new IllegalArgumentException("ExportCSV requires at least one URL when not requesting audit logs");
                }
                for (int k = 0; k < request.getJobs().get(i).getExportCSVDataRequestMsg().getURLs().size(); k++) {
                    if (request.getJobs().get(i).getExportCSVDataRequestMsg().getExportType() == ExportRecordsEnum.TRANSACTIONS) {
                        UserIdentityUtil.assertAuditAccess(request.getJobs().get(i).getExportCSVDataRequestMsg().getURLs().get(k), currentUser, "addOrUpdateScheduledReport", request.getClassification(), ctx);
                    } else {
                        UserIdentityUtil.assertReadAccess(request.getJobs().get(i).getExportCSVDataRequestMsg().getURLs().get(k), currentUser, "addOrUpdateScheduledReport", request.getClassification(), ctx);
                    }
                }
                assertNotNull(request.getJobs().get(i).getExportCSVDataRequestMsg().getExportType());
                assertNotNull(request.getJobs().get(i).getExportCSVDataRequestMsg().getRange());
                assertNotNull(request.getJobs().get(i).getExportCSVDataRequestMsg().getRange().getEnd());
                assertNotNull(request.getJobs().get(i).getExportCSVDataRequestMsg().getRange().getStart());
                validateRange(request.getJobs().get(i).getExportCSVDataRequestMsg().getRange());
                if (request.getJobs().get(i).getExportCSVDataRequestMsg().getExportType() == ExportRecordsEnum.AUDIT_LOGS) {
                    UserIdentityUtil.assertGlobalAuditRole(currentUser, "ValidReportDefinition", request.getClassification(), ctx);
                }
            }
            if (request.getJobs().get(i).getExportDataRequestMsg() != null) {
                for (int k = 0; k < request.getJobs().get(i).getExportDataRequestMsg().getURLs().size(); k++) {
                    UserIdentityUtil.assertReadAccess(request.getJobs().get(i).getExportDataRequestMsg().getURLs().get(k), currentUser, "addOrUpdateScheduledReport", request.getClassification(), ctx);
                }

                assertNotNull(request.getJobs().get(i).getExportDataRequestMsg().getReportTypes());
                assertNotNull(request.getJobs().get(i).getExportDataRequestMsg().getReportTypes().getReportTypeContainer());
                assertFalse(request.getJobs().get(i).getExportDataRequestMsg().getReportTypes().getReportTypeContainer().isEmpty());
                //validate that
                for (int k = 0; k < request.getJobs().get(i).getExportDataRequestMsg().getReportTypes().getReportTypeContainer().size(); k++) {
                    ReportTypeContainer reportType = request.getJobs().get(i).getExportDataRequestMsg().getReportTypes().getReportTypeContainer().get(i);
                    try {
                        //validate that the plugin is registered.
                        validatePluginRegistered(reportType.getType());
                    } catch (Exception ex) {
                        log.warn(null, ex);
                        throw new IllegalArgumentException(ex.getMessage());
                    }
                    //validate that the plugin is infact loadable
                    try {
                        ReportGeneratorPlugin plugin = (ReportGeneratorPlugin) Class.forName(reportType.getType()).newInstance();
                    } catch (Throwable t) {
                        log.warn(null, t);
                        throw new IllegalArgumentException(reportType.getType() + " could not be initialized");
                    }
                }
                assertNotNull(request.getJobs().get(i).getExportDataRequestMsg().getRange());
                assertNotNull(request.getJobs().get(i).getExportDataRequestMsg().getRange().getEnd());
                assertNotNull(request.getJobs().get(i).getExportDataRequestMsg().getRange().getStart());
                validateRange(request.getJobs().get(i).getExportDataRequestMsg().getRange());

            }
        }
    }

    private static void validateReportAlerts(ReportDefinition get) {
        if (get.getNotifications().isEmpty()) {
            return;
        }
        //FIXME
        /*
        for (int i = 0; i < get.getNotifications().size(); i++) {
            if (get.getNotifications().get(i) instanceof SLAActionEmail) {
                throw new IllegalArgumentException("SLA Email actions cannot be used in this context");
            }
            if (get.getNotifications().get(i) instanceof SLAActionRestart) {
                throw new IllegalArgumentException("SLA Restart actions cannot be used in this context");
            }

        }*/
    }

    /**
     * Get the operating status of this service
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage
     */
    @WebMethod(operationName = "GetOperatingStatus", action = "urn:org:miloss:fgsms:services:interfaces:opStatusService/GetOperatingStatus")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common")
    @RequestWrapper(localName = "GetOperatingStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common", className = "org.miloss.fgsms.services.interfaces.common.GetOperatingStatus")
    @ResponseWrapper(localName = "GetOperatingStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common", className = "org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponse")
    public GetOperatingStatusResponseMessage getOperatingStatus(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common") GetOperatingStatusRequestMessage request) {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getOperatingStatus", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        GetOperatingStatusResponseMessage res = new GetOperatingStatusResponseMessage();

        res.setClassification(request.getClassification());
        res.setVersionInfo(new GetOperatingStatusResponseMessage.VersionInfo());
        res.getVersionInfo().setVersionData(org.miloss.fgsms.common.Constants.Version);
        res.getVersionInfo().setVersionSource(org.miloss.fgsms.common.Constants.class.getCanonicalName());
        res.setStartedAt(started);
        boolean ok = true;
        Connection con = Utility.getConfigurationDBConnection();
        Connection con2 = Utility.getPerformanceDBConnection();
        PreparedStatement prepareStatement = null;
        PreparedStatement prepareStatement2 = null;
        try {
            prepareStatement = con.prepareStatement("select 1=1;");
            prepareStatement.execute();
            prepareStatement.close();

            prepareStatement2 = con2.prepareStatement("select 1=1;");
            prepareStatement2.execute();
            prepareStatement2.close();
            res.setStatusMessage("OK");
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
            ok = false;
            res.setStatusMessage("One or more of the database connections is available");
        } finally {
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(prepareStatement2);

            DBUtils.safeClose(con2);
            DBUtils.safeClose(con);
        }
        res.setStatus(ok);
        return res;
    }
}
