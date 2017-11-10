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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "HelloWorldPort", targetNamespace = "http://new.webservice.namespace")
public interface HelloWorldPort {


    /**
     * 
     * @param helloWorld
     * @return
     *     returns namespace.webservice._new.HelloWorldResponse
     */
    @WebMethod(operationName = "SayHelloWorld", action = "urn:#SayHelloWorld")
    @WebResult(name = "HelloWorldResponse", targetNamespace = "http://new.webservice.namespace")
    @RequestWrapper(localName = "SayHelloWorld", targetNamespace = "http://new.webservice.namespace", className = "namespace.webservice._new.SayHelloWorld")
    @ResponseWrapper(localName = "SayHelloWorldResponse", targetNamespace = "http://new.webservice.namespace", className = "namespace.webservice._new.SayHelloWorldResponse")
    public HelloWorldResponse sayHelloWorld(
        @WebParam(name = "HelloWorld", targetNamespace = "http://new.webservice.namespace")
        HelloWorld helloWorld);

}
