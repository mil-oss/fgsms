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
package org.miloss.fgsms.smoke.sunri;;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.Handler;
import org.tempuri.IService1;
import org.tempuri.Service1;

/**
 *
 * @author AO
 */
public class FgsmsSmokeTest2 {

    public static void main(String[] args) throws InterruptedException {
        try {
            new FgsmsSmokeTest().main(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void PrintUsage() {
        System.out.println("Usage");
        System.out.println("\tjava -jar SmokeTestSunRI client (url) method\t\t=== run the client");
        System.out.println("\tjava -jar SmokeTestSunRI client_monitored (url) method\t\t=== run the client with fgsms agent");
        System.out.print("Available Methods: ");
        for (int i = 0; i < Supported_Methods.values().length; i++) {
            System.out.print(Supported_Methods.values()[i] + " ");
        }
        System.out.println();
        System.out.println();
        System.out.println("\tjava -jar SmokeTestSunRI service (url)\t\t=== run the service");
        System.out.println("\tjava -jar SmokeTestSunRI service_monitored (url)\t\t=== run the service with fgsms agent");
    }

    private void StartService(String url) throws InterruptedException {
        Endpoint ep = Endpoint.create(new org.tempuri.Service1Impl());
        ep.publish(url);

        if (!ep.isPublished()) {
            System.out.println("unable to start the service at " + url + "  !!");
            System.exit(1);
        }
        System.out.println("Service started....Press Ctrl-C to exit");
        while (true) {
            Thread.sleep(1000);
        }
        //    Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());

    }

    private void main2(String[] args) throws InterruptedException {
        if (args == null || args.length < 2) {
            PrintUsage();
            return;
        }
        if (args[0].equalsIgnoreCase("client")) {
            if (args.length == 3) {
                RunClient(args[1], Supported_Methods.valueOf(args[2]));
            } else {
                PrintUsage();
            }
        } else if (args[0].equalsIgnoreCase("client_monitored")) {
            if (args.length == 3) {
                RunClientWithMonitor(args[1], Supported_Methods.valueOf(args[2]));
            } else {
                PrintUsage();
            }
        } else if (args[0].equalsIgnoreCase("service")) {
            StartService(args[1]);
        } else if (args[0].equalsIgnoreCase("service_monitored")) {
            StartServiceMonitored(args[1]);
        } else {
            PrintUsage();
        }
    }

    enum Supported_Methods {

        workingGetData, failingGetData, longRunningGetData, randomWorkingMethod
    }

    private void RunClient(String url, Supported_Methods method) {
        Service1 client = new Service1(Thread.currentThread().getContextClassLoader().getResource("META-INF/Service1.wsdl"), new QName("http://tempuri.org/", "Service1"));
        IService1 port = client.getBasicHttpBindingIService1();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        CallClient(port, method);
    }

    void CallClient(IService1 port, Supported_Methods method) {
        String result = null;
        long start = System.currentTimeMillis();
        try {
            switch (method) {
                case workingGetData:
                    result = port.workingGetData(5);
                    break;
                case failingGetData:
                    result = port.failingGetData(5);
                    break;
                case longRunningGetData:
                    result = port.longRunningGetData(5);
                    break;
                case randomWorkingMethod:
                    result = port.randomWorkingMethod(5);
                    break;
            }
            System.out.println("Success in " + (System.currentTimeMillis() - start) + "ms, response was " + result);
        } catch (Exception e) {
            System.out.println("Failed in " + (System.currentTimeMillis() - start) + "ms, response was ");
            e.printStackTrace();
        }
    }

    private void RunClientWithMonitor(String url, Supported_Methods method) {
        Service1 client = new Service1(Thread.currentThread().getContextClassLoader().getResource("META-INF/Service1.wsdl"), new QName("http://tempuri.org/", "Service1"));
        IService1 port = client.getBasicHttpBindingIService1();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        List<Handler> hsc = new ArrayList<Handler>();
        hsc.add(new org.miloss.fgsms.agents.JAXWSGenericClientAgent());
        bp.getBinding().setHandlerChain(hsc);
        CallClient(port, method);
    }

    private void StartServiceMonitored(String url) throws InterruptedException {
        Endpoint ep = Endpoint.create(new org.tempuri.Service1Impl());
        List<Handler> hs = new ArrayList<Handler>();
        hs.add(new org.miloss.fgsms.agents.JAXWSGenericAgent());
        ep.getBinding().setHandlerChain(hs);
        ep.publish(url);

        if (!ep.isPublished()) {
            System.out.println("unable to start the service at " + url + "  !!");
            System.exit(1);
        }
        System.out.println("Press Ctrl-C to exit");
        while (true) {
            Thread.sleep(1000);
        }
    }
}
