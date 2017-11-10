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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletInputStream;
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
public class ServletInputStreamWrapper extends ServletInputStream {

  InputStream _out;
  boolean closed = false;
  
  public ServletInputStreamWrapper(InputStream realStream) {
      if (realStream.markSupported())
          try {
            realStream.reset();
        } catch (IOException ex) {
            Logger.getLogger(ServletInputStreamWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    this._out = realStream;
  }
  
  @Override
  public void close() throws IOException {
    if (closed) {
     // throw new IOException("This output stream has already been closed");
        return;
    }
    _out.close();
    closed = true;
  }

    @Override
    public int read() throws IOException {
        return this._out.read();
    }
  
  
}
