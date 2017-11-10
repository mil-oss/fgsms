
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * Data Access Service for fgsms
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "dataAccessService", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataAccessService"
        //,         wsdlLocation = "classpath:/DASv8.wsdl"
)
public class DataAccessService_Service
    extends Service
{

    private static URL DATAACCESSSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service.class.getName());

      static {
           ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            DATAACCESSSERVICE_WSDL_LOCATION = cl.getResource("DASv8.wsdl");
        } else {
            DATAACCESSSERVICE_WSDL_LOCATION = DataAccessService_Service.class.getClassLoader().getResource("DASv8.wsdl");
        }
        if (DATAACCESSSERVICE_WSDL_LOCATION==null) {
            try{
            DATAACCESSSERVICE_WSDL_LOCATION = DataAccessService_Service.class.getClassLoader().getParent().getResource("DASv8.wsdl");
            }catch (Exception ex){
            }
        }
        if (DATAACCESSSERVICE_WSDL_LOCATION==null) {
            DATAACCESSSERVICE_WSDL_LOCATION = DataAccessService_Service.class.getClassLoader().getResource("/META-INF/wsdl/DASv8.wsdl");
        }
        if (DATAACCESSSERVICE_WSDL_LOCATION==null) {
            DATAACCESSSERVICE_WSDL_LOCATION = DataAccessService_Service.class.getClassLoader().getResource("META-INF/wsdl/DASv8.wsdl");
        }
    }


    public DataAccessService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DataAccessService_Service() {
        super(DATAACCESSSERVICE_WSDL_LOCATION, new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "dataAccessService"));
    }

    /**
     * Port definition, be sure to update the execution address as necessary.
     * 
     * @return
     *     returns DataAccessService
     */
    @WebEndpoint(name = "DASPort")
    public DataAccessService getDASPort() {
        return super.getPort(new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "DASPort"), DataAccessService.class);
    }

    /**
     * 
     * @return
     *     returns OpStatusService
     */
    @WebEndpoint(name = "opStatusServiceBinding")
    public OpStatusService getOpStatusServiceBinding() {
        return super.getPort(new QName("urn:org:miloss:fgsms:services:interfaces:dataAccessService", "opStatusServiceBinding"), OpStatusService.class);
    }

}