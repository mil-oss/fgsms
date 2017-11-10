
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.datacontract.schemas._2004._07.helloworldesmwcf2.CompositeType;


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
 *         &lt;element name="composite" type="{http://schemas.datacontract.org/2004/07/HelloWorldESMWCF2}CompositeType" minOccurs="0"/>
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
    "composite"
})
@XmlRootElement(name = "GetDataUsingDataContract")
public class GetDataUsingDataContract {

    @XmlElementRef(name = "composite", namespace = "http://tempuri.org/", type = JAXBElement.class, required = false)
    protected JAXBElement<CompositeType> composite;

    /**
     * Gets the value of the composite property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CompositeType }{@code >}
     *     
     */
    public JAXBElement<CompositeType> getComposite() {
        return composite;
    }

    /**
     * Sets the value of the composite property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CompositeType }{@code >}
     *     
     */
    public void setComposite(JAXBElement<CompositeType> value) {
        this.composite = value;
    }

}
