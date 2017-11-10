/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.log4j.Level;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.DailySchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.Daynames;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ImmediateSchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.MonthlySchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.Monthnames;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.OneTimeSchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ReportDefinition;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.WeeklySchedule;
import static org.miloss.fgsms.services.rs.impl.FgsmsReportGenerator.log;

/**
 *
 * @author AO
 */
public class CalendarCalculator {

    public static boolean isTimeToRun(ReportDefinition get, Calendar now) {

        if (get == null) {
            throw new IllegalArgumentException();
        }
        Calendar cal = now;
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        //TODO compare rules to the last time it was ran, if a report exists already for that defined timestamp, then we skip execution
        //basically if the time different from the last report is greater than say 1 minute
        for (int i = 0; i < get.getSchedule().getTriggers().size(); i++) {
            Date starton = null;
            if (get.getSchedule().getTriggers().get(i).getStartingAt() != null) {
                starton = get.getSchedule().getTriggers().get(i).getStartingAt().getTime();
            }
            Date nowDate = cal.getTime();
            //are we past the start date?
            if (starton != null && (starton.before(cal.getTime()) || starton.equals(cal.getTime()))) {

                //if we are a triggering time
                if (get.getSchedule().getTriggers().get(i).getStartingAt().get(Calendar.HOUR_OF_DAY) == cal.get(Calendar.HOUR_OF_DAY)
                        && get.getSchedule().getTriggers().get(i).getStartingAt().get(Calendar.MINUTE) == cal.get(Calendar.MINUTE)) {

                    //                log.log(Level.INFO, "it's currently a trigger time");
                    if (get.getSchedule().getTriggers().get(i).getClass().getCanonicalName().equalsIgnoreCase(DailySchedule.class.getCanonicalName())) {
                        DailySchedule d = (DailySchedule) get.getSchedule().getTriggers().get(i);
                        if (d.getReoccurs() == BigInteger.ONE) {
                            return true;
                        }
                        if (get.getLastRanAt() == null) {
                            return true;
                        }
                        Calendar nextrun = get.getLastRanAt();
                        int compare = nextrun.compareTo(cal);
                        log.log(Level.INFO, "compare value " + compare);
                        nextrun.add(Calendar.DATE, d.getReoccurs().intValue());
                        if (nextrun.compareTo(cal) <= 0) {
                            return true;

                        }
                    } else if (get.getSchedule().getTriggers().get(i).getClass().getCanonicalName().equalsIgnoreCase(WeeklySchedule.class.getCanonicalName())) {
                        WeeklySchedule d = (WeeklySchedule) get.getSchedule().getTriggers().get(i);
                        //occur weekly on the following days
                        if (isTodayATriggerDay(d.getDayOfTheWeekIs())) {
                            if (get.getLastRanAt() == null) {
                                return true;
                            }
                            if (d.getReoccurs() == null || (d.getReoccurs() != null && d.getReoccurs().intValue() == 1)) {
                                return true;
                            } else {
                                //we're skipping weeks here
                                Calendar lastRanAt = get.getLastRanAt();
                                if (lastRanAt == null) {
                                    return true;//this should have been handled already but...
                                }
                                Calendar gcal = (Calendar) lastRanAt.clone();
                                //add the number of skipped weeks to the last ran at date
                                gcal.add(Calendar.DATE, 7 * d.getReoccurs().intValue());
                                //calculate the date, then see if today's day of the month is today
                                List<Integer> dates = new ArrayList<Integer>();
                                dates.add(gcal.get(Calendar.DAY_OF_MONTH));
                                if (isNowATriggerDate(dates,now)) {
                                    return true;
                                }
                            }
                        }
                    } else if (get.getSchedule().getTriggers().get(i).getClass().getCanonicalName().equalsIgnoreCase(MonthlySchedule.class.getCanonicalName())) {
                        MonthlySchedule d = (MonthlySchedule) get.getSchedule().getTriggers().get(i);
                        if (isThisMonthATriggerMonth(d.getMonthNameIs(),now) && isNowATriggerDate(d.getDayOfTheMonthIs(),now)) {
                            return true;
                        }
                    } else if (get.getSchedule().getTriggers().get(i).getClass().getCanonicalName().equalsIgnoreCase(OneTimeSchedule.class.getCanonicalName())) {
                        //fire once at the starting at date
                        return true;
                    } else {
                        log.log(Level.WARN, "unhandled schedule type" + get.getSchedule().getTriggers().get(i).getClass().getCanonicalName());

                    }
                }
            } 
            if (get.getLastRanAt() == null && get.getSchedule().getTriggers().get(i).getClass().getCanonicalName().equalsIgnoreCase(ImmediateSchedule.class.getCanonicalName())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isThisMonthATriggerMonth(List<Monthnames> monthNameIs, Calendar cal) {
        if (monthNameIs.isEmpty()) {
            return false;
        }

        int month = cal.get(Calendar.MONTH);
        for (int i = 0; i < monthNameIs.size(); i++) {
            if (monthNameIs.get(i) == Monthnames.JANURARY && month == Calendar.JANUARY) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.FEBURARY && month == Calendar.FEBRUARY) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.MARCH && month == Calendar.MARCH) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.APRIL && month == Calendar.APRIL) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.MAY && month == Calendar.MAY) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.JUNE && month == Calendar.JUNE) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.JULY && month == Calendar.JULY) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.AUGUST && month == Calendar.AUGUST) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.SEPTEMBER && month == Calendar.SEPTEMBER) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.OCTOBER && month == Calendar.OCTOBER) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.NOVEMBER && month == Calendar.NOVEMBER) {
                return true;
            }
            if (monthNameIs.get(i) == Monthnames.DECEMBER && month == Calendar.DECEMBER) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTodayATriggerDay(List<Daynames> days) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        int day = gcal.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i) == Daynames.SUNDAY && day == Calendar.SUNDAY) {
                return true;
            }
            if (days.get(i) == Daynames.MONDAY && day == Calendar.MONDAY) {
                return true;
            }
            if (days.get(i) == Daynames.TUESDAY && day == Calendar.TUESDAY) {
                return true;
            }
            if (days.get(i) == Daynames.WEDNESDAY && day == Calendar.WEDNESDAY) {
                return true;
            }
            if (days.get(i) == Daynames.THURSDAY && day == Calendar.THURSDAY) {
                return true;
            }
            if (days.get(i) == Daynames.FRIDAY && day == Calendar.FRIDAY) {
                return true;
            }
            if (days.get(i) == Daynames.SATURDAY && day == Calendar.SATURDAY) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNowATriggerHour(List<Integer> hours) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        int hour = gcal.get(Calendar.HOUR_OF_DAY);
        for (int i = 0; i < hours.size(); i++) {
            if (hours.get(i).intValue() == hour) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNowATriggerDate(List<Integer> dayofmonth, Calendar now) {
        
        int day = now.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < dayofmonth.size(); i++) {
            if (dayofmonth.get(i).intValue() == day) {
                return true;
            }
        }
        return false;
    }

}
