/**
 * AddressServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package samples.jaxrpc.address;

public interface AddressServiceService extends javax.xml.rpc.Service {
    public java.lang.String getAddressAddress();

    public samples.jaxrpc.address.AddressService getAddress() throws javax.xml.rpc.ServiceException;

    public samples.jaxrpc.address.AddressService getAddress(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
