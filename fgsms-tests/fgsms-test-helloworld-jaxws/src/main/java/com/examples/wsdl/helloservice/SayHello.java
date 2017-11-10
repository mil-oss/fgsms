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
 *         &lt;element name="SayHelloReq" type="{http://www.examples.com/wsdl/HelloService}SayHelloReq"/>
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
    "sayHelloReq"
})
@XmlRootElement(name = "SayHello")
public class SayHello {

    @XmlElement(name = "SayHelloReq", required = true)
    protected SayHelloReq sayHelloReq;

    /**
     * Gets the value of the sayHelloReq property.
     * 
     * @return
     *     possible object is
     *     {@link SayHelloReq }
     *     
     */
    public SayHelloReq getSayHelloReq() {
        return sayHelloReq;
    }

    /**
     * Sets the value of the sayHelloReq property.
     * 
     * @param value
     *     allowed object is
     *     {@link SayHelloReq }
     *     
     */
    public void setSayHelloReq(SayHelloReq value) {
        this.sayHelloReq = value;
    }

}
