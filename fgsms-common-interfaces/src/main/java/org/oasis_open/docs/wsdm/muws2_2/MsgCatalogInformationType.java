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

package org.oasis_open.docs.wsdm.muws2_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MsgCatalogInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MsgCatalogInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msgCatalog" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="msgCatalogType" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MsgCatalogInformationType", propOrder = {
    "msgCatalog",
    "msgCatalogType"
})
public class MsgCatalogInformationType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String msgCatalog;
    @XmlSchemaType(name = "anyURI")
    protected String msgCatalogType;

    /**
     * Gets the value of the msgCatalog property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgCatalog() {
        return msgCatalog;
    }

    /**
     * Sets the value of the msgCatalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgCatalog(String value) {
        this.msgCatalog = value;
    }

    /**
     * Gets the value of the msgCatalogType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgCatalogType() {
        return msgCatalogType;
    }

    /**
     * Sets the value of the msgCatalogType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgCatalogType(String value) {
        this.msgCatalogType = value;
    }

}
