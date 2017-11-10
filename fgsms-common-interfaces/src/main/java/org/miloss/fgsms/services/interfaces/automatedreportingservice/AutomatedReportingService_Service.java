
package org.miloss.fgsms.services.interfaces.automatedreportingservice;

import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * Automated Reporting Service 
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "AutomatedReportingService", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:automatedReportingService"
        //, wsdlLocation = "classpath:/ARSv1.wsdl"
)
public class AutomatedReportingService_Service
    extends Service
{

    private static URL AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService_Service.class.getName());

    
      static {
           ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION = cl.getResource("ARSv1.wsdl");
        } else {
            AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION = AutomatedReportingService_Service.class.getClassLoader().getResource("ARSv1.wsdl");
        }
        if (AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION==null) {
            try{
            AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION = AutomatedReportingService_Service.class.getClassLoader().getParent().getResource("ARSv1.wsdl");
            }catch (Exception ex){
            }
        }
        if (AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION==null) {
            AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION = AutomatedReportingService_Service.class.getClassLoader().getResource("/META-INF/wsdl/ARSv1.wsdl");
        }
        if (AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION==null) {
            AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION = AutomatedReportingService_Service.class.getClassLoader().getResource("META-INF/wsdl/ARSv1.wsdl");
        }
    }
    
    public AutomatedReportingService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public AutomatedReportingService_Service() {
        super(AUTOMATEDREPORTINGSERVICE_WSDL_LOCATION, new QName("urn:org:miloss:fgsms:services:interfaces:automatedReportingService", "AutomatedReportingService"));
    }

    /**
     * Automated Reporting Service Port
     * 
     * @return
     *     returns AutomatedReportingService
     */
    @WebEndpoint(name = "automatedReportingServicePort")
    public AutomatedReportingService getAutomatedReportingServicePort() {
        return super.getPort(new QName("urn:org:miloss:fgsms:services:interfaces:automatedReportingService", "automatedReportingServicePort"), AutomatedReportingService.class);
    }

    /**
     * 
     * @return
     *     returns OpStatusService
     */
    @WebEndpoint(name = "opStatusServiceBinding")
    public OpStatusService getOpStatusServiceBinding() {
        return super.getPort(new QName("urn:org:miloss:fgsms:services:interfaces:automatedReportingService", "opStatusServiceBinding"), OpStatusService.class);
    }

}
