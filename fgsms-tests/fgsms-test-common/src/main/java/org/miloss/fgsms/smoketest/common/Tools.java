package org.miloss.fgsms.smoketest.common;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by AO on 1/14/2017.
 */
public class Tools {

    /**
     * @since 7.0.0
     * @param connectTO
     */
    public static boolean waitForServer(String connectTO) {
        final long timeout = 5 * 60 * 1000;     //5 minutes
        long start = System.currentTimeMillis();

        while (start + timeout > System.currentTimeMillis()) {
            System.out.println("Connecting to " + connectTO);
            URL url = null;
            try {
                url = new URL(connectTO);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int code = connection.getResponseCode();
                System.out.println("Response code was " + connectTO + " " + code);

                connection.disconnect();
                if (code == 200)
                    return true;

            } catch (Exception e) {
                System.out.println("Connction failed: " + e.getMessage());
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           
        }
         return false;
    }
}
