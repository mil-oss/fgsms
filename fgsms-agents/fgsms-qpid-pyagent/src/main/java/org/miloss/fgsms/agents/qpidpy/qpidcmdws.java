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

package org.miloss.fgsms.agents.qpidpy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.agentcore.PolicyFetch;
import org.miloss.fgsms.agentcore.StatisticalHelper;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;

/**
 * Redhat MRG/Apache Qpid C++ Broker agent Calls the
 * quid-stat2 python command (including with fgsms) to parse the output and
 * statististics from a C++ Qpid broker Then sends the data back via web service
 * call
 *
 * @author AO
 */
public class qpidcmdws {

    public qpidcmdws() {
    }
    private static final Logger log = Logger.getLogger("fgsms.QpidPython");

    private List<BrokerData> LoadExchangeData(String url) {
        //Logger log = Logger.getAnonymousLogger();
        String cmd = "./qpid-stat2 -e";
        Runtime run = Runtime.getRuntime();
        List<BrokerData> list = new ArrayList<BrokerData>();
        Process pr = null;
        try {
            pr = run.exec(cmd);
        } catch (IOException ex) {
            log.log(Level.WARN, null, ex);
            return null;
        }
        try {
            pr.waitFor();
        } catch (InterruptedException ex) {
            log.log(Level.WARN, null, ex);
            return null;
        }


        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";

        int count = 0;
        try {
            while ((line = buf.readLine()) != null) {
                if (count == 0) {
                    if (line.contains("Failed")) {

                        //AuxHelper.TryUpdateStatus(false, url, line, false, AuxHelper.UNSPECIFIED, SLACommon.getHostName());
                        log.log(Level.WARN, "broker at " + url + " is not available");
                        return null;
                    }
                }
                if (count > 2) {
                    //first item of exchanges
                    //name(may be null) type durable(bool may be null) bind msgIn msgOut dropped, bytein byteout bytedrop
                    line = line.trim();
                    //qpid.management topic
                    String[] data = line.split(",");
                    String name = "";
                    String type = "";
                    long msgdrop = 0, msgin = 0, msgout = 0, bytesdrop = 0, bytesin = 0, bytesout = 0, bind = 0;
                    if (data.length == 9) {
                        name = data[0].trim();
                        type = data[1].trim();
                        bind = parseText(data[2].trim());
                        msgin = parseText(data[3].trim());
                        msgout = parseText(data[4].trim());
                        msgdrop = parseText(data[5].trim());
                        bytesin = parseText(data[6].trim());
                        bytesout = parseText(data[7].trim());
                        bytesdrop = parseText(data[8].trim());
                        BrokerData d = new BrokerData();
                        d.setQueueOrTopicName(name);
                        d.setQueueOrTopicCanonicalName(url + name);
                        d.setActiveConsumers(bind);
                        d.setItemType(type);
                        d.setBytesDropped(bytesdrop);
                        d.setBytesIn(bytesin);
                        d.setBytesOut(bytesout);
                        d.setMessagesDropped(msgdrop);
                        d.setMessagesIn(msgin);
                        d.setMessagesOut(msgout);
                        d.setDepth(0);
                        list.add(d);
                    }

                }
                count++;
            }
        } catch (IOException ex) {
            log.log(Level.WARN, null, ex);
        }
        return list;
    }

    private Long parseText(String s) {
        Long l;
        Double d;
        String working = s.trim();
        if (s.contains("k") || s.contains("K")) {
            working = working.replace("k", "");
            working = working.replace("K", "");
            d = Double.parseDouble(working) * 1000;
            return d.longValue();
        } else if (s.contains("m") || s.contains("M")) {
            working = working.replace("m", "");
            working = working.replace("M", "");
            d = Double.parseDouble(working) * 1000000;
            return d.longValue();
        } else if (s.contains("g") || s.contains("G")) {
            working = working.replace("g", "");
            working = working.replace("G", "");
            d = Double.parseDouble(working) * 1000000000;
            return d.longValue();
        }

        return Long.parseLong(working);
    }

