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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


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
 *         &lt;element name="Request" type="{http://docs.oasis-open.org/wsdm/mows-2.xsd}MessageInformationType" minOccurs="0"/>
 *         &lt;element name="Reply" type="{http://docs.oasis-open.org/wsdm/mows-2.xsd}MessageInformationType" minOccurs="0"/>
 *         &lt;element name="StateInformation" type="{http://docs.oasis-open.org/wsdm/mows-2.xsd}RequestProcessingStateInformationType"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="CurrentTime" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request",
    "reply",
    "stateInformation",
    "any"
})
@XmlRootElement(name = "RequestProcessingNotification")
public class RequestProcessingNotification {

    @XmlElement(name = "Request")
    protected MessageInformationType request;
    @XmlElement(name = "Reply")
    protected MessageInformationType reply;
    @XmlElement(name = "StateInformation", required = true)
    protected RequestProcessingStateInformationType stateInformation;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "CurrentTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar currentTime;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link MessageInformationType }
     *     
     */
    public MessageInformationType getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageInformationType }
     *     
     */
    public void setRequest(MessageInformationType value) {
        this.request = value;
    }

    /**
     * Gets the value of the reply property.
     * 
     * @return
     *     possible object is
     *     {@link MessageInformationType }
     *     
     */
    public MessageInformationType getReply() {
        return reply;
    }

    /**
     * Sets the value of the reply property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageInformationType }
     *     
     */
    public void setReply(MessageInformationType value) {
        this.reply = value;
    }

    /**
     * Gets the value of the stateInformation property.
     * 
     * @return
     *     possible object is
     *     {@link RequestProcessingStateInformationType }
     *     
     */
    public RequestProcessingStateInformationType getStateInformation() {
        return stateInformation;
    }

    /**
     * Sets the value of the stateInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestProcessingStateInformationType }
     *     
     */
    public void setStateInformation(RequestProcessingStateInformationType value) {
        this.stateInformation = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the currentTime property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets the value of the currentTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setCurrentTime(Calendar value) {
        this.currentTime = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
