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

namespace FGSMS.Tools.AgentConfig
{
    class Program
    {
        static void Main(string[] args)
        {
            org.miloss.fgsms.agent.Util u = new org.miloss.fgsms.agent.Util();
            System.Console.WriteLine("Encrypts a password that can only be decrypted by FGSMS.NET Agents and Services. Java based capabilities do not and cannot use this cipher.");
            //System.Console.WriteLine("WARNING, password will be echoed locally.");
            System.Console.Write("Password: ");
            //string t=System.Console.ReadLine();
            string t = ReadPassword();
            System.Console.WriteLine();
            System.Console.Write("Password Confirmation: ");
            //string t=System.Console.ReadLine();
            string t2 = ReadPassword();
            System.Console.WriteLine();
            if (t.Equals(t2, StringComparison.CurrentCulture))
                System.Console.WriteLine(u.EN(t));
            else
                System.Console.WriteLine("Passwords did not match");
        }
        static string ReadPassword()
        {
            Stack<string> passbits = new Stack<string>();
            for (ConsoleKeyInfo cki = Console.ReadKey(true); cki.Key != ConsoleKey.Enter; cki = Console.ReadKey(true))
            {
                if (cki.Key == ConsoleKey.Backspace)
                {
                    Console.SetCursorPosition(Console.CursorLeft - 1, Console.CursorTop);
                    Console.Write(" ");
                    Console.SetCursorPosition(Console.CursorLeft - 1, Console.CursorTop);
                    passbits.Pop();
                }
                else
                {
                    Console.Write("*");
                    passbits.Push(cki.KeyChar.ToString());
                }

            }
            string[] pass = passbits.ToArray();
            Array.Reverse(pass);
            return string.Join(string.Empty, pass);
        }
        

    }
}
