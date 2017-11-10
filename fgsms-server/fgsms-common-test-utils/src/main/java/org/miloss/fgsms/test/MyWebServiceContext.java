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
package org.miloss.fgsms.test;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.*;
import javax.xml.ws.handler.MessageContext;
import org.w3c.dom.*;

/**
 *
 * @author AO
 */
public class MyWebServiceContext implements WebServiceContext, javax.xml.ws.handler.soap.SOAPMessageContext {

    public MyWebServiceContext() {
    }

    public MyWebServiceContext(MyMessageContext mc, String username) {
        here = mc;
        uname = username;
    }
    public MyMessageContext here;
    public String uname;

    public MyWebServiceContext(String username) {
        uname = username;
    }
    public QName header = new QName("http://docs.oasis-open.org/wsdm/muws1-2.xsd", "ResourceID");
    public QName wsa = new QName("http://www.w3.org/2005/08/addressing", "Action");
    public String targeturl = null;

    @Override
    public MessageContext getMessageContext() {
        return this;
    }

    public Principal getUserPrincipal() {
        return new SimplePrincipal(uname);
    }

    public boolean isUserInRole(String role) {
        return false;
    }

    public EndpointReference getEndpointReference(Element... referenceParameters) {
        return null;
    }

    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
        return null;
    }

    @Override
    public SOAPMessage getMessage() {
        return new SOAPMessage() {

            @Override
            public void setContentDescription(String description) {
            }

            @Override
            public String getContentDescription() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public SOAPHeader getSOAPHeader() throws SOAPException {
                return new SOAPHeader() {

                    @Override
                    public SOAPHeaderElement addHeaderElement(Name name) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPHeaderElement addHeaderElement(QName qname) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator examineMustUnderstandHeaderElements(String actor) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator examineHeaderElements(String actor) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator extractHeaderElements(String actor) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPHeaderElement addNotUnderstoodHeaderElement(QName name) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPHeaderElement addUpgradeHeaderElement(Iterator supportedSOAPURIs) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPHeaderElement addUpgradeHeaderElement(String supportedSoapUri) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator examineAllHeaderElements() {
                        List<SOAPHeader> headers = new ArrayList<SOAPHeader>();
                        headers.add(new SOAPHeader() {

                            @Override
                            public SOAPHeaderElement addHeaderElement(Name name) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addHeaderElement(QName qname) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator examineMustUnderstandHeaderElements(String actor) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator examineHeaderElements(String actor) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator extractHeaderElements(String actor) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addNotUnderstoodHeaderElement(QName name) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addUpgradeHeaderElement(Iterator supportedSOAPURIs) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addUpgradeHeaderElement(String supportedSoapUri) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator examineAllHeaderElements() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator extractAllHeaderElements() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(Name name) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(QName qname) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(String localName) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void removeContents() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addTextNode(String text) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addAttribute(Name name, String value) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addAttribute(QName qname, String value) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addNamespaceDeclaration(String prefix, String uri) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttributeValue(Name name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttributeValue(QName qname) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getAllAttributes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getAllAttributesAsQNames() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNamespaceURI(String prefix) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getNamespacePrefixes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getVisibleNamespacePrefixes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public QName createQName(String localName, String prefix) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Name getElementName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public QName getElementQName() {
                                return header;
                            }

                            @Override
                            public SOAPElement setElementQName(QName newName) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean removeAttribute(Name name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean removeAttribute(QName qname) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean removeNamespaceDeclaration(String prefix) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getChildElements() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getChildElements(Name name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getChildElements(QName qname) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setEncodingStyle(String encodingStyle) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getEncodingStyle() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getValue() {
                                return targeturl;
                            }

                            @Override
                            public void setValue(String value) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setParentElement(SOAPElement parent) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement getParentElement() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void detachNode() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void recycleNode() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNodeName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNodeValue() throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setNodeValue(String nodeValue) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public short getNodeType() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public javax.xml.soap.Node getParentNode() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NodeList getChildNodes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public javax.xml.soap.Node getFirstChild() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public javax.xml.soap.Node getLastChild() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public javax.xml.soap.Node getPreviousSibling() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public javax.xml.soap.Node getNextSibling() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NamedNodeMap getAttributes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Document getOwnerDocument() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasChildNodes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public javax.xml.soap.Node cloneNode(boolean deep) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void normalize() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isSupported(String feature, String version) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNamespaceURI() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getPrefix() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setPrefix(String prefix) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getLocalName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasAttributes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getBaseURI() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getTextContent() throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setTextContent(String textContent) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isSameNode(org.w3c.dom.Node other) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String lookupPrefix(String namespaceURI) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isDefaultNamespace(String namespaceURI) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String lookupNamespaceURI(String prefix) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isEqualNode(org.w3c.dom.Node arg) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Object getFeature(String feature, String version) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Object setUserData(String key, Object data, UserDataHandler handler) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Object getUserData(String key) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getTagName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttribute(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setAttribute(String name, String value) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void removeAttribute(String name) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr getAttributeNode(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr setAttributeNode(Attr newAttr) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NodeList getElementsByTagName(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasAttribute(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public TypeInfo getSchemaTypeInfo() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setIdAttribute(String name, boolean isId) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }
                        });


                        headers.add(new SOAPHeader() {

                            @Override
                            public SOAPHeaderElement addHeaderElement(Name name) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addHeaderElement(QName qname) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator examineMustUnderstandHeaderElements(String actor) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator examineHeaderElements(String actor) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator extractHeaderElements(String actor) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addNotUnderstoodHeaderElement(QName name) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addUpgradeHeaderElement(Iterator supportedSOAPURIs) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPHeaderElement addUpgradeHeaderElement(String supportedSoapUri) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator examineAllHeaderElements() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator extractAllHeaderElements() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(Name name) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(QName qname) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(String localName) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void removeContents() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addTextNode(String text) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addAttribute(Name name, String value) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addAttribute(QName qname, String value) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement addNamespaceDeclaration(String prefix, String uri) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttributeValue(Name name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttributeValue(QName qname) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getAllAttributes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getAllAttributesAsQNames() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNamespaceURI(String prefix) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getNamespacePrefixes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getVisibleNamespacePrefixes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public QName createQName(String localName, String prefix) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Name getElementName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public QName getElementQName() {
                               return wsa;
                            }

                            @Override
                            public SOAPElement setElementQName(QName newName) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean removeAttribute(Name name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean removeAttribute(QName qname) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean removeNamespaceDeclaration(String prefix) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getChildElements() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getChildElements(Name name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Iterator getChildElements(QName qname) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setEncodingStyle(String encodingStyle) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getEncodingStyle() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getValue() {
                                return "http://docs.oasis-open.org/wsrf/rpw-2/GetMultipleResourceProperties/GetMultipleResourcePropertiesRequest";
                            }

                            @Override
                            public void setValue(String value) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setParentElement(SOAPElement parent) throws SOAPException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public SOAPElement getParentElement() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void detachNode() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void recycleNode() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNodeName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNodeValue() throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setNodeValue(String nodeValue) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public short getNodeType() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node getParentNode() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NodeList getChildNodes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node getFirstChild() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node getLastChild() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node getPreviousSibling() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node getNextSibling() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NamedNodeMap getAttributes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Document getOwnerDocument() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasChildNodes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public org.w3c.dom.Node cloneNode(boolean deep) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void normalize() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isSupported(String feature, String version) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getNamespaceURI() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getPrefix() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setPrefix(String prefix) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getLocalName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasAttributes() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getBaseURI() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getTextContent() throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setTextContent(String textContent) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isSameNode(org.w3c.dom.Node other) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String lookupPrefix(String namespaceURI) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isDefaultNamespace(String namespaceURI) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String lookupNamespaceURI(String prefix) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean isEqualNode(org.w3c.dom.Node arg) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Object getFeature(String feature, String version) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Object setUserData(String key, Object data, UserDataHandler handler) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Object getUserData(String key) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getTagName() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttribute(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setAttribute(String name, String value) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void removeAttribute(String name) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr getAttributeNode(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr setAttributeNode(Attr newAttr) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NodeList getElementsByTagName(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasAttribute(String name) {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public TypeInfo getSchemaTypeInfo() {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setIdAttribute(String name, boolean isId) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }

                            @Override
                            public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
                                throw new UnsupportedOperationException("Not supported yet.");
                            }
                        });

                        return headers.iterator();
                    }

                    @Override
                    public Iterator extractAllHeaderElements() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addChildElement(Name name) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addChildElement(QName qname) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addChildElement(String localName) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void removeContents() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addTextNode(String text) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addAttribute(Name name, String value) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addAttribute(QName qname, String value) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement addNamespaceDeclaration(String prefix, String uri) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getAttributeValue(Name name) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getAttributeValue(QName qname) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator getAllAttributes() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator getAllAttributesAsQNames() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getNamespaceURI(String prefix) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator getNamespacePrefixes() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator getVisibleNamespacePrefixes() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public QName createQName(String localName, String prefix) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Name getElementName() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public QName getElementQName() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement setElementQName(QName newName) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean removeAttribute(Name name) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean removeAttribute(QName qname) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean removeNamespaceDeclaration(String prefix) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator getChildElements() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator getChildElements(Name name) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Iterator getChildElements(QName qname) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setEncodingStyle(String encodingStyle) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getEncodingStyle() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getValue() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setValue(String value) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setParentElement(SOAPElement parent) throws SOAPException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public SOAPElement getParentElement() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void detachNode() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void recycleNode() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getNodeName() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getNodeValue() throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setNodeValue(String nodeValue) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public short getNodeType() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node getParentNode() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public NodeList getChildNodes() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node getFirstChild() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node getLastChild() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node getPreviousSibling() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node getNextSibling() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public NamedNodeMap getAttributes() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Document getOwnerDocument() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean hasChildNodes() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public org.w3c.dom.Node cloneNode(boolean deep) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void normalize() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean isSupported(String feature, String version) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getNamespaceURI() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getPrefix() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setPrefix(String prefix) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getLocalName() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean hasAttributes() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getBaseURI() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getTextContent() throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setTextContent(String textContent) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean isSameNode(org.w3c.dom.Node other) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String lookupPrefix(String namespaceURI) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean isDefaultNamespace(String namespaceURI) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String lookupNamespaceURI(String prefix) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean isEqualNode(org.w3c.dom.Node arg) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Object getFeature(String feature, String version) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Object setUserData(String key, Object data, UserDataHandler handler) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Object getUserData(String key) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getTagName() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getAttribute(String name) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setAttribute(String name, String value) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void removeAttribute(String name) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Attr getAttributeNode(String name) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Attr setAttributeNode(Attr newAttr) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public NodeList getElementsByTagName(String name) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean hasAttribute(String name) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public TypeInfo getSchemaTypeInfo() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setIdAttribute(String name, boolean isId) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }

            @Override
            public SOAPPart getSOAPPart() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void removeAllAttachments() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public int countAttachments() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Iterator getAttachments() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Iterator getAttachments(MimeHeaders headers) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void removeAttachments(MimeHeaders headers) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public AttachmentPart getAttachment(SOAPElement element) throws SOAPException {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addAttachmentPart(AttachmentPart AttachmentPart) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public AttachmentPart createAttachmentPart() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public MimeHeaders getMimeHeaders() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void saveChanges() throws SOAPException {
            }

            @Override
            public boolean saveRequired() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void writeTo(OutputStream out) throws SOAPException, IOException {
            }
        };
    }

    @Override
    public void setMessage(SOAPMessage message) {
    }

    @Override
    public Object[] getHeaders(QName header, JAXBContext context, boolean allRoles) {
        return null;
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override
    public void setScope(String name, MessageContext.Scope scope) {
    }

    @Override
    public MessageContext.Scope getScope(String name) {
        return null;
    }

    @Override
    public int size() {
        return here.size();
    }

    @Override
    public boolean isEmpty() {
        return here.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return here.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return here.containsValue(value);
    }

    @Override
    public Object get(Object key) {
       return here.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return here.put(key, value);
    }

    @Override
    public Object remove(Object key) {
          return here.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
    }

    @Override
    public void clear() {
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Object> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return null;
    }
}
