<%-- 
mailSettings
Created on : Apr 11, 2011, 3:33:44 PM
    Author     : Alex 
--%>

<%@page import="java.util.Enumeration"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
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
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    PCS pcsport = pl.GetPCS(application, request, response);
    if (request.getMethod().equalsIgnoreCase("post")) {
        try {


            GetMailSettingsRequestMsg rs = new GetMailSettingsRequestMsg();
            rs.setClassification(c);
            // String action = request.getParameter("action");
//            if (!Utility.stringIsNullOrEmpty(action)) 
            {
                GetMailSettingsResponseMsg rrs = pcsport.getMailSettings(rs);
                //          if (action.equalsIgnoreCase("removeSelected")) {
                if (!Utility.stringIsNullOrEmpty(request.getParameter("removeItem"))) {
                    String[] removeditems = request.getParameterValues("removeItem");

                    if (removeditems != null && removeditems.length > 0) {
                        SetMailSettingsRequestMsg req1 = new SetMailSettingsRequestMsg();
                        req1.setClassification(c);
                        for (int i = 0; i < rrs.getPropertiesList().size(); i++) {
                            boolean skip = false;
                            for (int k = 0; k < removeditems.length; k++) {
                                if (removeditems[k].equalsIgnoreCase(rrs.getPropertiesList().get(i).getPropertyName())) {
                                    skip = true;
                                }
                            }
                            if (!skip) {
                                req1.getPropertiesList().add(rrs.getPropertiesList().get(i));
                            }
                        }
                        pcsport.setMailSettings(req1);
                        out.write("Changes Saved");
                    }

                }

                //     if (action.equalsIgnoreCase("addSelected")) {
                if (!Utility.stringIsNullOrEmpty(request.getParameter("addSelected"))) {
                    String p = request.getParameter("propertyName");
                    String v = request.getParameter("propertyValue");
                    if (!Utility.stringIsNullOrEmpty(p) && !Utility.stringIsNullOrEmpty(v) ) {
                        SetMailSettingsRequestMsg req1 = new SetMailSettingsRequestMsg();
                        req1.setClassification(c);
                        for (int i = 0; i < rrs.getPropertiesList().size(); i++) {
                            req1.getPropertiesList().add(rrs.getPropertiesList().get(i));
                        }
                        PropertiesList e = new PropertiesList();
                        e.setPropertyName(p);
                        e.setPropertyValue(v);
                        req1.getPropertiesList().add(e);
                        pcsport.setMailSettings(req1);
                        out.write("Changes Saved");
                    } else {
                        out.write("A property name and value must be specified.");
                        response.setStatus(400);
                    }
                }
                //if (action.equalsIgnoreCase("saveChanges")) {
                if (!Utility.stringIsNullOrEmpty(request.getParameter("saveChanges"))) {
                    Enumeration it = request.getParameterNames();
                    SetMailSettingsRequestMsg req1 = new SetMailSettingsRequestMsg();
                    req1.setClassification(c);
                    boolean ok = true;
                    while (it.hasMoreElements()) {
                        String v = (String) it.nextElement();
                        if (v.startsWith("newvalue")) {
                            PropertiesList e = new PropertiesList();

                            e.setPropertyName(v.replaceFirst("newvalue", ""));
                            e.setPropertyValue(request.getParameter(v));
                            req1.getPropertiesList().add(e);
                        }
                    }
                    if (!ok) {
                        out.write("Error, all parameters must have a value");
                    } else {
                        pcsport.setMailSettings(req1);
                        out.write("Changes Saved");
                    }

                }
            }
        } catch (Exception e) {
            out.write("There was an error processing your request. Message: " + e.getLocalizedMessage());
            LogHelper.getLog().log(Level.WARN, "Error caught alertingSettings", e);

        }
    } else {
%>
<div class="well">
    <h1>Email Settings</h1>
</div>

<div class="row-fluid">
    <div class="span12">
        FGSMS has a number of components that are capable of sending email alerts when evens occur, such as an SLA violations which includes performance metrics and status changes. Use the following listing to add or remove properties. This is available for global administrators only.<br><br>



        <%
            try {
                GetMailSettingsRequestMsg r = new GetMailSettingsRequestMsg();
                r.setClassification(c);

                GetMailSettingsResponseMsg email = pcsport.getMailSettings(r);

                out.write("<h3>Current mail settings</h3>");
                if (email != null && !email.getPropertiesList().isEmpty()) {
                    out.write("<table  class=\"table\"><tr><th></th><th>Name</th><th>Current Value</th><th>New Value</th></tr>");
                    for (int i = 0; i < email.getPropertiesList().size(); i++) {
                        out.write("<tr><td>");
                        //FGSMS.GUI.URL, defaultReplyAddress, mail.smtp.host
                        if (email.getPropertiesList().get(i).getPropertyName().equalsIgnoreCase("FGSMS.GUI.URL")
                                || email.getPropertiesList().get(i).getPropertyName().equalsIgnoreCase("defaultReplyAddress")
                                || email.getPropertiesList().get(i).getPropertyName().equalsIgnoreCase("mail.smtp.host")) {
                            //don't output the checkbox
                        } else {
                            out.write("<input type=checkbox name=\"removeItem\" value=\"" + Utility.encodeHTML(email.getPropertiesList().get(i).getPropertyName()) + "\">");
                        }
                        out.write("</td>"
                                + "<td>" + Utility.encodeHTML(email.getPropertiesList().get(i).getPropertyName()) + "</td><td>");
                        out.write(Utility.encodeHTML(email.getPropertiesList().get(i).getPropertyValue()) + "</td>");
                        out.write("<td><input type=text name=\"newvalue" + Utility.encodeHTML(email.getPropertiesList().get(i).getPropertyName()) + "\" value=\"" + Utility.encodeHTML(email.getPropertiesList().get(i).getPropertyValue()) + "\" ></td></tr>");
                    }
                    out.write("</table>");

                    out.write("<input type=\"button\"  value=\"Remove Selected\" name=\"removeSelected\" onclick=\"javascript:postBack('removeSelected','admin/mailSettings.jsp');\" />"
                            + "<input type=\"button\"  value=\"Save Changes\" name=\"saveChanges\" onclick=\"javascript:postBack('saveChanges','admin/mailSettings.jsp');\" /><br><Br>");
                } else {
                    out.write("No mail settings are currently set, this means that FGSMS's SLA Processor will NOT be able to send email alerts.<Br><Br>");
                }



            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage() + "<br><Br>");
                LogHelper.getLog().log(Level.WARN, "Error caught alertingSettings", ex);
            }
        %>

        </script>
        <Br><Br>
        <h3>Add a new property</h3>
        <table border="1"><tr><td>Name</td><td><input type="text" size="40" name="propertyName" id="propertyName"/></td></tr>
            <tr><td>Value</td><td><input type="text" size="40" name="propertyValue" id="propertyValue"/></td></tr>
        </table>
        <input type="button" onclick="javascript:postBack('addSelected','admin/mailSettings.jsp');"  value="Add and Save" name="addSelected"/>

        <br><br>
        More information can be found on the javax.mail i.e. JavaMail API. Here's a few settings to get you started.<br>
        Required settings:
        <ul>
            <li>mail.smtp.host - the hostname or ip of the mail server</li>
            <li>mail.smtp.port - the port it's listening on</li>
            <li>mail.smtp.auth - true or false for whether or not the mail server requires authentication.</li>
            <li>mail.smtp.ssl.enable - true or false for whether or not the mail server requires authentication.</li>
            <li>mail.smtp.ssl.checkserveridentity - true or false for whether or not the mail server requires authentication.</li>
            <li>mail.smtp.user - username for mail server authentication.</li>
            <li>mail.smtp.auth.plain- password in plain text for mail server authentication.</li>
            <li>mail.smtp.auth.digest-md5- hashed password for mail server authentication.</li>
            <li>FGSMS.GUI.URL - the resolvable address of the FGSMS Web interface that you are currently looking at, required</li>
            <li>defaultReplyAddress - the default reply or from address, required</li>
        </ul>

        <script>
            $(function() {
                $( "input:submit").button();
                $("input:text").resizable();
            });
        </script>

    </div>
</div><%
    }
%>