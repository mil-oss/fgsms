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

/**
 *Provides a sortable list for the status of monitored services
 * @author AO
 */
public class SortableStatusList implements Comparable<SortableStatusList> {

    public SortableStatusList()
    {}
    public SortableStatusList(String n, Status s, String url)
    {
        this.name = n;
        this.status = s;
        this.url = url;
    }
    public enum Status {

        ONLINE_FRESH(8),
        ONLINE_STALE(7),
        ONLINE_SLA(4),
        ONLINE_FAULTS(3),
        OFFLINE_FRESH(0),
        OFFLINE_STALE(1),
        UNKNOWN(6);
        public int value;

        Status(int v) {
            value = v;
        }
    }
    public String name;
     public String url;
    public Status status;

    public int compareTo(SortableStatusList o) {
        if (this.status.value > o.status.value) {
            return 1;
        } else if (this.status.value < o.status.value) {
            return -1;
        }
        return (this.name.compareTo(o.name));
    }
    
    public static String ConvertToImageLink(Status e)
    {
        /*
             String onlinefresh = "<img src=\"img/greencheck.png\" width=\"25\" />";
    String offlinefresh = "<img src=\"img/redx.png\" width=\"25\" />";
    String online_sla = "<img src=\"img/yellowwarn.png\" width=\"25\" />";
    String online_stale = "<img src=\"img/greenwarn.png\" width=\"25\" />";
    String offline_stale = "<img src=\"img/redwarn.png\" width=\"25\" />";
    String unknown = "<img src=\"img/unknown.png\" width=\"25\" />";
         */
        switch (e)
        {
            case OFFLINE_FRESH:
                return "<img src=\"img/redx.png\" width=\"25\" />";
            case OFFLINE_STALE:
                return "<img src=\"img/redwarn.png\" width=\"25\" />";
            case ONLINE_FAULTS:
                return "<img src=\"img/yellowwarn.png\" width=\"25\" />";
            case ONLINE_FRESH:
                return  "<img src=\"img/greencheck.png\" width=\"25\" />";
            case ONLINE_SLA:
                return "<img src=\"img/yellowwarn.png\" width=\"25\" />";
            case ONLINE_STALE:
                return "<img src=\"img/greenwarn.png\" width=\"25\" />";
            case UNKNOWN:
                return "<img src=\"img/unknown.png\" width=\"25\" />";
        }
        return "<img src=\"img/unknown.png\" width=\"25\" />";
    }
    static final String ss = " Status is ";
    public static String ConvertToFriendlyName(Status e)
    {
             switch (e)
        {
            case OFFLINE_FRESH:
                return ss + "offline";
            case OFFLINE_STALE:
                return ss + "offline but the data is stale";
            case ONLINE_FAULTS:
                return ss + "online but has had recent faults";
            case ONLINE_FRESH:
                return  ss+"online";
            case ONLINE_SLA:
                return ss+"online but with recent SLA faults";
            case ONLINE_STALE:
                return ss+"online but the data is stale";
            case UNKNOWN:
                return ss+"unknown";
            
        }
        return ss+"unknown";
    }
}
