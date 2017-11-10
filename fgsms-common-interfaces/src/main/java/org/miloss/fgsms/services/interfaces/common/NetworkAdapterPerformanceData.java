
package org.miloss.fgsms.services.interfaces.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NetworkAdapterPerformanceData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NetworkAdapterPerformanceData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="kilobytespersecondNetworkReceive" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="kilobytespersecondNetworkTransmit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="adapterName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkAdapterPerformanceData", propOrder = {
    "kilobytespersecondNetworkReceive",
    "kilobytespersecondNetworkTransmit",
    "adapterName"
})
public class NetworkAdapterPerformanceData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long kilobytespersecondNetworkReceive;
    @XmlElement(required = true, type = Long.class, nillable = true)
    protected Long kilobytespersecondNetworkTransmit;
    @XmlElement(required = true)
    protected String adapterName;

    /**
     * Gets the value of the kilobytespersecondNetworkReceive property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getKilobytespersecondNetworkReceive() {
        return kilobytespersecondNetworkReceive;
    }

    /**
     * Sets the value of the kilobytespersecondNetworkReceive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setKilobytespersecondNetworkReceive(Long value) {
        this.kilobytespersecondNetworkReceive = value;
    }

    /**
     * Gets the value of the kilobytespersecondNetworkTransmit property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getKilobytespersecondNetworkTransmit() {
        return kilobytespersecondNetworkTransmit;
    }

    /**
     * Sets the value of the kilobytespersecondNetworkTransmit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setKilobytespersecondNetworkTransmit(Long value) {
        this.kilobytespersecondNetworkTransmit = value;
    }

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

}
