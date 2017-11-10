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

package org.miloss.fgsms.common;

/**
 *Publication Constants - these values are used for publishing data to federation targets 
 * <p>As of RC6, this class provides UDDI tModel key information.</p>
 * @author AO
 * @since RC6
 */
public class PublicationConstants {
    public static final String UDDI_USE_TYPE_ENDPOINT="endPoint";
    public static final String UDDI_USE_TYPE_BINDING_TEMPLATE_REFERENCE="bindingTemplate";
    public static final String UDDI_USE_TYPE_BINDING_HOSTING_REDIRECT="hostingRedirector";
    
    
 public static String[] getAllTmodelKeys()
        {
            return new String[]{
                    tmodelSuccessCount5min, tmodelFailureCount5min, tmodelAverageResponseTime5min, tmodelMTBF5min, tmodelSLAViolations5min, tmodelUpDownTimePercentage5min, tmodelMaxRequestSize5min, tmodelMaxResponseSize5min, tmodelMaxresponseTime5min,
                    tmodelSuccessCount15min, tmodelFailureCount15min, tmodelAverageResponseTime15min, tmodelMTBF15min, tmodelSLAViolations15min, tmodelUpDownTimePercentage15min, tmodelMaxRequestSize15min, tmodelMaxResponseSize15min, tmodelMaxresponseTime15min,
                    tmodelSuccessCount60min, tmodelFailureCount60min, tmodelAverageResponseTime60min, tmodelMTBF60min, tmodelSLAViolations60min, tmodelUpDownTimePercentage60min, tmodelMaxRequestSize60min, tmodelMaxResponseSize60min, tmodelMaxresponseTime60min,
                    tmodelSuccessCount24hr, tmodelFailureCount24hr, tmodelAverageResponseTime24hr, tmodelMTBF24hr, tmodelSLAViolations24hr, tmodelUpDownTimePercentage24hr, tmodelMaxRequestSize24hr, tmodelMaxResponseSize24hr, tmodelMaxresponseTime24hr,
                    tmodelOperationalStatus, tmodelTimeRange, tmodelPublishTimeStamp, tmodelOperationalMonitoredBy, tmodelOperationalStatusTimeStamp, tmodelOperationalStatuschange
                };  //30 items
        }
 
 public static boolean isTmodelFromfgsms(String tmodelKey)
 {
     if (tmodelKey==null)
         return false;
     String [] keys=getAllTmodelKeys();
     for (int i=0; i < keys.length; i++)
     {
         if (keys[i].equalsIgnoreCase(tmodelKey))
             return true;
         
     }
     return false;
 }

   /**
        * success
       */
        //"uddi:juddi.apache.org:something
        public static final String tmodelKeyGen = "uddi:fgsms:keygenerator";
        public static final String tmodelKeyGenText = "fgsms:KeyGenator";
        
 
 
        /**
        * success
       */
        //"uddi:juddi.apache.org:something
        public static final String tmodelSuccessCount5min = "uddi:fgsms:successcount5min";
        public static final String tmodelSuccessCountDescription5min = "fgsms:SuccessCount5min";
        public static final String tmodelSuccessText = "This represents the number of times this service has been succesfully invoked over the given time.";
        /**
        * failure
       */
        public static final String tmodelFailureCount5min = "uddi:fgsms:failurecount5min";
        public static final String tmodelFailureCountDescription5min = "fgsms:FailureCount5min";
        public static final String tmodelFailureCountText = "This represents the number of times this service has faulted over the given period of time.";
        /**
        * response time
       */
        public static final String tmodelAverageResponseTime5min = "uddi:fgsms:averageresponsetime5min";
        public static final String tmodelAverageResponseTimeDescription5min = "fgsms:AverageResponseTime5min";
        public static final String tmodelAverageResponseTimeText = "This represents the average response time in milliseconds for the service over the given period of time.";
        /**
        * MTBF
       */
        public static final String tmodelMTBF5min = "uddi:fgsms:mfbf5min";
        public static final String tmodelMTBFDescription5min = "fgsms:MTBF5min";
        public static final String tmodelMTBFText = "This represents the mean time between failure for the service over the given period of time.";
        /**
        * SLA Violations
       */
        public static final String tmodelSLAViolations5min = "uddi:fgsms:slaviolations5min";
        public static final String tmodelSLAViolationsDescription5min = "fgsms:SLAViolations5min";
        public static final String tmodelSLAViolationsText = "This represents the number of SLA Violations for the service over the given period of time. Keep in mind that SLA violations can be set up to trigger on every transaction and thus is not necessarily a measure of the service's reliability or availability.";

        /**
        * SLA Violations
       */
        public static final String tmodelUpDownTimePercentage5min = "uddi:fgsms:availability5min";
        public static final String tmodelUpDownTimePercentageDescription5min = "fgsms:Availability5min";
        public static final String tmodelUpDownTimePercentageText = "This represents the percentage of the time period from which this service was available.";


