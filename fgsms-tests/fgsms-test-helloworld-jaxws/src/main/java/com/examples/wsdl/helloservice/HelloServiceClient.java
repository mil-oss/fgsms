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

package com.examples.wsdl.helloservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * WSDL File for HelloServiceClient
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "Hello_Service", targetNamespace = "http://www.examples.com/wsdl/HelloService", wsdlLocation = "classpath:/hello.wsdl")
public class HelloServiceClient
    extends Service
{
public final static QName qname=new QName("http://www.examples.com/wsdl/HelloService", "Hello_Service");
    private final static URL HELLOSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.examples.wsdl.helloservice.HelloServiceClient.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.examples.wsdl.helloservice.HelloServiceClient.class.getResource(".");
            url = new URL(baseUrl, "hello.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'file:/c:/Projects/helloworld/hello.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        HELLOSERVICE_WSDL_LOCATION = url;
    }

    public HelloServiceClient(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public HelloServiceClient() {
        super(HELLOSERVICE_WSDL_LOCATION, new QName("http://www.examples.com/wsdl/HelloService", "Hello_Service"));
    }

    /**
     * 
     * @return
     *     returns HelloPortType
     */
    @WebEndpoint(name = "Hello_Port")
    public HelloPortType getHelloPort() {
        return super.getPort(new QName("http://www.examples.com/wsdl/HelloService", "Hello_Port"), HelloPortType.class);
    }

}
