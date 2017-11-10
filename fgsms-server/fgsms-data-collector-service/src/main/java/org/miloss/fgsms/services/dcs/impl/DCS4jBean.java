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
package org.miloss.fgsms.services.dcs.impl;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.*;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ProcessPolicy;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.sla.TransactionalSLAProcessor;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * fgsms's Data Collector Service, provides a centralized and unified interfaced
 * for collecting supported data from fgsms's agents
 *
 * @author AO
 */
//@javax.ejb.Stateless
@WebService(serviceName = "DataCollectorService", name = "DCS",
        targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector"
//,wsdlLocation = "WEB-INF/wsdl/DCS8.wsdl"
)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED, use = SOAPBinding.Use.LITERAL)
public class DCS4jBean implements DCS, org.miloss.fgsms.services.interfaces.datacollector.OpStatusService {

    private static final int MAXTEXT = 128;
    private static final Logger log = Logger.getLogger("fgsms.DataCollectorService");
    private static boolean IsRegistered = false;
    private static Calendar started = null;
    private static DatatypeFactory df = null;
    private static long successTX = 0;
    private static long failuresTX = 0;
    private static HashMap uriaction = new HashMap();
    private static HashMap urialturi = new HashMap();
    private static HashMap hosts = new HashMap();
    private static HashMap agents = new HashMap();
    private static boolean DEBUG = false;

    public DCS4jBean() throws DatatypeConfigurationException {
        Init();
    }
    private static TransactionalSLAProcessor p = new TransactionalSLAProcessor();

    /**
     * constructor used for unit testing, do not remove
     *
     * @param c2
     */
    protected DCS4jBean(WebServiceContext c2) throws DatatypeConfigurationException {
        ctx = c2;
        Init();
    }

    private synchronized void Init() throws DatatypeConfigurationException {
        df = DatatypeFactory.newInstance();
        if (started == null) {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            started = (gcal);
        }
    }
    @Resource
    private WebServiceContext ctx;

