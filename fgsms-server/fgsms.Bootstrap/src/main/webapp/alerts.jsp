<%-- 
    Document   : alerts
    Created on : Mar 31, 2011, 4:41:54 PM
    Author     : Alex
--%>
 
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>

<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.List"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%
    if (request.getUserPrincipal() != null) {
       LogHelper.getLog().log(Level.INFO, "Current User: " + request.getUserPrincipal().getName() + " by " + request.getAuthType() + " ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());

    } else {
        LogHelper.getLog().log(Level.INFO, "Current User: anonymous ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());
    } 

    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);
    try {
        
        DataAccessService dasport = pl.GetDAS(application, request, response);

        GetAlertsRequestMsg req = new GetAlertsRequestMsg();
        req.setClassification(c);
        req.setRecordcount(40);
        List<GetAlertsResponseMsg> res = dasport.getAlerts(req);

        if (res != null && !res.isEmpty()) {
            out.write("<a href=\"javascript:toggleVisibility('adetails');\"><font color=black>" + res.size() + " alerts waiting</font></a>"
                    + "<div id=adetails style=\"display:none; text-align: left; padding-left: 5px; color:black;\">");
            for (int i = 0; i < res.size(); i++) {
                if (res.get(i) != null) {

                    //filter out status message for stuff that's not pingable
                    // if (!res.get(i).getUrl().startsWith("jms:")) {
                    out.write(Utility.formatDateTime(res.get(i).getTimestamp()) + ": "
                            + Utility.encodeHTML(res.get(i).getUrl()) + " ");
                    // if (!Utility.stringIsNullOrEmpty(res.get(i).getTransactionid())) {
                    //     out.write(res.get(i).getTransactionid() + " ");
                    // }
                    //  if (!Utility.stringIsNullOrEmpty(res.get(i).getMessage())) {
                    //      out.write(res.get(i).getMessage() + " ");
                    // }
                    if (res.get(i).getType() != null) {
                        if (res.get(i).getType() == AlertType.OPERATING_STATUS) {
                            out.write("Service Offline <a href=index.jsp>View Details</a>");
                        }
                        if (res.get(i).getType() == AlertType.MESSAGE_FAULT) {
                            out.write("Message Fault <a href=\"SpecificTransactionLogViewer.jsp?ID=" + URLEncoder.encode(res.get(i).getTransactionid()) + "\">View Transaction</a>");
                        }
                        if (res.get(i).getType() == AlertType.SLA_FAULT) {
                            if (Utility.stringIsNullOrEmpty(res.get(i).getTransactionid())) {
                                out.write("SLA Fault <a href=\"slarecords.jsp?url=" + URLEncoder.encode(res.get(i).getUrl()) + "\">View This Record</a>");
                            } else {
                                out.write("SLA Fault <a href=\"SpecificTransactionLogViewer.jsp?ID=" + URLEncoder.encode(res.get(i).getTransactionid()) + "\">View This Transaction</a>");
                            }
                        }
                    }
                    out.write("<br>");
                    //}
                }
            }
            out.write("</div>");
        }

    } catch (Exception ex) {
        out.write("There was an error processing your request. " + ex.getLocalizedMessage());
        LogHelper.getLog().log(Level.WARN, "error caught getting alerting information", ex);
    }


%>