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

package org.oasis_open.docs.wsrf.rp_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResourcePropertyValueChangeNotificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourcePropertyValueChangeNotificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OldValues" type="Object" minOccurs="0"/>
 *         &lt;element name="NewValues" type="Object"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourcePropertyValueChangeNotificationType", propOrder = {
    "oldValues",
    "newValues"
})
public class ResourcePropertyValueChangeNotificationType {

    @XmlElementRef(name = "OldValues", namespace = "http://docs.oasis-open.org/wsrf/rp-2", type = JAXBElement.class)

    protected JAXBElement<Object> oldValues;
    @XmlElement(name = "NewValues", required = true, nillable = true)

    protected Object newValues;

    /**
     * Gets the value of the oldValues property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EsmPropertiesChoiceType }{@code >}
     *     
     */

    public JAXBElement<Object> getOldValues() {
        return oldValues;
    }

    /**
     * Sets the value of the oldValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EsmPropertiesChoiceType }{@code >}
     *     
     */

    public void setOldValues(JAXBElement<Object> value) {
        this.oldValues = ((JAXBElement<Object> ) value);
    }

    /**
     * Gets the value of the newValues property.
     * 
     * @return
     *     possible object is
     *     {@link EsmPropertiesChoiceType }
     */

    public Object getNewValues() {
        return newValues;
    }

    /**
     * Sets the value of the newValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link EsmPropertiesChoiceType }
     */     

    public void setNewValues(Object value) {
        this.newValues = value;
    }

}
