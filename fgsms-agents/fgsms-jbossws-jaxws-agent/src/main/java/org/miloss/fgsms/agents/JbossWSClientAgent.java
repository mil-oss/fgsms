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

package org.miloss.fgsms.agents;

import org.miloss.fgsms.common.Constants;
import java.util.Collections;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 *
 * @author AO
 */
public class JbossWSClientAgent implements SOAPHandler<SOAPMessageContext> {

    private Logger log;

    public JbossWSClientAgent() {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    }

    public boolean handleMessage(SOAPMessageContext messageContext) {
        long start = System.currentTimeMillis();
        // MessageProcessor obj = MessageProcessor.getSingletonObject();
        Boolean isOutbound = (Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isOutbound) {

            try {
                ProcessOutboundMessage(messageContext);
            } catch (SOAPException ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
                return true;
            }
        } else {
            try {
                ProcessInboundMessage(messageContext, false);
            } catch (SOAPException ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }

        }


        // log.log(Level.WARNING, "fgsms Timer, handleMessage " + (System.currentTimeMillis() - start));
        return true;
    }

    public Set<QName> getHeaders() {
        return Collections.EMPTY_SET;
    }
//this will not be called if there is a problem with the proxy, we'll need to add something to purge old records
    
    public boolean handleFault(SOAPMessageContext messageContext) {
        Boolean isOutbound = (Boolean) messageContext.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isOutbound) {

            try {
                ProcessOutboundMessage(messageContext);
            } catch (SOAPException ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
            }
        } else {
            try {
                ProcessInboundMessage(messageContext, true);
            } catch (SOAPException ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }

        }
        return true;
    }

    public void close(MessageContext context) {
    }

    private void ProcessOutboundMessage(SOAPMessageContext messageContext) throws SOAPException {

        //this is for an outbound request

        JbossWSCommonMessageHandler.ProcessRequest(messageContext, true, this.getClass().getCanonicalName());

    }



    private void ProcessInboundMessage(SOAPMessageContext messageContext, boolean fault) throws SOAPException {

        /*
         * This is an inbound reply message!
         *
         */
        JbossWSCommonMessageHandler.ProcessResponse(messageContext, fault, true, this.getClass().getCanonicalName());
/*
 * This is an inbound reply message!
 *
 */

    }
}
