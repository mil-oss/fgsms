/**
 * fgsms Policy Configuration Service. UNCLASSIFIED
 * v9
 * added opstat binding 2-26-2013	
 * add agent mail box, designed for OS level agents for executing 	5-31-2013
 * add plugin system Oct/Nov 2013
 * 	v8
 * 	added Transport authentication styles to resolve ntlm authentication issues for bueller
 * 	added support for up to 4 email addresses per person
 * 	adding a general SLA Rule for extensibility reasons. SLARuleGeneric
 * 	adding a general SLA Action for extensiblity reasons SLAActionGeneric
 * 	added a SLA rule for stale records, StaleData
 * 	
 * 	v7
 * 	added additional SLA alert types, Army DDS Alert Msg, JMS alerting, SLAActionRestart
 * 	refactor of federation policies
 * 	New methods
 * 		SetGeneralSettings
 * 		GetGeneralSettings
 * 		RemoveGeneralSettings
 * 		SetCredentials
 * 		ClearCredentials
 * 		
 * 		GetDomainList - gui interaction
 * 		
 * 		GetMachinesByDomain - gui interaction 
 * 		
 * 		GetProcessesListByMachine - gui and management features, returns everything running on a machine
 * 		
 * 		SetProcessListByMachine - managed agents call this on start up and periodically, creates a machine policy if it doesn't exist
 * 		
 * 		GetMonitoredItemsByMachine - managed service agents call this to find out what to do
 * 		
 * 		SetServicePolicy updates, refactor of 'ServicePolicy' it's now abstract and can be one of a number of things, refactor
 * 		
 * 
 * Get/Set Buckets/Categories?		
 * 		
 * 		Add SLA Rules
 * 		QueueOrTopicDoesNotExist
 * 		
 * 		LowDiskSpace
 * 		HighCPUUsageOverTime
 * 		HighCPUUsage
 * 		HighMemoryUsage
 * 		HighMemoryUsageOverTime
 * 		HighNetworkUsage
 * 		HighNetworkUsageOverTime
 * 			HighDiskUsageOverTime
 * 			HighDiskUsage
 * 			
 * 			HighOpenFileHandles
 * 			HighThreadCount
 * 			
 * 		StatusAgentMessageContainsIgnoreCase
 * 		TransactionalAgentMemoContainsIgnoreCase
 * 		ConsumerEqualsIgnoreCase
 * 		ConsumerContainsIgnoreCase
 * 		SLAActionRunScript, RunAtLocation
 * 		
 * 		
 * 		Added new elements to ServicePolicy
 * 			POC
 * 			Description
 * 			BucketCategory
 * 		Added optional parameter to GetPolicy, to specify the creation type in the case of an agent requesting something that doesn't exist.
 * 		
 * 		DriveInformation
 * 		Removed logLocation from SLAAction Log
 * 		
 * 	
 * for fgsms RC5, PCS wsdl version 6
 * 	
 * 	setmyemailaddress
 * 	getmyemailaddress
 * 	Service Policy Display Name, changes to dashboard charting in gui
 * 	get/set alert registrations
 * 	Some SLA rules were removed to simplify processing
 * 	
 * 	for fgsms RC4, PCS wsdl version 5
 * 	
 * 	changes: added new global policy to disable agents
 * 	added RecordHeaders for agents to record http or other header information
 * 	AgentsEnabled
 * 	BuellerEnabled
 * 	GeoTagging for services
 * 	ServicePolicy type
 * 	
 * 	added method GetAgentPrinicples to determine if any agent roles are defined.
 * 	added new SLA type, BrokerQueueSizeGreaterThan
 * 	
 * 	
 * 	
 * 
 */
@javax.xml.bind.annotation.XmlSchema(namespace = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.miloss.fgsms.services.interfaces.policyconfiguration;
