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

package com.examples.wsdl.helloservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="SayHelloRes" type="{http://www.examples.com/wsdl/HelloService}SayHelloRes"/>
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
    "sayHelloRes"
})
@XmlRootElement(name = "SayHelloResponse")
public class SayHelloResponse {

    @XmlElement(name = "SayHelloRes", required = true)
    protected SayHelloRes sayHelloRes;

    /**
     * Gets the value of the sayHelloRes property.
     * 
     * @return
     *     possible object is
     *     {@link SayHelloRes }
     *     
     */
    public SayHelloRes getSayHelloRes() {
        return sayHelloRes;
    }

    /**
     * Sets the value of the sayHelloRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link SayHelloRes }
     *     
     */
    public void setSayHelloRes(SayHelloRes value) {
        this.sayHelloRes = value;
    }

}
