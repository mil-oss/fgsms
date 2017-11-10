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
using System.ServiceModel;

namespace HelloWorldESMTester
{
    // NOTE: If you change the interface name "IService1" here, you must also update the reference to "IService1" in Web.config.
    [ServiceContract]
    public interface IService1
    {
        [OperationContract]
        string WorkingGetData(int value); // end WorkingGetData

        [OperationContract]
        string CallDependantService(int value); // end CallDependantService

        [OperationContract]
        string CallWCFDependantService(int value); // end CallDependantService

        [OperationContract]
        string FailingGetData(int value); // end FailingGetData

        [OperationContract]
        string LongRunningGetData(int value); // end LongRunningGetData

        [OperationContract]
        string RandomWorkingMethod(int value); // end RandomWorkingMethod

        // TODO: Add your service operations here
    } // end interface IService1
} // end namespace HelloWorldESMTester
