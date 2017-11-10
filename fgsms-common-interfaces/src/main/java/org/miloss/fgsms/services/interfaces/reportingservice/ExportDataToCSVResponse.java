
package org.miloss.fgsms.services.interfaces.reportingservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExportDataToCSVResult" type="{urn:org:miloss:fgsms:services:interfaces:reportingService}ExportDataToCSV_ResponseMsg"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "exportDataToCSVResult"
})
@XmlRootElement(name = "ExportDataToCSVResponse")
public class ExportDataToCSVResponse {

    @XmlElement(name = "ExportDataToCSVResult", required = true, nillable = true)
    protected ExportDataToCSVResponseMsg exportDataToCSVResult;

    /**
     * Gets the value of the exportDataToCSVResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExportDataToCSVResponseMsg }
     *     
     */
    public ExportDataToCSVResponseMsg getExportDataToCSVResult() {
        return exportDataToCSVResult;
    }

    /**
     * Sets the value of the exportDataToCSVResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDataToCSVResponseMsg }
     *     
     */
    public void setExportDataToCSVResult(ExportDataToCSVResponseMsg value) {
        this.exportDataToCSVResult = value;
    }

}
