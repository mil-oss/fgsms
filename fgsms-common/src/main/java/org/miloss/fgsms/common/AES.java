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
package org.miloss.fgsms.common;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.codec.Base64;

;

/**
 * This program uses a AES key, retrieves its raw bytes, and then reinstantiates
 * a AES key from the key bytes. The reinstantiated key is used to initialize a
 * AES cipher for encryption and decryption. source :
 * http://java.sun.com/developer/technicalArticles/Security/AES/AES_v1.html
 *
 * This is the encryption/decryption module of fgsms. used for passwords and
 * certain database columns
 */
public class AES {

    public final static boolean isJCEInstalled;

    public static final String logname = "fgsms.Utility";
    public static final Logger log = Logger.getLogger(logname);

    //pist don't tell anyone, but this is the default key in case all other mechanisms fail to load a valid key
    // private final static String something256 = "gaPVPK6SONri9LCeWJlB2cUMPQL8a3JLegXaJObWj0Q=:F9798Q6FY+YNEaZz5xa6c8OkXk9yp8BcBpX/5avR/Oc=";
    private final static String something128 = "m0b3b70CH0P4Ua7rxksnRw==:o1QNVJ/KY453VEB3VsQn39tQpu5yKovGAH/LQvyzawI=";

    /**
     * Generate a new AES 256 bit encryption key. Once generated, this key can
     * be used to replace the default key.
     *
     * @return
     */
    public static String GEN() {

        return GEN((short) 128);
    }

    /**
     * Generate a new AES 256 bit encryption key. Once generated, this key can
     * be used to replace the default key.
     *
     * @param keysize must be a supported key size, using 128 or 256
     * @return
     */
    public static String GEN(final short keysize) {
        try {
            return AesCbcWithIntegrity.generateKey().toString();
        } catch (GeneralSecurityException ex) {
            log.log(Level.ERROR, "There was an error generating key, this could indicate that you're making a 256 bit key on a system that does not have the Java Crypto Extensions installed. . Is JCE installed? " + (isJCEInstalled ? "yes " : "no ") + ex.getMessage());
            log.log(Level.DEBUG, "error generating key, this could indicate that you're making a 256 bit key on a system that does not have the Java Crypto Extensions installed. " + ex.getMessage(), ex);
        }
        return null;
    }

    private URI getUrl(String FileName) {
        URL pcsurl = null;
        if (pcsurl == null) {
            try {
                pcsurl = Thread.currentThread().getContextClassLoader().getResource(FileName);
                log.log(Level.DEBUG, "Loading encryption key from " + pcsurl.toString());
            } catch (Exception ex) {
                log.log(Level.DEBUG, "not found", ex);
            }
        }
        if (pcsurl == null) {
            try {
                pcsurl = Thread.currentThread().getContextClassLoader().getResource("/" + FileName);
                log.log(Level.DEBUG, "Loading encryption key from " + pcsurl.toString());
            } catch (Exception ex) {
                log.log(Level.DEBUG, "not found", ex);
            }
        }

        if (pcsurl == null) {
            try {
                pcsurl = new URL(FileName);
                log.log(Level.DEBUG, "Loading encryption key from " + pcsurl.toString());
            } catch (Exception ex) {
                log.log(Level.DEBUG, "not found", ex);
            }
        }

        if (pcsurl == null) {
            try {
                pcsurl = AES.class.getClassLoader().getResource(FileName);
                log.log(Level.DEBUG, "Loading encryption key from " + pcsurl.toString());
            } catch (Exception ex) {
                log.log(Level.DEBUG, "not found", ex);
            }
        }
        if (pcsurl == null) {
            try {
                pcsurl = AES.class.getClassLoader().getResource("/" + FileName);
                log.log(Level.DEBUG, "Loading encryption key from " + pcsurl.toString());
            } catch (Exception ex) {
                log.log(Level.DEBUG, "not found", ex);
            }
        }
        try {
            if (pcsurl != null) {
                return pcsurl.toURI();
            }
        } catch (URISyntaxException ex) {
            log.log(Level.DEBUG, null, ex);
        }
        return null;
    }

