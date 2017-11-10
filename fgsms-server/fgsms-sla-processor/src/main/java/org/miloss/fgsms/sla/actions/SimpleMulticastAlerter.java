/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.sla.actions;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.SLAUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;

/**
 * This is an super simple UDP Multicast alerting mechanism. It transmits an XML
 * WSDN alert over the specified Multicast group and port.
 *
 * Because it sends XML formatted messages, it is not recommended to forward
 * this particular multicast group over metered or low bandwidth networks.
 *
 * @author AO
 */
public class SimpleMulticastAlerter implements SLAActionInterface {

    private static final String KEY = "MulticastAlerting";
    static final Logger log = Logger.getLogger("fgsms.MulticastAlerting");
    private static boolean isconfigured = false;
    
    private static String ConnectionURL = "";
    
    private static long LastConfigRefresh = 0;
    

    /**
     * Returns true if the message was delivered successfully
     *
     * @param alert
     * @return
     */
    private boolean ProcessActionRet(AlertContainer alert) {

        boolean pooled = alert.isPooled();
        String XmlMessage = SLAUtils.WSDMtoXmlString((SLAUtils.createWSDMEvent(alert)));
        NameValuePair nvpConnectionURL = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "ConnectionURL");
        String url = null;
        if (nvpConnectionURL != null) {
            if (nvpConnectionURL.isEncrypted()) {
                url = (Utility.DE(nvpConnectionURL.getValue()));
            } else {
                url = (nvpConnectionURL.getValue());
            }
        }

        if (!isconfigured) {
            configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }
        if (!isconfigured) {
            log.log(Level.ERROR, "Not configured");
            return false;
        }
        if ((System.currentTimeMillis() - 5 * 60 * 1000) > LastConfigRefresh) {
            log.log(Level.INFO, "Config refreshed");
            configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }


        boolean ok = false;
        MulticastSocket socket = null;
        try {
            //expecting udp://IP:PORT
            String[] ipport = ConnectionURL.replace("udp://", "").split("\\:");
            int port = Integer.parseInt(ipport[1]);
            String ipaddress = ipport[0];
            InetAddress group = InetAddress.getByName(ipaddress);

            byte[] buf = XmlMessage.getBytes(Constants.CHARSET);

            DatagramPacket packet;
            packet = new DatagramPacket(buf, buf.length, group, port);
            socket = new MulticastSocket(port);
            socket.send(packet);
            socket.close();
        } catch (Exception ex) {
            log.log(Level.WARN, "Failed to tx multicast alert", ex);

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, null, ex);
                }
            }
        }
        return ok;
    }

    private static String configure(boolean pooled) {
        String errors = null;
        boolean ok = true;
        KeyNameValueEnc p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "ConnectionURL");
        if (p != null && p.getKeyNameValue() != null) {
            ConnectionURL = p.getKeyNameValue().getPropertyValue();
        }

        //validate config
        if (Utility.stringIsNullOrEmpty(ConnectionURL)) {
            ok = false;
            errors += "ConnectionURL is invalid. ";
        }
        if (!ConnectionURL.startsWith("udp://")) {
            ok = false;
            errors += "ConnectionURL does not start with udp:/// ";
        } else {
            String[] ipport = ConnectionURL.replace("udp://", "").split("\\:");
            if (ipport.length == 2) {
                try {
                    int port = Integer.parseInt(ipport[1]);
                    if (port <= 0 || port >= 65500) {
                        errors += "Port is invalid";
                        ok = false;
                    }
                } catch (Exception ex) {
                    errors += "Port is invalid";
                    ok = false;
                }
                try {
                    String ipaddress = ipport[0];
                    InetAddress byName = InetAddress.getByName(ipaddress);
                    if (!byName.isMulticastAddress()) {
                        errors += "IP is not a multicast address";
                        ok = false;
                    }
                } catch (Exception ex) {
                    errors += "IP is invalid";
                    ok = false;
                }
            }

        }

        if (ok) {
            isconfigured = true;
        } else {
            isconfigured = false;
        }
        return errors;
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        return r;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        r.add(Utility.newNameValuePair("ConnectionURL", null, false, false));

        return r;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        String msg = configure(true);

        if (isconfigured) {
            return true;
        } else {
            outError.set("The administrator hasn't configured the default settings yet using General Settings. Multicast Alerts won't be available until then." + outError.get() + msg);
            return false;
        }

    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Sends a WSDM Alert via UDP Multicast<br>This will send a WSDM formatted XML message that corresponds to the type of rule that was triggered."
                + "Most settings are configured via the <b>General Settings</b> page, however they can be overridden using optional parameters."
                + "<ul>"
                + "<li>ConnectionURL - delivers alerts to a different (IP:PORT) other than the administrator defined one."
                + "Example: udp://224.1.1.1:5000</li>"
                + "</ul>";
    }

    @Override
    public String GetDisplayName() {
        return "Simple UDP Multicast Alert";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
        ProcessActionRet(alert);
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }
}
