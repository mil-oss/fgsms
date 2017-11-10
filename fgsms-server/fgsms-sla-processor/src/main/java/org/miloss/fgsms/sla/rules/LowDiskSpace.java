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
package org.miloss.fgsms.sla.rules;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertType;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetProcessListByMachineRequestMsg;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;

/**
 *
 * @author AO
 */
public class LowDiskSpace implements SLARuleInterface {

    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");

    @Override
    public boolean CheckTransactionalRule(SetStatusRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(ProcessPerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(MachinePerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        if (nullableFaultMsg == null) {
            nullableFaultMsg = new AtomicReference<String>();
        }
        NameValuePair partition = Utility.getNameValuePairByName(params, "partition");
        NameValuePair value = Utility.getNameValuePairByName(params, "value");
        long val = 0;
        String part = partition.getValue();
        if (partition.isEncrypted()) {
            part = Utility.DE(partition.getValue());
        }
        if (value.isEncrypted()) {
            val = Long.parseLong(Utility.DE(value.getValue()));
        } else {
            val = Long.parseLong((value.getValue()));
        }

        for (int i = 0; i < req.getDriveInformation().size(); i++) {
            if (req.getDriveInformation().get(i).getPartition().equalsIgnoreCase(part)) {
                if (req.getDriveInformation().get(i).getFreespace() < val) {
                    nullableFaultMsg.set("The partion's freespace, " + req.getDriveInformation().get(i).getFreespace() + " is less than the threshold of " + val + ". " + nullableFaultMsg.get());
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean CheckTransactionalRule(AddDataRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(String url, List<BrokerData> data, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckNonTransactionalRule(ServicePolicy pol, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg, boolean pooled) {
        return false;
    }

    @Override
    public String GetDisplayName() {
        return "Low disk space on a partition or drive";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This rule will trigger when a partition's free disk space becomes less than the threshold 'value'. <br><br>"
                + "Required parameters:"
                + "<ul>"
                + "<li>partition - the name of the partition. It must exist and must be monitored</li>"
                + "<li>value - the size in megabytes</li>"
                + "</ul>";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(Utility.newNameValuePair("partition", null, false, false));
        arrayList.add(Utility.newNameValuePair("value", null, false, false));
        return arrayList;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError, ServicePolicy policy) {

        if (outError == null) {
            outError = new AtomicReference<String>();
        }

        if (!(policy instanceof MachinePolicy)) {
            outError.set("This rule only applies to Machine Policies. " + outError.get());
            return false;
        }

        boolean partitionfound = false;
        boolean valuefound = false;
        //fetch partition from params
        long value = -1;
        String partition = null;
        for (int i = 0; i < params.size(); i++) {

            if (params.get(i).getName().equals("partition")) {
                partitionfound = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'partition'. " + outError.get());
                }
                if (params.get(i).isEncrypted()) {
                    partition = Utility.DE(params.get(i).getValue());
                } else {
                    partition = (params.get(i).getValue());
                }
            }
            if (params.get(i).getName().equals("value")) {
                valuefound = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'value'. " + outError.get());
                }
                try {
                    value = Long.parseLong(params.get(i).getValue());
                    if (value < 0) {
                        outError.set("The parameter 'value' must be greater than or equal to zero. " + outError.get());
                    }
                } catch (Exception ex) {
                    outError.set("Bad value for parameter 'value'. It must be an integer or long. Message:" + ex.getMessage() + ". " + outError.get());
                }
            }
        }

        if (!partitionfound) {
            outError.set("The parameter 'partition' is required. " + outError.get());
        }
        if (!valuefound) {
            outError.set("The parameter 'value' is required. " + outError.get());
        }

        MachinePolicy pol = (MachinePolicy) policy;
        //confirm that the low disk space parition exists on the machine and is being monitored.

        if (partitionfound) {
            boolean driveismonitored = false;
            GetProcessesListByMachineResponseMsg data = GetMachineInfo(policy.getMachineName(), policy.getDomainName());
            if (!ConfirmDriveExists(data, partition)) {
                outError.set("The partition " + partition + " must exist on the machine being monitored. " + outError.get());
            }
            for (int k = 0; k < pol.getRecordDiskSpace().size(); k++) {
                if (pol.getRecordDiskSpace().get(k).equalsIgnoreCase(partition)) {
                    driveismonitored = true;
                }
            }
            if (!driveismonitored) {
                outError.set("The partition " + partition + " s not currently being monitored, please add it to the list of monitored partitions then you can add this SLA Rule. " + outError.get());
            }
        }
        if (Utility.stringIsNullOrEmpty(outError.get())) {
            return true;
        } else {
            return false;
        }

    }

    /**
     *
     * @param data
     * @param partition
     * @return true if valid
     */
    private boolean ConfirmDriveExists(GetProcessesListByMachineResponseMsg data, String partition) {
        if (data == null) {
            return false;
        }
        if (data.getMachineInformation() == null) {
            return false;
        }
        if (data.getMachineInformation().getDriveInformation() == null) {
            return false;
        }
        for (int i = 0; i < data.getMachineInformation().getDriveInformation().size(); i++) {
            if (data.getMachineInformation().getDriveInformation().get(i).getPartition().equalsIgnoreCase(partition)) {
                return true;
            }
        }
        return false;
    }

    private GetProcessesListByMachineResponseMsg GetMachineInfo(String hostname, String domain) {
        try {

            GetProcessesListByMachineResponseMsg response = new GetProcessesListByMachineResponseMsg();

            PreparedStatement comm = null;
            Connection con = Utility.getConfigurationDBConnection();
            
            JAXBContext jc = Utility.getSerializationContext();
            comm = con.prepareStatement(""
                    + "select * from machineinfo where hostname=? and domaincol=?;");
            comm.setString(1, hostname);
            comm.setString(2, domain);
            ResultSet rs = comm.executeQuery();
            if (rs.next()) {
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(rs.getLong("lastchanged"));
                response.setLastupdateat((gcal));
                Unmarshaller u = jc.createUnmarshaller();
                byte[] s = rs.getBytes("xmlcol");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);

                JAXBElement<SetProcessListByMachineRequestMsg> foo = (JAXBElement<SetProcessListByMachineRequestMsg>) u.unmarshal(r, SetProcessListByMachineRequestMsg.class);
                if (foo == null || foo.getValue() == null) {
                    log.log(Level.WARN, "xml is unexpectedly null or empty");
                } else {
                    response.setMachineInformation(foo.getValue().getMachineInformation());
                    response.getProcessName().addAll(foo.getValue().getServices());
                }
            }


            rs.close();
            comm.close();
            con.close();
            return response;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error caught loading machine information", ex);
            return null;
        }
    }
    
       @Override
    public AlertType GetType() {
       return AlertType.Performance;
    }
       
       @Override
    public String GetHtmlFormattedDisplay(List<NameValuePair> params) {
       NameValuePair mc = Utility.getNameValuePairByName(params, "value");
        String item = UNDEFINED_VALUE;
        if (mc != null) {
            item = mc.getValue();
            if (mc.isEncrypted() || mc.isEncryptOnSave()) {
                item = ENCRYPTED_MASK;
            }
        }
        
        NameValuePair mc2 = Utility.getNameValuePairByName(params, "partition");
        String item2 = UNDEFINED_VALUE;
        if (mc2 != null) {
            item2 = mc2.getValue();
            if (mc2.isEncrypted() || mc2.isEncryptOnSave()) {
                item2 = ENCRYPTED_MASK;
            }
        }
        return Utility.encodeHTML(GetDisplayName() + " " + item2 + " " + item+"MB");
    }
       
         @Override
    public List<PolicyType> GetAppliesTo() {
         List<PolicyType> x = new ArrayList<PolicyType>();
         x.add(PolicyType.MACHINE);
      
         
         return x;
    }
}
