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

package org.oasis_open.docs.wsrf.rp_2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;



/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oasis_open.docs.wsrf.rp_2 package. 
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

    private final static QName _InvalidModificationFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "InvalidModificationFault");
    private final static QName _InvalidQueryExpressionFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "InvalidQueryExpressionFault");
    private final static QName _UnknownQueryExpressionDialectFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "UnknownQueryExpressionDialectFault");
    private final static QName _UnableToPutResourcePropertyDocumentFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "UnableToPutResourcePropertyDocumentFault");
    private final static QName _Update_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "Update");
    private final static QName _QueryEvaluationErrorFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "QueryEvaluationErrorFault");
    private final static QName _InvalidResourcePropertyQNameFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "InvalidResourcePropertyQNameFault");
    private final static QName _UpdateResourcePropertiesRequestFailedFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "UpdateResourcePropertiesRequestFailedFault");
    private final static QName _QueryExpressionDialect_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "QueryExpressionDialect");
    private final static QName _DeleteResourcePropertiesRequestFailedFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "DeleteResourcePropertiesRequestFailedFault");
    private final static QName _ResourcePropertyValueChangeNotification_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "ResourcePropertyValueChangeNotification");
    private final static QName _InsertResourcePropertiesRequestFailedFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "InsertResourcePropertiesRequestFailedFault");
    private final static QName _SetResourcePropertyRequestFailedFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "SetResourcePropertyRequestFailedFault");
    private final static QName _GetResourceProperty_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "GetResourceProperty");
    private final static QName _Insert_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "Insert");
    private final static QName _UnableToModifyResourcePropertyFault_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "UnableToModifyResourcePropertyFault");
    private final static QName _GetMultipleResourcePropertiesResponse_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "GetMultipleResourcePropertiesResponse");
    private final static QName _QueryExpression_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "QueryExpression");
    private final static QName _Delete_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "Delete");
    private final static QName _ResourcePropertyValueChangeNotificationTypeOldValues_QNAME = new QName("http://docs.oasis-open.org/wsrf/rp-2", "OldValues");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oasis_open.docs.wsrf.rp_2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InsertResourceProperties }
     * 
     */
    public InsertResourceProperties createInsertResourceProperties() {
        return new InsertResourceProperties();
    }

    /**
     * Create an instance of {@link InsertType }
     * 
     */
    public InsertType createInsertType() {
        return new InsertType();
    }

    /**
     * Create an instance of {@link UnableToPutResourcePropertyDocumentFaultType }
     * 
     */
    public UnableToPutResourcePropertyDocumentFaultType createUnableToPutResourcePropertyDocumentFaultType() {
        return new UnableToPutResourcePropertyDocumentFaultType();
    }

    /**
     * Create an instance of {@link QueryResourcePropertiesResponse }
     * 
     */
    public QueryResourcePropertiesResponse createQueryResourcePropertiesResponse() {
        return new QueryResourcePropertiesResponse();
    }

    /**
     * Create an instance of {@link PutResourcePropertyDocumentResponse }
     * 
     */
    public PutResourcePropertyDocumentResponse createPutResourcePropertyDocumentResponse() {
        return new PutResourcePropertyDocumentResponse();
    }

    /**
     * Create an instance of {@link UpdateResourcePropertiesResponse }
     * 
     */
    public UpdateResourcePropertiesResponse createUpdateResourcePropertiesResponse() {
        return new UpdateResourcePropertiesResponse();
    }

    /**
     * Create an instance of {@link ResourcePropertyChangeFailureType }
     * 
     */
    public ResourcePropertyChangeFailureType createResourcePropertyChangeFailureType() {
        return new ResourcePropertyChangeFailureType();
    }

    /**
     * Create an instance of {@link QueryEvaluationErrorFaultType }
     * 
     */
    public QueryEvaluationErrorFaultType createQueryEvaluationErrorFaultType() {
        return new QueryEvaluationErrorFaultType();
    }

    /**
     * Create an instance of {@link InsertResourcePropertiesResponse }
     * 
     */
    public InsertResourcePropertiesResponse createInsertResourcePropertiesResponse() {
        return new InsertResourcePropertiesResponse();
    }

    /**
     * Create an instance of {@link SetResourceProperties }
     * 
     */
    public SetResourceProperties createSetResourceProperties() {
        return new SetResourceProperties();
    }

    /**
     * Create an instance of {@link SetResourcePropertiesResponse }
     * 
     */
    public SetResourcePropertiesResponse createSetResourcePropertiesResponse() {
        return new SetResourcePropertiesResponse();
    }

    /**
     * Create an instance of {@link InvalidQueryExpressionFaultType }
     * 
     */
    public InvalidQueryExpressionFaultType createInvalidQueryExpressionFaultType() {
        return new InvalidQueryExpressionFaultType();
    }

    /**
     * Create an instance of {@link DeleteResourceProperties }
     * 
     */
    public DeleteResourceProperties createDeleteResourceProperties() {
        return new DeleteResourceProperties();
    }

    /**
     * Create an instance of {@link UpdateType }
     * 
     */
    public UpdateType createUpdateType() {
        return new UpdateType();
    }

    /**
     * Create an instance of {@link DeleteResourcePropertiesRequestFailedFaultType }
     * 
     */
    public DeleteResourcePropertiesRequestFailedFaultType createDeleteResourcePropertiesRequestFailedFaultType() {
        return new DeleteResourcePropertiesRequestFailedFaultType();
    }

    /**
     * Create an instance of {@link InvalidResourcePropertyQNameFaultType }
     * 
     */
    public InvalidResourcePropertyQNameFaultType createInvalidResourcePropertyQNameFaultType() {
        return new InvalidResourcePropertyQNameFaultType();
    }

    /**
     * Create an instance of {@link GetResourcePropertyDocument }
     * 
     */
    public GetResourcePropertyDocument createGetResourcePropertyDocument() {
        return new GetResourcePropertyDocument();
    }

    /**
     * Create an instance of {@link QueryExpressionRPDocument }
     * 
     */
    public QueryExpressionRPDocument createQueryExpressionRPDocument() {
        return new QueryExpressionRPDocument();
    }

    /**
     * Create an instance of {@link DeleteType }
     * 
     */
    public DeleteType createDeleteType() {
        return new DeleteType();
    }

    /**
     * Create an instance of {@link InvalidModificationFaultType }
     * 
     */
    public InvalidModificationFaultType createInvalidModificationFaultType() {
        return new InvalidModificationFaultType();
    }

    /**
     * Create an instance of {@link GetResourcePropertyResponse }
     * 
     */
    public GetResourcePropertyResponse createGetResourcePropertyResponse() {
        return new GetResourcePropertyResponse();
    }

    /**
     * Create an instance of {@link UpdateResourcePropertiesRequestFailedFaultType }
     * 
     */
    public UpdateResourcePropertiesRequestFailedFaultType createUpdateResourcePropertiesRequestFailedFaultType() {
        return new UpdateResourcePropertiesRequestFailedFaultType();
    }

    /**
     * Create an instance of {@link InsertResourcePropertiesRequestFailedFaultType }
     * 
     */
    public InsertResourcePropertiesRequestFailedFaultType createInsertResourcePropertiesRequestFailedFaultType() {
        return new InsertResourcePropertiesRequestFailedFaultType();
    }

    /**
     * Create an instance of {@link QueryExpressionType }
     * 
     */
    public QueryExpressionType createQueryExpressionType() {
        return new QueryExpressionType();
    }

    /**
     * Create an instance of {@link UnableToModifyResourcePropertyFaultType }
     * 
     */
    public UnableToModifyResourcePropertyFaultType createUnableToModifyResourcePropertyFaultType() {
        return new UnableToModifyResourcePropertyFaultType();
    }

    /**
     * Create an instance of {@link GetMultipleResourceProperties }
     * 
     */
    public GetMultipleResourceProperties createGetMultipleResourceProperties() {
        return new GetMultipleResourceProperties();
    }

    /**
     * Create an instance of {@link SetResourcePropertyRequestFailedFaultType }
     * 
     */
    public SetResourcePropertyRequestFailedFaultType createSetResourcePropertyRequestFailedFaultType() {
        return new SetResourcePropertyRequestFailedFaultType();
    }

    /**
     * Create an instance of {@link UpdateResourceProperties }
     * 
     */
    public UpdateResourceProperties createUpdateResourceProperties() {
        return new UpdateResourceProperties();
    }

    /**
     * Create an instance of {@link ResourcePropertyValueChangeNotificationType }
     * 
     */
    public ResourcePropertyValueChangeNotificationType createResourcePropertyValueChangeNotificationType() {
        return new ResourcePropertyValueChangeNotificationType();
    }

    /**
     * Create an instance of {@link PutResourcePropertyDocument }
     * 
     */
    public PutResourcePropertyDocument createPutResourcePropertyDocument() {
        return new PutResourcePropertyDocument();
    }

    /**
     * Create an instance of {@link ResourcePropertyChangeFailureType.RequestedValue }
     * 
     */
    public ResourcePropertyChangeFailureType.RequestedValue createResourcePropertyChangeFailureTypeRequestedValue() {
        return new ResourcePropertyChangeFailureType.RequestedValue();
    }

    /**
     * Create an instance of {@link UnknownQueryExpressionDialectFaultType }
     * 
     */
    public UnknownQueryExpressionDialectFaultType createUnknownQueryExpressionDialectFaultType() {
        return new UnknownQueryExpressionDialectFaultType();
    }

    /**
     * Create an instance of {@link DeleteResourcePropertiesResponse }
     * 
     */
    public DeleteResourcePropertiesResponse createDeleteResourcePropertiesResponse() {
        return new DeleteResourcePropertiesResponse();
    }

    /**
     * Create an instance of {@link GetResourcePropertyDocumentResponse }
     * 
     */
    public GetResourcePropertyDocumentResponse createGetResourcePropertyDocumentResponse() {
        return new GetResourcePropertyDocumentResponse();
    }

    /**
     * Create an instance of {@link ResourcePropertyChangeFailureType.CurrentValue }
     * 
     */
    public ResourcePropertyChangeFailureType.CurrentValue createResourcePropertyChangeFailureTypeCurrentValue() {
        return new ResourcePropertyChangeFailureType.CurrentValue();
    }

    /**
     * Create an instance of {@link QueryResourceProperties }
     * 
     */
    public QueryResourceProperties createQueryResourceProperties() {
        return new QueryResourceProperties();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidModificationFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "InvalidModificationFault")
    public JAXBElement<InvalidModificationFaultType> createInvalidModificationFault(InvalidModificationFaultType value) {
        return new JAXBElement<InvalidModificationFaultType>(_InvalidModificationFault_QNAME, InvalidModificationFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidQueryExpressionFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "InvalidQueryExpressionFault")
    public JAXBElement<InvalidQueryExpressionFaultType> createInvalidQueryExpressionFault(InvalidQueryExpressionFaultType value) {
        return new JAXBElement<InvalidQueryExpressionFaultType>(_InvalidQueryExpressionFault_QNAME, InvalidQueryExpressionFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnknownQueryExpressionDialectFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "UnknownQueryExpressionDialectFault")
    public JAXBElement<UnknownQueryExpressionDialectFaultType> createUnknownQueryExpressionDialectFault(UnknownQueryExpressionDialectFaultType value) {
        return new JAXBElement<UnknownQueryExpressionDialectFaultType>(_UnknownQueryExpressionDialectFault_QNAME, UnknownQueryExpressionDialectFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnableToPutResourcePropertyDocumentFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "UnableToPutResourcePropertyDocumentFault")
    public JAXBElement<UnableToPutResourcePropertyDocumentFaultType> createUnableToPutResourcePropertyDocumentFault(UnableToPutResourcePropertyDocumentFaultType value) {
        return new JAXBElement<UnableToPutResourcePropertyDocumentFaultType>(_UnableToPutResourcePropertyDocumentFault_QNAME, UnableToPutResourcePropertyDocumentFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "Update")
    public JAXBElement<UpdateType> createUpdate(UpdateType value) {
        return new JAXBElement<UpdateType>(_Update_QNAME, UpdateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryEvaluationErrorFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "QueryEvaluationErrorFault")
    public JAXBElement<QueryEvaluationErrorFaultType> createQueryEvaluationErrorFault(QueryEvaluationErrorFaultType value) {
        return new JAXBElement<QueryEvaluationErrorFaultType>(_QueryEvaluationErrorFault_QNAME, QueryEvaluationErrorFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidResourcePropertyQNameFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "InvalidResourcePropertyQNameFault")
    public JAXBElement<InvalidResourcePropertyQNameFaultType> createInvalidResourcePropertyQNameFault(InvalidResourcePropertyQNameFaultType value) {
        return new JAXBElement<InvalidResourcePropertyQNameFaultType>(_InvalidResourcePropertyQNameFault_QNAME, InvalidResourcePropertyQNameFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateResourcePropertiesRequestFailedFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "UpdateResourcePropertiesRequestFailedFault")
    public JAXBElement<UpdateResourcePropertiesRequestFailedFaultType> createUpdateResourcePropertiesRequestFailedFault(UpdateResourcePropertiesRequestFailedFaultType value) {
        return new JAXBElement<UpdateResourcePropertiesRequestFailedFaultType>(_UpdateResourcePropertiesRequestFailedFault_QNAME, UpdateResourcePropertiesRequestFailedFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "QueryExpressionDialect")
    public JAXBElement<String> createQueryExpressionDialect(String value) {
        return new JAXBElement<String>(_QueryExpressionDialect_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteResourcePropertiesRequestFailedFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "DeleteResourcePropertiesRequestFailedFault")
    public JAXBElement<DeleteResourcePropertiesRequestFailedFaultType> createDeleteResourcePropertiesRequestFailedFault(DeleteResourcePropertiesRequestFailedFaultType value) {
        return new JAXBElement<DeleteResourcePropertiesRequestFailedFaultType>(_DeleteResourcePropertiesRequestFailedFault_QNAME, DeleteResourcePropertiesRequestFailedFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResourcePropertyValueChangeNotificationType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "ResourcePropertyValueChangeNotification")
    public JAXBElement<ResourcePropertyValueChangeNotificationType> createResourcePropertyValueChangeNotification(ResourcePropertyValueChangeNotificationType value) {
        return new JAXBElement<ResourcePropertyValueChangeNotificationType>(_ResourcePropertyValueChangeNotification_QNAME, ResourcePropertyValueChangeNotificationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertResourcePropertiesRequestFailedFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "InsertResourcePropertiesRequestFailedFault")
    public JAXBElement<InsertResourcePropertiesRequestFailedFaultType> createInsertResourcePropertiesRequestFailedFault(InsertResourcePropertiesRequestFailedFaultType value) {
        return new JAXBElement<InsertResourcePropertiesRequestFailedFaultType>(_InsertResourcePropertiesRequestFailedFault_QNAME, InsertResourcePropertiesRequestFailedFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetResourcePropertyRequestFailedFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "SetResourcePropertyRequestFailedFault")
    public JAXBElement<SetResourcePropertyRequestFailedFaultType> createSetResourcePropertyRequestFailedFault(SetResourcePropertyRequestFailedFaultType value) {
        return new JAXBElement<SetResourcePropertyRequestFailedFaultType>(_SetResourcePropertyRequestFailedFault_QNAME, SetResourcePropertyRequestFailedFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "GetResourceProperty")
    public JAXBElement<QName> createGetResourceProperty(QName value) {
        return new JAXBElement<QName>(_GetResourceProperty_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "Insert")
    public JAXBElement<InsertType> createInsert(InsertType value) {
        return new JAXBElement<InsertType>(_Insert_QNAME, InsertType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnableToModifyResourcePropertyFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "UnableToModifyResourcePropertyFault")
    public JAXBElement<UnableToModifyResourcePropertyFaultType> createUnableToModifyResourcePropertyFault(UnableToModifyResourcePropertyFaultType value) {
        return new JAXBElement<UnableToModifyResourcePropertyFaultType>(_UnableToModifyResourcePropertyFault_QNAME, UnableToModifyResourcePropertyFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EsmPropertiesType }{@code >}}
     * 

    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "GetMultipleResourcePropertiesResponse")
    public JAXBElement<EsmPropertiesType> createGetMultipleResourcePropertiesResponse(EsmPropertiesType value) {
        return new JAXBElement<EsmPropertiesType>(_GetMultipleResourcePropertiesResponse_QNAME, EsmPropertiesType.class, null, value);
    }     *

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryExpressionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "QueryExpression")
    public JAXBElement<QueryExpressionType> createQueryExpression(QueryExpressionType value) {
        return new JAXBElement<QueryExpressionType>(_QueryExpression_QNAME, QueryExpressionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "Delete")
    public JAXBElement<DeleteType> createDelete(DeleteType value) {
        return new JAXBElement<DeleteType>(_Delete_QNAME, DeleteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EsmPropertiesChoiceType }{@code >}}
     */

    @XmlElementDecl(namespace = "http://docs.oasis-open.org/wsrf/rp-2", name = "OldValues", scope = ResourcePropertyValueChangeNotificationType.class)
    public JAXBElement<Object> createResourcePropertyValueChangeNotificationTypeOldValues(Object value) {
        return new JAXBElement<Object>(_ResourcePropertyValueChangeNotificationTypeOldValues_QNAME, Object.class, ResourcePropertyValueChangeNotificationType.class, value);
    }     

}
