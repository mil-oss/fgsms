
package us.gov.ic.ism.v2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClassificationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClassificationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="U"/>
 *     &lt;enumeration value="C"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="TS"/>
 *     &lt;enumeration value="R"/>
 *     &lt;enumeration value="CTS"/>
 *     &lt;enumeration value="CTS-B"/>
 *     &lt;enumeration value="CTS-BALK"/>
 *     &lt;enumeration value="NU"/>
 *     &lt;enumeration value="NR"/>
 *     &lt;enumeration value="NC"/>
 *     &lt;enumeration value="NS"/>
 *     &lt;enumeration value="NS-S"/>
 *     &lt;enumeration value="NS-A"/>
 *     &lt;enumeration value="CTSA"/>
 *     &lt;enumeration value="NSAT"/>
 *     &lt;enumeration value="NCA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClassificationType", namespace = "urn:us:gov:ic:ism:v2")
@XmlEnum
public enum ClassificationType {


    /**
     * 
     * US, non-US or joint UNCLASSIFIED.
     *               
     * 
     */
    U("U"),
    C("C"),
    S("S"),
    TS("TS"),
    R("R"),
    CTS("CTS"),
    @XmlEnumValue("CTS-B")
    CTS_B("CTS-B"),
    @XmlEnumValue("CTS-BALK")
    CTS_BALK("CTS-BALK"),
    NU("NU"),
    NR("NR"),
    NC("NC"),
    NS("NS"),
    @XmlEnumValue("NS-S")
    NS_S("NS-S"),
    @XmlEnumValue("NS-A")
    NS_A("NS-A"),
    CTSA("CTSA"),
    NSAT("NSAT"),
    NCA("NCA");
    private final String value;

    ClassificationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClassificationType fromValue(String v) {
        for (ClassificationType c: ClassificationType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
