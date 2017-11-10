/*
 * Copyright (c) 2006 Hyperic, Inc.
 * Copyright (c) 2010 VMware, Inc.
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
import java.util.GregorianCalendar;
import java.util.List;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.DriveInformation;

import org.hyperic.sigar.*;
import org.hyperic.sigar.cmd.Shell;

import org.hyperic.sigar.shell.FileCompleter;
import org.hyperic.sigar.util.GetlineCompleter;

/**
 * Report filesytem disk space usage.
 */
public class Df extends SigarCommandBase implements Closable{

   
    private GetlineCompleter completer;
    private boolean opt_i;

    public Df(Shell shell) {
        super(shell);
      
        this.completer = new FileCompleter(shell);
    }

    public Df() {
        super();
      
    }

    public GetlineCompleter getCompleter() {
        return this.completer;
    }



    public List<DriveInformation> DriveInfo() throws SigarException {
        List<DriveInformation> info = new ArrayList<DriveInformation>();
        ArrayList sys = new ArrayList();
        FileSystem[] fslist = this.proxy.getFileSystemList();
        for (int i = 0; i < fslist.length; i++) {
            sys.add(fslist[i]);
        }
        long  total=0;
        FileSystem fs = null;
        DriveInformation d = null;
        for (int i = 0; i < sys.size(); i++) {
            d = new DriveInformation();
            d.setTimestamp(new GregorianCalendar());
            fs = (FileSystem) sys.get(i);
            try {
                FileSystemUsage usage;
                d.setSystemid(fs.getDevName());
           //     d.set
                d.setPartition(fs.getDirName());
                if (Utility.stringIsNullOrEmpty(d.getSystemid())) {
                    d.setSystemid(d.getPartition());
                }
                d.setType(fs.getTypeName() + " " + fs.getSysTypeName());

                if (fs instanceof NfsFileSystem) {
                    NfsFileSystem nfs = (NfsFileSystem) fs;

                    if (!nfs.ping()) {
                        d.setOperational(false);
                        println(nfs.getUnreachableMessage());
                        info.add(d);
                        continue;
                    } else {
                        d.setOperational(true);
                    }
                }
                usage = null;
                try {
                    usage = this.sigar.getFileSystemUsage(fs.getDevName());
                } catch (Exception ex) {
                }
                if (usage == null) {
                    try {
                        usage = this.sigar.getFileSystemUsage(fs.getDirName());
                    } catch (Exception ex) {
                    }
                }
                if (this.opt_i) {
           //         used = usage.getFiles() - usage.getFreeFiles();
             //       avail = usage.getFreeFiles();
                    total = usage.getFiles();
                    if (total == 0) {
                     //   pct = 0;
                    } else {
            //            long u100 = used * 100;
            //            pct = u100 / total
            //                    + ((u100 % total != 0) ? 1 : 0);
                    }
                } else {
                 //   used = usage.getTotal() - usage.getFree();
                  //  avail = usage.getAvail();
                    total = usage.getTotal();

            //        pct = (long) (usage.getUsePercent() * 100);
                }
                d.setTotalspace(total / 1024);  //this is reported in MB
                d.setOperational(true);

                info.add(d);
            } catch (Exception e) {
                //e.g. on win32 D:\ fails with "Device not ready"
                //if there is no cd in the drive.
         //       e.printStackTrace();
              //  used = avail = total = pct = 0;
            }
        }
        return info;
    }


    public DriveInformation GetDriveInfo(String get) throws SigarException {
        DriveInformation d = new DriveInformation();
        FileSystem[] fslist = this.proxy.getFileSystemList();
        
        for (int i = 0; i < fslist.length; i++) {
            //modified to allow for C: , sigars returns C:\
            if (fslist[i].getDevName().startsWith(get)) {

                if (fslist[i] instanceof NfsFileSystem) {
                    NfsFileSystem nfs = (NfsFileSystem) fslist[i];
                    if (!nfs.ping()) {
                        d.setOperational(false);
                    }
                }
                FileSystemUsage usage = this.sigar.getFileSystemUsage(fslist[i].getDirName());
                d.setId(fslist[i].getDevName());
                d.setFreespace(usage.getFree());
                d.setPartition(fslist[i].getDirName());
                d.setType(fslist[i].getTypeName());
                return d;
            }
        }
        return null;
    }

      @Override
    public void close() throws Exception{
        if (sigar != null) {
            sigar.close();
            sigar=null;
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