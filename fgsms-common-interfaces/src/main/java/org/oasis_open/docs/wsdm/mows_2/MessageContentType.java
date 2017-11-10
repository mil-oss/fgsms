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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for MessageContentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageContentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Size" type="{http://docs.oasis-open.org/wsdm/mows-2.xsd}MessageContentSizeType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="NotIncluded" type="{http://docs.oasis-open.org/wsdm/mows-2.xsd}MessageContentNotIncludedFlag"/>
 *           &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="Binary" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *           &lt;element name="Xml" type="{http://docs.oasis-open.org/wsdm/mows-2.xsd}AnyXmlContentsType"/>
 *         &lt;/choice>
 *         &lt;any/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageContentType", propOrder = {
    "size",
    "notIncluded",
    "text",
    "binary",
    "xml",
    "any"
})
public class MessageContentType {

    @XmlElement(name = "Size")
    protected MessageContentSizeType size;
    @XmlElement(name = "NotIncluded")
    protected MessageContentNotIncludedFlag notIncluded;
    @XmlElement(name = "Text")
    protected String text;
    @XmlElement(name = "Binary")
    protected byte[] binary;
    @XmlElement(name = "Xml")
    protected AnyXmlContentsType xml;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link MessageContentSizeType }
     *     
     */
    public MessageContentSizeType getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageContentSizeType }
     *     
     */
    public void setSize(MessageContentSizeType value) {
        this.size = value;
    }

    /**
     * Gets the value of the notIncluded property.
     * 
     * @return
     *     possible object is
     *     {@link MessageContentNotIncludedFlag }
     *     
     */
    public MessageContentNotIncludedFlag getNotIncluded() {
        return notIncluded;
    }

    /**
     * Sets the value of the notIncluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageContentNotIncludedFlag }
     *     
     */
    public void setNotIncluded(MessageContentNotIncludedFlag value) {
        this.notIncluded = value;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the binary property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBinary() {
        return binary;
    }

    /**
     * Sets the value of the binary property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBinary(byte[] value) {
        this.binary = ((byte[]) value);
    }

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link AnyXmlContentsType }
     *     
     */
    public AnyXmlContentsType getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnyXmlContentsType }
     *     
     */
    public void setXml(AnyXmlContentsType value) {
        this.xml = value;
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
