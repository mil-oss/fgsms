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
package namespace.webservice.no1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the namespace.webservice._new package. 
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

    private final static QName _HelloWorldResponseMsg_QNAME = new QName("http://new.webservice.namespace", "HelloWorldResponseMsg");
    private final static QName _HelloWorldResponseHelloWorldResult_QNAME = new QName("http://new.webservice.namespace", "HelloWorldResult");
    private final static QName _HelloWorldReq_QNAME = new QName("http://new.webservice.namespace", "req");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: namespace.webservice._new
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HelloWorldResponse }
     * 
     */
    public HelloWorldResponse createHelloWorldResponse() {
        return new HelloWorldResponse();
    }

    /**
     * Create an instance of {@link HelloWorldData }
     * 
     */
    public HelloWorldData createHelloWorldData() {
        return new HelloWorldData();
    }

    /**
     * Create an instance of {@link HelloWorldResponseMsg }
     * 
     */
    public HelloWorldResponseMsg createHelloWorldResponseMsg() {
        return new HelloWorldResponseMsg();
    }

    /**
     * Create an instance of {@link HelloWorld }
     * 
     */
    public HelloWorld createHelloWorld() {
        return new HelloWorld();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HelloWorldResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://new.webservice.namespace", name = "HelloWorldResponseMsg")
    public JAXBElement<HelloWorldResponseMsg> createHelloWorldResponseMsg(HelloWorldResponseMsg value) {
        return new JAXBElement<HelloWorldResponseMsg>(_HelloWorldResponseMsg_QNAME, HelloWorldResponseMsg.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HelloWorldResponseMsg }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://new.webservice.namespace", name = "HelloWorldResult", scope = HelloWorldResponse.class)
    public JAXBElement<HelloWorldResponseMsg> createHelloWorldResponseHelloWorldResult(HelloWorldResponseMsg value) {
        return new JAXBElement<HelloWorldResponseMsg>(_HelloWorldResponseHelloWorldResult_QNAME, HelloWorldResponseMsg.class, HelloWorldResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HelloWorldData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://new.webservice.namespace", name = "req", scope = HelloWorld.class)
    public JAXBElement<HelloWorldData> createHelloWorldReq(HelloWorldData value) {
        return new JAXBElement<HelloWorldData>(_HelloWorldReq_QNAME, HelloWorldData.class, HelloWorld.class, value);
    }

}
