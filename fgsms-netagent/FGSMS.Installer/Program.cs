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
using System.Linq;
using System.Text;

using System;
using System.Collections.Generic;

using System.Text;
using System.IO;
using System.Collections;
using System.Diagnostics;
using System.Configuration;
using System.ServiceModel.Configuration;
using System.Xml;
using System.ServiceProcess;
using System.Security.Permissions;
using System.Security.Principal;
using System.Threading;
using System.Reflection;
using org.miloss.fgsms.agent;
using org.miloss.fgsms.agent.wcf;

namespace FSMS.Installer
{
    /// <summary>
    /// .NET command line installer, allows for scripted installed of all .NET web services agents, .NET persistent storage agents.
    /// </summary>
    public class Program
    {

        static string workingDirectory = "";

        string ASPNET = typeof(AgentSoapExtension).FullName; // "FGSMS.Agent.AgentSoapExtension, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=e81ab410f7d48538";
        string SERVICE_BEHAVIOR_FQN = typeof(AgentWCFServiceBehaviorElement).FullName; //"FGSMS.Agents.AgentWCF.AgentWCFServiceBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral PublicKeyToken=e81ab410f7d48538";
        string CLIENT_BEHAVIOR_FQN = typeof(AgentWCFClientEndpointBehaviorElement).FullName; //"FGSMS.Agents.AgentWCF.AgentWCFClientEndpointBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=e81ab410f7d48538";
        public Program()
        {
            GetMachineConfigFiles();
        }

        [PrincipalPermission(SecurityAction.Demand, Role = "Administrators")]
        static void checkAdministrator()
        {
            Console.WriteLine("Access Check: OK!");
        }

        static void Main(string[] args)
        {
            String dir = Assembly.GetExecutingAssembly().Location;
            dir = dir.Substring(0, dir.LastIndexOf("\\"));
            workingDirectory = dir + "\\";
            Console.Out.WriteLine("Current working directory is " + dir);


            try
            {
                // PrincipalPolicy must be set to WindowsPrincipal to check roles.
                AppDomain.CurrentDomain.SetPrincipalPolicy(PrincipalPolicy.WindowsPrincipal);
                // Check using the PrincipalPermissionAttribute
                checkAdministrator();
                // Check using PrincipalPermission class.
                PrincipalPermission principalPerm = new PrincipalPermission(null, "Administrators");
                principalPerm.Demand();
                //Console.WriteLine("Demand succeeded.");
            }
            catch (Exception e)
            {
                ConsoleColor old = Console.ForegroundColor;
                ConsoleColor bak = Console.BackgroundColor;
                Console.ForegroundColor = ConsoleColor.Red;
                Console.BackgroundColor = ConsoleColor.Black;
                Console.Beep();
                Console.WriteLine("Access Check Failed! You must run this tool with administrative privledges if UAC is turned on");
                Console.WriteLine(e.Message);
                Console.ForegroundColor = old;
                Console.BackgroundColor = bak;
                Environment.Exit(1);

            }

            Program p = new Program();
            p.validatePackage();
            p.ValidateDotNetInstalled();

            if (args == null || args.Length == 0)
            {
                Console.WriteLine("Usage:");
                Console.WriteLine("\tFGSMS.NETInstaller.exe <cmd> <options>");
                //Console.WriteLine("Note: must run as administrator");
                Console.WriteLine("Available options:");

                //TODO certificate installation?

                Console.WriteLine("\tvalidatePackage - Ensures that all the necessary files are in place for installation. No configuration changes are made.");
                Console.WriteLine("------------Installing and Configuring------------");
                Console.WriteLine("\tinstallWebServiceAgentsLibraries - copies the assemblies to the GAC. No configuration changes are made.");
                Console.WriteLine("\tconfigure - Launches the .NET agent configuration utility");
                Console.WriteLine("\tapplyConfiguration - applies the defined configuration to the machine config files");
                Console.WriteLine("\taddWcfExtensions - Changes the machine config file to register the FGSMS WCF based capabilities");
                Console.WriteLine("\tenableWcfServiceMonitor - Changes the machine config file to monitor all WCF service based capabilities");
                Console.WriteLine("\tenableWcfClientMonitor - Changes the machine config file to monitor all WCF client based capabilities");
                Console.WriteLine("\tenableASPMonitor - Changes the machine config file to monitor all ASP.NET web service clients and services");

                Console.WriteLine("------------Disabling and Uninstalling ------------");
                Console.WriteLine("\tdelWcfExtensions - Changes the machine config file to unregister the FGSMS WCF based capabilities and disable all WCF monitoring");
                Console.WriteLine("\tdisableWcfServiceMonitor - Changes the machine config file to NOT monitor all WCF based service capabilities");
                Console.WriteLine("\tdisableWcfClientMonitor - Changes the machine config file to NOT monitor all WCF based client capabilities");
                Console.WriteLine("\tdisableASPMonitor - Changes the machine config file to NOT monitor all ASP.NET web service clients and services");

                //TODO web parts?
                //TODO monitor a specific service or client
                //Console.WriteLine("\tdeployOsAgent - installs and starts the FGSMS OS Agent as a Windows Service");
                Console.WriteLine("\tdisableAll - disables all web service agents");

                Console.WriteLine("\tuninstallWebServiceAgentsLibraries - removes the assemblies to the GAC and removes all applied configuration settings and extensions.");
                Console.WriteLine("\tinstallPersistentWebServiceAgent - Installs and starts the persistent storage agent as a Windows Service.");
                Console.WriteLine("\tremovePersistentWebServiceAgent - Stops and removes the persistent storage agent as a Windows Service.");
                return;
            }


            try
            {
                Console.WriteLine("Command: " + args[0]);


                switch (args[0])
                {
                    case "validatePackage":
                        p.validatePackage(args);
                        break;
                    case "installWebServiceAgentsLibraries":
                        p.installWebServiceAgentsLibraries(args);
                        break;
                    case "installPersistentWebServiceAgent":
                        p.installPersistentWebServiceAgent(args);
                        break;
                    case "removePersistentWebServiceAgent":
                        p.removePersistentWebServiceAgent(args);
                        break;
                    case "configure":
                        p.configure(args);
                        break;
                    case "applyConfiguration":
                        p.applyConfiguration(args);
                        break;
                    case "enableWcfServiceMonitor":
                        p.enableWcfServiceMonitor(args);
                        break;
                    case "enableWcfClientMonitor":
                        p.enableWcfClientMonitor(args);
                        break;
                    case "addWcfExtensions":
                        p.addWcfExtensions(args);
                        break;
                    case "delWcfExtensions":
                        p.delWcfExtensions(args);
                        break;
                    case "disableWcfServiceMonitor":
                        p.disableWcfServiceMonitor(args);
                        break;
                    case "disableWcfClientMonitor":
                        p.disableWcfClientMonitor(args);
                        break;
                    case "enableASPMonitor":
                        p.enableASPMonitor(args);
                        break;
                    case "disableASPMonitor":
                        p.disableASPMonitor(args);
                        break;
                    case "deployOsAgent":
                        p.deployOsAgent(args);
                        break;
                    case "disableAll":
                        p.disableAll(args);
                        break;
                    case "uninstallWebServiceAgentsLibraries":
                        p.uninstallWebServiceAgentsLibraries(args);
                        break;
                    default:
                        Console.WriteLine("unknown command");
                        break;
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine("An error occured. For reference, if any changes were made to machine config files, there were backed up before the change and are located in the same folder as the existing machine.config, usually in c:\\windows\\microsoft.net\\framework(x64)\\version\\config. " +
                "Please report this error and/or submit a bug at https://github.com/mil-oss/fgsms");
                Console.Error.WriteLine(ex.Message);
                Environment.Exit(1);
            }
        }

        private void validatePackage(string[] args)
        {
            try
            {
                this.validatePackage();
                Environment.Exit(0);
            }
            catch (Exception ex)
            {
                Environment.Exit(1);
            }
        }

        private void uninstallWebServiceAgentsLibraries(string[] args)
        {
            delWcfExtensions(args);
            disableASPMonitor(args);
            unconfig(args);
            removeFromGAC();
        }

        private void unconfig(string[] args)
        {
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(File.ReadAllText(configs[i]));
                XmlNodeList list2 = doc.GetElementsByTagName("configuration");
                XmlNode configroot = null;
                if (list2.Count < 1 || list2.Count > 1)
                    throw new Exception("config file is corrupted!");
                else
                    configroot = list2[0];
                XmlNodeList list = doc.GetElementsByTagName("appSettings");
                XmlNode appsettings = null;
                if (list.Count == 1)
                    appsettings = list[0];
                else
                {
                    appsettings = doc.CreateElement("appSettings");
                    configroot.AppendChild(appsettings);
                }

                List<XmlNode> itemstoremove = new List<XmlNode>();
                IEnumerator it = appsettings.ChildNodes.GetEnumerator();

                while (it.MoveNext())
                {
                    XmlNode temp = (XmlNode)it.Current;
                    XmlAttribute a = temp.Attributes["key"];
                    if (a != null)
                    {
                        string key = a.Value;
                        if (key.StartsWith("FGSMS."))
                        {
                            itemstoremove.Add(temp);
                        }
                    }
                }
                for (int k = 0; k < itemstoremove.Count; k++)
                {
                    appsettings.RemoveChild(itemstoremove[k]);
                }

                Console.Out.WriteLine("App Settings cleared from " + configs[i]);
                XmlWriterSettings xws = new XmlWriterSettings();

                xws.ConformanceLevel = ConformanceLevel.Document;
                xws.Indent = true;
                // xws.OutputMethod = XmlOutputMethod.Xml;
                XmlWriter xw = XmlWriter.Create(configs[i], xws);
                doc.Save(xw);
                xw.Flush();
                xw.Close();

            }
        }

