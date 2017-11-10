
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="GetAllOperationalStatusLogResult" type="{urn:org:miloss:fgsms:services:interfaces:dataAccessService}GetAllOperationalStatusLogResponseMsg" maxOccurs="unbounded"/>
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
    "getAllOperationalStatusLogResult"
})
@XmlRootElement(name = "GetAllOperationalStatusLogResponse")
public class GetAllOperationalStatusLogResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAllOperationalStatusLogResult", required = true, nillable = true)
    protected List<GetAllOperationalStatusLogResponseMsg> getAllOperationalStatusLogResult;

    /**
     * Gets the value of the getAllOperationalStatusLogResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getAllOperationalStatusLogResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetAllOperationalStatusLogResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GetAllOperationalStatusLogResponseMsg }
     * 
     * 
     */
    public List<GetAllOperationalStatusLogResponseMsg> getGetAllOperationalStatusLogResult() {
        if (getAllOperationalStatusLogResult == null) {
            getAllOperationalStatusLogResult = new ArrayList<GetAllOperationalStatusLogResponseMsg>();
        }
        return this.getAllOperationalStatusLogResult;
    }

    public boolean isSetGetAllOperationalStatusLogResult() {
        return ((this.getAllOperationalStatusLogResult!= null)&&(!this.getAllOperationalStatusLogResult.isEmpty()));
    }

    public void unsetGetAllOperationalStatusLogResult() {
        this.getAllOperationalStatusLogResult = null;
    }

}
