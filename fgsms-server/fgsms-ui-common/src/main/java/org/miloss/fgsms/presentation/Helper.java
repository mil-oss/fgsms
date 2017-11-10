/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular file,
 * then You may include the notice in a location (such as a LICENSE file in a
 * relevant directory) where a recipient would be likely to look for such a
 * notice.
 *
 * 
 */
/*  ---------------------------------------------------------------------------
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.presentation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.BindingProvider;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetQuickStatsAllResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.MachineData;
import org.miloss.fgsms.services.interfaces.dataaccessservice.QuickStatURIWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ServiceType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.status.GetStatusResponseMsg;

/**
 * Web GUI Presentation layer helper functions
 *
 * @author AO
 */
public class Helper {

     final static boolean DEBUG = false;

     public static String RenderRuleRunAt(RunAtLocation val) {
          StringBuilder s = new StringBuilder();
          s.append("<select name=\"slagenericrulerunat\">");
          for (int i = 0; i < RunAtLocation.values().length; i++) {
               s.append("<option value=\"").append(RunAtLocation.values()[i].value()).append("\" ");
               if (val != null && val == RunAtLocation.values()[i]) {
                    s.append(" selected ");
               }
               s.append(" >").append(RunAtLocation.values()[i].value()).append("</option>");
          }
          s.append("</select>");
          return s.toString();
     }

     /**
      * used on index2.jsp
      *
      * @since 6.2
      */
     public static MachineData FindMachineRecord(List<MachineData> data, String Domainname, String Hostname, String policyURL) {
          if (data == null) {
               return null;
          }
          for (int i = 0; i < data.size(); i++) {
               if (data.get(i).getDomainName().equalsIgnoreCase(Domainname) && data.get(i).getHostname().equalsIgnoreCase(Hostname)) {
                    return data.get(i);
               }
          }
          return null;
     }

     /**
      * used on index2.jsp
      *
      * @since 6.2
      */
     public static ProcessPerformanceData FindProcessRecord(List<MachineData> data, String policyURL) {
          if (data == null) {
               return null;
          }
          if (policyURL == null) {
               return null;
          }
          for (int i = 0; i < data.size(); i++) {
               if (data.get(i).getProcessPerformanceData() == null) {
                    continue;
               }
               for (int k = 0; k < data.get(i).getProcessPerformanceData().size(); k++) {
                    if (data.get(i).getProcessPerformanceData().get(k) != null
                         && data.get(i).getProcessPerformanceData().get(k).getUri() != null && data.get(i).getProcessPerformanceData().get(k).getUri().equalsIgnoreCase(policyURL)) {
                         return data.get(i).getProcessPerformanceData().get(k);
                    }
               }
          }
          return null;
     }

     public static boolean FederationContains(List<Duration> d, int timeInMinutes) {
          if (d == null || d.isEmpty()) {
               return false;
          }
          for (int i = 0; i < d.size(); i++) {
               long span = Utility.durationToTimeInMS(d.get(i));
               if (span == ((long) timeInMinutes * 60L * 1000L)) {
                    return true;
               }
          }
          return false;
     }

     /**
      * used from tree view of home page, returns all nodes that have a
      * null or empty parent object
      *
      * @param completelist
      * @return
      */
     public static List<ServiceType> GetParentNodes(List<ServiceType> completelist) {
          List<ServiceType> ret = new ArrayList<ServiceType>();
          if (completelist == null || completelist.isEmpty()) {
               return ret;
          }
          for (int i = 0; i < completelist.size(); i++) {
               if (Utility.stringIsNullOrEmpty(completelist.get(i).getParentobject())) {
                    ret.add(completelist.get(i));
               }
          }
          return ret;
     }

     /**
      * used from tree view of home page, returns all child nodes from
      * a given parent 
      *
      * @param uri
      * @param completelist
      * @return
      */
     public static List<ServiceType> GetChildNodes(String uri, List<ServiceType> completelist) {
          List<ServiceType> ret = new ArrayList<ServiceType>();
          if (completelist == null || completelist.isEmpty()) {
               return ret;
          }
          for (int i = 0; i < completelist.size(); i++) {
               if (!Utility.stringIsNullOrEmpty(completelist.get(i).getParentobject())) {
                    if (completelist.get(i).getParentobject().equalsIgnoreCase(uri)) {
                         ret.add(completelist.get(i));
                    }
               }
          }
          return ret;
     }

     /**
      * used on serviceprofile.jsp 
      *
      * @param fc
      * @return
      */
     public static FederationPolicy FindUDDIFederationRecord(FederationPolicyCollection fc) {
          if (fc == null) {
               return null;
          }
          if (fc.getFederationPolicy().isEmpty()) {
               return null;
          }
          for (int i = 0; i < fc.getFederationPolicy().size(); i++) {
               if (fc.getFederationPolicy().get(i).getImplementingClassName() != null
                    && fc.getFederationPolicy().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.uddipub.UddiPublisher")) {
                    return fc.getFederationPolicy().get(i);
               }
          }
          return null;
     }

     /**
      * used on serviceprofile.jsp 
      *
      * @param items
      * @return
      */
     public static String PublishRangeToString(List<Duration> items) {
          if (items == null || items.isEmpty()) {
               return null;
          }
          StringBuilder r = new StringBuilder();
          for (int i = 0; i < items.size(); i++) {
               r = r.append(Helper.DurationToString(items.get(i))).append(" ");
          }
          return r.toString().trim();
     }

     public static QuickStatURIWrapper FindRecord(GetQuickStatsAllResponseMsg res, String url) {
          if (res == null || res.getQuickStatURIWrapper() == null || res.getQuickStatURIWrapper().isEmpty()) {
               return null;
          }
          for (int i = 0; i < res.getQuickStatURIWrapper().size(); i++) {
               if (res.getQuickStatURIWrapper().get(i).getUri().equalsIgnoreCase(url)) {
                    return res.getQuickStatURIWrapper().get(i);
               }
          }
          return null;
     }

     public static boolean ContainsSLAID(List<SLAregistration> items, String sla_id) {
          if (items == null || items.isEmpty()) {
               return false;
          }
          for (int i = 0; i < items.size(); i++) {
               if (items.get(i).getSLAID().equalsIgnoreCase(sla_id)) {
                    return true;
               }
          }

          return false;
     }

     public static String ToShortActionString(String action) {
          if (Utility.stringIsNullOrEmpty(action)) {
               return "";
          }
          String ret = action;
          int clip = 0;
          if (ret.lastIndexOf("/") > clip) {
               clip = ret.lastIndexOf("/");
          }
          if (ret.lastIndexOf("}") > clip) {
               clip = ret.lastIndexOf("}");
          }
          if (ret.lastIndexOf(":") > clip) {
               clip = ret.lastIndexOf(":");
          }
          if (ret.lastIndexOf("#") > clip) {
               clip = ret.lastIndexOf("#");
          }

          if (clip > 0) {
               ret = (ret.substring(clip + 1));
          }
          return ret;

     }

     public static String PrettyPrintXMLToHtml(String xml) {
          try {
               Document doc = DocumentHelper.parseText(xml);
               StringWriter sw = new StringWriter();
               OutputFormat format = OutputFormat.createPrettyPrint();
               XMLWriter xw = new XMLWriter(sw, format);
               xw.write(doc);
               return Utility.encodeHTML(sw.toString());

          } catch (Exception ex) {
               //LogHelper.getLog().log(Level.INFO, "error creating pretty print xml. It's probably not an xml message: " + ex.getLocalizedMessage());
          }

          try {
               Document doc = DocumentHelper.parseText(xml.substring(xml.indexOf("<")));
               StringWriter sw = new StringWriter();
               OutputFormat format = OutputFormat.createPrettyPrint();
               XMLWriter xw = new XMLWriter(sw, format);
               xw.write(doc);
               return Utility.encodeHTML(sw.toString());

          } catch (Exception ex) {
               //LogHelper.getLog().log(Level.INFO, "error creating pretty print xml. It's probably not an xml message: " + ex.getLocalizedMessage());
          }
          LogHelper.getLog().log(Level.INFO, "error creating pretty print xml. It's probably not an xml message. No action is required.");
          return Utility.encodeHTML(xml);

     }

     /**
      * used on the index.jsp page of fgsms, finds a particular record
      * returned from fgsms.StatusService and returns the specific record.
      * 
      */
     public static GetStatusResponseMsg Findrecord(String url, List<GetStatusResponseMsg> list) {
          if (Utility.stringIsNullOrEmpty(url)) {
               return null;
          }
          if (list == null || list.isEmpty()) {
               return null;
          }
          for (int i = 0; i < list.size(); i++) {
               if (list.get(i).getURI().equalsIgnoreCase(url)) {
                    GetStatusResponseMsg t = list.get(i);
                    list.remove(i);
                    return t;
               }
          }
          return null;
     }

     /**
      * Use this function for loading properties files from web
      * applications 
      */
     public static Properties loadForJSP(URL name) throws IOException {

          InputStream in = name.openStream();
          Properties p = new Properties();
          p.load(in);
          in.close();
          return p;
     }

