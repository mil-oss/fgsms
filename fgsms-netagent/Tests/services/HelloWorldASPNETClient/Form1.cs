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
using System.Windows.Forms;

namespace HelloWorldASPNETClient
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            helloworldaspnet.Service1 svc = new HelloWorldASPNETClient.helloworldaspnet.Service1();
            svc.Url = textBox1.Text;    
            MessageBox.Show(svc.HelloWorld());
        }

        private void button2_Click(object sender, EventArgs e)
        {
            dataAccessService c = new dataAccessService();
            c.GetMonitoredServiceList(new GetMonitoredServiceListRequestMsg());
        }
    }
}
