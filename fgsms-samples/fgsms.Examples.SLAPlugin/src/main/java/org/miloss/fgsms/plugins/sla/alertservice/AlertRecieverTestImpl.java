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
package org.miloss.fgsms.plugins.sla.alertservice;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.Calendar;
import javax.xml.ws.RequestWrapper;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

@WebService(name = "AlertRecieverPortType", targetNamespace = "urn:mil:army:cerdec:fgsms:plugins:sla:alertservice")
@XmlSeeAlso({
    ObjectFactory.class
})
public class AlertRecieverTestImpl {

    static final Logger log = Logger.getLogger("RecieverServiceAlertsSample");
    public static int count=0;
    /**
     *
     * @param message
     * @param timestamp
     */
    @WebMethod(operationName = "RecieveServiceAlert", action = "urn:RecieveServiceAlert")
    @Oneway
    @RequestWrapper(localName = "RecieveServiceAlert", targetNamespace = "urn:mil:army:cerdec:fgsms:plugins:sla:alertservice", className = "mil.army.cerdec.fgsms.plugins.sla.alertservice.RecieveServiceAlert")
    public void recieveServiceAlert(
            @WebParam(name = "utcdatetime", targetNamespace = "urn:mil:army:cerdec:fgsms:plugins:sla:alertservice") Calendar timestamp,
            @WebParam(name = "message", targetNamespace = "urn:mil:army:cerdec:fgsms:plugins:sla:alertservice") String message) {
        count++;
        if (timestamp != null) {
            log.log(Level.WARN, "Alert Recieved " + timestamp.toString() + " " + message);
        } else {
            log.log(Level.WARN, "Alert Recieved " + " null timestamp " + " " + message);
        }
    }
}
