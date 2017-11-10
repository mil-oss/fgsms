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

package org.oasis_open.docs.wsdm.mows_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.oasis_open.docs.wsdm.muws2_2.StateTransitionType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oasis_open.docs.wsdm.mows_2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LastOperationalStateTransition_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "LastOperationalStateTransition");
    private final static QName _LastResponseSize_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "LastResponseSize");
    private final static QName _OperationOperationalStatus_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "OperationOperationalStatus");
    private final static QName _GetManageabilityReferences_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "GetManageabilityReferences");
    private final static QName _MaxResponseSize_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "MaxResponseSize");
    private final static QName _IPV4Address_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "IPV4Address");
    private final static QName _LastRequestSize_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "LastRequestSize");
    private final static QName _MaxRequestSize_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "MaxRequestSize");
    private final static QName _CurrentOperationalState_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "CurrentOperationalState");
    private final static QName _OperationMetrics_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "OperationMetrics");
    private final static QName _IPV6Address_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "IPV6Address");
    private final static QName _MaxResponseTime_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "MaxResponseTime");
    private final static QName _EndpointReference_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "EndpointReference");
    private final static QName _LastResponseTime_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "LastResponseTime");
    private final static QName _ServiceTime_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "ServiceTime");
    private final static QName _NumberOfSuccessfulRequests_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "NumberOfSuccessfulRequests");
    private final static QName _NumberOfFailedRequests_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "NumberOfFailedRequests");
    private final static QName _NumberOfRequests_QNAME = new QName("http://docs.oasis-open.org/wsdm/mows-2.xsd", "NumberOfRequests");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.wsdm.mows_2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RequestProcessingNotification }
     * 
     */
    public RequestProcessingNotification createRequestProcessingNotification() {
        return new RequestProcessingNotification();
    }

    /**
     * Create an instance of {@link RequestProcessingState }
     * 
     */
    public RequestProcessingState createRequestProcessingState() {
        return new RequestProcessingState();
    }

    /**
     * Create an instance of {@link AnyXmlContentsType }
     * 
     */
    public AnyXmlContentsType createAnyXmlContentsType() {
        return new AnyXmlContentsType();
    }

    /**
     * Create an instance of {@link RequestReceivedState }
     * 
     */
    public RequestReceivedState createRequestReceivedState() {
        return new RequestReceivedState();
    }

    /**
     * Create an instance of {@link IntegerCounter }
     * 
     */
    public IntegerCounter createIntegerCounter() {
        return new IntegerCounter();
    }

    /**
     * Create an instance of {@link MessageContentType }
     * 
     */
    public MessageContentType createMessageContentType() {
        return new MessageContentType();
    }

    /**
     * Create an instance of {@link DownState }
     * 
     */
    public DownState createDownState() {
        return new DownState();
    }

    /**
     * Create an instance of {@link RequestProcessingStateType }
     * 
     */
    public RequestProcessingStateType createRequestProcessingStateType() {
        return new RequestProcessingStateType();
    }

    /**
     * Create an instance of {@link GetManageabilityReferencesResponse }
     * 
     */
    public GetManageabilityReferencesResponse createGetManageabilityReferencesResponse() {
        return new GetManageabilityReferencesResponse();
    }

    /**
     * Create an instance of {@link SaturatedState }
     * 
     */
    public SaturatedState createSaturatedState() {
        return new SaturatedState();
    }

    /**
     * Create an instance of {@link OperationalStateType }
     * 
     */
    public OperationalStateType createOperationalStateType() {
        return new OperationalStateType();
    }

    /**
     * Create an instance of {@link EndpointDescriptions }
     * 
     */
    public EndpointDescriptions createEndpointDescriptions() {
        return new EndpointDescriptions();
    }

    /**
     * Create an instance of {@link MessageInformationType }
     * 
     */
    public MessageInformationType createMessageInformationType() {
        return new MessageInformationType();
    }

    /**
     * Create an instance of {@link MessageContentSizeType }
     * 
     */
    public MessageContentSizeType createMessageContentSizeType() {
        return new MessageContentSizeType();
    }

    /**
     * Create an instance of {@link UpState }
     * 
     */
    public UpState createUpState() {
        return new UpState();
    }

    /**
     * Create an instance of {@link IdleState }
     * 
     */
    public IdleState createIdleState() {
        return new IdleState();
    }

    /**
     * Create an instance of {@link OperationDurationMetric }
     * 
     */
    public OperationDurationMetric createOperationDurationMetric() {
        return new OperationDurationMetric();
    }

    /**
     * Create an instance of {@link BusyState }
     * 
     */
    public BusyState createBusyState() {
        return new BusyState();
    }

    /**
     * Create an instance of {@link RequestFailedState }
     * 
     */
    public RequestFailedState createRequestFailedState() {
        return new RequestFailedState();
    }

    /**
     * Create an instance of {@link RequestProcessingStateInformationType }
     * 
     */
    public RequestProcessingStateInformationType createRequestProcessingStateInformationType() {
        return new RequestProcessingStateInformationType();
    }

    /**
     * Create an instance of {@link OperationOperationalStatusType }
     * 
     */
    public OperationOperationalStatusType createOperationOperationalStatusType() {
        return new OperationOperationalStatusType();
    }

    /**
     * Create an instance of {@link RequestCompletedState }
     * 
     */
    public RequestCompletedState createRequestCompletedState() {
        return new RequestCompletedState();
    }

    /**
     * Create an instance of {@link CrashedState }
     * 
     */
    public CrashedState createCrashedState() {
        return new CrashedState();
    }

    /**
     * Create an instance of {@link TcpIpInfo }
     * 
     */
    public TcpIpInfo createTcpIpInfo() {
        return new TcpIpInfo();
    }

    /**
     * Create an instance of {@link DurationMetric }
     * 
     */
    public DurationMetric createDurationMetric() {
        return new DurationMetric();
    }

    /**
     * Create an instance of {@link OperationIntegerCounter }
     * 
     */
    public OperationIntegerCounter createOperationIntegerCounter() {
        return new OperationIntegerCounter();
    }

    /**
     * Create an instance of {@link MessageContentNotIncludedFlag }
     * 
     */
    public MessageContentNotIncludedFlag createMessageContentNotIncludedFlag() {
        return new MessageContentNotIncludedFlag();
    }

    /**
     * Create an instance of {@link StoppedState }
     * 
     */
    public StoppedState createStoppedState() {
        return new StoppedState();
    }

    /**
     * Create an instance of {@link OperationMetricType }
     * 
     */
    public OperationMetricType createOperationMetricType() {
        return new OperationMetricType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StateTransitionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "LastOperationalStateTransition")
    public JAXBElement<StateTransitionType> createLastOperationalStateTransition(StateTransitionType value) {
        return new JAXBElement<StateTransitionType>(_LastOperationalStateTransition_QNAME, StateTransitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegerCounter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "LastResponseSize")
    public JAXBElement<IntegerCounter> createLastResponseSize(IntegerCounter value) {
        return new JAXBElement<IntegerCounter>(_LastResponseSize_QNAME, IntegerCounter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationOperationalStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "OperationOperationalStatus")
    public JAXBElement<OperationOperationalStatusType> createOperationOperationalStatus(OperationOperationalStatusType value) {
        return new JAXBElement<OperationOperationalStatusType>(_OperationOperationalStatus_QNAME, OperationOperationalStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "GetManageabilityReferences")
    public JAXBElement<Object> createGetManageabilityReferences(Object value) {
        return new JAXBElement<Object>(_GetManageabilityReferences_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegerCounter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "MaxResponseSize")
    public JAXBElement<IntegerCounter> createMaxResponseSize(IntegerCounter value) {
        return new JAXBElement<IntegerCounter>(_MaxResponseSize_QNAME, IntegerCounter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "IPV4Address")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    public JAXBElement<byte[]> createIPV4Address(byte[] value) {
        return new JAXBElement<byte[]>(_IPV4Address_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegerCounter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "LastRequestSize")
    public JAXBElement<IntegerCounter> createLastRequestSize(IntegerCounter value) {
        return new JAXBElement<IntegerCounter>(_LastRequestSize_QNAME, IntegerCounter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegerCounter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "MaxRequestSize")
    public JAXBElement<IntegerCounter> createMaxRequestSize(IntegerCounter value) {
        return new JAXBElement<IntegerCounter>(_MaxRequestSize_QNAME, IntegerCounter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationalStateType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "CurrentOperationalState")
    public JAXBElement<OperationalStateType> createCurrentOperationalState(OperationalStateType value) {
        return new JAXBElement<OperationalStateType>(_CurrentOperationalState_QNAME, OperationalStateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OperationMetricType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "OperationMetrics")
    public JAXBElement<OperationMetricType> createOperationMetrics(OperationMetricType value) {
        return new JAXBElement<OperationMetricType>(_OperationMetrics_QNAME, OperationMetricType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "IPV6Address")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    public JAXBElement<byte[]> createIPV6Address(byte[] value) {
        return new JAXBElement<byte[]>(_IPV6Address_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DurationMetric }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "MaxResponseTime")
    public JAXBElement<DurationMetric> createMaxResponseTime(DurationMetric value) {
        return new JAXBElement<DurationMetric>(_MaxResponseTime_QNAME, DurationMetric.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link W3CEndpointReference }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "EndpointReference")
    public JAXBElement<W3CEndpointReference> createEndpointReference(W3CEndpointReference value) {
        return new JAXBElement<W3CEndpointReference>(_EndpointReference_QNAME, W3CEndpointReference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DurationMetric }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "LastResponseTime")
    public JAXBElement<DurationMetric> createLastResponseTime(DurationMetric value) {
        return new JAXBElement<DurationMetric>(_LastResponseTime_QNAME, DurationMetric.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DurationMetric }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "ServiceTime")
    public JAXBElement<DurationMetric> createServiceTime(DurationMetric value) {
        return new JAXBElement<DurationMetric>(_ServiceTime_QNAME, DurationMetric.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegerCounter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "NumberOfSuccessfulRequests")
    public JAXBElement<IntegerCounter> createNumberOfSuccessfulRequests(IntegerCounter value) {
        return new JAXBElement<IntegerCounter>(_NumberOfSuccessfulRequests_QNAME, IntegerCounter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegerCounter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "NumberOfFailedRequests")
    public JAXBElement<IntegerCounter> createNumberOfFailedRequests(IntegerCounter value) {
        return new JAXBElement<IntegerCounter>(_NumberOfFailedRequests_QNAME, IntegerCounter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegerCounter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/mows-2.xsd", name = "NumberOfRequests")
    public JAXBElement<IntegerCounter> createNumberOfRequests(IntegerCounter value) {
        return new JAXBElement<IntegerCounter>(_NumberOfRequests_QNAME, IntegerCounter.class, null, value);
    }

}