    /**
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.datacollector.AddDataResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "AddData", action = "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddData")
    @WebResult(name = "AddDataResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector")
    @RequestWrapper(localName = "AddData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddData")
    @ResponseWrapper(localName = "AddDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddDataResponse")
    public AddDataResponseMsg addData(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector") AddDataRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {
        CheckRegistration(ctx.getMessageContext());
        if (req == null || Utility.stringIsNullOrEmpty(req.getURI())) {
            failuresTX++;
            throw new IllegalArgumentException("request or uri is null");
        }
        Utility.validateClassification(req.getClassification());

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "addData", currentUser, req.getURI() + " agent type " + req.getAgentType() + " " + req.getMessage(), req.getClassification(), ctx.getMessageContext());
        UserIdentityUtil.assertAgentRole(currentUser, "addData", req.getClassification(), ctx);
        AddDataResponseMsg res = new AddDataResponseMsg();
        String tid = "";
        boolean success = true;
        if (Utility.stringIsNullOrEmpty(req.getTransactionID())) {
            tid = java.util.UUID.randomUUID().toString();
        } else {
            tid = req.getTransactionID();
        }
        Connection con = Utility.getPerformanceDBConnection();;

        if (con == null) {
            org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException t = new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException();
            t.setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            ServiceUnavailableException e = new ServiceUnavailableException("db unavailable", t);
            failuresTX++;
            throw e;
        }
        if (success) {

            if (DEBUG) {
                log.log(Level.WARN, "Recording tid " + tid);
            }
            success = pushNewDatatoSQL(req, tid, con);
        }

        DBUtils.safeClose(con);

        if (success) {
            successTX++;

            updateServiceHostStats(req.getServiceHost(), 1);
            res.setStatus(DataResponseStatus.SUCCESS);
            try {
                p.ProcessNewTransaction(req, tid);
            } catch (Exception ex) {
                log.log(Level.ERROR, "error caught processing SLAs", ex);
            }
        } else {
            failuresTX++;
            res.setStatus(DataResponseStatus.FAILURE);
        }
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
        org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException t = new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException();
        t.setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        ServiceUnavailableException e = new ServiceUnavailableException("db unavailable", t);
        throw e;
    }

    private boolean pushNewDatatoSQL(AddDataRequestMsg req, String tid, Connection con) {
        if (req == null) {
            return false;//throw new Exception("Add Request is null");
        }
        long now = System.currentTimeMillis();
        //Driver d;
        PreparedStatement com = null;
        try {
            com = con.prepareStatement("INSERT INTO RawData(ConsumerIdentity, "
                    + "HostingSource, responseSize, requestSize, URI, Success, MonitorSource, requestxml, "
                    + "responsexml, UTCdatetime, ResponseTimeMS, originalurl, agenttype, soapaction, TransactionID"
                    + ", relatedtransactionid, threadid, requestheaders, responseheaders, message" //20 params
                    + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            //int MAXTEXT = 128;
            if (req.getIdentity() != null && req.getIdentity().size() > 0) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < req.getIdentity().size(); i++) {
                    if (!Utility.stringIsNullOrEmpty(req.getIdentity().get(i))) {
                        sb = sb.append(req.getIdentity().get(i)).append(";");
                    }
                }
                //workaround for a bug discovered post RC6 from the ASP.NET Agent
                String ids = sb.toString();
                if (!Utility.stringIsNullOrEmpty(ids)) {
                    ids = ids.trim();
                }
                if (!Utility.stringIsNullOrEmpty(ids)) {
                    ids = ids.substring(0, ids.length() - 1);
                    com.setString(1, Utility.truncate((ids), MAXTEXT));
                } else {
                    com.setString(1, "");
                }
            } else {
                com.setString(1, "");
            }
            com.setString(2, Utility.truncate(req.getServiceHost(), MAXTEXT));
            com.setLong(3, req.getResponseSize());
            com.setLong(4, req.getRequestSize());
            com.setString(5, Utility.truncate(req.getURI(), MAXTEXT));
            com.setBoolean(6, req.isSuccess());
            com.setString(7, Utility.truncate(InetAddress.getLocalHost().getHostName().toLowerCase(), MAXTEXT));
            if (Utility.stringIsNullOrEmpty(req.getXmlRequest())) {
                com.setNull(8, java.sql.Types.BINARY);
            } else {
                byte[] x = Utility.EN((req.getXmlRequest()).trim()).getBytes(Constants.CHARSET);// Encoding.UTF8.GetBytes(req.getXmlRequest());
                ByteArrayInputStream s = new ByteArrayInputStream(x);
                com.setBinaryStream(8, s, (int) x.length);
            }
            if (Utility.stringIsNullOrEmpty(req.getXmlResponse())) {
                com.setNull(9, java.sql.Types.BINARY);
            } else {
                byte[] x = Utility.EN((req.getXmlResponse()).trim()).getBytes(Constants.CHARSET);// Encoding.UTF8.GetBytes(req.getXmlRequest());
                ByteArrayInputStream s = new ByteArrayInputStream(x);
                com.setBinaryStream(9, s, (int) x.length);
            }

            if (req.getRecordedat() == null) {
                com.setLong(10, System.currentTimeMillis());
            } else {
                com.setLong(10, req.getRecordedat().getTimeInMillis());
            }
            com.setLong(11, req.getResponseTime());
            com.setString(12, Utility.truncate(req.getRequestURI(), MAXTEXT));
            if (Utility.stringIsNullOrEmpty(req.getAgentType())) {
                com.setNull(13, java.sql.Types.VARCHAR);
            } else {
                com.setString(13, Utility.truncate(req.getAgentType(), MAXTEXT));
            }

            if (Utility.stringIsNullOrEmpty(req.getAction())) {
                com.setString(14, "unspecified");
                req.setAction("unspecified");
                //com.setNull(14, java.sql.Types.VARCHAR);
            } else {
                com.setString(14, Utility.truncate(req.getAction(), MAXTEXT));
            }

            com.setString(15, tid);

            if (Utility.stringIsNullOrEmpty(req.getRelatedTransactionID())) {
                com.setNull(16, java.sql.Types.VARCHAR);
            } else {
                com.setString(16, Utility.truncate(req.getRelatedTransactionID(), MAXTEXT));
            }

            if (Utility.stringIsNullOrEmpty(req.getTransactionThreadID())) {
                com.setNull(17, java.sql.Types.VARCHAR);
            } else {
                com.setString(17, Utility.truncate(req.getTransactionThreadID(), MAXTEXT));
            }
            if (req.getHeadersRequest() == null || req.getHeadersRequest().isEmpty()) {
                com.setNull(18, java.sql.Types.BINARY);
            } else {
                StringBuilder data = new StringBuilder();

                for (int i = 0; i < req.getHeadersRequest().size(); i++) {
                    if (req.getHeadersRequest().get(i) != null) {
                        if (!Utility.stringIsNullOrEmpty(req.getHeadersRequest().get(i).getName())) {
                            if (req.getHeadersRequest().get(i).getName().equalsIgnoreCase("Authorization")) {
                                data.append(req.getHeadersRequest().get(i).getName()).append("=***************");
                            } else {
                                data.append(req.getHeadersRequest().get(i).getName()).append("=");
                                for (int k = 0; k < req.getHeadersRequest().get(i).getValue().size(); k++) {
                                    data.append(req.getHeadersRequest().get(i).getValue().get(k)).append(";");
                                }
                            }
                            data.append("|");
                        }
                    }
                }
                //  if (data.length() > 0) {
                //      data = data.substring(0, data.length() - 2);
                //   }
                String d2 = Utility.EN(data.toString());
                com.setObject(18, (d2).getBytes(Constants.CHARSET));
            }
            if (req.getHeadersResponse() == null || req.getHeadersResponse().isEmpty()) {
                com.setNull(19, java.sql.Types.BINARY);
            } else {
                StringBuilder data = new StringBuilder();
                for (int i = 0; i < req.getHeadersResponse().size(); i++) {
                    if (req.getHeadersResponse().get(i) != null) {
                        if (!Utility.stringIsNullOrEmpty(req.getHeadersResponse().get(i).getName())) {
                            //password scrubber
                            if (req.getHeadersResponse().get(i).getName().equalsIgnoreCase("Authorization")) {
                                data.append(req.getHeadersResponse().get(i).getName()).append("=");
                                data.append("***************");
                            } else {
                                data.append(req.getHeadersResponse().get(i).getName()).append("=");
                                for (int k = 0; k < req.getHeadersResponse().get(i).getValue().size(); k++) {
                                    data.append(req.getHeadersResponse().get(i).getValue().get(k)).append(";");
                                }
                            }
                            data.append("|");
                        }
                    }
                }
                // if (data.length() > 0) {
                //    data = data.substring(0, data.length() - 2);
                // }
                String d2 = Utility.EN(data.toString());
                com.setObject(19, (d2).getBytes(Constants.CHARSET));
            }

            if (req.getMessage() == null || req.getMessage().isEmpty()) {
                com.setNull(20, java.sql.Types.BINARY);
            } else {

                com.setObject(20, (req.getMessage()).getBytes(Constants.CHARSET));
            }
            try {
                com.execute();
            } catch (Exception ex) {
                log.log(Level.ERROR, "DCS error saving transactional data " + ex.getMessage());
                log.log(Level.DEBUG, "DCS error saving transactional data ", ex);
            } finally {
                DBUtils.safeClose(com);
            }
            if (!urialturi.containsKey(req.getURI() + req.getRequestURI())) {
                urialturi.put(req.getURI() + req.getRequestURI(), true);
                try {
                    com = con.prepareStatement("insert into alternateurls (uri, alturi) values (?,?);");
                    com.setString(1, req.getURI());
                    com.setString(2, req.getRequestURI());
                    com.execute();
                    log.log(Level.DEBUG, "DCS DEBUG adding alt URL");
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "DCS DEBUG adding alt URL FAILED");
                } finally {
                    DBUtils.safeClose(com);
                }
            }
            if (!uriaction.containsKey(req.getURI() + req.getAction())) {
                uriaction.put(req.getURI() + req.getAction(), true);
                try {
                    com = con.prepareStatement("insert into actionlist (uri, soapaction) values (?,?);");
                    com.setString(1, req.getURI());
                    com.setString(2, req.getAction());
                    com.execute();
                    log.log(Level.DEBUG, "DCS DEBUG adding URL action");
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "DCS DEBUG adding URL action FAILED");
                } finally {
                    DBUtils.safeClose(com);
                }
            }

            if (!agents.containsKey(req.getAgentType())) {
                agents.put(req.getAgentType(), true);
                Connection config = Utility.getConfigurationDBConnection();
                try {
                    com = config.prepareStatement("INSERT INTO agents(agenttype)  VALUES (?)");
                    com.setString(1, req.getAgentType());
                    com.execute();
                    log.log(Level.DEBUG, "DCS DEBUG adding agent type");
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "DCS DEBUG adding agent type FAILED");
                } finally {
                    DBUtils.safeClose(com);
                    DBUtils.safeClose(config);
                }
            }

            now = System.currentTimeMillis() - now;
            log.log(Level.DEBUG, "SQL Data Push for " + req.getURI() + " in " + now + "ms");

        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg.contains("rawdata_pkey")) {
                //this means that there was an issue in agent land, basically, the agent is continuously trying to transmit the same data to this DCS instance over and over again. Please
                //report this to the developers
                log.log(Level.WARN, "Error caught recording performance data. "
                        + "this means that there was an issue in agent land, basically, the agent is continuously trying to transmit the same data to this DCS instance over and over again. Please report this to the developers URL: "
                        + req.getURI() + " Action: " + req.getAction() + " Agent: " + req.getAgentType() + " Host: " + req.getServiceHost() + " I'm going to return this as successful, even though it was not.");
                return true;
            } else {
                log.log(Level.ERROR, "Error recording performance data. Data may be lost URL: " + req.getURI() + " Action: " + req.getAction() + " Agent: " + req.getAgentType() + " Host: " + req.getServiceHost(), ex);
                return false;
            }
        } finally {
          

        }

        return true;
    }

    /**
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.datacollector.AddDataResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "AddMoreData", action = "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddMoreData")
    @WebResult(name = "AddDataResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector")
    @RequestWrapper(localName = "AddMoreData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddMoreData")
    @ResponseWrapper(localName = "AddMoreDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddMoreDataResponse")
    public AddDataResponseMsg addMoreData(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector") List<AddDataRequestMsg> req)
            throws AccessDeniedException, ServiceUnavailableException {
        CheckRegistration(ctx.getMessageContext());
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (req == null || req.isEmpty())// Utility.stringIsNullOrEmpty(req.getURI())) {
        {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "addMoreData", currentUser, "null or empty request ", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is empty");
        }
        if (req.get(0) == null || req.get(0).getClassification() == null || req.get(0).getClassification().getClassification() == null) {
            throw new IllegalArgumentException("a classificaiton level must be specified for at least the first item");

        }

        AuditLogger.logItem(this.getClass().getCanonicalName(), "addMoreData", currentUser, "msg count=" + req.size() + " 1st record agent type=" + req.get(0).getAgentType() + " uri=" + req.get(0).getURI(), req.get(0).getClassification(), ctx.getMessageContext());
        UserIdentityUtil.assertAgentRole(currentUser, "addData", req.get(0).getClassification(), ctx);
        Connection con = null;
        boolean success = true;

        con = Utility.getPerformanceDBConnection();

        if (con == null) {
            org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException t = new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException();
            t.setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            ServiceUnavailableException e = new ServiceUnavailableException("db unavailable", t);
            throw e;
        }
        AddDataResponseMsg res = new AddDataResponseMsg();
        String tid = "";
        for (int i = 0; i < req.size(); i++) {
            if (Utility.stringIsNullOrEmpty(req.get(i).getTransactionID())) {
                tid = java.util.UUID.randomUUID().toString();
            } else {
                tid = req.get(i).getTransactionID();
            }
            if (DEBUG) {
                log.log(Level.WARN, "Recording tid " + tid);
            }
            boolean t = pushNewDatatoSQL(req.get(i), tid, con);
            success = success && t;
            if (t) {
                try {
                    p.ProcessNewTransaction(req.get(i), tid);
                } catch (Exception ex) {
                    log.log(Level.ERROR, "error caught processing SLAs", ex);
                }
                updateServiceHostStats(req.get(0).getServiceHost(), req.size());
            }
        }
        DBUtils.safeClose(con);

        if (success) {
            successTX += req.size();
            res.setStatus(DataResponseStatus.SUCCESS);
        } else {
            failuresTX += req.size();
            res.setStatus(DataResponseStatus.FAILURE);
        }
        try {
            res.setClassification(getCurrentClassificationLevel());
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
        return res;
    }

    private void updateServiceHostStats(String host, int count) {
        Connection config = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        if (!hosts.containsKey(host)) {
            hosts.put(host, true);
            try {
                com = config.prepareStatement("INSERT INTO servicehosts(hostname)  VALUES (?)");
                com.setString(1, host);
                com.execute();

                if (DEBUG) {
                    log.log(Level.DEBUG, "DCS DEBUG adding service host");
                }
            } catch (Exception ex) {
                log.log(Level.DEBUG, "DCS DEBUG adding service host FAILED " + ex.getMessage(), ex);
            } finally {
                DBUtils.safeClose(com);
            }
        }
        try {
            com = config.prepareStatement("UPDATE servicehosts   SET records=records + ? WHERE hostname=?; "
                    + "UPDATE dcsservicehosts   SET  records=records + ? WHERE hostname=? ;  ");
            com.setInt(1, count);
            com.setString(2, host);
            com.setInt(3, count);
            com.setString(4, Utility.getHostName());
            com.execute();

            if (DEBUG) {
                log.log(Level.INFO, "DCS DEBUG updated servicehost stats");
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "Trouble updating statistics for either servicehsots or dscservicehosts", ex);
        } finally {
            DBUtils.safeClose(com);
            DBUtils.safeClose(config);
        }

    }

    /**
     * checks the registration of this instance of this service by recreating a
     * service policy based on the modified url of an inbound request
     *
     * @param messageContext
     */
    private void CheckRegistration(MessageContext messageContext) {
        if (!IsRegistered) {
            if (messageContext != null) {
                HttpServletRequest ctxg = null;
                try {
                    ctxg = (HttpServletRequest) messageContext.get(SOAPMessageContext.SERVLET_REQUEST);
                } catch (Exception ex) {
                }
                String requestURL = "";
                //     if (Utility.stringIsNullOrEmpty(requestURL)) {
                try {
                    Object j = messageContext.get("javax.xml.ws.service.endpoint.address");
                    if (j != null) {
                        requestURL = (String) j;
                    }
                } catch (Exception ex) {
                    // log.log(Level.WARN, "fgsms error getting request url " + ex.getLocalizedMessage());
                }
                //        }
                if ((Utility.stringIsNullOrEmpty(requestURL)) && ctxg != null) {
                    try {
                        StringBuffer buff = ctxg.getRequestURL();
                        requestURL = buff.toString();
                    } catch (Exception ex) {
                        //log.log(Level.WARN, "fgsms error getting request url " + ex.getLocalizedMessage());
                    }
                }
                if (Utility.stringIsNullOrEmpty(requestURL)) {
                    log.log(Level.WARN, "Unable to self register this fgsms DCS service, giving up");
                    return;
                }
                requestURL = IpAddressUtility.modifyURL(requestURL, false);
                Connection con = Utility.getConfigurationDBConnection();
                try {

                    AuxHelper.CheckPolicyAndCreate(requestURL, con, PolicyType.TRANSACTIONAL, true, AuxHelper.UNSPECIFIED, Utility.getHostName());

                    log.log(Level.INFO, "Successful self registration of this DCS at " + requestURL);
                } catch (Exception ex) {
                    log.log(Level.WARN, "Unable to self register this DCS service at " + requestURL, ex);
                } finally {
                    DBUtils.safeClose(con);
                }
                IsRegistered = true;
            } else {
                log.log(Level.INFO, "unable to self register this DCS, the context object is null.");
            }

            Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement cmd = null;
            try {
                cmd = con.prepareStatement("INSERT INTO dcsservicehosts(hostname)    VALUES (?);");
                cmd.setString(1, Utility.getHostName());
                cmd.executeUpdate();
                cmd.close();
            } catch (Exception ex) {
                log.log(Level.DEBUG, "error caught inserting dcs record into dcsservicehosts", ex);
            } finally {
                DBUtils.safeClose(cmd);
                DBUtils.safeClose(con);
            }

        }
    }

