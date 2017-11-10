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
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.wcfservice1.CompositeType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetDataUsingDataContractResult" type="{http://schemas.datacontract.org/2004/07/WcfService1}CompositeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getDataUsingDataContractResult"
})
@XmlRootElement(name = "GetDataUsingDataContractResponse")
public class GetDataUsingDataContractResponse {

    @XmlElementRef(name = "GetDataUsingDataContractResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<CompositeType> getDataUsingDataContractResult;

    /**
     * Gets the value of the getDataUsingDataContractResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CompositeType }{@code >}
     *     
     */
    public JAXBElement<CompositeType> getGetDataUsingDataContractResult() {
        return getDataUsingDataContractResult;
    }

    /**
     * Sets the value of the getDataUsingDataContractResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CompositeType }{@code >}
     *     
     */
    public void setGetDataUsingDataContractResult(JAXBElement<CompositeType> value) {
        this.getDataUsingDataContractResult = ((JAXBElement<CompositeType> ) value);
    }

}
