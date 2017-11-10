/**
 * SOAPStructStruct.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package samples.echo;

public class SOAPStructStruct  implements java.io.Serializable {
    private java.lang.String varString;

    private int varInt;

    private float varFloat;

    private samples.echo.SOAPStruct varStruct;

    public SOAPStructStruct() {
    }

    public SOAPStructStruct(
           java.lang.String varString,
           int varInt,
           float varFloat,
           samples.echo.SOAPStruct varStruct) {
           this.varString = varString;
           this.varInt = varInt;
           this.varFloat = varFloat;
           this.varStruct = varStruct;
    }


    /**
     * Gets the varString value for this SOAPStructStruct.
     * 
     * @return varString
     */
    public java.lang.String getVarString() {
        return varString;
    }


    /**
     * Sets the varString value for this SOAPStructStruct.
     * 
     * @param varString
     */
    public void setVarString(java.lang.String varString) {
        this.varString = varString;
    }


    /**
     * Gets the varInt value for this SOAPStructStruct.
     * 
     * @return varInt
     */
    public int getVarInt() {
        return varInt;
    }


    /**
     * Sets the varInt value for this SOAPStructStruct.
     * 
     * @param varInt
     */
    public void setVarInt(int varInt) {
        this.varInt = varInt;
    }


    /**
     * Gets the varFloat value for this SOAPStructStruct.
     * 
     * @return varFloat
     */
    public float getVarFloat() {
        return varFloat;
    }


    /**
     * Sets the varFloat value for this SOAPStructStruct.
     * 
     * @param varFloat
     */
    public void setVarFloat(float varFloat) {
        this.varFloat = varFloat;
    }


    /**
     * Gets the varStruct value for this SOAPStructStruct.
     * 
     * @return varStruct
     */
    public samples.echo.SOAPStruct getVarStruct() {
        return varStruct;
    }


    /**
     * Sets the varStruct value for this SOAPStructStruct.
     * 
     * @param varStruct
     */
    public void setVarStruct(samples.echo.SOAPStruct varStruct) {
        this.varStruct = varStruct;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SOAPStructStruct)) return false;
        SOAPStructStruct other = (SOAPStructStruct) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.varString==null && other.getVarString()==null) || 
             (this.varString!=null &&
              this.varString.equals(other.getVarString()))) &&
            this.varInt == other.getVarInt() &&
            this.varFloat == other.getVarFloat() &&
            ((this.varStruct==null && other.getVarStruct()==null) || 
             (this.varStruct!=null &&
              this.varStruct.equals(other.getVarStruct())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getVarString() != null) {
            _hashCode += getVarString().hashCode();
        }
        _hashCode += getVarInt();
        _hashCode += new Float(getVarFloat()).hashCode();
        if (getVarStruct() != null) {
            _hashCode += getVarStruct().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SOAPStructStruct.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://soapinterop.org/xsd", "SOAPStructStruct"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varString");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varString"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varInt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varInt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varFloat");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varFloat"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("varStruct");
        elemField.setXmlName(new javax.xml.namespace.QName("", "varStruct"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://soapinterop.org/xsd", "SOAPStruct"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
