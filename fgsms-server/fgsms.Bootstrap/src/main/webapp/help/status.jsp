<%@include file="../csrf.jsp" %>
<%String parent = "help/status.jsp";%>
<div class="well">
    <h1>Status Information</h1>
    <p>How does FGSMS get status and performance information?</p>
</div>
<div class="row-fluid">
    <div class="span12">
        <table>
            <tr><th>Component</th><th>How do we get Status data?</th><th>How do we get Performance data?</th></tr>
            <tr><td>Web Services (also proxy)	</td><td>From the FGSMS server component, <a href="javascript:loadpage('help/bueller.jsp','mainpane', '<%=parent %>');">Bueller</a>. Retrieves the WSDL. OR via  <a href="javascript:loadpage('help/healthstatuscheck.jsp','mainpane', '<%=parent %>');">Health Status Check API</a>	</td><td>From embedded agents, reported on a per transaction basis, asynchronously</td></tr>
            <tr><td>Operating Systems/Process	</td><td colspan="2">From system service (agent) on each machine</td></tr>
            <tr><td>Statistical - Apache Qpid C++/Redhat MRG	</td><td colspan="2">From embedded agents</td></tr>
            <tr><td>Statistical - Apache Qpid Java	</td><td colspan="2">From the FGSMS server component QpidJMX</td></tr>
            <tr><td>Statistical - Apache ActiveMQ	</td><td colspan="2">From the FGSMS server component SMXJMX</td></tr>
            <tr><td>Statistical - Apache ServiceMix	</td><td colspan="2">From the FGSMS server component SMXJMX</td></tr>
            <tr><td>Statistical - Jboss HornetQ	</td><td colspan="2">From the FGSMS server component Hornet JMX</td></tr>
            <tr><td>Status Only External       </td><td>Periodic calls to the Status Service</td><td>N/A</td></tr>
            <tr><td>Status Only Interal       </td><td>Periodic updates to the database using FGSMS's AuxHelper</td><td>N/A</td></tr>
        </table>
    </div><!--/span-->
</div><!--/row-->
