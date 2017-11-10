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
package org.miloss.fgsms.services.pcs.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.PluginCommon;
import org.miloss.fgsms.plugins.federation.FederationInterface;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.*;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.sla.actions.EmailAlerter;
import org.miloss.fgsms.sla.actions.SLAActionRestart;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * fgsms's Policy Configuration Service This web service provides one of the
 * core functions of fgsms, managing service policies, access control and
 * general configuration elements of the agents and fgsms itself. All methods
 * require a classification level and process access control rules accept for
 * GetGlobalPolicy, which actually contains little information other than the
 * current configuration level.
 *
 * @author AO
 */


@WebService(serviceName = "PolicyConfigurationService",
        name = "PCS",
        targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration" //wsdlLocation = "classpath:/PCS8.wsdl"
)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED, use = SOAPBinding.Use.LITERAL)
public class PCS4jBean implements PCS {

    private final static String name = "fgsms.PolicyConfigurationService";
    protected final static Logger log = Logger.getLogger(name);
    private static SecurityWrapper currentLevel;
    private static Calendar started = null;
    private static DatatypeFactory df = null;
    static BlockingQueue<Runnable> alertQueue = new LinkedBlockingQueue<Runnable>();
    private static ThreadPoolExecutor alertingThreadPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, alertQueue);

    private synchronized void init() throws DatatypeConfigurationException {

        if (df == null) {
            df = DatatypeFactory.newInstance();
        }
        currentLevel = getCurrentOperatingClassificationLevel();
        if (started == null) {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            started = (gcal);
        }
        try {
            jc = Utility.getSerializationContext();
        } catch (Exception ex) {
            log.log(Level.FATAL, "Couldn't initial the JAXB serialization context. Check for the presense of xercesImpl.jar");
            throw new IllegalAccessError("can't create the JAXB Context, therefore I can't save or read from the database");
        }
    }

    public PCS4jBean() throws DatatypeConfigurationException {
        init();
    }

    /**
     * constructor used for unit testing, do not remove
     *
     * @param ct
     */
    protected PCS4jBean(WebServiceContext ct) throws DatatypeConfigurationException {
        this();
        ctx = ct;
    }
    @Resource
    private WebServiceContext ctx;

    /**
     *
     *
     * GetServicePolicy returns a policy for a specific service
     *
     * Each policy has a type (Service Policy is abstract) ServicePolicy itself
     * has a wide range of settings that are common to all service policy types.
     * Concrete policies extend from ServicePolicy
     *
     * Default Data retension (dataTTL) = 30 days,set by the General Settings
     * parameters All other defaults also come from General Settings collection
     *
     * Required Permission: <ul> <li> If the policy exists, Read for non-agent
     * credentials, </il> <li> If the policy exists, agent credentials can view
     * any policy</il> <li> If the policy doesn't exists, non-agent credentials
     * will return a null policy</il> <li> If the policy doesn't exists, global
     * administrator credentials will return a new policy. In this case, input
     * parameters of domain, machine name, and policy type are required</il>
     * <li> If the policy does not exist, agent credentials, then a new policy
     * will be created with the default parameters. In this case, input
     * parameters of domain, machine name, and policy type are required</il>
     * </ul>
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetServicePolicy", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy")
    @WebResult(name = "GetServicePolicyResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetServicePolicy", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePolicy")
    @ResponseWrapper(localName = "GetServicePolicyResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePolicyResponse")
    public ServicePolicyResponseMsg getServicePolicy(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") ServicePolicyRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getURI())) {
            throw new IllegalArgumentException("URI is null");
        }
        if ((request.getPolicytype() == null)) {
            //   throw new IllegalArgumentException("As of RC6, agents now need to specify the Policy Type?");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getServicePolicy", currentUser, request.getURI(), (request.getClassification()), ctx.getMessageContext());

        ServicePolicyResponseMsg response = new ServicePolicyResponseMsg();
        response.setClassification(currentLevel);

        //http/https only service url contains IP address, try to resolve it here to a hostname, if i can't resolve it, leave it as is
        //  IpAddressUtility.modifyURL(request.getURI(), false);
        response.setPolicy(getPolicyFromDB(IpAddressUtility.modifyURL(request.getURI(), true), (request.getClassification()), currentUser, request.getPolicytype(), request.getMachine(), request.getDomain(), true));
        if (response.getPolicy() == null && (request.getPolicytype() == null || Utility.stringIsNullOrEmpty(request.getMachine()))) {
            throw new IllegalArgumentException("When requesting a policy that doesn't exist already, you must specify the hostname of the machine it's running on and a policy type. If the hostname is not known, use 'unspecified'");
        }

        return response;
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

    /**
     *
     *
     * GetServicePolicy returns a list of service policies for all services that
     * are
     *
     * configured to data publication to any federation target
     *
     *
     *
     * Note: Only information pretaining to uddi publication is returned,
     *
     * other data such as SLA or UserIdentification policies are not returned.
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetUDDIDataPublishServicePoliciesResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetUDDIDataPublishServicePolicies", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetUDDIDataPublishServicePolicies")
    @WebResult(name = "GetUDDIDataPublishServicePoliciesResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetUDDIDataPublishServicePolicies", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetUDDIDataPublishServicePolicies")
    @ResponseWrapper(localName = "GetUDDIDataPublishServicePoliciesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetUDDIDataPublishServicePoliciesResponse")
    public GetUDDIDataPublishServicePoliciesResponseMsg getUDDIDataPublishServicePolicies(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetUDDIDataPublishServicePoliciesRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getUDDIDataPublishServicePolicies", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertAgentRole(currentUser, "getUDDIDataPublishServicePolicies", (request.getClassification()), ctx);
        //log.log(Level.INFO, "GetUDDIDataPublishServicePolicies " + currentUser);

        GetUDDIDataPublishServicePoliciesResponseMsg response = new GetUDDIDataPublishServicePoliciesResponseMsg();
        ArrayOfServicePolicy list = getUDDIPoliciesFromDB(request.getClassification());
        if (list != null) {
            log.log(Level.INFO, "GetUDDIDataPublishServicePolicies returned " + list.getServicePolicy().size() + " policies");
        } else {
            log.log(Level.INFO, "GetUDDIDataPublishServicePolicies returned a null list!");
        }
        //good
        response.setPolicies((list));
        response.setClassification(currentLevel);
        return response;
    }

    /**
     *
     *
     * Saves a service policy to the configuration database
     *
     * Any existing policy for this service is deleted and replaced with the
     * parameter.
     *
     * User Permissions are not affected
     *
     * Required Permission: write
     *
     * Special Note: when setting the policy on an item contains SLAAction
     * Restart or RunScript, Administrative permissions are required
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "SetServicePolicy", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetServicePolicy")
    @WebResult(name = "SetServicePolicyResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetServicePolicy", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicy")
    @ResponseWrapper(localName = "SetServicePolicyResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyResponse")
    public SetServicePolicyResponseMsg setServicePolicy(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetServicePolicyRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        long now = System.currentTimeMillis();
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (request.getPolicy() == null) {
            throw new IllegalArgumentException("policy is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getURL())) {
            throw new IllegalArgumentException("URL is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getPolicy().getURL())) {
            throw new IllegalArgumentException("Policy URL is null");
        }
        if (!request.getURL().equals(request.getPolicy().getURL())) {
            throw new IllegalArgumentException("URL mismatch!");
        }

        log.trace("setServicePolicy trace 1 - " + (System.currentTimeMillis() - now));
        Utility.validateClassification(request.getClassification());
        log.trace("setServicePolicy trace 2 - " + (System.currentTimeMillis() - now));
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setServicePolicy", currentUser, request.getURL(), (request.getClassification()), ctx.getMessageContext());
        log.trace("setServicePolicy trace 3 - " + (System.currentTimeMillis() - now));
        UserIdentityUtil.assertWriteAccess(request.getURL(), currentUser, "setServicePolicy", (request.getClassification()), ctx);
        log.trace("setServicePolicy trace 4 - " + (System.currentTimeMillis() - now));
        if (request.getPolicy().getPolicyType() == null) {
            throw new IllegalArgumentException("A policy type must be specified");
        }

        validateSLAs(request.getPolicy());
        log.trace("setServicePolicy trace 5 - " + (System.currentTimeMillis() - now));
        validatePolicyByType(request.getPolicy());
        log.trace("setServicePolicy trace 6 - " + (System.currentTimeMillis() - now));
        ValidationTools.validateFederationPolicies(request.getPolicy().getFederationPolicyCollection());
        log.trace("setServicePolicy trace 7 - " + (System.currentTimeMillis() - now));
        encryptIfNeeded(request.getPolicy().getFederationPolicyCollection());
        encryptIfNeeded(request.getPolicy());
        writePolicytoDB(request.getPolicy(), request.getPolicy().getURL(), currentUser);

        SetServicePolicyResponseMsg response = new SetServicePolicyResponseMsg();
        response.setStatus(true);
        response.setClassification(getCurrentOperatingClassificationLevel());
        log.trace("setServicePolicy trace 8 - " + (System.currentTimeMillis() - now));
        return response;
    }

    /**
     *
     *
     * Sets the global policy values
     *
     * Required permission: global admin role
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetGlobalPolicyResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetGlobalPolicy", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetGlobalPolicy")
    @WebResult(name = "SetGlobalPolicyResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetGlobalPolicy", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetGlobalPolicy")
    @ResponseWrapper(localName = "SetGlobalPolicyResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetGlobalPolicyResponse")
    public SetGlobalPolicyResponseMsg setGlobalPolicy(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetGlobalPolicyRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        if (request == null || request.getPolicy() == null) {
            throw new IllegalArgumentException("requested policy is null");
        }
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setGlobalPolicy", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "setGlobalPolicy", (request.getClassification()), ctx);
        if (request.getPolicy().getPolicyRefreshRate() == null || request.getPolicy().getRecordedMessageCap() < 1) {
            throw new IllegalArgumentException("the policy is invalid");
        }

        Connection con = null;
        PreparedStatement comm = null;
        try {

            con = Utility.getConfigurationDBConnection();
            GetGlobalPolicyResponseMsg currentSecPol = getGlobalPolicyFromDB();
            comm = con.prepareStatement("BEGIN WORK;LOCK TABLE GlobalPolicies IN ACCESS EXCLUSIVE MODE;"
                    + "DELETE FROM GlobalPolicies; INSERT INTO GlobalPolicies (PolicyRefreshRate, RecordedMessageCap, agentsenable, classification, caveat) "
                    + "     VALUES (?, ?,?,?,?); COMMIT WORK;");

            if (request.getPolicy().getPolicyRefreshRate() == null) {
                comm.setLong(1, 3 * 60 * 1000); //3 minutes
            } else {
                comm.setLong(1, Utility.durationToTimeInMS(request.getPolicy().getPolicyRefreshRate()));
            }
            /*
             * if (request.getPolicy().getUDDIPublishRate() == null) {
             * comm.setLong(2, 60 * 60 * 1000); //60 minutes } else {
             * comm.setLong(2,
             * Utility.durationToTimeInMS(request.getPolicy().getUDDIPublishRate()));
             * }
             */
            comm.setInt(2, request.getPolicy().getRecordedMessageCap());
            comm.setBoolean(3, request.getPolicy().isAgentsEnabled());
            comm.setString(4, currentSecPol.getClassification().getClassification().value());
            comm.setString(5, currentSecPol.getClassification().getCaveats());
            comm.execute();

            SetGlobalPolicyResponseMsg r = new SetGlobalPolicyResponseMsg();
            r.setClassification(this.getCurrentOperatingClassificationLevel());
            return r;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error caught inserting global policy, attempting rolling back", ex);

            if (con != null) {
                PreparedStatement rollback = null;
                try {
                    rollback = con.prepareStatement("ROLLBACK WORK;");
                    rollback.execute();
                    log.log(Level.INFO, "Error caught inserting global policy, rollback successful");
                    rollback.close();
                } catch (Exception e) {
                    log.log(Level.FATAL, "unable to rollback transaction, this can result in deadlocks!", e);
                } finally {
                    DBUtils.safeClose(rollback);
                }
            }
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
     *
     * requires the global policy values
     *
     * required permission: none
     *
     *
     *
     * <param name="request">can't be null, class level should be specified but
     * can be null</param>
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetGlobalPolicy", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetGlobalPolicy")
    @WebResult(name = "GetGlobalPolicyResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetGlobalPolicy", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicy")
    @ResponseWrapper(localName = "GetGlobalPolicyResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyResponse")
    public GetGlobalPolicyResponseMsg getGlobalPolicy(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetGlobalPolicyRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        //  Utility.validateClassification(request.getClassification());

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getGlobalPolicy", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        return getGlobalPolicyFromDB();
    }

    private static GetGlobalPolicyResponseMsg getGlobalPolicyFromDB() throws ServiceUnavailableException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        GetGlobalPolicyResponseMsg response = new GetGlobalPolicyResponseMsg();
        boolean foundPolicy = false;
        try {

            response.setPolicy(new GlobalPolicy());
            //we don't care about the request in this case

            comm = con.prepareStatement("Select * from GlobalPolicies;");

            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            results = comm.executeQuery();

            if (results.next()) {
                response.getPolicy().setPolicyRefreshRate(df.newDuration(results.getLong("PolicyRefreshRate")));
                //moved this to settings list

                //response.getPolicy().setUDDIPublishRate(fac.newDuration(results.getLong("UDDIPublishRate")));
                response.getPolicy().setRecordedMessageCap(results.getInt("RecordedMessageCap"));
                SecurityWrapper wrap = new SecurityWrapper(ClassificationType.fromValue(results.getString("classification")),
                        results.getString("caveat"));
                response.setClassification(wrap);
                response.getPolicy().setAgentsEnabled(results.getBoolean("agentsenable"));
                currentLevel = response.getClassification();
                foundPolicy = true;
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting global policy", ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);

        }

        if (!foundPolicy) {
            try {
                comm = con.prepareStatement("INSERT INTO GlobalPolicies (PolicyRefreshRate, RecordedMessageCap, classification, agentsenable, caveat) "
                        + " VALUES (?, ?, ?, true, ?);");
                comm.setLong(1, 30 * 60 * 100);
                comm.setLong(2, 1024000);
                //comm.setLong(3, 1024000);
                comm.setString(3, "U");
                comm.setString(4, "");
                comm.execute();
                response.getPolicy().setRecordedMessageCap(1024000);
                //response.getPolicy().setUDDIPublishRate(fac.newDuration(30 * 60 * 100));
                //response.getPolicy().setUDDIPublishRate(fac.newDuration(0));
                response.getPolicy().setClassification(new SecurityWrapper(ClassificationType.U, "None"));
                response.getPolicy().setAgentsEnabled(true);
            } catch (SQLException ex) {
                log.log(Level.FATAL, "error setting global policy", ex);
                response = null;
            } finally {
                DBUtils.safeClose(results);
                DBUtils.safeClose(comm);
                DBUtils.safeClose(con);
            }
        }
        DBUtils.safeClose(con);
        if (response != null) {
            KeyNameValueEnc d = DBSettingsLoader.GetPropertiesFromDB(true, "UddiPublisher", "Interval");
            if (d != null && d.getKeyNameValue() != null) {
                try {
                    response.getPolicy().setUDDIPublishRate(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "error setting global policy", ex);
                    response.getPolicy().setUDDIPublishRate(df.newDuration(30 * 60 * 100));
                }
            } else {
                response.getPolicy().setUDDIPublishRate(df.newDuration(30 * 60 * 100));
            }
            return response;
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw f;
    }

    /**
     *
     *
     * Deletes a service policy to the configuration database and optionally,
     * performance data
     *
     * Required Permission: administer
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteServicePolicyResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "DeleteServicePolicy", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/DeleteServicePolicy")
    @WebResult(name = "DeleteServicePolicyResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "DeleteServicePolicy", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteServicePolicy")
    @ResponseWrapper(localName = "DeleteServicePolicyResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteServicePolicyResponse")
    public DeleteServicePolicyResponseMsg deleteServicePolicy(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") DeleteServicePolicyRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getURL())) {
            throw new IllegalArgumentException("a URL must be specified");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "deleteServicePolicy", currentUser, request.getURL(), (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertWriteAccess(request.getURL(), currentUser, "DeleteServicePolicy", (request.getClassification()), ctx);
        deleteFromDB(request.getURL(), request.isDeletePerformanceData(), true);
        if (request.isDeletePerformanceData()) {
            deleteAvailabilityData(request.getURL());
        }
        deleteAllSLASubscriptions(request.getURL(), currentUser);

        DeleteServicePolicyResponseMsg response = new DeleteServicePolicyResponseMsg();
        removeFromStatus(request.getURL());
        ClearCredentialsRequestMsg rr = new ClearCredentialsRequestMsg();
        rr.setClassification(request.getClassification());
        rr.setUrl(request.getURL());
        clearCredentials(rr);
        response.setSuccess(true);
        response.setClassification(getCurrentOperatingClassificationLevel());
        return response;
    }

    /**
     *
     *
     * Sets the permissions on a particular service object
     *
     * Removes the current permission set and replaces it with the requested
     * permission set
     *
     * Required Permission: administer
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePermissionsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetServicePermissions", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetServicePermissions")
    @WebResult(name = "SetServicePermissionsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetServicePermissions", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePermissions")
    @ResponseWrapper(localName = "SetServicePermissionsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePermissionsResponse")
    public SetServicePermissionsResponseMsg setServicePermissions(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetServicePermissionsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        //log.log(Level.INFO, "SetServicePermissions ", currentUser);

        if (request == null) {
            throw new IllegalArgumentException("request");
        }
        Utility.validateClassification(request.getClassification());
        if (Utility.stringIsNullOrEmpty(request.getURL())) {
            throw new IllegalArgumentException("URL");
        }
        if (request.getRights() == null) {
            throw new IllegalArgumentException("rights");
        }

        validatePolicyExists(request.getURL());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setServicePermissions", currentUser, request.getURL(), (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertAdministerAccess(request.getURL(), currentUser, "setServicePerimssions", (request.getClassification()), ctx);

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        try {
            removePermissionsFromDB(request.getURL());
        } catch (Exception ex) {
            log.log(Level.ERROR, "error removing permissions from the db", ex);
        }

        try {
            for (int i = 0; i < request.getRights().getUserPermissionType().size(); i++) {
                try {
                    comm = con.prepareStatement("INSERT INTO UserPermissions (Username,ObjectURI,ReadObject,WriteObject,AdministerObject,AuditObject) " + " VALUES (?, ?, ?, ?, ?, ?);");
                    comm.setString(1, request.getRights().getUserPermissionType().get(i).getUser());
                    comm.setString(2, request.getURL());
                    switch (request.getRights().getUserPermissionType().get(i).getRight()) {
                        case ADMINISTER:
                            comm.setBoolean(3, false);
                            comm.setBoolean(4, false);
                            comm.setBoolean(5, true);
                            comm.setBoolean(6, false);
                            //   cmd.CommandText += "0,0,1,0)";
                            break;
                        case AUDIT:
                            comm.setBoolean(3, false);
                            comm.setBoolean(4, false);
                            comm.setBoolean(5, false);
                            comm.setBoolean(6, true);
                            //   cmd.CommandText += "0,0,0,1)";
                            break;
                        case READ:
                            comm.setBoolean(3, true);
                            comm.setBoolean(4, false);
                            comm.setBoolean(5, false);
                            comm.setBoolean(6, false);
                            // cmd.CommandText += "1,0,0,0)";
                            break;
                        case WRITE:
                            comm.setBoolean(3, false);
                            comm.setBoolean(4, true);
                            comm.setBoolean(5, false);
                            comm.setBoolean(6, false);
                            //    cmd.CommandText += "0,1,0,0)";
                            break;
                    }
                    comm.execute();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "error setting service permissions", ex);
                    ServiceUnavailableException f = new ServiceUnavailableException("", null);
                    f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
                    throw f;
                } finally {
                    DBUtils.safeClose(comm);
                }
            }
        } catch (ServiceUnavailableException ex) {
            throw ex;
        } finally {
            DBUtils.safeClose(con);
        }

        SetServicePermissionsResponseMsg response = new SetServicePermissionsResponseMsg();
        response.setClassification(getCurrentOperatingClassificationLevel());
        return response;

    }

    /**
     *
     *
     * Returns the permission set from a service object
     *
     * Required Permission: read
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePermissionsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetServicePermissions", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePermissions")
    @WebResult(name = "GetServicePermissionsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetServicePermissions", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePermissions")
    @ResponseWrapper(localName = "GetServicePermissionsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePermissionsResponse")
    public GetServicePermissionsResponseMsg getServicePermissions(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetServicePermissionsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("a message must be specified");
        }
        Utility.validateClassification(request.getClassification());

        GetServicePermissionsResponseMsg res = null;
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getServicePermissions", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        if (request.getURL() != null && !Utility.stringIsNullOrEmpty(request.getURL())) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getServicePermissions", currentUser, request.getURL(), (request.getClassification()), ctx.getMessageContext());
            UserIdentityUtil.assertAuditAccess(request.getURL(), currentUser, "GetServicePermissionsURL", (request.getClassification()), ctx);
            validatePolicyExists(request.getURL());
            res = getServicePermissions(request.getURL(), null);
        }
        if (request.getUser() != null && !Utility.stringIsNullOrEmpty(request.getUser())) {
            if ((currentUser.equalsIgnoreCase(request.getUser())) || (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "GetServicePermissionsUSER", (request.getClassification()), ctx))) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "getServicePermissions", currentUser, request.getUser(), (request.getClassification()), ctx.getMessageContext());
                res = getServicePermissions(null, request.getUser());
            }
        }
        if (res == null) {
            throw new IllegalArgumentException("You  must specify a user or url");
        }
        return res;
    }

    /**
     *
     *
     * Returns a list of users with the global admin role
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetAdministratorsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAdministrators", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetAdministrators")
    @WebResult(name = "GetAdministratorsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetAdministrators", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAdministrators")
    @ResponseWrapper(localName = "GetAdministratorsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAdministratorsResponse")
    public GetAdministratorsResponseMsg getAdministrators(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetAdministratorsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        ResultSet results = null;
        PreparedStatement comm = null;
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("a request msg must be specified");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAdministrators", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        //log.log(Level.INFO, "getAdministrators " + currentUser);
        //we don't care about the request in this case
        GetAdministratorsResponseMsg response = new GetAdministratorsResponseMsg();
        try {

            ArrayOfUserInfo list2 = new ArrayOfUserInfo();
            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("Select * from Users where rolecol='admin';");
            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            results = comm.executeQuery();
            while (results.next()) {
                UserInfo u = new UserInfo();
                u.setUsername(results.getString("Username"));
                u.setRole("admin");
                if (!Utility.stringIsNullOrEmpty(results.getString("email"))) {
                    u.getEmail().add(results.getString("email"));
                }
                if (!Utility.stringIsNullOrEmpty(results.getString("email1"))) {
                    u.getEmail().add(results.getString("email1"));
                }
                if (!Utility.stringIsNullOrEmpty(results.getString("email2"))) {
                    u.getEmail().add(results.getString("email2"));
                }
                if (!Utility.stringIsNullOrEmpty(results.getString("email3"))) {
                    u.getEmail().add(results.getString("email3"));
                }
                u.setDisplayName((results.getString("DisplayName")));
                list2.getUserInfo().add(u);
            }

            response.setUserList((list2));
            response.setClassification(currentLevel);

            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting fgsms admins", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    }

    /**
     *
     *
     * Sets a list of users with the global admin role
     *
     * All previous entries are removed
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetAdministratorResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "SetAdministrator", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetAdministrator")
    @WebResult(name = "SetAdministratorResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetAdministrator", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetAdministrator")
    @ResponseWrapper(localName = "SetAdministratorResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetAdministratorResponse")
    public SetAdministratorResponseMsg setAdministrator(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetAdministratorRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "setAdministrator", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "setAdministrator", (request.getClassification()), ctx);
        if (request.getUserList() == null) {
            log.log(Level.ERROR, "userlist null");
        }
        if (request.getUserList() == null) {
            log.log(Level.ERROR, "userlist value null");
        }
        if (request.getUserList().getUserInfo() == null) {
            log.log(Level.ERROR, "getuserinfo null");
        }
        if (request.getUserList().getUserInfo().isEmpty()) {
            log.log(Level.ERROR, "getuserinfo is empty");
        }
        if (request.getUserList() == null
                || request.getUserList().getUserInfo() == null
                || request.getUserList().getUserInfo().isEmpty()) {
            throw new IllegalArgumentException("userlist is empty or null");
        }
        for (int i = 0; i < request.getUserList().getUserInfo().size(); i++) {
            if (request.getUserList().getUserInfo().get(i).getEmail().size() > 4) {
                throw new IllegalArgumentException("a user has more than 4 email addresses defined. Only 4 are supported");
            }
        }

        con = Utility.getConfigurationDBConnection();
        try {

            comm = con.prepareStatement("delete from Users where rolecol='admin';");
            comm.execute();
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting fgsms admins", ex);
        } finally {
            DBUtils.safeClose(comm);
        }
        /////////////////////////////////////////////
        //get the global policy for data retension
        /////////////////////////////////////////////
        try {
            for (int i = 0; i < request.getUserList().getUserInfo().size(); i++) {
                try {
                    comm = con.prepareStatement("INSERT INTO Users (Username, DisplayName,Email, email1, email2, email3,rolecol) VALUES (?,?,?,?,?,?, 'admin');");
                    comm.setString(1, request.getUserList().getUserInfo().get(i).getUsername());
                    comm.setString(2, request.getUserList().getUserInfo().get(i).getDisplayName());
                    if (request.getUserList().getUserInfo().get(i).getEmail().size() > 0) {
                        comm.setString(3, request.getUserList().getUserInfo().get(i).getEmail().get(0));
                    } else {
                        comm.setNull(3, java.sql.Types.VARCHAR);
                    }
                    if (request.getUserList().getUserInfo().get(i).getEmail().size() > 1) {
                        comm.setString(4, request.getUserList().getUserInfo().get(i).getEmail().get(1));
                    } else {
                        comm.setNull(4, java.sql.Types.VARCHAR);
                    }

                    if (request.getUserList().getUserInfo().get(i).getEmail().size() > 2) {
                        comm.setString(5, request.getUserList().getUserInfo().get(i).getEmail().get(2));
                    } else {
                        comm.setNull(5, java.sql.Types.VARCHAR);
                    }
                    if (request.getUserList().getUserInfo().get(i).getEmail().size() > 3) {
                        comm.setString(6, request.getUserList().getUserInfo().get(i).getEmail().get(3));
                    } else {
                        comm.setNull(6, java.sql.Types.VARCHAR);
                    }

                    comm.execute();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "error setting fgsms admins", ex);
                } finally {
                    DBUtils.safeClose(comm);

                }
            }
            SetAdministratorResponseMsg ret = new SetAdministratorResponseMsg();
            ret.setClassification(currentLevel);
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error setting fgsms admins", ex);
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;
    }

    /**
     * Gets the specified policy from postgres database
     *
     * @param requestedURI
     * @param classification
     * @param currentUser
     * @param requestedPt must be specified
     * @param hostname used for policy creation
     * @param domain used for policy creation
     * @param create if true and the user has permissions to create a policy,
     * one will be made using the requested policytype
     * @return
     */
    private ServicePolicy getPolicyFromDB(final String requestedURI, final SecurityWrapper classification, final String currentUser, final PolicyType requestedPt, final String hostname, String domain, boolean create) {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            if (Utility.stringIsNullOrEmpty(requestedURI)) {
                throw new IllegalArgumentException("requestedURI");
            }

            GetGlobalPolicyResponseMsg gp = getGlobalPolicyFromDB();

            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("Select * from ServicePolicies where URI=?;");
            comm.setString(1, requestedURI);

            /////////////////////////////////////////////
            //get the policy for this service
            /////////////////////////////////////////////
            ServicePolicy ret = null;
            boolean CapDefined = true;

            results = comm.executeQuery();

            if (results.next()) {
                PolicyType pt = PolicyType.values()[results.getInt("policytype")];
                Unmarshaller um = jc.createUnmarshaller();
                byte[] s = results.getBytes("xmlpolicy");

                //System.err.println(new String(s));
                //System.out.println(new String(s));
                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                switch (pt) {
                    case MACHINE:
                        JAXBElement<MachinePolicy> foo = (JAXBElement<MachinePolicy>) um.unmarshal(r, MachinePolicy.class);
                        if (foo == null || foo.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo.getValue();
                        }
                        break;
                    case PROCESS:
                        JAXBElement<ProcessPolicy> foo3 = (JAXBElement<ProcessPolicy>) um.unmarshal(r, ProcessPolicy.class);
                        if (foo3 == null || foo3.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo3.getValue();
                        }
                        break;
                    case STATISTICAL:
                        JAXBElement<StatisticalServicePolicy> foo1 = (JAXBElement<StatisticalServicePolicy>) um.unmarshal(r, StatisticalServicePolicy.class);
                        if (foo1 == null || foo1.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo1.getValue();
                        }
                        break;
                    case STATUS:
                        JAXBElement<StatusServicePolicy> foo2 = (JAXBElement<StatusServicePolicy>) um.unmarshal(r, StatusServicePolicy.class);
                        if (foo2 == null || foo2.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo2.getValue();
                        }
                        break;
                    case TRANSACTIONAL:
                        JAXBElement<TransactionalWebServicePolicy> foo4 = (JAXBElement<TransactionalWebServicePolicy>) um.unmarshal(r, TransactionalWebServicePolicy.class);
                        if (foo4 == null || foo4.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo4.getValue();
                        }
                        break;

                }

                r.close();
                bss.close();

                if (results.next()) {
                    log.log(Level.ERROR, "Unable to determine the policy for service " + requestedURI
                            + " because more than one policy is defined for this service! This is an unexpected database error. "
                            + "Returning the default policy to the requestor. Please contact the developers and report this at https://github.com/mil-oss/fgsms");

                }
            } else {

                if (!create) {
                    comm.close();
                    con.close();
                    return null;
                }
                //policy does not exist, create it
                if (create && requestedPt == null) {
                    throw new IllegalArgumentException("a policy type must be specified");
                }
                AuditLogger.logItem(this.getClass().getCanonicalName(), "Createpolicy", currentUser, requestedURI, classification, ctx.getMessageContext());
                UserIdentityUtil.assertAdminOrAgentRole(currentUser, "GetPolicy then CreatePolicy", currentLevel, ctx);
                //new to rc6 only

                //set the default values
                KeyNameValueEnc d = null;
                switch (requestedPt) {
                    case MACHINE:
                        ret = new MachinePolicy();
                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Machine", "defaultRentention");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ret.setDataTTL(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                                ((MachinePolicy) ret).setRecordCPUusage(true);
                                ((MachinePolicy) ret).setRecordMemoryUsage(true);
                            } catch (Exception ex) {
                                log.log(Level.WARN, "Defaulting to 2592000000 for machine default retention.", ex);
                            }
                        } else {
                            ret.setDataTTL(df.newDuration(Long.parseLong("2592000000")));
                        }
                        break;
                    case PROCESS:
                        ret = new ProcessPolicy();
                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Process", "defaultRentention");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ret.setDataTTL(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                                ((ProcessPolicy) ret).setRecordCPUusage(true);
                                ((ProcessPolicy) ret).setRecordMemoryUsage(true);
                                ((ProcessPolicy) ret).setRecordOpenFileHandles(true);
                            } catch (Exception ex) {
                                log.log(Level.WARN, "unable to parse .\"Policy.Process\", \"defaultRentention\"", ex);
                            }
                        } else {
                            ret.setDataTTL(df.newDuration(Long.parseLong("2592000000")));
                        }
                        break;
                    case STATISTICAL:
                        ret = new StatisticalServicePolicy();
                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Machine", "defaultRentention");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ret.setDataTTL(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                            } catch (Exception ex) {
                                log.log(Level.WARN, "unable to parse .\"Policy.Machine\", \"defaultRentention\"", ex);
                            }
                        } else {
                            ret.setDataTTL(df.newDuration(Long.parseLong("2592000000")));
                        }
                        break;
                    case STATUS:
                        ret = new StatusServicePolicy();
                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Status", "defaultRentention");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ret.setDataTTL(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                            } catch (Exception ex) {
                                log.log(Level.WARN, "Defaulting to 2592000000 for status default retention.", ex);
                            }
                        } else {
                            ret.setDataTTL(df.newDuration(Long.parseLong("2592000000")));
                        }
                        break;
                    case TRANSACTIONAL:
                        ret = new TransactionalWebServicePolicy();

                        ((TransactionalWebServicePolicy) ret).setBuellerEnabled(true);
                        ((TransactionalWebServicePolicy) ret).setAgentsEnabled(gp.getPolicy().isAgentsEnabled());
                        ((TransactionalWebServicePolicy) ret).setRecordedMessageCap(gp.getPolicy().getRecordedMessageCap());

                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Transactional", "defaultRecordRequest");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ((TransactionalWebServicePolicy) ret).setRecordRequestMessage(Boolean.parseBoolean(d.getKeyNameValue().getPropertyValue()));
                            } catch (Exception ex) {
                                log.log(Level.WARN, "Unable to parse .\"Policy.Transactional\", \"defaultRecordRequest\"", ex);
                            }
                        } else {
                            ((TransactionalWebServicePolicy) ret).setRecordRequestMessage(false);
                        }
                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Transactional", "defaultRecordResponse");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ((TransactionalWebServicePolicy) ret).setRecordResponseMessage(Boolean.parseBoolean(d.getKeyNameValue().getPropertyValue()));
                            } catch (Exception ex) {
                                log.log(Level.WARN, "Unable to parse .\"Policy.Transactional\", \"defaultRecordResponse\"", ex);
                            }
                        } else {
                            ((TransactionalWebServicePolicy) ret).setRecordResponseMessage(false);
                        }

                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Transactional", "defaultRecordFaults");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ((TransactionalWebServicePolicy) ret).setRecordFaultsOnly(Boolean.parseBoolean(d.getKeyNameValue().getPropertyValue()));
                            } catch (Exception ex) {
                                log.log(Level.WARN, "Unable to parse .\"Policy.Transactional\", \"defaultRecordFaults\"", ex);
                            }
                        } else {
                            ((TransactionalWebServicePolicy) ret).setRecordFaultsOnly(false);
                        }
                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Transactional", "defaultRecordHeaders");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ((TransactionalWebServicePolicy) ret).setRecordHeaders(Boolean.parseBoolean(d.getKeyNameValue().getPropertyValue()));
                            } catch (Exception ex) {
                                log.log(Level.WARN, "Unable to parse .\"Policy.Transactional\", \"defaultRecordHeaders\"", ex);
                            }
                        } else {
                            ((TransactionalWebServicePolicy) ret).setRecordHeaders(true);
                        }

                        d = DBSettingsLoader.GetPropertiesFromDB(true, "Policy.Transactional", "defaultRentention");
                        if (d != null && d.getKeyNameValue() != null) {
                            try {
                                ret.setDataTTL(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                            } catch (Exception ex) {
                                log.log(Level.WARN, "Unable to parse .\"Policy.Transactional\", \"defaultRentention\"", ex);
                            }
                        } else {
                            ret.setDataTTL(df.newDuration(Long.parseLong("2592000000")));
                        }
                        break;
                }
                if (ret == null) {
                    throw new Exception("unhandled case, very expected");
                }

                ret.setDomainName(domain);
                ret.setMachineName(hostname);
                ret.setURL(requestedURI);
                ret.setPolicyType(requestedPt);

                Utility.addStatusChangeSLA(ret);
                //RC6 add default settings if available
                createPolicy(requestedURI, ret, currentUser, requestedPt);
                alertingThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        SLACommon.AlertGlobalAdminsNewPolicyCreated(currentUser, requestedURI, true, requestedPt);
                    }
                });

            }

            results.close();
            comm.close();
            if (ret == null) {
                throw new Exception("unhandled case, very expected");
            }
            ret.setPolicyRefreshRate(gp.getPolicy().getPolicyRefreshRate());
            ret.setAgentsEnabled(gp.getPolicy().isAgentsEnabled());

            if (!CapDefined && ret instanceof TransactionalWebServicePolicy) {
                ((TransactionalWebServicePolicy) ret).setRecordedMessageCap((gp.getPolicy().getRecordedMessageCap()));
            }

            ret.setURL(requestedURI);

            con.close();
            return ret;
        } catch (SQLException ex) {
            log.log(Level.DEBUG, "error retrieving policy from database, db error", ex);
            log.log(Level.ERROR, "error retrieving policy from database, db error" + ex.getMessage());
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error retrieving policy from database, Exception", ex);
            log.log(Level.ERROR, "error retrieving policy from database, Exception" + ex.getMessage());
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        return null;
    }

    /**
     * returns the machine policy or null if it doesn't exist
     *
     * @param hostname
     * @return
     */
    private MachinePolicy getMachinePolicyFromDB(String hostname) {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            if (Utility.stringIsNullOrEmpty(hostname)) {
                throw new IllegalArgumentException("hostname");
            }

            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("Select * from ServicePolicies where hostname=? and policytype=?;");
            comm.setString(1, hostname);
            comm.setInt(2, PolicyType.MACHINE.ordinal());

            /////////////////////////////////////////////
            //get the policy for this service
            /////////////////////////////////////////////
            MachinePolicy ret = null;

            results = comm.executeQuery();

            if (results.next()) {

                Unmarshaller um = jc.createUnmarshaller();
                byte[] s = results.getBytes("xmlpolicy");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);

                JAXBElement<MachinePolicy> foo = (JAXBElement<MachinePolicy>) um.unmarshal(r, MachinePolicy.class);
                if (foo == null || foo.getValue() == null) {
                    log.log(Level.WARN, "policy is unexpectedly null or empty");
                } else {
                    ret = foo.getValue();
                }
                r.close();
                bss.close();
                if (results.next()) {
                    log.log(Level.ERROR, "Unable to determine the policy for service " + hostname
                            + " because more than one policy is defined for this service! This is an unexpected database error. "
                            + "Returning the default policy to the requestor. Please contact the developers and report this at https://github.com/mil-oss/fgsms");

                }
            }
            if (ret != null) {
                GetGlobalPolicyResponseMsg gp = getGlobalPolicyFromDB();
                ret.setAgentsEnabled(gp.getPolicy().isAgentsEnabled());
            }
            comm.close();
            con.close();
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error retrieving policy from database, DatatypeConfigurationException", ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        return null;

    }

    /**
     *  /// /// This is used for the self registration of a web service via a
     * data collector ///
     *
     * @param requestedURI
     */
    private synchronized void createPolicy(String requestedURI, ServicePolicy sp, String username, PolicyType p) {

        if (Utility.stringIsNullOrEmpty(requestedURI)) {
            throw new IllegalArgumentException("requestedURI");
        }
        writePolicytoDB(sp, requestedURI, username);
    }

    /**
     * /// /// First removes any existing policy then writes the policy to the
     * db ///
     *
     * @param sp
     * @param string
     */
    private boolean writePolicytoDB(final ServicePolicy policy, final String URL, final String currentuser) {
        Connection con = null;
        PreparedStatement comm = null;
        long now = System.currentTimeMillis();
        try {
            if (policy == null) {
                throw new IllegalArgumentException("policy");

            }
            if (Utility.stringIsNullOrEmpty(URL)) {
                throw new IllegalArgumentException("URL");

            }
            log.trace("WritePolicytoDB 1 " + (System.currentTimeMillis() - now));
            //must be done BEFORE the new policy is written and the old one removed.
            validateExistingSLASubscriptions(policy, currentuser);
            log.trace("WritePolicytoDB 2 " + (System.currentTimeMillis() - now));
            boolean previouslyexisted = deleteFromDB(URL, false, false);
            if (!previouslyexisted) {
                alertingThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        SLACommon.AlertGlobalAdminsNewPolicyCreated(currentuser, URL, true, policy.getPolicyType());
                    }
                });
            }
            log.trace("WritePolicytoDB 3 " + (System.currentTimeMillis() - now));

            con = Utility.getConfigurationDBConnection();

            Connection p = null;
            PreparedStatement c2 = null;
            try {
                p = Utility.getPerformanceDBConnection();
                c2 = p.prepareStatement("INSERT INTO rawdatatally (URI)"
                        + "SELECT ? WHERE NOT EXISTS"
                        + "(SELECT uri FROM rawdatatally WHERE uri=?);");
                c2.setString(1, URL);
                c2.setString(2, URL);
                c2.execute();

            } catch (Exception ex) {
                log.log(Level.WARN, "Error inserting rawdatatally into the tally table. This is probably ignorable", ex);
            } finally {
                DBUtils.safeClose(c2);
                DBUtils.safeClose(p);

            }

            log.trace("WritePolicytoDB 4 " + (System.currentTimeMillis() - now));

            comm = con.prepareStatement("BEGIN WORK;LOCK TABLE servicepolicies IN SHARE ROW EXCLUSIVE MODE;"
                    + "INSERT INTO servicepolicies (URI, DataTTL, " //2
                    + " latitude, longitude, displayname, policytype, buellerenabled, hostname, domaincol, xmlpolicy, hassla, hasfederation, bucket, lastchanged, lastchangedby," //13
                    + "healthstatusenabled, parenturi )" //1
                    + " SELECT ?,?,?,?,?,  ?,?,?,?,?, ?,?,?, ?,?,?,? WHERE NOT EXISTS"
                    + " (SELECT uri FROM servicepolicies WHERE uri=?);" //1
                    + ""
                    + "COMMIT WORK;");

            comm.setString(1, URL);
            if (policy.getDataTTL() == null) {
                comm.setLong(2, 30 * 24 * 60 * 60);
            } else {
                comm.setLong(2, Utility.durationToTimeInMS(policy.getDataTTL()));
            }
            if (policy.getLocation() != null) {
                comm.setDouble(3, policy.getLocation().getLatitude());
                comm.setDouble(4, policy.getLocation().getLongitude());
            } else {
                comm.setNull(3, Types.DOUBLE);
                comm.setNull(4, Types.DOUBLE);
            }
            if (Utility.stringIsNullOrEmpty(policy.getDisplayName())) {
                comm.setNull(5, java.sql.Types.VARCHAR);
            } else {
                comm.setString(5, policy.getDisplayName());
            }
            comm.setInt(6, policy.getPolicyType().ordinal());
            if (policy instanceof TransactionalWebServicePolicy) {
                TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) policy;
                comm.setBoolean(7, tp.isBuellerEnabled());
            } else {
                comm.setBoolean(7, false);
            }

            if (Utility.stringIsNullOrEmpty(policy.getMachineName())) {
                comm.setString(8, UNSPECIFIED);
            } else {
                comm.setString(8, policy.getMachineName());
            }

            if (Utility.stringIsNullOrEmpty(policy.getDomainName())) {
                comm.setString(9, UNSPECIFIED);
            } else {
                comm.setString(9, policy.getDomainName());
            }
            //xml
            log.trace("WritePolicytoDB 5 " + (System.currentTimeMillis() - now));
            StringWriter sw = new StringWriter();
            JAXB.marshal(policy, sw);
            log.trace("WritePolicytoDB 6 " + (System.currentTimeMillis() - now));

            String s = sw.toString();
            comm.setBytes(10, s.getBytes(Constants.CHARSET));

            if (policy.getServiceLevelAggrements() != null
                    && policy.getServiceLevelAggrements().getSLA() != null && !policy.getServiceLevelAggrements().getSLA().isEmpty()) {
                comm.setBoolean(11, true);
            } else {
                comm.setBoolean(11, false);
            }
            if (policy.getFederationPolicyCollection() != null && !policy.getFederationPolicyCollection().getFederationPolicy().isEmpty()) {
                comm.setBoolean(12, true);
            } else {
                comm.setBoolean(12, false);
            }
            if (Utility.stringIsNullOrEmpty(policy.getBucketCategory())) {
                comm.setString(13, UNSPECIFIED);
            } else {
                comm.setString(13, policy.getBucketCategory());
            }
            comm.setLong(14, System.currentTimeMillis());
            comm.setString(15, currentuser);
            if (policy instanceof TransactionalWebServicePolicy) {
                TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) policy;
                comm.setBoolean(16, tp.isHealthStatusEnabled());
            } else {
                comm.setBoolean(16, false);
            }
            if (Utility.stringIsNullOrEmpty(policy.getParentObject())) {
                comm.setNull(17, java.sql.Types.VARCHAR);
            } else {
                comm.setString(17, policy.getParentObject());
            }
            comm.setString(18, URL);

            comm.execute();
            log.trace("WritePolicytoDB 7 " + (System.currentTimeMillis() - now));
            comm.close();
            con.close();
            log.trace("WritePolicytoDB 8 " + (System.currentTimeMillis() - now));
            return true;
        } catch (Exception ex) {
            log.log(Level.ERROR, "unexpected error writing policy to database, attempting rollback", ex);
            if (con != null) {
                PreparedStatement cmd = null;
                try {
                    cmd = con.prepareStatement("ROLLBACK WORK;");
                    cmd.execute();

                } catch (Exception e) {
                    log.log(Level.ERROR, "unexpected error attempting rollback", e);
                } finally {
                    DBUtils.safeClose(cmd);
                }
            }
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            log.trace("WritePolicytoDB 9 " + (System.currentTimeMillis() - now));
        }
        return false;
    }

    /**
     * returns true only if the policy previously existed and was deleted
     *
     * @param URL
     * @param deletePerformanceData
     * @param deletePermissions
     * @return
     */
    private boolean deleteFromDB(String URL, boolean deletePerformanceData, boolean deletePermissions) {

        long now = System.currentTimeMillis();
        Connection con = null;
        Connection con2 = null;
        PreparedStatement comm = null;
        PreparedStatement comm2 = null;
        try {
            if (Utility.stringIsNullOrEmpty(URL)) {
                throw new IllegalArgumentException("url");
            }

            log.trace("DeleteFromDB 1 " + (System.currentTimeMillis() - now));
            int r = 0;
            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement(
                    "Delete from ServicePolicies where URI=?; ");

            comm.setString(1, URL);
            r = comm.executeUpdate();
            log.trace("DeleteFromDB 2 " + (System.currentTimeMillis() - now));
            comm.close();
            log.trace("DeleteFromDB 3 " + (System.currentTimeMillis() - now));

            if (deletePerformanceData) {
                con2 = Utility.getPerformanceDBConnection();
                comm2 = con2.prepareStatement(
                        "Delete from rawdata where uri=?;"
                        + "Delete from agg2 where uri=?;"
                        + "Delete from brokerrawdata where host=?;"
                        + "Delete from brokerhistory where host=?;"
                        + " Delete from actionlist where uri=?; "
                        + "Delete from slaviolations where uri=?; "
                        + "Delete from availability where uri=?;"
                        + "Delete from alternateurls where uri=?;"
                        + "Delete from rawdatatally where uri=?;"
                        + "Delete from rawdatanic where uri=?; "
                        + "Delete from rawdatamachineprocess where uri=?; "
                        + "Delete from rawdatadrives where uri=?; "
                        + "Delete from rawdatamachinesensor where uri=?; ");
                comm2.setString(1, URL);
                comm2.setString(2, URL);
                comm2.setString(3, URL);
                comm2.setString(4, URL);
                comm2.setString(5, URL);
                comm2.setString(6, URL);
                comm2.setString(7, URL);
                comm2.setString(8, URL);
                comm2.setString(9, URL);

                comm2.setString(10, URL);
                comm2.setString(11, URL);
                comm2.setString(12, URL);
                comm2.setString(13, URL);
//                comm2.setString(14, URL);
                comm2.executeUpdate();
                comm2.close();
                con2.close();

            }
            log.trace("DeleteFromDB 4 " + (System.currentTimeMillis() - now));
            if (deletePermissions) {
                comm = con.prepareStatement("Delete from UserPermissions where ObjectURI=?");
                comm.setString(1, URL);
                comm.execute();
                comm.close();
            }
            log.trace("DeleteFromDB 5 " + (System.currentTimeMillis() - now));
            con.close();

            if (r > 0) {
                return true;
            }

        } // end DeleteFromDB
        catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(comm2);
            DBUtils.safeClose(con);
            DBUtils.safeClose(con2);
        }
        log.trace("DeleteFromDB 6 " + (System.currentTimeMillis() - now));
        return false;

    } // end DeleteFromDB

    /**
     * returns an array of service policy
     *
     * @param w
     * @return null if none found
     */
    private ArrayOfServicePolicy getUDDIPoliciesFromDB(SecurityWrapper w) {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            ArrayOfServicePolicy ret = new ArrayOfServicePolicy();

            comm = con.prepareStatement("SELECT * from servicepolicies where hasfederation=true;");
            results = comm.executeQuery();
            XMLInputFactory xf = XMLInputFactory.newInstance();
            while (results.next()) {
                PolicyType pt = PolicyType.values()[results.getInt("policytype")];
                Unmarshaller um = jc.createUnmarshaller();
                byte[] s = results.getBytes("xmlpolicy");
                ServicePolicy t = null;
                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLStreamReader r = xf.createXMLStreamReader(bss);

                Class type = null;
                switch (pt) {
                    case MACHINE:
                        type = MachinePolicy.class;
                        break;
                    case PROCESS:
                        type = ProcessPolicy.class;
                        break;
                    case STATISTICAL:
                        type = StatisticalServicePolicy.class;
                        break;
                    case STATUS:
                        type = StatusServicePolicy.class;
                        break;
                    case TRANSACTIONAL:
                        type = TransactionalWebServicePolicy.class;
                        break;
                }
                t = (ServicePolicy) JAXB.unmarshal(bss, type);
                if (t != null) {
                    ret.getServicePolicy().add(t);
                }
            }

            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error retrieving Federation Policies for services ", ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        return null;

    }

    //one param or the other
    private GetServicePermissionsResponseMsg getServicePermissions(String URL, String username) throws ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet dt = null;
        try {
            if (Utility.stringIsNullOrEmpty(URL) && Utility.stringIsNullOrEmpty(username)) {
                throw new IllegalArgumentException("URL or username must be specified");
            }

            GetServicePermissionsResponseMsg res = new GetServicePermissionsResponseMsg();
            con = Utility.getConfigurationDBConnection();
            if (Utility.stringIsNullOrEmpty(URL)) {
                comm = con.prepareStatement("Select * from UserPermissions where Username=?;");
                comm.setString(1, username);
            } else {
                validatePolicyExists(URL);
                comm = con.prepareStatement("Select * from UserPermissions where ObjectURI=?;");
                comm.setString(1, URL);
            }
            dt = comm.executeQuery();

            ArrayOfUserServicePermissionType res2 = new ArrayOfUserServicePermissionType();
            UserServicePermissionType temp = null;
            while (dt.next()) {
                temp = new UserServicePermissionType();
                temp.setURL(dt.getString("ObjectURI"));
                temp.setUser(dt.getString("username"));
                boolean read = dt.getBoolean("ReadObject");
                boolean write = dt.getBoolean("WriteObject");
                boolean audit = dt.getBoolean("AuditObject");
                boolean admin = dt.getBoolean("AdministerObject");
                if (admin) {
                    temp.setRight(RightEnum.ADMINISTER);
                } else if (audit) {
                    temp.setRight(RightEnum.AUDIT);
                } else if (write) {
                    temp.setRight(RightEnum.WRITE);
                } else if (read) {
                    temp.setRight(RightEnum.READ);
                }
                res2.getUserServicePermissionType().add(temp);
            }
            res.setRights(res2);

            res.setClassification(getCurrentOperatingClassificationLevel());
            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, "GetServicePermissionsUser", ex);
            return null;
        } finally {
            DBUtils.safeClose(dt);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    }

    private void removePermissionsFromDB(String uRL) throws ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        try {
            if (Utility.stringIsNullOrEmpty(uRL)) {
                throw new IllegalArgumentException("url");
            }
            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("DELETE FROM UserPermissions where ObjectURI=?");
            comm.setString(1, uRL);
            comm.execute();

        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error removing old permission set from database for service uri " + uRL, ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {

            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    }

    private void deleteAvailabilityData(String uRL) throws ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;

        try {
            if (Utility.stringIsNullOrEmpty(uRL)) {
                throw new IllegalArgumentException("url");
            }
            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("DELETE FROM status where uri=?");
            comm.setString(1, uRL);
            comm.execute();
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error removing availability from database for service uri " + uRL, ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);

        }
        try {

            con = Utility.getPerformanceDBConnection();
            comm = con.prepareStatement("delete from availability where uri=?");
            comm.setString(1, uRL);
            comm.execute();
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error removing availability from database for service uri " + uRL, ex);
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
     *
     * Elevate the security classification level of fgsms
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.ElevateSecurityLevelResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "ElevateSecurityLevel", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/ElevateSecurityLevel")
    @WebResult(name = "ElevateSecurityLevelResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "ElevateSecurityLevel", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.ElevateSecurityLevel")
    @ResponseWrapper(localName = "ElevateSecurityLevelResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.ElevateSecurityLevelResponse")
    public ElevateSecurityLevelResponseMsg elevateSecurityLevel(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") ElevateSecurityLevelRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        SecurityWrapper current = this.getCurrentOperatingClassificationLevel();
        String cc = (current.getClassification().value()) + " " + current.getCaveats();
        AuditLogger.logItem(this.getClass().getCanonicalName(), "elevateSecurityLevel", currentUser, "current classification:"
                + cc + " requested level: " + (request.getClassification()), cc, ctx.getMessageContext());

        ElevateSecurityLevelResponseMsg res = new ElevateSecurityLevelResponseMsg();
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "elevateSecurityLevel", current, ctx);
        /*
         * if
         * (request.getClassification().getClassification().compareTo(ClassificationType.U)
         * current) < 0) { throw new IllegalArgumentException(); }
         */
        //if we are current higher than confidential
        if (current.getClassification() != ClassificationType.U && current.getClassification() != ClassificationType.NU
                && current.getClassification() != ClassificationType.NC && current.getClassification() != ClassificationType.C) {
            if (request.getClassification().getClassification() == ClassificationType.C
                    || request.getClassification().getClassification() == ClassificationType.NU
                    || request.getClassification().getClassification() == ClassificationType.NC
                    || request.getClassification().getClassification() == ClassificationType.U) {
                AccessDeniedException ad = new AccessDeniedException("Currently classified, cannot declassify this system.", null);
                AuditLogger.logItem(this.getClass().getCanonicalName(), "elevateSecurityLevel", currentUser, "current classification:"
                        + cc + " requested level: " + (request.getClassification()) + " DENY", cc, ctx.getMessageContext());
                throw ad;
            }
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = null;
        try {

            prepareStatement = con.prepareStatement("UPDATE globalpolicies  SET classification=?, caveat=?;");
            prepareStatement.setString(1, request.getClassification().getClassification().value());
            prepareStatement.setString(2, request.getClassification().getCaveats());
            prepareStatement.execute();
            res.setClassification(request.getClassification());

            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error elevating security level", ex);

        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            if (con != null) {
                try {

                    con.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw f;
    }

    /**
     *
     *
     * Get my email address
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetMyEmailAddressResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMyEmailAddress", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetMyEmailAddress")
    @WebResult(name = "GetMyEmailAddressResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetMyEmailAddress", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMyEmailAddress")
    @ResponseWrapper(localName = "GetMyEmailAddressResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMyEmailAddressResponse")
    public GetMyEmailAddressResponseMsg getMyEmailAddress(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetMyEmailAddressRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        if (request == null) {
            throw new IllegalArgumentException("requested is null");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getMyEmailAddress", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        GetMyEmailAddressResponseMsg res = new GetMyEmailAddressResponseMsg();
        res.setClassification(getCurrentOperatingClassificationLevel());

        if (currentUser.equalsIgnoreCase("anonymous")) {
            return res;
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet rs = null;
        try {

            comm = con.prepareStatement("select email,email1,email2,email3 from users where username=?;");
            comm.setString(1, currentUser);
            rs = comm.executeQuery();
            if (rs.next()) {
                //res.setEmail(rs.getString(1));
                if (!Utility.stringIsNullOrEmpty(rs.getString("email"))) {
                    res.getEmail().add(rs.getString("email"));
                }
                if (!Utility.stringIsNullOrEmpty(rs.getString("email1"))) {
                    res.getEmail().add(rs.getString("email1"));
                }
                if (!Utility.stringIsNullOrEmpty(rs.getString("email2"))) {
                    res.getEmail().add(rs.getString("email2"));
                }
                if (!Utility.stringIsNullOrEmpty(rs.getString("email3"))) {
                    res.getEmail().add(rs.getString("email3"));
                }
            }

            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            if (comm != null) {
                try {
                    comm.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
        }
    }

    /**
     *
     *
     * sets my email address
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetMyEmailAddressResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetMyEmailAddress", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetMyEmailAddress")
    @WebResult(name = "SetMyEmailAddressResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetMyEmailAddress", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetMyEmailAddress")
    @ResponseWrapper(localName = "SetMyEmailAddressResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetMyEmailAddressResponse")
    public SetMyEmailAddressResponseMsg setMyEmailAddress(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetMyEmailAddressRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("requested is null");
        }
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setMyEmailAddress", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        SetMyEmailAddressResponseMsg res = new SetMyEmailAddressResponseMsg();
        res.setClassification(getCurrentOperatingClassificationLevel());

        if (currentUser.equalsIgnoreCase("anonymous")) {
            return res;
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = null;
        try {

            prepareStatement = con.prepareStatement("BEGIN WORK;"
                    + ";LOCK TABLE users IN ACCESS EXCLUSIVE MODE;"
                    + "INSERT INTO users (username)    SELECT  ?  WHERE NOT EXISTS"
                    + "(Select username from users where username=?);"
                    + "UPDATE users   SET email=?, email1=?, email2=?, email3=? WHERE username=?;"
                    + "COMMIT WORK;");
            prepareStatement.setString(1, currentUser);
            prepareStatement.setString(2, currentUser);
            if (request.getEmail().size() > 0) {
                prepareStatement.setString(3, request.getEmail().get(0));
            } else {
                prepareStatement.setNull(3, java.sql.Types.VARCHAR);
            }

            if (request.getEmail().size() > 1) {
                prepareStatement.setString(4, request.getEmail().get(1));
            } else {
                prepareStatement.setNull(4, java.sql.Types.VARCHAR);
            }

            if (request.getEmail().size() > 2) {
                prepareStatement.setString(5, request.getEmail().get(2));
            } else {
                prepareStatement.setNull(5, java.sql.Types.VARCHAR);
            }

            if (request.getEmail().size() > 3) {
                prepareStatement.setString(6, request.getEmail().get(3));
            } else {
                prepareStatement.setNull(6, java.sql.Types.VARCHAR);
            }

            prepareStatement.setString(7, currentUser);
            prepareStatement.execute();

            prepareStatement.close();

            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error inserting user recorded to the database, attempting rollback", ex);
            PreparedStatement cmd = null;
            try {
                cmd = con.prepareStatement("ROLLBACK WORK;");
                cmd.execute();
            } catch (Exception e) {
                log.log(Level.ERROR, "error attempting rollback", e);
            } finally {
                if (cmd != null) {
                    try {
                        cmd.close();
                    } catch (Exception ex1) {
                        log.log(Level.DEBUG, "", ex);
                    }
                }
            }
        } finally {
            if (prepareStatement != null) {
                try {
                    prepareStatement.close();
                } catch (SQLException ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception ex) {
                log.log(Level.DEBUG, "", ex);
            }
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw f;
    }

    /**
     *
     *
     * sets a list of SLAs that I am subscribed to
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetAlertRegistrationsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetAlertRegistrations", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetAlertRegistrations")
    @WebResult(name = "SetAlertRegistrationsResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetAlertRegistrations", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetAlertRegistrations")
    @ResponseWrapper(localName = "SetAlertRegistrationsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetAlertRegistrationsResponse")
    public SetAlertRegistrationsResponseMsg setAlertRegistrations(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetAlertRegistrationsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

            if (request == null) {
                throw new IllegalArgumentException("requested is null");
            }
            Utility.validateClassification(request.getClassification());

            AuditLogger.logItem(this.getClass().getCanonicalName(), "getAlertRegistrations", currentUser, "", (request.getClassification()), ctx.getMessageContext());

            SetAlertRegistrationsResponseMsg res = new SetAlertRegistrationsResponseMsg();
            res.setClassification(getCurrentOperatingClassificationLevel());

            if (currentUser.equalsIgnoreCase("anonymous")) {
                return res;
            }

            List<String> problemids = new ArrayList<String>();
            boolean ok = true;
            for (int i = 0; i < request.getItems().size(); i++) {
                ServicePolicy p = SLACommon.LoadPolicyPooled(request.getItems().get(i).getServiceUri());//, request.getClassification(), currentUser, PolicyType.TRANSACTIONAL, null, null, false);
                if (p == null) {
                    ok = false;
                    problemids.add(request.getItems().get(i).getServiceUri() + " " + request.getItems().get(i).getSLAID());
                    break;
                } else {
                    boolean localok = false;
                    if (p.getServiceLevelAggrements() != null) {
                        for (int k = 0; k < p.getServiceLevelAggrements().getSLA().size(); k++) {
                            if (p.getServiceLevelAggrements().getSLA().get(k).getGuid().equalsIgnoreCase(request.getItems().get(i).getSLAID())) {
                                if (containsEmailAction(p.getServiceLevelAggrements().getSLA().get(k))) {
                                    localok = true;
                                }
                                break;
                            }
                        }
                    }
                    if (!localok) {
                        ok = false;
                        problemids.add(request.getItems().get(i).getServiceUri() + " " + request.getItems().get(i).getSLAID());
                        break;
                    }
                }
            }
            if (!ok) {
                throw new IllegalArgumentException("All requested SLA subscription IDs must be already defined in an existing service policy and have SLA Email actions defined. Faulting items: " + Utility.listStringtoString(problemids));
            }
            con = Utility.getConfigurationDBConnection();
            //insert the new data
            PreparedStatement comm = con.prepareStatement("delete from slasub where username=?;");
            comm.setString(1, currentUser);
            comm.execute();
            comm.close();
            for (int i = 0; i < request.getItems().size(); i++) {
                comm = con.prepareStatement("INSERT INTO slasub (slaid, username, uri)  VALUES (?, ? ,?);");
                comm.setString(1, request.getItems().get(i).getSLAID());
                comm.setString(2, currentUser);
                comm.setString(3, request.getItems().get(i).getServiceUri());
                comm.execute();
                comm.close();
            }
            List<String> email = new ArrayList<String>();
            comm = con.prepareStatement("select email, email1, email2,email3 from users where username=?;");
            comm.setString(1, currentUser);
            ResultSet rs = comm.executeQuery();
            while (rs.next()) {
                if (!Utility.stringIsNullOrEmpty(rs.getString(1))) {
                    if (!Utility.stringIsNullOrEmpty(rs.getString(1).trim())) {
                        email.add(rs.getString(1).trim());
                    }
                }
            }
            rs.close();
            comm.close();
            con.close();
            SLACommon.AlertUserSLASubscribed(currentUser, email, request, true);
            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     *
     *
     * returns a list of SLAs that I am subscribed to
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetAlertRegistrationsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAlertRegistrations", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetAlertRegistrations")
    @WebResult(name = "GetAlertRegistrationsResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetAlertRegistrations", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAlertRegistrations")
    @ResponseWrapper(localName = "GetAlertRegistrationsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAlertRegistrationsResponse")
    @Override
    public GetAlertRegistrationsResponseMsg getAlertRegistrations(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetAlertRegistrationsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("requested is null");
            }
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getAlertRegistrations", currentUser, "", (request.getClassification()), ctx.getMessageContext());

            Utility.validateClassification(request.getClassification());

            GetAlertRegistrationsResponseMsg res = new GetAlertRegistrationsResponseMsg();
            res.setClassification(getCurrentOperatingClassificationLevel());

            if (currentUser.equalsIgnoreCase("anonymous")) {
                return res;
            }

            con = Utility.getConfigurationDBConnection();
            PreparedStatement comm = con.prepareStatement("select * from slasub  where username=?;");
            comm.setString(1, currentUser);
            ResultSet rs = comm.executeQuery();
            while (rs.next()) {
                SLAregistration re = new SLAregistration();
                re.setSLAID(rs.getString("slaid"));
                re.setServiceUri(rs.getString("uri"));
                res.getItems().add(re);
            }
            rs.close();
            comm.close();
            con.close();
            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     *
     *
     * returns a list of all SLAs that the current user can subscribe to
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetAvailableAlertRegistrationsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAvailableAlertRegistrations", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetAvailableAlertRegistrations")
    @WebResult(name = "GetAvailableAlertRegistrationsResponseMsg", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetAvailableAlertRegistrations", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAvailableAlertRegistrations")
    @ResponseWrapper(localName = "GetAvailableAlertRegistrationsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAvailableAlertRegistrationsResponse")
    public GetAvailableAlertRegistrationsResponseMsg getAvailableAlertRegistrations(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetAvailableAlertRegistrationsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet rs=null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

            if (request == null) {
                throw new IllegalArgumentException("requested is null");
            }

            AuditLogger.logItem(this.getClass().getCanonicalName(), "getAlertRegistrations", currentUser, "", (request.getClassification()), ctx.getMessageContext());

            Utility.validateClassification(request.getClassification());
            GetAvailableAlertRegistrationsResponseMsg res = new GetAvailableAlertRegistrationsResponseMsg();
            res.setClassification(getCurrentOperatingClassificationLevel());

            if (currentUser.equalsIgnoreCase("anonymous")) {
                throw new IllegalArgumentException("Not supported for anonymous users");
            }
            con = Utility.getConfigurationDBConnection();
            HashMap svc = new HashMap();
            
            if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "getAvailableAlertRegistrations", currentLevel, ctx)) {
                //get all services with an sla
                comm = con.prepareStatement("select uri from servicepolicies where hassla=true order by uri desc;");
            } else {
                //join on sla2 on userpermissions
                comm = con.prepareStatement("select uri from servicepolicies, userpermissions where userpermissions.objecturi=servicepolicies.uri and username=? and "
                        + "(auditobject=true OR administerobject=true OR writeobject=true or readobject=true)  and  hassla=true order by uri desc;");
                comm.setString(1, currentUser);
            }
            rs = comm.executeQuery();
            while (rs.next()) {
                if (!svc.containsKey(rs.getString(1))) {
                    svc.put(rs.getString(1), "");
                }
            }
            Iterator it = svc.keySet().iterator();
            while (it.hasNext()) {
                //for (int i = 0; i < svc.size(); i++) {
                ServicePolicy pol = this.getPolicyFromDB((String) it.next(), currentLevel, currentUser, null, null, null, false);
                if (pol != null) {
                    if (Utility.hasEmailSLA(pol)) {
                        res.getServicePolicy().add(pol);
                    }
                }
            }
            //load all policies from the list
            //check before adding to result set if an "emailaction" is an option

            return res;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
    }

    /**
     * validates that a policy exists, no changes are made if it does not
     *
     * @param uRL
     * @throws ServiceUnavailableException
     */
    private void validatePolicyExists(String uRL) throws ServiceUnavailableException {
        if (Utility.stringIsNullOrEmpty(uRL)) {
            throw new IllegalArgumentException("No url provided");
        }
        Connection con = null;
        try {

            con = Utility.getConfigurationDBConnection();
            PreparedStatement prepareStatement = con.prepareStatement("select * from servicepolicies where uri=?");
            prepareStatement.setString(1, uRL);
            ResultSet rs = prepareStatement.executeQuery();
            boolean ok = false;
            if (rs.next()) {
                ok = true;
            }
            rs.close();
            prepareStatement.close();
            con.close();
            if (!ok) {
                throw new IllegalArgumentException("The specified service is not registered");
            }
        } catch (SQLException ex) {
            log.log(Level.ERROR, "could find the policy for " + uRL + " due to an error", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {
            try {
                con.close();
            } catch (Exception ex) {
                log.log(Level.DEBUG, "", ex);
            }
        }

    }

    /**
     * validates user identification parameters
     *
     * @param policy
     */
    private void validatePolicyByType(ServicePolicy policy) throws ServiceUnavailableException {
        if (policy == null || policy.getPolicyType() == null) {
            throw new IllegalArgumentException("null policy or policy type");
        }
        if (policy instanceof TransactionalWebServicePolicy) {
            TransactionalWebServicePolicy p = (TransactionalWebServicePolicy) policy;

            if (p.getUserIdentification() != null && !p.getUserIdentification().getUserIdentity().isEmpty()) {
                for (int i = 0; i < p.getUserIdentification().getUserIdentity().size(); i++) {
                    if (p.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader() != null
                            && p.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader()
                            && (p.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName() == null
                            || Utility.stringIsNullOrEmpty(p.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName()))) {
                        throw new IllegalArgumentException("when specifying an http header as a user identity, the header name must be set");
                    }
                }
            }
        }

        if (!Utility.stringIsNullOrEmpty(policy.getParentObject())) {
            this.validatePolicyExists(policy.getParentObject());
        }
    }
    private JAXBContext jc = null;

    private boolean containsEmailAction(SLA get) {
        if (get == null) {
            return false;
        }
        if (get.getAction() == null) {
            return false;
        }
        if (get.getAction().getSLAAction().isEmpty()) {
            return false;
        }
        for (int i = 0; i < get.getAction().getSLAAction().size(); i++) {
            if (get.getAction().getSLAAction().get(i).getImplementingClassName().equals(EmailAlerter.class.getCanonicalName())) {
                return true;
            }
        }
        return false;

    }

    /**
     * Call before the new policy is written and before the old one is deleted
     * if the policy was set and an sla was removed, remove all SLA
     * subscriptions that were signed to that person and alert the person that
     * the SLA no longer exists
     *
     */
    private void validateExistingSLASubscriptions(ServicePolicy policy, String currentUser) {
        //get all existing slasubscriptions for the url
        //compare with new sla list
        //generate list of slasubscriptions for slaids that no longer exist
        //  for each item, remove sla and alert
        Connection config = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            config = Utility.getConfigurationDBConnection();
            com = config.prepareStatement(
                    "SELECT \"slaid\", users.username as uid, email,email1,email2,email3  "
                    + "FROM slasub, users "
                    + "where slasub.username=users.username "
                    //+ "and sla2.id=slasub.slaid "
                    + "and slasub.uri=?");

            List<SLASubscriptionInfo> existingslas = new ArrayList<SLASubscriptionInfo>();
            //exist
            com.setString(1, policy.getURL());
            rs = com.executeQuery();
            while (rs.next()) {
                SLASubscriptionInfo st = new SLASubscriptionInfo();

                String id = rs.getString("slaid");
                String subscribersUsername = rs.getString("uid");
                String subscribersEmail = rs.getString("email");
                st.id = id;
                st.username = subscribersUsername;
                st.email = new ArrayList<String>();
                if (!Utility.stringIsNullOrEmpty(subscribersEmail)) {
                    st.email.add(subscribersEmail.trim());
                }
                subscribersEmail = rs.getString("email1");
                if (!Utility.stringIsNullOrEmpty(subscribersEmail)) {
                    st.email.add(subscribersEmail.trim());
                }
                subscribersEmail = rs.getString("email2");
                if (!Utility.stringIsNullOrEmpty(subscribersEmail)) {
                    st.email.add(subscribersEmail.trim());
                }
                subscribersEmail = rs.getString("email3");
                if (!Utility.stringIsNullOrEmpty(subscribersEmail)) {
                    st.email.add(subscribersEmail.trim());
                }
                //
                existingslas.add(st);
            }

            for (int i = 0; i < existingslas.size(); i++) {
                if (notContainsSLAid(policy, existingslas.get(i).id)) {
                    deleteSLASubscription(existingslas.get(i).id);
                    SLACommon.AlertUserSLADeleted(currentUser, existingslas.get(i).username, existingslas.get(i).email, policy.getURL(), existingslas.get(i).id, true);
                }
            }

        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            if (com != null) {
                try {
                    com.close();

                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }

            if (config != null) {
                try {
                    config.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
        }
    }

    private boolean notContainsSLAid(ServicePolicy policy, String id) {
        if (policy == null) {
            throw new IllegalArgumentException("policy is null");
        }
        if (policy.getServiceLevelAggrements() == null || policy.getServiceLevelAggrements().getSLA().isEmpty()) {
            return true;
        }
        for (int i = 0; i < policy.getServiceLevelAggrements().getSLA().size(); i++) {
            if (policy.getServiceLevelAggrements().getSLA().get(i).getGuid().equalsIgnoreCase(id)) {
                return false;
            }
        }
        return true;
    }

    private void deleteSLASubscription(String id) {
        Connection config = null;
        PreparedStatement com = null;
        try {
            config = Utility.getConfigurationDBConnection();
            com = config.prepareStatement("delete  FROM slasub where slaid=?");
            com.setString(1, id);
            com.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.WARN, "error caught removing sla subscription", ex);
        } finally {
            if (com != null) {
                try {
                    com.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            if (config != null) {
                try {
                    config.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
        }
    }

    /**
     *
     *
     * returns a list of all user principles with the agent role, these are the
     * service level users that are allowed to request and set new service
     * policies and to add data to fgsms via the DCS
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentPrinicplesResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAgentPrinicples", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetAgentPrinicples")
    @WebResult(name = "GetAgentPrinicplesResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetAgentPrinicples", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentPrinicples")
    @ResponseWrapper(localName = "GetAgentPrinicplesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentPrinicplesResponse")
    public GetAgentPrinicplesResponseMsg getAgentPrinicples(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetAgentPrinicplesRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet results = null;
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getAdministrators", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getAgentPrinicples", currentLevel, ctx);

        try {
            //log.log(Level.INFO, "getAdministrators " + currentUser);
            //we don't care about the request in this case
            GetAgentPrinicplesResponseMsg response = new GetAgentPrinicplesResponseMsg();
            ArrayOfUserInfo list2 = new ArrayOfUserInfo();
            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("Select * from Users where rolecol='agent';");
            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            results = comm.executeQuery();
            while (results.next()) {
                UserInfo u = new UserInfo();
                u.setUsername(results.getString("Username"));
                u.setRole("agent");
                u.setDisplayName((results.getString("DisplayName")));
                list2.getUserInfo().add(u);
            }
            response.setClassification(currentLevel);
            response.setUserList((list2));

            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting fgsms admins", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error setting fgsms admins", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
    }

    /**
     *
     *
     * seta list of all user principles with the agent role, these are the
     * service level users that are allowed to request and set new service
     * policies and to add data to fgsms via the DCS all previous records are
     * replaced
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetAgentPrinicplesResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetAgentPrinicples", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetAgentPrinicples")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetAgentPrinicples", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetAgentPrinicples")
    @ResponseWrapper(localName = "SetAgentPrinicplesResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetAgentPrinicplesResponse")
    public SetAgentPrinicplesResponseMsg setAgentPrinicples(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetAgentPrinicplesRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("request is null");
            }
            Utility.validateClassification(request.getClassification());

            AuditLogger.logItem(this.getClass().getCanonicalName(), "setAgentPrinicples", currentUser, "", (request.getClassification()), ctx.getMessageContext());
//            log.log(Level.INFO, "setAdministrator " + currentUser);
            UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "setAgentPrinicples", (request.getClassification()), ctx);

            if (request.getUserList() == null) {
                log.log(Level.ERROR, "userlist null");
            }
            if (request.getUserList().getUserInfo() == null) {
                log.log(Level.ERROR, "getuserinfo null");
            }
            if (request.getUserList().getUserInfo().isEmpty()) {
                log.log(Level.ERROR, "getuserinfo is empty");
            }
            if (request.getUserList() == null
                    || request.getUserList().getUserInfo() == null
                    || request.getUserList().getUserInfo().isEmpty()) {
                throw new IllegalArgumentException("userlist is empty or null");
            }
            con = Utility.getConfigurationDBConnection();
            PreparedStatement comm = con.prepareStatement("delete from Users where rolecol='agent';");
            comm.execute();
            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            for (int i = 0; i < request.getUserList().getUserInfo().size(); i++) {
                comm = con.prepareStatement("INSERT INTO Users (Username, DisplayName,rolecol) VALUES (?,?,'agent');");
                comm.setString(1, request.getUserList().getUserInfo().get(i).getUsername());
                comm.setString(2, request.getUserList().getUserInfo().get(i).getDisplayName());

                comm.execute();
            }
            comm.close();
            con.close();
            SetAgentPrinicplesResponseMsg ret = new SetAgentPrinicplesResponseMsg();
            ret.setClassification(currentLevel);
            return ret;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting fgsms admins", ex);
        } finally {
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;
    }

    /**
     * validates the SLAs included within a policy match the type of policy
     *
     * @param pol
     */
    private void validateSLAs(ServicePolicy pol) {
        if (pol == null) {
            return;
        }
        if (pol.getServiceLevelAggrements() == null || pol.getServiceLevelAggrements().getSLA().isEmpty()) {
            return;
        }
        AtomicReference<String> outmsg = new AtomicReference<String>();
        boolean isvalid = SLACommon.ValidateSLAs(outmsg, pol);

        //special case for access control on SLA Restart Action, admin required
        this.validateRestartRunScriptAsGlobalAdministrator(pol.getServiceLevelAggrements());

        if (!isvalid) {
            throw new IllegalArgumentException(outmsg.get());
        }

    }

    /**
     *
     *
     * gets mail settings that are used to deliver email for SLA alerts
     *
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetMailSettingsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMailSettings", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetMailSettings")
    @WebResult(name = "GetMailSettingsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetMailSettings", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMailSettings")
    @ResponseWrapper(localName = "GetMailSettingsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMailSettingsResponse")
    public GetMailSettingsResponseMsg getMailSettings(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetMailSettingsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "getMailSettings", currentUser, "", (request.getClassification()), ctx.getMessageContext());
//            log.log(Level.INFO, "setAdministrator " + currentUser);
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getMailSettings", (request.getClassification()), ctx);

        Connection con = null;
        PreparedStatement comm = null;
        ResultSet rs = null;
        try {

            GetMailSettingsResponseMsg response = new GetMailSettingsResponseMsg();

            con = Utility.getConfigurationDBConnection();
            comm = con.prepareStatement("select * from mail;");
            rs = comm.executeQuery();
            while (rs.next()) {
                PropertiesList e = new PropertiesList();
                e.setPropertyName(rs.getString("property"));
                e.setPropertyValue(rs.getString("valuecol"));
                response.getPropertiesList().add(e);
            }

            response.setClassification(currentLevel);

            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting fgsms email settings", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;

    }

    /**
     *
     *
     * sets a list of all settings that are used for SLA email alerts requires
     * global admin rights
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetMailSettingsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetMailSettings", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetMailSettings")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetMailSettings", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetMailSettings")
    @ResponseWrapper(localName = "SetMailSettingsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetMailSettingsResponse")
    public SetMailSettingsResponseMsg setMailSettings(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetMailSettingsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

            if (request == null) {
                throw new IllegalArgumentException("request is null");
            }

            Utility.validateClassification(request.getClassification());

            AuditLogger.logItem(this.getClass().getCanonicalName(), "setMailSettings", currentUser, "", (request.getClassification()), ctx.getMessageContext());
//            log.log(Level.INFO, "setAdministrator " + currentUser);
            UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "setMailSettings", (request.getClassification()), ctx);

            if (request.getPropertiesList() == null) {
                log.log(Level.ERROR, "property list is null");
                throw new IllegalArgumentException("request is null");
            }
            if (request.getPropertiesList() == null || request.getPropertiesList().isEmpty()) {
                throw new IllegalArgumentException("request is null");
            }
            validateEmailSettings(request.getPropertiesList());

            con = Utility.getConfigurationDBConnection();
            StringBuilder sb = new StringBuilder();

            //Findbugs and other tools like it will flag this as potential sql injection
            //which it is not. move along...
            for (int i = 0; i < request.getPropertiesList().size(); i++) {
                sb = sb.append("(?,?),");
            }
            String insert = sb.toString();
            insert = insert.substring(0, insert.length() - 1);

            comm = con.prepareStatement(
                    "DELETE FROM mail; INSERT INTO mail (property, valuecol) values "
                    + insert + "; ");

            for (int i = 0; i < request.getPropertiesList().size(); i++) {
                //comm = con.prepareStatement("INSERT INTO Users (Username, DisplayName,Email,rolecol) VALUES (?,?,?,'agent');");
                comm.setString((i * 2) + 1, Utility.truncate(request.getPropertiesList().get(i).getPropertyName(), 128));
                comm.setString((i * 2) + 2, Utility.truncate(request.getPropertiesList().get(i).getPropertyValue(), 128));
            }
            comm.execute();

            SetMailSettingsResponseMsg ret = new SetMailSettingsResponseMsg();
            ret.setClassification(currentLevel);
            return ret;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting fgsms email settings", ex);
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;
    }

    private void validateEmailSettings(List<PropertiesList> propertiesList) {
        boolean founddefaultReplyAddress = false;
        boolean foundfgsmsgui = false;
        boolean foundMailhost = false;
        for (int i = 0; i < propertiesList.size(); i++) {
            if (propertiesList.get(i).getPropertyName().equalsIgnoreCase("fgsms.GUI.URL")) {
                foundfgsmsgui = true;
            }
            if (propertiesList.get(i).getPropertyName().equalsIgnoreCase("defaultReplyAddress")) {
                founddefaultReplyAddress = true;
            }
            if (propertiesList.get(i).getPropertyName().equalsIgnoreCase("mail.smtp.host")) {
                foundMailhost = true;
            }
        }
        if (foundMailhost && foundfgsmsgui && founddefaultReplyAddress) {
            return;
        }
        throw new IllegalArgumentException("The properties list is missing one of (fgsms.GUI.URL, defaultReplyAddress, mail.smtp.host)");
    }

    private void deleteAllSLASubscriptions(final String uRL, final String currentUser) {
        Connection con = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            con = Utility.getConfigurationDBConnection();
            com = con.prepareStatement(
                    "SELECT \"slaid\", users.username as uid, email,email1,email2,email3  "
                    + "FROM slasub, users "
                    + "where slasub.username=users.username "
                    + "and uri=?");
            com.setString(1, uRL);
            rs = com.executeQuery();

            while (rs.next()) {
                final String id = rs.getString("slaid");
                final String subscribersUsername = rs.getString("uid");

                //idstodelete.add(id);
                deleteSLASubscription(id);
                final List<String> subscribersEmail = new ArrayList<String>();
                String emaill = rs.getString("email");
                if (!Utility.stringIsNullOrEmpty(emaill)) {
                    subscribersEmail.add(emaill.trim());
                }
                emaill = rs.getString("email1");
                if (!Utility.stringIsNullOrEmpty(emaill)) {
                    subscribersEmail.add(emaill.trim());
                }
                emaill = rs.getString("email2");
                if (!Utility.stringIsNullOrEmpty(emaill)) {
                    subscribersEmail.add(emaill.trim());
                }
                emaill = rs.getString("email3");
                if (!Utility.stringIsNullOrEmpty(emaill)) {
                    subscribersEmail.add(emaill.trim());
                }

                alertingThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        SLACommon.AlertUserSLADeleted(currentUser, subscribersUsername, subscribersEmail, uRL, id, true);
                    }
                });

            }

        } catch (Exception ex) {
            log.log(Level.WARN, "error caught deleting sla subscriptions", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        con = Utility.getConfigurationDBConnection();
        try {
            com = con.prepareStatement("delete from slasub where uri=?;");
            com.setString(1, uRL);
            com.execute();
        } catch (Exception ex) {
            log.log(Level.WARN, "error caught deleting sla subscriptions", ex);
        } finally {
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

    }

    private void removeFromStatus(String uRL) {
        Connection config = null;
        PreparedStatement com = null;
        try {
            config = Utility.getConfigurationDBConnection();
            com = config.prepareStatement("delete from status where uri=?");
            com.setString(1, uRL);
            com.execute();;

        } catch (Exception ex) {
            log.log(Level.WARN, "trouble remove data from the status table.", ex);
        } finally {
            if (com != null) {
                try {
                    com.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            if (config != null) {
                try {
                    config.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
        }
    }

    /**
     * sets a list of all settings that are used for fgsmss services requires
     * global admin rights or agent As of RC6
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetGeneralSettings", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetGeneralSettings")
    @WebResult(name = "GetGeneralSettingsResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetGeneralSettings", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettings")
    @ResponseWrapper(localName = "GetGeneralSettingsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponse")
    public GetGeneralSettingsResponseMsg getGeneralSettings(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetGeneralSettingsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet rs = null;
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getGeneralSettings", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertAdminOrAgentRole(currentUser, "getGeneralSettings", (request.getClassification()), ctx);

        try {

            con = Utility.getConfigurationDBConnection();
            if (Utility.stringIsNullOrEmpty(request.getKeyvalue())) {
                comm = con.prepareStatement("select * from settings order by keycol asc;");
            } else {
                comm = con.prepareStatement("select * from settings where keycol=?  order by namecol asc;");
                comm.setString(1, request.getKeyvalue());
            }
            GetGeneralSettingsResponseMsg response = new GetGeneralSettingsResponseMsg();

            rs = comm.executeQuery();
            while (rs.next()) {
                KeyNameValue e = new KeyNameValue();
                e.setPropertyKey(rs.getString("keycol"));
                e.setPropertyName(rs.getString("namecol"));
                e.setPropertyValue(new String(rs.getBytes("valuecol"), Constants.CHARSET));
                response.getKeyNameValue().add(e);
            }

            response.setClassification(currentLevel);

            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error getting general settings", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting general settings", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    }

    /**
     *
     *
     * set 1 or more specific settings, previous values are overwritten, values
     * not specified in the request, if present, remain represent requires
     * global admin rights As of RC6
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetGeneralSettings", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetGeneralSettings")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetGeneralSettings", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettings")
    @ResponseWrapper(localName = "SetGeneralSettingsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsResponse")
    public SetGeneralSettingsResponseMsg setGeneralSettings(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetGeneralSettingsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("request is null");
            }
            if (request.getKeyNameValueEnc().isEmpty()) {
                throw new IllegalArgumentException("at least one setting must be specified");
            }
            for (int i = 0; i < request.getKeyNameValueEnc().size(); i++) {
                if (request.getKeyNameValueEnc().get(i).getKeyNameValue() == null) {
                    throw new IllegalArgumentException("no value specified for item " + i);
                }
                if (Utility.stringIsNullOrEmpty(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyKey())
                        || Utility.stringIsNullOrEmpty(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyName())
                        || Utility.stringIsNullOrEmpty(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyValue())) {
                    throw new IllegalArgumentException("no parameter specified for either the key, name or value of item " + i);
                }
            }
            Utility.validateClassification(request.getClassification());
            AuditLogger.logItem(this.getClass().getCanonicalName(), "setGeneralSettings", currentUser, "", (request.getClassification()), ctx.getMessageContext());
            UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "setGeneralSettings", (request.getClassification()), ctx);

            con = Utility.getConfigurationDBConnection();
            for (int i = 0; i < request.getKeyNameValueEnc().size(); i++) {
                comm = con.prepareStatement("delete from settings where keycol=? and namecol=?; "
                        + "INSERT INTO settings(keycol, namecol, valuecol, isencrypted)    VALUES (?, ?, ?, ?);");
                comm.setString(1, Utility.truncate(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyKey().trim(), 128));
                comm.setString(2, Utility.truncate(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyName().trim(), 128));
                comm.setString(3, Utility.truncate(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyKey().trim(), 128));
                comm.setString(4, Utility.truncate(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyName().trim(), 128));
                if (request.getKeyNameValueEnc().get(i).isShouldEncrypt()) {
                    comm.setBytes(5, Utility.EN(request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyValue().trim()).getBytes(Constants.CHARSET));
                    comm.setBoolean(6, true);
                } else {
                    comm.setBytes(5, request.getKeyNameValueEnc().get(i).getKeyNameValue().getPropertyValue().trim().getBytes(Constants.CHARSET));
                    comm.setBoolean(6, false);
                }
                comm.execute();
                comm.close();
            }

            con.close();
            SetGeneralSettingsResponseMsg response = new SetGeneralSettingsResponseMsg();
            response.setClassification(currentLevel);
            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting general settings", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error setting general settings", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {

            if (comm != null) {
                try {
                    comm.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                }
            }
        }

    }

    /**
     *
     *
     * removes 1 or more specific settings, previous values are discarded
     * requires global admin rights As of RC6
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettingsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "RemoveGeneralSettings", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/RemoveGeneralSettings")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "RemoveGeneralSettings", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettings")
    @ResponseWrapper(localName = "RemoveGeneralSettingsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettingsResponse")
    public RemoveGeneralSettingsResponseMsg removeGeneralSettings(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") RemoveGeneralSettingsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (request.getKeyNameValue().isEmpty()) {
            throw new IllegalArgumentException("you must specify at least one item to remove");
        }
        for (int i = 0; i < request.getKeyNameValue().size(); i++) {
            if (request.getKeyNameValue().get(i) == null) {
                throw new IllegalArgumentException("you must specify at least one item to remove for parameter " + i);
            }
            if (Utility.stringIsNullOrEmpty(request.getKeyNameValue().get(i).getPropertyKey()) || Utility.stringIsNullOrEmpty(request.getKeyNameValue().get(i).getPropertyName())) {
                throw new IllegalArgumentException("you must specify a key and name for parameter " + i);
            }
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "removeGeneralSettings", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "removeGeneralSettings", (request.getClassification()), ctx);

        Connection con = null;
        PreparedStatement comm = null;
        try {

            con = Utility.getConfigurationDBConnection();
            for (int i = 0; i < request.getKeyNameValue().size(); i++) {
                try {
                    comm = con.prepareStatement("delete from settings where keycol=? and namecol=?;");
                    comm.setString(1, request.getKeyNameValue().get(i).getPropertyKey());
                    comm.setString(2, request.getKeyNameValue().get(i).getPropertyName());
                    comm.execute();
                    comm.close();
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    DBUtils.safeClose(comm);
                }
            }

            RemoveGeneralSettingsResponseMsg response = new RemoveGeneralSettingsResponseMsg();
            response.setClassification(currentLevel);
            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error removeGeneralSettings" + ex.getMessage());
            log.log(Level.DEBUG, "error removeGeneralSettings", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw f;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error removeGeneralSettings" + ex.getMessage());
            log.log(Level.DEBUG, "error removeGeneralSettings", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);

        }

    }

    /**
     *
     *
     * sets authentication credentials for a specific service, typically used
     * for connecting and obtaining status information only requires write
     * access permissions to the specified url As of RC6
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetCredentialsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetCredentials", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetCredentials")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetCredentials", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetCredentials")
    @ResponseWrapper(localName = "SetCredentialsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetCredentialsResponse")
    public SetCredentialsResponseMsg setCredentials(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetCredentialsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        if (Utility.stringIsNullOrEmpty(request.getUrl())) {
            throw new IllegalArgumentException("url is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getUsername())) {
            throw new IllegalArgumentException("username is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getPassword())) {
            throw new IllegalArgumentException("usernname or password is null");
        }
        if (request.getStyle() == null) {
            request.setStyle(TransportAuthenticationStyle.NA);
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "setCredentials", currentUser, request.getUrl(), (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertWriteAccess(request.getUrl(), currentUser, "setCredentials", (request.getClassification()), ctx);
        ServicePolicy policyFromDB = getPolicyFromDB(request.getUrl(), currentLevel, currentUser, null, null, null, false);
        if (policyFromDB == null) {
            throw new IllegalArgumentException("url is not a registered service");
        }

        try {

            con = Utility.getConfigurationDBConnection();

            comm = con.prepareStatement("delete from bueller where uri=?; "
                    + "INSERT INTO bueller(            uri, username, pwdcol, authtype)    VALUES (?, ?, ?, ?); ");
            comm.setString(1, request.getUrl());
            comm.setString(2, request.getUrl());
            comm.setString(3, request.getUsername());

            if (request.isPasswordEncrypted()) {
                comm.setBytes(4, request.getPassword().getBytes(Constants.CHARSET));
            } else {
                comm.setBytes(4, Utility.EN(request.getPassword()).getBytes(Constants.CHARSET));
            }

            comm.setInt(5, request.getStyle().ordinal());
            comm.execute();

            SetCredentialsResponseMsg response = new SetCredentialsResponseMsg();
            response.setClassification(currentLevel);
            return response;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error setting setting credentials settings", ex);
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;

    }

    /**
     *
     *
     * removes authentication credentials for a specific service, typically used
     * for connecting and obtaining status information only requires write
     * access permissions to the specified url As of RC6
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.ClearCredentialsResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "ClearCredentials", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/ClearCredentials")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "ClearCredentials", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.ClearCredentials")
    @ResponseWrapper(localName = "ClearCredentialsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.ClearCredentialsResponse")
    public ClearCredentialsResponseMsg clearCredentials(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") ClearCredentialsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("request is null");
            }
            Utility.validateClassification(request.getClassification());
            if (Utility.stringIsNullOrEmpty(request.getUrl())) {
                throw new IllegalArgumentException("url is null");
            }

            AuditLogger.logItem(this.getClass().getCanonicalName(), "clearCredentials", currentUser, request.getUrl(), (request.getClassification()), ctx.getMessageContext());
            UserIdentityUtil.assertWriteAccess(request.getUrl(), currentUser, "clearCredentials", (request.getClassification()), ctx);

            con = Utility.getConfigurationDBConnection();

            comm = con.prepareStatement("delete from bueller where uri=?; ");
            comm.setString(1, request.getUrl());
            comm.execute();
            comm.close();
            con.close();
            ClearCredentialsResponseMsg response = new ClearCredentialsResponseMsg();
            response.setClassification(currentLevel);
            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error setting  clearing credentials", ex);
        } finally {
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;

    }
    public static final String UNSPECIFIED = "unspecified";

    /**
     *
     *
     * provides infrastructure information, a list of all domains associated
     * with this fgsms instance As of RC6
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetDomainListResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetDomainList", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetDomainList")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetDomainList", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetDomainList")
    @ResponseWrapper(localName = "GetDomainListResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetDomainListResponse")
    public GetDomainListResponseMsg getDomainList(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetDomainListRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet rs = null;

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        GetDomainListResponseMsg response = new GetDomainListResponseMsg();
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getDomainList", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getDomainList", (request.getClassification()), ctx);

        try {

            con = Utility.getConfigurationDBConnection();

            comm = con.prepareStatement("select domaincol from servicepolicies group by domaincol; ");
            rs = comm.executeQuery();
            while (rs.next()) {
                response.getDomains().add(rs.getString(1));
            }

            response.setClassification(currentLevel);
            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error getting  getDomainList", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;
    }

    /**
     *
     *
     * provides infrastructure information, a list of all known hostnames
     * associated with this fgsms instance As of RC6
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetMachinesByDomainResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetMachinesByDomain", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetMachinesByDomain")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetMachinesByDomain", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMachinesByDomain")
    @ResponseWrapper(localName = "GetMachinesByDomainResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMachinesByDomainResponse")
    public GetMachinesByDomainResponseMsg getMachinesByDomain(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetMachinesByDomainRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet rs = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            if (request == null) {
                throw new IllegalArgumentException("request is null");
            }
            Utility.validateClassification(request.getClassification());
            if (Utility.stringIsNullOrEmpty(request.getDomain())) {
                throw new IllegalArgumentException("a domain must be specified, or 'unspecified' must be given");
            }
            GetMachinesByDomainResponseMsg response = new GetMachinesByDomainResponseMsg();
            AuditLogger.logItem(this.getClass().getCanonicalName(), "getMachinesByDomain", currentUser, request.getDomain(), (request.getClassification()), ctx.getMessageContext());
            UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getMachinesByDomain", (request.getClassification()), ctx);

            con = Utility.getConfigurationDBConnection();

            comm = con.prepareStatement("select hostname from servicepolicies where domaincol=? group by hostname; ");
            comm.setString(1, request.getDomain());
            rs = comm.executeQuery();
            while (rs.next()) {
                response.getHostname().add(rs.getString(1));
            }

            response.setClassification(currentLevel);
            return response;
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error getting  getMachinesByDomain", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;
    }

    /**
     *
     *
     * provides infrastructure information, a list of all known processes
     * associated with a given hostname requires global admin rights As of RC6
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetProcessesListByMachine", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetProcessesListByMachine")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetProcessesListByMachine", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachine")
    @ResponseWrapper(localName = "GetProcessesListByMachineResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineResponse")
    public GetProcessesListByMachineResponseMsg getProcessesListByMachine(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetProcessesListByMachineRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        if (Utility.stringIsNullOrEmpty(request.getHostname())) {
            throw new IllegalArgumentException("a hostname must be specified");
        }
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getProcessesListByMachine", currentUser, request.getHostname(), (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getProcessesListByMachine", (request.getClassification()), ctx);

        try {
            return getMachineInfo(request.getHostname());

        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting  getDomainList", ex);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", null);
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;
    }

    /**
     * loads the last known configuration for a given machine
     *
     * @param hostname
     * @return
     */
    private GetProcessesListByMachineResponseMsg getMachineInfo(String hostname) throws Exception {
        Connection con = null;
        PreparedStatement comm = null;
        ResultSet rs = null;
        try {

            GetProcessesListByMachineResponseMsg response = new GetProcessesListByMachineResponseMsg();

            con = Utility.getConfigurationDBConnection();

            comm = con.prepareStatement("select * from machineinfo where hostname=?;");
            comm.setString(1, hostname);
            rs = comm.executeQuery();
            if (rs.next()) {
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("lastchanged"));
                response.setLastupdateat((gcal));
                Unmarshaller u = jc.createUnmarshaller();
                byte[] s = rs.getBytes("xmlcol");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);

                JAXBElement<SetProcessListByMachineRequestMsg> foo = (JAXBElement<SetProcessListByMachineRequestMsg>) u.unmarshal(r, SetProcessListByMachineRequestMsg.class);
                if (foo == null || foo.getValue() == null) {
                    log.log(Level.WARN, "xml is unexpectedly null or empty");
                } else {
                    response.setMachineInformation(foo.getValue().getMachineInformation());
                    response.getProcessName().addAll(foo.getValue().getServices());
                }
            }
            response.setClassification(currentLevel);

            return response;
        } catch (Exception ex) {
            throw ex;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
    }

    /**
     *
     *
     * provides infrastructure information, a list of all known processes
     * associated with a given hostname requires agent rights As of RC6
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetProcessListByMachineResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "SetProcessListByMachine", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/SetProcessListByMachine")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "SetProcessListByMachine", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetProcessListByMachine")
    @ResponseWrapper(localName = "SetProcessListByMachineResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.SetProcessListByMachineResponse")
    public SetProcessListByMachineResponseMsg setProcessListByMachine(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") SetProcessListByMachineRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        if (request.getMachineInformation() == null) {
            throw new IllegalArgumentException("a machine information must be specified, or 'unspecified' must be given");
        }
        if (Utility.stringIsNullOrEmpty(request.getMachineInformation().getHostname())) {
            throw new IllegalArgumentException("a hostname must be specified");
        }
        if (Utility.stringIsNullOrEmpty(request.getMachineInformation().getDomain())) {
            throw new IllegalArgumentException("a hostname must be specified");
        }
        Connection con = null;
        PreparedStatement comm = null;
        try {
            SetProcessListByMachineResponseMsg response = new SetProcessListByMachineResponseMsg();
            AuditLogger.logItem(this.getClass().getCanonicalName(), "setProcessListByMachine", currentUser, request.getMachineInformation().getHostname(), (request.getClassification()), ctx.getMessageContext());
            UserIdentityUtil.assertAgentRole(currentUser, "setProcessListByMachine", (request.getClassification()), ctx);

            con = Utility.getConfigurationDBConnection();
            //Marshaller m = null;

            //m = jc.createMarshaller();
            comm = con.prepareStatement(""
                    + "delete from machineinfo where hostname=?; "
                    + "INSERT INTO machineinfo(  xmlcol, hostname, domaincol, lastchanged)    "
                    + "VALUES (?, ?, ?, ?);");
            comm.setString(1, request.getMachineInformation().getHostname());
            StringWriter sw = new StringWriter();
            JAXB.marshal(request, sw);
            //m.marshal(request, sw);
            comm.setBytes(2, sw.toString().getBytes(Constants.CHARSET));
            comm.setString(3, request.getMachineInformation().getHostname());
            comm.setString(4, request.getMachineInformation().getDomain());
            comm.setLong(5, System.currentTimeMillis());
            comm.execute();

            try {
                response.setClassification(currentLevel);

                response.setMachinePolicy((MachinePolicy) getPolicyFromDB("urn:" + request.getMachineInformation().getHostname().toLowerCase() + ":system", currentLevel, currentUser, PolicyType.MACHINE, request.getMachineInformation().getHostname().toLowerCase(),
                        request.getMachineInformation().getDomain().toLowerCase(), true));
                AuxHelper.TryUpdateStatus(true, "urn:" + request.getMachineInformation().getHostname().toLowerCase() + ":system", "online", true, PolicyType.MACHINE, request.getMachineInformation().getDomain(), request.getMachineInformation().getHostname());
                response.getProcessPolicy().addAll(SLACommon.LoadProcessPoliciesPooledByHostname(request.getMachineInformation().getHostname()));
                //     response.setCheckingFrequency(df.newDuration(1000*60*30));
                KeyNameValueEnc s = DBSettingsLoader.GetPropertiesFromDB(true, "Agents.Process", "ReportingFrequency");
                if (s != null && s.getKeyNameValue() != null) {
                    try {
                        long l = Long.parseLong(s.getKeyNameValue().getPropertyValue());
                        response.setReportingFrequency(df.newDuration(l));
                    } catch (Exception ex) {
                        response.setReportingFrequency(df.newDuration(1000 * 30));
                    }
                } else {
                    response.setReportingFrequency(df.newDuration(1000 * 30));
                }

            } catch (ClassCastException cs) {
                log.log(Level.WARN, "error caught, this can mean that either service policy urls have collided or that something went haywire.", cs);
                throw cs;
            } catch (Exception ex) {
                log.log(Level.WARN, "error caught", ex);
                throw ex;
            }

            if (!agents.containsKey(request.getAgentType())) {
                agents.put(request.getAgentType(), true);
                Connection config = Utility.getConfigurationDBConnection();
                PreparedStatement com = null;
                try {
                    com = config.prepareStatement("INSERT INTO agents(agenttype)  VALUES (?)");
                    com.setString(1, request.getAgentType());
                    com.execute();
                    log.log(Level.DEBUG, "PCS DEBUG adding agent type");
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "PCS DEBUG adding agent type FAILED");
                } finally {
                    DBUtils.safeClose(com);
                    DBUtils.safeClose(config);
                }
            }

            return response;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting  setting the current machine information", ex);
        } finally {

            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException f = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
        f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw f;

    }
    private static HashMap agents = new HashMap();

    /**
     *
     *
     * provides infrastructure information, a list of all known processes
     * associated with a given hostname requires agent rights As of RC6
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetMonitoredItemsByMachineResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetMonitoredItemsByMachine", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetMonitoredItemsByMachine")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetMonitoredItemsByMachine", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMonitoredItemsByMachine")
    @ResponseWrapper(localName = "GetMonitoredItemsByMachineResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetMonitoredItemsByMachineResponse")
    public GetMonitoredItemsByMachineResponseMsg getMonitoredItemsByMachine(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetMonitoredItemsByMachineRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        if (Utility.stringIsNullOrEmpty(request.getHostname())) {
            throw new IllegalArgumentException("a hostname must be specified");
        }
        GetMonitoredItemsByMachineResponseMsg response = new GetMonitoredItemsByMachineResponseMsg();
        AuditLogger.logItem(this.getClass().getCanonicalName(), "GetMonitoredItemsByMachineResponseMsg", currentUser, request.getHostname(), (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "GetMonitoredItemsByMachineResponseMsg", (request.getClassification()), ctx);

        try {
            response.setClassification(currentLevel);
            response.setMachinePolicy(getMachinePolicyFromDB(request.getHostname()));
            response.getProcessPolicy().addAll(SLACommon.LoadProcessPoliciesPooledByHostname(request.getHostname()));
        } catch (Exception ex) {
            log.log(Level.WARN, "error caught", ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;
        }

        return response;
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

    /**
     *
     *
     * returns a list of all administrative action for an agent to perform by
     * machine. Only applies to machine policy items, requires agent or global
     * administrative rights.
     *
     * As of 6.3
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentActionsByMachineResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    //@WebMethod(operationName = "GetAgentActionsByMachine", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetAgentActionsByMachine")
    //@WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    //@RequestWrapper(localName = "GetAgentActionsByMachine", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentActionsByMachine")
    //@ResponseWrapper(localName = "GetAgentActionsByMachineResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentActionsByMachineResponse")
    public GetAgentActionsByMachineResponseMsg getAgentActionsByMachine(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetAgentActionsByMachineRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        return new GetAgentActionsByMachineResponseMsg();
        /*
         String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
         if (request == null) {
         throw new IllegalArgumentException("request is null");
         }
         Utility.validateClassification(request.getClassification());
         if (Utility.stringIsNullOrEmpty(request.getHostname()) && Utility.stringIsNullOrEmpty(request.getUri())) {
         throw new IllegalArgumentException("a hostname or policy URI must be specified but not both");
         }
         if (!Utility.stringIsNullOrEmpty(request.getHostname()) && !Utility.stringIsNullOrEmpty(request.getUri())) {
         throw new IllegalArgumentException("a hostname or policy URI must be specified but not both");
         }
         GetAgentActionsByMachineResponseMsg response = new GetAgentActionsByMachineResponseMsg();
         AuditLogger.logItem(this.getClass().getCanonicalName(), "getAgentActionsByMachine", currentUser, request.getHostname(), (request.getClassification()), ctx.getMessageContext());
         UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getAgentActionsByMachine", (request.getClassification()), ctx);

         try {
         response.setClassification(currentLevel);
         if (!Utility.stringIsNullOrEmpty(request.getHostname())) {
         response.getAgentActions().addAll(GetAgentActionsByHostname(request.getHostname(), UserIdentityUtil.isTrustedAgent(currentUser, "getAgentActionsByMachine", request.getClassification(), ctx)));
         } else {
         response.getAgentActions().addAll(GetAgentActionsByPolicyURI(request.getUri(), UserIdentityUtil.isTrustedAgent(currentUser, "getAgentActionsByMachine", request.getClassification(), ctx)));
         }
         } catch (Exception ex) {
         log.log(Level.WARN, "error caught", ex);
         }

         return response;*/
    }

    /**
     *
     *
     * updates a administrative action for an agent to perform. Only applies to
     * machine policy items, requires agent rights.
     *
     * As of 6.3
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.UpdateAgentActionResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "UpdateAgentAction", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/UpdateAgentAction")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "UpdateAgentAction", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.UpdateAgentAction")
    @ResponseWrapper(localName = "UpdateAgentActionResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.UpdateAgentActionResponse")
    public UpdateAgentActionResponseMsg updateAgentAction(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") UpdateAgentActionRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        return null;
    }

    /**
     *
     *
     * deletes a list of administrative action for an agent to perform. Only
     * applies to machine policy items, requires global administrative rights.
     * actions must be in the completed state
     *
     * As of 6.3
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteAgentActionsResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "DeleteAgentActions", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/DeleteAgentActions")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "DeleteAgentActions", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteAgentActions")
    @ResponseWrapper(localName = "DeleteAgentActionsResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteAgentActionsResponse")
    public DeleteAgentActionsResponseMsg deleteAgentActions(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") DeleteAgentActionsRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        return null;
    }

    /**
     *
     *
     * creates a new administrative action for an agent to perform. Only applies
     * to machine policy items, requires global administrative rights
     *
     * As of 6.3
     *
     *
     *
     *
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.CreateAgentActionResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "CreateAgentAction", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/CreateAgentAction")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "CreateAgentAction", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.CreateAgentAction")
    @ResponseWrapper(localName = "CreateAgentActionResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.CreateAgentActionResponse")
    public CreateAgentActionResponseMsg createAgentAction(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") CreateAgentActionRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        return null;
    }

    /*    private List<AgentAction> GetAgentActionsByHostname(String hostname, boolean isagent) {
     }

     private List<AgentAction> GetAgentActionsByPolicyURI(String uri, boolean isagent) {
     Connection con = Utility.getConfigurationDBConnection();
     PreparedStatement cmd = con.prepareStatement("select * from agentmailbox where uri=?; ");
     cmd.setString(1, uri);
     ResultSet rs = cmd.executeQuery();
     while (rs.next()) {
     AgentAction a = new AgentAction();
     }


     }*/
    /**
     * Registers the existence of a new plugin. Note: this does not actually
     * copy files to the server(s) Permissions: global administrators only
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.RegisterPluginResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "RegisterPlugin", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/RegisterPlugin")
    @WebResult(name = "registerPluginResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public RegisterPluginResponse registerPlugin(
            @WebParam(name = "registerPlugin", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters") RegisterPlugin parameters)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (parameters == null) {
            throw new IllegalArgumentException("request is null");
        }
        RegisterPluginRequestMsg request = parameters.getRequest();
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (request.getPlugin().isEmpty()) {
            throw new IllegalArgumentException("no plugins were specified");
        }
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "registerPlugin", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "registerPlugin", request.getClassification(), ctx);
        boolean registered = false;

        for (int i = 0; i < request.getPlugin().size(); i++) {
            if (Utility.stringIsNullOrEmpty(request.getPlugin().get(i).getClassname())) {
                throw new IllegalArgumentException("plugin[" + i + "].classname is null");
            }
            //validate that the class name is a valid class name
            if (!ValidationTools.isValidPackageName(request.getPlugin().get(i).getClassname())) {
                throw new IllegalArgumentException("plugin[" + i + "].classname is invalid");
            }

            if (Utility.stringIsNullOrEmpty(request.getPlugin().get(i).getPlugintype())) {
                throw new IllegalArgumentException("plugin[" + i + "].type is null");
            }

            //first check to see if its already registered
            Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement prepareStatement = null;
            ResultSet rs = null;
            try {

                prepareStatement = con.prepareStatement("select * from plugins where classname=? and appliesto=?");
                prepareStatement.setString(1, request.getPlugin().get(i).getClassname());
                prepareStatement.setString(2, request.getPlugin().get(i).getPlugintype().toUpperCase());
                rs = prepareStatement.executeQuery();
                if (rs.next()) {
                    registered = true;
                }

            } catch (Exception ex) {
                log.error(ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(prepareStatement);
                DBUtils.safeClose(con);

            }
        }
        if (registered) {
            throw new IllegalArgumentException("one of more plugins are already registered");
        }

        for (int i = 0; i < request.getPlugin().size(); i++) {
            //create an instance of the plugin
            String displayname = null;

            try {
                Class c = Class.forName(request.getPlugin().get(i).getClassname());
                Object j = c.newInstance();
                //FEDERATION_PUBLISH,SLA_RULE,SLA_ACTION
                if (request.getPlugin().get(i).getPlugintype().equalsIgnoreCase("SLA_ACTION")) {
                    SLAActionInterface sla = (SLAActionInterface) j;

                    displayname = (sla.GetDisplayName());

                } else if (request.getPlugin().get(i).getPlugintype().equalsIgnoreCase("SLA_RULE")) {
                    SLARuleInterface sla = (SLARuleInterface) j;
                    displayname = (sla.GetDisplayName());

                } else if (request.getPlugin().get(i).getPlugintype().equalsIgnoreCase("FEDERATION_PUBLISH")) {
                    FederationInterface sla = (FederationInterface) j;
                    displayname = (sla.GetDisplayName());

                } else {
                    throw new IllegalArgumentException("plugin[" + i + "].classname doesn't implement a known plugin interface type");
                }

            } catch (InstantiationException ex) {
                log.error("unable to load plugin " + request.getPlugin().get(i).getClassname(), ex);
                throw new IllegalArgumentException("plugin is invalid");
            } catch (IllegalAccessException ex) {
                log.error("unable to load plugin " + request.getPlugin().get(i).getClassname(), ex);
                throw new IllegalArgumentException("plugin is invalid");
            } catch (ClassNotFoundException ex) {
                log.error("unable to load plugin " + request.getPlugin().get(i).getClassname(), ex);
                throw new IllegalArgumentException("plugin is invalid");
            } catch (Exception ex) {
                log.error("The implementation for " + request.getPlugin().get(i).getClassname() + " is faulty and threw and exception. Please contact the developer for resolution", ex);
                throw new IllegalArgumentException("plugin is invalid");
            }
            //pull data from it

            //insert into database
            Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement prepareStatement = null;
            try {
                prepareStatement = con.prepareStatement("INSERT INTO plugins(            classname, displayname, appliesto)    VALUES (?, ?, ?);");
                prepareStatement.setString(1, request.getPlugin().get(i).getClassname());
                prepareStatement.setString(2, displayname);
                prepareStatement.setString(3, request.getPlugin().get(i).getPlugintype().toUpperCase());
                prepareStatement.execute();
            } catch (Exception ex) {

                ServiceUnavailableException f = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
                f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                throw f;

            } finally {
                DBUtils.safeClose(prepareStatement);
                DBUtils.safeClose(con);

            }
        }
        //return success
        RegisterPluginResponseMsg res = new RegisterPluginResponseMsg();
        res.setClassification(getCurrentOperatingClassificationLevel());

        RegisterPluginResponse r = new RegisterPluginResponse();
        r.setResponse(res);
        return r;
    }

    /**
     * Unregisters the existence of a plugin. Plugins cannot be unregistered if
     * they are currently referenced from an existing policy. Note: this does
     * not actually remove files to the server(s) Permissions: global
     * administrators only
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.UnregisterPluginResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "UnregisterPlugin", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/UnregisterPlugin")
    @WebResult(name = "unregisterPluginResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public UnregisterPluginResponse unregisterPlugin(
            @WebParam(name = "unregisterPlugin", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters") UnregisterPlugin parameter) throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (parameter == null) {
            throw new IllegalArgumentException("request is null");
        }
        UnregisterPluginRequestMsg request = parameter.getRequest();
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "unregisterPlugin", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        for (int i = 0; i < request.getPlugin().size(); i++) {
            if (Utility.stringIsNullOrEmpty(request.getPlugin().get(i).getClassname())) {
                throw new IllegalArgumentException("plugin[" + i + "].classname is null");
            }
            if (Utility.stringIsNullOrEmpty(request.getPlugin().get(i).getPlugintype())) {
                throw new IllegalArgumentException("plugin[" + i + "].type is null");
            }
        }

        UnregisterPluginResponseMsg res = new UnregisterPluginResponseMsg();
        res.setClassification(getCurrentOperatingClassificationLevel());

        boolean used = false;
        List<String> users_filtered = new ArrayList<String>();
        int usercount = 0;
        //check to see if its used

        List<ServicePolicy> sps = SLACommon.LoadServicePoliciesPooled();
        for (int i = 0; i < sps.size(); i++) {
            if (sps.get(i).getServiceLevelAggrements() != null
                    && sps.get(i).getServiceLevelAggrements().getSLA() != null) {
                for (int k = 0; k < sps.get(i).getServiceLevelAggrements().getSLA().size(); k++) {
                    for (int x = 0; x < request.getPlugin().size(); x++) {
                        if (request.getPlugin().get(x).getPlugintype().equalsIgnoreCase("SLA_ACTION")) {
                            if (sps.get(i).getServiceLevelAggrements().getSLA().get(k).getAction() != null) {
                                for (int j = 0; j < sps.get(i).getServiceLevelAggrements().getSLA().get(k).getAction().getSLAAction().size(); j++) {
                                    if (sps.get(i).getServiceLevelAggrements().getSLA().get(k).getAction().getSLAAction().get(j).getImplementingClassName().equalsIgnoreCase(
                                            request.getPlugin().get(x).getClassname())) {
                                        usercount++;
                                        if (UserIdentityUtil.hasReadAccess(currentUser, "unregisterPlugin", sps.get(i).getURL(), request.getClassification(), ctx)) {
                                            users_filtered.add(sps.get(i).getURL());
                                        }
                                        used = true;
                                    }
                                }
                            }
                        } else if (request.getPlugin().get(x).getPlugintype().equalsIgnoreCase("SLA_RULE")) {
                            //TODO
                        } else if (request.getPlugin().get(x).getPlugintype().equalsIgnoreCase("SLA_FEDERATION")) {
                            //TODO
                        }
                    }
                }
            }
        }

        if (used) {
            throw new IllegalArgumentException("Unable to unregister plugin, it is currently referenced from " + usercount + " service policies. Those service policies are "
                    + Utility.listStringtoString(users_filtered) + ". Note: this list may be filtered based on access control rules");
        }
        //remove it from db
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = null;
        int x = 0;
        try {
            for (int z = 0; z < request.getPlugin().size(); z++) {
                try {
                    prepareStatement = con.prepareStatement("delete from plugins where appliesto=? and classname=?");
                    prepareStatement.setString(1, request.getPlugin().get(z).getPlugintype().toUpperCase());
                    prepareStatement.setString(2, request.getPlugin().get(z).getClassname());
                    x = prepareStatement.executeUpdate();
                } catch (Exception ex) {
                    log.error(ex);
                    ServiceUnavailableException f = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
                    f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                    throw f;

                } finally {
                    DBUtils.safeClose(prepareStatement);
                }
            }

        } catch (Exception ex) {
            log.error(ex);
            ServiceUnavailableException f = new ServiceUnavailableException("", new org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException());
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
            throw f;

        } finally {
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(con);

        }
        if (x == 0) {
            log.fatal("DELETION from database failed! PCS unregisterPlugin!");
        }
        UnregisterPluginResponse unregisterPluginResponse = new UnregisterPluginResponse();
        unregisterPluginResponse.setResponse(res);
        return unregisterPluginResponse;
    }

    /**
     * Gets the current plugin list Permissions: All authenticated users
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginListResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetPluginList", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetPluginList")
    @WebResult(name = "getPluginListResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public GetPluginListResponse getPluginList(
            @WebParam(name = "getPluginList", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters") GetPluginList parameters) {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (parameters == null) {
            throw new IllegalArgumentException("request is null");
        }
        GetPluginListRequestMsg request = parameters.getRequest();
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }

        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getPluginList", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        GetPluginListResponse r = new GetPluginListResponse();

        GetPluginListResponseMsg res = new GetPluginListResponseMsg();
        r.setResponse(res);
        res.setClassification(getCurrentOperatingClassificationLevel());
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            if (Utility.stringIsNullOrEmpty(request.getPlugintype())) {
                prepareStatement = con.prepareStatement("select * from plugins");
            } else {
                prepareStatement = con.prepareStatement("select * from plugins where appliesto=?");
                prepareStatement.setString(1, request.getPlugintype().toUpperCase());
            }
            rs = prepareStatement.executeQuery();
            while (rs.next()) {
                Class<PluginCommon> rule = (Class<PluginCommon>) Class.forName(rs.getString("classname"));
                PluginCommon newInstance = rule.newInstance();
                if (request.getOptionalPolicyTypeFilter() != null) {
                    try {
                        if (Utility.containsPolicyType(rule.newInstance().GetAppliesTo(), request.getOptionalPolicyTypeFilter())) {
                            res.getPlugins().add(Utility.newPlugin(rs.getString("classname"), newInstance.GetDisplayName(), rs.getString("appliesto")));
                        }
                    } catch (Exception ex) {
                        log.log(Level.ERROR, "problem creating instance of plugin" + rs.getString("classname"), ex);
                    }
                } else {
                    res.getPlugins().add(Utility.newPlugin(rs.getString("classname"), newInstance.GetDisplayName(), rs.getString("appliesto")));
                }
            }

        } catch (Exception ex) {
            log.error(ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(con);
        }
        return r;
    }

    /**
     * Gets detailed information about a plugin, since 6.3 Permissions: all
     * authenticated users
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginInformationResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "GetPluginInformation", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetPluginInformation")
    @WebResult(name = "getPluginInformationResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public GetPluginInformationResponse getPluginInformation(
            @WebParam(name = "getPluginInformation", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", partName = "parameters") GetPluginInformation parameters)
            throws AccessDeniedException, ServiceUnavailableException {

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (parameters == null) {
            throw new IllegalArgumentException("request is null");
        }
        GetPluginInformationRequestMsg request = parameters.getRequest();
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (request.getGetPluginInformationRequestWrapper() == null) {
            throw new IllegalArgumentException("request is null");
        }

        Utility.validateClassification(request.getGetPluginInformationRequestWrapper().getClassification());
        AuditLogger.logItem(this.getClass().getCanonicalName(), "getPluginInformation", currentUser, "", (request.getGetPluginInformationRequestWrapper().getClassification()), ctx.getMessageContext());

        if (request.getGetPluginInformationRequestWrapper().getPlugin() == null) {
            throw new IllegalArgumentException("request is null");
        }
        //    for (int i = 0; i < request.getPlugin().size(); i++) {
        if (Utility.stringIsNullOrEmpty(request.getGetPluginInformationRequestWrapper().getPlugin().getClassname())) {
            throw new IllegalArgumentException("plugin[" + "].classname is empty");
        }
        if (Utility.stringIsNullOrEmpty(request.getGetPluginInformationRequestWrapper().getPlugin().getPlugintype())) {
            throw new IllegalArgumentException("plugin[" + "].plugintype is empty");
        }
        //  }

        GetPluginInformationResponse r = new GetPluginInformationResponse();

        GetPluginInformationResponseMsg res = new GetPluginInformationResponseMsg();

        res.setClassification(getCurrentOperatingClassificationLevel());
        //first, confirm that the requested plugin is registered
        boolean registered = false;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            prepareStatement = con.prepareStatement("select * from plugins where classname=? and appliesto=?");
            prepareStatement.setString(1, request.getGetPluginInformationRequestWrapper().getPlugin().getClassname());
            prepareStatement.setString(2, request.getGetPluginInformationRequestWrapper().getPlugin().getPlugintype());
            rs = prepareStatement.executeQuery();
            if (rs.next()) {
                registered = true;
            }
        } catch (Exception ex) {
            log.error(ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(con);
        }
        if (registered) {
            try {
                Class c = Class.forName(request.getGetPluginInformationRequestWrapper().getPlugin().getClassname());
                Object j = c.newInstance();
                if (request.getGetPluginInformationRequestWrapper().getPlugin().getPlugintype().equalsIgnoreCase("SLA_ACTION")) {
                    SLAActionInterface sla = (SLAActionInterface) j;
                    //res.setPlugintype(request.getPlugin().getPlugintype().toUpperCase());
                    res.setDisplayName(sla.GetDisplayName());
                    res.setHelp(sla.GetHtmlFormattedHelp());
                    res.getRequiredParameter().addAll(sla.GetRequiredParameters());
                    res.getOptionalParameter().addAll(sla.GetOptionalParameters());

                }
                //FEDERATION_PUBLISH,SLA_RULE,SLA_ACTION
                if (request.getGetPluginInformationRequestWrapper().getPlugin().getPlugintype().equalsIgnoreCase("SLA_RULE")) {
                    SLARuleInterface sla = (SLARuleInterface) j;
                    // res.setPlugintype(request.getPlugin().getPlugintype().toUpperCase());
                    res.setDisplayName(sla.GetDisplayName());
                    res.setHelp(sla.GetHtmlFormattedHelp());
                    res.getRequiredParameter().addAll(sla.GetRequiredParameters());
                    res.getOptionalParameter().addAll(sla.GetOptionalParameters());

                }
                if (request.getGetPluginInformationRequestWrapper().getPlugin().getPlugintype().equalsIgnoreCase("FEDERATION_PUBLISH")) {
                    FederationInterface sla = (FederationInterface) j;
                    //    res.setPlugintype(request.getPlugin().getPlugintype().toUpperCase());
                    res.setDisplayName(sla.GetDisplayName());
                    res.setHelp(sla.GetHtmlFormattedHelp());
                    res.getRequiredParameter().addAll(sla.GetRequiredParameters());
                    res.getOptionalParameter().addAll(sla.GetOptionalParameters());

                }
            } catch (InstantiationException ex) {
                log.error("unable to load plugin " + request.getGetPluginInformationRequestWrapper().getPlugin().getClassname(), ex);
            } catch (IllegalAccessException ex) {
                log.error("unable to load plugin " + request.getGetPluginInformationRequestWrapper().getPlugin().getClassname(), ex);
            } catch (ClassNotFoundException ex) {
                log.error("unable to load plugin " + request.getGetPluginInformationRequestWrapper().getPlugin().getClassname(), ex);
            } catch (Exception ex) {
                log.error("The implementation for " + request.getGetPluginInformationRequestWrapper().getPlugin().getClassname() + " is faulty and threw and exception. Please contact the developer for resolution", ex);
            }
        }
        r.setResponse(res);
        return r;
    }

    private void validateRestartRunScriptAsGlobalAdministrator(ArrayOfSLA serviceLevelAggrements) {
        if (serviceLevelAggrements == null) {
            return;
        }

        for (int i = 0; i < serviceLevelAggrements.getSLA().size(); i++) {
            for (int x = 0; x < serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().size(); x++) {
                if (serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().get(x).getImplementingClassName().equalsIgnoreCase(SLAActionRestart.class.getCanonicalName())) {
                    UserIdentityUtil.assertGlobalAdministratorRole(UserIdentityUtil.getFirstIdentityToString(ctx), "ValidateRestartRunScriptAsGlobalAdministrator", currentLevel, ctx);
                }
            }
        }
    }

    /**
     * Gets Html Formatted display of a given instance of a plugin, since 6.3
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginHtmlFormattedDisplayResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetPluginHtmlFormattedDisplay", action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetPluginHtmlFormattedDisplay")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration")
    @RequestWrapper(localName = "GetPluginHtmlFormattedDisplay", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginHtmlFormattedDisplay")
    @ResponseWrapper(localName = "GetPluginHtmlFormattedDisplayResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", className = "org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginHtmlFormattedDisplayResponse")
    public GetPluginHtmlFormattedDisplayResponseMsg getPluginHtmlFormattedDisplay(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration") GetPluginHtmlFormattedDisplayRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void encryptIfNeeded(ServicePolicy policy) {
        if (policy == null) {
            return;
        }
        if (policy.getServiceLevelAggrements() == null) {
            return;
        }
        for (int i = 0; i < policy.getServiceLevelAggrements().getSLA().size(); i++) {
            SLA get = policy.getServiceLevelAggrements().getSLA().get(i);
            if (get.getAction() != null) {
                Iterator<SLAAction> iterator = get.getAction().getSLAAction().iterator();
                while (iterator.hasNext()) {
                    SLAAction next = iterator.next();
                    Iterator<NameValuePair> iterator1 = next.getParameterNameValue().iterator();
                    while (iterator1.hasNext()) {
                        NameValuePair next1 = iterator1.next();
                        if (next1.isEncryptOnSave()!=null && next1.isEncryptOnSave()) {
                            next1.setValue(Utility.EN(next1.getValue()));
                            next1.setEncrypted(true);
                            next1.setEncryptOnSave(false);
                        }
                    }
                }
            }
            if (get.getRule() != null) {
                if (get.getRule() instanceof SLARuleGeneric) {
                    SLARuleGeneric rule = (SLARuleGeneric) get.getRule();
                    Iterator<NameValuePair> iterator1 = rule.getParameterNameValue().iterator();
                    while (iterator1.hasNext()) {
                        NameValuePair next1 = iterator1.next();
                        if (next1.isEncryptOnSave()!=null && next1.isEncryptOnSave()) {
                            next1.setValue(Utility.EN(next1.getValue()));
                            next1.setEncrypted(true);
                            next1.setEncryptOnSave(false);
                        }
                    }
                }
                //TODO recursive and/or/not stuff
            }

        }
    }
    
    private void encryptIfNeeded(FederationPolicyCollection policy) {
        if (policy == null) {
            return;
        }
        if (policy.getFederationPolicy() == null) {
            return;
        }
        for (int i = 0; i < policy.getFederationPolicy().size(); i++) {
            FederationPolicy get = policy.getFederationPolicy().get(i);
            if (get.getParameterNameValue() != null) {
                Iterator<NameValuePair> iterator1 = get.getParameterNameValue().iterator();
                while (iterator1.hasNext()) {

                    NameValuePair next1 = iterator1.next();
                    if (next1.isEncryptOnSave() != null && next1.isEncryptOnSave()) {
                        next1.setValue(Utility.EN(next1.getValue()));
                        next1.setEncrypted(true);
                        next1.setEncryptOnSave(false);
                    }
                }
            }
        }
    }

}
