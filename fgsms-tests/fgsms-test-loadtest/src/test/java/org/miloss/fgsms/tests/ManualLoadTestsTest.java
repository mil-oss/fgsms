/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author AO
 */
public class ManualLoadTestsTest {

     static Properties p = new Properties();
    public ManualLoadTestsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
       
        p.load(new FileInputStream(new File("../test.properties")));
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class ManualLoadTests.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        ManualLoadTests obj = new ManualLoadTests();

        obj.init(p.getProperty("pcsurl"), p.getProperty("fgsmsadminuser"), p.getProperty("fgsmsadminpass"), false);
        obj.outputSystemProperties();

        obj.createPerfData(10, 100, "Run 1 - 10x1000");
        obj.killPolicies(10);
        //run it again but with smaller numbers to make sure the threads don't die
        obj.createPerfData(10, 1000, "Run 2 - 10x1000 repeated");
        obj.killPolicies(10);
        
        
        obj.createPerfData(100, 10, "Run 4 - 100x100");
        obj.killPolicies(100);
        //run it again but with smaller numbers to make sure the threads don't die
        obj.createPerfData(100, 100, "Run 5 - 100x1000");
        obj.killPolicies(100);
        
        
        
        obj.close();

    }

}
