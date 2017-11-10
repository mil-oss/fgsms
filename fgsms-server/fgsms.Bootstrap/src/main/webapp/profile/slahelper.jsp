<%-- 
    Document   : slahelper, point here is to render an editable version of an existing sla ACTIONS
    Created on : Oct 19, 2015, 8:50:05 PM
    Author     : alex
--%>


<%@page import="org.miloss.fgsms.services.interfaces.common.NameValuePair"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePolicy"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.PCS"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginInformationResponseMsg"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

     SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
     ProxyLoader pl = ProxyLoader.getInstance(application);

     StringBuilder rightside = new StringBuilder();

     String serviceUrl = request.getParameter("url");
     String slaId = request.getParameter("slaId");
     if (slaId != null && serviceUrl != null) {
          // GetPluginInformationResponseMsg info = Helper.GetPluginInfo(classname, plugintype, pl.GetPCS(application, request, response), c);
          PCS pcs = pl.GetPCS(application, request, response);

          ServicePolicyRequestMsg sprm = new ServicePolicyRequestMsg();
          sprm.setClassification(c);
          sprm.setURI(serviceUrl);
          ServicePolicyResponseMsg policy = pcs.getServicePolicy(sprm);

          if (policy == null || policy.getPolicy() == null || policy.getPolicy().getServiceLevelAggrements() == null
               || policy.getPolicy().getServiceLevelAggrements().getSLA() == null
               || policy.getPolicy().getServiceLevelAggrements().getSLA().isEmpty()) {
               rightside.append("<h4>").append("No information returned!").append("</h4>");
          } else {
               for (int i = 0; i
                    < policy.getPolicy().getServiceLevelAggrements().getSLA().size(); i++) {
                    if (policy.getPolicy().getServiceLevelAggrements().getSLA().get(i).getGuid().equalsIgnoreCase(slaId)) {
                         for (int k = 0; k < policy.getPolicy().getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().size(); k++) {

                              GetPluginInformationResponseMsg info = Helper.GetPluginInfo(policy.getPolicy().getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(k).getImplementingClassName(),
				   "SLA_ACTION", pl.GetPCS(application, request, response), c);
			      if (info==null)
				   continue
					;
			      
			      SLAAction action = policy.getPolicy().getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(k);
                              rightside.append("<h4>").append(Utility.encodeHTML(info.getDisplayName())).append("</h4>");

                              rightside.append("<b>Help: </b>").append(info.getHelp());
                              rightside.append("<br>");
                              if (!info.getRequiredParameter().isEmpty()) {
                                   rightside.append("<h5>Required Parameters</h5>");
                                   rightside.append("<input type=hidden name=\"plugin_classname\" value=\"" + Utility.encodeHTML("classname") + "\">");
                                   rightside.append("<table class=\"table table-hover\"><tr><th>Key</th><th>Value</th><th>Encrypt on Save?</th></tr>");
                                   for (int x3 = 0; x3 < info.getRequiredParameter().size(); x3++) {
                                        rightside.append("<tr>"
                                             + "<td>" + Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + ""
                                             + "<td><input type=text name=\"plugin_" + Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" value=\"");
					//find the record for this value.
					NameValuePair param= Utility.getNameValuePairByName(action.getParameterNameValue(),info.getRequiredParameter().get(x3).getName() );
					if (param!=null)
					     rightside.append(Utility.encodeHTML(param.getValue()));
					rightside.append("\"></td>"
                                             + "<td><input type=checkbox name=\"p_enc_" + Utility.encodeHTML(info.getRequiredParameter().get(x3).getName()) + "\" " + (info.getRequiredParameter().get(x3).isEncryptOnSave() ? "checked=checked" : " ") + " ></td></tr>");
                                   }

                                   rightside.append("</table><br>");
                              }
                              if (!info.getOptionalParameter().isEmpty()) {
                                   rightside.append("<h5>Optional Parameters</h5>");
                                   rightside.append("<table  class=\"table table-hover\"><tr><th>Key</th><th>Value</th><th>Encrypt on Save?</th></tr>");
                                   for (int x3 = 0; x3 < info.getOptionalParameter().size(); x3++) {
                                        rightside.append("<tr>"
                                             + "<td>" + Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + ""
                                             //+ "<input type=hidden name=\"plugin_classname\" value=\"" + Utility.encodeHTML("classname") + "\"></td>"
                                             + "<td><input type=text name=\"plugin_" + Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" value=\"");
					NameValuePair param= Utility.getNameValuePairByName(action.getParameterNameValue(),info.getOptionalParameter().get(x3).getName() );
					if (param!=null)
					     rightside.append(Utility.encodeHTML(param.getValue()));
					rightside.append("\"></td>"
                                             + "<td><input type=checkbox name=\"p_enc" + Utility.encodeHTML(info.getOptionalParameter().get(x3).getName()) + "\" " + (info.getOptionalParameter().get(x3).isEncryptOnSave() ? "checked=checked" : " ") + " ></td></tr>");
                                   }

                                   rightside.append("</table>");
                              }
                         }
                         break;
                    }

               }

          }
          out.print(rightside.toString());
     } else {
          out.print("Huh?");
     }

%> 