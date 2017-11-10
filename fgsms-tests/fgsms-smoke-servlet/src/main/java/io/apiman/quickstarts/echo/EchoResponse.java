/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apiman.quickstarts.echo;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * https://raw.githubusercontent.com/apiman/apiman-quickstarts/master/echo-service/src/main/java/io/apiman/quickstarts/echo/EchoResponse.java
 * A simple echo response POJO.
 *
 * @author eric.wittmann@redhat.com
 */
public class EchoResponse {

    /**
     * Create an echo response from the inbound information in the http server
     * request.
     * @param request
     * @param withBody 
     * @return a new echo response
     */
    public static EchoResponse from(HttpServletRequest request, boolean withBody) {
        EchoResponse response = new EchoResponse();
        response.setMethod(request.getMethod());
        response.setResource(request.getRequestURI());
        response.setUri(request.getRequestURI());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            response.getHeaders().put(name, value);
        }
        if (withBody) {
            long totalBytes = 0;
            InputStream is = null;
            try {
                is = request.getInputStream();
                MessageDigest sha1 = MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
                byte[] data = new byte[1024];
                int read = 0;
                while ((read = is.read(data)) != -1) {
                    sha1.update(data, 0, read);
                    totalBytes += read;
                };
                
                byte[] hashBytes = sha1.digest();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < hashBytes.length; i++) {
                  sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                String fileHash = sb.toString();
                
                response.setBodyLength(totalBytes);
                response.setBodySha1(fileHash);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try { is.close(); } catch (IOException e) { }
            }
        }
        return response;
    }

    private String method;
    private String resource;
    private String uri;
    private Map<String, String> headers = new HashMap<String, String>();
    private Long bodyLength;
    private String bodySha1;
    
    /**
     * Constructor.
     */
    public EchoResponse() {
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the bodyLength
     */
    public Long getBodyLength() {
        return bodyLength;
    }

    /**
     * @param bodyLength the bodyLength to set
     */
    public void setBodyLength(Long bodyLength) {
        this.bodyLength = bodyLength;
    }

    /**
     * @return the bodySha1
     */
    public String getBodySha1() {
        return bodySha1;
    }

    /**
     * @param bodySha1 the bodySha1 to set
     */
    public void setBodySha1(String bodySha1) {
        this.bodySha1 = bodySha1;
    }

}