    /**
     *
     * @param addStatisticalDataRequestMsg
     * @return returns
     * org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "AddStatisticalData", action = "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddStatisticalData")
    @WebResult(name = "AddStatisticalDataResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector")
    @RequestWrapper(localName = "AddStatisticalData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalData")
    @ResponseWrapper(localName = "AddStatisticalDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataResponse")
    @SuppressWarnings("unchecked")
    public AddStatisticalDataResponseMsg addStatisticalData(
            @WebParam(name = "AddStatisticalDataRequestMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector") AddStatisticalDataRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {

        CheckRegistration(ctx.getMessageContext());

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (req == null)// Utility.stringIsNullOrEmpty(req.getURI())) {
        {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "addStatisticalData", currentUser, "null or empty request ", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is empty");
        }
        Utility.validateClassification(req.getClassification());

        if (Utility.stringIsNullOrEmpty(req.getAgentType())) {
            throw new IllegalArgumentException("A agent type must be specified");
        }
        if (Utility.stringIsNullOrEmpty(req.getBrokerURI())) {
            throw new IllegalArgumentException("A uri must be specified");
        }
        if (Utility.stringIsNullOrEmpty(req.getBrokerHostname())) {
            throw new IllegalArgumentException("A hostname must be specified");
        }

        AuditLogger.logItem(this.getClass().getCanonicalName(), "addStatisticalData", currentUser, "uri=" + req.getBrokerURI() + " " + req.getAgentType(), req.getClassification(), ctx.getMessageContext());
        UserIdentityUtil.assertAgentRole(currentUser, "addStatisticalData", req.getClassification(), ctx);

        Connection config = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        try {
            if (!agents.containsKey(req.getAgentType())) {
                agents.put(req.getAgentType(), true);
                try {
                    com = config.prepareStatement("INSERT INTO agents(agenttype)  VALUES (?)");
                    com.setString(1, req.getAgentType());
                    com.execute();
                    com.close();
                    log.log(Level.DEBUG, "DCS DEBUG adding agent type");
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "DCS DEBUG adding agent type FAILED");
                }
            }
            AuxHelper.CheckStatisticalPolicyAndCreate(req.getBrokerURI(), config, true, AuxHelper.UNSPECIFIED, req.getBrokerHostname());
        } catch (Exception ex) {
            log.log(Level.DEBUG, null, ex);
        } finally {
            DBUtils.safeClose(com);
            DBUtils.safeClose(config);
        }

        Connection con = Utility.getPerformanceDBConnection();
        boolean error = false;
        if (con == null) {
            org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException t = new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException();
            t.setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            ServiceUnavailableException e = new ServiceUnavailableException("db unavailable", t);
            throw e;
        }
        try {

            AuxHelper.TryUpdateStatus(req.isOperationalStatus(), req.getBrokerURI(), req.getOperationalStatusMessage(), true, PolicyType.STATISTICAL, "unspecified", req.getBrokerHostname());

            //this is necessary for when a queue/topic is no longer active
            com = con.prepareStatement("delete from brokerrawdata where host=?");
            com.setString(1, req.getBrokerURI());
            com.execute();
            for (int i = 0; i < req.getData().size(); i++) {
                updateBrokerData(req.getBrokerURI(), req.getData().get(i).getQueueOrTopicName(), req.getData().get(i).getQueueOrTopicCanonicalName(),
                        req.getData().get(i).getMessagesOut(), req.getData().get(i).getMessagesIn(), req.getData().get(i).getTotalConsumers(), req.getData().get(i).getActiveConsumers(),
                        req.getData().get(i).getDepth(), req.getData().get(i).getItemType(), req.getData().get(i).getBytesIn(), req.getData().get(i).getBytesOut(), req.getData().get(i).getBytesDropped(),
                        req.getData().get(i).getMessagesDropped(), con, req.getAgentType());
            }
            //Do standard SLA rules
            SLACommon.ProcessStatisticalSLARules(req, true);

        } catch (Exception ex) {
            log.log(Level.WARN, "error saving broker data", ex);
            error = true;
        } finally {
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        if (error) {
            failuresTX++;
            org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException t = new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException();
            t.setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            ServiceUnavailableException e = new ServiceUnavailableException("", t);
            throw e;
        }
        successTX += req.getData().size();
        AddStatisticalDataResponseMsg res = new AddStatisticalDataResponseMsg();
        res.setClassification(getCurrentClassificationLevel());
        res.setSuccess(true);
        return res;
    }

    private void updateBrokerData(String url, String name, String bigname,
            long MessageCount, long RecievedMessageCount, long ConsumerCount, long ActiveConsumerCount,
            long QueueDepth, String ExchangeType, long bytesin, long bytesout, long bytesdrop,
            long MessageDropCount, Connection perf, String agenttype) {

        if (!Utility.stringIsNullOrEmpty(name)) {
            PreparedStatement comm = null;
            try {
                comm = perf.prepareStatement("Delete from brokerrawdata where host=? and namecol=? and canonicalname=?; "
                        + "INSERT INTO brokerrawdata (host, utcdatetime, namecol, canonicalname, messagecount,recievedmessagecount, consumercount, "
                        + "activeconsumercount, queuedepth, typecol, agenttype, messagedropcount, bytesdropcount, bytesin, bytesout) "
                        + "VALUES (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                        + "INSERT INTO brokerhistory (host, utcdatetime, namecol,canonicalname, messagecount,recievedmessagecount, consumercount, "
                        + "activeconsumercount, queuedepth, typecol, agenttype, messagedropcount, bytesdropcount, bytesin, bytesout) "
                        + "VALUES (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                comm.setString(1, Utility.truncate(url, MAXTEXT));
                comm.setString(2, Utility.truncate(name, MAXTEXT));
                comm.setString(3, Utility.truncate(bigname, MAXTEXT));
                comm.setString(4, Utility.truncate(url, MAXTEXT));
                comm.setLong(5, System.currentTimeMillis());
                comm.setString(6, Utility.truncate(name, MAXTEXT));
                comm.setString(7, Utility.truncate(bigname, MAXTEXT));
                comm.setLong(8, MessageCount);
                comm.setLong(9, RecievedMessageCount);
                comm.setLong(10, ConsumerCount);
                comm.setLong(11, ActiveConsumerCount);
                comm.setLong(12, QueueDepth);
                comm.setString(13, Utility.truncate(ExchangeType, MAXTEXT));
                comm.setString(14, Utility.truncate(agenttype, MAXTEXT));
                comm.setLong(15, MessageDropCount);
                comm.setLong(16, bytesdrop);
                comm.setLong(17, bytesin);
                comm.setLong(18, bytesout);

                comm.setString(19, Utility.truncate(url, MAXTEXT));
                comm.setLong(20, System.currentTimeMillis());
                comm.setString(21, Utility.truncate(name, MAXTEXT));
                comm.setString(22, Utility.truncate(bigname, MAXTEXT));
                comm.setLong(23, MessageCount);
                comm.setLong(24, RecievedMessageCount);
                comm.setLong(25, ConsumerCount);
                comm.setLong(26, ActiveConsumerCount);
                comm.setLong(27, QueueDepth);
                comm.setString(28, Utility.truncate(ExchangeType, MAXTEXT));
                comm.setString(29, Utility.truncate(agenttype, MAXTEXT));
                comm.setLong(30, MessageDropCount);
                comm.setLong(31, bytesdrop);
                comm.setLong(32, bytesin);
                comm.setLong(33, bytesout);
                comm.execute();
                comm.close();
            } catch (SQLException ex) {
                log.log(Level.WARN, null, ex);
            } finally {
                DBUtils.safeClose(comm);
            }

        }

    }

    /**
     * AddMachineAndProcessData, adds a set of performance records from a set of
     * monitored processes from a given machine
     *
     * @param req
     * @param addMachineAndProcessDataRequestMsg
     * @return returns
     * org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessDataResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "AddMachineAndProcessData", action = "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddMachineAndProcessData")
    @WebResult(name = "AddMachineAndProcessDataResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector")
    @RequestWrapper(localName = "AddMachineAndProcessData", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessData")
    @ResponseWrapper(localName = "AddMachineAndProcessDataResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", className = "org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessDataResponse")
    @SuppressWarnings("unchecked")
    public AddMachineAndProcessDataResponseMsg addMachineAndProcessData(
            @WebParam(name = "AddMachineAndProcessDataRequestMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector") AddMachineAndProcessDataRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {

        CheckRegistration(ctx.getMessageContext());

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (req == null)// Utility.stringIsNullOrEmpty(req.getURI())) {
        {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "addMachineAndProcessData", currentUser, "null or empty request ", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is empty");
        }
        Utility.validateClassification(req.getClassification());
        if (Utility.stringIsNullOrEmpty(req.getHostname())) {
            throw new IllegalArgumentException("A hostname must be specified");
        }
        if (Utility.stringIsNullOrEmpty(req.getDomainname())) {
            throw new IllegalArgumentException("A domain name or 'unspecified' must be specified");
        }

        AuditLogger.logItem(this.getClass().getCanonicalName(), "addMachineAndProcessData", currentUser, "uri=" + req.getHostname(), req.getClassification(), ctx.getMessageContext());
        UserIdentityUtil.assertAgentRole(currentUser, "addMachineAndProcessData", req.getClassification(), ctx);

        if (req.getMachineData() == null) {
            throw new IllegalArgumentException("The element MachineData must be specified and contain at least the current status information");
        }
        if (Utility.stringIsNullOrEmpty(req.getMachineData().getUri())) {
            throw new IllegalArgumentException("The element MachineData must specify a uri representing the system, typically in the format urn:hostname:system");
        }

        Connection con = null;
        long now = System.currentTimeMillis();
        UUID id = UUID.randomUUID();
        boolean ok = true;
        PreparedStatement com = null;
        try {
            con = Utility.getPerformanceDBConnection();

            //machine level data
            com = con.prepareStatement("INSERT INTO rawdatamachineprocess "
                    + "(uri, memoryused, percentcpu, id, utcdatetime, threads, startedat)    "
                    + "VALUES (?, ?, ?, ?,     ? ,?, ?);");
            com.setString(1, Utility.truncate(req.getMachineData().getUri(), MAXTEXT));
            if (req.getMachineData().getBytesusedMemory() == null) {
                com.setNull(2, java.sql.Types.BIGINT);
            } else {
                com.setLong(2, req.getMachineData().getBytesusedMemory());
            }
            /*
             * if (req.getMachineData().getKilobytespersecondNetwork() == null)
             * { com.setNull(3, java.sql.Types.BIGINT); } else { com.setLong(3,
             * req.getMachineData().getKilobytespersecondNetwork().longValue());
             * } if (req.getMachineData().getKilobytespersecondDisk() == null) {
             * com.setNull(4, java.sql.Types.BIGINT); } else { com.setLong(4,
             * req.getMachineData().getKilobytespersecondDisk().longValue()); }
             */
            if (req.getMachineData().getPercentusedCPU() == null) {
                com.setNull(3, java.sql.Types.INTEGER);
            } else {
                com.setLong(3, req.getMachineData().getPercentusedCPU().intValue());
            }

