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

package org.miloss.fgsms.discovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.ws.BindingProvider;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.agents.IEndpointDiscovery;

import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * Provides UDDI discovery services for use with agents
 *
 * @author AO
 */
public class UDDIDiscovery implements IEndpointDiscovery {

    private static final Logger log = Logger.getLogger("fgsms.Discovery.UDDI");
    private UDDIService uddi = null;
    private UDDIInquiryPortType inquiry = uddi.getUDDIInquiryPort();
    
    private UDDISecurityPortType security = uddi.getUDDISecurityPort();
    private boolean LoginRequired = false;
    private String PCSlookupkey = "";
    private String DCSlookupkey = "";
    private String SSlookupkey = "";

    boolean uddiauth = true;
    /// 
    /// Checks to see if the open esm tmodels have been registered with the uddi registry.
    /// if they haven't been, they are published
    /// 
    boolean good;
    protected String username = "";
    protected String encryptedpassword = "";
    long lastlookup = 0;
    boolean UddiEnabled = true;
    private findType currentFind = findType.EndpointBindingKey;
    public UDDIDiscovery() {
    }

    private List<String> DiscoveryServiceEndpoints(String lookupkey, findType findType) {
        List<String> list = new ArrayList<String>();
        switch (currentFind) {
            case EndpointBindingKey:
                list.addAll(DiscoverEndpointBindingKey(lookupkey, null));
                break;
            case EndpointKeyWord:
                list.addAll(DiscoverEndpointKeyWord(lookupkey, null));
                break;
            case ServiceEntityKey:
                list.addAll(DiscoverByServiceKey(lookupkey, null));
                break;
        }
        return new ArrayList<String>(new HashSet(list));
    }

    private List<String> DiscoverEndpointBindingKey(String key, String token) {
        if (Utility.stringIsNullOrEmpty(key)) {
            throw new IllegalArgumentException("bindingKey");
        }

        if (LoginRequired && uddiauth && Utility.stringIsNullOrEmpty(token)) {
            token = LoginWrapper();
        }
        GetBindingDetail req = new GetBindingDetail();

        req.setAuthInfo(token);
        req.getBindingKey().add(key);
        BindingDetail response = null;
        try {
            response = inquiry.getBindingDetail(req);
        } catch (Exception ex) {
            log.log(Level.ERROR, "error caught uddi agent endpoints", ex);
        }
//        HashMap endpoints = new HashMap();
        List<String> ret = new ArrayList<String>();
        if (response != null && !response.getBindingTemplate().isEmpty()) {
            for (int i = 0; i < response.getBindingTemplate().size(); i++) {
                if (response.getBindingTemplate().get(i).getAccessPoint() != null) {
                    ret.addAll(parseResults(response.getBindingTemplate().get(i).getAccessPoint(), token));
                }
                if (response.getBindingTemplate().get(i).getHostingRedirector() != null) {
                    ret.addAll(DiscoverEndpointBindingKey(response.getBindingTemplate().get(i).getHostingRedirector().getBindingKey(), token));
                }
            }
        }
        return ret;
    }

    private List<String> DiscoverByServiceKey(String key, String token) {
        if (key == null) {
            throw new IllegalArgumentException("keycol");
        }

        if (this.LoginRequired && uddiauth && Utility.stringIsNullOrEmpty(token)) {
            token = LoginWrapper();
        }
        GetServiceDetail req = new GetServiceDetail();
        req.getServiceKey().add(key);
        req.setAuthInfo(token);
        ServiceDetail response = null;
        try {
            response = inquiry.getServiceDetail(req);

        } catch (Exception ex) {
            log.log(Level.ERROR, "error caught discovering agent endpoints from uddi", ex);
        }

        List<String> ret = new ArrayList<String>();
        if (response == null || response == null || response.getBusinessService() == null || response.getBusinessService().isEmpty()) {
            return ret;
        }
        for (int i = 0; i < response.getBusinessService().size(); i++) {
            if (response.getBusinessService().get(i).getBindingTemplates() != null) {
                for (int k = 0; k < response.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                    if (response.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint() != null) {
                        ret.addAll(parseResults(response.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint(), token));
                    }
                }
            }
        }

        return ret;
    }

