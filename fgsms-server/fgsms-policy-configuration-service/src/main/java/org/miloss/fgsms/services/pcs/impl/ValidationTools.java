/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.miloss.fgsms.services.pcs.impl;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.federation.FederationInterface;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicyCollection;

/**
 * eventually, all of the validation stuff for the PCS service will end up here
 * with the goal of reducing the line counts
 * @author AO
 */
public class ValidationTools {

    //private static final String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    //private static final Pattern FQCN = Pattern.compile(ID_PATTERN + "(\\." + ID_PATTERN + ")*");
    // return FQCN.matcher(identifier).matches();

     // All Java reserved words that must not be used in a valid package name.
    private static final HashSet reserved;

    static {
        reserved = new HashSet();
        reserved.add("abstract");reserved.add("assert");reserved.add("boolean");
        reserved.add("break");reserved.add("byte");reserved.add("case");
        reserved.add("catch");reserved.add("char");reserved.add("class");
        reserved.add("const");reserved.add("continue");reserved.add("default");
        reserved.add("do");reserved.add("double");reserved.add("else");
        reserved.add("enum");reserved.add("extends");reserved.add("false");
        reserved.add("final");reserved.add("finally");reserved.add("float");
        reserved.add("for");reserved.add("if");reserved.add("goto");
        reserved.add("implements");reserved.add("import");reserved.add("instanceof");
        reserved.add("int");reserved.add("interface");reserved.add("long");
        reserved.add("native");reserved.add("new");reserved.add("null");
        reserved.add("package");reserved.add("private");reserved.add("protected");
        reserved.add("public");reserved.add("return");reserved.add("short");
        reserved.add("static");reserved.add("strictfp");reserved.add("super");
        reserved.add("switch");reserved.add("synchronized");reserved.add("this");
        reserved.add("throw");reserved.add("throws");reserved.add("transient");
        reserved.add("true");reserved.add("try");reserved.add("void");
        reserved.add("volatile");reserved.add("while");
    }

    /**
     * Checks if the string that is provided is a valid Java package name (contains only
     * [a-z,A-Z,_,$], every element is separated by a single '.' , an element can't be one of Java's
     * reserved words.
     *
     * @param name The package name that needs to be validated.
     * @return <b>true</b> if the package name is valid, <b>false</b> if its not valid.
     */
    public static final boolean isValidPackageName(String name) {
        String[] parts=name.split("\\.",-1);
        for (String part:parts){
            System.out.println(part);
            if (reserved.contains(part)) return false;
            if (!validPart(part)) return false;
        }
        
        return true;
    }

    /**
     * Checks that a part (a word between dots) is a valid part to be used in a Java package name.
     * @param part The part between dots (e.g. *PART*.*PART*.*PART*.*PART*).
     * @return <b>true</b> if the part is valid, <b>false</b> if its not valid.
     */
    private static boolean validPart(String part){
        if (part==null || part.length()<1){
            // Package part is null or empty !
            return false;
        }
        if (Character.isJavaIdentifierStart(part.charAt(0))){
            for (int i = 0; i < part.length(); i++){
                char c = part.charAt(i);
                if (!Character.isJavaIdentifierPart(c)){
                    // Package part contains invalid JavaIdentifier !
                    return false;
                }
            }
        }else{
            // Package part does not begin with a valid JavaIdentifier !
            return false;
        }

        return true;
    }
    

    public static void validateFederationPolicies(FederationPolicyCollection federationPolicyCollection) {
        if (federationPolicyCollection == null) {
            return;
        }
        if (federationPolicyCollection.getFederationPolicy().isEmpty()) {
            return;
        }
        for (FederationPolicy pol : federationPolicyCollection.getFederationPolicy()) {
            if (Utility.stringIsNullOrEmpty(pol.getImplementingClassName())) {
                throw new IllegalArgumentException("federation class name is null or empty");
            }
            AtomicReference<String> outMessage = new AtomicReference<String>("");
            try {
                FederationInterface plugin = (FederationInterface) Class.forName(pol.getImplementingClassName()).newInstance();
                if (!plugin.ValidateConfiguration(pol, outMessage)) {
                    throw new IllegalArgumentException("federation policy parameters are invalid, msg: " + outMessage.get());
                }
            } catch (Throwable ex) {
                PCS4jBean.log.error("Unable to load class " + pol.getImplementingClassName(), ex);
                throw new IllegalArgumentException("federation policy class is invalid: " + pol.getImplementingClassName() + ": " + ex.getMessage());
            }
        }
    }
    
    
}
