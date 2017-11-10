
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
 * XML namespace prefix collection, applies to transactional services
 * 			
 * 
 * <p>Java class for ArrayOfXMLNamespacePrefixies complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfXMLNamespacePrefixies">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XMLNamespacePrefixies" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}XMLNamespacePrefixies" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfXMLNamespacePrefixies", propOrder = {
    "xmlNamespacePrefixies"
})
public class ArrayOfXMLNamespacePrefixies
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "XMLNamespacePrefixies", required = true, nillable = true)
    protected List<XMLNamespacePrefixies> xmlNamespacePrefixies;

    /**
     * Gets the value of the xmlNamespacePrefixies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlNamespacePrefixies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXMLNamespacePrefixies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLNamespacePrefixies }
     * 
     * 
     */
    public List<XMLNamespacePrefixies> getXMLNamespacePrefixies() {
        if (xmlNamespacePrefixies == null) {
            xmlNamespacePrefixies = new ArrayList<XMLNamespacePrefixies>();
        }
        return this.xmlNamespacePrefixies;
    }

    public boolean isSetXMLNamespacePrefixies() {
        return ((this.xmlNamespacePrefixies!= null)&&(!this.xmlNamespacePrefixies.isEmpty()));
    }

    public void unsetXMLNamespacePrefixies() {
        this.xmlNamespacePrefixies = null;
    }

}
