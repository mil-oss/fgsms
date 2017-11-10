
package org.miloss.fgsms.services.interfaces.status;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.miloss.fgsms.services.interfaces.status package. 
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

    private final static QName _SetResponseStatus_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:status", "SetResponseStatus");
    private final static QName _SetExtendedStatusRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:status", "SetExtendedStatusRequestMsg");
    private final static QName _SetStatusRequestMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:status", "SetStatusRequestMsg");
    private final static QName _SetStatusResponseMsg_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:status", "SetStatusResponseMsg");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.miloss.fgsms.services.interfaces.status
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetStatus }
     * 
     */
    public SetStatus createSetStatus() {
        return new SetStatus();
    }

    /**
     * Create an instance of {@link SetMoreStatus }
     * 
     */
    public SetMoreStatus createSetMoreStatus() {
        return new SetMoreStatus();
    }

    /**
     * Create an instance of {@link RemoveStatusResponseMsg }
     * 
     */
    public RemoveStatusResponseMsg createRemoveStatusResponseMsg() {
        return new RemoveStatusResponseMsg();
    }

    /**
     * Create an instance of {@link RemoveStatusResponse }
     * 
     */
    public RemoveStatusResponse createRemoveStatusResponse() {
        return new RemoveStatusResponse();
    }

    /**
     * Create an instance of {@link RemoveStatus }
     * 
     */
    public RemoveStatus createRemoveStatus() {
        return new RemoveStatus();
    }

    /**
     * Create an instance of {@link SetStatusResponseMsg }
     * 
     */
    public SetStatusResponseMsg createSetStatusResponseMsg() {
        return new SetStatusResponseMsg();
    }

    /**
     * Create an instance of {@link RemoveStatusRequestMsg }
     * 
     */
    public RemoveStatusRequestMsg createRemoveStatusRequestMsg() {
        return new RemoveStatusRequestMsg();
    }

    /**
     * Create an instance of {@link GetStatus }
     * 
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link GetStatusRequestMsg }
     * 
     */
    public GetStatusRequestMsg createGetStatusRequestMsg() {
        return new GetStatusRequestMsg();
    }

    /**
     * Create an instance of {@link GetAllStatus }
     * 
     */
    public GetAllStatus createGetAllStatus() {
        return new GetAllStatus();
    }

    /**
     * Create an instance of {@link GetStatusResponse }
     * 
     */
    public GetStatusResponse createGetStatusResponse() {
        return new GetStatusResponse();
    }

    /**
     * Create an instance of {@link GetAllStatusRequestMsg }
     * 
     */
    public GetAllStatusRequestMsg createGetAllStatusRequestMsg() {
        return new GetAllStatusRequestMsg();
    }

    /**
     * Create an instance of {@link SetExtendedStatus }
     * 
     */
    public SetExtendedStatus createSetExtendedStatus() {
        return new SetExtendedStatus();
    }

    /**
     * Create an instance of {@link SetExtendedStatusRequestMsg }
     * 
     */
    public SetExtendedStatusRequestMsg createSetExtendedStatusRequestMsg() {
        return new SetExtendedStatusRequestMsg();
    }

    /**
     * Create an instance of {@link SetMoreStatusResponse }
     * 
     */
    public SetMoreStatusResponse createSetMoreStatusResponse() {
        return new SetMoreStatusResponse();
    }

    /**
     * Create an instance of {@link GetStatusResponseMsg }
     * 
     */
    public GetStatusResponseMsg createGetStatusResponseMsg() {
        return new GetStatusResponseMsg();
    }

    /**
     * Create an instance of {@link SetStatusRequestMsg }
     * 
     */
    public SetStatusRequestMsg createSetStatusRequestMsg() {
        return new SetStatusRequestMsg();
    }

    /**
     * Create an instance of {@link GetAllStatusResponse }
     * 
     */
    public GetAllStatusResponse createGetAllStatusResponse() {
        return new GetAllStatusResponse();
    }

    /**
     * Create an instance of {@link SetStatusResponse }
     * 
     */
    public SetStatusResponse createSetStatusResponse() {
        return new SetStatusResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetResponseStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:status", name = "SetResponseStatus")
    public JAXBElement<SetResponseStatus> createSetResponseStatus(SetResponseStatus value) {
        return new JAXBElement<SetResponseStatus>(_SetResponseStatus_QNAME, SetResponseStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetStatusRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:status", name = "SetExtendedStatusRequestMsg")
    public JAXBElement<SetStatusRequestMsg> createSetExtendedStatusRequestMsg(SetStatusRequestMsg value) {
        return new JAXBElement<SetStatusRequestMsg>(_SetExtendedStatusRequestMsg_QNAME, SetStatusRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetStatusRequestMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:status", name = "SetStatusRequestMsg")
    public JAXBElement<SetStatusRequestMsg> createSetStatusRequestMsg(SetStatusRequestMsg value) {
        return new JAXBElement<SetStatusRequestMsg>(_SetStatusRequestMsg_QNAME, SetStatusRequestMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetStatusResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:status", name = "SetStatusResponseMsg")
    public JAXBElement<SetStatusResponseMsg> createSetStatusResponseMsg(SetStatusResponseMsg value) {
        return new JAXBElement<SetStatusResponseMsg>(_SetStatusResponseMsg_QNAME, SetStatusResponseMsg.class, null, value);
    }

}
