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

package org.miloss.fgsms.agents;

/**
 *
 * @author AO
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * A response wrapper to be used when we want our own custom servlet output stream.
 *
 */
public class OutputStreamResponseWrapper extends HttpServletResponseWrapper {
    /*
    protected HttpServletResponse origResponse = null;
    protected OutputStream realOutputStream = null;
    protected ServletOutputStream stream = null;
    protected PrintWriter writer = null;
    protected int ContentLength=0;
    Class<? extends OutputStream> outputStreamClass;
    
    public OutputStreamResponseWrapper(HttpServletResponse response,
    Class<? extends OutputStream> outputStreamClass) {
    super(response);
    origResponse = response;
    this.outputStreamClass = outputStreamClass;
    }
    
    public ServletOutputStream createOutputStream() throws IOException {
    try {
    Constructor<?> c = outputStreamClass.getConstructor(new Class[]{HttpServletResponse.class});
    realOutputStream = (OutputStream) c.newInstance(origResponse);
    return new ServletOutputStreamWrapper(realOutputStream);
    } catch (Exception ex) {
    throw new IOException("Unable to construct servlet output stream: " + ex.getMessage(), ex);
    }
    }
    
    public void finishResponse() {
    try {
    if (writer != null) {
    writer.close();
    } else {
    if (stream != null) {
    stream.close();
    }
    }
    } catch (IOException e) {
    }
    }
    
    @Override
    public void flushBuffer() throws IOException {
    stream.flush();
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
    if (writer != null) {
    throw new IllegalStateException("getOutputStream() has already been called!");
    }
    
    if (stream == null) {
    stream = createOutputStream();
    }
    return stream;
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
    if (writer != null) {
    return (writer);
    }
    
    if (stream != null) {
    throw new IllegalStateException("getOutputStream() has already been called!");
    }
    
    stream = createOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
    return (writer);
    }
    
    @Override
    public void setContentLength(int length) {
    this.ContentLength = length;
    super.setContentLength(length);
    }
    
    public int getContentLength()
    {
    return ContentLength;
    }
    
    @Override
    public void sendError(int sc) throws IOException {
    httpStatus = sc;
    super.sendError(sc);
    }
    
    @Override
    public void sendError(int sc, String msg) throws IOException {
    httpStatus = sc;
    super.sendError(sc, msg);
    }
    
    @Override
    public void setStatus(int sc) {
    httpStatus = sc;
    super.setStatus(sc);
    }
    
    public int getStatus() {
    return httpStatus;
    }
    private int httpStatus = 200;
    
    @Override
    public void sendRedirect(String location) throws IOException {
    httpStatus = 302;
    super.sendRedirect(location);
    }
    
    /**
     * Gets the underlying instance of the output stream.
     * @return
    
    public OutputStream getRealOutputStream() {
    return realOutputStream;
    }*/

    public OutputStreamResponseWrapper(HttpServletResponse response) {
        super(response);
    }
    private int BUFFER_SIZE = 1024;
    private StringWriter sw = new StringWriter(BUFFER_SIZE);

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(sw);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return sw.toString();
    }

    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    //@Override
    public int getStatus() {
        return httpStatus;
    }
    private int httpStatus = 200;
    int ContentLength = 0;

    @Override
    public void setContentLength(int length) {
        this.ContentLength = length;
        super.setContentLength(length);
    }

    public int getContentLength() {
        return ContentLength;
    }
}