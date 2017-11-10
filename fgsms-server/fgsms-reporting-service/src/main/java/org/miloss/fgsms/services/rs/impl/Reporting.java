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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.services.rs.impl;

import org.miloss.fgsms.plugins.reporting.ReportGeneratorPlugin;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import org.miloss.fgsms.services.interfaces.reportingservice.*;
import org.miloss.fgsms.services.interfaces.status.OpStatusService;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * The Reporting Service for fgsms Returns user selectable reports and csv files
 *
 * @author AO
 */
//@javax.ejb.Stateless
@WebService(name = "reportingService", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService" //, wsdlLocation = "WEB-INF/wsdl/RSv6.wsdl"
)
//@Stateless
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED, use = SOAPBinding.Use.LITERAL)
public class Reporting implements ReportingService, OpStatusService {

    protected static final String delimiter = "|";
    protected static final String name = "fgsms.ReportingService";
    private static DatatypeFactory df = null;
    public final static Logger log = Logger.getLogger(name);
    private static Calendar started = null;
    public final static String allitems = "All-Methods";
    private static Map<String, ReportGeneratorPlugin> reportingPlugins;
    

    public Reporting() throws DatatypeConfigurationException {
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
      reportingPlugins = new HashMap<String, ReportGeneratorPlugin>();

    }

    /**
     * constructor used for unit testing, do not remove
     *
     * @param w
     */
    public Reporting(WebServiceContext w) throws DatatypeConfigurationException {
        ctx = w;
        InitDF();
    }
    @Resource
    private WebServiceContext ctx;

    /**
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTMLResponseMsg
     * @throws ServiceUnavailableException
     * @throws AccessDeniedException
     */
    @WebMethod(operationName = "ExportDataToHTML", action = "urn:org:miloss:fgsms:services:interfaces:reportingService/reportingService/ExportDataToHTML")
    @WebResult(name = "ExportDataToHTMLResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService")
    @RequestWrapper(localName = "ExportDataToHTML", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", className = "org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTML")
    @ResponseWrapper(localName = "ExportDataToHTMLResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", className = "org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTMLResponse")
    public ExportDataToHTMLResponseMsg exportDataToHTML(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService") ExportDataRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        return generateHtmlReport(currentUser,request);
    }
    
    protected ExportDataToHTMLResponseMsg generateHtmlReport(String currentUser,ExportDataRequestMsg request)
            throws AccessDeniedException, ServiceUnavailableException {
        if (request == null) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "exportDataToHTML", currentUser, "", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        AuditLogger.logItem(this.getClass().getCanonicalName(), "exportDataToHTML", currentUser, "", (request.getClassification()), ctx!=null? ctx.getMessageContext():null);
        //log.log(Level.INFO, name + "exportDataToHTML" + currentUser);

        if (request.getRange() == null || request.getRange().getEnd() == null || request.getRange().getStart() == null) {
            throw new IllegalArgumentException("range is null");
        }
        if (request.getReportTypes() == null
                || request.getReportTypes().getReportTypeContainer() == null
                || request.getReportTypes().getReportTypeContainer().isEmpty()) {
            throw new IllegalArgumentException("ReportTypes is null, at least one must be specified");
        }
        if (request.isAllServices() == false
                && (request.getURLs() == null
                || request.getURLs().isEmpty())) {
            throw new IllegalArgumentException("AllServices must be true or at least one URL must be specified");
        }

        // Read properties file.
        //  Properties properties = new Properties();
        //String folder = "";
        String path = "";
        String header = "";
        String currentFolder = "";
        String footer = "";
        File targetfolder = null;
        UUID id = UUID.randomUUID();
        try {
            currentFolder = System.getProperty("jboss.server.temp.dir");
            if (currentFolder == null || currentFolder.equals("") || currentFolder.equalsIgnoreCase("null")) {
                currentFolder = System.getProperty("java.io.tmpdir");
            }

            path = currentFolder + getFilePathDelimitor() + id.toString();
            targetfolder = new File(path);
            boolean mkdir = targetfolder.mkdir();
            if (!mkdir) {
                HttpServletRequest session = ((HttpServletRequest) ctx.getMessageContext().get(MessageContext.SERVLET_REQUEST));

                targetfolder = (File) session.getAttribute("javax.servlet.context.tmpdir");

            }

            if (!targetfolder.exists()) {
                log.log(Level.INFO, name + "exportDataToHTML, unable to create temporary directory " + currentFolder + targetfolder.getAbsolutePath());
                ServiceUnavailableException f = new ServiceUnavailableException("", null);
                f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.MISCONFIGURATION);
                throw f;
            }

            header = getHeader();
            footer = getFooter();

        } catch (Exception e) {
            log.log(Level.INFO, name + "exportDataToHTML, load props" + e);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.MISCONFIGURATION);
            throw f;
        }
        List<String> files = new ArrayList<String>();

