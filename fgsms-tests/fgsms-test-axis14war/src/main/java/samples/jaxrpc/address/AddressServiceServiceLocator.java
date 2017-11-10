/**
 * AddressServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package samples.jaxrpc.address;

public class AddressServiceServiceLocator extends org.apache.axis.client.Service implements samples.jaxrpc.address.AddressServiceService {

    public AddressServiceServiceLocator() {
    }


    public AddressServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AddressServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Address
    private java.lang.String Address_address = "http://localhost:8080/axis/services/Address";

    public java.lang.String getAddressAddress() {
        return Address_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AddressWSDDServiceName = "Address";

    public java.lang.String getAddressWSDDServiceName() {
        return AddressWSDDServiceName;
    }

    public void setAddressWSDDServiceName(java.lang.String name) {
        AddressWSDDServiceName = name;
    }

    public samples.jaxrpc.address.AddressService getAddress() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Address_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAddress(endpoint);
    }

    public samples.jaxrpc.address.AddressService getAddress(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            samples.jaxrpc.address.AddressSoapBindingStub _stub = new samples.jaxrpc.address.AddressSoapBindingStub(portAddress, this);
            _stub.setPortName(getAddressWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAddressEndpointAddress(java.lang.String address) {
        Address_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (samples.jaxrpc.address.AddressService.class.isAssignableFrom(serviceEndpointInterface)) {
                samples.jaxrpc.address.AddressSoapBindingStub _stub = new samples.jaxrpc.address.AddressSoapBindingStub(new java.net.URL(Address_address), this);
                _stub.setPortName(getAddressWSDDServiceName());
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
        if ("Address".equals(inputPortName)) {
            return getAddress();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://address.jaxrpc.samples", "AddressServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://address.jaxrpc.samples", "Address"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Address".equals(portName)) {
            setAddressEndpointAddress(address);
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
