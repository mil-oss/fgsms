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
package org.miloss.fgsms.auxsrv;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.federation.FederationInterface;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.QuickStatData;
import org.miloss.fgsms.services.interfaces.dataaccessservice.QuickStatWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 **Quartz Job that kicks off the UDDI Publisher This job as
 * well as the UDDI publisher is handled differently from the other fgsms quartz
 * jobs. It runs every 5 minutes by default, however does no work unless we have
 * gone past the scheduled interval. This is done so that status information is
 * correctly displayed.
 *
 * @author AO
 */
public class FederationScheduler implements org.quartz.StatefulJob // implements org.jboss.varia.scheduler.Schedulable {
{

     public void UddiScheduler() {
     }
     private static Logger log = Logger.getLogger("fgsms.FederationScheduler");

     public void execute(JobExecutionContext jec) throws JobExecutionException {
          
          KeyNameValueEnc enabled = DBSettingsLoader.GetPropertiesFromDB(true, "FederationScheduler", "Enabled");
          if (enabled == null || enabled.getKeyNameValue() == null || enabled.getKeyNameValue().getPropertyValue() == null
                  || enabled.getKeyNameValue().getPropertyValue().equalsIgnoreCase("false")) {
               //added 5/8/2012 to indicate that the cron job is running, however the required settings are not set for the uddi publisher, sort of sanity check
               AuxHelper.TryUpdateStatus(true, "urn:fgsms:FederationScheduler:" + SLACommon.GetHostName(), "Disabled", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
               return;
          }
          AuxHelper.TryUpdateStatus(true, "urn:fgsms:FederationScheduler:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());

          KeyNameValueEnc interval = DBSettingsLoader.GetPropertiesFromDB(true, "FederationScheduler", "Interval");
          long intinterval = 3600000;
          if (interval != null) {
               try {
                    intinterval = Long.parseLong(interval.getKeyNameValue().getPropertyValue());
                    if (intinterval < 300000) {
                         intinterval = 300000;
                    }
               } catch (Exception ex) {
               }
          }

          Long lastranat = null;
          try {
               lastranat = (Long) jec.getJobDetail().getJobDataMap().get("lastranat");
          } catch (Exception ex) {
          }
          if (lastranat == null) {
               lastranat = (long) 0;

          }

          if (lastranat + intinterval < System.currentTimeMillis()) {
               log.log(Level.INFO, "========================================================== Federation Job executing...");
               try {
               // UddiPublisher pub = new UddiPublisher(true);
                    // if (pub.State) {
                    List<ServicePolicy> LoadFederationServicePoliciesPooled = SLACommon.LoadFederationServicePoliciesPooled();
                    for (int i = 0; i < LoadFederationServicePoliciesPooled.size(); i++) {
                         if (LoadFederationServicePoliciesPooled.get(i).getFederationPolicyCollection() != null) {
                              for (int k = 0; k < LoadFederationServicePoliciesPooled.get(i).getFederationPolicyCollection().getFederationPolicy().size(); k++) {
                                   String clazz = LoadFederationServicePoliciesPooled.get(i).getFederationPolicyCollection().getFederationPolicy().get(k).getImplementingClassName();
                                   FederationInterface newInstance = null;
                                   try {
                                        Class<FederationInterface> forName = (Class<FederationInterface>) Class.forName(clazz);
                                        newInstance = forName.newInstance();
                                        if (newInstance != null) {
                                             newInstance.Publish(true, getQuickStatData(LoadFederationServicePoliciesPooled.get(i).getURL(), true), LoadFederationServicePoliciesPooled.get(i), LoadFederationServicePoliciesPooled.get(i).getFederationPolicyCollection().getFederationPolicy().get(k));
                                        }
                                   } catch (Exception ex) {
                                        log.log(Level.FATAL, "unable to load federation plugin! classname=" + clazz, ex);
                                   }

                              }

                         }
                         // }

                         AuxHelper.TryUpdateStatus(true, "urn:fgsms:FederationScheduler:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
               // } else {
                         //     log.log(Level.ERROR, "uddi publisher state is false, something went wrong");
                         //     AuxHelper.TryUpdateStatus(false, "urn:fgsms:FederationScheduler:" + SLACommon.getHostName(), "Error - Check Config", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.getHostName());
                    }
               } catch (Exception ex) {
                    log.log(Level.ERROR, "error caught from uddi publisher", ex);
                    AuxHelper.TryUpdateStatus(false, "urn:fgsms:FederationScheduler:" + SLACommon.GetHostName(), ex.getMessage(), true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
               }
               jec.getJobDetail().getJobDataMap().put("lastranat", System.currentTimeMillis());
          } else {
               //this needs to just update the same timestamp, not set the status
               //repeat for the other jobs
               //AuxHelper.TryUpdateStatus(true, "urn:fgsms:FederationScheduler:" + SLACommon.getHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.getHostName());
          }
     }

     private QuickStatWrapper getQuickStatData(String urL, boolean pooled) {

          PreparedStatement com=null;
         
          if (Utility.stringIsNullOrEmpty(urL)) {
               return null;
          }
          Connection con = null;
          
          ResultSet rs=null;
          try {
               
               if (pooled) {
                    con = Utility.getPerformanceDBConnection();
               } else {
                    con = Utility.getPerformanceDB_NONPOOLED_Connection();
               }
               com = con.prepareStatement("select * from agg2 where uri=? and soapaction=?;");
               com.setString(1, urL);
               com.setString(2, "All-Methods");
               rs = com.executeQuery();

               QuickStatWrapper w = new QuickStatWrapper();
               w.setAction("All-Methods");

               DatatypeFactory df = DatatypeFactory.newInstance();
               while (rs.next()) {

                    QuickStatData d = new QuickStatData();

                    long timerange = rs.getLong("timerange");
                    d.setTimeInMinutes(BigInteger.valueOf(timerange / 60000));
                    d.setAvailabilityPercentage(rs.getDouble("avail"));
                    d.setFailureCount(rs.getLong("failure"));
                    d.setSuccessCount(rs.getLong("success"));
                    d.setSLAViolationCount(rs.getLong("sla"));
                    d.setAverageResponseTime(rs.getLong("avgres"));
                    if (rs.getLong("mtbf") == -1) {
                         d.setMTBF(null);
                    } else {
                         d.setMTBF(df.newDuration(rs.getLong("mtbf")));
                    }
                    d.setMaximumRequestSize(rs.getLong("maxreq"));
                    d.setMaximumResponseSize(rs.getLong("maxres"));
                    d.setMaximumResponseTime(rs.getLong("maxresponsetime"));
                    GregorianCalendar gcal = new GregorianCalendar();
                    gcal.setTimeInMillis(rs.getLong("timestampepoch"));
                    d.setUpdatedAt((gcal));
                    w.getQuickStatData().add(d);

               }

               w.setUptime(getUpTime(urL));
               rs.close();
               com.close();
               con.close();

               return w;
          } catch (Exception ex) {
               log.log(Level.WARN, "can't get statistics for " + urL, ex);
          } finally {
              DBUtils.safeClose(rs);
              DBUtils.safeClose(com);
              DBUtils.safeClose(con);
          }
          return null;
     }

     private Duration getUpTime(String uri) {
         Connection con=null;
         PreparedStatement com=null;
         ResultSet rs= null;
          try {
               con = Utility.getPerformanceDBConnection();
               com = con.prepareStatement("select * from availability where uri=? order by utcdatetime desc limit 1");
               com.setString(1, uri);
               Duration d = null;
               rs = com.executeQuery();
               if (rs.next()) {
                    long changeat = rs.getLong("utcdatetime");
                    long now = System.currentTimeMillis();
                    DatatypeFactory f = DatatypeFactory.newInstance();
                    d = f.newDuration(now - changeat);
               }
               
               if (d != null) {
                    return d;
               }
          } catch (Exception ex) {
               log.log(Level.ERROR, null, ex);
          } finally {
              DBUtils.safeClose(rs);
              DBUtils.safeClose(com);
              DBUtils.safeClose(con);
          }
          return null;
     }
}
