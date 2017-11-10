<%-- 
    Document   : logout
    Created on : May 22, 2011, 6:56:17 PM
    Author     : Alex 
--%>


<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%
    if (request.getMethod().equalsIgnoreCase("post")) {
        LogHelper.getLog().log(Level.INFO, "User logout " + request.getUserPrincipal().getName());
        session.invalidate();

        out.write("<script>javascript:window.open('', '_self', '');window.close();</script>");
    }
    else
               {
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<br><br><br>
Confirm Logout


Are you sure you wish to log out?


<input type="button" title="Logout" value="Logout" onclick="javascript:postBack('submit','logout.jsp');" />



<%

    }
    
    %>
