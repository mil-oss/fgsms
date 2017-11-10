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
package org.miloss.fgsms.presentation;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.TransactionLog;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author AO
 */
public class TransactionLogViewerData implements DatasetProducer, Serializable {

    public TransactionLogViewerData() {
    }

    public TransactionLogViewerData(URL url) {
        dasu = url;
    }
    private URL dasu=null;
    private List<TransactionLog> data=null;

    /*
     * this is only called from the service profile page
     
     *

    @Deprecated
    private String GetRenderedData(String URL, int offset, int records, boolean slaFaultsOnly, boolean faultsOnly, HttpSession session, String username, String password, SecurityWrapper c, AuthMode mode_) {
        LogHelper.getLog().log(Level.INFO, "fgsmsWEB TransactionLogViewerData, produce rendered data");
        String out = "";
        try {

            DataAccessService_Service das = new DataAccessService_Service(this.getClass().getResource("/META-INF/" + org.miloss.fgsms.common.Constants.DAS_META));
            DataAccessService dasport = das.getDASPort();
            BindingProvider bpPCS = (BindingProvider) dasport;
            Map<String, Object> contextPCS = bpPCS.getRequestContext();
            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, dasu.toString());
            StubExt sec = (StubExt) bpPCS;
            if (mode_ == AuthMode.UsernamePassword) {
                sec.setSecurityConfig("fgsms-username-config.xml");
                contextPCS.put(BindingProvider.USERNAME_PROPERTY, username);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(password));
            }
            if (mode_ == AuthMode.PKI) {
                sec.setSecurityConfig("fgsms-pki-config.xml");
            }

            GetRecentMessageLogsRequestMsg req1 = new GetRecentMessageLogsRequestMsg();
            req1.setClassification(c);
            req1.setURL(URL);
            req1.setOffset(offset);
            req1.setRecords(records);

            req1.setSlaViolationsOnly(slaFaultsOnly);
            req1.setFaultsOnly(faultsOnly);
            GetMessageLogsResponseMsg res = dasport.getRecentMessageLogs(req1);
            out += ("Total Records available: " + res.getTotalRecords());

            if (res.getLogs() != null
                    && res.getLogs().getValue() != null
                    && res.getLogs().getValue().getTransactionLog() != null
                    && res.getLogs().getValue().getTransactionLog().size() > 0) {
                out += ("<table border=1><tr>"
                        + "<th>Action</th>"
                        + "<th>Success/Fault</th>"
                        + "<th>Requestor Identity</th>"
                        + "<th>Response Time</th>"
                        //     + "<th>Request / Response</th>"
                        + "<th>Timestamp</th>"
                        + "<th>SLA Violation</th>"
                        + "<th>Details</th></tr>");
                data = res.getLogs().getValue().getTransactionLog();

                // is this needed? no it isn't
                //session.setAttribute("fgsms.transactionlog" + URL, data);
                boolean colorflag = false;
                for (int i = 0; i < res.getLogs().getValue().getTransactionLog().size(); i++) {
                    out += ("<tr ");
                    colorflag = !colorflag;
                    if (colorflag) {
                        out += " bgcolor=#EEFFEE><td>";
                    } else {
                        out += " bgcolor=#DDFFDD><td>";
                    }
                    int clip = 0;

                    if (Utility.stringIsNullOrEmpty(res.getLogs().getValue().getTransactionLog().get(i).getAction())) {
                        out += "NA</td><td>";
                    } else {
                        out += (res.getLogs().getValue().getTransactionLog().get(i).getAction() + "</td><td>");
                    }
                    if (!res.getLogs().getValue().getTransactionLog().get(i).isIsFault()) {
                        out += ("<font color=#FF0000><b>Fault</b></font>");
                    } else {
                        out += ("Success");
                    }
                    out += ("</td><td>" + Utility.encodeHTML(ParseIdentities(res.getLogs().getValue().getTransactionLog().get(i).getIdentity()))
                            + "</td><td>" + res.getLogs().getValue().getTransactionLog().get(i).getResponseTime()
                            + "ms</td>");

                    out += ("<td>" + res.getLogs().getValue().getTransactionLog().get(i).getTimestamp().toString()
                            + "</td><td>");
                    if (res.getLogs().getValue().getTransactionLog().get(i).isIsSLAFault()) {
                        out += ("<font color=#FF0000><b>Fault</b></font>");
                    }
                    out += "</td><td>"
                            + Utility.encodeHTML(res.getLogs().getValue().getTransactionLog().get(i).getSlaFaultMsg())
                            + "<a href=\"SpecificTransactionLogViewer.jsp?ID=" + res.getLogs().getValue().getTransactionLog().get(i).getTransactionId() + "\">Details</a>"
                            + "</td></tr></tr>";

                }


            } else {
                out += "<br><Br>No records were returned.";
            }

        } catch (SecurityException ex) {
            out += ("Access was denied to the requested resource.");
            LogHelper.getLog().log(Level.ERROR, "TransactionLogViewer, access denied when error rendering data for user " + username);
        } catch (Exception ex) {
            Logger.getLogger("fgsms.Web").log(Level.WARN, "Error caught", ex);
            out += ("There was an error processing your request. Message: " + ex.getLocalizedMessage());
            LogHelper.getLog().log(Level.ERROR, "TransactionLogViewer, error rendering data for user " + username, ex);
        }
        return out;
    }
     */
    public String GetRenderedData(GetMessageLogsResponseMsg res)
    {
        String out = "";
        if (res == null)
            return out;
        try
        {
        out += ("Total Records available: " + res.getTotalRecords());

            if (res.getLogs() != null
                    && res.getLogs() != null
                    && res.getLogs().getTransactionLog() != null
                    && res.getLogs().getTransactionLog().size() > 0) {
                out += ("<table border=1><tr>"
                        + "<th>Action</th>"
                        + "<th>Success/Fault</th>"
                        + "<th>Requestor Identity</th>"
                        + "<th>Response Time</th>"
                        //     + "<th>Request / Response</th>"
                        + "<th>Timestamp</th>"
                        + "<th>SLA Violation</th>"
                        + "<th>Details</th></tr>");
                data = res.getLogs().getTransactionLog();

                // is this needed? no it isn't
                //session.setAttribute("fgsms.transactionlog" + URL, data);
                boolean colorflag = false;
                for (int i = 0; i < res.getLogs().getTransactionLog().size(); i++) {
                    out += ("<tr ");
                    colorflag = !colorflag;
                    if (colorflag) {
                        out += " bgcolor=#EEFFEE><td>";
                    } else {
                        out += " bgcolor=#DDFFDD><td>";
                    }
                    int clip = 0;

                    if (Utility.stringIsNullOrEmpty(res.getLogs().getTransactionLog().get(i).getAction())) {
                        out += "NA</td><td>";
                    } else {
                        out += (res.getLogs().getTransactionLog().get(i).getAction() + "</td><td>");
                    }
                    if (!res.getLogs().getTransactionLog().get(i).isIsFault()) {
                        out += ("<font color=#FF0000><b>Fault</b></font>");
                    } else {
                        out += ("Success");
                    }
                    out += ("</td><td>" + Utility.encodeHTML(ParseIdentities(res.getLogs().getTransactionLog().get(i).getIdentity()))
                            + "</td><td>" + res.getLogs().getTransactionLog().get(i).getResponseTime()
                            + "ms</td>");

                    out += ("<td>" + res.getLogs().getTransactionLog().get(i).getTimestamp().toString()
                            + "</td><td>");
                    if (res.getLogs().getTransactionLog().get(i).isIsSLAFault()) {
                        out += ("<font color=#FF0000><b>Fault</b></font>");
                    }
                    out += "</td><td>"
                            + Utility.encodeHTML(res.getLogs().getTransactionLog().get(i).getSlaFaultMsg())
                            + "<a href=\"SpecificTransactionLogViewer.jsp?ID=" + res.getLogs().getTransactionLog().get(i).getTransactionId() + "\">Details</a>"
                            + "</td></tr></tr>";

                }


            } else {
                out += "<br><Br>No records were returned.";
            }

        } catch (SecurityException ex) {
            out += ("Access was denied to the requested resource.");
            LogHelper.getLog().log(Level.ERROR, "TransactionLogViewer, access denied when error rendering data for user ");
        } catch (Exception ex) {
            Logger.getLogger("fgsms.Web").log(Level.WARN, "Error caught", ex);
            out += ("There was an error processing your request. Message: " + ex.getLocalizedMessage());
            LogHelper.getLog().log(Level.ERROR, "TransactionLogViewer, error rendering data for user ", ex);
        }
        return out;
    }
    public Object produceDataset(Map params) throws DatasetProduceException {

        //  Logger.getAnonymousLogger().log(Level.INFO, "fgsmsWEB TransactionLogViewerData, produce chart data");
        TimeSeriesCollection col = new TimeSeriesCollection();


        try {
            GetMessageLogsResponseMsg res = (GetMessageLogsResponseMsg) params.get("fgsms.data");
            TransactionLogData recordset = new TransactionLogData();
            if (res != null && res.getLogs() != null && res.getLogs() != null && res.getLogs().getTransactionLog() != null && res.getLogs().getTransactionLog().size() > 0) {
                
                //loop through all records
                for (int i = 0; i < res.getLogs().getTransactionLog().size(); i++) {
                    String action = res.getLogs().getTransactionLog().get(i).getAction();
                    int clip = 0;
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("/") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("/");
                    }
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("}") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("}");
                    }
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf(":") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf(":");
                    }
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("#") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("#");
                    }

