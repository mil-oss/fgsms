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
 
package org.miloss.fgsms.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.GregorianCalendar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Level;

import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.oasis_open.docs.wsdm.mows_2.AnyXmlContentsType;
import org.oasis_open.docs.wsdm.mows_2.BusyState;
import org.oasis_open.docs.wsdm.mows_2.CrashedState;
import org.oasis_open.docs.wsdm.mows_2.DownState;
import org.oasis_open.docs.wsdm.mows_2.DurationMetric;
import org.oasis_open.docs.wsdm.mows_2.EndpointDescriptions;
import org.oasis_open.docs.wsdm.mows_2.GetManageabilityReferencesResponse;
import org.oasis_open.docs.wsdm.mows_2.IdleState;
import org.oasis_open.docs.wsdm.mows_2.IntegerCounter;
import org.oasis_open.docs.wsdm.mows_2.MessageContentNotIncludedFlag;
import org.oasis_open.docs.wsdm.mows_2.MessageContentSizeType;
import org.oasis_open.docs.wsdm.mows_2.MessageContentType;
import org.oasis_open.docs.wsdm.mows_2.MessageInformationType;
import org.oasis_open.docs.wsdm.mows_2.MessageSizeUnitType;
import org.oasis_open.docs.wsdm.mows_2.OperationDurationMetric;
import org.oasis_open.docs.wsdm.mows_2.OperationIntegerCounter;
import org.oasis_open.docs.wsdm.mows_2.OperationMetricType;
import org.oasis_open.docs.wsdm.mows_2.OperationOperationalStatusType;
import org.oasis_open.docs.wsdm.mows_2.OperationalStateType;
import org.oasis_open.docs.wsdm.mows_2.RequestCompletedState;
import org.oasis_open.docs.wsdm.mows_2.RequestFailedState;
import org.oasis_open.docs.wsdm.mows_2.RequestProcessingNotification;
import org.oasis_open.docs.wsdm.mows_2.RequestProcessingState;
import org.oasis_open.docs.wsdm.mows_2.RequestProcessingStateInformationType;
import org.oasis_open.docs.wsdm.mows_2.RequestProcessingStateType;
import org.oasis_open.docs.wsdm.mows_2.RequestReceivedState;
import org.oasis_open.docs.wsdm.mows_2.SaturatedState;
import org.oasis_open.docs.wsdm.mows_2.StoppedState;
import org.oasis_open.docs.wsdm.mows_2.TcpIpDirectionType;
import org.oasis_open.docs.wsdm.mows_2.TcpIpInfo;
import org.oasis_open.docs.wsdm.mows_2.TcpIpProtocolType;
import org.oasis_open.docs.wsdm.mows_2.UpState;
import org.oasis_open.docs.wsdm.muws1_2.ComponentAddressType;
import org.oasis_open.docs.wsdm.muws1_2.ComponentType;
import org.oasis_open.docs.wsdm.muws1_2.CorrelatablePropertiesType;
import org.oasis_open.docs.wsdm.muws1_2.ManagementEventType;
import org.oasis_open.docs.wsdm.muws2_2.AbortInitiated;
import org.oasis_open.docs.wsdm.muws2_2.AvailabilitySituation;
import org.oasis_open.docs.wsdm.muws2_2.CapabilitySituation;
import org.oasis_open.docs.wsdm.muws2_2.CategoryType;
import org.oasis_open.docs.wsdm.muws2_2.ConfigureSituation;
import org.oasis_open.docs.wsdm.muws2_2.ConnectCompleted;
import org.oasis_open.docs.wsdm.muws2_2.ConnectInitiated;
import org.oasis_open.docs.wsdm.muws2_2.ConnectSituation;
import org.oasis_open.docs.wsdm.muws2_2.CreateCompleted;
import org.oasis_open.docs.wsdm.muws2_2.CreateInitiated;
import org.oasis_open.docs.wsdm.muws2_2.CreateSituation;
import org.oasis_open.docs.wsdm.muws2_2.CreationNotification;
import org.oasis_open.docs.wsdm.muws2_2.DebugReport;
import org.oasis_open.docs.wsdm.muws2_2.DestroyCompleted;
import org.oasis_open.docs.wsdm.muws2_2.DestroyInitiated;
import org.oasis_open.docs.wsdm.muws2_2.DestroySituation;
import org.oasis_open.docs.wsdm.muws2_2.DestructionNotification;
import org.oasis_open.docs.wsdm.muws2_2.DialectableExpressionType;
import org.oasis_open.docs.wsdm.muws2_2.EventCorrelationPropertiesType;
import org.oasis_open.docs.wsdm.muws2_2.HeartbeatReport;
import org.oasis_open.docs.wsdm.muws2_2.LangString;
import org.oasis_open.docs.wsdm.muws2_2.LogReport;
import org.oasis_open.docs.wsdm.muws2_2.MsgCatalogInformationType;
import org.oasis_open.docs.wsdm.muws2_2.OtherSituation;
import org.oasis_open.docs.wsdm.muws2_2.PauseInitiated;
import org.oasis_open.docs.wsdm.muws2_2.PerformanceReport;
import org.oasis_open.docs.wsdm.muws2_2.QueryRelationshipsByType;
import org.oasis_open.docs.wsdm.muws2_2.QueryRelationshipsByTypeResponse;
import org.oasis_open.docs.wsdm.muws2_2.ReconnectInitiated;
import org.oasis_open.docs.wsdm.muws2_2.RelationshipCreatedNotification;
import org.oasis_open.docs.wsdm.muws2_2.RelationshipDeletedNotification;
import org.oasis_open.docs.wsdm.muws2_2.RelationshipParticipantType;
import org.oasis_open.docs.wsdm.muws2_2.RelationshipType;
import org.oasis_open.docs.wsdm.muws2_2.RelationshipTypeType;
import org.oasis_open.docs.wsdm.muws2_2.ReportSituation;
import org.oasis_open.docs.wsdm.muws2_2.RequestCompleted;
import org.oasis_open.docs.wsdm.muws2_2.RequestInitiated;
import org.oasis_open.docs.wsdm.muws2_2.RequestSituation;
import org.oasis_open.docs.wsdm.muws2_2.RestartInitiated;
import org.oasis_open.docs.wsdm.muws2_2.SecurityReport;
import org.oasis_open.docs.wsdm.muws2_2.Self;
import org.oasis_open.docs.wsdm.muws2_2.SituationCategoryType;
import org.oasis_open.docs.wsdm.muws2_2.SituationType;
import org.oasis_open.docs.wsdm.muws2_2.StartCompleted;
import org.oasis_open.docs.wsdm.muws2_2.StartInitiated;
import org.oasis_open.docs.wsdm.muws2_2.StartSituation;
import org.oasis_open.docs.wsdm.muws2_2.StateTransitionType;
import org.oasis_open.docs.wsdm.muws2_2.StateType;
import org.oasis_open.docs.wsdm.muws2_2.StatusReport;
import org.oasis_open.docs.wsdm.muws2_2.StopCompleted;
import org.oasis_open.docs.wsdm.muws2_2.StopInitiated;
import org.oasis_open.docs.wsdm.muws2_2.StopSituation;
import org.oasis_open.docs.wsdm.muws2_2.SubstitutableMsgType;
import org.oasis_open.docs.wsdm.muws2_2.TraceReport;
import org.oasis_open.docs.wsrf.bf_2.BaseFaultType;
import org.oasis_open.docs.wsrf.rp_2.DeleteResourceProperties;
import org.oasis_open.docs.wsrf.rp_2.DeleteResourcePropertiesRequestFailedFaultType;
import org.oasis_open.docs.wsrf.rp_2.DeleteResourcePropertiesResponse;
import org.oasis_open.docs.wsrf.rp_2.DeleteType;
import org.oasis_open.docs.wsrf.rp_2.GetMultipleResourceProperties;
import org.oasis_open.docs.wsrf.rp_2.GetResourcePropertyDocument;
import org.oasis_open.docs.wsrf.rp_2.GetResourcePropertyDocumentResponse;
import org.oasis_open.docs.wsrf.rp_2.GetResourcePropertyResponse;
import org.oasis_open.docs.wsrf.rp_2.InsertResourceProperties;
import org.oasis_open.docs.wsrf.rp_2.InsertResourcePropertiesRequestFailedFaultType;
import org.oasis_open.docs.wsrf.rp_2.InsertResourcePropertiesResponse;
import org.oasis_open.docs.wsrf.rp_2.InsertType;
import org.oasis_open.docs.wsrf.rp_2.InvalidModificationFaultType;
import org.oasis_open.docs.wsrf.rp_2.InvalidQueryExpressionFaultType;
import org.oasis_open.docs.wsrf.rp_2.InvalidResourcePropertyQNameFaultType;
import org.oasis_open.docs.wsrf.rp_2.PutResourcePropertyDocument;
import org.oasis_open.docs.wsrf.rp_2.PutResourcePropertyDocumentResponse;
import org.oasis_open.docs.wsrf.rp_2.QueryEvaluationErrorFaultType;
import org.oasis_open.docs.wsrf.rp_2.QueryExpressionRPDocument;
import org.oasis_open.docs.wsrf.rp_2.QueryExpressionType;
import org.oasis_open.docs.wsrf.rp_2.QueryResourceProperties;
import org.oasis_open.docs.wsrf.rp_2.QueryResourcePropertiesResponse;
import org.oasis_open.docs.wsrf.rp_2.ResourcePropertyChangeFailureType;
import org.oasis_open.docs.wsrf.rp_2.ResourcePropertyValueChangeNotificationType;
import org.oasis_open.docs.wsrf.rp_2.SetResourceProperties;
import org.oasis_open.docs.wsrf.rp_2.SetResourcePropertiesResponse;
import org.oasis_open.docs.wsrf.rp_2.SetResourcePropertyRequestFailedFaultType;
import org.oasis_open.docs.wsrf.rp_2.UnableToModifyResourcePropertyFaultType;
import org.oasis_open.docs.wsrf.rp_2.UnableToPutResourcePropertyDocumentFaultType;
import org.oasis_open.docs.wsrf.rp_2.UnknownQueryExpressionDialectFaultType;
import org.oasis_open.docs.wsrf.rp_2.UpdateResourceProperties;
import org.oasis_open.docs.wsrf.rp_2.UpdateResourcePropertiesRequestFailedFaultType;
import org.oasis_open.docs.wsrf.rp_2.UpdateResourcePropertiesResponse;
import org.oasis_open.docs.wsrf.rp_2.UpdateType;
import org.w3c.dom.Document;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author AO
 */
