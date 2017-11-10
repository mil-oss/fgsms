
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransportAuthenticationStyle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransportAuthenticationStyle">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="HTTP_BASIC"/>
 *     &lt;enumeration value="HTTP_NTLM"/>
 *     &lt;enumeration value="HTTP_DIGEST"/>
 *     &lt;enumeration value="OTHER"/>
 *     &lt;enumeration value="NA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransportAuthenticationStyle")
@XmlEnum
public enum TransportAuthenticationStyle {

    HTTP_BASIC,
    HTTP_NTLM,
    HTTP_DIGEST,
    OTHER,

    /**
     * default value
     * 
     */
    NA;

    public String value() {
        return name();
    }

    public static TransportAuthenticationStyle fromValue(String v) {
        return valueOf(v);
    }

}
