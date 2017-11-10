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
using System.Text;
using System.Windows.Forms;
using System.ComponentModel;
using System.Security.Cryptography.X509Certificates;
using System.Drawing;

namespace FGSMS.Tools.AgentConfig
{
   public class CertificateStoreLocation : Form
{
    // Fields
    private Button buttonOK;
    private ComboBox comboBox1;
    private IContainer components = null;
    private Label label1;

    // Methods
    public CertificateStoreLocation()
    {
        this.InitializeComponent();
    }

    private void buttonOK_Click(object sender, EventArgs e)
    {
        base.Dispose();
    }

    protected override void Dispose(bool disposing)
    {
        if (disposing && (this.components != null))
        {
            this.components.Dispose();
        }
        base.Dispose(disposing);
    }

    public static StoreName GetStoreName()
    {
        CertificateStoreLocation form = new CertificateStoreLocation();
        form.ShowDialog();
        try
        {
            return (StoreName) Enum.Parse(typeof(StoreName), (string) form.comboBox1.SelectedItem);
        }
        catch (Exception)
        {
            return StoreName.My;
        }
    }

    private void InitializeComponent()
    {
        this.comboBox1 = new ComboBox();
        this.label1 = new Label();
        this.buttonOK = new Button();
        base.SuspendLayout();
        this.comboBox1.FormattingEnabled = true;
        this.comboBox1.Location = new Point(0x89, 12);
        this.comboBox1.Name = "comboBox1";
        this.comboBox1.Size = new Size(0x79, 0x15);
        this.comboBox1.TabIndex = 0;
        this.label1.AutoSize = true;
        this.label1.Location = new Point(13, 13);
        this.label1.Name = "label1";
        this.label1.Size = new Size(0x4c, 13);
        this.label1.TabIndex = 1;
        this.label1.Text = "Store Location";
        this.buttonOK.Location = new Point(0x63, 0x30);
        this.buttonOK.Name = "buttonOK";
        this.buttonOK.Size = new Size(0x4b, 0x17);
        this.buttonOK.TabIndex = 2;
        this.buttonOK.Text = "OK";
        this.buttonOK.UseVisualStyleBackColor = true;
        this.buttonOK.Click += new EventHandler(this.buttonOK_Click);
        base.AutoScaleDimensions = new SizeF(6f, 13f);
        base.AutoScaleMode = AutoScaleMode.Font;
        base.ClientSize = new Size(0x11c, 0x54);
        base.Controls.Add(this.buttonOK);
        base.Controls.Add(this.label1);
        base.Controls.Add(this.comboBox1);
        base.Name = "CertificateStoreLocation";
        base.StartPosition = FormStartPosition.CenterScreen;
        this.Text = "Choose a Store Location";
        base.Load += new EventHandler(this.StoreLocation_Load);
        base.ResumeLayout(false);
        base.PerformLayout();
    }

    private void StoreLocation_Load(object sender, EventArgs e)
    {
        this.comboBox1.Items.Add(StoreName.My.ToString());
        this.comboBox1.Items.Add(StoreName.Root.ToString());
        this.comboBox1.Items.Add(StoreName.TrustedPeople.ToString());
        this.comboBox1.Items.Add(StoreName.TrustedPublisher.ToString());
        this.comboBox1.Items.Add(StoreName.CertificateAuthority.ToString());
        this.comboBox1.Items.Add(StoreName.AuthRoot.ToString());
        this.comboBox1.Items.Add(StoreName.AddressBook.ToString());
        this.comboBox1.Items.Add(StoreName.Disallowed.ToString());
    }
}

 


}
