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

import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Utility;
import java.net.InetAddress;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author *
 */
public class IpAddressUtilityTest {

    public IpAddressUtilityTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of modifyURL method, of class IpAddressUtility.
     */
    @Test
    public void testModifyURL() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        System.out.println("ModifyURL " + url + " to " + exp);
        //String expResult = "";
        String result = IpAddressUtility.modifyURL(url, false);
        assertEquals(result + " should be " + url + " service test 1", exp, result);
        
    }    
    @Test
    public void testModifyURL2() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        System.out.println("ModifyURL " + url + " to " + exp);
        //String expResult = "";
        String result = IpAddressUtility.modifyURL(url, false);
        
        
        
        
        
        url = "http://" + myhostname + ":8080/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 2", exp, result);
        
         }    
    @Test
    public void testModifyURL3() {
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        //String expResult = "";
        String result = IpAddressUtility.modifyURL(url, false);
        
        url = "http://127.0.0.1:8080/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 3", exp, result);

        
         }    
    @Test
    public void testModifyURL4() {
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
        
        
        url = "http://127.0.0.1:8080/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 4", exp, result);
     }    
    
    @Test
    public void testModifyURL5() {
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        url = "http://" + myip + ":8080/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 5", exp, result);
     }    
    
    
    
    
    
    
    
    
    @Test
    public void testModifyURL6() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        //client test
        url = "http://127.0.0.1:8080/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " client test 6", exp, result);
     }    
    @Test
    public void testModifyURL7() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        //client test
        url = "http://localhost:8080/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " client test 7", exp, result);
}    
    @Test
    public void testModifyURL8() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        //client test
        url = "http://localhost:8080/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " client test 8", exp, result);
}    
    @Test
    public void testModifyURL9() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        //client test
        url = "http://" + myip + ":8080/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " client test 9", exp, result);
}    
    @Test
    public void testModifyURL9_1() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        url = "http://www.google.com:8080/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 10", "http://www.google.com:8080/something", url);

}    
    @Test
    public void testModifyURL10() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        url = "http://254.254.254.254:8080/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 10-1", result, url);




}    
    @Test
    public void testModifyURL11() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":8080/something";
        String result="";
    
        //NO PORT DEFINED

        url = "http://localhost/something";
        exp = "http://" + myhostname + ":80/something";
        //String expResult = "";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 11", exp, result);

        }    
    @Test
    public void testModifyURL12() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    
        
        url = "http://" + myhostname + "/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 12", exp, result);
}    
    @Test
    public void testModifyURL13() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    
        url = "http://localhost/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 13", exp, result);
}    
    @Test
    public void testModifyURL14() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    
        url = "http://127.0.0.1/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 14", exp, result);
}    
    @Test
    public void testModifyURL15() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    
        url = "http://" + myip + "/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 15", exp, result);
}    
    @Test
    public void testModifyURL16() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    
        //client test
        url = "http://127.0.0.1/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 16", exp, result);
}    
    @Test
    public void testModifyURL17() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    

        //client test
        url = "http://localhost/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 17", exp, result);
}    
    @Test
    public void testModifyURL18() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    
        //client test
        url = "http://localhost/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 18", exp, result);
}    
    @Test
    public void testModifyURL19() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "http://" + myhostname + ":80/something";
        String result="";
    
        //client test
        url = "http://" + myip + "/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 19", exp, result);





        //NO PORT DEFINED
}    
    @Test
    public void testModifyURL20() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
    

        //NO PORT DEFINED

        url = "https://localhost/something";
        exp = "https://" + myhostname + ":443/something";
        //String expResult = "";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 20", exp, result);
}    
    @Test
    public void testModifyURL21() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
    
        url = "https://" + myhostname + "/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 21", exp, result);
}    
    @Test
    public void testModifyURL22() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
        url = "https://localhost/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 22", exp, result);
}    
    @Test
    public void testModifyURL23() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
        url = "https://127.0.0.1/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 23", exp, result);
}    
    @Test
    public void testModifyURL24() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
        url = "https://" + myip + "/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, service 24", exp, result);
}    
    @Test
    public void testModifyURL25() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
        //client test
        url = "https://127.0.0.1/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 25", exp, result);
}    
    @Test
    public void testModifyURL26() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";

        //client test
        url = "https://localhost/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 26", exp, result);
}    
    @Test
    public void testModifyURL27() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
        //client test
        url = "https://localhost/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 27", exp, result);
}    
    @Test
    public void testModifyURL28() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName().toLowerCase();
        } catch (Exception ex) {
        }

        String url = "http://localhost:8080/something";
        String exp = "https://" + myhostname + ":443/something";
        String result="";
        //client test
        url = "https://" + myip + "/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals("no port defined, client 28", exp, result);





    }
    
    
    
    @Test
    public void testModifyURL29() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName();
        } catch (Exception ex) {
        }

        String url = "http://"+ myhostname +":8080/something";
        String exp = "http://" + myhostname.toLowerCase() + ":8080/something";
        String result="";
    
        url = "http://" + myip + ":8080/something";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 5", exp, result);
     }    
    
    
        @Test
    public void testModifyURL30() {
        
        String myip = "";
        String myhostname = "";
        try {
            // Get hostname by textual representation of IP address
            InetAddress addr = InetAddress.getLocalHost();
            myip = addr.getHostAddress();
            myhostname = addr.getHostName();
        } catch (Exception ex) {
        }

        String url = "http://"+ myhostname +":8080/something";
        String exp = "http://" + myhostname.toLowerCase() + ":8080/something";
        String result="";
    
        url = "http://" + myip + ":8080/something";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 5", exp, result);
     }    
        
        
              @Test
    public void testModifyURL31() {
        
        String myip = "";
        
        
        String url = "https://msuddi.example.com/uddipublic/inquiry.asmx";
        String exp = "https://msuddi.example.com:443/uddipublic/inquiry.asmx";
        String result="";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 5", exp, result);
     }    
              
                            @Test
    public void testModifyURL32() {
        
        String myip = "";
        
        
        String url = "https://msuddi.example.com/uddipublic/inquiry.asmx";
        String exp = "https://" + Utility.getHostName() + ":443/uddipublic/inquiry.asmx";
        String result="";
        result = IpAddressUtility.modifyURL(url, false);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 5", exp, result);
     }    
                            
                            
                                     @Test
    public void testModifyURL33() {
        
        String myip = "";
        
        
        String url = "http://msuddi.example.com/uddipublic/inquiry.asmx";
        String exp = "http://msuddi.example.com:80/uddipublic/inquiry.asmx";
        String result="";
        result = IpAddressUtility.modifyURL(url, true);
        System.out.println("ModifyURL " + url + " to " + exp);
        assertEquals(result + " should be " + url + " service test 5", exp, result);
     }    
}
