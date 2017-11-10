<%-- 
    Document   : machineDataJson
    Created on : Sep 24, 2012, 4:05:48 PM
    Author     : Administrator
--%>
<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="com.google.gson.Gson"%>

<%@page import="java.io.StringWriter"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.PCS"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineRequestMsg"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>

<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.common.Utility"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

    if (!Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
        SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
        ProxyLoader pl = ProxyLoader.getInstance(application);

        try {
 
            DataAccessService dasport = pl.GetDAS(application, request, response);
            GetCurrentBrokerDetailsRequestMsg req = new GetCurrentBrokerDetailsRequestMsg();
            req.setUrl(request.getParameter("uri"));
            req.setClassification(c);
Gson gson = new Gson();
            GetCurrentBrokerDetailsResponseMsg res = dasport.getCurrentBrokerDetails(req);

            if (res != null && !res.getQueueORtopicDetails().isEmpty()) {

                out.write("{" + gson.toJson(request.getParameter("uri")) + ":[{");
                for (int i = 0; i < res.getQueueORtopicDetails().size(); i++) {
                  /*  StringWriter sw = new StringWriter();
                    Configuration jsonconfig = new Configuration();
                    MappedNamespaceConvention con = new  MappedNamespaceConvention(jsonconfig);
                    AbstractXMLStreamWriter w = new MappedXMLStreamWriter(con, sw);
                    w.writeCharacters(res.getQueueORtopicDetails().get(i).getCanonicalname());
                    w.close();
                    sw.close();
                    out.write(sw.toString());*/



                  out.write(gson.toJson(res.getQueueORtopicDetails().get(i).getCanonicalname()));
                    out.write( ": {");
                    out.write("\"activeconsumers\":" + res.getQueueORtopicDetails().get(i).getActiveconsumercount() + ",");
                    out.write("\"totalconsumers\":" + res.getQueueORtopicDetails().get(i).getConsumercount() + ",");
                    out.write("\"bytesin\":" + res.getQueueORtopicDetails().get(i).getBytesin() + ",");
                    out.write("\"bytesout\":" + res.getQueueORtopicDetails().get(i).getBytesout() + ",");
                    out.write("\"bytesdropped\":" + res.getQueueORtopicDetails().get(i).getBytesdropped() + ",");
                    out.write("\"msgin\":" + res.getQueueORtopicDetails().get(i).getRecievedmessagecount() + ",");
                    out.write("\"msgout\":" + res.getQueueORtopicDetails().get(i).getMessagecount() + ",");
                    out.write("\"msgdropped\":" + res.getQueueORtopicDetails().get(i).getMessagesdropped() + ",");
                    out.write("\"depth\":" + res.getQueueORtopicDetails().get(i).getQueueDepth() + ",");
                    out.write("\"timestamp\":" + res.getQueueORtopicDetails().get(i).getTimestamp().getTimeInMillis());

                    out.write("}");
                    if (i != (res.getQueueORtopicDetails().size() - 1)) {
                        out.write(",");
                    }
                }

                out.write("}]}");

            } else {
                //out.write("No records returned");
            }
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught generating process charts over time", ex);
        }

    }

%>

