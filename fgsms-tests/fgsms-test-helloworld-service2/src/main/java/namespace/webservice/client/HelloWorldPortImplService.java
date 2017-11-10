/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package namespace.webservice.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "HelloWorldPortImplService", targetNamespace = "http://new.webservice.namespace", wsdlLocation = "http://localhost:8080/HelloWorldServiceNo1/HelloWorldPortImpl?wsdl")
public class HelloWorldPortImplService
    extends Service
{

    private final static URL HELLOWORLDPORTIMPLSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(namespace.webservice.client.HelloWorldPortImplService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = namespace.webservice.client.HelloWorldPortImplService.class.getResource(".");
            url = new URL(baseUrl, "http://localhost:8080/HelloWorldServiceNo1/HelloWorldPortImpl?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://localhost:8080/HelloWorldServiceNo1/HelloWorldPortImpl?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        HELLOWORLDPORTIMPLSERVICE_WSDL_LOCATION = url;
    }

    public HelloWorldPortImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public HelloWorldPortImplService() {
        super(HELLOWORLDPORTIMPLSERVICE_WSDL_LOCATION, new QName("http://new.webservice.namespace", "HelloWorldPortImplService"));
    }

    /**
     * 
     * @return
     *     returns HelloWorldPort
     */
    @WebEndpoint(name = "HelloWorldPortPort")
    public HelloWorldPort getHelloWorldPortPort() {
        return super.getPort(new QName("http://new.webservice.namespace", "HelloWorldPortPort"), HelloWorldPort.class);
    }

}
