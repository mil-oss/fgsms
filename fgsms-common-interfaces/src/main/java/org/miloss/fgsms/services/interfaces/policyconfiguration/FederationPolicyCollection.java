
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines a policy for sharing data via federated partners and components, such as UDDI
 * 
 * <p>Java class for FederationPolicyCollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FederationPolicyCollection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FederationPolicy" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}FederationPolicy" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FederationPolicyCollection", propOrder = {
    "federationPolicy"
})
@XmlRootElement
public class FederationPolicyCollection
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "FederationPolicy")
    protected List<FederationPolicy> federationPolicy;

    /**
     * Gets the value of the federationPolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the federationPolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFederationPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FederationPolicy }
     * 
     * 
     */
    public List<FederationPolicy> getFederationPolicy() {
        if (federationPolicy == null) {
            federationPolicy = new ArrayList<FederationPolicy>();
        }
        return this.federationPolicy;
    }

    public boolean isSetFederationPolicy() {
        return ((this.federationPolicy!= null)&&(!this.federationPolicy.isEmpty()));
    }

    public void unsetFederationPolicy() {
        this.federationPolicy = null;
    }

}
