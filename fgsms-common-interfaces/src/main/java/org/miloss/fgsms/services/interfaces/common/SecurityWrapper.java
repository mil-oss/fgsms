
package org.miloss.fgsms.services.interfaces.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import us.gov.ic.ism.v2.ClassificationType;


/**
 * 
 * 				provides a simple wrapper that adds caveat information to classification levels
 * 			
 * 
 * <p>Java class for SecurityWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SecurityWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:us:gov:ic:ism:v2}ClassificationType"/>
 *         &lt;element name="caveats" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecurityWrapper", propOrder = {
    "classification",
    "caveats"
})
public class SecurityWrapper
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, defaultValue = "U")
    protected ClassificationType classification;
    @XmlElement(required = true, defaultValue = "")
    protected String caveats;

        public SecurityWrapper(ClassificationType classificationType, String cavaet) {
                this.classification=classificationType;
                this.caveats = cavaet;
        }
        
         public SecurityWrapper() {
                this.classification=ClassificationType.U;
                this.caveats="";
        }

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link ClassificationType }
     *     
     */
    public ClassificationType getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassificationType }
     *     
     */
    public void setClassification(ClassificationType value) {
        this.classification = value;
    }

    /**
     * Gets the value of the caveats property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaveats() {
        return caveats;
    }

    /**
     * Sets the value of the caveats property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaveats(String value) {
        this.caveats = value;
    }

}
