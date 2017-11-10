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

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

/**
 *
 * @author AO
 */
public class CXFInterceptorInService extends org.apache.cxf.phase.AbstractPhaseInterceptor {

    public CXFInterceptorInService() {
        super(Phase.POST_LOGICAL);
    }

    @Override
    public void handleMessage(Message message) {
        Boolean isclient = (Boolean) message.get("org.apache.cxf.client");
        Boolean isinbound = (Boolean) message.get("org.apache.cxf.message.inbound");
        if (isclient == null) {
            isclient = false;
        }
        if (isinbound == null) {
            isinbound = false;
        }

        if (isclient && !isinbound) //client outbound request
        {
            CXFCommonMessageHandler.ProcessRequest(message, true, this.getClass().getCanonicalName());
        } else if (isclient && isinbound) {
            CXFCommonMessageHandler.ProcessResponse(message, false, true, this.getClass().getCanonicalName());
        } else {
            CXFCommonMessageHandler.ProcessRequest(message, false, this.getClass().getCanonicalName());
        }

        // CXFCommonMessageHandler.ProcessResponse(message, false, true, this.getClass().getCanonicalName());

    }

    @Override
    public void handleFault(Message message) {
        //   CXFCommonMessageHandler.ProcessRequest(message, true, this.getClass().getCanonicalName());

        CXFCommonMessageHandler.ProcessResponse(message, true, true, this.getClass().getCanonicalName());

    }
}
