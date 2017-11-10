
package org.tempuri;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.tempuri package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CallDependantServiceResponseCallDependantServiceResult_QNAME = new QName("http://tempuri.org/", "CallDependantServiceResult");
    private final static QName _FailingGetDataResponseFailingGetDataResult_QNAME = new QName("http://tempuri.org/", "FailingGetDataResult");
    private final static QName _WorkingGetDataResponseWorkingGetDataResult_QNAME = new QName("http://tempuri.org/", "WorkingGetDataResult");
    private final static QName _RandomWorkingMethodResponseRandomWorkingMethodResult_QNAME = new QName("http://tempuri.org/", "RandomWorkingMethodResult");
    private final static QName _LongRunningGetDataResponseLongRunningGetDataResult_QNAME = new QName("http://tempuri.org/", "LongRunningGetDataResult");
    private final static QName _CallWCFDependantServiceResponseCallWCFDependantServiceResult_QNAME = new QName("http://tempuri.org/", "CallWCFDependantServiceResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.tempuri
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LongRunningGetDataResponse }
     * 
     */
    public LongRunningGetDataResponse createLongRunningGetDataResponse() {
        return new LongRunningGetDataResponse();
    }

    /**
     * Create an instance of {@link CallWCFDependantServiceResponse }
     * 
     */
    public CallWCFDependantServiceResponse createCallWCFDependantServiceResponse() {
        return new CallWCFDependantServiceResponse();
    }

    /**
     * Create an instance of {@link WorkingGetDataResponse }
     * 
     */
    public WorkingGetDataResponse createWorkingGetDataResponse() {
        return new WorkingGetDataResponse();
    }

    /**
     * Create an instance of {@link CallDependantServiceResponse }
     * 
     */
    public CallDependantServiceResponse createCallDependantServiceResponse() {
        return new CallDependantServiceResponse();
    }

    /**
     * Create an instance of {@link LongRunningGetData }
     * 
     */
    public LongRunningGetData createLongRunningGetData() {
        return new LongRunningGetData();
    }

    /**
     * Create an instance of {@link OneWayMethodMethod }
     * 
     */
    public OneWayMethodMethod createOneWayMethodMethod() {
        return new OneWayMethodMethod();
    }

    /**
     * Create an instance of {@link RandomWorkingMethod }
     * 
     */
    public RandomWorkingMethod createRandomWorkingMethod() {
        return new RandomWorkingMethod();
    }

    /**
     * Create an instance of {@link CallWCFDependantService }
     * 
     */
    public CallWCFDependantService createCallWCFDependantService() {
        return new CallWCFDependantService();
    }

    /**
     * Create an instance of {@link FailingGetData }
     * 
     */
    public FailingGetData createFailingGetData() {
        return new FailingGetData();
    }

    /**
     * Create an instance of {@link RandomWorkingMethodResponse }
     * 
     */
    public RandomWorkingMethodResponse createRandomWorkingMethodResponse() {
        return new RandomWorkingMethodResponse();
    }

    /**
     * Create an instance of {@link CallDependantService }
     * 
     */
    public CallDependantService createCallDependantService() {
        return new CallDependantService();
    }

    /**
     * Create an instance of {@link FailingGetDataResponse }
     * 
     */
    public FailingGetDataResponse createFailingGetDataResponse() {
        return new FailingGetDataResponse();
    }

    /**
     * Create an instance of {@link WorkingGetData }
     * 
     */
    public WorkingGetData createWorkingGetData() {
        return new WorkingGetData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CallDependantServiceResult", scope = CallDependantServiceResponse.class)
    public JAXBElement<String> createCallDependantServiceResponseCallDependantServiceResult(String value) {
        return new JAXBElement<String>(_CallDependantServiceResponseCallDependantServiceResult_QNAME, String.class, CallDependantServiceResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "FailingGetDataResult", scope = FailingGetDataResponse.class)
    public JAXBElement<String> createFailingGetDataResponseFailingGetDataResult(String value) {
        return new JAXBElement<String>(_FailingGetDataResponseFailingGetDataResult_QNAME, String.class, FailingGetDataResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "WorkingGetDataResult", scope = WorkingGetDataResponse.class)
    public JAXBElement<String> createWorkingGetDataResponseWorkingGetDataResult(String value) {
        return new JAXBElement<String>(_WorkingGetDataResponseWorkingGetDataResult_QNAME, String.class, WorkingGetDataResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "RandomWorkingMethodResult", scope = RandomWorkingMethodResponse.class)
    public JAXBElement<String> createRandomWorkingMethodResponseRandomWorkingMethodResult(String value) {
        return new JAXBElement<String>(_RandomWorkingMethodResponseRandomWorkingMethodResult_QNAME, String.class, RandomWorkingMethodResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "LongRunningGetDataResult", scope = LongRunningGetDataResponse.class)
    public JAXBElement<String> createLongRunningGetDataResponseLongRunningGetDataResult(String value) {
        return new JAXBElement<String>(_LongRunningGetDataResponseLongRunningGetDataResult_QNAME, String.class, LongRunningGetDataResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "CallWCFDependantServiceResult", scope = CallWCFDependantServiceResponse.class)
    public JAXBElement<String> createCallWCFDependantServiceResponseCallWCFDependantServiceResult(String value) {
        return new JAXBElement<String>(_CallWCFDependantServiceResponseCallWCFDependantServiceResult_QNAME, String.class, CallWCFDependantServiceResponse.class, value);
    }

}
