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
using System.Threading;

namespace HelloWorldESMWCFSignEncrypt
{
    // NOTE: If you change the class name "Service1" here, you must also update the reference to "Service1" in Web.config and in the associated .svc file.
    public class Service1 : IService1
    {
        #region IService1 Members

        public string WorkingGetData(int value)
        {

            return string.Format("You entered: {0}", value);

        }

        public string FailingGetData(int value)
        {
            throw new NotImplementedException();
        }

        public string LongRunningGetData(int value)
        {
            Thread.Sleep(5000);
            return string.Format("You entered: {0}", value);
        }

        #endregion

        #region IService1 Members


        public string RandomWorkingMethod(int value)
        {
            Random r = new Random();
            int x = r.Next(3);
            if (x == 0)
                throw new ArithmeticException();
            return "This only works 1 out of 4 times. By the way, you entered " + value;
        }

        #endregion
    }
}
