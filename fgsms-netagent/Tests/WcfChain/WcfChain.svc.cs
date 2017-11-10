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
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;

namespace WcfChain
{
    // NOTE: You can use the "Rename" command on the "Refactor" menu to change the class name "Service1" in code, svc and config file together.
    public class Service1 : WcfChain
    {
        public string GetData(int value)
        {
            Chain.Service1Client c = new Chain.Service1Client();
            return c.GetData(value);
            //return string.Format("You entered: {0}", value);
        }

        public CompositeType GetDataUsingDataContract(CompositeType composite)
        {
            Chain.Service1Client c = new Chain.Service1Client();
            Chain.CompositeType req = new Chain.CompositeType();
            req.BoolValue = composite.BoolValue;
            req.StringValue = composite.StringValue;
            req = c.GetDataUsingDataContract(req);

            CompositeType res = new CompositeType();
            res.StringValue = req.StringValue;
            res.BoolValue = req.BoolValue;
            return res;
        }
    }
}
