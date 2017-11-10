
package org.miloss.fgsms.services.interfaces.reportingservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReportType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ReportType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AverageResponseTimeByService"/>
 *     &lt;enumeration value="AverageResponseTimeByServiceByMethod"/>
 *     &lt;enumeration value="AverageMessageSizeByService"/>
 *     &lt;enumeration value="AverageMessageSizeByServiceByMethod"/>
 *     &lt;enumeration value="ThroughputByService"/>
 *     &lt;enumeration value="ThroughputByServiceByMethod"/>
 *     &lt;enumeration value="TotalMessageSizesByService"/>
 *     &lt;enumeration value="TotalMessageSizesByServiceByMethod"/>
 *     &lt;enumeration value="ThroughputByHostingServer"/>
 *     &lt;enumeration value="SuccessFailureCountByService"/>
 *     &lt;enumeration value="SuccessFailureCountByServiceByMethod"/>
 *     &lt;enumeration value="SuccessFailureCountByHostingServer"/>
 *     &lt;enumeration value="InvocationsByHostingServer"/>
 *     &lt;enumeration value="ResponseTimeOverTime"/>
 *     &lt;enumeration value="InvocationsByDataCollector"/>
 *     &lt;enumeration value="InvocationsByService"/>
 *     &lt;enumeration value="InvocationsByServiceByMethod"/>
 *     &lt;enumeration value="InvocationsByConsumer"/>
 *     &lt;enumeration value="InvocationsByConsumerByService"/>
 *     &lt;enumeration value="InvocationsByConsumerByServiceByMethod"/>
 *     &lt;enumeration value="MTBFByService"/>
 *     &lt;enumeration value="AvailabilityByService"/>
 *     &lt;enumeration value="MemoryUsageByTimeService"/>
 *     &lt;enumeration value="DiskIOUsageByTimeService"/>
 *     &lt;enumeration value="DiskFreeSpaceUsageByTimeService"/>
 *     &lt;enumeration value="OpenFilesByTimeService"/>
 *     &lt;enumeration value="CPUUsageByTimeService"/>
 *     &lt;enumeration value="NetworkUsageByTimeService"/>
 *     &lt;enumeration value="ThreadCountByTimeService"/>
 *     &lt;enumeration value="QueueTopicCountByService"/>
 *     &lt;enumeration value="ConsumersByQueueTopic"/>
 *     &lt;enumeration value="QueueDepthByQueueTopic"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ReportType")
@XmlEnum
public enum ReportType {

