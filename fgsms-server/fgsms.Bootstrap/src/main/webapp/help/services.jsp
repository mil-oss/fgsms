<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>FGSMS's Web Services</h1>
    <p>A quick tutorial to find and get access to the data that you're looking for.</p>
</div>
<div class="row-fluid">
    <div class="span12">
        <h2>The Services</h2>
        FGSMS has a number of SOAP/XML web services. Here's the list along with a short description. For single server installs, WSDL's can be accessed <a href="/FGSMSServices">here</a>.
        <ul>
            <li>
                DCS - The Data Collector Service (DCS) serves as the primary data collection point for FGSMS. All monitored services issue requests to the DCS via agents which are subsequently stored in the performance database. The DCS is also responsible for perform transactional service level agreement processing and alerting.
            </li>
            <li>
                PCS - The Policy Configuration Service (PCS) serves as the primary access point for the configuration of monitored services, user permissions, publication information, and service level agreements. The PCS is also used by agents to obtain the most recent service policy and is used by the Web GUI or administrative tools to change service policies.
            </li>
                <li>
                DAS - The Data Access Service (DAS) serves as the primary data access point for FGSMS. It is used extensively by the Web GUI for rendering all charts and graphs.
            </li>
            <li>
                SS - The Status Service (SS) serves as an access point getting and setting the current operating status of one or more services.
            </li>
            <li>
                RS - The Reporting Service (RS) generates a zipped HTML or CSV containing a set of reports for a given set of services over a given period of time. These reports can further be customized via style sheets and html files.
            </li>
            <li>
                ARS - The Automated Reporting Service (ARS) works hand in hand with the RS and a scheduler to enable users to schedule reports to be automatically generated at specific intervals.
            </li>
           
            <li>ACS and ACSA - The Agent Configuration Service. These are very early prototypes for providing a centrally controlled configuration file for remote web services agents</li>
        </ul>
        
        <h2>A few pointers before beginning</h2>
        Every request to FGSMS must have a classification level specified. If you're unsure what level the server is at, you can request it using the PCS's method GetPolicyPolicy. It is the only 
        web service method you can call without specifying the classification level.
        <h2>Where do I start?</h2>
        Start with the DAS. The simplest thing to do is to ask for what you have access to by calling Get Monitored Service List.
        Request:
        <pre>
&lt;soapenv:Envelope xmlns:soapenv=&quot;http://schemas.xmlsoap.org/soap/envelope/&quot; xmlns:urn=&quot;urn:org:miloss:fgsms:services:interfaces:dataAccessService&quot; xmlns:urn1=&quot;urn:org:miloss:fgsms:services:interfaces:common&quot;&gt;
   &lt;soapenv:Header/&gt;
   &lt;soapenv:Body&gt;
      &lt;urn:GetMonitoredServiceList&gt;
         &lt;urn:request&gt;
            &lt;urn:classification&gt;
               &lt;urn1:classification&gt;U&lt;/urn1:classification&gt;
               &lt;urn1:caveats&gt;&lt;/urn1:caveats&gt;
            &lt;/urn:classification&gt;
         &lt;/urn:request&gt;
      &lt;/urn:GetMonitoredServiceList&gt;
   &lt;/soapenv:Body&gt;
&lt;/soapenv:Envelope&gt;
        </pre>
        And the response:
        <pre>
&lt;env:Envelope xmlns:env=&quot;http://schemas.xmlsoap.org/soap/envelope/&quot;&gt;
   &lt;env:Header&gt;
      &lt;fgsms.relatedmessage xmlns=&quot;org.miloss.fgsms.headers&quot;&gt;8a90f1bf-c9d8-4c31-839b-9ec41e1d7f0a&lt;/fgsms.relatedmessage&gt;
      &lt;fgsms.threadid xmlns=&quot;org.miloss.fgsms.headers&quot;&gt;57072f27-6502-414d-9260-b9a9f6135775&lt;/fgsms.threadid&gt;
   &lt;/env:Header&gt;
   &lt;env:Body&gt;
      &lt;ns2:GetMonitoredServiceListResponse xmlns=&quot;urn:org:miloss:fgsms:services:interfaces:common&quot; xmlns:ns2=&quot;urn:org:miloss:fgsms:services:interfaces:dataAccessService&quot; xmlns:ns3=&quot;urn:org:miloss:fgsms:services:interfaces:faults&quot;&gt;
         &lt;ns2:GetMonitoredServiceListResult&gt;
            &lt;ns2:classification&gt;
               &lt;classification&gt;U&lt;/classification&gt;
               &lt;caveats&gt;&lt;/caveats&gt;
            &lt;/ns2:classification&gt;
            &lt;ns2:ServiceList&gt;
               &lt;ns2:ServiceType&gt;
                  &lt;ns2:URL&gt;http://fgsms:8888/FGSMSServices/DAS&lt;/ns2:URL&gt;
                  <b>&lt;ns2:policyType&gt;Transactional&lt;/ns2:policyType&gt;</b>
                  &lt;ns2:hostname&gt;fgsmsdev1&lt;/ns2:hostname&gt;
                  &lt;ns2:domainname&gt;unspecified&lt;/ns2:domainname&gt;
               &lt;/ns2:ServiceType&gt;
           &lt;/ns2:ServiceList&gt;
         &lt;/ns2:GetMonitoredServiceListResult&gt;
      &lt;/ns2:GetMonitoredServiceListResponse&gt;
   &lt;/env:Body&gt;
&lt;/env:Envelope&gt;
        </pre>
        <p>From here, you can request transaction logs, aggregated statistics (Quick Stats), current status information (SS), view and change the service policy (PCS) and a whole variety of other things. This list is access control filtered.
        Pay attention to the <b>policy type</b>. This identifies what kind of thing that is represented by the URL. You'll also find this mentioned elsewhere in this website.</p>
        <p>For more information, you want to want to download the FGSMS API Documentation as well as the SDK Samples.
        </p>
        
    </div><!--/span-->

    <!--/span-->
</div><!--/row-->

