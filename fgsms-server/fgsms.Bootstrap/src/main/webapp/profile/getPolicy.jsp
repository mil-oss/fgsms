<%@page import="java.util.List"%>
<%     /**
      * This file handles all of the service policy management functions
      */
%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.*"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="javax.xml.bind.JAXBElement"%> 
<%@page import="java.math.BigInteger"%>
<%@page import="java.util.UUID"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Enumeration"%>  
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>

<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@include file="../csrf.jsp" %>
<%    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
     ProxyLoader pl = ProxyLoader.getInstance(application);
     ServicePolicy pol = null;
     try {

          pol = (ServicePolicy) request.getSession().getAttribute(request.getParameter("url"));
     } catch (Exception ex) {
     }
     StringBuilder rightside = new StringBuilder();

     boolean saved = false;
     boolean deleted = false;
     try {

          PCS pcsport = pl.GetPCS(application, request, response);

          List<Plugin> plugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "SLA_RULE", pol.getPolicyType());
          DataAccessService dasport = pl.GetDAS(application, request, response);

          DatatypeFactory df = DatatypeFactory.newInstance();

          if (Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
               response.sendRedirect("index.jsp");
          }

          if (request.getMethod().equalsIgnoreCase("post")) {
               SetServicePolicyRequestMsg req3 = new SetServicePolicyRequestMsg();

            //########################################## debugging #############################################
/*
                Enumeration enumera = request.getParameterNames();
                while (enumera.hasMoreElements()) {
                String s = (String) enumera.nextElement();
                out.write(s + "=" + request.getParameter(s) + "<br>");

                }


                */
            //########################################## debugging #############################################
               if (pol == null) {
                    response.sendRedirect("index.jsp");
               } else {
                    Integer i = 1024000;
                    try {
                         i = Integer.parseInt(request.getParameter("messagecap"));
                         if (i < 1024) {
                              i = 1024;
                         }
                    } catch (Exception ex) {
                    }

                    if (!Utility.stringIsNullOrEmpty(request.getParameter("datattl"))) {
                         pol.setDataTTL(Helper.StringToDuration(request.getParameter("datattl")));
                    }
                    if (!Utility.stringIsNullOrEmpty(request.getParameter("refreshrate"))) {
                         pol.setPolicyRefreshRate(Helper.StringToDuration(request.getParameter("refreshrate")));
                    }

                    if (pol instanceof ProcessPolicy) {
                         ProcessPolicy mp = (ProcessPolicy) pol;
                         mp.setAlsoKnownAs(request.getParameter("processaka"));
                         //

                         if (!Utility.stringIsNullOrEmpty(request.getParameter("recordopenfiles"))) {
                              mp.setRecordOpenFileHandles(true);
                         } else {
                              mp.setRecordOpenFileHandles(false);
                         }

                         if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcpuusage"))) {
                              mp.setRecordCPUusage(true);
                         } else {
                              mp.setRecordCPUusage(false);
                         }

                         if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcmemusage"))) {
                              mp.setRecordMemoryUsage(true);
                         } else {
                              mp.setRecordMemoryUsage(false);
                         }

                    }

                    if (pol instanceof MachinePolicy) {
                         MachinePolicy mp = (MachinePolicy) pol;

//recorddiskusage
//recordnetworkusage
                         if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcpuusage"))) {
                              mp.setRecordCPUusage(true);
                         } else {
                              mp.setRecordCPUusage(false);
                         }

                         if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcmemusage"))) {
                              mp.setRecordMemoryUsage(true);
                         } else {
                              mp.setRecordMemoryUsage(false);
                         }

                         mp.getRecordDiskUsagePartitionNames().clear();
                         if ((request.getParameterValues("recorddiskusage") != null)) {
                              String[] t = request.getParameterValues("recorddiskusage");
                              for (int k = 0; k < t.length; k++) {
                                   if (!Utility.stringIsNullOrEmpty(t[k])) {
                                        mp.getRecordDiskUsagePartitionNames().add(t[k]);
                                   }
                              }
                         }

                         mp.getRecordNetworkUsage().clear();
                         if ((request.getParameterValues("recordnetworkusage") != null)) {
                              String[] t = request.getParameterValues("recordnetworkusage");
                              for (int k = 0; k < t.length; k++) {
                                   if (!Utility.stringIsNullOrEmpty(t[k])) {
                                        mp.getRecordNetworkUsage().add(t[k]);
                                   }
                              }
                         }

                         mp.getRecordDiskSpace().clear();
                         if ((request.getParameterValues("recorddrivespace") != null)) {
                              String[] t = request.getParameterValues("recorddrivespace");
                              for (int k = 0; k < t.length; k++) {
                                   if (!Utility.stringIsNullOrEmpty(t[k])) {
                                        mp.getRecordDiskSpace().add(t[k]);
                                   }
                              }
                         }
                    }

                    if (pol instanceof TransactionalWebServicePolicy) {
                         TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
                         if (!Utility.stringIsNullOrEmpty(request.getParameter("messagecap"))) {
                              try {
                                   int cap = Integer.parseInt(request.getParameter("messagecap"));
                                   tp.setRecordedMessageCap(cap);
                              } catch (Exception ex) {
                              }
                         }
                         tp.setRecordedMessageCap(i);
                         if (Utility.stringIsNullOrEmpty(request.getParameter("recordrequest"))) {
                              tp.setRecordRequestMessage(false);
                         } else {
                              tp.setRecordRequestMessage(true);
                         }
                         if (Utility.stringIsNullOrEmpty(request.getParameter("recordresponse"))) {
                              tp.setRecordResponseMessage(false);
                         } else {
                              tp.setRecordResponseMessage(true);
                         }
                         if (Utility.stringIsNullOrEmpty(request.getParameter("recordfault"))) {
                              tp.setRecordFaultsOnly(false);
                         } else {
                              tp.setRecordFaultsOnly(true);
                         }

                         if (Utility.stringIsNullOrEmpty(request.getParameter("buellerenabled"))) {
                              tp.setBuellerEnabled(false);
                         } else {
                              tp.setBuellerEnabled(true);
                         }

                         if (Utility.stringIsNullOrEmpty(request.getParameter("healthstatusenabled"))) {
                              tp.setHealthStatusEnabled(false);
                         } else {
                              tp.setHealthStatusEnabled(true);
                         }

                         if (Utility.stringIsNullOrEmpty(request.getParameter("recordheaders"))) {
                              tp.setRecordHeaders(false);
                         } else {
                              tp.setRecordHeaders(true);
                         }
                    }

                    if (Utility.stringIsNullOrEmpty(request.getParameter("externalurl"))) {
                         pol.setExternalURL(null);
                    } else {
                         pol.setExternalURL(request.getParameter("externalurl"));
                    }

                    if (Utility.stringIsNullOrEmpty(request.getParameter("displayName"))) {
                         pol.setDisplayName(null);
                    } else {
                         pol.setDisplayName(request.getParameter("displayName"));
                    }
                    if (Utility.stringIsNullOrEmpty(request.getParameter("servername"))) {
                         pol.setMachineName(null);
                    } else {
                         pol.setMachineName(request.getParameter("servername"));
                    }
                    if (Utility.stringIsNullOrEmpty(request.getParameter("domainname"))) {
                         pol.setDomainName(null);
                    } else {
                         pol.setDomainName(request.getParameter("domainname"));
                    }
                    if (Utility.stringIsNullOrEmpty(request.getParameter("bucket"))) {
                         pol.setBucketCategory(null);
                    } else {
                         pol.setBucketCategory(request.getParameter("bucket"));
                    }
                    if (Utility.stringIsNullOrEmpty(request.getParameter("parentobject"))) {
                         pol.setParentObject(null);
                    } else {
                         pol.setParentObject(request.getParameter("parentobject"));
                    }

                    if (Utility.stringIsNullOrEmpty(request.getParameter("description"))) {
                         pol.setDescription(null);
                    } else {
                         pol.setDescription(request.getParameter("description"));
                    }
                    if (Utility.stringIsNullOrEmpty(request.getParameter("poc"))) {
                         pol.setPOC(null);
                    } else {
                         pol.setPOC(request.getParameter("poc"));
                    }

                    if (!Utility.stringIsNullOrEmpty(request.getParameter("locationlat"))
                            && !Utility.stringIsNullOrEmpty(request.getParameter("locationlong"))) {
                         try {
                              double lat = Double.parseDouble(request.getParameter("locationlat"));
                              double lon = Double.parseDouble(request.getParameter("locationlong"));
                              if (lon >= -180 && lon <= 180 && lat <= 90 && lat >= -90) {
                                   GeoTag location = new GeoTag();
                                   location.setLatitude(lat);
                                   location.setLongitude(lon);
                                   pol.setLocation((location));
                              }
                         } catch (Exception ex) {
                         }
                    }

                //UserID data here
                    if (!Utility.stringIsNullOrEmpty(request.getParameter("savepolicy"))) {
                         //todo validat that the policy is correct
                         try {
                              if (pol instanceof TransactionalWebServicePolicy) {
                                   req3.setPolicy((TransactionalWebServicePolicy) pol);
                              } else if (pol instanceof StatusServicePolicy) {
                                   req3.setPolicy((StatusServicePolicy) pol);
                              } else if (pol instanceof StatisticalServicePolicy) {
                                   req3.setPolicy((StatisticalServicePolicy) pol);
                              } else if (pol instanceof MachinePolicy) {
                                   req3.setPolicy((MachinePolicy) pol);
                              } else if (pol instanceof ProcessPolicy) {
                                   req3.setPolicy((ProcessPolicy) pol);
                              } else {
                                   req3.setPolicy(pol);
                              }
                              req3.setURL(pol.getURL());

                              req3.setClassification(c);
                              SetServicePolicyResponseMsg res3 = pcsport.setServicePolicy(req3);
                              request.getSession().removeAttribute(request.getParameter("url"));
                              out.write("<font color=#FF0000>Policy Saved.</font><br>");
                              saved = true;
                         } catch (SecurityException se) {
                              out.write("<font color=#FF0000>Access was denied.</font><Br>");
                         } catch (Exception ex) {
                              out.write("<font color=#FF0000>Error saving policy: " + ex.getLocalizedMessage() + "</font><bR>");
                         }
                    }

                    //addFedMethod
                    if (!Utility.stringIsNullOrEmpty(request.getParameter("addFedMethod"))) {

                         rightside.append("The Federation Policy is a way to define how data collected about this service can be shared. Information such as last known "
                                 + "status and various statistics can be made available via a federation plugin."
                                 + "Each federation plugin may require configuration settings. Currently registered plugins are: <ul>");
                         List<Plugin> fedplugions = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "FEDERATION_PUBLISH");
                         for (int i3 = 0; i3 < fedplugions.size(); i3++) {
                              rightside.append(("<li>" + Utility.encodeHTML(fedplugions.get(i3).getDisplayname()) + "</li>"));
                         }
                         rightside.append("</ul>");
                         rightside.append("You can specify multiple targets by adding additional policies, however only one target of each type is supported. Information related to which server data is published to "
                                 + "is set by the administrators of this FGSMS stance.<br><br>"
                                 + "When publishing data to UDDI, a binding key must be specified. This represents a unique identifier associated with a 'Endpoint Binding Key'. "
                                 + "Other federation targets do not require the binding key.  SImplly select what data you wish to share and what range of time"
                                 + " the data should be calculated over. This information is calculate at the time of publication.<br><br>"
                                 + "<br><Br><table><tr><td>");

                         rightside.append("Federation Target</td><td><select name=\"fedtarget\" id=\"fedtarget\" onchange=\"refreshPluginParams('fedtarget','pluginparameters','FEDERATION_PUBLISH');\">");
                         for (int i3 = 0; i3 < fedplugions.size(); i3++) {
                              rightside.append(("<option value=" + Utility.encodeHTML(fedplugions.get(i3).getClassname()) + ">" + Utility.encodeHTML(fedplugions.get(i3).getDisplayname()) + "</option>"));
                         }
                         rightside.append("</select></td</tr></table>");

       // rightside.append("<a class=\"btn btn-primary\" href=\"javascript:refreshPluginParams('fedtarget','pluginparameters','FEDERATION_PUBLISH')\">Refresh</a> <br>");
                         rightside.append("<div id=\"pluginparameters\">Plug a plugin!</div>");

                         rightside.append("<input type=button name=addFedRecord value=\"Add\" onclick=\"javascript:postBackReRender('addFedRecord','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">");

                    }
