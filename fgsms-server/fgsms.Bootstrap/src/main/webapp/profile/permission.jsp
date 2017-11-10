<%--
    Document   : permission
    Created on : Jan 6, 2011, 8:45:56 PM
    Author     : Alex 
--%>


<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Enumeration"%>

<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="../csrf.jsp" %>

<%
 
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    ObjectFactory fac = new ObjectFactory();

    PCS pcsport = pl.GetPCS(application, request, response);

    if (Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
        response.sendRedirect("../index.jsp");
        return;
    }
    if (request.getMethod().equalsIgnoreCase("post")) {

        //*****************************************************
        // post
                    /*Enumeration enumer = request.getParameterNames();
         while (enumer.hasMoreElements()) {
         String s = (String) enumer.nextElement();
         out.write(s + "=" + request.getParameter(s));
         }*/
        SetServicePermissionsRequestMsg reqp = new SetServicePermissionsRequestMsg();
        reqp.setClassification(c);
        List<UserServicePermissionType> currentdata = null;
        try {
            currentdata = (List<UserServicePermissionType>) request.getSession().getAttribute("permissions");
        } catch (Exception ex) {
        }

        //**************************************************************
        // grant a new user permission to this service
        if (!Utility.stringIsNullOrEmpty(request.getParameter("adduserbutton"))) {

// <editor-fold defaultstate="collapsed" desc="user-description">
            ArrayOfUserPermissionType temp = new ArrayOfUserPermissionType();
            reqp.setURL(request.getParameter("url"));

            boolean error = false;
            if (currentdata == null) {
                //no existing users
                //out.write("<BR>no existing users<br>");
                UserPermissionType pt = new UserPermissionType();
                pt.setUser(request.getParameter("username"));
                pt.setRight(RightEnum.fromValue(request.getParameter("adduserperm")));
                temp.getUserPermissionType().add(pt);
                reqp.setRights(temp);
            } else {//at least one ACL permission already exist, add to them
                //out.write("<BR>existing users... " + currentdata.size());

                boolean ok = true;
                UserPermissionType pt = null;

                for (int i = 0; i < currentdata.size(); i++) {

                    if (currentdata.get(i).getUser().equalsIgnoreCase(request.getParameter("username"))) {
                        out.write("This user has already been added to the list.");
                        continue;
                    }

                    String right = request.getParameter("changepermission" + i);
                    if (Utility.stringIsNullOrEmpty(right)) {
                        continue;   //some strange error
                    }
                    if (right.equalsIgnoreCase("norights")) {
                        continue;   //remove a user
                    }
                    pt = new UserPermissionType();
                    pt.setUser(request.getParameter("usernameField" + i));
                    try {
                        pt.setRight(RightEnum.fromValue(right));
                    } catch (Exception ex) {
                        error = true;
                        //  out.write("error");
                    }
                    //out.write("<br>adding " + pt.getUser() + pt.getRight().value());
                    temp.getUserPermissionType().add(pt);

                }
                if (ok) {
                    //ook, this really is a new user, add them to the list
                    pt = new UserPermissionType();
                    pt.setUser(request.getParameter("username"));
                    pt.setRight(RightEnum.fromValue(request.getParameter("adduserperm")));
                    //out.write("<br>adding " + pt.getUser() + pt.getRight().value());
                    temp.getUserPermissionType().add(pt);
                }
                if (!error) {
                    reqp.setRights(temp);
                }
            }
            try {
                pcsport.setServicePermissions(reqp);
                out.write("<font color=#FF0000>Permissions Saved.</font><br><br>");
            } catch (SecurityException ex) {
                out.write("<font color=#FF0000>Access Denied.</font><br><br>");
            } catch (Exception ex) {
                out.write("<font color=#FF0000>There was an error processing your add user request. Message: " + ex.getLocalizedMessage() + "</font><br>");
            }

// </editor-fold>
        } else if (!Utility.stringIsNullOrEmpty(request.getParameter("savechanges"))) {
            //********************************************************
            //just modify existing records

            //List<UserPermissionType> data = new ArrayList<UserPermissionType>();
            ArrayOfUserPermissionType temp = new ArrayOfUserPermissionType();
            reqp.setURL(request.getParameter("url"));

            boolean error = false;
            int i = 0;
            String right = "", user = "";
            while (true) {
                try {
                    right = request.getParameter("changepermission" + i);
                    user = request.getParameter("usernameField" + i);
                } catch (Exception ex) {
                }
                if (Utility.stringIsNullOrEmpty(right) || Utility.stringIsNullOrEmpty(request.getParameter("usernameField" + i))) {
                    break;
                }
                if (!right.equalsIgnoreCase("norights")) {
                    //keep this user
                    UserPermissionType pt = new UserPermissionType();
                    pt.setUser(user);

                    try {
                        pt.setRight(RightEnum.fromValue(right));
                    } catch (Exception ex) {
                        error = true;
                        //  out.write("error");
                    }
                    // out.write(Utility.encodeHTML(pt.getUser() + pt.getRight().value()));
                    //out.write("<br>adding " + pt.getUser() + pt.getRight().value());
                    temp.getUserPermissionType().add(pt);
                }
                i++;
            }
            reqp.setRights(temp);
            try {
                pcsport.setServicePermissions(reqp);
                out.write("<font color=#FF0000>Permissions Saved.</font><br><br>");
            } catch (SecurityException ex) {
                out.write("<font color=#FF0000>Access Denied.</font><br><br>");
            } catch (Exception ex) {
                out.write("<font color=#FF0000>There was an error processing your set permission request. Message: " + ex.getLocalizedMessage() + "</font><br>");

            }

        }

    }
    //normal op, get 
    {

%>

The following represents the access control list for access to information within the FGSMS web application and FGSMS services.
It does NOT represent permissions related towards executing the monitored service. To remove all access rights for a specific user, select
none, then click Save Changes. <a href="javascript:loadpage('help/permissions.jsp','mainpane');" class="btn">Learn more</a>.
<%

    out.write("Showing permission set for the service identified by " + Utility.encodeHTML(request.getParameter("url")) + "<br>");
    try {

        if (Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
            response.sendRedirect("../index.jsp");
            return;
        }





        GetServicePermissionsRequestMsg req = new GetServicePermissionsRequestMsg();
        req.setURL((request.getParameter("url")));
        req.setClassification(c);

        GetServicePermissionsResponseMsg res = pcsport.getServicePermissions(req);
        if (res != null && res.getRights() != null
                && res.getRights().getUserServicePermissionType() != null
                && !res.getRights().getUserServicePermissionType().isEmpty()) {
            out.write("<table border=1><tr><th>URL</th><th>User</th><th>Current Permission</th><th>New Value</th></tr>");
            for (int i = 0; i < res.getRights().getUserServicePermissionType().size(); i++) {
                out.write("<tr><td>" + Utility.encodeHTML(res.getRights().getUserServicePermissionType().get(i).getURL())
                        + "</td><td><input type=text readonly size=50 name=usernameField" + i + " value=\"" + Utility.encodeHTML(res.getRights().getUserServicePermissionType().get(i).getUser())
                        + "\"></td><td>" + Utility.encodeHTML(res.getRights().getUserServicePermissionType().get(i).getRight().value())
                        + "</td><td><select name=changepermission" + i + ">");
                if (res.getRights().getUserServicePermissionType().get(i).getRight() == RightEnum.ADMINISTER) {
                    out.write("<option value=administer selected>Administer</option>");
                    out.write("<option value=audit>Audit</option>");
                    out.write("<option value=write >Write</option>");
                    out.write("<option value=read>Read</option>");
                }
                if (res.getRights().getUserServicePermissionType().get(i).getRight() == RightEnum.AUDIT) {
                    out.write("<option value=administer >Administer</option>");
                    out.write("<option value=audit selected>Audit</option>");
                    out.write("<option value=write >Write</option>");
                    out.write("<option value=read>Read</option>");
                }
                if (res.getRights().getUserServicePermissionType().get(i).getRight() == RightEnum.WRITE) {
                    out.write("<option value=administer >Administer</option>");
                    out.write("<option value=audit>Audit</option>");
                    out.write("<option value=write selected>Write</option>");
                    out.write("<option value=read>Read</option>");
                }
                if (res.getRights().getUserServicePermissionType().get(i).getRight() == RightEnum.READ) {
                    out.write("<option value=administer >Administer</option>");
                    out.write("<option value=audit>Audit</option>");
                    out.write("<option value=write >Write</option>");
                    out.write("<option value=read selected>Read</option>");

                } /*
                 out.write("<option value=administer>Administer</option>");
                 out.write("<option value=audit>Audit</option>");
                 out.write("<option value=write >Write</option>");
                 out.write("<option value=read>Read</option>");*/
                out.write("<option value=norights>None</option>");
                out.write("</select></td></tr>");
            }
            out.write("</table>");
            out.write("<input type=button value=\"Save Changes\" class=\"btn btn-primary\" name=savechanges onclick=\"javascript:postBackReRender('savechanges','profile/permission.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab6');\" >");
            request.getSession().setAttribute("permissions", res.getRights().getUserServicePermissionType());
        } else {
            out.write("<Br><h3>No permissions were returned.</h3>"
                    + "This generally means that only users with the administrator role can access information related to this service.<Br><br>");
        }
        out.write("<br><br>"
                + "<h3>Add a new user</h3>"
                + "<table border=1><tr><th>Username</th><th>Rights</th><th></th></tr>"
                + "<tr><td>"
                + "<input type=text name=username value=username></td><td>"
                + "<select name=adduserperm>"
                + "<option value=administer>Administer</option>"
                + "<option value=audit>Audit</option>"
                + "<option value=write>Write</option>"
                + "<option value=read selected>Read</option>"
                + "</select></td><td>");
        out.write("<input type=button name=adduserbutton value=\"Add User\" class=\"btn btn-primary\" onclick=\"javascript:postBackReRender('adduserbutton','profile/permission.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab6');\" ></td></tr></table>");

    } catch (Exception ex) {
        out.write("<font color=#FF0000>There was an error processing your request. Message: " + ex.getLocalizedMessage() + "</font>");
    }


%>
<Br>
The username "everyone" will enable all authenticated users the chosen privilege, but only for read, write, and audit.<br>
<Br>
The following table defines what each permission level means as far as access control within FGSMS.<br>
<table border="1">
    <tr><th>Level</th><th>Rights</th></tr>
    <tr><td>Read</td><td>The user can read permissions and performance statistics for a service.</td></tr>
    <tr><td>Write</td><td>The user can read and make changes to the service policy for a service.</td></tr>
    <tr><td>Audit</td><td>The user can read, make changes to the service policy and view message transaction logs.</td></tr>
    <tr><td>Administer</td><td>The user can read, make changes to the service policy, view message transaction logs and delegate and change permissions for this service.</td></tr>

</table>
<%
    }
%>