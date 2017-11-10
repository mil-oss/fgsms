/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.common;

import java.util.Enumeration;
import java.util.ResourceBundle;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Mirrors Apache Log4j and acts as a wrapper. Modeled after the OWASP
 * Enterprise Security API ESAPI
 *
 * Primary function, protect against log forging
 *
 * @author alex.oree
 */
public class Logger {

    final static String CHAR_WHITELIST = "[^A-Za-z0-9 \\?\\\\\\!@#\\$%\\^&\\*\\(\\)\\;\\:/\\.,\\+\\<\\>\\-_\\=\\{\\}\\|'\"\\[\\]]";
    /**
     * this is the method that provides log forging prevention, basically, 
     * remove new lines from the log message, stack traces are excluded
     * @param message
     * @return 
     */
    private static String protect(Object message) {
        if (message == null) {
            return null;
        }
        String content = message.toString();
        if (content != null) {
            return content.replaceAll(CHAR_WHITELIST,"");//('\n', '_').replace('\r', '_');
        } else {
            return null;
        }
    }

    final org.apache.log4j.Logger parent;

    protected Logger(String name) {
        parent = org.apache.log4j.Logger.getLogger(name);
    }

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public static Logger getLogger(Class clazz) {
        return new Logger(clazz.getName());
    }

    public static Logger getRootLogger() {
        return new Logger("");
    }

    public static Logger getLogger(String name, LoggerFactory factory) {
        return new Logger(name);
    }

    public void trace(Object message) {
        parent.trace(message);
    }

    public void trace(Object message, Throwable t) {
        parent.trace(message, t);
    }

    public boolean isTraceEnabled() {
        return parent.isTraceEnabled();
    }

    public synchronized void addAppender(Appender newAppender) {
        parent.addAppender(newAppender);
    }

    public void assertLog(boolean assertion, String msg) {
        parent.assertLog(assertion, msg);
    }

    public void callAppenders(LoggingEvent event) {
        parent.callAppenders(event);
    }

    public void debug(Object message) {
        parent.debug(protect(message));
    }

    public void debug(Object message, Throwable t) {
        parent.debug(protect(message), t);
    }

    public void error(Object message) {
        parent.error(protect(message));
    }

    public void error(Object message, Throwable t) {
        parent.error(protect(message), t);
    }

    public void fatal(Object message) {
        parent.fatal(protect(message));
    }

    public void fatal(Object message, Throwable t) {
        parent.fatal(protect(message), t);
    }

    public boolean getAdditivity() {
        return parent.getAdditivity();
    }

    public synchronized Enumeration getAllAppenders() {
        return parent.getAllAppenders();
    }

    public synchronized Appender getAppender(String name) {
        return parent.getAppender(name);
    }

    public Level getEffectiveLevel() {
        return parent.getEffectiveLevel();
    }

    public Priority getChainedPriority() {
        return parent.getChainedPriority();
    }

 
    public final String getName() {
        return parent.getName();
    }

    public final Category getParent() {
        return parent.getParent();
    }

    public final Level getLevel() {
        return parent.getLevel();
    }

    public final Level getPriority() {
        return parent.getPriority();
    }

    public ResourceBundle getResourceBundle() {
        return parent.getResourceBundle();
    }

    public void info(Object message) {
        parent.info(protect(message));
    }

    public void info(Object message, Throwable t) {
        parent.info(protect(message), t);
    }

    public boolean isAttached(Appender appender) {
        return parent.isAttached(appender);
    }

    public boolean isDebugEnabled() {
        return parent.isDebugEnabled();
    }

    public boolean isEnabledFor(Priority level) {
        return parent.isEnabledFor(level);
    }

    public boolean isInfoEnabled() {
        return parent.isInfoEnabled();
    }

    public void l7dlog(Priority priority, String key, Throwable t) {
        parent.l7dlog(priority, key, t);
    }

    public void l7dlog(Priority priority, String key, Object[] params, Throwable t) {
        parent.l7dlog(priority, key, params, t);
    }

    public void log(Priority priority, Object message, Throwable t) {
        parent.log(priority, protect(message), t);
    }

    public void log(Priority priority, Object message) {
        parent.log(priority, protect(message));
    }

    public void log(String callerFQCN, Priority level, Object message, Throwable t) {
        parent.log(callerFQCN, level, protect(message), t);
    }

    public synchronized void removeAllAppenders() {
        parent.removeAllAppenders();
    }

    public synchronized void removeAppender(Appender appender) {
        parent.removeAppender(appender);
    }

    public synchronized void removeAppender(String name) {
        parent.removeAppender(name);
    }

    public void setAdditivity(boolean additive) {
        parent.setAdditivity(additive);
    }

    public void setLevel(Level level) {
        parent.setLevel(level);
    }

    public void setPriority(Priority priority) {
        parent.setPriority(priority);
    }

    public void setResourceBundle(ResourceBundle bundle) {
        parent.setResourceBundle(bundle);
    }

    public static void shutdown() {
        org.apache.log4j.Logger.shutdown();
    }

    public void warn(Object message) {
        parent.warn(protect(message));
    }

    public void warn(Object message, Throwable t) {
        parent.warn(protect(message), t);
    }
}
