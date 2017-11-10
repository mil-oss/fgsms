/*
 * Copyright (c) 2006-2007 Hyperic, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.miloss.fgsms.osagent;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ProcCredName;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcTime;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.Shell;


/**
 * Show process status.
 */
public class Ps extends SigarCommandBase implements Closable {

    public Ps(Shell shell) {
        super(shell);
    }

    public Ps() {
        super();
    }


    public boolean isPidCompleter() {
        return true;
    }

    public static List getInfo(SigarProxy sigar, long pid)
            throws SigarException {

        ProcState state = sigar.getProcState(pid);
        ProcTime time = null;
        String unknown = "???";

        List info = new ArrayList();
        info.add(String.valueOf(pid));

        try {
            ProcCredName cred = sigar.getProcCredName(pid);
            info.add(cred.getUser());
        } catch (SigarException e) {
            info.add(unknown);
        }

        try {
            time = sigar.getProcTime(pid);
            info.add(getStartTime(time.getStartTime()));
        } catch (SigarException e) {
            info.add(unknown);
        }

        try {
            ProcMem mem = sigar.getProcMem(pid);
            info.add(Sigar.formatSize(mem.getSize()));
            info.add(Sigar.formatSize(mem.getRss()));
            info.add(Sigar.formatSize(mem.getShare()));
        } catch (SigarException e) {
            info.add(unknown);
        }

        info.add(String.valueOf(state.getState()));

        if (time != null) {
            info.add(getCpuTime(time));
        } else {
            info.add(unknown);
        }

        String name = ProcUtil.getDescription(sigar, pid);
        info.add(name);

        return info;
    }


    public List<String> runningProcesses() throws SigarException {
        List<String> ret = new ArrayList<String>();
        long[] pids = this.proxy.getProcList();
        //   ProcState state = sigar.getProcState(pid);
        //   ProcTime time = null;
        //  String unknown = "???";

        for (int i = 0; i < pids.length; i++) {
            /*String user = "";
             String group = "";
             try {
             ProcCredName cred = sigar.getProcCredName(pids[i]);
             user = cred.getUser();
             group = cred.getGroup();
             } catch (Exception e) {
             }*/
            try {
                String name = ProcUtil.getDescription(sigar, pids[i]);
                if (!Utility.stringIsNullOrEmpty(name)) {
                    ret.add(name);
                }
            } catch (Exception ex) {
            }
        }
        return ret;
    }

    public static String getCpuTime(long total) {
        long t = total / 1000;
        return t / 60 + ":" + t % 60;
    }

    public static String getCpuTime(ProcTime time) {
        return getCpuTime(time.getTotal());
    }

    private static String getStartTime(long time) {
        if (time == 0) {
            return "00:00";
        }
        long timeNow = System.currentTimeMillis();
        String fmt = "MMMd";

        if ((timeNow - time) < ((60 * 60 * 24) * 1000)) {
            fmt = "HH:mm";
        }

        return new SimpleDateFormat(fmt).format(new Date(time));
    }

    public long GetSystemBootTimeInMS() {
        try {
            double timesincebootupinseconds = sigar.getUptime().getUptime();
            long l = (long) (timesincebootupinseconds * 1000);
            return l;

        } catch (SigarException e) {
            return System.currentTimeMillis();
        }

    }

    public double GetCurrentCPUUsage() throws SigarException {
        CpuPerc c = sigar.getCpuPerc();
        return ((1 - c.getIdle()) * 100.0);
        /*
         * while (true) {
         *
         * System.out.println("cpu idel " + c.getIdle()*100);
         * System.out.println("cpu sys " + c.getSys()*100);
         * System.out.println("cpu user " + c.getUser()*100);
         * System.out.println("cpu combo " + c.getCombined()*100);
         * System.out.println("cpu wait " + c.getWait()*100); try {
         * Thread.sleep(1000); } catch (InterruptedException ex) {
         * Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex); } }
         *
         * /* CpuTimer t = new CpuTimer(sigar); System.out.println("cpu usages
         * " + t.getCpuUsage()); t.start();
         *
         * t.stop();
         *
         * System.out.println("cpu usages " + t.getCpuUsage());
         * System.out.println("cpu usages " + t.getCpuUsage());
         * System.out.println("idle " + sigar.getCpu().getIdle());
         * System.out.println("irq " + sigar.getCpu().getIrq());
         * System.out.println("nice " + sigar.getCpu().getNice());
         * System.out.println("softirq " + sigar.getCpu().getSoftIrq());
         * System.out.println("stolen " + sigar.getCpu().getStolen());
         * System.out.println("isys " + sigar.getCpu().getSys());
         * System.out.println("total " + sigar.getCpu().getTotal());
         * System.out.println("user " + sigar.getCpu().getUser());
         * System.out.println("wait " + sigar.getCpu().getWait());
         *
         *
         *
         *
         * return 0;
         */
    }

