
package org.miloss.fgsms.services.interfaces.faults;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.miloss.fgsms.services.interfaces.faults package. 
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

    private final static QName _AccessDeniedException_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:faults", "AccessDeniedException");
    private final static QName _ServiceUnavailableFaultCodes_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:faults", "ServiceUnavailableFaultCodes");
    private final static QName _ServiceUnavailableException_QNAME = new QName("urn:org:miloss:fgsms:services:interfaces:faults", "ServiceUnavailableException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.miloss.fgsms.services.interfaces.faults
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServiceUnavailableException }
     * 
     */
    public ServiceUnavailableException createServiceUnavailableException() {
        return new ServiceUnavailableException();
    }

    /**
     * Create an instance of {@link AccessDeniedException }
     * 
     */
    public AccessDeniedException createAccessDeniedException() {
        return new AccessDeniedException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccessDeniedException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:faults", name = "AccessDeniedException")
    public JAXBElement<AccessDeniedException> createAccessDeniedException(AccessDeniedException value) {
        return new JAXBElement<AccessDeniedException>(_AccessDeniedException_QNAME, AccessDeniedException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceUnavailableFaultCodes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:faults", name = "ServiceUnavailableFaultCodes")
    public JAXBElement<ServiceUnavailableFaultCodes> createServiceUnavailableFaultCodes(ServiceUnavailableFaultCodes value) {
        return new JAXBElement<ServiceUnavailableFaultCodes>(_ServiceUnavailableFaultCodes_QNAME, ServiceUnavailableFaultCodes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceUnavailableException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:org:miloss:fgsms:services:interfaces:faults", name = "ServiceUnavailableException")
    public JAXBElement<ServiceUnavailableException> createServiceUnavailableException(ServiceUnavailableException value) {
        return new JAXBElement<ServiceUnavailableException>(_ServiceUnavailableException_QNAME, ServiceUnavailableException.class, null, value);
    }

}