public class SLAUtils {

     private static Logger log = Logger.getLogger("fgsms.SLAUtils");
     
    /**
     * Converts a ManagementEventType to a dom Element
     *
     * @param alert
     * @return may return null if the document cannot be marshalled
     */
    public static org.w3c.dom.Element WSDMAlertToDomElement(ManagementEventType alert) {
        try {
            InputStream is = new ByteArrayInputStream(WSDMtoXmlString(alert).getBytes(Constants.CHARSET));
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            fac.setNamespaceAware(true);
            DocumentBuilder newDocumentBuilder = fac.newDocumentBuilder();
            Document xmlDocument = newDocumentBuilder.parse(is);
            xmlDocument.getDocumentElement().normalize();
            return xmlDocument.getDocumentElement();
        } catch (Exception ex) {
            log.log(Level.FATAL, BundleLoader.getBundleString("ErrorWSDMCreateDomElement"), ex);
        }
        return null;

    }

    /**
     * Converts a ManagementEventType to an XML String
     *
     * @param e
     * @returnmay return null if the document cannot be marshalled
     */
    public static String WSDMtoXmlString(ManagementEventType e) {
        try {
            JAXBContext jc = JAXBContext.newInstance(
                  //  EsmPropertiesChoiceType.class,
                   // EsmPropertiesType.class,
                    AnyXmlContentsType.class,
                    BusyState.class,
                    CrashedState.class,
                    DownState.class,
                    DurationMetric.class,
                    EndpointDescriptions.class,
                    GetManageabilityReferencesResponse.class,
                    IdleState.class,
                    IntegerCounter.class,
                    MessageContentNotIncludedFlag.class,
                    MessageContentSizeType.class,
                    MessageContentType.class,
                    MessageInformationType.class,
                    MessageSizeUnitType.class,
                    OperationalStateType.class,
                    OperationDurationMetric.class,
                    OperationIntegerCounter.class,
                    OperationMetricType.class,
                    OperationOperationalStatusType.class,
                    RequestCompletedState.class,
                    RequestFailedState.class,
                    RequestProcessingNotification.class,
                    RequestProcessingState.class,
                    RequestProcessingStateInformationType.class,
                    RequestProcessingStateType.class,
                    RequestReceivedState.class,
                    SaturatedState.class,
                    StoppedState.class,
                    TcpIpDirectionType.class,
                    TcpIpInfo.class,
                    TcpIpProtocolType.class,
                    UpState.class,
                    ComponentAddressType.class,
                    ComponentType.class,
                    CorrelatablePropertiesType.class,
                    ManagementEventType.class,
                    AbortInitiated.class,
                    AvailabilitySituation.class,
                    CapabilitySituation.class,
                    CategoryType.class,
                    ConfigureSituation.class,
                    ConnectCompleted.class,
                    ConnectInitiated.class,
                    ConnectSituation.class,
                    CreateCompleted.class,
                    CreateInitiated.class,
                    CreateSituation.class,
                    CreationNotification.class,
                    DebugReport.class,
                    DestroyCompleted.class,
                    DestroyInitiated.class,
                    DestroySituation.class,
                    DestructionNotification.class,
                    DialectableExpressionType.class,
                    EventCorrelationPropertiesType.class,
                    HeartbeatReport.class,
                    LangString.class,
                    LogReport.class,
                    MsgCatalogInformationType.class,
                    OtherSituation.class,
                    PauseInitiated.class,
                    PerformanceReport.class,
                    QueryRelationshipsByType.class,
                    QueryRelationshipsByTypeResponse.class,
                    ReconnectInitiated.class,
                    RelationshipCreatedNotification.class,
                    RelationshipDeletedNotification.class,
                    RelationshipParticipantType.class,
                    RelationshipType.class,
                    RelationshipTypeType.class,
                    ReportSituation.class,
                    RequestCompleted.class,
                    RequestInitiated.class,
                    RequestSituation.class,
                    RestartInitiated.class,
                    SecurityReport.class,
                    Self.class,
                    SituationCategoryType.class,
                    SituationType.class,
                    StartCompleted.class,
                    StartInitiated.class,
                    StartSituation.class,
                    StateTransitionType.class,
                    StateType.class,
                    StatusReport.class,
                    StopCompleted.class,
                    StopInitiated.class,
                    StopSituation.class,
                    SubstitutableMsgType.class,
                    TraceReport.class,
                    BaseFaultType.class,
                    DeleteResourceProperties.class,
                    DeleteResourcePropertiesRequestFailedFaultType.class,
                    DeleteResourcePropertiesResponse.class,
                    DeleteType.class,
                    GetMultipleResourceProperties.class,
                    GetResourcePropertyDocument.class,
                    GetResourcePropertyDocumentResponse.class,
                    GetResourcePropertyResponse.class,
                    InsertResourceProperties.class,
                    InsertResourcePropertiesRequestFailedFaultType.class,
                    InsertResourcePropertiesResponse.class,
                    InsertType.class,
                    InvalidModificationFaultType.class,
                    InvalidQueryExpressionFaultType.class,
                    InvalidResourcePropertyQNameFaultType.class,
                    PutResourcePropertyDocument.class,
                    PutResourcePropertyDocumentResponse.class,
                    QueryEvaluationErrorFaultType.class,
                    QueryExpressionRPDocument.class,
                    QueryExpressionType.class,
                    QueryResourceProperties.class,
                    QueryResourcePropertiesResponse.class,
                    ResourcePropertyChangeFailureType.class,
                    ResourcePropertyValueChangeNotificationType.class,
                    SetResourceProperties.class,
                    SetResourcePropertiesResponse.class,
                    SetResourcePropertyRequestFailedFaultType.class,
                    UnableToModifyResourcePropertyFaultType.class,
                    UnableToPutResourcePropertyDocumentFaultType.class,
                    UnknownQueryExpressionDialectFaultType.class,
                    UpdateResourceProperties.class,
                    UpdateResourcePropertiesRequestFailedFaultType.class,
                    UpdateResourcePropertiesResponse.class,
                    UpdateType.class,
                    ClassificationType.class);
            Marshaller um = jc.createMarshaller();
            StringWriter sw = new StringWriter();
            um.marshal(e, sw);
            return sw.toString();
        } catch (JAXBException ex) {
            log.log(Level.WARN, BundleLoader.getBundleString("ErrorWSDMMarshalling"), ex);
        }
        return null;
    }

