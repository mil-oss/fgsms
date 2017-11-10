
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Reporting</h1>
    <p>Your one stop shop for getting data out of FGSMS.</p>
    <p><a class="btn btn-primary btn-large">Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span4">
        <h2>Web Reporting</h2>
        <p>Global policies define how certain types of agents behave and serve as general agent performance tuning parameters.</p>
        <p><a class="btn" href="javascript:loadpage('admin/globalPolicies.jsp','mainpane');">Configure &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>CSV Data Export</h2>
        <p>General Settings is a collection of configuration parameters used for many of the FGSMS components and some of the agents.</p>
        <p><a class="btn" href="javascript:loadpage('admin/globalSettings.jsp','mainpane');">Configure &raquo;</a></p>
    </div><!--/span-->
    <div class="span4">
        <h2>Scheduled Reports</h2>
        <p>Email settings controls how FGSMS sends email, such as the location of your mail server and credentials.</p>
        <p><a class="btn" href="javascript:loadpage('admin/mailSettings.jsp','mainpane');">Configure &raquo;</a></p>
    </div>


    
</div><!--/row-->