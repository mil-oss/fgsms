<%-- 
    Document   : policy Import
    Created on : Oct 30, 2011, 8:19:51 AM
    Author     : AO 
--%>

<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Properties"%>

<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="org.miloss.fgsms.common.Constants.*"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="org.miloss.fgsms.common.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">



<div class="well">
    <h1>Policy Import</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <style>
            #resizable { width: 150px; height: 150px; padding: 0.5em; }
        </style>
        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");

        %>
        <%
            String emails = "";
            ProxyLoader pl = ProxyLoader.getInstance(application);
            if (request.getMethod().equalsIgnoreCase("POST")) {
                if (!Utility.stringIsNullOrEmpty(request.getParameter("resizable")) && !Utility.stringIsNullOrEmpty(request.getParameter("policytype"))) {
                    PolicyType tp = null;
                    try {
                        tp = PolicyType.fromValue(request.getParameter("policytype"));
                    } catch (Exception e) {
                    }
                    try {
                        tp = PolicyType.valueOf(request.getParameter("policytype"));
                    } catch (Exception e) {
                    }
                    if (tp == null) {
                        out.write("<h3>Please select a policy tyoe</h3><Br><br>");
                    } else {
                        ServicePolicy p = Helper.ImportServicePolicy(request.getParameter("resizable").trim(), tp);
                        if (p != null && !Utility.stringIsNullOrEmpty(p.getURL())) {
                            try {
                                SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
                                req.setClassification(c);
                                req.setURL(p.getURL());
                                req.setPolicy(p);
                                PCS pcsport = pl.GetPCS(application, request, response);
                                SetServicePolicyResponseMsg res = pcsport.setServicePolicy(req);
                                out.write("<h3>Policy Validated and saved successfully. <a href=\"manage.jsp?url=" + URLEncoder.encode(p.getURL()) + "\">Click here to view the policy</a>.</h3><Br><br>");
                            } catch (Exception ex) {
                                out.write("<h3>The policy entered is valid xml, but it was rejected by the server. The error message was " + ex.getMessage() + "</h3><Br><br>");
                            }

                        } else {
                            out.write("<h3>There was a problem validating the correctness of the policy (in terms of XML and XSD validation). Ensure that the policy entered conforms to the Service Policy schema.</h3><Br><br>");
                        }
                    }
                } else {
                    out.write("<h3>You must enter a policy.</h3><Br><br>");
                }
            }

        %>






        Advanced Users: Use this page to import a Service Policy from  XML.<br><br>

        <b>Required</b>: Please select the policy type that you are importing<br>
        <select name="policytype">
            <%        for (int i = 0;
                        i < PolicyType.values().length; i++) {
                    out.write("<option value=\"" + Utility.encodeHTML(PolicyType.values()[i].value()) + "\">" + Utility.encodeHTML(PolicyType.values()[i].value()) + "</option>");
                }
            %>
        </select>
        <br><Br>
        <textarea cols="120" rows="10" id="resizable" name="resizable"><%    if (!Utility.stringIsNullOrEmpty(request.getParameter("resizable"))) {
                out.write(Utility.encodeHTML(request.getParameter("resizable")));
            }
            %></textarea> 
        <a href="javascript:postBackReRender('Import','profile/policyImport.jsp','mainpane');" class="btn btn-primary">Import</a>
        <script type="text/javascript">
            $(function() {
                $( "#resizable" ).resizable();
            });
        </script>



    </div>
</div>