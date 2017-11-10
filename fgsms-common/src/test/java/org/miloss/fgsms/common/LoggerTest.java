/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.common;

import java.util.Date;
import org.junit.Test;

/**
 *
 * @author alex.oree
 */
public class LoggerTest {
    
    @Test
    public void testLogger(){
        Logger logger = Logger.getLogger(LoggerTest.class);
        logger.info("test");
        logger.info("something happened\n[INFO] LOG FORGE " + new Date() + " bob logged in");
        logger.info("something happened\r[INFO] LOG FORGE " + new Date() + " bob logged in");
        org.apache.log4j.Logger logger1 = org.apache.log4j.Logger.getLogger(LoggerTest.class);
        logger1.info("test");
        logger1.info("something happened\n[INFO] LOG FORGE " + new Date() + " bob logged in");
        logger1.info("something happened\r[INFO] LOG FORGE " + new Date() + " bob logged in");
    }
    
}
