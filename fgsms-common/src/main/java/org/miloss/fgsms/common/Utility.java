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
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.common;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.Duration;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Level;

import org.miloss.fgsms.services.interfaces.automatedreportingservice.*;
import org.miloss.fgsms.services.interfaces.common.*;
import org.miloss.fgsms.services.interfaces.datacollector.AddData;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.AddMoreData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.reportingservice.ArrayOfReportTypeContainer;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * fgsms's Utility functions - Ahh the good old Utility class.
 * containsPolicyType string functions, friendly name conversions, serialization
 * contexts and more. This also is the place to go to for JDBC connections
 *
 * @author AO
 */
public class Utility {

    public static final String logname = "fgsms.Utility";
    static final Logger log = Logger.getLogger(logname);

    public static final String JDBC_PERFORMANCE = "jdbc/fgsmsPerformance";
    public static final String JDBC_CONFIG = "jdbc/fgsmsConfig";
    public static final String SECONDARY_POSTIFX = "Secondary";
    private static final SimpleDateFormat dtg = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static JAXBContext serialctx = null;
    private static JAXBContext arsctx = null;
    //Primary ooled NON database settings
    private static final String PerformanceDBURL = "performancedbURL";
    private static final String ConfigDBURL = "configdbURL";
    private static final String ConfigUsername = "configUser";
    private static final String ConfigPassword = "configPass";
    private static final String PerformanceUsername = "perfUser";
    private static final String PerformancePassword = "perfPass";
    //Alternate/FAILOVER NON pooled database settings
    private static final String PerformanceDBURL_FAILOVER = "performancedbURL_FAILOVER";
    private static final String ConfigDBURL_FAILOVER = "configdbURL_FAILOVER";
    private static final String ConfigUsername_FAILOVER = "configUser_FAILOVER";
    private static final String ConfigPassword_FAILOVER = "configPass_FAILOVER";
    private static final String PerformanceUsername_FAILOVER = "perfUser_FAILOVER";
    private static final String PerformancePassword_FAILOVER = "perfPass_FAILOVER";
    private static final String DBdriver = "driver";
    public static final String PropertyFilePath = "org/miloss/fgsms/common/database";
    private static String myHostname = null;

    public static String formatDateTime(Calendar cal) {
        synchronized (dtg) {
            if (cal == null) {
                return "unknown";
            }
            return dtg.format(cal.getTime());
        }
    }

    public static String formatDateTime(Date cal) {
        synchronized (dtg) {
            if (cal == null) {
                return "unknown";
            }
            return dtg.format(cal);
        }
    }

    public static String formatDateTime(long timestampEpoch) {
        synchronized (dtg) {
            return dtg.format(new Date(timestampEpoch));
        }
    }

    /**
     * Parses the date/time stamp from a given input parameter using one of the
     * following formats<br> <ul> <li>mm/dd/yyyy</li> <li>mm/dd/yyyy
     * hh:mm:ss</li> <li>EEE MMM dd HH:mm:ss zzz yyyy - this is the standard
     * output from the unix date command</li> <li>EEE mm/dd/yyyy HH:mm:ss.ms -
     * this is the standard output from the windows command echo %date%
     * %time%</li> <li>yyyy-MM-ddThh:mm:ss.zzzzzzz</li> <li>Epoch time (ms)</li>
     * <li>PnYnMnDTnHnMnS - XML lexical representation</li> </ul>
     *
     * @param s the input string
     * @return a non-null instance of Calendar matching the interpreted date.
     * @throws Exception if the date cannot be parsed
     */
    public static Calendar parseDateTime(String s) throws Exception {
        //this is what the calendar widget gives us
        SimpleDateFormat f1 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat f2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        //std unix date format
        SimpleDateFormat f3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        //std windows  date format
        SimpleDateFormat f4 = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss.ms");
        SimpleDateFormat f5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.zzzzzzz");
        Date d = null;

        try {
            d = f1.parse(s, new ParsePosition(0));
        } catch (Exception ex) {
        }
        if (d == null) {
            try {
                d = f2.parse(s, new ParsePosition(0));
            } catch (Exception ex) {
            }
        }
        if (d == null) {
            try {
                d = f3.parse(s, new ParsePosition(0));
            } catch (Exception ex) {
            }
        }
        if (d == null) {
            try {
                d = f4.parse(s, new ParsePosition(0));
            } catch (Exception ex) {
            }
        }
        if (d == null) {
            try {
                d = f5.parse(s, new ParsePosition(0));
            } catch (Exception ex) {
            }
        }
        if (d != null) {
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(d);
            return (gcal);
        }
        try {
            long timestamp = Long.parseLong(s);
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(timestamp);
            return (gcal);
        } catch (Exception x) {
        }
        throw new Exception("Unable to parse the date, see documentation for correct usage");
    }

