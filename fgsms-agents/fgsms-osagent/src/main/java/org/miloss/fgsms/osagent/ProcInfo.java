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

import java.util.ArrayList;
import java.util.List;
import org.hyperic.sigar.ProcExe;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcUtil;
import org.hyperic.sigar.SigarException;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;

import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.win32.Service;
import org.hyperic.sigar.win32.ServiceConfig;

/**
 * Display all process information.
 */
public class ProcInfo extends SigarCommandBase implements Closable {

    public ProcInfo(Shell shell) {
        super(shell);
    }

    public ProcInfo() {
        super();
    }

    /**
     * gets the list of PIDs for a specific process merged by name and altname
     *
     * @param processname
     * @param altname
     * @return
     * @throws SigarException
     */
    public List<Long> GetData(String processname, String altname) throws SigarException {
        List<Long> pids = new ArrayList<Long>();

        ProcessPerformanceData ppd = new ProcessPerformanceData();

        pids.addAll(getPidsByName(processname, false)); //this.shell.findPids(processname);

        if (processname.contains("\\")) {
            String s = processname.substring(processname.lastIndexOf("\\"));
            if (Utility.stringIsNullOrEmpty(s)) {
                pids.addAll(getPidsByName(s, false)); //this.shell.findPids(processname);
            }
        }
        if (Utility.stringIsNullOrEmpty(altname)) {
            pids.addAll(getPidsByName(altname, false));
        }
        if (processname.contains("/")) {
            String s = processname.substring(processname.lastIndexOf("/"));
            if (Utility.stringIsNullOrEmpty(s)) {
                pids.addAll(getPidsByName(s, false)); //this.shell.findPids(processname);
            }
        }
        return pids;
    }

    /**
     * gets the list of PIDs for a specific process merged by name and altname
     *
     * @param processname
     * @param altname
     * @return
     * @throws SigarException
     */
    public List<Long> getDataWindows(String servicename, String altname, boolean IsWindowsService) throws SigarException {
        List<Long> pids = new ArrayList<Long>();
        //System.out.println("service name" + servicename + " " + altname);
        // ServiceConfig serviceConfig = new ServiceConfig(servicename);
        if (!Utility.stringIsNullOrEmpty(altname)) {
            pids.addAll(getPidsByName(altname, IsWindowsService));
        }
        try {
            Service svc = new Service(servicename);
            ServiceConfig config = svc.getConfig();
            String exe = svc.getConfig().getExe();
            String path = svc.getConfig().getPath();
            String statusstr = svc.getStatusString();
            String x = svc.getConfig().getStartName();
            int status = svc.getStatus();

            //ProcessPerformanceData ppd = new ProcessPerformanceData();
            pids.addAll(getPidsByName(exe, true)); //this.shell.findPids(processname);
        } catch (org.hyperic.sigar.win32.Win32Exception ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return pids;
    }
   

    public void restartWindowsService(String servicename) throws SigarException {
        // List<Long> pids = new ArrayList<Long>();
        // ServiceConfig serviceConfig = new ServiceConfig(servicename);
        Service svc = new Service(servicename);
        String exe = svc.getConfig().getExe();
        int status = svc.getStatus();
        //only stop if it's in a stoppable state
        if (status == 4) {
            svc.stop();
        }
        svc.start();
    }

    private List<Long> getPidsByName(String name, boolean isWindowsService) throws SigarException {

        //List<String> ret = new ArrayList<String>();
        List<Long> list = new ArrayList<Long>();
        if (Utility.stringIsNullOrEmpty(name)) {
            return list;
        }
        String test = name;
        if (isWindowsService && (test.lastIndexOf("\\") >= 0)) {
            test = test.substring(test.lastIndexOf("\\") + 1, test.length());  //trim off the path name
            test = test.substring(0, test.lastIndexOf("."));    //trim off the extension
        } else if ((test.lastIndexOf("\\") >= 0)) {
            test = test.substring(test.lastIndexOf("\\") + 1, test.length());  //trim off the path name
            //   test = test.substring(0, test.lastIndexOf("."));    //trim off the extension
        }
        long[] pids = this.proxy.getProcList();
        //   ProcState state = sigar.getProcState(pid);
        //   ProcTime time = null;
        //  String unknown = "???";

        for (int i = 0; i < pids.length; i++) {

            try {
                //ProcCredName cred = sigar.getProcCredName(pids[i]);
                ProcExe pe = sigar.getProcExe(pids[i]);
                //System.out.println(pe.getName());
                if (pe.getName().equalsIgnoreCase(test) || pe.getName().endsWith(test)) {

                    list.add(pids[i]);
                }
            } catch (Exception e) {
            }
            try {
                ProcState ps = sigar.getProcState(pids[i]);

                //  System.out.println(ps.getName());
                if (ps.getName().equalsIgnoreCase(test) || ps.getName().toLowerCase().endsWith(test.toLowerCase())) {
                    list.add(pids[i]);
                }
            } catch (Exception e) {
            }
        }
        return list;
    }


    List<Long> getDataJava(String name, String alsoKnownAs) {
        //throw new UnsupportedOperationException("Not yet implemented");

        //List<String> ret = new ArrayList<String>();
        List<Long> list = new ArrayList<Long>();
        if (Utility.stringIsNullOrEmpty(name)) {
            return list;
        }
        String test = name.replace("java|", "java:");
        try {

            long[] pids = this.proxy.getProcList();
            //   ProcState state = sigar.getProcState(pid);
            //   ProcTime time = null;
            //  String unknown = "???";

            for (int i = 0; i < pids.length; i++) {
                try {
                    String value = ProcUtil.getDescription(sigar, pids[i]);
                    if (value.equalsIgnoreCase(test)) {
                        list.add(pids[i]);
                    }
                } catch (Exception e) {
                }

                try {
                    //ProcCredName cred = sigar.getProcCredName(pids[i]);
                    ProcExe pe = sigar.getProcExe(pids[i]);
                    if (pe.getName().equalsIgnoreCase(test)) {
                        list.add(pids[i]);
                    }
                } catch (Exception e) {
                }
                try {
                    ProcState ps = sigar.getProcState(pids[i]);
                    if (ps.getName().equalsIgnoreCase(test)) {
                        list.add(pids[i]);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception ex) {
        }
        return list;
    }

    @Override
    public void close() throws Exception {
        if (sigar != null) {
            sigar.close();
            sigar = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {

        if (sigar != null) {
            System.out.println("WARN,. finalize called without closing sigar first" + this.getClass().getCanonicalName());
            sigar.close();
        }
        super.finalize();
    }
}
