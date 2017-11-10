<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Data</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <h2>How long does FGSMS's save data for?</h2>
        <p>As long as you need to save it. DoD Regulations state that auditing information should be kept for 1 year for non-SAMI data and for 7 years for SAMI data. Having years and years of
            data sitting in FGSMS's database not only makes things slow, but it puts an additional burden on your database server. Most data collected by FGSMS can be exported to spreadsheets or via database dumps.
            Therefore, how long you keep the data is up to you. Each service can have defined a period of time to keep data around the database, otherwise it is deleted periodically.<Br>
            Manage >  General > Data Retention Time<br>
            The default settings for new services can also be defined in the Administration > General Settings page.
        </p>
    </div><!--/span-->
</div><!--/row-->
