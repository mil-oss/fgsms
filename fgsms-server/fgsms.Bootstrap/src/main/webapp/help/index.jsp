<%@include file="../csrf.jsp" %>
<% String parent = "help/index.jsp";  %>
<div class="well">
    <h1>Help!</h1>
    <a href="javascript:open_in_new_tab();" class="btn btn-primary">Load Help in a Separate Tab</a>
</div>
<script type="text/javascript">
    function open_in_new_tab()
{
  window.open('helploader.jsp', '_blank');
  window.focus();
}
</script>
<div class="row-fluid">
    <div class="span12">
        <h2>Help Topics</h2>
        <a href="javascript:loadpage('help/agent.jsp','mainpane', '<%=parent %>');">What's an agent?</a><br>
        <a href="javascript:loadpage('help/policies.jsp','mainpane', '<%=parent %>');">What's a service policy?</a><br>
        <a href="javascript:loadpage('help/sla.jsp','mainpane', '<%=parent %>');">What are Service Level Agreements/Alerts?</a><br>
        
        <a href="javascript:loadpage('help/services.jsp','mainpane', '<%=parent %>');">Accessing FGSMS's Services</a><br>
        <a href="javascript:loadpage('help/uddi.jsp','mainpane', '<%=parent %>');">UDDI Integration</a><br>
        
        <a href="javascript:loadpage('help/agentconfig.jsp','mainpane', '<%=parent %>');">Configuring Embedded Agents</a><br>
        <a href="javascript:loadpage('help/versions.jsp','mainpane', '<%=parent %>');">About FGSMS's version numbering</a><br>
        <a href="javascript:loadpage('help/sdk.jsp','mainpane', '<%=parent %>');">FGSMS's SDK</a><br>
        <a href="javascript:loadpage('help/permissions.jsp','mainpane', '<%=parent %>');">FGSMS's Permission Structure</a><br>
        <a href="javascript:loadpage('help/status.jsp','mainpane', '<%=parent %>');">How does FGSMS get status and performance information?</a><br>
        <a href="javascript:loadpage('help/bueller.jsp','mainpane', '<%=parent %>');">What is the Status Bueller?</a><br>
        <a href="javascript:loadpage('help/alerting.jsp','mainpane', '<%=parent %>');">What format are non-email alerts sent in?</a><br>
        <a href="javascript:loadpage('help/amqp.jsp','mainpane', '<%=parent %>');">How to Monitor Apache Qpid or Redhat MRG?</a><br>
        <a href="javascript:loadpage('help/hornetq.jsp','mainpane', '<%=parent %>');">How to Monitor Jboss HornetQ?</a><br>
        <a href="javascript:loadpage('help/smx.jsp','mainpane', '<%=parent %>');">How to Monitor Apache Service Mix and Active MQ?</a><br>
        
        <a href="javascript:loadpage('help/mrg.jsp','mainpane', '<%=parent %>');">How to Monitor Redhat MRG?</a><br>
        
        
        <a href="javascript:loadpage('help/wsn.jsp','mainpane', '<%=parent %>');">WS-Notification Alerts</a><br>
        <a href="javascript:loadpage('help/amqp.jsp','mainpane', '<%=parent %>');">AMQP Qpid Alerts</a><br>
        <a href="javascript:loadpage('help/email.jsp','mainpane', '<%=parent %>');">Email Alerts</a><br>
        <a href="javascript:loadpage('help/uddi.jsp','mainpane', '<%=parent %>');">UDDI Publishing</a><br>
        <a href="javascript:loadpage('help/data.jsp','mainpane', '<%=parent %>');">How long does FGSMS store data for?</a><br>
        <a href="javascript:loadpage('help/scheduledreports.jsp','mainpane', '<%=parent %>');">All about Scheduled Reporting</a><br>
        <a href="https://github.com/mil-oss/fgsms/issues/">Report a bug</a><br>
        
    </div>
</div>