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
 package org.miloss.fgsms.examples.export;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;

public class ExportDataFromDatabase {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            PrintUsage();
            return;
        }
        long maxrecords = Long.parseLong(args[1]);
        FileOutputStream fos = new FileOutputStream(args[2]);

        String username = getString("Username", "fgsms");
        String password = getStringPw();
        String url = getString("URL", "jdbc:postgresql://localhost:5432/fgsms_performance");
        Connection con = getConnection(url, username, password);
        if (args[0].equalsIgnoreCase("transactions")) {
            PreparedStatement cmd = con.prepareStatement("select * from rawdata limit ?");
            cmd.setLong(1, maxrecords);
            ResultSet rs = cmd.executeQuery();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append(rs.getString("transactionid")).append(",").append(rs.getString("uri")).append(",").append(rs.getLong("responsetimems")).append(",").append(rs.getLong("utcdatetime"));
                fos.write(sb.toString().getBytes());
                fos.write(System.getProperty("line.separator").getBytes(Constants.CHARSET));
                sb = null;
                //NOTE some of the columns are encrypted, to decrypt run the following
                //String clearText = Utility.DE("cipherText");
            }
            rs.close();
            cmd.close();
        } else if (args[0].equalsIgnoreCase("availability")) {
            PreparedStatement cmd = con.prepareStatement("select * from availability limit ?");
            cmd.setLong(1, maxrecords);
            ResultSet rs = cmd.executeQuery();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append(rs.getString("uri")).append(",").append(rs.getString("message")).append(",").append(rs.getLong("utcdatetime"));
                fos.write(sb.toString().getBytes());
                fos.write(System.getProperty("line.separator").getBytes(Constants.CHARSET));
                sb = null;
            }
            rs.close();
            cmd.close();
        } else if (args[0].equalsIgnoreCase("aggregate")) {
            PreparedStatement cmd = con.prepareStatement("select * from agg2");
            cmd.setLong(1, maxrecords);
            ResultSet rs = cmd.executeQuery();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append(rs.getString("uri")).append(",").append(rs.getString("soapaction")).append(",").append(rs.getLong("timerange")).append(",").append(rs.getLong("success"));
                fos.write(sb.toString().getBytes());
                fos.write(System.getProperty("line.separator").getBytes(Constants.CHARSET));
                sb = null;
            }
            rs.close();
            cmd.close();
        }
        con.close();
        fos.close();
    }

    private static String getString(String field, String defaults) {
        String s = null;
        //while (s == null || s.length() == 0) {
        System.out.print(field + "[" + defaults + "] = ");
        s = System.console().readLine();
        //}
        if (s == null || s.length() == 0) {
            return defaults;
        }
        return s;
    }

    private static String getStringPw() {
        System.out.print("Password = ");
        return new String(System.console().readPassword());
    }

    private static Connection getConnection(String url, String username, String password) throws Exception {
        //String url = "jdbc:postgresql://localhost:5432/fgsmsPerformance";
        Driver d = (Driver) Class.forName("org.postgresql.Driver").newInstance();
        return DriverManager.getConnection(url, username, password);

    }

    private static void PrintUsage() {
        System.out.println("This program will export data from fgsms's databases. It's just a sample application, so feel free to expand on this.");
        System.out.println("Usage");
        System.out.println("java -jar ExportDataFromDatabase.jar <datatype> <maxrecords> <outputfile>");
        System.out.println("datatype can be one of: ");
        System.out.println("\ttransactions");
        System.out.println("\tavailability");
        System.out.println("\taggregate");
    }
}
