
package org.miloss.fgsms.services.interfaces.status;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * Status Port Type
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.0
 * 
 */
@WebService(name = "statusService", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
public interface StatusService {


    /**
     * Set the status on more than one item. If a policy does not exist for this item, it will be automatically created
     * 
     * @param req
     * @return
     *     returns org.miloss.fgsms.services.interfaces.status.SetStatusResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetMoreStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/SetMoreStatus")
    @WebResult(name = "SetStatusMoreResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "SetMoreStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetMoreStatus")
    @ResponseWrapper(localName = "SetMoreStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetMoreStatusResponse")
    public SetStatusResponseMsg setMoreStatus(
        @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
        List<SetStatusRequestMsg> req)
        throws AccessDeniedException, ServiceUnavailableException
    ;

    /**
     * Set the Extended Status on a single item. If a policy does not exist for this item, it will be automatically created
     * 
     * @param req
     * @return
     *     returns org.miloss.fgsms.services.interfaces.status.SetStatusResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetExtendedStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/SetExtendedStatus")
    @WebResult(name = "SetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "SetExtendedStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetExtendedStatus")
    @ResponseWrapper(localName = "SetStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetStatusResponse")
    public SetStatusResponseMsg setExtendedStatus(
        @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
        SetExtendedStatusRequestMsg req)
        throws AccessDeniedException, ServiceUnavailableException
    ;

    /**
     * Set the Status on a single item. If a policy does not exist for this item, it will be automatically created
     * 
     * @param req
     * @return
     *     returns org.miloss.fgsms.services.interfaces.status.SetStatusResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "SetStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/SetStatus")
    @WebResult(name = "SetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "SetStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetStatus")
    @ResponseWrapper(localName = "SetStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.SetStatusResponse")
    public SetStatusResponseMsg setStatus(
        @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
        SetStatusRequestMsg req)
        throws AccessDeniedException, ServiceUnavailableException
    ;

    /**
     * Get the status on a single item
     * 
     * @param req
     * @return
     *     returns org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/GetStatus")
    @WebResult(name = "GetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "GetStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetStatus")
    @ResponseWrapper(localName = "GetStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetStatusResponse")
    public GetStatusResponseMsg getStatus(
        @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
        GetStatusRequestMsg req)
        throws AccessDeniedException, ServiceUnavailableException
    ;

    /**
     * Gets the status of all items that the requestor has access to
     * 
     * @param req
     * @return
     *     returns java.util.List<org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg>
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "GetAllStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/GetAllStatus")
    @WebResult(name = "GetStatusResponseResult", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "GetAllStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetAllStatus")
    @ResponseWrapper(localName = "GetAllStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.GetAllStatusResponse")
    public List<GetStatusResponseMsg> getAllStatus(
        @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
        GetStatusRequestMsg req)
        throws AccessDeniedException, ServiceUnavailableException
    ;

    /**
     * 
     * 			Removes an entry from the status listing, all associated history, SLAs, and alerting subscriptions, requires global admin rights
     * 			
     * 
     * @param req
     * @return
     *     returns org.miloss.fgsms.services.interfaces.status.RemoveStatusResponseMsg
     * @throws AccessDeniedException
     * @throws ServiceUnavailableException
     */
    @WebMethod(operationName = "RemoveStatus", action = "urn:org:miloss:fgsms:services:interfaces:statusService/RemoveStatus")
    @WebResult(name = "response", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
    @RequestWrapper(localName = "RemoveStatus", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.RemoveStatus")
    @ResponseWrapper(localName = "RemoveStatusResponse", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status", className = "org.miloss.fgsms.services.interfaces.status.RemoveStatusResponse")
    public RemoveStatusResponseMsg removeStatus(
        @WebParam(name = "req", targetNamespace = "urn:org:miloss:fgsms:services:interfaces:status")
        RemoveStatusRequestMsg req)
        throws AccessDeniedException, ServiceUnavailableException
    ;

}