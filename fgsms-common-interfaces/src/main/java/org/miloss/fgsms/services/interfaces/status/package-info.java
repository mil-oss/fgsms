/**
 * fgsms Status Service. Provides getters and setters for monitored service status UNCLASSIFIED
 * 	WSDL v2 used with fgsms RC3.2 and up
 * 	WSDL v4 used with fgsms RC5 
 * 		changes include requiring the security classification level to be declared on all messages
 * 		added removestatus which enables sysadmins to completely remove a status entry and associated records
 * 
 * 	v5, added SetMoreStatus overload to set the status of multiple items at the same time
 * 	added AddStatisticalData, usage for C++ qpid or other brokers via
 * 	
 * 	v6 documentation changes
 * 		policy type is now required for set status
 * 	v7 correction of several documentation typos 2/26/2013
 * 		addition of Extended Status methods, enables agents to send in abitrary data along with status information.
 * 
 * 	
 * 
 */
@javax.xml.bind.annotation.XmlSchema(namespace = "urn:org:miloss:fgsms:services:interfaces:status", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.miloss.fgsms.services.interfaces.status;
