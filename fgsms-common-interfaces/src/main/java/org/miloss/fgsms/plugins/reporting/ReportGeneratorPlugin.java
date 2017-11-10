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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.plugins.reporting;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.miloss.fgsms.plugins.PluginCommon;

import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;

/**
 * This interface is a plugin type of interface for generating user
 * facing reports based on their specified criteria, namely service policy
 * URI, time range, and report type. The output is formatted HTML content.
 * 
 * Implementations must be thread safe as instances can be shared across threads
 * @author AO
 * @since 7.1
 */
public interface ReportGeneratorPlugin extends PluginCommon{

   
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
