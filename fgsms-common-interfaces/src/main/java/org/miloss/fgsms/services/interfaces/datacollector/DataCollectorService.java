
package org.miloss.fgsms.services.interfaces.datacollector;

import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;


/**
 * DCS Service
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.0
 * 
 */
@WebServiceClient(name = "DataCollectorService", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:dataCollector"
        //,         wsdlLocation = "classpath:/DCS8.wsdl"
)
public class DataCollectorService
    extends Service
{

    private static URL DATACOLLECTORSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.miloss.fgsms.services.interfaces.datacollector.DataCollectorService.class.getName());

      static {
           ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            DATACOLLECTORSERVICE_WSDL_LOCATION = cl.getResource("DCS8.wsdl");
        } else {
            DATACOLLECTORSERVICE_WSDL_LOCATION = DataCollectorService.class.getClassLoader().getResource("DCS8.wsdl");
        }
       
        if (DATACOLLECTORSERVICE_WSDL_LOCATION==null) {
            try{
            DATACOLLECTORSERVICE_WSDL_LOCATION = DataCollectorService.class.getClassLoader().getParent().getResource("DCS8.wsdl");
            }catch (Exception ex){
            }
        }
        if (DATACOLLECTORSERVICE_WSDL_LOCATION==null) {
            DATACOLLECTORSERVICE_WSDL_LOCATION = DataCollectorService.class.getClassLoader().getResource("/META-INF/wsdl/DCS8.wsdl");
        }
        if (DATACOLLECTORSERVICE_WSDL_LOCATION==null) {
            DATACOLLECTORSERVICE_WSDL_LOCATION = DataCollectorService.class.getClassLoader().getResource("META-INF/wsdl/DCS8.wsdl");
        }
    }

    public DataCollectorService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public DataCollectorService() {
        super(DATACOLLECTORSERVICE_WSDL_LOCATION, new QName("urn:org:miloss:fgsms:services:interfaces:dataCollector", "DataCollectorService"));
    }

    /**
     * DCS Port
     * 
     * @return
     *     returns DCS
     */
    @WebEndpoint(name = "DCSPort")
    public DCS getDCSPort() {
        return super.getPort(new QName("urn:org:miloss:fgsms:services:interfaces:dataCollector", "DCSPort"), DCS.class);
    }

    /**
     * 
     * @return
     *     returns OpStatusService
     */
    @WebEndpoint(name = "opStatusServiceBinding")
    public OpStatusService getOpStatusServiceBinding() {
        return super.getPort(new QName("urn:org:miloss:fgsms:services:interfaces:dataCollector", "opStatusServiceBinding"), OpStatusService.class);
    }

}