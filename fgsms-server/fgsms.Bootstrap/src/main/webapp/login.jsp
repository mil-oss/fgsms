<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyResponseMsg"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.PCS"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>


<%
    String error = "";
    ProxyLoader pl = ProxyLoader.getInstance(application);

    String redirect = "index.jsp";
    try {
        if (!Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
            redirect = request.getParameter("url");
            redirect = URLDecoder.decode(redirect, Constants.CHARSET);
            if (redirect.toLowerCase().startsWith("http") ||
                    redirect.toLowerCase().startsWith("javascript")){
                //possible CSRF or redirect attack, block it
                redirect = "index.jsp";
            }
            //what else to do here?....
            //this is a url part////
        }
    } catch (Exception ex) {
    } 
    if (pl.getAuthmode() == AuthMode.None) {
        response.sendRedirect("index.jsp");
        return;
    }
    if (pl.getAuthmode() == AuthMode.PKI) {
        response.sendRedirect("index.jsp");
        return;
    }
    //otherwise, we are in username/password mode

//if postback, validate form, then set
    if (request.getMethod().equalsIgnoreCase("POST")) {
        if (!Utility.stringIsNullOrEmpty(request.getParameter("username")) && !Utility.stringIsNullOrEmpty(request.getParameter("password"))) {
            try {

               PCS p2 = pl.GetPCSForUsernamePasswordLogin(application, request.getParameter("username"), request.getParameter("password"));

               GetGlobalPolicyRequestMsg req = new GetGlobalPolicyRequestMsg();
                req.setClassification(new SecurityWrapper(ClassificationType.U, "none"));
                GetGlobalPolicyResponseMsg pol = p2.getGlobalPolicy(req); 
                if (pol != null) { 
                    request.getSession().setAttribute("currentclassification", pol.getClassification());
                    request.getSession().setAttribute("loginusername", request.getParameter("username"));
                    request.getSession().setAttribute("loginpassword", Utility.EN(request.getParameter("password")));
                    response.sendRedirect(redirect);
                    return;
                } else {
                    error = "No policy was returned.";
                }
            } catch (Exception ex) {
                error = ("Access Denied. See server logs for details. ");
                LogHelper.getLog().log(Level.WARN, "Authentication Error: ", ex);
            }

        } else {
            error = "You must enter a username and a password.";
        }
    }


 

    //Requested URL = < %=Utility.encodeHTML(redirect)% >

%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html><head><title>Login Page</title></head>
    <body width="100%">
        <div style="width: 80%; margin-left: 10%; text-align: center">
            
                    
                    <h2>
                            INSERT YOUR DISCLAIMER HERE
                        </h2>
                    
                    Disclaimer does not cover misuse, accident, lightning, flood, tornado, tsunami, volcanic eruption, earthquake, hurricanes and other Acts of God, neglect, damage from improper or unauthorized repair, incorrect line voltage, broken antenna or marred cabinet, missing or altered serial numbers, electromagnetic radiation from nuclear blasts, sonic boom vibrations, customer adjustments that are not covered in this joke list, and incidents owing to airplane crash, ship sinking, motor vehicle accidents, dropping the item, falling rocks, leaky roof, broken glass, mud slides, forest fire, or projectile (which can include, but not be limited to, arrows, bullets, shot, BB's, shrapnel, lasers, napalm, torpedoes, or emissions of X-rays, Alpha, Beta and Gamma rays, knives, sticks and stones, et al.)

This supersedes any previous disclaimer.
                    
                     <%
                        if (!request.isSecure()) {
                            out.write("<br><h2>WARNING: You are currently accessing this page of a non-encrypted connection, meaning your password will be viewable by others.</h2><br><br>");
                        }

                    %>

                    <font color='blue'>Please Login</font>
                    <form method='post'>
                        <table align="center">
                            <tr><td>Name:</td>
                                <td><input type='text' name='username'></td></tr>
                            <tr><td>Password:</td> 
                                <td><input type='password' name='password' ></td>
                            </tr>
                        </table>
                        <br>
                        <%=error%>
                        <br><br>
                        <input id="loginButton" type='submit' value='Login' style="color:#ffd530;  font-size:1.875em;  background-color: #030000;"> 
                    </form>
        </div>
    </body>
</html>