    /**
     * Creates a ManagementEventType populated from the AlertContainer
     *
     * @param alert
     * @return
     * @throws IllegalArgumentException if the alert is null
     */
    public static org.oasis_open.docs.wsdm.muws1_2.ManagementEventType createWSDMEvent(AlertContainer alert) throws IllegalArgumentException {
        if (alert == null) {
            throw new IllegalArgumentException("alert");
        }
       
        ManagementEventType m = new ManagementEventType();

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());

        m.setEventId(BundleLoader.getBundleString("WSDMEventIdPrefix") + alert.getSLAID());
        ComponentType c = new ComponentType();
        c.setResourceId(alert.getModifiedurl());
        m.setSourceComponent(c);
        SituationType sit = new SituationType();
        LangString l = new LangString();
        l.setLang(BundleLoader.getBundleString("WSDMLangauge"));
        l.setValue(alert.getFaultMsg());
        sit.setMessage(l);

        sit.setSituationCategory(alert.getAlerttype());
        short severity = 4;
        sit.setSeverity(severity);
        m.setSituation(sit);
        try {
           //atatypeFactory df = DatatypeFactory.newInstance();
            m.setReportTime((gcal));
            sit.setSituationTime((gcal));
        } catch (Exception ex) {
            log.log(Level.WARN, BundleLoader.getBundleString("ErrorDataTypeFactorLoad"), ex);
        }
        return m;

    }
}