                    if (clip > 0) {
                        action = (res.getLogs().getTransactionLog().get(i).getAction().substring(clip + 1));
                    }
                    TransactionLogTimeStampStruct t2 = new TransactionLogTimeStampStruct();
                    t2.ResponseTime = res.getLogs().getTransactionLog().get(i).getResponseTime();
                    t2.cal = res.getLogs().getTransactionLog().get(i).getTimestamp();
                    if (recordset.Contains(action)) {
                        recordset.get(action).add(t2);
                    } else {
                        TransactionLogStruct t3 = new TransactionLogStruct();
                        t3.action = action;
                        t3.data = new ArrayList<TransactionLogTimeStampStruct>();
                        t3.data.add(t2);
                        recordset.add(t3);
                    }


                }

                for (int i = 0; i < recordset.stuff.size(); i++) {
                    TimeSeries s = new TimeSeries(recordset.stuff.get(i).action, org.jfree.data.time.Millisecond.class);

                    for (int k = 0; k < recordset.stuff.get(i).data.size(); k++) {
                        Millisecond m = new Millisecond(recordset.stuff.get(i).data.get(k).cal.getTime());

                        //  TimeSeriesDataItem t = new TimeSeriesDataItem(m,
                        //          recordset.stuff.get(i).data.get(k).ResponseTime);
                        // s.add(t);
                        s.addOrUpdate(m, recordset.stuff.get(i).data.get(k).ResponseTime);

                    }

                    col.addSeries(s);
                    //col.addSeries((TimeSeries)s.clone());
                }
            }
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "TransactionLogViewerData produce chart data " + ex.getLocalizedMessage());
        }

        return col;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
        return "fgsms.TransactionLogViewerData";
    }

    public static String ParseIdentities(List<String> identity) {
        if (identity == null || identity.isEmpty()) {

            return "";
        }
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < identity.size(); i++) {
           ret= ret.append(identity.get(i)).append(", ");
        }
        String r = ret.toString().trim();
        
       return r.substring(0, ret.length() - 1);
        
    }
}
