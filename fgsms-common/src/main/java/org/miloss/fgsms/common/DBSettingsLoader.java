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

import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValue;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransportAuthenticationStyle;
import org.apache.log4j.Level;

import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GlobalPolicy;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * Provides a simple mechanism for loading configuration information from the
 * fgsms Config Database, table: settings. Primarily used for agents that run on
 * the server, SLA plugins and or federation jobs that run on the fgsms server.
 *
 * This is only usable on the FGSMS server.
 *
 * @author AO
 */
public class DBSettingsLoader {

    /**
     * Gets the General Settings from the config database for fgsms Aux Services
     * and some agents
     *
     * if isShouldEncrypt is true, then the value is encrypted
     *
     * @param isPooled
     * @return key.name = value * returns null if if an error occurs
     */
    public static List<KeyNameValueEnc> GetPropertiesFromDB(boolean isPooled) {
        Connection con = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        List<KeyNameValueEnc> items = new ArrayList<KeyNameValueEnc>();
        try {
            if (isPooled) {
                con = Utility.getConfigurationDBConnection();
            } else {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
            }
            com = con.prepareStatement("select * from settings;");
            rs = com.executeQuery();
            while (rs.next()) {
                KeyNameValueEnc k = new KeyNameValueEnc();
                k.setShouldEncrypt(rs.getBoolean("isencrypted"));
                KeyNameValue p = new KeyNameValue();
                p.setPropertyKey(rs.getString("keycol"));
                p.setPropertyName(rs.getString("namecol"));
                p.setPropertyValue(new String(rs.getBytes("valuecol"), Charset.forName("UTF-8")));
                k.setKeyNameValue(p);
                items.add(k);
            }

        } catch (Exception ex) {
            Logger.getLogger(Utility.logname).log(Level.ERROR, "problem loading settings from the database", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        return items;
    }

    /**
     * Gets the General Settings from the config database for fgsms Aux Services
     * and some agents for items matching the specified key
     *
     * @param isPooled
     * @return key.name = value returns null if if an error occurs
     */
    public static List<KeyNameValueEnc> GetPropertiesFromDB(boolean isPooled, String keyfilter) {
        Connection con = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        List<KeyNameValueEnc> items = new ArrayList<KeyNameValueEnc>();

        try {

            if (isPooled) {
                con = Utility.getConfigurationDBConnection();
            } else {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
            }
            com = con.prepareStatement("select * from settings where keycol=?;");
            com.setString(1, keyfilter);
            rs = com.executeQuery();
            while (rs.next()) {
                KeyNameValueEnc k = new KeyNameValueEnc();
                k.setShouldEncrypt(rs.getBoolean("isencrypted"));
                KeyNameValue p = new KeyNameValue();
                p.setPropertyKey(rs.getString("keycol"));
                p.setPropertyName(rs.getString("namecol"));
                p.setPropertyValue(new String(rs.getBytes("valuecol"), Constants.CHARSET));
                k.setKeyNameValue(p);
                items.add(k);
            }

        } catch (Exception ex) {
            Logger.getLogger(Utility.logname).log(Level.ERROR, "problem loading " + keyfilter + " settings from the database", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return items;
    }

    /**
     * Gets a specific General Settings from the config database for fgsms Aux
     * Services and some agents for items matching the specified key and name
     *
     * @param isPooled
     * @return key.name = value returns null if if an error occurs
     */
    public static KeyNameValueEnc GetPropertiesFromDB(boolean isPooled, String keyfilter, String namefilter) {
        Connection con = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        KeyNameValueEnc k = new KeyNameValueEnc();
        try {

            if (isPooled) {
                con = Utility.getConfigurationDBConnection();
            } else {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
            }
            com = con.prepareStatement("select * from settings where keycol=? and  namecol=?;");
            com.setString(1, keyfilter);
            com.setString(2, namefilter);
            rs = com.executeQuery();
            if (rs.next()) {
                k.setShouldEncrypt(rs.getBoolean("isencrypted"));
                KeyNameValue p = new KeyNameValue();
                p.setPropertyKey(rs.getString("keycol"));
                p.setPropertyName(rs.getString("namecol"));
                p.setPropertyValue(new String(rs.getBytes("valuecol"), Constants.CHARSET));
                k.setKeyNameValue(p);
            }

        } catch (Exception ex) {
            Logger.getLogger(Utility.logname).log(Level.ERROR, "problem loading " + keyfilter + " " + namefilter + " settings from the database", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return k;

    }

    /**
     * returns a default Buller username and an encrypted password or NULL if
     * not defined Items are returned from the bueller table. As of RC6
     *
     * @param isPooled
     * @param url
     * @return
     */
    public static String[] GetDefaultBuellerCredentials(boolean isPooled) {
        Connection con = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        String[] data = new String[2];
        try {

            if (isPooled) {
                con = Utility.getConfigurationDBConnection();
            } else {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
            }

            com = con.prepareStatement("select * from settings where keycol=?;");
            com.setString(1, "Bueller");
            rs = com.executeQuery();
            //data[0] = rs.getString("keycol");
            //data[1] = rs.getString("valuecol");
            while (rs.next()) {
                if (rs.getString("namecol").equalsIgnoreCase("defaultUser")) {
                    data[0] = new String(rs.getBytes("valuecol"), Constants.CHARSET);
                }
                if (rs.getString("namecol").equalsIgnoreCase("defaultPassword")) {
                    data[1] = new String(rs.getBytes("valuecol"), Constants.CHARSET);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(Utility.logname).log(Level.ERROR, "problem loading default bueller credentials from the database", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

        if (Utility.stringIsNullOrEmpty(data[0]) || Utility.stringIsNullOrEmpty(data[1])) {
            return null;
        }
        return data;
    }

    /**
     * returns a username and an encrypted password for a specific url or NULL
     * if not credentials are available. this is typically a lowest permissions
     * account to ascertain status of http or jms urls if not defined. Items are
     * returned from the bueller table. As of RC6
     *
     * 6.2 added a third parameter equal to the value of the authentication
     * mechanism specified
     *
     * @param isPooled
     * @param url
     * @return
     */
    public static String[] GetCredentials(boolean isPooled, final String url) {
        Connection con = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        String[] data = null;
        try {

            if (isPooled) {
                con = Utility.getConfigurationDBConnection();
            } else {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
            }
            com = con.prepareStatement("select * from bueller where uri=?;");
            com.setString(1, url);
            rs = com.executeQuery();
            if (rs.next()) {
                data = new String[3];
                data[0] = rs.getString("username");
                data[1] = new String(rs.getBytes("pwdcol"), Constants.CHARSET);
                try {
                    int x = rs.getInt("authtype");
                    TransportAuthenticationStyle tas = TransportAuthenticationStyle.values()[x];
                    data[2] = tas.value();
                } catch (Exception e) {
                    data[2] = TransportAuthenticationStyle.NA.value();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Utility.logname).log(Level.ERROR, "problem loading credentials for " + url + " from the database", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return data;
    }

    public static GlobalPolicy GetGlobalPolicy(boolean pooled) {

        PreparedStatement comm = null;
        ResultSet results = null;
        GlobalPolicy ret = new GlobalPolicy();
        Connection con;
        if (pooled) {
            con = Utility.getConfigurationDBConnection();
        } else {
            con = Utility.getConfigurationDB_NONPOOLED_Connection();
        }
        try {
            DatatypeFactory df = DatatypeFactory.newInstance();

            comm = con.prepareStatement("Select * from GlobalPolicies;");

            /////////////////////////////////////////////
            //get the global policy for data retension
            /////////////////////////////////////////////
            results = comm.executeQuery();
            boolean foundPolicy = false;
            if (results.next()) {
                ret.setPolicyRefreshRate(df.newDuration(results.getLong("PolicyRefreshRate")));
                //moved this to settings list

                //response.getPolicy().setUDDIPublishRate(fac.newDuration(results.getLong("UDDIPublishRate")));
                ret.setRecordedMessageCap(results.getInt("RecordedMessageCap"));
                SecurityWrapper wrap = new SecurityWrapper(ClassificationType.fromValue(results.getString("classification")),
                        results.getString("caveat"));
                ret.setClassification(wrap);
                ret.setAgentsEnabled(results.getBoolean("agentsenable"));

                foundPolicy = true;
            }
            results.close();
            comm.close();

            if (!foundPolicy) {
                try {
                    comm = con.prepareStatement("INSERT INTO GlobalPolicies (PolicyRefreshRate, RecordedMessageCap, classification, agentsenable, caveat) "
                            + " VALUES (?, ?, ?, true, ?);");
                    comm.setLong(1, 30 * 60 * 100);
                    comm.setLong(2, 1024000);
                    //comm.setLong(3, 1024000);
                    comm.setString(3, "U");
                    comm.setString(4, "");
                    comm.execute();
                    ret.setRecordedMessageCap(1024000);
                    ret.setClassification(new SecurityWrapper(ClassificationType.U, "None"));
                    ret.setAgentsEnabled(true);
                } catch (SQLException ex) {
                    Logger.getLogger(Utility.logname).log(Level.ERROR, "error setting global policy", ex);
                }
            }
            comm.close();
            con.close();

            KeyNameValueEnc d = DBSettingsLoader.GetPropertiesFromDB(true, "UddiPublisher", "Interval");
            if (d != null && d.getKeyNameValue() != null) {
                try {
                    ret.setUDDIPublishRate(df.newDuration(Long.parseLong(d.getKeyNameValue().getPropertyValue())));
                } catch (Exception ex) {
                    ret.setUDDIPublishRate(df.newDuration(30 * 60 * 100));
                }
            } else {
                ret.setUDDIPublishRate(df.newDuration(30 * 60 * 100));
            }

        } catch (Exception ex) {
            Logger.getLogger(Utility.logname).log(Level.ERROR, "error getting global policy", ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        return ret;
    }
}
