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
using System.Threading;

namespace WebServiceTestBedCMD
{
    class Program
    {
        static   HelloWorldESMWCF2.Service1Client c =null;
        static Random r;
        static MessageProcessor mp = null;
        static long count = 0;
        static bool running = true;
        static void Main(string[] args)
        {
           


            r = new Random();
             c= new HelloWorldESMWCF2.Service1Client();
            mp = MessageProcessor.Instance;

            int threads = 1;
      //     Thread[] items = new Thread[threads];
            Console.CancelKeyPress += delegate
            {
                // call methods to clean up
                running = false;
               /* for (int i = 0; i < items.Length; i++)
                {
                    items[i].Join();
                }*/
            };

         /*   for (int i = 0; i < items.Length; i++)
            {
                items[i] = new Thread(new ThreadStart(DoWork));
                items[i].Start();
            }
            while (running)
                Thread.Sleep(1000);*/
            DoWork();
        }

        static ConsoleColor fore = Console.ForegroundColor;
        static ConsoleColor bg = Console.BackgroundColor;
        static void DoWork()
        {
            while (running)
            {
             try
                {
                    count++;
                    String s = c.GetData(r.Next(100));
                    Console.WriteLine(count + " " + DateTime.Now.ToString("o") + " " + s + " Client Queue:" + MessageProcessor.GetQueueSize() + " Client Cache:" +MessageProcessor.GetPolicyCacheSize() + " Client last error " + MessageProcessor.LastErrorMessage);
                }
                catch (Exception ex)
                {
                    Console.BackgroundColor = ConsoleColor.Red;
                    Console.ForegroundColor = ConsoleColor.White;
                    Console.WriteLine(count + " " + DateTime.Now.ToString("o") + " " + ex.Message + " Client Queue:" + MessageProcessor.GetQueueSize() + " Client Cache:" + MessageProcessor.GetPolicyCacheSize() + " Client last error " + MessageProcessor.LastErrorMessage);
                    Console.ForegroundColor = fore;
                    Console.BackgroundColor = bg;
                }

            }
        }
    }


    
}
