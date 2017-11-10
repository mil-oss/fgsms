/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.agentcore.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.miloss.fgsms.agentcore.IMessageProcessor;
import org.miloss.fgsms.agentcore.MessageProcessor;

/**
 * This is designed to stop and terminate the message process when the agent is
 * ran inside of a web application WAR or EAR file. It will block until the
 * queue has been purged, but no additional requests will be allowed to enter
 * the queue.
 *
 * @author AO
 */
public class ServetShutdownListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        IMessageProcessor singletonObject = MessageProcessor.getSingletonObject();
        System.out.println(sce.getServletContext().getContextPath() + " is shutting down. FGSMS data enqueued is " + singletonObject.outboundQueueSize());

        singletonObject.setRunning(false);
        while (singletonObject.outboundQueueSize() > 0) {
            System.out.println(sce.getServletContext().getContextPath() + " is shutting down. Waiting for queue to finish processing: items left: " + singletonObject.outboundQueueSize());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        singletonObject.terminate();

        org.apache.commons.logging.LogFactory.release(
                Thread.currentThread().getContextClassLoader());
        org.apache.commons.logging.LogFactory.release(this.getClass().getClassLoader());
        org.apache.log4j.LogManager.shutdown();
        
     

    }

}
