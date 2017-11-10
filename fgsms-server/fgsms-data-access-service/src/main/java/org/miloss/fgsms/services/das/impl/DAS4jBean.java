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
package org.miloss.fgsms.services.das.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import javax.annotation.Resource;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.util.Calendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.*;
import org.miloss.fgsms.services.interfaces.dataaccessservice.*;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetProcessListByMachineRequestMsg;
import org.miloss.fgsms.sla.SLACommon;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.DBUtils;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * DataAccessService WSDL Interface implementation Provides access to all
 * recorded data of fgsms to authorized users
 *
 * @author AO
 */
@MTOM(enabled = false)
//@javax.ejb.Stateless
@WebService(serviceName = "dataAccessService", name = "DAS",
        targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService"
//, wsdlLocation = "WEB-INF/wsdl/DASv8.wsdl"
)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED, use = SOAPBinding.Use.LITERAL)
public class DAS4jBean implements DataAccessService, OpStatusService {

    protected static final Logger log = Logger.getLogger("fgsms.DataAccessService");
    @Resource
    private WebServiceContext ctx;
    protected static DatatypeFactory df = null;
    private static JAXBContext jc = null;
    private static ResourceBundle bundle = null;
    private static Calendar started = null;

    public DAS4jBean() throws DatatypeConfigurationException {
        SetupBundle();
        init();
    }

