/**
 * InteropTestService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package samples.echo;

public interface InteropTestService extends javax.xml.rpc.Service {
    public java.lang.String getechoAddress();

    public samples.echo.InteropTestPortType getecho() throws javax.xml.rpc.ServiceException;

    public samples.echo.InteropTestPortType getecho(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
