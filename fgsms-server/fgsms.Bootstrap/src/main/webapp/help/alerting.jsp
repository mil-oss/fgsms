<%@include file="../csrf.jsp" %>
<% String parent = "help/alerting.jsp";  %>
<div class="well">
    <h1>Alerting</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <p>FGSMS can send alerts through a number of different publish and subscribe mechanisms out of the box. These include 
            
            <a href="javascript:loadpage('help/amqp.jsp','mainpane',  '<%=parent %>');">AMQP Qpid/MRG</a>,
            <a href="javascript:loadpage('help/wsn.jsp','mainpane',  '<%=parent %>');">WS-Notification</a>, 
            <a href="javascript:loadpage('help/hornetq.jsp','mainpane', '<%=parent %>');">HornetQ/JMS</a>, 
            <a href="javascript:loadpage('help/email.jsp','mainpane', '<%=parent %>');">Email</a>, 
            <a href="javascript:loadpage('help/loggers.jsp','mainpane', '<%=parent %>');">Loggers</a>, and 
            <a href="javascript:loadpage('help/sdk.jsp','mainpane', '<%=parent %>');">Plugins</a>.
            This can also be expanded using the <a href="javascript:loadpage('help/sdk.jsp','mainpane',  '<%=parent %>');">SDK</a>.
            The real question is, what do these messages look like? All of the above mechanisms are typically machine to machine communications, which means that people, like you and me, 
            may have a hard type trying to read it. These messages are sent in XML
        </p>
        <h2 class="well-small">Alerting Formats</h2>
        <p>FGSMS uses <a href="http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=wsdm" target="_blank">WSDM</a> as an alerting mechanism. Click here for a <a href="help/sampleAlert.xml" target="_blank">sample message</a>.
        </p>
    </div><!--/span-->
</div><!--/row-->
