# FGSMS Messaging Specifications

Want to use FGSMS from a language other than Java? (or just like to DIY). No problem.
Eventually this document will evolve into an interface control document.

100% of the functions and capabilities that both the user interface and the collections of agents communication with the FGSMS server using <a href="http://www.w3.org/TR/soap/">SOAP</a> web services (REST endpoints are in development).

FGSMS's capabilities are clusted and grouped into a number of different service endpoints. 

### Things you'll need to know before hand

 * URLs to the service(s)
 * Credentials
 * Authentication Mode (Username/password)


### XML SOAP Services
Access to all information in the FGSMS system is via web services. FGSMS comes with six core web services. All of the WSDL and XSD specifications can be 
<a href="https://github.com/mil-oss/fgsms/tree/master/fgsms-common-interfaces/src/main/resources">found here</a>.

 * DCS - The Data Collector Service (DCS) serves as the primary data collection point for FGSMS. 
 * PCS - The Policy Configuration Service (PCS) serves as the primary access point for the configuration of monitored services, user permissions, publication information, and service level agreements. The PCS is also used by agents to obtain the most recent service policy and is used by the Web GUI or administrative tools to change service policies.
 * RS - The Reporting Service (RS) generates a zipped HTML containing a set of reports for a given set of services over a given period of time. These reports can further be customized via style sheets and html files..
 * ARS - The Automated Reporting Service (ARS) works hand in hand with the RS and a scheduler to enable users to schedule reports to be automatically generated at specific intervals.
 * DAS - The Data Access Service (DAS) serves as the primary data access point for FGSM. It is used extensively by the Web GUI for rendering all charts and graphs.
 * SS - The Status Service (SS) serves as an access point getting and setting the current operating status of a service.

