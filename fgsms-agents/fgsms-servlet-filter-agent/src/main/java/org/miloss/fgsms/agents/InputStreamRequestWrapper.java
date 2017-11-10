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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.miloss.fgsms.common.Constants;

/**
 *
 * @author AO
 */
public class InputStreamRequestWrapper extends HttpServletRequestWrapper {

    int BUFFER_SIZE = 1024;
    private StringWriter sw = new StringWriter(BUFFER_SIZE);

    public InputStreamRequestWrapper(HttpServletRequest request) {
        super(request);

        try {
            BufferedInputStream buff = new BufferedInputStream(request.getInputStream());
            if (buff.markSupported()) {
                buff.mark(1000000000);
            }
            byte[] buffer = new byte[1024];
            int len = buff.read(buffer);
            while (len > 0) {
                String data = new String(buffer, 0, len,Constants.CHARSET);
                sw.append(data);
                len = buff.read(buffer);
            }
            if (buff.markSupported()) {
                buff.reset();
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(sw.toString().getBytes(Constants.CHARSET));
        return new ServletInputStreamWrapper(in);

    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(sw);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /*
     * returns the actual request message as a string
     */
    @Override
    public String toString() {
        return sw.toString();
    }
}