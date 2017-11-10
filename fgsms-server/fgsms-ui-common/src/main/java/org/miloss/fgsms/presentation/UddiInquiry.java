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
package org.miloss.fgsms.presentation;

import java.net.URLEncoder;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.miloss.fgsms.common.PublicationConstants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
;
import org.uddi.api_v3.*;

/**
 * Provides UDDI functions from the Web GUI most functions return rendered html
 *
 * @author AO
 */


public class UddiInquiry {

    private static final Logger log = LogHelper.getLog();
    private UDDIConfig config = null;

    public UddiInquiry(UDDIConfig config) throws Exception {
        this.config = config;
        if (this.config == null || this.config.inquiry == null) {
            good = false;
        }

        good = true;
    }
    boolean good = false;

    private String LoginWrapper() {
        if (!good || config == null) {
            return "";
        }
        if (Utility.stringIsNullOrEmpty(config.username) || Utility.stringIsNullOrEmpty(config.encryptedPassword)) {
            return "";
        }
        if (config.useUDDI) {
            GetAuthToken request = new GetAuthToken();
            request.setUserID(config.username);
            request.setCred(Utility.DE(config.encryptedPassword));
            try {
                //            BindingProvider bp = (BindingProvider) config.security;
//                Map<String, Object> requestContext = bp.getRequestContext();
                AuthToken response = config.security.getAuthToken(request);
                return response.getAuthInfo();
            } catch (Exception ex) {
                log.log(Level.ERROR, "Error authenticating to the UDDI server: ", ex);
                good = false;
                return "";
            }
        }
        return "";
    }

