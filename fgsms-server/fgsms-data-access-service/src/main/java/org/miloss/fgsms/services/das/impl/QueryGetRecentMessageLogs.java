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
import javax.jws.WebParam;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import static org.miloss.fgsms.services.das.impl.DAS4jBean.log;
import org.miloss.fgsms.services.interfaces.dataaccessservice.AccessDeniedException;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ArrayOfTransactionLog;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetRecentMessageLogsRequestMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ServiceUnavailableException;
import org.miloss.fgsms.services.interfaces.dataaccessservice.TransactionLog;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;

/**
 *
 * @author AO
 */
public class QueryGetRecentMessageLogs {

    public static GetMessageLogsResponseMsg getRecentMessageLogs(GetRecentMessageLogsRequestMsg request,
            WebServiceContext ctx) throws AccessDeniedException, ServiceUnavailableException{
         String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        if (request.isFaultsOnly() && request.isSlaViolationsOnly()) {
            throw new IllegalArgumentException("specifiy SLA Faults OR Faults Only, but not both");
        }
        //validate the request
        if (Utility.stringIsNullOrEmpty(request.getAgentType())
                && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                && Utility.stringIsNullOrEmpty(request.getServicehostname())
                && Utility.stringIsNullOrEmpty(request.getURL())) {
            throw new IllegalArgumentException("at least one of URL, Agent, Monitor hostname, Service hostname");
        }

        Utility.validateClassification(request.getClassification());
        AuditLogger.logItem(DAS4jBean.class.getCanonicalName(), "getRecentMessageLogs", currentUser, "", (request.getClassification()), ctx.getMessageContext());
        boolean ga = false;
        if (Utility.stringIsNullOrEmpty(request.getURL())) {
            UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "getRecentMessageLogs", (request.getClassification()), ctx);
            ga = true;
        }
        UserIdentityUtil.assertAuditAccess(request.getURL(), currentUser, "getRecentMessageLogs", (request.getClassification()), ctx);

        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement comm = null;
        PreparedStatement comm2 = null;
        ResultSet s = null;
        ResultSet results2=null;
        try {

            int offset = 0;
            int limit = 100;
            if (request.getRecords() >= 1) {
                limit = request.getRecords();
            }
            if (limit < 0 || limit > 300) {
                limit = 300;
            }
            if (request.getOffset() != null && request.getOffset() > 0) {
                offset = request.getOffset();
            }

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
                        comm2 = con.prepareStatement("select faults from RawDatatally where (URI=?);");
                        comm2.setString(1, request.getURL());

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = Long.valueOf(results2.getLong(1)).intValue();
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select slafault from RawDatatally where (URI=?) ;");
                        comm2.setString(1, request.getURL());

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = Long.valueOf(results2.getLong(1)).intValue();
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where(URI=? or originalurl=?) and "
                                + "\"slafault\" is not null  "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    } else {
                        comm2 = con.prepareStatement("select (success + faults) from rawdatatally where (URI=?) ;");
                        comm2.setString(1, request.getURL());

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            long x = (results2.getLong(1));

                            totalrecords = Long.valueOf(x).intValue();
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?)  "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setLong(3, limit);
                        comm.setLong(4, offset);

                    }
                }// </editor-fold>

                //URL AND agent only ok2
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 2");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " agenttype=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) "
                                + " and agenttype=? and \"slafault\" is not null ;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "\"slafault\" is not null and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and  agenttype=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);

                    }
                }// </editor-fold>

                //URL AND agent AND service host ok3
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 3");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " agenttype=? and hostingsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());

                        comm.setLong(5, limit);
                        comm.setLong(6, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " agenttype=? and hostingsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "\"slafault\" is not null and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " agenttype=? and hostingsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, offset);

                    }
                }// </editor-fold>

                //URL AND agent AND monitor host, ok4
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 4");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + "agenttype=? and monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false and agenttype=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and agenttype=? and monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "\"slafault\" is not null and agenttype=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and agenttype=? and monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());
                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + " agenttype=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, offset);

                    }
                }// </editor-fold>

                //URL AND serivce host AND monitor host, ok5
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 5");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + "hostingsource=? and monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        comm2.setString(4, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false and hostingsource=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getServicehostname());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, limit);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " hostingsource=? and monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        comm2.setString(4, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "\"slafault\" is not null and hostingsource=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getServicehostname());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and hostingsource=? and monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        comm2.setString(4, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + " hostingsource=? and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getServicehostname());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setLong(5, limit);
                        comm.setLong(6, offset);
                    }
                }
                // </editor-fold>

                //URL AND serivce host AND monitor host AND agent
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 5-2");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + "hostingsource=? and monitorsource=? and agenttype=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        comm2.setString(4, request.getMonitorhostname());
                        comm2.setString(5, request.getAgentType());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false and hostingsource=? and monitorsource=? and agenttype=?"
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getServicehostname());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setString(5, request.getAgentType());
                        comm.setLong(6, limit);
                        comm.setLong(7, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " hostingsource=? and monitorsource=? and \"slafault\" is not null and agenttype=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        comm2.setString(4, request.getMonitorhostname());
                        comm2.setString(5, request.getAgentType());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "\"slafault\" is not null and hostingsource=? and monitorsource=? and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getServicehostname());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setString(5, request.getAgentType());
                        comm.setLong(6, limit);
                        comm.setLong(7, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and hostingsource=? and monitorsource=? and agenttype=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        comm2.setString(4, request.getMonitorhostname());
                        comm2.setString(5, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + " hostingsource=? and monitorsource=? and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getServicehostname());
                        comm.setString(4, request.getMonitorhostname());
                        comm.setString(5, request.getAgentType());
                        comm.setLong(6, limit);
                        comm.setLong(7, offset);
                    }
                }
                // </editor-fold>

                //URL AND service host ok6
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 6");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " hostingsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false  and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getServicehostname());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " hostingsource=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "\"slafault\" is not null and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());
                        comm.setString(3, request.getServicehostname());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " hostingsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                            results2.close();
                            comm = con.prepareStatement("select * from RawData "
                                    + "where (URI=? or originalurl=?) and "
                                    + " hostingsource=? "
                                    + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                            comm.setString(1, request.getURL());
                            comm.setString(2, request.getURL());
                            comm.setString(3, request.getServicehostname());
                            comm.setLong(4, limit);
                            comm.setLong(5, offset);
                        }
                    }
                }
                // </editor-fold>

                //URL AND monitor host k7
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 7");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=?) and "
                                + " monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getMonitorhostname());

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=?) and "
                                + "success=false  and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());

                        comm.setString(2, request.getMonitorhostname());

                        comm.setLong(3, limit);
                        comm.setLong(4, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=?)  and "
                                + " monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=?) and "
                                + "\"slafault\" is not null and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getMonitorhostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=?) and"
                                + " monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? ) and "
                                + " monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getMonitorhostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    }
                }// </editor-fold>

                //URL AND agent AND service host AND monitoring host k8
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (!Utility.stringIsNullOrEmpty(request.getAgentType())
                        && !Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && !Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 8");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " agenttype=? and hostingsource=? and monitorsource=? and success=false;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        comm2.setString(5, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "success=false and agenttype=? and hostingsource=?  and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                        comm.setString(5, request.getMonitorhostname());
                        comm.setLong(6, limit);
                        comm.setLong(7, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and "
                                + " agenttype=? and hostingsource=? and monitorsource=? and \"slafault\" is not null;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        comm2.setString(5, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "\"slafault\" is not null and agenttype=? and hostingsource=?  and monitorsource=?  "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                        comm.setString(5, request.getMonitorhostname());
                        comm.setLong(6, limit);
                        comm.setLong(7, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where (URI=? or originalurl=?) and"
                                + " agenttype=? and hostingsource=? and monitorsource=?;");
                        comm2.setString(1, request.getURL());
                        comm2.setString(2, request.getURL());

                        comm2.setString(3, request.getAgentType());
                        comm2.setString(4, request.getServicehostname());
                        comm2.setString(5, request.getMonitorhostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where (URI=? or originalurl=?) and "
                                + "agenttype=? and hostingsource=?  and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getURL());
                        comm.setString(2, request.getURL());

                        comm.setString(3, request.getAgentType());
                        comm.setString(4, request.getServicehostname());
                        comm.setString(5, request.getMonitorhostname());
                        comm.setLong(6, limit);
                        comm.setLong(7, offset);
                    }
                }// </editor-fold>

            } else //URL is not defined
            {

//works ok
                //NO url, nothing defined k9
                // <editor-fold defaultstate="collapsed" desc="comment">
                if (Utility.stringIsNullOrEmpty(request.getAgentType())
                        && Utility.stringIsNullOrEmpty(request.getMonitorhostname())
                        && Utility.stringIsNullOrEmpty(request.getServicehostname())) {
                    log.log(Level.DEBUG, "DEBUG DAS Translog 9");

                    if (request.isFaultsOnly()) {
                        comm2 = con.prepareStatement("select sum(faults) from RawDatatally;");
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = Long.valueOf(results2.getLong(1)).intValue();
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setLong(1, limit);
                        comm.setLong(2, offset);

                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select sum(slafault) from RawDatatally where "
                                + " \"slafault\" is not null ;");

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = Long.valueOf(results2.getLong(1)).intValue();
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setLong(1, limit);
                        comm.setLong(2, offset);
                    } else {
                        comm2 = con.prepareStatement("SELECT sum(success) + sum(faults) FROM rawdatatally;");

                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = Long.valueOf(results2.getLong(1)).intValue();
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setLong(1, limit);
                        comm.setLong(2, offset);
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
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + "agenttype=? and success=false");

                        comm2.setString(1, request.getAgentType());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getAgentType());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + "agenttype=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getAgentType());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getAgentType());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " agenttype=?;");

                        comm2.setString(1, request.getAgentType());
                        //comm2.setLong(2, limit);
                        //comm2.setLong(3, offset);
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + " agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getAgentType());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
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
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " hostingsource=? and success=false;");

                        comm2.setString(1, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getServicehostname());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " hostingsource=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getServicehostname());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " hostingsource=?;");

                        comm2.setString(1, request.getServicehostname());
                        results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + " hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getServicehostname());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
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
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and success=false;");

                        comm2.setString(1, request.getMonitorhostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getMonitorhostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null and monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");
                        comm.setString(1, request.getMonitorhostname());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=?;");

                        comm2.setString(1, request.getMonitorhostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + " monitorsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setLong(2, limit);
                        comm.setLong(3, offset);
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
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and agenttype=? and hostingsource=? and success=false;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getAgentType());
                        comm2.setString(3, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false and monitorsource=?  and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getAgentType());
                        comm.setString(3, request.getServicehostname());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and agenttype=? and hostingsource=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getAgentType());
                        comm2.setString(3, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null and monitorsource=?  and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getAgentType());
                        comm.setString(3, request.getServicehostname());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and agenttype=? and hostingsource=?;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getAgentType());
                        comm2.setString(3, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();

                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + " monitorsource=?  and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getAgentType());
                        comm.setString(3, request.getServicehostname());
                        comm.setLong(4, limit);
                        comm.setLong(5, offset);
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
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=?  and hostingsource=? and success=false;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false and monitorsource=?   and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getServicehostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=?  and hostingsource=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null and monitorsource=?  and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());

                        comm.setString(2, request.getServicehostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=?  and hostingsource=?;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + " monitorsource=?  and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getServicehostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
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
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and agenttype=? and success=false;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getAgentType());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false and monitorsource=?  and agenttype=?  "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getAgentType());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and agenttype=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getAgentType());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null and monitorsource=?  and agenttype=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getAgentType());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);

                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " monitorsource=? and agenttype=?;");

                        comm2.setString(1, request.getMonitorhostname());
                        comm2.setString(2, request.getAgentType());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + " monitorsource=?  and agenttype=?  "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getMonitorhostname());
                        comm.setString(2, request.getAgentType());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);

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
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " agenttype=? and hostingsource=? and success=false;");

                        comm2.setString(1, request.getAgentType());
                        comm2.setString(2, request.getServicehostname());

                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "success=false and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC LIMIT ? OFFSET ?;");

                        comm.setString(1, request.getAgentType());
                        comm.setString(2, request.getServicehostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    } else if (request.isSlaViolationsOnly()) {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " agenttype=? and hostingsource=? and \"slafault\" is not null;");

                        comm2.setString(1, request.getAgentType());
                        comm2.setString(2, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + "\"slafault\" is not null and agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getAgentType());
                        comm.setString(2, request.getServicehostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    } else {
                        comm2 = con.prepareStatement("select count(*) from RawData where "
                                + " agenttype=? and hostingsource=?;");

                        comm2.setString(1, request.getAgentType());
                        comm2.setString(2, request.getServicehostname());
                         results2 = comm2.executeQuery();
                        if (results2.next()) {
                            totalrecords = results2.getInt(1);
                        }
                        results2.close();
                        comm = con.prepareStatement("select * from RawData "
                                + "where "
                                + " agenttype=? and hostingsource=? "
                                + "ORDER BY UTCdatetime DESC limit ? offset ?;");

                        comm.setString(1, request.getAgentType());
                        comm.setString(2, request.getServicehostname());
                        comm.setLong(3, limit);
                        comm.setLong(4, offset);
                    }
                }// </editor-fold>

            }

            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            //comm.setFetchSize(request.getRecords());
