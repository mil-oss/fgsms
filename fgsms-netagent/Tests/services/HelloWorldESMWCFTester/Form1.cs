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

namespace HellWorldESMTesterClient
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            client = new HellWorldESMTesterClient.ServiceReference1.Service1Client();
        } // end Form1

        ServiceReference1.Service1Client client;

        private void button1_Click(object sender, EventArgs e)
        {
           // = new HellWorldESMTesterClient.ServiceReference1.Service1Client();
            string s = client.WorkingGetData(5);
            MessageBox.Show(s);
        } // end button1_Click

        private void button2_Click(object sender, EventArgs e)
        {
            //ServiceReference1.Service1Client client = new HellWorldESMTesterClient.ServiceReference1.Service1Client();
            string s = client.LongRunningGetData(5);
            MessageBox.Show(s);
        } // end button2_Click

        private void button3_Click(object sender, EventArgs e)
        {
            //ServiceReference1.Service1Client client = new HellWorldESMTesterClient.ServiceReference1.Service1Client();
            try
            {
                string s = client.FailingGetData(5);
                MessageBox.Show(s);
            }
            catch (Exception ex)
            {
                MessageBox.Show("all is normal" + ex.Message);
            }
        } // end button3_Click

        private void button4_Click(object sender, EventArgs e)
        {
            ServiceReference1.Service1Client gamble = new HellWorldESMTesterClient.ServiceReference1.Service1Client();
            try
            {
                string s = gamble.RandomWorkingMethod(5);
                MessageBox.Show(s);
            }
            catch (Exception ex)
            {
                MessageBox.Show("all is normal" + ex.Message);
            }
        } // end button4_Click

        private void button5_Click(object sender, EventArgs e)
        {
            ServiceReference1.Service1Client gamble = new HellWorldESMTesterClient.ServiceReference1.Service1Client();
            try
            {
                string s = gamble.CallDependantService(5);
                MessageBox.Show(s);
            }
            catch (Exception ex)
            {
                MessageBox.Show("all is normal" + ex.Message);
            }
        }

        private void button6_Click(object sender, EventArgs e)
        {
            ServiceReference1.Service1Client gamble = new HellWorldESMTesterClient.ServiceReference1.Service1Client();
            try
            {
                string s = gamble.CallWCFDependantService(5);
                MessageBox.Show(s);
            }
            catch (Exception ex)
            {
                MessageBox.Show("all is normal" + ex.Message);
            }
        } // end button5_Click
    } // end class Form1
} // end namespace HellWorldESMTesterClient
