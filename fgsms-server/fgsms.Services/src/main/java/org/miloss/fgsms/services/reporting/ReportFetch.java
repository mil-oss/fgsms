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

package org.miloss.fgsms.services.reporting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ReportDefinition;

/**
 * This little servlet will attempt to fetched a zipped csv/html report from the fgsms database. These reports are scheduled by the 
 * Automated Reporting Service and are generated by the fgsms.ReportGenerator
 * @author AO
 * @since 6.2
 */
public class ReportFetch extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user = null;
        if (request.getUserPrincipal() != null) {
            user = request.getUserPrincipal().getName();
        }
        if (user==null)
            user="anonymouse";
        String reportid = request.getParameter("reportid");
        ServletOutputStream out = response.getOutputStream();
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd =null;
        ResultSet rs=null;
        try {


            if (Utility.stringIsNullOrEmpty(user)) {
                response.sendError(401, "Authorization Required");
            } else if (Utility.stringIsNullOrEmpty(user)) {
                response.sendError(400, "Bad request, a report id is required");
            } else {

//arsjobs.owninguser=? and
                cmd = con.prepareStatement("select hasextrapermissions, reportdef, owninguser, reportcontents, arsjobs.jobid as JOB from arsjobs, arsreports where arsjobs.jobid=arsreports.jobid and  arsreports.reportid=? ");
//                cmd.setString(1, user);
                cmd.setString(1, reportid);
                rs = cmd.executeQuery();
                if (rs.next()) {
                    boolean accessCheck = false;
                    boolean accesscontrolrules = rs.getBoolean("hasextrapermissions");
                    if (user.equalsIgnoreCase(rs.getString("owninguser"))) {
                        accessCheck = true;
                    }
                    if (accesscontrolrules) {
                        ReportDefinition def = null;
                        //load the job from xml   ReportDefinition ret = null;
                        JAXBContext GetARSSerializationContext = Utility.getARSSerializationContext();
                        Unmarshaller u = GetARSSerializationContext.createUnmarshaller();
                        byte[] s = rs.getBytes("reportdef");
                        ByteArrayInputStream bss = new ByteArrayInputStream(s);
                        XMLInputFactory xf = XMLInputFactory.newInstance();
                        XMLStreamReader r = xf.createXMLStreamReader(bss);
                        JAXBElement<ReportDefinition> foo = (JAXBElement<ReportDefinition>) u.unmarshal(r, ReportDefinition.class);
                        if (foo != null && foo.getValue() != null) {
                            def = foo.getValue();
                        }

                        if (def != null) {
                            for (int i = 0; i < def.getAdditionalReaders().size(); i++) {
                                if (def.getAdditionalReaders().get(i).equalsIgnoreCase(user)) {
                                    accessCheck = true;
                                }
                            }
                        }
                    }
                    if (accessCheck) {
                        AuditLogger.logItem("ServletReportFetcher", "GET (access a stored report)", user, "Report id " + reportid + " Job id " + rs.getString("JOB"), "unspecified", null);
                        response.setHeader("contentType", "application/zip");
                        response.setHeader("Cache-Control", "no-cache");
                        response.setHeader("Content-disposition", "attachment; filename=\"report"
                                + GregorianCalendar.getInstance().get(Calendar.YEAR)
                                + (GregorianCalendar.getInstance().get(Calendar.MONTH) + 1)
                                + GregorianCalendar.getInstance().get(Calendar.DATE) + "-" + reportid + ".zip\"");
                        response.setStatus(200);
                        response.setCharacterEncoding("UTF-8");
                        byte[] buffer = new byte[1024];
                        InputStream binaryStream = rs.getBinaryStream("reportcontents");
                        int x = binaryStream.read(buffer);


                        while (x > 0) {
                            out.write(buffer, 0, x);
                            x = binaryStream.read(buffer);
                        }

                        binaryStream.close();
                    } else {
                        AuditLogger.logItem("ServletReportFetcher", "GET (access a stored report)", user, "ACCESS DENIED Report id " + reportid + " Job id " + rs.getString("JOB"), "unspecified", null);
                        response.sendError(403, "Access Denied");
                    }
                } else {
                    response.sendError(404, "Report Not Found");

                }
                rs.close();
                cmd.close();
            }

            con.close();
        } catch (Exception ex) {
            Logger.getLogger("report fetcher").log(Level.FATAL, null, ex);
            AuditLogger.logItem("ServletReportFetcher", "GET (access a stored report)", user, "ERROR Report id " + reportid + " " + ex.getMessage(), "unspecified", null);
            response.sendError(500);
        } finally {
            out.close();
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "fgsms Report Fetcher";
    }// </editor-fold>
}