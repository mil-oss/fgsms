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

package org.miloss.fgsms.plugins.sla.alertservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mil.army.cerdec.fgsms.plugins.sla.alertservice package. 
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

    private final static QName _RecieveServiceAlert_QNAME = new QName("urn:mil:army:cerdec:fgsms:plugins:sla:alertservice", "RecieveServiceAlert");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mil.army.cerdec.fgsms.plugins.sla.alertservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RecieveServiceAlert }
     * 
     */
    public RecieveServiceAlert createRecieveServiceAlert() {
        return new RecieveServiceAlert();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecieveServiceAlert }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:mil:army:cerdec:fgsms:plugins:sla:alertservice", name = "RecieveServiceAlert")
    public JAXBElement<RecieveServiceAlert> createRecieveServiceAlert(RecieveServiceAlert value) {
        return new JAXBElement<RecieveServiceAlert>(_RecieveServiceAlert_QNAME, RecieveServiceAlert.class, null, value);
    }

}
