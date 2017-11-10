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
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.examples.export;

import java.nio.file.AccessDeniedException;
import javax.naming.ServiceUnavailableException;
import javax.xml.bind.JAXB;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListRequestMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetRecentMessageLogsRequestMsg;
import us.gov.ic.ism.v2.ClassificationType;

public class ExportViaWS {

    public static void main(String args[]) throws AccessDeniedException, ServiceUnavailableException, Exception {
        if (args.length != 3) {
            System.out.println("Usage java -jar ExportDataFromViaWebService.jar <URL to DAS Service> <Admin username> <password>");
            return;
        }

        DataAccessService_Service svc = new DataAccessService_Service();
        DataAccessService dasPort = svc.getDASPort();
        BindingProvider bp = (BindingProvider) dasPort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, args[0]);
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, args[1]);
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, args[2]);

        //get the list of services that are monitored that "username" has at least read access to
        GetMonitoredServiceListRequestMsg req = new GetMonitoredServiceListRequestMsg();
        req.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        GetMonitoredServiceListResponseMsg response = dasPort.getMonitoredServiceList(req);
        if (response != null && response.getServiceList() != null && response.getServiceList() != null) {
            System.out.println(response.getServiceList().getServiceType().size() + " services found");
            for (int i = 0; i < response.getServiceList().getServiceType().size(); i++) {
                GetRecentMessageLogsRequestMsg req1 = new GetRecentMessageLogsRequestMsg();
                req1.setClassification(new SecurityWrapper(ClassificationType.U, ""));
                req1.setOffset(0);
                req1.setRecords(10);
                req1.setURL(response.getServiceList().getServiceType().get(i).getURL());
                GetMessageLogsResponseMsg recentMessageLogs = dasPort.getRecentMessageLogs(req1);
                //TODO loop the through the records and output what you want
                JAXB.marshal(recentMessageLogs, System.out);
            }
        } else {
            System.out.println("no services found");
        }

        //Get current status of a service
        //TODO
        //Get current statistics for a web service, availability for all others
        //TODO
    }
}