    /**
     * find service by name
     *
     * @param key
     * @param token
     * @return
     */
    private List<String> DiscoverEndpointKeyWord(String key, String token) {

        if (key == null) {
            throw new IllegalArgumentException("keywords");
        }



        if (LoginRequired && uddiauth && Utility.stringIsNullOrEmpty((token))) {
            token = LoginWrapper();
        }

        FindService req = new FindService();
        Name n = new Name();
        n.setValue(key);
        req.getName().add(n);
        req.setFindQualifiers(new FindQualifiers());
        req.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        req.getFindQualifiers().getFindQualifier().add(UDDIConstants.CASE_INSENSITIVE_MATCH);
        req.setAuthInfo(token);

        ServiceList response = null;

        List<String> ret = new ArrayList<String>();
        try {
            response = inquiry.findService(req);
        } catch (Exception ex) {
            log.log(Level.ERROR, "error discoverying agent endpoints", ex);
            return ret;
        }

        if (response == null || response.getServiceInfos() == null || response.getServiceInfos().getServiceInfo().isEmpty()) {
            log.log(Level.INFO, "Discovery from service name search in UDDI yielded no results.");
            return ret;
        }

        GetServiceDetail sreq = new GetServiceDetail();
        sreq.setAuthInfo(token);


        for (int k = 0; k < response.getServiceInfos().getServiceInfo().size(); k++) {
            sreq.getServiceKey().add(response.getServiceInfos().getServiceInfo().get(k).getServiceKey());
        }
        ServiceDetail sresponse = null;

        try {
            sresponse = inquiry.getServiceDetail(sreq);
        } catch (Exception ex) {
            log.log(Level.ERROR, "error discoverying agent endpoints", ex);
            return ret;
        }

        if (sresponse != null && !sresponse.getBusinessService().isEmpty()) {
            log.log(Level.INFO, "Discovery from service name search in UDDI yielded " + sresponse.getBusinessService().size() + " results.");
            for (int i = 0; i < sresponse.getBusinessService().size(); i++) {
                if (sresponse.getBusinessService().get(i).getBindingTemplates() != null) {
                    for (int k = 0; k < sresponse.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                        if (sresponse.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint() != null) {
                            ret.addAll(parseResults(sresponse.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint(), token));
                        }
                    }
                }
            }
        } else {
            log.log(Level.INFO, "Discovery from service name search in UDDI yielded no results");
        }
        return ret;
    }
/// 
    /// helper class to try and dicipher the complicated arrangements of the uddi type accessPoint
    /// 
    /// <param name="ap"></param>
    /// <param name="username"></param>
    /// <param name="password"></param>
    /// <param name="discoveryEndpointAddress"></param>
    /// <returns></returns>

