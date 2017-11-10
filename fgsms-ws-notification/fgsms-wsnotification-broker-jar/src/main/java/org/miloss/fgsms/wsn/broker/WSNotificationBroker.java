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


import java.util.GregorianCalendar;
import javax.annotation.Resource;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wsn.b_2.GetCurrentMessage;
import org.oasis_open.docs.wsn.b_2.GetCurrentMessageResponse;
import org.oasis_open.docs.wsn.b_2.InvalidFilterFaultType;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.b_2.Subscribe;
import org.oasis_open.docs.wsn.b_2.SubscribeCreationFailedFaultType;
import org.oasis_open.docs.wsn.b_2.SubscribeResponse;
import org.oasis_open.docs.wsn.b_2.TopicExpressionType;
import org.oasis_open.docs.wsn.br_2.RegisterPublisher;
import org.oasis_open.docs.wsn.br_2.RegisterPublisherResponse;
import org.oasis_open.docs.wsn.brw_2.*;
import org.miloss.fgsms.wsn.WSNConstants;




/**
 *
 * @author Administrator
 */
							    
@WebService(name = "NotificationBroker", targetNamespace = "http://docs.oasis-open.org/wsn/brw-2", serviceName = "NotificationService", portName = "NotificationPort"
,wsdlLocation = "brw-2impl.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class WSNotificationBroker implements org.oasis_open.docs.wsn.brw_2.NotificationBroker
{

   
    private DatatypeFactory df = null;
    @Resource
    private WebServiceContext ctx;
    final static Logger log = Logger.getLogger("WS-NotificationBroker");

    public WSNotificationBroker() throws DatatypeConfigurationException {
        df = DatatypeFactory.newInstance();
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
        return null;
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
        //authorize client
        //validate request
        if (subscribeRequest.getFilter() == null || subscribeRequest.getFilter().getAny().isEmpty()) {
            InvalidFilterFault iff = new InvalidFilterFault("at least one filter of WS-Topic must be specified", new InvalidFilterFaultType());
            throw iff;
        }
        for (int i = 0; i < subscribeRequest.getFilter().getAny().size(); i++) {

            if (!(subscribeRequest.getFilter().getAny().get(i) instanceof JAXBElement)) {

                InvalidFilterFault iff = new InvalidFilterFault("All filters must be of type " + WSNConstants.WST_TOPICEXPRESSION_SIMPLE, new InvalidFilterFaultType());
                throw iff;
            } else {
                JAXBElement<TopicExpressionType> item = (JAXBElement<TopicExpressionType>) subscribeRequest.getFilter().getAny().get(i);
                org.oasis_open.docs.wsn.b_2.TopicExpressionType te = item.getValue();
                if (!te.getDialect().equalsIgnoreCase(WSNConstants.WST_TOPICEXPRESSION_SIMPLE)) {
                    InvalidFilterFault iff = new InvalidFilterFault("Only TopicExpressions with the dialect of " + WSNConstants.WST_TOPICEXPRESSION_SIMPLE + " are supported.", new InvalidFilterFaultType());
                    throw iff;
                }
            }
        }
        W3CEndpointReferenceBuilder b = new W3CEndpointReferenceBuilder();
        if (subscribeRequest.getConsumerReference() == null) {
            SubscribeCreationFailedFault iff = new SubscribeCreationFailedFault("Invalid callback address.", new SubscribeCreationFailedFaultType());
            throw iff;
        }


        SingletonBroker.getInstance();
        String AddSubscription = SingletonBroker.AddSubscription(subscribeRequest);
        SubscribeResponse res = new SubscribeResponse();

        b.address("SubscriptionId:" + AddSubscription);
        res.setSubscriptionReference(b.build());
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        res.setCurrentTime(df.newXMLGregorianCalendar(gcal));
        return res;
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
        //not sure what this is used for. i can only see it being using for pull points
        
        return null;
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

    
    
    
    
    
    //TODO modify the WSN spec to include,,,,
    //we really need a GetMySubscriptions if supporting pause, resume and renew

    
    


}
