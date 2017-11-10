/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular file,
 * then You may include the notice in a location (such as a LICENSE file in a
 * relevant directory) where a recipient would be likely to look for such a
 * notice.
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.Soap12;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.agentcore.FgsmsSoapHeaderConstants;
import org.miloss.fgsms.common.Utility;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.Binding;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.databinding.DataBinding;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.IMessageProcessor;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Logger;;
import org.w3c.dom.Element;

/**
 * provides a wrapper that sends data to the fgsms message processor. remember,
 * this can be used for any cxf service, from jaxws, jaxrs and others make no
 * assumptions and check for nulls
 *
 * @author AO
 */
public class CXFCommonMessageHandler {

    static boolean DEBUG = false;
    static final Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    /**
     *
     * @param messageContext
     * @param client
     */
    static void ProcessRequest(Message messageContext, boolean client, String classname) {
         if (DEBUG) {
             System.out.println("Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
             System.out.println("Message Processor " + MessageProcessor.getSingletonObject().getPolicyCache() + "," + MessageProcessor.getSingletonObject().internalMessageMapSize() +","+ MessageProcessor.getSingletonObject().outboundQueueSize());
         }
        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
        long start = System.currentTimeMillis();

        //handle client requests for @OneWay methods
        BindingMessageInfo bi = (BindingMessageInfo) messageContext.get("org.apache.cxf.service.model.BindingMessageInfo");
        boolean isoneway = false;
        if (bi != null) {
            BindingOperationInfo bindingOperation = bi.getBindingOperation();
            if (bindingOperation != null) {
                BindingMessageInfo output = bindingOperation.getOutput();
                if (output == null) {
                    isoneway = true;
                }
            }
        }
        //handle service requests for @OneWay methods
        if (messageContext.containsKey("org.apache.cxf.interceptor.OneWayProcessorInterceptor")) {
            isoneway = true;
        }


        UUID id = UUID.randomUUID();
        messageContext.getExchange().put(org.miloss.fgsms.common.Constants.key, id);

        try {
            IMessageProcessor mp = MessageProcessor.getSingletonObject();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the Message Processor singleton object", ex);
        }
        String transactionthreadid = "";
        HttpServletRequest ctx = null;
        try {
            ctx = (HttpServletRequest) messageContext.get(AbstractHTTPDestination.HTTP_REQUEST);
        } catch (Exception ex) {
        }

        log.log(Level.DEBUG, "fgsms message context is of type " + messageContext.getClass().getName());

        String action = org.miloss.fgsms.common.Constants.Undetermineable;
        java.util.Map headers = new HashMap();
        try { //doesn't look like cxf uses this headers.putAll((Map)
            Map m = (Map) messageContext.get(Message.MIME_HEADERS);
            headers.putAll(m);
        } catch (Exception ex) {
        }
        try {
            headers.putAll((Map) messageContext.get(Message.PROTOCOL_HEADERS));
        } catch (Exception ex) {
        }
        try {
            List l = (List) headers.get("SOAPAction");
            if (l != null && !l.isEmpty()) {
                action = (String) l.get(0);
            }
            l = (List) headers.get("Soapaction");
            if (l != null && !l.isEmpty()) {
                action = (String) l.get(0);
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error getting outbound soap action", ex);
        }

        if (action != null) {
            action = action.replace("\"", "");
            action = action.replace("'", "");
        } else {
            action = org.miloss.fgsms.common.Constants.Undetermineable;
        }
        if (action==null || action.trim().length()==0)
            action = org.miloss.fgsms.common.Constants.Undetermineable;

        //for CXF web services
        if (action.equals(org.miloss.fgsms.common.Constants.Undetermineable)) {
            try {
                BindingOperationInfo bindingOperationInfo = messageContext.getExchange().getBindingOperationInfo();
                if (bindingOperationInfo != null && bindingOperationInfo.getOperationInfo() != null) {
                    action = (bindingOperationInfo.getOperationInfo().getName().toString());
                }
            } catch (Exception ex) {
            }
        }
        boolean isrest = false;
        //for CXF REST services
        if (Utility.stringIsNullOrEmpty(action) || action.equals(org.miloss.fgsms.common.Constants.Undetermineable)) {
            Exchange exchange = messageContext.getExchange();//"=> getOrder"

            try {
                action = (String) exchange.get("org.apache.cxf.resource.operation.name");
            } catch (Exception ex) {
            }
            if (Utility.stringIsNullOrEmpty(action)) {
                action = org.miloss.fgsms.common.Constants.Undetermineable;
            }
            Binding binding = exchange.getBinding();
            BindingInfo bindingInfo = binding.getBindingInfo();
            if (binding.getBindingInfo().getBindingId().equalsIgnoreCase("http://apache.org/cxf/binding/jaxrs")) {
                isrest = true;
            }
            /*System.out.println(bindingInfo.getName().toString());
            Destination destination = exchange.getDestination();
            System.out.println(destination.getAddress().toString());
            //destination.
            Endpoint endpoint = exchange.getEndpoint();
            System.out.println(endpoint.toString());
            Service service = exchange.getService();
            System.out.println(service.getName().toString());
*/

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
        //if (action.equalsIgnoreCase("urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy")) {
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

                requestURL = (String) messageContext.get("org.apache.cxf.request.url");
                if (Utility.stringIsNullOrEmpty(requestURL)) {
                    requestURL = (String) messageContext.get(Message.ENDPOINT_ADDRESS);
                }
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
        if (!client) {
            if (isrest) {
                //if REST service?

                requestURL = (String) messageContext.get("http.base.path");
                requestURL += (String) messageContext.get("org.apache.cxf.message.Message.BASE_PATH");// => /restTest");
            }
            //"org.apache.cxf.request.url => http://localhost:9000/restTest/customerservice/orders/223/products/323"
            //"http.base.path => http://localhost:9000"
        }

        if (Utility.stringIsNullOrEmpty(requestURL)) {
            requestURL = "urn:" + MessageProcessor.getSingletonObject().getHostName() + ":undeterminable";
        }
//HTTP.REQUEST 
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        String messagexml = "";
        if (MessageProcessor.getSingletonObject().shouldAgentRecordRequestContent(requestURL))
        {

            try {

                boolean gotPayload = false;
                InputStream buff = messageContext.getContent(InputStream.class);
                if (buff == null) {
                    messagexml = "";
                } else {
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int len = buff.read(buffer);
                    while (len > 0) {
                        String data = new String(buffer, 0, len, Constants.CHARSET);
                        sb.append(data);
                        len = buff.read(buffer);
                    }
                    if (buff.markSupported()) {
                        buff.reset();
                    }
                    messagexml = sb.toString();
                    if (messagexml.length()>0)
                        gotPayload = true;
                }
                if (!gotPayload) {
                    try {
                        List list = messageContext.getContent(List.class);
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                //this is a list of deserialized objects (pojos)
                               //  if (DEBUG) 
                               // System.out.println("###################### " + list.get(i).toString());
                                StringWriter sw = new StringWriter();
                                JAXB.marshal(list.get(i), sw);
                                messagexml = sw.toString();
                                gotPayload = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        log.log(Level.WARN, "fgsms, error obtaining request message.", e);
                    }
                }
                if (!gotPayload) {
                    log.log(Level.INFO, "Payload not obtainable");
                }
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

        log.log(Level.DEBUG, "fgsms Agent for CXF, inbound message for " + requestURL + " action " + action);
        //log.log(Level.WARN, "fgsms Timer, inbound setup4 " + (System.currentTimeMillis() - start));
        //special case

        String user = "";

        try {
            if (ctx != null) {
                user = ctx.getRemoteUser();
            }
            if (ctx != null && ctx.getAuthType() != null && ctx.getUserPrincipal() != null) {

                user += ctx.getUserPrincipal().getName();
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
                Map realheaders = (Map) messageContext.get(Message.PROTOCOL_HEADERS);
                if (realheaders == null) {
                    realheaders = new HashMap();
                }
                ArrayList t = new ArrayList();
                t.add(transactionthreadid);
                realheaders.put(org.miloss.fgsms.common.Constants.transactionthreadKey, t);
                t = new ArrayList();
                t.add(id.toString());
                realheaders.put(org.miloss.fgsms.common.Constants.relatedtransactionKey, t);
                //append to http header, transactionthreadid
                //append to http header, id
                messageContext.remove(Message.PROTOCOL_HEADERS);
                messageContext.put(Message.PROTOCOL_HEADERS, realheaders);
                try {
                    List<Header> soapheaders = (List<Header>) messageContext.get(Header.HEADER_LIST);
                    if (soapheaders != null) {
                        DataBinding db = new JAXBDataBinding(String.class);
                        soapheaders.add(new SoapHeader(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.related_message_localname), id.toString(), db));
                        soapheaders.add(new SoapHeader(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.threadid_message_localname), transactionthreadid, db));
                    }
                } catch (Exception ex) {
                    log.log(Level.DEBUG, null, ex);
                }
            }

            try {
                InetAddress addr = InetAddress.getLocalHost();
                ipaddress = addr.getHostAddress();
            } catch (UnknownHostException ex) {
                log.log(Level.ERROR, "error obtaining local ip address", ex);
            }
        } else if (ctx != null) {
            ipaddress = ctx.getRemoteAddr();
        }
        String relatedTransaction = null;

        if (!client) {
            //inbound request to a service
            //check for thread id
            //if not null, MP.setthreadid
            //check for related message id
            Map realheaders = (Map) messageContext.get(Message.PROTOCOL_HEADERS);
            if (realheaders != null) {
                if (realheaders.containsKey(org.miloss.fgsms.common.Constants.relatedtransactionKey)) {
                    relatedTransaction = ((List<String>) realheaders.get(org.miloss.fgsms.common.Constants.relatedtransactionKey)).get(0);
                }
                if (realheaders.containsKey(org.miloss.fgsms.common.Constants.transactionthreadKey)) {
                    transactionthreadid = ((List<String>) realheaders.get(org.miloss.fgsms.common.Constants.transactionthreadKey)).get(0);
                }
            }
            List<Header> soapheaders = (List<Header>) messageContext.get(Header.HEADER_LIST);
            if (soapheaders != null && !soapheaders.isEmpty()) {
                //soapheaders.add(new SoapHeader(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.related_message_localname), id.toString()));
                //soapheaders.add(new SoapHeader(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.threadid_message_localname), transactionthreadid));
                for (int k = 0; k < soapheaders.size(); k++) {
                    if (soapheaders.get(k).getName().getNamespaceURI().equalsIgnoreCase(FgsmsSoapHeaderConstants.namespace)
                            && soapheaders.get(k).getName().getLocalPart().equalsIgnoreCase(FgsmsSoapHeaderConstants.related_message_localname)) {
                        if (Utility.stringIsNullOrEmpty(relatedTransaction)) {
                            relatedTransaction = soapheaders.get(k).getObject().toString();
                        }
                    } else if (soapheaders.get(k).getName().getNamespaceURI().equalsIgnoreCase(FgsmsSoapHeaderConstants.namespace)
                            && soapheaders.get(k).getName().getLocalPart().equalsIgnoreCase(FgsmsSoapHeaderConstants.threadid_message_localname)) {
                        if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
                            transactionthreadid = soapheaders.get(k).getObject().toString();
                        }
                    }
                }

            }

            if (transactionthreadid == null) {
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
                log.log(Level.DEBUG, "fgsms error getting http headers " + ex.getMessage(),ex);
            }
        }
        headervalues.putAll(headers);
        //log.log(Level.WARN, "fgsms Timer, inbound setup8 " + (System.currentTimeMillis() - start));

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
        //    log.log(Level.INFO, "fgsms,request url: " + requestURL);
        messageContext.getExchange().put(org.miloss.fgsms.common.Constants.urlKEY, requestURL);

        log.log(Level.DEBUG, "fgsms Message intercepted, this is a request message to " + requestURL + " with the SOAPAction of " + action + ". Assigning message id:" + id.toString());
          //   log.log(Level.WARN, "fgsms Timer, inbound setup9 " + (System.currentTimeMillis() - start));

        try {
            MessageProcessor.getSingletonObject().processMessageInput(messagexml, messagexml.length(), requestURL, action, user, id.toString(), headervalues, ipaddress, classname, relatedTransaction, transactionthreadid);
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the MessageProcessor.processMessageInput", ex);
        }
        if (isoneway) {
            //it's a one way message,  what do we do?
            messageContext.put(org.miloss.fgsms.common.Constants.oneway, true);
            ProcessResponse(messageContext, false, client, classname);
        }
        log.log(Level.DEBUG, "estimated additional latency " + (System.currentTimeMillis() - start));

    }

    static void ProcessResponse(Message messageContext, boolean fault, boolean client, String classname) {

        if (DEBUG) {
             System.out.println("Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
             System.out.println("Message Processor " + MessageProcessor.getSingletonObject().getPolicyCache() + "," + MessageProcessor.getSingletonObject().internalMessageMapSize() +","+ MessageProcessor.getSingletonObject().outboundQueueSize());
         }
        
        //log.log(Level.INFO, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());

        Bus bus = BusFactory.getDefaultBus();
        long start = System.currentTimeMillis();

        UUID id = (UUID) messageContext.getExchange().get(org.miloss.fgsms.common.Constants.key);
        boolean isoneway = messageContext.containsKey(org.miloss.fgsms.common.Constants.oneway);

        String requrl = (String) messageContext.getExchange().get(org.miloss.fgsms.common.Constants.urlKEY);

        boolean ok = true;
        if (id == null) {
            log.log(Level.DEBUG, "fgsms reply message did not have context variable " + org.miloss.fgsms.common.Constants.key + " added. This transaction will be ignored.");
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(requrl)) {
            log.log(Level.DEBUG, "fgsms reply message did not have context variable " + org.miloss.fgsms.common.Constants.urlKEY + " added. This transaction will be ignored.");
            ok = false;
        }
        bus.getProperties().remove(org.miloss.fgsms.common.Constants.key);
        bus.getProperties().remove(org.miloss.fgsms.common.Constants.urlKEY);

        if (!ok) {
            if (id != null) {
                MessageProcessor.getSingletonObject().removeFromQueue(id);
            }
            return;
        }

        java.util.HashMap headervalues = new HashMap();
        try { //doesn't look like cxf uses this headers.putAll((Map)
            Map m = (Map) messageContext.get(Message.MIME_HEADERS);
            headervalues.putAll(m);
        } catch (Exception ex) {
        }
        try {
            headervalues.putAll((Map) messageContext.get(Message.PROTOCOL_HEADERS));
        } catch (Exception ex) {
        }
        try{
            HttpServletResponse get = (HttpServletResponse) messageContext.get(AbstractHTTPDestination.HTTP_RESPONSE);
            if (get!=null) {
                Iterator<String> iterator = get.getHeaderNames().iterator();
                while (iterator.hasNext()) {
                    String key=iterator.next();
                    String val=get.getHeader(key);
                    if (val!=null)
                        headervalues.put(key, val);
                }
            }
        }catch (Exception ex) {
            
        }
        String relatedTransaction = null;
        {
            List<Header> soapheaders = (List<Header>) messageContext.get(Header.HEADER_LIST);
            if (soapheaders != null) {
                for (int k = 0; k < soapheaders.size(); k++) {
                    if (soapheaders.get(k).getName().getNamespaceURI().equalsIgnoreCase(FgsmsSoapHeaderConstants.namespace)
                            && soapheaders.get(k).getName().getLocalPart().equalsIgnoreCase(FgsmsSoapHeaderConstants.related_message_localname)) {
                        if (Utility.stringIsNullOrEmpty(relatedTransaction)) {
                            if (soapheaders.get(k).getObject() instanceof String) {
                                relatedTransaction = (String) soapheaders.get(k).getObject();
                            } else if (soapheaders.get(k).getObject() instanceof Element) {
                                Element e = (Element) soapheaders.get(k).getObject();
                                relatedTransaction = e.getTextContent();
                            } else {
                                log.debug("unhandled cases for related transaction id " + soapheaders.get(k).getObject().getClass().getCanonicalName());
                            }
                        }
                    }
                }
            }
        }

        if (client && !isoneway) {

            try {
                Map headers = (Map) messageContext.get(Message.PROTOCOL_HEADERS);
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

        if (!client && !isoneway) {
            //service response, insert fgsms headers
            ArrayList<String> l = new ArrayList<String>();
            l.add(id.toString());
            ArrayList<String> threadlist = new ArrayList<String>();
            threadlist.add(MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId()));
            Map headers = (Map) messageContext.get(Message.PROTOCOL_HEADERS);
            if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {

                List<Header> soapheaders = (List<Header>) messageContext.get(Header.HEADER_LIST);
                try {
                    DataBinding b = new JAXBDataBinding(String.class);
                    if (soapheaders != null) {
                        soapheaders.add(new SoapHeader(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.related_message_localname), id.toString(), b));
                        soapheaders.add(new SoapHeader(new QName(FgsmsSoapHeaderConstants.namespace, FgsmsSoapHeaderConstants.threadid_message_localname), MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId()), b));
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                }
                if (headers != null) {
                    headers.put(org.miloss.fgsms.common.Constants.relatedtransactionKey, l);
                } else {
                    headers = new HashMap<String, List<String>>();
                    headers.put(org.miloss.fgsms.common.Constants.relatedtransactionKey, l);
                    headers.put(org.miloss.fgsms.common.Constants.transactionthreadKey, threadlist);
                    messageContext.remove(Message.PROTOCOL_HEADERS);
                    messageContext.put(messageContext.PROTOCOL_HEADERS, headers);
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

        }

        String messagexml = "";
        if (!isoneway) {
            try {
                boolean gotPayload = false;
                InputStream buff = messageContext.getContent(InputStream.class);
                if (buff != null) {
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int len = buff.read(buffer);
                    if (len < 0) {
                        gotPayload = false;
                    } else {
                        while (len > 0) {
                            String data = new String(buffer, 0, len, Constants.CHARSET);
                            sb.append(data);
                            len = buff.read(buffer);
                        }
                        if (buff.markSupported()) {
                            buff.reset();
                        }
                        messagexml = sb.toString();
                        gotPayload = true;
                    }
                }
                if (!gotPayload) {
                    List list = messageContext.getContent(List.class);
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            StringWriter sw = new StringWriter();
                            JAXB.marshal(list.get(i), sw);
                            messagexml = sw.toString();
                            gotPayload = true;
                            break;
                        }
                    }
                }
                if (!gotPayload && messageContext.containsKey("org.apache.cxf.binding.soap.SoapVersion")) {
                    Object obj = messageContext.get("org.apache.cxf.binding.soap.SoapVersion");
                    if (obj instanceof Soap11) {
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(obj, sw);
                        messagexml = sw.toString();
                        gotPayload = true;
                    } else if (obj instanceof Soap12) {
                        StringWriter sw = new StringWriter();
                        JAXB.marshal(obj, sw);
                        messagexml = sw.toString();
                        gotPayload = true;
                    }
                }
                if (!gotPayload) {
                    log.log(Level.WARN, "fgsms, couldn't get payload.");
                }
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms, error obtaining request message.", ex);
                messagexml = "fgsms was unable to obtain the request message.";
            }
        } else {
            //one way transactions have no response, therefore there's nothing to read
        }

        String code2 = "200";
        //  boolean fault = false;
        try {
            Object j = messageContext.get(Message.RESPONSE_CODE).toString();
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

        //    fault = fault;
        log.log(Level.DEBUG, "fgsms Message intercepted, this is a response message transaction id:" + id.toString() + " fault=" + fault + " " + classname);

        //log.log(Level.WARN, "fgsms Timer, outbound setup" + (System.currentTimeMillis() - start));
        MessageProcessor.getSingletonObject().processMessageOutput(id.toString(), messagexml, messagexml.length(), fault, Long.valueOf(System.currentTimeMillis()), headervalues, relatedTransaction);
        //log.log(Level.WARN, "fgsms Timer, outbound final" + (System.currentTimeMillis() - start));
        if (!client) {
            MessageProcessor.getSingletonObject().clearTransactionThreadId(Thread.currentThread().getId());
        }
        log.log(Level.DEBUG, "estimated additional latency " + (System.currentTimeMillis() - start));
    }
}
