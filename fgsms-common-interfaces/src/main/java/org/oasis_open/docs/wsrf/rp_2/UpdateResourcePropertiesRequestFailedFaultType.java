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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.wsrf.bf_2.BaseFaultType;


/**
 * <p>Java class for UpdateResourcePropertiesRequestFailedFaultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateResourcePropertiesRequestFailedFaultType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsrf/bf-2}BaseFaultType">
 *       &lt;sequence>
 *         &lt;element name="ResourcePropertyChangeFailure" type="{http://docs.oasis-open.org/wsrf/rp-2}ResourcePropertyChangeFailureType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateResourcePropertiesRequestFailedFaultType", propOrder = {
    "resourcePropertyChangeFailure"
})
public class UpdateResourcePropertiesRequestFailedFaultType
    extends BaseFaultType
{

    @XmlElement(name = "ResourcePropertyChangeFailure", required = true)
    protected ResourcePropertyChangeFailureType resourcePropertyChangeFailure;

    /**
     * Gets the value of the resourcePropertyChangeFailure property.
     * 
     * @return
     *     possible object is
     *     {@link ResourcePropertyChangeFailureType }
     *     
     */
    public ResourcePropertyChangeFailureType getResourcePropertyChangeFailure() {
        return resourcePropertyChangeFailure;
    }

    /**
     * Sets the value of the resourcePropertyChangeFailure property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourcePropertyChangeFailureType }
     *     
     */
    public void setResourcePropertyChangeFailure(ResourcePropertyChangeFailureType value) {
        this.resourcePropertyChangeFailure = value;
    }

}
