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

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.SigarException;

/**
 * Watch for changes in program memory usage.
 */
public class MemWatch {

    static final int SLEEP_TIME = 1000 * 10;

    public Mem MemoryBytesUsed() throws SigarException 
    {
        Sigar sigar = new Sigar();
           Mem last = sigar.getMem();
           sigar.close();
           sigar = null;
           return last;
    }
     
}

            
