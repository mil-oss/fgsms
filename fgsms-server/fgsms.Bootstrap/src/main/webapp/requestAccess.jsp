<%-- 
    Document   : requestAccess
    Created on : Oct 30, 2011, 8:19:51 AM
    Author     : Administrator 
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
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
<%@include file="csrf.jsp" %>
<div class="well">
    <h1>Request Access</h1>
</div>

<div class="row-fluid">
    <div class="span12">



<style>
    #resizable { width: 150px; height: 150px; padding: 0.5em; }
</style>
 
<%    
    String emails = "";
    
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    
    String myemailaddress = "";
    try {
        
        PCS pcsport = pl.GetPCS(application, request, response);
        
        try {
            GetMyEmailAddressRequestMsg r2 = new GetMyEmailAddressRequestMsg();
            r2.setClassification(c);
            GetMyEmailAddressResponseMsg email = pcsport.getMyEmailAddress(r2);
            myemailaddress = Utility.listStringtoString(email.getEmail());
        } catch (Exception ex) {
        }
        GetAdministratorsRequestMsg req = new GetAdministratorsRequestMsg();
        req.setClassification(c);
        GetAdministratorsResponseMsg res = pcsport.getAdministrators(req);
       
        
        if (res != null && res.getUserList() != null && res.getUserList() != null
                && res.getUserList().getUserInfo() != null
                && res.getUserList().getUserInfo().size() > 0) {
            for (int i = 0; i < res.getUserList().getUserInfo().size(); i++) {
                if (res.getUserList().getUserInfo().get(i).getEmail() != null
                        && !(res.getUserList().getUserInfo().get(i).getEmail().isEmpty())) {
                    for (int k = 0; k < res.getUserList().getUserInfo().get(i).getEmail().size(); k++) {
                        if (!Utility.stringIsNullOrEmpty(res.getUserList().getUserInfo().get(i).getEmail().get(k))) {
                            emails += (res.getUserList().getUserInfo().get(i).getEmail().get(k)) + ",";
                        }
                    }
                }
                
            }
            emails = emails.substring(0, emails.length() - 1);
        }
    } catch (Exception ex) {
        out.write("There was a problem processing your request. " + ex.getLocalizedMessage());
    }
    
%>

<script>
    $(function() {
        $( "#resizable" ).resizable();
        $( "#resizable1" ).resizable();
        $( "#resizable2" ).resizable();
        $( "#resizable3" ).resizable();
        
    });
</script>
<script language="javascript" type="text/javascript" >
    function buildmail()
    {
        var user = document.getElementById('resizable').value;
        var email = document.getElementById('resizable2').value;
        var url = document.getElementById('resizable3').value;
        var right = document.getElementById('rights').value;

        window.location = "mailto:<%=emails%>?subject=FGSMS Access Request&body=I, " + encodeURI(user) + " am requesting access to the service(s) described as " + encodeURI(url) + " at the permission level " + encodeURI(right) + ". My email address is " + encodeURI(email)+ "";
    }
</script>




Use this page to request access to a service or resource. Fill in as much information as you can.<br>

URL/URI of the thing you are requesting, or a description of what you're looking for.<br>
<input  id="resizable3" type="text" maxlength="255" size="20"><br>
Type of Access
<select name="rights" id="rights">
    <option  value="read">Read</option>
    <option value="write">Write</option>
    <option value="audit">Audit</option>
    <option value="administer">Administer</option>
    <option value="global">Global Admin</option>
</select><br>
Your username <input id="resizable" type="text" value="<%=(request.getUserPrincipal().getName())%>"><Br>
Your email address <input  id="resizable2" type="text" value="<%=Utility.encodeHTML(myemailaddress)%>"><Br>
<input type="button" value="Request Access" onclick="javascript:buildmail();">
    </div>
</div>