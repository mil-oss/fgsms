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

package org.miloss.fgsms.agentcore;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author AO
 */
public class DependencyHelper {

    public static String getThreadIdFromSoapHeader(SOAPMessageContext messageContext) {
        try {
            SOAPMessage message = messageContext.getMessage();
            SOAPHeader soapHeader = message.getSOAPHeader();
            if (soapHeader != null) {
                Iterator childElements = soapHeader.getChildElements(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.threadid_message_localname));
                if (childElements.hasNext()) {
                    SOAPElement e = (SOAPElement) childElements.next();
                    String t = e.getTextContent();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        return t;
                    }
                    t = e.getValue();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        return t;
                    }
                    Node c = e.getFirstChild();
                    if (c != null) {
                        t = c.getTextContent();
                        if (!Utility.stringIsNullOrEmpty(t)) {
                            return t;
                        }
                        t = e.getValue();
                        if (!Utility.stringIsNullOrEmpty(t)) {
                            return t;
                        }
                    }
                }
                NodeList elementsByTagNameNS = soapHeader.getElementsByTagNameNS(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.threadid_message_localname);
                if (elementsByTagNameNS.getLength() > 0) {
                    Node node = elementsByTagNameNS.item(0);
                    String t = node.getTextContent();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        return t;
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName).log(Level.WARN, "unable to get soap header for dependency detection", ex);
        }


        //            messageContext.getMessage().getSOAPHeader().addHeaderElement(null).setTextContent(classname);
        return null;
    }

    public static String getRelatedMessageIdFromSoapHeader(SOAPMessageContext messageContext) {
        try {
            SOAPMessage message = messageContext.getMessage();
            SOAPHeader soapHeader = message.getSOAPHeader();
            if (soapHeader != null) {
                Iterator childElements = soapHeader.getChildElements(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.related_message_localname));
                if (childElements.hasNext()) {
                    SOAPElement e = (SOAPElement) childElements.next();
                    String t = e.getTextContent();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        return t;
                    }
                    t = e.getValue();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        return t;
                    }
                    Node c = e.getFirstChild();
                    if (c != null) {
                        t = c.getTextContent();
                        if (!Utility.stringIsNullOrEmpty(t)) {
                            return t;
                        }
                        t = e.getValue();
                        if (!Utility.stringIsNullOrEmpty(t)) {
                            return t;
                        }
                    }
                }
                NodeList elementsByTagNameNS = soapHeader.getElementsByTagNameNS(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.related_message_localname);
                if (elementsByTagNameNS.getLength() > 0) {
                    Node node = elementsByTagNameNS.item(0);
                    String t = node.getTextContent();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        return t;
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName).log(Level.WARN, "unable to get soap header for dependency detection", ex);
        }

        //            messageContext.getMessage().getSOAPHeader().addHeaderElement(null).setTextContent(classname);
        return null;
    }

    public static void insertRelatedMessageHeader(String messageid, SOAPMessageContext messageContext) {

        try {
            SOAPMessage msg = messageContext.getMessage();
            SOAPHeader header = msg.getSOAPHeader();
// get the part
            if (header == null) {
                SOAPPart soapPart = msg.getSOAPPart();
// get the envelope from the part
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
// add a header to the envelope
                header = soapEnvelope.addHeader();
            }
// add a namespace declaration. headers MUST be "namespace-qualified"
            //   header.addNamespaceDeclaration("hehe", "http://www.hehe.org");
// now add the child element User
            SOAPElement soapElement = header.addChildElement(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.related_message_localname));
// then add an attribute to the child element

// and finally add the value
            soapElement.addTextNode(messageid);
        } catch (Exception ex) {
            Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName).log(Level.WARN, "unable to insert soap header for dependency detection", ex);
        }
        /*
         * try { SOAPMessage message = messageContext.getMessage(); SOAPHeader
         * soapHeader = message.getSOAPHeader(); if (soapHeader == null) { //
         * Node env=message.getSOAPBody().getParentNode(); // Element
         * createElement = env.getOwnerDocument().createElement("Header");
         * SOAPFactory newInstance = SOAPFactory.newInstance(); SOAPElement
         * createElement = newInstance.createElement("Header");
         * message.getSOAPBody().getParentElement().addChildElement(createElement);
         *
         * SOAPElement addChildElement = createElement.addChildElement(new
         * QName(FgsmsSoapHeaderConstants.namespace,
         * FgsmsSoapHeaderConstants.related_message_localname));
         * addChildElement.setTextContent(messageid);
         *
         * } else { SOAPElement addChildElement = soapHeader.addChildElement(new
         * QName(FgsmsSoapHeaderConstants.namespace,
         * FgsmsSoapHeaderConstants.related_message_localname));
         * addChildElement.setTextContent(messageid); } // SOAPElement
         * addChildElement =
         * messageContext.getMessage().getSOAPHeader().addChildElement(new
         * QName(FgsmsSoapHeaderConstants.namespace,
         * FgsmsSoapHeaderConstants.related_message_localname)); //
         * addChildElement.setTextContent(messageid); } catch (Exception ex) {
         * Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName).log(Level.WARN, "unable
         * to insert soap header for dependency detection", ex); }
         */
    }

    public static void insertThreadIdHeader(String transactionthreadid, SOAPMessageContext messageContext) {
        try {
            SOAPMessage msg = messageContext.getMessage();
            SOAPHeader header = msg.getSOAPHeader();
            if (header == null) {
// get the part
                SOAPPart soapPart = msg.getSOAPPart();
// get the envelope from the part
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
// add a header to the envelope
                header = soapEnvelope.addHeader();
            }
// add a namespace declaration. headers MUST be "namespace-qualified"
            //   header.addNamespaceDeclaration("hehe", "http://www.hehe.org");
// now add the child element User
            SOAPElement soapElement = header.addChildElement(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.threadid_message_localname));
// then add an attribute to the child element

// and finally add the value
            soapElement.addTextNode(transactionthreadid);
        } catch (Exception ex) {
            Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName).log(Level.WARN, "unable to insert soap header for dependency detection", ex);
        }
        /*
         * try { SOAPMessage message = messageContext.getMessage(); SOAPHeader
         * soapHeader = message.getSOAPHeader();
         *
         * if (soapHeader == null) { // Node
         * env=message.getSOAPBody().getParentNode(); // Element createElement =
         * env.getOwnerDocument().createElement("Header"); SOAPElement header =
         * message.getSOAPBody().getParentElement().addChildElement("Header");
         * SOAPElement addChildElement = header.addChildElement(new
         * QName(FgsmsSoapHeaderConstants.namespace,
         * FgsmsSoapHeaderConstants.threadid_message_localname));
         * addChildElement.setTextContent(transactionthreadid); } else {
         * SOAPElement addChildElement = soapHeader.addChildElement(new
         * QName(FgsmsSoapHeaderConstants.namespace,
         * FgsmsSoapHeaderConstants.threadid_message_localname));
         * addChildElement.setTextContent(transactionthreadid); } } catch
         * (Exception ex) {
         * Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName).log(Level.WARN, "unable
         * to insert soap header for dependency detection", ex); }
         */
    }
}
