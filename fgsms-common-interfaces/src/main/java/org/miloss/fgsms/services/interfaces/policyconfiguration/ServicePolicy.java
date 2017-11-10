
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.miloss.fgsms.services.interfaces.common.NameValuePairSet;
import org.miloss.fgsms.services.interfaces.common.PolicyType;


/**
 * 
 *                 ServicePolicy - The service policy is one of the most important elements in fgsms, it defines how agents react to observed traffic keyed on URLs
 *             
 * 
 * <p>Java class for ServicePolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServicePolicy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BucketCategory" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="POC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExternalURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MachineName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DomainName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ParentObject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DataTTL" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="PolicyRefreshRate" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="FederationPolicyCollection" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}FederationPolicyCollection"/>
 *         &lt;element name="PolicyType" type="{urn:org:miloss:fgsms:services:interfaces:common}policyType"/>
 *         &lt;element name="AgentsEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Location" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GeoTag"/>
 *         &lt;element name="ServiceLevelAggrements" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ArrayOfSLA"/>
 *         &lt;element name="URL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="displayName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="settings" type="{urn:org:miloss:fgsms:services:interfaces:common}NameValuePairSet" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServicePolicy", propOrder = {
    "bucketCategory",
    "description",
    "poc",
    "externalURL",
    "machineName",
    "domainName",
    "parentObject",
    "dataTTL",
    "policyRefreshRate",
    "federationPolicyCollection",
    "policyType",
    "agentsEnabled",
    "location",
    "serviceLevelAggrements",
    "url",
    "displayName",
    "settings"
})
@XmlRootElement
@XmlSeeAlso({TransactionalWebServicePolicy.class, StatusServicePolicy.class, StatisticalServicePolicy.class,
      ProcessPolicy.class, MachinePolicy.class
})
public class ServicePolicy
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "BucketCategory", required = false, nillable = true)
    protected String bucketCategory;
    @XmlElement(name = "Description", required = false, nillable = true)
    protected String description;
    @XmlElement(name = "POC", required = true, nillable = true)
    protected String poc;
    @XmlElement(name = "ExternalURL", required = false, nillable = true)
    protected String externalURL;
    @XmlElement(name = "MachineName", required = true, defaultValue = "UNSPECIFIED", nillable = true)
    protected String machineName;
    @XmlElement(name = "DomainName", required = true, defaultValue = "UNSPECIFIED", nillable = true)
    protected String domainName;
    @XmlElement(name = "ParentObject", required = false, nillable = true)
    protected String parentObject;
    @XmlElement(name = "DataTTL" , required = false,nillable = true)
    protected Duration dataTTL;
    @XmlElement(name = "PolicyRefreshRate", required = false,nillable = true)
    protected Duration policyRefreshRate;
    @XmlElement(name = "FederationPolicyCollection", required = false, nillable = true)
    protected FederationPolicyCollection federationPolicyCollection;
    @XmlElement(name = "PolicyType", required = true, defaultValue = "Transactional")
    protected PolicyType policyType;
    @XmlElement(name = "AgentsEnabled")
    protected boolean agentsEnabled;
    @XmlElement(name = "Location", required = false, nillable = true)
    protected GeoTag location;
    @XmlElement(name = "ServiceLevelAggrements", required = false, nillable = true)
    protected ArrayOfSLA serviceLevelAggrements;
    @XmlElement(name = "URL", required = true)
    protected String url;
    @XmlElement(required = true, nillable = true)
    protected String displayName;
    protected NameValuePairSet settings;

    /**
     * Gets the value of the bucketCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBucketCategory() {
        return bucketCategory;
    }

    /**
     * Sets the value of the bucketCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBucketCategory(String value) {
        this.bucketCategory = value;
    }

    public boolean isSetBucketCategory() {
        return (this.bucketCategory!= null);
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public boolean isSetDescription() {
        return (this.description!= null);
    }

    /**
     * Gets the value of the poc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOC() {
        return poc;
    }

    /**
     * Sets the value of the poc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOC(String value) {
        this.poc = value;
    }

    public boolean isSetPOC() {
        return (this.poc!= null);
    }

    /**
     * Gets the value of the externalURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalURL() {
        return externalURL;
    }

    /**
     * Sets the value of the externalURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalURL(String value) {
        this.externalURL = value;
    }

    public boolean isSetExternalURL() {
        return (this.externalURL!= null);
    }

    /**
     * Gets the value of the machineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * Sets the value of the machineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineName(String value) {
        this.machineName = value;
    }

    public boolean isSetMachineName() {
        return (this.machineName!= null);
    }

    /**
     * Gets the value of the domainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    public boolean isSetDomainName() {
        return (this.domainName!= null);
    }

    /**
     * Gets the value of the parentObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentObject() {
        return parentObject;
    }

    /**
     * Sets the value of the parentObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentObject(String value) {
        this.parentObject = value;
    }

    public boolean isSetParentObject() {
        return (this.parentObject!= null);
    }

    /**
     * Gets the value of the dataTTL property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getDataTTL() {
        return dataTTL;
    }

    /**
     * Sets the value of the dataTTL property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setDataTTL(Duration value) {
        this.dataTTL = value;
    }

    public boolean isSetDataTTL() {
        return (this.dataTTL!= null);
    }

    /**
     * Gets the value of the policyRefreshRate property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getPolicyRefreshRate() {
        return policyRefreshRate;
    }

    /**
     * Sets the value of the policyRefreshRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setPolicyRefreshRate(Duration value) {
        this.policyRefreshRate = value;
    }

    public boolean isSetPolicyRefreshRate() {
        return (this.policyRefreshRate!= null);
    }

    /**
     * Gets the value of the federationPolicyCollection property.
     * 
     * @return
     *     possible object is
     *     {@link FederationPolicyCollection }
     *     
     */
    public FederationPolicyCollection getFederationPolicyCollection() {
        return federationPolicyCollection;
    }

    /**
     * Sets the value of the federationPolicyCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link FederationPolicyCollection }
     *     
     */
    public void setFederationPolicyCollection(FederationPolicyCollection value) {
        this.federationPolicyCollection = value;
    }

    public boolean isSetFederationPolicyCollection() {
        return (this.federationPolicyCollection!= null);
    }

    /**
     * Gets the value of the policyType property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyType }
     *     
     */
    public PolicyType getPolicyType() {
        return policyType;
    }

    /**
     * Sets the value of the policyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyType }
     *     
     */
    public void setPolicyType(PolicyType value) {
        this.policyType = value;
    }

    public boolean isSetPolicyType() {
        return (this.policyType!= null);
    }

    /**
     * Gets the value of the agentsEnabled property.
     * 
     */
    public boolean isAgentsEnabled() {
        return agentsEnabled;
    }

    /**
     * Sets the value of the agentsEnabled property.
     * 
     */
    public void setAgentsEnabled(boolean value) {
        this.agentsEnabled = value;
    }

    public boolean isSetAgentsEnabled() {
        return true;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link GeoTag }
     *     
     */
    public GeoTag getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeoTag }
     *     
     */
    public void setLocation(GeoTag value) {
        this.location = value;
    }

    public boolean isSetLocation() {
        return (this.location!= null);
    }

    /**
     * Gets the value of the serviceLevelAggrements property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSLA }
     *     
     */
    public ArrayOfSLA getServiceLevelAggrements() {
        return serviceLevelAggrements;
    }

    /**
     * Sets the value of the serviceLevelAggrements property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSLA }
     *     
     */
    public void setServiceLevelAggrements(ArrayOfSLA value) {
        this.serviceLevelAggrements = value;
    }

    public boolean isSetServiceLevelAggrements() {
        return (this.serviceLevelAggrements!= null);
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setURL(String value) {
        this.url = value;
    }

    public boolean isSetURL() {
        return (this.url!= null);
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    public boolean isSetDisplayName() {
        return (this.displayName!= null);
    }

    /**
     * Gets the value of the settings property.
     * 
     * @return
     *     possible object is
     *     {@link NameValuePairSet }
     *     
     */
    public NameValuePairSet getSettings() {
        return settings;
    }

    /**
     * Sets the value of the settings property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameValuePairSet }
     *     
     */
    public void setSettings(NameValuePairSet value) {
        this.settings = value;
    }

    public boolean isSetSettings() {
        return (this.settings!= null);
    }

}
