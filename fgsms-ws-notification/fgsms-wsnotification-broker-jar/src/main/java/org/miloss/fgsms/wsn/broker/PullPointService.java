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
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.brw_2.ResourceUnknownFault;
import org.oasis_open.docs.wsn.brw_2.UnableToDestroyPullPointFault;
import org.oasis_open.docs.wsn.brw_2.UnableToGetMessagesFault;

/**
 *
 * @author Administrator
 *  {http://docs.oasis-open.org/wsn/brw-2}PullPointService
 */
@WebService(name = "PullPoint", targetNamespace = "http://docs.oasis-open.org/wsn/brw-2", serviceName = "PullPointService", portName = "PullPointBindingPort"
,wsdlLocation = "brw-2impl.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    org.oasis_open.docs.wsrf.r_2.ObjectFactory.class,
    org.oasis_open.docs.wsrf.bf_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.t_1.ObjectFactory.class,
    org.oasis_open.docs.wsn.b_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.br_2.ObjectFactory.class
})
public class PullPointService implements org.oasis_open.docs.wsn.brw_2.PullPoint {

      private DatatypeFactory df = null;
    @Resource
    private WebServiceContext ctx;
    final static Logger log = Logger.getLogger("WS-NotificationBroker");
    
    /**
     *
     * @param getMessagesRequest
     * @return returns org.oasis_open.docs.wsn.b_2.GetMessagesResponse
     * @throws UnableToGetMessagesFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "GetMessages", action = "GetMessages")
    @WebResult(name = "GetMessagesResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "GetMessagesResponse")
    public GetMessagesResponse getMessages(
            @WebParam(name = "GetMessages", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "GetMessagesRequest") GetMessages getMessagesRequest)
            throws ResourceUnknownFault, UnableToGetMessagesFault {
        SingletonBroker.getInstance();
        return SingletonBroker.GetMessages(getMessagesRequest);
        
    }

    /**
     *
     * @param destroyPullPointRequest
     * @return returns org.oasis_open.docs.wsn.b_2.DestroyPullPointResponse
     * @throws UnableToDestroyPullPointFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "DestroyPullPoint", action = "DestroyPullPoint")
    @WebResult(name = "DestroyPullPointResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "DestroyPullPointResponse")
    public DestroyPullPointResponse destroyPullPoint(
            @WebParam(name = "DestroyPullPoint", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "DestroyPullPointRequest") DestroyPullPoint destroyPullPointRequest)
            throws ResourceUnknownFault, UnableToDestroyPullPointFault {
        //authorize request
        SingletonBroker.getInstance();
        SingletonBroker.DestroyMailBox(destroyPullPointRequest.getAny());
        DestroyPullPointResponse res = new DestroyPullPointResponse();
        return res;
    }

    /**
     *
     * @param notify
     */
    @WebMethod(operationName = "Notify", action = "Notify")
    @Oneway
    public void notify(
            @WebParam(name = "Notify", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "Notify") Notify notify) {
        //authorize client

        //inbound notification
        SingletonBroker.getInstance();
        SingletonBroker.Dispatch(notify);
    }
}
