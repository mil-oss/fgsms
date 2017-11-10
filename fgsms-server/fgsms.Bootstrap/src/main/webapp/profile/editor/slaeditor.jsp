<%-- 
    Document   : slaeditor
    Created on : Oct 22, 2015, 3:53:46 PM
    Author     : alex
--%>

<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.*"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="javax.xml.bind.JAXBElement"%> 
<%@page import="java.math.BigInteger"%>
<%@page import="java.util.UUID"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Enumeration"%>  
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>

<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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


  <link rel="stylesheet" type="text/css" href="css/main.css"/>
 
  <script src="js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script src="js/bootstrap.js" type="text/javascript"></script>
  <script src="js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script> 
  <script src="js/main.js" type="text/javascript"></script>
 
  </head>
  <body style="padding-bottom: 15px" >
  

  </body>
</html>