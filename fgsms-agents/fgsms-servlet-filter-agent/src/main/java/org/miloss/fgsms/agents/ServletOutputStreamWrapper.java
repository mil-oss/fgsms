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

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

/**
 *
 * @author AO
 */
/**
 * A wrapper to provide a concrete implementation of the servlet output stream, so we can wrap other streams.
 * Such as in a filter wrapping a servlet response.
 * @author thein
 *
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {

  OutputStream _out;
  boolean closed = false;
  
  public ServletOutputStreamWrapper(OutputStream realStream) {
    this._out = realStream;
  }
  
  @Override
  public void close() throws IOException {
    if (closed) {
      throw new IOException("This output stream has already been closed");
    }
    _out.flush();
    _out.close();

    closed = true;
  }

  @Override
  public void flush() throws IOException {
    if (closed) {
      throw new IOException("Cannot flush a closed output stream");
    }
    _out.flush();
  }

  @Override
  public void write(int b) throws IOException {
    if (closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    _out.write((byte) b);
  }

  @Override
  public void write(byte b[]) throws IOException {
    write(b, 0, b.length);
  }

  @Override
  public void write(byte b[], int off, int len) throws IOException {
    // System.out.println("writing...");
    if (closed) {
      throw new IOException("Cannot write to a closed output stream");
    }
    _out.write(b, off, len);
  }
  
  
}
