/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.common;

import java.security.Principal;

/**
 *
 * @author alex
 */
public class SimplePrincipal implements Principal {

     String name;
     public SimplePrincipal(String uname) {
          name=uname;
     }

     @Override
     public String getName() {
          return name;
     }
     
}
