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
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.dependency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.apache.log4j.PropertyConfigurator;
import org.miloss.fgsms.common.DBUtils;

/**
 * yet another quartz job that scans through the database looking for
 * correlations/transaction threads with more than 2 transactions associated
 * with it
 *
 * @author AO
 */
public class DependencyScanner {

    static final Logger log = Logger.getLogger("fgsms.DependencyScanner");

    public static void main(String args[]) {
          PropertyConfigurator.configure("log4j.properties");
        new DependencyScanner().go(false, 0);
    }

    public void go(boolean pooled, long timestampAtLastRunTime) {
        //loop through the last 10 minutes worth of traffic, looking for dependencies
        Connection con = null;
        if (pooled) {
            con = Utility.getPerformanceDBConnection();
        } else {
            con = Utility.getPerformanceDB_NONPOOLED_Connection();
        }
        long timer = System.currentTimeMillis();
        long threadsSearched=0;
        long dependenciesFound=0;
        PreparedStatement cmd=null;
        PreparedStatement c2=null;
        ResultSet rs=null;
        ResultSet r2=null;
        try {
            cmd = con.prepareStatement("select * from (select  threadid,count(threadid) as t from rawdata where utcdatetime >= ? group by threadid ) as foo where foo.t > 3;");
            cmd.setLong(1, timestampAtLastRunTime - (30 * 1000));
             rs = cmd.executeQuery();
            while (rs.next()) {
                threadsSearched++;
                try{
                c2 = con.prepareCall("select url,monitorsource,soapaction  from rawdata where threadid=? order by utcdatetime asc;");
                c2.setString(1, rs.getString("threadid"));
                //first hit is the initial request
                r2 = c2.executeQuery();
                record first = null;
                record second = null;
                while (r2.next()) {
                    if (first == null) {
                        //first run through this thread
                        first = new record();
                        first.url = r2.getString("url");
                        first.action = rs.getString("soapaction");
                        first.hostname = rs.getString("monitorsource");
                        if (r2.next()) {
                            if (second==null)
                                second=new record();
                            second.url = r2.getString("url");
                            second.action = rs.getString("soapaction");
                            second.hostname = rs.getString("monitorsource");
                        }
                    } else {
                        if (second==null)
                                second=new record();
                        second.url = r2.getString("url");
                        second.action = rs.getString("soapaction");
                        second.hostname = rs.getString("monitorsource");
                    }



                    //do some work
                    if (first == null || second == null) {
                        continue;
                    }
                    if ((!first.url.equalsIgnoreCase(second.url) || !(first.action.equalsIgnoreCase(second.action))) && first.hostname.equalsIgnoreCase(second.hostname)) {
                        dependenciesFound++;
                        RecordDependency(first.url, first.action, second.url, second.action, con);
                    }

                    //setup for the next iteration
                    first = second;
                    second = null;
                }
                }catch (Exception ex){
                    log.log(Level.ERROR, null, ex);
                } finally {
                    DBUtils.safeClose(r2);
                    DBUtils.safeClose(c2);
                }

            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        finally{
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        
        log.log(Level.INFO, "Web Service Threads Searched: " + threadsSearched + " Dependencies Found: " + dependenciesFound + " took " + (System.currentTimeMillis()-timer) + " ms to run");
    }

    private void RecordDependency(String url, String action, String url0, String action0, Connection con) {
        PreparedStatement cmd=null;
        try {
            cmd = con.prepareStatement("INSERT INTO dependencies(sourceurl, sourcesoapaction, destintationurl, destinationsoapaction)    VALUES (?, ?, ?, ?);");
            cmd.setString(1, url);
            cmd.setString(2, action);
            cmd.setString(3, url0);
            cmd.setString(4, action0);
            cmd.executeUpdate();
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(cmd);
        }
    }

    /**
     * internal class used for temporary record storage
     */
    class record {

        public String url;
        public String action;
        public String hostname;
    }
}
/*
 * from notes
 * select * from rawdata
 *       group by theadid
 *      order by timestamp asc
 * for each record pair
 *      if threadid matches and 
 *  (url is different or action is different ) && (hostname matches)
 *      record dependency
 */