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
package org.oasis_open.docs.wsn.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import org.oasis_open.docs.wsn.brw_2.PullPoint;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.6 in JDK 6 Generated
 * source version: 2.1
 *
 */
@WebServiceClient(name = "PullPointService", targetNamespace = "http://docs.oasis-open.org/wsn/brw-2"
//wsdlLocation = "classpath:/rw-2impl.wsdl"
        )
public class PullPointService
        extends Service {

    private final static URL PULLPOINTSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(PullPointService.class.getName());

    static {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            PULLPOINTSERVICE_WSDL_LOCATION = cl.getResource("brw-2impl.wsdl");
        } else {
            PULLPOINTSERVICE_WSDL_LOCATION = PullPoint.class.getClassLoader().getResource("brw-2impl.wsdl");
        }
    }

    public PullPointService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public PullPointService() {
        super(PULLPOINTSERVICE_WSDL_LOCATION, new QName("http://docs.oasis-open.org/wsn/brw-2", "PullPointService"));
    }

    /**
     *
     * @return returns PullPoint
     */
    @WebEndpoint(name = "PullPointBindingPort")
    public PullPoint getPullPointBindingPort() {
        return super.getPort(new QName("http://docs.oasis-open.org/wsn/brw-2", "PullPointBindingPort"), PullPoint.class);
    }

    /**
     *
     * @param features A list of {@link javax.xml.ws.WebServiceFeature} to
     * configure on the proxy. Supported features not in
     * the <code>features</code> parameter will have their default values.
     * @return returns PullPoint
     */
    @WebEndpoint(name = "PullPointBindingPort")
    public PullPoint getPullPointBindingPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://docs.oasis-open.org/wsn/brw-2", "PullPointBindingPort"), PullPoint.class, features);
    }
}