     /**
      *  returns xyr xmo xd xhr xmin xs representation of a duration
      * 
      */
     public static String DurationToString(Duration d) {
          if (d == null) {
               return "";
          }
          String s = "";
          if (d.getYears() > 0) {
               s += d.getYears() + "yr ";
          }
          if (d.getMonths() > 0) {
               s += d.getMonths() + "mo ";
          }
          if (d.getDays() > 0) {
               s += d.getDays() + "d ";
          }
          if (d.getHours() > 0) {
               s += d.getHours() + "hr ";
          }
          if (d.getMinutes() > 0) {
               s += d.getMinutes() + "min ";
          }
          if (d.getSeconds() > 0) {
               s += d.getSeconds() + "s ";
          }
          return s;
     }

     /**
      * returns a duration parsed from xyr xmo xd xhr xmin xs
      * 
      * first it will parse it as a xml representation then as a Long then the
      * specific format
      */
     public static Duration StringToDuration(String d) throws DatatypeConfigurationException {
          if (Utility.stringIsNullOrEmpty(d)) {
               return null;
          }
          String[] s = d.split(" ");
          if (s == null || s.length == 0) {
               return null;
          }
          DatatypeFactory fac = DatatypeFactory.newInstance();
          try {
               Duration dur = fac.newDuration(d);
               return dur;
          } catch (Exception ex) {
          }
          try {
               Duration dur = fac.newDuration(Long.parseLong(d));
               return dur;
          } catch (Exception ex) {
          }
          long durationInMilliSeconds = 0;
          String temp;
          long x = 0;
          for (int i = 0; i < s.length; i++) {
               if (s[i].contains("yr")) {
                    temp = s[i].replace("yr", "").trim();
                    x = Long.parseLong(temp);
                    durationInMilliSeconds += (x * 365 * 24 * 60 * 60 * 1000);
               } else if (s[i].contains("mo")) {
                    temp = s[i].replace("mo", "").trim();
                    x = Long.parseLong(temp);
                    durationInMilliSeconds += (x * 30 * 24 * 60 * 60 * 1000);
               } else if (s[i].contains("d")) {
                    temp = s[i].replace("d", "").trim();
                    x = Long.parseLong(temp);
                    durationInMilliSeconds += (x * 24 * 60 * 60 * 1000);
               } else if (s[i].contains("hr")) {
                    temp = s[i].replace("hr", "").trim();
                    x = Long.parseLong(temp);
                    durationInMilliSeconds += (x * 60 * 60 * 1000);
               } else if (s[i].contains("min")) {
                    temp = s[i].replace("min", "").trim();
                    x = Long.parseLong(temp);
                    durationInMilliSeconds += (x * 60 * 1000);
               } else if (s[i].contains("s")) {
                    temp = s[i].replace("s", "").trim();
                    x = Long.parseLong(temp);
                    durationInMilliSeconds += (x * 1000);
               }
          }
          Duration dur = fac.newDuration(durationInMilliSeconds);
          return dur;
     }

     /**
      *  returns the number of milliseconds from the following string
      * format: xyr xmo xd xhr xmin xs 
      */
     public static long StringToLongMS(String d) {
          if (Utility.stringIsNullOrEmpty(d)) {
               return 0;
          }
          String[] s = d.split(" ");
          if (s == null || s.length == 0) {
               return 0;
          }
          // DatatypeFactory fac = DatatypeFactory.newInstance();
          long durationInMilliSeconds = 0;
          String temp;
          for (int i = 0; i < s.length; i++) {
               if (s[i].contains("yr")) {
                    temp = s[i].replace("yr", "").trim();
                    durationInMilliSeconds += (Long.parseLong(temp) * 365 * 24 * 60 * 60 * 1000);
               } else if (s[i].contains("mo")) {
                    temp = s[i].replace("mo", "").trim();
                    durationInMilliSeconds += (Long.parseLong(temp) * 30 * 24 * 60 * 60 * 1000);
               } else if (s[i].contains("d")) {
                    temp = s[i].replace("d", "").trim();
                    durationInMilliSeconds += (Long.parseLong(temp) * 24 * 60 * 60 * 1000);
               } else if (s[i].contains("hr")) {
                    temp = s[i].replace("hr", "").trim();
                    durationInMilliSeconds += (Long.parseLong(temp) * 60 * 60 * 1000);
               } else if (s[i].contains("min")) {
                    temp = s[i].replace("min", "").trim();
                    durationInMilliSeconds += (Long.parseLong(temp) * 60 * 1000);
               } else if (s[i].contains("s")) {
                    temp = s[i].replace("s", "").trim();
                    durationInMilliSeconds += (Long.parseLong(temp) * 1000);
               }
          }
          return durationInMilliSeconds;
     }

     /**
      * report type to a friendly name for report types
      *
      * @param r
      * @return
      */
      public static String ToFriendlyName(String action, PCS pcs, SecurityWrapper c) {
          if (action == null) {
               return "Null classname";
          }

          GetPluginInformationRequestMsg req = new GetPluginInformationRequestMsg();
          req.setGetPluginInformationRequestWrapper(new GetPluginInformationRequestWrapper());
          req.getGetPluginInformationRequestWrapper().setClassification(c);
          req.getGetPluginInformationRequestWrapper().setPlugin(new Plugin());
          req.getGetPluginInformationRequestWrapper().getPlugin().setClassname(action);
          req.getGetPluginInformationRequestWrapper().getPlugin().setPlugintype("REPORTING");
          GetPluginInformation r = new GetPluginInformation();
          r.setRequest(req);

          try {
               GetPluginInformationResponse pluginInformation = pcs.getPluginInformation(r);

               return Utility.encodeHTML(pluginInformation.getResponse().getDisplayName());
          } catch (Exception ex) {
               LogHelper.getLog().error(ex);
          }
          return "Unrecognized report type " + Utility.encodeHTML(action);
     }

     /**
      * base 64 encoder 
      */
     public static String encode(String text) {
         try {
             return org.apache.commons.codec.binary.Base64.encodeBase64String(text.getBytes(Constants.CHARSET));
         } catch (UnsupportedEncodingException ex) {
             LogHelper.getLog().warn("unexpected base64 encoding error", ex);
         }
         return "";
     }

     public static String ExportServicePolicy(ServicePolicy pol) {
          try {
               JAXBContext jc = Utility.getSerializationContext();

               Marshaller m = jc.createMarshaller();
               StringWriter sw = new StringWriter();
//            org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory pcsfac = new org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory ();

               m.marshal((pol), sw);
               String s = sw.toString();
               return s;
          } catch (Exception ex) {
               Logger.getLogger(Helper.class).log(Level.ERROR, null, ex);
          }
          return null;
     }

     public static ServicePolicy ImportServicePolicy(String pol, PolicyType pt) {
          try {
               JAXBContext jc = Utility.getSerializationContext();

               Unmarshaller u = jc.createUnmarshaller();

               ByteArrayInputStream bss = new ByteArrayInputStream(pol.getBytes(Constants.CHARSET));
               //1 = reader
               //2 = writer
               XMLInputFactory xf = XMLInputFactory.newInstance();
               XMLStreamReader r = xf.createXMLStreamReader(bss);
               
               //        com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl r = new com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl(bss, new com.sun.org.apache.xerces.internal.impl.PropertyManager(1));

               switch (pt) {
                    case MACHINE:
                         JAXBElement<MachinePolicy> foo = (JAXBElement<MachinePolicy>) u.unmarshal(r, MachinePolicy.class);
                         bss.close();
                         if (foo != null && foo.getValue() != null) {
                              return foo.getValue();
                         }
                         break;
                    case PROCESS:
                         JAXBElement<ProcessPolicy> foo2 = (JAXBElement<ProcessPolicy>) u.unmarshal(r, ProcessPolicy.class);
                         bss.close();
                         if (foo2 != null && foo2.getValue() != null) {
                              return foo2.getValue();
                         }
                         break;
                    case STATISTICAL:
                         JAXBElement<StatisticalServicePolicy> foo3 = (JAXBElement<StatisticalServicePolicy>) u.unmarshal(r, StatisticalServicePolicy.class);
                         bss.close();
                         if (foo3 != null && foo3.getValue() != null) {
                              return foo3.getValue();
                         }
                         break;
                    case STATUS:
                         JAXBElement<StatusServicePolicy> foo4 = (JAXBElement<StatusServicePolicy>) u.unmarshal(r, StatusServicePolicy.class);
                         bss.close();
                         if (foo4 != null && foo4.getValue() != null) {
                              return foo4.getValue();
                         }
                         break;
                    case TRANSACTIONAL:
                         JAXBElement<TransactionalWebServicePolicy> foo5 = (JAXBElement<TransactionalWebServicePolicy>) u.unmarshal(r, TransactionalWebServicePolicy.class);
                         bss.close();
                         if (foo5 != null && foo5.getValue() != null) {
                              return foo5.getValue();
                         }
                         break;
               }
               bss.close();
               Logger.getLogger(Helper.class).log(Level.WARN, "ServicePolicy is unexpectedly null or empty");
               return null;
          } catch (Exception ex) {
               Logger.getLogger(Helper.class).log(Level.ERROR, null, ex);
          }
          return null;

     }

