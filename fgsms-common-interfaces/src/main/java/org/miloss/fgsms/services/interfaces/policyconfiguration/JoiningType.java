
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JoiningType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="JoiningType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="And"/>
 *     &lt;enumeration value="Or"/>
 *     &lt;enumeration value="Not"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "JoiningType")
@XmlEnum
public enum JoiningType {


    /**
     * 
     * join with AND logic, applies to all services
     * 			
     * 
     */
    @XmlEnumValue("And")
    AND("And"),

    /**
     * 
     * join with OR logic
     * 			
     * 
     */
    @XmlEnumValue("Or")
    OR("Or"),

    /**
     * 
     * negate the response
     * 			
     * 
     */
    @XmlEnumValue("Not")
    NOT("Not");
    private final String value;

    JoiningType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static JoiningType fromValue(String v) {
        for (JoiningType c: JoiningType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
