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

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *  Command line utility to encrypt passwords and run database
 * connectivity tests for command line aux services (non pooled
 * connections)
 *
 * @author AO
 */
public class Main {

     public static void main(String[] args) {
          if (args==null || args.length == 0) {
               PrintUsage();
          } else if (args.length == 1 && args[0].equalsIgnoreCase("en")) {

               try {
                    System.out.print("Enter password: ");
                    String s = new String(System.console().readPassword());
                    System.out.print("Enter password to confirm: ");
                    String s2 = new String(System.console().readPassword());
                    if (s.equalsIgnoreCase(s2)) {
                         //String s = System.console().readLine();
                         System.out.println("Cipher Text: " + AES.EN(s));
                    } else {
                         System.out.println("Passwords do not match");
                    }
               } catch (Exception ex) {
                    System.out.println("Error caught encrypting string. This usually indicates that you have not yet installed the unlimited strenth Java Crypto Extensions. This can be downloaded at http://www.oracle.com/technetwork/java/javase/downloads/index.html " + ex.getMessage());
                    ex.printStackTrace();
               }
          } else if (args.length >= 1 && args[0].equalsIgnoreCase("gen")) {

               short keysize = 256;
               if (args.length > 1) {
                    try {
                         keysize = Short.parseShort(args[1]);
                    } catch (Exception ex) {
                         ex.printStackTrace();
                    }
               }
               String key = AES.GEN(keysize);
               if (key != null) {
                    System.out.println(key);
               } else {
                    System.out.println("Unable to generate key. This usually indicates that you have not yet installed the unlimited strenth Java Crypto Extensions. This can be downloaded at http://www.oracle.com/technetwork/java/javase/downloads/index.html ");
               }

          } else {
               PrintUsage();
          }

     }

     private static void PrintUsage() {
          System.out.println("Usage ....");
          System.out.println("java -jar fgsms.Common.jar en\t\tThis will encrypt a password using AES.");
          System.out.println("java -jar fgsms.Common.jar gen\t\tThis will generate a new 256 bit encryption key.");
          System.out.println("java -jar fgsms.Common.jar gen (size)\t\tThis will generate a new (size) bit encryption key.");
          //System.out.println("java -jar fgsms.Common.jar de \"someString\"           This will decrypt a password using AES.");
          // System.out.println("java -jar fgsms.Common.jar md5 \"someString\"          This will hash a password using MD5.");
     }
     /*
     * public static String getMD5(String input) { try { MessageDigest md =
     * MessageDigest.getInstance("MD5"); byte[] messageDigest =
     * md.digest(input.getBytes()); BigInteger number = new BigInteger(1,
     * messageDigest); String hashtext = number.toString(16); // Now we need to
     * zero pad it if you actually want the full 32 chars. while
     * (hashtext.length() < 32) { hashtext = "0" + hashtext; } return hashtext;
     * } catch (Exception e) { System.out.println("Error caught performing md5
     * calculation: " + e.getMessage()); } return ""; }
      */
}
