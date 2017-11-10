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
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wsn.b_2.CreatePullPointResponse;
import org.oasis_open.docs.wsn.brw_2.UnableToCreatePullPointFault;

/**
 *
 * @author Administrator
 */

@WebService(name = "CreatePullPoint", targetNamespace = "http://docs.oasis-open.org/wsn/brw-2", serviceName = "CreatePullPointService", portName = "CreatePullPointBindingPort"
	,wsdlLocation = "brw-2impl.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    org.oasis_open.docs.wsrf.r_2.ObjectFactory.class,
    org.oasis_open.docs.wsrf.bf_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.t_1.ObjectFactory.class,
    org.oasis_open.docs.wsn.b_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.br_2.ObjectFactory.class
})
public class CreatePullPointService implements
        org.oasis_open.docs.wsn.brw_2.CreatePullPoint {

    private DatatypeFactory df = null;
    @Resource
    private WebServiceContext ctx;
    final static Logger log = Logger.getLogger("WS-NotificationBroker");

    /**
     *
     * @param createPullPointRequest
     * @return returns org.oasis_open.docs.wsn.b_2.CreatePullPointResponse
     * @throws UnableToCreatePullPointFault
     */
    @WebMethod(operationName = "CreatePullPoint", action = "CreatePullPoint")
    @WebResult(name = "CreatePullPointResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "CreatePullPointResponse")
    public CreatePullPointResponse createPullPoint(
            @WebParam(name = "CreatePullPoint", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "CreatePullPointRequest") org.oasis_open.docs.wsn.b_2.CreatePullPoint createPullPointRequest)
            throws UnableToCreatePullPointFault {
        CreatePullPointResponse res = new CreatePullPointResponse();


        SingletonBroker.getInstance();
        W3CEndpointReferenceBuilder b = new W3CEndpointReferenceBuilder();
        //TODO replace with user principle
        String AddSubscription = SingletonBroker.CreateMailBox(createPullPointRequest, "anonymous");
        b.address(AddSubscription);
        res.setPullPoint(b.build());
        return res;

    }
}
