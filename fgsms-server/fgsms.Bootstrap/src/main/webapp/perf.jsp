<%@page import="org.miloss.fgsms.services.interfaces.common.PolicyType"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@include file="csrf.jsp" %>
<div class="well">
    <h1>Performance</h1>
    <p>This is the most up to date performance information available for all of the services from which you have access..</p>
    <p><a class="btn btn-primary btn-large" href="javascript:loadpage('help/status.jsp','mainpane');">Learn more &raquo;</a></p>
    <!-- help page to status -->
</div>

<div class="row-fluid">
    <div class="span3">


        <a href="javascript:updatechart('transactions','availability','counters','resource','iochart');" class="btn btn-primary">Refresh</a> 
        <a href="javascript:togglePerfAutoRefresh();" class="btn btn-primary" id="PerfAutoRefresh">Auto Refresh Disabled</a>

        <% //<a href="javascript:loadpage('profile.jsp','mainpane');">go</a>
        %>
        <div class="tabbable"> <!-- Only required for left/right tabs -->
            <ul class="nav nav-tabs">
                <li class="active"><a href="#tab1" data-toggle="tab">Transactional</a></li>
                <li><a href="#tab2" data-toggle="tab">Statistical</a></li>
                <li><a href="#tab3" data-toggle="tab">M/P</a></li>
                <li><a href="#tab4" data-toggle="tab" >Status</a></li>
            </ul>
            <div class="tab-content">


                <%
                    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
                    if (c == null) {
                        LogHelper.getLog().log(Level.INFO, "redirecting to the index page, security wrapper is null");
                        response.sendRedirect("index.jsp");
                    } 
                    ProxyLoader pl = ProxyLoader.getInstance(application);

                    DataAccessService dasport = pl.GetDAS(application, request, response);
                    GetMonitoredServiceListRequestMsg req = new GetMonitoredServiceListRequestMsg();
                    req.setClassification(c);

                    GetMonitoredServiceListResponseMsg res = dasport.getMonitoredServiceList(req);
                    if (res != null && res.getServiceList() != null && res.getServiceList() != null) {
                %>

                <div class="tab-pane active" id="tab1">
                    <%
                        for (int i = 0; i < res.getServiceList().getServiceType().size(); i++) {
                            if (res.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.TRANSACTIONAL) {
                                out.write("P:<input type=checkbox value=\"wsperf:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/>");
                                out.write("A:<input type=checkbox value=\"avail:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/>");
                                out.write(Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "<br>");
                            }
                        }
                    %>
                </div>
                <div class="tab-pane" id="tab2">
                    <%
                        for (int i = 0; i < res.getServiceList().getServiceType().size(); i++) {
                            if (res.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.STATISTICAL) {
                                out.write("P:<input type=checkbox value=\"statperf:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/>");
                                out.write("A:<input type=checkbox value=\"avail:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/>");
                                out.write(Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "<br>");
                            }
                        }
                    %>
                </div>
                <div class="tab-pane" id="tab3">
                    <%
                        for (int i = 0; i < res.getServiceList().getServiceType().size(); i++) {
                            if (res.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.MACHINE) {
                                out.write("P:<input type=checkbox value=\"mpperf:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "|" + res.getServiceList().getServiceType().get(i).getHostname() + "\"/>");
                                out.write("A:<input type=checkbox value=\"avail:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/>");
                                out.write(Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "<br>");
                            }
                            if (res.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.PROCESS) {
                                out.write("P:<input type=checkbox value=\"ppperf:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "|" + res.getServiceList().getServiceType().get(i).getHostname() + "\"/>");
                                out.write("A:<input type=checkbox value=\"avail:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/>");
                                out.write(Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "<br>");
                            }
                        }
                    %>
                </div>
                <div class="tab-pane" id="tab4">
                    <%
                        for (int i = 0; i < res.getServiceList().getServiceType().size(); i++) {
                            if (res.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.STATUS) {
                                out.write("A:<input type=checkbox value=\"avail:" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/>");
                                out.write(Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "<br>");
                            }
                        }
                    %>
                </div>
                <%
                    }

                %>




            </div>
        </div>

    </div>
    <div class="span9" id="transactions" style="height: 400px">
    </div>
    <div class="span9" id="availability" style="height: 400px">
    </div>
    <div class="span9" id="counters" style="height: 400px">
    </div>
    <div class="span9" id="resource" style="height: 400px">
    </div>
    <div class="span9" id="iochart" style="height: 400px">
    </div>

    <script type="text/javascript">
        $("#chart").resizable();
        var chartupdater=null;
                
        function togglePerfAutoRefresh()
        {
            if (chartupdater==null || chartupdater == undefined)
            {
                startChartUpdate();
                $("#PerfAutoRefresh").html("Auto Refresh Enabled");
            }
            else{
                stopChartUpdate();
                $("#PerfAutoRefresh").html("Auto Refresh Disabled");
            }
        }
        function startChartUpdate()
        {
            if (chartupdater==null || chartupdater == undefined)
            {
                chartupdater = setTimeout(updatechart, 30000,'transactions','availability','counters','resource','iochart');
                console.log('setting auto refresh performance chart');
            }
        }
        function stopChartUpdate()
        {
            clearInterval(chartupdater);
            console.log('unsetting auto refresh performance chart');
        }
    </script>
    <%
        //<a href="javascript:stopChartUpdate();" class="btn btn-primary">Stop Timeout</a>
%>
    <%@include  file="reporting/transactionModel.jsp" %>
</div>