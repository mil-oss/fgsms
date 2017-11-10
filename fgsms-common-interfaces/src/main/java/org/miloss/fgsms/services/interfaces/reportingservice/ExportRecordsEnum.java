
package org.miloss.fgsms.services.interfaces.reportingservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExportRecordsEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExportRecordsEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Transactions"/>
 *     &lt;enumeration value="Machine"/>
 *     &lt;enumeration value="Process"/>
 *     &lt;enumeration value="Availability"/>
 *     &lt;enumeration value="Statistics"/>
 *     &lt;enumeration value="AuditLogs"/>
 *     &lt;enumeration value="All"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExportRecordsEnum")
@XmlEnum
public enum ExportRecordsEnum {

    @XmlEnumValue("Transactions")
    TRANSACTIONS("Transactions"),
    @XmlEnumValue("Machine")
    MACHINE("Machine"),
    @XmlEnumValue("Process")
    PROCESS("Process"),
    @XmlEnumValue("Availability")
    AVAILABILITY("Availability"),
    @XmlEnumValue("Statistics")
    STATISTICS("Statistics"),
    @XmlEnumValue("AuditLogs")
    AUDIT_LOGS("AuditLogs"),
    @XmlEnumValue("All")
    ALL("All");
    private final String value;

    ExportRecordsEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExportRecordsEnum fromValue(String v) {
        for (ExportRecordsEnum c: ExportRecordsEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
