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

package org.miloss.fgsms.presentation;

import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;


/**
 *Provides a simple container to hold configuration and proxy class instances for presentation layer uddi discovery
 * @author AO
 */
public class UDDIConfig {
         public UDDIInquiryPortType inquiry=null;
         public String inquiryendpoint="";
        public UDDISecurityPortType security=null;
        public String secendpoint="";
        
        public UDDIPublicationPortType publish=null;
        public String publishendpoint="";
        
        
        public boolean useUDDI = false;
        public boolean useHTTPUsernamePassword = false;
        public boolean useHTTPClientCert = false;

        public String username=null;
        public String encryptedPassword=null;
        public boolean IsjUDDI=false;
}
