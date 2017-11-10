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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "Hello_PortType", targetNamespace = "http://www.examples.com/wsdl/HelloService")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class HelloWorldImpl1 implements HelloPortType{


    /**
     * 
     * @param parameter
     * @return
     *     returns com.examples.wsdl.helloservice.SayHelloResponse
     */
    @WebMethod(action = "sayHello")
    @WebResult(name = "SayHelloResponse", targetNamespace = "http://www.examples.com/wsdl/HelloService", partName = "parameter")
    public SayHelloResponse sayHello(
        @WebParam(name = "SayHello", targetNamespace = "http://www.examples.com/wsdl/HelloService", partName = "parameter")
        SayHello parameter)
    {
        SayHelloResponse res = new SayHelloResponse();
        SayHelloRes msg = new SayHelloRes();
        msg.setGreeting("Hi from Number 1 " + parameter.getSayHelloReq().getYourname());
        res.setSayHelloRes(msg);
        return res;
    }

}