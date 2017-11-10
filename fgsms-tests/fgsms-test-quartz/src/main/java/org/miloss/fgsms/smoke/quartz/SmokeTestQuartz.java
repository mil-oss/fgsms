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
package org.miloss.fgsms.smoke.quartz;

import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.miloss.fgsms.auxstatus.QuartzStatus;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author AO
 */
public class SmokeTestQuartz {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        //connect to aux services, get list of quartz jobs that are running
        //compare with expected list
        String b = new SmokeTestQuartz().TestQuartz("http://localhost:8888/fgsmsServices/admin/quartz-xml.jsp", "", "");
        if (b.length() == 0) {
            System.out.println("Pass");
        } else {
            System.out.println("Fail! " + b);
        }
    }

    /**
     * returns null if OK, otherwise an error message
     *
     * @param url
     * @return
     * @throws Exception
     */
    public String TestQuartz(String url, String username, String password) throws Exception {

        org.apache.commons.httpclient.HttpClient c = new HttpClient();
        GetMethod get = new GetMethod(url);
        get.setURI(new URI(url));
        get.setDoAuthentication(true);
        c.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        int executeMethod = c.executeMethod(get);
        if (executeMethod == 200) {
            String responseBodyAsStream = get.getResponseBodyAsString();
            if (responseBodyAsStream.length() > 100) {
                System.out.println("Response as follows " + responseBodyAsStream.substring(0, 100));
            } else {
                System.out.println("Response as follows " + responseBodyAsStream);
            }
            JAXBContext ctx = JAXBContext.newInstance(org.miloss.fgsms.auxstatus.Group.class, org.miloss.fgsms.auxstatus.GroupNames.class, org.miloss.fgsms.auxstatus.Job.class, org.miloss.fgsms.auxstatus.Jobs.class, org.miloss.fgsms.auxstatus.QuartzStatus.class, org.miloss.fgsms.auxstatus.Trigger.class, org.miloss.fgsms.auxstatus.TriggerGroup.class, org.miloss.fgsms.auxstatus.Triggers.class);
            Unmarshaller m = ctx.createUnmarshaller();
            Object unmarshal = m.unmarshal(new StringReader(responseBodyAsStream));
            QuartzStatus qs = (QuartzStatus) unmarshal;
            System.out.println(qs.getVersion());
            boolean ok = true;
            String err = "";
            if (!qs.getVersion().startsWith("1.8")) {
                err += ("wrong version returned! " + qs.getVersion() + " i'm expecting a 1.8.x variant");
            }
            if (qs.getJobsExecuted() <= 01) {
                err += ("no jobs executed! ");
            }
            if (qs.getJobs().getJob().size() < 10) {
                err += ("Less jobs running an expected! ");
            } else {
                System.out.println("Jobs running " + qs.getJobs().getJob().size());
            }
            if (qs.getTriggers().getTriggerGroup().isEmpty()) {
                err += ("No trigger groups! ");
            } else {
                for (int i = 0; i < qs.getTriggers().getTriggerGroup().size(); i++) {
                    if (qs.getTriggers().getTriggerGroup().get(i).getName().equalsIgnoreCase("fgsmsAuxServicesTriggers")) {
                        if (qs.getTriggers().getTriggerGroup().get(i).getTrigger().isEmpty()) {
                            err += ("No triggers are defined for aux services. this probably means that none of them are running! ");
                        }
                        if (qs.getTriggers().getTriggerGroup().get(i).getTrigger().size() < 11) {
                            err += ("Less triggers defined that expected");
                        }
                    }
                }
            }
            return err;
        }
        return ("unable to authenticate or an error occured, status code " + executeMethod);

    }

}
