
package org.miloss.fgsms.services.interfaces.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.miloss.fgsms.services.interfaces.common package. 
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

    private final static QName _AlertMessage_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:common", "AlertMessage");
    private final static QName _TimeRange_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:common", "TimeRange");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.miloss.fgsms.services.interfaces.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetOperatingStatus }
     * 
     */
    public GetOperatingStatus createGetOperatingStatus() {
        return new GetOperatingStatus();
    }

    /**
     * Create an instance of {@link Header }
     * 
     */
    public Header createHeader() {
        return new Header();
    }

    /**
     * Create an instance of {@link TimeRange }
     * 
     */
    public TimeRange createTimeRange() {
        return new TimeRange();
    }

    /**
     * Create an instance of {@link SecurityWrapper }
     * 
     */
    public SecurityWrapper createSecurityWrapper() {
        return new SecurityWrapper();
    }

    /**
     * Create an instance of {@link ProcessPerformanceData }
     * 
     */
    public ProcessPerformanceData createProcessPerformanceData() {
        return new ProcessPerformanceData();
    }

    /**
     * Create an instance of {@link NetworkAdapterInfo }
     * 
     */
    public NetworkAdapterInfo createNetworkAdapterInfo() {
        return new NetworkAdapterInfo();
    }

    /**
     * Create an instance of {@link GetOperatingStatusResponseMessage }
     * 
     */
    public GetOperatingStatusResponseMessage createGetOperatingStatusResponseMessage() {
        return new GetOperatingStatusResponseMessage();
    }

    /**
     * Create an instance of {@link MachinePerformanceData }
     * 
     */
    public MachinePerformanceData createMachinePerformanceData() {
        return new MachinePerformanceData();
    }

    /**
     * Create an instance of {@link GetOperatingStatusRequestMessage }
     * 
     */
    public GetOperatingStatusRequestMessage createGetOperatingStatusRequestMessage() {
        return new GetOperatingStatusRequestMessage();
    }

    /**
     * Create an instance of {@link NameValuePair }
     * 
     */
    public NameValuePair createNameValuePair() {
        return new NameValuePair();
    }

    /**
     * Create an instance of {@link NetworkAdapterPerformanceData }
     * 
     */
    public NetworkAdapterPerformanceData createNetworkAdapterPerformanceData() {
        return new NetworkAdapterPerformanceData();
    }

    /**
     * Create an instance of {@link GetOperatingStatusResponseMessage.VersionInfo }
     * 
     */
    public GetOperatingStatusResponseMessage.VersionInfo createGetOperatingStatusResponseMessageVersionInfo() {
        return new GetOperatingStatusResponseMessage.VersionInfo();
    }

    /**
     * Create an instance of {@link GetOperatingStatusResponse }
     * 
     */
    public GetOperatingStatusResponse createGetOperatingStatusResponse() {
        return new GetOperatingStatusResponse();
    }

    /**
     * Create an instance of {@link NameValuePairSet }
     * 
     */
    public NameValuePairSet createNameValuePairSet() {
        return new NameValuePairSet();
    }

    /**
     * Create an instance of {@link DriveInformation }
     * 
     */
    public DriveInformation createDriveInformation() {
        return new DriveInformation();
    }

    /**
     * Create an instance of {@link AlertMessageDefinition }
     * 
     */
    public AlertMessageDefinition createAlertMessageDefinition() {
        return new AlertMessageDefinition();
    }

    /**
     * Create an instance of {@link AgentAction }
     * 
     */
    public AgentAction createAgentAction() {
        return new AgentAction();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlertMessageDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:common", name = "AlertMessage")
    public JAXBElement<AlertMessageDefinition> createAlertMessage(AlertMessageDefinition value) {
        return new JAXBElement<AlertMessageDefinition>(_AlertMessage_QNAME, AlertMessageDefinition.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeRange }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:common", name = "TimeRange")
    public JAXBElement<TimeRange> createTimeRange(TimeRange value) {
        return new JAXBElement<TimeRange>(_TimeRange_QNAME, TimeRange.class, null, value);
    }

}
