<%--
    Document   : policyEditor
    Created on : Oct 21, 2015, 8:30:39 PM
    Author     : alex
--%>

<%@page import="javax.xml.bind.JAXB"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.*"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="javax.xml.bind.JAXBElement"%>
<%@page import="java.math.BigInteger"%>
<%@page import="java.util.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>

<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@include file="../csrf.jsp" %>

<%    boolean DEBUG = false;
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    ServicePolicy pol = null;
    PCS pcsport = null;
    List<Plugin> rule_plugins = new ArrayList<Plugin>();
    List<Plugin> actions = new ArrayList<Plugin>();
    try {

        pol = (ServicePolicy) request.getSession().getAttribute(request.getParameter("url"));
    } catch (Exception ex) {
    }
//render the read/edit views
    boolean saved = false;
    boolean deleted = false;

    try {

        pcsport = pl.GetPCS(application, request, response);

        // DataAccessService dasport = pl.GetDAS(application, request, response);
        //DatatypeFactory df = DatatypeFactory.newInstance();
        if (Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
            //System.err.println("no url, aborting request");
            response.sendRedirect("../index.jsp");
            return;
        }
        ServicePolicyRequestMsg req = new ServicePolicyRequestMsg();
        req.setURI(request.getParameter("url"));

        req.setClassification(c);
        ServicePolicyResponseMsg res = pcsport.getServicePolicy(req);
        if (res != null && res.getPolicy() != null) {
            pol = res.getPolicy();

            request.getSession().setAttribute(request.getParameter("url"), res.getPolicy());
        }
        rule_plugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "SLA_RULE", pol.getPolicyType());
        actions = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "SLA_ACTION", pol.getPolicyType());
    } catch (Exception ex) {
        LogHelper.getLog().log(Level.DEBUG, "error trapped postback to policyEditor.jsp", ex);
    }

    if (request.getMethod().equalsIgnoreCase("POST")) {
        try {

            if (pol == null) {
                LogHelper.getLog().warn("no policy, aborting request");
                //response.setStatus(400);
                response.sendRedirect("../index.jsp");
                return;
            }

            pol = Helper.buildFromHttpPost(request, pol);

            //federation policies
            //store the changes
            request.getSession().setAttribute(request.getParameter("url"), pol);

            if (!Utility.stringIsNullOrEmpty(request.getParameter("save_policy"))) {

                //todo validat that the policy is correct
                try {
                    SetServicePolicyRequestMsg req3 = new SetServicePolicyRequestMsg();

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
                    //JAXB.marshal(req3, System.out);
                    SetServicePolicyResponseMsg res3 = pcsport.setServicePolicy(req3);
                    //TODO confirm this is still used
                    request.getSession().removeAttribute(request.getParameter("url"));
                    out.write("<font color=#FF0000>Policy Saved.</font><br>");
                    saved = true;
                } catch (SecurityException se) {
                    LogHelper.getLog().warn("access denied for " + request.getParameter("url"));
                    out.write("<font color=#FF0000>Access was denied.</font><Br>");
                } catch (Exception ex) {
                    LogHelper.getLog().warn("error saving policy" + request.getParameter("url"),ex);
                    out.write("<font color=#FF0000>Error saving policy: " + ex.getLocalizedMessage() + "</font><bR>");
                }
            }

        } catch (Exception ex) {
            LogHelper.getLog().warn("unexpected error",ex);
        }

    } else {

%>

<script type="text/javascript">

    function togglePolicyEditorTab(tab) {
        $('#div_basics').hide();
        $('#div_dependencies').hide();
        $('#div_federation').hide();
        $('#div_machine').hide();
        $('#div_process').hide();
        $('#div_sla').hide();
        $('#div_status').hide();
        $('#div_transactional').hide();
        $('#div_admin').hide();

        $('#header_basics').removeClass("active");
        $('#header_dependencies').removeClass("active");
        $('#header_federation').removeClass("active");
        $('#header_machine').removeClass("active");
        $('#header_process').removeClass("active");
        $('#header_sla').removeClass("active");
        $('#header_status').removeClass("active");
        $('#header_transactional').removeClass("active");
        $('#header_admin').removeClass("active");

        $('#div_' + tab).show();
        $('#header_' + tab).addClass("active");
    }
    togglePolicyEditorTab('basics');

</script>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <div class="well sidebar-nav">
                <ul class="nav nav-list">
                    <li  id="header_basics"><a href="javascript:togglePolicyEditorTab('basics');">Basics</a></li>
                        <%                                  if (pol.getPolicyType() == PolicyType.TRANSACTIONAL) {
                        %>
                    <li id="header_transactional"><a href="javascript:togglePolicyEditorTab('transactional')">Transactional</a></li>
                    <li id="header_dependencies"><a href="javascript:togglePolicyEditorTab('dependencies');">Dependencies</a></li>
                        <%
                            }
                            if (pol.getPolicyType() == PolicyType.MACHINE) {
                        %>
                    <li id="header_machine"><a href="javascript:togglePolicyEditorTab('machine');">Machine</a></li>
                        <%
                            }
                            if (pol.getPolicyType() == PolicyType.PROCESS) {
                        %>
                    <li id="header_process"><a href="javascript:togglePolicyEditorTab('process');">Process</a></li>
                        <%
                            }
                        %>
                    <li id="header_federation"><a href="javascript:togglePolicyEditorTab('federation');">Federation</a></li>
                    <li id="header_sla"><a href="javascript:togglePolicyEditorTab('sla');">Service Level Agreements</a></li>
                        <%
                            if (pol.getPolicyType() != PolicyType.PROCESS && pol.getPolicyType() != PolicyType.MACHINE) {
                        %>
                    <li id="header_status"><a href="javascript:togglePolicyEditorTab('status');">Status Monitoring</a></li>
                        <%
                            }

                        %>
                    <li id="header_admin"><a href="javascript:togglePolicyEditorTab('admin');">Administration</a></li>
                </ul>

            </div><!--/.well -->
            <%                            out.write("<a  class=\"btn\" id=\"btn_save\" href=\"javascript:postBack('save_policy','profile/policyEditor.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + " ');\" >Save Policy</a>");
            %>
        </div><!--/span-->
        <div class="span8">

            <div class="row-fluid">
                <div id="div_basics">

                    <%
                        out.write("Policy Type = " + pol.getPolicyType().value());
                        out.write("<table class=\"table table-hover\"><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");
                        if (Utility.stringIsNullOrEmpty(pol.getDisplayName())) {
                            out.write("<tr><td>Display Name</td><td>Not defined.</td><td><input class=\"baseitem\"  type=text name=\"displayName\"></td></tr>");
                        } else {
                            out.write("<tr><td>Display Name</td><td>" + Utility.encodeHTML(pol.getDisplayName()) + "</td><td><input class=\"baseitem\"  type=text name=\"displayName\" value=\"" + Utility.encodeHTML(pol.getDisplayName()) + "\"></td></tr>");
                        }
                        out.write("<tr><td>Hosted on</td><td>" + Utility.encodeHTML(pol.getMachineName()) + "</td><td><input class=\"baseitem\"  type=text name=\"servername\" value=\"" + Utility.encodeHTML(pol.getMachineName()) + "\"></td></tr>");
                        out.write("<tr><td>Domain Memebership</td><td>" + Utility.encodeHTML(pol.getDomainName()) + "</td><td><input class=\"baseitem\"  type=text name=\"domainname\" value=\"" + Utility.encodeHTML(pol.getDomainName()) + "\"></td></tr>");
                        out.write("<tr><td>Bucket Container</td><td>" + Utility.encodeHTML(pol.getBucketCategory()) + "</td><td><input class=\"baseitem\"  type=text name=\"bucket\" value=\"" + Utility.encodeHTML(pol.getBucketCategory()) + "\"></td></tr>");
                        out.write("<tr><td>Parent Object</td><td>" + Utility.encodeHTML(pol.getParentObject()) + "</td><td><input class=\"baseitem\"  type=text name=\"parentobject\" value=\"" + Utility.encodeHTML(pol.getParentObject()) + "\"></td></tr>");
                        out.write("<tr><td>Description</td><td>" + Utility.encodeHTML(pol.getDescription()) + "</td><td><input class=\"baseitem\"  type=text name=\"description\" value=\"" + Utility.encodeHTML(pol.getDescription()) + "\"></td></tr>");
                        out.write("<tr><td>External URL</td><td>" + Utility.encodeHTML(pol.getExternalURL()) + "</td><td><input class=\"baseitem\"  type=text name=\"externalurl\" value=\"" + Utility.encodeHTML(pol.getExternalURL()) + "\"></td></tr>");
                        out.write("<tr><td>Point of Contact</td><td>" + Utility.encodeHTML(pol.getPOC()) + "</td><td><input class=\"baseitem\"  type=text name=\"poc\" value=\"" + Utility.encodeHTML(pol.getPOC()) + "\"></td></tr>");
                        out.write("<tr><td>Geo Tagging (decimal lat/lon)</td><td>");

                        if (pol.getLocation() != null && pol.getLocation() != null) {

                            out.write(pol.getLocation().getLatitude() + "," + pol.getLocation().getLongitude());
                        } else {
                            out.write("Not defined.");
                        }
                        out.write("</td><td><input class=\"baseitem\" type=text name=locationlat ");
                        if (pol.getLocation() != null && pol.getLocation() != null) {
                            out.write(" value=\"" + pol.getLocation().getLatitude() + "\"");
                        }
                        out.write("><br><input class=\"baseitem\" type=text name=locationlong ");
                        if (pol.getLocation() != null && pol.getLocation() != null) {
                            out.write(" value=\"" + pol.getLocation().getLongitude() + "\"");
                        }
                        out.write("></td></tr>");
                        out.write("<tr><td>Data Retention Time (<a href=\"javascript:ShowDateHelp();\">help</a>)</td><td>" + Helper.DurationToString(pol.getDataTTL())
                                + "</td><td><input class=\"baseitem\" type=text name=datattl value=\"" + Helper.DurationToString(pol.getDataTTL()) + "\"></td></tr>");
                        out.write("</table>");
                    %>
                </div>


                <div  id="div_transactional">
                    <table  class="table table-hover"><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>
                                <%
                                    if (pol instanceof TransactionalWebServicePolicy) {
                                        TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
                                        out.write("");

                                        out.write("<tr><th colspan=3>Data Collection</th></tr>");
                                        out.write("<tr><td>Recorded message cap</td><td> " + tp.getRecordedMessageCap()
                                                + " bytes</td><td><input class=\"baseitem\"  type=text name=\"messagecap\" value=\"" + tp.getRecordedMessageCap() + "\"></td></tr>");
                                        out.write("<tr><td>Policy Refresh Rate</td><td>" + Helper.DurationToString(pol.getPolicyRefreshRate())
                                                + "</td><td><input readonly type=text name=refreshrate value=\"" + Helper.DurationToString(pol.getPolicyRefreshRate()) + "\"></td></tr>");
                                        out.write("<tr><td colspan=3>WARNING: when selecting any of these values, there is a possiblity of recording PII and security credentials. Only users with Administer and Audit rights can access these logs. For security compliance purposes, this instance of FGSMS must use a FIPS 140-2 certified library. </td></tr>");
                                        out.write("<tr><td>Record Request Messages</td><td>" + tp.isRecordRequestMessage()
                                                + "</td><td><input class=\"baseitem\" type=checkbox name=recordrequest value=recordrequest ");
                                        if (tp.isRecordRequestMessage()) {
                                            out.write("checked");
                                        }
                                        out.write("></td></tr>");
                                        out.write("<tr><td>Record Response Messages</td><td>" + tp.isRecordResponseMessage()
                                                + "</td><td><input class=\"baseitem\" type=checkbox name=recordresponse value=recordresponse ");
                                        if (tp.isRecordResponseMessage()) {
                                            out.write("checked");
                                        }
                                        out.write("></td></tr>");
                                        out.write("<tr><td>Record Both When Faulting</td><td>" + tp.isRecordFaultsOnly()
                                                + "</td><td><input class=\"baseitem\" type=checkbox name=recordfault value=recordfault ");
                                        if (tp.isRecordFaultsOnly()) {
                                            out.write("checked");
                                        }
                                        out.write("></td></tr>");

                                        out.write("<tr><td>Record Headers</td><td>" + tp.isRecordHeaders()
                                                + "</td><td><input class=\"baseitem\" type=checkbox name=recordheaders value=recordheaders ");
                                        if (tp.isRecordHeaders()) {
                                            out.write("checked");
                                        }
                                        out.write("></td></tr>");
                                        //FIXME
                                %>
                        <tr><td colspan=3 >
                                <h4>Consumer Identification Policies <a class="btn" href="javascript:showUserIdentModal();">Add</a></h4>
                                <table id="consumerIdentTable"><tbody>


                                        <%
                                            if (tp.getUserIdentification() != null
                                                    && tp.getUserIdentification() != null
                                                    && tp.getUserIdentification().getUserIdentity() != null
                                                    && !tp.getUserIdentification().getUserIdentity().isEmpty()) {

                                                for (int i = 0; i < tp.getUserIdentification().getUserIdentity().size(); i++) {
                                                    String uid = UUID.randomUUID().toString();
                                                    out.write("<tr id=\"userIdentRow" + uid + "\"><td>"
                                                            + "<a href=\"javascript:EditUserIdent('" + uid + "');\" class=\"btn\"><i class=\"icon-edit\"></i> Edit</a> "
                                                            + "<a href=\"javascript:DeleteUserIdent('" + uid + "');\" class=\"btn btn-danger\"><i class=\"icon-remove-sign  icon-white\"></i> Remove</a><br>"
                                                            + "<b>Method: </b>");

                                                    //output human readable method.
                                                    //<input type=button name\"=RemoveUserID" + i + "\" value=\"Remove\" onclick=\"javascript:postBackReRender('RemoveUserID" + i + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\"></td></tr>");
                                                    if (tp.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential() != null && tp.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential()) {
                                                        out.write("Http Credential");
                                                        out.write("<input type=\"hidden\" name=\"uid_" + uid + "_method\" id=\"uid_" + uid + "_method\" value=\"httpcred\">");
                                                    } else if (tp.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader() != null && tp.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader()) {
                                                        out.write("Http Header<br><b>Header: </b>" + Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName()));
                                                        out.write("<input type=\"hidden\" name=\"uid_" + uid + "_method\" id=\"uid_" + uid + "_method\" value=\"httpheader\">");

                                                        if (!Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName())) {
                                                            out.write("<input type=\"hidden\" name=\"uid_" + uid + "_httpheadername\" id=\"uid_" + uid + "_httpheadername\"  value=\"" + Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName()) + "\">");
                                                        }
                                                    } else if (tp.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                                                            && tp.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                                                            && tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType() != null
                                                            && !tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().isEmpty()) {
                                                        out.write("XPath Expression<br>");
                                                        out.write("<input type=\"hidden\" name=\"uid_" + uid + "_method\" id=\"uid_" + uid + "_method\" value=\"xpath\">");

                                                        if (tp.getUserIdentification().getUserIdentity().get(i).getNamespaces() != null
                                                                && tp.getUserIdentification().getUserIdentity().get(i).getNamespaces() != null
                                                                && tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies() != null
                                                                && !tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().isEmpty()) {
                                                            out.write("XML Namespace Prefixes<br>");
                                                            
                                                            for (int k = 0; k < tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().size(); k++) {
                                                                String xmlnsid = UUID.randomUUID().toString();

                                                                if (!Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getPrefix())) {
                                                                    out.write("<input type=\"hidden\" name=\"uid_" + uid + "_xpath_" + xmlnsid + "_prefix\" id=\"uid_" + uid + "_xpath_" + xmlnsid + "_prefix\" value=\"");
                                                                    out.write(Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getPrefix()));
                                                                    out.write("\">");
                                                                    out.write(Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getPrefix()) + ": ");
                                                                }
                                                                if (!Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getNamespace())) {
                                                                    out.write("<input type=\"hidden\" name=\"uid_" + uid + "_xpath_" + xmlnsid + "_namespace\" id=\"uid_" + uid + "_xpath_" + xmlnsid + "_namespace\"  value=\"");
                                                                    out.write(Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getNamespace()));
                                                                    out.write("\">");
                                                                    out.write(Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getNamespace()) + "<br>");
                                                                }
                                                            }
                                                        }
                                                        //out.write("XPath Expressions<br>");
                                                        if (tp.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                                                                && tp.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                                                                && tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType() != null
                                                                && !tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().isEmpty()) {
                                                            for (int k = 0; k < tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().size(); k++) {

                                                                String xmlnsid = UUID.randomUUID().toString();
                                                                if (!Utility.stringIsNullOrEmpty(tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(k).getXPath())) {
                                                                    out.write("<input type=\"hidden\" name=\"uid_" + uid + "_xpath_" + xmlnsid + "_xpath\" id=\"uid_" + uid + "_xpath_" + xmlnsid + "_xpath\" value=\"");
                                                                    out.write(Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(k).getXPath()));
                                                                    out.write("\">");
                                                                    out.write("XPath: " + Utility.encodeHTML(tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(k).getXPath()));
                                                                }

                                                                out.write("<Br>Does this Xpath result in an X509 Certificate? ");
                                                                if (tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(k).isIsCertificate()) {
                                                                    out.write("<input type=\"hidden\" name=\"uid_" + uid + "_xpath_" + xmlnsid + "_x509certxpath\" id=\"uid_" + uid + "_xpath_" + xmlnsid + "_x509certxpath\" value=\"");
                                                                    out.write("true\">");

                                                                    out.write("yes");
                                                                } else {
                                                                    out.write("<input type=\"hidden\" name=\"uid_" + uid + "_xpath_" + xmlnsid + "_x509certxpath\" id=\"uid_" + uid + "_xpath_" + xmlnsid + "_x509certxpath\" value=\"");
                                                                    out.write("false\">");
                                                                    out.write("no");
                                                                }

                                                            }
                                                        }
                                                    } else {
                                                        out.write("error!?");
                                                    }
                                                    out.write("</td></tr>");

                                                }
                                            }
     //end of user ident
                                        %>
                                    </tbody>
                                </table>
                            </td>
                        </tr>

                        <%
                            }
