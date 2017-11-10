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
package org.miloss.fgsms.smoke.jms;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponseMsg;

/**
 *
 * @author AO
 */
class TopicExample implements MessageListener {

    public TopicExample(ConfigLoader cfg, String url) {
        this.cfg = cfg;
        this.url = url;
    }
    ConfigLoader cfg = null;
    String url = null;
    public int count = 0;
    public int timeout = 60000;   //1min
    public int expectedcount = 0;
    private boolean running = false;
    private long starttime = 0;

    String GetConfigValue(GetGeneralSettingsResponseMsg g, String name) {
        for (int i = 0; i < g.getKeyNameValue().size(); i++) {
            if (g.getKeyNameValue().get(i).getPropertyName().equalsIgnoreCase(name)) {
                return g.getKeyNameValue().get(i).getPropertyValue();
            }
        }
        return "";
    }

    void example(GetGeneralSettingsResponseMsg g, boolean listenonly, Properties p) {

        Context ic = null;
        ConnectionFactory cf;
        Connection con = null;


        try {
            
            //p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            //p.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
            //p.put(Context.PROVIDER_URL, "jnp://localhost:1199");

            p.put(Context.INITIAL_CONTEXT_FACTORY, GetConfigValue(g,"INITIAL_CONTEXT_FACTORY"));
            p.put(Context.URL_PKG_PREFIXES,                    GetConfigValue(g, "URL_PKG_PREFIXES"));
            p.put(Context.PROVIDER_URL, GetConfigValue(g, "ContextProviderUrl"));

            ic = new InitialContext(p);
            String destinstation = GetConfigValue(g, "Destination");
            
            
            System.out.println("attempting dump");
            Hashtable environment = ic.getEnvironment();
            Iterator<Entry> it = (Iterator<Entry>) environment.entrySet().iterator();
            while (it.hasNext())
            {
                Entry next = it.next();
                //System.out.println(next.getKey().toString() + " " + next.getValue().toString());
            }

            System.out.println("Looking up connection factory " + GetConfigValue(g, "ConnectionFactoryLookup"));
            
            
            
            cf = (ConnectionFactory) ic.lookup(GetConfigValue(g, "ConnectionFactoryLookup"));
            
            System.out.println("Connection factory is " + cf.getClass().getCanonicalName());
            //cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
            String t = (GetConfigValue(g, "Destinationtype"));
            boolean istopic = "topic".equalsIgnoreCase(t);
            MessageConsumer sub = null;
            //HornetQXAConnectionFactory hc = (HornetQXAConnectionFactory) cf;
            con = cf.createConnection();
            Session s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            if (istopic) {
                Topic topic = (Topic) ic.lookup(destinstation);
                sub = s.createConsumer(topic);
            } else {
                Queue q = (Queue) ic.lookup(destinstation);
                sub = s.createConsumer(q);
            }




            sub.setMessageListener(this);
            con.start();
            running = true;
            starttime = System.currentTimeMillis();
            Scanner keyin = new Scanner(System.in);
            System.out.print("JMS Consumer listening");
            if (!listenonly) {
                SendData();
            }
            if (listenonly) {
                System.out.println("CTRL-C to exit");
                keyin.next();
            } else {
                while (running && ((starttime + timeout) > System.currentTimeMillis())) {
                    //add data request here
                    Thread.sleep(1000);
                    if (expectedcount == count) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();;
        } finally {
            if (ic != null) {
                try {
                    ic.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (JMSException ex) {
                }
            }
        }
    }

    @Override
    public void onMessage(Message msg) {
        count++;
        System.out.println(count + "/" + expectedcount + " Inbound message delievered.");
        TextMessage t = (TextMessage) msg;
        try {
            System.out.println(t.getText());
        } catch (JMSException ex) {
            Logger.getLogger(TopicExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void SendData() throws Exception {
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        for (int i = 0; i < expectedcount; i++) {
            AddDataRequestMsg r = new AddDataRequestMsg();
            r.setSuccess(false);
            r.setAgentType("jUnit");
            r.setAction("test");
            r.setClassification(new SecurityWrapper());
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setRecordedat((gcal));
            r.setRequestSize(100);
            r.setRequestURI(url);
            r.setResponseSize(100);
            r.setResponseTime(100);
            r.setServiceHost(Utility.getHostName());
            r.setTransactionID(UUID.randomUUID().toString());
            r.setURI(url);
            req.add(r);
        }
        DCS dcs = cfg.getDcsport();
        BindingProvider bp = (BindingProvider) dcs;
        System.out.println("sending dcs data to " + cfg.getDCS_URLS().get(0));
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getDCS_URLS().get(0));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, cfg.getUsername());
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.getPassword()));
        System.out.println("Delivering " + expectedcount + " msgs to dcs");
        dcs.addMoreData(req);
    }
}
