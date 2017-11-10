/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.wsn;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wsn.b_2.TopicExpressionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 */
public class WSNUtility {

    final static Logger log = Logger.getLogger("WS-NotificationBroker");

    public static List<String> topicExpressionToList(TopicExpressionType te) {
        List<String> items = new ArrayList<String>();
        if (te == null) {
            return items;
        }
        if (te.getDialect().equalsIgnoreCase(WSNConstants.WST_TOPICEXPRESSION_SIMPLE)) {
            for (int k = 0; k < te.getContent().size(); k++) {
                items.add(((String) te.getContent().get(k)).trim());
            }
        }
        return items;
    }

    public static String getWSAAddress(W3CEndpointReference ref) {
        try {
            Document xmlDocument = null;
            xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element element = xmlDocument.createElement("elem");
            ref.writeTo(new DOMResult(element));
            NodeList nl = element.getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", "Address");
            if (nl != null && nl.getLength() > 0) {
                Element e = (Element) nl.item(0);
                log.log(Level.DEBUG, "return " + e.getTextContent().trim() + " for a callback address");
                return e.getTextContent().trim();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "unable to obtain a WS-Addressing endpoint", ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * converts a list of string to a single string whitespace, null and empty
     * strings are ignored
     */
    public static String listStringtoString(List<String> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        String s = "";
        for (int i = 0; i < data.size(); i++) {
            if (!stringIsNullOrEmpty(data.get(i))) {
                String t = data.get(i);
                t = t.trim();
                if (!stringIsNullOrEmpty(t)) {
                    s += t + " ";
                }

            }
        }
        return s.substring(0, s.length() - 1);
    }

    /**
     * why in the world doesn't java have this?
     *
     * @param s
     * @return
     */
    public static boolean stringIsNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }
        if (s.length() == 0) {
            return true;
        }
        return false;
    }
}
