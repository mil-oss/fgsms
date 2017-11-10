<%@include file="../csrf.jsp" %>

<% String parent = "federation.jsp&help/uddi.jsp";  %>
<div class="well">
    <h1>UDDI</h1>
    <p>The standardized web service discovery service</p>
    <p><a class="btn btn-primary btn-large">Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span12">
        <h2>Discovery</h2>
        <p>The Universal Discovery Description and Integration service provides a virtual phone book for services. 
            FGSMS can advertise performance and availability metrics of monitored services using UDDI's technical model (tModel) definitions. 
            FGSMS's agents can also use UDDI for discovering the location of the FGSMS server.</p>
        <p><a class="btn" href="javascript:loadpage('help/agentconfig.jsp','mainpane', '<%=parent %>');">Learn More About Agent Discovery&raquo;</a> 
            <a class="btn" href="javascript:loadpage('federation/uddibrowser.jsp','mainpane', '<%=parent %>');">View the UDDI Browser &raquo;</a>
            <a class="btn" href="javascript:loadpage('federation/uddibrowser.jsp','mainpane', '<%=parent %>');">Learn More About Metrics Publication &raquo;</a>
        </p>
    </div><!--/span-->
</div><!--/row-->

<div class="row-fluid">
    <div class="span12">
        <h2>Publishing Endpoints </h2>
        <p>FGSMS can publish web service endpoints into a UDDI v3 server. This will make these endpoints discoverable by other users using the UDDI specification.<br><br>
            In order to do so, a system administrator for this instance of FGSMS must configure the user interface (this web site) with the necessary information.
            To publish web services that FGSMS know's about into UDDI, use the <a href="javascript:loadpage('federation/endpointPublisher.jsp', 'mainpane', '<%=parent %>');">Endpoint Publisher</a>.
        
        </p>
        
    </div><!--/span-->
</div>
