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

package org.miloss.fgsms.services.rs.impl;

/**
 * This is a managed bean adapter instance for the singleton SLA
 * Processor Only needed when jboss thread pools are not available
 *
 * @author AO
 */
public class RSProcessorController implements RSProcessorControllerMBean {

    @Override
    public void Start() {
        RSProcessorSingleton instance = RSProcessorSingleton.getInstance();
        RSProcessorSingleton.running = true;
        RSProcessorSingleton.run();
    }

    @Override
    public void Stop() {
        RSProcessorSingleton instance = RSProcessorSingleton.getInstance();
        RSProcessorSingleton.running = false;
    }

    @Override
    public int SizeOfQueue() {
        RSProcessorSingleton instance = RSProcessorSingleton.getInstance();
        return RSProcessorSingleton.GetQueueSize();
    }
}
