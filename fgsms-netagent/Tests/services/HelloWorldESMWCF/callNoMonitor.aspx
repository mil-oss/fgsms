<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="callNoMonitor.aspx.cs"
    Inherits="HelloWorldESMTester.call" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title></title>
</head>
<body>
    <%
      
        
      System.ServiceModel.BasicHttpBinding b = null;

      b = new System.ServiceModel.BasicHttpBinding(System.ServiceModel.BasicHttpSecurityMode.None);

      b.MaxBufferSize = Int32.MaxValue;
      b.MaxReceivedMessageSize = Int32.MaxValue;
      b.MaxBufferPoolSize = Int32.MaxValue;
      b.TransferMode = System.ServiceModel.TransferMode.Buffered;
      b.ReaderQuotas.MaxArrayLength = Int32.MaxValue;
      b.ReaderQuotas.MaxBytesPerRead = Int32.MaxValue;
      b.ReaderQuotas.MaxDepth = Int32.MaxValue;
      b.ReaderQuotas.MaxNameTableCharCount = Int32.MaxValue;
      b.ReaderQuotas.MaxStringContentLength = Int32.MaxValue;
      b.ReceiveTimeout = new TimeSpan(0, 5, 0);
      b.SendTimeout = new TimeSpan(0, 5, 0);
      b.UseDefaultWebProxy = true;
      b.TextEncoding = Encoding.UTF8;
      b.OpenTimeout = new TimeSpan(0, 0, 5);
      
      b.MessageEncoding = System.ServiceModel.WSMessageEncoding.Text;
      b.CloseTimeout = new TimeSpan(0, 0, 5);
      b.Security.Transport.ClientCredentialType = System.ServiceModel.HttpClientCredentialType.Basic;
      System.ServiceModel.ChannelFactory<HelloWorldESMTester.IService1> factory =
          new System.ServiceModel.ChannelFactory<HelloWorldESMTester.IService1>(b, System.Web.Configuration.WebConfigurationManager.AppSettings["executionurl"]);
      b.Security.Transport.ClientCredentialType = System.ServiceModel.HttpClientCredentialType.Basic;
      
      HelloWorldESMTester.IService1 polservice = factory.CreateChannel();
      string s = "";
      try
      {
          s = polservice.WorkingGetData(5);
          Response.StatusCode = 200;
          //Response.Status = "OK";
      }
      catch (Exception ex)
      {
          s = "ERROR ";
          while (ex != null) 
          {
              s += ex.Message + ex.StackTrace;
              ex = ex.InnerException;
              Response.StatusCode = 500;
              //Response.Status = "ERROR";
          }
      }
    %>
    <%=s%>
</body>
</html>
