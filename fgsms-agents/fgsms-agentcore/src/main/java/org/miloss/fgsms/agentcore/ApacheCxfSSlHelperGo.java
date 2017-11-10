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

package org.miloss.fgsms.agentcore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.miloss.fgsms.common.Utility;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 *This class will add the necessary keystore/trust store information into an Apache CXF Conduit
 * @author AO
 */
public class ApacheCxfSSlHelperGo {

    private static final Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    private static KeyStore createKeyStore(final URL url, final String password)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("Keystore url may not be null");
        }

        KeyStore keystore = KeyStore.getInstance("jks");
        InputStream is = null;
        try {
            is = url.openStream();
            keystore.load(is, password != null ? password.toCharArray() : null);
        } catch (Exception ex) {
            log.log(Level.WARN, "trouble opening keystore at " + url.toString(), ex);
        } finally {
            if (is != null) {
                try{is.close();}catch (Exception ex){}
            }
        }
        return keystore;
    }

    public static void doCXF(Object webserviceclient, ConfigLoader cfg) {

        try {
            //if (webserviceclient instanceof Client) {
                HTTPConduit http = (HTTPConduit) ClientProxy.getClient(webserviceclient).getConduit();
                TLSClientParameters parameters = new TLSClientParameters();
                parameters.setDisableCNCheck(false);
                KeyManager[] keymanagers = null;
                TrustManager[] trustmanagers = null;
                if (!Utility.stringIsNullOrEmpty(cfg.javaxkeystore)) {
                    KeyStore keystore = createKeyStore(new URL(cfg.javaxkeystore), Utility.DE(cfg.javaxkeystorepass));
                    keymanagers = getKeyManagers(keystore, Utility.DE(cfg.javaxkeystorepass));
                }
                if (!Utility.stringIsNullOrEmpty(cfg.javaxtruststore)) {
                    KeyStore keystore = createKeyStore(new URL(cfg.javaxtruststore), Utility.DE(cfg.javaxtruststorepass));
                    trustmanagers = getTrustManagers(keystore);
                }
                parameters.setKeyManagers(keymanagers);
                parameters.setTrustManagers(trustmanagers);
                TLSClientParameters tlsCP = new TLSClientParameters();

                tlsCP.setKeyManagers(keymanagers);
                tlsCP.setTrustManagers(trustmanagers);

                //  httpConduit.setTlsClientParameters(tlsCP);
                http.setTlsClientParameters(tlsCP);

            //}
        } catch (Exception ex) {
            log.log(Level.ERROR, "unable to initialize the CXF Conduit for configuration SSL information. The transaction may not go through", ex);
        }
    }

    private static TrustManager[] getTrustManagers(KeyStore trustStore)
            throws NoSuchAlgorithmException, KeyStoreException {
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory fac = TrustManagerFactory.getInstance(alg);
        fac.init(trustStore);
        return fac.getTrustManagers();
    }

    private static KeyManager[] getKeyManagers(KeyStore keyStore, String keyPassword)
            throws GeneralSecurityException, IOException {
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        char[] keyPass = keyPassword != null
                ? keyPassword.toCharArray()
                : null;
        KeyManagerFactory fac = KeyManagerFactory.getInstance(alg);
        fac.init(keyStore, keyPass);
        return fac.getKeyManagers();
    }
}
