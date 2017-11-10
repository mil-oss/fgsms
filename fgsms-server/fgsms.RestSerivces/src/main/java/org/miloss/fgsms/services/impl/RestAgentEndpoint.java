/*
 * Copyright 2015 alex.
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
package org.miloss.fgsms.services.impl;


import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author alex
 */
@Path("/")
@Produces({"application/octet-stream"})
@org.apache.cxf.jaxrs.model.wadl.Description("")
public class RestAgentEndpoint {

     @POST
     @javax.ws.rs.Path("/kryo/dcs/addMoreData")
     @Produces("application/octet-stream")
     @org.apache.cxf.jaxrs.model.wadl.Description("Returns png of the specific map tile from the database")
     public byte[] DataCollectorAddMoreData(@javax.ws.rs.FormParam("request") byte[] bits) {
          return null;
     }

     @POST
     @javax.ws.rs.Path("/kryo/dcs/addData")
     @Produces("application/octet-stream")
     @org.apache.cxf.jaxrs.model.wadl.Description("Returns png of the specific map tile from the database")
     public byte[] DataCollectorAddDataKryo(@javax.ws.rs.FormParam("request") byte[] bits) {
          return null;
     }

     @POST
     @javax.ws.rs.Path("/json/dcs/addData")
     @Produces("application/octet-stream")
     @org.apache.cxf.jaxrs.model.wadl.Description("Returns png of the specific map tile from the database")
     public String DataCollectorAddDataJson(@javax.ws.rs.FormParam("request") String bits) {
          return null;
     }

}
