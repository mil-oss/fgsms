<%@include file="csrf.jsp" %>

<% String parent = "admin.jsp";%>
<div class="well">
    <h1>Administration</h1>
    <p>Your one stop shop for administering this instance of FGSMS.</p>
</div>
<div class="row-fluid">
    <div class="span4">
        <h2>Register a Service</h2>
        <p>Manually register a web accessible service and start monitoring it</p>
        <p><a class="btn" href="javascript:loadpage('addservice.jsp','mainpane', '<%=parent%>');">Go &raquo;</a></p>
    </div><!--/span-->


    <div class="span4">
        <h2>Global Policies</h2>
        <p>Global policies define how certain types of agents behave and serve as general agent performance tuning parameters.</p>
        <p><a class="btn" href="javascript:loadpage('admin/globalPolicies.jsp','mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>General Settings</h2>
        <p>General Settings is a collection of configuration parameters used for many of the FGSMS components and some of the agents such as Qpid, SMX, and HornetQ.</p>
        <p><a class="btn" href="javascript:loadpage('admin/globalSettings.jsp','mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->

    <!--/span-->
</div><!--/row-->
<div class="row-fluid">
    <div class="span4">
        <h2>Email Settings</h2>
        <p>Email settings controls how FGSMS sends email, such as the location of your mail server and credentials.</p>
        <p><a class="btn" href="javascript:loadpage('admin/mailSettings.jsp','mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div>

    <div class="span4">
        <h2>Connections</h2>
        <p>Control this instance of FGSMS's management web site (WebGUI)'s connections to the FGSMS web services and federation services (UDDI and FDS).</p>
        <p><a class="btn" href="javascript:loadpage('admin/index.jsp','mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div>
    <div class="span4">
        <h2>Administrators</h2>
        <p>FGSMS has the ability to track the performance of web services, message brokers and even machines and system services. </p>
        <p><a class="btn" href="javascript:loadpage('admin/siteAdministration.jsp', 'mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->

</div><!--/row-->

<div class="row-fluid">
    <div class="span4">
        <h2>Agents</h2>
        <p>FGSMS's agents must authenticate to the FGSMS server. This page lets you control which accounts are authorized for use as agents.</p>
        <p><a class="btn" href="javascript:loadpage('admin/siteAgents.jsp', 'mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->

    <div class="span4">
        <h2>Security Level</h2>
        <p>This page lets you adjust the Security Classification and Caveats of this instance of FGSMS, including all agents.</p>
        <p><a class="btn" href="javascript:loadpage('admin/securityLevel.jsp', 'mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Auditing</h2>
        <p>Access Logs to all information within FGSMS is recorded in an audit log. This page will let you browse through the most recent logs. </p>
        <p><a class="btn" href="javascript:loadpage('admin/audit.jsp', 'mainpane', '<%=parent%>');">Go &raquo;</a></p>
    </div><!--/span-->

</div><!--/row-->



<div class="row-fluid">
    <div class="span4">
        <h2>Stale Status Records</h2>
        <p>Sometimes the status bar will be left with information on services that are no longer monitored or may have even been deleted. Use this page to remove them or other services in bulk.</p>
        <p><a class="btn" href="javascript:loadpage('admin/removeStaleRecords.jsp', 'mainpane', '<%=parent%>');">Go &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Data Collectors</h2>
        <p>This page provides general statistics such as invocation counts on a per Data Collector Service for web services. This is useful for confirming local balancing between instances of FGSMS's DCS Web Service.</p>
        <p><a class="btn" href="javascript:loadpage('admin/dataCollectors.jsp', 'mainpane', '<%=parent%>');">View &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Service Hosts</h2>
        <p>This page provides general statistics such as invocation counts on a per host basis for web services.</p>
        <p><a class="btn" href="javascript:loadpage('admin/serviceHosts.jsp', 'mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->


</div><!--/row-->
<div class="row-fluid">
    <div class="span4">
        <h2>Agents Types</h2>
        <p>This provides a handy list of all web service agents that have reported back to FGSMS successfully.</p>
        <p><a class="btn" href="javascript:loadpage('admin/agents.jsp', 'mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Operational Status</h2>
        <p>This provides useful debugging information, operational status and version information for FGSMS's web services.</p>
        <p><a class="btn" href="javascript:loadpage('admin/opstat.jsp', 'mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->

    <div class="span4">
        <h2>Plugin Management</h2>
        <p>This provides the ability to register and unregister plugins.</p>
        <p><a class="btn" href="javascript:loadpage('admin/plugin.jsp', 'mainpane', '<%=parent%>');">Configure &raquo;</a></p>
    </div><!--/span-->

</div>
    
    
    <div class="row-fluid">
    <div class="span4">
        <h2>Bulk Delete</h2>
        <p>Enables you to bulk delete service policies and their associated data.</p>
        <p><a class="btn" href="javascript:loadpage('admin/bulk.jsp', 'mainpane', '<%=parent%>');">Go &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
       
    </div><!--/span-->

    <div class="span4">
       
    </div><!--/span-->

</div>