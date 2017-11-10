
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *              XPathExpressionType for user identification
 *             
 * 
 * <p>Java class for XPathExpressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XPathExpressionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IsCertificate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="XPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XPathExpressionType", propOrder = {
    "isCertificate",
    "xPath"
})
public class XPathExpressionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "IsCertificate")
    protected boolean isCertificate;
    @XmlElement(name = "XPath", required = true, nillable = true)
    protected String xPath;

    /**
     * Gets the value of the isCertificate property.
     * 
     */
    public boolean isIsCertificate() {
        return isCertificate;
    }

    /**
     * Sets the value of the isCertificate property.
     * 
     */
    public void setIsCertificate(boolean value) {
        this.isCertificate = value;
    }

    public boolean isSetIsCertificate() {
        return true;
    }

    /**
     * Gets the value of the xPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXPath() {
        return xPath;
    }

    /**
     * Sets the value of the xPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXPath(String value) {
        this.xPath = value;
    }

    public boolean isSetXPath() {
        return (this.xPath!= null);
    }

}