    private static String readAllText(File file) {
        if (file == null || !file.exists()) {
            log.log(Level.WARN, "Referenced key does not exist" + file.getAbsolutePath());
            return null;
        }
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            String str = readAllText(stream);
            stream.close();
            return (str);
        } catch (Exception e) {
            log.log(Level.DEBUG, "error reading key", e);
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "error reading key", ex);
                }
            }
        }

    }

    private static String readAllText(InputStream stream) {
        try {
            int size = 1024;
            byte chars[] = new byte[size];
            int k = stream.read(chars);
            StringBuilder str = new StringBuilder();
            while (k > 0) {

                for (int i = 0; i < k; i++) {
                    str.append((char) chars[i]);
                }
                k = stream.read(chars);
            }
            stream.close();
            return (str.toString());
        } catch (Exception e) {
            log.log(Level.DEBUG, "error reading key", e);
            return null;
        }
    }

    /* 
     * 
     * note we don't actually store the key in memory at any point in time
     * double edged sword, 1) increases IO, 2) decreases risk
     */
    private static String loadKey() {
        String key = null;
        if (System.getenv("fgsms.keyFile") != null) {
            //attempt load from system environment variable, kind of risky but ok
            key = readAllText(new File(System.getenv("fgsms.keyFile")));
        } else if (key == null && System.getProperty("fgsms.keyFile") != null) {
            //attempt to load from system property
            key = readAllText(new File(System.getProperty("fgsms.keyFile")));
        } else if (key == null) {

            //if (isJCEInstalled)
            {
                //prefer the strong encryption, if it's available
                try {
                    File f = new File(new AES().getUrl("fgsms-aes128.key"));
                    key = readAllText(f);
                } catch (Exception ex) {
                    log.log(Level.DEBUG,null,ex);
                }
                if (key == null) {
                    try {
                        File f = new File(AES.class.getResource("fgsms-aes128.key").toURI());
                        key = readAllText(f);
                    } catch (Exception ex) {
                        log.log(Level.DEBUG,null,ex);
                    }
                }
                if (key == null) {
                    try {
                        InputStream is = AES.class.getResourceAsStream("fgsms-aes128.key");
                        key = readAllText(is);
                        is.close();
                    } catch (Exception ex) {
                        log.log(Level.DEBUG,null,ex);
                    }
                }
                //try to load as a class resource

            }
            /*else {
                try {
                    File f = new File(new AES().getUrl("org/miloss/fgsms/common/aes128.key"));
                    key = readAllText(f);
                } catch (Exception e) {

                }
            }*/
        }
        if (key != null) {
            log.log(Level.DEBUG, "key loaded from file");
            return (key);
        } else {
            log.log(Level.WARN, "Could not load the key, using hard coded default key instead. This should be considered a security risk.");

            return (something128);

        }
    }

    public static String EN(final String cleartext) throws Exception {
        return EN(cleartext, (loadKey()));
    }

    public static String EN(final String cleartext, final String key) throws Exception {
        AesCbcWithIntegrity.SecretKeys skey = AesCbcWithIntegrity.keys(key);
        AesCbcWithIntegrity.CipherTextIvMac encrypt = AesCbcWithIntegrity.encrypt(cleartext, skey);
        return encrypt.toString();
    }

    public static String DE(final String ciphertext) throws Exception {

        return DE(ciphertext, (loadKey()));
    }

    public static String DE(final String ciphertext, final String key) throws Exception {
        AesCbcWithIntegrity.SecretKeys skey = AesCbcWithIntegrity.keys(key);
        AesCbcWithIntegrity.CipherTextIvMac civ = new AesCbcWithIntegrity.CipherTextIvMac(ciphertext);
        return AesCbcWithIntegrity.decryptString(civ, skey);
    }

    /**
     * return true is the supplied key is a valid aes key
     *
     * @param key
     * @return
     */
    public static boolean validateKey(final String key) {
        try {
            String src = "abcdefghijklmopqrstuvwxyz123567890!@#$%^&*()_+{}|:\">?<,";
            String x = EN(src, key);
            String y = DE(x, key);
            //if the sample text is encryptable and decryptable, and it was actually encrypted
            return y.equals(src) && !x.equals(y);
        } catch (Throwable ex) {
//            log.log(Level.WARN, null, ex);
            return false;
        }
    }

    /*
     * public static OutputStream DE(InputStream ciphertext) throws Exception {
     * byte[] raw =//skey.getEncoded(); hexToBytes(something); // SecretKeySpec
     * skeySpec = new SecretKeySpec(raw, "AES"); Cipher cipher =
     * Cipher.getInstance("AES"); cipher.init(Cipher.DECRYPT_MODE, skeySpec);
     * CipherInputStream cis = new CipherInputStream(ciphertext, cipher);
     * cis.read(raw);
     *
     * byte[] original = cipher.doFinal(hexToBytes(ciphertext)); return new
     * String(original); }
     */
    static {
        String key = GEN((short) 256);
        isJCEInstalled = validateKey(key);
    }
}