%>



<script type="text/javascript"  >
   
    refreshPluginParams('fedtarget', 'pluginparameters', 'FEDERATION_PUBLISH');
    refreshPluginParams('rule1', 'SLARuleGenericdiv', 'SLA_RULE');
    refreshPluginParams('Action', 'pluginaction', 'SLA_ACTION');

   
</script>
<%
               //Add a new federation record
               if (!Utility.stringIsNullOrEmpty(request.getParameter("addFedRecord"))) {
                    if (pol.getFederationPolicyCollection() == null) {
                         pol.setFederationPolicyCollection(new FederationPolicyCollection());
                    }
                    boolean ok = true;
                    String ft = null;
                    try {
                         ft = (request.getParameter("fedtarget"));
                    } catch (Exception ex) {
                         ok = false;
                         rightside.append("<h2>Unrecognized federation target</h2>");
                    }
                    if (Utility.stringIsNullOrEmpty(ft)) {
                         ok = false;
                         rightside.append("<h2>Unrecognized federation target</h2>");
                    }
                    if (ok) {
                         //validation
                         for (int i3 = 0; i3 > pol.getFederationPolicyCollection().getFederationPolicy().size(); i3++) {
                              if (ft.equals(pol.getFederationPolicyCollection().getFederationPolicy().get(i3).getImplementingClassName())) {
                                   ok = false;
                                   rightside.append("<h2>You can only have one instance of each federation target</h2>");
                              }
                         }

                    }

                    if (ok) {
                         FederationPolicy fp = new FederationPolicy();
                         fp.setImplementingClassName(ft);
                         fp = Helper.BuildFederation(fp, pl.GetPCS(application, request, response), c, request);

                         pol.getFederationPolicyCollection().getFederationPolicy().add(fp);
                         rightside.append("<h2>Federation policy added (don't forget to save your changes!).</h2>");
                    }
               }

	       int editsla2 = -1;
	       Enumeration enumer = request.getParameterNames();
               while (enumer.hasMoreElements()) {
		    String namedParam =(String)enumer.nextElement();
                    if (namedParam.startsWith("EditSLA")) {
                         String ns = namedParam.replace("EditSLA", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                   editsla2 = i2;
                              }
                         } catch (Exception ex) {
                         }
                    }
               }



               //addSLAMethod initial look
               if (!Utility.stringIsNullOrEmpty(request.getParameter("addSLAMethod")) || editsla2 >=0) {
		    SLA existing=null;
		    if (editsla2>=0){
			existing= pol.getServiceLevelAggrements().getSLA().get(editsla2);
		    }

                    rightside.append("Adding a Service Level Agreement is easy. A SLA requires at least one triggering rule and at least one action "
                            + "to perform. Rules vary by the service policy type.");
                    rightside.append("<br><br>");

                    rightside.append("<h3>Rules</h3>");

                    rightside.append("<select name=\"rule1\" id=\"rule1\" onchange=\"refreshPluginParams('rule1','SLARuleGenericdiv','SLA_RULE');\">");
                    
                    for (int i5 = 0; i5 < plugins.size(); i5++) {
			 
                         rightside.append("<option value=\"" + Utility.encodeHTML(plugins.get(i5).getClassname()) + "\"  ");
			 if (existing!=null && ((SLARuleGeneric)existing.getRule()).getClassName().equals(plugins.get(i5).getClassname()))
			      rightside.append(" selected ");
			 rightside.append(">" + Utility.encodeHTML(plugins.get(i5).getDisplayname()) + "</option>");
                    }
                    rightside.append("</select>");

                    rightside.append("<div id=\"SLARuleGenericdiv\" >Pick a Rule plugin!"
                            + "</div>");

                    out.write("<br><h3>Actions</h3>");
		    out.write("<br><h2>Existing Actions</h2>");

		    out.write("<div id=\"existingSLAactions\"></div>");
		    out.write("<script type=\"text/javascript\">"
			 + "//existingSLAs"
			 + "function loadExistingActions(){"
			 + "var request=   $.ajax({url: 'profile/slahelper.jsp?url=" + pol.getURL() + "&slaId=" + existing.getGuid() + "', type:'GET', cache: false});"
			 + "request.done(function(msg) {$('#existingSLAactions').html( msg );});"
			 + "request.fail(function(jqXHR, textStatus) {"
			 + "window.console && console.log('loadpage_params failed ' + link);$('#existingSLAactions').html( jqXHR.responseText );});"
			 + "}"
			 + "loadExistingActions();"
			 
			 + "</script>");
                    rightside.append("<select name=\"Action\" id=\"Action\" onchange=\"refreshPluginParams('Action','pluginaction','SLA_ACTION');\">");
                    List<Plugin> actions = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "SLA_ACTION", pol.getPolicyType());
                    for (int i5 = 0; i5 < actions.size(); i5++) {
			 
                         rightside.append("<option value=\"" + Utility.encodeHTML(actions.get(i5).getClassname()) + "\"" );
			 
			 rightside.append(" >" + Utility.encodeHTML(actions.get(i5).getDisplayname()) + "</option>");
                    }
                    rightside.append("</select><Br>");
                    rightside.append("<div  id=pluginaction name=pluginaction>Pick an Action!</div>");
                    rightside.append("<input type=button name=addSLARecord value=\"Add SLA\" onclick=\"javascript:postBackReRender('addSLARecord','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">");
		    
		    
		   
               }

