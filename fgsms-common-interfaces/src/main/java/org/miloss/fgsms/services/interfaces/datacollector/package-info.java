/**
 * fgsms Data Collector Service. Primary collection point for record message transaction. UNCLASSIFIED
 * 	This is DCS iteration 5, distributed with fgsms RC4
 * 	
 * 	added, recordat to record the timestamp from which a message was observed. this was initially set at the DCS service as the timestamp that it was record in the database to handle the case of backup queues for DCS unavailability
 * 	added, new fault for AddData/AddMoreData requests when the global setting of agents disabled is set
 * 	added, headers, a space for recording header information outside of soap envelopes
 * 	
 * 	DCSv6
 * 	changed recordedAt from a long to dateTime for easier compatibility with .net agents
 * 	
 * 	DCSv7, added statistical adddata for brokers
 * 	
 * 	DCS8, documentation changes
 * 		added AddMachineAndProcessData
 * 
 * 	DCS9, added opstatus function
 * 	
 * 	
 * 
 */
@javax.xml.bind.annotation.XmlSchema(namespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.miloss.fgsms.services.interfaces.datacollector;
