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
namespace WebServiceTestBed
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.go_button = new System.Windows.Forms.Button();
            this.textBoxThreadsToRun = new System.Windows.Forms.TextBox();
            this.textBoxURL = new System.Windows.Forms.TextBox();
            this.button1 = new System.Windows.Forms.Button();
            this.textBox2 = new System.Windows.Forms.TextBox();
            this.textBoxContext1 = new System.Windows.Forms.TextBox();
            this.textboxContext2 = new System.Windows.Forms.TextBox();
            this.textBoxContext3 = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.textBoxMultiplier = new System.Windows.Forms.TextBox();
            this.label6 = new System.Windows.Forms.Label();
            this.button2 = new System.Windows.Forms.Button();
            this.label7 = new System.Windows.Forms.Label();
            this.textBoxTimeOutASPNET = new System.Windows.Forms.TextBox();
            this.stop_threads = new System.Windows.Forms.Button();
            this.logdisplay = new System.Windows.Forms.Button();
            this.button3 = new System.Windows.Forms.Button();
            this.textBoxTTLRunForTime = new System.Windows.Forms.TextBox();
            this.label8 = new System.Windows.Forms.Label();
            this.button4 = new System.Windows.Forms.Button();
            this.checkBoxPersistConnection = new System.Windows.Forms.CheckBox();
            this.textBox4 = new System.Windows.Forms.TextBox();
            this.button5 = new System.Windows.Forms.Button();
            this.textBox5Timer = new System.Windows.Forms.TextBox();
            this.textBoxDelayPerHit = new System.Windows.Forms.TextBox();
            this.label9 = new System.Windows.Forms.Label();
            this.button6 = new System.Windows.Forms.Button();
            this.button7 = new System.Windows.Forms.Button();
            this.comboBox1 = new System.Windows.Forms.ComboBox();
            this.label10 = new System.Windows.Forms.Label();
            this.textBoxFilepath = new System.Windows.Forms.TextBox();
            this.checkBoxStopOnError = new System.Windows.Forms.CheckBox();
            this.button8 = new System.Windows.Forms.Button();
            this.label11 = new System.Windows.Forms.Label();
            this.label12 = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // go_button
            // 
            this.go_button.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.go_button.Location = new System.Drawing.Point(90, 222);
            this.go_button.Name = "go_button";
            this.go_button.Size = new System.Drawing.Size(65, 23);
            this.go_button.TabIndex = 0;
            this.go_button.Text = "Go";
            this.go_button.UseVisualStyleBackColor = true;
            this.go_button.Click += new System.EventHandler(this.go_button_Click);
            // 
            // textBoxThreadsToRun
            // 
            this.textBoxThreadsToRun.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBoxThreadsToRun.Location = new System.Drawing.Point(125, 117);
            this.textBoxThreadsToRun.Name = "textBoxThreadsToRun";
            this.textBoxThreadsToRun.Size = new System.Drawing.Size(434, 20);
            this.textBoxThreadsToRun.TabIndex = 1;
            this.textBoxThreadsToRun.Text = "4";
            // 
            // textBoxURL
            // 
            this.textBoxURL.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBoxURL.Location = new System.Drawing.Point(126, 12);
            this.textBoxURL.Name = "textBoxURL";
            this.textBoxURL.Size = new System.Drawing.Size(433, 20);
            this.textBoxURL.TabIndex = 2;
            this.textBoxURL.Text = "http://localhost:44593/Echo1.svc";
            // 
            // button1
            // 
            this.button1.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.button1.Location = new System.Drawing.Point(161, 222);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(65, 23);
            this.button1.TabIndex = 3;
            this.button1.Text = "Save ";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // textBox2
            // 
            this.textBox2.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBox2.Location = new System.Drawing.Point(9, 466);
            this.textBox2.Multiline = true;
            this.textBox2.Name = "textBox2";
            this.textBox2.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox2.Size = new System.Drawing.Size(624, 136);
            this.textBox2.TabIndex = 4;
            // 
            // textBoxContext1
            // 
            this.textBoxContext1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBoxContext1.Location = new System.Drawing.Point(126, 39);
            this.textBoxContext1.Name = "textBoxContext1";
            this.textBoxContext1.Size = new System.Drawing.Size(433, 20);
            this.textBoxContext1.TabIndex = 5;
            this.textBoxContext1.Text = "CN=Test,CN=Users,DC=domain,DC=com";
            // 
            // textboxContext2
            // 
            this.textboxContext2.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textboxContext2.Location = new System.Drawing.Point(126, 65);
            this.textboxContext2.Name = "textboxContext2";
            this.textboxContext2.Size = new System.Drawing.Size(433, 20);
            this.textboxContext2.TabIndex = 6;
            this.textboxContext2.Text = "testResource";
            // 
            // textBoxContext3
            // 
            this.textBoxContext3.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBoxContext3.Location = new System.Drawing.Point(125, 91);
            this.textBoxContext3.Name = "textBoxContext3";
            this.textBoxContext3.Size = new System.Drawing.Size(434, 20);
            this.textBoxContext3.TabIndex = 7;
            this.textBoxContext3.Text = "testAction";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 15);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(80, 13);
            this.label1.TabIndex = 8;
            this.label1.Text = "URL to Service";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(12, 42);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(49, 13);
            this.label2.TabIndex = 9;
            this.label2.Text = "Context1";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(12, 68);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(49, 13);
            this.label3.TabIndex = 10;
            this.label3.Text = "Context2";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(12, 94);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(49, 13);
            this.label4.TabIndex = 11;
            this.label4.Text = "Context3";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(12, 120);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(96, 13);
            this.label5.TabIndex = 12;
            this.label5.Text = "Threads/Requests";
            // 
            // textBoxMultiplier
            // 
            this.textBoxMultiplier.Location = new System.Drawing.Point(125, 143);
            this.textBoxMultiplier.Name = "textBoxMultiplier";
            this.textBoxMultiplier.Size = new System.Drawing.Size(339, 20);
            this.textBoxMultiplier.TabIndex = 13;
            this.textBoxMultiplier.Text = "100000";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(12, 146);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(48, 13);
            this.label6.TabIndex = 14;
            this.label6.Text = "Multiplier";
            // 
            // button2
            // 
            this.button2.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.button2.Location = new System.Drawing.Point(232, 222);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(65, 23);
            this.button2.TabIndex = 15;
            this.button2.Text = "Count";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(12, 171);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(67, 13);
            this.label7.TabIndex = 17;
            this.label7.Text = "Timeout (ms)";
            // 
            // textBoxTimeOutASPNET
            // 
            this.textBoxTimeOutASPNET.Location = new System.Drawing.Point(125, 168);
            this.textBoxTimeOutASPNET.Name = "textBoxTimeOutASPNET";
            this.textBoxTimeOutASPNET.Size = new System.Drawing.Size(339, 20);
            this.textBoxTimeOutASPNET.TabIndex = 16;
            this.textBoxTimeOutASPNET.Text = "300000";
            // 
            // stop_threads
            // 
            this.stop_threads.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.stop_threads.Location = new System.Drawing.Point(303, 222);
            this.stop_threads.Name = "stop_threads";
            this.stop_threads.Size = new System.Drawing.Size(75, 23);
            this.stop_threads.TabIndex = 18;
            this.stop_threads.Text = "Stop all Threads";
            this.stop_threads.UseVisualStyleBackColor = true;
            this.stop_threads.Click += new System.EventHandler(this.stop_threads_Click);
            // 
            // logdisplay
            // 
            this.logdisplay.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.logdisplay.Location = new System.Drawing.Point(384, 222);
            this.logdisplay.Name = "logdisplay";
            this.logdisplay.Size = new System.Drawing.Size(75, 23);
            this.logdisplay.TabIndex = 19;
            this.logdisplay.Text = "Display Log";
            this.logdisplay.UseVisualStyleBackColor = true;
            this.logdisplay.Click += new System.EventHandler(this.logdisplay_Click);
            // 
            // button3
            // 
            this.button3.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.button3.Location = new System.Drawing.Point(465, 222);
            this.button3.Name = "button3";
            this.button3.Size = new System.Drawing.Size(83, 23);
            this.button3.TabIndex = 20;
            this.button3.Text = "Display Faults";
            this.button3.UseVisualStyleBackColor = true;
            this.button3.Click += new System.EventHandler(this.button3_Click);
            // 
            // textBoxTTLRunForTime
            // 
            this.textBoxTTLRunForTime.Location = new System.Drawing.Point(125, 194);
            this.textBoxTTLRunForTime.Name = "textBoxTTLRunForTime";
            this.textBoxTTLRunForTime.Size = new System.Drawing.Size(100, 20);
            this.textBoxTTLRunForTime.TabIndex = 21;
            this.textBoxTTLRunForTime.Text = "300";
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(12, 197);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(58, 13);
            this.label8.TabIndex = 22;
            this.label8.Text = "TTL (secs)";
            // 
            // button4
            // 
            this.button4.Location = new System.Drawing.Point(392, 193);
            this.button4.Name = "button4";
            this.button4.Size = new System.Drawing.Size(75, 23);
            this.button4.TabIndex = 23;
            this.button4.Text = "Reset";
            this.button4.UseVisualStyleBackColor = true;
            this.button4.Click += new System.EventHandler(this.button4_Click);
            // 
            // checkBoxPersistConnection
            // 
            this.checkBoxPersistConnection.AutoSize = true;
            this.checkBoxPersistConnection.Location = new System.Drawing.Point(256, 195);
            this.checkBoxPersistConnection.Margin = new System.Windows.Forms.Padding(2);
            this.checkBoxPersistConnection.Name = "checkBoxPersistConnection";
            this.checkBoxPersistConnection.Size = new System.Drawing.Size(114, 17);
            this.checkBoxPersistConnection.TabIndex = 24;
            this.checkBoxPersistConnection.Text = "Persist Connection";
            this.checkBoxPersistConnection.UseVisualStyleBackColor = true;
            // 
            // textBox4
            // 
            this.textBox4.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBox4.Location = new System.Drawing.Point(6, 318);
            this.textBox4.Margin = new System.Windows.Forms.Padding(2);
            this.textBox4.Multiline = true;
            this.textBox4.Name = "textBox4";
            this.textBox4.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox4.Size = new System.Drawing.Size(626, 143);
            this.textBox4.TabIndex = 25;
            // 
            // button5
            // 
            this.button5.Location = new System.Drawing.Point(480, 193);
            this.button5.Margin = new System.Windows.Forms.Padding(2);
            this.button5.Name = "button5";
            this.button5.Size = new System.Drawing.Size(107, 23);
            this.button5.TabIndex = 26;
            this.button5.Text = "Purge MP Queue";
            this.button5.UseVisualStyleBackColor = true;
            this.button5.Click += new System.EventHandler(this.button5_Click);
            // 
            // textBox5Timer
            // 
            this.textBox5Timer.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBox5Timer.Location = new System.Drawing.Point(480, 143);
            this.textBox5Timer.Margin = new System.Windows.Forms.Padding(2);
            this.textBox5Timer.Name = "textBox5Timer";
            this.textBox5Timer.Size = new System.Drawing.Size(159, 20);
            this.textBox5Timer.TabIndex = 27;
            // 
            // textBoxDelayPerHit
            // 
            this.textBoxDelayPerHit.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.textBoxDelayPerHit.Location = new System.Drawing.Point(540, 171);
            this.textBoxDelayPerHit.Margin = new System.Windows.Forms.Padding(2);
            this.textBoxDelayPerHit.Name = "textBoxDelayPerHit";
            this.textBoxDelayPerHit.Size = new System.Drawing.Size(99, 20);
            this.textBoxDelayPerHit.TabIndex = 28;
            this.textBoxDelayPerHit.Text = "0";
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(478, 173);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(56, 13);
            this.label9.TabIndex = 29;
            this.label9.Text = "Delay (ms)";
            // 
            // button6
            // 
            this.button6.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button6.Location = new System.Drawing.Point(578, 37);
            this.button6.Margin = new System.Windows.Forms.Padding(2);
            this.button6.Name = "button6";
            this.button6.Size = new System.Drawing.Size(56, 19);
            this.button6.TabIndex = 30;
            this.button6.Text = "Time";
            this.button6.UseVisualStyleBackColor = true;
            this.button6.Click += new System.EventHandler(this.button6_Click);
            // 
            // button7
            // 
            this.button7.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.button7.Location = new System.Drawing.Point(578, 63);
            this.button7.Margin = new System.Windows.Forms.Padding(2);
            this.button7.Name = "button7";
            this.button7.Size = new System.Drawing.Size(60, 71);
            this.button7.TabIndex = 31;
            this.button7.Text = "Check FGSMS MP Config";
            this.button7.UseVisualStyleBackColor = true;
            this.button7.Click += new System.EventHandler(this.button7_Click);
            // 
            // comboBox1
            // 
            this.comboBox1.FormattingEnabled = true;
            this.comboBox1.Items.AddRange(new object[] {
            "CallASPNETHelloWorldService2",
            "CallWCFFGSMSDASService",
            "CallWCFHelloWorldService",
            "CallEchoService",
            "CallWCFChain",
            "CallJbossESBHelloWorldProxy",
            "CallJbossESBHelloWorldRealService",
            "CallHelloWorldESMWCF2",
            "CallFGSMSDCS",
            "CallFGSMSDCSAddMoreData",
            "CallFGSMSDCSMtom"});
            this.comboBox1.Location = new System.Drawing.Point(90, 258);
            this.comboBox1.Margin = new System.Windows.Forms.Padding(2);
            this.comboBox1.Name = "comboBox1";
            this.comboBox1.Size = new System.Drawing.Size(535, 21);
            this.comboBox1.TabIndex = 32;
            this.comboBox1.Text = "CallEchoService";
            this.comboBox1.SelectedIndexChanged += new System.EventHandler(this.comboBox1_SelectedIndexChanged);
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Location = new System.Drawing.Point(10, 258);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(70, 13);
            this.label10.TabIndex = 33;
            this.label10.Text = "Service Type";
            // 
            // textBoxFilepath
            // 
            this.textBoxFilepath.Location = new System.Drawing.Point(161, 292);
            this.textBoxFilepath.Name = "textBoxFilepath";
            this.textBoxFilepath.Size = new System.Drawing.Size(339, 20);
            this.textBoxFilepath.TabIndex = 34;
            this.textBoxFilepath.Text = "c:\\perflogs\\perflog.txt";
            // 
            // checkBoxStopOnError
            // 
            this.checkBoxStopOnError.AutoSize = true;
            this.checkBoxStopOnError.Location = new System.Drawing.Point(509, 295);
            this.checkBoxStopOnError.Margin = new System.Windows.Forms.Padding(2);
            this.checkBoxStopOnError.Name = "checkBoxStopOnError";
            this.checkBoxStopOnError.Size = new System.Drawing.Size(85, 17);
            this.checkBoxStopOnError.TabIndex = 36;
            this.checkBoxStopOnError.Text = "stop on error";
            this.checkBoxStopOnError.UseVisualStyleBackColor = true;
            // 
            // button8
            // 
            this.button8.Location = new System.Drawing.Point(5, 290);
            this.button8.Name = "button8";
            this.button8.Size = new System.Drawing.Size(75, 23);
            this.button8.TabIndex = 37;
            this.button8.Text = "Write Log File";
            this.button8.UseVisualStyleBackColor = true;
            this.button8.Click += new System.EventHandler(this.button8_Click);
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Location = new System.Drawing.Point(87, 296);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(69, 13);
            this.label11.TabIndex = 38;
            this.label11.Text = "Log Location";
            // 
            // label12
            // 
            this.label12.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.label12.AutoSize = true;
            this.label12.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label12.Location = new System.Drawing.Point(31, 609);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(407, 13);
            this.label12.TabIndex = 41;
            this.label12.Text = "Do not do performance trails while running with the debugger attached";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(647, 634);
            this.Controls.Add(this.label12);
            this.Controls.Add(this.label11);
            this.Controls.Add(this.button8);
            this.Controls.Add(this.checkBoxStopOnError);
            this.Controls.Add(this.textBoxFilepath);
            this.Controls.Add(this.label10);
            this.Controls.Add(this.comboBox1);
            this.Controls.Add(this.button7);
            this.Controls.Add(this.button6);
            this.Controls.Add(this.label9);
            this.Controls.Add(this.textBoxDelayPerHit);
            this.Controls.Add(this.textBox5Timer);
            this.Controls.Add(this.button5);
            this.Controls.Add(this.textBox4);
            this.Controls.Add(this.checkBoxPersistConnection);
            this.Controls.Add(this.button4);
            this.Controls.Add(this.label8);
            this.Controls.Add(this.textBoxTTLRunForTime);
            this.Controls.Add(this.button3);
            this.Controls.Add(this.logdisplay);
            this.Controls.Add(this.stop_threads);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.textBoxTimeOutASPNET);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.textBoxMultiplier);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.textBoxContext3);
            this.Controls.Add(this.textboxContext2);
            this.Controls.Add(this.textBoxContext1);
            this.Controls.Add(this.textBox2);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.textBoxURL);
            this.Controls.Add(this.textBoxThreadsToRun);
            this.Controls.Add(this.go_button);
            this.Name = "Form1";
            this.Text = "Web Service Test Bed";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button go_button;
        private System.Windows.Forms.TextBox textBoxThreadsToRun;
        private System.Windows.Forms.TextBox textBoxURL;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.TextBox textBox2;
        private System.Windows.Forms.TextBox textBoxContext1;
        private System.Windows.Forms.TextBox textboxContext2;
        private System.Windows.Forms.TextBox textBoxContext3;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.TextBox textBoxMultiplier;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.TextBox textBoxTimeOutASPNET;
        private System.Windows.Forms.Button stop_threads;
        private System.Windows.Forms.Button logdisplay;
        private System.Windows.Forms.Button button3;
        private System.Windows.Forms.TextBox textBoxTTLRunForTime;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Button button4;
        private System.Windows.Forms.CheckBox checkBoxPersistConnection;
        private System.Windows.Forms.TextBox textBox4;
        private System.Windows.Forms.Button button5;
        private System.Windows.Forms.TextBox textBox5Timer;
        private System.Windows.Forms.TextBox textBoxDelayPerHit;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.Button button6;
        private System.Windows.Forms.Button button7;
        private System.Windows.Forms.ComboBox comboBox1;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.TextBox textBoxFilepath;
        private System.Windows.Forms.CheckBox checkBoxStopOnError;
        private System.Windows.Forms.Button button8;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.Label label12;
    }
}

