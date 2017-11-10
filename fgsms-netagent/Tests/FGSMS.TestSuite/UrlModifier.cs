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
using System.Text;
using System.Collections.Generic;
using System.Linq;
using NUnit.Framework;
using org.miloss.fgsms.agent;
using System.Net;

namespace FGSMS.NETTestSuite
{
    [TestFixture]
    public class UrlModifier
    {
        [Test]
        public void TestMethod1()
        {
        }

        [Test]
        public void testModifyURL()
        {


            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            //String expResult = "";
            String result = MessageProcessor.ModifyURL(url, false);

            Assert.AreEqual(exp, result, result);

        }
        [Test]
        public void testModifyURL2()
        {


            string myhostname = Environment.MachineName.ToLower();


            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            //String expResult = "";
            String result = MessageProcessor.ModifyURL(url, false);

            url = "http://" + myhostname + ":8080/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);

        }
        [Test]
        public void testModifyURL3()
        {


            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            //String expResult = "";
            String result = MessageProcessor.ModifyURL(url, false);

            url = "http://127.0.0.1:8080/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);


        }
        [Test]
        public void testModifyURL4()
        {


            string myhostname = Environment.MachineName.ToLower();
            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";


            url = "http://127.0.0.1:8080/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL5()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";

            url = "http://" + myip + ":8080/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }

        private string getAddress()
        {
            IPHostEntry host;
            string localIP = "?";
            host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (IPAddress ip in host.AddressList)
            {
                if (ip.AddressFamily.ToString() == "InterNetwork")
                {
                    localIP = ip.ToString();
                }
            }
            return localIP;
        }
        [Test]
        public void testModifyURL6()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();


            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";

            //client test
            url = "http://127.0.0.1:8080/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL7()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            try
            {
                // Get hostname by textual representation of IP address



            }
            catch (Exception ex)
            {
            }

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";

            //client test
            url = "http://localhost:8080/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL8()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();


            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";

            //client test
            url = "http://localhost:8080/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL9()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();



            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";

            //client test
            url = "http://" + myip + ":8080/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL9_1()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String exp = "http://" + myhostname + ":8080/something";
            String result = "";

            String url = "http://www.google.com:8080/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(url, result, result);

        }
        [Test]
        public void testModifyURL10()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String result = "";

            String url = "http://254.254.254.254:8080/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(url, result, result);

        }
        [Test]
        public void testModifyURL11()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();


            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";

            //NO PORT DEFINED

            url = "http://localhost/something";
            exp = "http://" + myhostname + ":80/something";
            //String expResult = "";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);

        }
        [Test]
        public void testModifyURL12()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();


            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";


            url = "http://" + myhostname + "/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL13()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";

            url = "http://localhost/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL14()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";

            url = "http://127.0.0.1/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL15()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";

            url = "http://" + myip + "/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL16()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";

            //client test
            url = "http://127.0.0.1/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL17()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            
            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";


            //client test
            url = "http://localhost/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL18()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";

            //client test
            url = "http://localhost/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL19()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
           
            //String url = "http://localhost:8080/something";
            String exp = "http://" + myhostname + ":80/something";
            String result = "";

            //client test
            string url = "http://" + myip + "/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
                       
            //NO PORT DEFINED
        }
        [Test]
        public void testModifyURL20()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String exp = "https://" + myhostname + ":443/something";
            String result = "";


            //NO PORT DEFINED

            string url = "https://localhost/something";
            exp = "https://" + myhostname + ":443/something";
            //String expResult = "";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }


        [Test]
        public void testModifyURL21()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();


            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";

            url = "https://" + myhostname + "/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL22()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";
            url = "https://localhost/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL23()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";
            url = "https://127.0.0.1/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL24()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();

            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";
            url = "https://" + myip + "/something";
            result = MessageProcessor.ModifyURL(url, false);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL25()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            try
            {
                // Get hostname by textual representation of IP address



            }
            catch (Exception ex)
            {
            }

            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";
            //client test
            url = "https://127.0.0.1/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL26()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            try
            {
                // Get hostname by textual representation of IP address



            }
            catch (Exception ex)
            {
            }

            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";

            //client test
            url = "https://localhost/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL27()
        {

            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            try
            {
                // Get hostname by textual representation of IP address



            }
            catch (Exception ex)
            {
            }

            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";
            //client test
            url = "https://localhost/something";
            result = MessageProcessor.ModifyURL(url, true);
            Assert.AreEqual(exp, result, result);
        }
        [Test]
        public void testModifyURL28()
        {
            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            try
            {
                // Get hostname by textual representation of IP address



            }
            catch (Exception ex)
            {
            }

            String url = "http://localhost:8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";
            //client test
            url = "https://" + myip + "/something";
            result = MessageProcessor.ModifyURL(url, true);

            Assert.AreEqual(exp, result, result);
        }


        public void testModifyURL28_1()
        {
            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
            try
            {
                // Get hostname by textual representation of IP address



            }
            catch (Exception ex)
            {
            }

            String url = "http://" + Environment.MachineName.ToUpper() + ":8080/something";
            String exp = "https://" + myhostname + ":443/something";
            String result = "";
            //client test
            url = "https://" + myip + "/something";
            result = MessageProcessor.ModifyURL(url, true);

            Assert.AreEqual(exp, result, result);
        }


        public void testModifyURL29()
        {
            String myip = getAddress();
            string myhostname = Environment.MachineName.ToLower();
          
            String url = "http://" + Environment.MachineName.ToUpper() + ":8080/something";
            String exp = "http://" + myhostname + ":8080/something";
            String result = "";
            //client test
          
            result = MessageProcessor.ModifyURL(url, true);

            Assert.AreEqual(exp, result, result);
        }





        /// <summary>
        /// This should attempt to force a remote url to include the port information
        /// </summary>
        [Test]
        public void testModifyURL31()
        {
            String url = "https://www.google.com/something";
            String exp = "https://www.google.com:443/something";
            String result = "";
            //client test

            result = MessageProcessor.ModifyURL(url, true);

            Assert.AreEqual(exp, result, result);
        }


        /// <summary>
        /// this should replace the hostname of the url to that of the current machine name
        /// </summary>
        [Test]
        public void testModifyURL32()
        {
            String url = "https://www.google.com/something";
            String exp = "https://" + Environment.MachineName.ToLower() + ":443/something";
            String result = "";
            //service test

            result = MessageProcessor.ModifyURL(url, false);

            Assert.AreEqual(exp, result, result);
        }

        

        [Test]
        public void testModifyURL33()
        {
            String url = "http://www.google.com/something";
            String exp = "http://www.google.com:80/something";
            String result = "";
            //client test

            result = MessageProcessor.ModifyURL(url, true);

            Assert.AreEqual(exp, result, result);
        }

        
    }
}
