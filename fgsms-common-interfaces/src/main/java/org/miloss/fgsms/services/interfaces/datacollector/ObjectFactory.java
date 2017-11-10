
package org.miloss.fgsms.services.interfaces.datacollector;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.miloss.fgsms.services.interfaces.datacollector package. 
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

    private final static QName _AddDataResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataCollector", "AddDataResponseMsg");
    private final static QName _DataResponseStatus_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataCollector", "DataResponseStatus");
    private final static QName _AddDataRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:dataCollector", "AddDataRequestMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.miloss.fgsms.services.interfaces.datacollector
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddStatisticalDataResponseMsg }
     * 
     */
    public AddStatisticalDataResponseMsg createAddStatisticalDataResponseMsg() {
        return new AddStatisticalDataResponseMsg();
    }

    /**
     * Create an instance of {@link BrokerData }
     * 
     */
    public BrokerData createBrokerData() {
        return new BrokerData();
    }

    /**
     * Create an instance of {@link AddMachineAndProcessDataResponseMsg }
     * 
     */
    public AddMachineAndProcessDataResponseMsg createAddMachineAndProcessDataResponseMsg() {
        return new AddMachineAndProcessDataResponseMsg();
    }

    /**
     * Create an instance of {@link AddDataResponseMsg }
     * 
     */
    public AddDataResponseMsg createAddDataResponseMsg() {
        return new AddDataResponseMsg();
    }

    /**
     * Create an instance of {@link AddMoreData }
     * 
     */
    public AddMoreData createAddMoreData() {
        return new AddMoreData();
    }

    /**
     * Create an instance of {@link AddDataResponse }
     * 
     */
    public AddDataResponse createAddDataResponse() {
        return new AddDataResponse();
    }

    /**
     * Create an instance of {@link AddStatisticalData }
     * 
     */
    public AddStatisticalData createAddStatisticalData() {
        return new AddStatisticalData();
    }

    /**
     * Create an instance of {@link AddMoreDataResponse }
     * 
     */
    public AddMoreDataResponse createAddMoreDataResponse() {
        return new AddMoreDataResponse();
    }

    /**
     * Create an instance of {@link AddDataRequestMsg }
     * 
     */
    public AddDataRequestMsg createAddDataRequestMsg() {
        return new AddDataRequestMsg();
    }

    /**
     * Create an instance of {@link AddMachineAndProcessData }
     * 
     */
    public AddMachineAndProcessData createAddMachineAndProcessData() {
        return new AddMachineAndProcessData();
    }

    /**
     * Create an instance of {@link AddData }
     * 
     */
    public AddData createAddData() {
        return new AddData();
    }

    /**
     * Create an instance of {@link AddStatisticalDataRequestMsg }
     * 
     */
    public AddStatisticalDataRequestMsg createAddStatisticalDataRequestMsg() {
        return new AddStatisticalDataRequestMsg();
    }

    /**
     * Create an instance of {@link AddStatisticalDataResponse }
     * 
     */
    public AddStatisticalDataResponse createAddStatisticalDataResponse() {
        return new AddStatisticalDataResponse();
    }

    /**
     * Create an instance of {@link AddMachineAndProcessDataResponse }
     * 
     */
    public AddMachineAndProcessDataResponse createAddMachineAndProcessDataResponse() {
        return new AddMachineAndProcessDataResponse();
    }

    /**
     * Create an instance of {@link AddMachineAndProcessDataRequestMsg }
     * 
     */
    public AddMachineAndProcessDataRequestMsg createAddMachineAndProcessDataRequestMsg() {
        return new AddMachineAndProcessDataRequestMsg();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddDataResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", name = "AddDataResponseMsg")
    public JAXBElement<AddDataResponseMsg> createAddDataResponseMsg(AddDataResponseMsg value) {
        return new JAXBElement<AddDataResponseMsg>(_AddDataResponseMsg_QNAME, AddDataResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataResponseStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", name = "DataResponseStatus")
    public JAXBElement<DataResponseStatus> createDataResponseStatus(DataResponseStatus value) {
        return new JAXBElement<DataResponseStatus>(_DataResponseStatus_QNAME, DataResponseStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddDataRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector", name = "AddDataRequestMsg")
    public JAXBElement<AddDataRequestMsg> createAddDataRequestMsg(AddDataRequestMsg value) {
        return new JAXBElement<AddDataRequestMsg>(_AddDataRequestMsg_QNAME, AddDataRequestMsg.class, null, value);
    }

}
