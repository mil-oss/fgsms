
package org.oasis_open.docs.wsn.brw_2;

import javax.xml.ws.WebFault;
import org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "PublisherRegistrationFailedFault", targetNamespace = "http://docs.oasis-open.org/wsn/br-2")
public class PublisherRegistrationFailedFault
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private PublisherRegistrationFailedFaultType faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public PublisherRegistrationFailedFault(String message, PublisherRegistrationFailedFaultType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public PublisherRegistrationFailedFault(String message, PublisherRegistrationFailedFaultType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.oasis_open.docs.wsn.br_2.PublisherRegistrationFailedFaultType
     */
    public PublisherRegistrationFailedFaultType getFaultInfo() {
        return faultInfo;
    }

}