    private List<String> parseResults(AccessPoint ap, String authtoken) {
        List<String> ret = new ArrayList<String>();
        boolean ok = false;
        if (!Utility.stringIsNullOrEmpty(ap.getUseType())) {
            //useType is defined, now what?
            //lets try and figure out what it is
            if (ap.getUseType().equalsIgnoreCase("endpoint")) {
                //for systinet servers, they tend to have endpoints with the value
                //equal to some java class file, why? who the hell knows, but it's against the 
                //uddi spec and thus not really expected.
                if (ap.getValue().toLowerCase().startsWith("http")) {
                    ret.add(ap.getValue());
                }
                //if (ap.Value.StartsWith("ftp", StringComparison.CurrentCultureIgnoreCase))
                //ret.Add(ap.Value);
                //if (ap.Value.StartsWith("smtp", StringComparison.CurrentCultureIgnoreCase))
                //ret.Add(ap.Value);
                // ret.Add(ap.Value);
                ok = true;
            } else if (ap.getUseType().equalsIgnoreCase("bindingtemplate") || ap.getUseType().equalsIgnoreCase("hostingredirector")) {

                //bindingKey
                //another call out to the same registry
                try {
                    ret.addAll(DiscoverEndpointBindingKey(ap.getValue(), authtoken));
                } catch (Exception ex) {
                    log.log(Level.WARN, "trouble discoverying endpoint from UDDI." + ex);
                }
                ok = true;
            } else /* case "hostingredirector":
             //another call out to a different registry
             try
             {
             //remote auth not supported
             string[] r2 = this.DiscoverEndpointKeyWord(keywords, ap.Value);
             for (int k = 0; k < r2.Length; k++)
             ret.Add(r2[k]);
             }
             catch (Exception ex)
             {
            
             }
            
             break;*/ if (ap.getUseType().equalsIgnoreCase("bindingtemplate") || ap.getUseType().equalsIgnoreCase("hostingredirector")) {

                ret.addAll(DiscoverEndpointByWSDL(ap.getValue()));


                ok = true;
            }


            if (!ok) {
                //this means it's an unknown useType OR
                //this means the useType is not defined. lets try to guess if the value is actually a url
                if (ap.getValue().toLowerCase().startsWith("http")) {
                    ret.add(ap.getValue());
                }
                //if (ap.Value.StartsWith("ftp", StringComparison.CurrentCultureIgnoreCase))
                //ret.Add(ap.Value);
                //if (ap.Value.StartsWith("smtp", StringComparison.CurrentCultureIgnoreCase))
                //ret.Add(ap.Value);
                //else ignore it, log unknown type as warning
            }

        }
        return ret;
    }

    private List<String> DiscoverEndpointByWSDL(String value) {
        //TODO maybe all commons.httpclient to fetch the wsdl
        return new ArrayList<String>();
    }

    @Override
    public List<String> GetRSURLs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> GetARSURLs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> GetURLs(String lookupkey) {
        return DiscoveryServiceEndpoints(lookupkey, currentFind);
    }

    @Override
    public List<String> GetDASURLs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void SetLastLookup(long timeinms) {
        lastlookup = timeinms;
    }

    @Override
    public long GetLastLookup() {
        return lastlookup;
    }

