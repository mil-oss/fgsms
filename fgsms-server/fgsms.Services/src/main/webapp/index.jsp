<%-- 
    Document   : index
    Created on : Sep 25, 2011, 9:16:37 AM
    Author     : *
--%>

<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FGSMS Web Services</title>
    </head>
    <body> 
        <h1>FGSMS Web Services</h1>
        This is just a simple splash page for the FGSMS Web Services. Version <% out.write(org.miloss.fgsms.common.Constants.Version);%><Br>
        <table border="1">
            <tr><td><a href="services/PCS?wsdl">Policy Configuration Service </a></td><td><a href="public/<% out.write(Constants.PCS_META);%>">wsdl</a></td></tr>
            <tr><td><a href="services/DCS?wsdl">Data Collector Service </a></td><td><a href="public/<% out.write(Constants.DCS_META);%>">wsdl</a></td></tr>
            <tr><td><a href="services/DAS?wsdl">Data Access Service </a></td><td><a href="public/<% out.write(Constants.DAS_META);%>">wsdl</a></td></tr>
            <tr><td><a href="services/SS?wsdl">Status Service </a></td><td><a href="public/<% out.write(Constants.SS_META);%>">wsdl</a></td></tr>
            <tr><td><a href="services/RS?wsdl">Reporting Service </a></td><td><a href="public/<% out.write(Constants.RS_META);%>">wsdl</a></td></tr>
            <tr><td><a href="services/ARS?wsdl">Automated Reporting Service </a></td><td><a href="public/<% out.write(Constants.ARS_META);%>">wsdl</a></td></tr>
              
        </table>

                 Deployed. <br>Version <%=org.miloss.fgsms.auxsrv.AuxConstants.VERSION%>
        <br>
        <a href="admin/quartz.jsp">Quartz Status</a>
        
    </body>
</html>
