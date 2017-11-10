/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * <p>
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.
 * <p>
 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.tempuri;

import com.sun.net.httpserver.HttpExchange;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.miloss.fgsms.agentcore.MessageProcessor;

/**
 *
 * @author AO
 */
//@javax.ejb.Stateless
@WebService(name = "IService1", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
        org.tempuri.ObjectFactory.class,
        com.microsoft.schemas._2003._10.serialization.ObjectFactory.class
})
public class Service1Impl implements IService1 {
    static Logger log = Logger.getAnonymousLogger();


    @Resource
    private WebServiceContext ctx;

    void Info(String function) {
        MessageContext messageContext = ctx.getMessageContext();
        if (messageContext.containsKey("com.sun.xml.internal.ws.http.exchange")) {
            try {
                HttpExchange exchg = (HttpExchange) messageContext.get("com.sun.xml.internal.ws.http.exchange");

                String requestURL = ((exchg.getProtocol().toLowerCase().startsWith("https")) ? "https:/" : "http:/") + exchg.getLocalAddress().toString() + exchg.getRequestURI().toString();
                System.out.println(System.currentTimeMillis() + " Recieved request from " + exchg.getRemoteAddress().toString() + " to " + requestURL + " for " + function);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    /**
     *
     * @param value
     * @return returns java.lang.String
     */

    @WebMethod(operationName = "WorkingGetData", action = "http://tempuri.org/IService1/WorkingGetData")
    @WebResult(name = "WorkingGetDataResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "WorkingGetData", targetNamespace = "http://tempuri.org/", className = "org.tempuri.WorkingGetData")
    @ResponseWrapper(localName = "WorkingGetDataResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.WorkingGetDataResponse")
    public String workingGetData(
            @WebParam(name = "value", targetNamespace = "http://tempuri.org/") Integer value) {


        MessageProcessor.getSingletonObject().purgePolicyCache();

        log.log(Level.INFO, " workingGetData");
        Info("workingGetData");


        return value.toString();
    }

    /**
     *
     * @param value
     * @return returns java.lang.String
     */
    @WebMethod(operationName = "CallDependantService", action = "http://tempuri.org/IService1/CallDependantService")
    @WebResult(name = "CallDependantServiceResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "CallDependantService", targetNamespace = "http://tempuri.org/", className = "org.tempuri.CallDependantService")
    @ResponseWrapper(localName = "CallDependantServiceResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.CallDependantServiceResponse")
    public String callDependantService(
            @WebParam(name = "value", targetNamespace = "http://tempuri.org/") Integer value) {
        log.log(Level.INFO, " callDependantService");
        MessageProcessor.getSingletonObject().purgePolicyCache();
        Info("callDependantService");
        System.out.println("callDependantService");
        return null;
    }

    /**
     *
     * @param value
     * @return returns java.lang.String
     */
    @WebMethod(operationName = "CallWCFDependantService", action = "http://tempuri.org/IService1/CallWCFDependantService")
    @WebResult(name = "CallWCFDependantServiceResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "CallWCFDependantService", targetNamespace = "http://tempuri.org/", className = "org.tempuri.CallWCFDependantService")
    @ResponseWrapper(localName = "CallWCFDependantServiceResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.CallWCFDependantServiceResponse")
    public String callWCFDependantService(
            @WebParam(name = "value", targetNamespace = "http://tempuri.org/") Integer value) {
        log.log(Level.INFO, " callWCFDependantService");
        MessageProcessor.getSingletonObject().purgePolicyCache();
        System.out.println("callWCFDependantService");
        Info("callWCFDependantService");
        //TODO
        return null;
    }

    /**
     *
     * @param value
     * @return returns java.lang.String
     */
    @WebMethod(operationName = "FailingGetData", action = "http://tempuri.org/IService1/FailingGetData")
    @WebResult(name = "FailingGetDataResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "FailingGetData", targetNamespace = "http://tempuri.org/", className = "org.tempuri.FailingGetData")
    @ResponseWrapper(localName = "FailingGetDataResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.FailingGetDataResponse")
    public String failingGetData(
            @WebParam(name = "value", targetNamespace = "http://tempuri.org/") Integer value) {
        log.log(Level.INFO, " failingGetData");

        MessageProcessor.getSingletonObject().purgePolicyCache();
        System.out.println("failingGetData");
        Info("failingGetData");
        throw new IllegalArgumentException();

    }

    /**
     *
     * @param value
     * @return returns java.lang.String
     */
    @WebMethod(operationName = "LongRunningGetData", action = "http://tempuri.org/IService1/LongRunningGetData")
    @WebResult(name = "LongRunningGetDataResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "LongRunningGetData", targetNamespace = "http://tempuri.org/", className = "org.tempuri.LongRunningGetData")
    @ResponseWrapper(localName = "LongRunningGetDataResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.LongRunningGetDataResponse")
    public String longRunningGetData(
            @WebParam(name = "value", targetNamespace = "http://tempuri.org/") Integer value) {
        log.log(Level.INFO, " longRunningGetData");
        MessageProcessor.getSingletonObject().purgePolicyCache();
        System.out.println("longRunningGetData");
        Info("longRunningGetData");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Service1Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value.toString();
    }

    /**
     *
     * @param value
     * @return returns java.lang.String
     */
    @WebMethod(operationName = "RandomWorkingMethod", action = "http://tempuri.org/IService1/RandomWorkingMethod")
    @WebResult(name = "RandomWorkingMethodResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "RandomWorkingMethod", targetNamespace = "http://tempuri.org/", className = "org.tempuri.RandomWorkingMethod")
    @ResponseWrapper(localName = "RandomWorkingMethodResponse", targetNamespace = "http://tempuri.org/", className = "org.tempuri.RandomWorkingMethodResponse")
    public String randomWorkingMethod(
            @WebParam(name = "value", targetNamespace = "http://tempuri.org/") Integer value) {
        log.log(Level.INFO, " randomWorkingMethod");
        MessageProcessor.getSingletonObject().purgePolicyCache();
        System.out.println("randomWorkingMethod");
        Info("randomWorkingMethod");
        int x = (int) Math.round(Math.random());
        if (x == 1) {
            return value.toString();
        }
        throw new IllegalArgumentException();

    }


    @WebMethod(operationName = "OneWayMethod", action = "http://tempuri.org/IService1/OneWayMethod")
    @Oneway
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @Override
    public void oneWayMethod(OneWayMethodMethod parameters) {
        MessageProcessor.getSingletonObject().purgePolicyCache();
        log.log(Level.INFO, " oneWayMethod");
        System.out.println("oneWayMethod");
    }
}
