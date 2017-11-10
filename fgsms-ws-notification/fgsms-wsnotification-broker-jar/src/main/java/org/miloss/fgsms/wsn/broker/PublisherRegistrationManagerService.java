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

package org.miloss.fgsms.wsn.broker;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wsn.br_2.DestroyRegistration;
import org.oasis_open.docs.wsn.br_2.DestroyRegistrationResponse;
import org.oasis_open.docs.wsn.brw_2.ResourceNotDestroyedFault;
import org.oasis_open.docs.wsn.brw_2.ResourceUnknownFault;

/**
 *
 * @author Administrator
 */
@WebService(name = "PublisherRegistrationManager", targetNamespace = "http://docs.oasis-open.org/wsn/brw-2", serviceName = "PublisherRegistrationManagerService", portName = "PublisherRegistrationManagerPort"
,wsdlLocation = "brw-2impl.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    org.oasis_open.docs.wsrf.r_2.ObjectFactory.class,
    org.oasis_open.docs.wsrf.bf_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.t_1.ObjectFactory.class,
    org.oasis_open.docs.wsn.b_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.br_2.ObjectFactory.class
})
public class PublisherRegistrationManagerService implements org.oasis_open.docs.wsn.brw_2.PublisherRegistrationManager{
    
      private DatatypeFactory df = null;
    @Resource
    private WebServiceContext ctx;
    final static Logger log = Logger.getLogger("WS-NotificationBroker");
    /**
     * 
     * @param destroyRegistrationRequest
     * @return
     *     returns org.oasis_open.docs.wsn.br_2.DestroyRegistrationResponse
     * @throws ResourceNotDestroyedFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "DestroyRegistration", action = "DestroyRegistration")
    @WebResult(name = "DestroyRegistrationResponse", targetNamespace = "http://docs.oasis-open.org/wsn/br-2", partName = "DestroyRegistrationResponse")
    public DestroyRegistrationResponse destroyRegistration(
        @WebParam(name = "DestroyRegistration", targetNamespace = "http://docs.oasis-open.org/wsn/br-2", partName = "DestroyRegistrationRequest")
        DestroyRegistration destroyRegistrationRequest)
        throws ResourceNotDestroyedFault, ResourceUnknownFault
    {return null;}

}