    @XmlEnumValue("AverageResponseTimeByService")
    AVERAGE_RESPONSE_TIME_BY_SERVICE("AverageResponseTimeByService"),
    @XmlEnumValue("AverageResponseTimeByServiceByMethod")
    AVERAGE_RESPONSE_TIME_BY_SERVICE_BY_METHOD("AverageResponseTimeByServiceByMethod"),
    @XmlEnumValue("AverageMessageSizeByService")
    AVERAGE_MESSAGE_SIZE_BY_SERVICE("AverageMessageSizeByService"),
    @XmlEnumValue("AverageMessageSizeByServiceByMethod")
    AVERAGE_MESSAGE_SIZE_BY_SERVICE_BY_METHOD("AverageMessageSizeByServiceByMethod"),
    @XmlEnumValue("ThroughputByService")
    THROUGHPUT_BY_SERVICE("ThroughputByService"),
    @XmlEnumValue("ThroughputByServiceByMethod")
    THROUGHPUT_BY_SERVICE_BY_METHOD("ThroughputByServiceByMethod"),
    @XmlEnumValue("TotalMessageSizesByService")
    TOTAL_MESSAGE_SIZES_BY_SERVICE("TotalMessageSizesByService"),
    @XmlEnumValue("TotalMessageSizesByServiceByMethod")
    TOTAL_MESSAGE_SIZES_BY_SERVICE_BY_METHOD("TotalMessageSizesByServiceByMethod"),
    @XmlEnumValue("ThroughputByHostingServer")
    THROUGHPUT_BY_HOSTING_SERVER("ThroughputByHostingServer"),
    @XmlEnumValue("SuccessFailureCountByService")
    SUCCESS_FAILURE_COUNT_BY_SERVICE("SuccessFailureCountByService"),
    @XmlEnumValue("SuccessFailureCountByServiceByMethod")
    SUCCESS_FAILURE_COUNT_BY_SERVICE_BY_METHOD("SuccessFailureCountByServiceByMethod"),
    @XmlEnumValue("SuccessFailureCountByHostingServer")
    SUCCESS_FAILURE_COUNT_BY_HOSTING_SERVER("SuccessFailureCountByHostingServer"),
    @XmlEnumValue("InvocationsByHostingServer")
    INVOCATIONS_BY_HOSTING_SERVER("InvocationsByHostingServer"),
    @XmlEnumValue("ResponseTimeOverTime")
    RESPONSE_TIME_OVER_TIME("ResponseTimeOverTime"),
    @XmlEnumValue("InvocationsByDataCollector")
    INVOCATIONS_BY_DATA_COLLECTOR("InvocationsByDataCollector"),
    @XmlEnumValue("InvocationsByService")
    INVOCATIONS_BY_SERVICE("InvocationsByService"),
    @XmlEnumValue("InvocationsByServiceByMethod")
    INVOCATIONS_BY_SERVICE_BY_METHOD("InvocationsByServiceByMethod"),
    @XmlEnumValue("InvocationsByConsumer")
    INVOCATIONS_BY_CONSUMER("InvocationsByConsumer"),
    @XmlEnumValue("InvocationsByConsumerByService")
    INVOCATIONS_BY_CONSUMER_BY_SERVICE("InvocationsByConsumerByService"),
    @XmlEnumValue("InvocationsByConsumerByServiceByMethod")
    INVOCATIONS_BY_CONSUMER_BY_SERVICE_BY_METHOD("InvocationsByConsumerByServiceByMethod"),
    @XmlEnumValue("MTBFByService")
    MTBF_BY_SERVICE("MTBFByService"),
    @XmlEnumValue("AvailabilityByService")
    AVAILABILITY_BY_SERVICE("AvailabilityByService"),
    @XmlEnumValue("MemoryUsageByTimeService")
    MEMORY_USAGE_BY_TIME_SERVICE("MemoryUsageByTimeService"),
    @XmlEnumValue("DiskIOUsageByTimeService")
    DISK_IO_USAGE_BY_TIME_SERVICE("DiskIOUsageByTimeService"),
    @XmlEnumValue("DiskFreeSpaceUsageByTimeService")
    DISK_FREE_SPACE_USAGE_BY_TIME_SERVICE("DiskFreeSpaceUsageByTimeService"),
    @XmlEnumValue("OpenFilesByTimeService")
    OPEN_FILES_BY_TIME_SERVICE("OpenFilesByTimeService"),
    @XmlEnumValue("CPUUsageByTimeService")
    CPU_USAGE_BY_TIME_SERVICE("CPUUsageByTimeService"),
    @XmlEnumValue("NetworkUsageByTimeService")
    NETWORK_USAGE_BY_TIME_SERVICE("NetworkUsageByTimeService"),
    @XmlEnumValue("ThreadCountByTimeService")
    THREAD_COUNT_BY_TIME_SERVICE("ThreadCountByTimeService"),
    @XmlEnumValue("QueueTopicCountByService")
    QUEUE_TOPIC_COUNT_BY_SERVICE("QueueTopicCountByService"),
    @XmlEnumValue("ConsumersByQueueTopic")
    CONSUMERS_BY_QUEUE_TOPIC("ConsumersByQueueTopic"),
    @XmlEnumValue("QueueDepthByQueueTopic")
    QUEUE_DEPTH_BY_QUEUE_TOPIC("QueueDepthByQueueTopic");
    private final String value;

    ReportType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ReportType fromValue(String v) {
        for (ReportType c: ReportType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
