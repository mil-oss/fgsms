/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.das.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.GregorianCalendar;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.dataaccessservice.AccessDeniedException;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ArrayOfTransactionLog;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsRequestMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ServiceUnavailableException;
import org.miloss.fgsms.services.interfaces.dataaccessservice.TransactionLog;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import static org.miloss.fgsms.services.das.impl.DAS4jBean.log;
import static org.miloss.fgsms.services.das.impl.DAS4jBean.getCurrentClassificationLevel;

/**
 *
 * @author AO
 */
public class QueryGetMessageLogsByRange {

    public static GetMessageLogsResponseMsg getMessageLogsByRange(
            GetMessageLogsRequestMsg request, WebServiceContext ctx)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        boolean ga = false;
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(DAS4jBean.class.getCanonicalName(), "getMessageLogs", currentUser, "", (request.getClassification()), ctx.getMessageContext());

        if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("time range is null");
        }
        if (Utility.stringIsNullOrEmpty(request.getURL())) {
            UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getMessageLogs", (request.getClassification()), ctx);
            ga = true;
        }
        UserIdentityUtil.assertAuditAccess(request.getURL(), currentUser, "getMessageLogs", (request.getClassification()), ctx);

        if (request.isFaultsOnly() && request.isSlaViolationsOnly()) {
            throw new IllegalArgumentException("specify SLA Faults or Faults, but not both");
        }

        //validate the request
        if (Utility.stringIsNullOrEmpty(request.getAgentType())
                && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                && Utility.stringIsNullOrEmpty(request.getServicehostname())
                && Utility.stringIsNullOrEmpty(request.getURL())) {
            throw new IllegalArgumentException("at least one of URL, Agent, Monitor hostname, Service hostname");
        }

        Connection con = Utility.getPerformanceDBConnection();
        ResultSet s = null;
        PreparedStatement comm = null;
        PreparedStatement comm2 = null;
        try {

            GetMessageLogsResponseMsg ret = new GetMessageLogsResponseMsg();

            int totalrecords = 0;

            //if URL is defined
            if (!Utility.stringIsNullOrEmpty(request.getURL())) {

                //and nothing else is ok 1
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 1");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where(URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null  "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?);");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());

                    }
                }// </editor-fold>

                //URL AND agent only ok2
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 2");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and agenttype=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and agenttype=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and agenttype=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                    }
                }// </editor-fold>

                //URL AND agent AND service host ok3
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 3");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getServicehostname());

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getServicehostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getServicehostname());
                    }
                }// </editor-fold>

                //URL AND agent AND monitor host, ok4
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 4");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and agenttype=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getMonitorhostname());

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and agenttype=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getMonitorhostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and agenttype=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setLong(2, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(3, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(4, request.getAgentType());
                        comm.setString(5, request.getMonitorhostname());
                    }
                }// </editor-fold>

                //URL AND serivce host AND monitor host, ok5
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 5");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and hostingsource=? and monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getServicehostname());
                        comm2.setString(6, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and hostingsource=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getServicehostname());
                        comm.setString(6, request.getMonitorhostname());

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and hostingsource=? and monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getServicehostname());
                        comm2.setString(6, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and hostingsource=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getServicehostname());
                        comm.setString(6, request.getMonitorhostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and hostingsource=? and monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getServicehostname());
                        comm2.setString(6, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and hostingsource=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getServicehostname());
                        comm.setString(6, request.getMonitorhostname());
                    }
                }// </editor-fold>

                //URL AND service host ok6
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 6");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?)  and hostingsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false  and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        //comm.setString(5, request.getAgentType());
                        comm.setString(5, request.getServicehostname());

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?)  and hostingsource=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getServicehostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?)  and hostingsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?)  and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getServicehostname());
                    }
                }// </editor-fold>

                //URL AND monitor host k7
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 7");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?)  and monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false  and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        //comm.setString(5, request.getAgentType());
                        comm.setString(5, request.getMonitorhostname());

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?)  and monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getMonitorhostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?)  and monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?)  and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getMonitorhostname());
                    }
                }// </editor-fold>

                //URL AND agent AND service host AND monitoring host k8
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 8");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=? and monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getServicehostname());
                        comm2.setString(7, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and agenttype=? and hostingsource=?  and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getServicehostname());
                        comm.setString(7, request.getMonitorhostname());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=? and monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getServicehostname());
                        comm2.setString(7, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and agenttype=? and hostingsource=?  and monitorsource=?  "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getServicehostname());
                        comm.setString(7, request.getMonitorhostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=? and monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(5, request.getAgentType());
                        comm2.setString(6, request.getServicehostname());
                        comm2.setString(7, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and agenttype=? and hostingsource=?  and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(4, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(5, request.getAgentType());
                        comm.setString(6, request.getServicehostname());
                        comm.setString(7, request.getMonitorhostname());
                    }
                }// </editor-fold>

            } else //URL is not defined
            {
                if (!ga) {
                    throw new SecurityException();
                }
//works ok
                //NO url, nothing defined k9
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 9");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and success = false;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?);");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                    }
                }// </editor-fold>
