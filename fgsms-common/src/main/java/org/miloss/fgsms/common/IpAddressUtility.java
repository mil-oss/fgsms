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

package org.miloss.fgsms.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Level;


/**
 *Provides functionality to convert a requested URL to an absolute URL which is used by fgsms to uniquely identify services
 * 
 * @author AO
 */
public class IpAddressUtility {

    public static final String logname = "fgsms.Utility";
/**
     * Modifies URLs to absolute/unique URLs
     * if isClient
     * If an IPv4 address is present and it's one of my IPs, use the hostname
     * else it will attempt to resolve it
     * 
     * if !isClient
     * replace the server part with my hostname
     * 
     * in both cases, explicitly state the port number
     * @param url
     * @param isClient
     * @return http(s)://hostname:port/path
     */
    public static String modifyURL(String url, boolean isClient) {
        if (Utility.stringIsNullOrEmpty(url))
            throw new IllegalArgumentException("URL cannot be null or empty");
        String str = url;
        
        ArrayList<String> ips = new ArrayList<String>();
        String host = "localhost";
        ips.add("127.0.0.1");

        //attempt to get a list of all local ips address
        try {
            InetAddress addr = InetAddress.getLocalHost();
            host = addr.getHostName().toLowerCase();
            InetAddress localhost = InetAddress.getLocalHost();
            ips.add(localhost.getHostAddress());
            InetAddress[] appmyips = InetAddress.getAllByName(localhost.getCanonicalHostName());
            if (appmyips != null && appmyips.length > 1) {
                for (int i = 0; i < appmyips.length; i++) {
                    ips.add(appmyips[i].getHostAddress());
                }
            }
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> eip = intf.getInetAddresses();
                while (eip.hasMoreElements()) {
                    InetAddress ip = eip.nextElement();
                    ips.add(ip.getHostAddress());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(logname).log(Level.WARN, "Error caught attempting to obtain local ip addresses " + ex.getLocalizedMessage());
        }



        //Clients only, if the url contains localhost or one of MY IP addresses, replace with my hostname, 
        //else attempt to resolve remote hostname, 
        //if it resolves, replace
        //if not, leave it as is
        if (isClient) {

            boolean containsLocalhostOrLocalIP = false;
            if (url.contains("://localhost:")) {
                containsLocalhostOrLocalIP = true;
            }
            if (url.contains("://localhost/")) {
                containsLocalhostOrLocalIP = true;
            }
            for (int i = 0; i < ips.size(); i++) {
                if (url.contains("://" + ips.get(i) + "/")) {
                    containsLocalhostOrLocalIP = true;
                    break;
                }
                if (url.contains("://" + ips.get(i) + ":")) {
                    containsLocalhostOrLocalIP = true;
                    break;
                }
            }
            if (containsLocalhostOrLocalIP) {

                String prefix = str.substring(0, 5).toLowerCase();
                for (int i = 0; i < ips.size(); i++) {
                    //  http://local/something
                    if (prefix.equals("https")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + ":443/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + ":443/");
                    } else if (prefix.equals("http:")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + ":80/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + ":80/");

                    } else { //who knows what it is
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + "/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + "/");
                    }

                }
                if (str.startsWith("jms:")) {
                    str = str.replaceFirst("\\:[a-zA-Z0-9\\-\\.]+:", host);
                }
                Logger.getLogger(logname).log(Level.DEBUG, "URL Modifier - original " + url + " modified " + str + " client=true");
                return str;
            }


            String ipurl = ContainsIPAddress(str);
            if (!Utility.stringIsNullOrEmpty(ipurl)) {

                //ok this transaction was captured from a client agent and the service is not co-located (not one of my ip addresses)
                //if the request url contains an ip address
                //attempt to reverse dns the ip address, replace and return
                try {
                    try {
                        // Get hostname by textual representation of IP address
                        InetAddress addr = InetAddress.getByName(ipurl);

                        // Get canonical host name
                        host = addr.getHostName().toLowerCase();
                        if (Utility.stringIsNullOrEmpty(host)) {
                            host = ipurl;
                        }
                    } catch (Exception e) {
                        host = ipurl;
                    }

                    str = str.replaceFirst(ipurl, host);
                    // for (int i = 0; i < ips.size(); i++) {
                    //  str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", host);
                    //  str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", host);
                    //  str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", host);
                    //  }
                    if (str.startsWith("jms:")) {
                        str = str.replaceFirst("\\:[a-zA-Z0-9\\-\\.]+:", "\\:" + host);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(logname).log(Level.WARN, "Client side url modifier, error caught trying to rdns the remote entry of  " + url + ex.getLocalizedMessage());
                }
                Logger.getLogger(logname).log(Level.DEBUG, "URL Modifier - original " + url + " modified " + str + " client=true");
                return str;
            } else {
                //url contains some kind of host name which isn't localhost, 
                //attempt to resolve to IP, if it's one of my IP addresses, replace with my hostname (handler host file entries)
                String hostname = "";
                // hostname = r2.Match(url).Value;
                String hostnameregex = "://([^/:]+)";
                Pattern p = Pattern.compile(hostnameregex);
                Matcher m = p.matcher(str);
                boolean find = m.find();
                if (!find)
                {
                    Logger.getLogger(logname).log(Level.DEBUG, "unexpected error modifing the url " + url + " maybe it's not an actual url?");
                    return url;
                }
                hostname = str.substring(m.start(), m.end());
                hostname = hostname.replace("://", "");
                boolean found = false;
                //this should now be an ipaddress or hostname
                try {
                    InetAddress localaddr = InetAddress.getByName(hostname);
                    String hostnameip = localaddr.getHostAddress();
                    for (int i = 0; i < ips.size(); i++) {
                        if (ips.get(i).equalsIgnoreCase(hostnameip)) {
                            //here's a bug, we need the regex patterns here
                            found = true;

                            //Logger.getLogger(logname).log(Level.DEBUG, "URL Modifier - original " + url + " modified " + str + " client=true");
                            //return str;
                        }
                    }
                    //ok now we need to test for ports
                } catch (Exception e) {
                }
                if (found) {
                    String prefix = str.substring(0, 5).toLowerCase();
                    if (prefix.equals("https")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + ":443/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + ":443/");
                    } else if (prefix.equals("http:")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + ":80/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + ":80/");

                    } else if (str.startsWith("jms:")) {
                        str = str.replaceFirst("\\:[a-zA-Z0-9\\-\\.]+:", host);
                    } else { //who knows what it is
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + "/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + "/");
                    }
                    //str = str.replace(hostname, host);
                }
                else
                {
                     {
                    String prefix = str.substring(0, 5).toLowerCase();
                    if (prefix.equals("https")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + hostname + ":443/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + hostname + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + hostname + ":443/");
                    } else if (prefix.equals("http:")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + hostname + ":80/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + hostname + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + hostname + ":80/");

                    } else if (str.startsWith("jms:")) {
                        str = str.replaceFirst("\\:[a-zA-Z0-9\\-\\.]+:", hostname);
                    } else { //who knows what it is
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + hostname + "/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + hostname + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + hostname + "/");
                    }
                    //str = str.replace(hostname, host);
                }
                }
                Logger.getLogger(logname).log(Level.DEBUG, "URL Modifier - original " + url + " modified " + str + " client=true");
                return str;


            }

        } else { //inbound request to a server, thus whatever the hostname is in the url, replace with the actual hostname of this machine


            /* URL u = null;
            try {
            u = new URL(url);
            } catch (MalformedURLException ex) {
            // Logger.getLogger(DataPusher.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (u != null) {
            String h1 = u.getHost();
            for (int i = 0; i < ips.size(); i++) {
            str = str.replaceFirst(h1, host);
            }
            } else*/ {
                String prefix = str.substring(0, 5).toLowerCase();
                for (int i = 0; i < ips.size(); i++) {
                    if (prefix.equals("https")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + ":443/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + ":443/");
                    } else if (prefix.equals("http:")) {
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + ":80/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + ":80/");

                    } else { //who knows what it is
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + "/");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                        str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + "/");
                    }
                    //      str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+\\/", "://" + host + "/");
                    //      str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+:", "://" + host + ":");
                    //     str = str.replaceFirst("\\://[a-zA-Z0-9\\-\\.]+?$", "://" + host + "/");
                }

                if (str.contains("?")) {
                    str = str.substring(0, str.indexOf("?"));
                }
            }

            if (str.startsWith("jms:")) {
                str = str.replaceFirst("\\:[a-zA-Z0-9\\-\\.]+:", ":" + host + ":");
            }
        }
        Logger.getLogger(logname).log(Level.DEBUG, "URL Modifier - original " + url + " modified " + str);

        return str;
    }

    /**
     * IPv4 only
     * @param str
     * @return 
     */
    private static String ContainsIPAddress(String str) {
        if (Utility.stringIsNullOrEmpty(str)) {
            return "";
        }
        String reg = "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
        String worker = str;
        try {
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(worker);
            if (m.find()) {
                return str.substring(m.start(), m.end());
            } else {
                return "";
            }

        } catch (Exception ex) {
        }
        return "";
    }
}
