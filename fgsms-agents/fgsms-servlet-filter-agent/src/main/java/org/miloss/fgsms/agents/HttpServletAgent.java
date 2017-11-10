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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;

/**
 *Provides basic monitoring capabilities of Servlets v3
 * @author AO
 */
public class HttpServletAgent implements Filter {

    private Logger log;

    public HttpServletAgent() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
        log.log(Level.INFO, "fgsms Servlet Filter startup, monitoring path: " + filterConfig.getServletContext().getContextPath());



    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {



        HttpServletRequest req1 = (HttpServletRequest) request;
        HttpServletResponse res1 = (HttpServletResponse) response;
        if (req1 != null) {
            log.log(Level.DEBUG, "Current VM Memory : total = " + Runtime.getRuntime().totalMemory() + " free = " + Runtime.getRuntime().freeMemory());

            InputStreamRequestWrapper x = new InputStreamRequestWrapper(req1);
            SnifferResponseWrapper wrappedResponse = new SnifferResponseWrapper((HttpServletResponse) res1);
            //ByteArrayOutputStream baos = (ByteArrayOutputStream) wrappedResponse.getRealOutputStream();
            ProcessInboundMessage(x);
            chain.doFilter(x, wrappedResponse);

            if (wrappedResponse instanceof SnifferResponseWrapper) {

                String text = wrappedResponse.toString();
                if (text != null) {
                    response.getWriter().write(text);
                }
                ProcessOutboundMessage(x, wrappedResponse, text);
                MessageProcessor.getSingletonObject().removeFromQueue(UUID.fromString((String) x.getAttribute("fgsms.TransactionID")));
            }
        }
        // else chain.doFilter(request, response);
    }

    public void ProcessInboundMessage(InputStreamRequestWrapper req1) throws IOException {
        String id = UUID.randomUUID().toString();
        log.log(Level.DEBUG, "Generating a new message id " + id);
        req1.setAttribute("fgsms.TransactionID", id);
        Long dob = System.currentTimeMillis();
        req1.setAttribute("fgsms.TimeIn", dob);


        String url = req1.getRequestURI();
        /*Enumeration headerNames = req1.getHeaderNames();
        while (headerNames.hasMoreElements()) {
        String s = (String) headerNames.nextElement();
        //log.log(Level.INFO, "header " + s + "=" + req1.getHeader(s));
        }*/
        // String host = req1.getHeader("host");
        url = req1.getRequestURI();
        if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
        } else {
            url = req1.getRequestURL().toString();

        }


        String body = null;
        int length = req1.getContentLength();
        TransactionalWebServicePolicy policy = MessageProcessor.getSingletonObject().getPolicyIfAvailable(url);

        if (policy != null) {
            if (policy.isRecordFaultsOnly() || policy.isRecordRequestMessage()) {
                body = req1.toString();
            }
        } else {
            body = req1.toString();
        }



        String action = null;
        if (req1 != null) {
            action = (String) req1.getHeader("SOAPAction");
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }
        }


        String sender = "";

        if (Utility.stringIsNullOrEmpty(sender) && req1 != null) {
            req1.getRemoteAddr();
        }



        if (Utility.stringIsNullOrEmpty(action)) {
            action = Utility.getActionNameFromXML(body);
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }
        }

        if (Utility.stringIsNullOrEmpty(action)) {
            if (req1 != null) {
                action = (String) req1.getMethod();
            }
            if (!Utility.stringIsNullOrEmpty(action)) {
                action = action.replaceAll("\"", "");
                action = action.replaceAll("\'", "");
            }
        }
        HashMap headers = new HashMap();
        String threadid = null;
        String relatedid = null;
        Enumeration headerNames1 = req1.getHeaderNames();
        while (headerNames1.hasMoreElements()) {

            String name = (String) headerNames1.nextElement();
            if (name.equals(org.miloss.fgsms.common.Constants.relatedtransactionKey)) {
                relatedid = req1.getHeader(name);
            }
            if (name.equals(org.miloss.fgsms.common.Constants.transactionthreadKey)) {
                threadid = req1.getHeader(name);
            }
            headers.put(name, req1.getHeader(name));
        }
        String q = req1.getQueryString();
        if (!Utility.stringIsNullOrEmpty(q))
            headers.put(org.miloss.fgsms.common.Constants.HTTPQueryName, q);
        if (threadid == null) {
            threadid = UUID.randomUUID().toString();
        }

        if (Utility.stringIsNullOrEmpty(action)) {
            action = "urn:undeterminable";
        }
        log.log(Level.DEBUG, "fgsms Servlet Filter , inbound message for " + url + " action " + action);

        String ip = null;
        if (req1 != null) {
            ip = req1.getRemoteAddr();
        }
        if (body == null) {
            body = "";
        }

        MessageProcessor.getSingletonObject().processMessageInput(body, length, url, action, sender, id, headers, ip, HttpServletAgent.class.getCanonicalName(), relatedid, threadid);


    }

    public void ProcessOutboundMessage(InputStreamRequestWrapper req1, SnifferResponseWrapper res1, String content) {
        String id = (String) req1.getAttribute("fgsms.TransactionID");
        log.log(Level.DEBUG, "Processing response for message id " + id);
        //Long dod = System.currentTimeMillis();
        //req1.setAttribute("fgsms.TimeIn", dob);

        //     String content = "";
        int status = res1.getStatus();
        int length = 0;
        if (content != null) {
            length = content.length();
        }
        //ByteArrayOutputStream baos = (ByteArrayOutputStream) res.getRealOutputStream();
        // and make use of this
        TransactionalWebServicePolicy policy = MessageProcessor.getSingletonObject().getPolicyIfAvailable(req1.getRequestURL().toString());
        if (policy != null) {
            if (!policy.isRecordFaultsOnly() && !policy.isRecordResponseMessage()) {
                content = "";
            }
        } else {
        }


        boolean fault = false;
        if (status != 200) {
            fault = true;
        }
        if (!fault && content != null && !content.isEmpty() && content.contains("soap:fault")) {
            fault = true;
        }

        HashMap headers = new HashMap();
       /* Iterator<String> headerNames1 = res1.getHeaderNames().iterator();
        while (headerNames1.hasNext()) {
            String name = (String) headerNames1.next();
            headers.put(name, res1.getHeaders(name));
        }*/


        MessageProcessor.getSingletonObject().processMessageOutput(id, content, length, fault, Long.valueOf(System.currentTimeMillis()), headers);
    }

    @Override
    public void destroy() {
    }
}
