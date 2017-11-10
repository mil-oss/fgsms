
package org.miloss.fgsms.services.interfaces.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AgentActionStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AgentActionStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="New"/>
 *     &lt;enumeration value="Received"/>
 *     &lt;enumeration value="Processing"/>
 *     &lt;enumeration value="Complete"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AgentActionStatus")
@XmlEnum
public enum AgentActionStatus {

    @XmlEnumValue("New")
    NEW("New"),
    @XmlEnumValue("Received")
    RECEIVED("Received"),
    @XmlEnumValue("Processing")
    PROCESSING("Processing"),
    @XmlEnumValue("Complete")
    COMPLETE("Complete");
    private final String value;

    AgentActionStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AgentActionStatus fromValue(String v) {
        for (AgentActionStatus c: AgentActionStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
