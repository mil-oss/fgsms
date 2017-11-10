<%-- 
    Document   : getPluginParametersEditable
    Created on : Jan 7, 2014, 10:24:04 AM
    Author     : localadministrator
--%>

<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginInformationResponseMsg"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<%

    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);

    StringBuilder rightside = new StringBuilder();
    String classname = request.getParameter("classname");
    String plugintype = request.getParameter("plugintype");
    
    if (classname != null && plugintype != null) {
        GetPluginInformationResponseMsg info = Helper.GetPluginInfo(classname, plugintype, pl.GetPCS(application, request, response), c);
        if (info==null){
            rightside.append("<h4>").append("No information returned!").append("</h4>");
        } else{
            String temp=plugintype.split("_")[1] + "_";
                rightside.append("<h4>").append(Utility.encodeHTML(info.getDisplayName())).append("</h4>");

                //this is really the only place XSS can occur, the info.getHelp is populated directly from the plugin
                //html is allowed in the plugin help section, but not script elements
                rightside.append("<b>Help: </b>").append(Helper.stripScripts(info.getHelp()));
                rightside.append("<br>");
                if (!info.getRequiredParameter().isEmpty()) {
                    rightside.append("<h5>Required Parameters</h5>");
                   // rightside.append("<input type=hidden name=\"plugin_"+temp+"class\" value=\"" + Utility.encodeHTML("classname") + "\">");
                    rightside.append("<table class=\"table table-hover\"><tr><th>Key</th><th>Value</th><th>Encrypt on Save?</th><th>Is Encrypted?</th></tr>");
                    for (int x3 = 0; x3 < info.getRequiredParameter().size(); x3++) {
                        rightside.append("<tr>"
                                + "<td>" + Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + ""
                                + "<td><input type=text "
			     + " name=\"plugin_" +temp+ Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" "
			     + " id=\"plugin_" +temp+ Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" "
			     + "></td>"
                                + "<td><input type=checkbox "
                                     //_encOnSave_
                                     //_enc_ is it encrypted now?
			     + "name=\"plugin_" +temp+ "encOnSave_"+Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" "
			     + "id=\"plugin_" +temp+ "encOnSave_"+Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" "
			     + "" + (info.getRequiredParameter().get(x3).isEncryptOnSave() ? "checked=checked" : " ") + " ></td><td><input type=checkbox "
                                     //_encOnSave_
                                     //_enc_ is it encrypted now?
			     + "name=\"plugin_" +temp+ "enc_"+Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" "
			     + "id=\"plugin_" +temp+ "enc_"+Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" "
			     + "" + (info.getRequiredParameter().get(x3).isEncrypted() ? "checked=checked" : " ") + " ></td></tr>");
                    }

                    rightside.append("</table>");
                }
                if (!info.getOptionalParameter().isEmpty()) {
                    rightside.append("<h5>Optional Parameters</h5>");
                    rightside.append("<table  class=\"table table-hover\"><tr><th>Key</th><th>Value</th><th>Encrypt on Save?</th><th>Is Encrypted?</th></tr>");
                    for (int x3 = 0; x3 < info.getOptionalParameter().size(); x3++) {
                        rightside.append("<tr>"
                                + "<td>" + Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + ""
                                //+ "<input type=hidden name=\"plugin_classname\" value=\"" + Utility.encodeHTML("classname") + "\"></td>"
                                + "<td><input type=text "
			     + "name=\"plugin_" +temp+ Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" "
			     + "id=\"plugin_" +temp+ Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" "
			     + "></td>"
                                + "<td><input type=checkbox "
			     + "name=\"plugin_" +temp+"encOnSave_"+ Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" "
			     + "id=\"plugin_" +temp+"encOnSave_"+ Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" "
			     + "" + (info.getOptionalParameter().get(x3).isEncryptOnSave() ? "checked=checked" : " ") + " ></td>"
                                     + "<td><input type=checkbox "
			     + "name=\"plugin_" +temp+"enc_"+ Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" "
			     + "id=\"plugin_" +temp+"enc_"+ Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" "
			     + "" + (info.getOptionalParameter().get(x3).isEncrypted()? "checked=checked" : " ") + " ></td></tr>");
                    }


                    rightside.append("</table>");
                }
        }
        out.print(rightside.toString());
    } else {
        out.print("Huh?");
    }

%> 