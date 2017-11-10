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
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Diagnostics;

namespace FGSMS.Tools.AgentConfig
{
    public class CertificateChooser : Form
    {
        // Fields
        private StoreLocation _location;
        private StoreName _name;
        private Button buttonCancel;
        private Button buttonOk;
        private Button buttonViewDetails;
        private X509Certificate2 cert = null;
        private IContainer components = null;
        private Label labelIssuer;
        private ListBox listBox1;

        // Methods
        public CertificateChooser()
        {
            this.InitializeComponent();
        }

        private void buttonCancel_Click(object sender, EventArgs e)
        {
            this.cert = null;
            base.Dispose();
        }

        private void buttonOk_Click(object sender, EventArgs e)
        {
            this.cert = this.getCurrent();
            base.Dispose();
        }

        private void buttonViewDetails_Click(object sender, EventArgs e)
        {
            if (this.listBox1.SelectedItem != null)
            {
                string str = (string)this.listBox1.SelectedItem;
                X509Store s = new X509Store(this._name, this._location);
                s.Open(OpenFlags.ReadOnly);
                X509Certificate2Enumerator it = s.Certificates.GetEnumerator();
                while (it.MoveNext())
                {
                    X509Certificate2 cert = it.Current;
                    if (str == (cert.Subject + " | " + cert.SerialNumber))
                    {
                        byte[] c = cert.RawData;
                        File.WriteAllBytes(Environment.SystemDirectory + @"\..\temp\temp.cer", c);
                        Process p = new Process
                        {
                            StartInfo = { FileName = Environment.SystemDirectory + @"\..\temp\temp.cer", UseShellExecute = true }
                        };
                        p.Start();
                        p.Close();
                        break;
                    }
                }
            }
        }

        private void CertificateChooser_Load(object sender, EventArgs e)
        {
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing && (this.components != null))
            {
                this.components.Dispose();
            }
            base.Dispose(disposing);
        }

        private X509Certificate2 getCurrent()
        {
            if (this.listBox1.SelectedItem != null)
            {
                string str = (string)this.listBox1.SelectedItem;
                X509Store s = new X509Store(this._name, this._location);
                s.Open(OpenFlags.ReadOnly);
                X509Certificate2Enumerator it = s.Certificates.GetEnumerator();
                while (it.MoveNext())
                {
                    X509Certificate2 cert = it.Current;
                    if (str == (cert.Subject + " | " + cert.SerialNumber))
                    {
                        return cert;
                    }
                }
            }
            return null;
        }

        private void InitializeComponent()
        {
            this.buttonOk = new Button();
            this.buttonCancel = new Button();
            this.buttonViewDetails = new Button();
            this.listBox1 = new ListBox();
            this.labelIssuer = new Label();
            base.SuspendLayout();
            this.buttonOk.Anchor = AnchorStyles.Top;
            this.buttonOk.Location = new Point(0x80, 0xc9);
            this.buttonOk.Name = "buttonOk";
            this.buttonOk.Size = new Size(0x4b, 0x17);
            this.buttonOk.TabIndex = 0;
            this.buttonOk.Text = "OK";
            this.buttonOk.UseVisualStyleBackColor = true;
            this.buttonOk.Click += new EventHandler(this.buttonOk_Click);
            this.buttonCancel.Anchor = AnchorStyles.Top;
            this.buttonCancel.Location = new Point(0xd9, 0xc9);
            this.buttonCancel.Name = "buttonCancel";
            this.buttonCancel.Size = new Size(0x4b, 0x17);
            this.buttonCancel.TabIndex = 1;
            this.buttonCancel.Text = "Cancel";
            this.buttonCancel.UseVisualStyleBackColor = true;
            this.buttonCancel.Click += new EventHandler(this.buttonCancel_Click);
            this.buttonViewDetails.Anchor = AnchorStyles.Top;
            this.buttonViewDetails.Location = new Point(0x80, 0xac);
            this.buttonViewDetails.Name = "buttonViewDetails";
            this.buttonViewDetails.Size = new Size(0xa4, 0x17);
            this.buttonViewDetails.TabIndex = 2;
            this.buttonViewDetails.Text = "Show Details";
            this.buttonViewDetails.UseVisualStyleBackColor = true;
            this.buttonViewDetails.Click += new EventHandler(this.buttonViewDetails_Click);
            this.listBox1.Anchor = AnchorStyles.Right | AnchorStyles.Left | AnchorStyles.Bottom | AnchorStyles.Top;
            this.listBox1.FormattingEnabled = true;
            this.listBox1.Location = new Point(13, 13);
            this.listBox1.Name = "listBox1";
            this.listBox1.Size = new Size(0x192, 0x86);
            this.listBox1.TabIndex = 3;
            this.listBox1.SelectedIndexChanged += new EventHandler(this.listBox1_SelectedIndexChanged);
            this.labelIssuer.AutoSize = true;
            this.labelIssuer.Location = new Point(12, 150);
            this.labelIssuer.Name = "labelIssuer";
            this.labelIssuer.Size = new Size(0, 13);
            this.labelIssuer.TabIndex = 4;
            base.AutoScaleDimensions = new SizeF(6f, 13f);
            base.AutoScaleMode = AutoScaleMode.Font;
            base.ClientSize = new Size(0x1ab, 0xfe);
            base.Controls.Add(this.labelIssuer);
            base.Controls.Add(this.listBox1);
            base.Controls.Add(this.buttonViewDetails);
            base.Controls.Add(this.buttonCancel);
            base.Controls.Add(this.buttonOk);
            base.Name = "CertificateChooser";
            base.StartPosition = FormStartPosition.CenterScreen;
            this.Text = "Choose a Certificate";
            base.Load += new EventHandler(this.CertificateChooser_Load);
            base.ResumeLayout(false);
            base.PerformLayout();
        }

        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            X509Certificate2 c = this.getCurrent();
            if (c != null)
            {
                this.labelIssuer.Text = "Issuer: " + c.Issuer;
            }
        }

        public static X509Certificate2 ShowDialog(StoreLocation location, StoreName name, bool showonlyCertsWithPrivateKeys)
        {
            CertificateChooser f = new CertificateChooser
            {
                _location = location,
                _name = name
            };
            X509Store s = new X509Store(name, location);
            s.Open(OpenFlags.ReadOnly);
            X509Certificate2Enumerator it = s.Certificates.GetEnumerator();
            while (it.MoveNext())
            {
                X509Certificate2 cert = it.Current;
                if (!showonlyCertsWithPrivateKeys || (showonlyCertsWithPrivateKeys && cert.HasPrivateKey))
                    f.listBox1.Items.Add(cert.Subject + " | " + cert.SerialNumber);
            }
            f.ShowDialog();
            return f.cert;
        }
    }



}
