
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.0
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
