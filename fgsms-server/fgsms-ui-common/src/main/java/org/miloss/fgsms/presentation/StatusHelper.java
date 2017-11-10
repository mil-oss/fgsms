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
package org.miloss.fgsms.presentation;

import java.net.URL;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 * Provides the Operating Status blurb on the gui  see
 * fgsmsstatus.jsp
 *
 * @author AO
 */
public class StatusHelper {

    public StatusHelper(String username, String password, org.miloss.fgsms.common.Constants.AuthMode mode, String keystore, String keystorepassword, String truststore, String truststorepassword) {
        try {
            this.username = username;
            this.password = password;
            this.mode = mode;
            
        } catch (Exception ex) {
            log.log(Level.WARN, "error initializing ssl sockets ", ex);
        }
    }
    private static final Logger log = LogHelper.getLog();
    
    private String username;
    private String password;
    private org.miloss.fgsms.common.Constants.AuthMode mode = org.miloss.fgsms.common.Constants.AuthMode.None;

    /**
     * determines if an fgsms service is currently accessible. not for use
     * with other services.
     *
     * @param endpoint
     * @return
     */
    public String sendGetRequest(String endpoint) {
        //   String result = null;
        if (endpoint.startsWith("http")) {
            // Send a GET request to the servlet
            try {

                URL url = new URL(endpoint);
                int port = url.getPort();
                if (port <= 0) {
                    if (endpoint.startsWith("https:")) {
                        port = 443;
                    } else {
                        port = 80;
                    }
                }
                
                HttpClientBuilder create = HttpClients.custom();
                
                if (mode == org.miloss.fgsms.common.Constants.AuthMode.UsernamePassword) {
                    CredentialsProvider credsProvider = new BasicCredentialsProvider();
                    credsProvider.setCredentials(
                            new AuthScope(url.getHost(), port),
                            new UsernamePasswordCredentials(username, Utility.DE(password)));
                    create.setDefaultCredentialsProvider(credsProvider);;
                          
                    
                }
                CloseableHttpClient c = create.build();
                
                CloseableHttpResponse response = c.execute(new HttpHost(url.getHost(), port), new HttpGet(endpoint));
                
                c.close();
                int status=response.getStatusLine().getStatusCode();
                if (status == 200) {
                    return "OK";
                }
                return String.valueOf(status);

            } catch (Exception ex) {
                Logger.getLogger(Helper.class).log(Level.WARN, "error fetching http doc from " + endpoint + ex.getLocalizedMessage());
                return "offline";
            }
        }  else {
            return "undeterminable";
        }
    }
}
