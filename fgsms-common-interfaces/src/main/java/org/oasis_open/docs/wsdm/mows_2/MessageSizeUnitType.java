/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.oasis_open.docs.wsdm.mows_2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageSizeUnitType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MessageSizeUnitType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="bit"/>
 *     &lt;enumeration value="byte"/>
 *     &lt;enumeration value="word"/>
 *     &lt;enumeration value="dword"/>
 *     &lt;enumeration value="qword"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MessageSizeUnitType")
@XmlEnum
public enum MessageSizeUnitType {

    @XmlEnumValue("bit")
    BIT("bit"),
    @XmlEnumValue("byte")
    BYTE("byte"),
    @XmlEnumValue("word")
    WORD("word"),
    @XmlEnumValue("dword")
    DWORD("dword"),
    @XmlEnumValue("qword")
    QWORD("qword");
    private final String value;

    MessageSizeUnitType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MessageSizeUnitType fromValue(String v) {
        for (MessageSizeUnitType c: MessageSizeUnitType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
