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

package org.oasis_open.docs.wsdm.muws1_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oasis_open.docs.wsdm.muws1_2 package. 
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

    private final static QName _ResourceId_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws1-2.xsd", "ResourceId");
    private final static QName _ManageabilityCapability_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws1-2.xsd", "ManageabilityCapability");
    private final static QName _ManagementEvent_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws1-2.xsd", "ManagementEvent");
    private final static QName _ManageabilityEndpointReference_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws1-2.xsd", "ManageabilityEndpointReference");
    private final static QName _CorrelatableProperties_QNAME = new QName("http://docs.oasis-open.org/wsdm/muws1-2.xsd", "CorrelatableProperties");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.wsdm.muws1_2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ComponentType }
     * 
     */
    public ComponentType createComponentType() {
        return new ComponentType();
    }

    /**
     * Create an instance of {@link CorrelatablePropertiesType }
     * 
     */
    public CorrelatablePropertiesType createCorrelatablePropertiesType() {
        return new CorrelatablePropertiesType();
    }

    /**
     * Create an instance of {@link ManagementEventType }
     * 
     */
    public ManagementEventType createManagementEventType() {
        return new ManagementEventType();
    }

    /**
     * Create an instance of {@link ComponentAddressType }
     * 
     */
    public ComponentAddressType createComponentAddressType() {
        return new ComponentAddressType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws1-2.xsd", name = "ResourceId")
    public JAXBElement<String> createResourceId(String value) {
        return new JAXBElement<String>(_ResourceId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws1-2.xsd", name = "ManageabilityCapability")
    public JAXBElement<String> createManageabilityCapability(String value) {
        return new JAXBElement<String>(_ManageabilityCapability_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ManagementEventType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws1-2.xsd", name = "ManagementEvent")
    public JAXBElement<ManagementEventType> createManagementEvent(ManagementEventType value) {
        return new JAXBElement<ManagementEventType>(_ManagementEvent_QNAME, ManagementEventType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link W3CEndpointReference }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws1-2.xsd", name = "ManageabilityEndpointReference")
    public JAXBElement<W3CEndpointReference> createManageabilityEndpointReference(W3CEndpointReference value) {
        return new JAXBElement<W3CEndpointReference>(_ManageabilityEndpointReference_QNAME, W3CEndpointReference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CorrelatablePropertiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsdm/muws1-2.xsd", name = "CorrelatableProperties")
    public JAXBElement<CorrelatablePropertiesType> createCorrelatableProperties(CorrelatablePropertiesType value) {
        return new JAXBElement<CorrelatablePropertiesType>(_CorrelatableProperties_QNAME, CorrelatablePropertiesType.class, null, value);
    }

}
