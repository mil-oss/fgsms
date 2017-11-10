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
package org.miloss.fgsms.smoke.auth;

import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author AO
 */
public class SmokeTestAuth {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        //connect to aux services, get list of quartz jobs that are running
        //compare with expected list
        int b = new SmokeTestAuth().testAuth("http://localhost:8888/fgsmsServices/DCS/?wsdl", "", "");
        if (b >= 401 && b < 500) {
            System.out.println("Pass");
        } else {
            System.out.println("Fail! " + b);
        }
    }

    /**
     * returns http status code
     *
     * @param url
     * @return
     * @throws Exception
     */
    public int testAuth(String url, String username, String password) throws Exception {

        org.apache.commons.httpclient.HttpClient c = new HttpClient();
        GetMethod get = new GetMethod(url);
        get.setURI(new URI(url));
        get.setDoAuthentication(true);
        if (username != null && password != null) {
            c.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }

        int executeMethod = c.executeMethod(get);

        String responseBodyAsStream = get.getResponseBodyAsString();
        System.out.println("Response code " + executeMethod);
        if (responseBodyAsStream.length() > 100) {
            System.out.println("Response as follows " + responseBodyAsStream.substring(0, 100));
        } else {
            System.out.println("Response as follows " + responseBodyAsStream);
        }
        return executeMethod;

    }

}
