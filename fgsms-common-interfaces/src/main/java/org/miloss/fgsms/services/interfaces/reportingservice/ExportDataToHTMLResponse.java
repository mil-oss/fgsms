
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
 *         &lt;element name="ExportDataToHTMLResult" type="{urn:org:miloss:fgsms:services:interfaces:reportingService}ExportDataToHTML_ResponseMsg"/>
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
    "exportDataToHTMLResult"
})
@XmlRootElement(name = "ExportDataToHTMLResponse")
public class ExportDataToHTMLResponse {

    @XmlElement(name = "ExportDataToHTMLResult", required = true, nillable = true)
    protected ExportDataToHTMLResponseMsg exportDataToHTMLResult;

    /**
     * Gets the value of the exportDataToHTMLResult property.
     * 
     * @return
     *     possible object is
     *     {@link ExportDataToHTMLResponseMsg }
     *     
     */
    public ExportDataToHTMLResponseMsg getExportDataToHTMLResult() {
        return exportDataToHTMLResult;
    }

    /**
     * Sets the value of the exportDataToHTMLResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportDataToHTMLResponseMsg }
     *     
     */
    public void setExportDataToHTMLResult(ExportDataToHTMLResponseMsg value) {
        this.exportDataToHTMLResult = value;
    }

}
