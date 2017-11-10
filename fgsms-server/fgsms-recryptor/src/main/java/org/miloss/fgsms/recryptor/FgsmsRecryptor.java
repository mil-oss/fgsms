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
package org.miloss.fgsms.recryptor;

import java.sql.*;
import org.miloss.fgsms.common.AES;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;

/**
 * This tool is a migration tool that will allow system administrator to decrypt
 * data from one encryption and encrypt with a new one. Knowledge of both keys
 * is required
 *
 * @author AO
 */
public class FgsmsRecryptor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new FgsmsRecryptor().Start(args);
    }

    private void Start(String[] args) throws Exception {
        PrintUsage();
        GetInputData();
        EstimateRecords();
        ProcessConfig();
        ProcessPerformance();

    }
    private Connection con = null;
    Driver d = null;
    private String oldkey = "";
    private String newkey = "";
    private String databaseurl_perf = "jdbc:postgresql://localhost:5432/fgsmsPerformance";
    private String databaseurl_config = "jdbc:postgresql://localhost:5432/fgsmsConfig";
    private String databasedriver = "org.postgresql.Driver";
    private String databaseusername = "fgsms";
    private String databasepassword = "";

    private void PrintUsage() {
        System.out.println("This tool can be used to change encryption keys used by fgsms for storing data at rest in the database.");
        System.out.println("Prerequists: ");
        System.out.println("\tAll fgsms servers must be stopped");
        System.out.println("\tThe Java Crypt Extensions must be installed on this machine's JRE/JDK");
        System.out.println("\tYou must have credentials for the database server with sufficent access writes to fgsms's Configuration and Performance databases.");
        System.out.println("\tYou must have possess the existing key");
        System.out.println("\tYou can provide the new key, or i can generate one for you");
        System.out.println("\tAfter completion, the key file must be revised and all instances of fgsms.Common.jar must be replaced on all fgsms servers (agents are not affected)");
        System.out.println("\tYou can possess the password used for fgsms's agents");
        System.out.println("\t(Note: agents running on the fgsms server will need to have the encrypted passwords recryted.) Agents running on other machines are a different story");
    }

    private void GetInputData() {
        System.out.print("Enter old key: ");
        oldkey = System.console().readLine();
        System.out.print("Enter new key: ");
        newkey = System.console().readLine();
        boolean ok = true;
        if (AES.validateKey(oldkey)) {
            System.out.println("Old key validated");
        } else {
            ok = false;
            System.out.println("Old key is invalid");
        }
        if (AES.validateKey(newkey)) {
            System.out.println("New key validated");
        } else {
            System.out.println("New key is invalid");
            ok = false;
        }
        System.out.print("Database Config URL: [" + databaseurl_config + "]: ");
        String x = "";
        x = System.console().readLine();
        if (!Utility.stringIsNullOrEmpty(x)) {
            databaseurl_config = x;
        }
        System.out.print("Database Perf URL: [" + databaseurl_perf + "]: ");
        x = "";
        x = System.console().readLine();
        if (!Utility.stringIsNullOrEmpty(x)) {
            databaseurl_perf = x;
        }
        System.out.print("Database driver: [" + databasedriver + "]: ");
        x = System.console().readLine();
        if (!Utility.stringIsNullOrEmpty(x)) {
            databasedriver = x;
        }
        System.out.print("Database username: [" + databaseusername + "]: ");
        x = System.console().readLine();
        if (!Utility.stringIsNullOrEmpty(x)) {
            databaseusername = x;
        }
        System.out.print("Database password: ");
        x = System.console().readLine();
        if (!Utility.stringIsNullOrEmpty(x)) {
            databaseusername = x;
        }

        if (!ok) {
            System.exit(1);
        }
    }

    private void ProcessPerformance() throws Exception {
        int x = 0;
        Driver d = (Driver) Class.forName(databasedriver).newInstance();
        Connection con = DriverManager.getConnection(
                databaseurl_perf,
                databaseusername, databasepassword);
        Connection con2 = DriverManager.getConnection(
                databaseurl_perf,
                databaseusername, databasepassword);
        PreparedStatement com = con.prepareStatement("select reportcontents,reportid from arsjobs");
        ResultSet rs = com.executeQuery();
        while (rs.next()) {
            byte[] bits = rs.getBytes(1);
            PreparedStatement com2 = con2.prepareStatement("Update arsjobs set reportcontents=? where reportid=?");
            com2.setString(2, rs.getString(2));
            com2.setBytes(1, AES.EN(AES.DE(new String(bits, Constants.CHARSET), oldkey), newkey).getBytes(Constants.CHARSET));
            com2.executeUpdate();
            com2.close();
            x++;
        }

        com.close();
        System.out.println("reports updated " + x);
        x = 0;

        com = con.prepareStatement("select requestheaders,transactionid from rawdata where requestheaders is not null");
        rs = com.executeQuery();
        while (rs.next()) {
            byte[] bits = rs.getBytes(1);
            PreparedStatement com2 = con2.prepareStatement("Update rawdata set requestheaders=? where transactionid=?");
            com2.setString(2, rs.getString(2));
            com2.setBytes(1, AES.EN(AES.DE(new String(bits, Constants.CHARSET), oldkey), newkey).getBytes(Constants.CHARSET));
            com2.executeUpdate();
            com2.close();
            x++;
        }
        com.close();
        System.out.println("transaction request headers" + x);
        x = 0;

        com = con.prepareStatement("select responseheaders,transactionid from rawdata where responseheaders is not null");
        rs = com.executeQuery();
        while (rs.next()) {
            byte[] bits = rs.getBytes(1);
            PreparedStatement com2 = con2.prepareStatement("Update rawdata set responseheaders=? where transactionid=?");
            com2.setString(2, rs.getString(2));
            com2.setBytes(1, AES.EN(AES.DE(new String(bits, Constants.CHARSET), oldkey), newkey).getBytes(Constants.CHARSET));
            com2.executeUpdate();
            com2.close();
            x++;
        }
        com.close();
        System.out.println("transaction responseheaders" + x);
        x = 0;

        com = con.prepareStatement("select requestxml,transactionid from rawdata where requestxml is not null");
        rs = com.executeQuery();
        while (rs.next()) {
            byte[] bits = rs.getBytes(1);
            PreparedStatement com2 = con2.prepareStatement("Update rawdata set requestxml=? where transactionid=?");
            com2.setString(2, rs.getString(2));
            com2.setBytes(1, AES.EN(AES.DE(new String(bits, Constants.CHARSET), oldkey), newkey).getBytes(Constants.CHARSET));
            com2.executeUpdate();
            com2.close();
            x++;
        }
        com.close();
        System.out.println("transaction requestxml" + x);
        x = 0;

        com = con.prepareStatement("select responsexml,transactionid from rawdata where responsexml is not null");
        rs = com.executeQuery();
        while (rs.next()) {
            byte[] bits = rs.getBytes(1);
            PreparedStatement com2 = con2.prepareStatement("Update rawdata set responsexml=? where transactionid=?");
            com2.setString(2, rs.getString(2));
            com2.setBytes(1, AES.EN(AES.DE(new String(bits, Constants.CHARSET), oldkey), newkey).getBytes(Constants.CHARSET));
            com2.executeUpdate();
            com2.close();
            x++;
        }
        com.close();
        System.out.println("transaction responsexml" + x);
        x = 0;

        con.close();
        con2.close();
    }

    private void ProcessConfig() throws Exception {

        int x = 0;
        Driver d = (Driver) Class.forName(databasedriver).newInstance();
        Connection con = DriverManager.getConnection(
                databaseurl_config,
                databaseusername, databasepassword);
        Connection con2 = DriverManager.getConnection(
                databaseurl_config,
                databaseusername, databasepassword);
        PreparedStatement com = con.prepareStatement("select pwdcol, uri from bueller");
        ResultSet rs = com.executeQuery();
        while (rs.next()) {
            byte[] bits = rs.getBytes(1);
            PreparedStatement com2 = con2.prepareStatement("Update bueller set  pwdcol=? where uri=?");
            com2.setString(2, rs.getString(2));
            com2.setBytes(1, AES.EN(AES.DE(new String(bits, Constants.CHARSET), oldkey), newkey).getBytes(Constants.CHARSET));
            com2.executeUpdate();
            com2.close();
            x++;
        }

        rs.close();
        com.close();
        System.out.println("bueller passwords updated " + x);
        x = 0;

        con.prepareStatement("select * from settings where isencrypted = true");
        rs = com.executeQuery();
        while (rs.next()) {
            byte[] bits = rs.getBytes("valuecol");
            PreparedStatement com2 = con2.prepareStatement("Update settings set  valuecol=? where keycol=? and namecol=? ");

            com2.setString(2, rs.getString("keycol"));
            com2.setString(2, rs.getString("namecol"));
            com2.setBytes(1, AES.EN(AES.DE(new String(bits, Constants.CHARSET), oldkey), newkey).getBytes(Constants.CHARSET));
            com2.executeUpdate();
            com2.close();
            x++;
        }
        rs.close();
        com.close();
        System.out.println("encrypted settings updated " + x);
        x = 0;

        con.close();
        con2.close();
    }

    private void EstimateRecords() {
    }
}
