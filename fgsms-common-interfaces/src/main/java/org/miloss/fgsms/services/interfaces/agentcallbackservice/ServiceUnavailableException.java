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

package org.miloss.fgsms.services.interfaces.agentcallbackservice;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-04/11/2011 03:11 PM(mockbuild)-
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "ServiceUnavailableException", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:faults")
public class ServiceUnavailableException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public ServiceUnavailableException(String message, org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ServiceUnavailableException(String message, org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException
     */
    public org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableException getFaultInfo() {
        return faultInfo;
    }

}
