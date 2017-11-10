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

package org.miloss.fgsms.services.interfaces.agentcallbackservice;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.miloss.fgsms.services.interfaces.agentcallbackservice package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.miloss.fgsms.services.interfaces.agentcallbackservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExecuteTasks }
     * 
     */
    public ExecuteTasks createExecuteTasks() {
        return new ExecuteTasks();
    }

    /**
     * Create an instance of {@link ExecuteTasksResponse }
     * 
     */
    public ExecuteTasksResponse createExecuteTasksResponse() {
        return new ExecuteTasksResponse();
    }

}