    public String InquiryUDDIMonitoredServiceByBinding(String url, String bindingKey) {
        if (!good) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        BindingDetail serviceDetail = null;
        GetBindingDetail req = null;
        try {
            req = new GetBindingDetail();
            req.getBindingKey().add(bindingKey);
            serviceDetail = config.inquiry.getBindingDetail(req);

            if (serviceDetail.getBindingTemplate() != null && !serviceDetail.getBindingTemplate().isEmpty()) {
                for (int k = 0; k < serviceDetail.getBindingTemplate().size(); k++) {

                    if (serviceDetail.getBindingTemplate().get(k).getTModelInstanceDetails() != null && !serviceDetail.getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().isEmpty()) {

                        for (int x = 0; x < serviceDetail.getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().size(); x++) {

                            if (PublicationConstants.isTmodelFromfgsms(serviceDetail.getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getTModelKey())) {

                                if (serviceDetail.getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getInstanceDetails() != null) {
                                    ret.append(Utility.encodeHTML(serviceDetail.getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getTModelKey()))
                                            .append(": ")
                                            .append(Utility.encodeHTML(serviceDetail.getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getInstanceDetails().getInstanceParms()))
                                            .append("<br>");
                                }

                            }

                        }

                    }

                }
            }

            return ret.toString();
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, null, ex);
        }
        return "";
    }

    public String InquiryUDDIBusnessesAsHtmlSelect() {
        if (!good) {
            return "";
        }
        StringBuilder ret = new StringBuilder();

        FindBusiness req = null;
        try {
            req = new FindBusiness();
            req.setAuthInfo(LoginWrapper());

            req.setFindQualifiers(new FindQualifiers());
            req.getFindQualifiers().getFindQualifier().add(PublicationConstants.UDDI_FIND_QUALIFIER_APPROXIMATE_MATCH);
            //  req.getFindQualifiers().getFindQualifier().add(PublicationConstants.UDDI_FIND_QUALIFIER_CASE_INSENSITIVE_MATCH);
            Name n = new Name();
            //n.setLang(PublicationConstants.lang);
            n.setValue(PublicationConstants.UDDI_WILDCARD);
            req.getName().add(n);
            BusinessList serviceDetail = config.inquiry.findBusiness(req);
            if (serviceDetail == null || serviceDetail.getBusinessInfos() == null || serviceDetail.getBusinessInfos().getBusinessInfo().isEmpty()) {
                ret.append("No Businesses are defined in the UDDI registry. Endpoints cannot be published");
            } else {
                ret.append("<select name=uddibusinesskey>");
                for (int i = 0; i < serviceDetail.getBusinessInfos().getBusinessInfo().size(); i++) {
                    ret.append("<option value=\"").append(Utility.encodeHTML(serviceDetail.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey()))
                            .append("\">")
                            .append(nameListToString(serviceDetail.getBusinessInfos().getBusinessInfo().get(i).getName()))
                            .append(" ").append(Utility.encodeHTML(serviceDetail.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey()))
                            .append("</option>");

                }
                ret.append("</select>");
            }
            return ret.toString();
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, null, ex);
        }
        return "An error occured. Please contact the system administrator for assistance. It's possible that the UDDI server associated with this instance of fgsms is down or not available.";
    }

    public String InquiryUDDIMonitoredService(String url, String servicekey) {
        if (!good) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ServiceDetail serviceDetail = null;
        GetServiceDetail req = null;
        try {
            req = new GetServiceDetail();
            req.setAuthInfo(LoginWrapper());

            req.getServiceKey().add(servicekey);
            serviceDetail = config.inquiry.getServiceDetail(req);

            for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {

                if (serviceDetail.getBusinessService().get(i).getBindingTemplates() != null && !serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().isEmpty()) {
                    for (int k = 0; k < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {

                        if (serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails() != null
                                && !serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().isEmpty()) {

                            for (int x = 0; x < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().size(); x++) {
                                String key = serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getTModelKey();

                                if (PublicationConstants.isTmodelFromfgsms(key)) {
                                    ret.append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getTModelKey()))
                                            .append(": ")
                                            .append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getInstanceDetails().getInstanceParms()))
                                            .append("<br>");
                                }

                            }
                        }

                    }
                }

            }

            return ret.toString();
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, null, ex);
        }
        return "";
    }

    /**
     * uses on the home page from the file index3udid.jsp returns an html string
     * containing a table of uddi data
     *
     * <p>
     * this works without issue with OpenUDDI, Microsoft's Biztalk UDDI (v3),
     * jUDDI v3 and HP SOA Systinet .</p>
     *
     * @return
     */
    public String InquiryMonitoredServices() {
        if (!good) {
            return "Not Availabile.";
        }
        StringBuilder ret = new StringBuilder();
        try {

            FindService req = new FindService();
            req.setAuthInfo(LoginWrapper());

            req.setFindQualifiers(new FindQualifiers());
            req.getFindQualifiers().getFindQualifier().add(PublicationConstants.UDDI_FIND_QUALIFIER_APPROXIMATE_MATCH);
            Name n = new Name();
            n.setValue(PublicationConstants.UDDI_WILDCARD);
            req.getName().add(n);

            req.setMaxRows(100);
            req.setListHead(0);

            ServiceList findService = config.inquiry.findService(req);

            if (findService == null || findService.getServiceInfos() == null || findService.getServiceInfos().getServiceInfo().isEmpty()) {
                log.log(Level.INFO, "no results return");
                return "";
            }
            GetServiceDetail sd = new GetServiceDetail();
            for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                sd.getServiceKey().add(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey());
            }
            ret.append("<table border=1 id=udditable name=udditable><tr><th>Service</th><th>Description</th><th>Business</th><th>Binding Information</th></tr>");

            ServiceDetail serviceDetail = config.inquiry.getServiceDetail(sd);
            for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
                ret.append("<tr><td>")
                        .append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getServiceKey()))
                        .append("<br>")
                        .append(Utility.encodeHTML(nameListToString(serviceDetail.getBusinessService().get(i).getName())))
                        .append("</td><td>")
                        .append(Utility.encodeHTML(descirptionToString(serviceDetail.getBusinessService().get(i).getDescription())))
                        .append("</td>");
                ret.append("<td>").append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getBusinessKey()))
                        .append("</td>");
                ret.append("<td>");  //binding info
                if (serviceDetail.getBusinessService().get(i).getBindingTemplates() != null && !serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().isEmpty()) {

                    for (int k = 0; k < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                        ret.append("Binding Key: ").append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getBindingKey()))
                                .append("<br>");
                        if (serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint() != null) {

                            ret.append("Access Point: <a href=\"").append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint().getValue()))
                                    .append("\">").append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint().getValue())).append("</a><br>");

                        }
                        if (serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails() != null
                                && !serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().isEmpty()) {

                            ret.append("Metadata<br>");
                            for (int x = 0; x < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().size(); x++) {
                                String key = serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getTModelKey();
                                ret.append(Utility.encodeHTML(key))
                                        .append(": ");
                                if (serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getInstanceDetails() != null) {
                                    ret.append(Utility.encodeHTML(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getTModelInstanceDetails().getTModelInstanceInfo().get(x).getInstanceDetails().getInstanceParms()))
                                            .append("<br>");
                                }
                            }
                        }
                        ret.append("<br>");
                    }
                } else {
                    ret.append("No binding information available");
                }
                ret.append("</td></tr>");
            }
            ret.append("</table>");
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            return "Not Available.";
        }
        return ret.toString();
    }

    private String nameListToString(List<Name> name) {
        StringBuilder ret = new StringBuilder();
        if (name == null || name.isEmpty()) {
            return "";
        }
        for (int i = 0; i < name.size(); i++) {
            ret.append(StringEscapeUtils.escapeHtml4(name.get(i).getValue())).append(" ");
        }
        return ret.toString().trim();
    }

    private String descirptionToString(List<Description> name) {
        StringBuilder ret = new StringBuilder();
        if (name == null || name.isEmpty()) {
            return "";
        }
        for (int i = 0; i < name.size(); i++) {
            ret.append(StringEscapeUtils.escapeHtml4(name.get(i).getValue())).append(" ");
        }
        return ret.toString().trim();
    }

    public String PublishEndpoints(String[] urls, PCS pcs, SecurityWrapper c, String bizkey, boolean updateFederationPolicy) {
        String ret = "";
        for (int i = 0; i < urls.length; i++) {
            String bindingkey = "";
            boolean worked = false;
            ServicePolicyResponseMsg servicePolicy = null;
            try {
                ServicePolicyRequestMsg res = new ServicePolicyRequestMsg();
                res.setClassification(c);
                res.setURI(urls[i]);
                servicePolicy = pcs.getServicePolicy(res);

                SaveService add = new SaveService();

                add.setAuthInfo(LoginWrapper());
                BusinessService svc = new BusinessService();
                svc.setBusinessKey(bizkey);
                Name n = new Name();
                n.setLang(PublicationConstants.lang);
                if (!Utility.stringIsNullOrEmpty(servicePolicy.getPolicy().getDisplayName())) {
                    n.setValue(servicePolicy.getPolicy().getDisplayName());
                } else {
                    n.setValue(servicePolicy.getPolicy().getURL());
                }
                svc.getName().add(n);
                Description d = new Description();
                d.setLang(PublicationConstants.lang);
                d.setValue(servicePolicy.getPolicy().getDescription());
                svc.getDescription().add(d);
                if (!Utility.stringIsNullOrEmpty(servicePolicy.getPolicy().getPOC())) {
                    d = new Description();
                    d.setLang(PublicationConstants.lang);
                    d.setValue("POC: " + servicePolicy.getPolicy().getPOC());
                    svc.getDescription().add(d);
                }

                BindingTemplates bts = new BindingTemplates();
                BindingTemplate bt = new BindingTemplate();

                AccessPoint ap = new AccessPoint();
                ap.setUseType(PublicationConstants.UDDI_USE_TYPE_ENDPOINT);
                ap.setValue(servicePolicy.getPolicy().getURL());

                /*
                 * CategoryBag cb = new CategoryBag(); KeyedReference kr = new
                 * KeyedReference();
                 * kr.setKeyName("uddi:uddi.org:categorization:types");
                 * kr.setKeyValue()
                 */
                bt.setAccessPoint(ap);
                bts.getBindingTemplate().add(bt);
                svc.setBindingTemplates(bts);
                add.getBusinessService().add(svc);
                ServiceDetail saveService = config.publish.saveService(add);
                bindingkey = saveService.getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
                worked = true;
                ret += "Successfully Registered " + StringEscapeUtils.escapeHtml4(urls[i]) + "<br>";
            } catch (Exception ex) {
                ret += "Could not register the service at " + StringEscapeUtils.escapeHtml4(urls[i]) + " reason: " + StringEscapeUtils.escapeHtml4(ex.getMessage()) + "<br>";
            }
            if (worked && updateFederationPolicy && servicePolicy != null && !Utility.stringIsNullOrEmpty(bindingkey)) {
                SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
                req.setClassification(servicePolicy.getClassification());
                req.setPolicy(servicePolicy.getPolicy());
                req.setURL(servicePolicy.getPolicy().getURL());
                //check if federation target exist, if it does update it
                FederationPolicy fp = getOrCreateFedPol(servicePolicy.getPolicy());
                fp.setImplementingClassName("org.miloss.fgsms.uddipub.UddiPublisher");
                fp.getParameterNameValue().add(Utility.newNameValuePair("Binding", bindingkey, false, false));
                // fp.setBindingKey(bindingkey);
                try {
                    pcs.setServicePolicy(req);
                    ret += "Service Policy Updated for <a href=\"manage.jsp?url=" + URLEncoder.encode(urls[i]) + "\">" + StringEscapeUtils.escapeHtml4(urls[i]) + "</a>. Don't forget to select what kind of data you want to be published!<br>";
                } catch (AccessDeniedException ex) {
                    ret += "Could not update the service policy, access was denied.<br>";
                } catch (ServiceUnavailableException ex) {
                    ret += "Could not update the service policy, error message: " + Utility.encodeHTML(ex.getMessage()) + "<br>";
                }
            }
            ret += "<br><hr><br>";
        }
        return ret;
    }

    private FederationPolicy getOrCreateFedPol(ServicePolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException();
        }
        if (policy.getFederationPolicyCollection() == null) {
            policy.setFederationPolicyCollection(new FederationPolicyCollection());
        }
        for (int i = 0; i < policy.getFederationPolicyCollection().getFederationPolicy().size(); i++) {
            if (policy.getFederationPolicyCollection().getFederationPolicy().get(i).getImplementingClassName() != null
                    && policy.getFederationPolicyCollection().getFederationPolicy().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.uddipub.UddiPublisher")) {
                return policy.getFederationPolicyCollection().getFederationPolicy().get(i);
            }
        }
        FederationPolicy fp = new FederationPolicy();
        fp.setImplementingClassName("org.miloss.fgsms.uddipub.UddiPublisher");
        policy.getFederationPolicyCollection().getFederationPolicy().add(fp);
        return fp;

    }
}
