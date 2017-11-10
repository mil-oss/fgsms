<%-- 
    Document   : dataExportPostback
    Created on : Sep 27, 2013, 2:36:50 PM
    Author     : localadministrator
--%>



<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>

<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Properties"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="javax.xml.bind.JAXBElement"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Calendar"%> 
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.URL"%>
<%@include file="../csrf.jsp" %>
 

<%
     SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    String error = "";


    if (request.getMethod().equalsIgnoreCase("post")) {
        try {
            /*
             * Enumeration enumera = request.getParameterNames(); while
             * (enumera.hasMoreElements()) { String s = (String)
             * enumera.nextElement(); out.write(s + "=" +
             * request.getParameter(s));
             }
             */


            //out.write(request.getMethod());
            Enumeration<String> listP = request.getParameterNames();
            List<String> urls = new ArrayList<String>();
            
            boolean allservices = false;
            
            while (listP.hasMoreElements()) {
                String name = listP.nextElement();
                LogHelper.getLog().log(Level.INFO, name + " " + request.getParameter(name));
                if (name.equalsIgnoreCase("selectAllServices")) {
                    allservices = true;
                } else if (name.startsWith("URL")) {
                    urls.add(URLDecoder.decode(name.substring(3), Constants.CHARSET));
                } 
            }
            //check form inputs
            //request.getp
            boolean ok = true;
            String fromdate = request.getParameter("fromdatepicker");
            String todate = request.getParameter("todatepicker");
            if (Utility.stringIsNullOrEmpty(fromdate) || Utility.stringIsNullOrEmpty(todate)) {
                error += "There was a problem parsing your date selections, please try again.";

                ok = false;
            }
            ExportRecordsEnum t = ExportRecordsEnum.TRANSACTIONS;
            if (!Utility.stringIsNullOrEmpty(request.getParameter("recordstype"))) {
                try {
                    String s = request.getParameter("recordstype");
                    t = ExportRecordsEnum.fromValue(s);
               
                } catch (Exception ex) {
                }
            } else {
                ok = false;
            }
            //if ok
            //build report request
            //output response

            org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory fac = new org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory();

            //todo

            ReportingService rsport = pl.GetRS(application, request, response);

            ExportCSVDataRequestMsg req = new ExportCSVDataRequestMsg();

            req.setClassification(c);

            req.setExportType(t);
            req.setAllServices(allservices);
            if (!allservices) {
                List<String> au = new ArrayList<String>();
                for (int i = 0; i < urls.size(); i++) {
                    req.getURLs().add(urls.get(i));
                }

            }

            TimeRange r = new TimeRange();

            if (fromdate.equalsIgnoreCase(todate)) {
                ok = false;
                error += "Please select two dates that differ from each other.";

            }
            try {
               Calendar t11 = Utility.parseDateTime(fromdate);
                r.setStart(t11);
                t11 = Utility.parseDateTime(todate);
                r.setEnd(t11);
                req.setRange(r);
            } catch (Exception ex) {
                ok = false;
                error += "There was a problem parsing your date selections, please try again. ";
            }

            if (ok) {
                ExportDataToCSVResponseMsg res = rsport.exportDataToCSV(req);
                if (res != null && res.getZipFile() != null) {
                    response.reset();
                    response.setHeader("contentType", "application/zip");
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Content-disposition", "attachment; filename=\"export"
                            + GregorianCalendar.getInstance().get(Calendar.YEAR)
                            + (GregorianCalendar.getInstance().get(Calendar.MONTH) + 1)
                            + GregorianCalendar.getInstance().get(Calendar.DATE) + " " + Utility.ICMClassificationToString(c.getClassification()) + ".zip\"");
                    response.setStatus(200);
                    response.setCharacterEncoding("UTF-8");

                    try {
                        ServletOutputStream output = response.getOutputStream();
                        output.write(res.getZipFile());
                        output.close();
                    } catch (Exception e) {
                    }
                } else {
                    ok = false;
                    error += "No results were returned.";
                }
            }
        } catch (Exception ex) {
            error += "There was a problem processing your request. Message:" + ex.getLocalizedMessage();
            LogHelper.getLog().log(Level.ERROR, "Error from dataExport.jsp", ex);
        }
    }

%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Data Export</title>
        <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="../js/bootstrap.js" type="text/javascript"></script>

        <script src="../js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script> 
        <script src="../js/main.js" type="text/javascript"></script>

    </head>
    <body>
        <h1>Please wait while your data is collected</h1>
        <%
            String url = request.getParameter("return");
            if (!Utility.stringIsNullOrEmpty(url)) {
                out.write("<h2><a href=\"../index.jsp?target=" + URLEncoder.encode(url, Constants.CHARSET) + "\">Click here to return</a></h2><br>");
            }
        %>

        <form action="dataExportPostback.jsp" method="POST" name="sub" id="sub">
            <%
                
                Enumeration it=request.getParameterNames();
                while (it.hasMoreElements())
                                       {
                    String key= (String)it.nextElement();
                
                        out.write("<input name=\"" + Utility.encodeHTML(key) + "\" value=\"" + Utility.encodeHTML(request.getParameter(key)) + "\" type=\"hidden\">");
                    
                
                               }
//                 <input type="submit">
                    %>    
      
        </form>
        <script type="text/javascript">
            $(function(){
                      $("#sub").submit();
            });
        </script>
    </body>
</html>
