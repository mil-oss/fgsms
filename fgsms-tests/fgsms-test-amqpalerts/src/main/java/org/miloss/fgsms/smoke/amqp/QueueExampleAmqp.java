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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author AO
 */
class QueueExampleAmqp implements MessageListener{

    public QueueExampleAmqp() {
    }

    void example() {
          Context ic = null;
        ConnectionFactory cf;
             
      QueueConnection con = null;
        try {
            Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
           p.put("java.naming.factory.initial","org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
            p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jndi.PropertiesFileInitialContextFactory");
           p.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
          //connectionfactory.local = amqp://guest:guest@clientid/test?brokerlist='tcp://localhost:5672'
                    p.put("connectionfactory.AMQP", "amqp://guest:guest@clientid/localhost?brokerlist='tcp://localhost:5672'");
                     p.put("queue.fgsmsAlerts", "fgsmsAlerts");
            ic = new InitialContext(p);
            String destinstation = "fgsmsAlerts";
             cf = (ConnectionFactory) ic.lookup("AMQP");

   //          AMQConnectionFactory q = new AMQConnectionFactory("amqp://guest:guest@/localhost?brokerlist='127.0.0.1'");
            
          //    tcon = q.createTopicConnection();
      
      //        Topic topic = (Topic) ic.lookup(destinstation);
                     
              
            con = (QueueConnection) cf.createConnection();
            con.setExceptionListener(new ExceptionListener()
           {
    public void onException(JMSException jmse)
    {
        System.err.println(": The sample received an exception through the ExceptionListener");
        //System.exit(0);
    }
});
            
            QueueSession s = (QueueSession)con.createSession(false, Session.AUTO_ACKNOWLEDGE);
           // Topic topic = new AMQAnyDestination(destinstation);
             //Topic topic= new AMQTopic(new AMQShortString(destinstation), "");
           
            Queue topic = (Queue) ic.lookup(destinstation);
            QueueReceiver sub = s.createReceiver(topic);
          
       //      MessageConsumer sub = s.createConsumer(topic);
            sub.setMessageListener(this);
            con.start();
          //  MessageProducer pub = s.createProducer(topic);
           // pub.send(s.createTextMessage("hello world"));
                    
            Scanner keyin = new Scanner(System.in);
            System.out.print("AMQP Consumer listening, type any key then enter to quit");
            keyin.next();

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
        System.out.println("Inbound message delievered.");
        TextMessage t = (TextMessage)msg;
        try {
            System.out.println(t.getText());
        } catch (JMSException ex) {
            Logger.getLogger(QueueExampleAmqp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
