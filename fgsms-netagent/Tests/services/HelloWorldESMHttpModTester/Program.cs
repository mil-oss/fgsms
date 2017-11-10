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

namespace HelloWorldESMHttpModTester
{
    class Program
    {
        static void Main(string[] args)
        {
            localhost.Service1 svc = new HelloWorldESMHttpModTester.localhost.Service1();
            svc.Url = @"http://localhost:55801/Service1.asmx";
            try
            {
                Console.Out.WriteLine(
                svc.HelloWorld());
            }
            catch (Exception ex)
            {
                Console.Out.WriteLine(ex.Message);
            }
        } // end Main
    } // end class Program
} // end namespace HelloWorldESMHttpModTester
