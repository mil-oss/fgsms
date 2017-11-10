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
import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.brw_2.*;

/**
 *
 * @author Administrator
 */
@WebService(name = "PausableSubscriptionManager", targetNamespace = "http://docs.oasis-open.org/wsn/brw-2", serviceName = "PausableSubscriptionManagerService", portName = "PausableSubscriptionManagerPort"
,wsdlLocation = "brw-2impl.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    org.oasis_open.docs.wsrf.r_2.ObjectFactory.class,
    org.oasis_open.docs.wsrf.bf_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.t_1.ObjectFactory.class,
    org.oasis_open.docs.wsn.b_2.ObjectFactory.class,
    org.oasis_open.docs.wsn.br_2.ObjectFactory.class
})
public class PausableSubscriptionManagerService implements org.oasis_open.docs.wsn.brw_2.PausableSubscriptionManager {

    private DatatypeFactory df = null;
    @Resource
    private WebServiceContext ctx;
    final static Logger log = Logger.getLogger("WS-NotificationBroker");

    /**
     *
     * @param renewRequest
     * @return returns org.oasis_open.docs.wsn.b_2.RenewResponse
     * @throws UnacceptableTerminationTimeFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "Renew", action = "Renew")
    @WebResult(name = "RenewResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "RenewResponse")
    public RenewResponse renew(
            @WebParam(name = "Renew", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "RenewRequest") Renew renewRequest)
            throws ResourceUnknownFault, UnacceptableTerminationTimeFault {
        return null;
    }

    /**
     *
     * @param unsubscribeRequest
     * @return returns org.oasis_open.docs.wsn.b_2.UnsubscribeResponse
     * @throws UnableToDestroySubscriptionFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "Unsubscribe", action = "Unsubscribe")
    @WebResult(name = "UnsubscribeResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "UnsubscribeResponse")
    public UnsubscribeResponse unsubscribe(
            @WebParam(name = "Unsubscribe", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "UnsubscribeRequest") Unsubscribe unsubscribeRequest)
            throws ResourceUnknownFault, UnableToDestroySubscriptionFault {
        SingletonBroker.getInstance();
        SingletonBroker.RemoveSubscription(unsubscribeRequest.getAny());
        UnsubscribeResponse res = new UnsubscribeResponse();
        return res;
    }

    /**
     *
     * @param pauseSubscriptionRequest
     * @return returns org.oasis_open.docs.wsn.b_2.PauseSubscriptionResponse
     * @throws PauseFailedFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "PauseSubscription", action = "PauseSubscription")
    @WebResult(name = "PauseSubscriptionResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "PauseSubscriptionResponse")
    public PauseSubscriptionResponse pauseSubscription(
            @WebParam(name = "PauseSubscription", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "PauseSubscriptionRequest") PauseSubscription pauseSubscriptionRequest)
            throws PauseFailedFault, ResourceUnknownFault {
        SingletonBroker.getInstance();
        SingletonBroker.PauseSubscription(pauseSubscriptionRequest);
        PauseSubscriptionResponse res = new PauseSubscriptionResponse();
        return res;
    }

    /**
     *
     * @param resumeSubscriptionRequest
     * @return returns org.oasis_open.docs.wsn.b_2.ResumeSubscriptionResponse
     * @throws ResumeFailedFault
     * @throws ResourceUnknownFault
     */
    @WebMethod(operationName = "ResumeSubscription", action = "ResumeSubscription")
    @WebResult(name = "ResumeSubscriptionResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "ResumeSubscriptionResponse")
    public ResumeSubscriptionResponse resumeSubscription(
            @WebParam(name = "ResumeSubscription", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "ResumeSubscriptionRequest") ResumeSubscription resumeSubscriptionRequest)
            throws ResourceUnknownFault, ResumeFailedFault {
           SingletonBroker.getInstance();
        SingletonBroker.ResumeSubscription(resumeSubscriptionRequest);
        ResumeSubscriptionResponse res = new ResumeSubscriptionResponse();
        return res;
    }
}
