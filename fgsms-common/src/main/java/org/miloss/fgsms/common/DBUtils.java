/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Level;


/**
 *
 * @author AO
 */
public class DBUtils {

    static Logger log = Logger.getLogger("fgsms.DBUtils");

    public static void safeClose(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Throwable ex) {
                log.log(Level.INFO, null, ex);
            }
        }
    }
    
    public static void safeClose(ResultSet con) {
        if (con != null) {
            try {
                con.close();
            } catch (Throwable ex) {
                log.log(Level.INFO, null, ex);
            }
        }
    }
    
    public static void safeClose(PreparedStatement con) {
        if (con != null) {
            try {
                con.close();
            } catch (Throwable ex) {
                log.log(Level.INFO, null, ex);
            }
        }
    }
}
