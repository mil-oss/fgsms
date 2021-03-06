
package org.oasis_open.docs.wsn.brw_2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "PausableSubscriptionManagerService", targetNamespace = "http://docs.oasis-open.org/wsn/brw-2")
public class PausableSubscriptionManagerService
    extends Service
{

    private final static URL PAUSABLESUBSCRIPTIONMANAGERSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.oasis_open.docs.wsn.brw_2.PausableSubscriptionManagerService.class.getName());

        static {
           ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            PAUSABLESUBSCRIPTIONMANAGERSERVICE_WSDL_LOCATION = cl.getResource("brw-2impl.wsdl");
        } else {
            PAUSABLESUBSCRIPTIONMANAGERSERVICE_WSDL_LOCATION = PausableSubscriptionManagerService.class.getClassLoader().getResource("brw-2impl.wsdl");
        }
    }

    public PausableSubscriptionManagerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public PausableSubscriptionManagerService() {
        super(PAUSABLESUBSCRIPTIONMANAGERSERVICE_WSDL_LOCATION, new QName("http://docs.oasis-open.org/wsn/brw-2", "PausableSubscriptionManagerService"));
    }

    /**
     * 
     * @return
     *     returns PausableSubscriptionManager
     */
    @WebEndpoint(name = "PausableSubscriptionManagerPort")
    public PausableSubscriptionManager getPausableSubscriptionManagerPort() {
        return super.getPort(new QName("http://docs.oasis-open.org/wsn/brw-2", "PausableSubscriptionManagerPort"), PausableSubscriptionManager.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns PausableSubscriptionManager
     */
    @WebEndpoint(name = "PausableSubscriptionManagerPort")
    public PausableSubscriptionManager getPausableSubscriptionManagerPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/wsn/brw-2", "PausableSubscriptionManagerPort"), PausableSubscriptionManager.class, features);
    }

}
