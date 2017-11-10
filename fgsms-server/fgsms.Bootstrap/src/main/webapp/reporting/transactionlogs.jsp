<%-- 
    Document   : transactionlogs
    Created on : May 18, 2011, 10:09:43 AM
    Author     : Alex
--%> 


<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>

<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
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
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Transaction Log</h1>
</div> 
<div class="row-fluid">
    <div class="span12">

        <table><tr><td>
                    <a href="javascript:restore();">Show Filters</a><br><a href="javascript:runEffect();">Hide Filters</a></td><td>
                    <a class="btn btn-primary"  title="Fetch" value="Fetch" name="Fetch"   onclick="javascript:Go();">Fetch</a>
                </td></tr></table>
        <style type="text/css">
            #effect {width:60%; position: relative;}
        </style>
        <script type="text/javascript" language="javascript">
            function Go()
            {
                //agenttype
                var url = 'reporting/transactionFetcher.jsp?';
                var mon
                var obj = document.getElementById("amount");
                if (obj!=null) mon=obj.value;
                if (mon !=null && mon!="")
                    url+= "records=" + encodeURIComponent( mon);
                else { Alert("Please select the number of records to display");
                    return false;
                }
                obj = document.getElementById("monitorhostname");
                if (obj!=null) mon=obj.value;
                if (mon !=null && mon!="")
                    url+= "&monitorhostname=" + encodeURIComponent( mon);
                obj = document.getElementById("servicehostname");
                if (obj!=null) mon=obj.value;
                if (mon !=null && mon!="")
                    url+= "&servicehostname=" + encodeURIComponent( mon);
                obj = document.getElementById("serviceurl");
                if (obj!=null) mon=obj.value;
                if (mon !=null && mon!="")
                    url+= "&url=" + encodeURIComponent( mon);
                var date1="";
                obj = document.getElementById("todatepicker");
                if (obj!=null) mon=obj.value;
                if (mon !=null && mon!="")
                {
                    url+= "&todate=" + encodeURIComponent( mon);
                    date1=mon;
                }
                var date2="";
                obj = document.getElementById("fromdatepicker");
                if (obj!=null) mon=obj.value;
                if (mon !=null && mon!="")
                {
                    url+= "&fromdate=" + encodeURIComponent( mon);
                    date2=mon;
                }

                if (date1==date2)
                {
                    Alert("Please select dates that differ from each other.");
                    return false;
                }


                obj = document.getElementById("agenttype");
                if (obj!=null) mon=obj.value;
                if (mon !=null && mon!="")
                    url+= "&agent=" + encodeURIComponent( mon);

                var filter=false;
                obj = document.getElementById("FaultedTransactions");
                if (obj!=null)
                {
                    mon=obj.value;
                    //alert(mon);
                    //alert("faults" + obj.checked);
                    if (obj.checked)
                    {
                        filter=true;
                        url+= "&filter=FaultedTransactions";
                    }
                }
                if (!filter)
                {
                    obj = document.getElementById("SLAViolations");
                    if (obj!=null)
                    {
                        mon=obj.value;
                        // alert(mon);
                        //alert("sla" + obj.checked);
                        if (obj.checked)
                        {
                            filter=true;
                            url+= "&filter=SLAViolations";
                        }
                    }
                }


                $("#xlogs").html("<center><img src=\"images/loadingthickboxgreen.gif\" ></center>");
                loadpage(url, "xlogs");
                runEffect();
                return false;
            }
            $(function()
            {
                $("#slider").slider
                ({range: "min",value: 10, min: 1, max: 2000, slide:
                        function (event, ui)
                    {
                        $("#amount").val(ui.value);
                    }
                } );
                $("#amount").val($("#slider").slider("value"));
            });

            function runEffect(){
                var selectedEffect = "fold";
                var options = { };
                $("#effect").hide(selectedEffect, options, 1000);
            }
            function restore()
            {
                var selectedEffect = "fold";
                var options = {};
                $("#effect").show(selectedEffect, options, 500);
                //$("#effect").prop("style",  $("#effect").attr("style") + ";width:60%; height: 300px; background-color: #ccffcc;");

            }


        </script>
        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);




            GetAgentTypesResponseMsg res = null;
            GetDataCollectorListResponseMsg res1 = null;
            GetMonitoredServiceListResponseMsg res2 = null;
            GetServiceHostListResponseMsg res3 = null;
            try {

                DataAccessService dasport = pl.GetDAS(application, request, response);

                GetAgentTypesRequestMsg req = new GetAgentTypesRequestMsg();
                req.setClassification(c);
                res = dasport.getAgentTypes(req);


                GetDataCollectorListRequestMsg req1 = new GetDataCollectorListRequestMsg();
                req1.setClassification(c);
                res1 = dasport.getDataCollectorList(req1);

                GetMonitoredServiceListRequestMsg req2 = new GetMonitoredServiceListRequestMsg();
                req2.setClassification(c);
                res2 = dasport.getMonitoredServiceList(req2);


                GetServiceHostListRequestMsg req3 = new GetServiceHostListRequestMsg();
                req3.setClassification(c);
                res3 = dasport.getServiceHostList(req3);
            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getLocalizedMessage());
            }
        %>
        <div >
            <div class="ui-widget-context ui-corner-all" id="effect" style="background-color: #ccffcc; width:60%;  overflow: auto;" >
                <h3 class="ui-widget-header ui-corner-all" style="text-align: center">Filters</h3>
                <table><tr><td>
                            Agent Type</td><td>
                            <select id="agenttype" name="agenttype"><option></option>
                                <%
                                    if (res != null && !res.getAgentType().isEmpty()) {
                                        for (int i = 0; i < res.getAgentType().size(); i++) {
                                            out.write("<option>" + Utility.encodeHTML(res.getAgentType().get(i)) + "</option>");
                                        }
                                    }
                                %>
                            </select></td></tr>
                    <tr><td>

                            Monitor Hostname (DCS)</td><td>
                            <select name="monitorhostname" id="monitorhostname"><option></option>
                                <%
                                    if (res1 != null && !res1.getHosts().getHostInstanceStats().isEmpty()) {
                                        for (int i = 0; i < res1.getHosts().getHostInstanceStats().size(); i++) {
                                            out.write("<option>" + Utility.encodeHTML(res1.getHosts().getHostInstanceStats().get(i).getHost()) + "</option>");
                                        }
                                    }
                                %>
                            </select></td></tr>
                    <tr><td>
                            Service Hostname</td><td>
                            <select name="servicehostname" id="servicehostname"><option></option>
                                <%
                                    if (res3 != null && !res3.getHosts().getHostInstanceStats().isEmpty()) {
                                        for (int i = 0; i < res3.getHosts().getHostInstanceStats().size(); i++) {
                                            out.write("<option>" + Utility.encodeHTML(res3.getHosts().getHostInstanceStats().get(i).getHost()) + "</option>");
                                        }
                                    }
                                %>
                            </select></td></tr>
                    <tr><td>
                            Service URL</td><td>
                            <select name="serviceurl" id="serviceurl"><option></option>
                                <%
                                    if (res2 != null && !res2.getServiceList().getServiceType().isEmpty()) {
                                        for (int i = 0; i < res2.getServiceList().getServiceType().size(); i++) {
                                            out.write("<option>" + Utility.encodeHTML(res2.getServiceList().getServiceType().get(i).getURL()) + "</option>");
                                        }
                                    }
                                %>
                            </select></td></tr>
                    <tr><td>
                            Date </td><td>

                            From:<input type="text" name="fromdatepicker" style="z-index:1000"  id="fromdatepicker"/><Br>
                            To:<input type="text" name="todatepicker" style="z-index:1000" id="todatepicker" value="<%

                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                out.write(df.format(cal.getTime()));

                                      %>"/>
                        </td></tr>
                    <tr><td>Records to fetch</td>
                        <td>

                            <label for="amount"></label><Br>
                            <input type="text" name="amount" id="amount"/>
                            <div id="slider" style="width:90%"></div>
                        </td></tr>
                    <tr><td>Faults</td><td>
                            <input type="radio" id="AllTransactions" name="filter" checked="true" value="AllTransactions">All Transactions
                            <input type="radio" id="FaultedTransactions" name="filter" value="FaultedTransactions">Show only Faulted Transaction
                            <input type="radio" id="SLAViolations" name="filter"    value="SLAViolations">Show only SLA Violations


                        </td></tr></table>
            </div>
        </div>
        Warning: Depending on your selections, certain queries may take a long time to execute.
        <br>
        <div id="xlogs"></div>

    </div>

</div>
<div id="modelMessageDetails" class="modal fade in" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="false" style="display: block; display:none; ">
    <div class="modal-header">
        <a href="#" class="close" data-dismiss="modal" aria-hidden="true">×</a>
        <h3>Transaction Details</h3>
    </div>
    <div class="modal-body">
        <div id="transactionDetailsDiv"></div>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal">Close</button>
    </div>
</div>
<script type="text/javascript">  
    /**
     * loads a new model, content is loaded from another jsp that fetches a specification transaction log and payloads
     */
    function showModalMessageDetails(messageId)
    {
        $("#modelMessageDetails").modal();
        $("#transactionDetailsDiv").html('Loading...');
        loadpage('reporting/SpecificTransactionLogViewer.jsp?ID=' + messageId, 'transactionDetailsDiv');
    }
    </script>