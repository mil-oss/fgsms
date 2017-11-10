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
 *
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.agentcore;

import org.miloss.fgsms.agentcore.mp.DefaultMessageProcessor;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;

/**
 * The Message Processor is a factory/single pattern class that provides access
 * to a singular instance of the IMessageProcessor.
 *
 * @see DefaultMessageProcessor
 *
 * @author AO
 */
public class MessageProcessor {

    private final static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    private static IMessageProcessor singletonObject;

    public static void setSingletonObject(Object object) {
        singletonObject = (IMessageProcessor) object;
    }

    /**
     * gets a reference to the singleton class. be sure to call this before any
     * other static methods
     *
     * @return
     */
    public static synchronized IMessageProcessor getSingletonObject() {
        if (singletonObject == null) {
            try {
                Properties p = ConfigLoader.loadProperties();
                if (p.containsKey(ConfigLoader.PROP_MESSAGE_PROCESSOR_IMPL)) {
                    String impl = p.getProperty(ConfigLoader.PROP_MESSAGE_PROCESSOR_IMPL);
                    IMessageProcessor mp = (IMessageProcessor) Class.forName(impl).newInstance();
                    singletonObject = mp;
                }
            } catch (ConfigurationException ex) {
                log.log(Level.WARN, "failed to load custom message processor implementation, falling back to default implementation", ex);
            } catch (NullPointerException ex) {
                log.log(Level.WARN, "failed to load custom message processor implementation, falling back to default implementation", ex);
            } catch (ClassNotFoundException ex) {
                log.log(Level.WARN, "failed to load custom message processor implementation, falling back to default implementation", ex);
            } catch (InstantiationException ex) {
                log.log(Level.WARN, "failed to load custom message processor implementation, falling back to default implementation", ex);
            } catch (IllegalAccessException ex) {
                log.log(Level.WARN, "failed to load custom message processor implementation, falling back to default implementation", ex);
            } catch (Throwable t) {
                log.log(Level.WARN, "failed to load custom message processor implementation, falling back to default implementation", t);
            }

            if (singletonObject == null) {
                singletonObject = new DefaultMessageProcessor();
            }
            try {
                MessageProcessorAdapter cache = new MessageProcessorAdapter();
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                ObjectName name = new ObjectName("org.miloss.fgsms.MessageProcessor:type=MessageProcessorAdapterMBean");
                mbs.registerMBean(cache, name);
            } catch (javax.management.InstanceAlreadyExistsException x) {

            } catch (Exception ex) {
                log.log(Level.WARN, "unable to register MessageProcessor mbean", ex);
            }
        }
        return singletonObject;
    }

}
