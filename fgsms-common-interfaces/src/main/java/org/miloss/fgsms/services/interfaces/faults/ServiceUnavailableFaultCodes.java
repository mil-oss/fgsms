
package org.miloss.fgsms.services.interfaces.faults;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceUnavailableFaultCodes.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ServiceUnavailableFaultCodes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DataBasePermissionError"/>
 *     &lt;enumeration value="UserPermissionError"/>
 *     &lt;enumeration value="DataBaseUnavailable"/>
 *     &lt;enumeration value="Misconfiguration"/>
 *     &lt;enumeration value="UnexpectedError"/>
 *     &lt;enumeration value="AgentsDisabledError"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ServiceUnavailableFaultCodes")
@XmlEnum
public enum ServiceUnavailableFaultCodes {


    /**
     * 
     *              Happens when the supplied credentials cannot perform some kind of query in the database
     *              
     * 
     */
    @XmlEnumValue("DataBasePermissionError")
    DATA_BASE_PERMISSION_ERROR("DataBasePermissionError"),

    /**
     * 
     *              Happens when the supplied user credentials does not have access to the requested resource or action.
     *              
     * 
     */
    @XmlEnumValue("UserPermissionError")
    USER_PERMISSION_ERROR("UserPermissionError"),

    /**
     * 
     *              Happens when the database is either offline or access to the database was denied for the
     *              specified credentials
     *              
     * 
     */
    @XmlEnumValue("DataBaseUnavailable")
    DATA_BASE_UNAVAILABLE("DataBaseUnavailable"),

    /**
     * 
     *              Happens when the connection string is not present in the configuration file.
     *              
     * 
     */
    @XmlEnumValue("Misconfiguration")
    MISCONFIGURATION("Misconfiguration"),

    /**
     * 
     *             A generic error message indicating an unhandled exception or fault.
     *             
     * 
     */
    @XmlEnumValue("UnexpectedError")
    UNEXPECTED_ERROR("UnexpectedError"),

    /**
     * Indicates that an agent has sent peformance data however the global settings have stated that all agent traffic should be disabled.
     * 
     */
    @XmlEnumValue("AgentsDisabledError")
    AGENTS_DISABLED_ERROR("AgentsDisabledError");
    private final String value;

    ServiceUnavailableFaultCodes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ServiceUnavailableFaultCodes fromValue(String v) {
        for (ServiceUnavailableFaultCodes c: ServiceUnavailableFaultCodes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