    private List<BrokerData> LoadQueueData(String url) {
        //Logger log = Logger.getAnonymousLogger();
        String cmd = "./qpid-stat2 -q";
        Runtime run = Runtime.getRuntime();
        Process pr = null;
        List<BrokerData> list = new ArrayList<BrokerData>();
        boolean ok = false;

        try {
            pr = run.exec(cmd);
            ok = true;
        } catch (IOException ex) {
            log.log(Level.WARN, null, ex);
            return null;
        }
        try {
            pr.waitFor();
            ok = true;
        } catch (InterruptedException ex) {
            log.log(Level.WARN, null, ex);
            return null;
        }

        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        int count = 0;
        try {
            while ((line = buf.readLine()) != null) {
                if (count == 0) {
                    if (line.contains("Failed")) {

                        //SetStatus(url, false, line,  perf,  con);
                        log.log(Level.WARN, "broker at " + url + " is not available");
                        pr.destroy();
                        pr = null;
                        return null;
                    }
                }
                if (count > 2) {
                    String[] data = line.split(",");
                    String name = "";
                    long msg = 0, msgin = 0, msgout = 0, bytes = 0, bytesin = 0, bytesout = 0, cons = 0, bind = 0;
                    if (data.length == 9) {
                        name = data[0].trim();
                        msg = parseText(data[1]);//message/queue depth
                        msgin = parseText(data[2]);//total enqueues
                        msgout = parseText(data[3]);//total dequeues
                        bytes = parseText(data[4]);//bytes depth
                        bytesin = parseText(data[5]);//bytes enqueue
                        bytesout = parseText(data[6]);//bytes dequeue
                        cons = parseText(data[7]);//consumer
                        bind = parseText(data[8]);//bindings
                        BrokerData d = new BrokerData();
                        d.setActiveConsumers(bind);
                        d.setTotalConsumers(cons);
                        d.setBytesIn(bytesin);
                        d.setBytesOut(bytesout);
                        d.setItemType("queue");
                        d.setMessagesIn(msgin);
                        d.setMessagesOut(msgout);
                        d.setQueueOrTopicName(name);
                        d.setQueueOrTopicCanonicalName(url + name);
                        d.setDepth(msg);
                        list.add(d);
                    }
                }
                count++;
            }

        } catch (IOException ex) {
            log.log(Level.WARN, null, ex);
        }
        try {
            pr.destroy();

            pr = null;
        } catch (Exception ex) {
        }
        return list;
    }

    void Fire(String url) throws ConfigurationException {
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        req.setBrokerURI(url);
        req.setBrokerHostname(Utility.getHostName());
        req.setAgentType(this.getClass().getCanonicalName());
        //req.setClassification(cfg.getClasslevel());
        req.setDomain("unspecified");

        req.setOperationalStatus(true);
        req.setOperationalStatusMessage("OK");
        List<BrokerData> d = this.LoadExchangeData(url);
        List<BrokerData> d1 = (this.LoadQueueData(url));
        if (d == null && d1 == null) {
            req.setOperationalStatus(false);
            req.setOperationalStatusMessage("Broker is offline");
        }
        if (d != null) {
            req.getData().addAll(d);
        }


        StatisticalHelper.send(req);
        StatisticalServicePolicy pol = (StatisticalServicePolicy) PolicyFetch.TryFetchPolicy(url, PolicyType.STATISTICAL, "unspecified", Utility.getHostName());
        if (pol != null) {
            ProcessStatisticalAgentActions(pol, req);

        }


    }

