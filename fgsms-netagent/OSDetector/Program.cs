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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;
using System.Reflection;

namespace OSDetector
{
    class Program
    {
        static void Main(string[] args)
        {
            String dir = Assembly.GetExecutingAssembly().Location;
            dir = dir.Substring(0, dir.LastIndexOf("\\"));
            dir = dir + "\\";
            Console.Out.WriteLine("Current working directory is " + dir);

            Process p;
            if (args == null || args.Length == 0)
            {
                if (Is64Bit())
                {
                    Console.Out.WriteLine("64-bit installer");
                    if (!File.Exists(dir + "\\OSAgentServiceInstallx64.cmd"))
                        Console.Out.WriteLine("Installer script not found!");
                    else
                    {
                        p = new Process();
                        p.StartInfo.FileName = dir + "\\OSAgentServiceInstallx64.cmd";
                        p.StartInfo.WorkingDirectory = dir;
                        p.Start();
                        p.WaitForExit();
                        p.Dispose();
                    }
                   
                    
                }
                else
                {
                    Console.Out.WriteLine("32-bit installer");
                    if (!File.Exists(dir + "\\OSAgentServiceInstall.cmd"))
                        Console.Out.WriteLine("Installer script not found!");
                    else
                    {
                        p = new Process();
                        p.StartInfo.FileName =dir +  "\\OSAgentServiceInstall.cmd";
                        p.StartInfo.WorkingDirectory = dir;
                        p.Start();
                        p.WaitForExit();
                        p.Dispose();
                       
                    }
                  
                }
            }
            else
            {
                if (Is64Bit())
                {
                    Console.Out.WriteLine("64-bit UNinstaller");
                    if (!File.Exists(dir + "\\OSAgentServiceRemovex64.cmd"))
                        Console.Out.WriteLine("Installer script not found!");
                    else
                    {
                        p = new Process();
                        p.StartInfo.FileName = dir + "\\OSAgentServiceRemovex64.cmd";
                        p.StartInfo.WorkingDirectory = dir;
                        p.Start();
                        p.WaitForExit();
                        p.Dispose();
                    }
                  

                 
                }
                else
                {
                    Console.Out.WriteLine("32-bit UNinstaller");
                    if (!File.Exists(dir + "\\OSAgentServiceRemove.cmd"))
                        Console.Out.WriteLine("Installer script not found!");
                    else
                    {
                        p = new Process();
                        p.StartInfo.FileName = dir + "\\OSAgentServiceRemove.cmd";
                        p.Start();
                        p.WaitForExit();
                        p.Dispose();
                        
                    }
                   
                }
            }
            //MessageBox.Show("hi");
        }

        [DllImport("kernel32.dll", SetLastError = true, CallingConvention = CallingConvention.Winapi)]
        [return: MarshalAs(UnmanagedType.Bool)]
        public static extern bool IsWow64Process([In] IntPtr hProcess, [Out] out bool lpSystemInfo);

        public static bool Is64Bit()
        {
            if (IntPtr.Size == 8 || (IntPtr.Size == 4 && Is32BitProcessOn64BigProcessor()))
            {
                return true;
            }
            else return false;

        }

        private static bool Is32BitProcessOn64BigProcessor()
        {
            bool retVal;
            IsWow64Process(Process.GetCurrentProcess().Handle, out retVal);
            return retVal;

        }
    }
}
