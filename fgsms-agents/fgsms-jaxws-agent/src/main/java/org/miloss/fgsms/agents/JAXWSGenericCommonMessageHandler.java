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

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.miloss.fgsms.agentcore.DependencyHelper;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.IMessageProcessor;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.Constants;

/**
 * This class covers all JAXWS based handler traffic, client and service side
 *
 * @author AO
 */


public class JAXWSGenericCommonMessageHandler {

    static final Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    public static void processRequest(SOAPMessageContext messageContext, boolean client, String classname) {
        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
        long start = System.currentTimeMillis();
        UUID id = UUID.randomUUID();
        messageContext.put(org.miloss.fgsms.common.Constants.key, id);
        SOAPMessage msg = messageContext.getMessage();
        try {
            IMessageProcessor mp = MessageProcessor.getSingletonObject();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the Message Processor singleton object", ex);
        }
        String transactionthreadid = "";

        log.log(Level.DEBUG, "fgsms message context is of type " + messageContext.getClass().getName());
        //log.log(Level.WARN, "fgsms Timer, inbound setup3 " + (System.currentTimeMillis() - start));
        HttpServletRequest ctx = null;

        try {
            ctx = (HttpServletRequest) messageContext.get(messageContext.SERVLET_REQUEST);
        } catch (Exception ex) {
        }

        String action = org.miloss.fgsms.common.Constants.Undetermineable;
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = null;
        try {

            headers = (Map<String, List<String>>) messageContext.get(messageContext.HTTP_REQUEST_HEADERS);
            if (headers != null) {
                List l = headers.get("SOAPAction");
                if (l != null && !l.isEmpty()) {
                    action = (String) l.get(0);
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error getting outbound soap action", ex);
        }

        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable) && ctx != null) {
            try {
                //ctx = (HttpServletRequest) messageContext.get(messageContext.SERVLET_REQUEST);
                action = ctx.getHeader("SOAPAction");
                if (Utility.stringIsNullOrEmpty(action)) {
                    action = org.miloss.fgsms.common.Constants.Undetermineable;
                }
                action = action.replace("\"", "");
                action = action.replace("'", "");
                if (Utility.stringIsNullOrEmpty(action)) {
                    action = org.miloss.fgsms.common.Constants.Undetermineable;
                }
                //  if (!Utility.stringIsNullOrEmpty(action)) {
                ///  action = action.replace("\"", "");
                //  action = action.replace("\'", "");
                //  }
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting context object " + ex.getLocalizedMessage());
            }
        }
        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable)) {
            try {
                action = (String) messageContext.get(messageContext.WSDL_OPERATION);
                action = action.replace("\"", "");
                action = action.replace("'", "");
                if (Utility.stringIsNullOrEmpty(action)) {
                    action = org.miloss.fgsms.common.Constants.Undetermineable;
                }
            } catch (Exception ex) {
            }
        }
        if (Utility.stringIsNullOrEmpty(action)) {
            action = org.miloss.fgsms.common.Constants.Undetermineable;
        }
        action = action.replace("\"", "");
        action = action.replace("'", "");

        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction3.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction4.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }

        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }
        //if (action.equalsIgnoreCase("urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy")) {
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.PCSaction.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for PCS GetServicePolicy to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }

        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction2.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.PCSaction2.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for PCS GetServicePolicy to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }

        String requestURL = "";
        if (ctx != null) {
            try {
                StringBuffer buff = ctx.getRequestURL();
                requestURL = buff.toString();
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting request url " + ex.getLocalizedMessage());
                requestURL = "";
            }

        }
        if (Utility.stringIsNullOrEmpty(requestURL)) {
            try {

                requestURL = (String) messageContext.get("javax.xml.ws.service.endpoint.address");
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting request url " + ex.getLocalizedMessage());
                requestURL = "";
            }
        }
        if ((Utility.stringIsNullOrEmpty(requestURL)) && ctx != null) {
            try {
                StringBuffer buff = ctx.getRequestURL();
                requestURL = buff.toString();
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting request url " + ex.getLocalizedMessage());
                requestURL = "";

            }
        }

        //fix for standalone web services using the Endpoint API
        if (Utility.stringIsNullOrEmpty(requestURL) && messageContext.containsKey("com.sun.xml.internal.ws.http.exchange")) {
            try {
                HttpExchange exchg = (HttpExchange) messageContext.get("com.sun.xml.internal.ws.http.exchange");
                requestURL = ((exchg.getProtocol().toLowerCase().startsWith("https")) ? "https:/" : "http:/") + exchg.getLocalAddress().toString() + exchg.getRequestURI().toString();
                // HTTP/1.1://192.168.1.1:4444/Service1
            } catch (Exception ex) {
            }
        }

        if (Utility.stringIsNullOrEmpty(requestURL)) {
            requestURL = "urn:" + MessageProcessor.getSingletonObject().getHostName() + ":undeterminable";
        }

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        String messagexml = "";
        //if (MessageProcessor.shouldAgentRecordRequestContent(requestURL))
        {
            try {
                msg.writeTo(b);
                messagexml = b.toString(Constants.CHARSET);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms, error obtaining request message.", ex);
                messagexml = "fgsms was unable to obtain the request message.";
            }
        }

        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable)) {
            action = Utility.getActionNameFromXML(messagexml);
            if (Utility.stringIsNullOrEmpty(action)) {
                action = org.miloss.fgsms.common.Constants.Undetermineable;
            }
            action = action.replace("\"", "");
            action = action.replace("'", "");
            if (Utility.stringIsNullOrEmpty(action)) {
                action = org.miloss.fgsms.common.Constants.Undetermineable;
            }
        }
        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable)) {
            action = (String) messageContext.get(messageContext.HTTP_REQUEST_METHOD);
            if (Utility.stringIsNullOrEmpty(action)) {
                action = org.miloss.fgsms.common.Constants.Undetermineable;
            }
            action = action.replace("\"", "");
            action = action.replace("'", "");
            if (Utility.stringIsNullOrEmpty(action)) {
                action = org.miloss.fgsms.common.Constants.Undetermineable;
            }
        }

        log.log(Level.DEBUG, "fgsms Agent for JAXWS, inbound message for " + requestURL + " action " + action);
        //log.log(Level.WARN, "fgsms Timer, inbound setup4 " + (System.currentTimeMillis() - start));
        //special case

        String user = "";

        try {
            if (ctx != null) {
                user = ctx.getRemoteUser();
            }
            if (ctx != null && ctx.getAuthType() != null && ctx.getUserPrincipal() != null) {
                String u = ctx.getUserPrincipal().getName();
                if (!u.equals(user)) {
                    user += " " + ctx.getUserPrincipal().getName();
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "fgsms error getting user principal " + ex.getLocalizedMessage());
        }

        //ctx2.getOperationMetaData().isOneWay();
        String ipaddress = "";
        if (client) {

            transactionthreadid = MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId());
            if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
                //if not, create a new one
                try {
                    transactionthreadid = UUID.randomUUID().toString();
                } catch (Exception ex) {
                    log.log(Level.WARN, "error caught build transaction thread id", ex);
                }

            }

            if (headers == null) {
                headers = new HashMap<String, List<String>>();
            }
            if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {
                DependencyHelper.insertThreadIdHeader(transactionthreadid, messageContext);
                DependencyHelper.insertRelatedMessageHeader(id.toString(), messageContext);
                ArrayList t = new ArrayList();
                t.add(transactionthreadid);
                headers.put(org.miloss.fgsms.common.Constants.transactionthreadKey, t);
                t = new ArrayList();
                t.add(id.toString());
                headers.put(org.miloss.fgsms.common.Constants.relatedtransactionKey, t);
                //append to http header, transactionthreadid
                //append to http header, id
                messageContext.remove(messageContext.HTTP_REQUEST_HEADERS);
                messageContext.put(messageContext.HTTP_REQUEST_HEADERS, headers);
            }

            try {
                InetAddress addr = InetAddress.getLocalHost();
                ipaddress = addr.getHostAddress();
            } catch (UnknownHostException ex) {
                log.log(Level.ERROR, "error obtaining local ip address", ex);
            }
        } else {
            if (ctx != null) {
                ipaddress = ctx.getRemoteAddr();
            }

        }
        String relatedTransaction = null;
        //String threadid = null;
        if (!client) {
            //inbound request to a service
            //check for thread id
            //if not null, MP.setthreadid
            //check for related message id
            if (headers == null) {
                headers = new HashMap<String, List<String>>();
            }
            if (headers.containsKey(org.miloss.fgsms.common.Constants.MessageId)) {
                relatedTransaction = ((List<String>) headers.get(org.miloss.fgsms.common.Constants.relatedtransactionKey)).get(0);
            }
            if (Utility.stringIsNullOrEmpty(relatedTransaction)) {
                relatedTransaction = org.miloss.fgsms.agentcore.DependencyHelper.getRelatedMessageIdFromSoapHeader(messageContext);
            }
            if (headers.containsKey(org.miloss.fgsms.common.Constants.transactionthreadKey)) {
                transactionthreadid = ((List<String>) headers.get(org.miloss.fgsms.common.Constants.transactionthreadKey)).get(0);
            }
            if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
                transactionthreadid = org.miloss.fgsms.agentcore.DependencyHelper.getThreadIdFromSoapHeader(messageContext);
            }

            if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
                transactionthreadid = UUID.randomUUID().toString();
            }
            try {
                MessageProcessor.getSingletonObject().setTransactionThreadId(Thread.currentThread().getId(), transactionthreadid);
            } catch (Exception ex) {
            }
        }

        HashMap headervalues = new HashMap();
        if (ctx != null) {
            try {
                Enumeration<String> headersnames = ctx.getHeaderNames();

                while (headersnames.hasMoreElements()) {
                    String s = headersnames.nextElement();
                    headervalues.put(s, ctx.getHeader(s));
                    //   log.log(Level.INFO, "fgsms, Http header key: " + s + " value " + ctx.getHeader(s));
                }
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting http headers " + ex.getLocalizedMessage());
            }
        }

        if (ctx != null) {
            try {
                String query = ctx.getQueryString();
                if (query != null && (query == null ? "null" != null : !query.equals("null"))) {
                    requestURL += "?" + query;
                }
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting request query string " + ex.getLocalizedMessage());
            }
        }

        messageContext.put(org.miloss.fgsms.common.Constants.urlKEY, requestURL);
        log.log(Level.DEBUG, "fgsms Message intercepted, this is a request message to " + requestURL + " with the SOAPAction of " + action + ". Assigning message id:" + id.toString());

        try {
            MessageProcessor.getSingletonObject().processMessageInput(messagexml, messagexml.length(), requestURL, action, user, id.toString(), headervalues, ipaddress, classname, relatedTransaction, transactionthreadid);
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the MessageProcessor.processMessageInput", ex);
        }
        log.log(Level.DEBUG, "estimated additional latency " + (System.currentTimeMillis() - start));
    }

    public static void processResponse(SOAPMessageContext messageContext, boolean fault, boolean client, String classname) {
        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
        long start = System.currentTimeMillis();
        UUID id = (UUID) messageContext.get(org.miloss.fgsms.common.Constants.key);
        String requrl = (String) messageContext.get(org.miloss.fgsms.common.Constants.urlKEY);
        boolean ok = true;
        if (id == null) {
            log.log(Level.DEBUG, "fgsms reply message did not have context variable " + org.miloss.fgsms.common.Constants.key + " added. This transaction will be ignored.");
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(requrl)) {
            log.log(Level.DEBUG, "fgsms reply message did not have context variable " + org.miloss.fgsms.common.Constants.urlKEY + " added. This transaction will be ignored.");
            ok = false;
        }

        if (!ok) {
            if (id != null) {
                MessageProcessor.getSingletonObject().removeFromQueue(id);
            }
            return;
        }

        HashMap headervalues = new HashMap();
        String relatedtransaction = null;
        if (client) {
            relatedtransaction = DependencyHelper.getRelatedMessageIdFromSoapHeader(messageContext);
            try {
                Map headers = (Map) messageContext.get(messageContext.HTTP_RESPONSE_HEADERS);
                if (headers != null) {
                    log.log(Level.DEBUG, "client received " + headers.size() + " transaction " + id.toString());
                    Iterator it = headers.keySet().iterator();
                    while (it.hasNext()) {
                        String s = (String) it.next();
                        log.log(Level.DEBUG, " client header " + s + " = " + headers.get(s).toString());
                        headervalues.put(s, headers.get(s).toString());
                    }

                }

            } catch (Exception ex) {
                log.log(Level.ERROR, "Unexpected error caught when searching for fgsms soap header", ex);
            }
        }

        if (!client) {
            //service response, insert fgsms headers
            ArrayList<String> l = new ArrayList<String>();
            l.add(id.toString());
            ArrayList<String> threadlist = new ArrayList<String>();
            threadlist.add(MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId()));
            java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = (Map<String, List<String>>) messageContext.get(messageContext.HTTP_RESPONSE_HEADERS);
            if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {
                DependencyHelper.insertRelatedMessageHeader(id.toString(), messageContext);
                DependencyHelper.insertThreadIdHeader(threadlist.get(0), messageContext);
                if (headers != null) {
                    headers.put(org.miloss.fgsms.common.Constants.relatedtransactionKey, l);
                } else {
                    headers = new HashMap<String, List<String>>();
                    headers.put(org.miloss.fgsms.common.Constants.relatedtransactionKey, l);
                    headers.put(org.miloss.fgsms.common.Constants.transactionthreadKey, threadlist);
                    messageContext.put(messageContext.HTTP_RESPONSE_HEADERS, headers);
                    //log.log(Level.WARN, "fgsms response header map is null");
                }
            }
            if (headers != null) {
                Iterator it = headers.keySet().iterator();
                while (it.hasNext()) {
                    String s = (String) it.next();
                    log.log(Level.DEBUG, " service response header " + s + " = " + headers.get(s).toString());
                    headervalues.put(s, headers.get(s).toString());
                }
            }
            /*
             * try {
             * messageContext.getMessage().getSOAPHeader().addHeaderElement(new
             * QName("org.miloss.fgsms.agent", "fgsmsAgent")); } catch
             * (SOAPException ex) {
             * Logger.getLogger(JbossWSCommonMessageHandler.class.getName()).log(Level.ERROR,
             * "Error adding fgsms soap header to response message", ex); }
             */
        }

        SOAPMessage msg = messageContext.getMessage();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        String messagexml = "";
        try {
            msg.writeTo(b);
            messagexml = b.toString(Constants.CHARSET);
        } catch (Exception ex) {
            log.log(Level.WARN, "fgsms, error obtaining response message.", ex);
            messagexml = "fgsms was unable to obtain response message.";
            //MessageProcessor.removeFromQueue(id);
            //return;
        } finally {
            try {
                b.close();
            } catch (IOException ex) {
                
            }
        }

        String code2 = "200";
        //  boolean fault = false;
        try {
            Object j = messageContext.get(messageContext.HTTP_RESPONSE_CODE).toString();
            if (j != null) {
                code2 = j.toString();
                if (!code2.equalsIgnoreCase("200")) {
                    fault = true;
                }
                headervalues.put("RESPONSE_CODE", code2);
            }
        } catch (Exception ex) {
            //log.log(Level.WARN, "fgsms error caught attempting to pull the http status code from the current context.");
        }

        log.log(Level.DEBUG, "fgsms Message intercepted, this is a response message transaction id:" + id.toString() + " fault=" + fault + " " + classname);

        MessageProcessor.getSingletonObject().processMessageOutput(id.toString(), messagexml, messagexml.length(), fault, Long.valueOf(System.currentTimeMillis()), headervalues, relatedtransaction);
        //log.log(Level.WARN, "fgsms Timer, outbound final" + (System.currentTimeMillis() - start));
        if (!client) {
            MessageProcessor.getSingletonObject().clearTransactionThreadId(Thread.currentThread().getId());
        }
        log.log(Level.DEBUG, "estimated additional latency " + (System.currentTimeMillis() - start));
    }
}
