
package org.miloss.fgsms.services.interfaces.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for policyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="policyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Transactional"/>
 *     &lt;enumeration value="Statistical"/>
 *     &lt;enumeration value="Status"/>
 *     &lt;enumeration value="Machine"/>
 *     &lt;enumeration value="Process"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "policyType")
@XmlEnum
public enum PolicyType {

    @XmlEnumValue("Transactional")
    TRANSACTIONAL("Transactional"),
    @XmlEnumValue("Statistical")
    STATISTICAL("Statistical"),
    @XmlEnumValue("Status")
    STATUS("Status"),
    @XmlEnumValue("Machine")
    MACHINE("Machine"),
    @XmlEnumValue("Process")
    PROCESS("Process");
    private final String value;

    PolicyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PolicyType fromValue(String v) {
        for (PolicyType c: PolicyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