    @Override
    public void LoadConfig(Properties props) {
        try {
            if (props == null) {
                throw new IllegalArgumentException("check for configuration file, uddi.properties");
            }

            try {
                UddiEnabled = Boolean.parseBoolean(props.getProperty("discovery.uddi.enabled").trim());
            } catch (Exception ex) {
                UddiEnabled = false;
                log.log(Level.WARN, null, ex);
            }

            
            inquiry = uddi.getUDDIInquiryPort();
            BindingProvider bpPCS = (BindingProvider) inquiry;
            Map<String, Object> contextPCS = bpPCS.getRequestContext();
            if (!Utility.stringIsNullOrEmpty(props.getProperty("discovery.uddi.inquiry.url"))) {
                contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, props.getProperty("discovery.uddi.inquiry.url"));
            }

            if (Utility.stringIsNullOrEmpty(props.getProperty("discovery.uddi.lookup.dcs.servicename"))) {
                DCSlookupkey = "fgsms.DCS";
                log.log(Level.WARN, "fgsms discovery.uddi.lookup.dcs.servicename lookup key is not defined. defaulting to fgsms.DCS");
            } else {
                DCSlookupkey = props.getProperty("discovery.uddi.lookup.dcs.servicename");
            }

            if (Utility.stringIsNullOrEmpty(props.getProperty("discovery.uddi.lookup.ss.servicename"))) {
                DCSlookupkey = "fgsms.SS";
                log.log(Level.WARN, "fgsms discovery.uddi.lookup.ss.servicename lookup key is not defined. defaulting to fgsms.SS");
            } else {
                SSlookupkey = props.getProperty("discovery.uddi.lookup.ss.servicename");
            }

            if (Utility.stringIsNullOrEmpty(props.getProperty("discovery.uddi.lookup.pcs.servicename"))) {
                PCSlookupkey = "fgsms.PCS";
                log.log(Level.WARN, "fgsms discovery.uddi.lookup.pcs.servicename lookup key is not defined. defaulting to fgsms.PCS");
            } else {
                PCSlookupkey = props.getProperty("discovery.uddi.lookup.pcs.servicename");
            }


            if (Utility.stringIsNullOrEmpty(props.getProperty("discovery.uddi.inquiry.authrequired"))) {
                LoginRequired = false;
                log.log(Level.WARN, "fgsms discovery.uddi.lookup.dcs.servicename lookup key is not defined. defaulting to fgsms.DCS");
            } else {
                LoginRequired = Boolean.parseBoolean(props.getProperty("discovery.uddi.inquiry.authrequired"));
            }


            try {
                currentFind = findType.valueOf(props.getProperty("discovery.uddi.lookup.findType"));
            } catch (Exception ex) {
            }

            if (LoginRequired) {
                String t = props.getProperty("discovery.uddi.inquiry.authmode");
                if (t.equalsIgnoreCase("http")) {
                    uddiauth = false;
                } else {
                    uddiauth = true;
                }

                if (uddiauth) {
                    try {
    
                        security = uddi.getUDDISecurityPort();
                        bpPCS = (BindingProvider) security;
                        contextPCS = bpPCS.getRequestContext();
                        if (!Utility.stringIsNullOrEmpty(props.getProperty("discovery.uddi.security.url"))) {
                            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, props.getProperty("discovery.uddi.security.url"));
                        }

                        username = (String) props.get("discovery.uddi.security.username");
                        if (Utility.stringIsNullOrEmpty(username)) {
                            throw new IllegalArgumentException("username is empty");
                        }
                        encryptedpassword = (String) props.get("discovery.uddi.security.password");


                    } catch (Exception e) {
                        throw new IllegalArgumentException("UDDI Login required, but the security service could not be initialized.", e);
                    }
                } else {
                    contextPCS.put(BindingProvider.USERNAME_PROPERTY, props.getProperty("discovery.uddi.security.username"));
                    contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(props.getProperty("discovery.uddi.security.password")));
                }
            }
            good = true;
        } catch (Exception ex) {
            log.log(Level.WARN, "Unable to initialize uddi inquiry parameters for rollover or load balancing. check the agent configuration file, " + ex);
            good = false;
        }
    }

    @Override
    public boolean IsEnabled() {
        return this.UddiEnabled;
    }


    private String LoginWrapper() {

        return Login(username, encryptedpassword);
    }

    private String Login(String username, String encryptedpassword) {
        if (Utility.stringIsNullOrEmpty(username) || Utility.stringIsNullOrEmpty(encryptedpassword)) {
            return "";
        }

        //return "";/*
        GetAuthToken request = new GetAuthToken();
        request.setUserID(username);
        request.setCred(Utility.DE(encryptedpassword));
        try {
            AuthToken response = security.getAuthToken(request);
            return response.getAuthInfo();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error authenticating to the UDDI server: ", ex);
            good = false;
            return "";
        }
    }

    public List<String> GetPCSURLs() {

        return DiscoveryServiceEndpoints(PCSlookupkey, currentFind);
    }

    public List<String> GetDCSURLs() {
        return DiscoveryServiceEndpoints(DCSlookupkey, currentFind);
    }

    public List<String> GetSSURLs() {
        return DiscoveryServiceEndpoints(SSlookupkey, currentFind);
    }
    public enum findType {
        
        EndpointBindingKey,
        EndpointKeyWord,
        ServiceEntityKey
    }
   
}
