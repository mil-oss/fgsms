
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AlertType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AlertType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SLAFault"/>
 *     &lt;enumeration value="MessageFault"/>
 *     &lt;enumeration value="OperatingStatus"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AlertType")
@XmlEnum
public enum AlertType {


    /**
     * an SLA fault
     * 
     */
    @XmlEnumValue("SLAFault")
    SLA_FAULT("SLAFault"),

    /**
     * a transactional item has faulted
     * 
     */
    @XmlEnumValue("MessageFault")
    MESSAGE_FAULT("MessageFault"),

    /**
     * something is offline
     * 
     */
    @XmlEnumValue("OperatingStatus")
    OPERATING_STATUS("OperatingStatus");
    private final String value;

    AlertType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AlertType fromValue(String v) {
        for (AlertType c: AlertType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
