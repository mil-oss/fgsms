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
 
package  org.miloss.fgsms.osagent.sensor;

import java.util.Properties;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;

/**
 * An interface for sensor data providers to implement. The result set of this 
 * call will be provided to the centralized fgsms server
 * @author AO
 */
public interface ISensorProvider {
    /**
     * Called every iteration of the OS agent. 
     * @return 
     */
    public NameValuePair getSensorData();
    
    /**
     * Called on startup
     * @param p 
     */
    public void init(Properties p);
    
    /**
     * called on OS agent shutdown. Use this function to clean up any external resources, such as serial ports
     */
    public void stop();

}
