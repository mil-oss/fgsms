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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import org.apache.axis.*;
import org.apache.axis.utils.LockableHashtable;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.IMessageProcessor;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author AO
 */
public class Axis1MonitorOutbound implements org.apache.axis.Handler {

    //<editor-fold defaultstate="collapsed" desc="comment">
    private Logger log;

    public Axis1MonitorOutbound() {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    }

    public void invoke(org.apache.axis.MessageContext mc) throws AxisFault {
        //  long start = System.currentTimeMillis();
        // MessageProcessor obj = MessageProcessor.getSingletonObject();
        log.log(Level.INFO, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
        Boolean isRequest = false;

        isRequest = !mc.getPastPivot();

        if (isRequest) {
            try {
                ProcessOutboundRequest(mc);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }
        } else {
            try {
                ProcessOutboundResponse(mc, false);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }

        }

    }
    protected boolean makeLockable = false;
    protected Hashtable options;
    protected String name;

    /**
     * Should this Handler use a LockableHashtable for options? Default is
     * 'false'.
     */
    protected void setOptionsLockable(boolean makeLockable) {
        this.makeLockable = makeLockable;
    }

    protected void initHashtable() {
        if (makeLockable) {
            options = new LockableHashtable();
        } else {
            options = new Hashtable();
        }
    }

    /**
     * Stubbed-out methods. Override in your child class to implement any real
     * behavior. Note that there is NOT a stub for invoke(), since we require
     * any Handler derivative to implement that.
     */
    public void init() {
    }

    public void cleanup() {
    }

    public boolean canHandleBlock(QName qname) {
        return false;
    }

    public void onFault(MessageContext mc) {
        Boolean isRequest = false;

        isRequest = !mc.getPastPivot();

        if (isRequest) {
            try {
                ProcessOutboundRequest(mc);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }
        } else {
            try {
                ProcessOutboundResponse(mc, true);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing outbound message", ex);
            }

        }


    }

    /**
     * Set the given option (name/value) in this handler's bag of options
     */
    public void setOption(String name, Object value) {
        if (options == null) {
            initHashtable();
        }
        options.put(name, value);
    }

    /**
     * Set a default value for the given option: if the option is not already
     * set, then set it. if the option is already set, then do not set it. <p>
     * If this is called multiple times, the first with a non-null value if
     * 'value' will set the default, remaining calls will be ignored. <p>
     * Returns true if value set (by this call), otherwise false;
     */
    public boolean setOptionDefault(String name, Object value) {
        boolean val = (options == null || options.get(name) == null) && value != null;
        if (val) {
            setOption(name, value);
        }
        return val;
    }

    /**
     * Returns the option corresponding to the 'name' given
     */
    public Object getOption(String name) {
        if (options == null) {
            return (null);
        }
        return (options.get(name));
    }

    /**
     * Return the entire list of options
     */
    public Hashtable getOptions() {
        return (options);
    }

    public void setOptions(Hashtable opts) {
        options = opts;
    }

    /**
     * Set the name (i.e. registry key) of this Handler
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the name (i.e. registry key) for this Handler
     */
    public String getName() {
        return name;
    }

    public Element getDeploymentData(Document doc) {
        log.log(Level.INFO, "Enter: BasicHandler::getDeploymentData");

        Element root = doc.createElementNS("", "handler");

        root.setAttribute("class", this.getClass().getName());
        options = this.getOptions();
        if (options != null) {
            Enumeration e = options.keys();
            while (e.hasMoreElements()) {
                String k = (String) e.nextElement();
                Object v = options.get(k);
                Element e1 = doc.createElementNS("", "option");
                e1.setAttribute("name", k);
                e1.setAttribute("value", v.toString());
                root.appendChild(e1);
            }
        }
        log.log(Level.INFO, "Exit: BasicHandler::getDeploymentData");
        return (root);
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
    }

    /**
     * Return a list of QNames which this Handler understands. By returning a
     * particular QName here, we are committing to fulfilling any contracts
     * defined in the specification of the SOAP header with that QName.
     */
    public List getUnderstoodHeaders() {
        return null;
    }
    //</editor-fold>

    /*
     * Client calling a service
     */
    private void ProcessOutboundRequest(MessageContext messageContext) {
        long now = System.currentTimeMillis();
        try {
            IMessageProcessor mp = MessageProcessor.getSingletonObject();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the Message Processor singleton object", ex);
        }
        log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());
        UUID id = UUID.randomUUID();
        messageContext.setProperty(org.miloss.fgsms.common.Constants.key, id);
        org.apache.axis.Message msg = messageContext.getCurrentMessage();

        String transactionthreadid = "";


        String action = "";

        HashMap headers = new HashMap();
        try {
            MimeHeaders me = messageContext.getMessage().getMimeHeaders();
            Iterator it = me.getAllHeaders();
            while (it.hasNext()) {
                MimeHeader h = (MimeHeader) it.next();
                if (h != null) {
                    headers.put(h.getName(), h.getValue());
                    if (h.getName().equalsIgnoreCase("SOAPAction")) {
                        action = h.getValue();
                    }
                }
            }

        } catch (Exception ex) {
            log.log(Level.WARN, "unexpected error caught obtaining http headers from message context" + ex.getLocalizedMessage());
        }

        action = messageContext.getOperation().getName();
        if (!Utility.stringIsNullOrEmpty(action)) {
            action = action.replace("\"", "");
            action = action.replace("'", "");
        }

        if (Utility.stringIsNullOrEmpty(action) && messageContext.useSOAPAction()) {
            action = messageContext.getSOAPActionURI();
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replace("\"", "");
                action = action.replace("'", "");
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
        String requestURL = (String) messageContext.getProperty(org.apache.axis.MessageContext.TRANS_URL);
        String messagexml = "";
        ByteArrayOutputStream b = new ByteArrayOutputStream();
       // if (MessageProcessor.shouldAgentRecordResponseContent(requestURL))
        {
            try {
                msg.writeTo(b);
                messagexml = b.toString(org.miloss.fgsms.common.Constants.CHARSET);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms, error obtaining request message.", ex);
            }
        }
        if (Utility.stringIsNullOrEmpty(action) || action.equals("/DDS")) {
            action = Utility.getActionNameFromXML(messagexml);
            if (Utility.stringIsNullOrEmpty(action)) {
                action = org.miloss.fgsms.common.Constants.Undetermineable;
            }
            action = action.replace("\"", "");
            action = action.replace("'", "");
        }

        if (Utility.stringIsNullOrEmpty(action)) {
            action = org.miloss.fgsms.common.Constants.Undetermineable;
        }





        String ip = "";
        try {
            ip = (String) messageContext.getProperty("remoteaddr");
        } catch (Exception ex) {
        }

        transactionthreadid = MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId());
        if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
            //if not, create a new one
            try {
                transactionthreadid = UUID.randomUUID().toString();
                //MessageProcessor.setTransactionThreadId(Thread.currentThread().getId(), transactionthreadid);
            } catch (Exception ex) {
                log.log(Level.WARN, "error caught build transaction thread id", ex);
            }

        }

