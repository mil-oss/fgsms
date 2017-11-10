<%-- 
    Document   : opstat
    Created on : Mar 15, 2013, 4:49:14 PM
    Author     : localadministrator
--%>


<%@page import="org.miloss.fgsms.presentation.OpStatWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage"%>
<%@page import="java.util.List"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.presentation.OpStatHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../csrf.jsp" %>
<% String parent = "admin.jsp";%>
<div class="well">
    <h1>OpStat</h1>
</div>
<div class="row-fluid">
    <div class="span12"> 

        <div style="border-style: solid; border-width: 2px">
            <%
                ProxyLoader pl = ProxyLoader.getInstance(application);
                List<OpStatWrapper> list = OpStatHelper.GetStatusAll(pl, application, request, response);
                for (int i = 0; i < list.size(); i++) {
                   out.write(OpStatHelper.toHtmlFormatedString(list.get(i).msg, list.get(i).uri));
                }
            %>
        </div>
    </div>
</div>