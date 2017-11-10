package org.miloss.fgsms.quartz;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.miloss.fgsms.common.DBUtils;
import org.quartz.utils.ConnectionProvider;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author AO
 */
public class EncryptedConnectionProvider implements ConnectionProvider {

    @Override
    public Connection getConnection() throws SQLException {
        Exception root = null;
        try {
            javax.naming.Context ctx1 = new javax.naming.InitialContext();
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:fgsmsQuartz");
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
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("java:/comp/env/jdbc/fgsmsQuartz");
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
            javax.sql.DataSource ds = (javax.sql.DataSource) ctx1.lookup("jdbc/fgsmsQuartz");
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
        if (root!=null)
            root.printStackTrace();
        return null;
    }

    @Override
    public void shutdown() throws SQLException {
        
    }

}