//////////////// add SLA postback
               if (!Utility.stringIsNullOrEmpty(request.getParameter("addSLARecord"))) {
                    boolean ok = true;
                    SLA newsla = new SLA();
                    newsla.setGuid(UUID.randomUUID().toString());
                    RuleBaseType rule = null;
                    SLAAction action = null;
                    if (!Utility.stringIsNullOrEmpty(request.getParameter("rule1"))) {

                         rule = Helper.BuildRule(request, pcsport, c);
                         if (rule == null) {
                              ok = false;
                              rightside.append(" There was an error validating the text you've entered. If the parameter expects a numerical value, only enter numbers. Negative numbers are not allowed. ");
                         }
                    } else {
                         ok = false;
                    }


                    if (!Utility.stringIsNullOrEmpty(request.getParameter("Action"))) {
                         action = Helper.BuildSLAAction(request, pl.GetPCS(application, request, response), c);
                         if (action == null) {
                              rightside.append(" There was an error validating SLA Action information. The request was aborted. ");
                         }
                    }

                    if (rule == null || action == null) {
                         rightside.append(" There was an error validating SLA Rule information. The request was aborted. ");
                         LogHelper.getLog().log(Level.ERROR, "rule is null");
                    } else {
                         newsla.setRule(rule);
			 
                         newsla.setAction(new ArrayOfSLAActionBaseType());
                         newsla.getAction().getSLAAction().add(action);

                         if (pol.getServiceLevelAggrements() == null
                                 || pol.getServiceLevelAggrements().getSLA() == null
                                 || pol.getServiceLevelAggrements().getSLA().isEmpty()) {
                              ArrayOfSLA slas = new ArrayOfSLA();
                              slas.getSLA().add(newsla);
                              pol.setServiceLevelAggrements((slas));

                         } else {
                              pol.getServiceLevelAggrements().getSLA().add(newsla);
                         }
                         rightside.append("<br><br><br><h2>SLA Successfully added! Don't forget to save</h2>");
                    }
               }
//////////////// END add SLA 

               //addUserIDMethod
               if (!Utility.stringIsNullOrEmpty(request.getParameter("addUserIDMethod"))) {

                    rightside.append("FGSMS can optional identify the requestor of a web service via a variety of mechanisms including"
                            + "<ul><li>Http Header Value - commonly used for applications residing behind C-TNOSC reverse SSL proxies or with Single Sign On solutions</li>"
                            + "<li>Http Identity - typically a username or PKI identity. As of RC4, this information is automatically recorded by the agents as well as the requestor's IP address</li>"
                            + "<li>XPath Expression - typically used for token based identification, such as SAML, WS-Username, or for selecting a "
                            + "Binary Security Token that is an X509 Digital Certificate. If the result is an encoded X509 Certificate, you can also optionally"
                            + " have FGSMS agents just record the subject name of the certificate instead of the entire token. <Br><Br>"
                            + "Note: .NET based Agents: all XML prefix/namespace definitions "
                            + "used in the XPath expression must be defined. Java based agents do not require prefix/namespace definitions (this also implies"
                            + " that you need to leave out all prefixes from your xpath query expressions for Java agents.</li></ul>"
                            + "Depending on the underlying framework, each mechanism may or may not be support by a given agent implementation.");
                    rightside.append("<br><br>User Identification Method:"
                            + "<select  onchange=\"toggleVisibilityUserID();\" name=useridtype id=useridtype>"
                            + "<option value=httpcred>Http Credential</option>"
                            + "<option value=httpheader>Http Header</option>"
                            + "<option value=xpath>XPath Expression</option>"
                            + "</select><br>");
                    rightside.append("<div id=httpheaderdiv style=\"display:none;\">Http Header Parameter<input type=text name=httpheadername></div><br>");
                    rightside.append("<div id=xpathdiv style=\"display:none;\">XPath Expression <input style=\"width: 250px\" type=text id=xpathexpression name=xpathexpression><br>");
                    rightside.append("XPath Prefix definitions <input style=\"width: 250px; height:25px; \" type=text id=xpathprefixes name=xpathprefixes value=\"prefix##namespace|prefix2##namespace2\"><br>");
                    rightside.append("Does this XPath expression yield an X509 Certificate? <input type=checkbox name=x509certxpath></div><br><br>");
                    rightside.append("<input type=button name=addUserID value=\"Add User Identification\" onclick=\"javascript:postBackReRender('addUserID','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">");

               }

               if (!Utility.stringIsNullOrEmpty(request.getParameter("addUserID"))) {
                    boolean ok = true;
                    if (Utility.stringIsNullOrEmpty(request.getParameter("useridtype"))) {
                         ok = false;
                         out.write("unable to determine user id type selection");
                    }
                    UserIdentity newuser = new UserIdentity();
                    String s = request.getParameter("useridtype");
                    if (s != null && s.equalsIgnoreCase("httpcred")) {
                         newuser.setUseHttpCredential(true);
                         newuser.setUseHttpHeader(false);
                    } else if (s != null && s.equalsIgnoreCase("httpheader")) {
                         newuser.setUseHttpHeader(true);
                         newuser.setUseHttpCredential(false);
                         if (!Utility.stringIsNullOrEmpty(request.getParameter("httpheadername"))) {
                              newuser.setHttpHeaderName((request.getParameter("httpheadername")));
                         } else {
                              ok = false;
                              out.write("unable to configure http header name, it's null or empty!");
                         }
                    } else if (s != null && s.equalsIgnoreCase("xpath")) {

                         if (Utility.stringIsNullOrEmpty(request.getParameter("xpathexpression"))) //   || Utility.stringIsNullOrEmpty(request.getParameter("xpathprefixes")))
                         {
                              ok = false;
                              out.write("xpath expression is null or empty");
                         }
                         if (ok) {
                              ArrayOfXMLNamespacePrefixies xmlns = new ArrayOfXMLNamespacePrefixies();
                              newuser.setNamespaces((xmlns));
                              ArrayOfXPathExpressionType xpath = new ArrayOfXPathExpressionType();
                              XPathExpressionType x = new XPathExpressionType();

                              if (Utility.stringIsNullOrEmpty(request.getParameter("x509certxpath"))) {
                                   x.setIsCertificate(false);
                              } else {
                                   x.setIsCertificate(true);
                              }
                              xpath.getXPathExpressionType().add(x);
                              x.setXPath(request.getParameter("xpathexpression"));
                              String t = request.getParameter("xpathprefixes");
                              String[] prefixes = t.split("\\|");
                              //out.write(prefixes.length + " prefixes detected");
                              for (int i2 = 0; i2 < prefixes.length; i2++) {
                                   String[] item = prefixes[i2].split("##");
                                   if (item.length == 2) {
                                        XMLNamespacePrefixies pf = new XMLNamespacePrefixies();

                                        pf.setPrefix(item[0]);
                                        pf.setNamespace(item[1]);
                                        xmlns.getXMLNamespacePrefixies().add(pf);

                                   } else {
                                        //out.write("error parsing xml prefixes!");
                                   }
                              }
                              newuser.setNamespaces((xmlns));
                              newuser.setXPaths((xpath));
                         }
                    }

                    if (ok && pol instanceof TransactionalWebServicePolicy) {
                         TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
                         if (tp.getUserIdentification() == null
                                 || tp.getUserIdentification() == null
                                 || tp.getUserIdentification().getUserIdentity() == null
                                 || tp.getUserIdentification().getUserIdentity().isEmpty()) {

                              ArrayOfUserIdentity uis = new ArrayOfUserIdentity();
                              uis.getUserIdentity().add(newuser);
                              tp.setUserIdentification((uis));
                         } else {
                              tp.getUserIdentification().getUserIdentity().add(newuser);
                         }
                    }
               }

               int editsla = -1;
            //SaveSLARuleChanges{slaindex}
               //RemoveSLAAction{slaindex}:actonindex
               //SaveSLAActionChanges{slaindex}:actionindex
               //AddSLAAction{slaindex}
               int saveslarulechanges = -1;
               int removeslaactionSLAindex = -1;
               int removeslaactionSLAindexActionIndex = -1;
               int saveslaactionchangesSLAindex = -1;
               int saveslaactionchangesSLAindexActionIndex = -1;
               int addslaactionSLAindex = -1;
               int editslaactionSLAindex = -1;
               int editslaactionindexActionIndex = -1;
               int Add2SLAActionSLAindex = -1;

               int saveModifiedSLAAction_SLAIndex = -1;
               int saveModifiedSLAAction_SLAIndexActionIndex = -1;
               int editfederationpol = -1;
               int saveFedRecord = -1;
                enumer = request.getParameterNames();
               while (enumer.hasMoreElements()) {
                    String namedParam = (String) enumer.nextElement();
                    if (namedParam.contains("EditFedPol")) {
                         String ns = namedParam.replace("EditFedPol", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              editfederationpol = i2;
                         } catch (Exception ex) {
                         }
                    }
                    if (namedParam.contains("saveFedRecord")) {
                         String ns = namedParam.replace("saveFedRecord", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              saveFedRecord = i2;
                         } catch (Exception ex) {
                         }
                    }
                    if (namedParam.contains("RemoveUserID")) {
                         String ns = namedParam.replace("RemoveUserID", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              if (pol instanceof TransactionalWebServicePolicy) {
                                   TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
                                   tp.getUserIdentification().getUserIdentity().remove(i2);
                              }
                         } catch (Exception ex) {
                         }
                    }
                    if (namedParam.contains("RemoveSLA")) {
                         String ns = namedParam.replace("RemoveSLA", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              pol.getServiceLevelAggrements().getSLA().remove(i2);
                              rightside.append("<h2>SLA removed. Don't forget to click Save</h2>");
                         } catch (Exception ex) {
                         }
                    }
                    if (namedParam.contains("RemoveFedPol")) {
                         String ns = namedParam.replace("RemoveFedPol", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              pol.getFederationPolicyCollection().getFederationPolicy().remove(i2);
                              rightside.append("<h2>Federation Policy removed. Don't forget to click Save</h2>");
                         } catch (Exception ex) {
                         }
                    }
                    if (namedParam.startsWith("EditSLA")) {
                         String ns = namedParam.replace("EditSLA", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                   editsla = i2;
                              }
                         } catch (Exception ex) {
                         }
                    }
                    //SaveSLARuleChanges{slaindex}
                    if (namedParam.startsWith("SaveSLARuleChanges")) {
                         String ns = namedParam.replace("SaveSLARuleChanges", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                   saveslarulechanges = i2;
                              }
                         } catch (Exception ex) {
                         }
                    }
                    //Remove2SLAAction{slaindex}:actonindex
                    if (namedParam.startsWith("Remove2SLAAction")) {
                         String ns = namedParam.replace("Remove2SLAAction", "");
                         try {
                              String[] t = ns.split(":");
                              if (t.length == 2) {
                                   int i2 = Integer.parseInt(t[0]);
                                   if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                        removeslaactionSLAindex = i2;
                                   }
                                   int i3 = Integer.parseInt(t[1]);
                                   if (i3 < pol.getServiceLevelAggrements().getSLA().get(removeslaactionSLAindex).getAction().getSLAAction().size()) {
                                        removeslaactionSLAindexActionIndex = i3;
                                   }
                              }
                         } catch (Exception ex) {
                         }
                    }
                    //SaveSLAActionChanges{slaindex}:actionindex
                    if (namedParam.startsWith("SaveSLAActionChanges")) {
                         String ns = namedParam.replace("SaveSLAActionChanges", "");
                         try {
                              String[] t = ns.split(":");
                              if (t.length == 2) {
                                   int i2 = Integer.parseInt(t[0]);
                                   if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                        saveslaactionchangesSLAindex = i2;
                                   }
                                   int i3 = Integer.parseInt(t[1]);
                                   if (i3 < pol.getServiceLevelAggrements().getSLA().get(saveslaactionchangesSLAindex).getAction().getSLAAction().size()) {
                                        saveslaactionchangesSLAindexActionIndex = i3;
                                   }
                              }
                         } catch (Exception ex) {
                         }
                    }
                    //AddSLAAction{slaindex}
                    if (namedParam.startsWith("AddSLAAction")) {
                         String ns = namedParam.replace("AddSLAAction", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                   addslaactionSLAindex = i2;
                              }
                         } catch (Exception ex) {
                         }
                    }
                    //AddSLAAction{slaindex}
                    if (namedParam.startsWith("Add2SLAAction")) {
                         String ns = namedParam.replace("Add2SLAAction", "");
                         try {
                              int i2 = Integer.parseInt(ns);
                              if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                   Add2SLAActionSLAindex = i2;
                              }
                         } catch (Exception ex) {
                         }
                    }
                    //Edit2SLAAction{slaindex}{actionidex}
                    if (namedParam.startsWith("Edit2SLAAction")) {
                         String ns = namedParam.replace("Edit2SLAAction", "");
                         try {
                              String[] t = ns.split(":");
                              if (t.length == 2) {
                                   int i2 = Integer.parseInt(t[0]);
                                   if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                        editslaactionSLAindex = i2;
                                   }
                                   int i3 = Integer.parseInt(t[1]);
                                   if (i3 < pol.getServiceLevelAggrements().getSLA().get(editslaactionSLAindex).getAction().getSLAAction().size()) {
                                        editslaactionindexActionIndex = i3;
                                   }
                              }
                         } catch (Exception ex) {
                         }
                    }
                //Edit3SLAAction save modified SLA Action  int saveModifiedSLAAction_SLAIndex=-1;
                    //int saveModifiedSLAAction_SLAIndexActionIndex=-1;
                    if (namedParam.startsWith("Edit3SLAAction")) {
                         String ns = namedParam.replace("Edit3SLAAction", "");
                         try {
                              String[] t = ns.split(":");
                              if (t.length == 2) {
                                   int i2 = Integer.parseInt(t[0]);
                                   if (i2 < pol.getServiceLevelAggrements().getSLA().size()) {
                                        saveModifiedSLAAction_SLAIndex = i2;
                                   }
                                   int i3 = Integer.parseInt(t[1]);
                                   if (i3 < pol.getServiceLevelAggrements().getSLA().get(saveModifiedSLAAction_SLAIndex).getAction().getSLAAction().size()) {
                                        saveModifiedSLAAction_SLAIndexActionIndex = i3;
                                   }
                              }
                         } catch (Exception ex) {
                         }
                    }

               }

               //EditFedPol
               if (editfederationpol > -1) {

                    rightside.append("The Federation Policy is a way to define how data collected about this service can be shared. Information such as last known "
                            + "status and various statistics can be made available via a federation plugin."
                            + "Each federation plugin may require configuration settings. Currently supported targets are: <br><ul>");
                    List<Plugin> federationPlugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "FEDERATION_PUBLISH", pol.getPolicyType());
                    for (int i3 = 0; i3 < federationPlugins.size(); i3++) {
                         rightside.append(("<li>" + Utility.encodeHTML(federationPlugins.get(i3).getDisplayname()) + "</li>"));
                    }
                    rightside.append("</ul>");
                    rightside.append("You can specify multiple plugins  by adding additional policies, however only one plugin of each type is supported across all policies. "
                            + "<br><Br>");
                    rightside.append("<h2>Edit an existing Federation Policy</h2><table>");
                    FederationPolicy fp = pol.getFederationPolicyCollection().getFederationPolicy().get(editfederationpol);

                    GetPluginInformationResponseMsg fedplugin = Helper.GetPluginInfo(fp.getImplementingClassName(), "FEDERATION_PUBLISH", pl.GetPCS(application, request, response), c);
