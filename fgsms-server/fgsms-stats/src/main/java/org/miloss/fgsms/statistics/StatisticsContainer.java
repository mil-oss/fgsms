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

package org.miloss.fgsms.statistics;

/**
 *
 * @author AO
 */
public class StatisticsContainer {

    public long faults = 0;
    public long success = 0;
    public long sla = 0;
    public long averageresponsetime = 0;
    public long mtbf = 0;
    public long max_responsetime = 0;
    public long max_request_size = 0;
    public long max_response_size = 0;
    public long totalprocessingtime = 0;
    public String uri;
    public String action;
    public long timeperiod;
    public double availibity = -1;

}
