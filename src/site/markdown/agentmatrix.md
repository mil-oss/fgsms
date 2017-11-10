# Agent Matrix

This article discusses how FGSMS agents function, particularly how they
 collect statistics and availability information and report that data
 back to the server.

## Method of Data Collection

FGSMS includes a number of different agents that serve different purposes.

| Agent                      | Type |  Runs at  | Status      | Metrics | Notes
| ---                        | -----| -------  | ------      | ------- | ----
| Generic JAXWS              | Web Service | JAXWS Handler | Bueller    | Sent on demand | Covers Axis 2.0, SunRI, Metro and CXF
| Apache CXF                 | Web Service | Interceptor | Bueller    | Sent on demand | Covers JAXWS and JAXRS with Apache CXF
| Jboss ESB                  | Web Service | Embedded | Bueller    | Sent on demand | Implements the proxy method of monitoring
| Java Servlets              | Web Service | Embedded | Bueller    | Sent on demand | v2.5 or newer
| .NET WCF and ASP.NET       | Web Service | Embedded or OS wide | Bueller    | Sent on demand | Covers IIS and self hosted services and clients via .config file. Can be applied globally via Machine.config applies to all
| Apache Axis 1.x            | Web Service | Embedded | Bueller    | Sent on demand | Covers Apache Axis 1.3-1.4 based services
| OS Agent                   | Operating System|Standalone Process | Self reporting, 30 sec interval | Self reporting, 30 sec interval, Covers processes and system level metrics
| HornetQ via JMS            | Broker | FGSMS server | Self reporting, 30 sec interval | Self reporting, 30 sec interval
| Apache Qpid via JMS        | Broker | FGSMS server | Self reporting, 30 sec interval | Self reporting, 30 sec interval
| Apache Qpid Python         | Broker | Standalone | Self reporting, 30 sec interval | Self reporting, 30 sec interval
| Apache ServiceMix via JMS  | Broker | FGSMS server | Self reporting, 30 sec interval | Self reporting, 30 sec interval
| Bueller                    | Status |FGSMS server | Self reporting, 30 sec interval | Self reporting, 30 sec interval | Attempts an HTTP connection to the web service/server

Transmission rates for non-web service agents are centrally configured via the 
FGSMS user interface. See Global Policies and General Settings in the [User Guide](user.html)

## What kind of data is or can be collected?

### Web Service, Servlets, and ESB (proxy type) Agents

- The identity of the requester
    - IP address
    - Username
    - PKI Certificate DN
    - XML Xpath query
- Timestamp the request came in
- Size of the payloads (request and response)*
- Content of the payloads (request and response)*
- HTTP headers of the request and response*
- Message ID (UUID generated)
- Thread ID (UUID generated), used to track conversations or service chaining events

(*) notates that this information isn't always available depending on the
framework, agent type, or operating configuration.

### Operating System Agent

FGSMS comes with an Operating System Agent that uses the SIGARS API. It
provides monitoring on a variety of different platforms. The following
data points can be recorded:

OS Wide

- System wide Memory and CPU usage
- List of running processes
- Network I/O on selected network cards
- Disk online/offline status, Disk I/O and free space on selected partitions
- Online/offline status is provided by automatically via a heartbeat

Process specific

- Open File Handles
- CPU Usage
- Memory Usage
- Operating Status

The OS Agent covers nearly all Windows and Linux/Unix flavored operating
systems (except Android). For processes, FGSMS will aggregate multiple instances 
of a specific process together. 

### Message Broker Agents

Message brokers are generally publish and subscribe servers that collect their
own statistics in their own way and generally expose them as either JMX
end points or via some kind of command line interface.

- Operating status
- On a per topic/queue basis
    - Queue/topic name
    - Messages In/Out/Dropped
    - Bytes In/Out/Dropped
    - Consumer count
    - Queue depth

### Status only agents

Right now, there's only one status only agent called Bueller. Named after
Ferris Bueller's Day Off, Bueller periodically 'pings' each endpoint by
essentially calling executing a HTTP GET request to the service URL.
FGSMS's Bueller function was originally designed to hit XML SOAP services
is to automatically appends a `?wsdl` to the request URL.

Bueller can also support scenarios where authentication is required, including
most HTTP authentication types, including NTLM and PKI/X509 certificate authentication.

## How do agents communicate with FGSMS's server?

Via TCP/HTTP/XML/SOAP messages. Agents only communicate with the Policy
and Configuration Service (PCS), the Data Collector Service (DCS), and
the Status Service (SS). Authentication can be username/password or PKI
authentication.

## Agent load balancing and offline support

Most agents have built in support load balancing/failover for communicating
with FGSMS's server and if enabled, they will support dynamic discovery
of FGSMS's server endpoints via a variety of mechanisms.

In the event an agent cannot access the FGSMS endpoints, it can be
configured to perform a number of actions.
 - Keep the content in memory and continue to retrying
 - Drop the content
 - Store it to disk, when connectivity is reestablished, content on disk
   will be enqueued for transmission.
