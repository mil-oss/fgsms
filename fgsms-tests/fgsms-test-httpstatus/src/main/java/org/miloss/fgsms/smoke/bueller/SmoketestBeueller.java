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
 package org.miloss.fgsms.smoke.bueller;

import java.io.FileInputStream;
import java.util.Properties;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.status.GetStatusRequestMsg;
import org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg;
import org.miloss.fgsms.services.interfaces.status.StatusService;

/**
 * this tests requires a computer running windows with NTLM v1 or v2 + IIS + a website configured for authentication using NTLM
 * @author AO
 */
public class SmoketestBeueller {

    public static boolean run(
            String connectToURL,
            String connectAsUsername,
            String connectAsPassword,
            TransportAuthenticationStyle connectAsStyle,
            String fgsmsUsername, String fgsmsPassword,
            String OpenESNURLPCS, String fgsmsURLSS) throws Exception {

        //


        System.out.println("Setting up proxies");



        cfg = new ConfigLoader();

        pcsport = cfg.getPcsport();
        BindingProvider bp = (BindingProvider) pcsport;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, fgsmsUsername);
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, fgsmsPassword);
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, OpenESNURLPCS);
        ssport = cfg.getSsport();
        bp = (BindingProvider) ssport;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, fgsmsUsername);
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, fgsmsPassword);
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, fgsmsURLSS);
        System.out.println("Configuring " + connectToURL);
        SetPolicy(connectToURL);
        System.out.println("Setting the credentials");
        SetCred(connectToURL, connectAsUsername, connectAsPassword, connectAsStyle);

        System.out.println("Sleeping to wait for bueller");
        Thread.sleep(60000);
        boolean status = VerifyStatus(connectToURL);
        System.out.println("Removing the policy");
        RemovePolicy(connectToURL);
        return status;
    }
    static StatusService ssport = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        //TODO load properties from file

        Properties props = new Properties();
        //props.load( new FileInputStream(settings.properties));


        props.load(new FileInputStream("settings.properties"));

        

        if (run(props.getProperty("NTLMv1URL"),
                props.getProperty("NTLMv1Username"),
                props.getProperty("NTLMv1Password"),
                TransportAuthenticationStyle.HTTP_NTLM,
                props.getProperty("fgsmsUsername"),
                props.getProperty("fgsmsPassword"),
                props.getProperty("pcsurl"),
                props.getProperty("ssurl"))) {
            System.out.println("Success!");
        } else {
            System.out.println("Failure");
        }

    }

    private static void SetPolicy(String url) throws Exception {

        System.out.println("Setting up policy");
        TransactionalWebServicePolicy pol = new TransactionalWebServicePolicy();
        pol.setAgentsEnabled(true);
        pol.setBuellerEnabled(true);
        pol.setDataTTL(DatatypeFactory.newInstance().newDuration(600000));
        pol.setHealthStatusEnabled(false);
        pol.setMachineName(Utility.getHostName());
        pol.setPolicyType(PolicyType.TRANSACTIONAL);
        pol.setURL(url);

        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(pol.getURL());
        req.setPolicy(pol);
        pcsport.setServicePolicy(req);

    }
    static ConfigLoader cfg = null;
    static PCS pcsport = null;

    private static void RemovePolicy(String url) throws Exception {
        System.out.println("Removing the policy");
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(url);
        req.setDeletePerformanceData(true);
        pcsport.deleteServicePolicy(req);
    }

    private static void SetCred(String url, String username, String password, TransportAuthenticationStyle style) throws Exception {
        SetCredentialsRequestMsg req = new SetCredentialsRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setPassword(password);
        req.setUrl(url);
        req.setUsername(username);
        req.setStyle(style);
        pcsport.setCredentials(req);
    }

    private static boolean VerifyStatus(String url) throws Exception {
        GetStatusRequestMsg req = new GetStatusRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURI(url);
        GetStatusResponseMsg status = ssport.getStatus(req);
        if (status != null) {
            System.out.println("status is : " + status.getMessage());
            if (status.isOperational()) {
                return true;
            }
        }
        return false;
    }
}
