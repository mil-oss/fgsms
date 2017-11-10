<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Process Policy - Also Known As</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        This setting is used for Process Policies which are monitored by Operating
        System agents. The OS Agent is designed to run on any operating system, however
        in some cases, it needs some additional information in order to identify
        all instances of a given service.<br><br>

        When monitoring a specific process on a computer, specifically on Windows operating systems,
        a 'Windows Service' might be called something like 'Postgres Database', however
        the executable name might be 'postgres.exe'. This is what the AKA field is for. 
        In this case, enter 'postgres.exe' into the AKA field. This will enable the agent
        to correctly monitor all known processes for a given 'service'.

    </div><!--/span-->
</div><!--/row-->
