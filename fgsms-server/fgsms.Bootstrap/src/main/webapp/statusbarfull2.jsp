<%@page import="java.util.Collections"%>
<%@page import="org.miloss.fgsms.presentation.SortableStatusList.Status"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.PolicyType"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="javax.xml.datatype.Duration"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="csrf.jsp" %>
<%
    //this is the left hand side widget that has the status informatio
    response.setHeader("Cache-Control", "no-cache");
    int maxitems = 1000;
    if (request.getParameter("screenheight") != null) {
        try {
            int screenheight = Integer.parseInt(request.getParameter("screenheight"));
            if (screenheight > 198) {
                maxitems = ((screenheight - 198) / 22);
            } else {
                maxitems = 10;
            }
        } catch (Exception ex) {
        }
    }

    String onlinefresh = "<img src=\"img/greencheck.png\" width=\"25\" title=ONLINE />";
    String offlinefresh = "<img src=\"img/redx.png\" width=\"25\" title=OFFLINE />";
    String online_sla = "<img src=\"img/yellowwarn.png\" width=\"25\" title=\"Online but with recent SLA/faults\"/>";
    String online_stale = "<img src=\"img/greenwarn.png\" width=\"25\" title=\"Online but the data is stale\" />";
    String offline_stale = "<img src=\"img/redwarn.png\" width=\"25\" title=\"Offline but the data is stale\"/>";
    String unknown = "<img src=\"img/unknown.png\" width=\"25\" title=UNKNOWN />";
    StringBuilder ministatus = new StringBuilder();
    ministatus.append("<div id=\"min_menu\" class=\"well\"><table border=0 width=\"100%\">");

    StringBuilder fullstatus = new StringBuilder();


    List<SortableStatusList> thislist = new ArrayList<SortableStatusList>();

    try {
        SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
        if (c == null) {
            LogHelper.getLog().log(Level.INFO, "redirecting to the index page, security wrapper is null");
            response.sendRedirect("index.jsp");
            return;
        }
        ProxyLoader pl = ProxyLoader.getInstance(application);

        DataAccessService dasport = pl.GetDAS(application, request, response);
        StatusService statusport = pl.GetSS(application, request, response);


        //get all status info
        GetStatusRequestMsg req = new GetStatusRequestMsg();
        req.setClassification(c);
        List<GetStatusResponseMsg> statuslist = statusport.getAllStatus(req);

        //get the list of stuff i have access to
        GetMonitoredServiceListRequestMsg Listrequest = new GetMonitoredServiceListRequestMsg();
        
        
        Listrequest.setClassification(c);
        GetMonitoredServiceListResponseMsg list = dasport.getMonitoredServiceList(Listrequest);


        //get all the current stats, used just from sla values
        GetQuickStatsAllRequestMsg quickres = new GetQuickStatsAllRequestMsg();
        quickres.setClassification(c);
        GetQuickStatsAllResponseMsg stats = dasport.getQuickStatsAll(quickres);
        DatatypeFactory fac = DatatypeFactory.newInstance();



        if (list == null || list.getServiceList() == null || list.getServiceList() == null
                || list.getServiceList().getServiceType() == null
                || list.getServiceList().getServiceType().size() == 0) {
            //out.write("<br>There are currently no services being monitored that you have access to.");
        } else {




            for (int i = 0; i < list.getServiceList().getServiceType().size(); i++) {
                GetStatusResponseMsg result = Helper.Findrecord(list.getServiceList().getServiceType().get(i).getURL(), statuslist);
                //if ((filter.equalsIgnoreCase("OfflineOnly") && (result == null || !result.isOperational())) || filter.equalsIgnoreCase("AllItems")) 
                {


                    boolean isonline = false;
                    boolean isfresh = false;        //TODO how fresh is fresh?
                    long freshness = 15; //minutes
                    boolean isRecentSLAFaults = false;        //TODO how recent is recent?
                    boolean isRecentFaults = false;
                    if (result == null) {
                        thislist.add(new SortableStatusList(
                                (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())
                                ? list.getServiceList().getServiceType().get(i).getURL() : list.getServiceList().getServiceType().get(i).getDisplayName()), Status.UNKNOWN, 
                                list.getServiceList().getServiceType().get(i).getURL() ));

                    } else {
                        //ok the status is known
                        //figure out status, staleness and sla fault status
                        Calendar time = (Calendar) result.getTimeStamp();
                        long diff = System.currentTimeMillis() - time.getTimeInMillis();
                        if (result.isOperational()) {
                            isonline = true;
                        }
                        if (diff > (60 * 60 * 1000)) {
                        } else if (diff > (10 * 60 * 1000)) {
                            //if diff is over 10 minutes, flag as orange
                        } else {
                            isfresh = true;
                        }

                        QuickStatURIWrapper res2 = Helper.FindRecord(stats, list.getServiceList().getServiceType().get(i).getURL());
                        if (res2 != null && res2.getQuickStatWrapper() != null && !res2.getQuickStatWrapper().isEmpty()) {
                            for (int i2 = 0; i2 < res2.getQuickStatWrapper().size(); i2++) {
                                if (res2.getQuickStatWrapper().get(i2).getAction().equalsIgnoreCase("All-Methods")) {
                                    for (int k = 0; k < res2.getQuickStatWrapper().get(i2).getQuickStatData().size(); k++) {
                                        if (res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getTimeInMinutes().longValue() == freshness) {

                                            if (res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSLAViolationCount() > 0) {
                                                isRecentSLAFaults = true;
                                            }
                                            if (res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getFailureCount() > 0) {
                                                isRecentFaults = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }



                        if (isonline && isfresh && !isRecentSLAFaults && !isRecentFaults) {

                            thislist.add(new SortableStatusList(
                                    (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())
                                    ? list.getServiceList().getServiceType().get(i).getURL() : list.getServiceList().getServiceType().get(i).getDisplayName()), Status.ONLINE_FRESH, list.getServiceList().getServiceType().get(i).getURL() ));
                        }

                        if (isonline && isfresh && isRecentFaults) {
                            thislist.add(new SortableStatusList(
                                    (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())
                                    ? list.getServiceList().getServiceType().get(i).getURL() : list.getServiceList().getServiceType().get(i).getDisplayName()), Status.ONLINE_FAULTS, list.getServiceList().getServiceType().get(i).getURL() ));
                        }

                        if (isonline && isfresh && isRecentSLAFaults) {

                            thislist.add(new SortableStatusList(
                                    (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())
                                    ? list.getServiceList().getServiceType().get(i).getURL() : list.getServiceList().getServiceType().get(i).getDisplayName()), Status.ONLINE_SLA, list.getServiceList().getServiceType().get(i).getURL() ));
                        }
                        if (isonline && !isfresh) {
                            thislist.add(new SortableStatusList(
                                    (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())
                                    ? list.getServiceList().getServiceType().get(i).getURL() : list.getServiceList().getServiceType().get(i).getDisplayName()), Status.ONLINE_STALE, list.getServiceList().getServiceType().get(i).getURL() ));
                        }
                        if (!isonline && isfresh) {

                            thislist.add(new SortableStatusList(
                                    (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())
                                    ? list.getServiceList().getServiceType().get(i).getURL() : list.getServiceList().getServiceType().get(i).getDisplayName()), Status.OFFLINE_FRESH, list.getServiceList().getServiceType().get(i).getURL() ));
                        }
                        if (!isonline && !isfresh) {
                            thislist.add(new SortableStatusList(
                                    (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())
                                    ? list.getServiceList().getServiceType().get(i).getURL() : list.getServiceList().getServiceType().get(i).getDisplayName()), Status.OFFLINE_STALE, list.getServiceList().getServiceType().get(i).getURL() ));
                        }
                    }

                }
            }
        }
    } catch (Exception ex) {
    }
    Collections.sort(thislist);


    int onlinefreshcount = 0;
    int onlineslacount = 0;
    int onlinestalecount = 0;
    int offlinefreshcount = 0;
    int offlinestalecount = 0;
    int unknowncount = 0;

    for (int i = 0; i < thislist.size(); i++) {
        if (i < maxitems) {
            ministatus.append("<tr  width=100% ><td width=100%  align=\"center\">").append(SortableStatusList.ConvertToImageLink(thislist.get(i).status)).append("</td></tr>");
            fullstatus.append("<tr><td class=");
            switch (thislist.get(i).status) {
                case OFFLINE_FRESH:
                case OFFLINE_STALE:
                    fullstatus.append("bad");
                    break;
                case ONLINE_FAULTS:
                case ONLINE_SLA:
                    fullstatus.append("warn");
                    break;
                case ONLINE_STALE:
                case UNKNOWN:
                    fullstatus.append("stale");
                    break;
                case ONLINE_FRESH:
                    fullstatus.append("good");
                    break;

            }
            fullstatus.append("><a href=\"javascript:loadpage('profile.jsp?url=").append(URLEncoder.encode(thislist.get(i).url,Constants.CHARSET))
                    .append("','mainpane');\" style=\"color:white\">").append(SortableStatusList.ConvertToImageLink(thislist.get(i).status)).
                    append(Utility.encodeHTML(thislist.get(i).name)).append("</a> " + SortableStatusList.ConvertToFriendlyName(thislist.get(i).status) + " </td></tr>");
        } else {
            switch (thislist.get(i).status) {
                case OFFLINE_FRESH:
                    offlinefreshcount++;
                    break;
                case OFFLINE_STALE:
                    offlinestalecount++;
                    break;
                case ONLINE_FAULTS:
                case ONLINE_SLA:
                    onlineslacount++;
                    break;
                case ONLINE_STALE:
                    onlinestalecount++;
                    break;
                case UNKNOWN:
                    unknowncount++;
                    break;
                case ONLINE_FRESH:
                    onlinefreshcount++;
                    break;

            }

        }
    }

//this renders the minified menu
    out.write(ministatus.toString());
    out.write("    </table></div>");
//this is the full menu
    out.write("<div id=\"menu\" class=\"well\"><table border=0 width=\"100%\">");
    out.write("<tr><td class=statusheader align=center>" + onlinefreshcount + onlinefresh + "  " + onlineslacount + online_sla + "  " + onlinestalecount + online_stale
            + offlinefreshcount + offlinefresh + "  " + offlinestalecount + offline_stale + "  " + unknowncount + unknown + "</td></tr>");

    out.write("<tr><td class=statusheader align=center><a href=\"javascript:loadpage('status.jsp','mainpane');\"><b>See All Items/Details</b></a></td></tr>");

    out.write(fullstatus.toString());
    out.write("    </table></div>");

%>