            req.getMachineData().setId(id.toString());
            com.setString(4, id.toString());
            if (req.getMachineData().getTimestamp() == null) {
                com.setLong(5, now);
            } else {
                com.setLong(5, req.getMachineData().getTimestamp().getTimeInMillis());
            }
            if (req.getMachineData().getNumberofActiveThreads() == null) {
                com.setNull(6, java.sql.Types.BIGINT);
            } else {
                com.setLong(6, req.getMachineData().getNumberofActiveThreads());
            }

            if (req.getMachineData().getStartedAt() == null) {
                com.setNull(7, java.sql.Types.BIGINT);
            } else {
                com.setLong(7, req.getMachineData().getStartedAt().getTimeInMillis());
            }

            com.executeUpdate();
        } catch (Exception ex) {
            log.log(Level.ERROR, "DCS unable to save machine data " + ex.getMessage());
            log.log(Level.DEBUG, "DCS unable to save machine data " + ex.getMessage(), ex);
        } finally {
            DBUtils.safeClose(com);
        }

        //process level data, if any
        for (int i = 0; i < req.getProcessData().size(); i++) {
            try {
                com = con.prepareStatement("INSERT INTO rawdatamachineprocess "
                        + "(uri, memoryused, percentcpu, id, utcdatetime, threads, openfiles, startedat)    "
                        + "VALUES (?, ?, ?, ?,     ?, ?, ?, ?);");
                com.setString(1, Utility.truncate(req.getProcessData().get(i).getUri(), MAXTEXT));
                if (req.getProcessData().get(i).getBytesusedMemory() == null) {
                    com.setNull(2, java.sql.Types.BIGINT);
                } else {
                    com.setLong(2, req.getProcessData().get(i).getBytesusedMemory().longValue());
                }

                if (req.getProcessData().get(i).getPercentusedCPU() == null) {
                    com.setNull(3, java.sql.Types.INTEGER);
                } else {
                    com.setLong(3, req.getProcessData().get(i).getPercentusedCPU().intValue());
                }

                id = UUID.randomUUID();
                req.getProcessData().get(i).setId(id.toString());
                com.setString(4, id.toString());
                if (req.getProcessData().get(i).getTimestamp() == null) {
                    com.setLong(5, now);
                } else {
                    com.setLong(5, req.getProcessData().get(i).getTimestamp().getTimeInMillis());
                }
                if (req.getProcessData().get(i).getNumberofActiveThreads() == null) {
                    com.setNull(6, java.sql.Types.BIGINT);
                } else {
                    com.setLong(6, req.getProcessData().get(i).getNumberofActiveThreads());
                }
                //  com.setLong(6, req.getProcessData().get(i).getNumberofActiveThreads());
                com.setLong(7, req.getProcessData().get(i).getOpenFileHandles());

                if (req.getProcessData().get(i).getStartedAt() == null) {
                    com.setNull(8, java.sql.Types.BIGINT);
                } else {
                    com.setLong(8, now);
                }
                com.executeUpdate();
            } catch (Exception ex) {
                log.log(Level.ERROR, "DCS unable to save process data " + ex.getMessage());
                log.log(Level.DEBUG, "DCS unable to save process data " + ex.getMessage(), ex);
                failuresTX++;
            } finally {
                DBUtils.safeClose(com);
            }
        }

        //do network information
        if (!req.getMachineData().getNetworkAdapterPerformanceData().isEmpty()) {
            for (int i = 0; i < req.getMachineData().getNetworkAdapterPerformanceData().size(); i++) {
                try {
                    com = con.prepareStatement("INSERT INTO rawdatanic"
                            + "(nicid, hostname,domainname, utcdatetime, sendkbs, receivekbs, id, uri)    "
                            + "VALUES (?, ?, ?, ?, ?,?, ?, ?);");
                    com.setString(1, Utility.truncate(req.getMachineData().getNetworkAdapterPerformanceData().get(i).getAdapterName(), MAXTEXT));
                    com.setString(2, Utility.truncate(req.getHostname(), MAXTEXT));
                    com.setString(3, Utility.truncate(req.getDomainname(), MAXTEXT));

                    com.setLong(4, now);

                    com.setLong(5, req.getMachineData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkTransmit());
                    com.setLong(6, req.getMachineData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkReceive());

                    com.setString(7, UUID.randomUUID().toString());
                    com.setString(8, req.getMachineData().getUri());

                    com.executeUpdate();
                } catch (Exception ex) {
                    log.log(Level.ERROR, "DCS unable to save machine/nic data " + ex.getMessage());
                    log.log(Level.DEBUG, "DCS unable to save machine/nicdata " + ex.getMessage(), ex);
                    failuresTX++;
                } finally {
                    DBUtils.safeClose(com);
                }
            }
        }
        //@since 6.3
        if (req.getSensorData() != null) {
            for (int i = 0; i < req.getSensorData().getItems().size(); i++) {
                try {
                    com = con.prepareStatement("INSERT INTO "
                            + "rawdatamachinesensor (uri, id, utcdatetime, dataname, datavalue)"
                            + "    VALUES (?, ?, ?, ?, ?);");
                    com.setString(1, Utility.truncate(req.getMachineData().getUri(), MAXTEXT));
                    com.setString(2, UUID.randomUUID().toString());
                    com.setLong(3, now);
                    com.setString(4, Utility.truncate(req.getSensorData().getItems().get(i).getName(), MAXTEXT));
                    com.setString(5, Utility.truncate(req.getSensorData().getItems().get(i).getValue(), MAXTEXT));
                    com.executeUpdate();
                } catch (Exception ex) {
                    log.log(Level.ERROR, "DCS unable to save machine/sensor data " + ex.getMessage());
                    log.log(Level.DEBUG, "DCS unable to save machine/sensor data " + ex.getMessage(), ex);
                    failuresTX++;
                } finally {
                    DBUtils.safeClose(com);
                }

            }
        }

        //do drive information
        if (!req.getMachineData().getDriveInformation().isEmpty()) {
            for (int i = 0; i < req.getMachineData().getDriveInformation().size(); i++) {
                try {
                    com = con.prepareStatement("INSERT INTO "
                            + "rawdatadrives(hostname,driveidentifier, freespace, utcdatetime, id,writekbs, readkbs, deviceidentifier, status, statusmsg, domainname, uri)"
                            + "    VALUES (?, ?, ?, ?, ?, ?,  ?, ?, ? ,?,?, ?);");
                    com.setString(1, Utility.truncate(req.getHostname(), MAXTEXT));
                    com.setString(2, Utility.truncate(req.getMachineData().getDriveInformation().get(i).getPartition(), MAXTEXT));
                    if (req.getMachineData().getDriveInformation().get(i).getFreespace() != null) {
                        com.setLong(3, req.getMachineData().getDriveInformation().get(i).getFreespace());
                    } else {
                        com.setLong(3, -1);
                    }
                    com.setLong(4, now);

                    req.getMachineData().getDriveInformation().get(i).setId(UUID.randomUUID().toString());
                    // com.setString(5, req.getMachineData().getDriveInformation().get(i).getId());

                    id = UUID.randomUUID();
                    req.getMachineData().setId(id.toString());
                    com.setString(5, id.toString());
                    if (req.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskWrite() != null) {
                        com.setLong(6, req.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskWrite());
                    } else {
                        com.setNull(6, java.sql.Types.BIGINT);
                    }
                    if (req.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskRead() != null) {
                        com.setLong(7, req.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskRead());
                    } else {
                        com.setNull(7, java.sql.Types.BIGINT);
                    }
                    com.setString(8, Utility.truncate(req.getMachineData().getDriveInformation().get(i).getSystemid(), MAXTEXT));
                    com.setBoolean(9, req.getMachineData().getDriveInformation().get(i).isOperational());
                    com.setString(10, Utility.truncate(req.getMachineData().getDriveInformation().get(i).getOperationalstatus(), MAXTEXT));
                    com.setString(11, Utility.truncate(req.getDomainname(), MAXTEXT));
                    com.setString(12, Utility.truncate(req.getMachineData().getUri(), MAXTEXT));
                    com.executeUpdate();
                } catch (Exception ex) {
                    log.log(Level.ERROR, "DCS error saving drive information! " + ex.getMessage());
                    log.log(Level.DEBUG, "DCS error saving drive information! " + ex.getMessage(), ex);
                    failuresTX++;
                } finally {
                    DBUtils.safeClose(com);
                }
            }
        }

        if (!agents.containsKey(req.getAgentType())) {
            agents.put(req.getAgentType(), true);
            Connection config = Utility.getConfigurationDBConnection();
            try {

                com = config.prepareStatement("INSERT INTO agents(agenttype)  VALUES (?)");
                com.setString(1, Utility.truncate(req.getAgentType(), MAXTEXT));
                com.execute();

                log.log(Level.DEBUG, "DCS DEBUG adding agent type");
            } catch (Exception ex) {
                log.log(Level.DEBUG, "DCS DEBUG adding agent type FAILED " + ex.getMessage());

            } finally {
                DBUtils.safeClose(com);
                DBUtils.safeClose(config);
            }
        }

        DBUtils.safeClose(con);

        if (!ok) {
            org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException t = new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException();
            t.setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            ServiceUnavailableException e = new ServiceUnavailableException("db unavailable", t);
            throw e;
        }

        AddMachineAndProcessDataResponseMsg res = new AddMachineAndProcessDataResponseMsg();

        res.setClassification(getCurrentClassificationLevel());
        //does all SLA alerts, change in status, policy create for everything BUT time based Rules
        try {
            MachinePolicy mp = SLACommon.ProcessMachineLevelSLAs(req, true);
            List<ProcessPolicy> pp = SLACommon.ProcessProcessLevelSLAs(req, true);
            res.setMachinePolicy(mp);
            res.getProcessPolicy().addAll(pp);
        } catch (Exception ex) {
            log.log(Level.ERROR, "error caught processing loading machine and process policies", ex);
        }
        
        if (res.getMachinePolicy()==null){
            //strange case, the os agent started (and is a new instance)
            //before the fgsms server was fullly up and running, so the initial
            //get policy message was never processed and thus the policy was never
            //created.
            //unfortunately making a new one here will cause some dependency spagetthi
            //so we'll just leave it as is and adjust the travis script to wait a bit
        }

        KeyNameValueEnc prop = DBSettingsLoader.GetPropertiesFromDB(true, "Agents.Process", "ReportingFrequency");
        if (prop == null || prop.getKeyNameValue() == null) {
            res.setReportingFrequency(df.newDuration(30000));
        } else {
            try {
                res.setReportingFrequency(df.newDuration(Long.parseLong(prop.getKeyNameValue().getPropertyValue())));
            } catch (Exception ex) {
                res.setReportingFrequency(df.newDuration(30000));
            }
        }
        successTX += req.getProcessData().size() + 1;
        return res;

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
        res.setDataSentSuccessfully(successTX);
        res.setDataNotSentSuccessfully(failuresTX);
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
        successTX++;
        return res;
    }
}
