
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for monthnames.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="monthnames">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Janurary"/>
 *     &lt;enumeration value="Feburary"/>
 *     &lt;enumeration value="March"/>
 *     &lt;enumeration value="April"/>
 *     &lt;enumeration value="May"/>
 *     &lt;enumeration value="June"/>
 *     &lt;enumeration value="July"/>
 *     &lt;enumeration value="August"/>
 *     &lt;enumeration value="September"/>
 *     &lt;enumeration value="October"/>
 *     &lt;enumeration value="November"/>
 *     &lt;enumeration value="December"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "monthnames")
@XmlEnum
public enum Monthnames {

    @XmlEnumValue("Janurary")
    JANURARY("Janurary"),
    @XmlEnumValue("Feburary")
    FEBURARY("Feburary"),
    @XmlEnumValue("March")
    MARCH("March"),
    @XmlEnumValue("April")
    APRIL("April"),
    @XmlEnumValue("May")
    MAY("May"),
    @XmlEnumValue("June")
    JUNE("June"),
    @XmlEnumValue("July")
    JULY("July"),
    @XmlEnumValue("August")
    AUGUST("August"),
    @XmlEnumValue("September")
    SEPTEMBER("September"),
    @XmlEnumValue("October")
    OCTOBER("October"),
    @XmlEnumValue("November")
    NOVEMBER("November"),
    @XmlEnumValue("December")
    DECEMBER("December");
    private final String value;

    Monthnames(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Monthnames fromValue(String v) {
        for (Monthnames c: Monthnames.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