     /**
      * used on manage.jsp to render SLA info in the tab'd box
      *
      * @param item
      * @return
      */
     public static String BuildSLASingleAction(SLAAction item, PCS pcs, SecurityWrapper c) {
          String s = "";
          GetPluginInformationResponseMsg GetPluginInfo = GetPluginInfo(item.getImplementingClassName(), "SLA_ACTION", pcs, c);
          if (GetPluginInfo == null) {
               return "Unknown SLA Action";
          }
          return GetPluginInfo.getDisplayName();

     }

     public static FederationPolicy BuildFederation(FederationPolicy src, PCS pcs, SecurityWrapper c, HttpServletRequest req) {
          Enumeration<String> parameterNames = req.getParameterNames();
          src.getParameterNameValue().clear();
          while (parameterNames.hasMoreElements()) {
               String t = parameterNames.nextElement();
               if (t.startsWith("plugin_")) {
                    boolean encryptOnSave = false;
                    String b = req.getParameter("p_enc_" + t.substring(7));
                    boolean enc = false;
                    if (b != null) {
                         try {
                              enc = Boolean.parseBoolean(b);
                         } catch (Exception ex) {
                         }
                    }
                    b = req.getParameter("fed_parameter_enc_" + t.substring(7));
                    if (b != null) {
                         try {
                              encryptOnSave = Boolean.parseBoolean(b);
                         } catch (Exception ex) {
                         }
                    }
                    if (!Utility.stringIsNullOrEmpty(req.getParameter(t))) {
                         src.getParameterNameValue().add(Utility.newNameValuePair(t.substring(7), req.getParameter(t), encryptOnSave, enc));
                    }
               }
          }

          return src;
     }

     /**
      * renders an action set as a user readable html string, occurs when
      * editing existing slas
      *
      * @param items
      * @return
      */
     public static String BuildSLAAction(List<SLAAction> items, PCS pcs, SecurityWrapper c) {
          String s = "";
          for (int i = 0; i < items.size(); i++) {
               if (items.get(i) instanceof SLAAction) {
                    GetPluginInformationResponseMsg GetPluginInfo = GetPluginInfo(items.get(i).getImplementingClassName(), "SLA_ACTION", pcs, c);
                    if (GetPluginInfo == null) {
                         s += "Unknown SLA Action<br>";
                    } else {
                         s += GetPluginInfo.getDisplayName() + "<br>";
                    }

               }
          }

          return s;
     }

     public static String RuleToString(RuleBaseType rule) {
          return rule.getClass().getSimpleName();
     }

     /**
      * Converts a rule to a friendly human readable name
      *
      * @param rule
      * @return
      */
     public static String ToFriendylName(RuleBaseType rule, PCS pcs, SecurityWrapper c) {
          if (rule == null) {
               return "";
          }
          if (rule instanceof AndOrNot) {
               AndOrNot x = (AndOrNot) rule;
               if (x.getFlag() == JoiningType.AND) {
                    return "(" + ToFriendylName(x.getLHS(), pcs, c) + " and " + ToFriendylName(x.getRHS(), pcs, c) + ")";
               }
               if (x.getFlag() == JoiningType.OR) {
                    return "(" + ToFriendylName(x.getLHS(), pcs, c) + " or " + ToFriendylName(x.getRHS(), pcs, c) + ")";
               }
               if (x.getFlag() == JoiningType.NOT) {
                    return "not " + ToFriendylName(x.getLHS(), pcs, c);
               }
          } else if (rule instanceof SLARuleGeneric) {
               SLARuleGeneric r = (SLARuleGeneric) rule;
               GetPluginInformationResponseMsg GetPluginInfo = GetPluginInfo(r.getClassName(), "SLA_RULE", pcs, c);
               if (GetPluginInfo == null) {
                    return "Unknown SLA Rule<br>";
               } else {
                    return GetPluginInfo.getDisplayName() + "<br>";
               }

          }

          return "Unrecongized rule " + Utility.encodeHTML(rule.getClass().getName());
     }

     /**
      * Converts a rule instance to a friendly human readable string with
      * parameter values
      *
      * @param rule
      * @return
      */
     public static String BuildSLARuleData(RuleBaseType rule, List<Plugin> rule_plugins) {

          if (rule == null) {
               return "";
          }
          if (rule instanceof AndOrNot) {
               AndOrNot x = (AndOrNot) rule;
               if (x.getFlag() == JoiningType.AND) {
                    return "(" + BuildSLARuleData(x.getLHS(), rule_plugins) + " and " + BuildSLARuleData(x.getRHS(), rule_plugins) + ")";
               }
               if (x.getFlag() == JoiningType.OR) {
                    return "(" + BuildSLARuleData(x.getLHS(), rule_plugins) + " or " + BuildSLARuleData(x.getRHS(), rule_plugins) + ")";
               }
               if (x.getFlag() == JoiningType.NOT) {
                    return "not " + BuildSLARuleData(x.getLHS(), rule_plugins);
               }
          }

          if (rule instanceof SLARuleGeneric) {
               SLARuleGeneric x = (SLARuleGeneric) rule;
               for (int i = 0; i < rule_plugins.size(); i++) {
                    if (rule_plugins.get(i).getClassname().equalsIgnoreCase(x.getClassName())) {
                         return rule_plugins.get(i).getDisplayname();
                    }
               }
               return "Plugin: " + Utility.encodeHTML(x.getClassName()) + " ";
          }
          return "Unrecongized rule " + rule.getClass().getName();

     }
     private static final String SLAPARAM_RULE_CLASS = "Class Name <input type=text  style=\"width: 250px; height:25px; \"  name=\"slaruleclass\" value=\"";
     private static final String SLAPARAM = "Parameter <input type=text  style=\"width: 250px; height:25px; \"  name=\"parameter\" value=\"";
     private static final String SLAPARAMPARTITION = "Partition <input type=text  style=\"width: 250px; height:25px; \"  name=\"parameter2\" value=\"";
     private static final String SLATIMEPARAM = "Time <input type=text  style=\"width: 250px; height:25px; \"  name=\"slatimerange\" value=\"";

     /**
      * Renders an SLA Rule for editing via browser
      *
      * @param rule
      * @return T
      */
     public static String RenderSLAParametersForEditing(RuleBaseType rule) {
          try {
               if (rule == null) {
                    return "Null rule!";
               }
               if (rule instanceof AndOrNot) {
                    AndOrNot x = (AndOrNot) rule;
                    if (x.getFlag() == JoiningType.AND) {
                         return "(" + RenderSLAParametersForEditing(x.getLHS()) + " and " + RenderSLAParametersForEditing(x.getRHS()) + ")";
                    }
                    if (x.getFlag() == JoiningType.OR) {
                         return "(" + RenderSLAParametersForEditing(x.getLHS()) + " or " + RenderSLAParametersForEditing(x.getRHS()) + ")";
                    }
                    if (x.getFlag() == JoiningType.NOT) {
                         return "not " + RenderSLAParametersForEditing(x.getLHS());
                    }
               }

               if (rule instanceof SLARuleGeneric) {
                    SLARuleGeneric x
                         = (SLARuleGeneric) rule;
                    return SLAPARAM_RULE_CLASS + Utility.encodeHTML(x.getClassName()) + "\"><br>Process at: " + RenderRuleRunAt(x.getProcessAt());
               }
               return "Unrecongized rule " + rule.getClass().getName();

          } catch (Exception ex) {
               Logger.getLogger(Helper.class).log(Level.ERROR, null, ex);
          }
          return "";
     }

     private static String ListToString(ArrayOfXMLNamespacePrefixies namespaces) {
          if (namespaces == null) {
               return "none";
          }
          if (namespaces.getXMLNamespacePrefixies().isEmpty()) {
               return "none";
          }
          StringBuilder ret = new StringBuilder();
          for (int i = 0; i < namespaces.getXMLNamespacePrefixies().size(); i++) {
               ret = ret.append(Utility.encodeHTML(namespaces.getXMLNamespacePrefixies().get(i).getPrefix())).append(":").append(Utility.encodeHTML(namespaces.getXMLNamespacePrefixies().get(i).getNamespace())).append(" ");
          }
          return ret.toString().trim();
     }

