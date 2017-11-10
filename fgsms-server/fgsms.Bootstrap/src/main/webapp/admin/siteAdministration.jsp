<%-- 
    Document   : siteAdministration
    Created on : Jan 6, 2011, 8:47:30 PM
    Author     : Alex
--%>

<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%> 
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    PCS pcsport = pl.GetPCS(application, request, response); 




    if (request.getMethod().equalsIgnoreCase("post")) {
        try {
            //out.write("a");
            SetAdministratorRequestMsg req = new SetAdministratorRequestMsg();
            req.setClassification(c);

            ArrayOfUserInfo adminlist = new ArrayOfUserInfo();
            int count = 0;
//which button was pushed
            if (!Utility.stringIsNullOrEmpty(request.getParameter("savechanges"))) {
                //    out.write("b");
                while (true) {
                    //        out.write("c");
                    if (Utility.stringIsNullOrEmpty(request.getParameter("adminusername" + count))) {
                        break;
                    }
                    //         out.write("d");
                    UserInfo t = new UserInfo();
                    t.setUsername(request.getParameter("adminusername" + count));
                    t.setRole("admin");
                    t.setDisplayName((request.getParameter("displayName" + count)));
                    t.getEmail().add((request.getParameter("email" + count)));
                    adminlist.getUserInfo().add(t);
                    count++;
                }
            } else if (!Utility.stringIsNullOrEmpty(request.getParameter("adduserbutton"))) {
                while (true) {
                    //        out.write("c");
                    if (Utility.stringIsNullOrEmpty(request.getParameter("adminusername" + count))) {
                        break;
                    }
                    //         out.write("d");
                    UserInfo t = new UserInfo();
                    t.setUsername(request.getParameter("adminusername" + count));
                    t.setRole("admin");
                    t.setDisplayName((request.getParameter("displayName" + count)));
                    t.getEmail().add((request.getParameter("email" + count)));
                    //   t.getDisplayName().setNil(false); 
                    //    t.getEmail().setNil(false);
                    adminlist.getUserInfo().add(t);
                    count++;
                }
                UserInfo t = new UserInfo();
                t.setUsername(request.getParameter("Addusername"));
                t.setRole("admin");
                t.getEmail().add((request.getParameter("Addemail")));
                t.setDisplayName((request.getParameter("AddDisplay")));
                //  t.getDisplayName().setNil(false);
                //   t.getEmail().setNil(false);
                adminlist.getUserInfo().add(t);
            } else { //one of the revoke buttons was pushed
                /**
                 * note, when removing a global admin, the pcs will remove all
                 * existing entries and replace it with the provided list
                 */
                // out.write("e");
                while (true) {
                    if (Utility.stringIsNullOrEmpty(request.getParameter("adminusername" + count))) {
                        //           out.write("f");
                        break;

                    }
                    //      out.write("g");
                    if (Utility.stringIsNullOrEmpty(request.getParameter("revoke_" + count))) {
                        //          out.write("h");
                        UserInfo t = new UserInfo();
                        t.setUsername(request.getParameter("adminusername" + count));
                        t.setRole("admin");
                        t.setDisplayName((request.getParameter("displayName" + count)));
                        t.getEmail().add(request.getParameter("email" + count));
                        adminlist.getUserInfo().add(t);
                    }
                    count++;

                }
            }
            //  out.write("i");
            req.setUserList((adminlist));
            //   out.write("list is = " + req.getUserList().getUserInfo().size());
            try {
                pcsport.setAdministrator(req);
                out.write("Saved.");
            } catch (SecurityException se) {
                out.write("<font color=#FF0000>Access was denied.</font><Br>");
            } catch (Exception ex) {
                out.write("<font color=#FF0000>Error saving policy: " + ex.getLocalizedMessage() + "</font><bR>");
            }
        } catch (Exception ex) {
            response.setStatus(400);
            out.write("There was a problem processing your request. " + ex.getLocalizedMessage());
        }
    } else {

%>
<div class="well">
    <h1>Administrators</h1>
</div>
<div class="row-fluid">
    <div class="span12">

        Global Administrators or Site Administrators (no difference) are users that can see and access everything within FGSMS. They can also delegate permissions for service objects to other users. Only Global Admins can add or revoke Global Admin privileges.<Br><Br>
        Note: this list only provides administrators that are defined using the FGSMS user permissions and roles. Permissions and roles can also come from external sources, such as LDAP. <a href="javascript:loadpage('help/persmissions.jsp','mainpane');" class="btn btn-primary">Learn More</a>
        <h3>Current administrators</h3>
        <%
            try {
                GetAdministratorsRequestMsg req = new GetAdministratorsRequestMsg();
                req.setClassification(c);
                GetAdministratorsResponseMsg res = pcsport.getAdministrators(req);


                if (res != null && res.getUserList() != null && res.getUserList() != null
                        && res.getUserList() != null
                        && res.getUserList().getUserInfo().size() > 0) {
                    out.write("<table class=\"table\">");
                    out.write("<tr><th>Username</th><th>Role</th><th>email</th><th>Display Name</th><th>Revoke</th></tr>");
                    for (int i = 0; i < res.getUserList().getUserInfo().size(); i++) {
                        out.write("<tr><td><input name=adminusername" + i + " value=\""
                                + res.getUserList().getUserInfo().get(i).getUsername()
                                + "\" type=text size=50 readonly>"
                                + "</td><td><input type=text name=role" + i + " readonly size=50 value=\"" + res.getUserList().getUserInfo().get(i).getRole()
                                + "\">"
                                + "</td><td><input type=text size=50 name=displayName" + i + " value=\"");
                        if (res.getUserList().getUserInfo().get(i).getDisplayName() != null
                                && !Utility.stringIsNullOrEmpty(res.getUserList().getUserInfo().get(i).getDisplayName())) {
                            out.write(res.getUserList().getUserInfo().get(i).getDisplayName());
                        }
                        out.write("\">"
                                + "</td><td><input type=text size=50 name=email" + i + " value=\"");
                        if (res.getUserList().getUserInfo().get(i).getEmail() != null
                                && !(res.getUserList().getUserInfo().get(i).getEmail().isEmpty())) {
                            out.write(Utility.listStringtoString(res.getUserList().getUserInfo().get(i).getEmail()));
                        }
                        out.write("\">"
                                + "</td><td><a class=\"btn btn-primary\"  href=\"javascript:postBack('revoke_" + i + "','admin/siteAdministration.jsp');\">Revoke</a>"
                                + "</td></tr>");
                    }
                    out.write("</table>");
                    out.write("<br><a class=\"btn btn-primary\"  href=\"javascript:postBack('savechanges','admin/siteAdministration.jsp');\">Save</a>");
                    out.write("<br><br>"
                            + "<h3>Add a new global Admin</h3>"
                            + "<table border=1><tr><td >"
                            + "Username</td><td><input type=text size=50 name=Addusername>"
                            + "</td></tr><tr><td>Email</td><td><input type=text size=50 name=AddEmail>"
                            + "</td></tr><tr><td>Display name</td><td><input type=text size=50 name=AddDisplay>"
                            + "</td></tr></table>");

                    out.write("<a class=\"btn btn-primary\"  href=\"javascript:postBack('adduserbutton','admin/siteAdministration.jsp');\">Add User</a></td></tr></table>");



                }
            } catch (Exception ex) {
                out.write("There was a problem processing your request. " + ex.getLocalizedMessage());
            }

        %> 

    </div></div>
<%
    }
%>