        if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {
            ArrayList t = new ArrayList();
            t.add(transactionthreadid);
            headers.put(org.miloss.fgsms.common.Constants.transactionthreadKey, t);
            t = new ArrayList();
            t.add(id.toString());
            headers.put(org.miloss.fgsms.common.Constants.MessageId, t);
            //append to http header, transactionthreadid
            //append to http header, id
            //messageContext.remove(messageContext.HTTP_REQUEST_HEADERS);
            //messageContext.put(messageContext.HTTP_REQUEST_HEADERS, headers);
            msg.getMimeHeaders().addHeader(org.miloss.fgsms.common.Constants.transactionthreadKey, transactionthreadid);
            msg.getMimeHeaders().addHeader(org.miloss.fgsms.common.Constants.MessageId, id.toString());
        }

        log.log(Level.DEBUG, "fgsms Agent for Axis 1.x, inbound message for " + requestURL + " action " + action);
        //log.log(Level.WARN, "fgsms Timer, inbound setup4 " + (System.currentTimeMillis() - start));
        //special case
        String relatedTransaction = null;

        String user = "";
        //log.log(Level.WARN, "fgsms Timer, inbound setup5 " + (System.currentTimeMillis() - start));
        if (!Utility.stringIsNullOrEmpty((String) messageContext.getProperty(org.apache.axis.MessageContext.AUTHUSER))) {
            user = (String) messageContext.getProperty(org.apache.axis.MessageContext.AUTHUSER);
        }