        /**
        * Max Response Time
       */
        public static final String tmodelMaxresponseTime5min = "uddi:fgsms:maxresponsetime5min";
        public static final String tmodelMaxresponseTimeDescription5min = "fgsms:MaxResponseTime5min";
        public static final String tmodelMaxresponseTimeText = "This represents the maximum response time in milliseconds during the given period of time.";


        /**
        * Max Request Size
       */
        public static final String tmodelMaxRequestSize5min = "uddi:fgsms:maxrequestsize5min";
        public static final String tmodelMaxRequestSizeDescription5min = "fgsms:MaxRequestSize5min";
        public static final String tmodelMaxRequestSizeText = "This represents the maximum request size in bytes during the given period of time.";


        /**
        * Max Response Size
       */
        public static final String tmodelMaxResponseSize5min = "uddi:fgsms:maxresponsesize5min";
        public static final String tmodelMaxResponseSizeDescription5min = "fgsms:MaxResponesSize5min";
        public static final String tmodelMaxResponseSizeText = "This represents the maximum response size in bytes during the given period of time.";










        /**
        * success
       */
        //"uddi:juddi.apache.org:something
        public static final String tmodelSuccessCount15min = "uddi:fgsms:successcount15min";
        public static final String tmodelSuccessCountDescription15min = "fgsms:SuccessCount15min";
        /**
        * failure
       */
        public static final String tmodelFailureCount15min = "uddi:fgsms:failurecount15min";
        public static final String tmodelFailureCountDescription15min = "fgsms:FailureCount15min";
        /**
        * response time
       */
        public static final String tmodelAverageResponseTime15min = "uddi:fgsms:averageresponsetime15min";
        public static final String tmodelAverageResponseTimeDescription15min = "fgsms:AverageResponseTime15min";

        /**
        * MTBF
       */
        public static final String tmodelMTBF15min = "uddi:fgsms:mfbf15min";
        public static final String tmodelMTBFDescription15min = "fgsms:MTBF15min";
        /**
        * SLA Violations
       */
        public static final String tmodelSLAViolations15min = "uddi:fgsms:slaviolations15min";
        public static final String tmodelSLAViolationsDescription15min = "fgsms:SLAViolations15min";

        /**
        * SLA Violations
       */
        public static final String tmodelUpDownTimePercentage15min = "uddi:fgsms:availability15min";
        public static final String tmodelUpDownTimePercentageDescription15min = "fgsms:Availability15min";


        /**
        * Max Response Time
       */
        public static final String tmodelMaxresponseTime15min = "uddi:fgsms:maxresponsetime15min";
        public static final String tmodelMaxresponseTimeDescription15min = "fgsms:MaxResponseTime15min";


        /**
        * Max Request Size
       */
        public static final String tmodelMaxRequestSize15min = "uddi:fgsms:maxrequestsize15min";
        public static final String tmodelMaxRequestSizeDescription15min = "fgsms:MaxResponseTime15min";


        /**
        * Max Response Size
       */
        public static final String tmodelMaxResponseSize15min = "uddi:fgsms:maxresponsesize15min";
        public static final String tmodelMaxResponseSizeDescription15min = "fgsms:MaxResponesTime15min";










        /**
        * success
       */
        //"uddi:juddi.apache.org:something
        public static final String tmodelSuccessCount60min = "uddi:fgsms:successcount60min";
        public static final String tmodelSuccessCountDescription60min = "fgsms:SuccessCount60min";
        /**
        * failure
       */
        public static final String tmodelFailureCount60min = "uddi:fgsms:failurecount60min";
        public static final String tmodelFailureCountDescription60min = "fgsms:FailureCount60min";
        /**
        * response time
       */
        public static final String tmodelAverageResponseTime60min = "uddi:fgsms:averageresponsetime60min";
        public static final String tmodelAverageResponseTimeDescription60min = "fgsms:AverageResponseTime60min";

        /**
        * MTBF
       */
        public static final String tmodelMTBF60min = "uddi:fgsms:mfbf60min";
        public static final String tmodelMTBFDescription60min = "fgsms:MTBF60min";

        /**
        * SLA Violations
       */
        public static final String tmodelSLAViolations60min = "uddi:fgsms:slaviolations60min";
        public static final String tmodelSLAViolationsDescription60min = "fgsms:SLAViolations60min";


        /**
        * SLA Violations
       */
        public static final String tmodelUpDownTimePercentage60min = "uddi:fgsms:availability60min";
        public static final String tmodelUpDownTimePercentageDescription60min = "fgsms:Availability60min";



        /**
        * Max Response Time
       */
        public static final String tmodelMaxresponseTime60min = "uddi:fgsms:maxresponsetime60min";
        public static final String tmodelMaxresponseTimeDescription60min = "fgsms:MaxResponseTime60min";



        /**
        * Max Request Size
       */
        public static final String tmodelMaxRequestSize60min = "uddi:fgsms:maxrequestsize60min";
        public static final String tmodelMaxRequestSizeDescription60min = "fgsms:MaxResponseTime60min";



