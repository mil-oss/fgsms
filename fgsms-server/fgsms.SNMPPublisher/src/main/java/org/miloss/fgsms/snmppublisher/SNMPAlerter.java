/*
 * Copyright 2015 alex.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.miloss.fgsms.snmppublisher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.QuickStatWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;

/**
 *
 * @author alex
 */
public class SNMPAlerter implements org.miloss.fgsms.plugins.federation.FederationInterface {

     protected static final org.apache.log4j.Logger log = Logger.getLogger("fgsms.SNMPAlerter");
     static final String rootOid = ".1.3.6.1.4.1.8072.1.3.2.4.1.2.7.128.128.83";
     static final String quickStatOid = ".1";
     static final String quickStatUrlOid = ".1";
     static final String quickStatActionOid = ".2";
     static final String quickStatDurationOid = ".3";
     static org.snmp4j.Snmp snmp = null;

     @Override
     public boolean ValidateConfiguration(FederationPolicy fp, AtomicReference<String> outMessage) {
          return true;
     }

     @Override
     public synchronized void Publish(boolean pooled, QuickStatWrapper data, ServicePolicy sp, FederationPolicy fp) {
          if (snmp == null) {
               snmp = new Snmp();
          }
          PDU pdu = new PDU();
          pdu.setType(PDU.SET);
          for (int i = 0; i < data.getQuickStatData().size(); i++) {
               try {
                    OID oid = new OID(rootOid + quickStatOid + quickStatUrlOid);
                    oid.setValue(sp.getURL());
                  //  sp.get
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + quickStatActionOid);
                    oid.setValue(data.getAction());
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + quickStatDurationOid);
                    oid.setValue(data.getQuickStatData().get(i).getTimeInMinutes().intValue() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + quickStatDurationOid);
                    oid.setValue(data.getQuickStatData().get(i).getTimeInMinutes().intValue() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".4");
                    oid.setValue(data.getQuickStatData().get(i).getSuccessCount() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".5");
                    oid.setValue(data.getQuickStatData().get(i).getFailureCount() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".6");
                    oid.setValue(data.getQuickStatData().get(i).getMaximumRequestSize() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".7");
                    oid.setValue(data.getQuickStatData().get(i).getMaximumResponseSize() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".8");
                    oid.setValue(data.getQuickStatData().get(i).getMaximumResponseTime() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".9");
                    oid.setValue(data.getQuickStatData().get(i).getSLAViolationCount() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".10");
                    oid.setValue(data.getQuickStatData().get(i).getAverageResponseTime() + "");
                    pdu.add(new VariableBinding(oid));

                    oid = new OID(rootOid + quickStatOid + ".11");
                    oid.setValue(data.getQuickStatData().get(i).getAvailabilityPercentage() + "");
                    pdu.add(new VariableBinding(oid));

                    if (data.getQuickStatData().get(i).getMTBF() != null) {
                         oid = new OID(rootOid + quickStatOid + ".12");
                         oid.setValue(Utility.DurationToString(data.getQuickStatData().get(i).getMTBF()));
                         pdu.add(new VariableBinding(oid));
                    }

                    oid = new OID(rootOid + quickStatOid + ".13");
                    oid.setValue(Utility.formatDateTime(data.getQuickStatData().get(i).getUpdatedAt()));
                    pdu.add(new VariableBinding(oid));

                    if (data.getQuickStatData().get(i).getAverageCPUUsage() != null) {
                         oid = new OID(rootOid + quickStatOid + ".14");
                         oid.setValue(data.getQuickStatData().get(i).getAverageCPUUsage().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }

                    if (data.getQuickStatData().get(i).getAverageMemoryUsage() != null) {
                         oid = new OID(rootOid + quickStatOid + ".15");
                         oid.setValue(data.getQuickStatData().get(i).getAverageMemoryUsage().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }

                    if (data.getQuickStatData().get(i).getAverageThreadCount() != null) {
                         oid = new OID(rootOid + quickStatOid + ".16");
                         oid.setValue(data.getQuickStatData().get(i).getAverageThreadCount().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }

                    if (data.getQuickStatData().get(i).getAverageOpenFileCount() != null) {
                         oid = new OID(rootOid + quickStatOid + ".17");
                         oid.setValue(data.getQuickStatData().get(i).getAverageOpenFileCount().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }

                    if (data.getQuickStatData().get(i).getAverageChannelCount() != null) {
                         oid = new OID(rootOid + quickStatOid + ".18");
                         oid.setValue(data.getQuickStatData().get(i).getAverageChannelCount().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }

                    if (data.getQuickStatData().get(i).getLargestQueueDepth() != null) {
                         oid = new OID(rootOid + quickStatOid + ".19");
                         oid.setValue(data.getQuickStatData().get(i).getLargestQueueDepth().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }

                    if (data.getQuickStatData().get(i).getAverageMessagesIn() != null) {
                         oid = new OID(rootOid + quickStatOid + ".20");
                         oid.setValue(data.getQuickStatData().get(i).getAverageMessagesIn().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }
                    if (data.getQuickStatData().get(i).getAverageMessagesOut() != null) {
                         oid = new OID(rootOid + quickStatOid + ".21");
                         oid.setValue(data.getQuickStatData().get(i).getAverageMessagesOut().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }
                    if (data.getQuickStatData().get(i).getAverageMessagesDropped() != null) {
                         oid = new OID(rootOid + quickStatOid + ".22");
                         oid.setValue(data.getQuickStatData().get(i).getAverageMessagesDropped().floatValue() + "");
                         pdu.add(new VariableBinding(oid));
                    }

                    //FIXME change this to be configurable
                    CommunityTarget target = new CommunityTarget();
                    target.setCommunity(new OctetString("public"));
                    target.setRetries(1);
                    target.setTimeout(1000);
                    target.setAddress(new UdpAddress(161));
                    snmp.send(pdu, target);
               } catch (IOException ex) {
                    log.log(org.apache.log4j.Level.ERROR, "Unable to SNMP publish : URLs (i/p/s) " + sp.getURL(), ex);

               }
          }

     }

     @Override
     public String GetDisplayName() {
          return "SNMP Publisher";
     }

     @Override
     public String GetHtmlFormattedHelp() {
          return "The SNMP Publisher is a federation target that enables publication of statistics on a periodic basis.";
     }

     @Override
     public List<NameValuePair> GetRequiredParameters() {
          return Collections.EMPTY_LIST;
     }

     @Override
     public List<NameValuePair> GetOptionalParameters() {
          return Collections.EMPTY_LIST;
     }

     @Override
     public List<PolicyType> GetAppliesTo() {
          return Utility.getAllPolicyTypes();
     }

}
