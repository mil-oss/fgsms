<%-- 
    Document   : index
    Created on : Mar 20, 2012, 11:18:21 AM
    Author     : Administrator
--%>

<%@page import="org.miloss.fgsms.agents.JAXWSGenericClientAgent"%>
<%@page import="java.util.ArrayList"%>
<%@page import="javax.xml.ws.handler.Handler"%>
<%@page import="java.util.List"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="com.examples.wsdl.helloservice.*"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>

        <form method="POST">
            Your name: <input type="text" name="data" id="data" value="your name"><br>
            <input type="submit" name="callno1" value="Call Number 1">
            <input type="submit" name="callno2" value="Call Number 2">

        </form>
        <%
            if (request.getMethod().equalsIgnoreCase("post")) {
                boolean call1 = true;
                String button = request.getParameter("callno1");
                if (button == null || button.isEmpty()) {
                    call1 = false;
                }

                HelloServiceClient svc = new HelloServiceClient();
                HelloPortType port = svc.getHelloPort();
                BindingProvider bp = (BindingProvider) port;
                if (call1) {
                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8888/HelloWorldJAXWS/services/Number1");
                } else {
                    //call 2
                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8888/HelloWorldJAXWS/services/Number2");
                }
                List<Handler> handlers = bp.getBinding().getHandlerChain();
                if (handlers==null)
                    handlers = new ArrayList<Handler>();
                handlers.add(new JAXWSGenericClientAgent());
                bp.getBinding().setHandlerChain(handlers);
                
                SayHello req = new SayHello();
                SayHelloReq r = new SayHelloReq();
                r.setYourname(request.getParameter("data"));
                req.setSayHelloReq(r);
                SayHelloResponse res = port.sayHello(req);
                //TODO handle errors
                out.write("<br>Response from service: " + res.getSayHelloRes().getGreeting());
            }
        %>
    </body>
</html>
