
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for oneTimeSchedule complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="oneTimeSchedule">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:automatedReportingService}abstractSchedule">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "oneTimeSchedule")
public class OneTimeSchedule
    extends AbstractSchedule
    implements Serializable
{

    private final static long serialVersionUID = 1L;

}
