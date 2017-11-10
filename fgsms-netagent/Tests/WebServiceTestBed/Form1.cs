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
using System.Threading;
using System.Collections;
using System.IO;

using System.ServiceModel;
using System.ServiceModel.Channels;
using System.Net;
using System.Diagnostics;

using System.Management;
using System.Globalization;
using org.miloss.fgsms.agent;
using org.miloss.fgsms.agent.wcf;

namespace WebServiceTestBed
{
    public partial class Form1 : Form
    {
        DateTime start2;

        Thread startcalls;
        ArrayList calls;
        List<string> success_messages;
        List<string> failure_messages;
        ArrayList responsetimes;
        int permit;
        int deny;
        int other;


        public Form1()
        {
            permit = 0;
            deny = 0;
            other = 0;
            responsetimes = new ArrayList();
            success_messages = new List<string>();
            failure_messages = new List<string>();
            calls = new ArrayList();
            InitializeComponent();
        }
        StringWriter sw = new StringWriter();
        DateTime masterstarttime;
        private void go_button_Click(object sender, EventArgs e)
        {

            permit = 0;
            deny = 0;
            other = 0;
            start2 = DateTime.Now;
            if (startcalls != null && startcalls.IsAlive)
                startcalls.Abort();
            startcalls = null;

            success_messages = new List<string>();
            failure_messages = new List<string>();
            failure_messages.Clear();
            System.GC.Collect();
            Thread temp = null;
            while (calls.Count != 0)
            {
                temp = (Thread)calls[calls.Count - 1];
                temp.Abort();
                calls.Remove(temp);
                temp = null;
            }
            calls.Clear();

            CheckForIllegalCrossThreadCalls = false;
            startcalls = new Thread(new ThreadStart(ThreadCallStarter));
            startcalls.Start();

        }

