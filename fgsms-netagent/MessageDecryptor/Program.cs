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
using org.miloss.fgsms.agent;
using System.IO;
using System.Diagnostics;

namespace MessageDecryptor
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length == 0)
            {
                Console.WriteLine("Usage, MessageDecryptor.exe <pathtoencryptedfile.msg> (optional, any parameter to use the shell to open the file)");
                Console.WriteLine(" Output is written <pathtoencryptedfile.msg>.xml ");
                return;
            }
            Util u = new Util();
            try
            {
                File.WriteAllText(args[0] + ".xml", u.DE(File.ReadAllText(args[0])));
                Console.WriteLine("success");
                if (args.Length == 2)
                {
                    try
                    {
                        Process p = new Process();
                        p.StartInfo.UseShellExecute = true;
                        p.StartInfo.FileName = args[0] + ".xml";
                        p.Start();
                        p.Close();
                        p.Dispose();
                    }
                    catch { }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
           
            
        }
    }
}
