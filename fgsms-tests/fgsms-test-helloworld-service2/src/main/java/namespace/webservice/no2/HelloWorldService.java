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
package namespace.webservice.no2;

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
@WebServiceClient(name = "HelloWorldService", targetNamespace = "http://new.webservice.namespace", wsdlLocation = "file:/D:/Projects/fgsms5/HelloWorld.wsdl")
public class HelloWorldService
    extends Service
{

    private final static URL HELLOWORLDSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(namespace.webservice.no2.HelloWorldService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = namespace.webservice.no2.HelloWorldService.class.getResource(".");
            url = new URL(baseUrl, "file:/D:/Projects/fgsms5/HelloWorld.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'file:/D:/Projects/fgsms5/HelloWorld.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        HELLOWORLDSERVICE_WSDL_LOCATION = url;
    }

    public HelloWorldService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public HelloWorldService() {
        super(HELLOWORLDSERVICE_WSDL_LOCATION, new QName("http://new.webservice.namespace", "HelloWorldService"));
    }

    HelloWorldService(URL uRL) {
          super(uRL, new QName("http://new.webservice.namespace", "HelloWorldService"));
    }

    /**
     * 
     * @return
     *     returns HelloWorldPort
     */
    @WebEndpoint(name = "HelloWorldPort")
    public HelloWorldPort getHelloWorldPort() {
        return super.getPort(new QName("http://new.webservice.namespace", "HelloWorldPort"), HelloWorldPort.class);
    }

}
