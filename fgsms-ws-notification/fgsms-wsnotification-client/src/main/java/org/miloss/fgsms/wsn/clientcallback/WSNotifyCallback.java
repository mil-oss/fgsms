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

package org.miloss.fgsms.wsn.clientcallback;



import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.DatatypeConfigurationException;
import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.br_2.*;
import org.oasis_open.docs.wsn.brw_2.*;

/**
 *
 * @author Administrator
 */
@WebService(name = "NotificationBroker", targetNamespace = "http://docs.oasis-open.org/wsn/bw-2")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class WSNotifyCallback implements org.oasis_open.docs.wsn.brw_2.NotificationBroker
{
    
    private IWSNCallBack callbackReference=null;
    public WSNotifyCallback(IWSNCallBack callbackDelegate) throws DatatypeConfigurationException {
        callbackReference = callbackDelegate;
    }
    
    
    /**
     * 
     * @param registerPublisherRequest
     * @return
     *     returns org.oasis_open.docs.wsn.br_2.RegisterPublisherResponse
     * @throws PublisherRegistrationFailedFault
     * @throws InvalidTopicExpressionFault
     * @throws ResourceUnknownFault
     * @throws TopicNotSupportedFault
     * @throws UnacceptableInitialTerminationTimeFault
     * @throws PublisherRegistrationRejectedFault
     */
    @WebMethod(operationName = "RegisterPublisher", action = "RegisterPublisher")
    @WebResult(name = "RegisterPublisherResponse", targetNamespace = "http://docs.oasis-open.org/wsn/br-2", partName = "RegisterPublisherResponse")
    public RegisterPublisherResponse registerPublisher(
        @WebParam(name = "RegisterPublisher", targetNamespace = "http://docs.oasis-open.org/wsn/br-2", partName = "RegisterPublisherRequest")
        RegisterPublisher registerPublisherRequest)
        throws InvalidTopicExpressionFault, PublisherRegistrationFailedFault, PublisherRegistrationRejectedFault, ResourceUnknownFault, TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 
     * @param subscribeRequest
     * @return
     *     returns org.oasis_open.docs.wsn.b_2.SubscribeResponse
     * @throws ResourceUnknownFault
     * @throws UnrecognizedPolicyRequestFault
     * @throws InvalidTopicExpressionFault
     * @throws InvalidFilterFault
     * @throws InvalidProducerPropertiesExpressionFault
     * @throws SubscribeCreationFailedFault
     * @throws NotifyMessageNotSupportedFault
     * @throws InvalidMessageContentExpressionFault
     * @throws TopicExpressionDialectUnknownFault
     * @throws UnsupportedPolicyRequestFault
     * @throws TopicNotSupportedFault
     * @throws UnacceptableInitialTerminationTimeFault
     */
    @WebMethod(operationName = "Subscribe", action = "Subscribe")
    @WebResult(name = "SubscribeResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "SubscribeResponse")
    public SubscribeResponse subscribe(
            @WebParam(name = "Subscribe", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "SubscribeRequest") Subscribe subscribeRequest)
            throws InvalidFilterFault, InvalidMessageContentExpressionFault, InvalidProducerPropertiesExpressionFault,
            InvalidTopicExpressionFault, NotifyMessageNotSupportedFault, ResourceUnknownFault, SubscribeCreationFailedFault,
            TopicExpressionDialectUnknownFault, TopicNotSupportedFault, UnacceptableInitialTerminationTimeFault,
            UnrecognizedPolicyRequestFault, UnsupportedPolicyRequestFault {
                throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 
     * @param getCurrentMessageRequest
     * @return
     *     returns org.oasis_open.docs.wsn.b_2.GetCurrentMessageResponse
     * @throws ResourceUnknownFault
     * @throws InvalidTopicExpressionFault
     * @throws MultipleTopicsSpecifiedFault
     * @throws TopicExpressionDialectUnknownFault
     * @throws TopicNotSupportedFault
     * @throws NoCurrentMessageOnTopicFault
     */
    @WebMethod(operationName = "GetCurrentMessage", action = "GetCurrentMessage")
    @WebResult(name = "GetCurrentMessageResponse", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "GetCurrentMessageResponse")
    public GetCurrentMessageResponse getCurrentMessage(
            @WebParam(name = "GetCurrentMessage", targetNamespace = "http://docs.oasis-open.org/wsn/b-2", partName = "GetCurrentMessageRequest") GetCurrentMessage getCurrentMessageRequest)
            throws InvalidTopicExpressionFault, MultipleTopicsSpecifiedFault, NoCurrentMessageOnTopicFault, ResourceUnknownFault, TopicExpressionDialectUnknownFault, TopicNotSupportedFault {
        throw new SecurityException("operaton not supported");
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
        if (callbackReference != null)
            callbackReference.OnMessage(notify);

    }

    
    
    
    
    
    //TODO modify the WSN spec to include,,,,
    //we really need a GetMySubscriptions if supporting pause, resume and renew

    
    


}
