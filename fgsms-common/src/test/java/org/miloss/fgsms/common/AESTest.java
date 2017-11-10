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

package org.miloss.fgsms.common;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Ignore;

/**
 *
 * @author AO
 */
public class AESTest {
    
    public AESTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    
    
    /**
     * Test of EN method, of class AES.
  
    @Test
    public void testHexEncoder() throws Exception {
        System.out.println("hex encoder test");
        String hello = "ABCDEF1234567890";
        byte[] bits = AES.hexToBytes(hello);
        
        String after = AES.asHex(bits);
        byte[] bits2 = AES.hexToBytes(hello);
        assertEquals(hello.toLowerCase(), after.toLowerCase());
        
        assertArrayEquals(bits,bits2);
    }   */
    @Ignore
    @Test
    public void test128On256bitJdk(){
        Assume.assumeTrue(AES.isJCEInstalled);
        String GEN = AES.GEN((short)128);   //generate a new 128bit key
        System.out.println("try 128 key on 256 jre");
        assertTrue(AES.validateKey(GEN));
        
    }
    
    @Ignore
    @Test
    public void test256On128bitJdk(){
        
        Assume.assumeFalse(AES.isJCEInstalled);
        System.out.println("try 256 key on 128 jre");
        String GEN = AES.GEN((short)256);   //generate a new 256bit key
        assertFalse(AES.validateKey(GEN));
        
    }

    /**
     * Test of EN method, of class AES.
     */
    @Test
    public void testEncryptionDecryption() throws Exception {
        System.out.println("Encryption and Decryption String data");
        String args = "pwd";
        String result = AES.EN(args);
        assertNotNull("resultant cipher text is null!", result);
        
        String clear = AES.DE(result);
        assertNotNull("resultant clear text is null!", result);
        assertEquals("clear-en-clear did not match", clear, args);
    }
    
    
       /**
     * Test of EN method, of class AES.
     */
    @Test
    public void testKeyGen() throws Exception {
        System.out.println("testKeyGen");
       // String args = pwdcol;
       //String key128 = AES.GEN((short)128);
       
        String result = AES.GEN();
        System.out.println(result);
        assertNotNull("resultant cipher text is null!", result);
        
        
        
         String args = "pwd";
        String result3 = AES.EN(args, result);
        assertNotNull("resultant cipher text is null!", result3);
        
        String clear = AES.DE(result3, result);
        assertNotNull("resultant clear text is null!", result3);
        assertEquals("clear-en-clear did not match", clear, args);
        
        
        
          String result2 = AES.GEN();
        System.out.println(result2);
        assertNotNull("resultant cipher text2 is null!", result2);
        
        assertNotSame("generation of the same key twice is unexpected!", result2, result);
      
    }
}
