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

package org.miloss.fgsms.agents;

import java.io.IOException;
import org.apache.cxf.attachment.AttachmentDeserializer;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

/**
 *
 * @author AO
 */
public class CXFInterceotorOutClient extends org.apache.cxf.phase.AbstractPhaseInterceptor {

    public CXFInterceotorOutClient() {
        //super(Phase.PRE_INVOKE);
        super(Phase.PREPARE_SEND);
    }

    @Override
    public void handleMessage(Message message) {
        CXFCommonMessageHandler.ProcessRequest(message, true, this.getClass().getCanonicalName());
        
    }

    @Override
    public void handleFault(Message messageParam) {
        CXFCommonMessageHandler.ProcessResponse(messageParam, true, true, this.getClass().getCanonicalName());
    }
}
