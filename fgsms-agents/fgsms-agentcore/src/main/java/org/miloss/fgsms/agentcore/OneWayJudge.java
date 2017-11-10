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
package org.miloss.fgsms.agentcore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.*;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.w3c.dom.Document;

/**
 * Experimental, determines if a URI/SOAP Action is an
 *
 * @OneWAY transaction by pulling the wsdl and parsing
 * @author AO
 * @since 6.2
 */
public class OneWayJudge {

    final static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    final static Map<String, Boolean> cache = new HashMap<String, Boolean>();

    /**
     * attempts to determine if the SOAP service is a one way transaction by parsing the wsdl
     * @param requestURL
     * @param messageContext
     * @param action
     * @return 
     */
    public static boolean determineOneWay(String requestURL, SOAPMessageContext messageContext, String action) {
        String actionLastPart = ToShortActionString(action);
        if (cache.containsKey(requestURL + "|" + actionLastPart)) {
            log.log(Level.DEBUG, "determined @OneWAY from cache");
            return cache.get(requestURL + "|" + actionLastPart);
        }

        //TODO handle ssl, ssl with auth? 

        log.log(Level.INFO, "Operation mappings not available for " + requestURL + " " + action + " attempting to load the wsdl and cache");
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(requestURL + "?wsdl").openConnection();
            conn.setRequestMethod("GET");

            InputStream inputStream = conn.getInputStream();
            conn.connect();

            WSDLReader newWSDLReader = javax.wsdl.factory.WSDLFactory.newInstance().newWSDLReader();
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            Document doc = f.newDocumentBuilder().parse(inputStream);
            conn.disconnect();
            Definition readWSDL = newWSDLReader.readWSDL(null, doc);

            Map allPortTypes = readWSDL.getAllPortTypes();
            Iterator ports = allPortTypes.keySet().iterator();
            while (ports.hasNext()) //for each port
            {
                Object t = ports.next();
                PortType portType = (PortType) allPortTypes.get(t);
                List operations = portType.getOperations();
                for (int i = 0; i < operations.size(); i++) {
                    Operation operation = (Operation) operations.get(i);
                    cache.put(requestURL + "|" + operation.getName(), operation.getOutput() == null || operation.getStyle().equals(OperationType.ONE_WAY));
                }
            }

            if (cache.containsKey(requestURL + "|" + actionLastPart)) {
                return cache.get(requestURL + "|" + actionLastPart);
            }
            log.log(Level.WARN, "the action " + action + " couldn't be found in the wsdl. I'm guessing that its NOT a one way transaction.");
            cache.put(requestURL + "|" + actionLastPart, false);
        } catch (Exception ex) {
            log.log(Level.WARN, "OneWAY  detected failed " + ex.getMessage());
            cache.put(requestURL + "|" + actionLastPart, false);

            return false;
        }
        return false;
    }

    private static String ToShortActionString(String action) {
        if (Utility.stringIsNullOrEmpty(action)) {
            return "";
        }
        String ret = action;
        int clip = 0;
        if (ret.lastIndexOf("/") > clip) {
            clip = ret.lastIndexOf("/");
        }
        if (ret.lastIndexOf("}") > clip) {
            clip = ret.lastIndexOf("}");
        }
        if (ret.lastIndexOf(":") > clip) {
            clip = ret.lastIndexOf(":");
        }
        if (ret.lastIndexOf("#") > clip) {
            clip = ret.lastIndexOf("#");
        }

        if (clip > 0) {
            ret = (ret.substring(clip + 1));
        }
        return ret;

    }
}