     private static String AggTimeRange(Duration currentvalue) {
          if (currentvalue == null) {
               return "<select name=\"slatimerange\">"
                    + "<option value=5min>5 minutes</option>"
                    + "<option value=15min>15 minutes</option>"
                    + "<option value=60min>60 minutes</option>"
                    + "<option value=24hr>24 hours</option></select>";
          }
          long l = Utility.durationToTimeInMS(currentvalue);
          if (l == 5 * 60 * 1000) {
               return "<select name=\"slatimerange\">"
                    + "<option value=5min selected>5 minutes</option>"
                    + "<option value=15min>15 minutes</option>"
                    + "<option value=60min>60 minutes</option>"
                    + "<option value=24hr>24 hours</option></select>";
          }
          if (l == 15 * 60 * 1000) {
               return "<select name=\"slatimerange\">"
                    + "<option value=5min >5 minutes</option>"
                    + "<option value=15min selected>15 minutes</option>"
                    + "<option value=60min>60 minutes</option>"
                    + "<option value=24hr>24 hours</option></select>";
          }
          if (l == 60 * 60 * 1000) {
               return "<select name=\"slatimerange\">"
                    + "<option value=5min >5 minutes</option>"
                    + "<option value=15min >15 minutes</option>"
                    + "<option value=60min selected>60 minutes</option>"
                    + "<option value=24hr>24 hours</option></select>";
          }
          if (l == Long.parseLong("86400000")) {
               return "<select name=\"slatimerange\">"
                    + "<option value=5min >5 minutes</option>"
                    + "<option value=15min >15 minutes</option>"
                    + "<option value=60min >60 minutes</option>"
                    + "<option value=24hr selected>24 hours</option></select>";
          }
          return "<select name=\"slatimerange\">"
               + "<option value=5min>5 minutes</option>"
               + "<option value=15min>15 minutes</option>"
               + "<option value=60min>60 minutes</option>"
               + "<option value=24hr>24 hours</option></select>";
     }
     static org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory fac = new ObjectFactory();

     /**
      * constructs a rule from a postback
      *
      * @param request
      * @return
      */
     public static RuleBaseType BuildRule(HttpServletRequest request, PCS pcs, SecurityWrapper c) {
          RuleBaseType rule = null;
          try {

               //this is the classname of the plugin
               String s = request.getParameter("rule1");

               SLARuleGeneric r = new SLARuleGeneric();
               try {
                    if (Utility.stringIsNullOrEmpty(s)) {
                         return null;
                    }
                    r.setClassName(s);

                    GetPluginInformationResponseMsg info = GetPluginInfo(s, "SLA_RULE", pcs, c);
                    if (info != null) {
                         List<NameValuePair> requiredParameter = info.getRequiredParameter();
                         //this is the authoritative list of parameters
                         boolean requiredparams = true;
                         for (NameValuePair pair : requiredParameter) {
                              if (request.getParameter("plugin_" + pair.getName()) == null) {
                                   return null;
                              }
                              r.getParameterNameValue().add(new NameValuePair(pair.getName(),
                                   request.getParameter("plugin_" + pair.getName()), false, "checked".equalsIgnoreCase(request.getParameter("p_enc_" + pair.getName()))));

                         }

                         requiredParameter = info.getOptionalParameter();;
                         for (NameValuePair pair : requiredParameter) {

                              if (request.getParameter("plugin_" + pair.getName()) != null) {
                                   r.getParameterNameValue().add(new NameValuePair(pair.getName(),
                                        request.getParameter("plugin_" + pair.getName()), false, "checked".equalsIgnoreCase(request.getParameter("p_enc_" + pair.getName()))));
                              }
                         }
                    }

                    try {
                         r.setProcessAt(RunAtLocation.valueOf(request.getParameter("slagenericrulerunat")));
                    } catch (Exception e) {
                         LogHelper.getLog().log(Level.INFO, "error building SLA Rule, unparsable value for RunAtLocation ", e);
                         r.setProcessAt(RunAtLocation.FGSMS_SERVER);
                    }
                    rule = r;
               } catch (Exception ex) {
                    LogHelper.getLog().log(Level.INFO, "error building SLA Rule", ex);
               }

          } catch (Exception ex) {
               LogHelper.getLog().log(Level.INFO, "error building SLA Rule", ex);
          }
          return rule;
     }
     public static final String ENCRYPTONSAVE = "ENCRYPTONSAVE";
     public static final String ISENCRYPTED = "ISENCRYPTED";
     public static final String PARAMETER_PREFIX = "PARAM-";

     /**
      * from manage.jsp and scheduledEditPost.jsp, occurs when a new sla is
      * added to a policy expects "Action" = plugin classname
      *
      * @param request
      * @param pcs
      * @return
      */
     public static SLAAction BuildSLAAction(HttpServletRequest request, PCS pcs, SecurityWrapper c) {
          SLAAction action = null;

          try {
               String s = request.getParameter("Action");
               //fix for automated reporting service
               if (s == null || s.equalsIgnoreCase("null") || s.equalsIgnoreCase("none")) {
                    return null;
               }
               action = new SLAAction();
               action.setImplementingClassName(s);
               GetPluginInformationResponseMsg plugin = GetPluginInfo(s, "SLA_ACTION", pcs, c);
               for (int i = 0; i < plugin.getRequiredParameter().size(); i++) {
                    NameValuePair nvp = new NameValuePair();
                    nvp.setName(plugin.getRequiredParameter().get(i).getName());
                    nvp.setValue(request.getParameter("plugin_" + plugin.getRequiredParameter().get(i).getName()));
                    nvp.setEncryptOnSave(Boolean.parseBoolean(request.getParameter("p_enc_" + plugin.getRequiredParameter().get(i).getName() + ENCRYPTONSAVE)));
                    nvp.setEncryptOnSave(Boolean.parseBoolean(request.getParameter(PARAMETER_PREFIX + plugin.getRequiredParameter().get(i).getName() + ISENCRYPTED)));
                    action.getParameterNameValue().add(nvp);
               }
               for (int i = 0; i < plugin.getOptionalParameter().size(); i++) {
                    NameValuePair nvp = new NameValuePair();
                    nvp.setName(plugin.getOptionalParameter().get(i).getName());
                    nvp.setValue(request.getParameter(plugin.getOptionalParameter().get(i).getName()));
                    if (nvp.getValue() != null) {
                         if (request.getParameter(PARAMETER_PREFIX + plugin.getOptionalParameter().get(i).getName() + ENCRYPTONSAVE) != null) {
                              nvp.setEncryptOnSave(Boolean.parseBoolean(request.getParameter(PARAMETER_PREFIX + plugin.getOptionalParameter().get(i).getName() + ENCRYPTONSAVE)));
                         }
                         if (request.getParameter(PARAMETER_PREFIX + plugin.getOptionalParameter().get(i).getName() + ISENCRYPTED) != null) {
                              nvp.setEncryptOnSave(Boolean.parseBoolean(request.getParameter(PARAMETER_PREFIX + plugin.getOptionalParameter().get(i).getName() + ISENCRYPTED)));
                         }
                         action.getParameterNameValue().add(nvp);
                    }
               }

          } catch (Exception ex) {
               LogHelper.getLog().log(Level.INFO, "error building SLA ACtion", ex);
          }
          return action;
     }

     public static SecurityWrapper GetCurrentSecurityLevel(ProxyLoader pl, ServletContext app, HttpServletRequest request, HttpServletResponse response) throws Exception {
          if (pl == null) {
               pl = ProxyLoader.getInstance(app);
          }
          PCS pcs = pl.GetPCS(app, request, response);
          GetGlobalPolicyResponseMsg globalPolicy = pcs.getGlobalPolicy(new GetGlobalPolicyRequestMsg());
          return globalPolicy.getClassification();

     }

     public static boolean RuleHasParameters(RuleBaseType rule, PCS pcs, SecurityWrapper c) {

          if (rule instanceof AndOrNot) {
               return true;
          }
          if (rule instanceof SLARuleGeneric) {
               SLARuleGeneric x = (SLARuleGeneric) rule;
               GetPluginInformationResponseMsg info = GetPluginInfo(x.getClassName(), "SLA_RULE", pcs, c);
               if (info != null) {
                    return !info.getRequiredParameter().isEmpty() && !info.getOptionalParameter().isEmpty();
               }
          }
          return false;
     }
     static Map pluginCache = new HashMap();

     /**
      *
      * @param clazz
      * @param type FEDERATION_PUBLISH,SLA_RULE,SLA_ACTION
      * @param pcs
      * @param c
      * @return
      */
     public static GetPluginInformationResponseMsg GetPluginInfo(String clazz, String type, PCS pcs, SecurityWrapper c) {
          if (pluginCache.containsKey(clazz + type)) {
               GetPluginInformationExt item = (GetPluginInformationExt) pluginCache.get(clazz + type);
               //TODO make this configurable
               if (System.currentTimeMillis() - item.RefreshedAt < 300000) {
                    return item;
               }
               pluginCache.remove(clazz + type);

          }
          GetPluginInformationRequestMsg req = new GetPluginInformationRequestMsg();
          req.setGetPluginInformationRequestWrapper(new GetPluginInformationRequestWrapper());
          req.getGetPluginInformationRequestWrapper().setClassification(c);
          req.getGetPluginInformationRequestWrapper().setPlugin(new Plugin());
          //req.getGetPluginInformationRequestWrapper().setOptionalPolicyTypeFilter(pt);
          req.getGetPluginInformationRequestWrapper().getPlugin().setClassname(clazz);
          req.getGetPluginInformationRequestWrapper().getPlugin().setPlugintype(type);

          GetPluginInformation r = new GetPluginInformation();
          r.setRequest(req);
          try {
               GetPluginInformationResponse pluginInformation = pcs.getPluginInformation(r);

               return pluginInformation.getResponse();
          } catch (Exception ex) {
               LogHelper.getLog().error(ex);
          }
          return null;
     }

