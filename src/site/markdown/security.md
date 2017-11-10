# Security Guide

FGSMS is designed to be compliant for up secure environments that operate 24-7. 
The following provides additional guidelines and security protocols to ensure 
operational reliability and security operation. This document is primarily for 
government agencies and references DISA STIG guidelines.

 * Authentication is configured for all components
 * Secure ports are in use on all components

For high availability, deployment criteria should be used.

 * Multiple instances of all FGSMS web services running within separate containers or separate machines.
 * URL roll over configuration for all supported agents
 * Failover/mirroring/clustering for the databases

# Required application documentation

## Software used without warranty APP2135/APP2090 

In compliance with the referenced Application Security Checklist items, the 
following software used without warranty.

 * JFreeChart - http://www.jfree.org/jfreechart/
 * CEWOLF - http://cewolf.sourceforge.net/new/index.html
 * jQuery - http://jquery.com/
 * SIGARS – Provided by VMware/Hyperic
 * Quartz – http://quartz-scheduler.org

## Updates and Patches - APP2130

Updates and patches are available at the FGSMS website 

## Change Control Board - APP4040

CCB information is available at the FGSMS website 

## Usage of mobile code - APP3730 

FGSMS’s Web GUI uses Javascript for a number of functions that aid user 
interaction, providing an automatically updating dashboard and form validation. 
jQuery is also used to enable on screen calendar and some dynamic html.

## Web Interface CAC authentication APP3280

*APP3305 PKI validation cat1, not revoked and issued by a trusted root certificate authority*

*APP3280 CAC enabled cat2 except on SIPR*

See the Installation Guide for details on how to enable PKI/CAC authentication to the FGSMS web interface and services.

## Auditing

*APP6140 cat2 audit trails must be retained for 1 year for non-SAMI, SAMI 5 years.*

FGSMS, as of RC3 includes an auditing system that logs information within the 
Configuration database. This data contains the basics of whom performed what action,
 when. It can be accessed via the Web GUI and web services, but only if you have Global Administrator permissions. These audit logs are also written to the Java logging system as a backup, however theses logs are not centrally located and there is no guarantee of how long it will be retained. 

## Connectivity over SSL

FGSMS ships with everything configured for “localhost” on non-secure ports. For 
production use, all connections should be over secure ports. This includes:

 * Agents to DCS/PCS service
 * Web GUI to PCS/DAS/RS/SS and UDDI server (optional)
 * PCS/DAS/RS/SS/DCS to PostgreSQL
 * Bueller to PostgreSQL
 * UDDI Publisher to PostgreSQL and UDDI server
 * Data Pruner to PostgreSQL
 * SLA Processor to PostgreSQL, SMTP
 * Statistics Aggregator to PostgeSQL
 * Any of the alerting mechanisms that support SSL

In certain cases, NIPR and non-SIPR deployments, DISA requires the usage of SSL 
with Client Certificate authentication. This scenario is supportable for almost 
all scenarios in which that configuration is supported.


### Authentication Scenarios

FGSMS ships with two authentication options, Username/Password and CAC/PKI. 

Authentication for FGSMS is delegated to the container, Tomcat/Jboss Application 
Server, to enforce and therefore any authentication module for Tomcat/Jboss can 
be supported with a few caveats. 

 * The authentication module must be a supportable HTTP based authentication scheme. This is used to ensure web service stack interoperability.
 * The web service stack must support the authentication mechanism (digest is not supported, only HTTP BASIC and HTTP CLIENT-CERT)
 * Authentication is enforced at the web service layer via FGSMSServices.war
 * Authentication via the Web GUI is merely a pass-through mechanism for usernames/passwords and therefore communication from the FGSMSWeb.war to FGSMSServices.war should be encrypted.

# Threat Models and Mitigations

## APP3020, threat models, mitigations cat2

Ensure all sensitive properties files are encrypted using AES 256 bit or better encryption algorithms.

## Threat: man in the middle attack
Mitigation: Use secure ports with sufficient encryption.

## Threat: Disclosing too much information within a service level agreement alert
Mitigation: Train users (with at least write access to a service policy) to use XPath queries for SLA alerts with caution.

## Changing Encryption Keys

FGSMS ships with an AES 256bit encryption key which is usable on virtually all 
JDK/JRE based systems. It is recommended to change the key by generating your 
own using the built in tool.

Keys can be changed after the fact using the Recryptor tool. Both the old key 
and the new key must be available at that time.


# Ports Protocols and Services

FGSMS Server to Agents
- Default port is 8888 for unsecure, 9443 for SSL/TLS based communication. Both 
  can be changed to anything by the administrator. An additional port can also be
  used for PKI based authentication (944).

FGSMS Server to Postgres
- Uses the standard Postgres port, 5432 which can be changed by the administrator

FGSMS Server to alerting endpoints
- SMTP on administrator specified port to deliver email alerts
- Uses AMQP and HornetQ on administrator specified port to delivery email alerts

FGSMS Server side agents
- Uses JMX on an administrator specified port to capture statistics from a number of different components
- Syslog on an administrator specified port to pipe log output to syslog, can be udp or tcp

FGSMS Agents
- If enabled, uses multicast DNS on port 5353 to discover the location of the server
- If enabled, uses HTTP/HTTPS on any specified port to a UDDI instance to discover the location of the server

