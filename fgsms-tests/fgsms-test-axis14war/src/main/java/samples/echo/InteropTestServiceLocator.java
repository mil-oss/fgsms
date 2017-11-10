/**
 * InteropTestServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package samples.echo;

public class InteropTestServiceLocator extends org.apache.axis.client.Service implements samples.echo.InteropTestService {

    public InteropTestServiceLocator() {
    }


    public InteropTestServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public InteropTestServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for echo
    private java.lang.String echo_address = "http://nagoya.apache.org:5049/axis/services/echo";

    public java.lang.String getechoAddress() {
        return echo_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String echoWSDDServiceName = "echo";

    public java.lang.String getechoWSDDServiceName() {
        return echoWSDDServiceName;
    }

    public void setechoWSDDServiceName(java.lang.String name) {
        echoWSDDServiceName = name;
    }

    public samples.echo.InteropTestPortType getecho() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(echo_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getecho(endpoint);
    }

    public samples.echo.InteropTestPortType getecho(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            samples.echo.InteropTestSoapBindingStub _stub = new samples.echo.InteropTestSoapBindingStub(portAddress, this);
            _stub.setPortName(getechoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setechoEndpointAddress(java.lang.String address) {
        echo_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (samples.echo.InteropTestPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                samples.echo.InteropTestSoapBindingStub _stub = new samples.echo.InteropTestSoapBindingStub(new java.net.URL(echo_address), this);
                _stub.setPortName(getechoWSDDServiceName());
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
        if ("echo".equals(inputPortName)) {
            return getecho();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://soapinterop.org/", "InteropTestService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://soapinterop.org/", "echo"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("echo".equals(portName)) {
            setechoEndpointAddress(address);
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
