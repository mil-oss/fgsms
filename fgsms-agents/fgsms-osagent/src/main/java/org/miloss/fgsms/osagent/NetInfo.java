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
import org.miloss.fgsms.services.interfaces.common.NetworkAdapterInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;

/**
 * Display network info.
 */
public class NetInfo extends SigarCommandBase implements Closable{

    public NetInfo(Shell shell) {
        super(shell);
    }

    public NetInfo() {
        super();
    }


    public List<NetworkAdapterInfo> GetNetworkInfo() throws SigarException {
        List<NetworkAdapterInfo> info = new ArrayList<NetworkAdapterInfo>();
        String[] names = this.sigar.getNetInterfaceList();
        NetworkAdapterInfo d = null;
        for (int i = 0; i < names.length; i++) {
            d = new NetworkAdapterInfo();
            NetInterfaceConfig config = this.sigar.getNetInterfaceConfig(names[i]);
            
            try {
                org.hyperic.sigar.NetInfo netinfo = this.sigar.getNetInfo();
                d.setDefaultGateway(netinfo.getDefaultGateway());
                d.getDns().add(netinfo.getPrimaryDns());
                d.getDns().add(netinfo.getSecondaryDns());
            } catch (Exception ex) {
                ex.printStackTrace();
                d.setDefaultGateway("unavailable");
            }
            d.setSubnetMask(config.getNetmask());
            d.setAdapterName(config.getName());
            d.setAdapterDescription(config.getDescription());
            d.setMtu(config.getMtu());
            d.setMac(config.getHwaddr());
            d.getIp().add(config.getAddress());
            // if (!ContainsMax(info, d.getMac()))
            info.add(d);
        }



        return info;
    }

   

    private boolean ContainsMax(List<NetworkAdapterInfo> info, String mac) {
        for (int i = 0; i < info.size(); i++) {
            if (info.get(i).getMac().equalsIgnoreCase(mac)) {
                return true;
            }
        }
        return false;
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
