
package org.miloss.fgsms.services.interfaces.common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkAdapterInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NetworkAdapterInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="adapterName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="adapterDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mtu" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="mac" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ip" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dns" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SubnetMask" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="defaultGateway" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkAdapterInfo", propOrder = {
    "adapterName",
    "adapterDescription",
    "mtu",
    "mac",
    "ip",
    "dns",
    "subnetMask",
    "defaultGateway"
})
public class NetworkAdapterInfo {

    @XmlElement(required = true)
    protected String adapterName;
    @XmlElement(required = true)
    protected String adapterDescription;
    protected long mtu;
    @XmlElement(required = true)
    protected String mac;
    protected List<String> ip;
    protected List<String> dns;
    @XmlElement(name = "SubnetMask", required = true)
    protected String subnetMask;
    @XmlElement(required = true)
    protected String defaultGateway;

    /**
     * Gets the value of the adapterName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdapterName() {
        return adapterName;
    }

    /**
     * Sets the value of the adapterName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdapterName(String value) {
        this.adapterName = value;
    }

    /**
     * Gets the value of the adapterDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdapterDescription() {
        return adapterDescription;
    }

    /**
     * Sets the value of the adapterDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdapterDescription(String value) {
        this.adapterDescription = value;
    }

    /**
     * Gets the value of the mtu property.
     * 
     */
    public long getMtu() {
        return mtu;
    }

    /**
     * Sets the value of the mtu property.
     * 
     */
    public void setMtu(long value) {
        this.mtu = value;
    }

    /**
     * Gets the value of the mac property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMac() {
        return mac;
    }

    /**
     * Sets the value of the mac property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMac(String value) {
        this.mac = value;
    }

    /**
     * Gets the value of the ip property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ip property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIp() {
        if (ip == null) {
            ip = new ArrayList<String>();
        }
        return this.ip;
    }

    /**
     * Gets the value of the dns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDns() {
        if (dns == null) {
            dns = new ArrayList<String>();
        }
        return this.dns;
    }

    /**
     * Gets the value of the subnetMask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubnetMask() {
        return subnetMask;
    }

    /**
     * Sets the value of the subnetMask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubnetMask(String value) {
        this.subnetMask = value;
    }

    /**
     * Gets the value of the defaultGateway property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultGateway() {
        return defaultGateway;
    }

    /**
     * Sets the value of the defaultGateway property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultGateway(String value) {
        this.defaultGateway = value;
    }

}