//            comm.setMaxRows(request.getRecords());
            if (comm==null){
                log.log(Level.FATAL, "unhandled query case for getRecentMessageLogs ");
                ServiceUnavailableException code = new ServiceUnavailableException("", null);
                code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
                throw code;
            }
            s = comm.executeQuery();

            ArrayOfTransactionLog logs = new ArrayOfTransactionLog();
            /*
             * if (request.getOffset() != null && request.getOffset() > 0) {
             * s.absolute(request.getOffset()); }
             */
            //int recordcount = 0;
            while (s.next()) {
                /*
                 * recordcount++; if (recordcount > request.getRecords()) {
                 * break; }
                 */
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
                    PreparedStatement slacom=null;
                     ResultSet tempset=null;
                    try {
                        slacom = con.prepareStatement("select msg from slaviolations where uri=? and incidentid=?;");
                        slacom.setString(1, request.getURL());
                        slacom.setString(2, t);
                        tempset = slacom.executeQuery();
                        if (tempset.next()) {
                            byte[] x = (byte[]) tempset.getBytes("msg");
                            if (x != null) {
                                tl.setSlaFaultMsg(new String(x, Constants.CHARSET));
                            }
                        }
                    } catch (Exception ex) {
                        tl.setSlaFaultMsg("Fault message unavailable");
                        log.log(Level.ERROR, "error parsing sla fault message from db", ex);
                    } finally {
                        DBUtils.safeClose(tempset);
                        DBUtils.safeClose(slacom);
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
            ret.setClassification(DAS4jBean.getCurrentClassificationLevel());

            //QName qname = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "logs");
            //ret.setLogs(new JAXBElement<ArrayOfTransactionLog>(qname, ArrayOfTransactionLog.class, logs));
            ret.setLogs((logs));

            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "getMessageLogs", ex);

        } finally {
            DBUtils.safeClose(results2);
            DBUtils.safeClose(s);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(comm2);
            DBUtils.safeClose(con);
        }

        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.UNEXPECTED_ERROR);
        throw code;
    }
}
