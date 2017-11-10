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

import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarNotImplementedException;
import org.hyperic.sigar.SigarPermissionDeniedException;
import org.hyperic.sigar.ProcFd;
import org.hyperic.sigar.ProcExe;
import org.hyperic.sigar.cmd.Shell;

/**
 * Display process file information.
 */
public class ProcFileInfo extends SigarCommandBase implements Closable{

    public ProcFileInfo(Shell shell) {
        super(shell);
    }

    public ProcFileInfo() {
        super();
    }


    public long GetOpenFileCount(String processname) throws SigarException {
        long[] pids = this.shell.findPids(processname);
        if (pids == null || pids.length == 0) {
            return -1;
        }
        long openfiles = 0;

        for (int i = 0; i < pids.length; i++) {
            try {
                ProcFd fd = sigar.getProcFd(pids[i]);
                println("open file descriptors=" + fd.getTotal());
            } catch (SigarNotImplementedException e) {
            }
        }
        return openfiles;
    }

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
              System.out.println("WARN,. finalize called without closing sigar first"+ this.getClass().getCanonicalName());
            sigar.close();
        }
        super.finalize();
    }
}
