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
package org.miloss.fgsms.smoke.amqp;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.qpid.client.AMQConnectionFactory;
import org.apache.qpid.client.AMQTopic;
import org.apache.qpid.framing.AMQShortString;

/**
 *
 * @author AO
 */
class TopicExampleAmqpDuce implements MessageListener {

    public TopicExampleAmqpDuce() {
    }

    void example() {
        Context ic = null;
        ConnectionFactory cf;
        TopicConnection tcon = null;
        Connection con = null;
        try {
            Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
            p.put("java.naming.factory.initial", "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
            p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
            p.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
            //connectionfactory.local = amqp://guest:guest@clientid/test?brokerlist='tcp://localhost:5672'
            p.put("connectionfactory.fgsmsAMQP", "amqp://guest:guest@clientid/localhost?brokerlist='tcp://localhost:5672'");
            p.put("topic.test9", "test9");
            ic = new InitialContext(p);
            String destinstation = "test9";
            cf = (AMQConnectionFactory) ic.lookup("fgsmsAMQP");


            Topic topic = (Topic) ic.lookup(destinstation);
            System.out.println();
            System.out.println(topic.getTopicName()); //      test9
            System.out.println(topic.toString()); //topic://amq.topic/test9/?routingkey='test9'&exclusive='true'&autodelete='true'
            AMQTopic at = (AMQTopic) topic;
            System.out.println(at.getAMQQueueName()); //null
            if (at.getAddress()!=null)
            System.out.println("address" + at.getAddress().toString());
            System.out.println(at.getAddressName()); //null
            System.out.println(at.getEncodedName()); //topic://amq.topic/test9/?routingkey='test9'&exclusive='true'&autodelete='true'
            System.out.println(at.getExchangeClass()); //topic
            at.setExchangeName((destinstation));
            System.out.println(at.getExchangeName()); //amq.topic
            System.out.println(at.getQueueName()); //null
            System.out.println(at.getRoutingKey()); //test9
            System.out.println(at.getTopicName()); //test9
            System.out.println(at.getSubject()); //null
            
            

            
            con = cf.createConnection();

            Session s = (Session) con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer sub = s.createConsumer(topic);
            MessageProducer pub = s.createProducer(topic);
            //Topic topic = new Topic((Connection)tcon, "fgsmsAlerts");


            //      MessageConsumer sub = s.createConsumer(topic);
            sub.setMessageListener(this);
            con.start();
            //  MessageProducer pub = s.createProducer(topic);
            while (true) {
                pub.send(s.createTextMessage("hello world " + System.currentTimeMillis()));
                Thread.sleep(1000);
            }
            //    Scanner keyin = new Scanner(System.in);
            //     System.out.print("AMQP Consumer listening, type any key then enter to quit");
            //     keyin.next();

        } catch (Exception ex) {
            ex.printStackTrace();;
        } finally {
            if (ic != null) {
                try {
                    ic.close();
                } catch (Exception e) {
                }
            }
            if (tcon != null) {
                try {
                    tcon.close();
                } catch (JMSException ex) {
                }
            }
        }
    }

    @Override
    public void onMessage(Message msg) {
        System.out.println("Inbound message delievered.");
        TextMessage t = (TextMessage) msg;
        try {
            System.out.println(t.getText());
        } catch (JMSException ex) {
            Logger.getLogger(TopicExampleAmqpDuce.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
