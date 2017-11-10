/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * You may add additional accurate notices of copyright ownership.
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.smoke.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.miloss.fgsms.common.Constants;

/**
 * borrowed from here
 * https://github.com/apiman/apiman-quickstarts/blob/master/echo-service/src/main/java/io/apiman/quickstarts/echo/EchoServlet.java
 * ASF license
 * @author AO
 */
public class NoFrameworkRestServlet extends HttpServlet {

     protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String protocol = req.getProtocol();

        resp.getOutputStream().write("<html><body>hello world</body></html>".getBytes(Constants.CHARSET));
    }

}
