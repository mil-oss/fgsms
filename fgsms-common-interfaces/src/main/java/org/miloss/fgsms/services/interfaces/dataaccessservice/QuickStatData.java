
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import java.util.Calendar;


/**
 * statistics data
 * 
 * <p>Java class for QuickStatData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuickStatData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TimeInMinutes" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="SuccessCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="FailureCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="MaximumResponseSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="MaximumRequestSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="MaximumResponseTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="SLAViolationCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AverageResponseTime" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AvailabilityPercentage" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="MTBF" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="UpdatedAt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="AverageCPUUsage" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="AverageMemoryUsage" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AverageThreadCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AverageOpenFileCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AverageChannelCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="LargestQueueDepth" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AverageMessagesIn" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AverageMessagesOut" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="AverageMessagesDropped" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuickStatData", propOrder = {
    "timeInMinutes",
    "successCount",
    "failureCount",
    "maximumResponseSize",
    "maximumRequestSize",
    "maximumResponseTime",
    "slaViolationCount",
    "averageResponseTime",
    "availabilityPercentage",
    "mtbf",
    "updatedAt",
    "averageCPUUsage",
    "averageMemoryUsage",
    "averageThreadCount",
    "averageOpenFileCount",
    "averageChannelCount",
    "largestQueueDepth",
    "averageMessagesIn",
    "averageMessagesOut",
    "averageMessagesDropped"
})
public class QuickStatData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "TimeInMinutes", required = true)
    protected BigInteger timeInMinutes;
    @XmlElement(name = "SuccessCount")
    protected long successCount;
    @XmlElement(name = "FailureCount")
    protected long failureCount;
    @XmlElement(name = "MaximumResponseSize")
    protected long maximumResponseSize;
    @XmlElement(name = "MaximumRequestSize")
    protected long maximumRequestSize;
    @XmlElement(name = "MaximumResponseTime")
    protected long maximumResponseTime;
    @XmlElement(name = "SLAViolationCount")
    protected long slaViolationCount;
    @XmlElement(name = "AverageResponseTime")
    protected long averageResponseTime;
    @XmlElement(name = "AvailabilityPercentage")
    protected double availabilityPercentage;
    @XmlElement(name = "MTBF", required = true)
    protected Duration mtbf;
    @XmlElement(name = "UpdatedAt", required = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar updatedAt;
    @XmlElement(name = "AverageCPUUsage", required = true, type = Double.class, nillable = true)
    protected Double averageCPUUsage;
    @XmlElement(name = "AverageMemoryUsage", required = true, type = Long.class, nillable = true)
    protected Long averageMemoryUsage;
    @XmlElement(name = "AverageThreadCount", required = true, type = Long.class, nillable = true)
    protected Long averageThreadCount;
    @XmlElement(name = "AverageOpenFileCount", required = true, type = Long.class, nillable = true)
    protected Long averageOpenFileCount;
    @XmlElement(name = "AverageChannelCount", required = true, type = Long.class, nillable = true)
    protected Long averageChannelCount;
    @XmlElement(name = "LargestQueueDepth", required = true, type = Long.class, nillable = true)
    protected Long largestQueueDepth;
    @XmlElement(name = "AverageMessagesIn", required = true, type = Long.class, nillable = true)
    protected Long averageMessagesIn;
    @XmlElement(name = "AverageMessagesOut", required = true, type = Long.class, nillable = true)
    protected Long averageMessagesOut;
    @XmlElement(name = "AverageMessagesDropped", required = true, type = Long.class, nillable = true)
    protected Long averageMessagesDropped;

    /**
     * Gets the value of the timeInMinutes property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTimeInMinutes() {
        return timeInMinutes;
    }

    /**
     * Sets the value of the timeInMinutes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTimeInMinutes(BigInteger value) {
        this.timeInMinutes = value;
    }

    public boolean isSetTimeInMinutes() {
        return (this.timeInMinutes!= null);
    }

    /**
     * Gets the value of the successCount property.
     * 
     */
    public long getSuccessCount() {
        return successCount;
    }

    /**
     * Sets the value of the successCount property.
     * 
     */
    public void setSuccessCount(long value) {
        this.successCount = value;
    }

    public boolean isSetSuccessCount() {
        return true;
    }

    /**
     * Gets the value of the failureCount property.
     * 
     */
    public long getFailureCount() {
        return failureCount;
    }

    /**
     * Sets the value of the failureCount property.
     * 
     */
    public void setFailureCount(long value) {
        this.failureCount = value;
    }

    public boolean isSetFailureCount() {
        return true;
    }

    /**
     * Gets the value of the maximumResponseSize property.
     * 
     */
    public long getMaximumResponseSize() {
        return maximumResponseSize;
    }

    /**
     * Sets the value of the maximumResponseSize property.
     * 
     */
    public void setMaximumResponseSize(long value) {
        this.maximumResponseSize = value;
    }

    public boolean isSetMaximumResponseSize() {
        return true;
    }

    /**
     * Gets the value of the maximumRequestSize property.
     * 
     */
    public long getMaximumRequestSize() {
        return maximumRequestSize;
    }

    /**
     * Sets the value of the maximumRequestSize property.
     * 
     */
    public void setMaximumRequestSize(long value) {
        this.maximumRequestSize = value;
    }

    public boolean isSetMaximumRequestSize() {
        return true;
    }

    /**
     * Gets the value of the maximumResponseTime property.
     * 
     */
    public long getMaximumResponseTime() {
        return maximumResponseTime;
    }

    /**
     * Sets the value of the maximumResponseTime property.
     * 
     */
    public void setMaximumResponseTime(long value) {
        this.maximumResponseTime = value;
    }

    public boolean isSetMaximumResponseTime() {
        return true;
    }

    /**
     * Gets the value of the slaViolationCount property.
     * 
     */
    public long getSLAViolationCount() {
        return slaViolationCount;
    }

    /**
     * Sets the value of the slaViolationCount property.
     * 
     */
    public void setSLAViolationCount(long value) {
        this.slaViolationCount = value;
    }

    public boolean isSetSLAViolationCount() {
        return true;
    }

    /**
     * Gets the value of the averageResponseTime property.
     * 
     */
    public long getAverageResponseTime() {
        return averageResponseTime;
    }

    /**
     * Sets the value of the averageResponseTime property.
     * 
     */
    public void setAverageResponseTime(long value) {
        this.averageResponseTime = value;
    }

    public boolean isSetAverageResponseTime() {
        return true;
    }

    /**
     * Gets the value of the availabilityPercentage property.
     * 
     */
    public double getAvailabilityPercentage() {
        return availabilityPercentage;
    }

    /**
     * Sets the value of the availabilityPercentage property.
     * 
     */
    public void setAvailabilityPercentage(double value) {
        this.availabilityPercentage = value;
    }

    public boolean isSetAvailabilityPercentage() {
        return true;
    }

    /**
     * Gets the value of the mtbf property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getMTBF() {
        return mtbf;
    }

    /**
     * Sets the value of the mtbf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setMTBF(Duration value) {
        this.mtbf = value;
    }

    public boolean isSetMTBF() {
        return (this.mtbf!= null);
    }

    /**
     * Gets the value of the updatedAt property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the value of the updatedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setUpdatedAt(Calendar value) {
        this.updatedAt = value;
    }

    public boolean isSetUpdatedAt() {
        return (this.updatedAt!= null);
    }

    /**
     * Gets the value of the averageCPUUsage property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAverageCPUUsage() {
        return averageCPUUsage;
    }

    /**
     * Sets the value of the averageCPUUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAverageCPUUsage(Double value) {
        this.averageCPUUsage = value;
    }

    public boolean isSetAverageCPUUsage() {
        return (this.averageCPUUsage!= null);
    }

    /**
     * Gets the value of the averageMemoryUsage property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAverageMemoryUsage() {
        return averageMemoryUsage;
    }

    /**
     * Sets the value of the averageMemoryUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAverageMemoryUsage(Long value) {
        this.averageMemoryUsage = value;
    }

    public boolean isSetAverageMemoryUsage() {
        return (this.averageMemoryUsage!= null);
    }

    /**
     * Gets the value of the averageThreadCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAverageThreadCount() {
        return averageThreadCount;
    }

    /**
     * Sets the value of the averageThreadCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAverageThreadCount(Long value) {
        this.averageThreadCount = value;
    }

    public boolean isSetAverageThreadCount() {
        return (this.averageThreadCount!= null);
    }

    /**
     * Gets the value of the averageOpenFileCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAverageOpenFileCount() {
        return averageOpenFileCount;
    }

    /**
     * Sets the value of the averageOpenFileCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAverageOpenFileCount(Long value) {
        this.averageOpenFileCount = value;
    }

    public boolean isSetAverageOpenFileCount() {
        return (this.averageOpenFileCount!= null);
    }

    /**
     * Gets the value of the averageChannelCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAverageChannelCount() {
        return averageChannelCount;
    }

    /**
     * Sets the value of the averageChannelCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAverageChannelCount(Long value) {
        this.averageChannelCount = value;
    }

    public boolean isSetAverageChannelCount() {
        return (this.averageChannelCount!= null);
    }

    /**
     * Gets the value of the largestQueueDepth property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLargestQueueDepth() {
        return largestQueueDepth;
    }

    /**
     * Sets the value of the largestQueueDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLargestQueueDepth(Long value) {
        this.largestQueueDepth = value;
    }

    public boolean isSetLargestQueueDepth() {
        return (this.largestQueueDepth!= null);
    }

    /**
     * Gets the value of the averageMessagesIn property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAverageMessagesIn() {
        return averageMessagesIn;
    }

    /**
     * Sets the value of the averageMessagesIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAverageMessagesIn(Long value) {
        this.averageMessagesIn = value;
    }

    public boolean isSetAverageMessagesIn() {
        return (this.averageMessagesIn!= null);
    }

    /**
     * Gets the value of the averageMessagesOut property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAverageMessagesOut() {
        return averageMessagesOut;
    }

    /**
     * Sets the value of the averageMessagesOut property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAverageMessagesOut(Long value) {
        this.averageMessagesOut = value;
    }

    public boolean isSetAverageMessagesOut() {
        return (this.averageMessagesOut!= null);
    }

    /**
     * Gets the value of the averageMessagesDropped property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getAverageMessagesDropped() {
        return averageMessagesDropped;
    }

    /**
     * Sets the value of the averageMessagesDropped property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setAverageMessagesDropped(Long value) {
        this.averageMessagesDropped = value;
    }

    public boolean isSetAverageMessagesDropped() {
        return (this.averageMessagesDropped!= null);
    }

}