        private void ThreadCallStarter()
        {
            totaltime = 0;
            CheckForIllegalCrossThreadCalls = false;
            Thread newthread = null;
            while (calls.Count < Int32.Parse(textBoxThreadsToRun.Text))
            {

                if (comboBox1.Text == "CallfgsmsDCSMtom")
                {
                    newthread = new Thread(new ThreadStart(CallfgsmsDCSMtom));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallfgsmsDCSAddMoreData")
                {
                    newthread = new Thread(new ThreadStart(CallfgsmsDCSAddMoreData));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallfgsmsDCS")
                {
                    newthread = new Thread(new ThreadStart(CallfgsmsDCS));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallWCFHelloWorldService")
                {
                    newthread = new Thread(new ThreadStart(CallWCFHelloWorldService));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallASPNETHelloWorldService2")
                {
                    newthread = new Thread(new ThreadStart(CallASPNETHelloWorldService2));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallWCFFGSMSDASService")
                {
                    newthread = new Thread(new ThreadStart(CallWCFfgsmsDASService));
                    newthread.Start();
                    calls.Add(newthread);
                }
               
                if (comboBox1.Text == "CallEchoService")
                {
                    newthread = new Thread(new ThreadStart(CallEchoService));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallWCFChain")
                {
                    newthread = new Thread(new ThreadStart(CallWCFChain));
                    newthread.Start();
                    calls.Add(newthread);
                }
               /* if (comboBox1.Text == "CallJaxwsHelloWorldNo1")
                {
                    newthread = new Thread(new ThreadStart(CallJaxwsHelloWorldNo1));
                    newthread.Start();
                    calls.Add(newthread);
                }*/
                if (comboBox1.Text == "CallJbossESBHelloWorldProxy")
                {
                    newthread = new Thread(new ThreadStart(CallJbossESBHelloWorldProxy));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallJbossESBHelloWorldRealService")
                {
                    newthread = new Thread(new ThreadStart(CallJbossESBHelloWorldRealService));
                    newthread.Start();
                    calls.Add(newthread);
                }
                if (comboBox1.Text == "CallHelloWorldESMWCF2")
                {
                    newthread = new Thread(new ThreadStart(CallHelloWorldESMWCF2));
                    newthread.Start();
                    calls.Add(newthread);
                }
            }
        }


        private void CallHelloWorldESMWCF2()
        {


            /*FileStream f = null;
            if (checkBox2.Checked)
            {
                try
                {
                    f = File.OpenWrite(textBoxFilepath.Text);
                }
                catch { }
            }*/
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            HelloWorldESMWCF2.Service1Client me = null;

            if (checkBoxPersistConnection.Checked)
            {

                me = new HelloWorldESMWCF2.Service1Client("WSHttpBinding_IService1", textBoxURL.Text);
            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                if (!checkBoxPersistConnection.Checked)
                    me = new HelloWorldESMWCF2.Service1Client("WSHttpBinding_IService1", textBoxURL.Text);
                try
                {
                    HelloWorldESMWCF2.CompositeType req = new HelloWorldESMWCF2.CompositeType();
                    req.StringValue = "hi " + DateTime.Now.ToString("o");
                    req.BoolValue = true;

                    start = DateTime.Now;
                    me.GetDataUsingDataContract(req);
                    //insert logic to determine if response was valid
                    ProcessSuccess(start);

                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;

                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
            /*  if (checkBox2.Checked)
              {
                  f.Close();
              }*/
        }

        private void ProcessFailure(DateTime start, Exception ex)
        {
            DateTime endtime = new DateTime(DateTime.Now.Ticks - start.Ticks);
            TimeSpan ts = new TimeSpan(DateTime.Now.Ticks - start.Ticks);
            totaltime += endtime.Ticks;

            other++;
            string error = "";
            while (ex != null)
            {
                error += ex.Message + Environment.NewLine + ex.StackTrace + Environment.NewLine;
                ex = ex.InnerException;
            }
            try
            {
                failure_messages.Add((DateTime.Now.Ticks - start2.Ticks) + ";false;" + ts.TotalMilliseconds + ";" + MessageProcessor.GetQueueSize() + ";" + MessageProcessor.GetPolicyCacheSize() + ";" + error + Environment.NewLine);
                //failure_messages.Add(error);
            }
            catch { }
            // endtime = new DateTime(DateTime.Now.Ticks - start.Ticks);

            /*
            if (checkBox2.Checked && f != null)
            {

                string s = ts.TotalMilliseconds + ";" + error;
                f.Write(Encoding.UTF8.GetBytes(s), 0, Encoding.UTF8.GetBytes(s).Length);
            }*/
        }

        private void ProcessSuccess(DateTime start)
        {
            permit++;
            DateTime endtime = new DateTime(DateTime.Now.Ticks - start.Ticks);
            TimeSpan ts = new TimeSpan(DateTime.Now.Ticks - start.Ticks);
            totaltime += endtime.Ticks;
            success_messages.Add((DateTime.Now.Ticks - start2.Ticks) + ";true;" + ts.TotalMilliseconds + ";" + MessageProcessor.GetQueueSize() + ";" + MessageProcessor.GetPolicyCacheSize() +
                ";" + (this.totaltime / TimeSpan.TicksPerMillisecond / (permit + deny + other)) + sw.NewLine);



            textBox4.Text = "Queue Size:" + MessageProcessor.GetQueueSize() + " Policy Cache:" + MessageProcessor.GetPolicyCacheSize() + " Runnning: " +
                MessageProcessor.IsThreadAlive() +
                " MP Threads " + MessageProcessor.ThreadPoolSize() + Environment.NewLine;
            /* if (checkBox2.Checked && f != null)
             {
                 string s = ts.TotalMilliseconds + Environment.NewLine;
                 f.Write(Encoding.UTF8.GetBytes(s), 0, Encoding.UTF8.GetBytes(s).Length);
             }*/
        }



        private void CallJbossESBHelloWorldRealService()
        {

            /* FileStream f = null;
             if (checkBox2.Checked)
             {
                 try
                 {
                     f = File.OpenWrite(textBoxFilepath.Text);
                 }
                 catch { }
             }*/
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            JbossESBProxyBasicHelloWorld.HelloWorldClient me = null;
            if (checkBoxPersistConnection.Checked)
            {
                me = new JbossESBProxyBasicHelloWorld.HelloWorldClient("HelloWorldPortReal", textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                    me = new JbossESBProxyBasicHelloWorld.HelloWorldClient("HelloWorldPortReal", textBoxURL.Text);
                //me.Url = jerichoUrlBox.Text;

                try
                {
                    //me.Timeout = Int32.Parse(timeoutTB.Text);
                    start = DateTime.Now;


                    JbossESBProxyBasicHelloWorld.sayHello req = new JbossESBProxyBasicHelloWorld.sayHello();
                    req.toWhom = System.Environment.UserName;
                    me.sayHello(req);
                    ProcessSuccess(start);
                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;

                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
            //if (checkBox2.Checked)
            //{
            //    f.Close();
            //}
        }







        private void CallJbossESBHelloWorldProxy()
        {

            //FileStream f = null;
            //if (checkBox2.Checked)
            //{
            //    try
            //    {
            //        f = File.OpenWrite(textBoxFilepath.Text);
            //    }
            //    catch { }
            //}
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            JbossESBProxyBasicHelloWorld.HelloWorldClient me = null;
            if (checkBoxPersistConnection.Checked)
            {
                me = new JbossESBProxyBasicHelloWorld.HelloWorldClient("HelloWorldPortProxy", textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                    me = new JbossESBProxyBasicHelloWorld.HelloWorldClient("HelloWorldPortProxy", textBoxURL.Text);
                //me.Url = jerichoUrlBox.Text;

                try
                {
                    //me.Timeout = Int32.Parse(timeoutTB.Text);
                    start = DateTime.Now;


                    JbossESBProxyBasicHelloWorld.sayHello req = new JbossESBProxyBasicHelloWorld.sayHello();
                    req.toWhom = System.Environment.UserName;
                    me.sayHello(req);
                    //permit++;
                    //endtime = new DateTime(DateTime.Now.Ticks - start.Ticks);
                    //TimeSpan ts = new TimeSpan(DateTime.Now.Ticks - start.Ticks);
                    //totaltime += endtime.Ticks;
                    ////success_messages.Add((i * 500) + "bytes took " + ts.TotalMilliseconds + "ms");
                    //textBox4.Text = "Queue Size:" + MessageProcessor.GetQueueSize() + " Policy Cache:" + MessageProcessor.GetPolicyCacheSize() + " Runnning: " +
                    //    MessageProcessor.IsThreadAlive() +
                    //    " MP Threads " + MessageProcessor.ThreadPoolSize() + Environment.NewLine;
                    //if (checkBox2.Checked && f != null)
                    //{
                    //    string s = i * 500 + ";" + ts.TotalMilliseconds + Environment.NewLine;
                    //    f.Write(Encoding.UTF8.GetBytes(s), 0, Encoding.UTF8.GetBytes(s).Length);
                    //}
                    ProcessSuccess(start);
                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;


                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
            //if (checkBox2.Checked)
            //{
            //    f.Close();
            //}
        }


















        private long totaltime = 0;

        /*
        private void CallJaxwsHelloWorldNo1()
        {

            //FileStream f = null;
            //if (checkBox2.Checked)
            //{
            //    try
            //    {
            //        f = File.OpenWrite(textBoxFilepath.Text);
            //    }
            //    catch { }
            //}
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            jaxwsHelloWorldNo1.HelloWorldPortClient me = null;
            if (checkBoxPersistConnection.Checked)
            {
                me = new jaxwsHelloWorldNo1.HelloWorldPortClient();//"EchoBasicHttpBinding_IService11", textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                    me = new jaxwsHelloWorldNo1.HelloWorldPortClient();//"dataCollectorServiceBinding_IService1", textBoxURL.Text);
                //me.Url = jerichoUrlBox.Text;

                try
                {
                    //me.Timeout = Int32.Parse(timeoutTB.Text);
                    start = DateTime.Now;


                    jaxwsHelloWorldNo1.SayHelloWorld req = new jaxwsHelloWorldNo1.SayHelloWorld();
                    req.HelloWorld = new jaxwsHelloWorldNo1.HelloWorld();
                    req.HelloWorld.req = new jaxwsHelloWorldNo1.HelloWorldData();
                    req.HelloWorld.req.sayhellodata = "bob";
                    me.SayHelloWorld(req);
                    ProcessSuccess(start);
                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;
                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
            //if (checkBox2.Checked)
            //{
            //    f.Close();
            //}
        }*/

        private void CallWCFChain()
        {

            //FileStream f = null;
            //if (checkBox2.Checked)
            //{
            //    try
            //    {
            //        f = File.OpenWrite(textBoxFilepath.Text);
            //    }
            //    catch { }
            //}
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            WcfChain.WcfChainClient me = null;
            if (checkBoxPersistConnection.Checked)
            {
                me = new WcfChain.WcfChainClient();//"EchoBasicHttpBinding_IService11", textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                    me = new WcfChain.WcfChainClient();//"dataCollectorServiceBinding_IService1", textBoxURL.Text);
                //me.Url = jerichoUrlBox.Text;

                try
                {
                    //me.Timeout = Int32.Parse(timeoutTB.Text);
                    start = DateTime.Now;
                    WcfChain.CompositeType req = new WcfChain.CompositeType();
                    // Echo1Client.CrazyType req = new Echo1Client.CrazyType();
                    req.BoolValue = false;
                    req.StringValue = "hello world";


                    me.GetDataUsingDataContract(req);
                    ProcessSuccess(start);
                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;
                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
            //if (checkBox2.Checked)
            //{
            //    f.Close();
            //}
        }
        private void CallEchoService()
        {
            //FileStream f = null;
            //if (checkBox2.Checked)
            //{
            //    try
            //    {
            //        f = File.OpenWrite(textBoxFilepath.Text);
            //    }
            //    catch { }
            //}
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            Echo1Client.Service1Client me = null;
            if (checkBoxPersistConnection.Checked)
            {
                me = new Echo1Client.Service1Client("dataCollectorServiceBinding_IService1", textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                    me = new Echo1Client.Service1Client("dataCollectorServiceBinding_IService1", textBoxURL.Text);


                try
                {
                    //me.Timeout = Int32.Parse(timeoutTB.Text);
                    start = DateTime.Now;
                    Echo1Client.CrazyType req = new Echo1Client.CrazyType();
                    req.Data = new string[i];
                    for (int k = 0; k < i; k++)
                    {
                        //500 bytes
                        req.Data[k] = "####################################################################################################################################################################################################################################################################################################################################################################################################################################################################################################################";
                    }
                    me.Echo(req);
                    ProcessSuccess(start);
                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;
                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
            //if (checkBox2.Checked)
            //{
            //    f.Close();
            //}
        }

        private void CallWCFHelloWorldService()
        {
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            WCFhelloworld.Service1Client me = null;
            if (checkBoxPersistConnection.Checked)
            {
                me = new WCFhelloworld.Service1Client("BasicHttpBinding_IService1", textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                    me = new WCFhelloworld.Service1Client("BasicHttpBinding_IService1", textBoxURL.Text);
                //me.Url = jerichoUrlBox.Text;

                try
                {
                    //me.Timeout = Int32.Parse(timeoutTB.Text);
                    start = DateTime.Now;
                    me.WorkingGetData(5);
                    ProcessSuccess(start);

                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;
                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
        }
        private void CallASPNETHelloWorldService2()
        {
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }
            sillyservice.Service1 me = null;
            // dataAccessServiceClient me = null;

            if (checkBoxPersistConnection.Checked)
            {
                me = new sillyservice.Service1();
                me.Url = textBoxURL.Text;
                //new dataAccessServiceClient("DASPort", jerichoUrlBox.Text);
            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;

                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                    //me = new dataAccessServiceClient("DASPort", jerichoUrlBox.Text);
                    me = new sillyservice.Service1();
                me.Url = textBoxURL.Text;

                try
                {
                    me.Timeout = Int32.Parse(textBoxTimeOutASPNET.Text);
                    start = DateTime.Now;
                    me.HelloWorld();
                    ProcessSuccess(start);
                }
                catch (Exception ex)
                {
                    other++;
                    endtime = new DateTime(DateTime.Now.Ticks - start.Ticks);


                }
                if (!checkBoxPersistConnection.Checked)
                {
                    me.Dispose();
                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                me.Dispose();
                me = null;
            }
        }
        private void CallWCFfgsmsDASService()
        {
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }
            //sillyservice.Service1 me = null;
            dataAccessService me = null;

            if (checkBoxPersistConnection.Checked)
            {
                //me = new sillyservice.Service1();
                // me.Url = jerichoUrlBox.Text;
                //me = new dataAccessServiceClient("DASPort", textBoxURL.Text);
                // me.ClientCredentials.UserName.Password = "password";
                // me.ClientCredentials.UserName.UserName = "fgsmsagent";
                me = GetDASProxyl(textBoxURL.Text, "fgsmsagent", "password");
            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                DateTime endtime;
                if (!checkBoxPersistConnection.Checked)
                {
                    //  me = new dataAccessServiceClient("DASPort", textBoxURL.Text);
                    //  me.ClientCredentials.UserName.Password = "password";
                    // me.ClientCredentials.UserName.UserName = "fgsmsagent";
                    me = GetDASProxyl(textBoxURL.Text, "fgsmsagent", "password");
                }


                GetMonitoredServiceListRequestMsg req = new GetMonitoredServiceListRequestMsg();
                req = new GetMonitoredServiceListRequestMsg();
                req.classification = new SecurityWrapper();
                req.classification.caveats = "none";
                req.classification.classification = ClassificationType.U;
                try
                {
                    //me.Timeout = Int32.Parse(timeoutTB.Text);
                    start = DateTime.Now;
                    //me.HelloWorld();
                    GetMonitoredServiceListResponseMsg res = me.GetMonitoredServiceList(req);
                    
                    ProcessSuccess(start);
                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;
                }
                if (!checkBoxPersistConnection.Checked)
                {
                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                //me.Dispose();
                me = null;
            }
        }




        private void button1_Click(object sender, EventArgs e)
        {//stop button
            SaveFileDialog openFileDialog1 = new SaveFileDialog();
            openFileDialog1.InitialDirectory = @"c:\";
            openFileDialog1.Title = "Save File";
            openFileDialog1.ValidateNames = true;

            openFileDialog1.Filter = "Text File |*.txt";
            openFileDialog1.CheckPathExists = true;
            openFileDialog1.RestoreDirectory = true;

            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                if (openFileDialog1.FileName != null)
                {
                    string a = "Success:" + success_messages.Count + " Failures:" + failure_messages.Count + sw.NewLine;
                    //for (int i = 0; i < sucess_messages.Count; i++)
                    //    a += sw.NewLine + sucess_messages[i];
                    using (System.IO.FileStream fs = System.IO.File.Create(openFileDialog1.FileName, 1024))
                    {
                        byte[] info2 = new System.Text.UTF8Encoding(true).GetBytes(a);
                        fs.Write(info2, 0, info2.Length);
                        for (int i = 0; i < success_messages.Count; i++)
                        {
                            // Add some information to the file.
                            byte[] info = new System.Text.UTF8Encoding(true).GetBytes((string)success_messages[i] + sw.NewLine);
                            fs.Write(info, 0, info.Length);
                        }

                        for (int i = 0; i < failure_messages.Count; i++)
                        {
                            // Add some information to the file.
                            byte[] info = new System.Text.UTF8Encoding(true).GetBytes((string)failure_messages[i] + sw.NewLine);
                            fs.Write(info, 0, info.Length);
                        }
                    }

                    //File.WriteAllText(openFileDialog1.FileName, a, Encoding.ASCII);
                }
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {//whats the current count?
            MessageProcessor m = MessageProcessor.Instance;
            textBox2.Text = "Successful invocations:" + success_messages.Count + sw.NewLine + "Failed invocations:" + failure_messages.Count + sw.NewLine;
            textBox2.Text += "Permit:" + permit + sw.NewLine + "Deny:" + deny + sw.NewLine + "Indeterminate:" + other + sw.NewLine;
            textBox2.Text += "MP Queue:" + MessageProcessor.GetQueueSize() + sw.NewLine;
            textBox2.Text += "MP Cache:" + MessageProcessor.GetPolicyCacheSize() + sw.NewLine;
            textBox2.Text += "MP Threads:" + MessageProcessor.ThreadPoolSize() + sw.NewLine;
            try
            {
                textBox2.Text += "Time/Invocation:" + this.totaltime / TimeSpan.TicksPerMillisecond / (permit + deny + other) + "ms" + sw.NewLine;
            }
            catch { }
            int totaltime = 0;
            int count = responsetimes.Count;
            for (int i = 0; i < responsetimes.Count; i++)
            {
                try
                {
                    totaltime += (int)responsetimes[i];
                }
                catch (Exception ex)
                {
                    count--;
                }
            }

            if (count != 0) textBox2.Text += "Average response time: " + (totaltime / count) + " ms" + sw.NewLine;
            TimeSpan remaining = new TimeSpan();

            if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
            {
                remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
            }
        }

        private void stop_threads_Click(object sender, EventArgs e)
        {
            textBox2.Text += "There are currently " + calls.Count + " threads running" + sw.NewLine;
            Thread temp = null;
            while (calls.Count != 0)
            {
                temp = (Thread)calls[calls.Count - 1];
                temp.Abort();
                calls.Remove(temp);
                temp = null;
            }
            textBox2.Text += "Sent abort sucess_messages to all threads";
        }

        private void logdisplay_Click(object sender, EventArgs e)
        {
            textBox2.Text = "";
            int count = 0;
            for (int i = 0; i < success_messages.Count; i++)
            {
                count++;
                textBox2.Text += success_messages[i] + sw.NewLine;
                if (count > 50) break;
            }
            count = 0;
            for (int i = 0; i < failure_messages.Count; i++)
            {
                count++;
                textBox2.Text += failure_messages[i] + sw.NewLine;
                if (count > 50) break;
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            textBox2.Text = "";
            int count = 0;
            if (failure_messages != null && failure_messages.Count > 0)
                for (int i = 0; i < failure_messages.Count; i++)
                {

                    textBox2.Text += failure_messages[i] + sw.NewLine;
                    count++;
                    if (count > 50) break;
                }
        }

        private void button4_Click(object sender, EventArgs e)
        {
            try
            {
                permit = 0;
                deny = 0;
                other = 0;
                responsetimes.Clear();
                success_messages.Clear();
                failure_messages.Clear();
                calls.Clear();
            }
            catch { }
        }

        private void button5_Click(object sender, EventArgs e)
        {
            MessageProcessor.Abort();
            MessageProcessor.PurgePolicyCache();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            textBoxThreadsToRun.Text = Environment.ProcessorCount.ToString();
            this.FormClosing += new FormClosingEventHandler(Form1_FormClosing);
        }

        void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            // this.start2 = DateTime.MinValue;
            stop_threads_Click(sender, e);
        }

        private void button6_Click(object sender, EventArgs e)
        {
            DateTime now = DateTime.Now;
            DateTime utc = DateTime.UtcNow;
            long nowl = now.Ticks;
            textBox2.Text = "UTC now = " + utc.Ticks + " Datetime Now = " + now.Ticks + Environment.NewLine
            + "Yesterday UTC now = " + utc.Subtract(new TimeSpan(24, 0, 0)).Ticks + " Datetime Now = " + now.Subtract(new TimeSpan(24, 0, 0)).Ticks + Environment.NewLine;
            textBox2.Text += Int32.MaxValue;
        }

        private void button7_Click(object sender, EventArgs e)
        {
            try
            {
                MessageProcessor mp = MessageProcessor.Instance;



                textBox4.Text = "Enabled: = " + MessageProcessor.IsEnabled + Environment.NewLine;
                textBox4.Text += "ErrorState: = " + MessageProcessor.ErrorState + Environment.NewLine;
                textBox4.Text += "Last error message: = " + MessageProcessor.LastErrorMessage + Environment.NewLine;
                textBox4.Text += "Thread Map size: = " + MessageProcessor.threadmap.Keys.Count + Environment.NewLine;
                textBox4.Text += "Dead message duration: = " + MessageProcessor.GetConfig.DeadMessageDuration + Environment.NewLine;
                textBox4.Text += "Auth Mode: = " + MessageProcessor.GetConfig.AuthenticationMode + Environment.NewLine;
                textBox4.Text += "DCS algo: = " + MessageProcessor.GetConfig.DCSalgo + Environment.NewLine;
                textBox4.Text += "DCS retry: = " + MessageProcessor.GetConfig.DCSretrycount + Environment.NewLine;
                textBox4.Text += "DCS URLs: = " + MessageProcessor.GetConfig.DCSurls.Count + Environment.NewLine;
                for (int i = 0; i < MessageProcessor.GetConfig.DCSurls.Count; i++)
                    textBox4.Text += "DCS URL: = " + MessageProcessor.GetConfig.DCSurls[i] + Environment.NewLine;
                textBox4.Text += "Ignore list size: = " + MessageProcessor.GetConfig.IgnoreList.Count + Environment.NewLine;
                textBox4.Text += "PCS algo: = " + MessageProcessor.GetConfig.PCSalgo + Environment.NewLine;
                textBox4.Text += "PCS retry: = " + MessageProcessor.GetConfig.PCSretrycount + Environment.NewLine;
                textBox4.Text += "PCS URLs: = " + MessageProcessor.GetConfig.PCSurls.Count + Environment.NewLine;
                for (int i = 0; i < MessageProcessor.GetConfig.PCSurls.Count; i++)
                    textBox4.Text += "PCS URL: = " + MessageProcessor.GetConfig.PCSurls[i] + Environment.NewLine;
                textBox4.Text += "ServiceUnavailableBehavior: = " + MessageProcessor.GetConfig.ServiceUnavailableBehavior + Environment.NewLine;
                textBox4.Text += "Uddi enabled: = " + MessageProcessor.GetConfig.UddiEnabled + Environment.NewLine;
                textBox4.Text += "Config loaded from: = " + MessageProcessor.GetConfig.ConfigPath + Environment.NewLine;

                textBox4.Text += "Queue size: = " + MessageProcessor.GetQueueSize()+ Environment.NewLine;
                textBox4.Text += "Policy Cache size: = " + MessageProcessor.GetPolicyCacheSize()+ Environment.NewLine;

                ConfigLoader l = new ConfigLoader();
                UDDIDiscovery uddi = new UDDIDiscovery(l);

                //  UddiPublisher pub = new UddiPublisher("http://localhost:8080/juddiv3/services/publish", "http://localhost:8080/juddiv3/services/security", l.UddiUsername, l.UddiEncryptedPassword, true);
                // pub.PublishBusiness("MyBiz");
                //pub.PublishService("fgsms.PCS", "uddi:juddi.apache.org:819d1f1b-4117-407d-a5ea-1ed1aaedca19");
                //  pub.PublishBinding("http://localhost:8180/fgsmsServices/PCS", "uddi:juddi.apache.org:f0ba338f-af8f-4bec-b0f4-c114fae5184a");
                List<string> urls = uddi.GetPCSURLs();

                //{"org.apache.juddi.model.BindingTemplate cannot be cast to org.apache.juddi.model.BusinessService"}
            }
            catch (Exception ex)
            {
                textBox4.Text += ex.Message;
            }


        }




        private dataAccessService GetDASProxyl(string url, string username, string password)
        {


            ServicePointManager.Expect100Continue = false;
            dataAccessService r = new dataAccessService();
            r.Credentials = new NetworkCredential(username, password);
            r.Url = url;
            return r; 

        }






        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (comboBox1.Text == "CallfgsmsDCS" || comboBox1.Text == "CallfgsmsDCSAddMoreData" || comboBox1.Text == "CallfgsmsDCSMtom")
            {
                textBoxURL.Text = "http://localhost:8888/fgsmsServices/DCS";
            }
            if (comboBox1.Text == "CallWCFHelloWorldService")
            {
                textBoxURL.Text = "http://localhost:50206/Service1.svc";
            }
            if (comboBox1.Text == "CallASPNETHelloWorldService2")
            {
                textBoxURL.Text = "http://localhost:49688/HelloWorldASPNET2.asmx";
            }
            if (comboBox1.Text == "CallWCFFGSMSDASService")
            {
                textBoxURL.Text = "http://localhost:8888/fgsmsServices/services/DAS";
            }
            
            if (comboBox1.Text == "CallEchoService")
            {
                textBoxURL.Text = "http://localhost:44593/Echo1.svc";
            }



            if (comboBox1.Text == "CallJbossESBHelloWorldProxy")
            {
                textBoxURL.Text = "http://localhost:8080/Quickstart_webservice_proxy_basic/http/Proxy_Basic/Proxy";
            }
            if (comboBox1.Text == "CallJbossESBHelloWorldRealService")
            {
                textBoxURL.Text = "http://127.0.0.1:8080/Quickstart_webservice_proxy_basic_ws/HelloWorldWS";
            }
            if (comboBox1.Text == "CallHelloWorldESMWCF2")
            {
                textBoxURL.Text = "http://localhost:36247/HelloWorldESMWCF2.svc";
            }


        }

        private void button8_Click(object sender, EventArgs e)
        {
            FileStream f = File.OpenWrite(textBoxFilepath.Text);
            for (int i = 0; i < success_messages.Count; i++)
            {
                try
                {
                    byte[] b = Encoding.UTF8.GetBytes((string)success_messages[i]);
                    f.Write(b, 0, b.Length);
                }
                catch { }
            }
            for (int i = 0; i < failure_messages.Count; i++)
            {
                try
                {
                    byte[] b = Encoding.UTF8.GetBytes((string)failure_messages[i]);
                    f.Write(b, 0, b.Length);
                }
                catch { }
            }
            f.Close();

        }
        public void CallfgsmsDCSAddMoreData()
        {
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            ConfigLoader c = new ConfigLoader();
            DCSBinding me = null;

            if (checkBoxPersistConnection.Checked)
            {
                me = c.GetDCSProxy(textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                if (!checkBoxPersistConnection.Checked)
                    me = c.GetDCSProxy(textBoxURL.Text);
                try
                {
                    AddDataRequestMsg[] req1 = null;// new AddMoreDataRequest();
                    req1 = new AddDataRequestMsg[40];
                    for (int a = 0; a < 40; a++)
                    {
                        AddDataRequestMsg req = new AddDataRequestMsg();
                        req = new AddDataRequestMsg();
                        req.Action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetUDDIDataPublishServicePolicies";
                        req.agentType = typeof(AgentWCFClientMessageInspector).FullName;
                        req.classification = new SecurityWrapper();
                        req.classification.classification = ClassificationType.U;
                        req.classification.caveats = "none";
                        req.Message = "Hello World from the WS Test Bed";
                        req.recordedat = DateTime.Now;
                        req.requestSize = 1234;
                        req.responseSize = 1234;
                        req.responseTime = 50;
                        req.RequestURI = "http://localhost:8180/fgsmsServices/PCS";
                        req.URI = "http://" + System.Environment.MachineName.ToLower() + ":8180/fgsmsServices/PCS";
                        req.TransactionID = Guid.NewGuid().ToString();
                        req.Success = true;
                        req.ServiceHost = System.Environment.MachineName.ToLower();
                        req.headersRequest = new header[1];
                        req.headersRequest[0] = new header();
                        req.headersRequest[0].name = "SOAPAction";
                        req.headersRequest[0].value = new string[1];
                        req.headersRequest[0].value[0] = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetUDDIDataPublishServicePolicies";
                        req1[a] = req;
                    }
                    start = DateTime.Now;
                    AddDataResponseMsg res = me.AddMoreData(req1);
                    if (res != null && res != null && res.Status != null && res.Status == DataResponseStatus.Success)
                        //insert logic to determine if response was valid

                        ProcessSuccess(start);
                    else
                        ProcessFailure(start, new Exception("Remote server said it failed for some reason"));

                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;

                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
        }



        public void CallfgsmsDCS()
        {
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }

            ConfigLoader c = new ConfigLoader();
            DCSBinding me = null;

            if (checkBoxPersistConnection.Checked)
            {
                me = c.GetDCSProxy(textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                if (!checkBoxPersistConnection.Checked)
                    me = c.GetDCSProxy(textBoxURL.Text);
                try
                {
                    AddDataRequestMsg req = new AddDataRequestMsg();
                    req = new AddDataRequestMsg();
                    req.Action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetUDDIDataPublishServicePolicies";
                    req.agentType = typeof(AgentWCFClientMessageInspector).FullName;
                    //req.classification = new SecurityWrapper(ClassificationType.U, "None");
                    req.classification = new SecurityWrapper();
                    req.classification.classification =  ClassificationType.U;
                    req.classification.caveats = "";
                    req.Message = "Hello World from the WS Test Bed";
                    req.recordedat = DateTime.Now;
                    req.requestSize = 1234;
                    req.responseSize = 1234;
                    req.responseTime = 50;

                    req.RequestURI = "http://localhost:8180/fgsmsServices/PCS";
                    req.URI = "http://" + System.Environment.MachineName.ToLower() + ":8180/fgsmsServices/PCS";
                    req.TransactionID = Guid.NewGuid().ToString();
                    req.Success = true;
                    req.ServiceHost = System.Environment.MachineName.ToLower();
                    req.headersRequest = new header[1];
                    req.headersRequest[0] = new header();
                    req.headersRequest[0].name = "SOAPAction";
                    req.headersRequest[0].value = new string[1];
                    req.headersRequest[0].value[0] = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetUDDIDataPublishServicePolicies";
                    start = DateTime.Now;
                    AddDataResponseMsg res = me.AddData(req);
                    if (res != null && res != null && res.Status != null && res.Status == DataResponseStatus.Success)
                        //insert logic to determine if response was valid

                        ProcessSuccess(start);
                    else
                        ProcessFailure(start, new Exception("Remote server said it failed for some reason"));

                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;

                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
        }




        public void CallfgsmsDCSMtom()
        {
            int delay = 0;
            try
            {
                delay = Int32.Parse(textBoxDelayPerHit.Text);
            }
            catch { }


            DCSBinding me = null;

            if (checkBoxPersistConnection.Checked)
            {
                me = GetDCSMTOMProxy(textBoxURL.Text);

            }

            MessageProcessor m = MessageProcessor.Instance;
            for (int i = 0; i < Int32.Parse(textBoxMultiplier.Text); i++)
            {
                Thread.Sleep(delay);
                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) < DateTime.Now)
                    break;
                TimeSpan remaining = new TimeSpan();

                if (start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) >= DateTime.Now)
                {
                    remaining = start2.AddSeconds(double.Parse(textBoxTTLRunForTime.Text)) - DateTime.Now;

                    textBox5Timer.Text = "Time remaining: " + +remaining.Minutes + " minutes " + remaining.Seconds + " second" + sw.NewLine;
                }
                DateTime start = DateTime.Now;
                if (!checkBoxPersistConnection.Checked)
                    me = GetDCSMTOMProxy(textBoxURL.Text);
                try
                {
                    AddDataRequestMsg req = new AddDataRequestMsg();
                    req = new AddDataRequestMsg();
                    req.Action = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetUDDIDataPublishServicePolicies";
                    req.agentType = typeof(AgentWCFClientMessageInspector).FullName;
                    req.classification = new SecurityWrapper();
                    req.classification.classification = ClassificationType.U;
                    req.classification.caveats = "none";
                    req.Message = "Hello World from the WS Test Bed";
                    req.recordedat = DateTime.Now;
                    req.requestSize = 1234;
                    req.responseSize = 1234;
                    req.responseTime = 50;

                    req.RequestURI = "http://localhost:8180/fgsmsServices/PCS";
                    req.URI = "http://" + System.Environment.MachineName.ToLower() + ":8180/fgsmsServices/PCS";
                    req.TransactionID = Guid.NewGuid().ToString();
                    req.Success = true;
                    req.ServiceHost = System.Environment.MachineName.ToLower();
                    req.headersRequest = new header[1];
                    req.headersRequest[0] = new header();
                    req.headersRequest[0].name = "SOAPAction";
                    req.headersRequest[0].value = new string[1];
                    req.headersRequest[0].value[0] = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetUDDIDataPublishServicePolicies";
                    start = DateTime.Now;
                    AddDataResponseMsg res = me.AddData(req);
                    if (res != null && res != null && res.Status != null && res.Status == DataResponseStatus.Success)
                        //insert logic to determine if response was valid

                        ProcessSuccess(start);
                    else
                        ProcessFailure(start, new Exception("Remote server said it failed for some reason"));

                }
                catch (Exception ex)
                {
                    ProcessFailure(start, ex);
                    if (checkBoxStopOnError.Checked)
                        break;

                }
                if (!checkBoxPersistConnection.Checked)
                {

                    me = null;
                }
            }
            if (checkBoxPersistConnection.Checked)
            {
                // me.Dispose();
                me = null;
            }
        }






        public DCSBinding GetDCSMTOMProxy(string url)
        {
            ServicePointManager.Expect100Continue = false;
            DCSBinding r = new DCSBinding();
            r.Url = url;
            return r;
           
        }


    
        private PerfCon GetData(string machine, string servicename)
        {
            string query = string.Format(
                "SELECT ProcessId FROM Win32_Service WHERE Name='{0}'",
                servicename);
            ManagementObjectSearcher searcher =
                new ManagementObjectSearcher(query);
            foreach (ManagementObject obj in searcher.Get())
            {
                uint processId = (uint)obj["ProcessId"];
                Process process = null;
                try
                {
                    process = Process.GetProcessById((int)processId);
                    PerfCon data = new PerfCon();
                    data.memory = process.PrivateMemorySize64;
                    return data;
                }
                catch (ArgumentException)
                {
                    // Thrown if the process specified by processId
                    // is no longer running.
                }

            }
            return null;
        }
        private static int GetProcessorIdleTime(string selectedServer)
        {
            try
            {
                ManagementObjectSearcher searcher = new ManagementObjectSearcher
                     (@"\\" + selectedServer + @"\root\CIMV2",
                      "SELECT * FROM Win32_PerfFormattedData_PerfOS_Processor WHERE Name=\"_Total\"");

                ManagementObjectCollection collection = searcher.Get();
                ManagementObject queryObj = (ManagementObject)collection.GetEnumerator().Current;

                return Convert.ToInt32(queryObj["PercentIdleTime"]);
            }
            catch (ManagementException e)
            {
                MessageBox.Show("An error occurred while querying for WMI data: " + e.Message);
            }
            return -1;
        }


        private data GetProcessInfo(Process p1, string machinename)
        {
            data d1 = null;
           // Process p1 = Process.GetProcessById((int)processId, machinename);
            d1 = new data();
            d1.name = p1.ProcessName;
            //                d.mem = p1.PrivateMemorySize64;
            d1.threads = p1.Threads.Count;
            d1.memInUse = p1.PrivateMemorySize64;
            try
            {
                d1.startedat = p1.StartTime;
            }
            catch { d1.startedat = DateTime.MinValue; }

            PerformanceCounter counter = new PerformanceCounter("Process", "% Processor Time", p1.ProcessName, machinename);
            float f = counter.NextValue();
            f = counter.NextValue();
            d1.percentCPU = f;
            /*
            ManagementObjectSearcher s2 = new ManagementObjectSearcher(@"\\" + machinename + @"\root\cimv2",
           "   select TotalPhysicalMemory from Win32_ComputerSystem");
            ManagementObjectCollection moc = s2.Get();
            ulong installedMemory = 0;
            foreach (ManagementObject o in moc)
            {
                d1.installedmem = installedMemory = Convert.ToUInt64(o["TotalPhysicalMemory"], CultureInfo.InvariantCulture);
            }

            moc.Dispose();
            s2.Dispose();*/

            //d1.percentMemInUse = ((double)(((double)d1.memInUse / (double)installedMemory)) * 100);
            counter.Close();
            counter.Dispose();
            counter = new PerformanceCounter("Process", "IO Data Bytes/sec", p1.ProcessName, machinename);
            //Thread.Sleep(1000);
            f = counter.NextValue();

            d1.iobytes = f;
            counter.Close();
            counter.Dispose();
           //// p1.Close();
          //  p1.Dispose();
            return d1;
        }



        private data GetProcessInfo(string processname, string machinename)
        {
            data d1 = null;
            // Process p1 = Process.GetProcessById((int)processId, machinename);
            d1 = new data();
            Process[] p1 = Process.GetProcessesByName(processname, machinename);
            if (p1.Length == 1)
            {
                d1.name = p1[0].ProcessName;
                //                d.mem = p1.PrivateMemorySize64;
                d1.threads = p1[0].Threads.Count;
                d1.memInUse = p1[0].PrivateMemorySize64;
                try
                {
                    d1.startedat = p1[0].StartTime;
                }
                catch { d1.startedat = DateTime.MinValue; }

                PerformanceCounter counter = new PerformanceCounter("Process", "% Processor Time", p1[0].ProcessName, machinename);
                float f = counter.NextValue();
                Thread.Sleep(1000);
                f = counter.NextValue();
                d1.percentCPU = f;

                ManagementObjectSearcher s2 = new ManagementObjectSearcher(@"\\" + machinename + @"\root\cimv2",
               "   select TotalPhysicalMemory from Win32_ComputerSystem");
                ManagementObjectCollection moc = s2.Get();
                ulong installedMemory = 0;
                foreach (ManagementObject o in moc)
                {
                    d1.installedmem = installedMemory = Convert.ToUInt64(o["TotalPhysicalMemory"], CultureInfo.InvariantCulture);
                }

                moc.Dispose();
                s2.Dispose();

                d1.percentMemInUse = ((double)(((double)d1.memInUse / (double)installedMemory)));
                counter.Close();
                counter.Dispose();
                counter = new PerformanceCounter("Process", "IO Data Bytes/sec", p1[0].ProcessName, machinename);
                //Thread.Sleep(1000);
                f = counter.NextValue();

                d1.iobytes = f;
                counter.Close();
                counter.Dispose();
                p1[0].Close();
                p1[0].Dispose();
            }
            else
            {
                for (int i = 0; i < p1.Length; i++)
                {
                    p1[i].Close();
                    p1[i].Dispose();
                }
                return null;
            }
            return d1;
        }


        private data GetProcessInfo(uint processId, string machinename)
        {
            data d1 = null;
            Process p1 = Process.GetProcessById((int)processId, machinename);
            d1 = new data();
            d1.name = p1.ProcessName;
            //                d.mem = p1.PrivateMemorySize64;
            d1.threads = p1.Threads.Count;
            d1.memInUse = p1.PrivateMemorySize64;
            try
            {
                d1.startedat = p1.StartTime;
            }
            catch { d1.startedat = DateTime.MinValue; }

            PerformanceCounter counter = new PerformanceCounter("Process", "% Processor Time", p1.ProcessName, machinename);
            float f = counter.NextValue();
            f = counter.NextValue();
            d1.percentCPU = f;

            ManagementObjectSearcher s2 = new ManagementObjectSearcher(@"\\" + machinename + @"\root\cimv2",
           "   select TotalPhysicalMemory from Win32_ComputerSystem");
            ManagementObjectCollection moc = s2.Get();
            ulong installedMemory = 0;
            foreach (ManagementObject o in moc)
            {
                d1.installedmem = installedMemory = Convert.ToUInt64(o["TotalPhysicalMemory"], CultureInfo.InvariantCulture);
            }

            moc.Dispose();
            s2.Dispose();

            d1.percentMemInUse = ((double)(((double)d1.memInUse / (double)installedMemory)) * 100);
            counter.Close();
            counter.Dispose();
            counter = new PerformanceCounter("Process", "IO Data Bytes/sec", p1.ProcessName, machinename);
           // Thread.Sleep(1000);
            f = counter.NextValue();
            f = counter.NextValue();

            d1.iobytes = f;
            counter.Close();
            counter.Dispose();
            p1.Close();
            p1.Dispose();
            return d1;
        }

        private data findAspecificWindowsServiceInfo(string processDisplayName, string machinename, string altprocessname)
        {
            data d1 = null;
            try
            {
                ManagementObjectSearcher searcher = new ManagementObjectSearcher(@"\\" + machinename + @"\root\cimv2",
                     "    SELECT * FROM Win32_Service WHERE DisplayName='" + processDisplayName + "'");
                ManagementObjectCollection collection = searcher.Get();
                ManagementObjectCollection.ManagementObjectEnumerator e = collection.GetEnumerator();
                if (e.MoveNext())
                {
                    ManagementObject queryObj = e.Current as ManagementObject;
                    if (queryObj != null)
                    {
                        uint processId = (uint)queryObj["ProcessId"];
                        queryObj.Dispose();
                        d1 = this.GetProcessInfo(processId, machinename);
                    }
                }
                e.Dispose();
                collection.Dispose();
                searcher.Dispose();
            }
            catch (ManagementException e)
            {
                //MessageBox.Show("An error occurred while querying for WMI data: " + e.Message);
                return null;
            }
            //    return -1;


            try
            {
                Process[] p1 = Process.GetProcessesByName(altprocessname, machinename);
                for (int i = 0; i < p1.Length; i++)
                {
                    try
                    {
                        data temp = GetProcessInfo(p1[i], machinename);
                        d1.iobytes += temp.iobytes;
                        d1.memInUse += temp.memInUse;
                        d1.percentCPU += temp.percentCPU;
                        d1.threads += temp.threads;
                        p1[i].Close();
                        p1[i].Dispose();
                    }
                    catch { }
                }
                
            }
            catch (Exception ex)
            {

            }
            
            d1.percentMemInUse = ((double)(((double)d1.memInUse / (double)d1.installedmem)));
      

            return d1;


        }

        private data findAspecificWindowsServiceInfo(string processDisplayName, string machinename)
        {
            data d1 = null;
            try
            {
                ManagementObjectSearcher searcher = new ManagementObjectSearcher(@"\\" + machinename + @"\root\cimv2",
                     "    SELECT * FROM Win32_Service WHERE DisplayName='" + processDisplayName + "'");
                ManagementObjectCollection collection = searcher.Get();
                ManagementObjectCollection.ManagementObjectEnumerator e = collection.GetEnumerator();
                if (e.MoveNext())
                {
                    ManagementObject queryObj = e.Current as ManagementObject;
                    if (queryObj != null)
                    {
                        uint processId = (uint)queryObj["ProcessId"];
                        queryObj.Dispose();
                        d1 = this.GetProcessInfo(processId, machinename);
                    }
                }
                e.Dispose();
                collection.Dispose();
                searcher.Dispose();
            }
            catch (ManagementException e)
            {
                return null;
            }

            d1.percentMemInUse = ((double)(((double)d1.memInUse / (double)d1.installedmem)));


            return d1;


        }

        private void button9_Click(object sender, EventArgs e)
        {
            DateTime now = DateTime.Now;
            data d = findAspecificWindowsServiceInfo("postgresql-x64-9.0 - PostgreSQL Server 9.0", "localhost");
            DateTime n2 = DateTime.Now;
            TimeSpan ts = new TimeSpan(n2.Ticks - now.Ticks);
            textBox2.Text = "took " +ts.TotalMilliseconds.ToString("N") + "ms" + Environment.NewLine +
                d.toString();
            /*
            Process[] p = Process.GetProcesses();
            List<data> counters = new List<data>();
            foreach (Process p1 in p)
            {
                try
                {
                    data d = new data();
                    d.name = p1.ProcessName;
                    //                d.mem = p1.PrivateMemorySize64;
                    d.threads = p1.Threads.Count;
                    d.mem = p1.WorkingSet64;
                    try
                    {
                        d.startedat = p1.StartTime;
                    }
                    catch { d.startedat = DateTime.MinValue; }

                    var counter = new PerformanceCounter("Process", "% Processor Time", p1.ProcessName);
                    float f = counter.NextValue();
                    d.cpu = f;
                    counters.Add(d);
                }
                catch { }
            }
            int i = 0;
            string s = "";
            foreach (data d1 in counters)
            {
                try
                {
                    s += d1.name + " " + d1.startedat.ToString() + " " + d1.mem + " " + d1.cpu + " " + d1.threads + Environment.NewLine;
                }
                catch { }
                i++;
            }
            textBox2.Text = s;
            //MessageBox.Show(s);
             * */
        }
        public class data
        {
            public DateTime startedat;
            public string name;
            public float percentCPU;
            public long memInUse;
            public double percentMemInUse;
            public int threads;
            public ulong installedmem;
            public float iobytes;
            public string toString()
            {
                
                return "Started at " + startedat.ToString("o") + Environment.NewLine +
                    "Name " + name + Environment.NewLine +
                    "CPU Usage " + percentCPU + Environment.NewLine +
                    "Memory " + memInUse + Environment.NewLine +
                    "Memory Usage " + percentMemInUse.ToString("P") + Environment.NewLine +
                    "Threads " + threads + Environment.NewLine +
                    "IO Bytes " + iobytes.ToString("N") + Environment.NewLine +
                    "Installed Memory " + installedmem.ToString("N") + Environment.NewLine;

            }
        }

        private void button10_Click(object sender, EventArgs e)
        {
            DateTime now = DateTime.Now;
            data d = findAspecificWindowsServiceInfo("postgresql-x64-9.0 - PostgreSQL Server 9.0", "localhost", "postgres");
            DateTime n2 = DateTime.Now;
            TimeSpan ts = new TimeSpan(n2.Ticks - now.Ticks);
            textBox2.Text = "took " + ts.TotalMilliseconds.ToString("N") + "ms" + Environment.NewLine +
                d.toString();
        }

        private void button11_Click(object sender, EventArgs e)
        {
            DateTime now = DateTime.Now;
            data d = GetProcessInfo("java", "localhost");
            DateTime n2 = DateTime.Now;
            TimeSpan ts = new TimeSpan(n2.Ticks - now.Ticks);
            textBox2.Text = "took " + ts.TotalMilliseconds.ToString("N") + "ms" + Environment.NewLine;
            if (d == null)
                textBox2.Text += "no results";
            else textBox2.Text += d.toString();
        }
    }


    public class PerfCon
    {
        public double cpu;
        public double memory;
        public double networkIN;
        public double networkOUT;
    }
}