        private void removeFromGAC()
        {
            Process p = new Process();
            p.StartInfo.FileName = ("gacutil.exe");
            p.StartInfo.Arguments = "/u FGSMS.NETAgent";
            p.StartInfo.WorkingDirectory = workingDirectory;
            p.Start();
            p.WaitForExit();
            int code = p.ExitCode;
            if (code == 0)
                Console.Out.WriteLine("Assemblies Removed from the GAC successfully");
            else
                Console.Out.WriteLine("Assemblies Removal failed, exit code was " + code + ". you may have to restart or manually remove the file FGSMS.NETAgent.dll from c:\\windows\\assembly or make sure you run as administrator.");
            p.Dispose();
        }

        private void delWcfExtensions(string[] args)
        {
            this.disableWcfClientMonitor(args);
            this.disableWcfServiceMonitor(args);
            Configuration currentMachineConfig = ConfigurationManager.OpenMachineConfiguration();
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                try
                {
                    XmlDocument doc = new XmlDocument();
                    doc.LoadXml(File.ReadAllText(configs[i]));
                    XmlNodeList list = doc.GetElementsByTagName("system.serviceModel");
                    XmlNode servicemodelsection = null;
                    if (list.Count == 1)
                        servicemodelsection = list[0];

                    IEnumerator it = servicemodelsection.ChildNodes.GetEnumerator();
                    XmlNode extensions = null;
                    XmlNode extensionsBehaviors = null;
                    bool removed = false;
                    //remove service extension
                    while (it.MoveNext() && !removed)
                    {
                        XmlNode temp = (XmlNode)it.Current;
                        if (temp.Name.Equals("extensions", StringComparison.CurrentCultureIgnoreCase))
                        {
                            extensions = temp;
                            IEnumerator it2 = extensions.ChildNodes.GetEnumerator();
                            while (it2.MoveNext() && !removed)
                            {
                                XmlNode temp2 = (XmlNode)it2.Current;
                                if (temp2.Name.Equals("behaviorExtensions", StringComparison.CurrentCultureIgnoreCase))
                                {
                                    extensionsBehaviors = temp2;
                                    IEnumerator it3 = extensionsBehaviors.ChildNodes.GetEnumerator();
                                    while (it3.MoveNext() && !removed)
                                    {
                                        XmlNode temp3 = (XmlNode)it3.Current;
                                        if (temp3.Attributes != null)
                                        {
                                            XmlAttribute a = temp3.Attributes["name"];

                                            if (a != null && a.Value.Equals(extendionelement, StringComparison.CurrentCultureIgnoreCase))
                                            {
                                                extensionsBehaviors.RemoveChild(temp3);
                                                removed = true;

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    it = servicemodelsection.ChildNodes.GetEnumerator();
                    extensions = null;
                    extensionsBehaviors = null;
                    bool removed2 = false;
                    //remove client extension
                    while (it.MoveNext() && !removed2)
                    {
                        XmlNode temp = (XmlNode)it.Current;
                        if (temp.Name.Equals("extensions", StringComparison.CurrentCultureIgnoreCase))
                        {
                            extensions = temp;
                            IEnumerator it2 = extensions.ChildNodes.GetEnumerator();
                            while (it2.MoveNext() && !removed2)
                            {
                                XmlNode temp2 = (XmlNode)it2.Current;
                                if (temp2.Name.Equals("behaviorExtensions", StringComparison.CurrentCultureIgnoreCase))
                                {
                                    extensionsBehaviors = temp2;
                                    IEnumerator it3 = extensionsBehaviors.ChildNodes.GetEnumerator();
                                    while (it3.MoveNext() && !removed2)
                                    {
                                        XmlNode temp3 = (XmlNode)it3.Current;
                                        if (temp3.Attributes != null)
                                        {
                                            XmlAttribute a = temp3.Attributes["name"];
                                            if (a != null && a.Value.Equals(extendionClientelement, StringComparison.CurrentCultureIgnoreCase))
                                            {
                                                extensionsBehaviors.RemoveChild(temp3);
                                                removed2 = true;

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    //add the extension if it doesn't exist
                    if (removed || removed2)
                    {
                        Console.Out.WriteLine("WCF extension unregistered at " + configs[i]);
                        XmlWriterSettings xws = new XmlWriterSettings();

                        xws.ConformanceLevel = ConformanceLevel.Document;
                        xws.Indent = true;
                        // xws.OutputMethod = XmlOutputMethod.Xml;
                        XmlWriter xw = XmlWriter.Create(configs[i], xws);
                        doc.Save(xw);
                        xw.Flush();
                        xw.Close();
                    }
                    else
                        Console.Out.WriteLine("WCF service extension was not registered at" + configs[i]);

                }
                catch (Exception ex)
                {
                    Console.Out.WriteLine("unable to process " + configs[i] + " " + ex.Message);
                }
            }

        }

        private void addWcfExtensions(string[] args)
        {
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                try
                {
                    XmlDocument doc = new XmlDocument();
                    doc.LoadXml(File.ReadAllText(configs[i]));
                    XmlNodeList list = doc.GetElementsByTagName("system.serviceModel");
                    XmlNode servicemodelsection = null;
                    if (list.Count == 1)
                        servicemodelsection = list[0];

                    IEnumerator it = servicemodelsection.ChildNodes.GetEnumerator();
                    XmlNode extensions = null;
                    XmlNode extensionsBehaviors = null;
                    while (it.MoveNext())
                    {
                        XmlNode temp = (XmlNode)it.Current;
                        if (temp.Name.Equals("extensions", StringComparison.CurrentCultureIgnoreCase))
                        {
                            extensions = temp;
                            IEnumerator it2 = extensions.ChildNodes.GetEnumerator();
                            while (it2.MoveNext())
                            {
                                XmlNode temp2 = (XmlNode)it2.Current;
                                if (temp2.Name.Equals("behaviorExtensions", StringComparison.CurrentCultureIgnoreCase))
                                {
                                    extensionsBehaviors = temp2;
                                }
                            }
                        }
                    }
                    //add the extension if it doesn't exist
                    if (AddWCFExtensionsIfNotExists(extensionsBehaviors, doc))
                    {
                        Console.Out.WriteLine("WCF extension registered at " + configs[i]);
                        XmlWriterSettings xws = new XmlWriterSettings();

                        xws.ConformanceLevel = ConformanceLevel.Document;
                        xws.Indent = true;
                        // xws.OutputMethod = XmlOutputMethod.Xml;
                        XmlWriter xw = XmlWriter.Create(configs[i], xws);
                        doc.Save(xw);
                        xw.Flush();
                        xw.Close();
                    }
                    else
                        Console.Out.WriteLine("WCF service extension was already registered at" + configs[i]);

                }
                catch (Exception ex)
                {
                    Console.Out.WriteLine("unable to process " + configs[i] + " " + ex.Message);
                }
            }
        }

        private void ValidateDotNetInstalled()
        {
            string win = System.Environment.SystemDirectory;
            if (!Directory.Exists(win + "\\..\\Microsoft.NET\\Framework"))
                throw new Exception(".NET directory was not found");
            GetMachineConfigFiles();
            if (!hasDotNet30orHigher)
                throw new Exception(".NET v3.0 or higher must be installed first!");
        }

        private void validatePackage()
        {
            bool valid = true;
            string msg = "";
            String dir = Directory.GetCurrentDirectory();
            dir = Assembly.GetExecutingAssembly().Location;
            dir = dir.Substring(0, dir.LastIndexOf("\\"));
            if (!File.Exists(dir + "\\gacutil.exe"))
            {
                valid = false;
                msg += "gacutil.exe" + Environment.NewLine;
            }
            if (!File.Exists(dir + "\\current.config"))
            {
                valid = false;
                msg += "current.config" + Environment.NewLine;
            }
            if (!File.Exists(dir + "\\FGSMS.NETAgent.dll"))
            {
                valid = false;
                msg += "FGSMS.NETAgent.dll" + Environment.NewLine;
            }
            if (!File.Exists(dir + "\\FGSMS.Tools.AgentConfig.exe"))
            {
                valid = false;
                msg += "FGSMS.Tools.AgentConfig.exe" + Environment.NewLine;
            }
            if (!File.Exists(dir + "\\FGSMS.PersistentStorageAgent.exe"))
            {
                valid = false;
                msg += "FGSMS.PersistentStorageAgent.exe" + Environment.NewLine;
            }


            string strongName = "";
            try
            {
                strongName = Assembly.LoadFile(workingDirectory + "FGSMS.NETAgent.dll").FullName;
            }
            catch (Exception ex)
            {
                // System process?
                System.Console.WriteLine(ex.Message);
            }
            if ("".Equals(strongName))
            {
                valid = false;
                msg += "Found not determine the FGSMS.NETAgent.dll strong name, was it signed? check to make sure it's present and was signed using'sn.exe -T FGSMS.NETAgent.dll'" + Environment.NewLine;
            }
            else
            {
                ASPNET = ASPNET + "," + strongName;
                SERVICE_BEHAVIOR_FQN = SERVICE_BEHAVIOR_FQN + "," + strongName;
                CLIENT_BEHAVIOR_FQN = CLIENT_BEHAVIOR_FQN + "," + strongName;

            }
            Console.WriteLine("Strong name detected as " + strongName + ". Please verify the signing key with your distribution");



            if (!valid)
            {
                Console.Out.WriteLine("The package is invalid and is missing some critical files!" + Environment.NewLine );
                Console.Out.WriteLine("Missing the following files" + Environment.NewLine + msg);
                Environment.Exit(1);
            }
        }



        private void disableAll(string[] args)
        {
            disableASPMonitor(args);
            disableWcfClientMonitor(args);
            disableWcfServiceMonitor(args);
        }

        private void disableASPMonitor(string[] args)
        {
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                try
                {
                    XmlDocument doc = new XmlDocument();
                    doc.LoadXml(File.ReadAllText(configs[i]));
                    XmlNodeList list = doc.GetElementsByTagName("system.web");
                    XmlNode systemweb = null;
                    if (list.Count == 1)
                        systemweb = list[0];
                    //webServices
                    IEnumerator it = systemweb.ChildNodes.GetEnumerator();
                    XmlNode webServices = null;
                    XmlNode soapExtensionTypes = null;
                    while (it.MoveNext())
                    {
                        XmlNode temp = (XmlNode)it.Current;
                        if (temp.Name.Equals("webServices", StringComparison.CurrentCultureIgnoreCase))
                        {
                            webServices = temp;
                            IEnumerator it2 = webServices.ChildNodes.GetEnumerator();
                            while (it2.MoveNext())
                            {
                                XmlNode temp2 = (XmlNode)it2.Current;
                                if (temp2.Name.Equals("soapExtensionTypes", StringComparison.CurrentCultureIgnoreCase))
                                {
                                    soapExtensionTypes = temp2;
                                }
                            }
                        }
                    }

                    if (RemoveASPNetExtensionIfNotExist(systemweb, webServices, soapExtensionTypes, doc))

                    //add the extension if it doesn't exist
                    // if (AddWCFExtensionsIfNotExists(extensionsBehaviors, doc))
                    {
                        Console.Out.WriteLine("ASP.NET monitor disabled at " + configs[i]);
                        XmlWriterSettings xws = new XmlWriterSettings();

                        xws.ConformanceLevel = ConformanceLevel.Document;
                        xws.Indent = true;
                        // xws.OutputMethod = XmlOutputMethod.Xml;
                        XmlWriter xw = XmlWriter.Create(configs[i], xws);
                        doc.Save(xw);
                        xw.Flush();
                        xw.Close();
                    }
                    else
                        Console.Out.WriteLine("ASP.NET monitor was already disabled at" + configs[i]);

                }
                catch (Exception ex)
                {
                    Console.Out.WriteLine("unable to process " + configs[i] + " " + ex.Message);
                }
            }
        }

        private bool RemoveASPNetExtensionIfNotExist(XmlNode systemweb, XmlNode webServices, XmlNode soapExtensionTypes, XmlDocument doc)
        {
            if (systemweb == null)
                return false;
            if (webServices == null)
            {
                return false;
            }
            if (soapExtensionTypes == null)
            {
                return false;
            }

            IEnumerator it = soapExtensionTypes.ChildNodes.GetEnumerator();
            while (it.MoveNext())
            {
                XmlNode temp = (XmlNode)it.Current;
                if (temp != null)
                {
                    if (temp.Name == "add")
                    {
                        XmlAttribute a = temp.Attributes["type"];
                        if (a != null)
                        {
                            if (a.Value == ASPNET)
                            {
                                soapExtensionTypes.RemoveChild(temp);
                                return true;
                            }
                        }
                    }

                }
            }

            return false;
        }

        private void deployOsAgent(string[] args)
        {
            //TODO, os agent is java based, might be better handled from the java installer
        }

        private void enableASPMonitor(string[] args)
        {
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                try
                {
                    XmlDocument doc = new XmlDocument();
                    doc.LoadXml(File.ReadAllText(configs[i]));
                    XmlNodeList list = doc.GetElementsByTagName("system.web");
                    XmlNode systemweb = null;
                    if (list.Count == 1)
                        systemweb = list[0];
                    //webServices
                    IEnumerator it = systemweb.ChildNodes.GetEnumerator();
                    XmlNode webServices = null;
                    XmlNode soapExtensionTypes = null;
                    while (it.MoveNext())
                    {
                        XmlNode temp = (XmlNode)it.Current;
                        if (temp.Name.Equals("webServices", StringComparison.CurrentCultureIgnoreCase))
                        {
                            webServices = temp;
                            IEnumerator it2 = webServices.ChildNodes.GetEnumerator();
                            while (it2.MoveNext())
                            {
                                XmlNode temp2 = (XmlNode)it2.Current;
                                if (temp2.Name.Equals("soapExtensionTypes", StringComparison.CurrentCultureIgnoreCase))
                                {
                                    soapExtensionTypes = temp2;
                                }
                            }
                        }
                    }

                    if (AddASPNetExtensionIfNotExist(systemweb, webServices, soapExtensionTypes, doc))
                    {
                        Console.Out.WriteLine("ASP.NET monitor enabled at " + configs[i]);
                        XmlWriterSettings xws = new XmlWriterSettings();

                        xws.ConformanceLevel = ConformanceLevel.Document;
                        xws.Indent = true;
                        // xws.OutputMethod = XmlOutputMethod.Xml;
                        XmlWriter xw = XmlWriter.Create(configs[i], xws);
                        doc.Save(xw);
                        xw.Flush();
                        xw.Close();
                    }
                    else
                        Console.Out.WriteLine("ASP.NET monitor was already enabled at" + configs[i]);

                }
                catch (Exception ex)
                {
                    Console.Out.WriteLine("unable to process " + configs[i] + " " + ex.Message);
                }
            }
        }

        private bool AddASPNetExtensionIfNotExist(XmlNode systemweb, XmlNode webServices, XmlNode soapExtensionTypes, XmlDocument doc)
        {
            if (systemweb == null)
                throw new ArgumentNullException();
            if (webServices == null)
            {
                webServices = doc.CreateElement("webServices");
                systemweb.AppendChild(webServices);
            }
            if (soapExtensionTypes == null)
            {
                soapExtensionTypes = doc.CreateElement("soapExtensionTypes");
                webServices.AppendChild(soapExtensionTypes);
            }

            IEnumerator it = soapExtensionTypes.ChildNodes.GetEnumerator();
            while (it.MoveNext())
            {
                XmlNode temp = (XmlNode)it.Current;
                if (temp != null)
                {
                    if (temp.Name == "add")
                    {
                        XmlAttribute a = temp.Attributes["type"];
                        if (a != null)
                        {
                            if (a.Value == ASPNET)
                            {
                                return false;
                            }
                        }
                    }

                }
            }
            //add it back in

            XmlElement agent = doc.CreateElement("add");
            agent.SetAttribute("type", ASPNET);
            agent.SetAttribute("priority", "1");
            agent.SetAttribute("group", "High");
            soapExtensionTypes.AppendChild(agent);
            return true;

        }


        private void disableWcfServiceMonitor(string[] args)
        {
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(File.ReadAllText(configs[i]));
                XmlNodeList list = doc.GetElementsByTagName("system.serviceModel");
                XmlNode servicemodelsection = null;
                if (list.Count == 1)
                    servicemodelsection = list[0];

                IEnumerator it = servicemodelsection.ChildNodes.GetEnumerator();
                XmlNode commonbehaviors = null;
                //XmlNode commonbehaviorsendpointBehaviors = null;
                XmlNode commonbehaviorsserviceBehaviors = null;
                bool removed = false;
                while (it.MoveNext() && !removed)
                {
                    XmlNode temp = (XmlNode)it.Current;
                    if (temp.Name.Equals("commonBehaviors", StringComparison.CurrentCultureIgnoreCase))
                    {
                        commonbehaviors = temp;
                        IEnumerator it2 = commonbehaviors.ChildNodes.GetEnumerator();
                        while (it2.MoveNext() && !removed)
                        {
                            XmlNode temp2 = (XmlNode)it2.Current;
                            /* if (temp2.Name.Equals("endpointBehaviors", StringComparison.CurrentCultureIgnoreCase))
                             {
                                 commonbehaviorsendpointBehaviors = temp2;
                             }
                             else*/
                            if (temp2.Name.Equals("serviceBehaviors", StringComparison.CurrentCultureIgnoreCase))
                            {
                                commonbehaviorsserviceBehaviors = temp2;
                                IEnumerator it3 = commonbehaviorsserviceBehaviors.ChildNodes.GetEnumerator();
                                while (it3.MoveNext() && !removed)
                                {
                                    XmlNode behavior = (XmlNode)it3.Current;
                                    // XmlAttribute a = behavior.Attributes["name"];
                                    if (behavior.Name.Equals(extendionelement, StringComparison.CurrentCultureIgnoreCase))
                                    {
                                        commonbehaviorsserviceBehaviors.RemoveChild(behavior);
                                        removed = true;
                                    }
                                }
                            }
                        }
                    }


                }
                //add the behavior if it doesn't exist
                if (removed)
                {
                    Console.Out.WriteLine("WCF service monitor removed from " + configs[i]);
                    XmlWriterSettings xws = new XmlWriterSettings();

                    xws.ConformanceLevel = ConformanceLevel.Document;
                    xws.Indent = true;
                    // xws.OutputMethod = XmlOutputMethod.Xml;
                    XmlWriter xw = XmlWriter.Create(configs[i], xws);
                    doc.Save(xw);
                    xw.Flush();
                    xw.Close();
                }
                else
                    Console.Out.WriteLine("WCF service monitor was not already present in" + configs[i]);

            }
        }

        private void disableWcfClientMonitor(string[] args)
        {

            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(File.ReadAllText(configs[i]));
                XmlNodeList list = doc.GetElementsByTagName("system.serviceModel");
                XmlNode servicemodelsection = null;
                if (list.Count == 1)
                    servicemodelsection = list[0];

                IEnumerator it = servicemodelsection.ChildNodes.GetEnumerator();
                XmlNode commonbehaviors = null;
                //XmlNode commonbehaviorsendpointBehaviors = null;
                XmlNode commonbehaviorsserviceBehaviors = null;
                bool removed = false;
                while (it.MoveNext() && !removed)
                {
                    XmlNode temp = (XmlNode)it.Current;
                    if (temp.Name.Equals("commonBehaviors", StringComparison.CurrentCultureIgnoreCase))
                    {
                        commonbehaviors = temp;
                        IEnumerator it2 = commonbehaviors.ChildNodes.GetEnumerator();
                        while (it2.MoveNext() && !removed)
                        {
                            XmlNode temp2 = (XmlNode)it2.Current;
                            /* if (temp2.Name.Equals("endpointBehaviors", StringComparison.CurrentCultureIgnoreCase))
                             {
                                 commonbehaviorsendpointBehaviors = temp2;
                             }
                             else*/
                            if (temp2.Name.Equals("endpointBehaviors", StringComparison.CurrentCultureIgnoreCase))
                            {
                                commonbehaviorsserviceBehaviors = temp2;
                                IEnumerator it3 = commonbehaviorsserviceBehaviors.ChildNodes.GetEnumerator();
                                while (it3.MoveNext() && !removed)
                                {
                                    XmlNode behavior = (XmlNode)it3.Current;
                                    // XmlAttribute a = behavior.Attributes["name"];
                                    if (behavior.Name.Equals(extendionClientelement, StringComparison.CurrentCultureIgnoreCase))
                                    {
                                        commonbehaviorsserviceBehaviors.RemoveChild(behavior);
                                        removed = true;
                                    }
                                }
                            }
                        }
                    }


                }
                //add the behavior if it doesn't exist
                if (removed)
                {
                    Console.Out.WriteLine("WCF client monitor removed from " + configs[i]);
                    XmlWriterSettings xws = new XmlWriterSettings();

                    xws.ConformanceLevel = ConformanceLevel.Document;
                    xws.Indent = true;
                    // xws.OutputMethod = XmlOutputMethod.Xml;
                    XmlWriter xw = XmlWriter.Create(configs[i], xws);
                    doc.Save(xw);
                    xw.Flush();
                    xw.Close();
                }
                else
                    Console.Out.WriteLine("WCF client monitor was not already present in " + configs[i]);

            }
        }

        private void enableWcfClientMonitor(string[] args)
        {
            if (!hasDotNet35orHigher)
            {
                Console.Out.WriteLine(".NET 3.5 or higher must be installed prior to enabling this feature");
                return;
            }
            //ensure that the extensions are present first
            this.addWcfExtensions(args);
            Configuration currentMachineConfig = ConfigurationManager.OpenMachineConfiguration();
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(File.ReadAllText(configs[i]));
                XmlNodeList list = doc.GetElementsByTagName("system.serviceModel");
                XmlNode servicemodelsection = null;
                if (list.Count == 1)
                    servicemodelsection = list[0];

                IEnumerator it = servicemodelsection.ChildNodes.GetEnumerator();
                XmlNode commonbehaviors = null;
                //XmlNode commonbehaviorsendpointBehaviors = null;
                XmlNode commonbehaviorsserviceBehaviors = null;
                while (it.MoveNext())
                {
                    XmlNode temp = (XmlNode)it.Current;
                    if (temp.Name.Equals("commonBehaviors", StringComparison.CurrentCultureIgnoreCase))
                    {
                        commonbehaviors = temp;
                        IEnumerator it2 = commonbehaviors.ChildNodes.GetEnumerator();
                        while (it2.MoveNext())
                        {
                            XmlNode temp2 = (XmlNode)it2.Current;
                            /* if (temp2.Name.Equals("endpointBehaviors", StringComparison.CurrentCultureIgnoreCase))
                             {
                                 commonbehaviorsendpointBehaviors = temp2;
                             }
                             else*/
                            if (temp2.Name.Equals("endpointBehaviors", StringComparison.CurrentCultureIgnoreCase))
                            {
                                commonbehaviorsserviceBehaviors = temp2;
                            }
                        }
                    }


                }
                //add the behavior if it doesn't exist
                if (AddWCFClientBehaviorIfNotExists(commonbehaviors, commonbehaviorsserviceBehaviors, servicemodelsection, doc))
                {
                    Console.Out.WriteLine("WCF client monitor added to " + configs[i]);
                    XmlWriterSettings xws = new XmlWriterSettings();

                    xws.ConformanceLevel = ConformanceLevel.Document;
                    xws.Indent = true;
                    // xws.OutputMethod = XmlOutputMethod.Xml;
                    XmlWriter xw = XmlWriter.Create(configs[i], xws);
                    doc.Save(xw);
                    xw.Flush();
                    xw.Close();
                }
                else
                    Console.Out.WriteLine("WCF client monitor was already added to " + configs[i]);


            }

        }

        private bool AddWCFClientBehaviorIfNotExists(XmlNode commonbehaviors, XmlNode commonbehaviorsserviceBehaviors, XmlNode systemServiceModel, XmlDocument doc)
        {
            if (systemServiceModel == null)
                throw new ArgumentNullException();
            if (commonbehaviors == null)
            {
                commonbehaviors = doc.CreateElement("commonBehaviors");
                systemServiceModel.AppendChild(commonbehaviors);
            }
            if (commonbehaviorsserviceBehaviors == null)
            {
                commonbehaviorsserviceBehaviors = doc.CreateElement("endpointBehaviors");
                commonbehaviors.AppendChild(commonbehaviorsserviceBehaviors);
            }

            bool found = false;
            IEnumerator it = commonbehaviorsserviceBehaviors.ChildNodes.GetEnumerator();
            while (it.MoveNext())
            {
                XmlNode item = (XmlNode)it.Current;
                // XmlAttribute a = item.Attributes["name"];
                if (item.Name.Equals(extendionClientelement, StringComparison.CurrentCultureIgnoreCase))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                XmlElement node = doc.CreateElement(extendionClientelement);
                commonbehaviorsserviceBehaviors.AppendChild(node);
                return true;
            }
            return false;
        }


        private void enableWcfServiceMonitor(string[] args)
        {
            //ensure that the extensions are present first
            this.addWcfExtensions(args);
            Configuration currentMachineConfig = ConfigurationManager.OpenMachineConfiguration();
            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(File.ReadAllText(configs[i]));
                XmlNodeList list = doc.GetElementsByTagName("system.serviceModel");
                XmlNode servicemodelsection = null;
                if (list.Count == 1)
                    servicemodelsection = list[0];

                IEnumerator it = servicemodelsection.ChildNodes.GetEnumerator();
                XmlNode commonbehaviors = null;
                //XmlNode commonbehaviorsendpointBehaviors = null;
                XmlNode commonbehaviorsserviceBehaviors = null;
                while (it.MoveNext())
                {
                    XmlNode temp = (XmlNode)it.Current;
                    if (temp.Name.Equals("commonBehaviors", StringComparison.CurrentCultureIgnoreCase))
                    {
                        commonbehaviors = temp;
                        IEnumerator it2 = commonbehaviors.ChildNodes.GetEnumerator();
                        while (it2.MoveNext())
                        {
                            XmlNode temp2 = (XmlNode)it2.Current;
                            /* if (temp2.Name.Equals("endpointBehaviors", StringComparison.CurrentCultureIgnoreCase))
                             {
                                 commonbehaviorsendpointBehaviors = temp2;
                             }
                             else*/
                            if (temp2.Name.Equals("serviceBehaviors", StringComparison.CurrentCultureIgnoreCase))
                            {
                                commonbehaviorsserviceBehaviors = temp2;
                            }
                        }
                    }


                }
                //add the behavior if it doesn't exist
                if (AddWCFServiceBehaviorIfNotExists(commonbehaviors, commonbehaviorsserviceBehaviors, servicemodelsection, doc))
                {
                    Console.Out.WriteLine("WCF service monitor added to " + configs[i]);
                    XmlWriterSettings xws = new XmlWriterSettings();

                    xws.ConformanceLevel = ConformanceLevel.Document;
                    xws.Indent = true;
                    // xws.OutputMethod = XmlOutputMethod.Xml;
                    XmlWriter xw = XmlWriter.Create(configs[i], xws);
                    doc.Save(xw);
                    xw.Flush();
                    xw.Close();
                }
                else
                    Console.Out.WriteLine("WCF service monitor was already added to " + configs[i]);


            }
        }

        private bool AddWCFServiceBehaviorIfNotExists(XmlNode commonbehaviors, XmlNode commonbehaviorsserviceBehaviors, XmlNode systemServiceModel, XmlDocument doc)
        {
            if (systemServiceModel == null)
                throw new ArgumentNullException();
            if (commonbehaviors == null)
            {
                commonbehaviors = doc.CreateElement("commonBehaviors");
                systemServiceModel.AppendChild(commonbehaviors);
            }
            if (commonbehaviorsserviceBehaviors == null)
            {
                commonbehaviorsserviceBehaviors = doc.CreateElement("serviceBehaviors");
                commonbehaviors.AppendChild(commonbehaviorsserviceBehaviors);
            }

            bool found = false;
            IEnumerator it = commonbehaviorsserviceBehaviors.ChildNodes.GetEnumerator();
            while (it.MoveNext())
            {
                XmlNode item = (XmlNode)it.Current;
                // XmlAttribute a = item.Attributes["name"];
                if (item.Name.Equals(extendionelement, StringComparison.CurrentCultureIgnoreCase))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                XmlElement node = doc.CreateElement(extendionelement);
                commonbehaviorsserviceBehaviors.AppendChild(node);
                return true;
            }
            return false;

        }

        private void MakeBackupConfig(string p)
        {
            long now = DateTime.Now.Ticks;
            Console.Out.WriteLine("Backing up " + p);// to " + p + "backup" + now);
            File.Copy(p, p + "backup" + now);
        }
        const string extendionelement = "FGSMS.ServiceAgent";
        const string extendionClientelement = "FGSMS.ClientAgent";
        private bool AddWCFExtensionsIfNotExists(XmlNode extensions, XmlDocument doc)
        {
            if (extensions == null)
                throw new ArgumentNullException();
            bool foundSvc = false;
            bool foundClient = false;
            IEnumerator it = extensions.ChildNodes.GetEnumerator();
            while (it.MoveNext())
            {
                XmlNode item = (XmlNode)it.Current;
                if (item.Attributes != null)
                {
                    XmlAttribute a = item.Attributes["name"];
                    if (a.Value.Equals(extendionelement, StringComparison.CurrentCultureIgnoreCase))
                    {
                        foundSvc = true;
                        //break;
                    }
                    if (a.Value.Equals(extendionClientelement, StringComparison.CurrentCultureIgnoreCase))
                    {
                        foundClient = true;
                        //break;
                    }
                }
            }
            bool added = false;
            if (!foundSvc)
            {
                XmlElement node = doc.CreateElement("add");
                node.SetAttribute("name", extendionelement);
                node.SetAttribute("type", SERVICE_BEHAVIOR_FQN);
                extensions.AppendChild(node);
                added = true;
            }
            if (!foundClient && hasDotNet35orHigher)
            {
                XmlElement node = doc.CreateElement("add");
                node.SetAttribute("name", extendionClientelement);
                node.SetAttribute("type", CLIENT_BEHAVIOR_FQN);
                extensions.AppendChild(node);
                added = true;
            }
            return added;
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
            CompleteConfig ret = new CompleteConfig();
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(File.ReadAllText(workingDirectory + "current.config"));
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


        private void applyConfiguration(string[] args)
        {
            CompleteConfig currentconfig = this.loadCurrentConfig();

            string[] configs = GetMachineConfigFiles();
            for (int i = 0; i < configs.Length; i++)
            {
                MakeBackupConfig(configs[i]);
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(File.ReadAllText(configs[i]));
                XmlNodeList list2 = doc.GetElementsByTagName("configuration");
                XmlNode configroot = null;
                if (list2.Count < 1 || list2.Count > 1)
                    throw new Exception("config file is corrupted!");
                else
                    configroot = list2[0];
                XmlNodeList list = doc.GetElementsByTagName("appSettings");
                XmlNode appsettings = null;
                if (list.Count == 1)
                    appsettings = list[0];
                else
                {
                    appsettings = doc.CreateElement("appSettings");
                    configroot.AppendChild(appsettings);
                }

                List<XmlNode> itemstoremove = new List<XmlNode>();
                IEnumerator it = appsettings.ChildNodes.GetEnumerator();

                while (it.MoveNext())
                {
                    XmlNode temp = (XmlNode)it.Current;
                    XmlAttribute a = temp.Attributes["key"];
                    if (a != null)
                    {
                        string key = a.Value;
                        if (key.StartsWith("FGSMS."))
                        {
                            itemstoremove.Add(temp);
                        }
                    }
                }
                for (int k = 0; k < itemstoremove.Count; k++)
                {
                    appsettings.RemoveChild(itemstoremove[k]);
                }
                for (int k = 0; k < currentconfig.items.Count; k++)
                {
                    XmlElement e = doc.CreateElement("add");
                    e.SetAttribute("key", currentconfig.items[k].key);
                    e.SetAttribute("value", currentconfig.items[k].value);
                    appsettings.AppendChild(e);
                }

                Console.Out.WriteLine("App Settings added to " + configs[i]);
                XmlWriterSettings xws = new XmlWriterSettings();

                xws.ConformanceLevel = ConformanceLevel.Document;
                xws.Indent = true;
                // xws.OutputMethod = XmlOutputMethod.Xml;
                XmlWriter xw = XmlWriter.Create(configs[i], xws);
                doc.Save(xw);
                xw.Flush();
                xw.Close();

            }
        }

        private void ValidateConfig(Configuration c)
        {

        }

        private void configure(string[] args)
        {
            Process p = new Process();
            p.StartInfo.FileName = ("FGSMS.Tools.AgentConfig.exe");
            p.Start();
        }

        private void removePersistentWebServiceAgent(string[] args)
        {
            if (!ServiceInstaller.ServiceIsInstalled(PERSISTENTAGENTNAME))
            {
                Console.Out.WriteLine("The service is not currently installed.");
                return;
            }
            else
            {
                try
                {
                    Console.Out.WriteLine("Stopping...");
                    ServiceInstaller.StopService(PERSISTENTAGENTNAME);
                    Console.Out.WriteLine("Uninstalling...");
                    ServiceInstaller.Uninstall(PERSISTENTAGENTNAME);
                    Console.Out.WriteLine("Uninstall succeeded");
                }
                catch (Exception ex)
                {
                    Console.Out.WriteLine("Uninstall failed " + ex.Message);
                }
            }
        }

        const string PERSISTENTAGENTNAME = "FGSMS.PersistentAgent";
        const string PERSISTENTAGENTNAME_DESCRIPTION = "FGSMS's Persistent Agent watches a specified directory for transactions recorded by agents that couldn't be sent and attempts to resend them periodically.";

        private void installPersistentWebServiceAgent(string[] args)
        {
            if (ServiceInstaller.ServiceIsInstalled(PERSISTENTAGENTNAME))
            {
                Console.Out.WriteLine("The service is already installed.");
                return;
            }

            if (File.Exists(workingDirectory + "\\FGSMS.PersistentStorageAgent.exe"))
            {
                try
                {
                    ServiceInstaller.InstallAndStart(PERSISTENTAGENTNAME, PERSISTENTAGENTNAME, workingDirectory + "\\FGSMS.PersistentStorageAgent.exe");
                    Console.Out.WriteLine("Installation succeeded");
                    Console.Out.Write("Checking status...");
                    ServiceController sc = new ServiceController(PERSISTENTAGENTNAME);
                    sc.WaitForStatus(ServiceControllerStatus.Running, new TimeSpan(0, 0, 5));
                    Console.Out.WriteLine(sc.Status.ToString());


                }
                catch (Exception ex)
                {
                    Console.Out.WriteLine("Installation failed " + ex.Message);
                }
            }
            else
            {
                Console.Out.WriteLine("The Persistent Storage Agent executable could not be found at " + workingDirectory + "\\FGSMS.PersistentStorageAgent.exe");
            }
        }

        private void installWebServiceAgentsLibraries(string[] args)
        {

            Process p = new Process();
            p.StartInfo.FileName = ("gacutil.exe");
            p.StartInfo.Arguments = "/u FGSMS.NETAgent";
            p.StartInfo.WorkingDirectory = workingDirectory;
            p.Start();
            p.WaitForExit();

            int code = p.ExitCode;
            Console.Out.WriteLine("Removal with code " + code);
            p.Dispose();

            p = new Process();
            p.StartInfo.FileName = ("gacutil.exe");
            p.StartInfo.Arguments = "/i FGSMS.NETAgent.dll";
            p.StartInfo.WorkingDirectory = workingDirectory;
            p.StartInfo.CreateNoWindow = true;
            p.StartInfo.UseShellExecute = false;
            p.StartInfo.RedirectStandardOutput = true;
            p.StartInfo.RedirectStandardError = true;
            p.Start();
            p.WaitForExit();
            string output = p.StandardOutput.ReadToEnd();
            string err = p.StandardError.ReadToEnd();
            code = p.ExitCode;
            p.Dispose();
            if (code > 0)
                Console.Out.WriteLine("Installation failed with exit code " + code + Environment.NewLine + "Output: " + output + err);
            else
                Console.Out.WriteLine("Install succeeded with code " + code);
        }
        bool hasDotNet35orHigher = false;
        bool hasDotNet30orHigher = false;


        private string[] GetMachineConfigFiles()
        {
            List<string> items = new List<string>();
            string[] x = Directory.GetDirectories(System.Environment.SystemDirectory + @"\..\Microsoft.NET\Framework");
            for (int i = 0; i < x.Length; i++)
            {
                if (x[i].Contains("v3.5"))
                    hasDotNet35orHigher = true;
                if (x[i].Contains("v3.0"))
                    hasDotNet30orHigher = true;
                string[] x2 = Directory.GetDirectories(x[i]);
                for (int k = 0; k < x2.Length; k++)
                {
                    if (File.Exists(x2[k] + @"\machine.config"))
                        items.Add(x2[k] + @"\machine.config");
                }
            }
            x = Directory.GetDirectories(System.Environment.SystemDirectory + @"\..\Microsoft.NET\Framework64");
            for (int i = 0; i < x.Length; i++)
            {
                if (x[i].Contains("v3.5"))
                    hasDotNet35orHigher = true;
                if (x[i].Contains("v3.0"))
                    hasDotNet30orHigher = true;
                string[] x2 = Directory.GetDirectories(x[i]);
                for (int k = 0; k < x2.Length; k++)
                {
                    if (File.Exists(x2[k] + @"\machine.config"))
                        items.Add(x2[k] + @"\machine.config");
                }
            }
            return items.ToArray();
        }


    }
}
