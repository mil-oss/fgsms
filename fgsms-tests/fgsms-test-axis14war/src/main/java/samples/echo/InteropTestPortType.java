/**
 * InteropTestPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package samples.echo;

public interface InteropTestPortType extends java.rmi.Remote {
    public java.lang.String echoString(java.lang.String inputString) throws java.rmi.RemoteException;
    public java.lang.String[] echoStringArray(java.lang.String[] inputStringArray) throws java.rmi.RemoteException;
    public int echoInteger(int inputInteger) throws java.rmi.RemoteException;
    public int[] echoIntegerArray(int[] inputIntegerArray) throws java.rmi.RemoteException;
    public float echoFloat(float inputFloat) throws java.rmi.RemoteException;
    public float[] echoFloatArray(float[] inputFloatArray) throws java.rmi.RemoteException;
    public samples.echo.SOAPStruct echoStruct(samples.echo.SOAPStruct inputStruct) throws java.rmi.RemoteException;
    public samples.echo.SOAPStruct[] echoStructArray(samples.echo.SOAPStruct[] inputStructArray) throws java.rmi.RemoteException;
    public void echoVoid() throws java.rmi.RemoteException;
    public byte[] echoBase64(byte[] inputBase64) throws java.rmi.RemoteException;
    public java.util.Calendar echoDate(java.util.Calendar inputDate) throws java.rmi.RemoteException;
    public byte[] echoHexBinary(byte[] inputHexBinary) throws java.rmi.RemoteException;
    public java.math.BigDecimal echoDecimal(java.math.BigDecimal inputDecimal) throws java.rmi.RemoteException;
    public boolean echoBoolean(boolean inputBoolean) throws java.rmi.RemoteException;
    public void echoStructAsSimpleTypes(samples.echo.SOAPStruct inputStruct, javax.xml.rpc.holders.StringHolder outputString, javax.xml.rpc.holders.IntHolder outputInteger, javax.xml.rpc.holders.FloatHolder outputFloat) throws java.rmi.RemoteException;
    public samples.echo.SOAPStruct echoSimpleTypesAsStruct(java.lang.String inputString, int inputInteger, float inputFloat) throws java.rmi.RemoteException;
    public java.lang.String[][] echo2DStringArray(java.lang.String[][] input2DStringArray) throws java.rmi.RemoteException;
    public samples.echo.SOAPStructStruct echoNestedStruct(samples.echo.SOAPStructStruct inputStruct) throws java.rmi.RemoteException;
    public samples.echo.SOAPArrayStruct echoNestedArray(samples.echo.SOAPArrayStruct inputStruct) throws java.rmi.RemoteException;
    public java.util.HashMap echoMap(java.util.HashMap input) throws java.rmi.RemoteException;
    public java.util.HashMap[] echoMapArray(java.util.HashMap[] input) throws java.rmi.RemoteException;
    public org.apache.axis.types.Token echoToken(org.apache.axis.types.Token inputToken) throws java.rmi.RemoteException;
    public org.apache.axis.types.NormalizedString echoNormalizedString(org.apache.axis.types.NormalizedString inputNormalizedString) throws java.rmi.RemoteException;
    public org.apache.axis.types.UnsignedLong echoUnsignedLong(org.apache.axis.types.UnsignedLong inputUnsignedLong) throws java.rmi.RemoteException;
    public org.apache.axis.types.UnsignedInt echoUnsignedInt(org.apache.axis.types.UnsignedInt inputUnsignedInt) throws java.rmi.RemoteException;
    public org.apache.axis.types.UnsignedShort echoUnsignedShort(org.apache.axis.types.UnsignedShort inputUnsignedShort) throws java.rmi.RemoteException;
    public org.apache.axis.types.UnsignedByte echoUnsignedByte(org.apache.axis.types.UnsignedByte inputUnsignedByte) throws java.rmi.RemoteException;
    public org.apache.axis.types.NonNegativeInteger echoNonNegativeInteger(org.apache.axis.types.NonNegativeInteger inputNonNegativeInteger) throws java.rmi.RemoteException;
    public org.apache.axis.types.PositiveInteger echoPositiveInteger(org.apache.axis.types.PositiveInteger inputPositiveInteger) throws java.rmi.RemoteException;
    public org.apache.axis.types.NonPositiveInteger echoNonPositiveInteger(org.apache.axis.types.NonPositiveInteger inputNonPositiveInteger) throws java.rmi.RemoteException;
    public org.apache.axis.types.NegativeInteger echoNegativeInteger(org.apache.axis.types.NegativeInteger inputNegativeInteger) throws java.rmi.RemoteException;
}
