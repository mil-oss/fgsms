/*
 * Copyright (c) 2006-2008 Hyperic, Inc.
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

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemMap;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.DiskUsage;
import org.hyperic.sigar.cmd.Shell;

import org.hyperic.sigar.shell.FileCompleter;
import org.hyperic.sigar.util.GetlineCompleter;

/**
 * Report filesytem disk space usage.
 */
public class Iostat extends SigarCommandBase implements Closable{

   
    public class DiskRates {

        long bytesreadpersecond;
        long byteswritepersecond;
    }

    public DiskRates GetBytesPerSecond(String PartitionName, boolean isWindows) throws SigarException, InterruptedException {

        DiskRates d = new DiskRates();
        FileSystemMap mounts = this.proxy.getFileSystemMap();
      
        DiskUsage disk =null;
        if (isWindows)
                disk = this.sigar.getDiskUsage(PartitionName.toUpperCase());
        else
            disk  = this.sigar.getDiskUsage(PartitionName.toUpperCase());
        if (disk.getReadBytes() == Sigar.FIELD_NOTIMPL) {
            d.bytesreadpersecond = -1;
            d.byteswritepersecond = -1;
            return d;
        } else {
            d.bytesreadpersecond = (disk.getReadBytes());
            d.byteswritepersecond = (disk.getWriteBytes());
            Thread.sleep(1000);
            disk  = this.sigar.getDiskUsage(PartitionName.toUpperCase());
            d.bytesreadpersecond = (disk.getReadBytes()) - d.bytesreadpersecond;
            d.byteswritepersecond = (disk.getWriteBytes()) - d.byteswritepersecond;

        }
        return d;
    }
    private GetlineCompleter completer;

    public Iostat(Shell shell) {
        super(shell);
       
        this.completer = new FileCompleter(shell);
    }

    public Iostat() {
        super();
      
    }

    public GetlineCompleter getCompleter() {
        return this.completer;
    }


    /*
     * public static void main(String[] args) throws Exception { new
     * Iostat().processCommand(args);
    }
     */
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
