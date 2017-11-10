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

package org.miloss.fgsms.sla.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;
import org.miloss.fgsms.sla.SLACommon;

/**
 *
 * @author AO
 */
public class EmailAlerter implements SLAActionInterface {

    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");

  //  @Override
    public void ProcessAction(AlertContainer alert) {
        SendMailAlert(alert.getFaultMsg(), alert.getSlaActionBaseType(), alert.getSLAID(), alert.isPooled(), alert.getModifiedurl());
    }

    private static void SendMailAlert(String htmlencodedTest, SLAAction email, String slaguid, boolean pooled, String policyurl) {
        Properties props = null;
        if (pooled) {
            props = SLACommon.LoadSLAPropertiesPooled();
        } else {
            props = SLACommon.LoadSLAPropertiesNotPooled();
        }
        InternetAddress[] subscribers = null;
        if (pooled) {
            subscribers = SLACommon.GetSubscribersPooled(slaguid);
        } else {
            subscribers = SLACommon.GetSubscribersNotPooled(slaguid);
        }
        if (subscribers != null && subscribers.length > 0) {
            try {
                //get all subscribers

                Session mailSession = Session.getDefaultInstance(props);
                Message simpleMessage = new MimeMessage(mailSession);
                InternetAddress from;
                NameValuePair nvfrom = Utility.getNameValuePairByName(email.getParameterNameValue(), "From");
                String sfrom = null;
                if (nvfrom != null) {
                    if (nvfrom.isEncrypted()) {
                        sfrom = Utility.DE(nvfrom.getValue());
                    } else {
                        sfrom = nvfrom.getValue();
                    }
                }
                NameValuePair nvsubject = Utility.getNameValuePairByName(email.getParameterNameValue(), "Subject");
                String subject = nvsubject.getValue();
                if (nvsubject.isEncrypted()) {
                    subject = Utility.DE(nvsubject.getValue());
                }

                NameValuePair nvbody = Utility.getNameValuePairByName(email.getParameterNameValue(), "Body");
                String body = nvbody.getValue();
                if (nvbody.isEncrypted()) {
                    body = Utility.DE(nvbody.getValue());
                }
                if (nvfrom == null || Utility.stringIsNullOrEmpty(sfrom)) {
                    from = new InternetAddress(props.getProperty("defaultReplyAddress"));
                } else {
                    from = new InternetAddress(sfrom);
                }
                for (int i2 = 0; i2 < subscribers.length; i2++) {
                    try {
                        simpleMessage.setFrom(from);
                        simpleMessage.setRecipient(Message.RecipientType.TO, subscribers[i2]);
                        simpleMessage.setSubject(subject);
                        //TODO, it would be nice to have some context variables
                        simpleMessage.setContent(Utility.encodeHTML(body) + "<br>" + htmlencodedTest + "<br>"
                                + SLACommon.GenerateManageLink(policyurl, props.getProperty("fgsms.GUI.URL")) + "<br><br>"
                                + SLACommon.GenerateSubscriptionLink(props.getProperty("fgsms.GUI.URL")), SLACommon.getBundleString("EmailEncodingType"));
                        // simpleMessage.setText();
                        Transport.send(simpleMessage);
                    } catch (Exception ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSendingEmail") + subscribers[i2].getAddress(), ex);
                    }
                }
            } catch (Exception ex) {
                log.log(Level.ERROR, SLACommon.getBundleString("ErrorUncaughtException"), ex);
            }
        }
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        r.add(Utility.newNameValuePair("Subject", null, false, false));
        r.add(Utility.newNameValuePair("Body", null, false, false));
        return r;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
       if (outError == null) {
            outError = new AtomicReference<String>();
        }
        if (params == null || params.isEmpty()) {
            outError.set("The parameter 'Subject' and 'Body' is required. " + outError.get());
        }
        boolean foundSubject=false;
        boolean foundBody=false;
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getName().equals("Subject")) {
                foundSubject=true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'Subject'. " + outError.get());
                }
            }
              if (params.get(i).getName().equals("Body")) {
                foundBody=true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'Body'. " + outError.get());
                }
            }
        }
        if (!foundBody || !foundSubject)
            outError.set("The parameter 'Subject' and 'Body' is required. " + outError.get());
        if (Utility.stringIsNullOrEmpty(outError.get())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Send an Email Alert<Br>Use this SLA Action to define an email delivery action that users can then subscribe to. Configurable fields:"
                + "<ul><li>Subject</li><li>Body</li><li>From - optional. This will override the sending address</li></ul>"
                + "The remaining options are administrator defined in the <b>General Settings</b> page of fgsms and primarily deal with mail server"
                + "settings.";
    }

    @Override
    public String GetDisplayName() {
        return "Email Alert";
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        r.add(Utility.newNameValuePair("From", null, false, false));
        return r;
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
          SendMailAlert(alert.getFaultMsg(), alert.getSlaActionBaseType(), alert.getSLAID(), alert.isPooled(), alert.getModifiedurl());
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
       return Utility.getAllPolicyTypes();
    }
}