//end of transactional policies
                        %>
                    </table>

                </div>


                <div id="div_dependencies">

                    None discovered.
                    <%
                        //these are loaded asynchronously via javascript
                    %>
                </div>

                <div  id="div_machine">

                    Machine

                    <%                        ManageHelper mhelper = new ManageHelper();
                        //machine
                        if (pol instanceof MachinePolicy) {
                            MachinePolicy mp = (MachinePolicy) pol;
                            out.write("<table class=\"table table-hover\"><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");
                            out.write("<tr>" + "<td>Monitor Disk Usage (Read/Writes/second) (space delimited)</td>" + "<td>" + Utility.encodeHTML(Utility.listStringtoString(mp.getRecordDiskUsagePartitionNames())) + "</td>"
                                    + "<td>");
                            //<input type=text id=recorddiskusage  value=\"");
                            out.write(mhelper.GetPartitionNamesAsHtmlListboxForThroughput(mp.getURL(), mp.getDomainName(), mp.getMachineName(), pcsport, c, mp.getRecordDiskUsagePartitionNames()));

                            //    out.write(Utility.encodeHTML(Utility.ListStringtoString(mp.getRecordDiskUsagePartitionNames())));
                            //                                 out.write("\" >"
                            out.write("</td></tr>");
                            //recorddiskusage
                            //recordnetworkusage
                            out.write("<tr>" + "<td>Monitor CPU Usage</td>" + "<td>" + mp.isRecordCPUusage() + "</td>" + "<td><input class=\"baseitem\" type=checkbox name=recordcpuusage value=recordcpuusage ");
                            if (mp.isRecordCPUusage()) {
                                out.write("checked");
                            }
                            out.write(" ></td></tr>");

                            out.write("<tr>" + "<td>Monitor Memory Usage</td>" + "<td>" + mp.isRecordMemoryUsage() + "</td>" + "<td><input class=\"baseitem\" type=checkbox name=recordcmemusage value=recordcmemusage ");
                            if (mp.isRecordMemoryUsage()) {
                                out.write("checked");
                            }
                            out.write(" ></td></tr>");

                            out.write("<tr>" + "<td>Monitor Network Usage (RX/TX KB/second) (<a href=\"javascript:ShowHelp('networkusage');\">help</a>)</td>"
                                    + "<td>" + Utility.encodeHTML(Utility.listStringtoString(mp.getRecordNetworkUsage())) + "</td>" + "<td>");
                            out.write(mhelper.GetNICNamesAsHtmlListbox(mp.getURL(), mp.getDomainName(), mp.getMachineName(), pcsport, c, mp.getRecordNetworkUsage()));
                            //      + "<input type=text id=recordnetworkusage value=\"");
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
                            //<input type=textbox id=recorddrivespace value=\"" + Utility.encodeHTML(Utility.ListStringtoString(mp.getRecordDiskSpace())) + "\" >
                            out.write(mhelper.GetPartitionNamesAsHtmlListboxForFreeSpace(mp.getURL(), mp.getDomainName(), mp.getMachineName(), pcsport, c, mp.getRecordDiskUsagePartitionNames()));
                            out.write("</td></tr>");

                            out.write("</table>");
                        } else {
                            out.write("Not applicable");
                        }
                    %>
                </div>


                <div id="div_process">
                    Process


                    <%
                        if (pol.getPolicyType() == PolicyType.PROCESS) {

                            //process
                            if (pol instanceof ProcessPolicy) {
                                ProcessPolicy mp = (ProcessPolicy) pol;
                                out.write("<table  class=\"table table-hover\"><tr><th>Key</th><th>Current Value</th><th>New Value</th></tr>");
                                out.write("<tr>" + "<td>Monitor Open File Count</td>" + "<td>" + mp.isRecordOpenFileHandles() + "</td>" + "<td><input type=checkbox name=recordopenfiles value=recordopenfiles ");
                                if (mp.isRecordOpenFileHandles()) {
                                    out.write("checked");
                                }
                                out.write(" ></td></tr>");

                                out.write("<tr>" + "<td>Monitor CPU Usage</td>" + "<td>" + mp.isRecordCPUusage() + "</td>" + "<td><input class=\"baseitem\" type=checkbox name=recordcpuusage value=recordcpuusage ");
                                if (mp.isRecordCPUusage()) {
                                    out.write("checked");
                                }
                                out.write(" ></td></tr>");

                                out.write("<tr>" + "<td>Monitor Memory Usage</td>" + "<td>" + mp.isRecordMemoryUsage() + "</td>" + "<td><input class=\"baseitem\" type=checkbox name=recordcmemusage value=recordcmemusage ");
                                if (mp.isRecordMemoryUsage()) {
                                    out.write("checked");
                                }
                                out.write(" ></td></tr>");

                                out.write("<tr><td>AKA (<a href=\"javascript:ShowHelp('aka')\">help</a>)</td><td>" + Utility.encodeHTML(mp.getAlsoKnownAs()) + "</td><td><input class=\"baseitem\" type=text name=processaka id=processaka value=\"" + Utility.encodeHTML(mp.getAlsoKnownAs()) + "\"></td></tr>");
                                //out.write(" ></td></tr>");

                                out.write("</table>");
                            } else if (pol instanceof MachinePolicy) {
                                out.write("<a id=viewproc href=\"serverprocess.jsp?server=" + URLEncoder.encode(pol.getMachineName()) + "\">View Processes on this machine</a>");
                            } else {
                                out.write("Not applicable");
                            }

                        }
                    %>
                </div>

                <div  id="div_federation">
                    Federation <a href="javascript:showFederationModal();"  class="btn" ><i class="icon-plus-sign"></i> Add</a>
                    <table id="federationTable" class="table table-hover">


                        <%
                            // out.write("Data Federation <input class=\"btn btn-primary\" type=button id=addFedMethod value=\"Add\" onclick=\"javascript:postBackReRender('addFedMethod','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">");
                            if (pol.getFederationPolicyCollection() != null) {
                                for (int i = 0; i < pol.getFederationPolicyCollection().getFederationPolicy().size(); i++) {
                                    String randomId = UUID.randomUUID().toString();
                                    out.write("<tr id=\"federationRow" + randomId + "\"><td>" + (DEBUG ? (" ID = " + randomId + "<br>") : "")
                                            + " <a class=\"btn\" href=\"javascript:EditFederation('" + randomId
                                            + "');\"><i class=\"icon-edit\"></i> Edit</a>"
                                            + " &nbsp;&nbsp;<a class=\"btn btn-danger\" href=\"javascript:DeleteFederation('" + randomId
                                            + "');\"><i class=\"icon-remove-sign  icon-white\"></i> Remove</a>"
                                            + "<br><b>Target:</b> ");
                                    GetPluginInformationResponseMsg info = Helper.GetPluginInfo(pol.getFederationPolicyCollection().getFederationPolicy().get(i).getImplementingClassName(), "FEDERATION_PUBLISH", pcsport, c);
                                    out.write(info.getDisplayName());

                                    out.write("<input type=hidden name=\"fed_" + randomId
                                            + "_class" + "\" value=\""
                                            + Utility.encodeHTML(pol.getFederationPolicyCollection().getFederationPolicy().get(i).getImplementingClassName()) + "\">");
                                    //output all existing values
                                    for (int x = 0; x < pol.getFederationPolicyCollection().getFederationPolicy().get(i).getParameterNameValue().size(); x++) {
                                        out.write("<input type=hidden name=\"fed_" + randomId
                                                + "_" + Utility.encodeHTML(pol.getFederationPolicyCollection().getFederationPolicy().get(i).getParameterNameValue().get(x).getName()) + "\" value=\""
                                                + Utility.encodeHTML(pol.getFederationPolicyCollection().getFederationPolicy().get(i).getParameterNameValue().get(x).getValue()) + "\">");
                                        out.write("<input type=hidden name=\"fed_" + randomId
                                                + "_enc_" + Utility.encodeHTML(pol.getFederationPolicyCollection().getFederationPolicy().get(i).getParameterNameValue().get(x).getName()) + "\" value=\""
                                                + pol.getFederationPolicyCollection().getFederationPolicy().get(i).getParameterNameValue().get(x).isEncrypted() + "\">");
                                    }

                                }
                            }
                        %>
                    </table>
                </div>




                <div  id="div_sla">
                    <table class="table table-hover" id="currentSlas">
                        <tr><th>Service Level Agreements
                                <!-- Button to trigger add a new SLA modal -->
                                <a href="javascript:showSlaModal();"  class="btn" ><i class="icon-plus-sign"></i> Add</a>
                            </th></tr>
                            <%
                                //render existing SLAs
                                if (pol.getServiceLevelAggrements() != null
                                        && pol.getServiceLevelAggrements() != null
                                        && pol.getServiceLevelAggrements().getSLA() != null
                                        && !pol.getServiceLevelAggrements().getSLA().isEmpty()) {
                                    for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
                                        out.write("<tr id=\"slaRow" + pol.getServiceLevelAggrements().getSLA().get(i).getGuid() + "\"><td>" + 
                                                (DEBUG ? (" ID = " + pol.getServiceLevelAggrements().getSLA().get(i).getGuid()) + "<br>" : "")
                                                + " <a class=\"btn\" href=\"javascript:EditSLA('" + pol.getServiceLevelAggrements().getSLA().get(i).getGuid() + "');\"><i class=\"icon-edit\"></i> Edit this SLA</a>"
                                                + " &nbsp;&nbsp;<a class=\"btn btn-danger\" href=\"javascript:DeleteSLA('" + pol.getServiceLevelAggrements().getSLA().get(i).getGuid() + "');\"><i class=\"icon-remove-sign  icon-white\"></i> Remove</a>"
                                                + "<br><b>Rule:</b>");

                                        SLARuleGeneric rule = (SLARuleGeneric) pol.getServiceLevelAggrements().getSLA().get(i).getRule();
                                        out.write("<input type=hidden name=\"sla_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid()) + "_RULE_class\" value=\""
                                                + Utility.encodeHTML(rule.getClassName()) + "\">");
                                        //NOT NEEDED  GetPluginInformationResponseMsg plugin=  Helper.GetPluginInfo(rule.getClassName(), "SLA_RULE", pl.GetPCS(application, request, response), c);
                                        //output all existing values
                                        for (int x = 0; x < rule.getParameterNameValue().size(); x++) {
                                            out.write("<input type=hidden name=\"sla_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid())
                                                    + "_RULE_" + Utility.encodeHTML(rule.getParameterNameValue().get(x).getName()) + "\" value=\""
                                                    + Utility.encodeHTML(rule.getParameterNameValue().get(x).getValue()) + "\">");
                                            out.write("<input type=hidden name=\"sla_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid())
                                                    + "_RULE_enc_" + Utility.encodeHTML(rule.getParameterNameValue().get(x).getName()) + "\" value=\""
                                                    + rule.getParameterNameValue().get(x).isEncrypted() + "\">");
                                        }

                                        //sla_{id}_ACTION_{id'}_class
                                        //sla_{id}_ACTION_{id'}_{param}
                                        //sla_{id}_ACTION_{id'}_enc_{param}
                                        //sla_{id}_RULE_class
                                        //sla_{id}_RULE_{param}=value
                                        //sla_{id}_RULE_enc_{param}=true/false
                                        String sla = Helper.BuildSLARuleData(pol.getServiceLevelAggrements().getSLA().get(i).getRule(), rule_plugins);
                                        out.write(sla);

                                        out.write("<br><b>Actions:</b><br><ul>");
                                        for (int i2 = 0; i2 < pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().size(); i2++) {
                                            String random = UUID.randomUUID().toString();
                                            out.write("<li>" + Helper.ToFriendlyName(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2), pcsport, c)
                                                    + "<//li>");
                                            out.write("<input type=hidden name=\"slaText_" +  Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid()));
                                            out.write("_ACTION_" + random +"\" value=\"");
                                            out.write(Helper.ToFriendlyName(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2), pcsport, c));
                                            out.write("\">");
                                            out.write("<input type=hidden name=\"sla_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid())
                                                    + "_ACTION_" + random + "_classname\" value=\""
                                                    + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2).getImplementingClassName()) + "\">");
                                            for (int i3 = 0; i3 < pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2).getParameterNameValue().size(); i3++) {
                                                out.write("<input type=hidden name=\"sla_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid())
                                                        + "_ACTION_" + random + "_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2).getParameterNameValue().get(i3).getName()) + "\" value=\""
                                                        + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2).getParameterNameValue().get(i3).getValue()) + "\">");

                                                //is in encrypted now?
                                                out.write("<input type=hidden name=\"sla_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid())
                                                        + "_ACTION_" + random + "_enc_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2).getParameterNameValue().get(i3).getName()) + "\" value=\""
                                                        + pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2).getParameterNameValue().get(i3).isEncrypted() + "\">");
                                                 //should it be encrypted on save
                                                 
                                                //out.write("<input type=hidden name=\"sla_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getGuid())
                                                  //      + "_ACTION_" + random + "_encOnSave_" + Utility.encodeHTML(pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(i2).getParameterNameValue().get(i3).getName()) + "\" value=\"false\">");
                                                
                                             

                                            }
                                        }
                                        //empty row
                                        out.write("</ul></td></tr>");
                                    }

                                } else {
                                    out.write("<tr><td colspan=2>No SLAs defined.</td></tr>");
                                }

                            %>
                    </table>
                </div>


                <div id="div_status">
                    <%                        if (pol.getPolicyType() != PolicyType.PROCESS && pol.getPolicyType() != PolicyType.MACHINE) {

                            out.write("<table class=\"table table-hover\">");

                            if (pol instanceof TransactionalWebServicePolicy) {
                                TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
                                out.write("<tr>"
                                        + "<td>Realtime Monitoring via HTTP GET (WSDL pull)</td>"
                                        + "<td>" + tp.isBuellerEnabled() + "</td>"
                                        + "<td><input class=\"baseitem\" type=checkbox name=buellerenabled  ");
                                if (tp.isBuellerEnabled()) {
                                    out.write("checked");
                                }
                                out.write("></td></tr>");

                            }
                            if (!(pol instanceof ProcessPolicy) && !(pol instanceof MachinePolicy)) {

                                out.write("<tr>"
                                        + "<td align=left><a id=setcred class=\"btn btn-primary\" href=\"javascript:loadpage('profile/setcredentials.jsp?uri=" + URLEncoder.encode(request.getParameter("url"), "UTF8") + "','mainpane');\">Set Status Credentials</a></td>"
                                        + "<td align=left></td>"
                                        + "<td align=left><a id=clearcred class=\"btn btn-primary\" href=\"javascript:loadpage('profile/removecredentials.jsp?uri=" + URLEncoder.encode(request.getParameter("url"), "UTF8") + "','mainpane');\">Clear Credentials</a></td>"
                                        + "</tr>");
                            }
                            out.write("</table>");
                        }   //status monitoring
                    %>
                </div>


                <div  id="div_admin">
                    <%
                        out.write("<table class=\"table table-hover\">");

                        out.write("<tr>"
                                + "<td></td>"
                                + "</tr>"
                                + "<tr>"
                                + "<td align=left><a class=\"btn btn-primary\" href=\"javascript:postBackReRender('deletepolicyanddata','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url"), "UTF8") + "','tab1');\" >Delete Policy and Data</a></td>"
                                + "<td align=right><a class=\"btn btn-primary\" href=\"javascript:postBackReRender('deletedata','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url"), "UTF8") + "','tab1');\" >Delete Data Only</a></td></tr>"
                                + "<tr><td>");
                        out.write("<a class=\"btn btn-primary\" id=exportlink href=\"profile/policyExport.jsp?url=" + URLEncoder.encode(request.getParameter("url"), "UTF8") + "\" >Export this policy to XML</a>");
                        out.write("</td><td>");
                        out.write("<a class=\"btn btn-primary\" id=importlink href=\"javascript:loadpage('profile/policyImport.jsp?url=" + URLEncoder.encode(request.getParameter("url"), "UTF8") + "','mainpane');\" >Import this policy from XML</a></td>");
                        out.write("</tr>");

                        out.write("</table>");

                        //TODO confirm we still use this
                        request.getSession().setAttribute("workingpolicy" + pol.getURL(), pol);
                    %>


                </div>
            </div>
        </div>
    </div>




    <!-- transaction user ident Modal -->
    <div id="myUserIdentModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myUserIdentLabel" aria-hidden="true">
        <div class="modal-header">
            <a class="close" data-dismiss="modal" aria-hidden="true">×</a>
            <h3 id="myUserIdentLabel">User Identification</h3>
        </div>
        <div class="modal-body" id="userident_pluginparameters">
            FGSMS can optionally identify the requestor of a web service via a variety of mechanisms including
            <ul>
                <li>Http Header Value - commonly used for applications residing behind reverse SSL proxies or with single sign on solutions</li>
                <li>Http Identity - typically a username or PKI identity. This information is automatically recorded by the agents as well as the requestor's IP address</li>
                <li>XPath Expression - typically used for token based identification, such as SAML, WS-Username, or for selecting a Binary Security Token that is an X509 Digital Certificate. 
                    If the result is an encoded X509 Certificate, you can also optionally have FGSMS agents just record the subject name of the certificate instead of the entire token. 
                    <br><br>Note: .NET based Agents: all XML prefix/namespace definitions used in the XPath expression must be defined. Java based agents do not require prefix/namespace
                    definitions (this also implies that you need to leave out all prefixes from your xpath query expressions for Java agents.</li></ul>
            Depending on the underlying framework, each mechanism may or may not be support by a given agent implementation.<br><br>
            User Identification Method:
            <select onchange="toggleVisibilityUserID();" name="newIdent_useridtype" id="newIdent_useridtype">
                <option value="httpcred">Http Credential</option>
                <option value="httpheader">Http Header</option>
                <option value="xpath">XPath Expression</option>
            </select><br>
            <div id="httpheaderdiv" style="display:none;">Http Header Parameter
                <input type="text" id="newIdent_httpheadername" name="newIdent_httpheadername">
            </div>
            <div id="xpathdiv" style="display:none;">
                <table class="table">
                    <tr>
                        <td>XPath Expression</td>
                        <td><input type="text" id="newIdent_xpathexpression" name="newIdent_xpathexpression" ></td>
                    </tr>
                    <tr>
                        <td>XPath Prefix</td>
                        <td><input type="text" id="newIdent_xpathprefixes" name="newIdent_xpathprefixes"  placeholder="prefix##namespace|prefix2##namespace2" ></td>
                    </tr>
                    <tr>
                        <td>Does this XPath expression yield an X509 Certificate?</td>
                        <td><input type="checkbox" id="newIdent_x509certxpath" name="newIdent_x509certxpath"></td>
                    </tr>
                </table>

            </div>



        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
            <a href="javascript:addUserIdent();" class="btn btn-primary">Add</a>
            <%
                //this bit should do an async push of the changes, big bang approach, then reload the policy editor

            %>
        </div>
    </div>

    <script type="text/javascript">
        //# sourceURL=addUserIdent.js

        var newestUserIdent = newUuid();


        function toggleVisibilityUserID()
        {


            var x = $("#newIdent_useridtype").val();
            if ( "httpcred" === x)
            {
                x = document.getElementById("httpheaderdiv");
                x.style.display = "none";
                x = document.getElementById("xpathdiv");
                x.style.display = "none";
            } else if ("httpheader"===x)
            {
                x = document.getElementById("httpheaderdiv");
                x.style.display = "";
                x = document.getElementById("xpathdiv");
                x.style.display = "none";
            } else if ("xpath"===x)
            {
                x = document.getElementById("httpheaderdiv");
                x.style.display = "none";
                x = document.getElementById("xpathdiv");
                x.style.display = "";
            } else {
                //x is undefined, strange edge case
                 x = document.getElementById("httpheaderdiv");
                x.style.display = "none";
                x = document.getElementById("xpathdiv");
                x.style.display = "none";
            }

            return false;

        }
        function EditUserIdent(userIdentUUID) {
            //first get the method of the current user ident mechanism
            var method = $("#uid_" + userIdentUUID + "_method").val();
            window.console.log('edit user ident with id ' + userIdentUUID + " current method is " + method);
            //clear the fields of the modal
            $("#newIdent_useridtype").val(method);
            if (method === "httpheader") {
                var currentheadervalue=$("#uid_" + userIdentUUID + "_httpheadername").val()
                $("#newIdent_httpheadername").val(currentheadervalue);
            } else if (method === "xpath") {
                //populate the xpath junk
                //loop
                var idsWeveSeenAlready = [];
                var nsWithXpaths = "";
                var items = $('[id^=' + escapeJQuerySelctor("uid_" + userIdentUUID + "_xpath_") + ']');
                $(items).each(function (index) {
                    var item = $(this);
                    var item_id = $(this).attr("id");
                    if ($.inArray(item_id, idsWeveSeenAlready) == -1) {
                        idsWeveSeenAlready.push(item_id);
                        var value = $("#" + item_id).val();
                        if (item_id.endsWith("namespace")) {
                            //value is the namespace, now get the namespace
                            var prefix = $("#" + (item_id.replace("namespace", "prefix"))).val();
                            nsWithXpaths = prefix + "##" + value + "|" + nsWithXpaths;
                            var xpath = $("#" + (item_id.replace("namespace", "xpath"))).val();
                            $("#newIdent_xpathexpression").val(xpath);
                        }
                        if (item_id.endsWith("prefix")) {
                            //value is the prefix, now get the namespace
                            var ns = $("#" + (item_id.replace("prefix", "namespace"))).val();
                            nsWithXpaths = value + "##" + ns + "|" + nsWithXpaths;

                            var xpath = $("#" + (item_id.replace("prefix", "xpath"))).val();
                            $("#newIdent_xpathexpression").val(xpath);

                            //newIdent_x509certxpath
                        }

                        //need the checkbox?
                    }
                });
                $("#newIdent_xpathprefixes").val(nsWithXpaths.slice(0, -1));



                var items = $('[id^=' + escapeJQuerySelctor("uid_" + userIdentUUID + "_xpath_") + ']');
                $(items).each(function (index) {
                    var item_id = $(this).attr("id");

                    var value = $("#" + item_id).val();
                    if (item_id.endsWith("_xpath")) {
                        //value is the namespace, now get the namespace
                        var checked = $("#" + (item_id.substring(0, item_id.lastIndexOf("xpath")) + "x509certxpath")).val();
                        //var trimmed = value.slice(0, -1);
                        //var trimmed2 = trimmed.slice(0, -1);
                        $("#newIdent_xpathexpression").val(value);	//trim the last | symbol
                        //need the checkbox?
                        if ("true" === checked)
                            $("#newIdent_x509certxpath").prop('checked', true);
                        else
                            $("#newIdent_x509certxpath").prop('checked', false);

                    }



                });

                //TODO loop?
                //var xpath = $("#uid_" + userIdentUUID + "_{different}_prefix").val();

            } else {
                //doesn't matter for container/http credential
            }
            toggleVisibilityUserID();
            //TODO 
            //populate the fields of the modal
            //display the modal
            newestUserIdent = userIdentUUID;

            $('#myUserIdentModal').modal();

        }

        function editUserIdentTargetPopulate(fedUUID) {
            //when done, cram in our values, starting with the rule data
            $("input[name^='fed_" + fedUUID + "_").each(function () {
                var name = $(this).attr("name");
                var bits = name.split("fed_" + fedUUID + "_");
                var val = bits[1];

                var value = $(this).val();
                if (val !== "class")
                    $("input[name='plugin_PUBLISH_" + val + "']").val(value);
                //TODO the "enc" fields
            });



            showFederationModal();


        }
        function DeleteUserIdent(id) {

            $('#userIdentRow' + id).remove();
            pulseSaveButton();
        }
        function addUserIdent() {

            //if editing an existing user ident, remove the old values from the page before replacing it with the new
            //
            //get rid of the old stuff and replace with hte new
            
            //not working?

            $("#userIdentRow" + newestUserIdent).remove();

            //TODO insert check for valid namespace prefixes
            //
            //
            //plugin_PUBLISH_enc_Averages
         $('#myUserIdentModal').modal('hide');
                 //var thisurl = '<%=StringEscapeUtils.escapeEcmaScript(pol.getURL())%>';


            var useridentselection = $('#newIdent_useridtype').val();
            var content = "";
            //process content to append to the current SLA menu
            content = "<tr id=\"userIdentRow" + newestUserIdent + "\"><td> <a class=\"btn\" href=\"javascript:EditUserIdent('" + newestUserIdent +
                    "');\"><i class=\"icon-edit\"></i> Edit</a> &nbsp;&nbsp;<a class=\"btn btn-danger\" href=\"javascript:DeleteUserIdent('" + newestUserIdent +
                    "');\"><i class=\"icon-remove-sign  icon-white\"></i> Remove</a><br><b>Method: </b>";

            content = content + ("<input type=\"hidden\" id=\"uid_" + newestUserIdent + "_method\"  name=\"uid_" + newestUserIdent + "_method" + "\" value=\"" + useridentselection + "\">");


            if (useridentselection === 'httpcred') {
                //no fields to aquire
                content = content + "Http Credential"

            } else if (useridentselection === 'httpheader') {
               

                var header = $("#newIdent_httpheadername").val();
                 content = content + "Http Header<br><b>Header: </b>" + escapeHtml(header);
                content = content + ("<input type=\"hidden\" id=\"uid_" + newestUserIdent + "_httpheadername\"  name=\"uid_" + newestUserIdent + "_httpheadername\"  value=\"" + escapeHtml(header) + "\">");
                //newIdent_httpheadername
            } else if (useridentselection === 'xpath') {
                content = content + "XPath Expression"
                var header = $("#newIdent_xpathexpression").val();
                content = content + ("<input type=\"hidden\" id=\"uid_" + newestUserIdent + "_xpathexpression\"  name=\"uid_" + newestUserIdent + "_xpathexpression\"  value=\"" + escapeHtml(header) + "\">");
                header = $("#newIdent_x509certxpath").val();
                content = content + ("<input type=\"hidden\" id=\"uid_" + newestUserIdent + "_x509certxpath\"  name=\"uid_" + newestUserIdent + "_x509certxpath\"  value=\"" + escapeHtml(header) + "\">");
                header = $("#newIdent_xpathprefixes").val();
                //need to split on |
                if (header.length!==0){
                    var groups = header.split("|");
                    for (var i = 0; i < groups.length; i++) {
                        var tempid = newUuid();
                        var item = groups[i];
                        var temp = item.split("##");
                        var prefix = temp[0];
                        var ns = temp[1];
                        if (ns !== undefined) {
                            content = content + ("<input type=\"hidden\" id=\"uid_" + newestUserIdent + "_xpath_" + tempid + "_prefix\"  name=\"uid_" + newestUserIdent + "_xpath_" + tempid + "_prefix\"  value=\"" + escapeHtml(prefix) + "\">");
                            content = content + ("<input type=\"hidden\" id=\"uid_" + newestUserIdent + "_xpath_" + tempid + "_namespace\"  name=\"uid_" + newestUserIdent + "_xpath_" + tempid + "_namespace\"  value=\"" + escapeHtml(ns) + "\">");
                        }
                    }
                }
                //then on ##

                //content = content + ("<input type=\"hidden\" id=\"uid_"+newestUserIdent +"_httpheadername\"  name=\"uid_"+newestUserIdent +"_httpheadername\"  value=\"" + header + "\">");
                //
                //newIdent_xpathprefixes
                //
            }



            content = content + "</td></tr>";

            $('#consumerIdentTable').append(content);
            pulseSaveButton();
        }
        function showUserIdentModal() {
            newestUserIdent = newUuid();

            $('#myUserIdentModal').modal();
        }
    </script>








    <!-- federation Modal -->
    <div id="myFederationTargetModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myFederationTargetLabel" aria-hidden="true">
        <div class="modal-header">
            <a class="close" data-dismiss="modal" aria-hidden="true">×</a>
            <h3 id="myFederationTargetLabel">Add a Federation Target</h3>
        </div>
        <div class="modal-body">
            <h3>Plugin

                <select id="fedtarget" onchange="refreshPluginParams('fedtarget', 'federation_pluginparameters', 'FEDERATION_PUBLISH');">
                    <%
                        List<Plugin> fedplugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "FEDERATION_PUBLISH");
                        for (int i5 = 0; i5 < fedplugins.size(); i5++) {
                            out.write("<option value=\"" + Utility.encodeHTML(fedplugins.get(i5).getClassname()) + "\"  ");
                            // if (existing != null && ((SLARuleGeneric) existing.getRule()).getClassName().equals(fedplugions.get(i5).getClassname())) {
                            //   out.write(" selected ");
                            //}
                            out.write(">" + Utility.encodeHTML(fedplugins.get(i5).getDisplayname()) + "</option>");
                        }
                    %>
                </select></h3>
            <div id="federation_pluginparameters" >Pick a plugin!</div>

        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
            <a href="javascript:addFederationTarget();" class="btn btn-primary">Done</a>
            <%
                //this bit should do an async push of the changes, big bang approach, then reload the policy editor

            %>
        </div>
    </div>

    <script type="text/javascript">
        //@ sourceURL=addFed.js
        refreshPluginParams('fedtarget', 'federation_pluginparameters', 'FEDERATION_PUBLISH');
        var newestFederationId = newUuid();
        var editingFederationRow;


        function EditFederation(federationUUID) {



            //get all the parameters for this item
            //switch the selector
            //when it's complete, populate the parameter list

            editingFederationRow = "#federationRow" + federationUUID;
            var slarule = $('input[name=fed_' + federationUUID + "_class]").val();
            $('#fedtarget').val(slarule);
            refreshPluginParamsWithCallback('fedtarget', 'federation_pluginparameters', 'FEDERATION_PUBLISH', editFederationTargetPopulate, federationUUID);
        }

        function editFederationTargetPopulate(fedUUID) {
            editableExisitingNames = [];
            //when done populating the plugin params, cram in our values, starting with the rule data
            $("input[name^='fed_" + fedUUID + "_").each(function () {
                var name = $(this).attr("name");
                editableExisitingNames.push(name);
                var bits = name.split("fed_" + fedUUID + "_");
                var val = bits[1];

                var value = $(this).val();
                if (val !== "class") {
                    $("input[name='plugin_PUBLISH_" + val + "']").val(value);
                    if (val.startsWith("enc_")){
                           $("#plugin_ACTION_" + val).prop('checked',value);
                       } else if (val.startsWith("encOnSave_")){
                           $("#plugin_ACTION_" + val).prop('checked',value);
                       }
                }
                //TODO the "enc" fields
            });



            showFederationModal();


        }
        function DeleteFederation(id) {

            $('#federationRow' + id).remove();
            pulseSaveButton();
        }

        /**
         * triggered after the user add finished adding/editing
         * @returns {undefined}
         */
        function addFederationTarget() {

            //get rid of the old stuff and replace with hte new
            var arrayLength = editableExisitingNames.length;
            for (var i = 0; i < arrayLength; i++) {
                $("input[name=" + (editableExisitingNames[i]) + "]").remove();
            }

            editableExisitingNames = [];
            $(editingFederationRow).remove();
            editingFederationRow = null;

            //plugin_PUBLISH_enc_Averages
             $('#myFederationTargetModal').modal('hide');
            var thisurl = '<%=StringEscapeUtils.escapeEcmaScript(pol.getURL())%>';


            //debug
            $('input, select').each(
                    function (index) {
                        var input = $(this);
                        window.console && console.log('Type: ' + input.attr('type') + 'Name: ' + input.attr('name') + 'Value: ' + input.val());
                    }
            );


            //var newid = newUuid();
            //get all inputs with id's that start with plugin_ACTION_

            var rule_classname = $('#fedtarget').val();
            var rule_text = $("#fedtarget option:selected").text();
            var content = "";//<tr><td>" + action_text + "</td><td>";


            //grab the rule data
            $('#federation_pluginparameters').find('input').each(function (i, l) {
                //alert(i + ' ' + l);
                var id = $(this).attr("name");
                if (id !== null && id !== undefined && id.indexOf('plugin_PUBLISH_') == 0) {
                    var val = $(this).val();
                    window.console && console.log(val);
                    window.console && console.log(id);
                    id = id.replace("plugin_PUBLISH_", "");
                    window.console && console.log(id);
                    //window.alert (id + ' ' + val);
                    var thisid = "fed_" + newestFederationId + "_" + id;
                    window.console && console.log(id + " = " + val);
                     if (id.indexOf("encOnSave_") > -1 || id.indexOf("enc_") > -1){
                        //whoa it's a checkbox, work on jquery magic
                        if ($(this).is(':checked'))
                            val="true";
                        else val = "false";
                    }
                    content = content + ("<input type=\"hidden\" name=\"" + thisid + "\" value=\"" + val + "\">");

                }
            });
            content = content + ("<input type=\"hidden\" name=\"fed_" + newestFederationId + "_" + "class" + "\" value=\"" + rule_classname + "\">");




            //process content to append to the current SLA menu
            content = "<tr id=\"federationRow" + newestFederationId + "\"><td> <a class=\"btn\" href=\"javascript:EditFederation('" + newestFederationId +
                    "');\"><i class=\"icon-edit\"></i> Edit</a> &nbsp;&nbsp;<a class=\"btn btn-danger\" href=\"javascript:DeleteFederation('" + newestFederationId +
                    "');\"><i class=\"icon-remove-sign  icon-white\"></i> Remove</a><br><b>Target:</b> " + rule_text + content +
                    "</td></tr>";

            $('#federationTable').append(content);
            pulseSaveButton();
        }
        function showFederationModal() {
            newestFederationId = newUuid();

            $('#myFederationTargetModal').modal();
        }
    </script>







    <%

        ////////////////////////////////
        // SLA Editor modals are below
        SLA existing = null;
    %>



    <!-- sla proper Modal -->
    <div id="mySLAModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
            <a class="close" data-dismiss="modal" aria-hidden="true">×</a>
            <h3 id="myModalLabel">Add a Service Level Agreement</h3>
        </div>
        <div class="modal-body">
            <h3>Rule

                <select id="rule1" onchange="refreshPluginParams('rule1', 'SLARuleGenericdiv', 'SLA_RULE');">
                    <%
                        for (int i5 = 0; i5 < rule_plugins.size(); i5++) {
                            out.write("<option value=\"" + Utility.encodeHTML(rule_plugins.get(i5).getClassname()) + "\"  ");
                            if (existing != null && ((SLARuleGeneric) existing.getRule()).getClassName().equals(rule_plugins.get(i5).getClassname())) {
                                out.write(" selected ");
                            }
                            out.write(">" + Utility.encodeHTML(rule_plugins.get(i5).getDisplayname()) + "</option>");
                        }
                    %>
                </select></h3>
            <div id="SLARuleGenericdiv" >Pick a Rule plugin!</div>
            <h3>Actions  <a href="#myAddSLA_ActionModal" role="button" class="btn btn-primary" data-toggle="modal">Add Action</a></h3> <!-- Button to trigger add a new SLA modal -->


            <table id="existingActions" class="table table-hover">
                <tbody>
                <tr><th>Type</th><th>Actions</th></tr>

                </tbody>
            </table>

        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
            <a href="javascript:addSLA();" class="btn btn-primary">Done</a>
            <%
                //this bit should do an async push of the changes, big bang approach, then reload the policy editor

            %>
        </div>
    </div>




    <!-- sla action Modal -->
    <div id="myAddSLA_ActionModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myAddSLA_ActionModalLabel" aria-hidden="true">
        <div class="modal-header">
            <a class="close" href="javascript:cancelSlaActionEdit();">×</a>
            <h3 id="myAddSLA_ActionModalLabel">Add an Action</h3>
        </div>
        <div class="modal-body">
            <select id="combo_SLA_ACTION" name="combo_SLA_ACTION" onchange="refreshPluginParams('combo_SLA_ACTION', 'pluginaction', 'SLA_ACTION');">
                <%    //List<Plugin> actions = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "SLA_ACTION", pol.getPolicyType());
                    for (int i5 = 0; i5 < actions.size(); i5++) {

                        out.write("<option value=\"" + Utility.encodeHTML(actions.get(i5).getClassname()) + "\"");

                        out.write(" >" + Utility.encodeHTML(actions.get(i5).getDisplayname()) + "</option>");
                    }

                %>
            </select><Br>
            <div  id=pluginaction id=pluginaction>Pick an Action!</div>

        </div>
        <div class="modal-footer">
            <a class="btn" href="javascript:cancelSlaActionEdit();">Cancel</a>
            <a href="javascript:addSLAAction();" class="btn btn-primary">Add SLA</a>

            <%                //this bit should do an async push of the changes

            %>
        </div>
    </div>









    <script type="text/javascript"  >
        //@ sourceURL=addSLA.js

        var editableExisitingNames = [];

        $(document).ready(function () {


            $('input.baseitem').on('keyup change', function () {
                //$('p.display').text('The value of the text input is: ' + $(this).val());
                pulseSaveButton();
            });
        });
        /////// this whole block is for adding, removing and editing SLAs


        refreshPluginParams('rule1', 'SLARuleGenericdiv', 'SLA_RULE');
        refreshPluginParams('combo_SLA_ACTION', 'pluginaction', 'SLA_ACTION');



        var newestSLA_id = newUuid();


        var ispulsing = false;
        function pulseSaveButton() {
            if (ispulsing)
                return;
            ispulsing = true;
            $('#btn_save').addClass("btn-primary");
            $('#btn_save').addClass("btn-large");
            // do fading 3 times
            for (i = 0; i < 20; i++) {
                $('#btn_save').fadeTo('slow', 0.5).fadeTo('slow', 1.0);
            }
            ispulsing = false;
        }


        function showSlaModal() {
            newestSLA_id = newUuid();

            $('#mySLAModal').modal();

        }
        function EditSLA(slaUUID) {
            //nuke all existing rows
            $("#existingActions").find("tr:gt(0)").remove();
            
            newestSLA_id = slaUUID;
            //prepopulate modal based on existing data
            var slarule = $('input[name=sla_' + slaUUID + "_RULE_class]").val();
            //sets the spinner
            $('#rule1').val(slarule);
            refreshPluginParamsWithCallback('rule1', 'SLARuleGenericdiv', 'SLA_RULE', editSlaPopulate, slaUUID);
        }

        function editSlaPopulate(slaUUID) {
            
            //nuke anything that was there previously
             //$("existingActions").find("tr:gt(0)").remove();
             
            editableExisitingNames = [];
            //editing an existing SLA rule/action set
            //when done, cram in our values, starting with the rule data
            $("input[name^='sla_" + slaUUID + "_RULE_']").each(function () {

                var name = $(this).attr("name");
                editableExisitingNames.push(name);
                var bits = name.split("sla_" + slaUUID + "_RULE_");
                var val = bits[1];
                if (val !== "classname") {
                    var value = $(this).val();
                    if (val !== "class")
                        $("input[name='plugin_RULE_" + val + "']").val(value);
                }
                if (val.startsWith("encOnSave_")) {
                    $("input[name='plugin_RULE_" + val + "']").prop('checked', value);
                }
                else if (val.startsWith("enc_")) {
                    $("input[name='plugin_RULE_" + val + "']").prop('checked', value);
                }
                //TODO the "enc" fields
            });

            //populate all existing sla actions
            //sla_{}_ACTION_{}_xyz
            //sla_{}_ACTION_{}_enc_xyz
            //sla_{}_ACTION_{}_classname
            //slaText_{}_ACTION_{}
            var slaactionids = [];
//definitely a problem here
            $("input[name^='" + escapeJQuerySelctor("sla_" + slaUUID + "_ACTION_") + "']").each(function () {
                var name = $(this).attr("name");
                editableExisitingNames.push(name);
                var temp = name.replace("sla_" + slaUUID + "_ACTION_", "");
                var bits = temp.split("_");

                //this is the sla action id
                var val = bits[0];
                if ($.inArray(val, slaactionids) === -1)
                    slaactionids.push(val);

                //bits[1] is the sla action parameter name

            });
            var content = "";
            //second pass, get everything related to a single sla
            for (var i = 0; i < slaactionids.length; i++)
            {
                //get all inputs with id's that start with plugin_ACTION_
                var action_classname;
                var action_text;//human readable
                var tcontent = "";

                var thisid = slaactionids[i];
                $("input[name^='" + escapeJQuerySelctor("sla_" + slaUUID + "_ACTION_" + thisid + "_") + "']").each(function () {
                    var name = $(this).attr("name").replace("sla_" + slaUUID + "_ACTION_" + thisid + "_", "");
                    if (name === "classname") {
                        
                        action_classname = $(this).val();
                    } 
                    tcontent = tcontent + ("<input type=\"hidden\" id=\"sla_" + slaUUID + "_ACTION_" + thisid + "_" + name + "\" name=\"sla_" + slaUUID + "_ACTION_" + thisid + "_" + name + "\" value=\"" + $(this).val() + "\">");
                });
                $("input[name='" + escapeJQuerySelctor("slaText_" + slaUUID + "_ACTION_" + thisid) + "']").each(function () {
                    tcontent = tcontent + ("<input type=\"hidden\" id=\"slaText_" + slaUUID + "_ACTION_" + thisid + "\" name=\"slaText_" + slaUUID + "_ACTION_" + thisid + "\" value=\"" + $(this).val() + "\">");
                    action_text = $(this).val() ;
                });
                
            
                content = content + "<tr id=\"slaActionRow_" + slaUUID + "_ACTION_" + thisid + "\"><td >" + action_text + "</td><td>";
            
                content = content + tcontent;
                content = content + ("<a href=\"javascript:Delete('slaActionRow_" + slaUUID + "_ACTION_" + thisid + "');\" class=\"btn btn-danger\" >Remove Action</a> <a href=\"javascript:editSlaAction('sla_" + slaUUID + "_ACTION_" +thisid+ "');\" class=\"btn\">Edit Action</a></td></tr>");
            }
            
            //note during this whole activity, all values for all sla related stuff is contained on the main view

            $('#existingActions').append(content);



              $('#mySLAModal').modal();
        }
        
        function Delete(slaUUID) {
            $('#' + slaUUID).remove();
            pulseSaveButton();
        }

        function DeleteSLA(slaUUID) {
            $('#slaRow' + slaUUID).remove();
            $("#existingActions").find("tr:gt(0)").remove();
            pulseSaveButton();
        }
        
        

        /**
         * triggered after a user has added a new sla
         * @returns {undefined}
         */
        function addSLA() {

           
            
            //hide the modal
             $('#mySLAModal').modal('hide');
            var thisurl = '<%=StringEscapeUtils.escapeEcmaScript(pol.getURL())%>';


            //debug
            $('input, select').each(
                    function (index) {
                        var input = $(this);
                        window.console && console.log('Type: ' + input.attr('type') + 'Name: ' + input.attr('name') + 'Value: ' + input.val());
                    }
            );


            //var newid = newUuid();
            //get all inputs with id's that start with plugin_ACTION_

            var rule_classname = $('#rule1').val();
            var rule_text = $("#rule1 option:selected").text();
            var content = "";//<tr><td>" + action_text + "</td><td>";


            //grab the rule data
            $('#SLARuleGenericdiv').find('input').each(function (i, l) {
                //alert(i + ' ' + l);
                var id = $(this).attr("name");
                if (id !== null && id !== undefined && id.indexOf('plugin_RULE_') === 0) {
                    var val = $(this).val();
                    window.console && console.log(val);
                    window.console && console.log(id);
                    id = id.replace("plugin_RULE_", "");
                    window.console && console.log(id);
                    //window.alert (id + ' ' + val);
                    var thisid = "sla_" + newestSLA_id + "_RULE_" + id;
                    window.console && console.log(id + " = " + val);
                    if (id.indexOf("encOnSave_") > -1 || id.indexOf("enc_") > -1){
                        //whoa it's a checkbox, work on jquery magic
                        if ($(this).is(':checked'))
                            val="true";
                        else val = "false";
                    }
                    content = content + ("<input type=\"hidden\" name=\"" + thisid + "\" value=\"" + val + "\">");

                }
            });
            content = content + ("<input type=\"hidden\" name=\"sla_" + newestSLA_id + "_RULE_" + "class" + "\" value=\"" + rule_classname + "\">");

            //grab the action data

            var actiondata = "";
            var newactions = "";
          
      
            $('#existingActions > tbody > tr').each(function (rowindex) {
                //should be on each row, representing every sla action for this rule/action set
                var row = $(this);
                window.console && console.log("processing sla action row " + rowindex + ' ' + row + ' ' + row.attr('id'));
                //find all hiddein input fields, there are the user input for the sla parameters, classnames, etc
                $(this).find('input[type=hidden]').each(function (elementindex) {
                    var item = $(this);
                    window.console && console.log(elementindex+ " appending sla data for " + item.attr('id'));
                    window.console && console.log(elementindex + " appending sla data for " + item);
                    var id = item.attr('id');
                    if (id !== undefined){
                         var val = item.val();
                         
                        if (id.startsWith('slaText_')) {
                            newactions = newactions + "<li>" + (item.val()) + "</li>";
                        } else {
                            actiondata = actiondata + "<input type=\"hidden\" name=\"" + id + "\" value=\"" + val + "\">";
                        }
                    } else {
                        //no id defined
                        window.console && console.log(elementindex + " WARN no id for appending sla data for " + item + ' ' + item.attr('name'));
                    }
                });
            });
            
             //get rid of the old stuff and replace with hte new
            var arrayLength = editableExisitingNames.length;
            for (var i = 0; i < arrayLength; i++) {
                $("input[name=" + (editableExisitingNames[i]) + "]").remove();
            }
            
            DeleteSLA(newestSLA_id);
            editableExisitingNames = [];
            
            
            //remove all sla action rows in the modal except the first one (header)
            $('#existingActions').find("tr:gt(0)").remove();
            
            //append the hidden input types to the page
            //append the ul/li items


            //process content to append to the current SLA menu
            content = "<tr id=\"slaRow" + newestSLA_id + "\"><td> <a class=\"btn\" href=\"javascript:EditSLA('" + newestSLA_id +
                    "');\"><i class=\"icon-edit\"></i> Edit this SLA</a> &nbsp;&nbsp;<a class=\"btn btn-danger\" href=\"javascript:DeleteSLA('" + newestSLA_id +
                    "');\"><i class=\"icon-remove-sign  icon-white\"></i> Remove</a><br><b>Rule:</b>" + rule_text +
                    "<br><b>Actions:</b><br><ul>" + newactions + "</ul>" + content + actiondata + "</td></tr>";

            $('#currentSlas').append(content);
            
            $("#currentSlas").find("tr:gt(0)").each(function(){
                    //since we just added a row, search for any "no sla's defined" rows and nuke them
                    var row = $(this);
                    var id = $(row).attr('id');
                    if (id === null || id === undefined || id.length === 0) {
                        $(row).remove();
                        return;
                    }
                    
                    
            });

            //make the save button larger and more in your face
            pulseSaveButton();

        }

        var editSlaCurrentAction=null;

        function addSLAAction() {
            //@ sourceURL=addSLAAction.js

            if (editSlaCurrentAction!==undefined &&
                    editSlaCurrentAction!==null) {
                //nuke the old row
                var targetRowId = editSlaCurrentAction.replace( "sla_", "slaActionRow_");
                $("#" + targetRowId).remove();
            }
            editSlaCurrentAction=null;
            //sla_{id}_ACTION_{id'}_class
            //sla_{id}_ACTION_{id'}_{param}
            //sla_{id}_ACTION_{id'}_enc_{param}
            //sla_{id}_RULE_class
            //sla_{id}_RULE_{param}=value
            //sla_{id}_RULE_enc_{param}=true/false



            $('#myAddSLA_ActionModal').modal('hide');
            var newid = newUuid();
            //get all inputs with id's that start with plugin_ACTION_

            var action_classname = $('#combo_SLA_ACTION').val();
            var action_text = $("#combo_SLA_ACTION option:selected").text();
            var content = "<tr id=\"slaActionRow_" + newestSLA_id + "_ACTION_" + newid + "\"><td>" + action_text + "</td><td>";

            //$('#existingActions').append("<tr><td>" + action_classname + "</td><td>");
            $('#pluginaction').find('input').each(function (i, l) {
                //alert(i + ' ' + l);
                var id = $(this).attr("name");
                if (id !== null && id !== undefined && id.indexOf('plugin_ACTION_') == 0) {
                    var val = $(this).val();
                    window.console && console.log(val);
                    window.console && console.log(id);
                    id = id.replace("plugin_ACTION_", "");
                    window.console && console.log(id);
                    //window.alert (id + ' ' + val);
                    var thisid = "sla_" + newestSLA_id + "_ACTION_" + newid + "_" + id;
                    window.console && console.log(id + " = " + val);
                    if (id.indexOf("encOnSave_") > -1 || id.indexOf("enc_") > -1){
                        //whoa it's a checkbox, work on jquery magic
                        if ($(this).is(':checked'))
                            val="true";
                        else val = "false";
                    }
                    content = content + ("<input type=\"hidden\" id=\"" + thisid + "\" name=\"" + thisid + "\" value=\"" + val + "\">");

                }
            });
            content = content + ("<input type=\"hidden\"  id=\"sla_" + newestSLA_id + "_ACTION_" + newid + "_" + "classname" + "\" name=\"sla_" + newestSLA_id + "_ACTION_" + newid + "_" + "classname" + "\" value=\"" + action_classname + "\">");
            content = content + ("<input type=\"hidden\" id=\"slaText_" + newestSLA_id + "_ACTION_" + newid + "\" name=\"slaText_" + newestSLA_id + "_ACTION_" + newid + "\" value=\"" + action_text + "\">");

            content = content + ("<a href=\"#\" class=\"btn btn-danger\">Remove Action</a> <a href=\"javascript:editSlaAction('sla_" + newestSLA_id + "_ACTION_" +newid+ "');\" class=\"btn\">Edit Action</a></td></tr>");

            $('#existingActions').append(content);
            //now append them to the page with a common uuid prefix

        }
        
        function editSlaAction(actionid){
            editSlaCurrentAction = actionid;
            //load the modal
            $('#myAddSLA_ActionModal').modal();
            //populate the spinner first, after completion, populate all existing values
            var classname = $("input[name='" + actionid + "_classname']").val();
            $("#combo_SLA_ACTION").val(classname);
            refreshPluginParamsWithCallback('combo_SLA_ACTION', 'pluginaction', 'SLA_ACTION', onActionPopulated, actionid);
            //this should populate the modal with all optional and default settings and
            //transfer control to onActionPopulated
        }
        
        function cancelSlaActionEdit(){
            editSlaCurrentAction=null;
            $('#myAddSLA_ActionModal').modal('hide');
        }
        function onActionPopulated(slaUUID) {
            //ok now populate all the parameters
            
            var editableExisitingNames = [];
             //editing an existing SLA rule/action set
            //when done, cram in our values, starting with the rule data
            $("input[name^='" + slaUUID + "']").each(function () {

                var name = $(this).attr("name");
                var currentValue= $(this).val();
                window.console && console.log('edit sla action, working on named input ' + name  + " " + currentValue);
                editableExisitingNames.push(name);
                var bits = name.split(slaUUID +"_");
                var val = bits[1];
                if (val !== "classname") {
                    //note $this is actually the hidden input type
                    var value = $(this).val();
                    if (val !== "class")
                        $("input[name='plugin_ACTION_" + val + "']").val(value);
                    if (val.startsWith("enc_")){
                        $("#plugin_ACTION_" + val).prop('checked',value);
                    } else if (val.startsWith("encOnSave_")){
                        $("#plugin_ACTION_" + val).prop('checked',value);
                    }
                }
            });



        }
    </script>

    <%
        }
    %>