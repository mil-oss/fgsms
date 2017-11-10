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
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace HelloWorldESMWCFSignEncryptTester
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            
            helloworld.Service1Client service = new HelloWorldESMWCFSignEncryptTester.helloworld.Service1Client();
            try
            {
                MessageBox.Show(service.WorkingGetData(1));
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            helloworld.Service1Client service = new HelloWorldESMWCFSignEncryptTester.helloworld.Service1Client();
            try
            {
                MessageBox.Show(service.FailingGetData(1));
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            helloworld.Service1Client service = new HelloWorldESMWCFSignEncryptTester.helloworld.Service1Client();
            try
            {
                MessageBox.Show(service.LongRunningGetData(1));
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {
            helloworld.Service1Client service = new HelloWorldESMWCFSignEncryptTester.helloworld.Service1Client();
            try
            {
                MessageBox.Show(service.RandomWorkingMethod(1));
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }
    }
}