//works
                //NO url, only agenttype  k
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 10");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and success=false");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and agenttype=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getAgentType());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and agenttype=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getAgentType());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=?;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and agenttype=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getAgentType());
                    }
                }// </editor-fold>

//works
                //NO url, only serivce host k
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 11");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and hostingsource=? and success=false;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getServicehostname());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and hostingsource=? and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getServicehostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and hostingsource=?;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getServicehostname());
                    }
                }// </editor-fold>

//works
                //NO url, only monitor host k
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 12");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and success=false;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=?;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                    }
                }// </editor-fold>

//works
                //NO url, monitor host and service host  and agent type k
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 13");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and agenttype=? and hostingsource=? and success=false;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getAgentType());
                        comm2.setString(5, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and monitorsource=?  and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getAgentType());
                        comm.setString(5, request.getServicehostname());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and agenttype=? and hostingsource=? and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getAgentType());
                        comm2.setString(5, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and monitorsource=?  and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getAgentType());
                        comm.setString(5, request.getServicehostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and agenttype=? and hostingsource=?;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getAgentType());
                        comm2.setString(5, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and monitorsource=?  and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getAgentType());
                        comm.setString(5, request.getServicehostname());
                    }
                }// </editor-fold>

//works
                //NO url, monitor host and service host k
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 14");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=?  and hostingsource=? and success=false;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and monitorsource=?   and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getServicehostname());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=?  and hostingsource=? and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and monitorsource=?  and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());

                        comm.setString(4, request.getServicehostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=?  and hostingsource=?;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and monitorsource=?  and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getServicehostname());
                    }
                }// </editor-fold>

//works
                //NO url, monitor host and agent type k
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 15");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and agenttype=? and success=false;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and monitorsource=?  and agenttype=?  "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getAgentType());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and agenttype=? and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and monitorsource=?  and agenttype=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getAgentType());

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and monitorsource=? and agenttype=?;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getMonitorhostname());
                        comm2.setString(4, request.getAgentType());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?) and monitorsource=?  and agenttype=?  "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getMonitorhostname());
                        comm.setString(4, request.getAgentType());

                    }
                }// </editor-fold>

//works
                //NO url, source host and agent type
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 16");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=? and success=false;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "success=false and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=? and \"slafault\" is not null;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and "
                                + "\"slafault\" is not null and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (UTCdatetime > ? "
                                + ") and (UTCdatetime < ?) and agenttype=? and hostingsource=?;");

                        comm2.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm2.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        ResultSet results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "(UTCdatetime > ?) and "
                                + "(UTCdatetime < ?)  and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC;", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        comm.setLong(1, request.getRange().getStart().getTimeInMillis());
                        comm.setLong(2, request.getRange().getEnd().getTimeInMillis());
                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                    }
                }// </editor-fold>

            }

            if (comm == null) {
                log.log(Level.ERROR, "unexpected error condition, prepared statement is null");
                ServiceUnavailableException code = new ServiceUnavailableException("", null);
                code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                throw code;
            }

            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            //comm.setFetchSize(request.getRecords());
