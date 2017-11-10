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

package org.miloss.fgsms.auth;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.x500.X500Principal;
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
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 *
 * @author AO
 */
public class PKI_LoginFilter implements Filter {

    private final static Logger log = Logger.getLogger("fgsms.Auth");
    private AuthMode mode = AuthMode.PKI;
    private List<X500Principal> authorizedDelegateCertifiations = new ArrayList<X500Principal>();
    
    public void init(FilterConfig fc) throws ServletException {
        String s = fc.getInitParameter("AuthorizedDelegates");
        if (Utility.stringIsNullOrEmpty(s)) {
            log.log(Level.WARN, "When configuring CAC/PKI authentication for fgsms, it is required for the web interface for the web gui's server certificate to be specified as an init param for this filter. Authenticaiton requests from the GUI will not be impresonated correctly.");
        }
        String[] pkis = s.split("\\|");
        for (int i = 0; i < pkis.length; i++) {
            authorizedDelegateCertifiations.add(new X500Principal(pkis[i]));
        }
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (mode == AuthMode.PKI) {
            boolean authorized = false;
            String cert = "";
            
            if (request instanceof HttpServletRequest) {
                HttpServletRequest r = (HttpServletRequest) request;
                
                String url = r.getRequestURI();
                
                HttpSession session = ((HttpServletRequest) request).getSession(true);
                if (session != null) {
                    //load approved list of delegated certificates
                    //check for http header
                    String header = r.getHeader(org.miloss.fgsms.common.Constants.CAC_DELEGATE_Authorization_Header);
                    if (Utility.stringIsNullOrEmpty(header)) {
                        cert = r.getUserPrincipal().getName();
                    } else {
                        
                        for (int i = 0; i < authorizedDelegateCertifiations.size(); i++) {
                            if (authorizedDelegateCertifiations.get(i).getName().equalsIgnoreCase(new X500Principal(r.getUserPrincipal().getName()).getName())) {
                                cert = header;
                                break;
                            }
                        }

                        //if r.getUserPrincipal().getName() is authorized for delegation
                    }
                    authorized = true;
                    //do something to validate the user
                }
                
                if (authorized) {
                    List<String> roles = new ArrayList<String>();
                    roles.add("everyone");
                    PKIRequestWrapper wrap = new PKIRequestWrapper(cert, roles, r);
                    chain.doFilter(wrap, response);
                    return;
                } else {
                    
                    ((HttpServletResponse) response).sendError(404);
                    return;
                    
                }
            }
            
        } else {
            chain.doFilter(request, response);
        }
    }
    
    public void destroy() {
    }
}
