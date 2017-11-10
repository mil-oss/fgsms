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
 package org.miloss.fgsms.plugins;

import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceFeature;
import org.miloss.fgsms.plugins.sla.alertservice.AlertRecieverPortType;
import org.miloss.fgsms.plugins.sla.alertservice.AlertRecieverService;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;

/**
 * This sample action calls a remote web service when the rule is triggered
 * @author AO
 * @since 6.2
 */
public class CustomSLAAction implements SLAActionInterface {

    
   
    private URL LoadWSDL() {
        URL wsdl = null;
        try {
            wsdl = Thread.currentThread().getContextClassLoader().getResource("META-INF/SLAPluginWebService.wsdl");
        } catch (Exception ex) {
        }
        try {
            wsdl = Thread.currentThread().getContextClassLoader().getResource("/META-INF/SLAPluginWebService.wsdl");
        } catch (Exception ex) {
        }
        try {
            wsdl = this.getClass().getClassLoader().getResource("/META-INF/SLAPluginWebService.wsdl");
        } catch (Exception ex) {
        }
        try {
            wsdl = this.getClass().getClassLoader().getResource("META-INF/SLAPluginWebService.wsdl");
        } catch (Exception ex) {
        }

        return wsdl;
    }

 
    @Override
    public List<NameValuePair> GetRequiredParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
        return true;
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Hello World";
    }

    @Override
    public String GetDisplayName() {
        return "Custom SLA Action Example";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
           try {
            KeyNameValueEnc settings = DBSettingsLoader.GetPropertiesFromDB(true, "SDK.Samples.AlertRecieverService", "URL");
            if (settings != null && settings.getKeyNameValue() != null && settings.getKeyNameValue().getPropertyValue() != null) {
                AlertRecieverService svc = new AlertRecieverService(LoadWSDL());

                AlertRecieverPortType port = svc.getAlertRecieverPort();
                BindingProvider bp = (BindingProvider) port;
                bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, settings.getKeyNameValue().getPropertyValue());
                DatatypeFactory fac = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis());

                port.recieveServiceAlert(gcal, alert.getSLAID() + " " + alert.getFaultMsg() + " " + alert.getModifiedurl());
            } else {
                Logger.getLogger("SDK.Samples.AlertRecieverService").log(Level.WARNING, "Plugin SDK.Samples.AlertRecieverService is not configured, add the general setting SDK.Samples.AlertRecieverService, URL");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }
}