    public long CurrentThreadCount() throws SigarException {
        return sigar.getProcStat().getThreads();
    }
  
    ProcessPerformanceData GetProcessData(List<Long> pids) {
        ProcessPerformanceData ppd = new ProcessPerformanceData();
        long theads = 0;
        long memb = 0;
        double cpu = 0;
        long files = 0;
        if (pids.isEmpty()) {
            ppd.setOperationalstatus(false);
            ppd.setStatusmessage("Stopped");
            return ppd;
        }
        ppd.setOperationalstatus(true);
        long startTimeinseconds = 0;
        double systemuptime = 0;
        for (int i = 0; i < pids.size(); i++) {

            try {

                ProcCpu cpu2 = sigar.getProcCpu(pids.get(i));
                long time = cpu2.getTotal();
                Thread.sleep(1000);
                cpu2 = sigar.getProcCpu(pids.get(i));
                time = cpu2.getTotal() - time;  //time spend in the last second on this process in ms
                float p = (float) ((float) (time) / 1000F) * 100;
                p = p / GetCPUCores();
                if (p < 0) {
                    p = 0;
                }
                cpu += p;


            } catch (Exception ex) {
                //            Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                files += (sigar.getProcFd(pids.get(i)).getTotal());
            } catch (SigarException ex) {
                //      Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                systemuptime = sigar.getUptime().getUptime();
            } catch (SigarException ex) {
                //       Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                startTimeinseconds = sigar.getProcCpu(pids.get(i)).getStartTime();
            } catch (SigarException ex) {
                //         Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
            }

            //   System.out.println(startTimeinseconds);
            //  System.out.println(systemuptime);
            try {
                ProcState state = sigar.getProcState(pids.get(i));
                char sta = state.getState();
                String stastr = "" + sta;
                ppd.setStatusmessage(stastr);
            } catch (SigarException ex) {
                //         Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                //ProcTime time = null;
                //String unknown = "???";
                 /*
                 * try { ProcCredName cred = sigar.getProcCredName(pids.get(i));
                 * info.add(cred.getUser()); } catch (SigarException e) {
                 * info.add(unknown); }
                 *
                 * try { time = sigar.getProcTime(pids.get(i));
                 * info.add(getStartTime(time.getStartTime())); } catch
                 * (SigarException e) { info.add(unknown); }
                 */
                theads += (sigar.getProcState(pids.get(i)).getThreads());
            } catch (SigarException ex) {
                //         Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                ProcMem mem = sigar.getProcMem(pids.get(i));
                memb += (mem.getSize());

            } catch (SigarException e) {
            }

            //  info.add(String.valueOf(state.getState()));
/*
             * if (time != null) { info.add(getCpuTime(time)); } else {
             * info.add(unknown); }
             *
             * String name = ProcUtil.getDescription(sigar, pids.get(i));
             * info.add(name);
             */
        }
        GregorianCalendar gcal = new GregorianCalendar();
        if (startTimeinseconds > 0) {
            gcal.setTimeInMillis(System.currentTimeMillis() - (startTimeinseconds * 1000));
        }
        if (systemuptime > 0) {
            gcal.setTimeInMillis(System.currentTimeMillis() - (new Double(systemuptime).longValue() * 1000));
        }
        try {
            ppd.setStartedAt((gcal));
        } catch (Exception ex) {
            //       Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
        }
        ppd.setPercentusedCPU(cpu);
        ppd.setOpenFileHandles(files);
        ppd.setBytesusedMemory(memb);
        ppd.setNumberofActiveThreads(theads);
        return ppd;
    }

    public int GetCPUCores() {
        int cores = 0;
        try {
            org.hyperic.sigar.CpuInfo[] c = sigar.getCpuInfoList();
            if (c == null) {
                return 1;
            }
            if (c.length > 0) {
                cores = c[0].getTotalCores();
            }
            //     for (int i = 0; i < c.length; i++) {
            //cores += (c[i].getTotalCores());
            // }
        } catch (SigarException ex) {
            Logger.getLogger(Ps.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cores;
    }

    @Override
    public void close() throws Exception {
        if (sigar != null) {
            sigar.close();
            sigar = null;
        }
    }
    @Override
    protected void finalize() throws Throwable
    {
        
          if (sigar != null) {
              System.out.println("WARN,. finalize called without closing sigar first"+ this.getClass().getCanonicalName());
            sigar.close();
        }
        super.finalize();
    }
}
