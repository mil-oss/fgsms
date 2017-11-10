
package org.oasis_open.docs.wsn.brw_2;

import javax.xml.ws.WebFault;
import org.oasis_open.docs.wsn.b_2.UnableToCreatePullPointFaultType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "UnableToCreatePullPointFault", targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
public class UnableToCreatePullPointFault
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private UnableToCreatePullPointFaultType faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public UnableToCreatePullPointFault(String message, UnableToCreatePullPointFaultType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public UnableToCreatePullPointFault(String message, UnableToCreatePullPointFaultType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: org.oasis_open.docs.wsn.b_2.UnableToCreatePullPointFaultType
     */
    public UnableToCreatePullPointFaultType getFaultInfo() {
        return faultInfo;
    }

}
