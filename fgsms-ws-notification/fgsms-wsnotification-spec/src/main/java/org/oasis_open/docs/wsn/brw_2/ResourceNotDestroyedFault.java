
package org.oasis_open.docs.wsn.brw_2;

import javax.xml.ws.WebFault;
import org.oasis_open.docs.wsn.br_2.ResourceNotDestroyedFaultType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "ResourceNotDestroyedFault", targetNamespace = "http://docs.oasis-open.org/wsn/br-2")
public class ResourceNotDestroyedFault
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private ResourceNotDestroyedFaultType faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public ResourceNotDestroyedFault(String message, ResourceNotDestroyedFaultType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ResourceNotDestroyedFault(String message, ResourceNotDestroyedFaultType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.oasis_open.docs.wsn.br_2.ResourceNotDestroyedFaultType
     */
    public ResourceNotDestroyedFaultType getFaultInfo() {
        return faultInfo;
    }

}