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

package org.miloss.fgsms.uddipub;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicy;

/**
 * For UDDI Publisher, utility helper methods for publication
 *
 * @author AO
 */
public class FederationPolicyExt {

    public FederationPolicyExt(FederationPolicy parent) {
        ref = parent;
    }

    public FederationPolicy getParentObject() {
        return ref;
    }
    private FederationPolicy ref = null;

    public List<Duration> getPublishTimeRange() {
        List<Duration> ret = new ArrayList<Duration>();
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_TIME_RANGE);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            String[] durs = val.split(",");
            try {
                DatatypeFactory fac = DatatypeFactory.newInstance();
                for (int i = 0; i < durs.length; i++) {
                    long x = Long.parseLong(durs[i]);
                    ret.add(fac.newDuration(x));
                }
            } catch (Exception ex) {
                UddiPublisher.log.log(Level.ERROR, "unable to retrieve durations for federation publication", ex);
            }

        }
        return ret;
    }

    public boolean isPublishAverageResponseTime() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_AVG_RES_TIME);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublishSuccessCount() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_SUCCESS);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublishFailureCount() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_FAULTS);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublishUpTimePercent() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_UPTIME);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublishMaximums() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_MAX);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublishSLAFaults() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_SLA);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public boolean isPublishLastKnownStatus() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_STATUS);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }

    public void setFederationTarget(String target) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.FederationTarget_UDDI_Publisher) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.FederationTarget_UDDI_Publisher, target, false, false));
        }
    }

    public void setPublishAverageResponseTime(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_AVG_RES_TIME) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_AVG_RES_TIME, Boolean.toString(b), false, false));
        }
    }

    public void setPublishFailureCount(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_FAULTS) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_FAULTS, Boolean.toString(b), false, false));
        }
    }

    public void setPublishLastKnownStatus(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_STATUS) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_STATUS, Boolean.toString(b), false, false));
        }
    }

    public void setPublishMaximums(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_MAX) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_MAX, Boolean.toString(b), false, false));
        }
    }

    public void setPublishSuccessCount(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_SUCCESS) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_SUCCESS, Boolean.toString(b), false, false));
        }
    }

    public void setPublishTimeSinceLastStatusChange(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_UPTIME) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_UPTIME, Boolean.toString(b), false, false));
        }
    }

    public void setPublishUpTimePercent(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_UPTIME) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_UPTIME, Boolean.toString(b), false, false));
        }
    }

    public void setPublishSLAFaults(boolean b) {
        if (Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_SLA) == null) {
            ref.getParameterNameValue().add(Utility.newNameValuePair(UddiPublisher.OPTION_PUBLISH_SLA, Boolean.toString(b), false, false));
        }
    }

    public String getBindingKey() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_BINDING_KEY);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }

            return val;
        }
        return null;
    }

    boolean isPublishTimeSinceLastStatusChange() {
        NameValuePair target = Utility.getNameValuePairByName(ref.getParameterNameValue(), UddiPublisher.OPTION_PUBLISH_UPTIME);
        if (target != null) {
            String val = target.getValue();
            if (target.isEncrypted()) {
                val = Utility.DE(val);
            }
            if (val.equalsIgnoreCase("true")) {
                return true;
            }
        }
        return false;
    }


    
}
