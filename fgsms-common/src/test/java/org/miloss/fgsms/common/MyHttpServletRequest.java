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

import java.io.*;
import java.security.Principal;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 *
 * @author AO
 */
public class MyHttpServletRequest implements HttpServletRequest {

    public MyHttpServletRequest(String username) {
        uname = username;
    }
    String url = "http://localhost:8180/fgsmsServices/DCS";

    public MyHttpServletRequest(String username, String requesturl) {
        uname = username;
        url = requesturl;
    }

    public String getAuthType() {
        return "Basic";
    }

    public Cookie[] getCookies() {
        return null;
    }

    public long getDateHeader(String string) {
        return 0;
    }

    public String getHeader(String string) {
        return null;
    }

    public Enumeration<String> getHeaders(String string) {
        return null;
    }

    public Enumeration<String> getHeaderNames() {
        return null;
    }

    public int getIntHeader(String string) {
        return 200;
    }

    public String getMethod() {
        return "POST";
    }

    public String getPathInfo() {
        return null;
    }

    public String getPathTranslated() {
        return null;
    }

    public String getContextPath() {
        return null;
    }

    public String getQueryString() {
        return null;
    }
    private String uname = "";

    public String getRemoteUser() {
        return uname;
    }

    public boolean isUserInRole(String string) {
        return false;
    }

    public Principal getUserPrincipal() {
        return new SimplePrincipal(uname);
    }

    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getRequestURI() {
        return url;
    }

    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getServletPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HttpSession getSession(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public HttpSession getSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean authenticate(HttpServletResponse hsr) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void login(String string, String string1) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void logout() throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Part getPart(String string) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getAttribute(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getContentLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getContentType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    public String getParameter(String string) {
        return null;
    }

    public Enumeration<String> getParameterNames() {
        return null;
    }

    public String[] getParameterValues(String string) {
        return null;
    }

    public Map<String, String[]> getParameterMap() {
        return null;
    }

    public String getProtocol() {
        return null;
    }

    public String getScheme() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public int getServerPort() {
        return 8180;
    }

    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getRemoteAddr() {
        return "127.0.0.1";
    }

    public String getRemoteHost() {
        return "fgsmsdev1";
    }

    public void setAttribute(String string, Object o) {
    }

    public void removeAttribute(String string) {
    }

    public Locale getLocale() {
        return null;
    }

    public Enumeration<Locale> getLocales() {
        return null;
    }

    public boolean isSecure() {
        return false;
    }

    public RequestDispatcher getRequestDispatcher(String string) {
        return null;
    }

    public String getRealPath(String string) {
        return null;
    }

    public int getRemotePort() {
        return 12345;
    }

    public String getLocalName() {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public int getLocalPort() {
        return 8180;
    }

    public ServletContext getServletContext() {
        return null;
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    public AsyncContext startAsync(ServletRequest sr, ServletResponse sr1) throws IllegalStateException {
        return null;
    }

    public boolean isAsyncStarted() {
        return false;
    }

    public boolean isAsyncSupported() {
        return false;
    }

    public AsyncContext getAsyncContext() {
        return null;
    }

    public DispatcherType getDispatcherType() {
        return null;
    }

}