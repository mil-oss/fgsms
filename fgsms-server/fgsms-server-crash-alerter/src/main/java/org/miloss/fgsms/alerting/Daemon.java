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
 
package org.miloss.fgsms.alerting;

/**
 *
 * @author AO
 */
public class Daemon {

    private static ThreadRunner runner = null;

    public static void start(String[] args) {
        runner = new ThreadRunner(args);
        t = new Thread(runner);
        t.start();
    }
    static Thread t;

    public static void stop(String[] args) {
        if (t != null && t.isAlive() && runner != null) {
            runner.m.running = false;
            try {
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