     /**
      *
      * @param action
      * @param pcs
      * @param c
      * @param item_type FEDERATION_PUBLISH,SLA_RULE,SLA_ACTION
      * @return
      */
     public static String GetPluginHelp(SLAAction action, PCS pcs, SecurityWrapper c, String item_type) {
          if (action == null) {
               return "Null Action";
          }

          GetPluginInformationRequestMsg req = new GetPluginInformationRequestMsg();
          req.setGetPluginInformationRequestWrapper(new GetPluginInformationRequestWrapper());
          req.getGetPluginInformationRequestWrapper().setClassification(c);
          req.getGetPluginInformationRequestWrapper().setPlugin(new Plugin());
          req.getGetPluginInformationRequestWrapper().getPlugin().setClassname(action.getImplementingClassName());
          req.getGetPluginInformationRequestWrapper().getPlugin().setPlugintype(item_type);
          GetPluginInformation r = new GetPluginInformation();
          r.setRequest(req);

          try {
               GetPluginInformationResponse pluginInformation = pcs.getPluginInformation(r);
               return pluginInformation.getResponse().getHelp();
          } catch (Exception ex) {
               LogHelper.getLog().error(ex);
          }
          return "No help information could be found.";
     }

     /**
      *
      * @param pcs
      * @param c
      * @param item_type FEDERATION_PUBLISH,SLA_RULE,SLA_ACTION
      * @return
      */
     public static List<Plugin> GetPluginList(PCS pcs, SecurityWrapper c, String item_type) {
          return GetPluginList(pcs, c, item_type, null);
     }

     public static List<Plugin> GetPluginList(PCS pcs, SecurityWrapper c, String item_type, PolicyType pt) {
          if (item_type == null) {
               return Collections.EMPTY_LIST;
          }

          GetPluginListRequestMsg req = new GetPluginListRequestMsg();
          req.setClassification(c);
          req.setPlugintype(item_type);
          req.setOptionalPolicyTypeFilter(pt);
          GetPluginList r = new GetPluginList();
          r.setRequest(req);

          try {
               GetPluginListResponse pluginList = pcs.getPluginList(r);
               return pluginList.getResponse().getPlugins();
          } catch (Exception ex) {
               LogHelper.getLog().error(ex);
          }
          return Collections.EMPTY_LIST;
     }

     /**
      *
      * @param action
      * @param pcs
      * @param c
      * @param item_type FEDERATION_PUBLISH,SLA_RULE,SLA_ACTION
      * @return html escaped friendly name of the plugin
      */
     public static String ToFriendlyName(SLAAction action, PCS pcs, SecurityWrapper c) {
          if (action == null) {
               return "Null Action";
          }

          GetPluginInformationRequestMsg req = new GetPluginInformationRequestMsg();
          req.setGetPluginInformationRequestWrapper(new GetPluginInformationRequestWrapper());
          req.getGetPluginInformationRequestWrapper().setClassification(c);
          req.getGetPluginInformationRequestWrapper().setPlugin(new Plugin());
          req.getGetPluginInformationRequestWrapper().getPlugin().setClassname(action.getImplementingClassName());
          req.getGetPluginInformationRequestWrapper().getPlugin().setPlugintype("SLA_ACTION");
          GetPluginInformation r = new GetPluginInformation();
          r.setRequest(req);

          try {
               GetPluginInformationResponse pluginInformation = pcs.getPluginInformation(r);

               return Utility.encodeHTML(pluginInformation.getResponse().getDisplayName());
          } catch (Exception ex) {
               LogHelper.getLog().error(ex);
          }
          return "Unrecognized action, " + Utility.encodeHTML(action.getImplementingClassName());
     }

     private static String XpathNamespacesToString(ArrayOfXMLNamespacePrefixies namespaces) {
          if (namespaces == null) {
               return "";
          }
          if (namespaces.getXMLNamespacePrefixies().isEmpty()) {
               return "";
          }
          //prefix##namespace|prefix2##namespace2
          StringBuilder ret = new StringBuilder();
          for (int i = 0; i < namespaces.getXMLNamespacePrefixies().size(); i++) {
               ret = ret.append(Utility.encodeHTML(namespaces.getXMLNamespacePrefixies().get(i).getPrefix())).append("##").append(Utility.encodeHTML(namespaces.getXMLNamespacePrefixies().get(i).getNamespace())).append("|");
          }
          return ret.toString().substring(1, ret.length() - 1);

     }

     public static int GetHalfScreenWidth(Cookie[] cookies) {
          for (int i = 0; i < cookies.length; i++) {

               if (cookies[i].getName().equalsIgnoreCase("screenwidth")) {
                    try {
                         int width = Integer.parseInt(cookies[i].getValue());
                         if (width > 2) {
                              return width / 2;
                         }
                    } catch (Exception ex) {
                    }
               }

          }
          return 320;
     }

