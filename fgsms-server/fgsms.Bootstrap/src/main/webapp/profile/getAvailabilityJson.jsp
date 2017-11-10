<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%><%@page import="org.miloss.fgsms.common.Constants"%><%@page import="org.miloss.fgsms.common.Constants.AuthMode"%><%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%><%@page import="org.miloss.fgsms.common.Logger"%><%@page import="org.apache.log4j.Level"%><%@page import="us.gov.ic.ism.v2.ClassificationType"%><%@page import="java.util.Map"%><%@page import="javax.xml.ws.BindingProvider"%><%@page import="org.miloss.fgsms.presentation.*"%> <%@page import="java.util.Calendar"%><%@page import="javax.xml.datatype.XMLGregorianCalendar"%><%@page import="javax.xml.datatype.DatatypeFactory"%><%@page import="java.util.GregorianCalendar"%><%@page import="java.util.GregorianCalendar"%><%@page import="org.miloss.fgsms.services.interfaces.status.*"%><%@page import="java.net.URLEncoder"%><%@page import="org.miloss.fgsms.common.Utility"%><%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%><%@page import="org.miloss.fgsms.presentation.Helper"%><%@page import="java.util.Properties"%><%@page import="java.net.URL"%><%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%><%@page contentType="text/html" pageEncoding="UTF-8"%><%@include file="../csrf.jsp" %><%
    response.setContentType("application/json");
    GetOperationalStatusLogResponseMsg res = null;


    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);

    if (!Utility.stringIsNullOrEmpty(request.getParameter("url"))) {

        try {
 
            DataAccessService dasport = pl.GetDAS(application, request, response);
            GetStatusResponseMsg res2 = null;

            StatusService statusport = pl.GetSS(application, request, response);

            GetStatusRequestMsg req2 = new GetStatusRequestMsg();
            req2.setClassification(c);
            req2.setURI(request.getParameter("url"));
            res2 = statusport.getStatus(req2);

            GetOperationalStatusLogRequestMsg req = new GetOperationalStatusLogRequestMsg();
            req.setClassification(c);
            req.setURL(request.getParameter("url"));
            TimeRange now = new TimeRange();
            long currentime = System.currentTimeMillis();
            Calendar gc = GregorianCalendar.getInstance();
            gc.setTimeInMillis(currentime);
            Calendar gc2 = GregorianCalendar.getInstance();
            gc2.setTimeInMillis(currentime);
            if (!Utility.stringIsNullOrEmpty(request.getParameter("since"))) {
                try {
                    gc2.setTimeInMillis(Long.parseLong(request.getParameter("since")));
                } catch (Exception x) {
                    gc2.add(Calendar.DATE, (-1 * 180));
                }
            } else {
                gc2.add(Calendar.DATE, (-1 * 180));
            }
            
            now.setStart(gc2);
            now.setEnd(gc);
            req.setRange(now);
            res = dasport.getOperationalStatusLog(req);


            if (res != null && res.getOperationalRecord() != null && !res.getOperationalRecord().isEmpty()) {
                out.write("{" + org.codehaus.jettison.json.JSONObject.quote(request.getParameter("url")) + ":[{");
                //current status
                if (res2 != null) {
                    out.write("\"lastKnownStatus\":{ ");
                    if (res2.getTimeStamp() != null) {
                        out.write("\"timestamp\":" + res2.getTimeStamp().getTimeInMillis() + ",");
                    }
                    if (res2.getLastStatusChangeTimeStamp() != null) {
                        out.write("\"lastStatusChange\":" + res2.getLastStatusChangeTimeStamp().getTimeInMillis() + ",");
                    }
                    out.write("\"operational\":" + res2.isOperational() + ",");
                    out.write("\"message\":" +org.codehaus.jettison.json.JSONObject.quote(res2.getMessage()) + "}");
                    if (res != null && !res.getOperationalRecord().isEmpty()) {
                        out.write(",");
                    } 
                }
                for (int i = res.getOperationalRecord().size() - 1; i >= 0; i--) {
                    out.write("\"" + res.getOperationalRecord().get(i).getID() + "\":{ ");
                    out.write("\"timestamp\":" + res.getOperationalRecord().get(i).getTimestamp().getTimeInMillis() + ",");
                    out.write("\"operational\":" + res.getOperationalRecord().get(i).isOperational() + ",");
                    out.write("\"message\":" + org.codehaus.jettison.json.JSONObject.quote(res.getOperationalRecord().get(i).getMessage()) + "}");
                    if (i != 0) {
                        out.write(",");
                    }
                }
                //end the set
                out.write("}]}");
            }
        } catch (Exception ex) {
            out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font>");
            LogHelper.getLog().log(Level.ERROR, "Error from availability.jsp", ex);
        }
    } else {
        response.setStatus(400);
    }
%>
