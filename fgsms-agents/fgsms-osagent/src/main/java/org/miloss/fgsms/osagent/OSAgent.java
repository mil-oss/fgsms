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
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.osagent;

import org.miloss.fgsms.osagent.sensor.ISensorProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.util.Calendar;
import java.util.Iterator;
import java.util.UUID;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.agentcore.OSAgentHelper;
import org.miloss.fgsms.agentcore.PersistentStorage;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.*;
import org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessDataResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.osagent.Ifconfig.NetworkRate;
import org.miloss.fgsms.osagent.Iostat.DiskRates;
import org.miloss.fgsms.osagent.sensor.SensorProviderFactory;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarLoader;
import org.hyperic.sigar.win32.Service;
import org.miloss.fgsms.common.Constants;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * fgsms's Operating System agent Provides monitoring and some SLA processing
 * for the machine as a whole or processes running on a machine. Uses the SIGARS
 * API provided by VMWare
 *
 * <br><br><br> FAQ: How to change the reporting rate of os (machine and process
 * monitor) agents?<Br>
 *
 * OS Agents use two different timers. <ol><li> 1) update the running process
 * list, hardware and network information. This is referred to as the refresh
 * rate.</li><li> 2) update the current status and performnace data. This is
 * referred to as the Reporting Frequency.</il></ol>
 *
 * The RF is changed via the global configuration setting, Agents.Process,
 * ReportingFrequency.<Br><br>
 *
 * However, changes with this value are by default updated <ul><li>when the
 * agent starts</li> <li>once an hour</li> </ul>
 *
 * Since 6.2.1. To increase the rate, a command line option can be added to the
 * os agent's startup script with overrides the default refresh rate to whatever
 * you want, measured in ms. This will generate slightly more load on the
 * machine hosting the agent, and will add additional bandwidth consumption if
 * it is turned down from the defaults. Use the following option
 * -DrefreshRateOverride=(some duration in milliseconds)
 *
 * Since 6.3, OS Agents can now report sensor data back to fgsms. To configure a
 * sensor data feed, a properties file must be provided. Either place
 * sensor.properties in the current working directory or use
 * -DsensorProviders=path to provide the configuration. <br><br> The properties
 * file needs only a single setting, sensorProviders with a value equal to a
 * comma delimited list of classes that implement the ISensorProvider interface.
 *
 * @since RC6
 * @see ISensorProvider
 * @see SensorProviderFactory
 * @author AO
 */
public class OSAgent {

    private static final String WINDOWS_KEY = "WindowsService:";
    public final static Logger log = Logger.getLogger("fgsms.fgsmsOSAgent");
    private static boolean isWindows = false;

    private DatatypeFactory df = null;
    protected boolean running = false;
    private boolean done = false;
    private File file;
    private FileChannel channel;
    private List<ISensorProvider> sensors = new ArrayList<ISensorProvider>();
    private FileLock lock;
    private SecurityWrapper classlevel = new SecurityWrapper(ClassificationType.U, "");
    private Duration frequency = null;
    private MachinePolicy machine = null;
    private List<ProcessPolicy> processes = null;
    /*
     * Debug adds additional logging
     */
    private boolean DEBUG = false;
    /**
     * NO_SEND prevents the agent from sending any data. It will perform a test
     * to detect memory leaks in hyperic's sigar api
     */
    private boolean NO_SEND = false;

    public long startedat = -1;
    public long datasend_success = 0;
    public long datasend_failures = 0;
    //  protected Endpoint callback = null;
    //  protected Endpoint callback_ipv6 = null;
    //   protected boolean callback_running = false;
    protected int callback_port = 4000;
    protected int maxportattempts = 1000;
    protected int currentportattempts = 0;
    private Properties sensorconfig = null;

    private long iteration = 0;

    private static String filter(String description) {
        return description.replaceAll("[^\\x00-\\x7F]", "").replaceAll("\\p{Cc}", "");
    }

    public OSAgent() throws DatatypeConfigurationException {
        df = DatatypeFactory.newInstance();
        this.classlevel = new SecurityWrapper(ClassificationType.U, "");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        new OSAgent().startup(args);

    }

