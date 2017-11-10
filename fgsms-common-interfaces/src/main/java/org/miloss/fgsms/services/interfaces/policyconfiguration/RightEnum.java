
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RightEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RightEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="administer"/>
 *     &lt;enumeration value="audit"/>
 *     &lt;enumeration value="write"/>
 *     &lt;enumeration value="read"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RightEnum")
@XmlEnum
public enum RightEnum {


    /**
     * 
     *                         Change Permissions
     *                     
     * 
     */
    @XmlEnumValue("administer")
    ADMINISTER("administer"),

    /**
     * 
     *                         Read Request/Response messages, Read change logs
     *                     
     * 
     */
    @XmlEnumValue("audit")
    AUDIT("audit"),

    /**
     * 
     *                         Change Policies
     *                     
     * 
     */
    @XmlEnumValue("write")
    WRITE("write"),

    /**
     * 
     *                         Read Performance Statistics
     *                     
     * 
     */
    @XmlEnumValue("read")
    READ("read");
    private final String value;

    RightEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RightEnum fromValue(String v) {
        for (RightEnum c: RightEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