    /**
     * Addings the default Status Change SLA for email alerts
     *
     * @param ret
     */
    public static void addStatusChangeSLA(ServicePolicy ret) {
        if (ret.getServiceLevelAggrements() == null) {
            ret.setServiceLevelAggrements(new ArrayOfSLA());
        }
        boolean existingSla = false;
        for (int i = 0; i < ret.getServiceLevelAggrements().getSLA().size(); i++) {
            if (ret.getServiceLevelAggrements().getSLA().get(i).getRule() instanceof SLARuleGeneric) {
                SLARuleGeneric r = (SLARuleGeneric) ret.getServiceLevelAggrements().getSLA().get(i).getRule();
                if (r.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus")) {
                    existingSla = true;
                }
            }
        }
        if (!existingSla) {
            SLA s = new SLA();
            SLARuleGeneric r = new SLARuleGeneric();
            r.setClassName("org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
            r.setProcessAt(RunAtLocation.FGSMS_SERVER);

            s.setRule(r);
            s.setGuid(UUID.randomUUID().toString());
            s.setAction(new ArrayOfSLAActionBaseType());
            //TODO i18n
            SLAAction e = Utility.newEmailAction(null, "Change of status for " + Utility.encodeHTML(ret.getURL()),
                    "Change of status for " + Utility.encodeHTML(ret.getURL()) + ". This is the default status alert. This can be changed by changing the policy for this service.");
            s.getAction().getSLAAction().add(e);
            ret.getServiceLevelAggrements().getSLA().add(s);
        }

        existingSla = false;
        for (int i = 0; i < ret.getServiceLevelAggrements().getSLA().size(); i++) {
            if (ret.getServiceLevelAggrements().getSLA().get(i).getRule() instanceof SLARuleGeneric) {
                SLARuleGeneric r = (SLARuleGeneric) ret.getServiceLevelAggrements().getSLA().get(i).getRule();
                if (r.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.StaleData")) {
                    existingSla = true;
                }
            }
        }
        if (!existingSla) {
            SLA s = new SLA();
            SLARuleGeneric r = new SLARuleGeneric();
            r.setProcessAt(RunAtLocation.FGSMS_SERVER);
            r.setClassName("org.miloss.fgsms.sla.rules.StaleData");

            s.setRule(r);
            s.setGuid(UUID.randomUUID().toString());
            s.setAction(new ArrayOfSLAActionBaseType());
            SLAAction e = Utility.newEmailAction(null, "Stale Data Alert for " + Utility.encodeHTML(ret.getURL()),
                    "Stale Data Alert for " + Utility.encodeHTML(ret.getURL()) + ". This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");

            s.getAction().getSLAAction().add(e);
            ret.getServiceLevelAggrements().getSLA().add(s);

        }
    }

    /**
     * used by the Automated Reporting Service, creates a jaxb context to
     * serialize/deserial a reporting job
     *
     * @return
     * @throws JAXBException
     */
    public synchronized static JAXBContext getARSSerializationContext() throws JAXBException {

        if (arsctx == null) {
            arsctx = JAXBContext.newInstance(ReportDefinition.class, TimeRangeDiff.class, ScheduleDefinition.class,
                    AbstractSchedule.class, ExportCSVDataRequestMsg.class, ExportDataRequestMsg.class, Daynames.class, DailySchedule.class, WeeklySchedule.class, MonthlySchedule.class,
                    SLAAction.class, NameValuePair.class, OneTimeSchedule.class,
                    SecurityWrapper.class, TimeRange.class, ArrayOfReportTypeContainer.class, ReportTypeContainer.class, 
                    ExportDataRequestMsg.class, String.class, Calendar.class,
                    ExportCSVDataRequestMsg.class, ExportRecordsEnum.class);
        }
        return arsctx;

    }

    /**
     * Gets a JAXBContext object which is used to create a
     * serializer/deserializer. Useage is only guaranteed for serializing
     * ServicePolicy, AddData, AddMoreData objects
     *
     * @return
     * @throws JAXBException
     */
    public synchronized static JAXBContext getSerializationContext() throws JAXBException {

        if (serialctx == null) {
            serialctx = JAXBContext.newInstance(
                    ArrayOfSLA.class,
                    SLA.class, SLAAction.class,
                    //LogTo.class,
                    RuleBaseType.class,
                    GeoTag.class, Long.class, double.class,
                    String.class, ServicePolicy.class, GeoTag.class, ArrayOfUserIdentity.class, Duration.class,
                    PolicyType.class, ArrayOfSLAActionBaseType.class, ArrayOfServicePolicy.class,
                    //new stuff RC6

                    //new Rules
                    //web services

                    //new types
                    FederationPolicy.class,
                    //    FederationTarget.class, 
                    FederationPolicyCollection.class,
                    AddData.class, AddMoreData.class, AddDataRequestMsg.class, Header.class,
                    ProcessPolicy.class, MachinePolicy.class, AlertMessageDefinition.class, StatusServicePolicy.class, StatisticalServicePolicy.class,
                    TransactionalWebServicePolicy.class,
                    RunAtLocation.class,
                    DriveInformation.class,
                    SetProcessListByMachineResponseMsg.class,
                    SetProcessListByMachineRequestMsg.class,
                    MachineInformation.class, NetworkAdapterInfo.class, PropertyPair.class, NetworkAdapterPerformanceData.class,
                    //new as of 6.2
                    NameValuePair.class,
                    SLARuleGeneric.class,
                    //new as of 6.3
                    org.miloss.fgsms.services.interfaces.common.NameValuePairSet.class);
        }

        return serialctx;

    }

    /**
     * throws an illegal argument exception if the object is null or not
     * specified, caveats is options
     *
     * @param w
     */
    public static void validateClassification(SecurityWrapper w) {
        if (w == null || w.getClassification() == null) {
            throw new IllegalArgumentException("A classification level must be specified");
        }
    }

    /**
     * returns the lowercase hostname
     *
     * @return
     */
    public static String getHostName() {
        if (myHostname == null) {
            try {
                InetAddress addr = InetAddress.getLocalHost();

                // Get IP Address
                // byte[] ipAddr = addr.getAddress();
                // Get hostname
                myHostname = addr.getHostName().toLowerCase();
            } catch (Exception e) {
                myHostname = "ADDRESS_UNKNOWN";
            }
        }
        return myHostname;
    }

    /**
     * does this policy have an email sla action?
     *
     * @param pc
     * @return
     */
    public static boolean hasEmailSLA(ServicePolicy pc) {
        if (pc == null) {
            return false;
        }
        if (pc.getServiceLevelAggrements() == null
                || pc.getServiceLevelAggrements().getSLA().isEmpty()) {
            return false;
        }
        for (int i = 0; i < pc.getServiceLevelAggrements().getSLA().size(); i++) {
            if (pc.getServiceLevelAggrements().getSLA().get(i).getAction() != null
                    && !pc.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().isEmpty()) {
                for (int k = 0; k < pc.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().size(); k++) {
                    if (pc.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(k).getImplementingClassName().equals("org.miloss.fgsms.sla.actions.EmailAlerter")) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * does this action list contain an email SLA
     *
     * @param pc
     * @return
     */
    public static boolean hasEmailSLA(ArrayOfSLAActionBaseType pc) {
        if (pc == null) {
            return false;
        }
        if (pc.getSLAAction().isEmpty()) {
            return false;
        }

        for (int i = 0; i < pc.getSLAAction().size(); i++) {

            if (pc.getSLAAction().get(i).getImplementingClassName().equals("org.miloss.fgsms.sla.actions.EmailAlerter")) {
                return true;
            }
        }
        return false;
    }

    /**
     * encrypts a password AES 256 bit Requires the Unlimited Strength Crypto
     * Extensions
     *
     * @param clear
     * @return
     */
    public static String EN(String clear) {
        if (stringIsNullOrEmpty(clear)) {
            return "";
        }
        try {
            return AES.EN(clear);
        } catch (Exception ex) {
            log.log(Level.FATAL, "############################################# fgsms cannot encrypt! Check to make sure the unlimited strength JCE is installed: " + ex.getMessage());
        }
        return "";
    }

    /**
     * Decrypts a password or other sensitive data. If the parameter is null or
     * empty, an empty string is returned. If the parameter is not encrypted or
     * was encrypted using a different key or it fails to decrypt, the original
     * text is returned.
     *
     * @param cipher
     * @return
     */
    public static String DE(String cipher) {
        if (stringIsNullOrEmpty(cipher)) {
            return "";
        }
        try {
            return AES.DE(cipher);
        } catch (Exception ex) {
            log.log(Level.FATAL, "############################################# fgsms cannot decrypt! Check to make sure the unlimited strength JCE is installed: " + ex.getMessage());
        }
        return cipher;

    }

    /*
      * removes NonUtf8 Compliant Characters
      *
      * @param inString
      * @return
     
      public static String removeNonUtf8CompliantCharacters(final String inString) {
      if (null == inString) {
      return null;
      }
      if (inString.isEmpty()) {
      return null;
      }
      byte[] byteArr = inString.getBytes();
      for (int i = 0; i < byteArr.length; i++) {
      byte ch = byteArr[i];
      // remove any characters outside the valid UTF-8 range as well as all control characters
      // except tabs and new lines
      if (!((ch > 31 && ch < 253) || ch == '\t' || ch == '\n' || ch == '\r')) {
      byteArr[i] = ' ';
      }
      }
      return new String(byteArr);
      }*/
    /**
     * Converts a duration to 1yr 1mo 1d 1hr 1min 1s string format
     *
     * @param d
     * @return
     */
    public static String durationToString(Duration d) {
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
        return s.trim();
    }

    /**
     * returns just the largest unit of measure for durations up to years
     *
     * @param d
     * @return
     */
    public static String durationLargestUnitToString(Duration d) {
        if (d == null) {
            return "";
        }
        if (d.getYears() > 0) {
            return d.getYears() + "yr";
        }
        if (d.getMonths() > 0) {
            return d.getMonths() + "mo";
        }
        if (d.getDays() > 0) {
            return d.getDays() + "d";
        }
        if (d.getHours() > 0) {
            return d.getHours() + "hr";
        }
        if (d.getMinutes() > 0) {
            return d.getMinutes() + "min";
        }
        if (d.getSeconds() > 0) {
            return d.getSeconds() + "s";
        }
        return "";
    }

    /**
     * pretty print for ICISM class levels
     *
     * @param type
     * @return
     */
    public static String ICMClassificationToString(ClassificationType type) {

        if (type == null) {
            return "UNCLASSIFIED";
        }
        switch (type) {
            case U:
                return "UNCLASSIFIED";
            case R:
                return "RESTRICTED";
            case C:
                return "CONFIDENTIAL";
            case S:
                return "SECRET";
            case TS:
                return "TOP SECRET";
            case CTS:
                return "COSMIC TS";
            case NU:
                return "NATO UNCLASSIFIED";
            case NC:
                return "NATO CONFIDENTIAL";
            case NR:
                return "NATO RESTRICTED";
            case NS:
                return "NATO SECRET";
            case CTSA:
                return "COSMIC TOP SECRET ATOMAL";
            case NSAT:
                return "NATO SECRET ATOMAL";
            case NCA:
                return "NATO CLASSIFIED ATOMAL";
            case CTS_BALK:
                return "COSMIC TOP SECRET BALK";
            case CTS_B:
                return "COSMIC TOP SECRET Bohemia";
            default:
                return "CLASSIFIED: " + type.value();
        }
    }

    /**
     * ICISM to color codes
     *
     * @param type
     * @return
     */
    public static String ICMClassificationToColorCodeString(ClassificationType type) {

        if (type == null) {
            return "lime";
        }
        switch (type) {
            case U:
                return "lime";
            case R:
                return "yellow";
            case C:
                return "blue";
            case S:
                return "red";
            case TS:
                return "orange";
            case CTS:
                return "red";
            case NU:
                return "lime";
            case NC:
                return "purple";
            case NR:
                return "purple";
            case NS:
                return "purple";
            case CTSA:
                return "orange";
            case NSAT:
                return "purple";
            case NCA:
                return "purple";
            case CTS_BALK:
                return "orange";
            case CTS_B:
                return "orange";
            default:
                return "red";
        }
    }

    /**
     * converts a list of string to a single string whitespace, null and empty
     * strings are ignored
     */
    public static String listStringtoString(List<String> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        //  String s = "";
        for (int i = 0; i < data.size(); i++) {
            if (!stringIsNullOrEmpty(data.get(i))) {
                String t = data.get(i);
                t = t.trim();
                if (!stringIsNullOrEmpty(t)) {
                    sb = sb.append(t).append(" ");
                    //s += t + " ";
                }

            }
        }
        return sb.toString().trim();
    }

    /**
     * uses a variety of classes to determine the name of the first XML child
     * node of a SOAP:envelope node
     *
     * @param xml
     * @return
     */
    public static String getActionNameFromXML(String xml) {
        if (stringIsNullOrEmpty(xml)) {
            return "";
        }
        try {
            InputStream is = null;
            Document xmlDocument = null;
            try {
                is = new ByteArrayInputStream(xml.trim().getBytes(Constants.CHARSET));
                xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            } catch (Exception ex) {
                log.log(Level.DEBUG, "fgsms Utility, error identifying request method by building request message as xml document: " + ex.getLocalizedMessage());
            }
            if (xmlDocument == null) {
                String workingdoc = "";
                try {
                    workingdoc = xml.substring(xml.indexOf("<"), xml.lastIndexOf(">"));
                } catch (Exception ex) {
                }
                if (Utility.stringIsNullOrEmpty(workingdoc)) {
                    log.log(Level.WARN, "fgsms Utility, error identifying request method by building request message as xml document: this most likely isn't xml");
                    return "";
                }

                if (Utility.stringIsNullOrEmpty(xml)) // return "";
                {
                    is = new ByteArrayInputStream(workingdoc.getBytes(Constants.CHARSET));
                }
                xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            }

            XPath xpath = XPathFactory.newInstance().newXPath();
            javax.xml.xpath.XPathExpression xp = xpath.compile("/Envelope/Body");
            try {
                DeferredElementImpl j = (DeferredElementImpl) xp.evaluate(xmlDocument, XPathConstants.NODE);

                //org.apache.xerces.dom.DeferredElementImpl j2 = (org.apache.xerces.dom.DeferredElementImpl)
                for (int i = 0; i < j.getChildNodes().getLength(); i++) {
                    if (!j.getChildNodes().item(i).getNodeName().equalsIgnoreCase("#text")) {
                        log.log(Level.DEBUG, "Found action via com.sun.org.apache.xerces.internal.dom.DeferredElementImpl");
                        return j.getChildNodes().item(i).getNodeName();

                    }
                }
            } catch (NoClassDefFoundError c) {
                log.log(Level.DEBUG, "couldn't find com.sun.org.apache.xerces.internal.dom.DeferredElementImpl, trying alternate method...");
            } catch (ClassCastException e) {
                log.log(Level.DEBUG, "couldn't find com.sun.org.apache.xerces.internal.dom.DeferredElementImpl, trying alternate method...");
            } catch (Exception ex) {
                log.log(Level.DEBUG, "error caught using com.sun.org.apache.xerces.internal.dom.DeferredElementImpl, trying alternate method...", ex);
            }

            try {
                org.apache.xerces.dom.DeferredElementImpl j = (org.apache.xerces.dom.DeferredElementImpl) xp.evaluate(xmlDocument, XPathConstants.NODE);
                for (int i = 0; i < j.getChildNodes().getLength(); i++) {
                    if (!j.getChildNodes().item(i).getNodeName().equalsIgnoreCase("#text")) {
                        Node n = j.getChildNodes().item(i);
                        String s = n.getNodeName();
                        s = n.getNodeValue();
                        s = n.getNodeValue();
                        s = n.getBaseURI();
                        log.log(Level.DEBUG, "Found action via org.apache.xerces.dom.DeferredElementImpl");
                        return j.getChildNodes().item(i).getNodeName();

                    }
                }

            } catch (NoClassDefFoundError c) {
                log.log(Level.DEBUG, "couldn't find org.apache.xerces.dom.DeferredElementImpl, giving up determining action");
            } catch (Exception ex) {
                log.log(Level.DEBUG, "error caught using org.apache.xerces.dom.DeferredElementImpl", ex);
            }

        } catch (Exception ex) {
            log.log(Level.DEBUG, "fgsms Utility, error identifying request method from xml, defaulted to urn:undeterminable", ex);
        }
        return "";
    }

    /**
     * HTML encodes strings to prevent CXS, if the parameter is an null or
     * empty, an empty string is returned, but never null
     *
     * @param s
     * @return
     */
    public static String encodeHTML(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        return StringEscapeUtils.escapeHtml4(s);
        /*StringBuffer out = new StringBuffer();
          for (int i = 0; i < s.length(); i++) {
               char c = s.charAt(i);
               if (c > 127 || c == '"' || c == '<' || c == '>') {
                    out.append("&#" + (int) c + ";");
               } else {
                    out.append(c);
               }
          }
          return out.toString();*/
    }

    /**
     * string is null or empty
     *
     * @param s
     * @return
     */
    public static boolean stringIsNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }
        if (s.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * converts a duration to time in ms
     *
     * @param d
     * @return
     */
    public static long durationToTimeInMS(Duration d) {
        if (d == null) {
            throw new IllegalArgumentException("duration cannot be null");
        }

        return d.getTimeInMillis(new GregorianCalendar());
        /*        return ((long) d.getYears() * yrms)
           + (d.getMonths() * monms)
           + (d.getDays() * dayms)
           + (d.getHours() * hrms)
           + (d.getMinutes() * minms)
           + (d.getSeconds() * secms);*/
    }

    /**
     * String Truncator if a null or empty string is passed, an empty string is
     * returned
     *
     * @param x
     * @param length
     * @return
     */
    public static String truncate(String x, int length) {
        if (x == null || x.length() == 0) {
            return "";
        }
        String ret = x;
        if (x.length() > length) {
            ret = x.substring(0, length);
        }
        return ret;
    }

    @Deprecated
    private static Connection getPerformanceDB_NONPOOLED_Connection_FAILOVER() {
        try {
            Properties prop = PropertyLoader.loadProperties(PropertyFilePath);
            Driver d = (Driver) Class.forName(prop.getProperty(DBdriver)).newInstance();
            DriverManager.registerDriver(d);
            Connection con = DriverManager.getConnection(
                    prop.getProperty(PerformanceDBURL_FAILOVER),
                    prop.getProperty(PerformanceUsername_FAILOVER),
                    DE(prop.getProperty(PerformancePassword_FAILOVER)));
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                log.log(Level.FATAL, "Error obtaining secondary perf database connection, msg:" + e.getLocalizedMessage(), e);
                return null;
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            log.log(Level.FATAL, "No connection to the secondary database could be made, msg:" + ex.getLocalizedMessage(), ex);
            return null;
        }
    }

    /**
     * returns a NON pooled database connection by using the data in the
     * database.properties if the primary cannot be reached, the secondary is
     * used or null is returned.
     *
     * configuration is loaded from the database.properties embedded within
     * fgsms.common.jar
     *
     * @return
     */
    @Deprecated
    public static Connection getPerformanceDB_NONPOOLED_Connection() {
        try {
            Properties prop = PropertyLoader.loadProperties(PropertyFilePath);
            Driver d = (Driver) Class.forName(prop.getProperty(DBdriver)).newInstance();
            DriverManager.registerDriver(d);
            Connection con = DriverManager.getConnection(
                    prop.getProperty(PerformanceDBURL),
                    prop.getProperty(PerformanceUsername),
                    DE(prop.getProperty(PerformancePassword)));
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {

                log.log(Level.ERROR, "Error obtaining primary perf database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                return getPerformanceDB_NONPOOLED_Connection_FAILOVER();
            } finally {
                DBUtils.safeClose(com);
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "No connection to the performance database could be made, msg:" + ex.getLocalizedMessage(), ex);
            return null;
        }
    }

    /**
     * returns a NON pooled database connection by using the data in the
     * database.properties if the primary cannot be reached, the secondary is
     * used or null is returned.
     *
     * configuration is loaded from the database.properties embedded within
     * fgsms.common.jar
     *
     * @return
     */
    @Deprecated
    public static Connection getConfigurationDB_NONPOOLED_Connection() {
        try {
            Properties prop = PropertyLoader.loadProperties(PropertyFilePath);
            Driver d = (Driver) Class.forName(prop.getProperty(DBdriver)).newInstance();
            DriverManager.registerDriver(d);
            Connection con = DriverManager.getConnection(
                    prop.getProperty(ConfigDBURL),
                    prop.getProperty(ConfigUsername),
                    DE(prop.getProperty(ConfigPassword)));
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                log.log(Level.ERROR, "Error obtaining primary config database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                return getConfigurationDB_NONPOOLED_Connection_FAILOVER();
            } finally {
                DBUtils.safeClose(com);
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "Error obtaining primary conf database connection:" + ex.getLocalizedMessage(), ex);
            return getConfigurationDB_NONPOOLED_Connection_FAILOVER();
        }
    }

    @Deprecated
    private static Connection getConfigurationDB_NONPOOLED_Connection_FAILOVER() {
        try {
            Properties prop = PropertyLoader.loadProperties(PropertyFilePath);
            Driver d = (Driver) Class.forName(prop.getProperty(DBdriver)).newInstance();
            DriverManager.registerDriver(d);
            Connection con = DriverManager.getConnection(
                    prop.getProperty(ConfigDBURL_FAILOVER),
                    prop.getProperty(ConfigUsername_FAILOVER),
                    DE(prop.getProperty(ConfigPassword_FAILOVER)));
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                log.log(Level.FATAL, "Error obtaining failover config database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                return null;
            } finally {
                DBUtils.safeClose(com);
            }

        } catch (Exception ex) {
            log.log(Level.FATAL, "Error obtaining primary conf database connection:" + ex.getLocalizedMessage(), ex);
            return null;
        }
    }

    /**
     * returns a JDNI pooled database connection if the primary cannot be
     * reached, the secondary is used or null is returned. java:fgsmsPerformance
     * java:fgsmsPerformanceSecondary
     *
     * be sure to close the connection when finished
     *
     * @return null if a connection cannot be established
     */
    public static Connection getPerformanceDBConnection() {
        Exception root = null;
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:fgsmsPerformance");
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                root = e;
                //log.log(Level.WARN, "Error obtaining primary pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                //return GetAlternatePerformanceDBConnection();
            } finally {
                DBUtils.safeClose(com);
            }

        } catch (Exception ex) {
            root = ex;
            //log.log(Level.WARN, "Error obtaining primary pooled performance database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
            //return GetAlternatePerformanceDBConnection();
        }

        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:/comp/env/" + JDBC_PERFORMANCE);
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                root = e;
                //log.log(Level.WARN, "Error obtaining primary pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            root = ex;
            //log.log(Level.WARN, "Error obtaining primary pooled performance database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
        }

        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup(JDBC_PERFORMANCE);
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                root = e;
                //log.log(Level.WARN, "Error obtaining primary pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            root = ex;
            //log.log(Level.WARN, "Error obtaining primary pooled performance database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
        }
        if (root != null) {
            log.log(Level.WARN, "Error obtaining primary pooled performance database connection, attempting alternate. msg:" + root.getLocalizedMessage(), root);
        }
        return getAlternatePerformanceDBConnection();
    }

    private static Connection getAlternatePerformanceDBConnection() {
        Exception root = null;
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:fgsmsPerformance" + SECONDARY_POSTIFX);
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                root = e;
                //log.log(Level.WARN, "Error obtaining secondary pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            root = ex;
            //log.log(Level.WARN, "Error obtaining secondary pooled performance database connection, giving up. Ensure that the database is available. msg:" + ex.getLocalizedMessage(), ex);
        }
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:/comp/env/" + JDBC_PERFORMANCE + SECONDARY_POSTIFX);
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                root = e;
                //log.log(Level.FATAL, "Error obtaining secondary pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            root = ex;
            //log.log(Level.FATAL, "Error obtaining secondary pooled performance database connection, giving up. Ensure that the database is available. msg:" + ex.getLocalizedMessage(), ex);
        }
        if (root != null) {
            if (root != null) {
                log.log(Level.FATAL, "Error obtaining secondary pooled performance database connection, giving up. Ensure that the database is available. msg:" + root.getLocalizedMessage(), root);
            }
        }
        return null;
    }

