<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Schedule Reporting</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        FGSMS can generate reports either on demand (via Data Export or the Report Generator) or on a schedule, such as daily, weekly or monthly.

        Choosing the right reporting mechanism is important as using the on demand generator with large data sets can take a significant amount of time to generate the report. This not 
        only causes additional stain on the Jboss server, it may result in browser time outs or running out of memory.
        <p class="well-small">
            The standard, on demand report generator is sufficient for smaller report requests. <br>The scheduled report generator should be used for larger requests.
        </p>
        Once a scheduled report has been generated, an email is automatically dispatched to the requestor's email address. Optionally, 
        <a href="javascript:loadpage('help/alerts.jsp','mainpane','help/scheduledreports.jsp');">alerts</a> can be sent to HornetQ, AMQP, WS-Notification
        WSN, a plugin, a logger, or you can    <a href="javascript:loadpage('help/permissions.jsp','mainpane','help/scheduledreports.jsp');">Run a Script</a>.

    </div><!--/span-->
</div><!--/row-->
