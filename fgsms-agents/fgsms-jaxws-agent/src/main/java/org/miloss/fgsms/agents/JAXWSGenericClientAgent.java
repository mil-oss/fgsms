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
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.agents;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 * FGSMS handlers for JAXWS Clients
 *
 * @author AO
 */
public class JAXWSGenericClientAgent implements SOAPHandler<SOAPMessageContext> {

    static final Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext messageContext) {
        Boolean isOutbound = (Boolean) messageContext.get(messageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isOutbound) {

            try {
                ProcessOutboundMessage(messageContext);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
                return true;
            }
        } else {
            try {
                ProcessInboundMessage(messageContext, false);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }

        }
        return true;
    }

    public boolean handleFault(SOAPMessageContext messageContext) {

        Boolean isOutbound = (Boolean) messageContext.get(messageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isOutbound) {

            try {
                ProcessOutboundMessage(messageContext);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
            }
        } else {
            try {
                ProcessInboundMessage(messageContext, true);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }

        }
        return true;
    }

    public void close(MessageContext context) {
    }

    private void ProcessOutboundMessage(SOAPMessageContext messageContext) {
        JAXWSGenericCommonMessageHandler.processRequest(messageContext, true, this.getClass().getCanonicalName());
    }

    private void ProcessInboundMessage(SOAPMessageContext messageContext, boolean fault) {
        JAXWSGenericCommonMessageHandler.processResponse(messageContext, fault, true, this.getClass().getCanonicalName());
    }
}
