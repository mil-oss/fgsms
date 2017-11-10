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
import java.util.*;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author AO
 */
    public class MyMessageContext implements MessageContext {

        public MyMessageContext() {
            map = new HashMap();
        }

        public void setScope(String name, Scope scope) {
        }

        public Scope getScope(String name) {
            return null;
        }

        public int size() {
            return map.size();
        }

        public boolean isEmpty() {
            return map.isEmpty();
        }

        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        public Object get(Object key) {
            return map.get(key);
        }
        private Map map = null;

        public Object put(String key, Object value) {
            return map.put(key, value);
        }

        public Object remove(Object key) {
            return map.remove(key);
        }

        public void putAll(Map<? extends String, ? extends Object> m) {
            map.putAll(map);
        }

        public void clear() {
            map.clear();
        }

        public Set<String> keySet() {
            return map.entrySet();
        }

        public Collection<Object> values() {
            return map.values();
        }

        public Set<Entry<String, Object>> entrySet() {
            return map.entrySet();
        }
    }