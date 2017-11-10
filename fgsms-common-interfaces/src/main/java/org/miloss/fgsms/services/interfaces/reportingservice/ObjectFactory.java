
package org.miloss.fgsms.services.interfaces.reportingservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.miloss.fgsms.services.interfaces.common.TimeRange;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.miloss.fgsms.services.interfaces.reportingservice package. 
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

    private final static QName _ExportDataToHTMLResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:reportingService", "ExportDataToHTML_ResponseMsg");
    private final static QName _ExportCSVDataRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:reportingService", "ExportCSVDataRequestMsg");
    private final static QName _ExportDataRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:reportingService", "ExportDataRequestMsg");
    private final static QName _ArrayOfReportTypeContainer_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:reportingService", "ArrayOfReportTypeContainer");
    private final static QName _TimeRange_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:reportingService", "TimeRange");
    private final static QName _ExportDataToCSVResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:reportingService", "ExportDataToCSV_ResponseMsg");
    private final static QName _ReportTypeContainer_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:reportingService", "ReportTypeContainer");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.miloss.fgsms.services.interfaces.reportingservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExportDataToHTMLResponse }
     * 
     */
    public ExportDataToHTMLResponse createExportDataToHTMLResponse() {
        return new ExportDataToHTMLResponse();
    }

    /**
     * Create an instance of {@link ExportDataToHTMLResponseMsg }
     * 
     */
    public ExportDataToHTMLResponseMsg createExportDataToHTMLResponseMsg() {
        return new ExportDataToHTMLResponseMsg();
    }

    /**
     * Create an instance of {@link ExportDataToHTML }
     * 
     */
    public ExportDataToHTML createExportDataToHTML() {
        return new ExportDataToHTML();
    }

    /**
     * Create an instance of {@link ExportDataRequestMsg }
     * 
     */
    public ExportDataRequestMsg createExportDataRequestMsg() {
        return new ExportDataRequestMsg();
    }

    /**
     * Create an instance of {@link ExportDataToCSVResponseMsg }
     * 
     */
    public ExportDataToCSVResponseMsg createExportDataToCSVResponseMsg() {
        return new ExportDataToCSVResponseMsg();
    }

    /**
     * Create an instance of {@link ExportDataToCSVResponse }
     * 
     */
    public ExportDataToCSVResponse createExportDataToCSVResponse() {
        return new ExportDataToCSVResponse();
    }

    /**
     * Create an instance of {@link ExportDataToCSV }
     * 
     */
    public ExportDataToCSV createExportDataToCSV() {
        return new ExportDataToCSV();
    }

    /**
     * Create an instance of {@link ExportCSVDataRequestMsg }
     * 
     */
    public ExportCSVDataRequestMsg createExportCSVDataRequestMsg() {
        return new ExportCSVDataRequestMsg();
    }

    /**
     * Create an instance of {@link ReportTypeContainer }
     * 
     */
    public ReportTypeContainer createReportTypeContainer() {
        return new ReportTypeContainer();
    }

    /**
     * Create an instance of {@link ArrayOfReportTypeContainer }
     * 
     */
    public ArrayOfReportTypeContainer createArrayOfReportTypeContainer() {
        return new ArrayOfReportTypeContainer();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportDataToHTMLResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", name = "ExportDataToHTML_ResponseMsg")
    public JAXBElement<ExportDataToHTMLResponseMsg> createExportDataToHTMLResponseMsg(ExportDataToHTMLResponseMsg value) {
        return new JAXBElement<ExportDataToHTMLResponseMsg>(_ExportDataToHTMLResponseMsg_QNAME, ExportDataToHTMLResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportCSVDataRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", name = "ExportCSVDataRequestMsg")
    public JAXBElement<ExportCSVDataRequestMsg> createExportCSVDataRequestMsg(ExportCSVDataRequestMsg value) {
        return new JAXBElement<ExportCSVDataRequestMsg>(_ExportCSVDataRequestMsg_QNAME, ExportCSVDataRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportDataRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", name = "ExportDataRequestMsg")
    public JAXBElement<ExportDataRequestMsg> createExportDataRequestMsg(ExportDataRequestMsg value) {
        return new JAXBElement<ExportDataRequestMsg>(_ExportDataRequestMsg_QNAME, ExportDataRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfReportTypeContainer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", name = "ArrayOfReportTypeContainer")
    public JAXBElement<ArrayOfReportTypeContainer> createArrayOfReportTypeContainer(ArrayOfReportTypeContainer value) {
        return new JAXBElement<ArrayOfReportTypeContainer>(_ArrayOfReportTypeContainer_QNAME, ArrayOfReportTypeContainer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeRange }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", name = "TimeRange")
    public JAXBElement<TimeRange> createTimeRange(TimeRange value) {
        return new JAXBElement<TimeRange>(_TimeRange_QNAME, TimeRange.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportDataToCSVResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", name = "ExportDataToCSV_ResponseMsg")
    public JAXBElement<ExportDataToCSVResponseMsg> createExportDataToCSVResponseMsg(ExportDataToCSVResponseMsg value) {
        return new JAXBElement<ExportDataToCSVResponseMsg>(_ExportDataToCSVResponseMsg_QNAME, ExportDataToCSVResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReportTypeContainer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:reportingService", name = "ReportTypeContainer")
    public JAXBElement<ReportTypeContainer> createReportTypeContainer(ReportTypeContainer value) {
        return new JAXBElement<ReportTypeContainer>(_ReportTypeContainer_QNAME, ReportTypeContainer.class, null, value);
    }

}
