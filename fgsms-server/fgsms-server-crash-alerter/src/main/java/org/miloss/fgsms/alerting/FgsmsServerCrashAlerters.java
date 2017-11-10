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
package org.miloss.fgsms.alerting;

import java.io.FileInputStream;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GlobalPolicy;

/**
 *
 * @author AO
 */
public class FgsmsServerCrashAlerters {

    boolean running = false;
    /**
     * This program will loop forever and periodically test to see if the PCS
     * service is available and thus also prove that the database is running. If
     * this assertion fails, an email is dispatched.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new FgsmsServerCrashAlerters().runMain();
    }
    static ThreadRunner runner = null;

    public static void start(String[] args) {
        runner = new ThreadRunner();
        t = new Thread(runner);
        t.start();
    }
    static Thread t;

    public static void stop(String[] args) {
        if (t != null && t.isAlive() && runner != null) {
            runner.m.running = false;
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void runMain() throws Exception {
        Properties p = new Properties();
        running = true;
        try {
            FileInputStream fis = new FileInputStream("mail.properties");
            p.load(fis);
            fis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (p == null || p.isEmpty()) {
            System.err.println("couldn't load main.properties");
        }


        org.miloss.fgsms.agentcore.ConfigLoader cf = new ConfigLoader();


        boolean istriggered = false;
        while (running) {
            try {
                GlobalPolicy policy = org.miloss.fgsms.agentcore.PolicyFetch.TryFetchGlobalPolicy();
                if (policy == null) {
                    System.out.println(System.currentTimeMillis() + " Null policy" + Utility.listStringtoString(cf.getPCS_URLS()));
                    if (!istriggered) {
                        triggerEmailAlert(p, "Couldn't reach any of the PCS urls at " + Utility.listStringtoString(cf.getPCS_URLS()));
                        System.out.println(System.currentTimeMillis() + " Email sent");
                        istriggered = true;
                    }
                } else {
                    System.out.println(System.currentTimeMillis() + " Success");
                    istriggered = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Thread.sleep(10000);
        }
    }

    private void triggerEmailAlert(Properties p, String message) throws Exception {

        Session mailSession = Session.getDefaultInstance(p);
        Message simpleMessage = new MimeMessage(mailSession);


        InternetAddress from = new InternetAddress(p.getProperty("sendfrom"));

        simpleMessage.setFrom(from);

        try {
            simpleMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(p.getProperty("sendto")));
            simpleMessage.setSubject("fgsms Server is down! ");

            simpleMessage.setContent(
                    "server is down!" + message, "text/html; charset=ISO-8859-1");
            Transport.send(simpleMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
