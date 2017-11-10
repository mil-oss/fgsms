<%--  Maintain the lack of line breaks!
    Document   : datavalidator
    Created on : Jun 2, 2012, 8:05:09 AM
    Author     : Administrator
--%><%@page import="javax.xml.datatype.DatatypeFactory"%><%@page import="javax.xml.datatype.Duration"%><%@page import="org.miloss.fgsms.presentation.Helper"%><%@page import="org.miloss.fgsms.common.Utility"%><%@page contentType="text/html" pageEncoding="UTF-8"%><%
    if (!Utility.stringIsNullOrEmpty(request.getParameter("val"))) {
        String s = request.getParameter("val").trim();
        try {
            Duration d = Helper.StringToDuration(s);
            if (d == null) {
                out.write("Invalid, parsing error");
            } else {
                out.write("Valid!");
            }
        } catch (Exception ex) {
            out.write("Invalid, parsing error, " + ex.getMessage());
        }
    } else {
        out.write("Invalid, nothing specified");
    }%>