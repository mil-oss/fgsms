/*
 * Copyright 2015 alex.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.miloss.fgsms.services.pcs.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.policyconfiguration.OpStatusService;

/**
 *
 * @author alex
 */
@WebService(serviceName = "PolicyConfigurationService",
        name = "opStatusService",
        targetNamespace = "urn:org:miloss:fgsms:services:interfaces:opstatus"
)
public class OpStat implements OpStatusService {

     public OpStat() {
     }
     @Resource
    private WebServiceContext ctx;

     @WebMethod(operationName = "GetOperatingStatus", action = "urn:org:miloss:fgsms:services:interfaces:opStatusService/GetOperatingStatus")
     @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common")
     @RequestWrapper(localName = "GetOperatingStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common", className = "org.miloss.fgsms.services.interfaces.common.GetOperatingStatus")
     @ResponseWrapper(localName = "GetOperatingStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common", className = "org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponse")
     public GetOperatingStatusResponseMessage getOperatingStatus(
             @WebParam(name = "request", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:common") GetOperatingStatusRequestMessage request) {

          try {
               return new PCS4jBean(ctx).getOperatingStatus(request);
          } catch (DatatypeConfigurationException ex) {
               Logger.getLogger(OpStat.class.getName()).log(Level.SEVERE, null, ex);
          }
          return null;
     }

}
