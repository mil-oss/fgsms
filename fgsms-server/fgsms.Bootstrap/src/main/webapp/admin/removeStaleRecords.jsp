<%-- 
    Document   : hostPerformance
    Created on : Apr 11, 2011, 3:33:44 PM
    Author     : Alex
--%> 

<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>

<%@page import="java.beans.Encoder"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@include file="../csrf.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
 
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);

    StatusService statusport = pl.GetSS(application, request, response);


    if (request.getMethod().equalsIgnoreCase("post")) {
        try {
            RemoveStatusRequestMsg req1 = new RemoveStatusRequestMsg();
            req1.setClassification(c);
            String[] items = request.getParameterValues("remove_item");
            if (items != null) {
                for (int i = 0; i < items.length; i++) {
                    req1.getURI().add((items[i]));
                }
                statusport.removeStatus(req1);
                out.write(items.length + " removed successfully");
            } else {
                response.setStatus(400);
                out.write("At least one item must be specified.");
            }

        } catch (Exception ex) {
            response.setStatus(400);
            out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
            LogHelper.getLog().log(Level.WARN, "Error caught ", ex);
        }
    } else {

%>

<div class="well">
    <h1>Removal of Stale Status Records</h1>
    <p>On certain occasions, such as when an resource is no longer in service and it was updated directly from the FGSMS Status web service, (such as Linux and Windows processes), it can leave items flagged as red on the home page. Use this to remove any such records. Requires global administrator privledges.</p>
</div>
<div class="row-fluid">
    <div class="span12">


        <%
            try {
                GetStatusRequestMsg req = new GetStatusRequestMsg();
                req.setClassification(c);
                List<GetStatusResponseMsg> res = statusport.getAllStatus(req);
                if (res == null || res.isEmpty()) {
                    out.write("No records were returned");
                } else {
                    %>
                    
                    <a href="javascript:selectAllRecords();" class="btn btn-large">Select All</a> | <a href="javascript:selectNoRecords();" class="btn btn-large">Select None</a><br>
                    <%
                    out.write("<table class=\"table\"><tr><th></th><th>URI</th><th>Last Update</th></tr>");
                    for (int i = 0; i < res.size(); i++) {
                        GregorianCalendar time = (GregorianCalendar) res.get(i).getTimeStamp();
                        DatatypeFactory fac = DatatypeFactory.newInstance();
                        long diff = System.currentTimeMillis() - time.getTimeInMillis();

                        out.write("<tr><td><input type=checkbox name=\"remove_item\" value=\"" + Utility.encodeHTML(res.get(i).getURI()) + "\" ");
                        if (diff > (60 * 60 * 1000)) {
                            out.write("checked");
                        }
                        out.write("></td><td>" + Utility.encodeHTML(res.get(i).getURI()) + "</td><td>" + Utility.durationToString(fac.newDuration(diff)) + "</td></tr>");
                    }
                    out.write("</table>");

                }
            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
                LogHelper.getLog().log(Level.WARN, "Error caught ", ex);
            }

        %>

        <script type="text/javascript">
            function selectAllRecords() {
                $("input:checkbox").prop("checked", true);
            }
            function selectNoRecords() {
                $("input:checkbox").prop('checked', false);
            }
            </script>

        <a class="btn btn-primary" href="javascript:postBack('submit','admin/removeStaleRecords.jsp');"  >Remove Selected Items</a> <br>This will delete all remaining records associated with the selected service URIs.

    </div>
</div>
<%
    }
%>