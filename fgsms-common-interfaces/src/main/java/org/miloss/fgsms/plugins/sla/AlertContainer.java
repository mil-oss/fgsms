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
/*  ----------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Command
 *  Command, Power and Intergration Directorate
 *  ----------------------------------------------------------------------------
 *  fgsms - Government Open Source Software
 *  ----------------------------------------------------------------------------
 *  Author:     AO
 *	Website: 	https://github.com/mil-oss/fgsms
 *  ----------------------------------------------------------------------------
 */
package org.miloss.fgsms.plugins.sla;

import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.oasis_open.docs.wsdm.muws2_2.SituationCategoryType;

/**
 * A simple structure to make the processing of alerting
 * easier fgsms processes alerts asynchronously either via a
 * Singleton message processor that runs in a separate thread or via Jboss's
 * Work Manager, depending on what's available. This container specifies
 * everything necessary for an implementation action class to successfully
 * process the action.
 * 
 * @see org.miloss.fgsms.helper.SLACommon
 * @see org.miloss.fgsms.helper.SLAProcessorSingleton
 * @see javax.resource.spi.work.WorkManager
 *
 * @author AO
 * @since 6.0
 */
public class AlertContainer {

    /**
     * creates a new alert container
     *
     * @param faultMsg
     * @param htmlEncodedFaultMessage
     * @param modifiedurl the policy url
     * @param relatedMessageId a specific transaction that triggered the action, may be null
     * @param currentTimeMillis the time it was triggered
     * @param incidentid this is the id that will or has been recorded in the sla table of the performance database. this is useful for providing web links to get more information
     * @param pooled whether or not we are running in jboss and using jndi lookups for database connections
     * @param sendtoAdminsOnly typically only used for email alerts
     * @param slaActionBaseType this is a reference to the specific action being triggered in the SLA identified by SLAID. This is necessary as some Actions have context parameters
     * @param SLAID a unique id of the SLA
     * @param ServicePolicy of the thing this action was triggered on 
     * @param typeofalert This is the type of alert, generally speaking, a AvailabilitySituation or PerformanceReport
     * report
     *
     * @see org.oasis_open.docs.wsdm.muws2_2.PerformanceReport
     * @see org.oasis_open.docs.wsdm.muws2_2.AvailabilitySituation
     */
    public AlertContainer(String faultMsg, String htmlEncodedFaultMessage, String modifiedurl, String relatedMessageId, long currentTimeMillis, String incidentid, boolean pooled, boolean sendtoAdminsOnly, SLAAction slaActionBaseType, String SLAID, ServicePolicy t,
            SituationCategoryType typeofalert) {
        this.faultMsg = faultMsg;
        this.htmlEncodedFaultMessage = htmlEncodedFaultMessage;
        this.modifiedurl = modifiedurl;
        this.relatedMessageId = relatedMessageId;
        this.currentTimeMillis = currentTimeMillis;
        this.incidentid = incidentid;
        this.pooled = pooled;
        this.sendtoAdminsOnly = sendtoAdminsOnly;
        this.slaActionBaseType = slaActionBaseType;
        this.SLAID = SLAID;
        this.t = t;
        this.alerttype = typeofalert;
    }

    /**
     * This is the type of alert, generally speaking, a performance or status
     * report
     *
     * @see org.oasis_open.docs.wsdm.muws2_2.PerformanceReport
     * @see org.oasis_open.docs.wsdm.muws2_2.AvailabilitySituation
     */
    public SituationCategoryType getAlerttype() {
        return alerttype;
    }

    /**
     * The fault message is usually a high readable string
     */
    public String getFaultMsg() {
        return faultMsg;
    }

    /**
     * This is an html encoded fault message, usually with formatting for an
     * email message or something similar.
     */
    public String getHtmlEncodedFaultMessage() {
        return htmlEncodedFaultMessage;
    }

    /**
     * The url of the fgsms policy that this alert is related to
     */
    public String getModifiedurl() {
        return modifiedurl;
    }

    /**
     * This is the id of the transaction that caused the SLA rule to trigger.
     * There is no guarantee that this is defined. This ID is a UUID that may
     * relate to a web service transaction, an availability ID or something
     * else.
     */
    public String getRelatedMessageId() {
        return relatedMessageId;
    }

    /**
     * The time this even occurred. fgsms processes SLAs asynchronously
     */
    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    /**
     * a unique UUID of this alert, with is also recorded in the SLA table of
     * the performance database.
     */
    public String getIncidentid() {
        return incidentid;
    }

    /**
     * indicates that the current context is running inside of jboss, useful for
     * accessing database connections
     */
    public boolean isPooled() {
        return pooled;
    }

    /**
     * A reference to the action that will be processed by this alert container.
     * This is used for asynchronous processes, do not modify
     */
    public boolean isSendtoAdminsOnly() {
        return sendtoAdminsOnly;
    }

    /**
     * A reference to the action that will be processed by this alert container.
     * This is used for asynchronous processes, do not modify
     */
    public SLAAction getSlaActionBaseType() {
        return slaActionBaseType;
    }

    /**
     * this is the UUID of the SLA Rule/Action set. use this to find the
     * corresponding record in the policy for further processing
     */
    public String getSLAID() {
        return SLAID;
    }

    /**
     * a reference to the service policy for this object, do not modify it
     */
    public ServicePolicy getServicePolicy() {
        return t;
    }
    /**
     * This is the type of alert, generally speaking, a performance or status
     * report
     *
     * @see org.oasis_open.docs.wsdm.muws2_2.PerformanceReport
     * @see org.oasis_open.docs.wsdm.muws2_2.AvailabilitySituation
     */
    protected SituationCategoryType alerttype;
    /**
     * The fault message is usually a high readable string
     */
    protected String faultMsg;
    /**
     * This is an html encoded fault message, usually with formatting for an
     * email message or something similar.
     */
    protected String htmlEncodedFaultMessage;
    /**
     * The url of the fgsms policy that this alert is related to
     */
    protected String modifiedurl;
    /**
     * This is the id of the transaction that caused the SLA rule to trigger.
     * There is no guarantee that this is defined. This ID is a UUID that may
     * relate to a web service transaction, an availability ID or something
     * else.
     */
    protected String relatedMessageId;
    /**
     * The time this even occurred. fgsms processes SLAs asynchronously
     */
    protected long currentTimeMillis;
    /**
     * a unique UUID of this alert, with is also recorded in the SLA table of
     * the performance database.
     */
    protected String incidentid;
    /**
     * indicates that the current context is running inside of jboss, useful for
     * accessing database connections
     */
    protected boolean pooled;
    protected boolean sendtoAdminsOnly;
    /**
     * A reference to the action that will be processed by this alert container.
     * This is used for asynchronous processes, do not modify
     */
    protected SLAAction slaActionBaseType;
    /**
     * this is the UUID of the SLA Rule/Action set. use this to find the
     * corresponding record in the policy for further processing
     */
    protected String SLAID;
    /**
     * a reference to the service policy for this object, do not modify it
     */
    protected ServicePolicy t;
}
