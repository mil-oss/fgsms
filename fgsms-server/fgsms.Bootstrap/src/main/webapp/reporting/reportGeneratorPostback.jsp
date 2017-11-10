<%-- 
    Document   : reportGeneratorPostback
    Created on : Sep 27, 2013, 12:06:35 PM
    Author     : localadministrator
--%>



<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.Plugin"%>
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
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.URL"%>
<%@include file="../csrf.jsp" %>

<%    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
%>
<%
    String error = "";
    try {

        if (request.getMethod().equalsIgnoreCase("post")) {

            //out.write(request.getMethod());
            Enumeration<String> listP = request.getParameterNames();
            List<String> urls = new ArrayList<String>();
            List<String> types = new ArrayList<String>();
            boolean allservices = false;
            boolean allreports = false;
            while (listP.hasMoreElements()) {
                String name = listP.nextElement();
                //LogHelper.getLog().log(Level.INFO, name + " " + request.getParameter(name));
                if (name.equalsIgnoreCase("AllReportTypes")) {
                    allreports = true;
                } else if (name.equalsIgnoreCase("selectAllServices")) {
                    allservices = true;
                } else if (name.startsWith("URL")) {
                    urls.add(URLDecoder.decode(name.substring(3), Constants.CHARSET));
                } else if (name.startsWith("REPORTTYPE_")) {
                    try {
                        types.add(name.replace("REPORTTYPE_", ""));
                    } catch (Exception ex) {
                    }
                }
            }
            //check form inputs
            //request.getp
            boolean ok = true;
            String fromdate = request.getParameter("fromdatepicker");
            String todate = request.getParameter("todatepicker");
            if (Utility.stringIsNullOrEmpty(fromdate) || Utility.stringIsNullOrEmpty(todate)) {
                error += "There was a problem parsing your date selections, please try again. ";

                ok = false;
            }

            //if ok
            //build report request
            //output response
            org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory fac = new org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory();

            //todo
            ReportingService rsport = pl.GetRS(application, request, response);

            ExportDataRequestMsg req = new ExportDataRequestMsg();

            req.setClassification(c);

            //  BindingProvider bpPCS = (BindingProvider) rsport;
            // Map<String, Object> contextPCS = bpPCS.getRequestContext();
            //Object oldAddress = context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
            // ontextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8888/DAS/DAS4jBean");
            req.setAllServices(allservices);
            if (!allservices) {
                for (int i = 0; i < urls.size(); i++) {
                    req.getURLs().add(urls.get(i));
                }

            }
            if (allreports) {
                ArrayOfReportTypeContainer x = new ArrayOfReportTypeContainer();
                List<Plugin> plugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "REPORTING");
                for (int i = 0; i < plugins.size(); i++) {

                    ReportTypeContainer x21 = new ReportTypeContainer();
                    x21.setType(plugins.get(i).getClassname());
                    x.getReportTypeContainer().add(x21);
                }
                req.setReportTypes(x);
            } else {
                ArrayOfReportTypeContainer reports = fac.createArrayOfReportTypeContainer();
                for (int i = 0; i < types.size(); i++) {
                    ReportTypeContainer t = new ReportTypeContainer();
                    t.setType(types.get(i));
                    reports.getReportTypeContainer().add(t);
                }
                req.setReportTypes(reports);
            }
            TimeRange r = new TimeRange();

            if (fromdate != null && fromdate.equalsIgnoreCase(todate)) {
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
                error += "There was a problem parsing your date selections, please try again.";
            }

            if (ok) {
                ExportDataToHTMLResponseMsg res = rsport.exportDataToHTML(req);
                response.reset();
                response.setHeader("contentType", "application/zip");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Content-disposition", "attachment; filename=\"report"
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
            }
        }

    } catch (Exception ex) {
        error += "There was a problem processing your request. Message:" + ex.getLocalizedMessage();
        LogHelper.getLog().log(Level.ERROR, "Error from reportGenerator.jsp", ex);
    }
    //           } else {

%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Report Generation</title>
        <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="../js/bootstrap.js" type="text/javascript"></script>

        <script src="../js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script> 
        <script src="../js/main.js" type="text/javascript"></script>

    </head>
    <body>
        <h1>Please wait while your report is generated</h1>
        <%            String url = request.getParameter("return");
            if (!Utility.stringIsNullOrEmpty(url)) {
                out.write("<h2><a href=\"../index.jsp?target=" + URLEncoder.encode(url) + "\">Click here to return</a></h2><br>");
            }
        %>

        <form action="reportGeneratorPostback.jsp" method="POST" name="sub" id="sub">
            <%
                //String data = request.getParameter("data");
                Enumeration it = request.getParameterNames();
                while (it.hasMoreElements()) {
                    String key = (String) it.nextElement();

                    out.write("<input name=\"" + Utility.encodeHTML(key) + "\" value=\"" + Utility.encodeHTML(request.getParameter(key)) + "\" type=\"hidden\">");

                }
            %>

        </form>
        <script type="text/javascript">
            <%
                //prevent the page from cycling on a failed report generation
                if (error.length() == 0) { %>
            $(function () {
                $("#sub").submit();


            });

            <% }
            %>
        </script>
    </body>
</html>
