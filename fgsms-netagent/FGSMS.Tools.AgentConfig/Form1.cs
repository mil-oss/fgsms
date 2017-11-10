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
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using System.ServiceModel.Configuration;
using System.Web.Services.Configuration;
using System.Windows.Forms;
using org.miloss.fgsms.agent.wcf;
using org.miloss.fgsms.agent;
using System.Collections.Generic;
using System.Xml;
using System.Collections;

namespace FGSMS.Tools.AgentConfig
{
    public partial class FormAgentConfig : Form
    {
        private CompleteConfig config = null;
        public FormAgentConfig()
        {
            InitializeComponent();
            tips = new ToolTip();
            tips.ShowAlways = true;
            config = loadCurrentConfig();
            this.FormClosed += new FormClosedEventHandler(FormAgentConfig_FormClosed);
          
        }

        void FormAgentConfig_FormClosed(object sender, FormClosedEventArgs e)
        {
           // this.Close();
           // this.Dispose();
            
        }

        class CompleteConfig
        {
            public List<container> items = new List<container>();
            public int containsKey(string key)
            {
                for (int i = 0; i < items.Count; i++)
                {
                    if (items[i].key.Equals(key))
                        return i;
                }
                return -1;
            }
        }
        class container
        {
            public string key;
            public string value;
        }
        private CompleteConfig loadCurrentConfig()
        {
            try
            {
                CompleteConfig ret = new CompleteConfig();
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(File.ReadAllText("current.config"));
                XmlNodeList list = doc.GetElementsByTagName("appSettings");
                XmlNode servicemodelsection = null;
                if (list.Count == 1)
                    servicemodelsection = list[0];

                IEnumerator it = servicemodelsection.ChildNodes.GetEnumerator();
                while (it.MoveNext())
                {
                    XmlNode temp = (XmlNode)it.Current;
                    if (temp.Name.Equals("add", StringComparison.CurrentCultureIgnoreCase))
                    {

                        container c = new container();
                        XmlAttribute a = temp.Attributes["key"];
                        XmlAttribute v = temp.Attributes["value"];
                        if (a != null && v != null)
                        {
                            c.key = a.Value;
                            c.value = v.Value;
                            ret.items.Add(c);
                        }
                    }
                }
                return ret;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error loading config! check for current.config file " + ex.Message);
            }
            return null;
        }
        private ToolTip tips;
        private bool hasDotNet35orHigher = false;



