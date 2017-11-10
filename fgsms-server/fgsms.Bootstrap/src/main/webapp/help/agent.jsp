<%@include file="../csrf.jsp" %>
<%String parent = "help/agent.jsp";%>
<div class="well">
    <h1>Service Policies</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        FGSMS ships with and supports 15 different agents which fall under just a few categories. In addition, you can write your own using the <a href="javascript:loadpage('help/sdk.jsp','mainpane', '<%=parent %>');">SDK</a>.
        <ul>
            <li>Status - Provides Up/Down status information periodically</li>
            <li>Statistical - Provides Broker provides data and Up/Down status information periodically</li>
            <li>Web Service Transactional - Provides data on a per transaction basis. Up/Down status is provided by FGSMS's  <a href="javascript:loadpage('help/bueller.jsp','mainpane', '<%=parent %>');">Bueller</a> component</li>
            <li>Operating System - Provides machine and process level monitoring for Windows, Linux and many others.</li>
        </ul>
    </div><!--/span-->
</div><!--/row-->
