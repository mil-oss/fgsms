<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="java.net.URLEncoder"%>
<%
//this page is a simple redirector in order to maintain compatibility with the existing SLA email alerts
    if (request.getParameter("url") != null) {
        response.sendRedirect("index.jsp?target=profile.jsp?url=" + URLEncoder.encode(request.getParameter("url"),Constants.CHARSET));
    }


%>