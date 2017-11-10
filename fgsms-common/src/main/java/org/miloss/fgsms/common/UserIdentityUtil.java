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
package org.miloss.fgsms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.log4j.Level;

import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;

/**
 * Performs all access control functions of fgsms based on ACLs and Roles.
 * Function of this class is critical has is it the sole location for access
 * control processing
 *
 * @author AO
 */
public class UserIdentityUtil {

    public static final String logname = "fgsms.UserIdentityUtil";
    static final Logger log = Logger.getLogger(logname);

    /**
     * assertReadAccess , checks the servlet context first, then the database
     *
     * @param uri
     * @param currentUser
     * @param fromFunction
     * @param classification
     */
    public static void assertReadAccess(final String uri, final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session != null) {
                {
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                        return;
                    }
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_WRITE)) {
                        return;
                    }
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_READ)) {
                        return;
                    }
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_AUDITOR)) {
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select count(*)"
                    + "where  ? in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username=? AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.ReadObject=true OR "
                    + "	UserPermissions.WriteObject=true OR"
                    + "	UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + "or 'everyone' in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username='everyone' AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.ReadObject=true OR "
                    + "	UserPermissions.WriteObject=true OR"
                    + "	UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + "or ?  in ("
                    + "	select Users.Username"
                    + "	from Users"
                    + "	Where Users.Username=?"
                    + "	AND Users.rolecol='admin');");
            comm.setString(1, currentUser);
            comm.setString(2, currentUser);
            comm.setString(3, uri);
            comm.setString(4, uri);
            comm.setString(5, currentUser);
            comm.setString(6, currentUser);
            r = comm.executeQuery();
            r.next();
            int right = 0;
            right = r.getInt(1);
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            if (right == 0) {
                log.log(Level.ERROR, currentUser + " does not have fgsms read rights for " + uri + " from " + fromFunction);
                AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "read deny", classification, null);
                throw new SecurityException("Access Denied");
            }
        } // end assertReadAccess
        catch (Exception ex) {
            if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            }
            log.log(Level.ERROR, "Error caught querying database for " + currentUser + ":fgsms read rights for " + uri + " from " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    } // end assertReadAccess

    /**
     * assertWriteAccess throws an exception if the role is not present , checks
     * the servlet context first, then the database
     *
     * @param uri
     * @param currentUser
     * @param fromFunction
     * @param classification
     */
    public static void assertWriteAccess(final String uri, final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return;
                }
                if (session.isUserInRole(Constants.ROLES_GLOBAL_WRITE)) {
                    return;
                }
                if (session.isUserInRole(Constants.ROLES_GLOBAL_AUDITOR)) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select count(*)"
                    + "where  ? in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username=? AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.WriteObject=true OR"
                    + "	UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + " or 'everyone' in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username='everyone' AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.WriteObject=true OR"
                    + "	UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + "or ?  in ("
                    + "	select Users.Username"
                    + "	from Users"
                    + "	Where Users.Username=?"
                    + "	AND Users.rolecol='admin');");
            comm.setString(1, currentUser);
            comm.setString(2, currentUser);
            comm.setString(3, uri);
            comm.setString(4, uri);
            comm.setString(5, currentUser);
            comm.setString(6, currentUser);
            r = comm.executeQuery();
            r.next();
            int right = 0;
            right = r.getInt(1);
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            if (right == 0) {
                log.log(Level.ERROR, currentUser + " does not have fgsms write rights for " + uri + " from " + fromFunction);
                AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "write deny", classification, null);
                throw new SecurityException("Access Denied");
            }

        } // end assertReadAccess
        catch (Exception ex) {
            if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            }
            log.log(Level.ERROR, "Error caught querying database for " + currentUser + ":fgsms write rights for " + uri + " from " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    } // end assertWriteAccess

    /**
     * assertAuditAccess throws a security exception if the user does not
     * contain the role audit for a specific URI or the global audit role ,
     * checks the servlet context first, then the database
     *
     * @param uri
     * @param currentUser
     * @param fromFunction
     * @param classification
     */
    public static void assertAuditAccess(final String uri, final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session.isUserInRole(Constants.ROLES_GLOBAL_AUDITOR)) {
                return;
            }
            if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                return;
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement(
                    "Select count(*)"
                    + "where ?  in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username=? AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + "or 'everyone'  in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username='everyone' AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + "or ?  in ("
                    + "	select Users.Username"
                    + "	from Users"
                    + "	Where Users.Username=?"
                    + "	AND (Users.rolecol='admin' OR Users.rolecol='audit'));");
            comm.setString(1, currentUser);
            comm.setString(2, currentUser);
            comm.setString(3, uri);
            comm.setString(4, uri);
            comm.setString(5, currentUser);
            comm.setString(6, currentUser);
            r = comm.executeQuery();
            r.next();
            int right = 0;
            right = r.getInt(1);
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            if (right == 0) {
                AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "audit deny", classification, null);
                log.log(Level.ERROR, currentUser + " does not have fgsms audit rights for " + uri + " from " + fromFunction);
                throw new SecurityException("Access Denied");
            }

        } // end assertReadAccess
        catch (Exception ex) {
            if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            }
            log.log(Level.ERROR, "Error caught querying database for " + currentUser + ":fgsms audit rights for " + uri + " from " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    } // end assertAuditAccess

    /**
     * throws a security exception of the user does not have the role administer
     * for a specific uri or global administrator, checks the servlet context
     * first, then the database
     *
     * @param uri
     * @param currentUser
     * @param fromFunction
     * @param classification
     */
    public static void assertAdministerAccess(final String uri, final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select count(*)"
                    + "where  ? in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username=? AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.AdministerObject=true))"
                    + "	)"
                    + "or ?  in ("
                    + "	select Users.Username"
                    + "	from Users"
                    + "	Where Users.Username=?"
                    + "	AND Users.rolecol='admin');");
            comm.setString(1, currentUser);
            comm.setString(2, currentUser);
            comm.setString(3, uri);
            comm.setString(4, currentUser);
            comm.setString(5, currentUser);
            r = comm.executeQuery();
            r.next();
            int right = 0;
            right = r.getInt(1);
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            if (right == 0) {
                log.log(Level.ERROR, currentUser + " does not have fgsms administer rights for " + uri + " from " + fromFunction);
                AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "admin deny", classification, null);
                throw new SecurityException("Access Denied");
            }

        } // end assertReadAccess
        catch (Exception ex) {
            if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            }
            log.log(Level.ERROR, "Error caught querying database for " + currentUser + ":fgsms administer rights for " + uri + " from " + fromFunction + "Msg: " + ex.getLocalizedMessage());
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

    } // end assertAdministerAccess

    /**
     * Assert global admin role, throws an exception if the role is not present,
     * checks the servlet context first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param classification
     * @param session
     */
    public static void assertGlobalAdministratorRole(final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        if (Utility.stringIsNullOrEmpty(currentUser)) {
            throw new SecurityException("Access Denied");
        }
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            while (r.next()) {
                String s = r.getString(1);
                if (!Utility.stringIsNullOrEmpty(s)) {
                    if (s.equalsIgnoreCase("admin")) {
                        DBUtils.safeClose(r);
                        DBUtils.safeClose(comm);
                        DBUtils.safeClose(con);
                        return;
                    }
                }
            }

        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms global admin rights. " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        log.log(Level.ERROR, currentUser + " does not have fgsms Global Admin rights." + fromFunction);
        AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "global admin deny", classification, null);
        throw new SecurityException("Access Denied");
    } // end assertGlobalAdministratorRole

    /**
     * return true if the user has the global admin role, , checks the servlet
     * context first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param classification
     * @param session
     * @return
     */
    public static boolean hasGlobalAdministratorRole(final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {
            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            while (r.next()) {
                String s = null;
                s = r.getString("rolecol");
                if (Utility.stringIsNullOrEmpty(s)) {
                    DBUtils.safeClose(r);
                    DBUtils.safeClose(comm);
                    DBUtils.safeClose(con);
                    return false;
                }
                if (s.equalsIgnoreCase("admin")) {
                    DBUtils.safeClose(r);
                    DBUtils.safeClose(comm);
                    DBUtils.safeClose(con);
                    return true;
                }
            }
        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms global admin rights. Assuming the answer is no." + fromFunction, ex);
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        return false;
    } // end hasGlobalAdministratorRole

    /**
     * Current only works for HTTP auth methods, unknown if SOAP credentials
     * will be available here
     *
     * @param webctx
     * @return
     */
    public static String getFirstIdentityToString(final WebServiceContext webctx) {

        if (webctx == null) {
            return "anonymous";
        }
        if (webctx.getUserPrincipal() != null && !Utility.stringIsNullOrEmpty(webctx.getUserPrincipal().getName())) {
            return webctx.getUserPrincipal().getName();
        }

        MessageContext mc = webctx.getMessageContext();
        try {//org.apache.cxf.configuration.security.AuthorizationPolicy
            //MessageContext mc = ctx.getMessageContext();
            if (mc == null) {
                return "anonymous";
            }
            //hack for CXF stacks
            Object cxfAuthz = mc.get("org.apache.cxf.configuration.security.AuthorizationPolicy");
            if (cxfAuthz != null) {
                String user = CXFUserIdentifyUtil.getFirstIdentityToString(cxfAuthz);
                if (!Utility.stringIsNullOrEmpty(user)) {
                    return user;
                }
            }
            HttpServletRequest session = ((HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST));
            if (session == null || session.getUserPrincipal() == null) {
                return "anonymous";
            } else {
                if (Utility.stringIsNullOrEmpty(session.getAuthType())) {
                    return "anonymous";
                }
                if (!Utility.stringIsNullOrEmpty(session.getUserPrincipal().getName())) {
                    return session.getUserPrincipal().getName();
                }

            }
        } catch (Exception ex) {
            log.log(Level.ERROR,
                    "Error caught determining the current user identity. Assuming anonymous.", ex);
        }

        return "anonymous";

    }

    /**
     * Assert Agent Role, throws a security exception if the user does not have
     * the agent role, checks the servlet context first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param currentLevel
     * @param mc
     */
    public static void assertAgentRole(final String currentUser, final String fromFunction, final SecurityWrapper currentLevel, final WebServiceContext session) {
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_AGENT)) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            if (r.next()) {
                String s = null;
                s = r.getString(1);
                if (!Utility.stringIsNullOrEmpty(s) && s.equalsIgnoreCase("agent")) {
                    //remnoved 1-7-2012 not sure why this was here || r.getString(1).equalsIgnoreCase("admin")) {
                    DBUtils.safeClose(r);
                    DBUtils.safeClose(comm);
                    DBUtils.safeClose(con);
                    return;
                }
            }

        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms agent rights. " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        log.log(Level.ERROR, currentUser + " does not have fgsms agent rights." + fromFunction);
        AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "agent deny", currentLevel, null);

        throw new SecurityException("Access Denied");

    }

    /**
     * return true if the current user is an agent, checks the servlet context
     * first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param classification
     * @param session
     * @return
     */
    public static boolean isTrustedAgent(final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_AGENT)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {
            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            if (r.next()) {
                String s = r.getString(1);
                if (!Utility.stringIsNullOrEmpty(s) && s.equalsIgnoreCase("agent")) {
                    DBUtils.safeClose(r);
                    DBUtils.safeClose(comm);
                    DBUtils.safeClose(con);
                    return true;
                }
            }
        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms agent rights. " + fromFunction, ex);
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        return false;

    }

    /**
     * throws an exception if the user is a global admin or an agent , checks
     * the servlet context first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param currentLevel
     * @param mc
     */
    public static void assertAdminOrAgentRole(final String currentUser, final String fromFunction, final SecurityWrapper currentLevel, final WebServiceContext mc) {
        try {
            if (mc != null) {
                if (mc.isUserInRole(Constants.ROLES_GLOBAL_AGENT)) {
                    return;
                }
                if (mc.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return;
                }

            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            if (r.next()) {
                String s = r.getString(1);
                if (!Utility.stringIsNullOrEmpty(s) && (s.equalsIgnoreCase("agent") || s.equalsIgnoreCase("admin"))) {
                    DBUtils.safeClose(r);
                    DBUtils.safeClose(comm);
                    DBUtils.safeClose(con);
                    return;
                }
            }

        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms agent or admin rights. " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        log.log(Level.ERROR, currentUser + " does not have fgsms agent or admin rights." + fromFunction);
        AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "agent deny", currentLevel, null);

        throw new SecurityException("Access Denied");

    }

    public static void assertAuditRole(final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        if (Utility.stringIsNullOrEmpty(currentUser)) {
            throw new SecurityException("Access Denied");
        }
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_AUDITOR)) {
                    return;
                }
                if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            while (r.next()) {
                String s = r.getString(1);
                if (!Utility.stringIsNullOrEmpty(s)) {
                    if (s.equalsIgnoreCase("audit") || s.equalsIgnoreCase("admin")) {
                        DBUtils.safeClose(r);
                        DBUtils.safeClose(comm);
                        DBUtils.safeClose(con);
                        return;
                    }
                }
            }

        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms global audit rights. " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        log.log(Level.ERROR, currentUser + " does not have fgsms Global audit rights." + fromFunction);
        AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "global audit deny", classification, null);
        throw new SecurityException("Access Denied");
    }

    /**
     * return true if the user has the global admin role, , checks the servlet
     * context first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param classification
     * @param mc
     * @return
     */
    public static boolean hasGlobalAuditRole(final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_AUDITOR)) {
                    return true;
                }
                if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            while (r.next()) {
                String s = null;
                s = r.getString("rolecol");
                if (Utility.stringIsNullOrEmpty(s)) {
                    DBUtils.safeClose(r);
                    DBUtils.safeClose(comm);
                    DBUtils.safeClose(con);
                    return false;
                }
                if (s.equalsIgnoreCase("audit") || s.equalsIgnoreCase("admin")) {
                    DBUtils.safeClose(r);
                    DBUtils.safeClose(comm);
                    DBUtils.safeClose(con);
                    return true;
                }
            }
        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms global audit rights. Assuming the answer is no." + fromFunction, ex);
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        return false;
    } // end hasGlobalAdministratorRole

    /**
     * Assert global admin role, throws an exception if the role is not present,
     * checks the servlet context first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param classification
     * @param mc
     */
    public static void assertGlobalAuditRole(final String currentUser, final String fromFunction, final SecurityWrapper classification, final WebServiceContext session) {
        if (Utility.stringIsNullOrEmpty(currentUser)) {
            throw new SecurityException("Access Denied");
        }
        try {
            if (session != null) {
                if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                    return;
                }
                if (session.isUserInRole(Constants.ROLES_GLOBAL_AUDITOR)) {
                    return;
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select rolecol from Users where Username=?;");
            comm.setString(1, currentUser);
            r = comm.executeQuery();
            while (r.next()) {
                String s = r.getString(1);
                if (!Utility.stringIsNullOrEmpty(s)) {
                    if (s.equalsIgnoreCase("admin") || s.equalsIgnoreCase("audit")) {
                        DBUtils.safeClose(r);
                        DBUtils.safeClose(comm);
                        DBUtils.safeClose(con);
                        return;
                    }
                }
            }

        } // end assertGlobalAdministratorRole
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught determining if " + currentUser + " has fgsms global audit rights. " + fromFunction, ex);
            throw new SecurityException("Access Denied");
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        log.log(Level.ERROR, currentUser + " does not have fgsms Global audit rights." + fromFunction);
        AuditLogger.logItem(UserIdentityUtil.class.getCanonicalName(), fromFunction, currentUser, "global audit deny", classification, null);

        throw new SecurityException("Access Denied");

    } // end assertGlobalAdministratorRole

    /**
     * return true if the user has the global admin role, , checks the servlet
     * context first, then the database
     *
     * @param currentUser
     * @param fromFunction
     * @param url
     * @param classification
     * @param session
     * @return
     */
    public static boolean hasReadAccess(final String currentUser, final String fromFunction, final String uri, final SecurityWrapper classification, final WebServiceContext session) {
        try {
            if (session != null) {
                {
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_ADMINISTRATOR)) {
                        return true;
                    }
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_WRITE)) {
                        return true;
                    }
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_READ)) {
                        return true;
                    }
                    if (session.isUserInRole(Constants.ROLES_GLOBAL_AUDITOR)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error type casting servlet request context", ex);
        }

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            comm = con.prepareStatement("Select count(*)"
                    + "where  ? in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username=? AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.ReadObject=true OR "
                    + "	UserPermissions.WriteObject=true OR"
                    + "	UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + "or 'everyone' in ("
                    + "	Select Username"
                    + "	from UserPermissions"
                    + "	Where (UserPermissions.Username='everyone' AND"
                    + "	UserPermissions.ObjectURI=?)"
                    + "	AND"
                    + "	((UserPermissions.ReadObject=true OR "
                    + "	UserPermissions.WriteObject=true OR"
                    + "	UserPermissions.AdministerObject=true OR"
                    + "	UserPermissions.AuditObject=true))"
                    + "	)"
                    + "or ?  in ("
                    + "	select Users.Username"
                    + "	from Users"
                    + "	Where Users.Username=?"
                    + "	AND Users.rolecol='admin');");
            comm.setString(1, currentUser);
            comm.setString(2, currentUser);
            comm.setString(3, uri);
            comm.setString(4, uri);
            comm.setString(5, currentUser);
            comm.setString(6, currentUser);
            r = comm.executeQuery();
            r.next();
            int right = r.getInt(1);
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            return right != 0;
        } // end assertReadAccess // end assertReadAccess
        catch (Exception ex) {
            log.log(Level.ERROR, "Error caught querying database for " + currentUser + ":fgsms read rights for " + uri + " from " + fromFunction, ex);
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }

        return false;

    } // end 
}
