//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.30 at 02:01:39 PM EDT 
//


package org.miloss.fgsms.auxstatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{}durable"/>
 *         &lt;element ref="{}stateful"/>
 *         &lt;element ref="{}volatile"/>
 *       &lt;/sequence>
 *       &lt;attribute name=namecol use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="group" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "durable",
    "stateful",
    "_volatile"
})
@XmlRootElement(name = "job")
public class Job {

    protected boolean durable;
    protected boolean stateful;
    @XmlElement(name = "volatile")
    protected boolean _volatile;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected String group;

    /**
     * Gets the value of the durable property.
     * 
     */
    public boolean isDurable() {
        return durable;
    }

    /**
     * Sets the value of the durable property.
     * 
     */
    public void setDurable(boolean value) {
        this.durable = value;
    }

    /**
     * Gets the value of the stateful property.
     * 
     */
    public boolean isStateful() {
        return stateful;
    }

    /**
     * Sets the value of the stateful property.
     * 
     */
    public void setStateful(boolean value) {
        this.stateful = value;
    }

    /**
     * Gets the value of the volatile property.
     * 
     */
    public boolean isVolatile() {
        return _volatile;
    }

    /**
     * Sets the value of the volatile property.
     * 
     */
    public void setVolatile(boolean value) {
        this._volatile = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
    }

}
