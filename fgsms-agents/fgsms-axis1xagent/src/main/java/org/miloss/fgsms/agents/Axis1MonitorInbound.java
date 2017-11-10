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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author AO
 */


public class Axis1MonitorInbound implements org.apache.axis.Handler {

    //<editor-fold defaultstate="collapsed" desc="comment">
    private Logger log;

    public Axis1MonitorInbound() {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    }

    public void invoke(org.apache.axis.MessageContext mc) throws AxisFault {
        log.log(Level.INFO, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());

        Boolean isRequest = true;

        /*
         *
         * when pivot is false, call is usually null
         *
         *
         * logic, inbound and call is defined, response from external service
         * inbound and call is NOT defined, request from a client
         *
         *
         * or
         *
         *
         * getPastPivot = true and inbound, reponse from external service
         * getPastPivor = false and inbound, request from a client
         */
        isRequest = !mc.getPastPivot();

        if (isRequest) {

            try {
                ProcessInboundRequestMessage(mc);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
            }
        } else {
            try {
                ProcessInboundResponseMessage(mc, false);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
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

        //determine if this is a request or response, then process
        Boolean isRequest = true;

        isRequest = !mc.getPastPivot();
        if (isRequest) {
            try {
                //hmm how can an inbound  request be a fault? I doubt this will ever get called
                ProcessInboundRequestMessage(mc);
            } catch (Exception ex) {
                log.log(Level.WARN, "fgsms Error processing inbound message", ex);
            }
        } else {
            ProcessInboundResponseMessage(mc, true);
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
     * set, then set it. if the option is already set, then do not set it.
     * <p>
     * If this is called multiple times, the first with a non-null value if
     * 'value' will set the default, remaining calls will be ignored.
     * <p>
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
     * Inbound client request to a service
     */
    private void ProcessInboundRequestMessage(org.apache.axis.MessageContext messageContext) throws SOAPException {

        long now = System.currentTimeMillis();
        try {
            IMessageProcessor mp = MessageProcessor.getSingletonObject();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to get a reference to the Message Processor singleton object", ex);
        }
        UUID id = UUID.randomUUID();
        //log.log(Level.WARN, "fgsms Timer, inbound setup1 " + (System.currentTimeMillis() - start));
        messageContext.setProperty(org.miloss.fgsms.common.Constants.key, id);
        //log.log(Level.WARN, "fgsms Timer, inbound setup2 " + (System.currentTimeMillis() - start));
        org.apache.axis.Message msg = messageContext.getCurrentMessage();
        String transactionthreadid = "";

        String action = "";

        HashMap map = new HashMap();
        try {
            MimeHeaders me = messageContext.getMessage().getMimeHeaders();
            Iterator it = me.getAllHeaders();
            while (it.hasNext()) {
                MimeHeader h = (MimeHeader) it.next();
                if (h != null) {
                    map.put(h.getName(), h.getValue());
                    if (h.getName().equalsIgnoreCase("SOAPAction")) {
                        action = h.getValue();
                    }
                }
            }

        } catch (Exception ex) {
            log.log(Level.WARN, "unexpected error caught obtaining http headers from message context" + ex.getLocalizedMessage());
        }

        if (Utility.stringIsNullOrEmpty(action)) {
            action = messageContext.getOperation().getName();
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replace("\"", "");
                action = action.replace("'", "");
            }
        }

        if (Utility.stringIsNullOrEmpty(action) && messageContext.useSOAPAction()) {
            action = messageContext.getSOAPActionURI();
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replace("\"", "");
                action = action.replace("'", "");
            }
        }

        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }

        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction3.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }
        if (action.toLowerCase().trim().equals(org.miloss.fgsms.common.Constants.DCSaction4.toLowerCase().trim())) {
            log.log(Level.DEBUG, "fgsms, skipping the request for DCS AddData to prevent recursive looping. This is normal and no action is required.");
            return;     //prevent recursive loops
        }
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

        String requestURL = (String) messageContext.getProperty(org.apache.axis.MessageContext.TRANS_URL);
        String messagexml = "";

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {
            msg.writeTo(b);
            messagexml = b.toString(org.miloss.fgsms.common.Constants.CHARSET);
        } catch (IOException ex) {
            log.log(Level.WARN, "fgsms, error obtaining request message.", ex);
        } finally {
            try {
                b.close();
            } catch (Exception ex) {

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
        action = action.replace("\"", "");
        action = action.replace("'", "");

        log.log(Level.DEBUG, "fgsms Agent for Axis 1.x, inbound message for " + requestURL + " action " + action);

        String ip = "";
        try {
            ip = (String) messageContext.getProperty("remoteaddr");
        } catch (Exception ex) {
        }
        String relatedTransaction = null;
        HashMap headers = new HashMap();
        MimeHeaders mimeHeaders = msg.getMimeHeaders();
        if (mimeHeaders != null) {
            Iterator it = mimeHeaders.getAllHeaders();
            while (it.hasNext()) {
                MimeHeader header = (MimeHeader) it.next();
                if (header != null) {
                    headers.put(header.getName(), header.getValue());
                }
            }
        }
        if (headers.containsKey(org.miloss.fgsms.common.Constants.MessageId)) {
            relatedTransaction = (String) headers.get(org.miloss.fgsms.common.Constants.MessageId);
        }
        if (headers.containsKey(org.miloss.fgsms.common.Constants.transactionthreadKey)) {
            transactionthreadid = (String) headers.get(org.miloss.fgsms.common.Constants.transactionthreadKey);
        }
        if (Utility.stringIsNullOrEmpty(transactionthreadid)) {
            transactionthreadid = UUID.randomUUID().toString();
        }
        try {
            MessageProcessor.getSingletonObject().setTransactionThreadId(Thread.currentThread().getId(), transactionthreadid);
        } catch (Exception ex) {
        }

        String user = "";

        if (!Utility.stringIsNullOrEmpty((String) messageContext.getProperty(org.apache.axis.MessageContext.AUTHUSER))) {
            user = (String) messageContext.getProperty(org.apache.axis.MessageContext.AUTHUSER);
        }
        messageContext.setProperty(org.miloss.fgsms.common.Constants.urlKEY, requestURL);

        //log.log(Level.WARN, "fgsms Timer, inbound setup10 " + (System.currentTimeMillis() - start));
        messageContext.setProperty(org.miloss.fgsms.common.Constants.relatedtransactionKey, relatedTransaction);
        MessageProcessor.getSingletonObject().processMessageInput(messagexml, messagexml.length(), requestURL, action, user, id.toString(), map, ip, this.getClass().getCanonicalName(), relatedTransaction, transactionthreadid);

    }

    /*
     * Client Response from a service
     */
    private void ProcessInboundResponseMessage(MessageContext messageContext, boolean fault) {

        /*
         * inbound response, aka this object is running on a client request
         * after the response is returned.
         */
        UUID id = (UUID) messageContext.getProperty(org.miloss.fgsms.common.Constants.key);
        String requrl = (String) messageContext.getProperty(org.miloss.fgsms.common.Constants.urlKEY);
        if (id == null) {
            log.log(Level.WARN, "fgsms InboundResponse message did not have context variable added. This is unexpected. This transaction will be ignored");
            return;
        }
        if (Utility.stringIsNullOrEmpty(requrl)) {
            log.log(Level.WARN, "fgsms InboundResponse message did not have the URL context variable added. This is unexpected. This transaction will be ignored");
            MessageProcessor.getSingletonObject().removeFromQueue(id);
            return;
        }
        org.apache.axis.Message msg = messageContext.getCurrentMessage();

        HashMap headers = new HashMap();

        try {
            MimeHeaders mimeHeaders = msg.getMimeHeaders();
            Iterator allHeaders = mimeHeaders.getAllHeaders();
            while (allHeaders.hasNext()) {
                String key = (String) allHeaders.next();
                headers.put(key, mimeHeaders.getHeader(key));
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "Unexpected error caught when searching for fgsms soap header", ex);
        }

        String messagexml = "not availabile.";

        ByteArrayOutputStream b = new ByteArrayOutputStream();

        try {
            msg.writeTo(b);
            messagexml = b.toString(org.miloss.fgsms.common.Constants.CHARSET);
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            try {
                b.close();
            } catch (Exception ex) {
            }
        }

        log.log(Level.DEBUG, "fgsms Message intercepted, this is a response message to " + requrl + " transaction id:" + id.toString() + " Fault=" + fault);
        if (fault) {
            log.log(Level.WARN, "fgsms, this message to " + requrl + " transaction id:" + id.toString() + " has faulted.");
        }

        MessageProcessor.getSingletonObject().processMessageOutput(id.toString(), messagexml, messagexml.length(), fault, System.currentTimeMillis(), headers);
        //  log.log(Level.WARN, "fgsms Timer, outbound final" + (System.currentTimeMillis() - start));

    }
}
