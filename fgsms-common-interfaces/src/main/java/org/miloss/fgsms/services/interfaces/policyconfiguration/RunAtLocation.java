
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RunAtLocation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RunAtLocation">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="fgsmsServer"/>
 *     &lt;enumeration value="fgsmsAgent"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RunAtLocation")
@XmlEnum
public enum RunAtLocation {


    /**
     * Runs at the fgsms server from the context of the SLA Processor
     * 
     */
    @XmlEnumValue("fgsmsServer")
    FGSMS_SERVER("fgsmsServer"),

    /**
     * Runs at the location of the fgsms agent 
     * 
     */
    @XmlEnumValue("fgsmsAgent")
    FGSMS_AGENT("fgsmsAgent");
    private final String value;

    RunAtLocation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RunAtLocation fromValue(String v) {
        for (RunAtLocation c: RunAtLocation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
