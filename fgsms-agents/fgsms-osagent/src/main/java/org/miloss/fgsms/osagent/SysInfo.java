/*
 * Copyright (c) 2006 Hyperic, Inc.
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

import java.util.Arrays;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.CpuInfo;
import org.hyperic.sigar.cmd.Free;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.Ulimit;
import org.hyperic.sigar.cmd.Uptime;


import org.hyperic.sigar.cmd.Version;


/**
 * Display System Information
 */
public class SysInfo extends SigarCommandBase implements Closable{

    public SysInfo(Shell shell) {
        super(shell);
    }

    public SysInfo() {
        super();
    }

    public String getUsageShort() {
        return "Display system information";
    }

    public void output(String[] args) throws SigarException {
        //sigar/os info
        Version.printInfo(this.out);
        println("");

        //uptime
        new Uptime(this.shell).output(args);
        println("");

        //cpu info
        CpuInfo cpuinfo = new CpuInfo(this.shell);
        cpuinfo.displayTimes = false;
        cpuinfo.output(args);
        println("");

        //memory info
        new Free(this.shell).output(args);
        println("");

        println("File Systems........." +
                Arrays.asList(this.sigar.getFileSystemList()));
        println("");

        println("Network Interfaces..." +
                Arrays.asList(this.sigar.getNetInterfaceList()));
        println("");

        //system resource limits
        println("System resource limits:");
        new Ulimit(this.shell).output(args);
    }
/*
    public static void main(String[] args) throws Exception {
        new SysInfo().processCommand(args);
    }*/
        @Override
    public void close() throws Exception{
        if (sigar != null) {
            sigar.close();
            sigar = null;
        }
    }
    @Override
    protected void finalize() throws Throwable
    {
        
          if (sigar != null) {
              System.out.println("WARN,. finalize called without closing sigar first" + this.getClass().getCanonicalName());
            sigar.close();
        }
        super.finalize();
    }
}
