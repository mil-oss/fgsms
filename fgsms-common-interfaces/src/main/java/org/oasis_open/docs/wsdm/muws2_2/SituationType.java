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
import java.util.Calendar;


/**
 * <p>Java class for SituationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SituationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SituationCategory" type="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}SituationCategoryType"/>
 *         &lt;element name="SuccessDisposition" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="Successful"/>
 *               &lt;enumeration value="Unsuccessful"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SituationTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Priority" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="Severity" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         &lt;element name="Message" type="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}LangString" minOccurs="0"/>
 *         &lt;element name="SubstitutableMsg" type="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}SubstitutableMsgType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SituationType", propOrder = {
    "situationCategory",
    "successDisposition",
    "situationTime",
    "priority",
    "severity",
    "message",
    "substitutableMsg"
})
public class SituationType {

    @XmlElement(name = "SituationCategory", required = true)
    protected SituationCategoryType situationCategory;
    @XmlElement(name = "SuccessDisposition")
    protected String successDisposition;
    @XmlElement(name = "SituationTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar situationTime;
    @XmlElement(name = "Priority")
    protected Short priority;
    @XmlElement(name = "Severity")
    protected Short severity;
    @XmlElement(name = "Message")
    protected LangString message;
    @XmlElement(name = "SubstitutableMsg")
    protected SubstitutableMsgType substitutableMsg;

    /**
     * Gets the value of the situationCategory property.
     * 
     * @return
     *     possible object is
     *     {@link SituationCategoryType }
     *     
     */
    public SituationCategoryType getSituationCategory() {
        return situationCategory;
    }

    /**
     * Sets the value of the situationCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link SituationCategoryType }
     *     
     */
    public void setSituationCategory(SituationCategoryType value) {
        this.situationCategory = value;
    }

    /**
     * Gets the value of the successDisposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuccessDisposition() {
        return successDisposition;
    }

    /**
     * Sets the value of the successDisposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuccessDisposition(String value) {
        this.successDisposition = value;
    }

    /**
     * Gets the value of the situationTime property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getSituationTime() {
        return situationTime;
    }

    /**
     * Sets the value of the situationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setSituationTime(Calendar value) {
        this.situationTime = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setPriority(Short value) {
        this.priority = value;
    }

    /**
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSeverity(Short value) {
        this.severity = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link LangString }
     *     
     */
    public LangString getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link LangString }
     *     
     */
    public void setMessage(LangString value) {
        this.message = value;
    }

    /**
     * Gets the value of the substitutableMsg property.
     * 
     * @return
     *     possible object is
     *     {@link SubstitutableMsgType }
     *     
     */
    public SubstitutableMsgType getSubstitutableMsg() {
        return substitutableMsg;
    }

    /**
     * Sets the value of the substitutableMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubstitutableMsgType }
     *     
     */
    public void setSubstitutableMsg(SubstitutableMsgType value) {
        this.substitutableMsg = value;
    }

}