     public static String ShowOpStat(SecurityWrapper c, org.miloss.fgsms.services.interfaces.status.OpStatusService ss, String url) {
          StringBuilder out = new StringBuilder();
          String[] urls = url.split("|");
          for (int i = 0; i < urls.length; i++) {
               try {

                    BindingProvider bp = (BindingProvider) ss;
                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urls[i]);

                    GetOperatingStatusRequestMessage req = new GetOperatingStatusRequestMessage();
                    req.setClassification(c);
                    GetOperatingStatusResponseMessage res = ss.getOperatingStatus(req);
                    if (res.isStatus()) {
                         out.append("<h2>OK</h2>");

                    } else {
                         out.append("<h2>BAD</h2>");
                    }
                    out.append("Started at: ").append(res.getStartedAt().toString()).append("<br>");
                    out.append("Status Message:").append(Utility.encodeHTML(res.getStatusMessage())).append("<br>");
                    out.append("Version Data: ").append(Utility.encodeHTML(res.getVersionInfo().getVersionData())).append("<br>");
                    out.append("Version Source: ").append(Utility.encodeHTML(res.getVersionInfo().getVersionSource())).append("<br>");
                    out.append("Data Sent Failure: ").append(res.getDataNotSentSuccessfully()).append("<br>");
                    out.append("Data Sent Success: ").append(res.getDataSentSuccessfully()).append("<br>");
               } catch (Exception ex) {
                    out.append("Error caught checking stats on ").append(urls[i]).append(" ").append(ex.getMessage());
               }
          }
          return out.toString();
     }

     public static String PluginToReadonlyHtmlString(FederationPolicy fp, int index, HttpServletRequest request) {
          StringBuilder out = new StringBuilder();
          out.append("<table border=1><tr><th>Key</th><th>Value</th></tr>");

          out.append("<tr><td >Policy Id " + (index) + "</td><td>");
          out.append("<input type=button value=\"Edit\" name=\"EditFedPol" + index + "\" "
               + " onclick=\"javascript:postBackReRender('EditFedPol" + index
               + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">"
               + "<input type=button value=Remove name=\"RemoveFedPol" + index + "\" onclick=\"javascript:postBackReRender('RemoveFedPol" + index
               + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\"></td></tr>");

          out.append("<tr><td>Plugin</td><td>" + Utility.encodeHTML(fp.getImplementingClassName()) + "</td></tr>");

          for (int i = 0; i < fp.getParameterNameValue().size(); i++) {
               out.append("<tr><td>").append(Utility.encodeHTML(fp.getParameterNameValue().get(i).getName())).append("</td><td>");
               if (fp.getParameterNameValue().get(i).isEncrypted()) {
                    out.append("ENCRYPTED");
               } else {
                    out.append(Utility.encodeHTML(fp.getParameterNameValue().get(i).getValue()));
               }
               out.append("</td></tr>");

          }

          out.append("</td></tr>");

          //end federation tab
          out.append("</table>");
          return out.toString();
     }

     public static String federationPluginEditableHtml(FederationPolicy fp, int index, HttpServletRequest request) {
          StringBuilder out = new StringBuilder();
          out.append("<table class=\"table table-hover\"><tr><th>Key</th><th>Value</th></tr>");

          out.append("<tr><td >Policy Id " + (index) + "</td><td>");
          out.append("<input type=button value=\"Edit\" name=\"EditFedPol" + index + "\" "
               + " onclick=\"javascript:postBackReRender('EditFedPol" + index
               + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\">"
               + "<input type=button value=Remove name=\"RemoveFedPol" + index + "\" onclick=\"javascript:postBackReRender('RemoveFedPol" + index
               + "','profile/getPolicy.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "','tab1');\"></td></tr>");

          out.append("<tr><td>Plugin</td><td>" + Utility.encodeHTML(fp.getImplementingClassName()) + "</td></tr>");

          for (int i = 0; i < fp.getParameterNameValue().size(); i++) {
               out.append("<tr><td>").append(Utility.encodeHTML(fp.getParameterNameValue().get(i).getName())).append("</td><td>");
               if (fp.getParameterNameValue().get(i).isEncrypted()) {
                    out.append("ENCRYPTED");
               } else {
                    out.append(Utility.encodeHTML(fp.getParameterNameValue().get(i).getValue()));
               }
               out.append("</td></tr>");

          }

          out.append("</td></tr>");

          //end federation tab
          out.append("</table>");
          return out.toString();
     }

     public static String GetPluginParameterValue(List<NameValuePair> params, String parameterName) {
          if (params == null) {
               return "";
          }
          for (int i = 0; i < params.size(); i++) {
               if (params.get(i).getName().equals(parameterName)) {
                    return params.get(i).getValue();
               }
          }
          return "";
     }

     /**
      * returns "checked=checked" if the selected parameter exists and it's
      * already encrypted
      *
      * @param params
      * @param parameterName
      * @return
      */
     public static String GetPluginParameterIsEncrypted(List<NameValuePair> params, String parameterName) {
          if (params == null) {
               return "";
          }
          for (int i = 0; i < params.size(); i++) {
               if (params.get(i).getName().equals(parameterName) && params.get(i).isEncrypted()) {
                    return "checked=checked";
               }
          }
          return "";
     }

     /**
      * this function is the sausage maker
      *
      * @param request
      * @param pol
      * @return
      * @throws Exception
      */
     public static ServicePolicy buildFromHttpPost(HttpServletRequest request, ServicePolicy pol) throws Exception {

           Enumeration em = request.getParameterNames();

          if (DEBUG) {
               System.out.println("*********************************************************");
               System.out.println("*********************************************************");
               System.out.println("*********************************************************");

               while (em.hasMoreElements()) {
                    String key = (String) em.nextElement();
                    System.out.println(" params.put(\"" + key + "\", \"" + request.getParameter(key) + "\");");
               }
               System.out.println("*********************************************************");
               System.out.println("*********************************************************");
               System.out.println("*********************************************************");
          }
          
          //ok so of all the input parameters, it should be enough to completely reconstruct the service policy.
          Integer messagecap = 1024000;
          try {
               messagecap = Integer.parseInt(request.getParameter("messagecap"));
               if (messagecap < 1024) {
                    messagecap = 1024;
               }
          } catch (Exception ex) {
          }

          if (!Utility.stringIsNullOrEmpty(request.getParameter("datattl"))) {
               pol.setDataTTL(Helper.StringToDuration(request.getParameter("datattl")));
          }
          if (!Utility.stringIsNullOrEmpty(request.getParameter("refreshrate"))) {
               pol.setPolicyRefreshRate(Helper.StringToDuration(request.getParameter("refreshrate")));
          }

          if (pol instanceof ProcessPolicy) {
               ProcessPolicy mp = (ProcessPolicy) pol;
               mp.setAlsoKnownAs(request.getParameter("processaka"));
               //

               if (!Utility.stringIsNullOrEmpty(request.getParameter("recordopenfiles"))) {
                    mp.setRecordOpenFileHandles(true);
               } else {
                    mp.setRecordOpenFileHandles(false);
               }

               if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcpuusage"))) {
                    mp.setRecordCPUusage(true);
               } else {
                    mp.setRecordCPUusage(false);
               }

               if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcmemusage"))) {
                    mp.setRecordMemoryUsage(true);
               } else {
                    mp.setRecordMemoryUsage(false);
               }

          }

          if (pol instanceof MachinePolicy) {
               MachinePolicy mp = (MachinePolicy) pol;

//recorddiskusage
//recordnetworkusage
               if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcpuusage"))) {
                    mp.setRecordCPUusage(true);
               } else {
                    mp.setRecordCPUusage(false);
               }

               if (!Utility.stringIsNullOrEmpty(request.getParameter("recordcmemusage"))) {
                    mp.setRecordMemoryUsage(true);
               } else {
                    mp.setRecordMemoryUsage(false);
               }

               mp.getRecordDiskUsagePartitionNames().clear();
               if ((request.getParameterValues("recorddiskusage") != null)) {
                    String[] t = request.getParameterValues("recorddiskusage");
                    for (int k = 0; k < t.length; k++) {
                         if (!Utility.stringIsNullOrEmpty(t[k])) {
                              mp.getRecordDiskUsagePartitionNames().add(t[k]);
                         }
                    }
               }

               mp.getRecordNetworkUsage().clear();
               if ((request.getParameterValues("recordnetworkusage") != null)) {
                    String[] t = request.getParameterValues("recordnetworkusage");
                    for (int k = 0; k < t.length; k++) {
                         if (!Utility.stringIsNullOrEmpty(t[k])) {
                              mp.getRecordNetworkUsage().add(t[k]);
                         }
                    }
               }

               mp.getRecordDiskSpace().clear();
               if ((request.getParameterValues("recorddrivespace") != null)) {
                    String[] t = request.getParameterValues("recorddrivespace");
                    for (int k = 0; k < t.length; k++) {
                         if (!Utility.stringIsNullOrEmpty(t[k])) {
                              mp.getRecordDiskSpace().add(t[k]);
                         }
                    }
               }
          }

          if (pol instanceof TransactionalWebServicePolicy) {
               TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
               if (!Utility.stringIsNullOrEmpty(request.getParameter("messagecap"))) {
                    try {
                         int cap = Integer.parseInt(request.getParameter("messagecap"));
                         tp.setRecordedMessageCap(cap);
                    } catch (Exception ex) {
                    }
               }
               tp.setRecordedMessageCap(messagecap);
               if (Utility.stringIsNullOrEmpty(request.getParameter("recordrequest"))) {
                    tp.setRecordRequestMessage(false);
               } else {
                    tp.setRecordRequestMessage(true);
               }
               if (Utility.stringIsNullOrEmpty(request.getParameter("recordresponse"))) {
                    tp.setRecordResponseMessage(false);
               } else {
                    tp.setRecordResponseMessage(true);
               }
               if (Utility.stringIsNullOrEmpty(request.getParameter("recordfault"))) {
                    tp.setRecordFaultsOnly(false);
               } else {
                    tp.setRecordFaultsOnly(true);
               }

               if (Utility.stringIsNullOrEmpty(request.getParameter("buellerenabled"))) {
                    tp.setBuellerEnabled(false);
               } else {
                    tp.setBuellerEnabled(true);
               }

               tp.setHealthStatusEnabled(false);

               if (Utility.stringIsNullOrEmpty(request.getParameter("recordheaders"))) {
                    tp.setRecordHeaders(false);
               } else {
                    tp.setRecordHeaders(true);
               }

               List<UserIdentity> userident = new ArrayList<UserIdentity>();
               //TODO consumer identity methods
               //1 of 4 options, nothing, httpcred, httpheader, xpath
               em = request.getParameterNames();
               while (em.hasMoreElements()) {
                    String key = (String) em.nextElement();
                    if (key != null && key.startsWith("uid_")) {
                         String[] bits = key.split("_");
                         //bits[0]=header
                         //bits[1]= uuid
                         //bits[2]=field
                         if (bits.length == 3) {
                              if (bits[2].equals("method")) {
                                   String val = request.getParameter(key);
                                   if ("httpcred".equals(val)) {
                                        UserIdentity ui = new UserIdentity();
                                        ui.setUseHttpCredential(true);
                                        userident.add(ui);
                                   } else if ("httpheader".equals(val)) {
                                        UserIdentity ui = new UserIdentity();
                                        ui.setUseHttpHeader(true);
                                        ui.setHttpHeaderName(request.getParameter(bits[0] + "_" + bits[1] + "_httpheadername"));
                                        userident.add(ui);
                                   } else if ("xpath".equals(val)) {
                                        //ugh
                                        UserIdentity ui = new UserIdentity();
                                        ui.setXPaths(new ArrayOfXPathExpressionType());
                                        //two potentials with even odds
                                        //storing an existing xpath setup
                                        //storing a new xpath setup

                                        //check for new xpath expressions
                                        if (request.getParameterMap().containsKey((bits[0] + "_" + bits[1] + "_xpathexpression")))
                                        {
                                             XPathExpressionType xpath = new XPathExpressionType();
                                             xpath.setXPath(request.getParameter(bits[0] + "_" + bits[1] + "_xpathexpression"));
                                             xpath.setIsCertificate("on".equalsIgnoreCase(request.getParameter(bits[0] + "_" + bits[1] + "_x509certxpath")));

                                             //do new xml namespaces
                                             String cluster = request.getParameter(bits[0] + "_" + bits[1] + "_xpathprefixes");
                                             if (cluster != null && cluster.length() > 0) {
                                                  ArrayOfXMLNamespacePrefixies prefixes = new ArrayOfXMLNamespacePrefixies();

                                                  String[] groups = cluster.split("\\|");
                                                  for (int i = 0; i < groups.length; i++) {
                                                       String[] pairs = groups[i].split("\\#\\#");
                                                       if (pairs != null && pairs.length == 2) {

                                                            XMLNamespacePrefixies xml = new XMLNamespacePrefixies();
                                                            xml.setPrefix(pairs[0]);
                                                            xml.setNamespace(pairs[1]);
                                                            prefixes.getXMLNamespacePrefixies().add(xml);
                                                       }
                                                  }

                                                  if (!prefixes.getXMLNamespacePrefixies().isEmpty()) {
                                                       ui.setNamespaces(prefixes);
                                                  }
                                             }

                                             ui.getXPaths().getXPathExpressionType().add(xpath);
                                        }
                                        
                                        //now for existing xpaths and prefixes, etc
                                        //bits[0]=header
                                        //bits[1]= uuid
                                        //bits[2]=xpath
                                        //bits[3]=uuid
                                        //bits[4]=xpath OR prefix OR namespace
                                        Set<String> uuidsSet = new HashSet<String>();     //this is the set of namespace prefixes we've already seen
                                        //need a temporary map for uuid mappings to prefix + namespace
                                         Enumeration em2 = request.getParameterNames();
                                        while (em2.hasMoreElements()) {
                                             String key2 = (String) em2.nextElement();
                                             if (key2 != null && key2.startsWith("uid_")) {
                                                  String[] bits2 = key2.split("_");
                                                  if (bits2.length==5 &&
                                                       bits2[0].equals("uid") && 
                                                       bits2[2].equals("xpath") && 
                                                       bits2[4].equals("xpath")){
                                                       if (ui.getXPaths()==null)
                                                            ui.setXPaths(new ArrayOfXPathExpressionType());
                                                       XPathExpressionType xpath = new XPathExpressionType();
                                                       xpath.setXPath(request.getParameter(key2));
                                                       xpath.setIsCertificate("on".equalsIgnoreCase(request.getParameter(bits2[0] + "_" + bits2[1] + "_" + bits2[2] + "_" + bits2[3] + "_x509certxpath")));
                                                       ui.getXPaths().getXPathExpressionType().add(xpath);
                                                       
                                                  } else if (bits2.length==5 &&
                                                       bits2[0].equals("uid") &&
                                                       (bits2[4].equals("prefix") || bits2[4].equals("namespace")))  {
                                                       if (!uuidsSet.contains(bits2[3])){
                                                            uuidsSet.add(bits2[3]);
                                                            if (ui.getNamespaces()==null)
                                                                 ui.setNamespaces(new ArrayOfXMLNamespacePrefixies());
                                                            XMLNamespacePrefixies xml = new XMLNamespacePrefixies();
                                                            xml.setPrefix((request.getParameter(bits2[0] + "_" + bits2[1] + "_" + bits2[2] + "_" + bits2[3] + "_" + "prefix")));
                                                            xml.setNamespace((request.getParameter(bits2[0] + "_" + bits2[1] + "_" + bits2[2] + "_" + bits2[3] + "_" + "namespace")));
                                                            
                                                            ui.getNamespaces().getXMLNamespacePrefixies().add(xml);
                                                            
                                                       }
                                                       
                                                       
                                                  }
                                             }
                                        }
                                        if (ui.getXPaths()!=null && ui.getXPaths().getXPathExpressionType().isEmpty())
                                             ui.setXPaths(null);
                                        if (ui.getNamespaces()!=null && ui.getNamespaces().getXMLNamespacePrefixies().isEmpty())
                                             ui.setNamespaces(null);
                                        userident.add(ui);
                                        //x509certxpath
                                   }

                                   //httpcred
                                   //httpheader
                                   //_headername
                                   //xpath
                                   //XXX_prefix
                                   //XXX_namespace
                                   //XXX_xpath
                              }
                         }
                         //if (key.equals(request))
                         //String value = (String) request.getParameter(key);

                    }

               }

               if (!userident.isEmpty()) {
                    ((TransactionalWebServicePolicy) pol).setUserIdentification(new ArrayOfUserIdentity());
                    ((TransactionalWebServicePolicy) pol).getUserIdentification().getUserIdentity().addAll(userident);
               } else {
                    //none are defined, make sure we clear out the list
                    ((TransactionalWebServicePolicy) pol).setUserIdentification(null);
               }

          }

          if (Utility.stringIsNullOrEmpty(request.getParameter("externalurl"))) {
               pol.setExternalURL(null);
          } else {
               pol.setExternalURL(request.getParameter("externalurl"));
          }

          if (Utility.stringIsNullOrEmpty(request.getParameter("displayName"))) {
               pol.setDisplayName(null);
          } else {
               pol.setDisplayName(request.getParameter("displayName"));
          }
          if (Utility.stringIsNullOrEmpty(request.getParameter("servername"))) {
               pol.setMachineName(null);
          } else {
               pol.setMachineName(request.getParameter("servername"));
          }
          if (Utility.stringIsNullOrEmpty(request.getParameter("domainname"))) {
               pol.setDomainName(null);
          } else {
               pol.setDomainName(request.getParameter("domainname"));
          }
          if (Utility.stringIsNullOrEmpty(request.getParameter("bucket"))) {
               pol.setBucketCategory(null);
          } else {
               pol.setBucketCategory(request.getParameter("bucket"));
          }
          if (Utility.stringIsNullOrEmpty(request.getParameter("parentobject"))) {
               pol.setParentObject(null);
          } else {
               pol.setParentObject(request.getParameter("parentobject"));
          }

          if (Utility.stringIsNullOrEmpty(request.getParameter("description"))) {
               pol.setDescription(null);
          } else {
               pol.setDescription(request.getParameter("description"));
          }
          if (Utility.stringIsNullOrEmpty(request.getParameter("poc"))) {
               pol.setPOC(null);
          } else {
               pol.setPOC(request.getParameter("poc"));
          }

          if (!Utility.stringIsNullOrEmpty(request.getParameter("locationlat"))
               && !Utility.stringIsNullOrEmpty(request.getParameter("locationlong"))) {
               try {
                    double lat = Double.parseDouble(request.getParameter("locationlat"));
                    double lon = Double.parseDouble(request.getParameter("locationlong"));
                    if (lon >= -180 && lon <= 180 && lat <= 90 && lat >= -90) {
                         GeoTag location = new GeoTag();
                         location.setLatitude(lat);
                         location.setLongitude(lon);
                         pol.setLocation((location));
                    }
               } catch (Exception ex) {
               }
          }

         
       
          em = request.getParameterNames();
          Map<String, SLA> slaCache = new HashMap<String, SLA>();

          Map<String, Boolean> processedIds = new HashMap<String, Boolean>();
          Map<SLA, List<SLAAction>> postAppend = new HashMap<SLA, List<SLAAction>>();
          while (em.hasMoreElements()) {
               String key = (String) em.nextElement();
               if (key.startsWith("sla_") && !key.startsWith("sla_action_")) {  //fixme bandaid
                    String[] bits = key.split("_");

                    if (!slaCache.containsKey(bits[1])) {
                         SLA stemp = new SLA();
                         stemp.setGuid(bits[1]);
                         stemp.setAction(new ArrayOfSLAActionBaseType());
                         stemp.setRule(new SLARuleGeneric());
                         slaCache.put(bits[1], stemp);
                    }
                    SLA stemp = (SLA) slaCache.get(bits[1]);
                    //stemp.getAction().getSLAAction().add(new SLAAction());
                    SLARuleGeneric rule = ((SLARuleGeneric) stemp.getRule());

                    //sla_{id}_ACTION_{index}_class
                    //sla_{id}_ACTION_{index}_{param}
                    //sla_{id}_ACTION_{index}_enc_{param}
                    //sla_{id}_RULE_class
                    //sla_{id}_RULE_
                    //sla_{id}_RULE_{param}
                    //sla_{id}_RULE_enc_{param}
                    if (bits[2].equalsIgnoreCase("ACTION")) {
                         //so we have a 1-N of SLAs, each with a list of actions
                         //eixsting SLAs have the index equal to a random uuid from the array
                         //new SLAs have a UUID to differentiate them

                         //this prsents a problem, if we see a new item before an existing item, we can't just created a new instance and
                         //add it to the array. that would cause data mixup from one action to another.
                         //this situation requires a different strategy. if we hit a uuid here, start up another loop to grab just those items
                         //add the uuid to the 'already processed' list, then add the new action to a list which will be appened after the main
                         //loop is complete
                         String index = bits[3];

                         if (!processedIds.containsKey(index)) {
                              processedIds.put(index, true);
                              if (!postAppend.containsKey(stemp)) {
                                   postAppend.put(stemp, new ArrayList<SLAAction>());
                              }
                              List<SLAAction> newactions = postAppend.get(stemp);

                              Enumeration em2 = request.getParameterNames();
                              String prefix = "sla_" + stemp.getGuid() + "_ACTION_" + index;
                              SLAAction newaction = new SLAAction();
                              while (em2.hasMoreElements()) {
                                   String key2 = (String) em2.nextElement();
                                   if (key2.startsWith(prefix)) {
                                        String value = request.getParameter(key2);
                                        String[] bits2 = key2.split("_");
                                        //build up the action here. parameters, classname and enc
                                        if (bits2[4].equalsIgnoreCase("classname")) {
                                             newaction.setImplementingClassName(request.getParameter(key2));
                                        } else if (bits2[4].equalsIgnoreCase("enc")) {
                                             boolean found = false;
                                             boolean enc = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
                                             for (int kk = 0; kk < newaction.getParameterNameValue().size(); kk++) {
                                                  if (newaction.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits2[5])) {
                                                       newaction.getParameterNameValue().get(kk).setEncrypted(enc);
                                                       found = true;
                                                       break;
                                                  }
                                             }
                                             if (!found) {
                                                  //haven't seen this parameter yet, add a new one
                                                  NameValuePair nvp = new NameValuePair();
                                                  nvp.setName(bits2[5]);
                                                  nvp.setEncrypted(enc);
                                                  newaction.getParameterNameValue().add(nvp);
                                             }
                                        } else if (bits2[4].equalsIgnoreCase("encOnSave")) {
                                             boolean found = false;
                                             boolean enc = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
                                             for (int kk = 0; kk < newaction.getParameterNameValue().size(); kk++) {
                                                  if (newaction.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits2[5])) {
                                                       newaction.getParameterNameValue().get(kk).setEncryptOnSave(enc);
                                                       found = true;
                                                       break;
                                                  }
                                             }
                                             if (!found) {
                                                  //haven't seen this parameter yet, add a new one
                                                  NameValuePair nvp = new NameValuePair();
                                                  nvp.setName(bits2[5]);
                                                  nvp.setEncryptOnSave(enc);
                                                  newaction.getParameterNameValue().add(nvp);
                                             }
                                        } else {
                                             //must be a parameter
                                             boolean found = false;
                                             for (int kk = 0; kk < newaction.getParameterNameValue().size(); kk++) {
                                                  if (newaction.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits2[4])) {
                                                       newaction.getParameterNameValue().get(kk).setValue(request.getParameter(key2));
                                                       found = true;
                                                       break;
                                                  }
                                             }
                                             if (!found) {
                                                  //haven't seen this parameter yet, add a new one
                                                  NameValuePair nvp = new NameValuePair();
                                                  nvp.setValue(request.getParameter(key2));
                                                  nvp.setName(bits2[4]);
                                                  newaction.getParameterNameValue().add(nvp);
                                             }

                                        }
                                   }

                              }
                              //TODO validate that the new action has all the necessary parts
                              newactions.add(newaction);
                         }
                    }
                    if (bits[2].equalsIgnoreCase("RULE")) {
                         if (bits[3].equalsIgnoreCase("class")) {
                              rule.setClassName(request.getParameter(key));

                         } else if (bits[3].equalsIgnoreCase("enc")) {
                              boolean found = false;
                              String value=request.getParameter(key);
                              boolean enc = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
                              for (int kk = 0; kk < rule.getParameterNameValue().size(); kk++) {
                                   if (rule.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits[4])) {
                                        rule.getParameterNameValue().get(kk).setEncrypted(enc);
                                        found = true;
                                        break;
                                   }
                              }
                              if (!found) {
                                   //haven't seen this parameter yet, add a new one
                                   NameValuePair nvp = new NameValuePair();
                                   nvp.setName(bits[4]);
                                   nvp.setEncrypted(enc);
                                   rule.getParameterNameValue().add(nvp);
                              }
                         } else if (bits[3].equalsIgnoreCase("encOnSave")) {
                              boolean found = false;
                              String value=request.getParameter(key);
                              boolean enc = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
                              for (int kk = 0; kk < rule.getParameterNameValue().size(); kk++) {
                                   if (rule.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits[4])) {
                                        rule.getParameterNameValue().get(kk).setEncryptOnSave(enc);
                                        found = true;
                                        break;
                                   }
                              }
                              if (!found) {
                                   //haven't seen this parameter yet, add a new one
                                   NameValuePair nvp = new NameValuePair();
                                   nvp.setName(bits[4]);
                                   nvp.setEncryptOnSave(enc);
                                   rule.getParameterNameValue().add(nvp);
                              }
                         } else {
                              //must be a parameter
                              boolean found = false;
                              
                              for (int kk = 0; kk < rule.getParameterNameValue().size(); kk++) {
                                   if (rule.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits[3])) {
                                        rule.getParameterNameValue().get(kk).setValue(request.getParameter(key));
                                        found = true;
                                        break;
                                   }
                              }
                              if (!found) {
                                   //haven't seen this parameter yet, add a new one
                                   NameValuePair nvp = new NameValuePair();
                                   nvp.setName(bits[3]);
                                   nvp.setValue(request.getParameter(key));
                                   rule.getParameterNameValue().add(nvp);
                              }
                         }
                    }

               }
          }

          //ok now to postappend the new actions
          //Map<String, SLA> slaCache = new HashMap<String, SLA>();
          //Map<SLA, List<SLAAction>> postAppend = new HashMap<SLA, List<SLAAction>>();
          Iterator<SLA> it = slaCache.values().iterator();
          while (it.hasNext()) {
               SLA t = it.next();
               if (postAppend.containsKey(t)) {
                    List<SLAAction> n = (List<SLAAction>) postAppend.get(t);
                    t.getAction().getSLAAction().addAll(n);
               }

          }
          if (pol.getServiceLevelAggrements() == null) {
               pol.setServiceLevelAggrements(new ArrayOfSLA());
          }

          pol.getServiceLevelAggrements().getSLA().clear();
          pol.getServiceLevelAggrements().getSLA().addAll(slaCache.values());
          if (pol.getServiceLevelAggrements().getSLA().isEmpty()) {
               pol.setServiceLevelAggrements(null);
          }

          //federation policy
          if (pol.getFederationPolicyCollection() == null) {
               pol.setFederationPolicyCollection(new FederationPolicyCollection());
          }
          pol.getFederationPolicyCollection().getFederationPolicy().clear();

          em = request.getParameterNames();
          Map<String, FederationPolicy> feds = new HashMap<String, FederationPolicy>();

          while (em.hasMoreElements()) {
               String key = (String) em.nextElement();
               if (key.startsWith("fed_")) {
                    String[] bits = key.split("_");
                    String uuid = bits[1];
                    if (!feds.containsKey(uuid)) {
                         feds.put(uuid, new FederationPolicy());
                    }
                    FederationPolicy fpol = feds.get(uuid);
                    if (bits[2].equals("class")) {
                         fpol.setImplementingClassName(request.getParameter(key));
                    } else if (bits[2].equalsIgnoreCase("enc")) {
                         boolean found = false;
                          String value=request.getParameter(key);
                              boolean enc = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
                         for (int kk = 0; kk < fpol.getParameterNameValue().size(); kk++) {
                              if (fpol.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits[3])) {
                                   fpol.getParameterNameValue().get(kk).setEncrypted(enc);
                                   found = true;
                                   break;
                              }
                         }
                         if (!found) {
                              //haven't seen this parameter yet, add a new one
                              NameValuePair nvp = new NameValuePair();
                              nvp.setName(bits[3]);
                              nvp.setEncrypted(enc);
                              fpol.getParameterNameValue().add(nvp);
                         }
                    } else if (bits[2].equalsIgnoreCase("encOnSave")) {
                         boolean found = false;
                          String value=request.getParameter(key);
                              boolean enc = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on");
                         for (int kk = 0; kk < fpol.getParameterNameValue().size(); kk++) {
                              if (fpol.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits[3])) {
                                   fpol.getParameterNameValue().get(kk).setEncryptOnSave(enc);
                                   found = true;
                                   break;
                              }
                         }
                         if (!found) {
                              //haven't seen this parameter yet, add a new one
                              NameValuePair nvp = new NameValuePair();
                              nvp.setName(bits[3]);
                              nvp.setEncryptOnSave(enc);
                              fpol.getParameterNameValue().add(nvp);
                         }
                    } else {
                         //must be a parameter
                         boolean found = false;
                         for (int kk = 0; kk < fpol.getParameterNameValue().size(); kk++) {
                              if (fpol.getParameterNameValue().get(kk).getName().equalsIgnoreCase(bits[2])) {
                                   fpol.getParameterNameValue().get(kk).setValue(request.getParameter(key));
                                   found = true;
                                   break;
                              }
                         }
                         if (!found) {
                              //haven't seen this parameter yet, add a new one
                              NameValuePair nvp = new NameValuePair();
                              nvp.setName(bits[2]);
                              nvp.setValue(request.getParameter(key));
                              fpol.getParameterNameValue().add(nvp);
                         }
                    }

               }
          }
          pol.getFederationPolicyCollection().getFederationPolicy().addAll(feds.values());
          if (pol.getFederationPolicyCollection().getFederationPolicy().isEmpty()) {
               pol.setFederationPolicyCollection(null);
          }

          return pol;
     }
     
     public static String stripScripts(String html){
         //TODO apply the OWASP Html Santitizer
         return html;
     }
}
