
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Class that defines how a user identity is obtained from an XML web service message
 *             
 * 
 * <p>Java class for UserIdentity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserIdentity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HttpHeaderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Namespaces" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ArrayOfXMLNamespacePrefixies"/>
 *         &lt;element name="UseHttpCredential" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="UseHttpHeader" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="XPaths" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ArrayOfXPathExpressionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserIdentity", propOrder = {
    "httpHeaderName",
    "namespaces",
    "useHttpCredential",
    "useHttpHeader",
    "xPaths"
})
public class UserIdentity
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "HttpHeaderName", required = true, nillable = true)
    protected String httpHeaderName;
    @XmlElement(name = "Namespaces", required = true, nillable = true)
    protected ArrayOfXMLNamespacePrefixies namespaces;
    @XmlElement(name = "UseHttpCredential")
    protected Boolean useHttpCredential;
    @XmlElement(name = "UseHttpHeader")
    protected Boolean useHttpHeader;
    @XmlElement(name = "XPaths", required = true, nillable = true)
    protected ArrayOfXPathExpressionType xPaths;

    /**
     * Gets the value of the httpHeaderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHttpHeaderName() {
        return httpHeaderName;
    }

    /**
     * Sets the value of the httpHeaderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHttpHeaderName(String value) {
        this.httpHeaderName = value;
    }

    public boolean isSetHttpHeaderName() {
        return (this.httpHeaderName!= null);
    }

    /**
     * Gets the value of the namespaces property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfXMLNamespacePrefixies }
     *     
     */
    public ArrayOfXMLNamespacePrefixies getNamespaces() {
        return namespaces;
    }

    /**
     * Sets the value of the namespaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfXMLNamespacePrefixies }
     *     
     */
    public void setNamespaces(ArrayOfXMLNamespacePrefixies value) {
        this.namespaces = value;
    }

    public boolean isSetNamespaces() {
        return (this.namespaces!= null);
    }

    /**
     * Gets the value of the useHttpCredential property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseHttpCredential() {
        return useHttpCredential;
    }

    /**
     * Sets the value of the useHttpCredential property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseHttpCredential(Boolean value) {
        this.useHttpCredential = value;
    }

    public boolean isSetUseHttpCredential() {
        return (this.useHttpCredential!= null);
    }

    /**
     * Gets the value of the useHttpHeader property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseHttpHeader() {
        return useHttpHeader;
    }

    /**
     * Sets the value of the useHttpHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseHttpHeader(Boolean value) {
        this.useHttpHeader = value;
    }

    public boolean isSetUseHttpHeader() {
        return (this.useHttpHeader!= null);
    }

    /**
     * Gets the value of the xPaths property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfXPathExpressionType }
     *     
     */
    public ArrayOfXPathExpressionType getXPaths() {
        return xPaths;
    }

    /**
     * Sets the value of the xPaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfXPathExpressionType }
     *     
     */
    public void setXPaths(ArrayOfXPathExpressionType value) {
        this.xPaths = value;
    }

    public boolean isSetXPaths() {
        return (this.xPaths!= null);
    }

}
