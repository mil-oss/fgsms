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
 
package org.miloss.fgsms.osagent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AO
 */
public class AgentThreadRunner implements Runnable{
    
    public AgentThreadRunner()
    {}
    public AgentThreadRunner(   String[] inargs)
    {
        this.args = inargs;
    
    }
    public String[] args;
     protected OSAgent m= null;
    @Override
    public void run() {
        try {
            m=new OSAgent();
            m.startup(args);
        } catch (Exception ex) {
            Logger.getLogger(AgentThreadRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
