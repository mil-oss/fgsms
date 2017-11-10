<%@include file="../csrf.jsp" %>
<%String parent = "help/bueller.jsp";%>
<div class="well">
    <h1>Status Bueller</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <p>
            Status Bueller's job is to periodically poll web services and web sites for availability information. By default, it is enabled for all web service endpoints, but can be disabled entirely or on a per service basis.
            Bueller only connects to <a href="javascript:loadpage('help/policies.jsp','mainpane', '<%=parent %>');">service policies</a> that are of type Transactional (i.e. Web Service). Bueller can be configured on the Administration > General Settings page.
            Bueller currently supports a variety of HTTP based authentication mechanisms.
            
        </p>
    </div><!--/span-->
</div><!--/row-->