//            comm.setMaxRows(request.getRecords());
            s = comm.executeQuery();

            ArrayOfTransactionLog logs = new ArrayOfTransactionLog();
            if (request.getOffset() != null && request.getOffset() > 0) {
                s.absolute(request.getOffset());
            }
            int recordcount = 0;
            while (s.next()) {
                recordcount++;
                if (recordcount > request.getRecords()) {
                    break;
                }
                TransactionLog tl = new TransactionLog();

                tl.setURL(s.getString("uri"));
                tl.setHasRequestMessage(false);
                tl.setHasResponseMessage(false);
                tl.setAction(s.getString("soapaction"));
                if (!Utility.stringIsNullOrEmpty(s.getString("RequestXML"))) {
                    tl.setHasRequestMessage(true);
                }
                tl.setRequestSize(s.getLong("requestSize"));
                tl.setResponseSize(s.getLong("responseSize"));
                if (!Utility.stringIsNullOrEmpty(s.getString("ResponseXML"))) {
                    tl.setHasResponseMessage(true);
                }
                tl.setIsFault(s.getBoolean("success"));
                tl.setMonitorHostname(s.getString("MonitorSource"));
                tl.setServiceHostname(s.getString("HostingSource"));
                tl.setResponseTime(s.getLong("ResponseTimeMS"));
                tl.setTransactionId(s.getString("TransactionID"));

                // boolean slafault = false;
                String t = null;
                try {
                    t = s.getString("slafault");

                } catch (Exception ex) {
                }
                if (Utility.stringIsNullOrEmpty(t)) {
                    tl.setIsSLAFault(false);
                } else {
                    tl.setIsSLAFault(true);
                    try {
                        PreparedStatement slacom = con.prepareStatement("select msg from slaviolations where uri=? and incidentid=?;");
                        slacom.setString(1, request.getURL());
                        slacom.setString(2, t);
                        ResultSet tempset = slacom.executeQuery();
                        if (tempset.next()) {
                            byte[] x = (byte[]) tempset.getBytes("msg");
                            if (x != null) {
                                tl.setSlaFaultMsg(new String(x,Constants.CHARSET));
                            }
                        }
                        tempset.close();
                        slacom.close();
                    } catch (Exception ex) {
                        tl.setSlaFaultMsg("Fault message unavailable");
                        log.log(Level.ERROR, "error parsing sla fault message from db", ex);
                    }
                }

                GregorianCalendar c = new GregorianCalendar();
                c.setTimeInMillis(s.getLong("utcdatetime"));
                tl.setTimestamp((c));

                if (!Utility.stringIsNullOrEmpty(s.getString("ConsumerIdentity"))) {
                    String temp = s.getString("ConsumerIdentity");
                    if (!Utility.stringIsNullOrEmpty(temp)) {
                        String[] ids = temp.split(";");
                        //ArrayOfstring l = new ArrayOfstring();
                        for (int i = 0; i < ids.length; i++) {
                            //l.getString().add(ids[i]);
                            tl.getIdentity().add(ids[i]);
                        }
                        // QName id = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "Identity");

                        //tl.setIdentity(new JAXBElement<ArrayOfstring>(id, ArrayOfstring.class, l));
                    }
                }

                logs.getTransactionLog().add(tl);
            }
            ret.setTotalRecords(totalrecords);

            ret.setLogs(logs);
            ret.setClassification(getCurrentClassificationLevel());

            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "getMessageLogs", ex);
        } finally {
            DBUtils.safeClose(s);
            DBUtils.safeClose(comm2);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;
    }

}
