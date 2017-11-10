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
package org.miloss.fgsms.smoke.post;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.status.OpStatusService;
import org.miloss.fgsms.services.interfaces.status.StatusServiceService;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * This executes some post build, post smoke test, tests related to the DCS
 * service and other components it's primarily meant to catch issues with agents
 * that are not easily detectable otherwise.
 *
 * @author AO
 */
public class SmokeTestPostActions {

   static  Properties p;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
	 p = new Properties();
        p.load(new FileInputStream(new File("../test.properties")));
	
        // TODO code application logic here
        if (!CheckForDCSAddDataRecords()) {
            System.out.println("DCS AddData Records found!");
        }
        if (!CheckForDCSAddMoreDataRecords()) {
            System.out.println("DCS AddMoreData Records found!");
        }
        if (!CheckForDCSRegistration()) {
            System.out.println("DCS not registered!");
        }
    }

    /**
     * returns true if the test passed (no records found)
     *
     * @return
     * @throws Exception
     */
    public static boolean CheckForDCSAddDataRecords() throws Exception {
        boolean ret = true;
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = con.prepareStatement("select agenttype from rawdata where soapaction=?");
        //cmd.setString(1, IpAddressUtility.modifyURL("http://localhost:8888/fgsmsServices/DCS", false));
        cmd.setString(1, "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddData");
        ResultSet rs = cmd.executeQuery();
        long count = 0;
        while (rs.next()) {
            count++;
            System.out.println("Found an AddData record from agent " + rs.getString(1));
        }
        rs.close();
        cmd.close();
        con.close();
        if (count > 0) {
            ret = false;
        }
        System.out.println("Total records " + count);
        return ret;
    }

    /**
     * returns true if the test passed (no records found)
     *
     * @return
     * @throws Exception
     */
    public static boolean CheckForDCSAddMoreDataRecords() throws Exception {
        boolean ret = true;
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = con.prepareStatement("select * from rawdata where soapaction=?");
        //cmd.setString(1, IpAddressUtility.modifyURL("http://localhost:8888/fgsmsServices/DCS", false));
        cmd.setString(1, "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddMoreData");
        ResultSet rs = cmd.executeQuery();
        long count = 0;
        while (rs.next()) {
            count++;
            System.out.println("Found an AddMoreData record from agent " + rs.getString("agenttype") + " " + rs.getString("uri") + " "
                    + rs.getString("transactionid")  + " " + rs.getLong("utcdatetime"));
        }
        rs.close();
        cmd.close();
        con.close();
        if (count > 0) {
            ret = false;
        }
        System.out.println("Total records " + count);
        return ret;
    }

    /**
     * returns true if the test passed (DCS is registered)
     *
     * @return
     * @throws Exception
     */
    public static boolean CheckForDCSRegistration() throws Exception {
        boolean ret = true;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement cmd = con.prepareStatement("select count(*) from servicepolicies where uri=?");
        cmd.setString(1, IpAddressUtility.modifyURL("http://localhost:8888/fgsmsServices/services/DCS", false));
        //cmd.setString(1, "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddMoreData");
        ResultSet rs = cmd.executeQuery();
        if (rs.next()) {
            long count = rs.getLong(1);
            if (count == 0) {
                ret = false;
            }
        } else {
            ret = false;
        }
        rs.close();
        cmd.close();
        con.close();
        return ret;
    }

    static boolean testCheckAPIDocDeployment() throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8080/fgsmsAPI").openConnection();
        con.setRequestMethod("GET");
        con.setUseCaches(false);
        con.connect();

        int code = con.getResponseCode();
        String msg = con.getResponseMessage();
        byte[] buffer = new byte[1024];
        InputStream is = con.getInputStream();
        int counter = is.read(buffer);
        while (counter >= 0) {
            String str = new String(buffer, 0, counter);
            System.out.println(str);
            counter = is.read(buffer);
        }
        con.disconnect();
        if (code != 200) {
            return false;
        }
        return true;
    }

    boolean testOpStat(String url) {
        try {
            
            org.miloss.fgsms.services.interfaces.status.StatusServiceService ss = new StatusServiceService();
            OpStatusService opStatusServiceBinding = ss.getOpStatusServiceBinding();
            BindingProvider bp = (BindingProvider) opStatusServiceBinding;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, p.getProperty("fgsmsadminuser"));
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, p.getProperty("fgsmsadminpass"));
            GetOperatingStatusRequestMessage req = new GetOperatingStatusRequestMessage();
            req.setClassification(new SecurityWrapper(ClassificationType.U, "none"));
            GetOperatingStatusResponseMessage operatingStatus = opStatusServiceBinding.getOperatingStatus(req);
            System.out.println("Status is " + operatingStatus.getStatusMessage() + " " + operatingStatus.isStatus());
            return (operatingStatus.isStatus());
        } catch (Exception ex) {
            System.err.println("Error caught accessing op stat at the url " + url);
            ex.printStackTrace();
            return false;
        }
    }
}
