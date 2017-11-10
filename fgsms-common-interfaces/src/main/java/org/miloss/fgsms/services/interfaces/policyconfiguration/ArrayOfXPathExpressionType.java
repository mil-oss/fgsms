
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * xpath 
 * 			
 * 
 * <p>Java class for ArrayOfXPathExpressionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfXPathExpressionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XPathExpressionType" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}XPathExpressionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfXPathExpressionType", propOrder = {
    "xPathExpressionType"
})
public class ArrayOfXPathExpressionType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "XPathExpressionType", required = true, nillable = true)
    protected List<XPathExpressionType> xPathExpressionType;

    /**
     * Gets the value of the xPathExpressionType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xPathExpressionType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXPathExpressionType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XPathExpressionType }
     * 
     * 
     */
    public List<XPathExpressionType> getXPathExpressionType() {
        if (xPathExpressionType == null) {
            xPathExpressionType = new ArrayList<XPathExpressionType>();
        }
        return this.xPathExpressionType;
    }

    public boolean isSetXPathExpressionType() {
        return ((this.xPathExpressionType!= null)&&(!this.xPathExpressionType.isEmpty()));
    }

    public void unsetXPathExpressionType() {
        this.xPathExpressionType = null;
    }

}
