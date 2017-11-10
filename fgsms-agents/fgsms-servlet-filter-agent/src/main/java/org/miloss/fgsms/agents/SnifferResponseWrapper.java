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
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.agents;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author AO
 */
public class SnifferResponseWrapper extends HttpServletResponseWrapper {

    protected Map<String,String> headers = new HashMap<String, String>();
    protected HttpServletResponse wrapped = null;
    protected CharArrayWriter charWriter;
    protected PrintWriter writer;
    protected boolean getOutputStreamCalled;
    protected boolean getWriterCalled;
    int status = SC_OK;

    public SnifferResponseWrapper(HttpServletResponse res) {
        super(res);
        charWriter = new CharArrayWriter();
        wrapped = res;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (getWriterCalled) {
            throw new IllegalStateException("getWriter already called");
        }

        getOutputStreamCalled = true;
        return super.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        if (getOutputStreamCalled) {
            throw new IllegalStateException("getOutputStream already called");
        }
        getWriterCalled = true;
        writer = new PrintWriter(charWriter);
        return writer;
    }

    @Override
    public void sendError(int sc) throws IOException {
        status = sc;
        super.sendError(sc);
    }
    
    
    @Override
    public void sendRedirect(String location) throws IOException {
        status=SC_MOVED_TEMPORARILY;
	super.sendRedirect(location);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        status = sc;
        super.sendError(sc, msg);
    }

    @Override
    public String toString() {
        String s = null;

        if (writer != null) {
            s = charWriter.toString();
        }
        return s;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
         super.setStatus(status);
    }

    public int getStatus() {
        return status;
    }

    public int getContentLength() {
        if (writer != null) {
            return charWriter.size();
        }
        return 0;
    }
    
    /**
     * The default behavior of this method is to return setHeader(String name, String value)
     * on the wrapped response object.
     */
    @Override
    public void setHeader(String name, String value) {
	super.setHeader(name, value);
        headers.put(name, value);
    }
    
    /**
     * The default behavior of this method is to return addHeader(String name, String value)
     * on the wrapped response object.
     */
    @Override
     public void addHeader(String name, String value) {
	super.addHeader(name, value);
        headers.put(name, value);
    }
     

     
    public Map<String,String> getHeaders() {
        return headers;

    }

}
