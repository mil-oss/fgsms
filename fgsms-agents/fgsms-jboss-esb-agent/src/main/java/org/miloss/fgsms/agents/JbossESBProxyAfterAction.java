/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.agents;

import java.util.HashMap;
import java.util.List;
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
import org.miloss.fgsms.agentcore.IMessageProcessor;

/**
 * For Jboss ESB deployments. Use this for AFTER all processing has been
 * completed in the deployment but before returning
 *
 * @author AO
 */
public class JbossESBProxyAfterAction implements org.jboss.soa.esb.actions.ActionPipelineProcessor {

    private Logger log;

    public JbossESBProxyAfterAction(ConfigTree config) throws ConfigurationException {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
        httpport = config.getAttribute("http_port", "80");
        httpsport = config.getAttribute("https_port", "443");

    }
    private String httpport = "80";
    private String httpsport = "443";

    public Message process(Message msg) throws ActionProcessingException {
        
        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());

        for (int i = 0; i < msg.getProperties().getNames().length; i++) {
            log.log(Level.DEBUG, "key = " + msg.getProperties().getNames()[i] + " value = " + msg.getProperties().getProperty(msg.getProperties().getNames()[i]));
        }

        String id = "";
        try {
            id = (String) msg.getProperties().getProperty("fgsms.TransactionID");
        } catch (Exception ex) {
            log.log(Level.ERROR, "fgsms Agent for JbossESBPostProxyAgent, could not obtain the transaction id, this was unexpected." + ex.getLocalizedMessage());
            return msg;
        }
        if (Utility.stringIsNullOrEmpty(id)) {
            log.log(Level.ERROR, "fgsms Agent for JbossESBPostProxyAgent, outbound message does not have transaction id, this was unexpected.");
            return msg;
        }

        HttpRequest request = HttpRequest.getRequest(msg);
        HttpResponse response = HttpResponse.getResponse(msg);
        HashMap headers = new HashMap();
        String relatedTransaction = "";
        //grab the original url from the gateway, useful but not fullproof
        String url = (String) msg.getProperties().getProperty("org.jboss.soa.esb.gateway.original.url");
        if (Utility.stringIsNullOrEmpty(url)) {
            if (request != null) {
                //this is here because there's no other way to get the information
                if (request.getScheme().equalsIgnoreCase("http")) {
                    url = request.getScheme() + "://" + request.getServerName() + ":" + httpport + request.getRequestURI();
                } else if (request.getScheme().equalsIgnoreCase("https")) {
                    url = request.getScheme() + "://" + request.getServerName() + ":" + httpsport + request.getRequestURI();
                }
            }
        }

        if (Utility.stringIsNullOrEmpty(url)) {
            try {
                //alternate method, but should always work
                url = msg.getHeader().getCall().getTo().getURI().toString();
                if (url.startsWith("invm://")) {
                    log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, response message is internal esb traffic " + url);
                    return msg;
                }
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, response message for " + url + " via SOAP To field.");
            } catch (Exception ex) {
                log.log(Level.DEBUG, "error caught determining the url of the response message", ex);
            }
        }

        if (Utility.stringIsNullOrEmpty(url)) {
            log.log(Level.WARN, "untable to determine request url, message will be ignored.");
            return msg;
        }