        private void radioButtonTransportOnly_CheckedChanged(object sender, EventArgs e)
        {

        }


        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (config != null)
            {
                dataGridView1.Rows.Clear();
                for (int i = 0; i < config.items.Count; i++)
                {
                    int idx = dataGridView1.Rows.Add(new DataGridViewRow());

                    dataGridView1.Rows[idx].Cells[0].Value = config.items[i].key;
                    dataGridView1.Rows[idx].Cells[1].Value = config.items[i].value;
                }
            }
        }







        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            WriteConfigFile("current.config");
        }

        private System.ServiceModel.Configuration.X509InitiatorCertificateServiceElement chooseServiceCert()
        {
           /* X509InitiatorCertificateServiceElement config = new X509InitiatorCertificateServiceElement();
            config.Certificate.StoreLocation = System.Security.Cryptography.X509Certificates.StoreLocation.LocalMachine;
            config.Certificate.StoreName = CertificateStoreLocation.GetStoreName();
            X509Certificate2 cert = CertificateChooser.ShowDialog(config.Certificate.StoreLocation, config.Certificate.StoreName);
            config.Certificate.X509FindType = X509FindType.FindBySerialNumber;
            config.Certificate.FindValue = cert.SerialNumber;
            return config;*/
            return null;
        }

        private 
            //System.ServiceModel.Configuration.X509InitiatorCertificateClientElement  
            void
            chooseClientCert()
        {
           /* X509InitiatorCertificateClientElement config = new X509InitiatorCertificateClientElement();
            config.StoreLocation = System.Security.Cryptography.X509Certificates.StoreLocation.LocalMachine;
            config.StoreName = CertificateStoreLocation.GetStoreName();*/
            X509Certificate2 cert = CertificateChooser.ShowDialog(StoreLocation.LocalMachine, StoreName.My, true);
            if (cert!=null)
            textBox1.Text = cert.SerialNumber;
            //config.FindValue = cert.SerialNumber;
            //return config;
        }

        private void buttonDCSCert_Click(object sender, EventArgs e)
        {
            /*  currentConfig.DataCollectorConfig.ServiceCertificate = chooseServiceCert();
              labelDCSCert.Text = currentConfig.DataCollectorConfig.ServiceCertificate.Certificate.FindValue;*/
        }

        private void buttonPCScert_Click(object sender, EventArgs e)
        {
            /*  currentConfig.PolicyConfig.ServiceCertificate = chooseServiceCert();
              labelPCSCert.Text = currentConfig.PolicyConfig.ServiceCertificate.Certificate.FindValue;*/
        }

        private void buttonAgentcert_Click(object sender, EventArgs e)
        {
           chooseClientCert();
            
        }

        private void aboutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(@"FGSMS was created by AO at US Army RDECOM CERDEC." + Environment.NewLine +
                "Available at https://github.com/mil-oss/fgsms." + Environment.NewLine +
                "Version " + FGSMSConstants.Version, "About");
        }

        private void radioButtonMutualCert_CheckedChanged(object sender, EventArgs e)
        {

            WriteConfigFile("current.config");
        }

        private void quitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Dispose();
        }

        private void saveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show("This will allow you to save a backup copy of the FGSMS config file." + Environment.NewLine +
                "It will not apply the new file to this machine nor will it be picked up by the installer.");
            SaveFileDialog sf = new SaveFileDialog();
            sf.AddExtension = true;
            sf.Filter = "Config File|*.config";
            //sf.FileName = "current.Config";
            sf.InitialDirectory = System.Environment.SystemDirectory;
            sf.OverwritePrompt = true;
            sf.Title = "Save as...";
            sf.ValidateNames = true;
            sf.CheckPathExists = true;
            sf.CheckFileExists = false;
            sf.AutoUpgradeEnabled = true;
            DialogResult r = sf.ShowDialog();
            if (r == DialogResult.OK)
            {
                WriteConfigFile(sf.FileName);
            }
        }

        private void WriteConfigFile(string filename)
        {
            try
            {
                config.items.Clear();
                //convert to the internal form
                foreach (DataGridViewRow r in dataGridView1.Rows)
                {
                    container c = new container();
                    c.key = r.Cells[0].Value.ToString();
                    c.value = r.Cells[1].Value.ToString();
                    config.items.Add(c);
                }


                XmlDocument doc = new XmlDocument();
                doc.LoadXml("<?xml version=\"1.0\"?><configuration>  <appSettings>  </appSettings></configuration>");
                XmlNodeList list = doc.GetElementsByTagName("appSettings");
                XmlNode servicemodelsection = null;
                if (list.Count == 1)
                    servicemodelsection = list[0];
                else
                {
                    MessageBox.Show("a strange error occured, please report this");
                }
                for (int i = 0; i < config.items.Count; i++)
                {
                    XmlElement e = doc.CreateElement("add");
                    e.SetAttribute("key", config.items[i].key);
                    e.SetAttribute("value", config.items[i].value);
                    servicemodelsection.AppendChild(e);
                }

                XmlWriterSettings xws = new XmlWriterSettings();

                xws.ConformanceLevel = ConformanceLevel.Document;
                xws.Indent = true;
                // xws.OutputMethod = XmlOutputMethod.Xml;
                XmlWriter xw = XmlWriter.Create(filename, xws);
                doc.Save(xw);
                xw.Flush();
                xw.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("error " + ex.Message);
            }
        }


        private void FormAgentConfig_Load(object sender, EventArgs e)
        {
            openToolStripMenuItem_Click(sender, e);
        }


        private void button1_Click(object sender, EventArgs e)
        {

        }

        private void dataGridView1_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            dataGridView1.CellEndEdit += new DataGridViewCellEventHandler(dataGridView1_CellEndEdit);
        }

        void dataGridView1_CellEndEdit(object sender, DataGridViewCellEventArgs e)
        {
            //TODO validate input
        }
        PasswordEncryptor f = new PasswordEncryptor();
        private void passwordEncryptorToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (f == null || f.IsDisposed)
            {
                f = new PasswordEncryptor();
            }
            if (f.Visible == false)
                f.Show(this);
        }

        private void helpToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            try
            {
                Process p = new Process();
                p.StartInfo = new ProcessStartInfo();
                p.StartInfo.UseShellExecute = true;
                p.StartInfo.FileName = Application.StartupPath + "\\help.html";
                p.Start();
                p.Close();
            }
            catch { }
        }

        private void dataGridView1_CellContentClick_1(object sender, DataGridViewCellEventArgs e)
        {

        }

        private void buttonAgentcert_Click_1(object sender, EventArgs e)
        {
            buttonAgentcert_Click(sender, e);
        }

        private void button1_Click_1(object sender, EventArgs e)
        {

        }

    }
}
