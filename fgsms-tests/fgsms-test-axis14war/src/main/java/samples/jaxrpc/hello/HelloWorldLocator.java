/**
 * HelloWorldLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package samples.jaxrpc.hello;

public class HelloWorldLocator extends org.apache.axis.client.Service implements samples.jaxrpc.hello.HelloWorld {

    public HelloWorldLocator() {
    }


    public HelloWorldLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public HelloWorldLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for HelloPort
    private java.lang.String HelloPort_address = "http://localhost:8080/axis/Hello";

    public java.lang.String getHelloPortAddress() {
        return HelloPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String HelloPortWSDDServiceName = "HelloPort";

    public java.lang.String getHelloPortWSDDServiceName() {
        return HelloPortWSDDServiceName;
    }

    public void setHelloPortWSDDServiceName(java.lang.String name) {
        HelloPortWSDDServiceName = name;
    }

    public samples.jaxrpc.hello.Hello getHelloPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(HelloPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getHelloPort(endpoint);
    }

    public samples.jaxrpc.hello.Hello getHelloPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            samples.jaxrpc.hello.HelloBindingStub _stub = new samples.jaxrpc.hello.HelloBindingStub(portAddress, this);
            _stub.setPortName(getHelloPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setHelloPortEndpointAddress(java.lang.String address) {
        HelloPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (samples.jaxrpc.hello.Hello.class.isAssignableFrom(serviceEndpointInterface)) {
                samples.jaxrpc.hello.HelloBindingStub _stub = new samples.jaxrpc.hello.HelloBindingStub(new java.net.URL(HelloPort_address), this);
                _stub.setPortName(getHelloPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("HelloPort".equals(inputPortName)) {
            return getHelloPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://hello.jaxrpc.samples/", "HelloWorld");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://hello.jaxrpc.samples/", "HelloPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("HelloPort".equals(portName)) {
            setHelloPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
