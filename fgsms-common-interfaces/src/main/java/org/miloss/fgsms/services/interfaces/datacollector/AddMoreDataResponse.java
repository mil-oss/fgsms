
package org.miloss.fgsms.services.interfaces.datacollector;

import java.io.Serializable;
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
 *         &lt;element name="AddDataResult" type="{urn:org:miloss:fgsms:services:interfaces:dataCollector}AddDataResponseMsg"/>
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
    "addDataResult"
})
@XmlRootElement(name = "AddMoreDataResponse")
public class AddMoreDataResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AddDataResult", required = true, nillable = true)
    protected AddDataResponseMsg addDataResult;

    /**
     * Gets the value of the addDataResult property.
     * 
     * @return
     *     possible object is
     *     {@link AddDataResponseMsg }
     *     
     */
    public AddDataResponseMsg getAddDataResult() {
        return addDataResult;
    }

    /**
     * Sets the value of the addDataResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddDataResponseMsg }
     *     
     */
    public void setAddDataResult(AddDataResponseMsg value) {
        this.addDataResult = value;
    }

    public boolean isSetAddDataResult() {
        return (this.addDataResult!= null);
    }

}
