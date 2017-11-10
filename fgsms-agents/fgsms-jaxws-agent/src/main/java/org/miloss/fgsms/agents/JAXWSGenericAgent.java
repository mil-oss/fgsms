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

import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.miloss.fgsms.common.Constants;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 * FGSMS agent for services
 *
 * @author AO
 */
public class JAXWSGenericAgent implements SOAPHandler<SOAPMessageContext> {

    private Logger log;

    public JAXWSGenericAgent() {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    }

    public boolean handleMessage(SOAPMessageContext messageContext) {
        //long start = System.currentTimeMillis();
        // MessageProcessor obj = MessageProcessor.getSingletonObject();
        Boolean isRequest = !(Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isRequest) {

            try {
                ProcessInboundMessage(messageContext);
            } catch (SOAPException ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
            }
        } else {
            try {

                ProcessOutboundMessage(messageContext, false);
            } catch (SOAPException ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }

        }
        return true;
    }

    public Set<QName> getHeaders() {
        return Collections.EMPTY_SET;
    }

    public boolean handleFault(SOAPMessageContext messageContext) {
        try {
            ProcessOutboundMessage(messageContext, true);
        } catch (SOAPException ex) {
            log.log(Level.WARN, "fgsms error processing fault message", ex);
        }
        return true;
    }

    public void close(MessageContext context) {
    }

    private void ProcessInboundMessage(SOAPMessageContext messageContext) throws SOAPException {

        JAXWSGenericCommonMessageHandler.processRequest(messageContext, false, this.getClass().getCanonicalName());

    }

    private void ProcessOutboundMessage(SOAPMessageContext messageContext, boolean fault) throws SOAPException {
        JAXWSGenericCommonMessageHandler.processResponse(messageContext, fault, false, this.getClass().getCanonicalName());

    }
}
