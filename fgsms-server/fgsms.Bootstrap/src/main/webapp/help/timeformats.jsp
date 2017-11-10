<%-- 
    Document   : timeformats
    Created on : May 24, 2011, 10:04:52 AM
    Author     : Alex
--%>

<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="StyleSheet" href="../css/help.css" type="text/css"/>

    </head>
    <body bcolor="white" >
        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            if (c == null) {
                response.sendRedirect("../index.jsp");
                return;
            }
            String classlevel = Utility.ICMClassificationToString(c.getClassification());
            String color = Utility.ICMClassificationToColorCodeString(c.getClassification());
        %>

    <center>
        <table width="100%" cellspacing="0" cellpadding="0">
            <tr width="100%">
                <td align="center" width="100%" bgcolor="<%=color%>">
                    <font size="2" color="#000000"><b>
                        <center>
                            <%=classlevel%>
                        </center>
                    </b></font>
                </td>
            </tr>
        </table>
    </center>





    <h3>Durations/Time spans</h3>
    Use the format 'xyr ymo zd nhr nmin ns'<br />
    Example:<br /><br />
    <ul>

        <li>1yr 2mo 10d 3hr 5min 3s = 1 year, 2 months, 10 days, 3 hours, 5 minutes and 3 seconds</li>
        <li>1yr  = 1 year </li>
        <li>1yr 10d = 1 year, 10 days</li>
        <li>3hr 3s = 3 hours, 3 seconds</li>
    </ul>
    <Br>
    New! as of FGSMS 6.2<br>
    You can now also use the following additional formats:
    <ul>

        <li><a href="http://www.w3.org/TR/xmlschema-2/#duration">XML Representation</a></li>
        <li>Length in time of milliseconds. </li>

    </ul>

</body>
</html>