        Long msgsize = null;
        try {
            Integer i = (Integer) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size");
            if (i != null) {
                msgsize = i.longValue();
            }
            if (msgsize != null && msgsize < 0) {
                msgsize = null;
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
                if (msgsize != null && msgsize < 0) {
                    msgsize = null;
                }
                if (msgsize != null) {
                    log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message size " + msgsize + " via response object.");
                }
            } catch (Exception ex) {
            }
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
            log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message has attachments, named: " + msg.getAttachment().getNamedCount() + " unnamed:" + msg.getAttachment().getUnnamedCount());
            int count = (msg.getAttachment().getNamedCount() + msg.getAttachment().getUnnamedCount());
            for (int i = 0; i < count; i++) {
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message has attachments " + i + " type: " + msg.getAttachment().itemAt(i).getClass().getCanonicalName());
            }
        }
        String threadid = MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId());
        if (response != null) {
            if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {
                List<HttpHeader> httpHeaders = response.getHttpHeaders();
                boolean foundthread = false;
                int relatedIdx = -1;
                if (headers != null) {
                    for (int i = 0; i < httpHeaders.size(); i++) {
                        if (httpHeaders.get(i).getName().equalsIgnoreCase(org.miloss.fgsms.common.Constants.transactionthreadKey)) {
                            foundthread = true;
                        }
                        if (httpHeaders.get(i).getName().equalsIgnoreCase(org.miloss.fgsms.common.Constants.relatedtransactionKey)) {
                            relatedTransaction = httpHeaders.get(i).getValue();
                            relatedIdx = -1;
                        }
                    }
                    if (relatedIdx > -1) {
                        httpHeaders.remove(relatedIdx);
                    }
                }
                


                //response.addHeader(new HttpHeader(org.miloss.fgsms.common.Constants.relatedtransactionKey, (String) msg.getProperties().getProperty(org.miloss.fgsms.common.Constants.relatedtransactionKey)));
                response.addHeader(new HttpHeader(org.miloss.fgsms.common.Constants.relatedtransactionKey, id));
                if (!foundthread) {
                    response.addHeader(new HttpHeader(org.miloss.fgsms.common.Constants.transactionthreadKey, threadid));
                }
            }
            for (int i = 0; i < response.getHttpHeaders().size(); i++) {
                headers.put(response.getHttpHeaders().get(i).getName(), response.getHttpHeaders().get(i).getValue());
            }

        }
        String body = "";
        if (MessageProcessor.getSingletonObject().shouldAgentRecordResponseContent(url)) {
            try {
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
                log.log(Level.DEBUG, "outbound messsage for " + url + " has a body of type " + msg.getBody().getClass().getName());
            } catch (Exception ex) {
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message for " + url + " has a non-text body", ex);

            }
        }

        if (msgsize == null) {
            MessageProcessor.getSingletonObject().processMessageOutput(id, body, body.length(), fault, Long.valueOf(dod), headers, relatedTransaction);
        } else {
            MessageProcessor.getSingletonObject().processMessageOutput(id, body, msgsize.intValue(), fault, Long.valueOf(dod), headers);
        }
        return msg;
    }

    @Override
    public void processException(Message msg, Throwable thrwbl) {

        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());


        for (int i = 0; i < msg.getProperties().getNames().length; i++) {
            log.log(Level.DEBUG, "key = " + msg.getProperties().getNames()[i] + " value = " + msg.getProperties().getProperty(msg.getProperties().getNames()[i]));
        }

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
        HashMap headers = new HashMap();

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
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message size " + msgsize + " via getPropertyInt.");
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, "error caught determining message size", ex);
        }
        if (msgsize == null) {
            try {
                msgsize = Long.parseLong((String) msg.getProperties().getProperty("org.jboss.soa.esb.message.byte.size"));
                if (msgsize != null) {
                    log.log(Level.INFO, "fgsms Agent for JbossESBPostProxyAgent, outbound message size " + msgsize + " via getPropertyLong.");
                }
            } catch (Exception ex) {
                log.log(Level.DEBUG, "error caught determining message size", ex);
            }
        }
        if (msgsize == null) {
            try {
                msgsize = response.getLength();
                if (msgsize != null) {
                    log.log(Level.INFO, "fgsms Agent for JbossESBPostProxyAgent, outbound message size " + msgsize + " via response object getLength.");
                }
            } catch (Exception ex) {
                log.log(Level.DEBUG, "error caught determining message size", ex);
            }
        }

        log.log(Level.DEBUG, "fgsms, this message to " + url + " transaction id:" + id.toString() + " has faulted.");

        Long dod = System.currentTimeMillis();

        if (msg.getAttachment().getNamedCount() > 0 || msg.getAttachment().getUnnamedCount() > 0) {
            log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message has attachments, named: " + msg.getAttachment().getNamedCount() + " unnamed:" + msg.getAttachment().getUnnamedCount());
            int count = (msg.getAttachment().getNamedCount() + msg.getAttachment().getUnnamedCount());
            for (int i = 0; i < count; i++) {
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message has attachments " + i + " type: " + msg.getAttachment().itemAt(i).getClass().getCanonicalName());
            }
        }


        if (response != null) {
            if (!response.getHttpHeaders().isEmpty()) {
                for (int i = 0; i < response.getHttpHeaders().size(); i++) {
                    headers.put(response.getHttpHeaders().get(i).getName(), response.getHttpHeaders().get(i).getValue());
                }
            }
            String threadid = MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId());
            if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {
                //response.addHeader(new HttpHeader(org.miloss.fgsms.common.Constants.relatedtransactionKey, (String) msg.getProperties().getProperty(org.miloss.fgsms.common.Constants.relatedtransactionKey)));
                response.addHeader(new HttpHeader(org.miloss.fgsms.common.Constants.relatedtransactionKey, id));
                response.addHeader(new HttpHeader(org.miloss.fgsms.common.Constants.transactionthreadKey, threadid));
            }
            for (int i = 0; i < response.getHttpHeaders().size(); i++) {
                headers.put(response.getHttpHeaders().get(i).getName(), response.getHttpHeaders().get(i).getValue());
            }
        }

        String body = "";
        if (MessageProcessor.getSingletonObject().shouldAgentRecordResponseContent(url)) {
            try {
                body = thrwbl.getMessage();
                body += thrwbl.toString();
            } catch (Exception ex) {
                log.log(Level.DEBUG, "fgsms Agent for JbossESBPostProxyAgent, outbound message for " + url + " has a non-text body", ex);
            }
        }
        IMessageProcessor mp = MessageProcessor.getSingletonObject();
        if (msgsize == null) {
            MessageProcessor.getSingletonObject().processMessageOutput(id, body, body.length(), true, (dod), headers);
        } else {
            MessageProcessor.getSingletonObject().processMessageOutput(id, body, msgsize.intValue(), true, (dod), headers);
        }
        // return msg;
    }

    public void processSuccess(Message msg) {
    }

    public void initialise() throws ActionLifecycleException {
    }

    public void destroy() throws ActionLifecycleException {
    }
}
