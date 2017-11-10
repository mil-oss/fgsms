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
package org.miloss.fgsms.services.status.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage.VersionInfo;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import org.miloss.fgsms.services.interfaces.status.*;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.DBUtils;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * fgsms's Status Service, gets/sets the status of any arbitrary service
 *
 * @author AO
 * @since v3.0
 */
//@javax.ejb.Stateless 


@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED, use = SOAPBinding.Use.LITERAL)
@WebService(name = "statusService", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status"
//    , wsdlLocation = "WEB-INF/wsdl/Statusv6.wsdl"
)
public class StatusServiceImpl implements org.miloss.fgsms.services.interfaces.status.StatusService, org.miloss.fgsms.services.interfaces.status.OpStatusService {

    private static final Logger log = Logger.getLogger("fgsms.StatusService");
    private static DatatypeFactory df = null;
    private static Calendar started = null;
    @Resource
    private WebServiceContext ctx;

    public StatusServiceImpl() throws DatatypeConfigurationException {
        InitDF();
    }

    private synchronized void InitDF() throws DatatypeConfigurationException {
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
     * constructor used for unit testing, do not remove
     *
     * @param w
     */
    protected StatusServiceImpl(WebServiceContext w) throws DatatypeConfigurationException {
        InitDF();
        ctx = w;
    }

    /**
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.status.SetStatusResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "SetStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/SetStatus")
    @WebResult(name = "SetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "SetStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetStatus")
    @ResponseWrapper(localName = "SetStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetStatusResponse")
    public SetStatusResponseMsg setStatus(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status") SetStatusRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {

        if (req == null) {
            throw new IllegalArgumentException("request");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setStatus", currentUser, "", (req.getClassification()), ctx.getMessageContext());
        Utility.validateClassification(req.getClassification());

        if (req.getTimeStamp() == null || Utility.stringIsNullOrEmpty(req.getURI())) {
            throw new IllegalArgumentException("request parameter uri or timestamp is null");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setStatus", currentUser, req.getURI(), (req.getClassification()), ctx.getMessageContext());

        //authorize request
        UserIdentityUtil.assertAgentRole(currentUser, "setStatus", req.getClassification(), ctx);

        PolicyType pt = PolicyType.STATUS;
        if (req.getPolicyType() != null) {
            pt = req.getPolicyType();
        }

        //CheckPolicyAndCreate(currentUser, req.getURI());
        return setStatusInternal(req.getURI(), pt, req.getDomainName(), req.getHostname(), req.getParentObject(), req.getTimeStamp(), req.getMessage(), req.isOperational());
    }

    private SetStatusResponseMsg setStatusInternal(String uri, PolicyType pt, String domain, String hostname, String parentObject, Calendar timestamp, String message, boolean operational) {
        Connection con = Utility.getConfigurationDBConnection();
        Connection perf = Utility.getPerformanceDBConnection();
        try {

            AuxHelper.CheckPolicyAndCreate(uri, con, pt, true, domain, hostname, parentObject);

            String oldstatusmsg = "";
            boolean oldstatus = true;
            boolean firstseenstatus = true;
            PreparedStatement com = perf.prepareStatement("select * from availability where uri=? order by utcdatetime desc limit 1;");
            com.setString(1, uri);
            ResultSet availset = com.executeQuery();
            if (availset.next()) {
                oldstatus = (availset.getBoolean("status"));
                oldstatusmsg = availset.getString("message");
                if (Utility.stringIsNullOrEmpty(oldstatusmsg)) {
                    oldstatusmsg = "";
                }
                //    oldstatusat = availset.getLong("utcdatetime");

            }
            availset.close();
            com.close();
            long usetime = System.currentTimeMillis();
            if (timestamp != null) {
                try {
                    usetime = timestamp.getTimeInMillis();
                } catch (Exception ex) {
                }
            }
            com = con.prepareStatement("select * from status  where uri=?;");
            com.setString(1, uri);
            availset = com.executeQuery();
            if (availset.next()) {
                firstseenstatus = false;
            }
            availset.close();
            com.close();

            if (firstseenstatus) {
                //insert in status
                PreparedStatement state = con.prepareStatement("INSERT INTO status(uri, utcdatetime, message, status, monitored) VALUES (?, ?, ?, ?, ?);");
                state.setString(1, uri);
                state.setLong(2, usetime);
                state.setString(3, message);
                state.setBoolean(4, operational);
                state.setBoolean(5, false);
                state.executeUpdate();
                state.close();
                //insert current into perf availity
                state = perf.prepareStatement("INSERT INTO availability(uri, utcdatetime, id, message, status)   VALUES (?, ?, ?, ?, ?);");
                state.setString(1, uri);
                state.setLong(2, usetime);
                state.setString(3, UUID.randomUUID().toString());
                state.setString(4, message);
                state.setBoolean(5, operational);
                state.executeUpdate();
                state.close();

            } else if (operational != oldstatus || (!operational && !oldstatusmsg.equalsIgnoreCase(message))) {
                //if (currenstatus != oldstatus || !oldstatusmsg.equalsIgnoreCase(s)) {
                //something happened, let's record it

                //update current in config status table
                PreparedStatement state = con.prepareStatement("UPDATE status set utcdatetime=?, message=?, status=? where uri=?;");
                state.setLong(1, usetime);
                state.setString(2, message);
                state.setBoolean(3, operational);
                state.setString(4, uri);
                state.executeUpdate();
                state.close();

                //insert current into perf availity
                state = perf.prepareStatement("INSERT INTO availability(uri, utcdatetime, id, message, status)   VALUES (?, ?, ?, ?, ?);");
                state.setString(1, uri);
                state.setLong(2, usetime);
                state.setString(3, UUID.randomUUID().toString());
                state.setString(4, message);
                state.setBoolean(5, operational);
                state.executeUpdate();
                state.close();
                if (oldstatus != operational) {
                    log.log(Level.INFO, "Trigger status change SLAs");
                    SLACommon.TriggerStatusChange(uri, oldstatus, oldstatusmsg, operational, message, true);
                }
            } else {
                //no change in status, just update the now timestamp to keep the ui happy
                //update current in config status table
                PreparedStatement state = con.prepareStatement("UPDATE status   SET utcdatetime=?, message=? WHERE uri=?;");

                state.setLong(1, usetime);
                state.setString(2, message);
                state.setString(3, uri);
                state.executeUpdate();
                state.close();
            }

            con.close();

            perf.close();
            SetStatusResponseMsg x = new SetStatusResponseMsg();

            x.setStatus(SetResponseStatus.SUCCESS);

            return x;
        } catch (SQLException ex) {
            try {
                perf.close();
            } catch (Exception x) {
            }
            try {
                con.close();
            } catch (Exception x) {
            }

            log.log(Level.ERROR, "Error querying database for setStatus", ex);
            SetStatusResponseMsg x = new SetStatusResponseMsg();
            x.setStatus(SetResponseStatus.FAILURE);
            return x;
        } catch (Exception ex) {
            try {
                perf.close();
            } catch (Exception x) {
            }
            try {
                con.close();
            } catch (Exception x) {
            }

            log.log(Level.ERROR, "Unexpected rrror querying database for setStatus", ex);
            SetStatusResponseMsg x = new SetStatusResponseMsg();
            x.setStatus(SetResponseStatus.FAILURE);
            return x;
        }
    }

    /**
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/GetStatus")
    @WebResult(name = "GetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "GetStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetStatus")
    @ResponseWrapper(localName = "GetStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetStatusResponse")
    public GetStatusResponseMsg getStatus(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status") GetStatusRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {
        if (req == null) {
            throw new IllegalArgumentException("null message body");
        }

        Utility.validateClassification(req.getClassification());

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getStatus", currentUser, "", (req.getClassification()), ctx.getMessageContext());

        UserIdentityUtil.assertReadAccess(req.getURI(), currentUser, "getStatus", req.getClassification(), ctx);
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            GetStatusResponseMsg ret = null;

            com = con.prepareStatement("select * from status where uri=? order by uri asc;");
            com.setString(1, req.getURI());
            rs = com.executeQuery();
            int records = 0;
            while (rs.next()) {
                GetStatusResponseMsg t = new GetStatusResponseMsg();
                t.setURI(rs.getString("uri"));
                t.setOperational(rs.getBoolean("status"));
                t.setMessage(rs.getString("message"));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getLong("utcdatetime"));
                t.setTimeStamp((cal));
                t.setLastStatusChangeTimeStamp(GetLastChangeTimeStamp(req.getURI()));
                ret = t;
                records++;
            }
            if (records > 1) {
                log.log(Level.ERROR, "multiple records for uri " + req.getURI() + " detected. This is unexpected");
                throw new NullPointerException("Internal error, see server logs for reason.");
            }
            if (ret == null) {
                ret = new GetStatusResponseMsg();
                ret.setMessage("Status Unknown");
                ret.setOperational(false);
            }
            ret.setTimeStamp(Calendar.getInstance());
            ret.setLastStatusChangeTimeStamp(GetLastChangeTimeStamp(req.getURI()));
            ret.setClassification(getCurrentClassificationLevel());
            return ret;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error querying database", ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw code;
        } catch (NullPointerException ex) {
            log.log(Level.ERROR, "Error querying database for setStatus", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
    }

    /**
     *
     * @param req
     * @return returns
     * java.util.List<org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg>
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAllStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/GetAllStatus")
    @WebResult(name = "GetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "GetAllStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetAllStatus")
    @ResponseWrapper(localName = "GetAllStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetAllStatusResponse")
    public List<GetStatusResponseMsg> getAllStatus(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status") GetStatusRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {
        //authorize request?
        Utility.validateClassification(req.getClassification());

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAllStatus", currentUser, "", (req.getClassification()), ctx.getMessageContext());
        Connection con = Utility.getConfigurationDBConnection();
         PreparedStatement com = null;
         ResultSet rs=null;
        try {
            List<GetStatusResponseMsg> ret = new ArrayList<GetStatusResponseMsg>();
            //DatatypeFactory fac = DatatypeFactory.newInstance();

           
            if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "getAllStatus", req.getClassification(), ctx)) {
                com = con.prepareStatement("select * from status;");
            } else {
                com = con.prepareStatement("select ObjectURI as URI from UserPermissions, status "
                        + "where status.uri=objecturi and username=? and (ReadObject=true or WriteObject=true or AdministerObject=true or AuditObject=true)  order by URI asc;");
                com.setString(1, currentUser);
            }
            SecurityWrapper currentlevel = getCurrentClassificationLevel();

            rs = com.executeQuery();
            while (rs.next()) {
                GetStatusResponseMsg t = new GetStatusResponseMsg();
                t.setURI(rs.getString("uri"));
                t.setOperational(rs.getBoolean("status"));
                t.setMessage(rs.getString("message"));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getLong("utcdatetime"));
                t.setTimeStamp((cal));
                t.setClassification(currentlevel);
                t.setLastStatusChangeTimeStamp(GetLastChangeTimeStamp(rs.getString("uri")));
                ret.add(t);
            }
            
            return ret;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error querying database", ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error querying database", ex);
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
     * Removes an entry from the status listing, all associated history, SLAs,
     * and alerting subscriptions requires global admin rights
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.status.RemoveStatusResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "RemoveStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/RemoveStatus")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "RemoveStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.RemoveStatus")
    @ResponseWrapper(localName = "RemoveStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.RemoveStatusResponse")
    public RemoveStatusResponseMsg removeStatus(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status") RemoveStatusRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {
        if (req == null) {
            throw new IllegalArgumentException("empty message body");
        }
        Utility.validateClassification(req.getClassification());

        if (req.getURI().isEmpty()) {
            throw new IllegalArgumentException("at least one url must be specified");
        }

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "removeStatus", currentUser, Utility.listStringtoString(req.getURI()), req.getClassification(), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "removeStatus", req.getClassification(), ctx);
        Connection con = Utility.getConfigurationDBConnection();
        Connection perf = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        try {

            for (int i = 0; i < req.getURI().size(); i++) {
                com = con.prepareStatement("delete from status where uri=?; delete from servicepolicies where uri=?; delete from userpermissions where ObjectURI=?;");
                com.setString(1, req.getURI().get(i));
                com.setString(2, req.getURI().get(i));
                com.setString(3, req.getURI().get(i));
                com.executeUpdate();
                com.close();

                com = perf.prepareStatement("delete from availability where uri=?;");
                com.setString(1, req.getURI().get(i));
                com.executeUpdate();
                com.close();
            }

            RemoveStatusResponseMsg ret = new RemoveStatusResponseMsg();
            ret.setClassification(getCurrentClassificationLevel());

            return ret;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error querying database", ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw code;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error querying database", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {
            DBUtils.safeClose(con);
            DBUtils.safeClose(perf);
        }

    }

    private SecurityWrapper getCurrentClassificationLevel() throws ServiceUnavailableException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            SecurityWrapper ret = new SecurityWrapper();

            comm = con.prepareStatement("Select classification, caveat from GlobalPolicies;");
            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            results = comm.executeQuery();

            try {
                if (results.next()) {
                    ret.setClassification(ClassificationType.fromValue(results.getString("classification")));
                    ret.setCaveats(results.getString("caveat"));
                    results.close();
                    comm.close();
                    con.close();
                    return ret;
                }
            } catch (Exception ex) {
                log.log(Level.ERROR, "error getting global policy", ex);
            } finally {
                DBUtils.safeClose(results);
                DBUtils.safeClose(comm);
            }

            try {
                comm = con.prepareStatement("INSERT INTO GlobalPolicies (PolicyRefreshRate, RecordedMessageCap, classification, caveat) " + " VALUES (?, ?, ?, ?);");
                comm.setLong(1, 3 * 60 * 100);
                comm.setLong(2, 1024000);
                comm.setString(3, "U");
                comm.setString(4, "FOUO");
                comm.execute();
            } catch (Exception ex) {
                log.log(Level.ERROR, "error setting global policy", ex);
            } finally {
                DBUtils.safeClose(comm);
            }

            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error setting global policy (no records currently exist", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(con);
        }
    }

    /**
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.status.SetStatusResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "SetMoreStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/SetMoreStatus")
    @WebResult(name = "SetStatusMoreResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "SetMoreStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetMoreStatus")
    @ResponseWrapper(localName = "SetMoreStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetMoreStatusResponse")
    public SetStatusResponseMsg setMoreStatus(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status") List<SetStatusRequestMsg> reqs)
            throws AccessDeniedException, ServiceUnavailableException {
        if (reqs == null) {
            throw new IllegalArgumentException("empty message body");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (reqs == null || reqs.isEmpty() || reqs.get(0) == null) {
            throw new IllegalArgumentException("at least one item must be specified");
        }
        Utility.validateClassification(reqs.get(0).getClassification());

        UserIdentityUtil.assertAgentRole(currentUser, "setMoreStatus", reqs.get(0).getClassification(), ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setMoreStatus", currentUser, "", (reqs.get(0).getClassification()), ctx.getMessageContext());

        Connection con = Utility.getConfigurationDBConnection();
        Connection perf = Utility.getPerformanceDBConnection();
        for (int i = 0; i < reqs.size(); i++) {
            SetStatusRequestMsg req = reqs.get(i);
            if (req == null) {
                throw new IllegalArgumentException("request item " + i + " is null");
            }
            if (req.getTimeStamp() == null || Utility.stringIsNullOrEmpty(req.getURI())) {
                throw new IllegalArgumentException("request parameter uri or timestamp");
            }
            PolicyType pt = PolicyType.STATUS;
            if (req.getPolicyType() != null) {
                pt = req.getPolicyType();
            }

            AuxHelper.CheckPolicyAndCreate(req.getURI(), con, pt, true, req.getDomainName(), req.getHostname(), req.getParentObject());
            //CheckPolicyAndCreate(currentUser, req.getURI());

            try {
                String oldstatusmsg = "";
                boolean oldstatus = true;
                boolean firstseenstatus = true;
                PreparedStatement com = perf.prepareStatement("select * from availability where uri=? order by utcdatetime desc limit 1;");
                com.setString(1, req.getURI());
                ResultSet availset = com.executeQuery();
                if (availset.next()) {
                    oldstatus = (availset.getBoolean("status"));
                    oldstatusmsg = availset.getString("message");
                }
                availset.close();
                com.close();
                long usetime = System.currentTimeMillis();
                if (req.getTimeStamp() != null) {
                    try {
                        usetime = req.getTimeStamp().getTimeInMillis();
                    } catch (Exception ex) {
                    }
                }
                com = con.prepareStatement("select * from status  where uri=?;");
                com.setString(1, req.getURI());
                availset = com.executeQuery();
                if (availset.next()) {
                    firstseenstatus = false;
                }
                availset.close();
                com.close();
                if (firstseenstatus) {
                    //insert in status
                    PreparedStatement state = con.prepareStatement("INSERT INTO status(uri, utcdatetime, message, status, monitored) VALUES (?, ?, ?, ?, ?);");
                    state.setString(1, req.getURI());
                    state.setLong(2, usetime);
                    state.setString(3, req.getMessage());
                    state.setBoolean(4, req.isOperational());
                    state.setBoolean(5, false);
                    state.executeUpdate();
                    state.close();
                    //insert current into perf availity
                    state = perf.prepareStatement("INSERT INTO availability(uri, utcdatetime, id, message, status)   VALUES (?, ?, ?, ?, ?);");
                    state.setString(1, req.getURI());
                    state.setLong(2, usetime);
                    state.setString(3, UUID.randomUUID().toString());
                    state.setString(4, req.getMessage());
                    state.setBoolean(5, req.isOperational());
                    state.executeUpdate();
                    state.close();

                } else if (req.isOperational() != oldstatus || (!req.isOperational() && !oldstatusmsg.equalsIgnoreCase(req.getMessage()))) {
                    //update current in config status table
                    PreparedStatement state = con.prepareStatement("UPDATE status set utcdatetime=?, message=?, status=? where uri=?;");
                    state.setLong(1, usetime);
                    state.setString(2, req.getMessage());
                    state.setBoolean(3, req.isOperational());
                    state.setString(4, req.getURI());
                    state.executeUpdate();
                    state.close();

                    //insert current into perf availity
                    state = perf.prepareStatement("INSERT INTO availability(uri, utcdatetime, id, message, status)   VALUES (?, ?, ?, ?, ?);");
                    state.setString(1, req.getURI());
                    state.setLong(2, usetime);
                    state.setString(3, UUID.randomUUID().toString());
                    state.setString(4, req.getMessage());
                    state.setBoolean(5, req.isOperational());
                    state.executeUpdate();
                    state.close();
                    if (oldstatus != req.isOperational()) {
                        log.log(Level.INFO, "Trigger status change SLAs");
                        SLACommon.TriggerStatusChange(req.getURI(), oldstatus, oldstatusmsg, req.isOperational(), req.getMessage(), true);
                    }
                } else {
                    //no change in status, just update the now timestamp to keep the ui happy
                    //update current in config status table
                    PreparedStatement state = con.prepareStatement("UPDATE status   SET utcdatetime=?, message=? WHERE uri=?;");

                    state.setLong(1, usetime);
                    state.setString(2, req.getMessage());
                    state.setString(3, req.getURI());
                    state.executeUpdate();
                    state.close();
                }

            } catch (SQLException ex) {
                log.log(Level.ERROR, "Error querying database for setStatus", ex);
                SetStatusResponseMsg x = new SetStatusResponseMsg();
                x.setStatus(SetResponseStatus.FAILURE);
                try {
                    con.close();
                    perf.close();
                } catch (Exception e) {
                }
                return x;
            }

        }
        DBUtils.safeClose(con);
        DBUtils.safeClose(perf);
        SetStatusResponseMsg x = new SetStatusResponseMsg();
        x.setStatus(SetResponseStatus.SUCCESS);
        return x;
    }

    /**
     * returns the last time the status changes, if it has never been recorded,
     * null is returned
     *
     * @param uri
     * @return
     */
    private Calendar GetLastChangeTimeStamp(String uri) {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            Calendar cal = null;

            com = con.prepareStatement("select * from availability where uri=? order by utcdatetime desc limit 1");
            com.setString(1, uri);
            //         Duration d=null;
            rs = com.executeQuery();
            if (rs.next()) {
                long changeat = rs.getLong("utcdatetime");
                //DatatypeFactory f = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(changeat);
                cal = (gcal);
            }

            if (cal == null) {
                cal = Calendar.getInstance();
            }
            return cal;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return null;
    }

    /**
     * Get the operating status of this service
     *
     * @param versionInfo
     * @param dataSentSuccessfully
     * @param status
     * @param classification
     * @param startedAt
     * @param dataNotSentSuccessfully
     */
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
        if (request == null) {
            throw new IllegalArgumentException("empty message body");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getOperatingStatus", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        GetOperatingStatusResponseMessage res = new GetOperatingStatusResponseMessage();

        res.setClassification(request.getClassification());
        res.setVersionInfo(new VersionInfo());
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

            prepareStatement2 = con2.prepareStatement("select 1=1;");
            prepareStatement2.execute();

            res.setStatusMessage("OK");
        } catch (Exception ex) {
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

    /**
     * Set the Extended Status on a single item. If a policy does not exist for
     * this item, it will be automatically created
     *
     * @param req
     * @return returns
     * org.miloss.fgsms.services.interfaces.status.SetStatusResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "SetExtendedStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/SetExtendedStatus")
    @WebResult(name = "SetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "SetExtendedStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetExtendedStatus")
    @ResponseWrapper(localName = "SetStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetStatusResponse")
    public SetStatusResponseMsg setExtendedStatus(
            @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status") SetExtendedStatusRequestMsg req)
            throws AccessDeniedException, ServiceUnavailableException {
        //TODO write this
        if (req == null) {
            throw new IllegalArgumentException("request");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setStatus", currentUser, "", (req.getClassification()), ctx.getMessageContext());
        Utility.validateClassification(req.getClassification());

        if (req.getTimeStamp() == null || Utility.stringIsNullOrEmpty(req.getURI())) {
            throw new IllegalArgumentException("request parameter uri or timestamp is null");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setStatus", currentUser, req.getURI(), (req.getClassification()), ctx.getMessageContext());

        //authorize request
        UserIdentityUtil.assertAgentRole(currentUser, "setStatus", req.getClassification(), ctx);

        PolicyType pt = PolicyType.STATUS;
        if (req.getPolicyType() != null) {
            pt = req.getPolicyType();
        }
        SetStatusResponseMsg setStatusInternal = this.setStatusInternal(req.getURI(), pt, req.getDomainName(), req.getHostname(), req.getParentObject(), req.getTimeStamp(), req.getMessage(), req.isOperational());
        if (setStatusInternal.getStatus() == SetResponseStatus.SUCCESS) {
            //save the extended status info
            if (req.getData() != null) {
                Connection con = Utility.getPerformanceDBConnection();
                PreparedStatement cmd = null;
                try {
                    for (int i = 0; i < req.getData().getItems().size(); i++) {
                        if (!Utility.stringIsNullOrEmpty(req.getData().getItems().get(i).getName()) && !Utility.stringIsNullOrEmpty(req.getData().getItems().get(i).getValue())) {

                            try {
                                cmd = con.prepareStatement("INSERT INTO statusext(uri, utcdatetime, dataname, datavalue)    VALUES (?, ?, ?, ?);");
                                cmd.setString(1, req.getURI());
                                cmd.setLong(2, req.getTimeStamp().getTimeInMillis());
                                cmd.setString(3, req.getData().getItems().get(i).getName());
                                cmd.setString(4, req.getData().getItems().get(i).getValue());
                            } catch (Exception ex) {
                                log.log(Level.ERROR, "unable to save extended status data", ex);
                            } finally {
                                DBUtils.safeClose(cmd);
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.log(Level.ERROR, "unable to save extended status data", ex);
                } finally {
                    DBUtils.safeClose(cmd);
                    DBUtils.safeClose(con);
                }

            }
        }

        return setStatusInternal;

    }
}
