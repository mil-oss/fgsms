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
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;
import org.apache.log4j.Level;

import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;

/**
 * performs audit logging via the fgsms Configuration
 * database. This is only usable on the FGSMS server
 *
 * @author AO
 */
public class AuditLogger {

    static final Logger log = Logger.getLogger(AuditLogger.class.getCanonicalName());

    /**
     * adds an item to the audit log, this is a wrapper function
     *
     * @param classname
     * @param method
     * @param username
     * @param memo
     * @param classification, if null, not specified will be used, otherwise a
     * prettyprint version of the classificaiton level will be used
     * @param messageContext
     */
    public static void logItem(String classname, String method, String username, String memo, SecurityWrapper classification, MessageContext messageContext) {
        if (classification == null || classification.getClassification() == null || classification.getCaveats() == null) {
            logItem(classname, method, username, memo, unspecified, messageContext);
        } else {
            logItem(classname, method, username, memo, Utility.ICMClassificationToString(classification.getClassification()) + " " + classification.getCaveats(), messageContext);
        }
    }
    public static final String unspecified = "UNSPECIFIED";

    /**
     * strips out newlines to prevent possible log forging attacks
     * @param input
     * @return 
     */
    private static String logForgePrevention(String input){
        if (input==null)
            return null;
        return input.replace("\n", "");
    }
    /**
     * automatically adds JVM memory stats and logs to the DEBUG log all input
     * data
     *
     * @param classname
     * @param method
     * @param username
     * @param memo
     * @param classification
     * @param messageContext
     */
    public static void logItem(String classname, String method, String username, String memo, String classification, MessageContext messageContext) {
        memo += " JVM Free:" + Runtime.getRuntime().freeMemory() + " Total: " + Runtime.getRuntime().totalMemory();
        double used = (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (double) Runtime.getRuntime().totalMemory();
        memo += " " + String.valueOf(used) + "% in use, processors: " + Runtime.getRuntime().availableProcessors();

        log.log(Level.DEBUG, "fgsms Audit Log: " + logForgePrevention(classname) + " " + logForgePrevention(method) + " username: " + logForgePrevention(username) + " memo: " + logForgePrevention(memo));
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com=null;
        try {

            if (con == null) {
                log.log(Level.FATAL, "database not available");
                System.out.println("database not available! cannot record audit logs!");
                System.err.println("database not available! cannot record audit logs!");
                return;
            }
            com = con.prepareStatement("INSERT INTO auditlog("
                    + "utcdatetime, username, classname, method, memo, classification, ipaddress) VALUES (?, ?, ?, ?, ?,?,?);");
            com.setLong(1, System.currentTimeMillis());
            com.setString(2, username);
            com.setString(3, classname);
            com.setString(4, method);
            com.setBytes(5, (memo).trim().getBytes(Constants.CHARSET));
            com.setString(6, classification);

            if (messageContext != null) {
                try {
                    HttpServletRequest ctx = null;
                    ctx = (HttpServletRequest) messageContext.get(messageContext.SERVLET_REQUEST);
                    com.setString(7, ctx.getRemoteAddr());
                } catch (Exception ex) {
                    com.setString(7, "NA");
                }
            } else {
                com.setString(7, "NA");
            }


            com.execute();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to log audit event", ex);
        } finally {
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);

        }
    }
}