        Calendar time = null;
        
        header = header.replaceAll("%DATETIME_NOW%",
                new Date().toString());
        time = request.getRange().getStart();
        header = header.replaceAll("%DATETIME_FROM%",
                time.getTime().toString());
        time = request.getRange().getEnd();
        header = header.replaceAll("%DATETIME_TO%",
                 time.getTime().toString());

        if (request.getClassification() == null) {
            request.setClassification(getClassLevelFromDB());
        }
        header = header.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " - " + request.getClassification().getCaveats()));
        header = header.replaceAll("%COLOR%", Utility.ICMClassificationToColorCodeString(request.getClassification().getClassification()));

        footer = footer.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " - " + request.getClassification().getCaveats()));
        footer = footer.replaceAll("%COLOR%", Utility.ICMClassificationToColorCodeString(request.getClassification().getClassification()));

        StringBuilder x = new StringBuilder();
        x.append("<ul>");

        if (request.isAllServices()) {
            x.append(buildAllServiceList(request, currentUser));
        } else {
            for (int i = 0; i < request.getURLs().size(); i++) {

                x.append("<li>").append(Utility.encodeHTML(request.getURLs().get(i))).append("</li>\n");
            }
        }
        x.append("</ul>");

        header = header.replaceAll("%SERVICE_LIST%", x.toString());

        OutputStreamWriter fw = null;
        try {
            File f = new File(path + getFilePathDelimitor() + "index.html");
            fw = new OutputStreamWriter(
                    new FileOutputStream(f), Constants.CHARSET);
            List<String> urls = urlListRead(request.isAllServices(), request.getURLs(), currentUser, request.getClassification());

            fw.write(header);
            for (int i = 0; i < request.getReportTypes().getReportTypeContainer().size(); i++) {
                ReportGeneratorPlugin plugin=null; 
                String name = request.getReportTypes().getReportTypeContainer().get(i).getType();
                if (reportingPlugins.containsKey(name)){
                    plugin = reportingPlugins.get(name);
                }
                if (plugin==null) {
                    try{
                    plugin = (ReportGeneratorPlugin)Class.forName(name).newInstance();
                    reportingPlugins.put(name, plugin);
                    }catch (Throwable t) {
                        log.log(Level.WARN, name + " could not load the user specified reporting plugin", t);
                    }
                }
                if (plugin == null) {
                    throw new IllegalArgumentException("unknwon report type;" + request.getReportTypes().getReportTypeContainer().get(i).getType());
                }
                plugin.generateReport(fw, urls, path, files, request.getRange(), currentUser, getClassLevelFromDB(), ctx);
            }

            fw.write(footer);
            fw.flush();
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception e) {
            log.log(Level.INFO, name + "WriteAllText, ", e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ex) {
                    log.log(Level.INFO, name + "WriteAllText, ", ex);
                }
            }
        }

        x = null;

        files.add(path + getFilePathDelimitor() + "index.html");
        ExportDataToHTMLResponseMsg response = new ExportDataToHTMLResponseMsg();
        response.setClassification(getClassLevelFromDB());
        response.setZipFile(generateZipFileandDelete(files, path));
        return response;
        
    }

    protected String getHeader() {
        String x = null;
        InputStream s = null;
        try {
            s = this.getClass().getClassLoader().getResourceAsStream("org/miloss/fgsms/services/rs/impl/header.txt");
            x = readAllText(s);

        } catch (Exception ex) {
            log.error(null, ex);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException ex) {
                }
            }
        }
        return x;
    }

    protected String getFooter() {
        String x = null;
        InputStream s = null;
        try {
            s = this.getClass().getClassLoader().getResourceAsStream("org/miloss/fgsms/services/rs/impl/footer.txt");
            x = readAllText(s);

        } catch (Exception ex) {
            log.error(null, ex);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException ex) {
                }
            }
        }
        return x;

    }

    private String buildAllServiceList(ExportDataRequestMsg request, String currentUser) {
        List<String> urls = urlListRead(request.isAllServices(), request.getURLs(), currentUser, request.getClassification());
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < urls.size(); i++) {
            list.append("<li>").append(Utility.encodeHTML(urls.get(i))).append("</li>\n");
        }
        return list.toString();
    }

    /**
     * if all services is true and not a global admin user returns a list of all
     * services that the current user has at least READ access if all services
     * is true and is a global admin user, all service urls are returned
     *
     * else, only the list of services from which the user has read access is
     * returned
     *
     * @param allServices
     * @param requestedurls
     * @param currentUser
     * @param sec
     * @return
     */
    private List<String> urlListRead(boolean allServices, List<String> requestedurls, String currentUser, SecurityWrapper sec) {
        List<String> urls = new ArrayList<String>();
        if (allServices) {
            Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement cmd = null;
            ResultSet rs = null;
            try {
                if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "GenerateReports", (sec), ctx)) {
                    cmd = con.prepareStatement("select URI from servicepolicies;");
                } else {
                    cmd = con.prepareStatement("select ObjectURI as URI from UserPermissions "
                            + "where username=? and "
                            + "ReadObject=1 or WriteObject=1 or AdministerObject=1 or AuditObject=1;");
                    cmd.setString(1, currentUser);
                }
                rs = cmd.executeQuery();
                while (rs.next()) {
                    urls.add(rs.getString("URI"));
                }

            } catch (Exception ex) {
                log.log(Level.INFO, name + "GenerateReports", ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(cmd);
                DBUtils.safeClose(con);
            }
        } else {

            if (requestedurls != null
                    && requestedurls.size() > 0) {
                for (int i = 0; i < requestedurls.size(); i++) {
                    urls.add(requestedurls.get(i));
                    UserIdentityUtil.assertReadAccess(urls.get(i), currentUser, "urlListRead", sec, ctx);
                }
            }
        }
        return urls;
    }

    /**
     * input stream remains open after exiting
     *
     * @param stream
     * @return
     */
    //works!
    protected static String readAllText(InputStream stream) {
        try {

            int size = 1024;
            byte chars[] = new byte[size];
            int k = stream.read(chars);
            StringBuilder str = new StringBuilder();
            while (k > 0) {

                for (int i = 0; i < k; i++) {
                    str.append((char) chars[i]);
                }
                k = stream.read(chars);
            }
            //    log.log(Level.INFO, name + "readAllText, Read " + str.length() + " bytes.");

            return str.toString();

        } catch (Exception e) {
            log.log(Level.ERROR, name + "ReadAllText, ", e);

            return "";
        }
    }

    protected void writeAllText(String filename, String text) {
        OutputStreamWriter fw = null;
        try {
            File f = new File(filename);

            log.log(Level.INFO, "WriteAllText Current Dir = " + f.getName() + f.getAbsolutePath());
            fw = new OutputStreamWriter(
                    new FileOutputStream(filename), Constants.CHARSET);

            fw.write(text);
            fw.flush();
        } catch (Exception e) {
            log.log(Level.INFO, name + "WriteAllText, ", e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException ex) {
                    log.log(Level.INFO, name + "WriteAllText, ", ex);
                }
            }
        }
    }

    protected static byte[] generateZipFileandDelete(List<String> files, String path) {
        // These are the files to include in the ZIP file
        log.log(Level.INFO, "Report Service, Building Zip file at " + path + " for " + files.size() + " files");

        byte[] buf = new byte[1024];

        ByteArrayOutputStream fos = null;
        ZipOutputStream out = null;
        try {
            // Create the ZIP file

            //  FileOutputStream fos = new FileOutputStream(outFilename);
            fos = new ByteArrayOutputStream();
            out = new ZipOutputStream(fos);

            // Compress the files
            for (int i = 0; i < files.size(); i++) {
                String temp = files.get(i);
                log.log(Level.DEBUG, "adding " + files.get(i) + " to the zip");
                FileInputStream in = null;
                try {
                    in = new FileInputStream(files.get(i));
                    // Add ZIP entry to output stream.
                    ZipEntry z = new ZipEntry(temp.substring(temp.lastIndexOf(getFilePathDelimitor()) + 1));
                    out.putNextEntry(z);

                    // Transfer bytes from the file to the ZIP file
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                } catch (Exception ex) {

                } finally {
                    if (in != null) {
                        in.close();
                    }
                }

                in = null;
            }
            // Complete the ZIP file
            out.flush();
            out.finish();
            out.flush();
            out.close();
            out = null;

            fos.flush();
            byte[] data = fos.toByteArray();
            fos.close();
            fos = null;

            File f;
            log.log(Level.DEBUG, "Zip file created at " + data.length + " bytes");
            for (int i = 0; i < files.size(); i++) {
                try {
                    f = new File(files.get(i)).getCanonicalFile();
                    f.setWritable(true);
                    if (f != null && !f.delete()) {
                        log.log(Level.WARN, "Error deleting file " + files.get(i));
                        f.deleteOnExit();
                    }

                } catch (Exception ex) {
                    log.log(Level.WARN, "Error deleting file " + files.get(i), ex);
                }
            }
            f = null;

            try {

                //kill the dir
                f = new File(path).getCanonicalFile();
                f.setWritable(true);
                if (!f.delete()) {
                    log.log(Level.WARN, "Error deleting temp folder" + path);
                    f.deleteOnExit();
                }
            } catch (Exception ex) {
                log.log(Level.WARN, "Error deleting file " + path + getFilePathDelimitor() + "output.zip", ex);
            }
            return data;
        } catch (IOException e) {
            log.log(Level.INFO, name + "generateZipFileandDelete", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, name + "generateZipFileandDelete", ex);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, name + "generateZipFileandDelete", ex);
                }
            }
        }
        return null;
    }

    protected SecurityWrapper getClassLevelFromDB() {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select classification, caveat from globalpolicies;");
            rs = com.executeQuery();
            if (rs.next()) {
                ClassificationType t = ClassificationType.fromValue(rs.getString(1));
                String cav = rs.getString(2);
                return new SecurityWrapper(t, cav);
            } else {
                log.log(Level.ERROR, "unable to determine current classification level, defaulting to UNCLASSIFIED");
                return new SecurityWrapper(ClassificationType.U, "None");
            }
        } catch (SQLException ex) {
            log.log(Level.ERROR, "error determining classification level from config db", ex);
            log.log(Level.ERROR, "unable to determine current classification level, defaulting to UNCLASSIFIED");
            return new SecurityWrapper(ClassificationType.U, "None");
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
    }

    public static int pixelHeightCalc(int items) {
        if (items > 0 && items < 3) {
            return items * 200;
        }
        if (items == 0) {
            return 200;
        }
        return items * 50;

    }

    /**
     *
     * Exports transactions logs for a given set of services, requires audit
     * level permissions or higher for each service requested service
     *
     *
     * @param request
     * @return returns
     * org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToCSVResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "ExportDataToCSV", action = "urn:org:miloss:fgsms:services:interfaces:reportingService/reportingService/ExportDataToCSV")
    @WebResult(name = "ExportDataToCSVResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService")
    @RequestWrapper(localName = "ExportDataToCSV", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", className = "org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToCSV")
    @ResponseWrapper(localName = "ExportDataToCSVResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", className = "org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToCSVResponse")
    public ExportDataToCSVResponseMsg exportDataToCSV(
            @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:reportingService") ExportCSVDataRequestMsg request) throws ServiceUnavailableException {
        //TODO this is not yet complete, need to support accessing records from a list of services and not just everythoing
        //if audit or higher, also geting xml text

        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);

        if (request == null) {
            AuditLogger.logItem(this.getClass().getCanonicalName(), "exportDataToHTML", currentUser, "null request", "not specified", ctx.getMessageContext());
            throw new IllegalArgumentException("request is null");
        }
        Utility.validateClassification(request.getClassification());

        if (request.getRange() == null) {
            throw new IllegalArgumentException("time range");
        }
        if (request.getExportType() == null) {
            throw new IllegalArgumentException("a records type must be specified");
        }

        UserIdentityUtil.assertGlobalAdministratorRole(currentUser, "exportDataToCSV", request.getClassification(), ctx);

        return CsvExporter.exportDataToCSV(ctx, currentUser, getClassLevelFromDB(), request);

    }

    public static String getFilePathDelimitor() {

        return File.separator;

    }

    public static boolean isPolicyTypeOf(String uri, PolicyType p) {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select policytype from servicepolicies where uri=?");
            com.setString(1, uri);
            rs = com.executeQuery();
            if (rs.next()) {
                int x = rs.getInt(1);
                if (PolicyType.values()[x].equals(p)) {
                    return true;
                }
            }

        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return false;

    }

    protected static String toSafeFileName(String url) {
        if (Utility.stringIsNullOrEmpty(url)) {
            return "EmptyFileName";
        }
        String s = url;
        s = s.replace("-", "--");
        s = s.replace('/', '-');
        s = s.replace('\\', '-');
        s = s.replace(':', '-');
        s = s.replace('*', '-');
        s = s.replace('|', '-');
        s = s.replace('<', '-');
        s = s.replace('>', '-');
        s = s.replace('?', '-');
        s = s.replace('"', '-');
        s = s.replace('%', '-');
        s = s.replace('"', '-');
        s = s.replace('{', '-');
        s = s.replace('}', '-');
        s = s.replace('[', '-');
        s = s.replace(']', '-');
        s = s.replace('~', '-');
        s = s.replace('!', '-');
        s = s.replace('$', '-');
        s = s.replace('&', '-');
        s = s.replace(';', '-');
        s = s.replace('#', '-');
        s = s.replace('@', '-');
        s = s.replace('(', '-');
        s = s.replace(')', '-');

        return s;
    }

    protected static String stripCommas(String string) {
        if (Utility.stringIsNullOrEmpty(string)) {
            return string;
        }
        return string.replace(',', ' ').trim();
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


}
