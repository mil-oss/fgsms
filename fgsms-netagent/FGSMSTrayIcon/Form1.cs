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
using System.Threading;

namespace FGSMSTrayIcon
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
            this.Resize += new EventHandler(Form1_Resize);
            notifyIcon1.DoubleClick += new EventHandler(notifyIcon1_DoubleClick);
            notifyIcon1.ContextMenuStrip = contextMenuStrip1;
            ConfigLoader cfg = new ConfigLoader();

            //FIXME need some kind of configuration tool for this
            ss = cfg.GetSSProxy(cfg.SSurls[0], "fgsmsadmin", "", null);
            t = new Thread(new ThreadStart(getStatusData));
            t.Start();
            this.FormClosing += new FormClosingEventHandler(Form1_FormClosing);
            listBox1.DrawItem += new DrawItemEventHandler(listBox1_DrawItem);
        }

        void listBox1_DrawItem(object sender, DrawItemEventArgs e)
        {
            if (e.Index < 0)
                return;
            e.DrawBackground();
            Brush b = Brushes.Black;
            ListBox box = (ListBox)sender;
            GetStatusResponseMsg item = (GetStatusResponseMsg)box.Items[e.Index];
            if (item.Operational)
                b = Brushes.Lime;
            else
                b = Brushes.Red;
            e.Graphics.DrawString(item.TimeStamp.ToString("o") + " " + item.URI + " " + item.Operational + " " + item.Message,
                e.Font, b, e.Bounds, StringFormat.GenericDefault);
            e.DrawFocusRectangle();
        }

        void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            running = false;
            t.Join();
            Application.Exit();
                
        }
        statusServiceBinding ss = null;

        int lastofflinecount = 0;
        Thread t;
        int interval = 10000;
        DateTime lastranat = DateTime.MinValue;
        bool running = true;
        void getStatusData()
        {
            while (running)
            {
                Thread.Sleep(1000);
                if (!running)
                    return;
                if (lastranat.AddMilliseconds(interval) < DateTime.Now)
                {
                    notifyIcon1.ShowBalloonTip(1000, "Requesting status info", "working", ToolTipIcon.Info);
                    lastranat = DateTime.Now;
                    CheckForIllegalCrossThreadCalls = false;
                    GetStatusRequestMsg req = new GetStatusRequestMsg();
                    req.classification = new SecurityWrapper();
                    req.classification.classification = ClassificationType.U;

                    GetStatusResponseMsg[] status = null;
                    try
                    {
                        status = ss.GetAllStatus(req);
                    }
                    catch (Exception ex)
                    {
                        notifyIcon1.ShowBalloonTip(10000, "Oh Uh", "Unable to contact FGSMS " + Environment.NewLine + ex.Message, ToolTipIcon.Error);
                        lastofflinecount = 0;
                        return;
                    }

                    if (status != null)
                    {
                        listBox1.Items.Clear();
                        String msg = "";
                        int count = 0;
                        for (int i = 0; i < status.Length; i++)
                        {
                            listBox1.Items.Add(status[i]);
                            //textBox1.Text +=status[i].Operational + " " + status[i].TimeStamp.ToString("o") + " " + status[i].URI + " " + status[i].Message + Environment.NewLine;
                            if (!status[i].Operational)
                            {
                                
                                msg += status[i].URI + Environment.NewLine;
                                count++;
                            }
                        }
                        if (!String.IsNullOrEmpty(msg))
                        {
                            if (count != lastofflinecount)
                            {
                                notifyIcon1.Text = "FGSMS - There are " + count + " services offline!";
                                lastofflinecount = count;
                                notifyIcon1.ShowBalloonTip(10000, "Oh Uh", "The following services are offline" + Environment.NewLine + msg, ToolTipIcon.Warning);
                            }
                        }
                        else
                            notifyIcon1.ShowBalloonTip(10000, "A-OK", "All Services are Online", ToolTipIcon.Info);

                    }
                }
            }
        }
        void notifyIcon1_DoubleClick(object sender, EventArgs e)
        {
            Show();
            WindowState = FormWindowState.Normal;
        }

        void Form1_Resize(object sender, EventArgs e)
        {
            if (FormWindowState.Minimized == WindowState)
                Hide();
        }

        private void toolStripMenuItem1_Click(object sender, EventArgs e)
        {
            running = false;
            t.Join();
            Application.Exit();
        }

        private void detailsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Show();
            WindowState = FormWindowState.Normal;
        }

        private void configureToolStripMenuItem_Click(object sender, EventArgs e)
        {
            panel1.Show();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            panel1.Hide();
        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            running = false;
            t.Join();
            Application.Exit();
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }
    }
}
