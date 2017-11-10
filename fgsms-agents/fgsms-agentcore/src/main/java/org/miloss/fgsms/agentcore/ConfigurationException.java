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

package org.miloss.fgsms.agentcore;

/**a simple configuration exception. this lets you know that the configuration file is somehow wrong
 *
 * @author AO
 */
public class ConfigurationException extends Exception{
    String msg;

    public ConfigurationException(String LastErrorMessage) {
          this.msg = LastErrorMessage;
    }
    
    @Override
    public String getMessage()
    {
        
        return msg;
    }
      @Override
    public String getLocalizedMessage()
    {
        return msg;
    }

    
}
