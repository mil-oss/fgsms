<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
     SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
     if (c == null) {
          LogHelper.getLog().log(Level.INFO, "redirecting to the index page, security wrapper is null");
          response.sendRedirect("index.jsp");
          return;
     }


%>
<html lang="en">
     <head>
	  <meta charset="utf-8">
	       <title>FGSMS</title>
	       <meta name="viewport" content="width=device-width, initial-scale=1.0">
		    <meta name="description" content="FGSMS"/>
		    <meta name="author" content="">
			 <link href="css/bootstrap.css" rel="stylesheet"></link>
			 <style type="text/css">
			      body {
				   padding-top: 0px;
				   padding-bottom: 40px;
			      }
			      .sidebar-nav {
				   padding: 9px 0;
			      }
			 </style>
			 <link href="css/bootstrap-responsive.css" rel="stylesheet"></link>
			 <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
			 <!--[if lt IE 9]>
			 <script src="js/html5.js"></script>
			 <![endif]-->

			 <!-- Le fav and touch icons -->
			 <link rel="icon" type="image/gif" href="favicon.ico"></link>


			 <link href="css/jquery-ui.css" rel="stylesheet"></link>
			 <link rel="stylesheet" type="text/css" href="css/main.css"/>
			 <!-- style sheet for the left hand side status bar -->
			 <style type="text/css">

			      .statusheader{
				   color:black;
				   background-color: whitesmoke;
			      }

			      .good{
				   color:white;
				   background-color: green;
			      }
			      .bad{
				   color:black;
				   background-color:red;
			      }
			      .warn{
				   color:black;
				   background-color:orange;
			      }
			      .stale{
				   color:white;

				   background-color:grey;
			      }

			      #menu_container
			      {
				   z-index: 1000;
				   position:fixed;
				   top: 50px;
				   left: 4px;
			      }

			      #menu {
				   display: none;
				   color:white;
				   /* background-color:black;*/
			      }
			      #min_menu
			      {
				   width:25px;
			      }

			 </style>
			 <script src="js/jquery-1.11.3.min.js" type="text/javascript"></script>
			 <script src="js/bootstrap.js" type="text/javascript"></script>
			 <script type="text/javascript">
			      //http://stackoverflow.com/questions/13649459/twitter-bootstrap-multiple-modal-error
			      //fix for nested modals and bootstrap 2
			      $.fn.modal.Constructor.prototype.enforceFocus = function () {};
			 </script>
			 <script src="js/highstocks/highstock.js"></script>
			 <script src="js/highcharts/modules/exporting.js"></script>
			 <script src="js/jquery-ui-1.11.4.min.js"></script>
			 <script src="js/charting.js" type="text/javascript" ></script>
			 <script src="js/jquery.history.js" type="text/javascript" ></script>
			 <script src="js/main.js" type="text/javascript"></script>
			 <script src="js/breadcrumbs.js" type="text/javascript"></script>
			 <script src="js/jquery.history.js" type="text/javascript"></script>


			 <script type="text/javascript">
			      
			      
			      // Bind to StateChange Event
			      History.Adapter.bind(window,'statechange',function(){ // Note: We are using statechange instead of popstate
				      var State = History.getState(); // Note: We are using History.getState() instead of event.state
				      window.console && window.console.log(State);
			      });
                              

			      //this var helps reduce the jitters when moving the mouse quickly over the menu
			      var isAnimating = false;
			      var isAnimating2 = false;
			      var menuactive = false;
			      function loadpagemenu()
			      {
				   var debug = getCookie("DEBUG");
				   var link = 'statusbarfull2';
				   var div = 'menu_container'
				   if (menuactive)
				   {
					if (debug != null)
					     alert('menu is active, skipping refresh screen height is ' + getScreenHeight());
					return;
				   }
				   //AJAX this to catch access denied messages/redirects
				   if (debug != null)
					alert('refreshing status container ' + getScreenHeight());

				   var request = $.ajax({
					url: link + '.jsp',
					type: "GET",
					cache: false,
					data: {screenheight: getScreenHeight()}
				   });


				   request.done(function (msg) {
					if (debug != null)
					     alert('refreshing status container success, jqueryui-ifying it');
					window.console && console.log('refreshing status container success, jqueryui-ifying it');
					if (msg.indexOf("Please Login") >=0){
					     window.location.assign("index.jsp");
					     clearInterval(menu_fresh_interval);
					     return;
					}
					$("#" + div).html(msg);
					$(function () {
					     $("#menu").mouseleave(function () {
						  if (isAnimating || isAnimating2)
						       return;
						  isAnimating = true;
						  isAnimating2 = true;
						  var menu = $("#menu");
						  $(menu).hide("slide", "fast", function () {
						       isAnimating = false;
						  });
						  $("#min_menu").delay(300).show("slide", "fast", function () {
						       isAnimating2 = false;
						  });
						  menuactive = false;

					     });


					     $("#min_menu").mouseenter(function () {
						  if (isAnimating || isAnimating2)
						       return;

						  isAnimating = true;
						  isAnimating2 = true;
						  var menu = $("#menu");
						  var minmenu = $("#min_menu");

						  $(minmenu).hide("slide", "fast", function () {
						       isAnimating2 = false;
						  });
						  $(menu).delay(300).show("slide", "fast", function () {
						       isAnimating = false;
						  });
						  menuactive = true;

					     });
					});
					if (menu_fresh_interval == null)
					     menu_fresh_interval = setInterval(loadpagemenu, 30000);

				   });

				   request.fail(function (jqXHR, textStatus) {
					window.console && console.log('refreshing status container failed redirecting to login page' + textStatus);
					window.location.assign("index.jsp");
					return;

				   });
			      }
			      var menu_fresh_interval = setInterval(loadpagemenu, 30000);
			      <%       if (Utility.stringIsNullOrEmpty(request.getParameter("target"))) {
				   out.write("loadpage('home.jsp', 'mainpane');");
			      } else {
                                  
                                  //TODO must validate this text
				   out.write("loadpage('" + StringEscapeUtils.escapeEcmaScript(request.getParameter("target")) + "', 'mainpane');");
			      }
			      // loadpage('home.jsp', 'mainpane');
			      %>
			      loadpagemenu();

			      $(function () {
				   $("#menu").mouseleave(function () {
					var menu = $("#menu");
					$(menu).hide("slide", "fast");
					$("#min_menu").delay(300).show("slide", "fast");
					menuactive = false;
				   });
				   $("#min_menu").mouseenter(function () {
					var menu = $("#menu");
					var minmenu = $("#min_menu");
					$(menu).effect("slide", "fast");
					$(minmenu).hide();
					menuactive = true;
				   });
			      });

			      //$("#min_menu").button();



			      function toggleDebug()
			      {
				   var x = getCookie("DEBUG");
				   if (x == null)
					setCookie("DEBUG", "ON", 1);
				   else
					deleteCookie("DEBUG");
				   showDebugLogo();
			      }

			      function showDebugLogo()
			      {
				   var x = getCookie("DEBUG");
				   var link = document.getElementById("debuglink");
				   if (x == null)
				   {
					link.textContent = "DEBUG OFF";
				   } else
				   {
					link.textContent = "DEBUG ON";
				   }
			      }
			      $(window).resize(function () {
				   resizer();
			      });
			      function resizer()
			      {
				   if ($("#thenavbar").css("position") == "static")
				   {
					$("#menu_container").css("position", "absolute");
					$("#container-fluid").css("position", "absolute");
				   }
				   if ($("#thenavbar").css("position") == "fixed")
				   {
					$("#menu_container").css("position", "fixed");
					$("#container-fluid").css("position", "absolute");
				   }
			      }
			      //run it once the doc loads
			      resizer();
			 </script>
			 </head>
			 <body style="padding-bottom: 15px" onload="javascript:checkCookie();">
			      <form name="form1" id="form1">
				   <%
					/*
				    *
				    * Cross site request forgery protection
				    *
					 */
					String current = null;

					if (request.getMethod().equalsIgnoreCase("post")) {

					     if (Utility.stringIsNullOrEmpty(request.getParameter("nonce"))) {
						  //reject it
						  session.removeAttribute("nonce");
						  response.sendRedirect("index.jsp");
						  LogHelper.getLog().log(Level.WARN, "FGSMS CSRF Test failed, no nonce guid." + request.getRemoteAddr() + request.getRemoteUser());
						  throw new SecurityException("Cross Site Request Forgery");
					     } else {

						  String noncestr = (String) session.getAttribute("nonce");
						  if (noncestr == null) {
						       //no session variable to test against, reject it
						       LogHelper.getLog().log(Level.WARN, "FGSMS CSRF Test failed, no session guid." + request.getRemoteAddr() + request.getRemoteUser());
						       session.removeAttribute("nonce");
						       return;
						  }
						  String postedstr = request.getParameter("nonce");

						  //check session against existing nonce, if match
						  //generate new one, add to page and session
						  //else redirect to index page
						  if (noncestr.equals(postedstr)) {
						       current = noncestr;
						       //OK
						       // current = UUID.randomUUID();
						       //session.removeAttribute("nonce");
						       // session.setAttribute("nonce", current.toString());
						       LogHelper.getLog().log(Level.INFO, "FGSMS CSRF Test passed.");
						  } else {
						       //mismatch, reject it
						       LogHelper.getLog().log(Level.WARN, "FGSMS CSRF Test failed, session did not match nonce guid." + request.getRemoteAddr() + request.getRemoteUser());
						       session.removeAttribute("nonce");
						       return;
						  }
					     }
					} else {
					     //HTTP GET or otherwise message

					     current = (String) session.getAttribute("nonce");
					     if (Utility.stringIsNullOrEmpty(current)) {
						  current = java.util.UUID.randomUUID().toString();
						  session.setAttribute("nonce", current);
					     }

					}
				   %>
				   <% // nonce %>
				   <input type="hidden" name="nonce" id="nonce" value="<%=Utility.encodeHTML(current)%>" />


				   <% // upper nav bar 
				 //position relative is critical for formatting, do not remove otherwise, with small browser screens, the drop down menu will be behind the status bar
					//when set to fixed, the collapsed view is offscreen with a -20px offset 
					//style="position:relative;
				   %>

				   <div class="navbar navbar-inverse navbar-fixed-top" id="thenavbar">
					<div class="navbar-inner" >
					     <div class="container-fluid" style="width:95%">
						  <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse" >
						       <span class="icon-bar"></span>
						       <span class="icon-bar"></span>
						       <span class="icon-bar"></span>
						  </a>
						  <a class="brand" href="javascript:loadpage('home.jsp','mainpane');">FGSMS</a>

						  <div class="nav-collapse collapse" >
						       <ul class="nav">
							    <li id="Home" class="active"><a href="javascript:loadpage('home.jsp','mainpane');">Home</a></li>
							    <li id="MyServices"><a href="javascript:loadpage('status.jsp','mainpane');">My Services</a></li>
							    <li id="ServiceProfiles"><a href="javascript:loadpage('profile.jsp', 'mainpane');">Manage</a></li>
							    <li id="Performance"><a href="javascript:loadpage('perf.jsp', 'mainpane');">Performance</a></li>
							    <li id="Infrastructure"><a href="javascript:loadpage('infrastructure.jsp', 'mainpane');">Infrastructure</a></li>
							    <li id="Data"><a href="javascript:loadpage('data.jsp', 'mainpane');">Data</a></li>
							    <li id="Federation"><a href="javascript:loadpage('federation.jsp', 'mainpane');">Federation</a></li> 
							    <li id="Administration"><a href="javascript:loadpage('admin.jsp', 'mainpane');">Administration</a></li>
							    <!--<li><a id="debuglink" href="javascript:toggleDebug()">DEBUG</a></li>-->
							    <li id="Help"><a href="javascript:loadpage('help/index.jsp','mainpane');">Help</a></li>
						       </ul>

                                                      
						       <div class="pull-right">
							    <a class="btn dropdown-toggle" data-toggle="dropdown" href="javascript:toggle('user_menu');">
								 <i class="icon-user"></i> <%
								      if (request.getUserPrincipal() == null) {
									   out.write("anonymous");
								      } else {
									   out.write(Utility.encodeHTML(request.getUserPrincipal().getName()));
								      }

								 %>
								 <span class="caret"></span>
							    </a>
							    <ul class="dropdown-menu" >

								 <li><a href="javascript:loadpage('account.jsp','mainpane');">My Account</a></li>

								 <li class="divider"></li>
								 <li><a href="javascript:loadpage('logout.jsp','mainpane');">Sign Out</a></li>
							    </ul>
						       </div>
                                                       <div class="pull-right">
                                                           <%
                                                               
                                                               if (!ProxyLoader.getInstance(application).isSecure() ||
                                                                       !request.isSecure()){
                                                                   %>
                                                                   
                                                                   <i class="icon-warning-sign icon-white"></i>Warning Not Secure!
                                                                   <%
                                                               }
                                                               
                                                               %>
                                                       </div>
						       <!-- snip1 -->
						  </div><!--/.nav-collapse -->
					     </div>
					</div>
				   </div>
				   <div id="alerts" style="top:40px; background-color: red; background-image: none; position: fixed; width:80%; left:10%; padding-left: 5px; padding-right: 5px; display: none; z-index: 10005; color:white;">
					ALERTS
				   </div>
				   <div style="top:0px; background-color: <%=Utility.ICMClassificationToColorCodeString(c.getClassification())%>; background-image: none; position: fixed; left:50%; z-index: 9000; padding-left: 5px; padding-right:5px">
					<%
					     out.write(Utility.ICMClassificationToString(c.getClassification()) + " - " + Utility.encodeHTML(c.getCaveats()));
					%></div>
				   <div class="alert" style="display:none; position: fixed; top:45px; width:80%; left: 10%; z-index: 1000; opacity: 1.0; background-color: #FFD530; " id="resultBar">
				   </div>
				   <% // left hand side container for the status bar %>

				   <div class="container-fluid" style="padding-top:30px;  position: absolute; width:95%" id="container-fluid">
					<div id="menu_container">
					     <div id="menu" class="well" >
					     </div> 
					     <div id="min_menu" class="well" >
						  <img border="0" src="img/ajax-loader.gif">
					     </div>
					</div>

					<div class="row-fluid">
					     <div class="span12" id="mainpane" style="padding-left: 80px">
					     </div>
					</div><!--/span-->
					<%
					     //was position: fixed; bottom:0px; width:95%; 
					%>

					<footer style="text-align: center; padding-bottom: 0px; margin-bottom: 0px; background-color: #FFF; z-index: 1000">
					     <ul class="breadcrumb" id="special" style="background-color: #FFF; padding:0px">
						  <li id="first"><a href="javascript:loadpage('home.jsp','mainpane');"></a><span class="divider">/</span></li>
						  <li id="second" ><a href="javascript:loadpage('data.jsp','mainpane');"></a><span class="divider">/</span></li>
						  <li id="third" ><a href="javascript:loadpage('data.jsp','mainpane');"></a><span class="divider">/</span></li>
					     </ul>
					     <p><a href="https://mil-oss.github.com/fgsms"></a>FGSMS, U.S. Government Open Source Software- <%
						  out.write(Utility.ICMClassificationToString(c.getClassification()) + " - " + Utility.encodeHTML(c.getCaveats()));
						  out.write(" Version " + Constants.Version);

						  %> - This web site is considered BETA.</p>
					</footer>
				   </div><!--/.fluid-container-->
			      </form>
			      <script type="text/javascript">
				   //showDebugLogo();
				   function bookmarkit()
				   {
					try {
					     var val = $("#directlinktextbox").val();
					     if (window.sidebar) { // Mozilla Firefox Bookmark
						  window.sidebar.addPanel(document.title, val, "");
					     } else if (window.external) { // IE Favorite
						  window.external.AddFavorite(val, document.title);
					     } else if (window.opera && window.print) { // Opera Hotlist
						  this.title = document.title;
					     } else {
						  alert("Sorry, I couldn't add the link for you automatically. Perhaps you're using Chrome.");
					     }
					} catch (x)
					{
					     alert("Sorry, I couldn't add the link for you automatically. Perhaps you're using Chrome.");
					     window.console && console.log("error " + x);
					}

				   }
			      </script>
			      <div id="ExternalLink" style="position: absolute; top:50px; right:1% ">
				   <a href="javascript:ShowLinkGenerator();" title="Share this page" alt="Share this page"><i class="icon-share-alt" title="Share this page"></i></a>
			      </div>
			      <div id="directLinkModal" class="modal fade in" tabindex="-1" role="dialog" aria-labelledby="directLinkModalLabel" aria-hidden="false" style="display: block; display: none ">
				   <div class="modal-header">
					<a href="#" class="close" data-dismiss="modal" aria-hidden="true">�</a>
					<h3 id="directLinkModal">Direct Link</h3>
				   </div>
				   <div class="modal-body">
					Below is a direct link to this page, use this to share with others or to <a href="javascript:bookmarkit();">bookmark </a>
					it.
					<input type="text" id="directlinktextbox" value="" style="width:98%; height: 18px"></input>
				   </div>
				   <div class="modal-footer">
					<button class="btn" data-dismiss="modal">Close</button>
				   </div>
			      </div>
			 </body>
			 </html>