    private synchronized void SetupBundle() {
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("org.miloss.fgsms.db/das", Locale.getDefault());
            } catch (Exception ex) {
                log.log(Level.FATAL, "unable to load the resource bundle for " + "org.miloss.fgsms.db/das" + Locale.getDefault().toString(), ex);
            }
        }
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("org.miloss.fgsms.db/das");
            } catch (Exception ex) {
                log.log(Level.FATAL, "unable to load the resource bundle for " + "org.miloss.fgsms.db/das", ex);
            }
        }
        if (bundle == null) {
            log.log(Level.FATAL, "unable to load the resource bundle for " + "org.miloss.fgsms.db/das" + Locale.getDefault().toString());
        } else {
            log.log(Level.DEBUG, "resouce bundle loaded");
        }
    }

    /**
     * constructor used for unit testing, do not remove
     *
     * @param w
     */
    protected DAS4jBean(WebServiceContext w) throws DatatypeConfigurationException {
        SetupBundle();
        init();
        ctx = w;
    }

    private synchronized void init() throws DatatypeConfigurationException {
        if (df == null) {
            df = DatatypeFactory.newInstance();
        }

        try {
            jc = Utility.getSerializationContext();
        } catch (Exception ex) {
            log.log(Level.FATAL, bundle.getString("JAXBException"), ex);
        }
        if (started == null) {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            started = (gcal);
        }
    }

    /**
     *
     *
     * Get Average Performance Statistics For a Service
     *
     * Global admins see everything Regular users can only see what they have
     * access to
     *
     * @param getAlertsRequest
     * @return returns
     * java.util.List<org.miloss.fgsms.services.interfaces.dataaccessservice.GetAlertsResponseMsg>
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetAlerts", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetAlerts")
    @WebResult(name = "GetAlertsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetAlerts", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAlerts")
    @ResponseWrapper(localName = "GetAlertsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAlertsResponse")
    public List<GetAlertsResponseMsg> getAlerts(
            @WebParam(name = "GetAlertsRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetAlertsRequestMsg getAlertsRequest)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (getAlertsRequest == null) {
            throw new IllegalArgumentException(bundle.getString("NullRequestMessage"));
        }

        Utility.validateClassification(getAlertsRequest.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "GetAlerts", currentUser, "", (getAlertsRequest.getClassification()), ctx.getMessageContext());
        if (getAlertsRequest.getRecordcount() <= 0 || getAlertsRequest.getRecordcount() > 1000) {
            throw new IllegalArgumentException(bundle.getString("InvalidRecordCount"));
        }

        long offset = 0;
        if (getAlertsRequest.getOffset() > 0) {
            offset = getAlertsRequest.getOffset();
        }

        SecurityWrapper level = (getCurrentClassificationLevel());
        List<GetAlertsResponseMsg> ret = new ArrayList<GetAlertsResponseMsg>();
        GetAlertsResponseMsg t = new GetAlertsResponseMsg();
        Connection config = Utility.getConfigurationDBConnection();;
        Connection con = Utility.getPerformanceDBConnection();
        GregorianCalendar cal = null;

        long timelimit = System.currentTimeMillis();
        GregorianCalendar now = new GregorianCalendar();
        now.setTimeInMillis(timelimit);
        now.add(GregorianCalendar.HOUR, -1);
        timelimit = now.getTimeInMillis();

        try {
            //get list of all monitored services that i have access to
            //put last x records for sla faults for all services
            //pull last x message faults
            //pull last status of monitored services, those that are not 'ok' add to list

            List<AlertHelper> list = new LinkedList<AlertHelper>();
            ArrayOfServiceType GetServiceListfromPolicyDB = DASHelper.getServiceListfromPolicyDB(ctx, (getAlertsRequest.getClassification()));
            if (GetServiceListfromPolicyDB != null && !GetServiceListfromPolicyDB.getServiceType().isEmpty()) {

                for (int i = 0; i < GetServiceListfromPolicyDB.getServiceType().size(); i++) {
                    PreparedStatement com = null;
                    ResultSet rs = null;
                    try {
                        com = con.prepareStatement(
                                //bundle.getString("GetSLAVioliations"));
                                "SELECT utcdatetime, msg, uri, relatedtransaction, incidentid FROM slaviolations "
                                + "where (uri = ?) and (utcdatetime > ?) order by utcdatetime desc limit ? offset ?;");
                        com.setLong(4, offset);
                        com.setLong(3, getAlertsRequest.getRecordcount());
                        com.setLong(2, timelimit);
                        com.setString(1, GetServiceListfromPolicyDB.getServiceType().get(i).getURL());

                        rs = com.executeQuery();
                        while (rs.next()) {
                            AlertHelper hj = new AlertHelper();
                            byte[] b = rs.getBytes("msg");
                            if (b != null && b.length > 0) {
                                hj.message = new String(b, Constants.CHARSET);
                            } else {
                                hj.message = "";
                            }
                            hj.timestamp = rs.getLong("utcdatetime");
                            hj.url = GetServiceListfromPolicyDB.getServiceType().get(i).getURL();
                            hj.type = AlertType.SLA_FAULT;
                            hj.tid = rs.getString("relatedtransaction");
                            list.add(hj);
                        }
                    } catch (Exception ex) {
                        log.log(Level.WARN, "", ex);
                    } finally {
                        DBUtils.safeClose(rs);
                        DBUtils.safeClose(com);
                    }

                    try {
                        com = con.prepareStatement("SELECT transactionid, utcdatetime FROM rawdata where uri=? and success=false and "
                                + "utcdatetime > ? "
                                + "order by utcdatetime desc limit ?;");
                        com.setLong(3, getAlertsRequest.getRecordcount());
                        com.setLong(2, timelimit);
                        com.setString(1, GetServiceListfromPolicyDB.getServiceType().get(i).getURL());

                        rs = com.executeQuery();
                        while (rs.next()) {
                            AlertHelper hj = new AlertHelper();
                            hj.message = "";
                            hj.timestamp = rs.getLong("utcdatetime");
                            hj.tid = rs.getString("transactionid");
                            hj.url = GetServiceListfromPolicyDB.getServiceType().get(i).getURL();
                            hj.type = AlertType.MESSAGE_FAULT;
                            list.add(hj);
                        }
                    } catch (Exception ex) {
                        log.log(Level.WARN, "", ex);
                    } finally {
                        DBUtils.safeClose(rs);
                        DBUtils.safeClose(com);
                    }

//      I'm not sure why there was an extra filter here
                    //removed 1-21-2012com = config.prepareStatement("select * from status where uri=? and status <> true and monitored = true;");
                    try {
                        com = config.prepareStatement("select * from status where uri=? and status =false;");
                        com.setString(1, GetServiceListfromPolicyDB.getServiceType().get(i).getURL());
                        rs = com.executeQuery();
                        if (rs.next()) {
                            boolean up = rs.getBoolean("status");
                            if (!up) {
                                AlertHelper hj = new AlertHelper();
                                hj.message = rs.getString("message");
                                if (hj.message == null) {
                                    hj.message = "";
                                }
                                hj.timestamp = rs.getLong("utcdatetime");
                                hj.url = rs.getString("uri");
                                hj.type = AlertType.OPERATING_STATUS;

                                list.add(hj);
                            }
                        }
                    } catch (Exception ex) {
                        log.log(Level.WARN, "", ex);
                    } finally {
                        DBUtils.safeClose(rs);
                        DBUtils.safeClose(com);
                    }

                }

            }
            Collections.sort(list, new AlertTypeComparator());
            Iterator e = list.iterator();

            int count = 0;
            while (e.hasNext() && count < getAlertsRequest.getRecordcount()) {
                AlertHelper h = (AlertHelper) e.next();
                t = new GetAlertsResponseMsg();
                t.setClassification(level);
                t.setType(h.type);
                t.setMessage(h.message);
                t.setUrl(h.url);
                t.setTransactionid(h.tid);
                cal = new GregorianCalendar();
                cal.setTimeInMillis(h.timestamp);
                t.setTimestamp((cal));
                ret.add(t);
                count++;
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "das1 error caught", ex);
            t.setType(AlertType.OPERATING_STATUS);
            t.setMessage("Performance Database is not available.");
            cal = new GregorianCalendar();
            t.setTimestamp((cal));
            ret.add(t);
        } finally {

            DBUtils.safeClose(con);
            DBUtils.safeClose(config);

        }

        return ret;
    }

    /**
     *
     *
     * Returns all available message logs sorted by timestamp descending, i.e.
     * the most recent first. options are offset (for paging) and record count *
     *
     *
     * This is the preferred method for accessing message transaction logs as it
     * is faster that the other method Global Admins can read all Users can only
     * see per ACLs <param name="request">Specify SLA Fault OR Faults Only, both
     * is not supported</param>
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetRecentMessageLogs", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetRecentMessageLogs")
    @WebResult(name = "GetMessageLogsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetRecentMessageLogs", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetRecentMessageLogs")
    @ResponseWrapper(localName = "GetRecentMessageLogsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetRecentMessageLogsResponse")
    public GetMessageLogsResponseMsg getRecentMessageLogs(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetRecentMessageLogsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        return QueryGetRecentMessageLogs.getRecentMessageLogs(request, ctx);

    }

    /**
     *
     *
     * Get Average Performance Statistics For All Transactional Services - this
     * will return performance stats for all services that the requestor has
     * access to
     *
     *
     *
     *
     *
     *
     *
     * @param range
     * @param classification
     * @return returns
     * java.util.List<org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsResponseMsg>
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetPerformanceAverageStatsAll", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetPerformanceAverageStatsAll")
    @WebResult(name = "AllStats", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetPerformanceAverageStatsAll", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsAll")
    @ResponseWrapper(localName = "GetPerformanceAverageStatsAllResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsAllResponse")
    public List<GetPerformanceAverageStatsResponseMsg> getPerformanceAverageStatsAll(
            @WebParam(name = "classification", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") SecurityWrapper classification,
            @WebParam(name = "range", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") TimeRange range)
            throws AccessDeniedException, ServiceUnavailableException {
        if (range == null || range.getEnd() == null || range.getStart() == null) {
            throw new IllegalArgumentException("Missing input parameters");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(classification);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getPerformanceAverageStatsAll", currentUser, "", (classification), ctx.getMessageContext());
        ArrayList<GetPerformanceAverageStatsResponseMsg> ret = new ArrayList<GetPerformanceAverageStatsResponseMsg>();
        ArrayOfServiceType GetServiceListfromPolicyDB = DASHelper.getServiceListfromPolicyDB(ctx, (classification), PolicyType.TRANSACTIONAL);
        if (GetServiceListfromPolicyDB == null
                || GetServiceListfromPolicyDB.getServiceType().isEmpty()) {
            return ret;
        }

        for (int i = 0; i < GetServiceListfromPolicyDB.getServiceType().size(); i++) {
            //unneccessary UserIdentityUtil.assertReadAccess(GetServiceListfromPolicyDB.getValue().getServiceType().get(i).getURL(), currentUser, "GetPerformanceAvgStatsFromDB");
            GetPerformanceAverageStatsResponseMsg item = DASHelper.getPerformanceAvgStatsFromDB(ctx,
                    GetServiceListfromPolicyDB.getServiceType().get(i).getURL(), GetServiceListfromPolicyDB.getServiceType().get(i).getDisplayName(),
                    //  GetServiceListfromPolicyDB.getValue().getServiceType().get(i).getDisplayName(),
                    classification);
            if (item != null) {
                ret.add(item);
            }
        }

        return ret;
    }

    /**
     *
     *
     * Returns a specific message transaction log for transactional web services
     * *
     *
     *
     *
     *
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogDetailsResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetMessageTransactionLogDetails", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMessageTransactionLogDetails")
    @WebResult(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMessageTransactionLogDetails", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogDetails")
    @ResponseWrapper(localName = "GetMessageTransactionLogDetailsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogDetailsResponse")
    public GetMessageTransactionLogDetailsResponseMsg getMessageTransactionLogDetails(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMessageTransactionLogDetailsMsg req)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        //log.log(Level.INFO, name + "getMessageTransactionLogDetails" + currentUser);
        if (req == null) {
            throw new IllegalArgumentException("Request is empty");
        }
        if (Utility.stringIsNullOrEmpty(req.getTransactionID())) {
            throw new IllegalArgumentException("Transactio ID  is empty");
        }
        Utility.validateClassification(req.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getMessageTransactionLogDetails", currentUser, "", (req.getClassification()), ctx.getMessageContext());
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement comm = null;
        ResultSet set = null;
        try {

            GetMessageTransactionLogDetailsResponseMsg ret = new GetMessageTransactionLogDetailsResponseMsg();

            comm = con.prepareStatement("select * from RawData " + "where transactionid=?");
            comm.setString(1, req.getTransactionID());
            set = comm.executeQuery();

            if (set.next()) {
                String url = set.getString("uri");
                UserIdentityUtil.assertAuditAccess(url, currentUser, "getMessageTransactionLogDetails", (req.getClassification()), ctx);
                ret.setCorrectedURL(url);
                ret.setOriginalRequestURL(set.getString("originalurl"));
                if (Utility.stringIsNullOrEmpty(ret.getOriginalRequestURL())) {
                    ret.setOriginalRequestURL(ret.getCorrectedURL());
                }
                ret.setAction(set.getString("soapaction"));
                ret.setAgentType(set.getString("agenttype"));
                ret.setIsFault(!set.getBoolean("success"));
                ret.setResponseTime(set.getLong("responsetimems"));
                String sla = set.getString("slafault");
                ret.setIsSLAFault(!Utility.stringIsNullOrEmpty(sla));
                if (ret.isIsSLAFault()) {
                    ret.setSlaFaultMsg(DASHelper.getSLAFaultMsg(con, sla));
                }
                ret.setMonitorHostname(set.getString("monitorsource"));
                ret.setServiceHostname(set.getString("hostingsource"));
                ret.setTransactionId(set.getString("transactionid"));
                ret.setRequestSize(set.getLong("requestsize"));
                ret.setResponseSize(set.getLong("responsesize"));

                byte[] header = null;
                try {
                    header = set.getBytes("requestHeaders");
                } catch (Exception ex) {
                    log.log(Level.INFO, "", ex);
                }
                if (header != null) {

                    String s = null;
                    try {
                        s = new String(header, Constants.CHARSET);
                        s = Utility.DE(s);
                        String[] headers = s.split("\\|");
                        for (int a = 0; a < headers.length; a++) {
                            String[] s1 = headers[a].split("=");
                            Header h = new Header();
                            h.setName(s1[0]);
                            if (s1.length == 2) {
                                String[] vs = s1[1].split(";");
                                if (vs != null && vs.length > 0) {
                                    for (int b = 0; b < vs.length; b++) {
                                        h.getValue().add(vs[b]);
                                    }
                                }
                            }
                            ret.getHeadersRequest().add(h);
                        }

                    } catch (Exception x) {
                    }
                }
                header = null;
                try {
                    header = set.getBytes("responseHeaders");
                } catch (Exception ex) {
                    log.log(Level.INFO, "", ex);
                }

                if (header != null) {
                    String s = "";
                    try {
                        s = new String(header, Constants.CHARSET);
                        s = Utility.DE(s);
                        String[] headers = s.split("\\|");
                        for (int a = 0; a < headers.length; a++) {
                            String[] s1 = headers[a].split("=");
                            Header h = new Header();
                            h.setName(s1[0]);
                            if (s1.length == 2) {
                                String[] vs = s1[1].split(";");
                                if (vs != null && vs.length > 0) {
                                    for (int b = 0; b < vs.length; b++) {
                                        h.getValue().add(vs[b]);
                                    }
                                }
                            }
                            ret.getHeadersResponse().add(h);
                        }
                    } catch (UnsupportedEncodingException ex) {
                        log.log(Level.ERROR, null, ex);
                    }
                }
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(set.getLong("utcdatetime"));

                ret.setTimestamp((gcal));
                //ret.setSlaFaultMsg(set.getString("slafault"));
                byte[] x = (byte[]) set.getBytes("RequestXML");
                if (x != null) {
                    ret.setXmlRequestMessage(Utility.DE(new String(x, Constants.CHARSET)));
                }
                x = (byte[]) set.getBytes("ResponseXML");
                if (x != null) {
                    ret.setXmlResponseMessage(Utility.DE(new String(x, Constants.CHARSET)));
                }
                String temp = set.getString("consumeridentity");
                if (!Utility.stringIsNullOrEmpty(temp)) {
                    String[] ids = temp.split(";");
                    for (int i = 0; i < ids.length; i++) {
                        ret.getIdentity().add(ids[i]);
                    }
                }

                x = (byte[]) set.getBytes("message");
                if (x != null) {
                    try {
                        ret.setAgentMemo(new String(x, "UTF-8"));
                        //ret.setXmlResponseMessage(new String(x));
                    } catch (UnsupportedEncodingException ex) {
                        log.log(Level.ERROR, null, ex);
                    }
                }

                ret.setRelatedTransactionID(set.getString("relatedTransactionID"));
                ret.setTransactionthreadId(set.getString("threadid"));
                ret.setClassification(getCurrentClassificationLevel());

                return ret;
            } else {

                throw new IllegalArgumentException("transaction not found");
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(set);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException e = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
        e.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw e;

    }

    /**
     *
     *
     * Get Average Performance Statistics For a Specific Transactional Service
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetPerformanceAverageStats", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetPerformanceAverageStats")
    @WebResult(name = "GetPerformanceAverageStatsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetPerformanceAverageStats", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStats")
    @ResponseWrapper(localName = "GetPerformanceAverageStatsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsResponse")
    public GetPerformanceAverageStatsResponseMsg getPerformanceAverageStats(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetPerformanceAverageStatsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        if (request == null) {
            throw new IllegalArgumentException("Missing input parameters");
        }

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getPerformanceAverageStats", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        if (!Utility.stringIsNullOrEmpty(request.getURL())) {
            UserIdentityUtil.assertReadAccess(request.getURL(), currentUser, "GetPerformanceAvgStatsFromDB", (request.getClassification()), ctx);
            GetPerformanceAverageStatsResponseMsg item = DASHelper.getPerformanceAvgStatsFromDB(ctx, request.getURL(), getPolicyDisplayName(request.getURL()), (request.getClassification()));
            if (item != null) {
                return item;
            } else {
                throw new IllegalArgumentException("No data is current available for the specified service");
            }
        } else {
            throw new IllegalArgumentException("Request URL is empty, at least one must be specified");
        }
    }

    /**
     *
     *
     * Get Average Performance Statistics a Hosting Server
     *
     * I.E. a machine hosting a web service that is monitored by fgsms
     *
     * Requires Global Admin permissions
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetPerformanceAverageHostingStats", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetPerformanceAverageHostingStats")
    @WebResult(name = "GetPerformanceAverageHostingStatsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetPerformanceAverageHostingStats", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageHostingStats")
    @ResponseWrapper(localName = "GetPerformanceAverageHostingStatsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageHostingStatsResponse")
    public GetPerformanceAverageStatsResponseMsg getPerformanceAverageHostingStats(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetPerformanceAverageHostStatsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        if (request == null) {
            throw new IllegalArgumentException("Request is empty");
        }
        if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("Time range is empty");
        }
        if (Utility.stringIsNullOrEmpty(request.getHostname())) {
            throw new IllegalArgumentException("Hostname is emtpy");
        }
        //FIXME add additional time range checks

        if (request.getRange().getStart().after(request.getRange().getEnd())) {
            throw new IllegalArgumentException("Start can't be after the end time");
        }
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 2);
        if (now.before(request.getRange().getEnd())) {
            throw new IllegalArgumentException("End time " + request.getRange().getEnd().getTime().toString() + " can't be more than one 2 days in the future from now " + new java.util.Date());
        }

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getPerformanceAverageHostingStats", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getPerformanceAverageHostingStats", (request.getClassification()), ctx);
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement comm = null;
        ResultSet avg = null;
        GetPerformanceAverageStatsResponseMsg res = new GetPerformanceAverageStatsResponseMsg();
        res.setAverageResponseTime(0);
        res.setFailingInvocations(0);
        res.setSuccessfulInvocations(0);
        boolean norecords = true;
        boolean toomanyrecords = false;
        long totalinvocations = 0;
        try {

            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            comm = con.prepareStatement("select avg(responsetimems),count(*)from rawdata where UTCdatetime >= ? and UTCdatetime <= ? and hostingsource=?;");
            //average response time for a host, total transactions
            //average response time for a host, total transactions

            comm.setLong(1, request.getRange().getStart().getTimeInMillis());
            comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
            comm.setString(3, request.getHostname());

            avg = comm.executeQuery();

            //if (!avg.isClosed()) {
            while (avg.next()) {
                if (!norecords) {
                    toomanyrecords = true;
                }
                norecords = false;
                res.setAverageResponseTime((long) avg.getFloat(1));
                totalinvocations = avg.getLong(2);
            }
            //}
            if (norecords) {
                //no data available
                res.setAverageResponseTime(0);
                res.setFailingInvocations(0);
                res.setSuccessfulInvocations(0);
                comm.close();
                con.close();
                return res;
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "sql error from getPerformanceAverageHostingStats", ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        } finally {
            DBUtils.safeClose(avg);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        try {

            if (toomanyrecords) {
                ServiceUnavailableException code = new ServiceUnavailableException("", null);
                code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                log.log(Level.ERROR, "too many records returned for getPerformanceAverageHostingStats returned. this is unexpected");
                throw code;
            }
            norecords = true;
            toomanyrecords = false;
            con = Utility.getPerformanceDBConnection();
            comm = con.prepareStatement("select count(*) from rawdata where UTCdatetime >= ? and UTCdatetime <= ? and hostingsource=? and success=false;");
            comm.setLong(1, request.getRange().getStart().getTimeInMillis());
            comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
            comm.setString(3, request.getHostname());
            avg = comm.executeQuery();
            //if (!failures.isClosed()) {
            while (avg.next()) {
                if (!norecords) {
                    toomanyrecords = true;
                }
                res.setFailingInvocations(avg.getLong(1));
                res.setSuccessfulInvocations(totalinvocations - avg.getLong(1));
                norecords = false;
            }
            // }
            if (toomanyrecords) {
                ServiceUnavailableException code = new ServiceUnavailableException("", null);
                code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                log.log(Level.ERROR, "too many records returned for getPerformanceAverageHostingStats returned. this is unexpected");
                throw code;
            }

            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, "sql error from getPerformanceAverageHostingStats", ex);
        } finally {
            DBUtils.safeClose(avg);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;
    }

    /**
     *
     *
     * Returns all available message logs for a given period of time, audit
     * access required
     *
     *
     *
     * <param name="request"> GetMessageLogsRequest Optional parameters: URL,
     * shows only transactions from a matching modified url monitorhostname
     * servicehostname agenttype
     *
     * Required: Time Range Records Offset faultsOnly SLAfaultsOnly</param>
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg
     * must specify SLA Faults OR Faults, or none, but not both
     * @throws
     * DataAccessServiceGetMessageLogsServiceUnavailableFaultCodesFaultFaultMessage
     * @throws
     * DataAccessServiceGetMessageLogsAccessDeniedExceptionFaultFaultMessage
     */
    @WebMethod(operationName = "GetMessageLogsByRange", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMessageLogsByRange")
    @WebResult(name = "GetMessageLogsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMessageLogsByRange", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsByRange")
    @ResponseWrapper(localName = "GetMessageLogsByRangeResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsByRangeResponse")
    public GetMessageLogsResponseMsg getMessageLogsByRange(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMessageLogsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        return QueryGetMessageLogsByRange.getMessageLogsByRange(request, ctx);
    }

    /**
     *
     *
     * Return a specific message log (payloads) for a given web service
     * transaction
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogResponseMsg
     * @throws
     * DataAccessServiceGetMessageTransactionLogServiceUnavailableExceptionFaultFaultMessage
     * @throws
     * DataAccessServiceGetMessageTransactionLogAccessDeniedExceptionFaultFaultMessage
     */
    @WebMethod(operationName = "GetMessageTransactionLog", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMessageTransactionLog")
    @WebResult(name = "GetMessageTransactionLogResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMessageTransactionLog", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLog")
    @ResponseWrapper(localName = "GetMessageTransactionLogResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageTransactionLogResponse")
    public GetMessageTransactionLogResponseMsg getMessageTransactionLog(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMessageTransactionLogRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (request.getTransactionId() == null) {
            throw new IllegalArgumentException("transaction id must be specified");
        }
        if (request.getURL() == null) {
            throw new IllegalArgumentException("a valid URI must be specified");
        }

        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            Utility.validateClassification(request.getClassification());
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getMessageTransactionLog", currentUser, "", (request.getClassification()), ctx.getMessageContext());
            //log.log(Level.INFO, name + " GetMessageTransactionLog " + currentUser);
            UserIdentityUtil.assertAuditAccess(request.getURL(), currentUser, "getMessageTransactionLog", (request.getClassification()), ctx);
            GetMessageTransactionLogResponseMsg res = new GetMessageTransactionLogResponseMsg();

            comm = con.prepareStatement("Select * from RawData where transactionId=? and URI=?;");
            comm.setString(1, request.getTransactionId());
            comm.setString(2, request.getURL());
            results = comm.executeQuery();
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            boolean norecords = true;
            boolean toomanyrecords = false;

            while (results.next()) {
                if (!norecords) {
                    toomanyrecords = true;
                }
                norecords = false;

                res = new GetMessageTransactionLogResponseMsg();

                byte[] x = (byte[]) results.getBytes("RequestXML");

                if (x != null) {
                    res.setRequestMessage(new String(x, Constants.CHARSET));
                }
                x = (byte[]) results.getBytes("ResponseXML");

                if (x != null) {
                    res.setResponseMessage(new String(x, Constants.CHARSET));
                }
                res.setResponseTime(results.getLong("ResponseTimeMS"));
                res.setIsFault(!results.getBoolean("Success"));
            }
            if (norecords) {

                return null;
            }
            if (toomanyrecords) {
                log.log(Level.ERROR, " GetMessageTransactionLog Database returned multiple rows when queriying for the primary transactionId key. This is unexpected");
                ServiceUnavailableException code = new ServiceUnavailableException("", null);
                code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                throw code;
            }

            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;

    }

    /**
     *
     *
     * Returns all available operational status change logs for a given period
     * of time and service
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetOperationalStatusLogResponseMsg
     * @throws
     * DataAccessServiceGetOperationalStatusLogAccessDeniedExceptionFaultFaultMessage
     * @throws
     * DataAccessServiceGetOperationalStatusLogServiceUnavailableExceptionFaultFaultMessage
     */
    @WebMethod(operationName = "GetOperationalStatusLog", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetOperationalStatusLog")
    @WebResult(name = "GetOperationalStatusLogResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetOperationalStatusLog", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetOperationalStatusLog")
    @ResponseWrapper(localName = "GetOperationalStatusLogResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetOperationalStatusLogResponse")
    public GetOperationalStatusLogResponseMsg getOperationalStatusLog(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetOperationalStatusLogRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getOperationalStatusLog", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("range is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getURL())) {
            throw new IllegalArgumentException("url");
        }

        UserIdentityUtil.assertReadAccess(request.getURL(), currentUser, "getOperationalStatusLog", (request.getClassification()), ctx);
        GetOperationalStatusLogResponseMsg ret = new GetOperationalStatusLogResponseMsg();
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet set = null;
        try {
            com = con.prepareStatement("SELECT uri, utcdatetime, status, id, message "
                    + "FROM availability where utcdatetime > ? and utcdatetime < ? and uri=? order by utcdatetime asc;");
            com.setString(3, request.getURL());
            com.setLong(1, request.getRange().getStart().getTimeInMillis());
            com.setLong(2, request.getRange().getEnd().getTimeInMillis());
            set = com.executeQuery();
            while (set.next()) {
                OperationalRecord op = new OperationalRecord();
                op.setID(set.getString("id"));
                byte[] b = set.getBytes("message");
                op.setMessage(new String(b, Constants.CHARSET));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(set.getLong("utcdatetime"));
                op.setTimestamp((cal));
                op.setOperational(set.getBoolean("status"));
                ret.getOperationalRecord().add(op);
            }

            ret.setURL(request.getURL());
            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw code;
        } finally {
            DBUtils.safeClose(set);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

    }

    /**
     *
     *
     * GetMonitoredServiceList
     *
     * Returns 0 or more monitored services.
     *
     * This list of services is generated from the defined policies
     *
     * The list of soap actions is generated from the actual message
     * transactions
     *
     * meaning that if a particular action has never been executed, fgsms
     * doesn't know about it.
     *
     * Returns a listing of services along with there associated soap actions
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListResponseMsg
     * @throws
     * DataAccessServiceGetMonitoredServiceListAccessDeniedExceptionFaultFaultMessage
     * @throws
     * DataAccessServiceGetMonitoredServiceListServiceUnavailableExceptionFaultFaultMessage
     */
    @WebMethod(operationName = "GetMonitoredServiceList", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMonitoredServiceList")
    @WebResult(name = "GetMonitoredServiceListResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMonitoredServiceList", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceList")
    @ResponseWrapper(localName = "GetMonitoredServiceListResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListResponse")
    public GetMonitoredServiceListResponseMsg getMonitoredServiceList(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMonitoredServiceListRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        //access control is processed in GetServiceListfromPolicyDB
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "GetMonitoredServiceList", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        GetMonitoredServiceListResponseMsg ret = new GetMonitoredServiceListResponseMsg();
        if (request.getURL() == null || Utility.stringIsNullOrEmpty(request.getURL())) {
            ret.setServiceList(DASHelper.getServiceListfromPolicyDB(ctx, (request.getClassification())));
        } else {
            ret.setServiceList(DASHelper.getServiceListfromPolicyDB(ctx, request.getURL(), (request.getClassification())));
        }
        ret.setClassification(getCurrentClassificationLevel());
        return ret;
    }

    /**
     *
     *
     * Returns all available SLA fault records, transactional and
     * nontransactional logs for a given period of time and service
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetSLAFaultRecordsResponseMsg
     * @throws
     * DataAccessServiceGetSLAFaultRecordsAccessDeniedExceptionFaultFaultMessage
     * @throws
     * DataAccessServiceGetSLAFaultRecordsServiceUnavailableExceptionFaultFaultMessage
     */
    @WebMethod(operationName = "GetSLAFaultRecords", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetSLAFaultRecords")
    @WebResult(name = "GetSLAFaultRecordsResponseMsgResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetSLAFaultRecords", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetSLAFaultRecords")
    @ResponseWrapper(localName = "GetSLAFaultRecordsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetSLAFaultRecordsResponse")
    public List<GetSLAFaultRecordsResponseMsg> getSLAFaultRecords(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetSLAFaultRecordsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getSLAFaultRecords", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("range is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getURL())) {
            throw new IllegalArgumentException("url is null");
        }
        long maxrecords = 200;
        if (request.getRecordcount() > 0 && request.getRecordcount() < 1000) {
            maxrecords = request.getRecordcount();
        }
        long offset = 0;
        if (request.getOffset() > 0) {
            offset = request.getOffset();
        }

//TODO add record acounts and limits via wsdl
        UserIdentityUtil.assertReadAccess(request.getURL(), currentUser, "getSLAFaultRecords", (request.getClassification()), ctx);
        List<GetSLAFaultRecordsResponseMsg> ret = new ArrayList<GetSLAFaultRecordsResponseMsg>();
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("SELECT utcdatetime, msg, uri, relatedtransaction, incidentid FROM slaviolations "
                    + "where (utcdatetime > ?) and (utcdatetime < ?) and (uri = ?) order by utcdatetime DESC limit ? offset ?;");
            com.setLong(1, request.getRange().getStart().getTimeInMillis());
            com.setLong(2, request.getRange().getEnd().getTimeInMillis());
            com.setString(3, request.getURL());
            com.setLong(4, maxrecords);
            com.setLong(5, offset);
            rs = com.executeQuery();
            while (rs.next()) {
                GetSLAFaultRecordsResponseMsg t = new GetSLAFaultRecordsResponseMsg();
                t.setIncidentID(rs.getString("incidentid"));
                byte[] b = rs.getBytes("msg");
                t.setMessage(new String(b, Constants.CHARSET));
                t.setRelatedTransaction(rs.getString("relatedtransaction"));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getLong("utcdatetime"));
                t.setTimestamp((cal));
                ret.add(t);

            }

            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw code;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

    }

    /**
     *
     *
     * Returns all available operational status change logs for a given period
     * of time for all services
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * java.util.List<org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllOperationalStatusLogResponseMsg>
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetAllOperationalStatusLog", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetAllOperationalStatusLog")
    @WebResult(name = "GetAllOperationalStatusLogResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetAllOperationalStatusLog", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllOperationalStatusLog")
    @ResponseWrapper(localName = "GetAllOperationalStatusLogResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllOperationalStatusLogResponse")
    public List<GetAllOperationalStatusLogResponseMsg> getAllOperationalStatusLog(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetAllOperationalStatusLogRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAllOperationalStatusLog", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("range is null");
        }
        if (request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("range is null");
        }
        List<GetAllOperationalStatusLogResponseMsg> ret = new ArrayList<GetAllOperationalStatusLogResponseMsg>();
        SecurityWrapper currentClassificationLevel = getCurrentClassificationLevel();
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet set = null;
        try {
            ArrayOfServiceType list = DASHelper.getServiceListfromPolicyDB(ctx, request.getClassification());
            if (!(list == null || list.getServiceType() == null)) {
                for (int i = 0; i < list.getServiceType().size(); i++) {
                    try {
                        com = con.prepareStatement("SELECT uri, utcdatetime, status, id, message " + "FROM availability where utcdatetime > ? and " + "utcdatetime < ? and uri=?");
                        com.setString(3, list.getServiceType().get(i).getURL());
                        com.setLong(1, request.getRange().getStart().getTimeInMillis());
                        com.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        set = com.executeQuery();
                        GetAllOperationalStatusLogResponseMsg t = new GetAllOperationalStatusLogResponseMsg();
                        t.setURL(list.getServiceType().get(i).getURL());
                        while (set.next()) {
                            OperationalRecord op = new OperationalRecord();
                            op.setID(set.getString("id"));
                            byte[] b = set.getBytes("message");
                            op.setMessage(new String(b, Constants.CHARSET));
                            GregorianCalendar cal = new GregorianCalendar();
                            cal.setTimeInMillis(set.getLong("utcdatetime"));
                            op.setTimestamp((cal));
                            op.setOperational(set.getBoolean("status"));
                            t.getOperationalRecord().add(op);
                        }

                        t.setClassification(currentClassificationLevel);
                        ret.add(t);
                    } catch (SQLException ex) {
                        log.log(Level.ERROR, "getAllOperationalStatusLog error caught sql error", ex);
                        ServiceUnavailableException code = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
                        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
                        throw code;
                    } finally {
                        DBUtils.safeClose(set);
                        DBUtils.safeClose(com);

                    }

                }
            }
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "getAllOperationalStatusLog", ex);
        } finally {
            DBUtils.safeClose(con);
        }

        ServiceUnavailableException code = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;
    }

    /**
     *
     *
     * GetServiceHostList
     *
     * Returns 0 or more host names of machines hosting monitored web services.
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetServiceHostListResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetServiceHostList", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetServiceHostList")
    @WebResult(name = "GetServiceHostListResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetServiceHostList", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetServiceHostList")
    @ResponseWrapper(localName = "GetServiceHostListResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetServiceHostListResponse")
    public GetServiceHostListResponseMsg getServiceHostList(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetServiceHostListRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getServiceHostList", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getServiceHostList", (request.getClassification()), ctx);

        //here we don't care about the request since we don't rely on any input
        GetServiceHostListResponseMsg res = new GetServiceHostListResponseMsg();
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {

            comm = con.prepareStatement("Select * from servicehosts;");
            results = comm.executeQuery();
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////

            ArrayOfHostInstanceStats list = new ArrayOfHostInstanceStats();
            while (results.next()) {

                HostInstanceStats temp = new HostInstanceStats();
                temp.setHost(results.getString("hostname"));
                temp.setRecordedTransactionCount(Long.valueOf(results.getLong("records")).intValue());
                list.getHostInstanceStats().add(temp);
            }
            results.close();

            res.setHosts(list);

            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw code;
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    }

    /**
     *
     *
     * GetDataCollectorList
     *
     * Returns 0 or more host names of machines hosting data collector services
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetDataCollectorListResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetDataCollectorList", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetDataCollectorList")
    @WebResult(name = "GetDataCollectorListResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetDataCollectorList", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetDataCollectorList")
    @ResponseWrapper(localName = "GetDataCollectorListResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetDataCollectorListResponse")
    public GetDataCollectorListResponseMsg getDataCollectorList(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetDataCollectorListRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getDataCollectorList", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getDataCollectorList", (request.getClassification()), ctx);

        //here, we don't rely on any input parameters, so it doesn't matter if it's null
        GetDataCollectorListResponseMsg res = new GetDataCollectorListResponseMsg();
        res.setClassification(getCurrentClassificationLevel());
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            comm = con.prepareStatement("Select * from dcsservicehosts;");
            results = comm.executeQuery();
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            //  boolean norecords = true;
            ArrayOfHostInstanceStats list = new ArrayOfHostInstanceStats();
            while (results.next()) {
                //         norecords = false;
                HostInstanceStats temp = new HostInstanceStats();
                temp.setHost(results.getString("hostname"));
                temp.setRecordedTransactionCount(Long.valueOf(results.getLong("records")).intValue());
                list.getHostInstanceStats().add(temp);
            }

            res.setHosts(list);
            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw code;
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    }

    /**
     *
     *
     * Removes performance data for the specified service(s) and optionally for
     * the specified time range.
     *
     * If access is denied for any item, no changes will be made and a fault
     * will be returned.
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.PurgePerformanceDataResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "PurgePerformanceData", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/PurgePerformanceData")
    @WebResult(name = "PurgePerformanceDataResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "PurgePerformanceData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.PurgePerformanceData")
    @ResponseWrapper(localName = "PurgePerformanceDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.PurgePerformanceDataResponse")
    public PurgePerformanceDataResponseMsg purgePerformanceData(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") PurgePerformanceDataRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }

        if ((request.getURL() == null)) {
            throw new IllegalArgumentException("URL is null");
        }

        PurgePerformanceDataResponseMsg response = new PurgePerformanceDataResponseMsg();

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "purgePerformanceData", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        for (int i = 0; i < request.getURL().size(); i++) {
            UserIdentityUtil.assertAdministerAccess(request.getURL().get(i), currentUser, "purgePerformanceData", (request.getClassification()), ctx);
        }

        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = null;
        try {

            for (int i = 0; i < request.getURL().size(); i++) {
                try {
                    if (request.getRange() != null) {
                        cmd = con.prepareStatement("Delete from RawData where URI=? and utcdatetime >= ? and UTCdatetime <= ?;");

                        cmd.setLong(2, request.getRange().getStart().getTimeInMillis());
                        cmd.setLong(3, request.getRange().getEnd().getTimeInMillis());
                    } else {
                        cmd = con.prepareStatement("Delete from RawData where URI=?");
                    }
                    cmd.setString(1, request.getURL().get(i));
                    cmd.execute();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "error caught deleted records for service" + request.getURL().get(i), ex);
                } finally {
                    DBUtils.safeClose(cmd);
                }
            }
            response.setClassification(getCurrentClassificationLevel());
            return response;
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
    }

    /**
     *
     *
     * Returns the fgsms audit log for
     *
     *
     *
     *
     *
     *
     *
     * @param getAuditLog
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetAuditLogResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetAuditLog", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetAuditLog")
    @WebResult(name = "GetAuditLogResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetAuditLog", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAuditLog")
    @ResponseWrapper(localName = "GetAuditLogResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAuditLogResponse")
    public GetAuditLogResponseMsg getAuditLog(
            @WebParam(name = "GetAuditLog", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetAuditLogRequestMsg getAuditLog)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (getAuditLog == null) {
            throw new IllegalArgumentException("null message");
        }
        Utility.validateClassification(getAuditLog.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAuditLog", currentUser, "read audit logs", (getAuditLog.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAuditRole(currentUser, "getAuditLog", (getAuditLog.getClassification()), ctx);
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            GetAuditLogResponseMsg ret = new GetAuditLogResponseMsg();

            long count = 100;
            long offset = 0;
            if (getAuditLog.getRecordcount() >= 1) {
                count = getAuditLog.getRecordcount();
            }
            if (getAuditLog.getOffset() < 0) {
                offset = 0;
            } else {
                offset = getAuditLog.getOffset();
            }

            com = con.prepareStatement(" SELECT utcdatetime, username, classname, method, memo, "
                    + "classification, ipaddress FROM auditlog order by utcdatetime desc limit ? offset ?;");
            com.setLong(1, count);
            com.setLong(2, offset);

            rs = com.executeQuery();

            while (rs.next()) {
                /*
                 * recordcount++; if (recordcount >
                 * getAuditLog.getRecordcount()) { break; }
                 */
                AuditLog e = new AuditLog();
                e.setClassification(rs.getString("classification"));
                e.setClazz(rs.getString("classname"));
                e.setIpaddress(rs.getString("ipaddress"));
                e.setUsername(rs.getString("username"));
                e.setMethod(rs.getString("method"));
                e.setMemo(new String(rs.getBytes("memo"), Constants.CHARSET));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getLong("utcdatetime"));
                e.setTimestamp((cal));
                ret.getAuditLog().add(e);

            }

            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
    }

    /**
     *
     *
     * Returns the current list of monitored brokers and some general statistics
     * on each broker, such as current total message counts as reported by the
     * broker, average queue depth amonst all queues, etc.
     *
     *
     *
     *
     *
     *
     *
     * @param getBrokerListRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetBrokerListResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetBrokerList", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetBrokerList")
    @WebResult(name = "GetBrokerListResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetBrokerList", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetBrokerList")
    @ResponseWrapper(localName = "GetBrokerListResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetBrokerListResponse")
    public GetBrokerListResponseMsg getBrokerList(
            @WebParam(name = "GetBrokerListRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetBrokerListRequestMsg getBrokerListRequest)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (getBrokerListRequest == null) {
            throw new IllegalArgumentException("null message");
        }
        Utility.validateClassification(getBrokerListRequest.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getBrokerList", currentUser, "", (getBrokerListRequest.getClassification()), ctx.getMessageContext());

        GetBrokerListResponseMsg res = new GetBrokerListResponseMsg();

        boolean admin = UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "getBrokerList", getBrokerListRequest.getClassification(), ctx);
        Connection con = Utility.getPerformanceDBConnection();
        ResultSet rs = null;

        PreparedStatement com = null;
//TODO this needs to be rewritten, probably easier to load the service policies of type
//broker, then query for the details of thsoe brokers
        if (admin) {
            try {
                com = con.prepareStatement("select host  from brokerrawdata group by host;");
                rs = com.executeQuery();
                while (rs.next()) {
                    BrokerDetails bd = new BrokerDetails();
                    bd.setUri(rs.getString(1));
                    res.getBrokerList().add(bd);
                }
            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(com);

            }
        } else {
            try {
                List<String> potentials = new ArrayList<String>();

                com = con.prepareStatement("select host  from brokerrawdata group by host;");
                rs = com.executeQuery();
                while (rs.next()) {
                    potentials.add(rs.getString(1));
                }
                rs.close();
                com.close();

                PreparedStatement comPermissions = null;
                Connection config = Utility.getConfigurationDBConnection();
                for (int i = 0; i < potentials.size(); i++) {
                    comPermissions = config.prepareStatement("select ObjectURI from UserPermissions where ObjectURI=? and username=? and (ReadObject=true or WriteObject=true or AdministerObject=true or AuditObject=true);");
                    comPermissions.setString(1, potentials.get(i));
                    comPermissions.setString(2, currentUser);
                    rs = comPermissions.executeQuery();
                    while (rs.next()) {
                        BrokerDetails bd = new BrokerDetails();
                        bd.setUri(potentials.get(i));
                        res.getBrokerList().add(bd);
                    }
                    rs.close();
                    comPermissions.close();
                }
            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(com);

            }

        }

        for (int i = 0; i < res.getBrokerList().size(); i++) {
            try {
                com = con.prepareStatement("select sum(messagecount), sum(consumercount), sum(recievedmessagecount), sum(activeconsumercount), "
                        + "sum(queuedepth), sum(messagedropcount), sum(bytesdropcount), sum(bytesin), sum(bytesout) from brokerrawdata where host=?");
                com.setString(1, res.getBrokerList().get(i).getUri());
                rs = com.executeQuery();
                if (rs.next()) {

                    res.getBrokerList().get(i).setTotalmessagesent(rs.getLong(1));
                    res.getBrokerList().get(i).setTotalconsumers(rs.getLong(2));
                    res.getBrokerList().get(i).setTotalmessagesrecieved(rs.getLong(3));
                    res.getBrokerList().get(i).setTotalactiveconsumers(rs.getLong(4));
                    res.getBrokerList().get(i).setTotalqueuedepth(rs.getLong(5));
                    res.getBrokerList().get(i).setTotalmessagesdropped(rs.getLong(6));
                    res.getBrokerList().get(i).setTotalbytesdropped(rs.getLong(7));
                    res.getBrokerList().get(i).setTotalbytesin(rs.getLong(8));
                    res.getBrokerList().get(i).setTotalbytesout(rs.getLong(9));

                }
            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(com);
            }

        }
        for (int i = 0; i < res.getBrokerList().size(); i++) {
            try {
                // com = con.prepareStatement("select host  from brokerrawdata group by host;");
                com = con.prepareStatement("select count(*) from brokerrawdata where host=?;");
                com.setString(1, res.getBrokerList().get(i).getUri());
                rs = com.executeQuery();
                if (rs.next()) {
                    res.getBrokerList().get(i).setNumberOfQueuesTopics(rs.getLong(1));
                }
                rs.close();
                com.close();
            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(com);
            }
        }

        con = Utility.getConfigurationDBConnection();

        for (int i = 0; i < res.getBrokerList().size(); i++) {
            try {
                com = con.prepareStatement("SELECT utcdatetime, status  FROM status where uri=?;");
                com.setString(1, res.getBrokerList().get(i).getUri());
                rs = com.executeQuery();
                if (rs.next()) {
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTimeInMillis((long) rs.getLong(1));

                    res.getBrokerList().get(i).setLastCheckIn((c));
                    res.getBrokerList().get(i).setOperational(rs.getBoolean(2));
                }
            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(com);
            }

        }
        DBUtils.safeClose(con);
        res.setClassification(getCurrentClassificationLevel());
        return res;

    }

    protected static SecurityWrapper getCurrentClassificationLevel() throws ServiceUnavailableException {
        Connection con = Utility.getConfigurationDBConnection();
        ResultSet results = null;
        PreparedStatement comm = null;
        SecurityWrapper ret = new SecurityWrapper();
        try {

            comm = con.prepareStatement("Select classification, caveat from GlobalPolicies;");

            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            results = comm.executeQuery();

            if (results.next()) {
                ret.setClassification(ClassificationType.fromValue(results.getString("classification")));
                ret.setCaveats(results.getString("caveat"));

                return ret;
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error setting global policy (no records currently exist", ex);

        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        try {
            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("INSERT INTO GlobalPolicies (PolicyRefreshRate, RecordedMessageCap, classification, caveat) " + " VALUES (?, ?, ?, ?);");
            comm.setLong(1, 3 * 60 * 100);
            comm.setLong(2, 1024000);
            comm.setString(3, "U");
            comm.setString(4, "None");
            comm.execute();
            return ret;

        } catch (Exception ex) {
            log.log(Level.ERROR, "Error setting global policy (no records currently exist", ex);

        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw f;
    }

    /**
     *
     *
     * Returns the last known data set for a broker
     *
     *
     *
     *
     *
     *
     *
     * @param getCurrentBrokerDetailsRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetCurrentBrokerDetailsResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetCurrentBrokerDetails", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetCurrentBrokerDetails")
    @WebResult(name = "GetCurrentBrokerDetailsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetCurrentBrokerDetails", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetCurrentBrokerDetails")
    @ResponseWrapper(localName = "GetCurrentBrokerDetailsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetCurrentBrokerDetailsResponse")
    public GetCurrentBrokerDetailsResponseMsg getCurrentBrokerDetails(
            @WebParam(name = "GetCurrentBrokerDetailsRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetCurrentBrokerDetailsRequestMsg getCurrentBrokerDetailsRequest)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(getCurrentBrokerDetailsRequest.getClassification());
        if (Utility.stringIsNullOrEmpty(getCurrentBrokerDetailsRequest.getUrl())) {
            throw new IllegalArgumentException("url is null");
        }
        UserIdentityUtil.assertReadAccess(getCurrentBrokerDetailsRequest.getUrl(), currentUser, "getCurrentBrokerDetails", getCurrentBrokerDetailsRequest.getClassification(), ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getCurrentBrokerDetails", currentUser, getCurrentBrokerDetailsRequest.getUrl(), (getCurrentBrokerDetailsRequest.getClassification()), ctx.getMessageContext());
        GetCurrentBrokerDetailsResponseMsg res = new GetCurrentBrokerDetailsResponseMsg();

        Connection con = Utility.getPerformanceDBConnection();
        ResultSet rs = null;
        PreparedStatement com = null;
        try {
            com = con.prepareStatement("SELECT *  FROM brokerrawdata where host=?;");
            com.setString(1, getCurrentBrokerDetailsRequest.getUrl());
            res.setUri(getCurrentBrokerDetailsRequest.getUrl());
            rs = com.executeQuery();

            while (rs.next()) {
                QueueORtopicDetails item = new QueueORtopicDetails();
                item.setActiveconsumercount(rs.getLong("activeconsumercount"));
                item.setAgenttype(rs.getString("agenttype"));
                item.setCanonicalname(rs.getString("canonicalname"));
                item.setConsumercount(rs.getLong("consumercount"));
                item.setItemtype(rs.getString("typecol"));
                item.setMessagecount(rs.getLong("messagecount"));
                item.setName(rs.getString("namecol"));
                item.setQueueDepth(rs.getLong("queuedepth"));
                item.setRecievedmessagecount(rs.getLong("recievedmessagecount"));
                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(rs.getLong("utcdatetime"));
                item.setTimestamp((c));
                item.setBytesdropped(rs.getLong("bytesdropcount"));
                item.setBytesin(rs.getLong("bytesin"));
                item.setBytesout(rs.getLong("bytesout"));
                item.setMessagesdropped(rs.getLong("messagedropcount"));
                res.getQueueORtopicDetails().add(item);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);

        }
        con = Utility.getConfigurationDBConnection();
        try {
            com = con.prepareCall("SELECT status FROM status where uri=?;");
            com.setString(1, getCurrentBrokerDetailsRequest.getUrl());
            rs = com.executeQuery();
            if (rs.next()) {
                res.setOperational(rs.getBoolean("Status"));

            } else {
                //throw unexpected error
                res.setOperational(false);
            }
            res.setClassification(getCurrentClassificationLevel());

            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;

    }

    /**
     *
     *
     * Returns the historical data for a specific broker, such as average queue
     * depth across all topics and queues, over the given period of time
     *
     *
     *
     *
     *
     *
     *
     * @param getHistoricalBrokerDetailsRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetHistoricalBrokerDetailsResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetHistoricalBrokerDetails", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetHistoricalBrokerDetails")
    @WebResult(name = "GetHistoricalBrokerDetailsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetHistoricalBrokerDetails", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetHistoricalBrokerDetails")
    @ResponseWrapper(localName = "GetHistoricalBrokerDetailsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetHistoricalBrokerDetailsResponse")
    public GetHistoricalBrokerDetailsResponseMsg getHistoricalBrokerDetails(
            @WebParam(name = "GetHistoricalBrokerDetailsRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetHistoricalBrokerDetailsRequestMsg getHistoricalBrokerDetailsRequest)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(getHistoricalBrokerDetailsRequest.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getCurrentBrokerDetails", currentUser, getHistoricalBrokerDetailsRequest.getUri(), (getHistoricalBrokerDetailsRequest.getClassification()), ctx.getMessageContext());
        GetHistoricalBrokerDetailsResponseMsg res = new GetHistoricalBrokerDetailsResponseMsg();
        if (Utility.stringIsNullOrEmpty(getHistoricalBrokerDetailsRequest.getUri())) {
            throw new IllegalArgumentException("url is null");
        }
        if (getHistoricalBrokerDetailsRequest.getRange() == null) {
            throw new IllegalArgumentException("time range is null");
        }
        UserIdentityUtil.assertReadAccess(getHistoricalBrokerDetailsRequest.getUri(),
                currentUser, "getHistoricalBrokerDetails", getHistoricalBrokerDetailsRequest.getClassification(),
                ctx);
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            res.setUri(getHistoricalBrokerDetailsRequest.getUri());

            com = con.prepareStatement("select avg(activeconsumercount), avg(consumercount), "
                    + "avg(messagecount), avg(recievedmessagecount), avg(queuedepth),  avg(messagedropcount), "
                    + "avg(bytesdropcount), avg(bytesin), avg(bytesout) "
                    + "from brokerrawdata where host=? "
                    + "and (utcdatetime > ?) and (utcdatetime < ?)");
            com.setString(1, getHistoricalBrokerDetailsRequest.getUri());
            com.setLong(
                    2, getHistoricalBrokerDetailsRequest.getRange().getStart().getTimeInMillis());
            com.setLong(
                    3, getHistoricalBrokerDetailsRequest.getRange().getEnd().getTimeInMillis());
            rs = com.executeQuery();
            if (rs.next()) {

                res.setAverageactiveconsumers(rs.getDouble(1));
                // res.setAveragenumberOfQueuesTopics(value)
                res.setAverageconsumers(rs.getDouble(2));
                res.setAveragemessagesent(rs.getDouble(3));
                res.setAveragemessagesrecieved(rs.getDouble(4));
                res.setAveragequeuedepth(rs.getDouble(5));
                res.setAveragemessagesdropped(rs.getDouble(6));
                res.setAveragebytesdropped(rs.getDouble(7));
                res.setAveragebytesin(rs.getDouble(8));
                res.setAveragebytesout(rs.getDouble(9));
            }

            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
    }

    /**
     *
     *
     * Returns the historical data for a specific topic or queue on a specific
     * broker Must input either (URI AND topic/queue name) OR (URI AND
     * cannoicalname)
     *
     *
     *
     *
     *
     * @param getHistoricalTopicQueueDetailsRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetHistoricalTopicQueueDetailsResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetHistoricalTopicQueueDetails", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetHistoricalTopicQueueDetails")
    @WebResult(name = "GetHistoricalTopicQueueDetailsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetHistoricalTopicQueueDetails", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetHistoricalTopicQueueDetails")
    @ResponseWrapper(localName = "GetHistoricalTopicQueueDetailsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetHistoricalTopicQueueDetailsResponse")
    public GetHistoricalTopicQueueDetailsResponseMsg getHistoricalTopicQueueDetails(
            @WebParam(name = "GetHistoricalTopicQueueDetailsRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetHistoricalTopicQueueDetailsRequestMsg getHistoricalTopicQueueDetailsRequest)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(getHistoricalTopicQueueDetailsRequest.getClassification());

        GetHistoricalTopicQueueDetailsResponseMsg res = new GetHistoricalTopicQueueDetailsResponseMsg();
        res.setClassification(getCurrentClassificationLevel());

        if (getHistoricalTopicQueueDetailsRequest.getUri() == null) {
            throw new IllegalArgumentException("uri is null");
        }
        if (getHistoricalTopicQueueDetailsRequest.getRange() == null) {
            throw new IllegalArgumentException("range is null");
        }
        if (Utility.stringIsNullOrEmpty(getHistoricalTopicQueueDetailsRequest.getQueuetopiccanonicalname()) || Utility.stringIsNullOrEmpty(getHistoricalTopicQueueDetailsRequest.getQueuetopicname())) {
            throw new IllegalArgumentException("queue name is null");
        }

        String name = "";
        if (!Utility.stringIsNullOrEmpty(getHistoricalTopicQueueDetailsRequest.getQueuetopiccanonicalname())) {
            name = getHistoricalTopicQueueDetailsRequest.getQueuetopiccanonicalname();
        } else {
            name = getHistoricalTopicQueueDetailsRequest.getQueuetopicname();
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getHistoricalTopicQueueDetails", currentUser, getHistoricalTopicQueueDetailsRequest.getQueuetopicname() + getHistoricalTopicQueueDetailsRequest.getQueuetopiccanonicalname(), (getHistoricalTopicQueueDetailsRequest.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertReadAccess(getHistoricalTopicQueueDetailsRequest.getUri(), currentUser, "getHistoricalTopicQueueDetails", getHistoricalTopicQueueDetailsRequest.getClassification(), ctx);
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            com = con.prepareStatement("select * from brokerhistory where  host=? and (utcdatetime > ?) and (utcdatetime < ?) and (name=? or canonicalname=?) order by utcdatetime desc;");
            com.setString(1, getHistoricalTopicQueueDetailsRequest.getUri());
            com.setLong(2, getHistoricalTopicQueueDetailsRequest.getRange().getStart().getTimeInMillis());
            com.setLong(3, getHistoricalTopicQueueDetailsRequest.getRange().getEnd().getTimeInMillis());
            com.setString(4, name);
            com.setString(5, name);
            rs = com.executeQuery();
            while (rs.next()) {
                QueueORtopicDetails qt = new QueueORtopicDetails();
                qt.setActiveconsumercount(rs.getLong("activeconsumercount"));
                qt.setAgenttype(rs.getString("agenttype"));
                qt.setBytesdropped(rs.getLong("bytesdropped"));
                qt.setBytesin(rs.getLong("bytesin"));
                qt.setBytesout(rs.getLong("bytesout"));
                qt.setCanonicalname(rs.getString("canonicalname"));
                qt.setName(rs.getString("namecol"));
                qt.setQueueDepth(rs.getLong("queuedepth"));
                qt.setRecievedmessagecount(rs.getLong("reievedmessagecount"));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getLong("utcdatetime"));
                qt.setTimestamp((cal));
                res.getHistoricalTopicQueueDetails().add(qt);
            }
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);

        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
    }

    /**
     *
     *
     * Returns a listing of fgsms agents that have recorded data
     *
     *
     *
     *
     *
     *
     *
     * @param getAgentTypesRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetAgentTypesResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetAgentTypes", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetAgentTypes")
    @WebResult(name = "GetAgentTypesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetAgentTypes", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAgentTypes")
    @ResponseWrapper(localName = "GetAgentTypesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAgentTypesResponse")
    public GetAgentTypesResponseMsg getAgentTypes(
            @WebParam(name = "GetAgentTypesRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetAgentTypesRequestMsg getAgentTypesRequest)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(getAgentTypesRequest.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAgentTypes", currentUser, "", (getAgentTypesRequest.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getAgentTypes",
                getAgentTypesRequest.getClassification(), ctx);

        Connection con;
        con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select * from agents order by agenttype asc;");

            rs = com.executeQuery();
            GetAgentTypesResponseMsg res = new GetAgentTypesResponseMsg();
            while (rs.next()) {
                String s = rs.getString(1);
                if (!Utility.stringIsNullOrEmpty(s)) {
                    res.getAgentType().add(s);
                }
            }
            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
    }

    /**
     *
     *
     * Returns a listing of related transaction IDs, given a thread transaction
     * id
     *
     *
     *
     *
     *
     * @param getThreadTransactionsRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetThreadTransactionsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetThreadTransactions", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetRelatedTransactions")
    @WebResult(name = "GetThreadTransactionsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetThreadTransactions", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetThreadTransactions")
    @ResponseWrapper(localName = "GetThreadTransactionsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetThreadTransactionsResponse")
    public GetThreadTransactionsResponseMsg getThreadTransactions(
            @WebParam(name = "GetThreadTransactionsRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetThreadTransactionsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getThreadTransactions", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            if (Utility.stringIsNullOrEmpty(request.getId())) {
                throw new IllegalArgumentException("id");
            }
            com = con.prepareStatement("select uri, success, utcdatetime, responsetimems, soapaction, transactionid from rawdata where threadid=? order by utcdatetime desc;");
            com.setString(1, request.getId());
            rs = com.executeQuery();
            GetThreadTransactionsResponseMsg res = new GetThreadTransactionsResponseMsg();
            while (rs.next()) {
                ThreadTime tt = new ThreadTime();
                tt.setAction(rs.getString("soapaction"));
                //tt.set
                tt.setUri(rs.getString("uri"));

                tt.setTransactionid(rs.getString("transactionid"));
                tt.setResponsetimems(rs.getLong("responsetimems"));
                tt.setSuccess(rs.getBoolean("success"));
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                tt.setTimestamp((gcal));
                if (UserIdentityUtil.hasReadAccess(currentUser, "getThreadTransactions",tt.getUri(), 
                        request.getClassification(), ctx)) {
                    res.getThreads().add(tt);
                }
            }

            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        ServiceUnavailableException code = new ServiceUnavailableException("", null);

        code.getFaultInfo()
                .setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;

    }

    /**
     *
     *
     * quickly returns statistics for a given service *
     *
     *
     *
     *
     *
     * @param getQuickStatsRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetQuickStatsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetQuickStats", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetQuickStats")
    @WebResult(name = "GetQuickStatsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetQuickStats", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetQuickStats")
    @ResponseWrapper(localName = "GetQuickStatsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetQuickStatsResponse")
    public GetQuickStatsResponseMsg getQuickStats(
            @WebParam(name = "GetQuickStatsRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetQuickStatsRequestMsg getQuickStatsRequest)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(getQuickStatsRequest.getClassification());
        if (!UserIdentityUtil.isTrustedAgent(currentUser, "getQuickStats", getQuickStatsRequest.getClassification(), ctx)) {
            UserIdentityUtil.assertReadAccess(getQuickStatsRequest.getUri(), currentUser, "getQuickStats", getQuickStatsRequest.getClassification(), ctx);
        }

        if (Utility.stringIsNullOrEmpty(getQuickStatsRequest.getUri())) {
            throw new IllegalArgumentException("uri");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getQuickStats", currentUser, getQuickStatsRequest.getUri(), (getQuickStatsRequest.getClassification()), ctx.getMessageContext());

        return fetchQuickStat(getQuickStatsRequest.getUri(), getQuickStatPeriods(), false);

    }

    /**
     * returns all of the current time periods used for statistics aggregation
     *
     * @return
     */
    private List<Long> getQuickStatPeriods() {
        List<Long> periods = new ArrayList<Long>();
        try {
            KeyNameValueEnc GetPropertiesFromDB = DBSettingsLoader.GetPropertiesFromDB(true, "StatisticsAggregator", "Periods");
            String[] items = GetPropertiesFromDB.getKeyNameValue().getPropertyValue().split(",");
            for (int i = 0; i < items.length; i++) {
                long l = Long.parseLong(items[i]);
                if (l > 0) {
                    periods.add(l);
                }
            }
            periods.add(Long.valueOf(5 * 60 * 1000));
            periods.add(Long.valueOf(15 * 60 * 1000));
            periods.add(Long.valueOf(60 * 60 * 1000));
            periods.add(Long.valueOf(24 * 60 * 60 * 1000));

            Set<Long> minified = new HashSet<Long>(periods);
            periods = new ArrayList<Long>(minified);
        } catch (Exception e) {
            log.log(Level.INFO, "Statistics settings from the database for time periods was unparsable, reverting to the defaults.");
            periods.add(Long.valueOf(5 * 60 * 1000));
            periods.add(Long.valueOf(15 * 60 * 1000));
            periods.add(Long.valueOf(60 * 60 * 1000));
            periods.add(Long.valueOf(24 * 60 * 60 * 1000));
        }
        Collections.sort(periods);
        return periods;
    }

    private GetQuickStatsResponseMsg fetchQuickStat(String url, List<Long> periods, boolean allmethodsonly) throws ServiceUnavailableException {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            //Load flexible quick stat periods
            GetQuickStatsResponseMsg res = new GetQuickStatsResponseMsg();

            res.setUri(url);

            List<String> actions = new ArrayList<String>();
            if (!allmethodsonly) {
                DASHelper.addServiceActionsFromDB(actions, url);
            }
            actions.add("All-Methods");

            Duration uptime = DASHelper.getUpTime(url);
            QuickStatWrapper w = null;// = new QuickStatWrapper();
            for (int i = 0; i < actions.size(); i++) {
                w = new QuickStatWrapper();
                w.setAction(actions.get(i));
                w.setUptime(uptime);

                for (int k = 0; k < periods.size(); k++) {
                    try {
                        com = con.prepareStatement("select * from agg2 where uri=? and soapaction=? and timerange=?;");
                        com.setString(1, url);
                        com.setString(2, actions.get(i));
                        com.setLong(3, periods.get(k));
                        rs = com.executeQuery();

                        while (rs.next()) {

                            QuickStatData d = new QuickStatData();
                            Long x = periods.get(k) / (60 * 1000);
                            d.setTimeInMinutes(BigInteger.valueOf(x));
                            d.setAvailabilityPercentage(rs.getDouble("avail"));
                            d.setFailureCount(rs.getLong("failure"));
                            d.setSuccessCount(rs.getLong("success"));
                            d.setSLAViolationCount(rs.getLong("sla"));
                            if (d.getSLAViolationCount() < 0) {
                                d.setSLAViolationCount(0);
                            }

                            d.setAverageResponseTime(rs.getLong("avgres"));
                            d.setMTBF(df.newDuration(rs.getLong("mtbf")));
                            d.setMaximumRequestSize(rs.getLong("maxreq"));
                            d.setMaximumResponseSize(rs.getLong("maxres"));
                            d.setMaximumResponseTime(rs.getLong("maxresponsetime"));
                            GregorianCalendar gcal = new GregorianCalendar();
                            gcal.setTimeInMillis(rs.getLong("timestampepoch"));
                            d.setUpdatedAt((gcal));

                            d.setAverageCPUUsage(rs.getDouble("avgcpu"));
                            d.setAverageMemoryUsage(rs.getLong("avgmem"));
                            d.setAverageThreadCount(rs.getLong("avgthread"));
                            d.setAverageOpenFileCount(rs.getLong("avgfile"));

                            d.setAverageMessagesIn(rs.getLong("avgmsgin"));
                            d.setAverageMessagesOut(rs.getLong("avgmsgout"));
                            d.setAverageMessagesDropped(rs.getLong("avgmsgdropped"));
                            d.setLargestQueueDepth(rs.getLong("maxqueuedepth"));
                            w.getQuickStatData().add(d);
                        }
                    } catch (Exception ex) {
                        log.log(Level.ERROR, "unexpected SQL error", ex);
                    } finally {
                        DBUtils.safeClose(rs);
                        DBUtils.safeClose(com);
                    }

                }   //end of for each period/timerange 
                res.getQuickStatWrapper().add(w);
            } //for each action

            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        } finally {
            DBUtils.safeClose(con);
        }
    }

    private String getPolicyDisplayName(String uRL) {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select displayname from servicepolicies where uri=?;");
            com.setString(1, uRL);
            rs = com.executeQuery();
            if (rs.next()) {
                String s = rs.getString(1);
                rs.close();
                com.close();
                con.close();
                return s;
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.WARN, "couldn't get the display name for " + uRL, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        return null;

    }

    /**
     *
     *
     * quickly returns statistics for all accessible services *
     *
     *
     *
     *
     *
     * @param getQuickStatsAllRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetQuickStatsAllResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetQuickStatsAll", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetQuickStatsAll")
    @WebResult(name = "GetQuickStatsAllResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetQuickStatsAll", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetQuickStatsAll")
    @ResponseWrapper(localName = "GetQuickStatsAllResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetQuickStatsAllResponse")
    public GetQuickStatsAllResponseMsg getQuickStatsAll(
            @WebParam(name = "GetQuickStatsAllRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetQuickStatsAllRequestMsg getQuickStatsAllRequest)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        Utility.validateClassification(getQuickStatsAllRequest.getClassification());
        GetQuickStatsAllResponseMsg res2 = new GetQuickStatsAllResponseMsg();
        res2.setClassification(getCurrentClassificationLevel());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getQuickStatsAllRequest", currentUser, "", (getQuickStatsAllRequest.getClassification()), ctx.getMessageContext());
        List<Long> periods = getQuickStatPeriods();

        ArrayOfServiceType temp = DASHelper.getServiceListfromPolicyDB(ctx, getQuickStatsAllRequest.getClassification());
        if (temp == null || temp.getServiceType().isEmpty()) {
            return res2;
        }

        ArrayOfServiceType urls = temp;
        for (int i = 0; i < urls.getServiceType().size(); i++) {
            GetQuickStatsResponseMsg w2 = fetchQuickStat(urls.getServiceType().get(i).getURL(), periods, getQuickStatsAllRequest.isAllMethodsOnly());
            QuickStatURIWrapper wrapper = new QuickStatURIWrapper();
            wrapper.setUri(urls.getServiceType().get(i).getURL());
            wrapper.getQuickStatWrapper().addAll(w2.getQuickStatWrapper());
            res2.getQuickStatURIWrapper().add(wrapper);

        } //for each service

        //con.close();
        return res2;

    }

    /**
     *
     *
     * obtain audit logs by time range, requires global admin role *
     *
     *
     *
     *
     *
     * @param getAuditLogsByTimeRangeRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetAuditLogsByTimeRangeResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAuditLogsByTimeRange", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetAuditLogsByTimeRange")
    @WebResult(name = "GetAuditLogsByTimeRangeResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetAuditLogsByTimeRange", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAuditLogsByTimeRange")
    @ResponseWrapper(localName = "GetAuditLogsByTimeRangeResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAuditLogsByTimeRangeResponse")
    public GetAuditLogsByTimeRangeResponseMsg getAuditLogsByTimeRange(
            @WebParam(name = "GetAuditLogsByTimeRangeRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetAuditLogsByTimeRangeRequestMsg getAuditLogsByTimeRangeRequest)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (getAuditLogsByTimeRangeRequest == null) {
            throw new IllegalArgumentException("null message");
        }
        Utility.validateClassification(getAuditLogsByTimeRangeRequest.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAuditLogsByTimeRangeRequest", currentUser, "read audit logs", (getAuditLogsByTimeRangeRequest.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAuditRole(currentUser, "getAuditLogsByTimeRangeRequest", (getAuditLogsByTimeRangeRequest.getClassification()), ctx);
        GetAuditLogsByTimeRangeResponseMsg ret = new GetAuditLogsByTimeRangeResponseMsg();
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            long count = 100;
            long offset = 0;
            if (getAuditLogsByTimeRangeRequest.getRecordcount() >= 1) {
                count = getAuditLogsByTimeRangeRequest.getRecordcount();
            }
            if (getAuditLogsByTimeRangeRequest.getOffset() < 0) {
                offset = 0;
            } else {
                offset = getAuditLogsByTimeRangeRequest.getOffset();
            }
            if (getAuditLogsByTimeRangeRequest.getRange() == null
                    || getAuditLogsByTimeRangeRequest.getRange().getEnd() == null
                    || getAuditLogsByTimeRangeRequest.getRange().getStart() == null) {
                throw new IllegalArgumentException("null time range");
            }

            com = con.prepareStatement(" SELECT utcdatetime, username, classname, method, memo, "
                    + "classification, ipaddress FROM auditlog where utcdatetime > ? and utcdatetime < ? order by utcdatetime desc limit ? offset ?;");
            com.setLong(1, getAuditLogsByTimeRangeRequest.getRange().getStart().getTimeInMillis());
            com.setLong(2, getAuditLogsByTimeRangeRequest.getRange().getEnd().getTimeInMillis());
            com.setLong(3, count);
            com.setLong(4, offset);

            rs = com.executeQuery();

            while (rs.next()) {

                AuditLog e = new AuditLog();
                e.setClassification(rs.getString("classification"));
                e.setClazz(rs.getString("classname"));
                e.setIpaddress(rs.getString("ipaddress"));
                e.setUsername(rs.getString("username"));
                e.setMethod(rs.getString("method"));
                e.setMemo(new String(rs.getBytes("memo"), Constants.CHARSET));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getLong("utcdatetime"));
                e.setTimestamp((cal));
                ret.getAuditLog().add(e);

            }

            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (IllegalArgumentException ex) {
            log.log(Level.INFO, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
    }

    /**
     *
     * get all machines in a given domain plus drive info
     *
     *
     * @param getMostRecentMachineDataByDomainRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineDataByDomainResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMostRecentMachineDataByDomain", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMostRecentMachineDataByDomain")
    @WebResult(name = "GetMostRecentMachineDataByDomainResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMostRecentMachineDataByDomain", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineDataByDomain")
    @ResponseWrapper(localName = "GetMostRecentMachineDataByDomainResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineDataByDomainResponse")
    public GetMostRecentMachineDataByDomainResponseMsg getMostRecentMachineDataByDomain(
            @WebParam(name = "GetMostRecentMachineDataByDomainRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMostRecentMachineDataByDomainRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("null message");
        }
        if (Utility.stringIsNullOrEmpty(request.getDomain())) {
            throw new IllegalArgumentException("null domain. use unspecified for domains that are not identified");
        }
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getMostRecentMachineDataByDomain", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getMostRecentMachineDataByDomain", (request.getClassification()), ctx);

        GetMostRecentMachineDataByDomainResponseMsg ret = new GetMostRecentMachineDataByDomainResponseMsg();

        //get all urls with policy type =machine
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            //this.GetServiceListfromPolicyDB(null, PolicyType.STATUS, currentUser)
            com = con.prepareStatement(" SELECT * from servicepolicies where policytype=? and domaincol=?;");
            com.setInt(1, PolicyType.MACHINE.ordinal());
            com.setString(2, request.getDomain());

            rs = com.executeQuery();

            while (rs.next()) {
                MachineData d = new MachineData();
                d.setHostname(rs.getString("hostname"));
                d.setDomainName(rs.getString("domaincol"));
                ret.getMachineData().add(d);
            }

            for (int i = 0; i < ret.getMachineData().size(); i++) {
                //get machine performance
                MachinePolicy mp = (MachinePolicy) SLACommon.LoadPolicyPooled("urn:" + ret.getMachineData().get(i).getHostname() + ":system");
                ret.getMachineData().get(i).setMachinePerformanceData(getCurrentMachinePerformanceData(mp));

                //get all monitored processes on this machine
                //    List<String> processURLlist = GetProcessListByHostname(ret.getMachineData().get(i).getHostname(), ret.getMachineData().get(i).getDomainName());
                ret.getMachineData().get(i).getProcessPerformanceData().addAll((getCurrentProcessPerformanceDataList(getProcessListByHostname(ret.getMachineData().get(i).getHostname(), ret.getMachineData().get(i).getDomainName()))));
                //get performance for each monitpored process
                //get al drive infomration
                ret.getMachineData().get(i).getDriveInformation().addAll(getCurrentDriveInformation(ret.getMachineData().get(i).getHostname(), ret.getMachineData().get(i).getDomainName()));
            }

            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
    }

    /**
     *
     * machine's perf data plus drive usage
     *
     *
     * @param getMostRecentMachineDataRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineDataResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMostRecentMachineData", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMostRecentMachineData")
    @WebResult(name = "GetMostRecentMachineDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMostRecentMachineData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineData")
    @ResponseWrapper(localName = "GetMostRecentMachineDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineDataResponse")
    public GetMostRecentMachineDataResponseMsg getMostRecentMachineData(
            @WebParam(name = "GetMostRecentMachineDataRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMostRecentMachineDataRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("null message");
            }
            if (Utility.stringIsNullOrEmpty(request.getUri()) && (Utility.stringIsNullOrEmpty(request.getHostname()) || Utility.stringIsNullOrEmpty(request.getDomainname()))) {
                throw new IllegalArgumentException("the uri must be specified.");
            }

            String hostname = request.getHostname();
            String domainname = request.getDomainname();
            String uri = request.getUri();
            if (!Utility.stringIsNullOrEmpty(uri)) {
                Pair p = translateURItoPair(uri);
                if (p == null) {
                    throw new IllegalArgumentException("the specific URI is not known");
                }
                hostname = p.hostname;
                domainname = p.domainname;
            }

            Utility.validateClassification(request.getClassification());
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getMostRecentMachineData", currentUser, "", (request.getClassification()), ctx.getMessageContext());
            //UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getMostRecentMachineData", (request.getClassification()), ctx);
            UserIdentityUtil.assertReadAccess(uri, currentUser, "getMostRecentMachineData", request.getClassification(), ctx);
            GetMostRecentMachineDataResponseMsg ret = new GetMostRecentMachineDataResponseMsg();
            MachineData d = new MachineData();
            d.setDomainName(domainname);
            d.setHostname(hostname);
            ServicePolicy sp = SLACommon.LoadPolicyPooled(uri);
            if (!(sp instanceof MachinePolicy)) {
                throw new IllegalArgumentException("The requested URL is not of type Machine Policy");
            }
            MachinePolicy mp = (MachinePolicy) sp;
            d.getDriveInformation().addAll(getCurrentDriveInformation(mp));
            d.setMachinePerformanceData(getCurrentMachinePerformanceData(mp));
            //d.getProcessPerformanceData().addAll(GetCurrentProcessPerformanceDataList());
            ret.setMachineData(d);
            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        }

    }

    /**
     *
     * process's perf data
     *
     *
     * @param getMostRecentProcessDataRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentProcessDataResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMostRecentProcessData", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMostRecentProcessData")
    @WebResult(name = "GetMostRecentProcessDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMostRecentProcessData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentProcessData")
    @ResponseWrapper(localName = "GetMostRecentProcessDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentProcessDataResponse")
    public GetMostRecentProcessDataResponseMsg getMostRecentProcessData(
            @WebParam(name = "GetMostRecentProcessDataRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMostRecentProcessDataRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("null message");
            }
            if (Utility.stringIsNullOrEmpty(request.getUri())) {
                throw new IllegalArgumentException("null uri. a process uri must be specified");
            }

            Utility.validateClassification(request.getClassification());
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getMostRecentProcessData", currentUser, "", (request.getClassification()), ctx.getMessageContext());
            //UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getMostRecentProcessData", (request.getClassification()), ctx);
            UserIdentityUtil.assertReadAccess(request.getUri(), currentUser, "getMostRecentProcessData", request.getClassification(), ctx);
            GetMostRecentProcessDataResponseMsg ret = new GetMostRecentProcessDataResponseMsg();
            ret.setPerformanceData(getCurrentProcessPerformanceDataList(request.getUri()));
            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (DatatypeConfigurationException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        }
    }

    /**
     *
     * returns performance and drive information logs of a specific machine
     * records by uri and time range
     *
     *
     * @param getMachinePerformanceLogsByRangeRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetMachinePerformanceLogsByRangeResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMachinePerformanceLogsByRange", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetMachinePerformanceLogsByRange")
    @WebResult(name = "GetMachinePerformanceLogsByRangeResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetMachinePerformanceLogsByRange", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMachinePerformanceLogsByRange")
    @ResponseWrapper(localName = "GetMachinePerformanceLogsByRangeResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetMachinePerformanceLogsByRangeResponse")
    public GetMachinePerformanceLogsByRangeResponseMsg getMachinePerformanceLogsByRange(
            @WebParam(name = "GetMachinePerformanceLogsByRangeRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetMachinePerformanceLogsByRangeRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("null message");
            }
            if (Utility.stringIsNullOrEmpty(request.getUri())) {
                throw new IllegalArgumentException("null uri. a process uri must be specified");
            }
            long offset = 0;
            if (request.getOffset() > 0) {
                offset = request.getOffset();
            }
            long records = 100;
            if (request.getRecordcount() < 0) {
                records = 1;
            }
            if (request.getRecordcount() > 1000) {
                records = 1000;
            }
            if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
                throw new IllegalArgumentException("Missing input parameters");
            }
            Utility.validateClassification(request.getClassification());
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getMachinePerformanceLogsByRange", currentUser, "", (request.getClassification()), ctx.getMessageContext());
            //    UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getMachinePerformanceLogsByRange", (request.getClassification()), ctx);
            UserIdentityUtil.assertReadAccess(request.getUri(), currentUser, "getMachinePerformanceLogsByRange", (request.getClassification()), ctx);
            GetMachinePerformanceLogsByRangeResponseMsg ret = GetMachinePerfLogsByRange(request.getUri(), request.getRange(), offset, records);

            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (DatatypeConfigurationException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        }

    }

    /**
     *
     * returns the most recent information for all machine and processes that
     * the requestor has access to
     *
     *
     * @param getAllMostRecentMachineAndProcessDataRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllMostRecentMachineAndProcessDataResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAllMostRecentMachineAndProcessData", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetAllMostRecentMachineAndProcessData")
    @WebResult(name = "GetAllMostRecentMachineAndProcessDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetAllMostRecentMachineAndProcessData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllMostRecentMachineAndProcessData")
    @ResponseWrapper(localName = "GetAllMostRecentMachineAndProcessDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllMostRecentMachineAndProcessDataResponse")
    public GetAllMostRecentMachineAndProcessDataResponseMsg getAllMostRecentMachineAndProcessData(
            @WebParam(name = "GetAllMostRecentMachineAndProcessDataRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetAllMostRecentMachineAndProcessDataRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("null message");
            }

            Utility.validateClassification(request.getClassification());
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getAllMostRecentMachineAndProcessData", currentUser, "", (request.getClassification()), ctx.getMessageContext());

            //TODO reduce to AssertRead access with filtering based on user
            UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getAllMostRecentMachineAndProcessData", (request.getClassification()), ctx);
            GetAllMostRecentMachineAndProcessDataResponseMsg ret = new GetAllMostRecentMachineAndProcessDataResponseMsg();
            ArrayOfServiceType machines = DASHelper.getServiceListfromPolicyDB(ctx, request.getClassification(), PolicyType.MACHINE);
            if (machines != null) {
                for (int i = 0; i < machines.getServiceType().size(); i++) {
                    ServicePolicy md = SLACommon.LoadPolicyPooled(machines.getServiceType().get(i).getURL());
                    MachineData data = new MachineData();
                    data.setDomainName(md.getDomainName());
                    data.setHostname(md.getMachineName());
                    data.setMachinePerformanceData(getCurrentMachinePerformanceData((MachinePolicy) md));
                    ArrayOfServiceType processes = DASHelper.getServiceListfromPolicyDB(ctx, request.getClassification(), PolicyType.PROCESS, md.getMachineName());
                    if (processes != null) {
                        for (int k = 0; k < processes.getServiceType().size(); k++) {
                            ProcessPerformanceData ppd = this.getCurrentProcessPerformanceDataList(processes.getServiceType().get(k).getURL());
                            data.getProcessPerformanceData().add(ppd);
                        }
                    }
                    ret.getMachineData().add(data);
                }
            }
            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (DatatypeConfigurationException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        }
    }

    /**
     *
     * returns performance logs of a specific process records by uri and time
     * range
     *
     *
     * @param getProcessPerformanceLogsByRangeRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetProcessPerformanceLogsByRangeResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetProcessPerformanceLogsByRange", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetProcessPerformanceLogsByRange")
    @WebResult(name = "GetProcessPerformanceLogsByRangeResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetProcessPerformanceLogsByRange", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetProcessPerformanceLogsByRange")
    @ResponseWrapper(localName = "GetProcessPerformanceLogsByRangeResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetProcessPerformanceLogsByRangeResponse")
    public GetProcessPerformanceLogsByRangeResponseMsg getProcessPerformanceLogsByRange(
            @WebParam(name = "GetProcessPerformanceLogsByRangeRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetProcessPerformanceLogsByRangeRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        if (request == null) {
            throw new IllegalArgumentException("null message");
        }
        if (Utility.stringIsNullOrEmpty(request.getUri())) {
            throw new IllegalArgumentException("null uri. a process uri must be specified");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        long offset = 0;
        if (request.getOffset() > 0) {
            offset = request.getOffset();
        }
        long records = 100;
        if (request.getRecordcount() < 0) {
            records = 1;
        }
        if (request.getRecordcount() > 1000) {
            records = 1000;
        }
        if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("Missing input parameters");
        }

        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getProcessPerformanceLogsByRange", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        //    UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getProcessPerformanceLogsByRange", (request.getClassification()), ctx);
        UserIdentityUtil.assertReadAccess(request.getUri(), currentUser, "getProcessPerformanceLogsByRange", (request.getClassification()), ctx);

        try {
            GetProcessPerformanceLogsByRangeResponseMsg ret = new GetProcessPerformanceLogsByRangeResponseMsg();
            ret.setInstalledMemory(this.getMachineInstalledRam(SLACommon.LoadPolicyPooled(request.getUri()).getMachineName()));
            ret.getProcessData().addAll(this.GetProcessPerformanceDataList(request.getUri(), request.getRange(), offset, records));

            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (DatatypeConfigurationException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw code;
        }
    }

    /**
     * used to load installed ram on a given system
     *
     * @param hostname
     * @return
     */
    protected long getMachineInstalledRam(String hostname) {
        long ret = -1;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet rs = null;

        try {

            comm = con.prepareStatement(""
                    + "select * from machineinfo where hostname=?;");
            comm.setString(1, hostname);
            rs = comm.executeQuery();
            if (rs.next()) {
                Unmarshaller u = jc.createUnmarshaller();
                byte[] s = rs.getBytes("xmlcol");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                JAXBElement<SetProcessListByMachineRequestMsg> foo = (JAXBElement<SetProcessListByMachineRequestMsg>) u.unmarshal(r, SetProcessListByMachineRequestMsg.class);
                if (foo
                        == null || foo.getValue()
                        == null) {
                    log.log(Level.WARN, "xml is unexpectedly null or empty");
                } else {
                    ret = foo.getValue().getMachineInformation().getMemoryinstalled();
                }
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "error caught loading machine information", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);

        }
        return ret;
    }

    private List<String> getProcessListByHostname(String hostname, String domainName) {
        List<String> r = new ArrayList<String>();

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select uri from servicepolicies where hostname=? and domaincol=? and policytype=?;");
            com.setString(1, hostname);
            com.setString(2, domainName);
            com.setInt(3, PolicyType.PROCESS.ordinal());
            rs = com.executeQuery();
            while (rs.next()) {
                r.add(rs.getString(1));
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return r;

    }

    private ProcessPerformanceData getCurrentProcessPerformanceDataList(String processURI) throws DatatypeConfigurationException {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        ProcessPerformanceData p = new ProcessPerformanceData();

        try {
            com = con.prepareStatement("select * from rawdatamachineprocess where uri=? order by utcdatetime desc limit 1;");
            com.setString(1, processURI);
            rs = com.executeQuery();
            if (rs.next()) {

                p.setUri(processURI);
                p.setBytesusedMemory(rs.getLong("memoryused"));
                p.setId(rs.getString("id"));
                //p.setKilobytespersecondDisk(rs.getLong("diskKBs"));
                p.setPercentusedCPU(rs.getDouble("percentCPU"));
                p.setNumberofActiveThreads(rs.getLong("threads"));
                //p.setKilobytespersecondNetwork(rs.getLong("networkKBs"));
                p.setOpenFileHandles(rs.getLong("openfiles"));
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                p.setTimestamp((gcal));

            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        try {
            StatusHelper sh = getStatus(processURI);
            if (sh != null) {
                p.setOperationalstatus(sh.running);
                p.setStatusmessage(sh.msg);
            } else {
                p.setStatusmessage("Unknown");
                p.setOperationalstatus(false);
            }
        } catch (Exception ex) {
            p.setStatusmessage("Unknown");
            p.setOperationalstatus(false);
            log.log(Level.WARN, "trouble getting status info", ex);
        }

        return p;
    }

    private List<ProcessPerformanceData> GetProcessPerformanceDataList(String processURI, TimeRange range, long offset, long records) throws DatatypeConfigurationException {
        List<ProcessPerformanceData> ret = new ArrayList<ProcessPerformanceData>();
        Connection con = Utility.getPerformanceDBConnection();

        ProcessPerformanceData p = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select * from rawdatamachineprocess where uri=? and utcdatetime > ? and utcdatetime < ? order by utcdatetime desc limit ? offset ?;");
            com.setString(1, processURI);
            com.setLong(2, range.getStart().getTimeInMillis());
            com.setLong(3, range.getEnd().getTimeInMillis());
            com.setLong(4, records);
            com.setLong(5, offset);
            rs = com.executeQuery();
            while (rs.next()) {
                p = new ProcessPerformanceData();
                p.setUri(processURI);
                p.setBytesusedMemory(rs.getLong("memoryused"));
                p.setId(rs.getString("id"));
                //p.setKilobytespersecondDisk(rs.getLong("diskKBs"));
                p.setPercentusedCPU(rs.getDouble("percentCPU"));
                p.setNumberofActiveThreads(rs.getLong("threads"));
                //p.setKilobytespersecondNetwork(rs.getLong("networkKBs"));
                p.setOpenFileHandles(rs.getLong("openfiles"));
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                p.setTimestamp((gcal));

                gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("startedat"));
                p.setStartedAt((gcal));
                ret.add(p);
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return ret;
    }

    private List<NetworkAdapterPerformanceData> GetCurrentNicInfo(MachinePolicy mp) {
        List<NetworkAdapterPerformanceData> items = new ArrayList<NetworkAdapterPerformanceData>();

        if (mp == null) {
            return items;
        }
        if (mp.getRecordNetworkUsage().isEmpty()) {
            return items;
        }
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        for (int i = 0; i < mp.getRecordNetworkUsage().size(); i++) {
            try {
                NetworkAdapterPerformanceData d = new NetworkAdapterPerformanceData();
                d.setAdapterName(mp.getRecordNetworkUsage().get(i));

                com = con.prepareStatement("select * from rawdatanic where nicid=? and hostname=? and domainname=? order by utcdatetime desc limit 1;");
                com.setString(1, mp.getRecordNetworkUsage().get(i));
                com.setString(2, mp.getMachineName());
                com.setString(3, mp.getDomainName());
                rs = com.executeQuery();
                if (rs.next()) {
                    d.setKilobytespersecondNetworkTransmit(rs.getLong("sendKBs"));
                    d.setKilobytespersecondNetworkReceive(rs.getLong("receiveKBs"));
                }

                items.add(d);
            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(com);

            }
        }

        DBUtils.safeClose(con);
        return items;

    }

    private GetMachinePerformanceLogsByRangeResponseMsg GetMachinePerfLogsByRange(String uri, TimeRange range, long offset, long records) throws DatatypeConfigurationException {
        GetMachinePerformanceLogsByRangeResponseMsg ret = new GetMachinePerformanceLogsByRangeResponseMsg();
        Pair TranslateURItoPair = this.translateURItoPair(uri);
        // List<ProcessPerformanceData> ret = new ArrayList<ProcessPerformanceData>();
        Connection con = Utility.getPerformanceDBConnection();

        MachinePerformanceData p = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select * from rawdatamachineprocess where uri=? and utcdatetime > ? and utcdatetime < ? order by utcdatetime desc limit ? offset ?;");
            com.setString(1, uri);
            com.setLong(2, range.getStart().getTimeInMillis());
            com.setLong(3, range.getEnd().getTimeInMillis());
            com.setLong(4, records);
            com.setLong(5, offset);
            rs = com.executeQuery();
            while (rs.next()) {
                p = new MachinePerformanceData();
                p.setUri(uri);
                p.setBytesusedMemory(rs.getLong("memoryused"));
                p.setId(rs.getString("id"));
                p.setPercentusedCPU(rs.getDouble("percentCPU"));
                p.setNumberofActiveThreads(rs.getLong("threads"));
                StatusHelper h = getStatus(uri);
                p.setStatusmessage(h.msg);

                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                p.setTimestamp((gcal));
                Long l = rs.getLong("startedat");
                if (l != null) {
                    gcal = new GregorianCalendar();
                    gcal.setTimeInMillis(l);
                    p.setStartedAt((gcal));
                }
                p.getDriveInformation().addAll(this.getDriveLogsByRange(TranslateURItoPair.hostname, TranslateURItoPair.domainname, rs.getLong("utcdatetime")));
                p.getNetworkAdapterPerformanceData().addAll(this.getNICLogsByRange(TranslateURItoPair.hostname, TranslateURItoPair.domainname, rs.getLong("utcdatetime")));
                ret.getMachinePerformanceData().add(p);
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return ret;

    }

    private List<DriveInformation> getDriveLogsByRange(String hostname, String domainname, long timestamp) {
        Connection con = Utility.getPerformanceDBConnection();
        List<DriveInformation> ret = new ArrayList<DriveInformation>();
        DriveInformation p = null;
//get a list of nics recorded during this time range
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select * from rawdatadrives where hostname=? and domainname=? and utcdatetime = ?;");
            com.setString(1, hostname);
            com.setString(2, domainname);
            com.setLong(3, timestamp);
            rs = com.executeQuery();
            while (rs.next()) {
                p = new DriveInformation();
                p.setFreespace(rs.getLong("freespace"));
                p.setId(rs.getString("id"));
                p.setKilobytespersecondDiskRead(rs.getLong("readKBs"));
                p.setKilobytespersecondDiskWrite(rs.getLong("writeKBs"));
                p.setOperationalstatus(rs.getString("statusmsg"));
                p.setOperational(rs.getBoolean("status"));
                p.setPartition(rs.getString("driveIdentifier"));
                p.setSystemid(rs.getString("deviceIdentifier"));
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(timestamp);
                p.setTimestamp((gcal));
                ret.add(p);
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        return ret;
    }

    private List<NetworkAdapterPerformanceData> getNICLogsByRange(String hostname, String domainname, long timestamp) {
        Connection con = Utility.getPerformanceDBConnection();
        List<NetworkAdapterPerformanceData> ret = new ArrayList<NetworkAdapterPerformanceData>();
        NetworkAdapterPerformanceData p = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select * from rawdatanic where hostname=? and domainname=? and utcdatetime = ?;");
            com.setString(1, hostname);
            com.setString(2, domainname);
            com.setLong(3, timestamp);
            rs = com.executeQuery();
            while (rs.next()) {
                p = new NetworkAdapterPerformanceData();
                p.setAdapterName(rs.getString("nicid"));
                p.setKilobytespersecondNetworkReceive(rs.getLong("receiveKBs"));
                p.setKilobytespersecondNetworkTransmit(rs.getLong("sendKBs"));

                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("utcdatetime"));

                ret.add(p);
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return ret;

    }

    private List<ProcessPerformanceData> getCurrentProcessPerformanceDataList(List<String> processURLlist) {
        List<ProcessPerformanceData> data = new ArrayList<ProcessPerformanceData>();
        Connection con = Utility.getPerformanceDBConnection();
        for (int i = 0; i < processURLlist.size(); i++) {
            PreparedStatement com = null;
            ResultSet rs = null;
            try {
                com = con.prepareStatement("select * from rawdatamachineprocess where uri=? order by utcdatetime desc limit 1;");
                com.setString(1, processURLlist.get(i));
                rs = com.executeQuery();
                if (rs.next()) {
                    ProcessPerformanceData p = new ProcessPerformanceData();
                    p.setUri(processURLlist.get(i));
                    p.setBytesusedMemory(rs.getLong("memoryused"));
                    p.setId(rs.getString("id"));
                    //p.setKilobytespersecondDisk(rs.getLong("diskKBs"));
                    p.setPercentusedCPU(rs.getDouble("percentCPU"));
                    p.setNumberofActiveThreads(rs.getLong("threads"));
                    p.setOpenFileHandles(rs.getLong("openfiles"));
                    //p.setKilobytespersecondNetwork(rs.getLong("networkKBs"));
                    StatusHelper h = getStatus(p.getUri());
                    if (h != null) {
                        p.setOperationalstatus(h.running);
                        p.setStatusmessage(h.msg);

                    } else {
                        p.setOperationalstatus(false);
                        p.setStatusmessage("Unknown");
                    }
                    GregorianCalendar gcal = new GregorianCalendar();
                    gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                    p.setTimestamp((gcal));
                    Long l = rs.getLong("startedat");
                    if (l != null) {
                        gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(l);
                        p.setStartedAt((gcal));
                    }
                    data.add(p);
                }

            } catch (Exception ex) {
                log.log(Level.ERROR, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(com);

            }
        }
        DBUtils.safeClose(con);

        return data;
    }

    private List<DriveInformation> getCurrentDriveInformation(String hostname, String domainName) {
        Connection con = Utility.getPerformanceDBConnection();
        List<DriveInformation> r = new ArrayList<DriveInformation>();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            com = con.prepareStatement("select * from rawdatadrives where hostname=? and domainname=? order by utcdatetime desc limit 1;");
            com.setString(1, hostname);
            com.setString(2, domainName);
            rs = com.executeQuery();
            while (rs.next()) {
                DriveInformation d = new DriveInformation();
                d.setFreespace(rs.getLong("freespace"));
                d.setPartition(rs.getString("driveIdentifier"));
                d.setKilobytespersecondDiskWrite(rs.getLong("writeKBs"));
                d.setKilobytespersecondDiskRead(rs.getLong("readKBs"));
                d.setOperational(rs.getBoolean("status"));
                d.setOperationalstatus(rs.getString("statusmsg"));
                d.setId(rs.getString("id"));
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                d.setTimestamp((gcal));
                r.add(d);
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        return r;
    }

    /**
     * returns null if not found
     *
     * @param uri
     * @return
     * @throws SQLException
     */
    private StatusHelper getStatus(String uri) throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            StatusHelper h = new StatusHelper();
            cmd = con.prepareStatement("select * from status where uri=?;");
            cmd.setString(1, uri);
            rs = cmd.executeQuery();
            if (rs.next()) {
                h.msg = rs.getString("message");
                h.running = rs.getBoolean("status");
                rs.close();
                cmd.close();
                con.close();
                return h;
            }
            rs.close();
            cmd.close();
            con.close();
            return null;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        return null;
    }

    private MachinePerformanceData getCurrentMachinePerformanceData(MachinePolicy mp) {
        MachinePerformanceData ret = new MachinePerformanceData();
        Connection con = null;
        //get machine info
        con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            com = con.prepareStatement("SELECT uri, memoryused, "
                    + "percentcpu, id, utcdatetime, threads, openfiles  FROM rawdatamachineprocess where uri=? order by utcdatetime desc limit 1;");
            com.setString(1, mp.getURL());
            rs = com.executeQuery();
            if (rs.next()) {
                ret.setBytesusedMemory(rs.getLong("memoryused"));
                ret.setId(rs.getString("id"));
                ret.setNumberofActiveThreads(rs.getLong("threads"));
                ret.setPercentusedCPU(rs.getDouble("percentCPU"));
                ret.setUri(rs.getString("uri"));
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                ret.setTimestamp((gcal));
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        ret.getDriveInformation().addAll(this.getCurrentDriveInformation(mp));
        ret.getNetworkAdapterPerformanceData().addAll(GetCurrentNicInfo(mp));
        try {
            StatusHelper sh = getStatus(mp.getURL());
            if (sh != null) {
                ret.setOperationalstatus(sh.running);
                ret.setStatusmessage(sh.msg);
            } else {
                ret.setStatusmessage("Unknown");
                ret.setOperationalstatus(false);
            }
        } catch (Exception ex) {
            ret.setStatusmessage("Unknown");
            log.log(Level.WARN, "trouble getting status info", ex);
        }
        return ret;
    }

    private List<DriveInformation> getCurrentDriveInformation(MachinePolicy mp) {
        return this.getCurrentDriveInformation(mp.getMachineName(), mp.getDomainName());
    }

    private Pair translateURItoPair(String policyURI) {
        Connection con = Utility.getConfigurationDBConnection();
        Pair p = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select hostname, domaincol from servicepolicies where uri=?;");
            com.setString(1, policyURI);
            rs = com.executeQuery();
            if (rs.next()) {
                p = new Pair();
                p.hostname = rs.getString("hostname");
                p.domainname = rs.getString("domaincol");
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return p;
    }

    /**
     *
     * removes a record of a specific service dependency
     *
     *
     * @param deleteServiceDependencyRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.DeleteServiceDependencyResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "DeleteServiceDependency", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/DeleteServiceDependency")
    @WebResult(name = "DeleteServiceDependencyResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "DeleteServiceDependency", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.DeleteServiceDependency")
    @ResponseWrapper(localName = "DeleteServiceDependencyResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.DeleteServiceDependencyResponse")
    public DeleteServiceDependencyResponseMsg deleteServiceDependency(
            @WebParam(name = "DeleteServiceDependencyRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") DeleteServiceDependencyRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("null message");
        }
        if (Utility.stringIsNullOrEmpty(request.getUri()) || Utility.stringIsNullOrEmpty(request.getDependenturi())) {
            throw new IllegalArgumentException("the uri must be specified");
        }

        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "deleteServiceDependency", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertWriteAccess(request.getUri(), currentUser, "deleteServiceDependency", (request.getClassification()), ctx);
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = null;
        try {
            cmd = con.prepareStatement("DELETE FROM dependencies WHERE sourceurl=? and sourcesoapaction=? and destinationurl=? and destinationsoapaction=?;");
            cmd.setString(1, request.getUri());
            if (Utility.stringIsNullOrEmpty(request.getAction())) {
                cmd.setNull(2, Types.VARCHAR);
            } else {
                cmd.setString(2, request.getAction());
            }
            cmd.setString(3, request.getDependenturi());
            if (Utility.stringIsNullOrEmpty(request.getDependentaction())) {
                cmd.setNull(4, Types.VARCHAR);
            } else {
                cmd.setString(4, request.getDependentaction());
            }
            cmd.executeUpdate();
            cmd.close();
            con.close();
            DeleteServiceDependencyResponseMsg res = new DeleteServiceDependencyResponseMsg();
            res.setClassification(getCurrentClassificationLevel());
            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;
    }

    /**
     *
     * returns the service policy urls that all the service depends on global
     * admin rights required
     *
     * @param getAllServiceDependenciesRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllServiceDependenciesResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAllServiceDependencies", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetAllServiceDependencies")
    @WebResult(name = "GetAllServiceDependenciesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetAllServiceDependencies", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllServiceDependencies")
    @ResponseWrapper(localName = "GetAllServiceDependenciesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetAllServiceDependenciesResponse")
    public GetAllServiceDependenciesResponseMsg getAllServiceDependencies(
            @WebParam(name = "GetAllServiceDependenciesRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetAllServiceDependenciesRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("null message");
        }

        Utility.validateClassification(request.getClassification());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getAllServiceDependencies", (request.getClassification()), ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAllServiceDependencies", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        Connection con = Utility.getPerformanceDBConnection();
        ResultSet rs = null;
        PreparedStatement cmd = null;
        try {

            GetAllServiceDependenciesResponseMsg res = new GetAllServiceDependenciesResponseMsg();
            res.setClassification(getCurrentClassificationLevel());

            cmd = con.prepareStatement("select * from dependencies");

            rs = cmd.executeQuery();

            while (rs.next()) {
                DependencyWrapper dw = new DependencyWrapper();
                dw.setSourceuri(rs.getString("sourceurl"));
                dw.setSourceaction(rs.getString("sourcesoapaction"));
                dw.setDestinationaction(rs.getString("destinationaction"));
                dw.setDestinationuri(rs.getString("destinationurl"));
                res.getDependencies().add(dw);
                //res.getIdependonAndDependonme().add(of.createGetAllServiceDependenciesResponseMsgIdependon(d));
            }

            /*

             cmd = con.prepareStatement("select * from dependencies where destinationurl=?");
             cmd.setString(1, list.getValue().getServiceType().get(i).getURL());
             rs = cmd.executeQuery();
             while (rs.next()) {
             Dependencies d = new Dependencies();
             d.setAction(rs.getString("sourcesoapaction"));
             d.setUrl(rs.getString("sourceurl"));
             res.getIdependonAndDependonme().add(of.createGetAllServiceDependenciesResponseMsgDependonme(d));
             }
             rs.close();
             cmd.close();*/
            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;

    }

    /**
     *
     * returns the service policy urls that the given service depends on
     *
     *
     * @param getServiceDependenciesRequest
     * @return returns
     * org.miloss.fgsms.services.interfaces.dataaccessservice.GetServiceDependenciesResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetServiceDependencies", action = "urn:org:miloss:fgsms:services:interfaces:dataAccessService/dataAccessService/GetServiceDependencies")
    @WebResult(name = "GetServiceDependenciesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService")
    @RequestWrapper(localName = "GetServiceDependencies", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetServiceDependencies")
    @ResponseWrapper(localName = "GetServiceDependenciesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", className = "org.miloss.fgsms.services.interfaces.dataaccessservice.GetServiceDependenciesResponse")
    public GetServiceDependenciesResponseMsg getServiceDependencies(
            @WebParam(name = "GetServiceDependenciesRequest", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService") GetServiceDependenciesRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("null message");
        }
        if (Utility.stringIsNullOrEmpty(request.getUri())) {
            throw new IllegalArgumentException("the uri must be specified");
        }

        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getServiceDependencies", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertReadAccess(request.getUri(), currentUser, "getServiceDependencies", (request.getClassification()), ctx);

        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = null;
        ResultSet rs = null;
        GetServiceDependenciesResponseMsg res = new GetServiceDependenciesResponseMsg();
        try {

            res.setClassification(getCurrentClassificationLevel());

            cmd = con.prepareStatement("select * from dependencies where sourceurl=?");
            cmd.setString(1, request.getUri());
            rs = cmd.executeQuery();
            while (rs.next()) {
                Dependencies d = new Dependencies();
                d.setAction(rs.getString("destinationsoapaction"));
                d.setUrl(rs.getString("destinationurl"));
                res.getIdependon().add(d);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
        }

        try {
            cmd = con.prepareStatement("select * from dependencies where destinationurl=?");
            cmd.setString(1, request.getUri());
            rs = cmd.executeQuery();
            while (rs.next()) {
                Dependencies d = new Dependencies();
                d.setAction(rs.getString("sourcesoapaction"));
                d.setUrl(rs.getString("sourceurl"));
                res.getDependonme().add(d);
            }
            return res;

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);

        code.getFaultInfo()
                .setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;

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
        res
                .getVersionInfo().setVersionSource(org.miloss.fgsms.common.Constants.class
                        .getCanonicalName());
        res.setStartedAt(started);
        boolean ok = true;
        Connection con = Utility.getConfigurationDBConnection();
        Connection con2 = Utility.getPerformanceDBConnection();
        PreparedStatement prepareStatement = null;
        PreparedStatement prepareStatement2 = null;
        try {
            prepareStatement = con.prepareStatement("select 1=1;");
            prepareStatement.execute();

            prepareStatement2 = con2.prepareStatement("select 1=1;");
            prepareStatement2.execute();
            res.setStatusMessage("OK");
        } catch (Exception ex) {
            ok = false;
            res.setStatusMessage("One or more of the database connections is available");
        } finally {
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(prepareStatement2);
            DBUtils.safeClose(con);
            DBUtils.safeClose(con2);
        }

        res.setStatus(ok);
        return res;
    }
}
