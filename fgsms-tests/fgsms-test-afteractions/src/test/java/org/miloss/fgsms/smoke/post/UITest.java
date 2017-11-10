/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * You may add additional accurate notices of copyright ownership.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 *
 * Attempts to ping all user interface pages of fgsms after authentication
 *
 * @author alex.oree
 */
public class UITest {

    static List<String> jspRelativePaths = null;
    static List<RemoteWebDriver> drivers;
    static String url = "http://localhost:8888/fgsmsBootstrap/";

    private static void buildJspList() {
        File root = findBootstrap();
        File sourceWeb = new File(root, "src/main/webapp");
        jspRelativePaths = findJsp(sourceWeb, "");

    }

    private static List<String> findJsp(File sourceWeb, String relativePath) {
        List<String> ret = new ArrayList<String>();
        if (sourceWeb.exists() && sourceWeb.isDirectory()) {

            File[] listFiles = sourceWeb.listFiles();
            if (listFiles != null) {
                for (int i = 0; i < listFiles.length; i++) {
                    if (listFiles[i].isDirectory()) {
                        if (relativePath.equals("")) {
                            ret.addAll(findJsp(listFiles[i], listFiles[i].getName() + "/"));
                        } else {
                            ret.addAll(findJsp(listFiles[i], relativePath + "/" + listFiles[i].getName() + "/"));
                        }
                    } else {
                        if (listFiles[i].getName().endsWith(".jsp")) {
                            ret.add(relativePath + listFiles[i].getName());
                        }
                    }
                }
            }
        }
        return ret;
    }

    private static File findBootstrap() {
        File cwd = new File(".");
        System.out.println("CWD " + cwd.getAbsolutePath());
        cwd = new File(cwd, "../../fgsms-server/fgsms.Bootstrap");
        System.out.println("Boostrap " + cwd.getAbsolutePath());
        return cwd;
    }

    public UITest() throws Exception {

    }

    static Properties props;

    @BeforeClass
    public static void setUpClass() throws Exception {
        props = new Properties();
        props.load(new FileInputStream("../test.properties"));
        url = props.getProperty("server").replace("fgsmsServices", "fgsmsBootstrap");
        drivers = new ArrayList<RemoteWebDriver>();
        //attempt login
        try {
            //FirefoxProfile profile = new FirefoxProfile();
            //profile.setPreference("browser.privatebrowsing.autostart", true);
            FirefoxDriver f = new FirefoxDriver();

            drivers.add(f);
        } catch (Exception ex) {
        }
        try {
            drivers.add(new ChromeDriver());
        } catch (Exception ex) {

        }
        try {
            drivers.add(new EdgeDriver());
        } catch (Exception ex) {
        }

        buildJspList();
        login();
    }

    @AfterClass
    public static void tearDownClass() {

        for (int i = 0; i < drivers.size(); i++) {
            drivers.get(i).close();
        }
        drivers.clear();
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    private static void login() throws Exception {
        Assume.assumeFalse(drivers.isEmpty());
        //DO login
        for (int i = 0; i < drivers.size(); i++) {
            RemoteWebDriver driver = drivers.get(i);
            driver.navigate().to(url);
            driver.findElementByName("username").sendKeys(props.getProperty("fgsmsadminuser"));
            driver.findElementByName("password").sendKeys(props.getProperty("fgsmsadminpass"));
            driver.findElementById("loginButton").click();
            //initial disclaimer
            driver.switchTo().alert().accept();
            if (url.startsWith("http://")) {
                //warning about ssl
                driver.switchTo().alert().accept();
            }

        }
    }

    @Test
    public void hitAllJspFiles() throws Exception {
        Assume.assumeFalse(drivers.isEmpty());

        for (int i = 0; i < drivers.size(); i++) {
            int success=0;
            int failure=0;
            RemoteWebDriver driver = drivers.get(i);
            for (int k = 0; k < jspRelativePaths.size(); k++) {
                //filters csrf
                String target = url + jspRelativePaths.get(k);
                System.out.println("nav to " + target);
                driver.navigate().to(target);
                System.out.println(driver.getTitle());
                if (driver.getTitle().toLowerCase().contains("error report")) {
                //    Assert.fail(target + " failed with " + driver.getTitle() + " body "
                //            + driver.getPageSource());
                    System.err.println(target + " failed with " + driver.getTitle() + " body "
                            + driver.getPageSource());
                    failure++;
                } else success++;

            }
             System.out.println("success: " + success + ", failures: " + failure);
            Assert.assertEquals("there were some failing test cases",0,failure);
        }
    }

}
