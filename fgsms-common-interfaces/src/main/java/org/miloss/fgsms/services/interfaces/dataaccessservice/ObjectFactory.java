
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.miloss.fgsms.services.interfaces.dataaccessservice package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetMessageLogsRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetMessageLogsRequestMsg");
    private final static QName _GetDataCollectorListResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetDataCollectorListResponseMsg");
    private final static QName _GetMonitoredServiceListRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetMonitoredServiceListRequestMsg");
    private final static QName _ArrayOfTransactionLog_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "ArrayOfTransactionLog");
    private final static QName _UUID_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "UUID");
    private final static QName _ServiceType_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "ServiceType");
    private final static QName _PurgePerformanceDataRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "PurgePerformanceDataRequestMsg");
    private final static QName _ArrayOfServiceType_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "ArrayOfServiceType");
    private final static QName _GetServiceHostListResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetServiceHostListResponseMsg");
    private final static QName _GetMessageLogsResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetMessageLogsResponseMsg");
    private final static QName _HostInstanceStats_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "HostInstanceStats");
    private final static QName _GetMonitoredServiceListResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetMonitoredServiceListResponseMsg");
    private final static QName _ArrayOfHostInstanceStats_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "ArrayOfHostInstanceStats");
    private final static QName _GetPerformanceAverageStatsRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetPerformanceAverageStatsRequestMsg");
    private final static QName _GetMessageTransactionLogRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetMessageTransactionLogRequestMsg");
    private final static QName _GetServiceHostListRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetServiceHostListRequestMsg");
    private final static QName _GetPerformanceAverageHostStatsRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetPerformanceAverageHostStatsRequestMsg");
    private final static QName _TransactionLog_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "TransactionLog");
    private final static QName _PurgePerformanceDataResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "PurgePerformanceDataResponseMsg");
    private final static QName _GetDataCollectorListRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetDataCollectorListRequestMsg");
    private final static QName _GetMessageTransactionLogResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetMessageTransactionLogResponseMsg");
    private final static QName _GetPerformanceAverageStatsResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "GetPerformanceAverageStatsResponseMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.miloss.fgsms.services.interfaces.dataaccessservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAllMostRecentMachineAndProcessDataResponse }
     * 
     */
    public GetAllMostRecentMachineAndProcessDataResponse createGetAllMostRecentMachineAndProcessDataResponse() {
        return new GetAllMostRecentMachineAndProcessDataResponse();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLogDetailsResponse }
     * 
     */
    public GetMessageTransactionLogDetailsResponse createGetMessageTransactionLogDetailsResponse() {
        return new GetMessageTransactionLogDetailsResponse();
    }

    /**
     * Create an instance of {@link GetQuickStatsResponse }
     * 
     */
    public GetQuickStatsResponse createGetQuickStatsResponse() {
        return new GetQuickStatsResponse();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageStatsResponse }
     * 
     */
    public GetPerformanceAverageStatsResponse createGetPerformanceAverageStatsResponse() {
        return new GetPerformanceAverageStatsResponse();
    }

    /**
     * Create an instance of {@link GetOperationalStatusLogResponse }
     * 
     */
    public GetOperationalStatusLogResponse createGetOperationalStatusLogResponse() {
        return new GetOperationalStatusLogResponse();
    }

    /**
     * Create an instance of {@link GetMachinePerformanceLogsByRange }
     * 
     */
    public GetMachinePerformanceLogsByRange createGetMachinePerformanceLogsByRange() {
        return new GetMachinePerformanceLogsByRange();
    }

    /**
     * Create an instance of {@link GetServiceHostListRequestMsg }
     * 
     */
    public GetServiceHostListRequestMsg createGetServiceHostListRequestMsg() {
        return new GetServiceHostListRequestMsg();
    }

    /**
     * Create an instance of {@link GetSLAFaultRecordsRequestMsg }
     * 
     */
    public GetSLAFaultRecordsRequestMsg createGetSLAFaultRecordsRequestMsg() {
        return new GetSLAFaultRecordsRequestMsg();
    }

    /**
     * Create an instance of {@link GetMessageLogsResponse }
     * 
     */
    public GetMessageLogsResponse createGetMessageLogsResponse() {
        return new GetMessageLogsResponse();
    }

    /**
     * Create an instance of {@link ThreadTime }
     * 
     */
    public ThreadTime createThreadTime() {
        return new ThreadTime();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLogRequestMsg }
     * 
     */
    public GetMessageTransactionLogRequestMsg createGetMessageTransactionLogRequestMsg() {
        return new GetMessageTransactionLogRequestMsg();
    }

    /**
     * Create an instance of {@link GetAgentTypesResponseMsg }
     * 
     */
    public GetAgentTypesResponseMsg createGetAgentTypesResponseMsg() {
        return new GetAgentTypesResponseMsg();
    }

    /**
     * Create an instance of {@link MachineData }
     * 
     */
    public MachineData createMachineData() {
        return new MachineData();
    }

    /**
     * Create an instance of {@link DeleteServiceDependencyResponse }
     * 
     */
    public DeleteServiceDependencyResponse createDeleteServiceDependencyResponse() {
        return new DeleteServiceDependencyResponse();
    }

    /**
     * Create an instance of {@link GetQuickStats }
     * 
     */
    public GetQuickStats createGetQuickStats() {
        return new GetQuickStats();
    }

    /**
     * Create an instance of {@link GetAlertsRequestMsg }
     * 
     */
    public GetAlertsRequestMsg createGetAlertsRequestMsg() {
        return new GetAlertsRequestMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentProcessDataResponseMsg }
     * 
     */
    public GetMostRecentProcessDataResponseMsg createGetMostRecentProcessDataResponseMsg() {
        return new GetMostRecentProcessDataResponseMsg();
    }

    /**
     * Create an instance of {@link GetProcessPerformanceLogsByRangeResponse }
     * 
     */
    public GetProcessPerformanceLogsByRangeResponse createGetProcessPerformanceLogsByRangeResponse() {
        return new GetProcessPerformanceLogsByRangeResponse();
    }

    /**
     * Create an instance of {@link GetAllOperationalStatusLogResponse }
     * 
     */
    public GetAllOperationalStatusLogResponse createGetAllOperationalStatusLogResponse() {
        return new GetAllOperationalStatusLogResponse();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLog }
     * 
     */
    public GetMessageTransactionLog createGetMessageTransactionLog() {
        return new GetMessageTransactionLog();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageStatsAllResponse }
     * 
     */
    public GetPerformanceAverageStatsAllResponse createGetPerformanceAverageStatsAllResponse() {
        return new GetPerformanceAverageStatsAllResponse();
    }

    /**
     * Create an instance of {@link GetServiceHostListResponseMsg }
     * 
     */
    public GetServiceHostListResponseMsg createGetServiceHostListResponseMsg() {
        return new GetServiceHostListResponseMsg();
    }

    /**
     * Create an instance of {@link GetThreadTransactionsResponse }
     * 
     */
    public GetThreadTransactionsResponse createGetThreadTransactionsResponse() {
        return new GetThreadTransactionsResponse();
    }

    /**
     * Create an instance of {@link GetBrokerList }
     * 
     */
    public GetBrokerList createGetBrokerList() {
        return new GetBrokerList();
    }

    /**
     * Create an instance of {@link GetDataCollectorListResponseMsg }
     * 
     */
    public GetDataCollectorListResponseMsg createGetDataCollectorListResponseMsg() {
        return new GetDataCollectorListResponseMsg();
    }

    /**
     * Create an instance of {@link GetOperationalStatusLog }
     * 
     */
    public GetOperationalStatusLog createGetOperationalStatusLog() {
        return new GetOperationalStatusLog();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageHostingStatsResponse }
     * 
     */
    public GetPerformanceAverageHostingStatsResponse createGetPerformanceAverageHostingStatsResponse() {
        return new GetPerformanceAverageHostingStatsResponse();
    }

    /**
     * Create an instance of {@link GetQuickStatsAll }
     * 
     */
    public GetQuickStatsAll createGetQuickStatsAll() {
        return new GetQuickStatsAll();
    }

    /**
     * Create an instance of {@link GetAuditLogRequestMsg }
     * 
     */
    public GetAuditLogRequestMsg createGetAuditLogRequestMsg() {
        return new GetAuditLogRequestMsg();
    }

    /**
     * Create an instance of {@link GetThreadTransactionsRequestMsg }
     * 
     */
    public GetThreadTransactionsRequestMsg createGetThreadTransactionsRequestMsg() {
        return new GetThreadTransactionsRequestMsg();
    }

    /**
     * Create an instance of {@link GetMessageLogsRequestMsg }
     * 
     */
    public GetMessageLogsRequestMsg createGetMessageLogsRequestMsg() {
        return new GetMessageLogsRequestMsg();
    }

    /**
     * Create an instance of {@link QuickStatURIWrapper }
     * 
     */
    public QuickStatURIWrapper createQuickStatURIWrapper() {
        return new QuickStatURIWrapper();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLogResponse }
     * 
     */
    public GetMessageTransactionLogResponse createGetMessageTransactionLogResponse() {
        return new GetMessageTransactionLogResponse();
    }

    /**
     * Create an instance of {@link GetAuditLog }
     * 
     */
    public GetAuditLog createGetAuditLog() {
        return new GetAuditLog();
    }

    /**
     * Create an instance of {@link Dependencies }
     * 
     */
    public Dependencies createDependencies() {
        return new Dependencies();
    }

    /**
     * Create an instance of {@link GetMostRecentProcessDataRequestMsg }
     * 
     */
    public GetMostRecentProcessDataRequestMsg createGetMostRecentProcessDataRequestMsg() {
        return new GetMostRecentProcessDataRequestMsg();
    }

    /**
     * Create an instance of {@link GetServiceDependenciesResponse }
     * 
     */
    public GetServiceDependenciesResponse createGetServiceDependenciesResponse() {
        return new GetServiceDependenciesResponse();
    }

    /**
     * Create an instance of {@link GetHistoricalTopicQueueDetails }
     * 
     */
    public GetHistoricalTopicQueueDetails createGetHistoricalTopicQueueDetails() {
        return new GetHistoricalTopicQueueDetails();
    }

    /**
     * Create an instance of {@link GetCurrentBrokerDetailsRequestMsg }
     * 
     */
    public GetCurrentBrokerDetailsRequestMsg createGetCurrentBrokerDetailsRequestMsg() {
        return new GetCurrentBrokerDetailsRequestMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineDataResponse }
     * 
     */
    public GetMostRecentMachineDataResponse createGetMostRecentMachineDataResponse() {
        return new GetMostRecentMachineDataResponse();
    }

    /**
     * Create an instance of {@link GetHistoricalBrokerDetails }
     * 
     */
    public GetHistoricalBrokerDetails createGetHistoricalBrokerDetails() {
        return new GetHistoricalBrokerDetails();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageHostStatsRequestMsg }
     * 
     */
    public GetPerformanceAverageHostStatsRequestMsg createGetPerformanceAverageHostStatsRequestMsg() {
        return new GetPerformanceAverageHostStatsRequestMsg();
    }

    /**
     * Create an instance of {@link AuditLog }
     * 
     */
    public AuditLog createAuditLog() {
        return new AuditLog();
    }

    /**
     * Create an instance of {@link GetRecentMessageLogs }
     * 
     */
    public GetRecentMessageLogs createGetRecentMessageLogs() {
        return new GetRecentMessageLogs();
    }

    /**
     * Create an instance of {@link GetHistoricalBrokerDetailsRequestMsg }
     * 
     */
    public GetHistoricalBrokerDetailsRequestMsg createGetHistoricalBrokerDetailsRequestMsg() {
        return new GetHistoricalBrokerDetailsRequestMsg();
    }

    /**
     * Create an instance of {@link GetMachinePerformanceLogsByRangeRequestMsg }
     * 
     */
    public GetMachinePerformanceLogsByRangeRequestMsg createGetMachinePerformanceLogsByRangeRequestMsg() {
        return new GetMachinePerformanceLogsByRangeRequestMsg();
    }

    /**
     * Create an instance of {@link GetProcessPerformanceLogsByRangeRequestMsg }
     * 
     */
    public GetProcessPerformanceLogsByRangeRequestMsg createGetProcessPerformanceLogsByRangeRequestMsg() {
        return new GetProcessPerformanceLogsByRangeRequestMsg();
    }

    /**
     * Create an instance of {@link DependencyWrapper }
     * 
     */
    public DependencyWrapper createDependencyWrapper() {
        return new DependencyWrapper();
    }

    /**
     * Create an instance of {@link PurgePerformanceDataResponseMsg }
     * 
     */
    public PurgePerformanceDataResponseMsg createPurgePerformanceDataResponseMsg() {
        return new PurgePerformanceDataResponseMsg();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageStatsAll }
     * 
     */
    public GetPerformanceAverageStatsAll createGetPerformanceAverageStatsAll() {
        return new GetPerformanceAverageStatsAll();
    }

    /**
     * Create an instance of {@link ArrayOfServiceType }
     * 
     */
    public ArrayOfServiceType createArrayOfServiceType() {
        return new ArrayOfServiceType();
    }

    /**
     * Create an instance of {@link GetMessageLogs }
     * 
     */
    public GetMessageLogs createGetMessageLogs() {
        return new GetMessageLogs();
    }

    /**
     * Create an instance of {@link GetAllServiceDependencies }
     * 
     */
    public GetAllServiceDependencies createGetAllServiceDependencies() {
        return new GetAllServiceDependencies();
    }

    /**
     * Create an instance of {@link GetMessageLogsByRangeResponse }
     * 
     */
    public GetMessageLogsByRangeResponse createGetMessageLogsByRangeResponse() {
        return new GetMessageLogsByRangeResponse();
    }

    /**
     * Create an instance of {@link GetQuickStatsAllResponse }
     * 
     */
    public GetQuickStatsAllResponse createGetQuickStatsAllResponse() {
        return new GetQuickStatsAllResponse();
    }

    /**
     * Create an instance of {@link QueueORtopicDetails }
     * 
     */
    public QueueORtopicDetails createQueueORtopicDetails() {
        return new QueueORtopicDetails();
    }

    /**
     * Create an instance of {@link GetAllMostRecentMachineAndProcessDataRequestMsg }
     * 
     */
    public GetAllMostRecentMachineAndProcessDataRequestMsg createGetAllMostRecentMachineAndProcessDataRequestMsg() {
        return new GetAllMostRecentMachineAndProcessDataRequestMsg();
    }

    /**
     * Create an instance of {@link GetAllMostRecentMachineAndProcessDataResponseMsg }
     * 
     */
    public GetAllMostRecentMachineAndProcessDataResponseMsg createGetAllMostRecentMachineAndProcessDataResponseMsg() {
        return new GetAllMostRecentMachineAndProcessDataResponseMsg();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageStatsRequestMsg }
     * 
     */
    public GetPerformanceAverageStatsRequestMsg createGetPerformanceAverageStatsRequestMsg() {
        return new GetPerformanceAverageStatsRequestMsg();
    }

    /**
     * Create an instance of {@link ServiceType }
     * 
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link GetAuditLogsByTimeRangeRequestMsg }
     * 
     */
    public GetAuditLogsByTimeRangeRequestMsg createGetAuditLogsByTimeRangeRequestMsg() {
        return new GetAuditLogsByTimeRangeRequestMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineDataByDomainResponseMsg }
     * 
     */
    public GetMostRecentMachineDataByDomainResponseMsg createGetMostRecentMachineDataByDomainResponseMsg() {
        return new GetMostRecentMachineDataByDomainResponseMsg();
    }

    /**
     * Create an instance of {@link GetAuditLogResponseMsg }
     * 
     */
    public GetAuditLogResponseMsg createGetAuditLogResponseMsg() {
        return new GetAuditLogResponseMsg();
    }

    /**
     * Create an instance of {@link GetQuickStatsRequestMsg }
     * 
     */
    public GetQuickStatsRequestMsg createGetQuickStatsRequestMsg() {
        return new GetQuickStatsRequestMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineData }
     * 
     */
    public GetMostRecentMachineData createGetMostRecentMachineData() {
        return new GetMostRecentMachineData();
    }

    /**
     * Create an instance of {@link GetSLAFaultRecordsResponseMsg }
     * 
     */
    public GetSLAFaultRecordsResponseMsg createGetSLAFaultRecordsResponseMsg() {
        return new GetSLAFaultRecordsResponseMsg();
    }

    /**
     * Create an instance of {@link GetQuickStatsResponseMsg }
     * 
     */
    public GetQuickStatsResponseMsg createGetQuickStatsResponseMsg() {
        return new GetQuickStatsResponseMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentProcessData }
     * 
     */
    public GetMostRecentProcessData createGetMostRecentProcessData() {
        return new GetMostRecentProcessData();
    }

    /**
     * Create an instance of {@link GetBrokerListResponseMsg }
     * 
     */
    public GetBrokerListResponseMsg createGetBrokerListResponseMsg() {
        return new GetBrokerListResponseMsg();
    }

    /**
     * Create an instance of {@link GetAuditLogResponse }
     * 
     */
    public GetAuditLogResponse createGetAuditLogResponse() {
        return new GetAuditLogResponse();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLogResponseMsg }
     * 
     */
    public GetMessageTransactionLogResponseMsg createGetMessageTransactionLogResponseMsg() {
        return new GetMessageTransactionLogResponseMsg();
    }

    /**
     * Create an instance of {@link GetHistoricalBrokerDetailsResponse }
     * 
     */
    public GetHistoricalBrokerDetailsResponse createGetHistoricalBrokerDetailsResponse() {
        return new GetHistoricalBrokerDetailsResponse();
    }

    /**
     * Create an instance of {@link GetSLAFaultRecords }
     * 
     */
    public GetSLAFaultRecords createGetSLAFaultRecords() {
        return new GetSLAFaultRecords();
    }

    /**
     * Create an instance of {@link PurgePerformanceDataRequestMsg }
     * 
     */
    public PurgePerformanceDataRequestMsg createPurgePerformanceDataRequestMsg() {
        return new PurgePerformanceDataRequestMsg();
    }

    /**
     * Create an instance of {@link GetAllMostRecentMachineAndProcessData }
     * 
     */
    public GetAllMostRecentMachineAndProcessData createGetAllMostRecentMachineAndProcessData() {
        return new GetAllMostRecentMachineAndProcessData();
    }

    /**
     * Create an instance of {@link GetBrokerListResponse }
     * 
     */
    public GetBrokerListResponse createGetBrokerListResponse() {
        return new GetBrokerListResponse();
    }

    /**
     * Create an instance of {@link GetMessageLogsResponseMsg }
     * 
     */
    public GetMessageLogsResponseMsg createGetMessageLogsResponseMsg() {
        return new GetMessageLogsResponseMsg();
    }

    /**
     * Create an instance of {@link ArrayOfHostInstanceStats }
     * 
     */
    public ArrayOfHostInstanceStats createArrayOfHostInstanceStats() {
        return new ArrayOfHostInstanceStats();
    }

    /**
     * Create an instance of {@link BrokerDetails }
     * 
     */
    public BrokerDetails createBrokerDetails() {
        return new BrokerDetails();
    }

    /**
     * Create an instance of {@link GetMachinePerformanceLogsByRangeResponse }
     * 
     */
    public GetMachinePerformanceLogsByRangeResponse createGetMachinePerformanceLogsByRangeResponse() {
        return new GetMachinePerformanceLogsByRangeResponse();
    }

    /**
     * Create an instance of {@link GetOperationalStatusLogResponseMsg }
     * 
     */
    public GetOperationalStatusLogResponseMsg createGetOperationalStatusLogResponseMsg() {
        return new GetOperationalStatusLogResponseMsg();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLogDetailsMsg }
     * 
     */
    public GetMessageTransactionLogDetailsMsg createGetMessageTransactionLogDetailsMsg() {
        return new GetMessageTransactionLogDetailsMsg();
    }

    /**
     * Create an instance of {@link GetHistoricalTopicQueueDetailsRequestMsg }
     * 
     */
    public GetHistoricalTopicQueueDetailsRequestMsg createGetHistoricalTopicQueueDetailsRequestMsg() {
        return new GetHistoricalTopicQueueDetailsRequestMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineDataByDomainRequestMsg }
     * 
     */
    public GetMostRecentMachineDataByDomainRequestMsg createGetMostRecentMachineDataByDomainRequestMsg() {
        return new GetMostRecentMachineDataByDomainRequestMsg();
    }

    /**
     * Create an instance of {@link QuickStatData }
     * 
     */
    public QuickStatData createQuickStatData() {
        return new QuickStatData();
    }

    /**
     * Create an instance of {@link QuickStatWrapper }
     * 
     */
    public QuickStatWrapper createQuickStatWrapper() {
        return new QuickStatWrapper();
    }

    /**
     * Create an instance of {@link GetCurrentBrokerDetails }
     * 
     */
    public GetCurrentBrokerDetails createGetCurrentBrokerDetails() {
        return new GetCurrentBrokerDetails();
    }

    /**
     * Create an instance of {@link GetServiceHostList }
     * 
     */
    public GetServiceHostList createGetServiceHostList() {
        return new GetServiceHostList();
    }

    /**
     * Create an instance of {@link GetServiceHostListResponse }
     * 
     */
    public GetServiceHostListResponse createGetServiceHostListResponse() {
        return new GetServiceHostListResponse();
    }

    /**
     * Create an instance of {@link GetAgentTypesRequestMsg }
     * 
     */
    public GetAgentTypesRequestMsg createGetAgentTypesRequestMsg() {
        return new GetAgentTypesRequestMsg();
    }

    /**
     * Create an instance of {@link OperationalRecord }
     * 
     */
    public OperationalRecord createOperationalRecord() {
        return new OperationalRecord();
    }

    /**
     * Create an instance of {@link GetAllOperationalStatusLog }
     * 
     */
    public GetAllOperationalStatusLog createGetAllOperationalStatusLog() {
        return new GetAllOperationalStatusLog();
    }

    /**
     * Create an instance of {@link GetProcessPerformanceLogsByRange }
     * 
     */
    public GetProcessPerformanceLogsByRange createGetProcessPerformanceLogsByRange() {
        return new GetProcessPerformanceLogsByRange();
    }

    /**
     * Create an instance of {@link GetOperationalStatusLogRequestMsg }
     * 
     */
    public GetOperationalStatusLogRequestMsg createGetOperationalStatusLogRequestMsg() {
        return new GetOperationalStatusLogRequestMsg();
    }

    /**
     * Create an instance of {@link GetDataCollectorListRequestMsg }
     * 
     */
    public GetDataCollectorListRequestMsg createGetDataCollectorListRequestMsg() {
        return new GetDataCollectorListRequestMsg();
    }

    /**
     * Create an instance of {@link GetQuickStatsAllRequestMsg }
     * 
     */
    public GetQuickStatsAllRequestMsg createGetQuickStatsAllRequestMsg() {
        return new GetQuickStatsAllRequestMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentProcessDataResponse }
     * 
     */
    public GetMostRecentProcessDataResponse createGetMostRecentProcessDataResponse() {
        return new GetMostRecentProcessDataResponse();
    }

    /**
     * Create an instance of {@link DeleteServiceDependencyResponseMsg }
     * 
     */
    public DeleteServiceDependencyResponseMsg createDeleteServiceDependencyResponseMsg() {
        return new DeleteServiceDependencyResponseMsg();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLogDetails }
     * 
     */
    public GetMessageTransactionLogDetails createGetMessageTransactionLogDetails() {
        return new GetMessageTransactionLogDetails();
    }

    /**
     * Create an instance of {@link GetMachinePerformanceLogsByRangeResponseMsg }
     * 
     */
    public GetMachinePerformanceLogsByRangeResponseMsg createGetMachinePerformanceLogsByRangeResponseMsg() {
        return new GetMachinePerformanceLogsByRangeResponseMsg();
    }

    /**
     * Create an instance of {@link GetAgentTypes }
     * 
     */
    public GetAgentTypes createGetAgentTypes() {
        return new GetAgentTypes();
    }

    /**
     * Create an instance of {@link GetHistoricalTopicQueueDetailsResponseMsg }
     * 
     */
    public GetHistoricalTopicQueueDetailsResponseMsg createGetHistoricalTopicQueueDetailsResponseMsg() {
        return new GetHistoricalTopicQueueDetailsResponseMsg();
    }

    /**
     * Create an instance of {@link GetServiceDependenciesRequestMsg }
     * 
     */
    public GetServiceDependenciesRequestMsg createGetServiceDependenciesRequestMsg() {
        return new GetServiceDependenciesRequestMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineDataRequestMsg }
     * 
     */
    public GetMostRecentMachineDataRequestMsg createGetMostRecentMachineDataRequestMsg() {
        return new GetMostRecentMachineDataRequestMsg();
    }

    /**
     * Create an instance of {@link GetAllServiceDependenciesRequestMsg }
     * 
     */
    public GetAllServiceDependenciesRequestMsg createGetAllServiceDependenciesRequestMsg() {
        return new GetAllServiceDependenciesRequestMsg();
    }

    /**
     * Create an instance of {@link GetMessageTransactionLogDetailsResponseMsg }
     * 
     */
    public GetMessageTransactionLogDetailsResponseMsg createGetMessageTransactionLogDetailsResponseMsg() {
        return new GetMessageTransactionLogDetailsResponseMsg();
    }

    /**
     * Create an instance of {@link GetAllOperationalStatusLogResponseMsg }
     * 
     */
    public GetAllOperationalStatusLogResponseMsg createGetAllOperationalStatusLogResponseMsg() {
        return new GetAllOperationalStatusLogResponseMsg();
    }

    /**
     * Create an instance of {@link GetServiceDependenciesResponseMsg }
     * 
     */
    public GetServiceDependenciesResponseMsg createGetServiceDependenciesResponseMsg() {
        return new GetServiceDependenciesResponseMsg();
    }

    /**
     * Create an instance of {@link ArrayOfTransactionLog }
     * 
     */
    public ArrayOfTransactionLog createArrayOfTransactionLog() {
        return new ArrayOfTransactionLog();
    }

    /**
     * Create an instance of {@link GetAuditLogsByTimeRange }
     * 
     */
    public GetAuditLogsByTimeRange createGetAuditLogsByTimeRange() {
        return new GetAuditLogsByTimeRange();
    }

    /**
     * Create an instance of {@link GetAuditLogsByTimeRangeResponse }
     * 
     */
    public GetAuditLogsByTimeRangeResponse createGetAuditLogsByTimeRangeResponse() {
        return new GetAuditLogsByTimeRangeResponse();
    }

    /**
     * Create an instance of {@link PurgePerformanceDataResponse }
     * 
     */
    public PurgePerformanceDataResponse createPurgePerformanceDataResponse() {
        return new PurgePerformanceDataResponse();
    }

    /**
     * Create an instance of {@link GetAllServiceDependenciesResponseMsg }
     * 
     */
    public GetAllServiceDependenciesResponseMsg createGetAllServiceDependenciesResponseMsg() {
        return new GetAllServiceDependenciesResponseMsg();
    }

    /**
     * Create an instance of {@link GetMonitoredServiceListRequestMsg }
     * 
     */
    public GetMonitoredServiceListRequestMsg createGetMonitoredServiceListRequestMsg() {
        return new GetMonitoredServiceListRequestMsg();
    }

    /**
     * Create an instance of {@link GetDataCollectorList }
     * 
     */
    public GetDataCollectorList createGetDataCollectorList() {
        return new GetDataCollectorList();
    }

    /**
     * Create an instance of {@link GetThreadTransactions }
     * 
     */
    public GetThreadTransactions createGetThreadTransactions() {
        return new GetThreadTransactions();
    }

    /**
     * Create an instance of {@link GetDataCollectorListResponse }
     * 
     */
    public GetDataCollectorListResponse createGetDataCollectorListResponse() {
        return new GetDataCollectorListResponse();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageStatsResponseMsg }
     * 
     */
    public GetPerformanceAverageStatsResponseMsg createGetPerformanceAverageStatsResponseMsg() {
        return new GetPerformanceAverageStatsResponseMsg();
    }

    /**
     * Create an instance of {@link HostInstanceStats }
     * 
     */
    public HostInstanceStats createHostInstanceStats() {
        return new HostInstanceStats();
    }

    /**
     * Create an instance of {@link GetProcessPerformanceLogsByRangeResponseMsg }
     * 
     */
    public GetProcessPerformanceLogsByRangeResponseMsg createGetProcessPerformanceLogsByRangeResponseMsg() {
        return new GetProcessPerformanceLogsByRangeResponseMsg();
    }

    /**
     * Create an instance of {@link GetQuickStatsAllResponseMsg }
     * 
     */
    public GetQuickStatsAllResponseMsg createGetQuickStatsAllResponseMsg() {
        return new GetQuickStatsAllResponseMsg();
    }

    /**
     * Create an instance of {@link GetAgentTypesResponse }
     * 
     */
    public GetAgentTypesResponse createGetAgentTypesResponse() {
        return new GetAgentTypesResponse();
    }

    /**
     * Create an instance of {@link GetAuditLogsByTimeRangeResponseMsg }
     * 
     */
    public GetAuditLogsByTimeRangeResponseMsg createGetAuditLogsByTimeRangeResponseMsg() {
        return new GetAuditLogsByTimeRangeResponseMsg();
    }

    /**
     * Create an instance of {@link GetAllServiceDependenciesResponse }
     * 
     */
    public GetAllServiceDependenciesResponse createGetAllServiceDependenciesResponse() {
        return new GetAllServiceDependenciesResponse();
    }

    /**
     * Create an instance of {@link GetMonitoredServiceListResponseMsg }
     * 
     */
    public GetMonitoredServiceListResponseMsg createGetMonitoredServiceListResponseMsg() {
        return new GetMonitoredServiceListResponseMsg();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageStats }
     * 
     */
    public GetPerformanceAverageStats createGetPerformanceAverageStats() {
        return new GetPerformanceAverageStats();
    }

    /**
     * Create an instance of {@link DeleteServiceDependency }
     * 
     */
    public DeleteServiceDependency createDeleteServiceDependency() {
        return new DeleteServiceDependency();
    }

    /**
     * Create an instance of {@link GetAlerts }
     * 
     */
    public GetAlerts createGetAlerts() {
        return new GetAlerts();
    }

    /**
     * Create an instance of {@link GetAllOperationalStatusLogRequestMsg }
     * 
     */
    public GetAllOperationalStatusLogRequestMsg createGetAllOperationalStatusLogRequestMsg() {
        return new GetAllOperationalStatusLogRequestMsg();
    }

    /**
     * Create an instance of {@link GetAlertsResponseMsg }
     * 
     */
    public GetAlertsResponseMsg createGetAlertsResponseMsg() {
        return new GetAlertsResponseMsg();
    }

    /**
     * Create an instance of {@link GetMonitoredServiceList }
     * 
     */
    public GetMonitoredServiceList createGetMonitoredServiceList() {
        return new GetMonitoredServiceList();
    }

    /**
     * Create an instance of {@link GetAlertsResponse }
     * 
     */
    public GetAlertsResponse createGetAlertsResponse() {
        return new GetAlertsResponse();
    }

    /**
     * Create an instance of {@link GetRecentMessageLogsResponse }
     * 
     */
    public GetRecentMessageLogsResponse createGetRecentMessageLogsResponse() {
        return new GetRecentMessageLogsResponse();
    }

    /**
     * Create an instance of {@link TransactionLog }
     * 
     */
    public TransactionLog createTransactionLog() {
        return new TransactionLog();
    }

    /**
     * Create an instance of {@link GetHistoricalBrokerDetailsResponseMsg }
     * 
     */
    public GetHistoricalBrokerDetailsResponseMsg createGetHistoricalBrokerDetailsResponseMsg() {
        return new GetHistoricalBrokerDetailsResponseMsg();
    }

    /**
     * Create an instance of {@link GetServiceDependencies }
     * 
     */
    public GetServiceDependencies createGetServiceDependencies() {
        return new GetServiceDependencies();
    }

    /**
     * Create an instance of {@link GetThreadTransactionsResponseMsg }
     * 
     */
    public GetThreadTransactionsResponseMsg createGetThreadTransactionsResponseMsg() {
        return new GetThreadTransactionsResponseMsg();
    }

    /**
     * Create an instance of {@link GetMonitoredServiceListResponse }
     * 
     */
    public GetMonitoredServiceListResponse createGetMonitoredServiceListResponse() {
        return new GetMonitoredServiceListResponse();
    }

    /**
     * Create an instance of {@link GetMessageLogsByRange }
     * 
     */
    public GetMessageLogsByRange createGetMessageLogsByRange() {
        return new GetMessageLogsByRange();
    }

    /**
     * Create an instance of {@link GetBrokerListRequestMsg }
     * 
     */
    public GetBrokerListRequestMsg createGetBrokerListRequestMsg() {
        return new GetBrokerListRequestMsg();
    }

    /**
     * Create an instance of {@link GetRecentMessageLogsRequestMsg }
     * 
     */
    public GetRecentMessageLogsRequestMsg createGetRecentMessageLogsRequestMsg() {
        return new GetRecentMessageLogsRequestMsg();
    }

    /**
     * Create an instance of {@link GetCurrentBrokerDetailsResponse }
     * 
     */
    public GetCurrentBrokerDetailsResponse createGetCurrentBrokerDetailsResponse() {
        return new GetCurrentBrokerDetailsResponse();
    }

    /**
     * Create an instance of {@link GetPerformanceAverageHostingStats }
     * 
     */
    public GetPerformanceAverageHostingStats createGetPerformanceAverageHostingStats() {
        return new GetPerformanceAverageHostingStats();
    }

    /**
     * Create an instance of {@link PurgePerformanceData }
     * 
     */
    public PurgePerformanceData createPurgePerformanceData() {
        return new PurgePerformanceData();
    }

    /**
     * Create an instance of {@link GetCurrentBrokerDetailsResponseMsg }
     * 
     */
    public GetCurrentBrokerDetailsResponseMsg createGetCurrentBrokerDetailsResponseMsg() {
        return new GetCurrentBrokerDetailsResponseMsg();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineDataResponseMsg }
     * 
     */
    public GetMostRecentMachineDataResponseMsg createGetMostRecentMachineDataResponseMsg() {
        return new GetMostRecentMachineDataResponseMsg();
    }

    /**
     * Create an instance of {@link GetSLAFaultRecordsResponse }
     * 
     */
    public GetSLAFaultRecordsResponse createGetSLAFaultRecordsResponse() {
        return new GetSLAFaultRecordsResponse();
    }

    /**
     * Create an instance of {@link GetHistoricalTopicQueueDetailsResponse }
     * 
     */
    public GetHistoricalTopicQueueDetailsResponse createGetHistoricalTopicQueueDetailsResponse() {
        return new GetHistoricalTopicQueueDetailsResponse();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineDataByDomain }
     * 
     */
    public GetMostRecentMachineDataByDomain createGetMostRecentMachineDataByDomain() {
        return new GetMostRecentMachineDataByDomain();
    }

    /**
     * Create an instance of {@link GetMostRecentMachineDataByDomainResponse }
     * 
     */
    public GetMostRecentMachineDataByDomainResponse createGetMostRecentMachineDataByDomainResponse() {
        return new GetMostRecentMachineDataByDomainResponse();
    }

    /**
     * Create an instance of {@link DeleteServiceDependencyRequestMsg }
     * 
     */
    public DeleteServiceDependencyRequestMsg createDeleteServiceDependencyRequestMsg() {
        return new DeleteServiceDependencyRequestMsg();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessageLogsRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetMessageLogsRequestMsg")
    public JAXBElement<GetMessageLogsRequestMsg> createGetMessageLogsRequestMsg(GetMessageLogsRequestMsg value) {
        return new JAXBElement<GetMessageLogsRequestMsg>(_GetMessageLogsRequestMsg_QNAME, GetMessageLogsRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataCollectorListResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetDataCollectorListResponseMsg")
    public JAXBElement<GetDataCollectorListResponseMsg> createGetDataCollectorListResponseMsg(GetDataCollectorListResponseMsg value) {
        return new JAXBElement<GetDataCollectorListResponseMsg>(_GetDataCollectorListResponseMsg_QNAME, GetDataCollectorListResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMonitoredServiceListRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetMonitoredServiceListRequestMsg")
    public JAXBElement<GetMonitoredServiceListRequestMsg> createGetMonitoredServiceListRequestMsg(GetMonitoredServiceListRequestMsg value) {
        return new JAXBElement<GetMonitoredServiceListRequestMsg>(_GetMonitoredServiceListRequestMsg_QNAME, GetMonitoredServiceListRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfTransactionLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "ArrayOfTransactionLog")
    public JAXBElement<ArrayOfTransactionLog> createArrayOfTransactionLog(ArrayOfTransactionLog value) {
        return new JAXBElement<ArrayOfTransactionLog>(_ArrayOfTransactionLog_QNAME, ArrayOfTransactionLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "UUID")
    public JAXBElement<String> createUUID(String value) {
        return new JAXBElement<String>(_UUID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "ServiceType")
    public JAXBElement<ServiceType> createServiceType(ServiceType value) {
        return new JAXBElement<ServiceType>(_ServiceType_QNAME, ServiceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PurgePerformanceDataRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "PurgePerformanceDataRequestMsg")
    public JAXBElement<PurgePerformanceDataRequestMsg> createPurgePerformanceDataRequestMsg(PurgePerformanceDataRequestMsg value) {
        return new JAXBElement<PurgePerformanceDataRequestMsg>(_PurgePerformanceDataRequestMsg_QNAME, PurgePerformanceDataRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfServiceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "ArrayOfServiceType")
    public JAXBElement<ArrayOfServiceType> createArrayOfServiceType(ArrayOfServiceType value) {
        return new JAXBElement<ArrayOfServiceType>(_ArrayOfServiceType_QNAME, ArrayOfServiceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceHostListResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetServiceHostListResponseMsg")
    public JAXBElement<GetServiceHostListResponseMsg> createGetServiceHostListResponseMsg(GetServiceHostListResponseMsg value) {
        return new JAXBElement<GetServiceHostListResponseMsg>(_GetServiceHostListResponseMsg_QNAME, GetServiceHostListResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessageLogsResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetMessageLogsResponseMsg")
    public JAXBElement<GetMessageLogsResponseMsg> createGetMessageLogsResponseMsg(GetMessageLogsResponseMsg value) {
        return new JAXBElement<GetMessageLogsResponseMsg>(_GetMessageLogsResponseMsg_QNAME, GetMessageLogsResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HostInstanceStats }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "HostInstanceStats")
    public JAXBElement<HostInstanceStats> createHostInstanceStats(HostInstanceStats value) {
        return new JAXBElement<HostInstanceStats>(_HostInstanceStats_QNAME, HostInstanceStats.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMonitoredServiceListResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetMonitoredServiceListResponseMsg")
    public JAXBElement<GetMonitoredServiceListResponseMsg> createGetMonitoredServiceListResponseMsg(GetMonitoredServiceListResponseMsg value) {
        return new JAXBElement<GetMonitoredServiceListResponseMsg>(_GetMonitoredServiceListResponseMsg_QNAME, GetMonitoredServiceListResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfHostInstanceStats }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "ArrayOfHostInstanceStats")
    public JAXBElement<ArrayOfHostInstanceStats> createArrayOfHostInstanceStats(ArrayOfHostInstanceStats value) {
        return new JAXBElement<ArrayOfHostInstanceStats>(_ArrayOfHostInstanceStats_QNAME, ArrayOfHostInstanceStats.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPerformanceAverageStatsRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetPerformanceAverageStatsRequestMsg")
    public JAXBElement<GetPerformanceAverageStatsRequestMsg> createGetPerformanceAverageStatsRequestMsg(GetPerformanceAverageStatsRequestMsg value) {
        return new JAXBElement<GetPerformanceAverageStatsRequestMsg>(_GetPerformanceAverageStatsRequestMsg_QNAME, GetPerformanceAverageStatsRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessageTransactionLogRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetMessageTransactionLogRequestMsg")
    public JAXBElement<GetMessageTransactionLogRequestMsg> createGetMessageTransactionLogRequestMsg(GetMessageTransactionLogRequestMsg value) {
        return new JAXBElement<GetMessageTransactionLogRequestMsg>(_GetMessageTransactionLogRequestMsg_QNAME, GetMessageTransactionLogRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceHostListRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetServiceHostListRequestMsg")
    public JAXBElement<GetServiceHostListRequestMsg> createGetServiceHostListRequestMsg(GetServiceHostListRequestMsg value) {
        return new JAXBElement<GetServiceHostListRequestMsg>(_GetServiceHostListRequestMsg_QNAME, GetServiceHostListRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPerformanceAverageHostStatsRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetPerformanceAverageHostStatsRequestMsg")
    public JAXBElement<GetPerformanceAverageHostStatsRequestMsg> createGetPerformanceAverageHostStatsRequestMsg(GetPerformanceAverageHostStatsRequestMsg value) {
        return new JAXBElement<GetPerformanceAverageHostStatsRequestMsg>(_GetPerformanceAverageHostStatsRequestMsg_QNAME, GetPerformanceAverageHostStatsRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "TransactionLog")
    public JAXBElement<TransactionLog> createTransactionLog(TransactionLog value) {
        return new JAXBElement<TransactionLog>(_TransactionLog_QNAME, TransactionLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PurgePerformanceDataResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "PurgePerformanceDataResponseMsg")
    public JAXBElement<PurgePerformanceDataResponseMsg> createPurgePerformanceDataResponseMsg(PurgePerformanceDataResponseMsg value) {
        return new JAXBElement<PurgePerformanceDataResponseMsg>(_PurgePerformanceDataResponseMsg_QNAME, PurgePerformanceDataResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataCollectorListRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetDataCollectorListRequestMsg")
    public JAXBElement<GetDataCollectorListRequestMsg> createGetDataCollectorListRequestMsg(GetDataCollectorListRequestMsg value) {
        return new JAXBElement<GetDataCollectorListRequestMsg>(_GetDataCollectorListRequestMsg_QNAME, GetDataCollectorListRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessageTransactionLogResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetMessageTransactionLogResponseMsg")
    public JAXBElement<GetMessageTransactionLogResponseMsg> createGetMessageTransactionLogResponseMsg(GetMessageTransactionLogResponseMsg value) {
        return new JAXBElement<GetMessageTransactionLogResponseMsg>(_GetMessageTransactionLogResponseMsg_QNAME, GetMessageTransactionLogResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPerformanceAverageStatsResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", name = "GetPerformanceAverageStatsResponseMsg")
    public JAXBElement<GetPerformanceAverageStatsResponseMsg> createGetPerformanceAverageStatsResponseMsg(GetPerformanceAverageStatsResponseMsg value) {
        return new JAXBElement<GetPerformanceAverageStatsResponseMsg>(_GetPerformanceAverageStatsResponseMsg_QNAME, GetPerformanceAverageStatsResponseMsg.class, null, value);
    }

}
