<%-- 
    Document   : loader
    Created on : Sep 29, 2012, 9:58:24 AM
    Author     : Administrator
This JSP will load a help file as a seperate window/tab
--%>

<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%

    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    if (c == null) {
        LogHelper.getLog().log(Level.INFO, "redirecting to the index page, security wrapper is null");
        response.sendRedirect("index.jsp");
    }


%>
<html lang="en">
    <head>
        <meta charset="utf-8">
            <title>FGSMS</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta name="description" content="FGSMS"/>
                <meta name="author" content="">
                    <link rel="icon" type="image/gif" href="favicon.ico"></link>
                    <link href="css/bootstrap.css" rel="stylesheet">
                        <link href="css/bootstrap-responsive.css" rel="stylesheet">


                            <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
                            <!--[if lt IE 9]>
                              <script src="js/html5.js"></script>
                            <![endif]-->

                            <!-- Le fav and touch icons -->
                            <!--<link rel="shortcut icon" href="favicon.ico">
                            <link rel="apple-touch-icon-precomposed" sizes="144x144" href="../assets/ico/apple-touch-icon-144-precomposed.png">
                            <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../assets/ico/apple-touch-icon-114-precomposed.png">
                            <link rel="apple-touch-icon-precomposed" sizes="72x72" href="../assets/ico/apple-touch-icon-72-precomposed.png">
                            <link rel="apple-touch-icon-precomposed" href="../assets/ico/apple-touch-icon-57-precomposed.png">-->


                            <link rel="stylesheet" type="text/css" href="css/army-jquery-ui-1.8.17.custom.css"/>
                            <link rel="stylesheet" type="text/css" href="css/main.css"/>

                            <script src="js/jquery-1.7.1.min.js" type="text/javascript"></script>
                            <script src="js/bootstrap.js" type="text/javascript"></script>

                            <script src="js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>        
                            <script type="text/javascript" src="js/charting.js"></script>

                            <script src="js/main.js" type="text/javascript"></script>
                            <script src="js/breadcrumbs.js" type="text/javascript"></script>
                            <script type="text/javascript">
                                <%
                                    if (Utility.stringIsNullOrEmpty(request.getParameter("target"))) {
                                        out.write("loadpage('help/index.jsp', 'mainpane');");
                                    } else {
                                        //TODO this needs validation and is dubious 
                                        out.write("loadpage('" + request.getParameter("target") + " ', 'mainpane');");
                                    }

                                %>
                                
                            </script>
                            </head>

                            <body style="padding-bottom: 15px" onload="javascript:checkCookie();">

                                <div class="navbar navbar-fixed-top" style="position:relative;">
                                    <div class="navbar-inner">
                                        <div class="container-fluid">
                                            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                                                <span class="icon-bar"></span>
                                                <span class="icon-bar"></span>
                                                <span class="icon-bar"></span>
                                            </a>

                                            <div class="pull-left ">
                                                <a class="dropdown-toggle brand" data-toggle="dropdown" >FGSMS
                                                    <span class="caret"></span>
                                                </a>
                                                <ul id="mobileNav" class="dropdown-menu">
                                                    <li id="Home"><a href="javascript:loadpage('help/index.jsp','mainpane');">Index</a></li>

                                                </ul>
                                            </div>





                                            <div class="nav-collapse" >
                                                <ul id="webNav" class="nav"><!--class="active"-->
                                                    <li id="Home"><a href="javascript:loadpage('help/index.jsp','mainpane');">Index</a></li>
                                                    <li id="Close"><a href="javascript:window.close();">Close</a></li>
                                                </ul>
                                            </div><!--/.nav-collapse -->
                                        </div>
                                    </div>
                                </div>



                                <div    style="top:0px; background-color: <%=Utility.ICMClassificationToColorCodeString(c.getClassification())%>; background-image: none; position: fixed; left:50%; z-index: 9000; padding-left: 5px; padding-right:5px">
                                    <%
                                        out.write(Utility.ICMClassificationToString(c.getClassification()) + " - " + Utility.encodeHTML(c.getCaveats()));
                                    %></div>

                                <div class="container-fluid" style="position:absolute; top:40px; width:95%;" >

                                    <div class="row-fluid">
                                        <div class="span12" id="mainpane"  >
                                        </div>
                                    </div>
                                    
                                        <br/>
                                        <br/>
                                        <br/>
                                        <footer  style="position: fixed; bottom:0px; width:80%; text-align: center; padding-bottom: 0px; margin-bottom: 0px; background-color: #FFF;">

                                            <p>&copy; FGSMS, U.S. Government Open Source Software- <%
                                                out.write(Utility.ICMClassificationToString(c.getClassification()) + " - " + Utility.encodeHTML(c.getCaveats()));
                                                out.write(" Version " + Constants.Version);

                                                %> - This web site is considered BETA.</p>


                                        </footer>

                                </div><!--/.fluid-container-->

                            </body>
                            </html>
