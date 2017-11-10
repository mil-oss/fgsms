<%@include file="csrf.jsp" %>
<div class="well">
    <h1>Infrastructure</h1>
    <p>Browse through and manage your infrastructure.</p>
    </div>
<div class="row-fluid">
    <div class="span4">
        <h2>Message Brokers</h2>
        <p>Health Status and Monitoring for Message Brokers, such as Qpid, MRG, ActiveMQ or HornetQ</p>
        <p><a class="btn" href="javascript:loadpage('brokers/messageBrokers.jsp','mainpane', 'infrastructure.jsp');">View details &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Domains and Servers</h2>
        <p>Browse through your monitored machines to see what running or to start monitoring specific processes.</p>
        <p><a class="btn" href="javascript:loadpage('os/domains.jsp','mainpane', 'infrastructure.jsp');">View details &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Monitored Services</h2>
        <p>Browse through your monitored services and processes to see what running or to start monitoring specific processes.</p>
        <p><a class="btn" href="javascript:loadpage('status.jsp','mainpane', 'home.jsp');">View details &raquo;</a></p>
    </div><!--/span-->
    <!--/span-->
</div><!--/row-->

