/**
 * fgsms Data Access Service. Provides read access and basic analytical functions of fgsms collected data. UNCLASSIFIED
 * 
 * DAS version 9 - feb26/2013
 * fgsms 6.3 and up.
 * Change to correct the spelling typo for 'Sucess'
 * added opstat binding
 * 
 * DAS version 8
 * GetAlertsRequestMsg - added offset and record count, both longs
 * GetQuickStatsAll - you can now request just All-Methods or everything
 * brokerDetail, GetCurrentBrokerDetailsResponseMsgs - added displayName
 * GetAgentTypesRequestMsg, removed hostname and monitor hostname, they are not activately tracked
 * adding uptime to quickstat wrapper
 * adding additional fields to quickstat wrapper to support brokers, processes and machine data
 * adding records and offset for sla faults
 * added wsdl/xsd documentation
 * corrected a spelling mistake in QuickStatsWrapper
 * new method, audit log search by time range
 * new methods
 * 
 * GetMostRecentMachineDataByDomain -- get all machines in a given domain plus drive info
 * GetMostRecentMachineData --machine's most recent perf data plus drive usage
 * GetMostRecentProcessData -- get processes most recent data
 * GetAllMostRecentMachineAndProcessData - returns the most recent information for all machine and processes that the requestor has access to
 * GetMachinePerformanceLogsByRange - returns performance and drive information logs of a specific machine records by uri and time range
 * GetProcessPerformanceLogsByRange - returns performance logs of a specific process records by uri and time range
 * 
 * GetServicesByDomain
 * GetServicesByBucket
 * GetServicesByPolicyType
 * //GetServicesByLocation
 * 
 *  8-2-2012
 * GetServiceDependencies (services i depend on and services that depend on me)
 * GetAllServiceDependencies (services i depend on and services that depend on me)
 * DeleteServiceDependency (removes a specific dependency from a specific service)
 * 
 * 
 * 		DAS version 7, added new columns to quickstats
 * 		added getquickstats all
 * DAS version 6 used with fgsms RC5	added new method, QuickStats which returns a cached list of stats for a given service, generated every 5 minutes via the Statistics Publisher. Refactor of GetMessageLogs
 * 	DAS version 5 used with fgsms RC4	added new parameters to transaction log, headers request, headers response, related and thread id numbers
 * 	
 * 	
 * 
 */
@javax.xml.bind.annotation.XmlSchema(namespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.miloss.fgsms.services.interfaces.dataaccessservice;
