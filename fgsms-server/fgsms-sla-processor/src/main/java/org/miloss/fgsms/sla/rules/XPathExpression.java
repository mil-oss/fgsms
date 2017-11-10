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
package org.miloss.fgsms.sla.rules;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertType;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;
import org.w3c.dom.Document;

/**
 *
 * @author AO
 */


public class XPathExpression implements SLARuleInterface {

    @Override
    public boolean CheckTransactionalRule(SetStatusRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(ProcessPerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(MachinePerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }
    static final Logger log = Logger.getLogger("fgsms.SLAProcessor");

    @Override
    public boolean CheckTransactionalRule(AddDataRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        if (nullableFaultMsg == null) {
            nullableFaultMsg = new AtomicReference<String>();
        }
        NameValuePair mc = Utility.getNameValuePairByName(params, "MessageChoice");
        String item = mc.getValue();
        if (mc.isEncrypted()) {
            item = Utility.DE(mc.getValue());
        }
        NameValuePair xpath = Utility.getNameValuePairByName(params, "XPath");
        String xpathi = xpath.getValue();
        if (xpath.isEncrypted()) {
            xpathi = Utility.DE(xpath.getValue());
        }
        InputStream is = null;
        try {

            if (item.equalsIgnoreCase("request")) {
                is = new ByteArrayInputStream(req.getXmlRequest().getBytes(Constants.CHARSET));
            } else {
                is = new ByteArrayInputStream(req.getXmlResponse().getBytes(Constants.CHARSET));
            }
            Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            is.close();
            XPath xp = XPathFactory.newInstance().newXPath();
            javax.xml.xpath.XPathExpression xpc = xp.compile(xpathi);
            Boolean result = (Boolean) xpc.evaluate(xmlDocument, XPathConstants.BOOLEAN);
            if (result) {
                nullableFaultMsg.set("Xpath expression " + xpathi + " result in TRUE. " + nullableFaultMsg.get());
                return true;
            }
        } catch (Exception ex) {
            log.error("SLA Processing failed for " + req.getURI(), ex);
        } finally {
            if (is!=null)
            try {
                is.close();
            } catch (IOException ex) {

            }
        }
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(String url, List<BrokerData> data, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckNonTransactionalRule(ServicePolicy pol, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg, boolean pooled) {
        return false;
    }

    @Override
    public String GetDisplayName() {
        return "Evaluate XPath expression";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This rule triggers when the specified XPath expression evaluates to true when processed against a request or response message. "
                + "This rule always processes on the fgsms Server, requires the request and/or response to be recorded"
                + " and applies only to transactional service policies. <br><br>"
                + "Required parameters:"
                + "<ul>"
                + "<li>MessageChoice - must be either Request or Response</li>"
                + "<li>XPath - an XPath expression that evaluates to true or false</li>"
                //   + "<li>XMLNamespacePrefixies - this must be in the format prefix||Namespace||prefix||Namespace....</li>"
                + "</ul>";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(Utility.newNameValuePair("MessageChoice", null, false, false));
        arrayList.add(Utility.newNameValuePair("XPath", null, false, false));
        //arrayList.add(Utility.newNameValuePair("IsCertificate", null, false, false));
        //  arrayList.add(Utility.newNameValuePair("XMLNamespacePrefixies", null, false, false));

        return arrayList;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public AlertType GetType() {
        return AlertType.Performance;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError, ServicePolicy policy) {

        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        if (params == null || params.isEmpty()) {
            outError.set("The parameter 'MessageChoice' and 'XPath' are required. " + outError.get());
        }
        if (!(policy instanceof TransactionalWebServicePolicy)) {
            outError.set("This rule only applies to Transactional Service Policies. " + outError.get());
        }
        boolean foundLogger = false;
        boolean foundLogge2r = false;
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getName().equals("MessageChoice")) {
                foundLogger = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'MessageChoice'. " + outError.get());
                }
            }
            if (params.get(i).getName().equals("XPath")) {
                foundLogge2r = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'XPath'. " + outError.get());
                }
            }
        }
        if (!foundLogger) {
            outError.set("The parameter 'MessageChoice' is required. " + outError.get());
        }
        if (!foundLogge2r) {
            outError.set("The parameter 'XPath' is required. " + outError.get());
        }
        if (Utility.stringIsNullOrEmpty(outError.get())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String GetHtmlFormattedDisplay(List<NameValuePair> params) {
        NameValuePair mc = Utility.getNameValuePairByName(params, "MessageChoice");
        String item = UNDEFINED_VALUE;
        if (mc != null) {
            item = mc.getValue();
            if (mc.isEncrypted() || mc.isEncryptOnSave()) {
                item = ENCRYPTED_MASK;
            }
        }
        String xpathi = UNDEFINED_VALUE;

        NameValuePair xpath = Utility.getNameValuePairByName(params, "XPath");
        if (xpath != null) {
            xpathi = xpath.getValue();
            if (xpath.isEncrypted() || mc.isEncryptOnSave()) {
                xpathi = ENCRYPTED_MASK;
            }
        }
        return GetDisplayName() + " " + Utility.encodeHTML(xpathi) + " on " + Utility.encodeHTML(item);
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        List<PolicyType> ret = new ArrayList<PolicyType>();
        ret.add(PolicyType.TRANSACTIONAL);
        return ret;
    }
}
