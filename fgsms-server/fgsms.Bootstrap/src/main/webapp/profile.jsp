<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@include file="csrf.jsp" %>
<div class="well">
     <h1>Service Profiles</h1>
     <p>One stop shop to manage your services.</p>
</div>


<script type="text/javascript">
     $("#currentUrl").resizable();
     $("#currentUrl").css({ 'height': "45px" });

     function LoadPolicy()
     {
          hideAlert();  //main.js
	  destroyCharts();
	  var url = $("#currentUrl").val();
	  if (url == null || url == "")
	       return;
           if (isUrl(url)) {
               $('#loadExternalLink').show();
           } else {
               $('#loadExternalLink').hide();
           }
	  var plswait = '<img src="img/ajax-loader.gif"/>';
	  $("#tab1").html(plswait);
	  //don't uncomment this        $("#tab2").html(plswait);
	  $("#tab3").html(plswait);
	  $("#tab4").html(plswait);
	  $("#tab5").html(plswait);
	  $("#tab6").html(plswait);

	  var debug = getCookie("DEBUG");
	  if (debug != null)
	       alert('loading policy for ' + url);
	  var requestPolicyEditor = $.ajax({
	       url: 'profile/policyEditor.jsp?url=' + encodeURI(url),
	       type: "GET",
	       cache: false
	  });
	  requestPolicyEditor.done(function (msg) {
	       window.console && console.log('loadpage done policyEditor');
	       if (msg.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       $("#tab1").html(msg);
	       $.get('profile/dependencies.jsp?uri=' + encodeURI(url), function (data2) {
		    $("#dependencies").html(data2);
	       });

	  });

	  requestPolicyEditor.fail(function (jqXHR, textStatus) {
	       window.console && console.log('loadpage failed policyEditor');
	       if (textStatus.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       //$("#" + div).html('Error! ' + jqXHR.responseText);
	       //$('#' + div).show();
	  });











	  var requestAvailability = $.ajax({
	       url: 'profile/getAvailability.jsp?url=' + encodeURI(url),
	       type: "GET",
	       cache: false
	  });
	  requestAvailability.done(function (msg) {
	       window.console && console.log('loadpage done getAvailability');
	       if (msg.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       $("#tab3").html(msg);
	       

	  });

	  requestAvailability.fail(function (jqXHR, textStatus) {
	       window.console && console.log('loadpage failed getAvailability');
	       if (textStatus.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       //$("#" + div).html('Error! ' + jqXHR.responseText);
	       //$('#' + div).show();
	  });

	
	 
	 
	 
	 
	 var requestSLAAlerts = $.ajax({
	       url: 'profile/slaalerts.jsp?url=' + encodeURI(url),
	       type: "GET",
	       cache: false
	  });
	  requestSLAAlerts.done(function (msg) {
	       window.console && console.log('loadpage done slaalerts' );
	       if (msg.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       $("#tab4").html(msg);
	       

	  });

	  requestSLAAlerts.fail(function (jqXHR, textStatus) {
	       window.console && console.log('loadpage failed slaalerts');
	       if (textStatus.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       //$("#" + div).html('Error! ' + jqXHR.responseText);
	       //$('#' + div).show();
	  });
	  
	
	



	  var requestPermissions = $.ajax({
	       url: 'profile/permission.jsp?url=' + encodeURI(url),
	       type: "GET",
	       cache: false
	  });
	  requestPermissions.done(function (msg) {
	       window.console && console.log('loadpage done permission');
	       if (msg.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       $("#tab6").html(msg);
	       

	  });

	  requestPermissions.fail(function (jqXHR, textStatus) {
	       window.console && console.log('loadpage failed permission');
	       if (textStatus.indexOf("Please Login") > -1)
	       {
		    window.console && console.log('autologout 5');
		    window.location.href = "index.jsp";
		    return;
	       }
	       //$("#" + div).html('Error! ' + jqXHR.responseText);
	       //$('#' + div).show();
	  });
	  

	 
	  //this gets web service transactions and builds the charts

	  addData3(url, 'chartspace', 'tab5');
     }
</script>
<div class="row-fluid">
     <div class="span12">
	  <select onchange="javascript:LoadPolicy();" id="currentUrl" style="width:450px; height:  30px !important;">
	       <%                 SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
		    if (c == null) {
			 Logger.getLogger("FGSMSWeb").log(Level.INFO, "redirecting to the index page, security wrapper is null");
			 response.sendRedirect("index.jsp");
		    }
		    ProxyLoader pl = ProxyLoader.getInstance(application);

		    DataAccessService dasport = pl.GetDAS(application, request, response);
		    GetMonitoredServiceListRequestMsg req = new GetMonitoredServiceListRequestMsg();
		    req.setClassification(c);

		    GetMonitoredServiceListResponseMsg res = dasport.getMonitoredServiceList(req);
		    if (res != null && res.getServiceList() != null && res.getServiceList() != null) {
			 for (int i = 0; i < res.getServiceList().getServiceType().size(); i++) {
			      out.write("<option value=\"" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"");
			      if (request.getParameter("url") != null) {
				   if (request.getParameter("url").equals(res.getServiceList().getServiceType().get(i).getURL())) {
					out.write(" selected=selected ");
				   }
			      }
			      out.write(">" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "</option>");
			 }
		    }

	       %>


	  </select>
               <a class="btn btn-large" href="javascript:LoadPolicy();"><i class="icon-refresh large"></i>Refresh</a>
               <a id="loadExternalLink" class="btn btn-large" href="javascript:popout();"><i class="icon-share-alt large"></i>New Window</a>

     </div>

</div>
<script type="text/javascript">
     LoadPolicy();
     
     function isUrl(url){
         if (url === undefined) return false;
         var temp=url.toLowerCase();
         if (temp.startsWith('http://') || temp.startsWith('https://')) {
             return true;
         }
         return false;
     }
     function popout(){
         var url = $("#currentUrl").val();
         if (isUrl(url)){
             window.open(url, "_blank");
         }
     }
</script>
<div class="row-fluid">
     <div class="span12" >
	  <div class="tabbable"> <!-- Only required for left/right tabs -->
	       <ul class="nav nav-tabs">
		    <li class="active"><a href="#tab1" data-toggle="tab">Policy</a></li>
		    <li><a href="#tab2" data-toggle="tab">Performance</a></li>
		    <li><a href="#tab3" data-toggle="tab">Availability</a></li>
		    <li><a href="#tab4" data-toggle="tab">Alerts</a></li>
		    <li><a href="#tab5" data-toggle="tab" >Logs</a></li>
		    <li><a href="#tab6" data-toggle="tab" >Permissions</a></li>
	       </ul>
	       <div class="tab-content">
		    <div class="tab-pane active" id="tab1">
			 <p>Select a service URL above</p>
		    </div>
		    <div class="tab-pane" id="tab2">
			 <div id="chartspace"  style="width:100%; min-height: 400px"></div>
		    </div>
		    <div class="tab-pane" id="tab3">
			 <p>Select a service URL above</p>
		    </div>

		    <div class="tab-pane" id="tab4">
			 <p>Select a service URL above</p>
		    </div>
		    <div class="tab-pane" id="tab5">
			 <p>Select a service URL above</p>
		    </div>
		    <div class="tab-pane" id="tab6">
			 <p>Select a service URL above</p>
		    </div>
	       </div>
	  </div>




     </div>

     <div class="row-fluid">

     </div>
</div>

<%@include  file="reporting/transactionModel.jsp" %>
