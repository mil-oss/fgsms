<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%

    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Pragma", "no-cache");
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
            //throw new SecurityException("Cross Site Request Forgery");
            return;
        } else { 

            String noncestr = (String) session.getAttribute("nonce");
            if (noncestr == null) {
                //no session variable to test against, reject it
                LogHelper.getLog().log(Level.WARN, "FGSMS CSRF Test failed, no session guid." + request.getRemoteAddr() + request.getRemoteUser());
                session.removeAttribute("nonce");
                response.sendRedirect("index.jsp");
                return;
                //throw new SecurityException("Cross Site Request Forgery");
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
               LogHelper.getLog().log(Level.DEBUG, "FGSMS CSRF Test passed.");
            } else {
                //mismatch, reject it
                LogHelper.getLog().log(Level.WARN, "FGSMS CSRF Test failed, session did not match nonce guid." + request.getRemoteAddr() + request.getRemoteUser());
                session.removeAttribute("nonce");
                //throw new SecurityException("Cross Site Request Forgery");
                //maybe just redirect to the login page instead?
                response.sendRedirect("index.jsp");
                return;
            }
        }
    } else {
        //HTTP GET or otherwise message
        //removed because this page is included with everything else
                    /*current = (String) session.getAttribute("nonce");
         if (Utility.stringIsNullOrEmpty(current)) {
         current = java.util.UUID.randomUUID().toString();
         session.setAttribute("nonce", current);
         }*/
    }
%>