    /**
     * returns a JDNI pooled database connection if the primary cannot be
     * reached, the secondary is used or null is returned. java:fgsmsConfig
     * java:fgsmsConfigSecondary
     *
     * @return
     */
    public static Connection getConfigurationDBConnection() {
        Exception root = null;
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.naming.Context env = (javax.naming.Context) ctx1.lookup("java:/comp/env");
            javax.sql.DataSource ds = (javax.sql.DataSource) env.lookup(JDBC_CONFIG);

            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                e.printStackTrace();
                root = e;
                if (System.getProperties().containsKey("debug")) {
                    log.log(Level.WARN, "Error obtaining primary conf pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                }
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            root = ex;
            if (System.getProperties().containsKey("debug")) {
                log.log(Level.WARN, "Error obtaining primary conf pooled database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
            }
        }
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:fgsmsConfig");
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                e.printStackTrace();
                root = e;
                if (System.getProperties().containsKey("debug")) {
                    log.log(Level.WARN, "Error obtaining primary conf pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                }
            } finally {
                DBUtils.safeClose(com);
            }

        } catch (Exception ex) {
            root = ex;
            if (System.getProperties().containsKey("debug")) {
                log.log(Level.WARN, "Error obtaining primary conf pooled database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
            }
        }
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup(JDBC_CONFIG);
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                e.printStackTrace();
                root = e;
                if (System.getProperties().containsKey("debug")) {
                    log.log(Level.WARN, "Error obtaining primary conf pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                }
            } finally {
                DBUtils.safeClose(com);
            }

        } catch (Exception ex) {
            root = ex;
            if (System.getProperties().containsKey("debug")) {
                log.log(Level.WARN, "Error obtaining primary conf pooled database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
            }
        }
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("fgsmsConfig");
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                e.printStackTrace();
                root = e;
                if (System.getProperties().containsKey("debug")) {
                    log.log(Level.WARN, "Error obtaining primary conf pooled performance database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                }
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            root = ex;
            if (System.getProperties().containsKey("debug")) {
                log.log(Level.WARN, "Error obtaining primary conf pooled database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
            }
        }
        if (root != null) {
            log.log(Level.WARN, "Error obtaining primary conf pooled database connection, attempting alternate. msg:" + root.getLocalizedMessage(), root);
        }
        return getAlternateConfigurationDBConnection();
    }

    private static Connection getAlternateConfigurationDBConnection() {
        Exception root = null;
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:fgsmsConfig" + SECONDARY_POSTIFX);
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                root = e;
                if (System.getProperties().containsKey("debug")) {
                    log.log(Level.WARN, "Error obtaining secondary pooled config database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                }
            } finally {
                DBUtils.safeClose(com);
            }

        } catch (Exception ex) {
            root = ex;
            if (System.getProperties().containsKey("debug")) {
                log.log(Level.WARN, "Error obtaining secondary pooled conf database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
            }
        }
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:/comp/env/" + JDBC_CONFIG + SECONDARY_POSTIFX);
            Connection con = ds.getConnection();
            PreparedStatement com = con.prepareStatement("select 1;");
            try {
                com.execute();
                DBUtils.safeClose(com);
                return con;
            } catch (Exception e) {
                root = e;
                if (System.getProperties().containsKey("debug")) {
                    log.log(Level.FATAL, "Error obtaining secondary pooled config database connection, attempting alternate. msg:" + e.getLocalizedMessage(), e);
                }
            } finally {
                DBUtils.safeClose(com);
            }
        } catch (Exception ex) {
            root = ex;
            if (System.getProperties().containsKey("debug")) {
                log.log(Level.FATAL, "Error obtaining secondary pooled conf database connection, attempting alternate. msg:" + ex.getLocalizedMessage(), ex);
            }
        }
        if (root != null) {
            log.log(Level.FATAL, "Error obtaining secondary pooled config database connection, attempting alternate. msg:" + root.getLocalizedMessage(), root);
        }
        return null;
    }

    /**
     * convenience method for NVP
     *
     * @param name
     * @param val
     * @param encrypted
     * @param encryptOnSave
     * @return
     */
    public static NameValuePair newNameValuePair(String name, String val, boolean encrypted, boolean encryptOnSave) {
        NameValuePair r = new NameValuePair();
        r.setName(name);
        r.setValue(val);
        r.setEncrypted(encrypted);
        r.setEncryptOnSave(encryptOnSave);
        return r;
    }

    /**
     * creates a new send an email sla action
     *
     * @param from
     * @param subject
     * @param body
     * @return
     */
    public static SLAAction newEmailAction(String from, String subject, String body) {
        SLAAction r = new SLAAction();
        r.setImplementingClassName("org.miloss.fgsms.sla.actions.EmailAlerter");
        if (!stringIsNullOrEmpty(from)) {
            r.getParameterNameValue().add(newNameValuePair("From", from, false, false));
        }
        r.getParameterNameValue().add(newNameValuePair("Body", body, false, false));
        r.getParameterNameValue().add(newNameValuePair("Subject", subject, false, false));
        return r;
    }

    /**
     * returns a live copy of the select nvp if it exists, or null
     *
     * @param set
     * @param name
     * @return
     */
    public static NameValuePair getNameValuePairByName(List<NameValuePair> set, String name) {
        if (set == null) {
            return null;
        }
        for (int i = 0; i < set.size(); i++) {
            if (set.get(i).getName().equalsIgnoreCase(name)) {
                return set.get(i);
            }
        }
        return null;
    }

    /**
     * a convenience wrapper to create a new plugin instance
     *
     * @param classname
     * @param displayname
     * @param appliesto FEDERATION_PUBLISH,SLA_RULE,SLA_ACTION, LOGGER
     * @return
     */
    public static Plugin newPlugin(String classname, String displayname, String appliesto) {
        Plugin p = new Plugin();
        p.setClassname(classname);
        p.setDisplayname(displayname);
        p.setPlugintype(appliesto);
        return p;
    }

    public static List<PolicyType> getAllPolicyTypes() {
        List<PolicyType> r = new ArrayList<PolicyType>();
        PolicyType[] values = PolicyType.values();
        r.addAll(Arrays.asList(values));
        return r;
    }

    /**
     * convenience method, does the list contain the policy type
     *
     * @param appliesTo
     * @param optionalPolicyTypeFilter
     * @return
     */
    public static boolean containsPolicyType(List<PolicyType> appliesTo, PolicyType optionalPolicyTypeFilter) {
        if (appliesTo == null) {
            return false;
        }
        if (optionalPolicyTypeFilter == null) {
            return false;
        }
        for (int i = 0; i < appliesTo.size(); i++) {
            if (appliesTo.get(i).equals(optionalPolicyTypeFilter)) {
                return true;
            }
        }
        return false;

    }

    /**
     * convenience wrapper
     *
     * @param key
     * @param name
     * @param value
     * @return
     */
    public static KeyNameValue newKeyNameValue(String key, String name, String value) {
        KeyNameValue x = new KeyNameValue();
        x.setPropertyKey(key);
        x.setPropertyName(name);
        x.setPropertyValue(value);
        return x;
    }

    /**
     * convenience wrapper
     *
     * @param value
     * @param shouldEncrypt
     * @return
     */
    public static KeyNameValueEnc newKeyNameValueEnc(KeyNameValue value, boolean shouldEncrypt) {
        KeyNameValueEnc x = new KeyNameValueEnc();
        x.setKeyNameValue(value);
        x.setShouldEncrypt(shouldEncrypt);
        return x;
    }
}
