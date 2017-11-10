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

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.miloss.fgsms.common.Constants;

/**
 *This is a soap handler that injects an http header that identifies the current user context.
 * It should only be used when using CAC/PKI logins from the fgsms web interface to the fgsms web services
 * @author AO
 */
public class PKIHandler implements SOAPHandler<SOAPMessageContext> {

    public PKIHandler() {
    }

    public PKIHandler(Principal user) {
        this.user = user;
    }
    private Principal user = null;

    public Set<QName> getHeaders() {
        return new HashSet<QName>();
    }

    public boolean handleMessage(SOAPMessageContext messageContext) {
        Boolean isOutbound = (Boolean) messageContext.get(messageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isOutbound) {

            try {
                ProcessOutboundMessage(messageContext);
            } catch (Exception ex) {
                //log.log(Level.WARNING, "fgsms Error processing inbound message", ex);
                return true;
            }
        } else {
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public void close(MessageContext context) {
    }

    private void ProcessOutboundMessage(SOAPMessageContext messageContext) {
        if (user == null) {
            return;
        }
        messageContext.getMessage().getMimeHeaders().addHeader(org.miloss.fgsms.common.Constants.CAC_DELEGATE_Authorization_Header, user.getName());
    }
}
