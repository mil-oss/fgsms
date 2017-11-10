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
package org.miloss.fgsms.agentcore;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Constants.AuthMode;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.Header;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.plugins.agents.IEndpointDiscovery;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * fgsms's Java Data Pusher
 *
 * Asynchronously handles the caching, loading, storing and sending of all
 * record data for transactional web services.
 *
 * @author AO
 * @since 3.0
 */


public class DataPusher implements Runnable {

    static boolean DEBUG = false;
    private static ConcurrentLinkedQueue outboundQueue;
    private static HashMap policyCache;
    private static final Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    private static ConfigLoader cfg = null;
    private static boolean ErrorState = false;

    // private static boolean configured = false;
    public DataPusher(HashMap cache, ConcurrentLinkedQueue queue) {
        Initializer(cache, queue);
    }

    private synchronized void Initializer(HashMap cache, ConcurrentLinkedQueue queue) {
        policyCache = cache;
        outboundQueue = queue;
    }

    /**
     * used only for the Persistent storage agent
     */
    protected DataPusher() {
        policyCache = new HashMap();
        outboundQueue = new ConcurrentLinkedQueue();
    }

    /**
     * loads all configuration data, setups up the remote service proxies for
     * PCS and DCS
     */
    protected static synchronized void Init() {
        if (cfg == null) {
            try {
                //TODO, if the ACS service is finally implemented, uncomment this 
                //cfg = ConfigLoader.DoDynamicConfig();
                if (cfg == null) {
                    cfg = MessageProcessor.getSingletonObject().getConfig();
                }
                if (cfg == null) {
                    throw new ConfigurationException("Config unavailable");
                }
                //   configured=true;
            } catch (Exception ex) {
                cfg = null;
                ErrorState = true;
                LastErrorMessage = "could not initialize the configuration from fgsms.AgentCore.jar";
                log.log(Level.FATAL, "could not initialize the configuration from fgsms.AgentCore.jar", ex);
                return;
            }

            BindingProvider bp = (BindingProvider) cfg.dcsport;
            Map<String, Object> context = bp.getRequestContext();

            if (cfg.mode_ == AuthMode.UsernamePassword) {
                context.put(BindingProvider.USERNAME_PROPERTY, cfg.username);
                context.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.password));
            }
            if (cfg.mode_ == AuthMode.PKI) {
                //for PKI, we need a client certificate for authenticating to the FGSMS server
                //and it wasn't already specified
                if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStore")) || Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStorePassword"))) {
                    if (cfg.getJavaxkeystore() != null) {
                        //the context setup only works on JbossWS, as far as I can tell
                        context.put("javax.net.ssl.keyStorePassword", Utility.DE(cfg.getJavaxkeystorepass()));
                        context.put("javax.net.ssl.keyStore", Utility.DE(cfg.getJavaxkeystore()));
                        try {
                            System.setProperty("javax.net.ssl.keyStorePassword", Utility.DE(cfg.getJavaxkeystorepass()));
                            System.setProperty("javax.net.ssl.keyStore", (cfg.getJavaxkeystore()));
                        } catch (Exception ex) {
                            log.log(Level.WARN, "error caught when referencing (get or set) System.properties for SSL communication. Check to ensure that this is enabled in your JAAS managemer", ex);
                        }
                    }
                }
            }

            //trust store. Fox Mulder always said to trust no one, so we won't use the default
            //JDK trust store and only trust the certifcate issuers that we want to trust
            //however if someone specified an alternate mechanism from the command line, don't
            //do anything
            if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStore")) && Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStorePassword"))) {
                if (cfg.getJavaxtruststore() != null) {
                    //the context setup only works on JbossWS, as far as I can tell
                    context.put("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
                    context.put("javax.net.ssl.trustStore", (cfg.getJavaxtruststore()));
                    try {
                        System.setProperty("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
                        System.setProperty("javax.net.ssl.trustStore", (cfg.getJavaxtruststore()));
                    } catch (Exception ex) {
                        log.log(Level.WARN, "error caught when referencing (get or set) System.properties for SSL communication. Check to ensure that this is enabled in your JAAS managemer", ex);
                    }
                }
            }

            ApacheCxfSslHelper.doCXF(cfg.dcsport, cfg);

            bp = (BindingProvider) cfg.pcsport;
            context = bp.getRequestContext();

            if (cfg.mode_ == AuthMode.UsernamePassword) {
                context.put(BindingProvider.USERNAME_PROPERTY, cfg.username);
                context.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.password));
            }
            if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStore")) || Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStorePassword"))) {
                if (cfg.getJavaxtruststore() != null) {
                    context.put("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
                    context.put("javax.net.ssl.trustStore", Utility.DE(cfg.getJavaxtruststore()));
                    try {
                        System.setProperty("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
                        System.setProperty("javax.net.ssl.trustStore", (cfg.getJavaxtruststore()));
                    } catch (Exception ex) {
                        log.log(Level.WARN, "error caught when referencing (get or set) System.properties for SSL communication. Check to ensure that this is enabled in your JAAS managemer", ex);
                    }
                }
            }

            if (cfg.mode_ == AuthMode.PKI) {

                if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStore")) || Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStorePassword"))) {
                    if (cfg.getJavaxkeystore() != null) {
                        context.put("javax.net.ssl.keyStorePassword", Utility.DE(cfg.getJavaxkeystorepass()));
                        context.put("javax.net.ssl.keyStore", Utility.DE(cfg.getJavaxkeystore()));
                        try {
                            System.setProperty("javax.net.ssl.keyStorePassword", Utility.DE(cfg.getJavaxkeystorepass()));
                            System.setProperty("javax.net.ssl.keyStore", cfg.getJavaxkeystore());
                        } catch (Exception ex) {
                            log.log(Level.WARN, "error caught when referencing (get or set) System.properties for SSL communication. Check to ensure that this is enabled in your JAAS managemer", ex);
                        }
                    }
                }
            }

            ApacheCxfSslHelper.doCXF(cfg.pcsport, cfg);

        }

    }

    /**
     * caches IP to DNS mappings to speed up performance this may block if an ip
     * address is not resolvable or there is some kind of DNS related problem
     *
     * @param URL
     * @param isclient
     * @return
     */
    protected static String IpWrapAndCacher(String URL, boolean isclient) {

        if (MessageProcessor.getSingletonObject().getURLaddressMap().containsKey(URL)) {
            return (String) MessageProcessor.getSingletonObject().getURLaddressMap().get(URL);
        }
        String newurl = IpAddressUtility.modifyURL(URL, isclient);
        MessageProcessor.getSingletonObject().getURLaddressMap().put(URL, newurl);
        return newurl;
    }

    /*
     * protected enum Algorithm {
     *
     * FAILOVER, ROUNDROBIN }
     *
     * protected enum UnavailableBehavior {
     *
     * /**
     * PURGE, if I can't send it, throw it out
     *
     * PURGE, /** HOLD, if I can't send it, keep it in memory until I can or
     * until I get destroyed by the garbage collector
     *
     * HOLD, /** HOLDPERSIST, if I can't send it, store it to disk, then send
     * when it's available
     *
     * HOLDPERSIST }
     */
    /**
     * Loads the policy from the fgsms PCS service
     *
     * @param url
     * @param style
     * @return null if it cannot reach the PCS or access was denied.
     * @isserver isclient, carried over from modify url
     */
    private static ServicePolicyResponseMsg FetchPolicy(String url, ConfigLoader.Algorithm style, boolean isclient) {
        boolean ok = false;
        int urlcount = 0;
        int retrycount = 0;
        BindingProvider bp = (BindingProvider) cfg.pcsport;
        Map<String, Object> context = bp.getRequestContext();

        switch (style) {
            case FAILOVER:
                retrycount = 0;
                urlcount = 0;
                ok = false;
                while (!ok && (retrycount < cfg.PCSRetryCount)) {

                    urlcount = 0;
                    while (!ok && urlcount < cfg.PCS_URLS.size()) {
                        try { // Call Web Service Operation

                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.PCS_URLS.get(urlcount));

                            //send it
                            ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
                            request.setClassification(cfg.classlevel);
                            request.setURI(url);
                            request.setPolicytype(PolicyType.TRANSACTIONAL);
                            if (!isclient) {
                                request.setMachine(Utility.getHostName());

                            } else {
                                request.setMachine(new URL(url).getHost());
                            }
                            ServicePolicyResponseMsg result = cfg.pcsport.getServicePolicy(request);
                            // result.getPolicy();

                            ok = true;
                            //log.log(Level.INFO, "fgsms successfully retrieved policy for " + request.getURI());
                            return result;

                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to fetch policy from PCS at " + cfg.PCS_URLS.get(urlcount) + " will retry " + (cfg.PCSRetryCount - retrycount) + " times." + ex.getMessage());
                        }
                        urlcount++;
                    }
                    retrycount++;
                }
                if (!ok) {
                    log.log(Level.FATAL, "fgsms unable to fetch policy from any of the PCS[" + cfg.PCS_URLS.size() + "] URLs, retry count exceeded. Falling back to default policy");
                    MessageProcessor.getSingletonObject().setLastErrorMessage("All PCS endpoints are either unreachable or are responding in error");
                }

                break;
            case ROUNDROBIN:
                retrycount = 0;
                urlcount = 0;
                ok = false;
                while (!ok && (retrycount < cfg.PCSRetryCount)) {

                    urlcount = 0;
                    while (!ok && urlcount < cfg.PCS_URLS.size()) {
                        try { // Call Web Service Operation

                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.PCS_URLS.get(urlcount));
                            //send it
                            ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
                            request.setClassification(cfg.classlevel);
                            request.setURI(url);
                            request.setPolicytype(PolicyType.TRANSACTIONAL);
                            if (!isclient) {
                                request.setMachine(Utility.getHostName());
                            } else {
                                request.setMachine(new URL(url).getHost());
                            }
                            ServicePolicyResponseMsg result = cfg.pcsport.getServicePolicy(request);
                            result.getPolicy();
                            ok = true;
                            //log.log(Level.INFO, "fgsms successfully retrieved policy for " + request.getURI());
                            return result;

                            //    dcsport.addMoreData(req.getReq());
                        } catch (Exception ex) {
                            // BindingProvider bp = (BindingProvider) dcsport;
                            //  Map<String, Object> context = bp.getRequestContext();
                            //  String oldAddress = (String) context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            log.log(Level.WARN, "fgsms unable to fetch policy from PCS at " + cfg.PCS_URLS.get(urlcount) + " will retry " + (cfg.PCSRetryCount - retrycount) + " times." + ex.getMessage());
                        }
                        urlcount++;
                    }
                    retrycount++;
                }
                if (!ok) {
                    log.log(Level.FATAL, "fgsms unable to fetch policy from any of the PCS[" + cfg.PCS_URLS.size() + "] URLs, retry count exceeded. Falling back to default policy");
                    MessageProcessor.getSingletonObject().setLastErrorMessage("All PCS endpoints are either unreachable or are responding in error");
                }

                break;
        }
        return null;
    }

    /**
     * Entry point for loading a policy, if one cannot be fetched from the PCS,
     * the default policy is returned
     *
     * @param url
     * @return
     */
    public static PolicyHelper LoadPolicy(String url, boolean isclient) {
        // boolean error = false;
        //check the policy cache first

        //TODO insert logic for ServiceUnavailable behavior
        //suggest adding anothe property which designates a folder from which to store data until the mother ship is available
        //be sure to encrypt the file before 
        //also an entry point for 100% offline usage, store all data locally working off of a templated policy
        PolicyHelper p = (PolicyHelper) policyCache.get(url);
        if (p == null) {
            try { // it's not in the cache, try to get it....Call Web Service Operation
                log.log(Level.INFO, "fgsms requesting policy for service " + url);
                //ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
                // request.setURI(url);
                p = new PolicyHelper();
                ServicePolicyResponseMsg result = FetchPolicy(url, cfg.PCSalgo, isclient);
                if (result == null) {
                    log.log(Level.WARN, "fgsms PCS is unavailable or returned an error, reverting to agent default policy");
                    result = LoadDefaultPolicy(url);
                    p.lastUpdate = 0;

                } else {
                    p.lastUpdate = Calendar.getInstance().getTimeInMillis();
                }
                //     if (!(p.policy instanceof TransactionalWebServicePolicy)) {
                //         log.log(Level.WARN, "fgsms PCS unexpectedly returned a policy for something other than transactional services. This is unexpected and should be reported to the developer. Type is " +result.getPolicy().getPolicyType());
                //     }
                p.policy = (TransactionalWebServicePolicy) result.getPolicy();

                cfg.classlevel = result.getClassification();
                policyCache.put(url, p);
                //if (p.policy.getPolicyType() != PolicyType.TRANSACTIONAL) {
//                    log.log(Level.WARN, "fgsms PCS unexpectedly returned a policy for something other than transactional services. This is unexpected and should be reported to the developer");
                //            }
                log.log(Level.DEBUG, "fgsms obtained policy for service " + url + " and cached it.");
                return p;
            } catch (Exception ex) {
                log.log(Level.ERROR, "fgsms Error retrieving policy from PCS for service at " + url + " because it's either down or busy (check config). This transaction will be discarded.", ex);
                //       error = true;
            }
        } else {
            //it's in the cache, check to see if it's expired
            long expirationtime = p.policy.getPolicyRefreshRate().getTimeInMillis(Calendar.getInstance());
            if (p.lastUpdate + expirationtime < System.currentTimeMillis()) {
                try { // it's expired, get a new copy....Call Web Service Operation
                    log.log(Level.INFO, "fgsms retrieved cached policy, but it has expired. Retrieving latest policy for service " + url);
                    policyCache.remove(url);

                    //ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
                    //request.setURI(url);
                    ServicePolicyResponseMsg result = FetchPolicy(url, cfg.PCSalgo, isclient);

                    // ServicePolicyResponseMsg result = pcsport.getServicePolicy(request);
                    p = new PolicyHelper();
                    p.lastUpdate = Calendar.getInstance().getTimeInMillis();
                    //if (!(p.policy instanceof TransactionalWebServicePolicy)) {
//                        log.log(Level.WARN, "fgsms PCS unexpectedly returned a policy for something other than transactional services. This is unexpected and should be reported to the developer");
                    //                }
                    p.policy = (TransactionalWebServicePolicy) result.getPolicy();
                    //      if (p.policy.getPolicyType() != PolicyType.TRANSACTIONAL) {
                    //           log.log(Level.WARN, "fgsms PCS unexpectedly returned a policy for something other than transactional services. This is unexpected and should be reported to the developer");
                    //       }
                    cfg.classlevel = result.getClassification();
                    policyCache.put(url, p);
                    log.log(Level.INFO, "fgsms obtained policy for service " + url + " and cached it.");
                    return p;
                } catch (Exception ex) {
                    log.log(Level.WARN, "fgsms Error refreshing policy from PCS for service at " + url + " using expired policy", ex);
                    return p;

                }
            } else {
                log.log(Level.DEBUG, "fgsms retrieved cached policy " + url + ". It expires in " + (System.currentTimeMillis() - (p.lastUpdate + expirationtime)) + "ms");
                return p;
            }
        }
        // else i can't get a policy from the pcs because it's either down or busy, we can't return a synthesized one else the database will get out of synch
        return null;
    }

    /**
     * Loads the default policy from disk defaultpolicy.xml
     *
     * @param url
     * @return
     */
    public static ServicePolicyResponseMsg LoadDefaultPolicy(String url) {

        try {
            InputStream in = null;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }

            // Returns null on lookup failures:
            in = loader.getResourceAsStream("org/miloss/fgsms/agentcore/defaultpolicy.xml");
            String pol = ReadAllText(in);
            in.close();

            JAXBContext jc = Utility.getSerializationContext();

            Unmarshaller u = jc.createUnmarshaller();

            ByteArrayInputStream bss = new ByteArrayInputStream(pol.getBytes(Constants.CHARSET));
            //1 = reader
            //2 = writer
            XMLInputFactory xf = XMLInputFactory.newInstance();
            XMLStreamReader r = xf.createXMLStreamReader(bss);

            JAXBElement<TransactionalWebServicePolicy> foo = (JAXBElement<TransactionalWebServicePolicy>) u.unmarshal(r, TransactionalWebServicePolicy.class);
            if (foo
                    == null || foo.getValue()
                    == null) {
                log.log(Level.WARN, "ServicePolicy is unexpectedly null or empty");
                return null;
            }
            ServicePolicyResponseMsg ret = new ServicePolicyResponseMsg();

            ret.setPolicy(foo.getValue());
            ret.setClassification(cfg.classlevel);

            ret.getPolicy().setURL(url);
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error loading default policy from disk", ex);
        }
        return null;
    }

    /**
     * prepares a message for transmission
     *
     * @param current
     * @param p
     * @return
     */
    private static AddDataRequestMsg PrepMessage(MessageCorrelator current, PolicyHelper p) {
        if (current == null) {
            return null;
        }
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(cfg.classlevel);

        //stuff that's recorded always
        req.setRequestURI(p.policy.getURL());
        req.setURI(current.URL);
        req.setAction(current.soapAction);
        req.setServiceHost(MessageProcessor.getSingletonObject().getHostName());
        req.setRequestSize(current.reqsize);
        req.setResponseSize(current.ressize);
        req.setMessage("Queue size:" + outboundQueue.size() + " PolicyCache:" + policyCache.size() + " MsgMap:" + current.currentMapsize + " CPUs:" + Runtime.getRuntime().availableProcessors());
        req.setAgentType(current.agent_class_name);
        req.setRequestURI(current.originalurl);
        req.setRelatedTransactionID(current.RelatedMsgId);
        req.setTransactionThreadID(current.TransactionThreadId);
        req.setTransactionID(current.MessageID);
        try {
            DatatypeFactory f = DatatypeFactory.newInstance();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(current.RecievedAt);
            req.setRecordedat((gcal));
        } catch (Exception ex) {
            log.log(Level.ERROR, "unexpected error caught when preparing a message.", ex);
        }
        long diff = Math.abs(current.CompletedAt - current.RecievedAt);
        int x = (int) diff;
        req.setResponseTime(x);
        // current.IsFault = true;
        req.setSuccess(!current.IsFault);

        //   log.log(Level.ERROR, current.MessageID + "Success = " + !current.IsFault);
        //stuff that's recorded sometimes
        if (current.RequestMessage != null) {
            if (p.policy.isRecordRequestMessage() || (p.policy.isRecordFaultsOnly() && current.IsFault)) {

                if (p.policy.getRecordedMessageCap() >= current.RequestMessage.length()) {

                    req.setXmlRequest(current.RequestMessage);

                } else {
                    req.setXmlRequest(current.RequestMessage.substring(0, p.policy.getRecordedMessageCap()));
                }
            } else {
                if (DEBUG) {
                    log.info("skipping request payload " + p.policy.isRecordRequestMessage() + " " + p.policy.isRecordFaultsOnly() + " " + current.IsFault);
                }
            }
        }
        if (current.ResponseMessage != null) {
            if (p.policy.isRecordResponseMessage() || (p.policy.isRecordFaultsOnly() && current.IsFault)) {
                if (p.policy.getRecordedMessageCap() >= current.ResponseMessage.length()) {
                    req.setXmlResponse(current.ResponseMessage);

                } else {
                    req.setXmlResponse(current.ResponseMessage.substring(0, p.policy.getRecordedMessageCap()));
                }
            } else {
                if (DEBUG) {
                    log.info("skipping response payload " + p.policy.isRecordRequestMessage() + " " + p.policy.isRecordFaultsOnly() + " " + current.IsFault);
                }
            }
        }

        // log.log(Level.INFO, "fgsms publish loop#2. Thread id: + " + Thread.currentThread().getName() + "Time" + (System.currentTimeMillis() - start));
        //fun with serialized types
        //   List<String> s = new LinkedList<String>();
        //   ArrayOfString s = new ArrayOfString();
        ArrayList<String> s2 = MessageProcessor.getSingletonObject().getUserIdentities(p.policy, current);
        //s2.add(current.ipaddress);
        // s.setString((List<String>)(s2));
        req.getIdentity().addAll(s2);
        // req.setIdentity((List<String>)s2);

        if (current.Headers != null && !current.Headers.isEmpty() && p.policy.isRecordHeaders()) {
            Iterator e = current.Headers.keySet().iterator();
            while (e.hasNext()) {
                org.miloss.fgsms.services.interfaces.common.Header h = new org.miloss.fgsms.services.interfaces.common.Header();
                String s = (String) e.next();
                h.setName(s);
                try {
                    String value = (String) current.Headers.get(s);
                    h.getValue().add(value);
                    req.getHeadersRequest().add(h);
                } catch (Exception ex) {
                }
                try {
                    List<String> value = (List<String>) current.Headers.get(s);
                    for (int k = 0; k < value.size(); k++) {
                        h.getValue().add(value.get(k));
                    }
                    req.getHeadersRequest().add(h);

                } catch (Exception ex) {
                }
            }
            if (current.Header_Response != null && !current.Header_Response.isEmpty()) {
                e = current.Header_Response.keySet().iterator();
                while (e.hasNext()) {
                    org.miloss.fgsms.services.interfaces.common.Header h = new org.miloss.fgsms.services.interfaces.common.Header();
                    String s = (String) e.next();
                    h.setName(s);
                    try {
                        String value = (String) current.Header_Response.get(s);
                        h.getValue().add(value);
                        req.getHeadersRequest().add(h);
                    } catch (Exception ex) {
                    }
                    try {
                        List<String> value = (List<String>) current.Header_Response.get(s);
                        for (int k = 0; k < value.size(); k++) {
                            h.getValue().add(value.get(k));
                        }
                        req.getHeadersRequest().add(h);

                    } catch (Exception ex) {
                    }
                }
            }
        }

        return req;
    }
    private static String LastErrorMessage = "";

    /**
     * Run's until the outbound queue is empty, sends at most 40 message or 1 MB
     * of data (if message logging is on) at a time.
     */
    @Override
    public void run() {

        MessageProcessor.getSingletonObject().removeDeadMessage();

        Init();
        if (ErrorState) {
            log.log(Level.FATAL, "fgsms Data Pusher is in an error state. Recorded data cannot be sent and will be discarded. Check the configuration file and log for reason. Last error message was " + LastErrorMessage);
            outboundQueue.clear();
            return;
        }

        DoDiscovery();

        AddDataRequestMsg PreppedMessage = null;
        boolean enabled = true;
        while (!outboundQueue.isEmpty()) {
            //long start = System.currentTimeMillis();
            log.log(Level.DEBUG, "fgsms entering publish loop " + outboundQueue.size() + " items to publish.");
            AddMoreData req = new AddMoreData();
            int count = 0;
            int totalbody = 0;
            while (count < 40 && !outboundQueue.isEmpty() && enabled && totalbody < 1024000) {
                //pop
                MessageCorrelator current = null;
                PreppedMessage = null;
                try {
                    Object j = outboundQueue.remove();
                    if (j instanceof MessageCorrelator) {
                        current = (MessageCorrelator) j;
                    }
                    if (j instanceof AddDataRequestMsg) {
                        PreppedMessage = (AddDataRequestMsg) j;
                    }
                } catch (Exception e) {//just swallow it and continue, another thread must have picked up the last item.
                    log.log(Level.DEBUG, "fgsms publish loop, error removing item from the queue, another thread must have grabbed the last item. Queue size is currently "
                            + outboundQueue.size());
                }

                boolean isclient = false;
                if (current != null) {
                    isclient = current.agent_class_name != null && (current.agent_class_name.contains("client") || current.agent_class_name.contains("Client"));
                    if (isclient) {
                        current.URL = IpWrapAndCacher(current.URL, true);
                        //current.URL = IpAddressUtility.modifyURL(current.URL, true);
                    } else {
                        current.URL = IpWrapAndCacher(current.URL, false);
                        //current.URL = IpAddressUtility.modifyURL(current.URL, false);
                    }

                    //Applying the ignore list once more
                    if (MessageProcessor.getSingletonObject().getIgnoreList().contains(current.URL.toLowerCase())) {
                        current = null;
                        continue;
                    }
                }
                if (PreppedMessage != null) {
                    //I've already gotten the policy and prepped this message, we just to build the counters up from here
                    if (!org.miloss.fgsms.common.Utility.stringIsNullOrEmpty(PreppedMessage.getXmlRequest())) {
                        totalbody += PreppedMessage.getXmlRequest().length();
                    }
                    if (!org.miloss.fgsms.common.Utility.stringIsNullOrEmpty(PreppedMessage.getXmlResponse())) {
                        totalbody += PreppedMessage.getXmlResponse().length();
                    }
                    req.getReq().add(PreppedMessage);
                    count++;
                }
                if (current != null) {
                    PolicyHelper p = LoadPolicy(current.URL, isclient);
                    if (p == null || p.policy == null) // outboundQueue.add(current);
                    {
                        //?  PCS is not available, stick the current item back in the queue? wont happen anymore
                        log.log(Level.WARN, "Unable to obtain policy for URL " + current.URL + ", transaction data will be lost.");
                        continue;
                    } else {

                        if (p.policy.isAgentsEnabled()) {
                            if (current != null) {
                                PreppedMessage = PrepMessage(current, p);
                            }
                            if (PreppedMessage != null && !org.miloss.fgsms.common.Utility.stringIsNullOrEmpty(PreppedMessage.getXmlRequest())) {
                                totalbody += PreppedMessage.getXmlRequest().length();
                            }
                            if (PreppedMessage != null && !org.miloss.fgsms.common.Utility.stringIsNullOrEmpty(PreppedMessage.getXmlResponse())) {
                                totalbody += PreppedMessage.getXmlResponse().length();
                            }
                            req.getReq().add(PreppedMessage);
                            count++;
                        } else {
                            enabled = false;
                            log.log(Level.WARN, "fgsms PCS reports that all agents are disabled!");
                            MessageProcessor.getSingletonObject().setLastErrorMessage("Agents centrally disabled");
                            //TODO, what if we are on persist and hold?
                            outboundQueue.clear();
                        }
                    }
                }

            }

            if (enabled) {

                BindingProvider bp = (BindingProvider) cfg.dcsport;
                Map<String, Object> context = bp.getRequestContext();

                int retrycount = 0;
                int urlcount = 0;
                boolean ok = false;
                if (!req.getReq().isEmpty()) {
                    switch (cfg.DCSalgo) {
                        case ROUNDROBIN:
                            retrycount = 0;
                            urlcount = 0;
                            ok = false;
                            while (!ok && (retrycount < cfg.DCSRetryCount)) {

                                urlcount = 0;
                                while (!ok && urlcount < cfg.DCS_URLS.size()) {
                                    try { // Call Web Service Operation

                                        //                                context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                                        context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.DCS_URLS.get(urlcount));

                                        if (DEBUG) {
                                            JAXB.marshal(req, System.out);
                                        }
                                        //send it
                                        AddDataResponseMsg addMoreData = cfg.dcsport.addMoreData(req.getReq());
                                        if (addMoreData != null && addMoreData.getStatus() != null && addMoreData.getStatus() == DataResponseStatus.SUCCESS) {
                                            ok = true;
                                            log.log(Level.DEBUG, "fgsms successfully sent transaction data for " + req.getReq().size() + " transactions. Items still to process: " + outboundQueue.size());
                                            MessageProcessor.getSingletonObject().incMessagesProcessed(req.getReq().size());
                                        } else {
                                            ok = false;
                                            log.log(Level.WARN, "fgsms failed to sent transaction data for " + req.getReq().size() + " transactions. 1 or more items couldn't be saved. Items still to process: " + outboundQueue.size());
                                        }
                                    } catch (Exception ex) {
                                        // BindingProvider bp = (BindingProvider) dcsport;
                                        //  Map<String, Object> context = bp.getRequestContext();
                                        //  String oldAddress = (String) context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                                        log.log(Level.WARN, "fgsms Error sending performance data to DCS for service at " + cfg.DCS_URLS.get(urlcount) + " will retry " + (cfg.DCSRetryCount - retrycount) + " times.", ex);
                                    }
                                    urlcount++;
                                }
                                retrycount++;
                            }
                            if (!ok) {
                                log.log(Level.ERROR, "fgsms unable to send performance data to DCS, retry count exceeded.");
                                MessageProcessor.getSingletonObject().setLastErrorMessage("retry count exceeded");
                                if (cfg.behavior == ConfigLoader.UnavailableBehavior.HOLD) {
                                    for (int i = 0; i < req.getReq().size(); i++) {
                                        outboundQueue.add(req.getReq());
                                    }
                                } else if (cfg.behavior == ConfigLoader.UnavailableBehavior.HOLDPERSIST) {
                                    for (int i = 0; i < req.getReq().size(); i++) {
                                        StorePersist(req);
                                    }
                                } else if (cfg.behavior == ConfigLoader.UnavailableBehavior.PURGE) {
                                    req.getReq().clear();
                                    req = null;
                                } else {
                                    throw new IllegalArgumentException("agent unavailable behavior");
                                }

                            }

                            break;
                        case FAILOVER:

                            retrycount = 0;
                            urlcount = 0;
                            ok = false;
                            while (!ok && (urlcount < cfg.DCS_URLS.size())) {

                                while (!ok && retrycount < cfg.DCSRetryCount) {
                                    try { // Call Web Service Operation

                                        //                            context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                                        context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.DCS_URLS.get(urlcount));

                                        if (DEBUG) {
                                            JAXB.marshal(req, System.out);
                                        }
                                        //send it
                                        AddDataResponseMsg addMoreData = cfg.dcsport.addMoreData(req.getReq());
                                        if (addMoreData != null && addMoreData.getStatus() != null && addMoreData.getStatus() == DataResponseStatus.SUCCESS) {
                                            ok = true;
                                            log.log(Level.DEBUG, "fgsms successfully sent transaction data for " + req.getReq().size() + " transactions. Items still to process: " + outboundQueue.size());
                                            MessageProcessor.getSingletonObject().incMessagesProcessed(req.getReq().size());
                                        } else {
                                            ok = false;
                                            log.log(Level.WARN, "fgsms failed to sent transaction data for " + req.getReq().size() + " transactions. 1 or more items couldn't be saved. Items still to process: " + outboundQueue.size());
                                        }
                                    } catch (Exception ex) {
                                        log.log(Level.WARN, "fgsms Error sending performance data to DCS for service at " + cfg.DCS_URLS.get(urlcount) + " will retry " + (cfg.DCSRetryCount - retrycount) + " times.", ex);
                                    }
                                    retrycount++;
                                }
                                urlcount++;
                            }
                            if (!ok) {
                                log.log(Level.ERROR, "fgsms unable to send performance data to DCS, retry count exceeded.");
                                MessageProcessor.getSingletonObject().setLastErrorMessage("retry count exceeded");

                                if (cfg.behavior == ConfigLoader.UnavailableBehavior.HOLD) {
                                    for (int i = 0; i < req.getReq().size(); i++) {
                                        outboundQueue.add(req.getReq());
                                    }
                                } else if (cfg.behavior == ConfigLoader.UnavailableBehavior.HOLDPERSIST) {
                                    for (int i = 0; i < req.getReq().size(); i++) {
                                        StorePersist(req);
                                    }
                                } else if (cfg.behavior == ConfigLoader.UnavailableBehavior.PURGE) {
                                    req.getReq().clear();
                                    req = null;
                                } else {
                                    throw new IllegalArgumentException("agent unavailable behavior");
                                }

                            }
                            break;
                    }
                }
            }
            //CheckPersistStore();
        }
        log.log(Level.DEBUG, "fgsms Data Pusher thread is terminating, no data available to send.");
    }

    /**
     * checks the local file system for files that were not able to be sent if
     * present,
     */
    protected static AddMoreData CheckPersistStore() {
        Init();
        //  needs to be thread safe 
        if (Utility.stringIsNullOrEmpty(cfg.offlinestorage)
                || cfg.behavior != ConfigLoader.UnavailableBehavior.HOLDPERSIST) {
            return null;
        }
        File f = new File(cfg.offlinestorage);
        if (f.exists() && f.isDirectory()) {
            File[] list = f.listFiles();
            if (list != null) {
                try {
                    JAXBContext jc = JAXBContext.newInstance(ArrayOfSLA.class, SLA.class,
                            SLAAction.class, SLARuleGeneric.class, AndOrNot.class,
                            AddDataRequestMsg.class, AddMoreData.class, AddData.class, String.class,
                            Duration.class, Long.class, SecurityWrapper.class,
                            ClassificationType.class, List.class, Header.class);
                    Unmarshaller u
                            = jc.createUnmarshaller();
                    for (int i = 0; i < list.length; i++) {
                        try {
                            String s
                                    = ReadAllText(f.getPath() + File.separator + f.getName());
                            s = Utility.DE(s);

                            ByteArrayInputStream bss = new ByteArrayInputStream(s.getBytes(Constants.CHARSET));
                            XMLStreamReader r
                                    = XMLInputFactory.newFactory().createXMLStreamReader(bss);
                            JAXBElement<AddMoreData> foo = (JAXBElement<AddMoreData>) u.unmarshal(r,
                                    AddMoreData.class);
                            if (foo
                                    == null || foo.getValue()
                                    == null) {
                                log.log(Level.WARN, "Add request is unexpectedly null or empty when     reading it in from disk                        ");
                            }
                            boolean delete = f.delete();
                            if (!delete) {
                                log.log(Level.ERROR, "Unable to delete file " + f.getPath() + File.separator + f.getName() + " this may cause unintented consequences, even infinite looping. Ensure that"
                                        + "this process has delete access to the folder");
                            }
                            if (foo != null) {
                                return foo.getValue();
                            } else {
                                return null;
                            }
                        } catch (Exception ex) {
                            log.log(Level.WARN, "error caught reading performance data from disk",
                                    ex);
                        }
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, "error caught reading performance data from disk",
                            ex);
                }
            }
        }

        return null;
    }

    protected static void EnsureFolderExists(String folder) {
        File f = null;
        try {
            f = new File(folder);
            if (f.exists()) {
                return;
            }
        } catch (Exception ex) {
        }
        try {
            new File(folder).mkdirs();
        } catch (Exception ex) {
            log.log(Level.WARN, "Cannot ensure that the folder " + folder + " exists");
        }
    }

    /**
     * serializes the request to a string, encrypts it, then stores it to disk
     */
    protected static void StorePersist(AddMoreData req) {
        if (req == null || req.getReq() == null || req.getReq().isEmpty()) {
            return;
        }
        Init();
        for (int i = 0; i < req.getReq().size(); i++) {
            JAXBContext jc = null;
            try {
                jc = JAXBContext.newInstance(AddDataRequestMsg.class,
                        AddDataRequestMsg.class, AddMoreData.class, AddData.class,
                        String.class, Duration.class, Long.class, SecurityWrapper.class,
                        ClassificationType.class, List.class, org.miloss.fgsms.services.interfaces.common.Header.class);
                Marshaller m = jc.createMarshaller();
                StringWriter sw = new StringWriter();

                m.marshal(
                        (req.getReq().get(i)), sw);
                String s = sw.toString();
                s = Utility.EN(s);

                WriteAllText(cfg.offlinestorage + File.separator + UUID.randomUUID().toString(), s);
            } catch (Exception ex) {
                log.log(Level.WARN, "Unable to marshall or store to disk service performance record", ex);
            }
        }
    }

    protected static String ReadAllText(InputStream in) {
        try {
            InputStreamReader sr = new InputStreamReader(in, Constants.CHARSET);
            StringBuilder fileData = new StringBuilder(1000);

            BufferedReader reader = new BufferedReader(sr);

            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            sr.close();
            return fileData.toString();
        } catch (Exception ex) {
        }
        return "";
    }

    private static String ReadAllText(String pathandfile) {
        try {

            StringBuilder fileData = new StringBuilder(1000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathandfile), Constants.CHARSET));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            return fileData.toString();
        } /*
         *
         * InputStream stream; int size = 1024; byte chars[] = new byte[size];
         * int k = stream.read(chars); String str = ""; while (k > 0) {
         *
         * for (int i = 0; i < k; i++) { str += (char) chars[i]; } k =
         * stream.read(chars); } // log.log(Level.INFO, name + "ReadAllText,
         * Read " + str.length() + " bytes.");
         */ catch (Exception e) {
            log.log(Level.ERROR, "ReadAllText, ", e);
            return "";
        }
    }

    private static void WriteAllText(String filename, String text) {
        try {
            File f = new File(filename);

            log.log(Level.INFO, "WriteAllText Current Dir = " + f.getName() + f.getAbsolutePath());
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), Constants.CHARSET));
            out.write(text);
            out.close();
        } catch (Exception e) {
            log.log(Level.INFO, filename + " WriteAllText, ", e);
        }
    }
    List<IEndpointDiscovery> endpointproviders = null;

    private void DoDiscovery() {
        if (cfg == null || cfg.prop == null) {
            throw new NullPointerException("fgsms properties file is not available.");
        }
        if (endpointproviders == null) {
            endpointproviders = LoadEndpointProviders(cfg);
        }
        List<String> endpointspcs = new ArrayList<String>();
        List<String> endpointsdcs = new ArrayList<String>();
        boolean ran = false;
        for (int i = 0; i < endpointproviders.size(); i++) {
            try {
                if ((System.currentTimeMillis() - cfg.discoveryInterval) > endpointproviders.get(i).GetLastLookup()) {
                    if (endpointproviders.get(i).IsEnabled()) {
                        endpointspcs.addAll(endpointproviders.get(i).GetPCSURLs());
                        endpointsdcs.addAll(endpointproviders.get(i).GetDCSURLs());
                        ran = true;
                        endpointproviders.get(i).SetLastLookup(System.currentTimeMillis());
                    }
                }
            } catch (Exception ex) {
                log.fatal("The discovery provery " + endpointproviders.getClass().getCanonicalName() + " is faulty and threw an exception", ex);
            }

        }
        if (ran) {
            for (int i = 0; i < endpointspcs.size(); i++) {
                if (!cfg.PCS_URLS.contains(endpointspcs.get(i))) {
                    cfg.PCS_URLS.add(endpointspcs.get(i));
                }
            }
            for (int i = 0; i < endpointspcs.size(); i++) {
                if (!cfg.DCS_URLS.contains(endpointsdcs.get(i))) {
                    cfg.DCS_URLS.add(endpointsdcs.get(i));
                }
            }
        }

    }

    public static List<IEndpointDiscovery> LoadEndpointProviders(ConfigLoader cl) {
        if (cl == null || cl.prop == null) {
            throw new NullPointerException("fgsms properties file is not available.");
        }
        List<String> discovery_providers = cl.getDiscovery_providers();
        List<IEndpointDiscovery> eps = new ArrayList<IEndpointDiscovery>();
        for (int i = 0; i < discovery_providers.size(); i++) {
            try {
                Class t = Class.forName(discovery_providers.get(i));
                IEndpointDiscovery newInstance = (IEndpointDiscovery) t.newInstance();
                newInstance.LoadConfig(cl.prop);
                eps.add(newInstance);
            } catch (Exception ex) {
                log.warn("Unable to load endpoint provider " + discovery_providers.get(i), ex);
            }
        }
        return eps;
    }
}
