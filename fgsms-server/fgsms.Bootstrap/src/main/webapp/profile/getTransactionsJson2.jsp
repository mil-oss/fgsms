<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="com.google.gson.Gson"%><%@include file="../csrf.jsp" %><%@page import="org.miloss.fgsms.common.Constants"%><%@page import="org.miloss.fgsms.common.Constants.AuthMode"%><%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%><%@page import="org.apache.log4j.Level"%><%@page import="org.miloss.fgsms.common.Logger"%> <%@page import="us.gov.ic.ism.v2.ClassificationType"%><%@page import="java.util.Map"%><%@page import="javax.xml.ws.BindingProvider"%><%@page import="java.util.List"%><%@page import="org.miloss.fgsms.presentation.*"%><%@page import="java.util.Calendar"%><%@page import="javax.xml.datatype.XMLGregorianCalendar"%><%@page import="javax.xml.datatype.DatatypeFactory"%><%@page import="java.util.GregorianCalendar"%><%@page import="java.util.GregorianCalendar"%><%@page import="org.miloss.fgsms.services.interfaces.status.*"%><%@page import="java.net.URLEncoder"%><%@page import="org.miloss.fgsms.common.Utility"%><%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%><%@page import="org.miloss.fgsms.presentation.*"%><%@page import="java.util.Properties"%><%@page import="java.net.URL"%><%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%
    response.setContentType("application/json");
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);

    if (!Utility.stringIsNullOrEmpty(request.getParameter("url")) && !Utility.stringIsNullOrEmpty(request.getParameter("since"))) {
 
        try {
            long x = Long.parseLong(request.getParameter("since"));
            if (x < 0 || x > System.currentTimeMillis())
                               {
                response.setStatus(400);
                return;
                //throw new IllegalArgumentException("since");
            }
            //TODO validate
            DataAccessService dasport = pl.GetDAS(application, request, response);
            GetMessageLogsRequestMsg req = new GetMessageLogsRequestMsg();
            req.setClassification(c);
            req.setURL(request.getParameter("url"));
            req.setOffset(0);
            req.setRecords(150);
            req.setRange(new TimeRange());
            DatatypeFactory df = DatatypeFactory.newInstance();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis() + 5000);
            req.getRange().setEnd((gcal));
            
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(x);
            req.getRange().setStart((gcal));
            Gson gson = new Gson();
            GetMessageLogsResponseMsg res = dasport.getMessageLogsByRange(req);
            out.write("{" + gson.toJson(request.getParameter("url")) + ":[{");
            for (int i = 0; i < res.getLogs().getTransactionLog().size(); i++) {
                out.write( gson.toJson(res.getLogs().getTransactionLog().get(i).getTransactionId()) + ":{ "
                        + "\"timestamp\":" + res.getLogs().getTransactionLog().get(i).getTimestamp().getTimeInMillis() + ","
                        + "\"responsetime\":" + res.getLogs().getTransactionLog().get(i).getResponseTime() + ","
                        + "\"success\":" + !res.getLogs().getTransactionLog().get(i).isIsFault() + ","
                        + "\"slafault\":" + res.getLogs().getTransactionLog().get(i).isIsSLAFault() + ","
                        + "\"requesturl\":" +  gson.toJson(res.getLogs().getTransactionLog().get(i).getURL()) + ","
                        + "\"recordedat\":" +  gson.toJson(res.getLogs().getTransactionLog().get(i).getServiceHostname()) + ","
                        + "\"requestsize\":" + res.getLogs().getTransactionLog().get(i).getRequestSize() + ","
                        + "\"responsesize\":" + res.getLogs().getTransactionLog().get(i).getResponseSize() + ","
                        + "\"consumer\":" +  gson.toJson(Utility.listStringtoString(res.getLogs().getTransactionLog().get(i).getIdentity())) + ","
                        + "\"requestpayloadavailable\":" + res.getLogs().getTransactionLog().get(i).isHasRequestMessage() + ","
                        + "\"responsepayloadavailable\":" + res.getLogs().getTransactionLog().get(i).isHasResponseMessage() + ","
                        + "\"action\":" +  gson.toJson(res.getLogs().getTransactionLog().get(i).getAction()) + "}");

                if (i != (res.getLogs().getTransactionLog().size() - 1)) {
                    out.write(",");
                }
            }
            out.write("}]}");

        } catch (Exception ex) {
            response.setStatus(400);
            log(ex.getMessage());
        }
    }
%>