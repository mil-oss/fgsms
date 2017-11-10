
package org.miloss.fgsms.services.interfaces.reportingservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * collection of report types
 * 
 * <p>Java class for ArrayOfReportTypeContainer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfReportTypeContainer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReportTypeContainer" type="{urn:org:miloss:fgsms:services:interfaces:reportingService}ReportTypeContainer" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfReportTypeContainer", propOrder = {
    "reportTypeContainer"
})
public class ArrayOfReportTypeContainer
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ReportTypeContainer", nillable = true)
    protected List<ReportTypeContainer> reportTypeContainer;

    /**
     * Gets the value of the reportTypeContainer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reportTypeContainer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReportTypeContainer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReportTypeContainer }
     * 
     * 
     */
    public List<ReportTypeContainer> getReportTypeContainer() {
        if (reportTypeContainer == null) {
            reportTypeContainer = new ArrayList<ReportTypeContainer>();
        }
        return this.reportTypeContainer;
    }

}
