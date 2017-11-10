
<%@include file="csrf.jsp" %>
<div class="well">
    <h1>Data</h1>
    <p>Get access to record data, generate reports and export the data that you need.</p>
    <p><a class="btn btn-primary btn-large" href="javascript:loadpage('help/data.jsp','mainpane','data.jsp');">Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span4">
        <h2>Web Service Transaction Log Search</h2>
        <p>Looking for a specific web service transaction? This page will help you narrow down the results.</p>
        <p><a class="btn" href="javascript:loadpage('reporting/transactionlogs.jsp','mainpane', 'data.jsp');">View details &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Reporting</h2>
        <p>Generate stylized web browser viewable reports</p>
        <p><a class="btn" href="javascript:loadpage('reporting/reportGenerator.jsp','mainpane', 'data.jsp');">View details &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Export</h2>
        <p>Export whatever data you want to a Comma Separated Value file.</p>
        <p><a class="btn" href="javascript:loadpage('reporting/dataExport.jsp','mainpane', 'data.jsp');">View details &raquo;</a></p>
    </div>
    
    <!--/span-->
</div><!--/row-->
 

<div class="row-fluid">
    <div class="span4">
        <h2>Transaction Logs</h2>
        <p>Looking for a specific web service transaction? This page will give you the data, provided you know the transaction id number.</p>
        <p><a class="btn" href="javascript:$('#myModalTransactionSearch').modal();">View details &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Schedule Reporting</h2>
        <p>Scheduled CSV for HTML Web Reports to occur in the future or to repeat on a specific interval.</p>
        <p><a class="btn" href="javascript:loadpage('reporting/scheduledReportsMain.jsp', 'mainpane', 'data.jsp');">View details &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Web Service Access</h2>
        <p>If the other features of the FGSMS Web GUI don't scratch your itch for data, you can use one of the FGSMS web services to get access to everything.</p>
        <p><a class="btn" href="javascript:loadpage('help/services.jsp','mainpane', 'data.jsp');">View details &raquo;</a></p>
    </div>
    
    <!--/span-->
</div><!--/row-->
<%@include  file="reporting/transactionSearchModel.jsp" %>