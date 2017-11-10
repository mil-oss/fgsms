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
using System.Text;
using System.Windows.Forms;
using org.miloss.fgsms.agent;

namespace FGSMS.Tools.AgentConfig
{
    public partial class PasswordEncryptor : Form
    {
        public PasswordEncryptor()
        {
            InitializeComponent();
        }
        org.miloss.fgsms.agent.Util u = new Util();
        private void button1_Click(object sender, EventArgs e)
        {
            textBox2.Text = u.EN(textBox1.Text);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (textBox1.Text.Equals(u.DE(textBox2.Text), StringComparison.CurrentCulture))
                MessageBox.Show("Validated", "Password", MessageBoxButtons.OK, MessageBoxIcon.None);
            else MessageBox.Show("Invalid", "Password", MessageBoxButtons.OK, MessageBoxIcon.Stop);
        }

        private void PasswordEncryptor_Load(object sender, EventArgs e)
        {
            
        }
    }
}