    /**
     * Gathers the current machine information
     *
     * @return
     * @throws SigarException
     */
    protected static MachineInformation gatherInformation() throws SigarException {
        Sigar s = new Sigar();
        MachineInformation m = new MachineInformation();

        m.setHostname(Utility.getHostName());
        OperatingSystem sys = OperatingSystem.getInstance();

        PropertyPair pp = null;
        pp = new PropertyPair();
        pp.setPropertyname("OS Description");
        pp.setPropertyvalue(filter(sys.getDescription()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS Name");
        pp.setPropertyvalue(filter(sys.getName()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS Architecture");
        pp.setPropertyvalue(filter(sys.getArch()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS Machine");
        pp.setPropertyvalue(filter(sys.getMachine()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS Version");
        pp.setPropertyvalue(filter(sys.getVersion()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS Patch Level");
        pp.setPropertyvalue(filter(sys.getPatchLevel()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS Vendor Version");
        pp.setPropertyvalue(filter(sys.getVendorVersion()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS Vendor");
        pp.setPropertyvalue(filter(sys.getVendor()));
        m.getPropertyPair().add(pp);

        if (sys.getVendorCodeName() != null) {
            pp = new PropertyPair();
            pp.setPropertyname("OS Code Name");
            pp.setPropertyvalue(filter(sys.getVendorCodeName()));
            m.getPropertyPair().add(pp);

        }
        pp = new PropertyPair();
        pp.setPropertyname("OS Data Model");
        pp.setPropertyvalue(filter(sys.getDataModel()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("OS CPU Endian");
        pp.setPropertyvalue(filter(sys.getCpuEndian()));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("JVM Version");
        pp.setPropertyvalue(filter(System.getProperty("java.vm.version")));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("JVM Vendor");
        pp.setPropertyvalue(filter(System.getProperty("java.vm.vendor")));
        m.getPropertyPair().add(pp);

        pp = new PropertyPair();
        pp.setPropertyname("JVM Home");
        pp.setPropertyvalue(filter(System.getProperty("java.home")));
        m.getPropertyPair().add(pp);

        Ps ps = new Ps();
        try {

            m.setCpucorecount(ps.GetCPUCores());
        } catch (Exception ex) {
            log.log(Level.WARN, "unable to get cpu core count", ex);
        } finally {
            try {
                ps.close();
            } catch (Exception ex) {
            }
        }

        Mem mem = s.getMem();

        m.setMemoryinstalled(mem.getRam() * 1024 * 1024);
        m.setOperatingsystem(System.getProperty("os.name"));
        if (m.getOperatingsystem().toLowerCase().contains("win")) {
            isWindows = true;
        }
        m.getDriveInformation().addAll(getDriveInfo());
        NetInfo ni = new NetInfo();
        try {
            m.getAddresses().addAll(ni.GetNetworkInfo());
        } catch (Exception ex) {
            log.log(Level.WARN, "unable to get network info", ex);
        } finally {
            try {
                ni.close();
            } catch (Exception ex) {
            }
        }
        m.getHostname();
        m.setDomain(getDomainName());

        m.setUri("urn:" + Utility.getHostName() + ":system");
        s.close();
        return m;

    }

    /**
     * Returns a list of running processes, if windows, the windows installed
     * service list is also included
     *
     * @param isWindows
     * @return
     * @throws SigarException
     */
    protected static List<String> getServiceList(boolean isWindows) throws SigarException {
        List<String> items = new ArrayList<String>();
        if (isWindows) {
            try {
                List services = Service.getServiceNames();
                for (int i = 0; i < services.size(); i++) {
                    items.add(WINDOWS_KEY + (String) services.get(i));

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //changed to prevent multiple instances of the same executable from being listed
        Ps ps = new Ps();
        try {
            List<String> data = (ps.runningProcesses());
            Set<String> set = new HashSet<String>(data);
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String i = iterator.next();
                i = Utility.encodeHTML(i);
                items.add(i);
            }

        } catch (Exception ex) {
            log.log(Level.WARN, "can't get the list of running processes");
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }

        return items;
    }

    /**
     * Sends the most recent performance metrics to fgsms
     *
     * @return
     * @throws SigarException
     * @throws ConfigurationException
     */
    private boolean refreshInfo() throws SigarException, ConfigurationException {
        MachineInformation m = gatherInformation();
        SetProcessListByMachineRequestMsg req = new SetProcessListByMachineRequestMsg();
        req.setAgentType(OSAgent.class.getCanonicalName());
        req.setMachineInformation(m);

        if (req.getMachineInformation().getOperatingsystem().toLowerCase().contains("win")) {
            req.getServices().addAll(getServiceList(true));
        } else {
            req.getServices().addAll(getServiceList(false));
        }
        /*    if (callback_running) {
         PropertyPair callbackp = new PropertyPair();
         callbackp.setPropertyname(org.miloss.fgsms.common.Constants.PROPERTYPAIR_OS_AGENT_CALLBACK_URL);
         callbackp.setPropertyvalue("http://" + Utility.getHostName() + ":" + callback_port + "/os_agent");
         req.getMachineInformation().getPropertyPair().add(callbackp);
         }*/

        PropertyPair callbackp = new PropertyPair();
        callbackp.setPropertyname(org.miloss.fgsms.common.Constants.PROPERTYPAIR_OS_AGENT_STARTED);
        callbackp.setPropertyvalue(Long.toString(startedat));
        req.getMachineInformation().getPropertyPair().add(callbackp);
        SetProcessListByMachineResponseMsg SetMachineInfo = OSAgentHelper.SetMachineInfo(req);
        if (SetMachineInfo == null) {
            datasend_failures++;
            log.log(Level.WARN, "unable to reach the fgsms PCS service. This could either be a connectivity issue or a problem at the service.");
            return false;
        } else {
            datasend_success++;
            frequency = SetMachineInfo.getReportingFrequency();
            classlevel = SetMachineInfo.getClassification();
            machine = SetMachineInfo.getMachinePolicy();
            processes = SetMachineInfo.getProcessPolicy();
            return true;
        }
    }

    protected void startup(String[] args) throws Exception {
        System.out.println("Use \"java -jar fgsms.OSBuellerNextGen.jar help\" for command line options");
        boolean dump = false;
        if (args != null && args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            printUsage();
            return;
        }
        if (args != null && args.length >= 1 && args[0].equalsIgnoreCase("debug")) {
            DEBUG = true;
        }
        if (args != null && args.length >= 1 && args[0].equalsIgnoreCase("nosend")) {
            NO_SEND = true;
            RUN_DEBUG_TEST();
            return;
        }
        if (args != null && args.length >= 1 && args[0].equalsIgnoreCase("runleaktest")) {
            try {
                RUN_LEAK_TEST();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(OSAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            return;
        }
        if (args != null && args.length >= 1 && args[0].equalsIgnoreCase("dump")) {
            dump = true;
        }
        if (dump) {
            dumpInfo();
            return;
        }
        try {
            file = new File("procmon.lck");
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
        } catch (Exception e) {
            // already locked
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file procmon.lck needs to be deleted.");
            return;
        }
        if (lock == null) {
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file procmon.lck needs to be deleted.");
            return;
        }
        startedat = System.currentTimeMillis();
        /*     RemoteAgentCallbackImpl callback_service = new RemoteAgentCallbackImpl(this);

         log.log(Level.INFO, "Attempting to start the callback endpoint");
         //TODO reconfigure this to also support IPv6
         while (!callback_running && currentportattempts < maxportattempts) {
         currentportattempts++;
         callback_port++;
         try {
         callback = Endpoint.publish("http://0.0.0.0:" + callback_port + "/os_agent", callback_service);
         } catch (Exception ex) {
         log.log(Level.WARN, "unable to set callback point on port " + callback_port, ex);
         }
         if (callback != null && callback.isPublished()) {
         callback_running = true;
         }
         }
         if (!callback_running) {
         log.log(Level.WARN, "Unable to establish callback service. Remote administrative actions from fgsms will not be possible");
         } else {
         log.log(Level.INFO, "Remote callbacks setup at http://" + Utility.getHostName() + ":" + callback_port + "/os_agent  (all IPs are bound)");
         }*/
        Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());
//        PropertyConfigurator.configure("log4j.properties");
        done = true;
        if (DEBUG) {
            log.log(Level.INFO, "fgsms.OS Agent (Machine, Process Performance Monitor) startup.....");
        }

        startSensorFeeds();

        try {
            refreshInfo();
        } catch (Exception ex) {
            log.log(Level.FATAL, "something went really wrong, exiting", ex);
            done = true;
            running = false;
            printSigarInfo();
            return;
        }
        if (frequency == null) {
            log.log(Level.WARN, "the current reporting frequency is unexpectedly null. defaulting to 30 seconds");
            frequency = df.newDuration(30 * 1000);
        }
        if (machine == null) {
            log.log(Level.WARN, "the machine policy is null, default to a basic policy");
            machine = loadDefaultMachinePolicy();
            //return;
        }
        done = false;
        running = true;
        long lastRanAt = 0;
        long interval = Utility.durationToTimeInMS(frequency);
        long refresh = 60 * 60 * 1000;

        String override = System.getProperty("refreshRateOverride");
        if (override != null) {
            try {
                refresh = Long.parseLong(override);
            } catch (Exception ex) {
                System.err.println("I couldn't parse the value of refreshRateOverride, defaulting to 1 hr" + ex.getMessage());
            }
        }

        long lastRefresh = System.currentTimeMillis();
        NumberFormat nf = NumberFormat.getNumberInstance();
        log.log(Level.INFO, "Starting Persistent Storage Agent");
        PersistentStorage.start(new String[]{"nostatus"});
        while (running) {
            if ((System.currentTimeMillis() - lastRefresh > refresh) || machine == null) {
                lastRefresh = System.currentTimeMillis();
                try {
                    if (DEBUG) {
                        log.log(Level.INFO, "Data Interval: " + interval + " Config Interval: " + refresh);
                    }
                    refreshInfo();
                } catch (Exception ex) {
                    log.log(Level.FATAL, "something went really wrong, exiting", ex);
                    done = true;
                    running = false;
                    return;
                }
            }
            if (System.currentTimeMillis() - lastRanAt > interval) {
                lastRanAt = System.currentTimeMillis();
                //run
                try {
                    iteration++;
                    long timer = System.currentTimeMillis();
                    if (DEBUG) {
                        log.log(Level.INFO, "Data Interval: " + interval + " Config Interval: " + refresh);
                        log.log(Level.INFO, "iteration " + nf.format(iteration) + " of " + nf.format(Long.MAX_VALUE));
                        log.log(Level.INFO, "Current VM Memory : total = " + nf.format(Runtime.getRuntime().totalMemory()) + " free = " + nf.format(Runtime.getRuntime().freeMemory())
                                + " used " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
                    }
                    fire();
                    if (DEBUG) {
                        log.log(Level.INFO, "Took " + nf.format((System.currentTimeMillis() - timer)) + " ms to run");
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, "error caught ", ex);
                }
                if (DEBUG) {
                    log.log(Level.INFO, "Pausing until the next iteration in " + interval + "ms.....");
                }
            }
            if (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(OSAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
        }
        running = false;
        done = true;
    }

    public SecurityWrapper getClasslevel() {
        return classlevel;
    }

    public SecurityWrapper getClassLevelAsCopy() {
        if (classlevel == null) {
            classlevel = new SecurityWrapper(ClassificationType.U, "");
        }
        SecurityWrapper x = new SecurityWrapper();
        x.setCaveats(classlevel.getCaveats());
        x.setClassification(classlevel.getClassification());
        return x;
    }

    private void closeLock() {
        try {
            lock.release();
        } catch (Exception e) {
        }
        try {
            channel.close();
        } catch (Exception e) {
        }
    }

    private void deleteFile() {
        try {
            file.delete();
        } catch (Exception e) {
        }
    }

    private static String getDomainName() {
        String domain = System.getenv().get("USERDOMAIN");
        if (domain != null && !domain.equalsIgnoreCase(Utility.getHostName())) {
            return (domain.toLowerCase().trim());
        } else {
            return Constants.UNSPECIFIED;
        }
    }

    /**
     * Fetches the most recent performance data, sends it to fgsms, then
     * performs some basic
     *
     * @throws ConfigurationException
     * @throws SigarException
     * @throws DatatypeConfigurationException
     */
    protected void fire() throws ConfigurationException, SigarException, DatatypeConfigurationException {
        AddMachineAndProcessDataRequestMsg req = new AddMachineAndProcessDataRequestMsg();
        req.setAgentType(this.getClass().getCanonicalName());
        req.setClassification(classlevel);

        //req.getDriveInformation().addAll(GetDriveInfo(this.machine.getRecordDiskSpace()));
        log.log(Level.INFO, "refreshing local machine performance information");
        req.setMachineData(getMachineInfo(this.machine));
        if (req.getMachineData() == null) {
            log.log(Level.WARN, "Machine info and/or the Machine Policy is unexpectly null. This can happen when all the fgsms servers are offline. I'll try again in a minute");
            return;
        }
        req.setHostname(Utility.getHostName());
        log.log(Level.INFO, "refreshing local process performance information");
        req.getProcessData().addAll(getProcessData(this.processes));
        req.setDomainname(getDomainName());
        req.setSensorData(getSensorData());
        if (!NO_SEND) {
            log.log(Level.INFO, "sending data...");
            //JAXB.marshal(req,System.out);
            AddMachineAndProcessDataResponseMsg res = OSAgentHelper.AddMachineAndProcessDataRequestMsg(req);
            if (res != null) {
                datasend_success++;
                log.log(Level.INFO, "sent successfully...");
                this.classlevel = res.getClassification();
                this.machine = res.getMachinePolicy();
                this.processes = res.getProcessPolicy();

                log.log(Level.DEBUG, "processing SLA actions");
            } else {
                datasend_failures++;
                log.log(Level.WARN, "trouble sending data...");
            }

            doAgentSLAActions(req);
        } else {
            log.log(Level.INFO, "skipping sending iteration");
        }
    }

    protected static List<DriveInformation> getDriveInfo() throws SigarException {
        Df sigar = new Df();
        List<DriveInformation> ret = new ArrayList<DriveInformation>();
        try {
            ret = sigar.DriveInfo();
        } catch (Exception ex) {
            log.log(Level.WARN, "trouble getting drive info ", ex);
        } finally {
            try {
                sigar.close();
            } catch (Exception ex) {
            }
        }
        return ret;

    }

    /**
     * Get machine information, such as current cpu, memory, disk, nic usage
     *
     * @param machine
     * @return
     * @throws DatatypeConfigurationException
     * @throws SigarException
     */
    protected MachinePerformanceData getMachineInfo(MachinePolicy machine) throws DatatypeConfigurationException, SigarException, ConfigurationException {
        if (machine == null) {
            refreshInfo();
        }
        if (machine == null) {
            return null;
        }
        MachinePerformanceData m = new MachinePerformanceData();
        m.setUri(machine.getURL());
        m.setId(UUID.randomUUID().toString());
        m.setOperationalstatus(true);
        m.setStatusmessage("Operational");
        Ps ps = new Ps();
        try {

            m.setNumberofActiveThreads(ps.CurrentThreadCount());
            if (machine.isRecordCPUusage()) {
                m.setPercentusedCPU(ps.GetCurrentCPUUsage());
                m.setOperationalstatus(true);
            }
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(ps.GetSystemBootTimeInMS());
            m.setStartedAt((gcal));
        } catch (Exception ex) {
            m.setNumberofActiveThreads(-1L);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
        //  SysInfo si = new SysInfo();
        MemWatch mw = new MemWatch();

        if (machine.isRecordMemoryUsage()) {
            if (DEBUG) {
                log.log(Level.INFO, "getting machine memory usage");
            }
            Mem MemoryBytesUsed = mw.MemoryBytesUsed();
            m.setBytesusedMemory(MemoryBytesUsed.getActualUsed());
            if (DEBUG) {
                log.log(Level.INFO, "RAW values " + MemoryBytesUsed.getActualUsed());
            }
            //        new SysInfo().
        }

        if (!machine.getRecordDiskUsagePartitionNames().isEmpty()) //if (machine.isRecordDiskUsage())
        {
            Iostat io = new Iostat();
            Df sigar = new Df();
            try {
                for (int i = 0; i < machine.getRecordDiskUsagePartitionNames().size(); i++) {
                    DriveInformation di = new DriveInformation();
                    di.setPartition(machine.getRecordDiskUsagePartitionNames().get(i));
                    di.setSystemid(machine.getRecordDiskUsagePartitionNames().get(i));
                    di.setTimestamp(new GregorianCalendar());
                    DriveInformation driveInfo = sigar.GetDriveInfo(machine.getRecordDiskUsagePartitionNames().get(i));
                    if (driveInfo != null) {
                        di.setSystemid(driveInfo.getSystemid());
                        di.setType(driveInfo.getType());
                        di.setTotalspace(di.getTotalspace());
                    } else {
                        di.setSystemid("");
                        di.setType("");
                        di.setTotalspace(0L);
                    }
                    try {
                        if (DEBUG) {
                            log.log(Level.INFO, "getting disk io stats/usage for " + machine.getRecordDiskUsagePartitionNames().get(i));
                        }

                        DiskRates rates = io.GetBytesPerSecond(machine.getRecordDiskUsagePartitionNames().get(i), isWindows);
                        if (DEBUG) {
                            log.log(Level.INFO, "RAW values " + rates.bytesreadpersecond + " " + rates.byteswritepersecond);
                        }
                        di.setKilobytespersecondDiskRead(rates.bytesreadpersecond / 1024);
                        di.setKilobytespersecondDiskWrite(rates.byteswritepersecond / 1024);
                        di.setOperational(true);
                    } catch (Exception ex) {
                        log.log(Level.WARN, "unable to get disk rates for " + machine.getRecordDiskUsagePartitionNames().get(i), ex);
                        di.setOperational(false);
                    }
                    m.getDriveInformation().add(di);
                }
            } catch (Exception ex) {
                log.log(Level.FATAL, "unexpected error", ex);
            } finally {
                try {
                    io.close();
                } catch (Exception ex) {
                }
                try {
                    sigar.close();
                } catch (Exception ex) {
                }
            }
        }

        if (!machine.getRecordNetworkUsage().isEmpty()) {
            Ifconfig ic = new Ifconfig();
            try {
                for (int i = 0; i < machine.getRecordNetworkUsage().size(); i++) {
                    try {
                        if (DEBUG) {
                            log.log(Level.INFO, "getting network io stats/usage for " + machine.getRecordNetworkUsage().get(i));
                        }

                        NetworkRate rate = ic.NetworkSendRate(machine.getRecordNetworkUsage().get(i));
                        NetworkAdapterPerformanceData nic = new NetworkAdapterPerformanceData();
                        nic.setAdapterName(machine.getRecordNetworkUsage().get(i));
                        if (DEBUG) {
                            log.log(Level.INFO, "RAW values " + rate.rx + " " + rate.tx);
                        }
                        nic.setKilobytespersecondNetworkReceive(rate.rx / 1024);
                        nic.setKilobytespersecondNetworkTransmit(rate.tx / 1024);
                        m.getNetworkAdapterPerformanceData().add(nic);
                    } catch (Exception ex) {
                        log.log(Level.WARN, "unable to get NIC information ", ex);
                    }
                }
            } catch (Exception ex) {
                log.log(Level.FATAL, "unexpected error", ex);
            } finally {
                try {
                    ic.close();
                } catch (Exception ex) {
                }
            }
        }

        m.setTimestamp(getNow());
        if (!machine.getRecordDiskSpace().isEmpty()) {
            updateFreeSpaceRecords(m.getDriveInformation(), machine.getRecordDiskSpace());
        }
        //    m.getDriveInformation().addAll(GetDriveInfo(this.machine.getRecordDiskSpace()));
        return m;
    }

    private List<ProcessPerformanceData> getProcessData(List<ProcessPolicy> processes) {

        List<ProcessPerformanceData> data = new ArrayList<ProcessPerformanceData>();
        if (processes == null || processes.isEmpty()) {
            return data;
        }
        Ps ps = new Ps();
        ProcInfo pi = new ProcInfo();
        try {
            for (int i = 0; i < processes.size(); i++) {
                try {
                    if (DEBUG) {
                        log.log(Level.INFO, "Getting process data for " + processes.get(i).getURL());
                    }
                    String name = getProcessName(processes.get(i).getURL());
                    List<Long> pids = new ArrayList<Long>();
                    if (name.startsWith(WINDOWS_KEY)) {
                        name = name.replace(WINDOWS_KEY, "");
                        pids.addAll(pi.getDataWindows(name, processes.get(i).getAlsoKnownAs(), true));
                    } else if (name.startsWith("java:")) {
                        //name = name.replace(WINDOWS_KEY, "");
                        pids.addAll(pi.getDataJava(name, processes.get(i).getAlsoKnownAs()));
                    } else {
                        if (isWindows) {
                            name = chopWindows(processes.get(i).getURL());
                        } else {
                            name = chopLinux(processes.get(i).getURL());
                        }
                        pids.addAll(pi.GetData(name, processes.get(i).getAlsoKnownAs()));
                        //   pids.addAll(pi.GetData(name, processes.get(i).getAlsoKnownAs()));
                    }
                    if (DEBUG) {
                        log.log(Level.INFO, "found " + pids.size() + " pids for the service" + processes.get(i).getURL() + " local name " + name);
                    }
                    ProcessPerformanceData ppd = ps.GetProcessData(pids);

                    ppd.setTimestamp(getNow());
                    if (ppd.isOperationalstatus()) {
                        if (DEBUG) {
                            log.log(Level.INFO, "running");
                        }
                    } else {
                        log.log(Level.WARN, processes.get(i).getURL() + " is NOT running " + ppd.getStatusmessage());
                    }
                    ppd.setUri(processes.get(i).getURL());
                    data.add(ppd);

                } catch (org.hyperic.sigar.win32.Win32Exception ex) {
                    //usually access denied, monitoring a windows service and the agent is not running as a system service (user land)
                    ProcessPerformanceData ppd = new ProcessPerformanceData();
                    ppd.setStatusmessage(ex.getMessage());
                    ppd.setOperationalstatus(false);
                    ppd.setTimestamp(getNow());
                    if (ppd.isOperationalstatus()) {
                        if (DEBUG) {
                            log.log(Level.INFO, "running");
                        }
                    } else {
                        log.log(Level.WARN, processes.get(i).getURL() + " is NOT running " + ppd.getStatusmessage());
                    }
                    ppd.setUri(processes.get(i).getURL());
                    data.add(ppd);
                    log.log(Level.WARN, processes.get(i).getURL(), ex);
                } catch (Exception ex) {
                    log.log(Level.WARN, processes.get(i).getURL(), ex);

                }

            }
        } catch (Exception ex) {
            log.log(Level.FATAL, "unexpected error", ex);
        } finally {
            try {
                ps.close();
            } catch (Exception ex) {
                log.log(Level.DEBUG, null, ex);
            }
            try {
                pi.close();
            } catch (Exception ex) {
                log.log(Level.DEBUG, null, ex);
            }
        }
        return data;
    }

    private void updateFreeSpaceRecords(List<DriveInformation> driveInformation, List<String> partitions) {
        Df driveinfo = new Df();
        //List<DriveInformation> items = new ArrayList<DriveInformation>();
        try {
            for (int i = 0; i < partitions.size(); i++) {
                if (DEBUG) {
                    log.log(Level.INFO, "getting free space for partition " + partitions.get(i));
                }
                try {
                    DriveInformation di = driveinfo.GetDriveInfo(partitions.get(i));

                    DriveInformation record = findRecord(driveInformation, partitions.get(i));
                    if (di == null) {
                        record.setOperational(false);
                    } else {
                        record.setOperational(true);
                        if (DEBUG) {
                            log.log(Level.INFO, "RAW value " + di.getFreespace());
                        }
                        record.setFreespace(di.getFreespace() / 1024);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, "error caught checking free space for " + partitions.get(i), ex);
                }
            }
        } catch (Exception ex) {
            log.log(Level.FATAL, "unexpected error", ex);
        } finally {
            try {
                driveinfo.close();
            } catch (Exception ex) {
                log.log(Level.DEBUG, null, ex);
            }
        }
    }

    private DriveInformation findRecord(List<DriveInformation> driveInformation, String get) {
        if (driveInformation == null) {
            driveInformation = new ArrayList<DriveInformation>();
        }
        for (int i = 0; i < driveInformation.size(); i++) {
            if (driveInformation.get(i).getPartition().equalsIgnoreCase(get)) {
                return driveInformation.get(i);
            }
        }
        DriveInformation di = new DriveInformation();
        di.setPartition(get);
        driveInformation.add(di);
        return di;
    }

    private String getProcessName(String url) {

        String[] bits = url.split(":");
        //urn
        //hostname
        //system
        //optionally WindowsService or java
        //process name
        if (bits[bits.length - 2].equalsIgnoreCase(WINDOWS_KEY.replace(":", ""))
                || bits[bits.length - 2].equalsIgnoreCase("java")) {
            return bits[bits.length - 2] + ":" + bits[bits.length - 1];
        }
        return bits[bits.length - 1];
    }

    private Calendar getNow() {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        return (gcal);
    }

    private void doAgentSLAActions(AddMachineAndProcessDataRequestMsg req) {
        if (this.machine != null) {
            if (this.machine.getServiceLevelAggrements() != null
                    && !this.machine.getServiceLevelAggrements().getSLA().isEmpty()) {
                doMachineSLAs(req, this.machine.getServiceLevelAggrements().getSLA());
            }
        }
        if (this.processes != null) {
            for (int i = 0; i < this.processes.size(); i++) {
                if (this.processes.get(i).getServiceLevelAggrements() != null && !this.processes.get(i).getServiceLevelAggrements().getSLA().isEmpty()) {

                    doProcessSLAs(getProcessRecord(this.processes.get(i).getURL(), req), this.machine.getServiceLevelAggrements().getSLA());
                }
            }
        }
    }

    private ProcessPerformanceData getProcessRecord(String url, AddMachineAndProcessDataRequestMsg data) {
        if (Utility.stringIsNullOrEmpty(url)) {
            return null;
        }
        if (data == null || data.getProcessData().isEmpty()) {
            return null;
        }
        for (int i = 0; i < data.getProcessData().size(); i++) {
            if (data.getProcessData().get(i).getUri().equalsIgnoreCase(url)) {
                return data.getProcessData().get(i);
            }
        }
        return null;
    }

    private void doMachineSLAs(AddMachineAndProcessDataRequestMsg rec, List<SLA> sla) {
        for (int i = 0; i < sla.size(); i++) {
            if (containsSLAAgentActon(sla.get(i).getAction())) {
                if (checkRules(rec, sla.get(i).getRule())) {
                    doActions(sla.get(i).getAction(), true, null);
                }
            }
        }
    }

    private void doProcessSLAs(ProcessPerformanceData rec, List<SLA> sla) {
        for (int i = 0; i < sla.size(); i++) {
            if (containsSLAAgentActon(sla.get(i).getAction())) {
                if (checkRules(rec, sla.get(i).getRule())) {
                    doActions(sla.get(i).getAction(), false, getPolicy(rec.getUri()));
                }
            }
        }
    }

    private boolean containsSLAAgentActon(ArrayOfSLAActionBaseType action) {
        if (action == null) {
            return false;
        }
        if (action.getSLAAction().isEmpty()) {
            return false;
        }
        for (int i = 0; i < action.getSLAAction().size(); i++) {
            if (action.getSLAAction().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRestart")) {
                NameValuePair RUNAT = Utility.getNameValuePairByName(action.getSLAAction().get(i).getParameterNameValue(), "runAt");
                if (RUNAT != null) {
                    RunAtLocation ral = null;
                    String t = null;
                    if (RUNAT.isEncrypted()) {
                        t = Utility.DE(RUNAT.getValue());
                    } else {
                        t = RUNAT.getValue();
                    }
                    ral = RunAtLocation.valueOf(t);
                    if (ral == RunAtLocation.FGSMS_AGENT) {
                        return true;
                    }
                }
            }
            if (action.getSLAAction().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRunScript")) {
                NameValuePair RUNAT = Utility.getNameValuePairByName(action.getSLAAction().get(i).getParameterNameValue(), "runAt");
                if (RUNAT != null) {
                    RunAtLocation ral = null;
                    String t = null;
                    if (RUNAT.isEncrypted()) {
                        t = Utility.DE(RUNAT.getValue());
                    } else {
                        t = RUNAT.getValue();
                    }
                    ral = RunAtLocation.valueOf(t);
                    if (ral == RunAtLocation.FGSMS_AGENT) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * return true if a rule's conditions are met (SLA fault)
     *
     * @param rec
     * @param rule
     * @return
     */
    private boolean checkRules(ProcessPerformanceData rec, RuleBaseType rule) {
        if (rule instanceof AndOrNot) {
            AndOrNot r = (AndOrNot) rule;
            switch (r.getFlag()) {
                case AND:
                    return checkRules(rec, r.getLHS()) && checkRules(rec, r.getRHS());
                case OR:
                    return checkRules(rec, r.getLHS()) || checkRules(rec, r.getRHS());
                case NOT:
                    return !checkRules(rec, r.getLHS());
            }
        }
        if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric x = (SLARuleGeneric) rule;
            if (x.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighCPUUsage")) {
                if (rec.getPercentusedCPU() == null) {
                    return false;
                }
                NameValuePair GetNameValuePairByName = Utility.getNameValuePairByName(x.getParameterNameValue(), "value");
                long rate = -1;
                if (GetNameValuePairByName.isEncrypted()) {
                    rate = Long.parseLong(Utility.DE(GetNameValuePairByName.getValue()));
                } else {
                    rate = Long.parseLong(GetNameValuePairByName.getValue());
                }

                if (rec.getPercentusedCPU() > rate) {
                    return true;
                }
            } else if (x.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighMemoryUsage")) {
                if (rec.getBytesusedMemory() == null) {
                    return false;
                }
                NameValuePair GetNameValuePairByName = Utility.getNameValuePairByName(x.getParameterNameValue(), "value");
                long rate = -1;
                if (GetNameValuePairByName.isEncrypted()) {
                    rate = Long.parseLong(Utility.DE(GetNameValuePairByName.getValue()));
                } else {
                    rate = Long.parseLong(GetNameValuePairByName.getValue());
                }

                if (rec.getBytesusedMemory() > rate) {
                    return true;
                }
            } else if (x.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighOpenFileHandles")) {
                NameValuePair GetNameValuePairByName = Utility.getNameValuePairByName(x.getParameterNameValue(), "value");
                long rate = -1;
                if (GetNameValuePairByName.isEncrypted()) {
                    rate = Long.parseLong(Utility.DE(GetNameValuePairByName.getValue()));
                } else {
                    rate = Long.parseLong(GetNameValuePairByName.getValue());
                }

                if (rec.getOpenFileHandles() > rate) {
                    return true;
                }
            } else if (x.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighThreadCount")) {
                NameValuePair GetNameValuePairByName = Utility.getNameValuePairByName(x.getParameterNameValue(), "value");
                long rate = -1;
                if (GetNameValuePairByName.isEncrypted()) {
                    rate = Long.parseLong(Utility.DE(GetNameValuePairByName.getValue()));
                } else {
                    rate = Long.parseLong(GetNameValuePairByName.getValue());
                }

                if (rec.getNumberofActiveThreads() != null && rec.getNumberofActiveThreads() > rate) {
                    return true;
                }
            }
        }
        return false;
    }

    private void doActions(ArrayOfSLAActionBaseType action, boolean ismachine, ProcessPolicy p) {
        if (action == null) {
            return;
        }
        if (action.getSLAAction().isEmpty()) {
            return;
        }
        for (int i = 0; i < action.getSLAAction().size(); i++) {
            if (action.getSLAAction().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRestart")) {
                if (ismachine) {
                    doRestartComputer();
                } else {
                    DoRestartProcess(p);
                }
            }
            if (action.getSLAAction().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRunScript")) {
                runScript(action.getSLAAction().get(i));
            }
        }
    }

    private boolean checkRules(AddMachineAndProcessDataRequestMsg rec, RuleBaseType rule) {
        if (rule instanceof AndOrNot) {
        } else if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric r2 = (SLARuleGeneric) rule;
            NameValuePair val = Utility.getNameValuePairByName(r2.getParameterNameValue(), "value");
            long value = -1;
            if (val.isEncrypted()) {
                value = Long.parseLong(Utility.DE(val.getValue()));
            } else {
                value = Long.parseLong((val.getValue()));
            }
            if (r2.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighCPUUsage")) {
                if (rec.getMachineData().getPercentusedCPU() == null) {
                    return false;
                }
                if (rec.getMachineData().getPercentusedCPU() > value) {
                    return true;
                }
            }
            if (r2.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighMemoryUsage")) {
                if (rec.getMachineData().getBytesusedMemory() == null) {
                    return false;
                }
                if (rec.getMachineData().getBytesusedMemory() > value) {
                    return true;
                }
            }
            if (r2.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighNetworkUsage")) {
                if (rec.getMachineData().getNetworkAdapterPerformanceData().isEmpty()) {
                    return false;
                }
                for (int i = 0; i < rec.getMachineData().getNetworkAdapterPerformanceData().size(); i++) {
                    if (rec.getMachineData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkReceive() != null) {
                        if (rec.getMachineData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkReceive() > value) {
                            return true;
                        }
                    }
                    if (rec.getMachineData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkTransmit() != null) {
                        if (rec.getMachineData().getNetworkAdapterPerformanceData().get(i).getKilobytespersecondNetworkTransmit() > value) {
                            return true;
                        }
                    }
                }

            }
            if (r2.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.HighDiskUsage")) {
                if (rec.getMachineData().getDriveInformation().isEmpty()) {
                    return false;
                }
                for (int i = 0; i < rec.getMachineData().getDriveInformation().size(); i++) {
                    if (rec.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskRead() != null) {
                        if (rec.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskRead() > value) {
                            return true;
                        }
                    }
                    if (rec.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskWrite() != null) {
                        if (rec.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskWrite() > value) {
                            return true;
                        }
                    }
                }
            }

            if (r2.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.LowDiskSpace")) {
                if (rec.getMachineData().getDriveInformation().isEmpty()) {
                    return false;
                }
                NameValuePair p = Utility.getNameValuePairByName(r2.getParameterNameValue(), "partition");
                String partition = null;
                if (val.isEncrypted()) {
                    partition = (Utility.DE(val.getValue()));
                } else {
                    partition = ((val.getValue()));
                }
                for (int i = 0; i < rec.getMachineData().getDriveInformation().size(); i++) {
                    if (rec.getMachineData().getDriveInformation().get(i).getPartition().equalsIgnoreCase(partition)) {
                        if (rec.getMachineData().getDriveInformation().get(i).getFreespace() < value) {
                            return true;
                        }
                    }
                }
            }

        }

        return false;
    }

    private void doRestartComputer() {
        log.log(Level.ALL, "RESTART MACHINE TRIGGERED");
        Runtime run = Runtime.getRuntime();

        Process pr = null;
        try {
            if (isWindows) {
                pr = run.exec("shutdown /r /c /f  /d u:00  \"fgsms Agent triggered shutdown\"");
            } else {
                pr = run.exec("shutdown now -r");
            }
        } catch (IOException ex) {
            log.log(Level.WARN, "Could not process the SLA restart action", ex);
        }

    }

    private void DoRestartProcess(ProcessPolicy p) {
        log.log(Level.ALL, "RESTART PROCESS TRIGGERED");
        if (p.getURL().startsWith(WINDOWS_KEY)) {
            String s = p.getURL().replace(WINDOWS_KEY, null);
            ProcInfo pi = new ProcInfo();
            try {
                pi.restartWindowsService(s);
            } catch (SigarException ex) {
                log.log(Level.WARN, "unable to restart service " + s + " " + p.getURL(), ex);
            }

        }
        //TODO restart processes for other OS'es

    }

    private ProcessPolicy getPolicy(String uri) {
        if (this.processes == null) {
            return null;
        }
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getURL().equalsIgnoreCase(uri)) {
                return processes.get(i);
            }
        }
        return null;
    }

    private void runScript(SLAAction slaActionRunScript) {
        log.log(Level.ALL, "RUN SCRIPT TRIGGERED");
        Runtime run = Runtime.getRuntime();

        if (slaActionRunScript.getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.sla.actions.SLAActionRunScript")) {
            String path = null;

            NameValuePair runAt = Utility.getNameValuePairByName(slaActionRunScript.getParameterNameValue(), "runAt");
            NameValuePair runFromPath = Utility.getNameValuePairByName(slaActionRunScript.getParameterNameValue(), "runFromPath");
            NameValuePair command = Utility.getNameValuePairByName(slaActionRunScript.getParameterNameValue(), "command");
            if (runAt.isEncrypted()) {
                if (!Utility.DE(runAt.getValue()).equalsIgnoreCase("fgsms_AGENT")) {
                    return;
                }
            }
            String runfrom = runFromPath.getValue();
            if (runFromPath.isEncrypted()) {
                runfrom = Utility.DE(runFromPath.getValue());
            }
            String cmd = command.getValue();
            if (command.isEncrypted()) {
                cmd = Utility.DE(command.getValue());
            }
            Process pr = null;
            try {
                if (!Utility.stringIsNullOrEmpty(runfrom)) {
                    File f = new File(runfrom);
                    if (f.exists()) {
                        pr = run.exec(cmd, null, f);
                    } else {
                        pr = run.exec(cmd);
                    }
                } else {
                    pr = run.exec(cmd);
                }
            } catch (IOException ex) {
                log.log(Level.WARN, "Could not process the SLA Run Script action", ex);
            }

        }
    }

    private void dumpInfo() {
        try {
            MachineInformation m = gatherInformation();
            SetProcessListByMachineRequestMsg req = new SetProcessListByMachineRequestMsg();
            req.setAgentType(OSAgent.class.getCanonicalName());
            req.setMachineInformation(m);

            if (req.getMachineInformation().getOperatingsystem().toLowerCase().contains("win")) {
                req.getServices().addAll(getServiceList(true));
            } else {
                req.getServices().addAll(getServiceList(false));
            }
            JAXBContext ctx = JAXBContext.newInstance(MachineInformation.class, SetProcessListByMachineRequestMsg.class,
                    NetworkAdapterInfo.class, DriveInformation.class, PropertyPair.class, SecurityWrapper.class);
            Marshaller createMarshaller = ctx.createMarshaller();
            createMarshaller.marshal(req, System.out);
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
    }

    private String chopWindows(String urL) {
        // urn:hostname:path and stuff
        int x = urL.indexOf(":");
        x = urL.indexOf(":", x);
        return urL.substring(x);
        // return urL;
    }

    private String chopLinux(String urL) {
        // urn:hostname:path and stuff
        int x = urL.indexOf(":");
        x = urL.indexOf(":", x + 1);
        return urL.substring(x + 1);
        // return urL;
    }

    private MachinePolicy loadDefaultMachinePolicy() {
        classlevel = new SecurityWrapper();
        MachinePolicy mp = new MachinePolicy();
        mp.setURL("urn:" + Utility.getHostName() + ":system");
        mp.setRecordCPUusage(true);
        mp.setRecordMemoryUsage(true);
        mp.setMachineName(Utility.getHostName());

        return mp;
    }

    private void RUN_DEBUG_TEST() {
        System.out.println("Running debug rutine. This will attempt to cause memory leak issues with sigar, hopefully this will run forever without any issues.");
        try {
            NumberFormat nf = NumberFormat.getNumberInstance();
            long it = 0;
            refreshInfo();
            while (true) {
                System.out.println("iteration " + nf.format(it) + " of " + nf.format(Long.MAX_VALUE));
                log.log(Level.INFO, "Current VM Memory : total = " + nf.format(Runtime.getRuntime().totalMemory()) + " free = " + nf.format(Runtime.getRuntime().freeMemory())
                        + " used " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
                it++;
                fire();

            }
        } catch (ConfigurationException ex) {
            java.util.logging.Logger.getLogger(OSAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (SigarException ex) {
            java.util.logging.Logger.getLogger(OSAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (DatatypeConfigurationException ex) {
            java.util.logging.Logger.getLogger(OSAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    private void RUN_LEAK_TEST() throws Exception {
        NumberFormat nf = NumberFormat.getNumberInstance();
        long it = 0;
        while (true) {
            System.out.println("iteration " + nf.format(it) + " of " + nf.format(Long.MAX_VALUE));
            log.log(Level.INFO, "Current VM Memory : total = " + nf.format(Runtime.getRuntime().totalMemory()) + " free = " + nf.format(Runtime.getRuntime().freeMemory())
                    + " used " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            it++;
            Sigar s = new Sigar();
            s.close();
            Ps ps = new Ps();
            ps.close();
            Df df = new Df();
            df.close();
            ProcFileInfo pfi = new ProcFileInfo();
            pfi.close();;
            ProcInfo pi = new ProcInfo();
            pi.close();
            SysInfo si = new SysInfo();
            si.close();
            Ifconfig ic = new Ifconfig();
            ic.close();
        }
    }

    private void printUsage() {
        System.out.println("help - Shows this help message (duh)");
        System.out.println("dump - Dumps configuration information as identified by this agent to console in XML");
        System.out.println("debug - Logs additional debugging information");
        System.out.println("nosend - Performs all calculations, it just doesn't send the data back to fgsms. It does not delay between iterations");
        System.out.println("runleaktest - Performs a memory leak test for access sigars. If reliability problems occur, this may help identify the cause.");

        System.out.println("Use -Dsensorconfig=pathto/sensor.properties to use an alternative sensor configuration file");
    }

    protected void startSensorFeeds() {
        log.log(Level.INFO, "Starting sensor feeds");
        sensorconfig = loadSensorConfig();
        if (sensorconfig == null) {
            log.log(Level.INFO, "unable to load sensor.properties, sensor data will be unavailable");
            return;
        }
        String providers = sensorconfig.getProperty("sensorProviders");
        if (providers != null) {
            providers = providers.trim();
            String[] ps = providers.split(",");
            for (int i = 0; i < ps.length; i++) {
                ISensorProvider instance = SensorProviderFactory.getInstance(ps[i].trim());
                if (instance != null) {
                    sensors.add(instance);
                    instance.init(sensorconfig);
                }
            }
        }
        log.log(Level.INFO, sensors.size() + " sensor providers loaded");
    }

    /**
     *
     * system properties for a value first check local file system check
     * classpath
     *
     * @return
     */
    private Properties loadSensorConfig() {
        File f = null;
        if (System.getProperty("sensorconfig") != null) {
            f = new File(System.getProperty("sensorconfig"));
        } else {
            f = new File("sensor.properties");
        }

        InputStream is = null;
        Properties p = new Properties();
        FileInputStream fis = null;
        if (f.exists()) {
            try {

                fis = new FileInputStream(file);
                is = fis;
                log.log(Level.INFO, "Sensor Config loaded from file system " + f.getAbsolutePath() + " use -Dsensorconfig=PATH/FILE to override");
            } catch (Exception ex) {
                log.log(Level.DEBUG, null, ex);
            }
        }
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sensor.properties");
            log.log(Level.INFO, "Sensor Config loaded from class path (probably within fgsms.OSBuellerNextGen.jar" + " use -Dsensorconfig=PATH/FILE to override");
        }
        if (is != null) {
            try {
                p.load(is);
                is.close();
            } catch (Exception ex) {
                log.log(Level.WARN, null, ex);
                p = null;
            } finally {
                try {
                    is.close();
                    if (fis != null) {
                        fis.close();
                    }
                } catch (Exception x) {
                    log.log(Level.DEBUG, null, x);
                }
            }
        }

        return p;
    }

    private void stopAllSensors() {
        if (sensors == null) {
            return;
        }
        for (int i = 0; i < sensors.size(); i++) {
            sensors.get(i).stop();
            sensors = null;
        }
    }

    private NameValuePairSet getSensorData() {
        if (sensors == null || sensors.isEmpty()) {
            return null;
        }
        NameValuePairSet set = new NameValuePairSet();

        for (int i = 0; i < sensors.size(); i++) {
            try {
                set.getItems().add(sensors.get(i).getSensorData());
            } catch (Exception ex) {
                log.log(Level.WARN, "unexpected error fetching sensor data", ex);
            }
        }
        if (set.getItems().isEmpty()) {
            return null;
        }
        return set;
    }

    private void printSigarInfo() throws Exception {
        SigarLoader loader = new SigarLoader(Sigar.class);
        System.out.println(loader.getLibraryName());
        System.out.println(loader.getArchLibName());
        System.out.println(loader.getDefaultLibName());
        System.out.println(loader.getName());
        System.out.println(loader.getNativeLibrary());
        System.out.println(loader.getPackageName());
        
    }

    public class RunWhenShuttingDown extends Thread {

        public void run() {
            System.out.println("Control-C caught. Shutting down...");
            log.log(Level.INFO, "Stopping Persistent Storage Agent");
            PersistentStorage.stop(null);
            running = false;
            while (!done) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            stopAllSensors();
            closeLock();
            deleteFile();
            try {
                org.miloss.fgsms.agentcore.StatusHelper.tryUpdateStatus(false, "urn:" + Utility.getHostName() + ":system", "Agent Stopped", false, PolicyType.MACHINE, Constants.UNSPECIFIED, Utility.getHostName());
            } catch (ConfigurationException ex) {
                System.err.println("Unable to send status update. Configuration error.");
            }
        }
    }
}