        /**
        * Max Response Size
       */
        public static final String tmodelMaxResponseSize60min = "uddi:fgsms:maxresponsesize60min";
        public static final String tmodelMaxResponseSizeDescription60min = "fgsms:MaxResponesTime60min";













        /**
        * success
       */
        //"uddi:juddi.apache.org:something
        public static final String tmodelSuccessCount24hr = "uddi:fgsms:successcount24hr";
        public static final String tmodelSuccessCountDescription24hr = "fgsms:SuccessCount24hr";
        /**
        * failure
       */
        public static final String tmodelFailureCount24hr = "uddi:fgsms:failurecount24hr";
        public static final String tmodelFailureCountDescription24hr = "fgsms:FailureCount24hr";
        /**
        * response time
       */
        public static final String tmodelAverageResponseTime24hr = "uddi:fgsms:averageresponsetime24hr";
        public static final String tmodelAverageResponseTimeDescription24hr = "fgsms:AverageResponseTime24hr";
        /**
        * MTBF
       */
        public static final String tmodelMTBF24hr = "uddi:fgsms:mfbf24hr";
        public static final String tmodelMTBFDescription24hr = "fgsms:MTBF24hr";
        /**
        * SLA Violations
       */
        public static final String tmodelSLAViolations24hr = "uddi:fgsms:slaviolations24hr";
        public static final String tmodelSLAViolationsDescription24hr = "fgsms:SLAViolations24hr";

        /**
        * Up time percentage
       */
        public static final String tmodelUpDownTimePercentage24hr = "uddi:fgsms:availability24hr";
        public static final String tmodelUpDownTimePercentageDescription24hr = "fgsms:Availability24hr";


        /**
        * Max Response Time
       */
        public static final String tmodelMaxresponseTime24hr = "uddi:fgsms:maxresponsetime24hr";
        public static final String tmodelMaxresponseTimeDescription24hr = "fgsms:MaxResponseTime24hr";


        /**
        * Max Request Size
       */
        public static final String tmodelMaxRequestSize24hr = "uddi:fgsms:maxrequestsize24hr";
        public static final String tmodelMaxRequestSizeDescription24hr = "fgsms:MaxResponseTime24hr";


        /**
        * Max Response Size
       */
        public static final String tmodelMaxResponseSize24hr = "uddi:fgsms:maxresponsesize24hr";
        public static final String tmodelMaxResponseSizeDescription24hr = "fgsms:MaxResponesTime24hr";










        /**]
        * current operational status
       */
        public static final String tmodelOperationalStatus = "uddi:fgsms:operationalstatus";
        public static final String tmodelOperationalStatusDescription = "fgsms:OperationalStatus";
        public static final String tmodelOperationalStatusText = "This represents the last known operational status of this resource.";
        /**
        * duration since the last status change
       */
        public static final String tmodelOperationalStatuschange = "uddi:fgsms:operationalstatuslastchange";
        public static final String tmodelOperationalStatuschangeDescription = "fgsms:OperationalStatusLastChange";
        public static final String tmodelOperationalStatuschangeText = "This represents time/date of this service's last change of status.";
        /**
        * current operational status timestamp
       */
        public static final String tmodelOperationalStatusTimeStamp = "uddi:fgsms:operationalstatustimestamp";
        public static final String tmodelOperationalStatusTimeStampDescription = "fgsms:OperationalStatusTimeStamp";
        public static final String tmodelOperationalStatusTimeStampText = "This represents the time/date stamp of the last known operational status of this resource.";
        /**
        * if it's monitored by fgsms's Bueller processor or not
       */
        public static final String tmodelOperationalMonitoredBy = "uddi:fgsms:monitoredby";
        public static final String tmodelOperationalMonitoredByDescription = "fgsms:MonitoredBy";
        public static final String tmodelOperationalMonitoredByText = "This represents whether or not this resource is directly monitored by fgsms (true) or if it is monitored by an external agent (false).";
        /**
        * timestamp that this data was published to uddi
       */
        public static final String tmodelPublishTimeStamp = "uddi:fgsms:publishtimestamp";
        public static final String tmodelPublishTimeStampDescription = "fgsms:PublishTimeStamp";
        public static final String tmodelPublishTimeStampText = "This represents the time/date at which these metrics were published.";

        /**
        * deprecated time range
       */
        @Deprecated
        public static final String tmodelTimeRange = "uddi:fgsms:timerange";
        @Deprecated
        public static final String tmodelTimeRangeDescription = "fgsms:TimeRange";
        @Deprecated
        public static final String tmodelTimeRangeText = "This represents the average response time in milliseconds for the service over the given period of time.";

        /**
        * english language
       */
        public static final String lang = "en";
        
        /**
         *  UDDI search wild card
         */
        public static final String UDDI_WILDCARD="%";
        
        
        public static final String UDDI_FIND_QUALIFIER_CASE_INSENSITIVE_MATCH = "caseInsensitiveMatch";
        public static final String UDDI_FIND_QUALIFIER_APPROXIMATE_MATCH = "approximateMatch";
}
