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

package org.oasis_open.docs.wsdm.muws2_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import java.util.Calendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oasis_open.docs.wsdm.muws2_2 package. 
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

    private final static QName _Version_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Version");
    private final static QName _EnteredState_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "EnteredState");
    private final static QName _ChangeType_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "ChangeType");
    private final static QName _MetricGroup_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "MetricGroup");
    private final static QName _ValidWhile_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "ValidWhile");
    private final static QName _Capability_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Capability");
    private final static QName _Relationship_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Relationship");
    private final static QName _Participant_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Participant");
    private final static QName _Situation_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Situation");
    private final static QName _PostCondition_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "PostCondition");
    private final static QName _Units_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Units");
    private final static QName _CalculationInterval_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "CalculationInterval");
    private final static QName _Caption_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Caption");
    private final static QName _GatheringTime_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "GatheringTime");
    private final static QName _PreviousState_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "PreviousState");
    private final static QName _Type_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Type");
    private final static QName _StateTransition_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "StateTransition");
    private final static QName _Name_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Name");
    private final static QName _CurrentTime_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "CurrentTime");
    private final static QName _EventCorrelationProperties_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "EventCorrelationProperties");
    private final static QName _State_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "State");
    private final static QName _MsgCatalogInformation_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "MsgCatalogInformation");
    private final static QName _OperationalStatus_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "OperationalStatus");
    private final static QName _Description_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "Description");
    private final static QName _TimeScope_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws2-2.xsd", "TimeScope");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.wsdm.muws2_2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReportSituation }
     * 
     */
    public ReportSituation createReportSituation() {
        return new ReportSituation();
    }

    /**
     * Create an instance of {@link PauseInitiated }
     * 
     */
    public PauseInitiated createPauseInitiated() {
        return new PauseInitiated();
    }

    /**
     * Create an instance of {@link LogReport }
     * 
     */
    public LogReport createLogReport() {
        return new LogReport();
    }

    /**
     * Create an instance of {@link CreationNotification }
     * 
     */
    public CreationNotification createCreationNotification() {
        return new CreationNotification();
    }

    /**
     * Create an instance of {@link StateType }
     * 
     */
    public StateType createStateType() {
        return new StateType();
    }

    /**
     * Create an instance of {@link TraceReport }
     * 
     */
    public TraceReport createTraceReport() {
        return new TraceReport();
    }

    /**
     * Create an instance of {@link StartInitiated }
     * 
     */
    public StartInitiated createStartInitiated() {
        return new StartInitiated();
    }

    /**
     * Create an instance of {@link CreateSituation }
     * 
     */
    public CreateSituation createCreateSituation() {
        return new CreateSituation();
    }

    /**
     * Create an instance of {@link ConnectInitiated }
     * 
     */
    public ConnectInitiated createConnectInitiated() {
        return new ConnectInitiated();
    }

    /**
     * Create an instance of {@link RequestInitiated }
     * 
     */
    public RequestInitiated createRequestInitiated() {
        return new RequestInitiated();
    }

    /**
     * Create an instance of {@link OtherSituation }
     * 
     */
    public OtherSituation createOtherSituation() {
        return new OtherSituation();
    }

    /**
     * Create an instance of {@link StopSituation }
     * 
     */
    public StopSituation createStopSituation() {
        return new StopSituation();
    }

    /**
     * Create an instance of {@link RelationshipCreatedNotification }
     * 
     */
    public RelationshipCreatedNotification createRelationshipCreatedNotification() {
        return new RelationshipCreatedNotification();
    }

    /**
     * Create an instance of {@link RelationshipType }
     * 
     */
    public RelationshipType createRelationshipType() {
        return new RelationshipType();
    }

    /**
     * Create an instance of {@link EventCorrelationPropertiesType }
     * 
     */
    public EventCorrelationPropertiesType createEventCorrelationPropertiesType() {
        return new EventCorrelationPropertiesType();
    }

    /**
     * Create an instance of {@link ConfigureSituation }
     * 
     */
    public ConfigureSituation createConfigureSituation() {
        return new ConfigureSituation();
    }

    /**
     * Create an instance of {@link StartCompleted }
     * 
     */
    public StartCompleted createStartCompleted() {
        return new StartCompleted();
    }

    /**
     * Create an instance of {@link SituationCategoryType }
     * 
     */
    public SituationCategoryType createSituationCategoryType() {
        return new SituationCategoryType();
    }

    /**
     * Create an instance of {@link MsgCatalogInformationType }
     * 
     */
    public MsgCatalogInformationType createMsgCatalogInformationType() {
        return new MsgCatalogInformationType();
    }

    /**
     * Create an instance of {@link StopInitiated }
     * 
     */
    public StopInitiated createStopInitiated() {
        return new StopInitiated();
    }

    /**
     * Create an instance of {@link AbortInitiated }
     * 
     */
    public AbortInitiated createAbortInitiated() {
        return new AbortInitiated();
    }

    /**
     * Create an instance of {@link ConnectCompleted }
     * 
     */
    public ConnectCompleted createConnectCompleted() {
        return new ConnectCompleted();
    }

    /**
     * Create an instance of {@link QueryRelationshipsByType }
     * 
     */
    public QueryRelationshipsByType createQueryRelationshipsByType() {
        return new QueryRelationshipsByType();
    }

    /**
     * Create an instance of {@link RestartInitiated }
     * 
     */
    public RestartInitiated createRestartInitiated() {
        return new RestartInitiated();
    }

    /**
     * Create an instance of {@link SubstitutableMsgType }
     * 
     */
    public SubstitutableMsgType createSubstitutableMsgType() {
        return new SubstitutableMsgType();
    }

    /**
     * Create an instance of {@link DestructionNotification }
     * 
     */
    public DestructionNotification createDestructionNotification() {
        return new DestructionNotification();
    }

    /**
     * Create an instance of {@link DialectableExpressionType }
     * 
     */
    public DialectableExpressionType createDialectableExpressionType() {
        return new DialectableExpressionType();
    }

    /**
     * Create an instance of {@link DebugReport }
     * 
     */
    public DebugReport createDebugReport() {
        return new DebugReport();
    }

    /**
     * Create an instance of {@link ConnectSituation }
     * 
     */
    public ConnectSituation createConnectSituation() {
        return new ConnectSituation();
    }

    /**
     * Create an instance of {@link DestroyInitiated }
     * 
     */
    public DestroyInitiated createDestroyInitiated() {
        return new DestroyInitiated();
    }

    /**
     * Create an instance of {@link StartSituation }
     * 
     */
    public StartSituation createStartSituation() {
        return new StartSituation();
    }

    /**
     * Create an instance of {@link AvailabilitySituation }
     * 
     */
    public AvailabilitySituation createAvailabilitySituation() {
        return new AvailabilitySituation();
    }

    /**
     * Create an instance of {@link RequestCompleted }
     * 
     */
    public RequestCompleted createRequestCompleted() {
        return new RequestCompleted();
    }

    /**
     * Create an instance of {@link StateTransitionType }
     * 
     */
    public StateTransitionType createStateTransitionType() {
        return new StateTransitionType();
    }

    /**
     * Create an instance of {@link DestroyCompleted }
     * 
     */
    public DestroyCompleted createDestroyCompleted() {
        return new DestroyCompleted();
    }

    /**
     * Create an instance of {@link CategoryType }
     * 
     */
    public CategoryType createCategoryType() {
        return new CategoryType();
    }

    /**
     * Create an instance of {@link SecurityReport }
     * 
     */
    public SecurityReport createSecurityReport() {
        return new SecurityReport();
    }

    /**
     * Create an instance of {@link HeartbeatReport }
     * 
     */
    public HeartbeatReport createHeartbeatReport() {
        return new HeartbeatReport();
    }

    /**
     * Create an instance of {@link SituationType }
     * 
     */
    public SituationType createSituationType() {
        return new SituationType();
    }

    /**
     * Create an instance of {@link CreateInitiated }
     * 
     */
    public CreateInitiated createCreateInitiated() {
        return new CreateInitiated();
    }

    /**
     * Create an instance of {@link RelationshipTypeType }
     * 
     */
    public RelationshipTypeType createRelationshipTypeType() {
        return new RelationshipTypeType();
    }

    /**
     * Create an instance of {@link RelationshipDeletedNotification }
     * 
     */
    public RelationshipDeletedNotification createRelationshipDeletedNotification() {
        return new RelationshipDeletedNotification();
    }

    /**
     * Create an instance of {@link Self }
     * 
     */
    public Self createSelf() {
        return new Self();
    }

    /**
     * Create an instance of {@link LangString }
     * 
     */
    public LangString createLangString() {
        return new LangString();
    }

    /**
     * Create an instance of {@link StatusReport }
     * 
     */
    public StatusReport createStatusReport() {
        return new StatusReport();
    }

    /**
     * Create an instance of {@link RelationshipParticipantType }
     * 
     */
    public RelationshipParticipantType createRelationshipParticipantType() {
        return new RelationshipParticipantType();
    }

    /**
     * Create an instance of {@link CapabilitySituation }
     * 
     */
    public CapabilitySituation createCapabilitySituation() {
        return new CapabilitySituation();
    }

    /**
     * Create an instance of {@link StopCompleted }
     * 
     */
    public StopCompleted createStopCompleted() {
        return new StopCompleted();
    }

    /**
     * Create an instance of {@link PerformanceReport }
     * 
     */
    public PerformanceReport createPerformanceReport() {
        return new PerformanceReport();
    }

    /**
     * Create an instance of {@link RequestSituation }
     * 
     */
    public RequestSituation createRequestSituation() {
        return new RequestSituation();
    }

    /**
     * Create an instance of {@link ReconnectInitiated }
     * 
     */
    public ReconnectInitiated createReconnectInitiated() {
        return new ReconnectInitiated();
    }

    /**
     * Create an instance of {@link DestroySituation }
     * 
     */
    public DestroySituation createDestroySituation() {
        return new DestroySituation();
    }

    /**
     * Create an instance of {@link QueryRelationshipsByTypeResponse }
     * 
     */
    public QueryRelationshipsByTypeResponse createQueryRelationshipsByTypeResponse() {
        return new QueryRelationshipsByTypeResponse();
    }

    /**
     * Create an instance of {@link CreateCompleted }
     * 
     */
    public CreateCompleted createCreateCompleted() {
        return new CreateCompleted();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Version")
    public JAXBElement<String> createVersion(String value) {
        return new JAXBElement<String>(_Version_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StateType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "EnteredState")
    public JAXBElement<StateType> createEnteredState(StateType value) {
        return new JAXBElement<StateType>(_EnteredState_QNAME, StateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "ChangeType")
    public JAXBElement<String> createChangeType(String value) {
        return new JAXBElement<String>(_ChangeType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "MetricGroup")
    public JAXBElement<String> createMetricGroup(String value) {
        return new JAXBElement<String>(_MetricGroup_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DialectableExpressionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "ValidWhile")
    public JAXBElement<DialectableExpressionType> createValidWhile(DialectableExpressionType value) {
        return new JAXBElement<DialectableExpressionType>(_ValidWhile_QNAME, DialectableExpressionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Capability")
    public JAXBElement<String> createCapability(String value) {
        return new JAXBElement<String>(_Capability_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelationshipType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Relationship")
    public JAXBElement<RelationshipType> createRelationship(RelationshipType value) {
        return new JAXBElement<RelationshipType>(_Relationship_QNAME, RelationshipType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelationshipParticipantType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Participant")
    public JAXBElement<RelationshipParticipantType> createParticipant(RelationshipParticipantType value) {
        return new JAXBElement<RelationshipParticipantType>(_Participant_QNAME, RelationshipParticipantType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SituationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Situation")
    public JAXBElement<SituationType> createSituation(SituationType value) {
        return new JAXBElement<SituationType>(_Situation_QNAME, SituationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DialectableExpressionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "PostCondition")
    public JAXBElement<DialectableExpressionType> createPostCondition(DialectableExpressionType value) {
        return new JAXBElement<DialectableExpressionType>(_PostCondition_QNAME, DialectableExpressionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Units")
    public JAXBElement<String> createUnits(String value) {
        return new JAXBElement<String>(_Units_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "CalculationInterval")
    public JAXBElement<Duration> createCalculationInterval(Duration value) {
        return new JAXBElement<Duration>(_CalculationInterval_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LangString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Caption")
    public JAXBElement<LangString> createCaption(LangString value) {
        return new JAXBElement<LangString>(_Caption_QNAME, LangString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "GatheringTime")
    public JAXBElement<String> createGatheringTime(String value) {
        return new JAXBElement<String>(_GatheringTime_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StateType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "PreviousState")
    public JAXBElement<StateType> createPreviousState(StateType value) {
        return new JAXBElement<StateType>(_PreviousState_QNAME, StateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelationshipTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Type")
    public JAXBElement<RelationshipTypeType> createType(RelationshipTypeType value) {
        return new JAXBElement<RelationshipTypeType>(_Type_QNAME, RelationshipTypeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StateTransitionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "StateTransition")
    public JAXBElement<StateTransitionType> createStateTransition(StateTransitionType value) {
        return new JAXBElement<StateTransitionType>(_StateTransition_QNAME, StateTransitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Calendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "CurrentTime")
    public JAXBElement<Calendar> createCurrentTime(Calendar value) {
        return new JAXBElement<Calendar>(_CurrentTime_QNAME, Calendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventCorrelationPropertiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "EventCorrelationProperties")
    public JAXBElement<EventCorrelationPropertiesType> createEventCorrelationProperties(EventCorrelationPropertiesType value) {
        return new JAXBElement<EventCorrelationPropertiesType>(_EventCorrelationProperties_QNAME, EventCorrelationPropertiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StateType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "State")
    public JAXBElement<StateType> createState(StateType value) {
        return new JAXBElement<StateType>(_State_QNAME, StateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MsgCatalogInformationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "MsgCatalogInformation")
    public JAXBElement<MsgCatalogInformationType> createMsgCatalogInformation(MsgCatalogInformationType value) {
        return new JAXBElement<MsgCatalogInformationType>(_MsgCatalogInformation_QNAME, MsgCatalogInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "OperationalStatus")
    public JAXBElement<String> createOperationalStatus(String value) {
        return new JAXBElement<String>(_OperationalStatus_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LangString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "Description")
    public JAXBElement<LangString> createDescription(LangString value) {
        return new JAXBElement<LangString>(_Description_QNAME, LangString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws2-2.xsd", name = "TimeScope")
    public JAXBElement<String> createTimeScope(String value) {
        return new JAXBElement<String>(_TimeScope_QNAME, String.class, null, value);
    }

}
