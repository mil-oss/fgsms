/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.tomcat.jdbcp;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import org.miloss.fgsms.common.AES;

/**
 * Automagically decrypts passwords for JDBC connections
 * @author alex.oree
 */
public class EncryptedConnectionFactory extends org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory {

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
        Object o = super.getObjectInstance(obj, name, nameCtx, environment);
        if (o != null) {
            BasicDataSource ds = (BasicDataSource) o;
            if (ds.getPassword() != null && ds.getPassword().length() > 0) {
                ds.setPassword(AES.DE(ds.getPassword()));
            }
            return ds;
        } else {
            return null;
        }
    }
}
