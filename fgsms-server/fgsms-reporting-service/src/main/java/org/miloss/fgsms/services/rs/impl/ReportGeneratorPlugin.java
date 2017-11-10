/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.miloss.fgsms.services.rs.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;

/**
 * This interface is a plugin type of interface for generating user
 * facing reports based on their specifiied criteria, namely service policy
 * URI, time range, and report type. The output is formatted HTML content.
 * 
 * Implementations must be thread safe as instances can be shared across threads
 * @author AO
 * @since 7.1
 */
public interface ReportGeneratorPlugin {

    /**
     * human friendly display name of the report type
     * @return 
     */
    public String getDisplayName();
    /**
     * a human readable description, it will be html encoded
     * @return 
     */
    public String getDescription();
    /**
     * a list of all policy types that this report applies to
     * @return 
     */
    public List<PolicyType> getAppliesTo();
    /**
     * Generates the content of the report
     * 
     * Implementors must check access control urls using {@link UserIdentityUtil}
     * prior to report generation. The expected behavior is to skip the url if
     * the appropriate access is not granted to the requestor
     * 
     * Use {@link Utility#getPerformanceDBConnection() for database connections
     * be sure to close all database records when done. See aksi {@link DBUtils}
     * @param data write your html content here
     * @param urls this the policy uris from which to generate reports from
     * @param path this is the temporary output path, if creating charts, produce them
     * here then add the filename to the files list
     * @param files the list of files, these files are added to the report at completion
     * @param range time range from which to generate the report
     * @param currentuser the current user
     * @param classification classification level
     */
    public void generateReport(OutputStreamWriter data,List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException;
}
