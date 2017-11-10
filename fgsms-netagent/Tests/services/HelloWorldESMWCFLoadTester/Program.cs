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

namespace HelloWorldESMWCFLoadTester
{
    class Program
    {
        static void Main(string[] args)
        {
            ServiceReference1.Service1Client service = new HelloWorldESMWCFLoadTester.ServiceReference1.Service1Client();
            try
            {
                int count = 0;
                while (true)
                {
                    Console.Out.WriteLine(count.ToString() + service.WorkingGetData(5));
                    count++;
                }
            }
            catch (Exception ex)
            {
                Console.Out.WriteLine(ex.Message);
            }
        }
    }
}