//fedtarget

                    rightside.append("<tr><td>Federation Plugin Class</td><td><input type=text readonly name=\"fedtarget\" value=\"" + Utility.encodeHTML(fp.getImplementingClassName()) + "\"></td></tr>");
                    rightside.append("<tr><td>Name</td><td>" + Utility.encodeHTML(fedplugin.getDisplayName()) + "</td></tr>");
                    rightside.append("<tr><td>Help</td><td>" + (fedplugin.getHelp()) + "</td></tr>");
                    rightside.append("</table>");

                    rightside.append("<h2>Required Parameters</h2>");
                    rightside.append("<table id=\"reqparamstable\">");
                    rightside.append("<tr><th>Name</th><th>Value</th><th>Is encrypted?</th><th>Encrypt on save</th></tr>");
                    for (int x2 = 0; x2 < fedplugin.getRequiredParameter().size(); x2++) {
                         rightside.append("<tr><td>" + Utility.encodeHTML(fedplugin.getRequiredParameter().get(x2).getName()) + "</td>");
                         rightside.append("<td><input type=text name=\"plugin_" + Utility.encodeHTML(fedplugin.getRequiredParameter().get(x2).getName()) + "\" value=\"" + Utility.encodeHTML(Helper.GetPluginParameterValue(fp.getParameterNameValue(), fedplugin.getRequiredParameter().get(x2).getName())) + "\"></td>");
                         rightside.append("<td><input type=checkbox name=\"fed_parameter_enc_" + Utility.encodeHTML(fedplugin.getRequiredParameter().get(x2).getName()) + "\" " + Utility.encodeHTML(Helper.GetPluginParameterIsEncrypted(fp.getParameterNameValue(), fedplugin.getRequiredParameter().get(x2).getName())) + "></td>");
                         rightside.append("<td><input type=checkbox name=\"p_enc" + Utility.encodeHTML(fedplugin.getRequiredParameter().get(x2).getName()) + "\" " + (fedplugin.getRequiredParameter().get(x2).isEncryptOnSave() ? "checked=checked" : "") + "></td></tr>");
                    }
                    rightside.append("</table>");

                    rightside.append("<h2>Optional Parameters</h2>");
                    rightside.append("<table id=\"optparamstable\">");
                    rightside.append("<tr><th>Name</th><th>Value</th><th>Is encrypted?</th><th>Encrypt on save</th></tr>");
                    for (int x2 = 0; x2 < fedplugin.getOptionalParameter().size(); x2++) {
                         rightside.append("<tr><td>" + Utility.encodeHTML(fedplugin.getOptionalParameter().get(x2).getName()) + "</td>");
                         rightside.append("<td><input type=text name=\"plugin_" + Utility.encodeHTML(fedplugin.getOptionalParameter().get(x2).getName()) + "\" value=\"" + Utility.encodeHTML(Helper.GetPluginParameterValue(fp.getParameterNameValue(), fedplugin.getOptionalParameter().get(x2).getName())) + "\"></td>");
                         rightside.append("<td><input type=checkbox name=\"fed_parameter_enc_" + Utility.encodeHTML(fedplugin.getOptionalParameter().get(x2).getName()) + "\" " + Utility.encodeHTML(Helper.GetPluginParameterIsEncrypted(fp.getParameterNameValue(), fedplugin.getOptionalParameter().get(x2).getName())) + "></td>");
                         rightside.append("<td><input type=checkbox name=\"p_enc_" + Utility.encodeHTML(fedplugin.getOptionalParameter().get(x2).getName()) + "\" " + (fedplugin.getOptionalParameter().get(x2).isEncryptOnSave() ? "checked=checked" : "") + "></td></tr>");
                    }
                    rightside.append("</table>");

                    //saveFedRecord
                    rightside.append("<input type=button name=saveFedRecord value=\"Update\" "
                            + "onclick=\"javascript:postBackReRender('saveFedRecord" + editfederationpol + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">");

               }

            //save modified federation policy
               if (saveFedRecord > -1) {

                    boolean ok = true;
                    //TODO

                    String ft = null;
                    try {
                         ft = (request.getParameter("fedtarget"));
                    } catch (Exception ex) {
                         ok = false;
                         out.write("Unrecognized federation target");
                    }
                    if (Utility.stringIsNullOrEmpty(ft)) {
                         ok = false;
                         out.write("Unrecognized federation target");
                    }
                    if (ok) {
                         //validation
                         for (int i3 = 0; i3 > pol.getFederationPolicyCollection().getFederationPolicy().size(); i3++) {
                              if (i3 != saveFedRecord && ft.equals(pol.getFederationPolicyCollection().getFederationPolicy().get(i3).getImplementingClassName())) {
                                   ok = false;
                                   rightside.append("<h2>You can only have one instance of each federation target</h2>");
                              }
                         }
                    }

                    if (ok) {
                         FederationPolicy fp = Helper.BuildFederation(pol.getFederationPolicyCollection().getFederationPolicy().get(saveFedRecord), pl.GetPCS(application, request, response), c, request);

                         rightside.append("<h2>Federation Record Updated</h2><h2>Don't forget to Save</h2>");
                    }
               }

               //deletedata
               if (!Utility.stringIsNullOrEmpty(request.getParameter("deletedata"))) {
                    PurgePerformanceDataRequestMsg req5 = new PurgePerformanceDataRequestMsg();
                    req5.setClassification(c);

                    req5.getURL().add(request.getParameter("url"));
                    try {
                         dasport.purgePerformanceData(req5);
                         out.write("All records removed successfully<br>");
                    } catch (SecurityException se) {
                         out.write("<font color=#FF0000>Access was denied.</font><Br>");
                         LogHelper.getLog().log(Level.WARN, "Access Denied Delete policy and data " + request.getUserPrincipal().getName(), se);
                    } catch (Exception ex) {
                         out.write("<font color=#FF0000>Error removing the service data: " + ex.getLocalizedMessage() + "</font><bR>");
                         LogHelper.getLog().log(Level.WARN, "Delete policy and data " + request.getUserPrincipal().getName(), ex);
                    }

               }
               //deletepolicyanddata

               if (!Utility.stringIsNullOrEmpty(request.getParameter("deletepolicyanddata"))) {
                    try {
                         DeleteServicePolicyRequestMsg req4 = new DeleteServicePolicyRequestMsg();
                         req4.setClassification(c);
                         req4.setDeletePerformanceData(true);
                         req4.setURL(request.getParameter("url"));
                         pcsport.deleteServicePolicy(req4);
                         // response.sendRedirect("index.jsp");
                         deleted = true;
                         out.write("Policy, Transactions, History, Statistics, and Availability Data were removed successfully. Any users that were subscribed to SLA alerts for this service were notified of the deletion.<br>");
                    } catch (SecurityException se) {
                         out.write("<font color=#FF0000>Access was denied.</font><Br>");
                         LogHelper.getLog().log(Level.WARN, "Access Denied Delete policy and data " + request.getUserPrincipal().getName(), se);
                    } catch (Exception ex) {
                         out.write("<font color=#FF0000>Error removing the service policy and data: " + ex.getLocalizedMessage() + "</font><bR>");
                         LogHelper.getLog().log(Level.WARN, "Delete policy and data " + request.getUserPrincipal().getName(), ex);
                    }
               }

          }

     } else {
          /**
           * ******************************************************************************************************
           */
          /**
           * ******************************************************************************************************
           */
          /**
           * ******************************************************************************************************
           */
          //RENDERING FOR HTTP GET
          /**
           * ******************************************************************************************************
           */
          /**
           * ******************************************************************************************************
           */
          /**
           * ******************************************************************************************************
           */
          rightside.append("This is the FGSMS Policy Viewer and Editor. Use this page to modify the policy and permission set for a"
                  + " specific service that is being monitored. After making changes, don't forget to click 'Save Policy'. Changes generally go into affect within a few minutes. "
                  + "The following provides some descriptional information.<Br><br>"
                  + "<ul>"
                  + "<li>Policy Type - FGSMS uses a variety of different policy types that match the type of thing that's being monitored. It is not possible to change policy types after creation.</li>"
                  + "<li>General - Provides general information about this policy</li>"
                  + "<li>Transactional"
                  + "<ul>"
                  + "<li>Recorded Message Cap - Defines the maximum size of a recorded message (truncates), if and only if the service request/response message is recorded. This is a globally defined item and is listed here for convenience for both users and for agents.</il>"
                  + "<li>Policy Refresh Rate - This is the rate at which cached policies are discarded and is used solely by agents. This parameter is a tuning mechanism to help reduce network utilization. This is a globally defined item and is listed here for convenience for both users and for agents.</il>"
                  + "<li>Record Request Messages - Always record the request message.</li>"
                  + "<li>Record Response Messages - Always record the response message.</li>"
                  + "<li>Record Headers - Always record header information, such as HTTP headers (default is true).</li>"
                  + "<li>Realtime Monitoring - Sets authentication credentials and for Transactional policies, enables or disabled the Status Bueller for this URL.</li>"
                  + "<li>Record Both When Faulting - Always record the request and response message, but only when the service faults.</li>"
                  + "</ul>"
                  + "</li>"
                  + "<li>Machine - Defines monitoring settings for a specific machine</li>"
                  + "<li>Policy - Defines monitoring settings for a specific process or service on a machine</li>"
                  + "<li>Data Retention Time - The period of time in which FGSMS will retain performance records for. Use the format 'xyr ymo zd nhr nmin ns'</li>"
                  + "<li>Federation -Defines if and what data is shared through other components.</li>"
                  + "<li>Service Level Agreements -Defines rules and alerting for a variety of different parameters</li>"
                  //+ "<li>Geo Tagging - Set a lat/lon for this service (decimal only).</li>"
                  + "</ul>"
                  + "Note: if both the Average Response Time and Failure count are selected, MTBF is also published to UDDI.<Br>"
                  + "Save Button: saves any and all changes made to the current policy.<Br>"
                  + "Delete Policy and Performance data - removes the policy, availability and all transactional logs from the database. Note: if the service is still being monitored by an FGSMS Agent, the policy will automatically be regenerated when it is executed again.<Br>"
                  + "Delete Performance Data - removes availability and performance data from the database, the policy remains.<Br>"
                  + "View Permissions - this will display the current permission set for this service. This controls access to information stored within FGSMS about the service, NOT access control to the service itself.<Br><Br>");
     }

