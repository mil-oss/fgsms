/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular file,
 * then You may include the notice in a location (such as a LICENSE file in a
 * relevant directory) where a recipient would be likely to look for such a
 * notice.
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
package org.miloss.fgsms.smoke.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;
import javax.xml.ws.BindingProvider;
import org.apache.log4j.Level;

import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponseMsg;

/**
 *
 * @author AO
 */
class MulticastReceiver implements Runnable {

    public MulticastReceiver() {
    }
    private static String Destination = "fgsmsAlerts";

    MulticastReceiver(ConfigLoader cfg, String url, GetGeneralSettingsResponseMsg g) {
        this.cfg = cfg;
        this.url = url;
        this.g = g;
    }
    GetGeneralSettingsResponseMsg g;
    ConfigLoader cfg = null;
    String url = null;
    public int count = 0;
    public int timeout = 60000;   //1min
    public int expectedcount = 0;
    private boolean running = true;
    private long starttime = 0;

    String GetConfigValue(GetGeneralSettingsResponseMsg g, String name) {
        for (int i = 0; i < g.getKeyNameValue().size(); i++) {
            if (g.getKeyNameValue().get(i).getPropertyName().equalsIgnoreCase(name)) {
                return g.getKeyNameValue().get(i).getPropertyValue();
            }
        }
        return "";
    }

    @Override
    public void run() {
        String ConnectionURL = GetConfigValue(g, "ConnectionURL");

        MulticastSocket socket = null;
        try {
            //expecting udp://IP:PORT
            String[] ipport = ConnectionURL.replace("udp://", "").split("\\:");
            int port = Integer.parseInt(ipport[1]);
            String ipaddress = ipport[0];
            InetAddress group = InetAddress.getByName(ipaddress);
            byte[] buf = new byte[655000];

            socket = new MulticastSocket(port);
            socket.joinGroup(group);

            long endtime = System.currentTimeMillis() + timeout;
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            while (running && count < expectedcount) {
                try {
                    socket.setSoTimeout(timeout);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength(), Constants.CHARSET);
                    count++;
                    System.out.println("msg received: " + msg);
                } catch (Exception ex) {

                }
                if (endtime < System.currentTimeMillis()) {
                    running = false;
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }
}
