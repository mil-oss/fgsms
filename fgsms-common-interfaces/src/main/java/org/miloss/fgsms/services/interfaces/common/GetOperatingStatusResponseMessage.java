
package org.miloss.fgsms.services.interfaces.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;


/**
 * 
 * 			since RC7
 * 			
 * 
 * <p>Java class for GetOperatingStatusResponseMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetOperatingStatusResponseMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="StatusMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartedAt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="DataSentSuccessfully" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="DataNotSentSuccessfully" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;sequence>
 *           &lt;element name="VersionInfo">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;attribute name="VersionSource" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                   &lt;attribute name="VersionData" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetOperatingStatusResponseMessage", propOrder = {
    "classification",
    "status",
    "statusMessage",
    "startedAt",
    "dataSentSuccessfully",
    "dataNotSentSuccessfully",
    "versionInfo"
})
public class GetOperatingStatusResponseMessage {

    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "Status")
    protected boolean status;
    @XmlElement(name = "StatusMessage", required = true)
    protected String statusMessage;
    @XmlElement(name = "StartedAt", required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar startedAt;
    @XmlElement(name = "DataSentSuccessfully")
    protected long dataSentSuccessfully;
    @XmlElement(name = "DataNotSentSuccessfully")
    protected long dataNotSentSuccessfully;
    @XmlElement(name = "VersionInfo", required = true)
    protected GetOperatingStatusResponseMessage.VersionInfo versionInfo;

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityWrapper }
     *     
     */
    public SecurityWrapper getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityWrapper }
     *     
     */
    public void setClassification(SecurityWrapper value) {
        this.classification = value;
    }

    /**
     * Gets the value of the status property.
     * 
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     */
    public void setStatus(boolean value) {
        this.status = value;
    }

    /**
     * Gets the value of the statusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

    /**
     * Gets the value of the startedAt property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getStartedAt() {
        return startedAt;
    }

    /**
     * Sets the value of the startedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setStartedAt(Calendar value) {
        this.startedAt = value;
    }

    /**
     * Gets the value of the dataSentSuccessfully property.
     * 
     */
    public long getDataSentSuccessfully() {
        return dataSentSuccessfully;
    }

    /**
     * Sets the value of the dataSentSuccessfully property.
     * 
     */
    public void setDataSentSuccessfully(long value) {
        this.dataSentSuccessfully = value;
    }

    /**
     * Gets the value of the dataNotSentSuccessfully property.
     * 
     */
    public long getDataNotSentSuccessfully() {
        return dataNotSentSuccessfully;
    }

    /**
     * Sets the value of the dataNotSentSuccessfully property.
     * 
     */
    public void setDataNotSentSuccessfully(long value) {
        this.dataNotSentSuccessfully = value;
    }

    /**
     * Gets the value of the versionInfo property.
     * 
     * @return
     *     possible object is
     *     {@link GetOperatingStatusResponseMessage.VersionInfo }
     *     
     */
    public GetOperatingStatusResponseMessage.VersionInfo getVersionInfo() {
        return versionInfo;
    }

    /**
     * Sets the value of the versionInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetOperatingStatusResponseMessage.VersionInfo }
     *     
     */
    public void setVersionInfo(GetOperatingStatusResponseMessage.VersionInfo value) {
        this.versionInfo = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="VersionSource" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="VersionData" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class VersionInfo {

        @XmlAttribute(name = "VersionSource")
        protected String versionSource;
        @XmlAttribute(name = "VersionData")
        protected String versionData;

        /**
         * Gets the value of the versionSource property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersionSource() {
            return versionSource;
        }

        /**
         * Sets the value of the versionSource property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersionSource(String value) {
            this.versionSource = value;
        }

        /**
         * Gets the value of the versionData property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersionData() {
            return versionData;
        }

        /**
         * Sets the value of the versionData property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersionData(String value) {
            this.versionData = value;
        }

    }

}
