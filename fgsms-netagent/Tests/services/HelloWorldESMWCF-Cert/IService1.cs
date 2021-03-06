﻿/**
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

namespace HelloWorldESMWCF_Cert
{
    [ServiceContract(ProtectionLevel = System.Net.Security.ProtectionLevel.Sign)]
    public interface IService1
    {
        [OperationContract(ProtectionLevel = System.Net.Security.ProtectionLevel.Sign)]
        string WorkingGetData(int value); // end WorkingGetData

        [OperationContract(ProtectionLevel = System.Net.Security.ProtectionLevel.Sign)]
        string FailingGetData(int value); // end FailingGetData

        [OperationContract(ProtectionLevel = System.Net.Security.ProtectionLevel.Sign)]
        string LongRunningGetData(int value); // end LongRunningGetData

        [OperationContract(ProtectionLevel = System.Net.Security.ProtectionLevel.Sign)]
        string RandomWorkingMethod(int value); // end RandomWorkingMethod

        // TODO: Add your service operations here
    } // end interface IService1
} // end namespace HelloWorldESMWCF_Cert