        messageContext.setProperty(org.miloss.fgsms.common.Constants.urlKEY, requestURL);
        //log.log(Level.INFO, "fgsms Message intercepted, this is a request message to " + requestURL + " with the SOAPAction of " + action + ". Assigning message id:" + id.toString());
        //log.log(Level.WARN, "fgsms Timer, inbound setup9 " + (System.currentTimeMillis() - start));
        //log.log(Level.WARN, "fgsms Timer, inbound setup10 " + (System.currentTimeMillis() - start));
        MessageProcessor.getSingletonObject().processMessageInput(messagexml, messagexml.length(), requestURL, action, user, id.toString(), headers, ip, this.getClass().getCanonicalName() + "client", relatedTransaction, transactionthreadid);
        //log.log(Level.WARN, "fgsms Timer, inbound " + (System.currentTimeMillis() - start));
    }

    /*
     * Service completing a client's request
     */
    private void ProcessOutboundResponse(org.apache.axis.MessageContext messageContext, boolean fault) throws SOAPException {
        long now = System.currentTimeMillis();
        
        UUID id = (UUID) messageContext.getProperty(org.miloss.fgsms.common.Constants.key);
        String requrl = (String) messageContext.getProperty(org.miloss.fgsms.common.Constants.urlKEY);
        if (id == null) {//|| requrl == null) {
            log.log(Level.WARN, "fgsms outbound response message did not have context variable added. This is unexpected. This transaction will be ignored");
            //MessageProcessor.removeFromQueue(id);
            return;
        }
        if (Utility.stringIsNullOrEmpty(requrl)) {
            log.log(Level.WARN, "fgsms outbound response message did not have the URL context variable added. This is unexpected. This transaction will be ignored");
            MessageProcessor.getSingletonObject().removeFromQueue(id);
            return;
        }
        HashMap headers = new HashMap();
        org.apache.axis.Message msg = messageContext.getCurrentMessage();
        //String relatedTransaction = (String) messageContext.getProperty(org.miloss.fgsms.common.Constants.relatedtransactionKey);
//        if (true) 
        {

            //service response, insert fgsms headers
            //ArrayList<String> l = new ArrayList<String>();
            //l.add(id.toString());
            //ArrayList<String> threadlist = new ArrayList<String>();
            //threadlist.add(MessageProcessor.getTransactionThreadId(Thread.currentThread().getId()));
            String threadid = MessageProcessor.getSingletonObject().getTransactionThreadId(Thread.currentThread().getId());
            if (Utility.stringIsNullOrEmpty(threadid)) {
                threadid = UUID.randomUUID().toString();
            }
            MimeHeaders mimeHeaders = msg.getMimeHeaders();
            if (mimeHeaders != null) {


                if (MessageProcessor.getSingletonObject().isDependencyInjectionEnabled()) {
                    mimeHeaders.addHeader(org.miloss.fgsms.common.Constants.relatedtransactionKey, id.toString());
                    mimeHeaders.addHeader(org.miloss.fgsms.common.Constants.transactionthreadKey, threadid);
                }
                Iterator allHeaders = mimeHeaders.getAllHeaders();
                while (allHeaders.hasNext()) {
                    MimeHeader h = (MimeHeader) allHeaders.next();
                    if (h != null) {
                        headers.put(h.getName(), h.getValue());
                    }
                }
            }
        }
        String messagexml = "";
        ByteArrayOutputStream b = new ByteArrayOutputStream();
       // if (MessageProcessor.shouldAgentRecordResponseContent(requrl))
        {
            try {
                msg.writeTo(b);
                messagexml = b.toString(org.miloss.fgsms.common.Constants.CHARSET);
            } catch (IOException ex) {
                log.log(Level.WARN, null, ex);
            }
        }

        log.log(Level.DEBUG, "fgsms Message intercepted, this is a response message to " + requrl + " transaction id:" + id.toString() + " Fault=" + fault);
        //System.out.print("message intercepted by fgsms, this is a response message");
        if (fault) {
            log.log(Level.WARN, "fgsms, this message to " + requrl + " transaction id:" + id.toString() + " has faulted.");
        }

        MessageProcessor.getSingletonObject().processMessageOutput(id.toString(), messagexml, messagexml.length(), fault, now, headers);

        MessageProcessor.getSingletonObject().clearTransactionThreadId(Thread.currentThread().getId());

    }
}
