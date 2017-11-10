# Fine Grained Service Monitoring System

The purpose of the FGSMS is to provide an open source enterprise service and 
resource management (ESM) capability that is secure, scalable and flexible.  In 
general, ESM products provide performance monitoring of web resources such as 
XML web services or messaging brokers, auditing and usage tracking, service 
level agreements, email alerts, report generation, include a user interface that 
makes sense of this data and lastly, some kind of federation mechanism to expose
 collected data to others. FGSMS currently provides these capabilities by installing 
and integrating agents into products and/or servers that are to be monitored with
 minimal effort and performance impact while providing a high level of robustness. 
 These agents are easily extendable to monitor new capabilities and technologies 
as they become available. Because of the scale and function of FGSMS, it is targeted 
for brigade level and higher echelons, network operating centers, cloud hosting facilities or 
other operation centers. 

Aside from production level monitoring of software, FGSMS can also be used for 
other purposes, such as developer and compliance testing, scenarios by using the 
message logging features. FGSMS is Open Source Software (OSS) and is currently 
available on github.com.

### The need for ESM

Centrally monitoring and managing web service and resources is a critical service
 oriented architecture (SOA) capability. The idea behind SOA, and now the 
micro-service trend, is to break monolithic components down into smaller, reusable
 pieces and exposing them as distributed XML services or resources. Distributing 
components introduces a number of unique challenges such as centralized access 
control, auditing, performance monitoring and management. These problems become 
further compounded when introducing cloud environments and virtualization. For 
example, for even the best of system administrators, it can be difficult to detect 
that particular web service has faulted and thus a critical component is no longer 
available. 

When distributing components, knowing the true dependencies for a specific 
component can be difficult, despite having detailed documentation. FGSMS’s agents 
can at least help solve this problem through dependency injection which allows the 
tracking of a service invocation from end to end. This also helps to enable root 
cause analysis.

### Capability Gaps in existing products

One of the problems common to existing ESM capabilities is the lack of 
extensibility. FGSMS was designed from the ground up to make it easy for 
developers to add new capabilities, components or data collection agents to their 
instance. The database and services are well documented and were designed with 
an easy to understand schema to make exporting data for specialized purposes simple.
 Some of the capability gaps that were identified include the lack of sufficient 
data access control measures, limited reporting capabilities, lack of easy data 
extraction or federation. All of these deficiencies are directly addressed by FGSMS.

