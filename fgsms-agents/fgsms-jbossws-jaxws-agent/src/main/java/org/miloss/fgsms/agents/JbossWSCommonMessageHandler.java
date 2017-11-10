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

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.miloss.fgsms.agentcore.DependencyHelper;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.agentcore.OneWayJudge;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.IMessageProcessor;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Logger;;

/**
 * This class is nearly the same as the standard JAXWS handler and the CXF handle
 * except it has some additional hooks for increased accuracy of data
 * @author AO
 */
public class JbossWSCommonMessageHandler {

    static final Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    public static void ProcessRequest(SOAPMessageContext messageContext, boolean client, String classname) {
        long now = System.currentTimeMillis();
        boolean isoneway = false;
        /*
         * NO WAY TO DETERMINE @ONEWAY METHODS IN THIS CONTEXT if
         * (messageContext.containsKey("com.sun.xml.internal.ws.server.OneWayOperation"))
         * { try { Object j =
         * messageContext.get("com.sun.xml.internal.ws.server.OneWayOperation");
         * if (j == null && !client) { isoneway = true; } if (j != null) { //
         * isoneway = !(Boolean) j; } } catch (Exception ex) {
         * log.log(Level.DEBUG, null, ex); } }
         */

      IMessageProcessor mp;
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessRequest1 timer: " + 0 + " thread:" + Thread.currentThread().getId());
      
        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
        try {
            mp = MessageProcessor.getSingletonObject();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the Message Processor singleton object. monitoring is not possible", ex);
            return;
        }
        UUID id = UUID.randomUUID();
        messageContext.put(org.miloss.fgsms.common.Constants.key, id);
        SOAPMessage msg = messageContext.getMessage();
//find a way to see the size without getting a full copy of it - it's not possible

        log.log(Level.DEBUG, "fgsms context is of type " + messageContext.getClass().getName() + " client=" + client + " class = " + classname);

        String transactionthreadid = "";

        HttpServletRequest ctx = null;
        org.jboss.ws.core.jaxws.handler.SOAPMessageContextJAXWS ctx2 = null;
        try {
            ctx = (HttpServletRequest) messageContext.get(SOAPMessageContext.SERVLET_REQUEST);
        } catch (Exception ex) {
        }
        try {
            ctx2 = (org.jboss.ws.core.jaxws.handler.SOAPMessageContextJAXWS) messageContext;
        } catch (Exception ex) {
        }

        String action = org.miloss.fgsms.common.Constants.Undetermineable;
        java.util.Map<java.lang.String, java.util.List<java.lang.String>> headers = null;
        try {
            headers = (Map<String, List<String>>) messageContext.get(SOAPMessageContext.HTTP_REQUEST_HEADERS);
            if (headers != null) {
                List l = headers.get("SOAPAction");
                if (l != null && !l.isEmpty()) {
                    action = (String) l.get(0);
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error getting outbound soap action", ex);
        }
        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable)) {
            try {
                //when in client mode, servlet context is not available
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
                if (!client) {
                    log.log(Level.INFO, "fgsms error getting servlet context object " + ex.getLocalizedMessage() + ". For transactions monitored from the client, this is normal. IsClient? " + client);
                }
            }
        }
        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable) && ctx2 != null) {
            Object j = ctx2.get(ctx2.WSDL_OPERATION);
            if (j != null) {
                QName q = (QName) j;
                action = (String) q.toString();
                if (Utility.stringIsNullOrEmpty(action)) {
                    action = org.miloss.fgsms.common.Constants.Undetermineable;
                }
                action = action.replace("\"", "");
                action = action.replace("'", "");
                if (Utility.stringIsNullOrEmpty(action)) {
                    action = org.miloss.fgsms.common.Constants.Undetermineable;
                }
            }
        }

        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable)) {
            try {
                Object j = messageContext.get(messageContext.WSDL_OPERATION);
                if (j != null) {
                    action = (String) j;
                    action = action.replace("\"", "");
                    action = action.replace("'", "");
                    if (Utility.stringIsNullOrEmpty(action)) {
                        action = org.miloss.fgsms.common.Constants.Undetermineable;
                    }
                }
            } catch (Exception ex) {
            }
        }

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
        //if (action.equalsIgnoreCase("urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy")) {
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.PCSaction2.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for PCS GetServicePolicy to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }


        ByteArrayOutputStream b = new ByteArrayOutputStream();
        String messagexml = "";
        try {
            msg.writeTo(b);
            messagexml = b.toString();
        } catch (Exception ex) {
            log.log(Level.WARN, "fgsms, error obtaining request message.", ex);
            messagexml = "fgsms was unable to obtain the request message.";
        } finally {
            try {
                b.close();
            } catch (Exception ex) {
            }
        }
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessRequest2 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
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
            Object j = messageContext.get(messageContext.HTTP_REQUEST_METHOD);
            if (j != null) {
                action = (String) j;
                if (Utility.stringIsNullOrEmpty(action)) {
                    action = org.miloss.fgsms.common.Constants.Undetermineable;
                }
                action = action.replace("\"", "");
                action = action.replace("'", "");
                if (Utility.stringIsNullOrEmpty(action)) {
                    action = org.miloss.fgsms.common.Constants.Undetermineable;
                }
            }
        }

        if (ctx == null && !client) {
            log.log(Level.WARN, "ctx object is unexpectedly null" + client);
        }

        String requestURL = "";
        if (Utility.stringIsNullOrEmpty(requestURL)) {
            try {
                Object j = messageContext.get("javax.xml.ws.service.endpoint.address");
                if (j != null) {
                    requestURL = (String) j;
                }
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting request url " + ex.getLocalizedMessage());
            }
        }
        if ((Utility.stringIsNullOrEmpty(requestURL)) && ctx != null) {
            try {
                StringBuffer buff = ctx.getRequestURL();
                requestURL = buff.toString();
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms error getting request url " + ex.getLocalizedMessage());

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
        if (Utility.stringIsNullOrEmpty(action)) {
            action = org.miloss.fgsms.common.Constants.Undetermineable;
        }

        action = action.replace("\"", "");
        action = action.replace("'", "");


        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessRequest3 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        String user = "";


        try {
            if (ctx != null && ctx.getAuthType() != null && ctx.getUserPrincipal() != null) {
                user = ctx.getUserPrincipal().getName();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "fgsms error getting user principal " + ex.getLocalizedMessage());
        }


        String ipaddress = "";
        if (client) {
            //outbound request from a client to a service
            //check if we are already within a transaction thread, if so, grab that thread id
            transactionthreadid = MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId());
            if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
                //if not, create a new one
                try {
                    transactionthreadid = UUID.randomUUID().toString();
                    //not sure why this was comment out, MessageProcessor.SetTransactionThreadId(Thread.currentThread().getId(), transactionthreadid);
                } catch (Exception ex) {
                    log.log(Level.WARN, "error caught build transaction thread id", ex);
                }

            }




            if (headers == null) {
                headers = new HashMap<String, List<String>>();
            }
            if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {
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

                DependencyHelper.insertRelatedMessageHeader(id.toString(), messageContext);
                DependencyHelper.insertThreadIdHeader(transactionthreadid, messageContext);
            }
            //IP address data
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
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessRequest4 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        String relatedTransaction = null;   //request

        if (!client) {
            //inbound request to a service
            //check for thread id
            //if not null, MP.setthreadid
            //check for related message id

            //Map<String, List<String>> m = (Map<String, List<String>>) messageContext.get(messageContext.HTTP_REQUEST_HEADERS);
            if (headers.containsKey(org.miloss.fgsms.common.Constants.relatedtransactionKey)) {
                relatedTransaction = ((List<String>) headers.get(org.miloss.fgsms.common.Constants.relatedtransactionKey)).get(0);
            }
            if (headers.containsKey(org.miloss.fgsms.common.Constants.transactionthreadKey)) {
                transactionthreadid = ((List<String>) headers.get(org.miloss.fgsms.common.Constants.transactionthreadKey)).get(0);
            }
            if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
                transactionthreadid = DependencyHelper.getThreadIdFromSoapHeader(messageContext);
            }
            if (Utility.stringIsNullOrEmpty(relatedTransaction)) {
                relatedTransaction = DependencyHelper.getRelatedMessageIdFromSoapHeader(messageContext);
            }
            if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
                transactionthreadid = UUID.randomUUID().toString();
            }
            try {
                MessageProcessor.getSingletonObject().setTransactionThreadId(Thread.currentThread().getId(), transactionthreadid);
            } catch (Exception ex) {
            }
        }
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessRequest5 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        HashMap headervalues = new HashMap();
        if (ctx != null && headers == null) {
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
        } else {
            if (headers != null) {
                Iterator<Entry<String, List<String>>> headersnames = headers.entrySet().iterator();

                while (headersnames.hasNext()) {
                    Entry<String, List<String>> s = headersnames.next();
                    headervalues.put(s.getKey(), s.getValue());
                    //   log.log(Level.INFO, "fgsms, Http header key: " + s + " value " + ctx.getHeader(s));
                }

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
        log.log(Level.DEBUG, "fgsms Message intercepted, this is a request message to " + requestURL + " with the SOAPAction of " + action + ". Assigning message id:" + id.toString() + " " + classname + " thread " + transactionthreadid);

        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessRequest6 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        try {
            MessageProcessor.getSingletonObject().processMessageInput(messagexml, messagexml.length(), requestURL, action, user, id.toString(), headervalues, ipaddress, classname, relatedTransaction, transactionthreadid);
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the MessageProcessor.ProcessMessageInput", ex);
        }
        isoneway = isoneway || DetermineOneWay(requestURL, messageContext, action);
        if (isoneway) {
            messageContext.put(org.miloss.fgsms.common.Constants.oneway, true);
            ProcessResponse(messageContext, false, client, classname);
        }
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessRequest6 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        //136ms most between 1 and 2
    }

    public static void ProcessResponse(SOAPMessageContext messageContext, boolean fault, boolean client, String classname) {
        long now = System.currentTimeMillis();
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessResponse0 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());

        boolean isoneway = messageContext.containsKey(org.miloss.fgsms.common.Constants.oneway);
        //if client, send in data including transport
        //if service, send in data for only processing time
        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
        
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
        String relatedTransaction = null;
        if (!ok) {
            if (id != null) {
                MessageProcessor.getSingletonObject().removeFromQueue(id);
            }
            return;
        }
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessResponse1 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        HashMap headervalues = new HashMap();


        if (client && !isoneway) {   //response
            try {
                Map headers = (Map) messageContext.get(messageContext.HTTP_RESPONSE_HEADERS);
                if (headers != null) {
                    log.log(Level.DEBUG, "client received " + headers.size() + " transaction " + id.toString());
                    //  Iterator it = headers.keySet().iterator();
                    Iterator<Entry<String, List<String>>> it = headers.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String, List<String>> s = (Entry<String, List<String>>) it.next();
                        //  log.log(Level.DEBUG, " client header " + s + " = " + headers.get(s).toString());
                        headervalues.put(s.getKey(), s.getValue());
                    }
                    if (headers.containsKey(org.miloss.fgsms.common.Constants.relatedtransactionKey)) {
                        relatedTransaction = (String) headers.get(org.miloss.fgsms.common.Constants.relatedtransactionKey);
                    }
                }
                /*
                 * if
                 * (messageContext.getMessage().getSOAPHeader().getChildElements(new
                 * QName("org.miloss.fgsms.agent",
                 * "fgsmsAgent")).hasNext()) { log.log(Level.INFO, "fgsms
                 * message has been flagged as being recorded by another agent
                 * via soap header. It will be ignored");
                 * MessageProcessor.RemoveFromQueue(id); return; }
                 */
            } catch (Exception ex) {
                log.log(Level.ERROR, "Unexpected error caught when searching for fgsms soap header", ex);
            }
            if (Utility.stringIsNullOrEmpty(relatedTransaction)) {
                relatedTransaction = DependencyHelper.getRelatedMessageIdFromSoapHeader(messageContext);
            }
        }
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessResponse2 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        if (!client && !isoneway) {
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
            Iterator<Entry<String, List<String>>> it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, List<String>> s = (Entry<String, List<String>>) it.next();
                //    log.log(Level.DEBUG, " service response header " + s + " = " + headers.get(s).toString());
                headervalues.put(s.getKey(), s.getValue());
            }
        }



        SOAPMessage msg = messageContext.getMessage();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        String messagexml = "";
        if (!isoneway) {
            try {
                msg.writeTo(b);
                messagexml = b.toString(Constants.CHARSET);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms, error obtaining response message.", ex);
                messagexml = "fgsms was unable to obtain response message.";
            } finally {
                try {
                    b.close();
                } catch (IOException ex) {
                }
            }
        }

        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessResponse3 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        String code2 = "200";
        try {
            Object j = messageContext.get(messageContext.HTTP_RESPONSE_CODE).toString();
            if (j != null) {
                code2 = j.toString();
                if (!code2.contains("200")) {
                    log.log(Level.DEBUG, "fgsms response code is " + code2 + " id = " + id.toString() + " fault was " + fault + " is now true");
                    fault = true;
                } else {
                    log.log(Level.DEBUG, "fgsms soap framework states that the response fault=" + fault);
                }
                headervalues.put("RESPONSE_CODE", code2);
            }
        } catch (Exception ex) {
        }

        
        log.log(Level.DEBUG, "fgsms Message intercepted, this is a response message transaction id:" + id.toString() + " fault=" + fault + " " + classname);
        
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessResponse4 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        MessageProcessor.getSingletonObject().processMessageOutput(id.toString(), messagexml, messagexml.length(), fault, Long.valueOf(System.currentTimeMillis()), headervalues, relatedTransaction);
        //log.log(Level.WARN, "fgsms Timer, outbound final" + (System.currentTimeMillis() - start));
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessResponse5 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        if (!client) {
            MessageProcessor.getSingletonObject().clearTransactionThreadId(Thread.currentThread().getId());
        }
        log.log(Level.TRACE, "JbossWSCommonMessageHandler.ProcessResponse6 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());

    }

    private static boolean DetermineOneWay(String requestURL, SOAPMessageContext messageContext, String action) {
        return OneWayJudge.determineOneWay(requestURL, messageContext, action);
    }

    
}
