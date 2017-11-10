/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.presentation;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Constants.AuthMode;
import org.miloss.fgsms.common.Utility;

/**
 *Login Filter is a Servlet Filter that handles username/password authentication for fgsms.
 * Since the authentication happens at the SOAP/web service level, the filter acts as a pass through, caching credentials
 * encrypted in the user's session object
 * 
 * use caution when editing this file, mistakes can cause security vulernabilities
 * and/or infinite redirects
 * 
 * @author AO
 */
public class LoginFilter implements Filter {

    private AuthMode mode = AuthMode.UsernamePassword;

    public void init(FilterConfig fc) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (mode == AuthMode.UsernamePassword) {
            boolean authorized = false;
            String username = "";

            if (request instanceof HttpServletRequest) {
                HttpServletRequest r = (HttpServletRequest) request;
                String url = r.getRequestURI();
                
                //System.out.println("CONTEXT PATH IS " + r.getContextPath());
                
                //context path should be /fgsmsBootstrap
                if (url.equalsIgnoreCase(r.getContextPath() + "/login.jsp")) {
                    chain.doFilter(request, response);
                    return;
                }
                HttpSession session = ((HttpServletRequest) request).getSession(true);
                if (session != null) {
                    username = (String) session.getAttribute("loginusername");
                    String password = (String) session.getAttribute("loginpassword");
                    if (!Utility.stringIsNullOrEmpty(password) && !Utility.stringIsNullOrEmpty(username)) {
                        authorized = true;
                    }
                }

                if (authorized) {
                    UserRequestWrapper wrap = new UserRequestWrapper(username, null, r);
                    chain.doFilter(wrap, response);
                    return;
                } else {

                    //what the http request asked for.
                    
                    String requesturl = r.getRequestURL().toString();
                    //http://server:port/fgsmsWeb/help/something.jsp          ?url=?http://localhost/something
                    //http://server:port/fgsmsWeb/something.jsp           ?url=?http://localhost/something
                    URL urlparsed = new URL(requesturl);
                    String urlparts=urlparsed.getPath();
                    if (!Utility.stringIsNullOrEmpty(r.getQueryString()))
                        urlparts=urlparts+ "?" + r.getQueryString();
                    //first of all, let's strip out
                    
                    //   String uri = r.getRequestURI();
                    
                    
                    //final String q = r.getQueryString();    
                    //if (Utility.stringIsNullOrEmpty(q)) {
                    //    ((HttpServletResponse) response).sendRedirect(r.getContextPath()+ "/login.jsp?url=" + URLEncoder.encode(urlparts));
                    //} else {
                        //this part is needed for links that are emailed from fgsms
                    //i.e. http://localhost:port/fgsmsBoostrap/showMeRecord?id=1234
                    //    ((HttpServletResponse) response).sendRedirect(r.getContextPath()+"/login.jsp?url=" + URLEncoder.encode(requesturl + "?" + q));
                   // }
                   
                   ((HttpServletResponse) response).sendRedirect(r.getContextPath()+"/login.jsp?url=" + URLEncoder.encode(urlparts, Constants.CHARSET));
                   
                    return;

                }
            } else throw new ServletException();

        } else {
            chain.doFilter(request, response);
        }
       
    }

    public void destroy() {
    }
}
