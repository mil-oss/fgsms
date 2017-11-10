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
package org.miloss.fgsms.smoketest.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author la.alex.oree
 */
public class FilterProperties {

     /**
      * removes all keys starting with the prefix
      * @param prefix
      * @param p 
      */
     public static void remove(String prefix, Properties p) {
	  Set<String> keys = new HashSet<String>();
	  Iterator<Map.Entry<Object, Object>> iterator = p.entrySet().iterator();
	  while (iterator.hasNext()) {
	       Map.Entry<Object, Object> next = iterator.next();
	       String key = (String) next.getKey();
	       if (key.startsWith(prefix)) {
		    keys.add(key);
	       }
	  }
	  Iterator<String> iterator1 = keys.iterator();
	  while (iterator1.hasNext()) {
	       p.remove(iterator1.next());
	  }
     }

     /**
      * trims all key's name starting with prefix +"." 
      * @param prefix
      * @param p 
      */
     public static void removeKeyPrefix(String prefix, Properties p) {
	  Set<String> keys = new HashSet<String>();
	  Properties newprops = new Properties();

	  Iterator<Map.Entry<Object, Object>> iterator = p.entrySet().iterator();
	  while (iterator.hasNext()) {
	       Map.Entry<Object, Object> next = iterator.next();
	       String key = (String) next.getKey();
	       if (key.startsWith(prefix)) {
		    key = key.replace(prefix + ".", "");
		    newprops.setProperty(key, (String) next.getValue());
	       } else
		    newprops.put(key, next.getValue());
	  }
	  p.clear();
	  p.putAll(newprops);
     }

}
