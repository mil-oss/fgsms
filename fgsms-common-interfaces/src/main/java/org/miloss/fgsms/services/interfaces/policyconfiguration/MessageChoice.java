
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageChoice.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MessageChoice">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Request"/>
 *     &lt;enumeration value="Response"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MessageChoice")
@XmlEnum
public enum MessageChoice {


    /**
     * 
     * process on the xml request
     * 			
     * 
     */
    @XmlEnumValue("Request")
    REQUEST("Request"),

    /**
     * 
     * process on the xml response
     * 			
     * 
     */
    @XmlEnumValue("Response")
    RESPONSE("Response");
    private final String value;

    MessageChoice(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MessageChoice fromValue(String v) {
        for (MessageChoice c: MessageChoice.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
