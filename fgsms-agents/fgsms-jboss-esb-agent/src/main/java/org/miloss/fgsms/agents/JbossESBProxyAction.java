package org.miloss.fgsms.agents;

import java.util.HashMap;
import java.util.UUID;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jboss.soa.esb.ConfigurationException;
import org.jboss.soa.esb.actions.ActionLifecycleException;
import org.jboss.soa.esb.actions.ActionProcessingException;
import org.jboss.soa.esb.helpers.ConfigTree;
import org.jboss.soa.esb.http.HttpHeader;
import org.jboss.soa.esb.http.HttpRequest;
import org.jboss.soa.esb.http.HttpResponse;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.body.content.TextBody;

/**
 * For Jboss ESB deployments. trigger this BEFORE doing any kind of work
 *
 * @author AO
 */
public class JbossESBProxyAction implements org.jboss.soa.esb.actions.ActionPipelineProcessor {

    private Logger log;

    public JbossESBProxyAction(ConfigTree config) throws ConfigurationException {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

        httpport = config.getAttribute("http_port", "80");
        httpsport = config.getAttribute("https_port", "443");

    }
    private String httpport = "80";
    private String httpsport = "443";

    public Message process(Message msg) throws ActionProcessingException {
        
        //JbossESB had a MessageID property: used to uniquely identify this message. OPTIONAL
        //Since they consider it optional, I'm relying on my own
        String id = UUID.randomUUID().toString();


        msg.getProperties().setProperty("fgsms.TransactionID", id);

        /*
         * for (int i = 0; i < msg.getProperties().getNames().length; i++) {
         * log.log(Level.DEBUG, "key = " + msg.getProperties().getNames()[i] + "
         * value = " +
         * msg.getProperties().getProperty(msg.getProperties().getNames()[i]));
        }
         */

        String relatedTransaction = "";
        String transactionthreadid = "";

        HttpRequest request = HttpRequest.getRequest(msg);

        HashMap headers = new HashMap();
        int relatedIndex=-1;
        if (request != null && !request.getHeaders().isEmpty()) {
            for (int i = 0; i < request.getHeaders().size(); i++) {
                headers.put(request.getHeaders().get(i).getName(), request.getHeaders().get(i).getValue());
                if (request.getHeaders().get(i).getName().equalsIgnoreCase(org.miloss.fgsms.common.Constants.relatedtransactionKey)) {
                    //represents that this transaction was recorded somewhere else before I got it, so we should track this
                    relatedTransaction = request.getHeaders().get(i).getValue();
                   relatedIndex = i;
                }
                if (request.getHeaders().get(i).getName().equalsIgnoreCase(org.miloss.fgsms.common.Constants.transactionthreadKey)) {
                    transactionthreadid = request.getHeaders().get(i).getValue();
                }

            }
            if (relatedIndex >=0 && MessageProcessor.getSingletonObject().isDependencyInjectionEnabled())
            {
                //replace the related message id with the id associated with *this
                request.getHeaders().remove(relatedIndex);
                request.getHeaders().add(new HttpHeader(org.miloss.fgsms.common.Constants.relatedtransactionKey, id));
            }
        }



        //msg.getProperties().setProperty(org.miloss.fgsms.common.Constants.relatedtransactionKey, relatedTransaction);

        if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
            transactionthreadid = UUID.randomUUID().toString();
            if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled() && request !=null)
            {
                //insert http header
                request.getHeaders().add(new HttpHeader(org.miloss.fgsms.common.Constants.transactionthreadKey, transactionthreadid));
                //insert soap header if the message is soap
            }
        }
        try {
            MessageProcessor.getSingletonObject().setTransactionThreadId(Thread.currentThread().getId(), transactionthreadid);
        } catch (Exception ex) {
        }
        
        



        //request.getHeaders()
        //    String httpport = org.jboss.soa.esb.common.Configuration.getHttpPort();
        //    String httpsport = org.jboss.soa.esb.common.Configuration.getHttpSecurePort();


        //grab the original url from the gateway, useful but not fullproof
        String url = (String) msg.getProperties().getProperty("org.jboss.soa.esb.gateway.original.url");
        if (Utility.stringIsNullOrEmpty(url)) {
            if (request != null) {

                if (request.getScheme().equalsIgnoreCase("http")) {
                    url = request.getScheme() + "://" + request.getServerName() + ":" + httpport + request.getRequestURI();
                } else if (request.getScheme().equalsIgnoreCase("https")) {
                    url = request.getScheme() + "://" + request.getServerName() + ":" + httpsport + request.getRequestURI();
                }
            }
            //   url = request.getProtocol() + request.getServerName() + request.getRequestURI() + request.getLocalAddr() + request.getLocalName()
            //            + request.getMethod() + request.getPathInfo() + request.getScheme() ;
        }

        if (Utility.stringIsNullOrEmpty(url)) {
            try {
                //alternate method, but should always work
                url = msg.getHeader().getCall().getTo().getURI().toString();
                if (url.startsWith("invm://")) {
                    log.log(Level.INFO, "fgsms Agent for JbossESBProxyAgent, inbound message is internal esb traffic " + url);
                    return msg;
                }
                log.log(Level.DEBUG, "fgsms Agent for JbossESBProxyAgent, inbound message for " + url + " via SOAP To field.");
            } catch (Exception ex) {
//                log.log(Level.WARN, "fgsms Agent for JbossESBProxyAgent, unable to determine the URL or TO field from the message, this message will be ignored");
                //               return msg;
            }
        }
        //else {
        //  log.log(Level.DEBUG, "fgsms Agent for JbossESBProxyAgent, inbound message for " + url + " via transport.");
        // }


        if (Utility.stringIsNullOrEmpty(url)) {
            log.log(Level.WARN, "untable to determine request url, message will be ignored.");
            return msg;
        }

        Integer msgsize = null;
        try {
            msgsize = (Integer) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size");
            log.log(Level.DEBUG, "fgsms Agent for JbossESBProxyAgent, inbound message size " + msgsize + " via long.");
        } catch (Exception ex) {
        }
        try {
            msgsize = Integer.parseInt((String) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size"));
            log.log(Level.DEBUG, "fgsms Agent for JbossESBProxyAgent, inbound message size " + msgsize + " via string.");
        } catch (Exception ex) {
        }

        if (msg.getAttachment().getNamedCount() > 0 || msg.getAttachment().getUnnamedCount() > 0) {
            log.log(Level.INFO, "fgsms Agent for JbossESBProxyAgent, inbound message has attachments, named: " + msg.getAttachment().getNamedCount() + " unnamed:" + msg.getAttachment().getUnnamedCount());
            int count = (msg.getAttachment().getNamedCount() + msg.getAttachment().getUnnamedCount());
            for (int i = 0; i < count; i++) {
                log.log(Level.INFO, "fgsms Agent for JbossESBProxyAgent, inbound message has attachments " + i + " type: " + msg.getAttachment().itemAt(i).getClass().getCanonicalName());
            }
        }



        String action = "";;
        if (request != null) {
            // request.getHeaders().add(new HttpHeader("TESTHeader","TESTvalue"));
            action = request.getHeaderValue("soapaction");
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }

            if (Utility.stringIsNullOrEmpty(action)) {
                action = request.getHeaderValue("SOAPAction");
                if (!Utility.stringIsNullOrEmpty(action)) {
                    action = action.replaceAll("\"", "");
                    action = action.replaceAll("\'", "");
                }
            }
        }
        if (Utility.stringIsNullOrEmpty(action)) {
            action = (String) msg.getProperties().getProperty("SOAPAction");
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
        if (MessageProcessor.getSingletonObject().shouldAgentRecordRequestContent(url)) {
            try {
                //msg.getBody could be one of several interfaces, for right now, just stick to text bodies
                ///org.jboss.internal.soa.esb.message.format.serialized.BodyImpl
                //org.jboss.internal.soa.esb.message.format.xml.BodyImpl
                if (msg.getBody() instanceof TextBody) {
                    TextBody t = (TextBody) msg.getBody();
                    if (t != null) {
                        //  log.log(Level.WARN, "*******getText()**********");
                        //   log.log(Level.WARN, t.getText());
                        //   log.log(Level.WARN, "*******get().toString()***********");
                        //   log.log(Level.WARN, t.get().toString());
                        // body = t.getText();
                        body += t.get().toString();
                    }
                }
                if (msg.getBody() instanceof org.jboss.internal.soa.esb.message.format.xml.BodyImpl) {
                    org.jboss.internal.soa.esb.message.format.xml.BodyImpl t = (org.jboss.internal.soa.esb.message.format.xml.BodyImpl) msg.getBody();
                    //body = new String(t.getContents());
                    //  body = t.toString();// new String(t.getContents());
                    log.log(Level.DEBUG, "body get is of type " + t.get().getClass().getCanonicalName());
                    body += t.get().toString();
                }
                if (msg.getBody() instanceof org.jboss.internal.soa.esb.message.format.serialized.BodyImpl) {
                    org.jboss.internal.soa.esb.message.format.serialized.BodyImpl t = (org.jboss.internal.soa.esb.message.format.serialized.BodyImpl) msg.getBody();
                    // body = t.toString();//new String(t.getContents());
                    //body = new String(t.getContents());
                    log.log(Level.DEBUG, "body get is of type " + t.get().getClass().getCanonicalName());
                    body += t.get().toString();
                }
                // log.log(Level.WARN, "fgsms Agent for JbossESBProxyAgent, inbound message for " + url + " has body that I don't know how to identify of type " + msg.getBody().getClass().getName());
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Agent for JbossESBProxyAgent, inbound message for " + url + " has body that I don't know how to identify of type " + msg.getBody().getClass().getName());
            }
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
            //action = org.miloss.fgsms.common.Constants.Undetermineable;
        }

        if (Utility.stringIsNullOrEmpty(action)) {
            action = org.miloss.fgsms.common.Constants.Undetermineable;
        }

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

        log.log(Level.INFO, "fgsms Agent for JbossESBProxyAgent, inbound message for " + url + " action " + action);

//        Long dob = (Long) msg.getProperties().getProperty("org.jboss.soa.esb.message.time.dob");
        //if (dob == null) {
        //    long dob = System.currentTimeMillis();
        //}//public static void processMessageInput(String XMLrequest, String url, String soapAction, String HttpUsername, String HashCode,
        //HashMap headers, String ipaddress, String agentclassname) {
        String ip = "";
        if (request != null) {
            ip = request.getRemoteAddr();
        }
        if (msgsize == null) {
            MessageProcessor.getSingletonObject().processMessageInput(body, body.length(), url, action, sender, id, headers, ip, JbossESBProxyAction.class.getCanonicalName(), relatedTransaction, transactionthreadid);
        } else {
            //related
            //threadid
            MessageProcessor.getSingletonObject().processMessageInput(body, msgsize, url, action, sender, id, headers, ip, JbossESBProxyAction.class.getCanonicalName(), relatedTransaction, transactionthreadid);
        }
        return msg;
    }

    /**
     * this is unlikely to ever be called
     * @param msg
     * @param thrwbl 
     */
    public void processException(Message msg, Throwable thrwbl) {

        log.log(Level.INFO, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());


        for (int i = 0; i < msg.getProperties().getNames().length; i++) {
            log.log(Level.DEBUG, "key = " + msg.getProperties().getNames()[i] + " value = " + msg.getProperties().getProperty(msg.getProperties().getNames()[i]));
        }


        //Serializable obj = (Serializable) msg.getBody().get();
        String id = "";
        try {
            id = (String) msg.getProperties().getProperty("fgsms.TransactionID");
        } catch (Exception ex) {
            log.log(Level.ERROR, "fgsms Agent for JbossESBPostProxyAgent, could not obtain the transaction id, this was unexpected." + ex.getLocalizedMessage());
            return;// msg;
        }
        if (Utility.stringIsNullOrEmpty(id)) {
            log.log(Level.ERROR, "fgsms Agent for JbossESBPostProxyAgent, outbound message does not have transaction id, this was unexpected.");
            return;// msg;
        }

        HttpRequest request = HttpRequest.getRequest(msg);
        HttpResponse response = HttpResponse.getResponse(msg);


        //grab the original url from the gateway, useful but not fullproof
        String url = (String) msg.getProperties().getProperty("org.jboss.soa.esb.gateway.original.url");
        if (Utility.stringIsNullOrEmpty(url)) {
            if (request != null) {

                if (request.getScheme().equalsIgnoreCase("http")) {
                    url = request.getScheme() + "://" + request.getServerName() + ":" + httpport + request.getRequestURI();
                } else if (request.getScheme().equalsIgnoreCase("https")) {
                    url = request.getScheme() + "://" + request.getServerName() + ":" + httpsport + request.getRequestURI();
                }
            }
            //   url = request.getProtocol() + request.getServerName() + request.getRequestURI() + request.getLocalAddr() + request.getLocalName()
            //            + request.getMethod() + request.getPathInfo() + request.getScheme() ;
        }

        if (Utility.stringIsNullOrEmpty(url)) {
            try {
                //alternate method, but should always work
                url = msg.getHeader().getCall().getTo().getURI().toString();
                if (url.startsWith("invm://")) {
                    log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, inbound message is internal esb traffic " + url);
                    return;
                }
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, inbound message for " + url + " via SOAP To field.");
            } catch (Exception ex) {
//                log.log(Level.WARN, "fgsms Agent for JbossESBPostProxyAgent, unable to determine the URL or TO field from the message, this message will be ignored");
                //               return msg;
            }
        }
        //else {
        //  log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, inbound message for " + url + " via transport.");
        // }


        if (Utility.stringIsNullOrEmpty(url)) {
            log.log(Level.WARN, "untable to determine request url, message will be ignored.");
            return;
        }


        Long msgsize = null;
        try {
            Integer i = (Integer) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size");
            if (i != null) {
                msgsize = i.longValue();
            }
            if (msgsize != null) {
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message size " + msgsize + " via long.");
            }
        } catch (Exception ex) {
        }
        if (msgsize == null) {
            try {
                msgsize = Long.parseLong((String) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size"));
                if (msgsize != null) {
                    log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message size " + msgsize + " via string.");
                }
            } catch (Exception ex) {
            }
        }
        if (msgsize == null) {
            try {
                msgsize = response.getLength();
                if (msgsize != null) {
                    log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message size " + msgsize + " via response object.");
                }
            } catch (Exception ex) {
            }
        }



        boolean fault = true;

        log.log(Level.WARN, "fgsms, this message to " + url + " transaction id:" + id.toString() + " has faulted.");

//        Long dod = (Long) params.get("org.jboss.soa.esb.message.time.dod");
        //      if (dod == null) {
        Long dod = System.currentTimeMillis();
        //    }

        if (msg.getAttachment().getNamedCount() > 0 || msg.getAttachment().getUnnamedCount() > 0) {
            log.log(Level.INFO, "fgsms Agent for JbossESBPostProxyAgent, outbound message has attachments, named: " + msg.getAttachment().getNamedCount() + " unnamed:" + msg.getAttachment().getUnnamedCount());
            int count = (msg.getAttachment().getNamedCount() + msg.getAttachment().getUnnamedCount());
            for (int i = 0; i < count; i++) {
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message has attachments " + i + " type: " + msg.getAttachment().itemAt(i).getClass().getCanonicalName());
            }
        }


        String body = "";
        try {
            body = thrwbl.getMessage();
            body += thrwbl.toString();
        } catch (Exception ex) {
            log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message for " + url + " has a non-text body", ex);

        }

       
        if (msgsize == null) {
            MessageProcessor.getSingletonObject().processMessageOutput(id, body, body.length(), true, Long.valueOf(dod), new HashMap());
        } else {
            MessageProcessor.getSingletonObject().processMessageOutput(id, body, msgsize.intValue(), true, Long.valueOf(dod),new HashMap());
        }
    }

    public void processSuccess(Message msg) {
        //log.log(Level.DEBUG, "#D1000 fgsms JbossESBProxyAgentProxyAction, exception recieved:" + msg.toString());
    }

    public void initialise() throws ActionLifecycleException {
        log.log(Level.INFO, "fgsms Agent for JbossESB Action Pipeline Processor started.");
    }

    public void destroy() throws ActionLifecycleException {
    }
}
