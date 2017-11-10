<%-- 
    Document   : index
    Created on : Dec 13, 2011, 7:05:12 PM
    Author     : Administrator
--%>

<%@page import="org.quartz.impl.StdSchedulerFactory"%>
<%@page import="org.quartz.Scheduler"%>
<%@page import="org.miloss.fgsms.auxsrv.AuxConstants"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Properties"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FGSMS Aux Services</title>
    </head>
    <body>
        Deployed. <br>Version <%=org.miloss.fgsms.auxsrv.AuxConstants.VERSION%>
        <br>
        <a href="quartz.jsp">Quartz Status</a>
        <%
        Properties p =System.getProperties();
      Iterator<Entry<Object, Object>> it=  p.entrySet().iterator();
      while (it.hasNext()){
           Entry<Object, Object> item=it.next();
       
           out.write(StringEscapeUtils.escapeHtml(item.getKey().toString()) + "=" + StringEscapeUtils.escapeHtml(item.getValue().toString())  + "<br>");
      }
        %>
    </body>
</html>