%>
<table border="0" width="100%"><tr  width="100%" valign="top"><td valign="top">

            <%                {

                      if (pol == null) {
                           ServicePolicyRequestMsg req = new ServicePolicyRequestMsg();
                           req.setURI(request.getParameter("url"));

                           req.setClassification(c);
                           ServicePolicyResponseMsg res = pcsport.getServicePolicy(req);
                           if (res != null && res.getPolicy() != null) {
                                pol = res.getPolicy();

                                request.getSession().setAttribute(request.getParameter("url"), res.getPolicy());
                           }
                      }
                      if (deleted) {
                           out.write("<a href=\"index.jsp\">Policy Deleted</a>");
                           out.write("<script type=\"text/javascript\">"
                                   + "setTimeout(function(){            loadpage('profile.jsp','tab1'); }, 5000);"
                                   + "           </script>");
                      } else if (saved) {
                           out.write("Policy Saved.");
                           out.write("<script type=\"text/javascript\">LoadPolicy();</script>");
                      } else {
                           if (pol != null) {
                                /**
                                 * ****************************************************************************************************************
                                 ******************************************************************************************************************
                                 ******************************************************************************************************************
                                 * RENDER TAB SECTION
                                 * *****************************************************************************************************************
                                 * *****************************************************************************************************************
                                 * *****************************************************************************************************************
                                 */
                                out.write("<h3>URL: " + Utility.encodeHTML(pol.getURL()) + "</h3>");
                                /*    if (pol.getPolicyType() == PolicyType.TRANSACTIONAL) {
                                 out.write("<a href=\"performanceViewer.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View Performance</a> | "
                                 + " <a href=\"serviceprofile.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View Profile</a> | "
                                 + " <a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View Transaction Log</a> | "
                                 + "<a href=\"availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">Availability</a> | "
                                 + "<a href=\"slarecords.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">SLA Violations</a><Br>");
                                 }
                                 if (pol.getPolicyType() == PolicyType.PROCESS) {
                                 out.write("<a href=\"processPerformance.jsp?uri=" + URLEncoder.encode(pol.getURL()) + "\">View Performance</a> | "
                                 + "<a href=\"availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">Availability</a> | "
                                 + "<a href=\"slarecords.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">SLA Violations</a><Br>");
                                 }
                                 if (pol.getPolicyType() == PolicyType.MACHINE) {
                                 out.write("<a href=\"machinePerformance.jsp?uri=" + URLEncoder.encode(pol.getURL()) + "\">View Performance</a> | "
                                 + "<a href=\"serverprocess.jsp?server=" + URLEncoder.encode(pol.getMachineName()) + "\">View Machine Information</a> | "
                                 + "<a href=\"availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">Availability</a> | "
                                 + "<a href=\"slarecords.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">SLA Violations</a><Br>");
                                 }
                                 if (pol.getPolicyType() == PolicyType.STATISTICAL) {
                                 out.write("<a href=\"messageBrokerDetail.jsp?uri=" + URLEncoder.encode(pol.getURL()) + "\">View Queues/Topics</a> | "
                                 + "<a href=\"availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">Availability</a> | "
                                 + "<a href=\"slarecords.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">SLA Violations</a><Br>");
                                 }
                                 if (pol.getPolicyType() == PolicyType.STATUS) {
                                 out.write("<a href=\"availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">Availability</a><br>");

                                 }*/
                                out.write("<div id=\"accord\"><ul>");

                                out.write("<li><a href=\"#accord-1\">General</a></li>");
                                if (pol.getPolicyType() == PolicyType.TRANSACTIONAL) {
                                     out.write("<li><a href=\"#accord-2\">Transactional</a></li>");
                                     out.write("<li><a href=\"#accord-2a\">Dependencies</a></li>");
                                }
                                if (pol.getPolicyType() == PolicyType.MACHINE) {
                                     out.write("<li><a href=\"#accord-3\">Machine</a></li>");
                                }
                                if (pol.getPolicyType() == PolicyType.PROCESS) {
                                     out.write("<li><a href=\"#accord-4\">Process</a></li>");
                                }
                                out.write("<li><a href=\"#accord-5\">Federation</a></li>");
                                out.write("<li><a href=\"#accord-6\">Service Level Agreements</a></li>");
                                if (pol.getPolicyType() != PolicyType.PROCESS && pol.getPolicyType() != PolicyType.MACHINE) {
                                     out.write("<li><a href=\"#accord-7\">Status Monitoring</a></li>");
                                }
                                out.write("<li><a href=\"#accord-8\">Administration</a></li>");
                                out.write("</ul>");

                                out.write("<div id=accord-1>");
                                out.write("Policy Type = " + pol.getPolicyType().value());
                                out.write("<table border=1><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");
                                if (Utility.stringIsNullOrEmpty(pol.getDisplayName())) {
                                     out.write("<tr><td>Display Name</td><td>Not defined.</td><td><input  type=text name=\"displayName\"></td></tr>");
                                } else {
                                     out.write("<tr><td>Display Name</td><td>" + Utility.encodeHTML(pol.getDisplayName()) + "</td><td><input  type=text name=\"displayName\" value=\"" + Utility.encodeHTML(pol.getDisplayName()) + "\"></td></tr>");
                                }
                                out.write("<tr><td>Hosted on</td><td>" + Utility.encodeHTML(pol.getMachineName()) + "</td><td><input  type=text name=\"servername\" value=\"" + Utility.encodeHTML(pol.getMachineName()) + "\"></td></tr>");
                                out.write("<tr><td>Domain Memebership</td><td>" + Utility.encodeHTML(pol.getDomainName()) + "</td><td><input  type=text name=\"domainname\" value=\"" + Utility.encodeHTML(pol.getDomainName()) + "\"></td></tr>");
                                out.write("<tr><td>Bucket Container</td><td>" + Utility.encodeHTML(pol.getBucketCategory()) + "</td><td><input  type=text name=\"bucket\" value=\"" + Utility.encodeHTML(pol.getBucketCategory()) + "\"></td></tr>");
                                out.write("<tr><td>Parent Object</td><td>" + Utility.encodeHTML(pol.getParentObject()) + "</td><td><input  type=text name=\"parentobject\" value=\"" + Utility.encodeHTML(pol.getParentObject()) + "\"></td></tr>");
                                out.write("<tr><td>Description</td><td>" + Utility.encodeHTML(pol.getDescription()) + "</td><td><input  type=text name=\"description\" value=\"" + Utility.encodeHTML(pol.getDescription()) + "\"></td></tr>");
                                out.write("<tr><td>External URL</td><td>" + Utility.encodeHTML(pol.getExternalURL()) + "</td><td><input  type=text name=\"externalurl\" value=\"" + Utility.encodeHTML(pol.getExternalURL()) + "\"></td></tr>");
                                out.write("<tr><td>Point of Contact</td><td>" + Utility.encodeHTML(pol.getPOC()) + "</td><td><input  type=text name=\"poc\" value=\"" + Utility.encodeHTML(pol.getPOC()) + "\"></td></tr>");
                                out.write("<tr><td>Geo Tagging (decimal lat/lon)</td><td>");

                                if (pol.getLocation() != null && pol.getLocation() != null) {

                                     out.write(pol.getLocation().getLatitude() + "," + pol.getLocation().getLongitude());
                                } else {
                                     out.write("Not defined.");
                                }
                                out.write("</td><td><input type=text name=locationlat ");
                                if (pol.getLocation() != null && pol.getLocation() != null) {
                                     out.write(" value=\"" + pol.getLocation().getLatitude() + "\"");
                                }
                                out.write("><br><input type=text name=locationlong ");
                                if (pol.getLocation() != null && pol.getLocation() != null) {
                                     out.write(" value=\"" + pol.getLocation().getLongitude() + "\"");
                                }
                                out.write("></td></tr>");
                                out.write("<tr><td>Data Retention Time (<a href=\"javascript:ShowDateHelp();\">help</a>)</td><td>" + Helper.DurationToString(pol.getDataTTL())
                                        + "</td><td><input type=text name=datattl value=\"" + Helper.DurationToString(pol.getDataTTL()) + "\"></td></tr>");
                                out.write("</table>"
                                        + "</div>");

                                if (pol.getPolicyType() == PolicyType.TRANSACTIONAL) {
                                     out.write("<div id=accord-2>");

                                     TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
                                     out.write("<table border=1><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");

                                     out.write("<tr><th colspan=3>Data Collection</th></tr>");
                                     out.write("<tr><td>Recorded message cap</td><td> " + tp.getRecordedMessageCap()
                                             + " bytes</td><td><input  type=text name=\"messagecap\" value=\"" + tp.getRecordedMessageCap() + "\"></td></tr>");
                                     out.write("<tr><td>Policy Refresh Rate</td><td>" + Helper.DurationToString(pol.getPolicyRefreshRate())
                                             + "</td><td><input readonly type=text name=refreshrate value=\"" + Helper.DurationToString(pol.getPolicyRefreshRate()) + "\"></td></tr>");
                                     out.write("<tr><td colspan=3>WARNING: when selecting any of these values, there is a possiblity of recording PII and security credentials. Only users with Administer and Audit rights can access these logs. For security compliance purposes, this instance of FGSMS must use a FIPS 140-2 certified library. </td></tr>");
                                     out.write("<tr><td>Record Request Messages</td><td>" + tp.isRecordRequestMessage()
                                             + "</td><td><input type=checkbox name=recordrequest value=recordrequest ");
                                     if (tp.isRecordRequestMessage()) {
                                          out.write("checked");
                                     }
                                     out.write("></td></tr>");
                                     out.write("<tr><td>Record Response Messages</td><td>" + tp.isRecordResponseMessage()
                                             + "</td><td><input type=checkbox name=recordresponse value=recordresponse ");
                                     if (tp.isRecordResponseMessage()) {
                                          out.write("checked");
                                     }
                                     out.write("></td></tr>");
                                     out.write("<tr><td>Record Both When Faulting</td><td>" + tp.isRecordFaultsOnly()
                                             + "</td><td><input type=checkbox name=recordfault value=recordfault ");
                                     if (tp.isRecordFaultsOnly()) {
                                          out.write("checked");
                                     }
                                     out.write("></td></tr>");

                                     out.write("<tr><td>Record Headers</td><td>" + tp.isRecordHeaders()
                                             + "</td><td><input type=checkbox name=recordheaders value=recordheaders ");
                                     if (tp.isRecordHeaders()) {
                                          out.write("checked");
                                     }
                                     out.write("></td></tr>");

                                     out.write("<tr><th colspan=3 >Consumer Identification Policies <input type=button name=addUserIDMethod value=\"Add\" onclick=\"javascript:postBackReRender('addUserIDMethod','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\"></th></tr>");
                                     if (tp.getUserIdentification() != null
                                             && tp.getUserIdentification() != null
                                             && tp.getUserIdentification().getUserIdentity() != null
                                             && !tp.getUserIdentification().getUserIdentity().isEmpty()) {

                                          for (int i = 0; i < tp.getUserIdentification().getUserIdentity().size(); i++) {
                                               out.write("<tr><td>User Identification Method</td><td>#" + (i + 1) + "</td><td><input type=button name=RemoveUserID" + i + " value=Remove onclick=\"javascript:postBackReRender('RemoveUserID" + i + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\"></td></tr>");
                                               if (tp.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential() != null && tp.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential()) {
                                                    out.write("<tr><td colspan=3>Http Credential</td></tr>");
                                               }
                                               if (tp.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader() != null && tp.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader()) {
                                                    out.write("<tr><td colspan=2>Http Header</td><td>");
                                                    if (tp.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName() != null
                                                            && !Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName())) {
                                                         out.write(tp.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName());
                                                         out.write("</td></tr>");
                                                    }
                                               }

                                               if (tp.getUserIdentification().getUserIdentity().get(i).getNamespaces() != null
                                                       && tp.getUserIdentification().getUserIdentity().get(i).getNamespaces() != null
                                                       && tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies() != null
                                                       && !tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().isEmpty()) {
                                                    out.write("<tr><td>Xpath Namespaces</td><td colspan=2>");
                                                    for (int k = 0; k < tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().size(); k++) {
                                                         if (!Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getPrefix())) {
                                                              out.write(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getPrefix());
                                                         } else {
                                                              out.write("null prefix");
                                                         }
                                                         out.write(" = ");
                                                         if (!Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getNamespace())) {
                                                              out.write(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getNamespace() + "<br>");
                                                         } else {
                                                              out.write("null namespace<Br>");
                                                         }
                                                    }
                                                    out.write("</td></tr>");

                                               }
                                               if (tp.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                                                       && tp.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                                                       && tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType() != null
                                                       && !tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().isEmpty()) {
                                                    out.write("<tr><td>Xpath Expressions</td><td colspan=2>");
                                                    for (int k = 0; k < tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().size(); k++) {
                                                         if (!Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(k).getXPath())) {
                                                              out.write(tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(k).getXPath());
                                                         } else {
                                                              out.write("null xpath expression");
                                                         }
                                                         out.write("<Br>Does this Xpath result in an X509 Certificate? ");
                                                         if (tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(k).isIsCertificate()) {
                                                              out.write("true");
                                                         } else {
                                                              out.write("false");
                                                         }
                                                         out.write("</td></tr>");
                                                    }

                                               }

                                          }

                                     } else {
                                          out.write("<tr><td colspan=3>No methods defined.</td></tr>");
                                     }

                                     out.write("</table>");

                                     out.write("</div>");

                                }

                                if (pol.getPolicyType() == PolicyType.TRANSACTIONAL) {
                                     out.write("<div id=accord-2a>");
                                     out.write("<div id=\"dependencies\"></div>");
            %>


            <%
                                          out.write(" </div>");
                                     }

                                     ManageHelper mhelper = new ManageHelper();
                                     if (pol.getPolicyType() == PolicyType.MACHINE) {

                                          out.write("<div id=accord-3>");
                                          //machine
                                          if (pol instanceof MachinePolicy) {
                                               MachinePolicy mp = (MachinePolicy) pol;
                                               out.write("<table border=1><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");
                                               out.write("<tr>" + "<td>Monitor Disk Usage (Read/Writes/second) (space delimited)</td>" + "<td>" + Utility.encodeHTML(Utility.listStringtoString(mp.getRecordDiskUsagePartitionNames())) + "</td>"
                                                       + "<td>");
                                               //<input type=text name=recorddiskusage  value=\"");
                                               out.write(mhelper.GetPartitionNamesAsHtmlListboxForThroughput(mp.getURL(), mp.getDomainName(), mp.getMachineName(), pcsport, c, mp.getRecordDiskUsagePartitionNames()));

                                        //    out.write(Utility.encodeHTML(Utility.ListStringtoString(mp.getRecordDiskUsagePartitionNames())));
                                               //                                 out.write("\" >"
                                               out.write("</td></tr>");
//recorddiskusage
//recordnetworkusage
                                               out.write("<tr>" + "<td>Monitor CPU Usage</td>" + "<td>" + mp.isRecordCPUusage() + "</td>" + "<td><input type=checkbox name=recordcpuusage value=recordcpuusage ");
                                               if (mp.isRecordCPUusage()) {
                                                    out.write("checked");
                                               }
                                               out.write(" ></td></tr>");

                                               out.write("<tr>" + "<td>Monitor Memory Usage</td>" + "<td>" + mp.isRecordMemoryUsage() + "</td>" + "<td><input type=checkbox name=recordcmemusage value=recordcmemusage ");
                                               if (mp.isRecordMemoryUsage()) {
                                                    out.write("checked");
                                               }
                                               out.write(" ></td></tr>");

                                               out.write("<tr>" + "<td>Monitor Network Usage (RX/TX KB/second) (<a href=\"javascript:ShowHelp('networkusage');\">help</a>)</td>"
                                                       + "<td>" + Utility.encodeHTML(Utility.listStringtoString(mp.getRecordNetworkUsage())) + "</td>" + "<td>");
                                               out.write(mhelper.GetNICNamesAsHtmlListbox(mp.getURL(), mp.getDomainName(), mp.getMachineName(), pcsport, c, mp.getRecordNetworkUsage()));
                                        //      + "<input type=text name=recordnetworkusage value=\"");
                                               //out.write(Utility.encodeHTML(Utility.ListStringtoString(mp.getRecordNetworkUsage())));
//                                    out.write("\" >"
                                               out.write("</td></tr>");

                                               out.write("<tr>" + "<td>Monitor Free Disk Space</td>" + "<td>");
                                               if (mp.getRecordDiskSpace().isEmpty()) {
                                                    out.write("none");
                                               } else {
                                                    for (int i = 0; i < mp.getRecordDiskSpace().size(); i++) {
                                                         out.write(Utility.encodeHTML(mp.getRecordDiskSpace().get(i)) + "<br>");
                                                    }
                                               }
                                               out.write("</td>" + "<td>");
                                               //<input type=textbox name=recorddrivespace value=\"" + Utility.encodeHTML(Utility.ListStringtoString(mp.getRecordDiskSpace())) + "\" >
                                               out.write(mhelper.GetPartitionNamesAsHtmlListboxForFreeSpace(mp.getURL(), mp.getDomainName(), mp.getMachineName(), pcsport, c, mp.getRecordDiskUsagePartitionNames()));
                                               out.write("</td></tr>");

                                               out.write("</table>");
                                          } else {
                                               out.write("Not applicable");
                                          }
                                          out.write("</div>");
                                     }

                                     if (pol.getPolicyType() == PolicyType.PROCESS) {
                                          out.write("<div id=accord-4>");
                                          //process
                                          if (pol instanceof ProcessPolicy) {
                                               ProcessPolicy mp = (ProcessPolicy) pol;
                                               out.write("<table border=1><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");
                                               out.write("<tr>" + "<td>Monitor Open File Count</td>" + "<td>" + mp.isRecordOpenFileHandles() + "</td>" + "<td><input type=checkbox name=recordopenfiles value=recordopenfiles ");
                                               if (mp.isRecordOpenFileHandles()) {
                                                    out.write("checked");
                                               }
                                               out.write(" ></td></tr>");

                                               out.write("<tr>" + "<td>Monitor CPU Usage</td>" + "<td>" + mp.isRecordCPUusage() + "</td>" + "<td><input type=checkbox name=recordcpuusage value=recordcpuusage ");
                                               if (mp.isRecordCPUusage()) {
                                                    out.write("checked");
                                               }
                                               out.write(" ></td></tr>");

                                               out.write("<tr>" + "<td>Monitor Memory Usage</td>" + "<td>" + mp.isRecordMemoryUsage() + "</td>" + "<td><input type=checkbox name=recordcmemusage value=recordcmemusage ");
                                               if (mp.isRecordMemoryUsage()) {
                                                    out.write("checked");
                                               }
                                               out.write(" ></td></tr>");

                                               //out.writeclass=\"table table-hover\"write("<tr><td>AKA (<a href=\"javascript:ShowHelp('aka')\">help</a>)</td><td>" + Utility.encodeHTML(mp.getAlsoKnownAs()) + "</td><td><input type=text name=processaka value=\"" + Utility.encodeHTML(mp.getAlsoKnownAs()) + "\"></td></tr>");
                                        //out.write(" ></td></tr>");

                                               out.write("</table>");
                                          } else if (pol instanceof MachinePolicy) {
                                               out.write("<a id=viewproc href=\"serverprocess.jsp?server=" + URLEncoder.encode(pol.getMachineName()) + "\">View Processes on this machine</a>");
                                          } else {
                                               out.write("Not applicable");
                                          }
                                          out.write("</div>");
                                     }

                                     /*
                                      * *************************************************************************************************************
                                      */
                                     /**
                                      * **********************************DATA
                                      * FEDERATION************************************************************
                                      * RENDER READ
                                      */
                                     /**
                                      * *************************************************************************************************************
                                      */
                                     /**
                                      * *************************************************************************************************************
                                      */
                                     out.write("<div id=accord-5>");
                                     out.write("Data Federation <input type=button name=addFedMethod value=\"Add\" onclick=\"javascript:postBackReRender('addFedMethod','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">");
                                     if (pol.getFederationPolicyCollection() != null) {
                                          for (int i = 0; i < pol.getFederationPolicyCollection().getFederationPolicy().size(); i++) {
                                               out.write(Helper.PluginToReadonlyHtmlString(pol.getFederationPolicyCollection().getFederationPolicy().get(i), i, request));

                                          }
                                     }
                                     out.write("</div>");

                                     /**
                                      * ********************************
                                      * SLAs
                                      * **************************************
                                      */
                                     out.write("<div id=accord-6>");
                                     out.write("<table border=1>");
                                     //             + "<tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");

                                     out.write("<tr><th colspan=2 >Service Level Aggreements <input type=button name=addSLAMethod value=\"Add\" onclick=\"javascript:postBackReRender('addSLAMethod','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\"></th></tr>");
                                     if (pol.getServiceLevelAggrements() != null
                                             && pol.getServiceLevelAggrements() != null
                                             && pol.getServiceLevelAggrements().getSLA() != null
                                             && !pol.getServiceLevelAggrements().getSLA().isEmpty()) {
                                          for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
                                               out.write("<tr><td colspan=3>SLA #" + (i + 1) + " ID = " + pol.getServiceLevelAggrements().getSLA().get(i).getGuid()
                                                       + "<br>"
                                                       + "<input type=button name=RemoveSLA" + i + " value=\"Remove this SLA\" onclick=\"javascript:postBackReRender('RemoveSLA" + i + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">"
                                                       + "<input type=button name=EditSLA" + i + " value=\"Edit this SLA\" onclick=\"javascript:postBackReRender('EditSLA" + i + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">"
                                                       + "</td></tr>");
                                               out.write("<tr><td>Triggering rule</td><td>");
                                               String sla = Helper.BuildSLARuleData(pol.getServiceLevelAggrements().getSLA().get(i).getRule(),plugins);
                                               out.write(sla);
                                               out.write("</td></tr>");

                                               out.write("<tr><td colspan=2>Actions to Perform</td><tr>");
                                               for (int i2 = 0; i2 < pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().size(); i2++) {
                                                    out.write("<tr><td>" + Helper.ToFriendlyName(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2), pcsport, c)
                                                            + "</td><td>" + Utility.encodeHTML(Helper.BuildSLASingleAction(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2), pcsport, c))
                                                            + "</td></tr>");

                                               }
                                               out.write("<tr><td> </td><td> </td></tr>");
                                          }

                                     } else {
                                          out.write("<tr><td colspan=2>No SLAs defined.</td></tr>");
                                     }
                                     //end sla tab
                                     out.write("</table></div>");

                                     /**
                                      * ******************************
                                      * status monitoring
                                      * *******************************
                                      */
                                     if (pol.getPolicyType() != PolicyType.PROCESS && pol.getPolicyType() != PolicyType.MACHINE) {
                                          out.write("<div id=accord-7>");
                                          out.write("<table border=1>");

                                          if (pol instanceof TransactionalWebServicePolicy) {
                                               TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
                                               out.write("<tr>"
                                                       + "<td>Realtime Monitoring via WSDL pull</td>"
                                                       + "<td>" + tp.isBuellerEnabled() + "</td>"
                                                       + "<td><input type=checkbox name=buellerenabled value=buellerenabled ");
                                               if (tp.isBuellerEnabled()) {
                                                    out.write("checked");
                                               }
                                               out.write("></td></tr>");
                                               out.write("<tr>"
                                                       + "<td>Realtime Monitoring via Health Status API</td>"
                                                       + "<td>" + tp.isHealthStatusEnabled() + "</td>"
                                                       + "<td><input type=checkbox name=healthstatusenabled value=healthstatusenabled ");
                                               if (tp.isHealthStatusEnabled()) {
                                                    out.write("checked");
                                               }
                                               out.write("></td></tr>");
                                          }
                                          if (!(pol instanceof ProcessPolicy) && !(pol instanceof MachinePolicy)) {

                                               out.write("<tr>"
                                                       + "<td align=left><a id=setcred class=\"btn btn-primary\" href=\"javascript:loadpage('profile/setcredentials.jsp?uri=" + URLEncoder.encode(request.getParameter("url")) + "','mainpane');\">Set Status Credentials</a></td>"
                                                       + "<td align=left></td>"
                                                       + "<td align=left><a id=clearcred class=\"btn btn-primary\" href=\"javascript:loadpage('profile/removecredentials.jsp?uri=" + URLEncoder.encode(request.getParameter("url")) + "','mainpane');\">Clear Credentials</a></td>"
                                                       + "</tr>");
                                          }
                                          out.write("</table></div>");
                                     }   //status monitoring

                                     /**
                                      * ************************************
                                      * ADMINISTRATION
                                      * ************************************
                                      */
                                     out.write("<div id=accord-8>");
                                     out.write("<table border=1>");

                                     out.write("<tr>"
                                             + "<td align=left><a  class=\"btn btn-primary\" href=\"javascript:postBackReRender('savepolicy','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + " ','tab1');\" >Save Policy</a></td>"
                                             //   + "<td align=right><a id=viewperm href=\"permission.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "\">View Permissions</a></td>"
                                             + "<td></td>"
                                             + "</tr>"
                                             + "<tr>"
                                             + "<td align=left><a class=\"btn btn-primary\" href=\"javascript:postBackReRender('deletepolicyanddata','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\" >Delete Policy and Data</a></td>"
                                             + "<td align=right><a class=\"btn btn-primary\" href=\"javascript:postBackReRender('deletedata','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\" >Delete Data Only</a></td></tr>"
                                             + "<tr><td>");
                                     out.write("<a class=\"btn btn-primary\" id=exportlink href=\"javascript:loadpage('profile/policyExport.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','mainpane');\" >Export this policy to XML</a>");
                                     out.write("</td><td>");
                                     out.write("<a class=\"btn btn-primary\" id=importlink href=\"javascript:loadpage('profile/policyImport.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','mainpane');\" >Import this policy from XML</a></td>");
                                     out.write("</tr>");

                                     out.write("</table></div>");
                                     out.write("<a  class=\"btn btn-primary\" href=\"javascript:postBackReRender('savepolicy','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + " ','tab1');\" >Save Policy</a>");
                                     //out.write("<input type=\"button\" name=\"savepolicy\"  value=\"Save Policy\" onclick=\"javascript:postBackReRender('savepolicy','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\"  />");
                                     request.getSession().setAttribute("workingpolicy" + pol.getURL(), pol);

                                } else {
                                     out.write("The selected URL is not valid or does not have a policy defined.<br>");
                                }
                           }
                      }
                 } catch (Exception ex) {
                      out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font>");
                      LogHelper.getLog().log(Level.ERROR, "Error from manage.jsp", ex);
		      ex.printStackTrace();

                 }

            %>
        </td><td valign="top"><%= rightside.toString()%></td></tr></table>

<Br><Br>

<script>


    $(function() {
        $("#accord").resizable({maxWidth: <%=Helper.GetHalfScreenWidth(request.getCookies())%>});
        $("input:button").button();
        $("a").click(function() {
            return true;
        });
        $("#parameter").resizable();
        $("#parameter2").resizable();
        $("#xpathprefixes").resizable();
        $("#xpathexpression").resizable();
        $("#feduddikey").resizable();


        $("#viewperm").button();
        $("#exportlink").button();
        $("#importlink").button();
        $("#setcred").button();
        $("#clearcred").button();
        $("#viewproc").button();



    });
</script>


