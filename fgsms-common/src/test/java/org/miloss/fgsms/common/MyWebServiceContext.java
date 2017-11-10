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

package org.miloss.fgsms.common;

import java.security.Principal;
import java.util.*;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.Element;
/**
 *
 * @author AO
 */
    public class MyWebServiceContext implements WebServiceContext {

        public MyWebServiceContext() {
        }

        public MyWebServiceContext(MyMessageContext mc, String username) {
            here = mc;
            uname = username;
        }
        public MyMessageContext here;
        public String uname;

        public MyWebServiceContext(String username) {
            uname = username;
        }

        public MessageContext getMessageContext() {
            return here;
        }

        public Principal getUserPrincipal() {
            return new SimplePrincipal(uname);
        }
        public List<String> roles = new ArrayList<String>();
        public boolean isUserInRole(String role) {
            for (int i=0; i < roles.size(); i++)
            {
                if (role.equalsIgnoreCase(roles.get(i)))
                    return true;
            }
            return false;
        }

        public EndpointReference getEndpointReference(Element... referenceParameters) {
            return null;
        }

        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
            return null;
        }
    }
