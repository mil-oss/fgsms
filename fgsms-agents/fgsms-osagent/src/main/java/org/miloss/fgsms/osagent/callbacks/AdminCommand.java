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
 
package org.miloss.fgsms.osagent.callbacks;

/**
 * This is a container for working with administrative remote commands from fgsms administrators
 * @author AO
 */
@Deprecated
public class AdminCommand {
    String workingdir;
    String command = new String();
    String result_stderr = new String();
    String result_stdout = new String();
    int exitcode = 0;
    long enqueuedat = -1;
    long completedat = -1;
    String id;
    String authcode;
    boolean waitfor = false;
    private final RemoteAgentCallbackImpl outer;

    AdminCommand(final RemoteAgentCallbackImpl outer) {
        this.outer = outer;
    }

}
