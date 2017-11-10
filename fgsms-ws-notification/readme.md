# WS-Notification Broker implementation

This package is a number of WS-Notification libraries, written primarily as a super
simple example for WSN. At the time this was written, the only thing
that existed was Apache CXF's implementation, which was closely tied to
the CXF stack and difficult to separate. Thus this library was born.

## fgsms-wsnotification-spec
Basically, we have a spec library, which is essentially `wsimport`'s all of the
OASIS provided XSD's and WSDL's. 


## fgsms-wsnotification-broker-jar and fgsms-wsnotification-broker-war

The broker jar is a super simple, minimally implemented WS-Notification broker.
It only implements the bare minimum that FGSMS needs in order to deliver the 
alerts to subscribers. It's not very robust and is really only suitable for testing
and would most likely fall apart at volume. FGSMS only uses this for integration
tests. The WAR file is just a wrapper for the broker JAR to make it deployable
in Tomcat with Apache CXF. It is stateless, all data is stored in memory 

It should deploy the following endpoints

http://localhost:8080/fgsms.WSNBroker/services/
/WSN-Broker/RegistrationManager
/WSN-Broker/Broker
/WSN-Broker/PullPoint
/WSN-Broker/CreatePullPoint
/WSN-Broker/SubscriptionManager


## fgsms-wsnotification-client

This library has some reusable bits to make writing an async notification WS-N
client. It's used by the WS-Notification SLA Alerting plugin for FGSMS.



client notify interfaces with a callback delegate is present, implement interface 
`org.miloss.fgsms.wsn.clientcallback.IWSNCallBack`


example:

````
import org.miloss.fgsms.wsn.WSNotificationBroker;
import org.miloss.fgsms.wsn.client.NotificationService;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.brw_2.NotificationBroker;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;

...

String callbackurl = "http://localhost:7777/WSNCallback";
org.oasis_open.docs.wsn.brw_2.NotificationBroker port = new mil.army.cerdec.wsn.client.WSNotifyCallback(this);
Endpoint publish = Endpoint.publish(callbackurl, port);
if (publish.isPublished()) {
    System.out.println("Callback listening on " + url);
} else {
    System.err.println("Callback NOT listening on " + url);
}
//wait for messages to come in
publish.stop();
````
