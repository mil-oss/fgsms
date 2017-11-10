/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.agents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.jboss.soa.esb.couriers.CourierException;
import org.miloss.fgsms.common.Logger;;
import org.jboss.soa.esb.http.HttpRequest;
import org.jboss.soa.esb.message.body.content.TextBody;
import org.miloss.fgsms.agentcore.IMessageProcessor;

/**
 * This class will log a ton of internal Jboss ESB messages, most of it probably
 * isn't useful data, therefore it is deprecated
 * @author AO
 * use action pipeline processor instead
 */
@Deprecated 
public class JbossESBAgent extends org.jboss.soa.esb.filter.InputOutputFilter {

    private Logger log;

    public JbossESBAgent() {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    }

    /**
     * Called as the message flows towards the transport.
     * this is really on output!!!
     * http://docs.jboss.org/jbossesb/docs/4.2MR3/javadoc/esb/index.html?org/jboss/soa/esb/filter/InputOutputFilter.html
     * @param msg
     * @param params
     * @return
     * @throws CourierException
     */
    public org.jboss.soa.esb.message.Message onInput(org.jboss.soa.esb.message.Message msg,
            java.util.Map<java.lang.String, java.lang.Object> params)
            throws org.jboss.soa.esb.couriers.CourierException {
        log.log(Level.INFO, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());


        for (int i = 0; i < msg.getProperties().getNames().length; i++) {
            log.log(Level.DEBUG, "key = " + msg.getProperties().getNames()[i] + " value = " + msg.getProperties().getProperty(msg.getProperties().getNames()[i]));
        }
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                log.log(Level.DEBUG, "pkey = " + s + " pvalue = " + params.get(s).toString());
            }
        }

        //an output message could be a request, service or web app as a client OR it could be a response from this service, handle this


        String id = "";
        try {
            id = (String) msg.getProperties().getProperty("fgsms.TransactionID");
        } catch (Exception ex) {
            log.log(Level.ERROR, "fgsms Agent for JbossESB, could not obtain the transaction id, this was unexpected." + ex.getLocalizedMessage());
            return msg;
        }
        if (Utility.stringIsNullOrEmpty(id)) {
            log.log(Level.ERROR, "fgsms Agent for JbossESB, outbound message does not have transaction id, this was unexpected.");
            return msg;
        }

        //grab the original url from the gateway, useful but not fullproof
        String url = (String) msg.getProperties().getProperty("org.jboss.soa.esb.gateway.original.url");
        if (Utility.stringIsNullOrEmpty(url)) {
            try {
                //alternate method, but should always work
                url = msg.getHeader().getCall().getTo().getURI().toString();
                if (url.startsWith("invm://")) {
                    log.log(Level.WARN, "fgsms Agent for JbossESB, outbound message is internal esb traffic, ignoring " + url);
                    return msg;
                }
                log.log(Level.WARN, "fgsms Agent for JbossESB, outbound message for " + url + " via SOAP To field.");
            } catch (Exception ex) {
                log.log(Level.ERROR, "fgsms Agent for JbossESB, unable to determine the URL or TO field from the message, this message will be ignored");
                return msg;
            }
        } else {
            log.log(Level.INFO, "fgsms Agent for JbossESB, outbound message for " + url + " via transport.");
        }
        // search for endswith _reply to identify reply messages from queues
        if (url.endsWith("_reply")) {
        }


        Integer msgsize = null;
        try {
            msgsize = (Integer) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size");
            log.log(Level.INFO, "fgsms Agent for JbossESB, outbound message size " + msgsize + " via long.");
        } catch (Exception ex) {
        }
        try {
            msgsize = Integer.parseInt((String) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size"));
            log.log(Level.INFO, "fgsms Agent for JbossESB, outbound message size " + msgsize + " via string.");
        } catch (Exception ex) {
        }


        boolean fault = false;
        //getFault 'should' be for comms error, not app error
        if (msg.getFault() != null) {
            if (!Utility.stringIsNullOrEmpty(msg.getFault().getReason())
                    || msg.getFault().getCode() != null
                    || msg.getFault().getCause() != null) //faulted msg
            {
                fault = true;
                log.log(Level.WARN, "fgsms, this message to " + url + " transaction id:" + id.toString() + " has faulted.");
            }
        }

//        Long dod = (Long) params.get("org.jboss.soa.esb.message.time.dod");
        //      if (dod == null) {
        Long dod = System.currentTimeMillis();
        //    }

        if (msg.getAttachment().getNamedCount() > 0 || msg.getAttachment().getUnnamedCount() > 0) {
            log.log(Level.INFO, "fgsms Agent for JbossESB, outbound message has attachments, named: " + msg.getAttachment().getNamedCount() + " unnamed:" + msg.getAttachment().getUnnamedCount());
            int count = (msg.getAttachment().getNamedCount() + msg.getAttachment().getUnnamedCount());
            for (int i = 0; i < count; i++) {
                log.log(Level.INFO, "fgsms Agent for JbossESB, outbound message has attachments " + i + " type: " + msg.getAttachment().itemAt(i).getClass().getCanonicalName());
            }
        }


        String body = "";
        try {
            if (msg.getBody() instanceof TextBody) {
                TextBody t = (TextBody) msg.getBody();
                if (t != null) {
                    body = t.getText();
                    body += t.get().toString();
                }
            }
            if (msg.getBody() instanceof org.jboss.internal.soa.esb.message.format.xml.BodyImpl) {
                org.jboss.internal.soa.esb.message.format.xml.BodyImpl t = (org.jboss.internal.soa.esb.message.format.xml.BodyImpl) msg.getBody();
                log.log(Level.DEBUG, "body get is of type " + t.get().getClass().getCanonicalName());
                body = t.toString();//new String(t.getContents());
                body += t.get().toString();
            }
            if (msg.getBody() instanceof org.jboss.internal.soa.esb.message.format.serialized.BodyImpl) {
                org.jboss.internal.soa.esb.message.format.serialized.BodyImpl t = (org.jboss.internal.soa.esb.message.format.serialized.BodyImpl) msg.getBody();
                log.log(Level.DEBUG, "body get is of type " + t.get().getClass().getCanonicalName());
                body = t.toString();//new String(t.getContents());
                body += t.get().toString();
            }
            log.log(Level.INFO, "outbound messsage for " + url + " has a body of type " + msg.getBody().getClass().getName());
        } catch (Exception ex) {
            log.log(Level.WARN, "fgsms Agent for JbossESB, outbound message for " + url + " has a non-text body", ex);

        }

        IMessageProcessor mp = MessageProcessor.getSingletonObject();
        if (msgsize == null) {
            mp.processMessageOutput(id, body, body.length(), fault, Long.valueOf(dod),new HashMap());
        } else {
            mp.processMessageOutput(id, body, msgsize, fault, Long.valueOf(dod),new HashMap());
        }
        return msg;
    }

    /**
     * Called immediately after the message is received from the transport.
     * this is really on input!!
     * http://docs.jboss.org/jbossesb/docs/4.2MR3/javadoc/esb/index.html?org/jboss/soa/esb/filter/InputOutputFilter.html
     * @param msg
     * @param params
     * @return
     * @throws CourierException
     */
    public org.jboss.soa.esb.message.Message onOutput(org.jboss.soa.esb.message.Message msg,
            java.util.Map<java.lang.String, java.lang.Object> params)
            throws org.jboss.soa.esb.couriers.CourierException {
        log.log(Level.INFO, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
//an inbound message could be a request from a client OR it could be a response from another service, handle this
        
        //MessageID: used to uniquely identify this message. OPTIONAL, therefore just make a new one
        String id = UUID.randomUUID().toString();


        msg.getProperties().setProperty("fgsms.TransactionID", id);


        for (int i = 0; i < msg.getProperties().getNames().length; i++) {
            log.log(Level.DEBUG, "JbossESBAgent key = " + msg.getProperties().getNames()[i] + " value = " + msg.getProperties().getProperty(msg.getProperties().getNames()[i]));
        }
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                log.log(Level.DEBUG, "JbossESBAgent pkey = " + s + " pvalue = " + params.get(s).toString());
            }
        }


        HttpRequest request = HttpRequest.getRequest(msg);

        //grab the original url from the gateway, useful but not fullproof
        String url = (String) msg.getProperties().getProperty("org.jboss.soa.esb.gateway.original.url");

        
        //if we get the rreques value here, there's no way to get the current connection's port, bummer.
        
        /*  if (Utility.stringIsNullOrEmpty(url)) {
        if (request != null) {
        
        url = request.getScheme() + "://" + request.getServerName() + request.getRequestURI();
        }
        //   url = request.getProtocol() + request.getServerName() + request.getRequestURI() + request.getLocalAddr() + request.getLocalName()
        //            + request.getMethod() + request.getPathInfo() + request.getScheme() ;
        }*/


        if (Utility.stringIsNullOrEmpty(url)) {
            try {
                //alternate method, but should always work
                url = msg.getHeader().getCall().getTo().getURI().toString();
                if (url.startsWith("invm://")) {
                    log.log(Level.INFO, "fgsms Agent for JbossESB, inbound message is internal esb traffic " + url);
                    return msg;
                }
                log.log(Level.DEBUG, "fgsms Agent for JbossESB, inbound message for " + url + " via SOAP To field.");
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Agent for JbossESB, unable to determine the URL or TO field from the message, this message will be ignored");
                return msg;
            }
        } else {
            log.log(Level.INFO, "fgsms Agent for JbossESB, inbound message for " + url + " via transport.");
        }


        Integer msgsize = null;
        if (request != null) {
            msgsize = request.getContentLength();
            if (msgsize < 0) {
                msgsize = null;
            } else {
                log.log(Level.DEBUG, "fgsms Agent for JbossESB, inbound message size " + msgsize + " via request object.");
            }
        }
        if (msgsize == null) {
            try {
                msgsize = (Integer) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size");
                log.log(Level.DEBUG, "fgsms Agent for JbossESB, inbound message size " + msgsize + " via long.");
            } catch (Exception ex) {
            }
        }
        if (msgsize == null) {
            try {
                msgsize = Integer.parseInt((String) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size"));
                log.log(Level.DEBUG, "fgsms Agent for JbossESB, inbound message size " + msgsize + " via string.");
            } catch (Exception ex) {
            }
        }

        if (msg.getAttachment().getNamedCount() > 0 || msg.getAttachment().getUnnamedCount() > 0) {
            log.log(Level.DEBUG, "fgsms Agent for JbossESB, inbound message has attachments, named: " + msg.getAttachment().getNamedCount() + " unnamed:" + msg.getAttachment().getUnnamedCount());
            int count = (msg.getAttachment().getNamedCount() + msg.getAttachment().getUnnamedCount());
            for (int i = 0; i < count; i++) {
                log.log(Level.DEBUG, "fgsms Agent for JbossESB, inbound message has attachments " + i + " type: " + msg.getAttachment().itemAt(i).getClass().getCanonicalName());
            }
        }



        String action = null;
        if (request != null) {
            action=request.getHeaderValue("soapaction");
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }
        }
        if (Utility.stringIsNullOrEmpty(action)) {
            action = (String) msg.getProperties().getProperty("soapaction");
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }
        }
        if (Utility.stringIsNullOrEmpty(action)) {

            //Action: used by the sender to indicate the semantics of the message. Must be unique. MANDATORY
            if (msg != null && msg.getHeader() != null && msg.getHeader().getCall() != null
                    && msg.getHeader().getCall().getAction() != null) {
                action = msg.getHeader().getCall().getAction().toString();
                if (!Utility.stringIsNullOrEmpty(action)) {
                    action = action.replaceAll("\"", "");
                    action = action.replaceAll("\'", "");
                }
            }
        }

        if (Utility.stringIsNullOrEmpty(action))
        {
            action = "urn:undeterminable";
        }

        if (action==null)
            action = "urn:undeterminable";
         if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction3.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return msg;     //prevent recursive loops
        }
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction4.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return msg;     //prevent recursive loops
        }

        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return msg;     //prevent recursive loops
        }
        //if (action.equalsIgnoreCase("urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy")) {
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.PCSaction.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for PCS GetServicePolicy to prevent recursive looping. This is normal and no action is required.");
            return msg;     //prevent recursive loops
        }

        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction2.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return msg;     //prevent recursive loops
        }
        //if (action.equalsIgnoreCase("urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy")) {
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.PCSaction2.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for PCS GetServicePolicy to prevent recursive looping. This is normal and no action is required.");
            return msg;     //prevent recursive loops
        }


        log.log(Level.DEBUG, "fgsms Agent for JbossESB, inbound message for " + url + " action " + action);

        String sender = "";

        //sender is optional
        if (msg != null && msg.getHeader() != null && msg.getHeader().getCall() != null
                && msg.getHeader().getCall().getFrom() != null) {
            sender = msg.getHeader().getCall().getFrom().toString();

        }
        if (Utility.stringIsNullOrEmpty(sender) && request != null) {
            request.getRemoteUser();
        }


        String body = "";
        try {
            //msg.getBody could be one of several interfaces, for right now, just stick to text bodies
            ///org.jboss.internal.soa.esb.message.format.serialized.BodyImpl
            //org.jboss.internal.soa.esb.message.format.xml.BodyImpl
            if (msg.getBody() instanceof TextBody) {
                TextBody t = (TextBody) msg.getBody();
                if (t != null) {
                    body = t.getText();
                    body += t.get().toString();
                }
            }
            if (msg.getBody() instanceof org.jboss.internal.soa.esb.message.format.xml.BodyImpl) {
                org.jboss.internal.soa.esb.message.format.xml.BodyImpl t = (org.jboss.internal.soa.esb.message.format.xml.BodyImpl) msg.getBody();
                //body = new String(t.getContents());
                body = t.toString();// new String(t.getContents());
                log.log(Level.DEBUG, "body get is of type " + t.get().getClass().getCanonicalName());
                body += t.get().toString();
            }
            if (msg.getBody() instanceof org.jboss.internal.soa.esb.message.format.serialized.BodyImpl) {
                org.jboss.internal.soa.esb.message.format.serialized.BodyImpl t = (org.jboss.internal.soa.esb.message.format.serialized.BodyImpl) msg.getBody();
                body = t.toString();//new String(t.getContents());
                //body = new String(t.getContents());
                log.log(Level.DEBUG, "body get is of type " + t.get().getClass().getCanonicalName());
                body += t.get().toString();
            }
            // log.log(Level.WARN, "fgsms Agent for JbossESB, inbound message for " + url + " has body that I don't know how to identify of type " + msg.getBody().getClass().getName());
        } catch (Exception ex) {
            log.log(Level.WARN, "fgsms Agent for JbossESB, inbound message for " + url + " has body that I don't know how to identify of type " + msg.getBody().getClass().getName());
        }

        if (Utility.stringIsNullOrEmpty(action)) {
            action = Utility.getActionNameFromXML(body);
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }
        }
        if (Utility.stringIsNullOrEmpty(action)) {
            if (request != null) {
                action = request.getMethod();
            }
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }
            //action = "urn:undeterminable";
        }


        if (Utility.stringIsNullOrEmpty(action)) {
            action = "urn:undeterminable";
        }

        String ip = null;
        if (request != null) {
            ip = request.getRemoteAddr();
        }
        if (msgsize == null) {
            MessageProcessor.getSingletonObject().processMessageInput(body, body.length(), url, action, sender, id, null, ip, JbossESBAgent.class.getCanonicalName(), null, null);
        } else {
            MessageProcessor.getSingletonObject().processMessageInput(body, msgsize, url, action, sender, id, null, ip, JbossESBAgent.class.getCanonicalName(), null, null);
        }
        return msg;
    }
}
