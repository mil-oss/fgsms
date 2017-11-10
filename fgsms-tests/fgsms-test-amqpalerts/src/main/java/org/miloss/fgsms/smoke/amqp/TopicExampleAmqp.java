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
package org.miloss.fgsms.smoke.amqp;

import java.net.URLEncoder;
import java.util.*;
import javax.jms.*;
import javax.naming.Context;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponseMsg;
import org.apache.qpid.AMQConnectionFailureException;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.client.AMQConnectionFactory;

/**
 *
 * @author AO
 */
class TopicExampleAmqp implements MessageListener {

     public TopicExampleAmqp() {
     }
     private static String Destination = "fgsmsAlerts";

     TopicExampleAmqp(ConfigLoader cfg, String url) {
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

     void example(GetGeneralSettingsResponseMsg g) {

	  Context ic = null;
	  javax.jms.Connection con = null;
	  AMQConnectionFactory cf = null;
	  String ConnectionURL = GetConfigValue(g, "ConnectionURL");
	  int parametercount = GetParameterCount(ConnectionURL);

	  //username/password only
	  if (parametercount == 2) {
	       ConnectionURL = String.format(ConnectionURL, GetConfigValue(g, "username"), Utility.DE(GetConfigValue(g, "password")));
	  }
	  //username/password with SSL
	  if (parametercount == 4) {
	       ConnectionURL = String.format(ConnectionURL, GetConfigValue(g, "username"), Utility.DE(GetConfigValue(g, "password")), System.getProperty("truststore"), URLEncoder.encode(System.getProperty("truststorepass")));
	  }
	  //username/password with SSL
	  if (parametercount == 6) {
	       ConnectionURL = String.format(ConnectionURL, GetConfigValue(g, "username"), Utility.DE(GetConfigValue(g, "password")), System.getProperty("truststore"), System.getProperty("truststorepass"), System.getProperty("keystore"), System.getProperty("keystorepass"));
	  }
	  try {
	       System.out.println("Connecting to " + ConnectionURL);
	       try {
		    con = new AMQConnection(ConnectionURL);
	       } catch (AMQConnectionFailureException e) {

		    e.printStackTrace();
		    throw new Exception("couldn't start listener", e);
	       }

	       Session s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

	       String dest = GetConfigValue(g, "Destination");
	       boolean istopic = false;
	       String t = GetConfigValue(g, "DestinationType");
	       if (t.equalsIgnoreCase("topic")) {
		    istopic = true;
	       }
	       if (Utility.stringIsNullOrEmpty(dest)) {
		    dest = Destination;
	       }
	       if (istopic) {
		    Topic topic = s.createTopic(dest);

		    MessageConsumer sub = s.createConsumer(topic);
		    sub.setMessageListener(this);
	       } else {
		    javax.jms.Queue topic = s.createQueue(dest);

		    MessageConsumer sub = s.createConsumer(topic);
		    sub.setMessageListener(this);
	       }

	       con.start();
	       System.out.println("AMQP Consumer listening on " + dest);

	       running = true;
	       starttime = System.currentTimeMillis();
	       SendData();
	       while (running && ((starttime + timeout) > System.currentTimeMillis())) {
		    //add data request here
		    Thread.sleep(1000);
		    if (expectedcount <= count) {
			 break;
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

     private static int GetParameterCount(String ConnectionURL) {
	  int count = 0;
	  int idx = 0;
	  while (idx < ConnectionURL.length()) {
	       int indexOf = ConnectionURL.indexOf("%s", idx);
	       if (indexOf == -1) {
		    return count;
	       }
	       count++;
	       idx = indexOf + 1;
	  }
	  return count;
     }

     @Override
     public void onMessage(Message msg) {
	  count++;
	  System.out.println(count + "/" + expectedcount + " Inbound message delievered.");
	  TextMessage t = (TextMessage) msg;
	  try {
	       System.out.println(t.getText());
	  } catch (JMSException ex) {
	       ex.printStackTrace();
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
	  bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getDCS_URLS().get(0));
	  bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, cfg.getUsername());
	  bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.getPassword()));
	  System.out.println("Delivering " + expectedcount + " msgs to dcs");
	  dcs.addMoreData(req);
     }
}