    private List<SLA> GetAgentActions(ArrayOfSLA serviceLevelAggrements) {
        if (serviceLevelAggrements == null) {
            return null;
        }
        
        if (serviceLevelAggrements.getSLA().isEmpty()) {
            return null;
        }
        List<SLA> ret = new ArrayList<SLA>();
        for (int i = 0; i < serviceLevelAggrements.getSLA().size(); i++) {
            if (serviceLevelAggrements.getSLA().get(i).getAction() != null) {
                if (serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction() != null) {
                    for (int k = 0; k < serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().size(); k++) {
                        if (serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().get(k).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRestart")) {
                            ret.add(serviceLevelAggrements.getSLA().get(i));
                        }
                        if (serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().get(k).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRunScript")) {
                            // SLAActionRunScript action = (SLAActionRunScript) serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().get(k);
                            NameValuePair runat = Utility.getNameValuePairByName(serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().get(k).getParameterNameValue(), "runAt");
                            String location = null;
                            if (runat != null) {

                                if (runat.isEncrypted()) {
                                    location = Utility.DE(runat.getValue());
                                } else {
                                    location = runat.getValue();
                                }
                            }
                            if (location != null && location.equalsIgnoreCase(RunAtLocation.FGSMS_AGENT.value())) {
                                ret.add(serviceLevelAggrements.getSLA().get(i));
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private void ProcessStatisticalAgentActions(StatisticalServicePolicy pol, AddStatisticalDataRequestMsg req) {
        if (pol == null) {
            return;
        }
        if (req == null) {
            return;
        }

        List<SLA> slas = GetAgentActions(pol.getServiceLevelAggrements());
        if (slas == null) {
            return;
        }
        if (slas.isEmpty()) {
            return;
        }
        ProcessSLAs(slas, req);

    }

    private void ProcessSLAs(List<SLA> slas, AddStatisticalDataRequestMsg req) {
        if (slas == null) {
            return;
        }
        for (int i = 0; i < slas.size(); i++) {
            boolean flag = ProcessSLA(slas.get(i), req);
            if (flag) {
                DoActions(slas.get(i));
            }
        }

    }

    private boolean ProcessSLARule(RuleBaseType rule1, AddStatisticalDataRequestMsg req) {
        if (rule1 == null) {
            return false;
        }
        if (rule1 instanceof AndOrNot) {
            AndOrNot aon = (AndOrNot) rule1;
            switch (aon.getFlag()) {
                case AND:
                    return ProcessSLARule(aon.getLHS(), req) && ProcessSLARule(aon.getRHS(), req);
                case NOT:
                    return ProcessSLARule(aon.getLHS(), req) || ProcessSLARule(aon.getRHS(), req);
                case OR:
                    return ProcessSLARule(aon.getLHS(), req);
            }

        }
        if (rule1 instanceof SLARuleGeneric) {
            SLARuleGeneric r = (SLARuleGeneric) rule1;
            if (r.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.QueueOrTopicDoesNotExist")) {
                String param = null;
                NameValuePair value = Utility.getNameValuePairByName(r.getParameterNameValue(), "value");
                if (value != null) {
                    if (value.isEncrypted()) {
                        param = Utility.DE(value.getValue());
                    } else {
                        param = value.getValue();
                    }

                    boolean found = false;
                    for (int i = 0; i < req.getData().size(); i++) {
                        if (req.getData().get(i).getQueueOrTopicName().toLowerCase().contains(param)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        return true;
                    }
                }

            }
            if (r.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.BrokerQueueSizeGreaterThan")) {

                NameValuePair value = Utility.getNameValuePairByName(r.getParameterNameValue(), "value");
                long val = -1;
                String topic = null;
                NameValuePair optionalName = Utility.getNameValuePairByName(r.getParameterNameValue(), "name");
                if (value != null) {
                    if (value.isEncrypted()) {
                        val = Long.parseLong(Utility.DE(value.getValue()));
                    } else {
                        val = Long.parseLong((value.getValue()));
                    }
                }
                if (value != null) {
                    if (value.isEncrypted()) {
                        topic = (Utility.DE(optionalName.getValue()));
                    } else {
                        topic = ((optionalName.getValue()));
                    }
                }
                long maxdepth = 0;
                for (int i = 0; i < req.getData().size(); i++) {
                    if (topic == null) {
                        if (req.getData().get(i).getDepth() > maxdepth) {
                            maxdepth = req.getData().get(i).getDepth();
                        }
                        if (val > maxdepth) {
                            return true;
                        }
                    } else {
                        if (req.getData().get(i).getQueueOrTopicName().equalsIgnoreCase(topic) && req.getData().get(i).getDepth() > maxdepth) {
                            maxdepth = req.getData().get(i).getDepth();
                        }
                        if (val > maxdepth) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }
//

    private boolean ProcessSLA(SLA sla, AddStatisticalDataRequestMsg req) {
        if (sla == null) {
            return false;
        }

        return ProcessSLARule(sla.getRule(), req);
    }

    private void DoActions(SLA sla) {
        if (sla == null) {
            return;
        }
        if (sla.getAction() == null) {
            return;
        }
        if (sla.getAction().getSLAAction().isEmpty()) {
            return;
        }

        for (int i = 0; i < sla.getAction().getSLAAction().size(); i++) {
            if (sla.getAction().getSLAAction().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRunScript")) {
                NameValuePair runat = Utility.getNameValuePairByName(sla.getAction().getSLAAction().get(i).getParameterNameValue(), "runAt");
                NameValuePair runFromPath = Utility.getNameValuePairByName(sla.getAction().getSLAAction().get(i).getParameterNameValue(), "runFromPath");
                NameValuePair command = Utility.getNameValuePairByName(sla.getAction().getSLAAction().get(i).getParameterNameValue(), "command");
                String location = null;
                String cmd = null;
                String path = null;
                if (runat != null) {

                    if (runat.isEncrypted()) {
                        location = Utility.DE(runat.getValue());
                    } else {
                        location = runat.getValue();
                    }
                }
                if (command != null) {

                    if (command.isEncrypted()) {
                        cmd = Utility.DE(command.getValue());
                    } else {
                        cmd = command.getValue();
                    }
                }
                if (runFromPath != null) {

                    if (runFromPath.isEncrypted()) {
                        path = Utility.DE(runFromPath.getValue());
                    } else {
                        path = runFromPath.getValue();
                    }
                }

                if (location != null && location.equalsIgnoreCase(RunAtLocation.FGSMS_AGENT.value())) {
                    Runtime run = Runtime.getRuntime();
                    Process pr = null;
                    try {
                        if (Utility.stringIsNullOrEmpty(path)) {
                            pr = run.exec(cmd);
                        } else {
                            pr = run.exec(cmd, null, new File(path));
                        }
                    } catch (IOException ex) {
                        log.log(Level.WARN, null, ex);
                    }

                }
            }
        }
    }
}
