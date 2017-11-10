/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.oasis_open.docs.wsdm.muws1_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;
import javax.xml.namespace.QName;
import org.oasis_open.docs.wsdm.muws2_2.SituationType;
import org.oasis_open.docs.wsrf.rp_2.ResourcePropertyValueChangeNotificationType;
import us.gov.ic.ism.v2.ClassificationType;


/**
 * <p>Java class for ManagementEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ManagementEventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EventId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="SourceComponent" type="{http://docs.oasis-open.org/wsdm/muws1-2.xsd}ComponentType"/>
 *         &lt;element name="ReporterComponent" type="{http://docs.oasis-open.org/wsdm/muws1-2.xsd}ComponentType" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsdm/muws2-2.xsd}Situation" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsrf/rp-2}ResourcePropertyValueChangeNotification" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:us:gov:ic:ism:v2}SecurityAttributesOptionGroup"/>
 *       &lt;attribute name="ReportTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagementEventType", propOrder = {
    "eventId",
    "sourceComponent",
    "reporterComponent",
    "situation",
    "resourcePropertyValueChangeNotification"
})
@XmlRootElement
public class ManagementEventType {

    @XmlElement(name = "EventId", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String eventId;
    @XmlElement(name = "SourceComponent", required = true)
    protected ComponentType sourceComponent;
    @XmlElement(name = "ReporterComponent")
    protected ComponentType reporterComponent;
    @XmlElement(name = "Situation", namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd")
    protected SituationType situation;
    @XmlElement(name = "ResourcePropertyValueChangeNotification", namespace = "http://docs.oasis-open.org/wsrf/rp-2")
    protected List<ResourcePropertyValueChangeNotificationType> resourcePropertyValueChangeNotification;
    @XmlAttribute(name = "ReportTime")
    @XmlSchemaType(name = "dateTime")
    protected Calendar reportTime;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected ClassificationType classification;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> ownerProducer;
    @XmlAttribute(name = "SCIcontrols", namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> scIcontrols;
    @XmlAttribute(name = "SARIdentifier", namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> sarIdentifier;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> disseminationControls;
    @XmlAttribute(name = "FGIsourceOpen", namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> fgIsourceOpen;
    @XmlAttribute(name = "FGIsourceProtected", namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> fgIsourceProtected;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> releasableTo;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> nonICmarkings;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected String classifiedBy;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected String classificationReason;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected String derivedFrom;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected Calendar declassDate;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected String declassEvent;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> declassException;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected List<String> typeOfExemptedSource;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected Calendar dateOfExemptedSource;
    @XmlAttribute(namespace = "urn:us:gov:ic:ism:v2")
    protected Boolean declassManualReview;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the eventId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventId(String value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the sourceComponent property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentType }
     *     
     */
    public ComponentType getSourceComponent() {
        return sourceComponent;
    }

    /**
     * Sets the value of the sourceComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentType }
     *     
     */
    public void setSourceComponent(ComponentType value) {
        this.sourceComponent = value;
    }

    /**
     * Gets the value of the reporterComponent property.
     * 
     * @return
     *     possible object is
     *     {@link ComponentType }
     *     
     */
    public ComponentType getReporterComponent() {
        return reporterComponent;
    }

    /**
     * Sets the value of the reporterComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComponentType }
     *     
     */
    public void setReporterComponent(ComponentType value) {
        this.reporterComponent = value;
    }

    /**
     * Gets the value of the situation property.
     * 
     * @return
     *     possible object is
     *     {@link SituationType }
     *     
     */
    public SituationType getSituation() {
        return situation;
    }

    /**
     * Sets the value of the situation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SituationType }
     *     
     */
    public void setSituation(SituationType value) {
        this.situation = value;
    }

    /**
     * Gets the value of the resourcePropertyValueChangeNotification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourcePropertyValueChangeNotification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourcePropertyValueChangeNotification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourcePropertyValueChangeNotificationType }
     * 
     * 
     */
    public List<ResourcePropertyValueChangeNotificationType> getResourcePropertyValueChangeNotification() {
        if (resourcePropertyValueChangeNotification == null) {
            resourcePropertyValueChangeNotification = new ArrayList<ResourcePropertyValueChangeNotificationType>();
        }
        return this.resourcePropertyValueChangeNotification;
    }

    /**
     * Gets the value of the reportTime property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getReportTime() {
        return reportTime;
    }

    /**
     * Sets the value of the reportTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setReportTime(Calendar value) {
        this.reportTime = value;
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
     * Gets the value of the ownerProducer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ownerProducer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOwnerProducer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOwnerProducer() {
        if (ownerProducer == null) {
            ownerProducer = new ArrayList<String>();
        }
        return this.ownerProducer;
    }

    /**
     * Gets the value of the scIcontrols property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scIcontrols property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSCIcontrols().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSCIcontrols() {
        if (scIcontrols == null) {
            scIcontrols = new ArrayList<String>();
        }
        return this.scIcontrols;
    }

    /**
     * Gets the value of the sarIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sarIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSARIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSARIdentifier() {
        if (sarIdentifier == null) {
            sarIdentifier = new ArrayList<String>();
        }
        return this.sarIdentifier;
    }

    /**
     * Gets the value of the disseminationControls property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the disseminationControls property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDisseminationControls().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDisseminationControls() {
        if (disseminationControls == null) {
            disseminationControls = new ArrayList<String>();
        }
        return this.disseminationControls;
    }

    /**
     * Gets the value of the fgIsourceOpen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fgIsourceOpen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFGIsourceOpen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFGIsourceOpen() {
        if (fgIsourceOpen == null) {
            fgIsourceOpen = new ArrayList<String>();
        }
        return this.fgIsourceOpen;
    }

    /**
     * Gets the value of the fgIsourceProtected property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fgIsourceProtected property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFGIsourceProtected().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFGIsourceProtected() {
        if (fgIsourceProtected == null) {
            fgIsourceProtected = new ArrayList<String>();
        }
        return this.fgIsourceProtected;
    }

    /**
     * Gets the value of the releasableTo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the releasableTo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReleasableTo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getReleasableTo() {
        if (releasableTo == null) {
            releasableTo = new ArrayList<String>();
        }
        return this.releasableTo;
    }

    /**
     * Gets the value of the nonICmarkings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nonICmarkings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNonICmarkings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNonICmarkings() {
        if (nonICmarkings == null) {
            nonICmarkings = new ArrayList<String>();
        }
        return this.nonICmarkings;
    }

    /**
     * Gets the value of the classifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassifiedBy() {
        return classifiedBy;
    }

    /**
     * Sets the value of the classifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassifiedBy(String value) {
        this.classifiedBy = value;
    }

    /**
     * Gets the value of the classificationReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassificationReason() {
        return classificationReason;
    }

    /**
     * Sets the value of the classificationReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassificationReason(String value) {
        this.classificationReason = value;
    }

    /**
     * Gets the value of the derivedFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * Sets the value of the derivedFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDerivedFrom(String value) {
        this.derivedFrom = value;
    }

    /**
     * Gets the value of the declassDate property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getDeclassDate() {
        return declassDate;
    }

    /**
     * Sets the value of the declassDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setDeclassDate(Calendar value) {
        this.declassDate = value;
    }

    /**
     * Gets the value of the declassEvent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeclassEvent() {
        return declassEvent;
    }

    /**
     * Sets the value of the declassEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeclassEvent(String value) {
        this.declassEvent = value;
    }

    /**
     * Gets the value of the declassException property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the declassException property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeclassException().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDeclassException() {
        if (declassException == null) {
            declassException = new ArrayList<String>();
        }
        return this.declassException;
    }

    /**
     * Gets the value of the typeOfExemptedSource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the typeOfExemptedSource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypeOfExemptedSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTypeOfExemptedSource() {
        if (typeOfExemptedSource == null) {
            typeOfExemptedSource = new ArrayList<String>();
        }
        return this.typeOfExemptedSource;
    }

    /**
     * Gets the value of the dateOfExemptedSource property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getDateOfExemptedSource() {
        return dateOfExemptedSource;
    }

    /**
     * Sets the value of the dateOfExemptedSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setDateOfExemptedSource(Calendar value) {
        this.dateOfExemptedSource = value;
    }

    /**
     * Gets the value of the declassManualReview property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeclassManualReview() {
        return declassManualReview;
    }

    /**
     * Sets the value of the declassManualReview property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeclassManualReview(Boolean value) {
        this.declassManualReview = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
