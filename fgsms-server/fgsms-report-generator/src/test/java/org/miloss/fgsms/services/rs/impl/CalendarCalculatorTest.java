/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl;

import java.util.Calendar;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ImmediateSchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.MonthlySchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.Monthnames;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.OneTimeSchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ReportDefinition;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ScheduleDefinition;

/**
 *
 * @author AO
 */
public class CalendarCalculatorTest {

    /**
     * Test of isTimeToRun method, of class CalendarCalculator.
     */
    @Test
    public void testItsTimeToRun() {
        System.out.println("itsTimeToRun");
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        get.getSchedule().getTriggers().add(new ImmediateSchedule());

        Calendar now = Calendar.getInstance();
        boolean expResult = true;
        boolean result = CalendarCalculator.isTimeToRun(get, now);
        assertEquals(expResult, result);

    }

    @Test
    public void testItsTimeToRunOnce() {
        System.out.println("itsTimeToRun");
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        OneTimeSchedule schedule = new OneTimeSchedule();

        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2000);
        now.set(Calendar.MONTH, Calendar.FEBRUARY);
        now.set(Calendar.DAY_OF_MONTH, 5);
        now.set(Calendar.HOUR_OF_DAY, 17);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);

        schedule.setStartingAt(now);
        get.getSchedule().getTriggers().add(schedule);

        boolean expResult = true;
        boolean result = CalendarCalculator.isTimeToRun(get, now);
        assertEquals(expResult, result);

    }

    @Test
    public void testItsTimeToRunOnce2() {
        System.out.println("itsTimeToRun");
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        OneTimeSchedule schedule = new OneTimeSchedule();

        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2000);
        now.set(Calendar.MONTH, Calendar.FEBRUARY);
        now.set(Calendar.DAY_OF_MONTH, 5);
        now.set(Calendar.HOUR_OF_DAY, 17);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 1);
        schedule.setStartingAt(now);
        get.getSchedule().getTriggers().add(schedule);

        Calendar nowFuture = Calendar.getInstance();
        nowFuture.set(Calendar.YEAR, 2000);
        nowFuture.set(Calendar.MONTH, Calendar.FEBRUARY);
        nowFuture.set(Calendar.DAY_OF_MONTH, 5);
        nowFuture.set(Calendar.HOUR_OF_DAY, 17);
        nowFuture.set(Calendar.MINUTE, 0);
        nowFuture.set(Calendar.SECOND, 0);

        //trigger time is one second in the future
        boolean expResult = false;
        boolean result = CalendarCalculator.isTimeToRun(get, nowFuture);
        assertEquals(expResult, result);

    }

    @Test
    public void testItsTimeToRunOnce3() {
        System.out.println("itsTimeToRun");
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        OneTimeSchedule schedule = new OneTimeSchedule();

        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2000);
        now.set(Calendar.MONTH, Calendar.FEBRUARY);
        now.set(Calendar.DAY_OF_MONTH, 5);
        now.set(Calendar.HOUR_OF_DAY, 17);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        schedule.setStartingAt(now);
        get.getSchedule().getTriggers().add(schedule);

        Calendar nowFuture = Calendar.getInstance();
        nowFuture.set(Calendar.YEAR, 2000);
        nowFuture.set(Calendar.MONTH, Calendar.FEBRUARY);
        nowFuture.set(Calendar.DAY_OF_MONTH, 5);
        nowFuture.set(Calendar.HOUR_OF_DAY, 17);
        nowFuture.set(Calendar.MINUTE, 0);
        nowFuture.set(Calendar.SECOND, 1);

        //trigger time is one second in the past
        boolean expResult = true;
        boolean result = CalendarCalculator.isTimeToRun(get, nowFuture);
        assertEquals(expResult, result);

    }

    @Test
    public void testMonthlyTrigger() {
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        MonthlySchedule schedule = new MonthlySchedule();
        schedule.getMonthNameIs().add(Monthnames.MAY);
        schedule.getDayOfTheMonthIs().add(1);
        
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2000);
        now.set(Calendar.MONTH, Calendar.MAY);
        now.set(Calendar.DAY_OF_MONTH, 5);
        now.set(Calendar.HOUR_OF_DAY, 17);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        schedule.setStartingAt(now);
        //fire on the 1st of may, effectively once a year
        
        get.getSchedule().getTriggers().add(schedule);

        Calendar nowFuture = Calendar.getInstance();
        nowFuture.set(Calendar.YEAR, 2000);
        nowFuture.set(Calendar.MONTH, Calendar.FEBRUARY);
        nowFuture.set(Calendar.DAY_OF_MONTH, 5);
        nowFuture.set(Calendar.HOUR_OF_DAY, 17);
        nowFuture.set(Calendar.MINUTE, 0);
        nowFuture.set(Calendar.SECOND, 1);

        boolean expResult = false;
        boolean result = CalendarCalculator.isTimeToRun(get, nowFuture);
        assertEquals(expResult, result);

    }
    
    
    @Test
    public void testMonthlyTrigger2() {
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        MonthlySchedule schedule = new MonthlySchedule();
        schedule.getMonthNameIs().add(Monthnames.MAY);
        schedule.getDayOfTheMonthIs().add(1);
        
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2000);
        now.set(Calendar.MONTH, Calendar.MAY);
        now.set(Calendar.DAY_OF_MONTH, 5);
        now.set(Calendar.HOUR_OF_DAY, 17);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        schedule.setStartingAt(now);
        //fire on the 1st of may, effectively once a year
        
        get.getSchedule().getTriggers().add(schedule);

        Calendar nowFuture = Calendar.getInstance();
        nowFuture.set(Calendar.YEAR, 2000);
        nowFuture.set(Calendar.MONTH, Calendar.MAY);
        nowFuture.set(Calendar.DAY_OF_MONTH, 5);
        nowFuture.set(Calendar.HOUR_OF_DAY, 17);
        nowFuture.set(Calendar.MINUTE, 0);
        nowFuture.set(Calendar.SECOND, 1);

   
        boolean expResult = false;
        boolean result = CalendarCalculator.isTimeToRun(get, nowFuture);
        assertEquals(expResult, result);

    }
    
    
    @Test
    public void testMonthlyTrigger3() {
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        MonthlySchedule schedule = new MonthlySchedule();
        schedule.getMonthNameIs().add(Monthnames.MAY);
        schedule.getDayOfTheMonthIs().add(1);
        
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2000);
        now.set(Calendar.MONTH, Calendar.MAY);
        now.set(Calendar.DAY_OF_MONTH, 5);
        now.set(Calendar.HOUR_OF_DAY, 17);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        schedule.setStartingAt(now);
        //fire on the 1st of may, effectively once a year
        
        get.getSchedule().getTriggers().add(schedule);

        Calendar nowFuture = Calendar.getInstance();
        nowFuture.set(Calendar.YEAR, 2000);
        nowFuture.set(Calendar.MONTH, Calendar.MAY);
        nowFuture.set(Calendar.DAY_OF_MONTH, 1);
        nowFuture.set(Calendar.HOUR_OF_DAY, 17);
        nowFuture.set(Calendar.MINUTE, 0);
        nowFuture.set(Calendar.SECOND, 1);

        boolean expResult = false;
        boolean result = CalendarCalculator.isTimeToRun(get, nowFuture);
        assertEquals(expResult, result);

    }
    
    
       
    @Test
    public void testMonthlyTrigger5() {
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        MonthlySchedule schedule = new MonthlySchedule();
        schedule.getMonthNameIs().add(Monthnames.MAY);
        schedule.getDayOfTheMonthIs().add(1);
        
        
        Calendar startingat = Calendar.getInstance();
        startingat.set(Calendar.YEAR, 2000);
        startingat.set(Calendar.MONTH, Calendar.MAY);
        startingat.set(Calendar.DAY_OF_MONTH, 3);
        startingat.set(Calendar.HOUR_OF_DAY, 17);
        startingat.set(Calendar.MINUTE, 0);
        startingat.set(Calendar.SECOND,0);
        schedule.setStartingAt(startingat);
        //fire on the 1st of may, effectively once a year
        
        get.getSchedule().getTriggers().add(schedule);

        Calendar nowFuture = Calendar.getInstance();
        nowFuture.set(Calendar.YEAR, 2001);
        nowFuture.set(Calendar.MONTH, Calendar.MAY);
        nowFuture.set(Calendar.DAY_OF_MONTH, 1);
        nowFuture.set(Calendar.HOUR_OF_DAY, 17);
        nowFuture.set(Calendar.MINUTE, 0);
        nowFuture.set(Calendar.SECOND, 00);

        boolean expResult = true;
        boolean result = CalendarCalculator.isTimeToRun(get, nowFuture);
        assertEquals(expResult, result);

    }
    
    
    @Test
    public void testMonthlyTrigger4() {
        ReportDefinition get = new ReportDefinition();
        get.setEnabled(true);
        get.setJobId(UUID.randomUUID().toString());
        get.setLastRanAt(null);
        get.setSchedule(new ScheduleDefinition());
        MonthlySchedule schedule = new MonthlySchedule();
        schedule.getMonthNameIs().add(Monthnames.MAY);
        schedule.getDayOfTheMonthIs().add(1);
         //fire on the 1st of may, effectively once a year
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, 2000);
        now.set(Calendar.MONTH, Calendar.MAY);
        now.set(Calendar.DAY_OF_MONTH, 5);
        now.set(Calendar.HOUR_OF_DAY, 17);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        schedule.setStartingAt(now);
       
        
        get.getSchedule().getTriggers().add(schedule);

        Calendar nowFuture = Calendar.getInstance();
        nowFuture.set(Calendar.YEAR, 2001);
        nowFuture.set(Calendar.MONTH, Calendar.MAY);
        nowFuture.set(Calendar.DAY_OF_MONTH, 1);
        nowFuture.set(Calendar.HOUR_OF_DAY, 17);
        nowFuture.set(Calendar.MINUTE, 0);
        nowFuture.set(Calendar.SECOND, 0);
        get.setLastRanAt(nowFuture);

        boolean expResult = true;
        boolean result = CalendarCalculator.isTimeToRun(get, nowFuture);
        assertEquals(expResult, result);

    }
    
    
}
