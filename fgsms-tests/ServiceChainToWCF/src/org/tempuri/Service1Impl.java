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
package org.tempuri;

import java.net.URL;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 *
 * @author * 
 * U.S. Government, Department of the Army, AMC, RDECOM, CERDEC
 */

@WebService(name = "IService1", targetNamespace = "http://tempuri.org/")
public class Service1Impl implements IService1 {

    /**
     * 
     * @param value
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetData", action = "http://tempuri.org/IService1/GetData")
    @WebResult(name = "GetDataResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetData", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GetData")
    @ResponseWrapper(localName = "GetDataResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GetDataResponse")
    @Override
    public String getData(
            @WebParam(name = "value", targetNamespace = "http://tempuri.org/") Integer value) {
         URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/service.wsdl");

        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource("/META-INF/service.wsdl");
        }
         Service1 client = new Service1(url);
        return "hi from jaxws, downstream service says " + client.getBasicHttpBindingIService1().getData(value);
          }

    /**
     * 
     * @param composite
     * @return
     *     returns org.datacontract.schemas._2004._07.wcfservice1.CompositeType
     */
    @WebMethod(operationName = "GetDataUsingDataContract", action = "http://tempuri.org/IService1/GetDataUsingDataContract")
    @WebResult(name = "GetDataUsingDataContractResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetDataUsingDataContract", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GetDataUsingDataContract")
    @ResponseWrapper(localName = "GetDataUsingDataContractResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.GetDataUsingDataContractResponse")
    @Override
    public org.datacontract.schemas._2004._07.wcfservice1.CompositeType getDataUsingDataContract(
            @WebParam(name = "composite", targetNamespace = "http://tempuri.org/") org.datacontract.schemas._2004._07.wcfservice1.CompositeType composite) {
         URL url = Thread.currentThread().getContextClassLoader().getResource("META-INF/service.wsdl");

        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource("/META-INF/service.wsdl");
        }
         Service1 client = new Service1(url);
        composite =client.getBasicHttpBindingIService1().getDataUsingDataContract(composite);
        return composite;
    }
}
