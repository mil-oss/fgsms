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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;



public partial class PCSBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
{
    /// <summary>
    /// Use this field to enable user impersonation. 
    /// </summary>
    /// This should only be used when 
    /// 1) accessing FGSMS services from a web site/application
    /// AND
    /// 2) accessing FGSMS services using CAC/PKI logins
    /// AND
    /// 3) a PKI certificate is provided
    /// AND 
    /// 4) The FGSMS Services war file is configured to allow user impersonation from the PKI certificate
    /// 
    /// default is false.
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    public bool useDoubleHopAuthWithPKI = false;
    /// <summary>
    /// If user impersonation is enabled, this field will be sent to the server via an HTTP header

    /// </summary>
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    /// @seealso useDoubleHopAuthWithPKI
    public string actual_username = String.Empty;
    protected override System.Net.WebRequest GetWebRequest(Uri uri)
    {
        HttpWebRequest r = (HttpWebRequest)base.GetWebRequest(uri);
        if (useDoubleHopAuthWithPKI && !String.IsNullOrEmpty(actual_username) && this.ClientCertificates.Count > 0)
        {
            r.Headers.Add("FGSMS.authorization", actual_username);
        }
        return r;
        //return base.GetWebRequest(uri);
    }
}


public partial class DCSBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
{
    /// <summary>
    /// Use this field to enable user impersonation. 
    /// </summary>
    /// This should only be used when 
    /// 1) accessing FGSMS services from a web site/application
    /// AND
    /// 2) accessing FGSMS services using CAC/PKI logins
    /// AND
    /// 3) a PKI certificate is provided
    /// AND 
    /// 4) The FGSMS Services war file is configured to allow user impersonation from the PKI certificate
    /// 
    /// default is false.
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    public bool useDoubleHopAuthWithPKI = false;
    /// <summary>
    /// If user impersonation is enabled, this field will be sent to the server via an HTTP header

    /// </summary>
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    /// @seealso useDoubleHopAuthWithPKI
    public string actual_username = String.Empty;
    protected override System.Net.WebRequest GetWebRequest(Uri uri)
    {
        HttpWebRequest r = (HttpWebRequest)base.GetWebRequest(uri);
        if (useDoubleHopAuthWithPKI && !String.IsNullOrEmpty(actual_username) && this.ClientCertificates.Count > 0)
        {
            r.Headers.Add("FGSMS.authorization", actual_username);
        }
        return r;
        //return base.GetWebRequest(uri);
    }
}


public partial class dataAccessService : System.Web.Services.Protocols.SoapHttpClientProtocol
{
    /// <summary>
    /// Use this field to enable user impersonation. 
    /// </summary>
    /// This should only be used when 
    /// 1) accessing FGSMS services from a web site/application
    /// AND
    /// 2) accessing FGSMS services using CAC/PKI logins
    /// AND
    /// 3) a PKI certificate is provided
    /// AND 
    /// 4) The FGSMS Services war file is configured to allow user impersonation from the PKI certificate
    /// 
    /// default is false.
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    public bool useDoubleHopAuthWithPKI = false;
    /// <summary>
    /// If user impersonation is enabled, this field will be sent to the server via an HTTP header

    /// </summary>
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    /// @seealso useDoubleHopAuthWithPKI
    public string actual_username = String.Empty;
    protected override System.Net.WebRequest GetWebRequest(Uri uri)
    {
        HttpWebRequest r = (HttpWebRequest)base.GetWebRequest(uri);
        if (useDoubleHopAuthWithPKI && !String.IsNullOrEmpty(actual_username) && this.ClientCertificates.Count > 0)
        {
            r.Headers.Add("FGSMS.authorization", actual_username);
        }
        return r;
        //return base.GetWebRequest(uri);
    }
}




public partial class reportingService : System.Web.Services.Protocols.SoapHttpClientProtocol
{
    /// <summary>
    /// Use this field to enable user impersonation. 
    /// </summary>
    /// This should only be used when 
    /// 1) accessing FGSMS services from a web site/application
    /// AND
    /// 2) accessing FGSMS services using CAC/PKI logins
    /// AND
    /// 3) a PKI certificate is provided
    /// AND 
    /// 4) The FGSMS Services war file is configured to allow user impersonation from the PKI certificate
    /// 
    /// default is false.
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    public bool useDoubleHopAuthWithPKI = false;
    /// <summary>
    /// If user impersonation is enabled, this field will be sent to the server via an HTTP header

    /// </summary>
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    /// @seealso useDoubleHopAuthWithPKI
    public string actual_username = String.Empty;
    protected override System.Net.WebRequest GetWebRequest(Uri uri)
    {
        HttpWebRequest r = (HttpWebRequest)base.GetWebRequest(uri);
        if (useDoubleHopAuthWithPKI && !String.IsNullOrEmpty(actual_username) && this.ClientCertificates.Count > 0)
        {
            r.Headers.Add("FGSMS.authorization", actual_username);
        }
        return r;
        //return base.GetWebRequest(uri);
    }
}


public partial class statusServiceBinding : System.Web.Services.Protocols.SoapHttpClientProtocol
{
    /// <summary>
    /// Use this field to enable user impersonation. 
    /// </summary>
    /// This should only be used when 
    /// 1) accessing FGSMS services from a web site/application
    /// AND
    /// 2) accessing FGSMS services using CAC/PKI logins
    /// AND
    /// 3) a PKI certificate is provided
    /// AND 
    /// 4) The FGSMS Services war file is configured to allow user impersonation from the PKI certificate
    /// 
    /// default is false.
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    public bool useDoubleHopAuthWithPKI = false;
    /// <summary>
    /// If user impersonation is enabled, this field will be sent to the server via an HTTP header

    /// </summary>
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    /// @seealso useDoubleHopAuthWithPKI
    public string actual_username = String.Empty;
    protected override System.Net.WebRequest GetWebRequest(Uri uri)
    {
        HttpWebRequest r = (HttpWebRequest)base.GetWebRequest(uri);
        if (useDoubleHopAuthWithPKI && !String.IsNullOrEmpty(actual_username) && this.ClientCertificates.Count > 0)
        {
            r.Headers.Add("FGSMS.authorization", actual_username);
        }
        return r;
        //return base.GetWebRequest(uri);
    }
}

/// <summary>
/// ARS
/// </summary>

public partial class reportingService1 : System.Web.Services.Protocols.SoapHttpClientProtocol
{
    /// <summary>
    /// Use this field to enable user impersonation. 
    /// </summary>
    /// This should only be used when 
    /// 1) accessing FGSMS services from a web site/application
    /// AND
    /// 2) accessing FGSMS services using CAC/PKI logins
    /// AND
    /// 3) a PKI certificate is provided
    /// AND 
    /// 4) The FGSMS Services war file is configured to allow user impersonation from the PKI certificate
    /// 
    /// default is false.
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    public bool useDoubleHopAuthWithPKI = false;
    /// <summary>
    /// If user impersonation is enabled, this field will be sent to the server via an HTTP header

    /// </summary>
    /// NOTE, this will only work correctly if the FGSMS server has the selected PKI certificate configured as trusted for impersonation.
    /// If the PKI cert is not trusted for impersonation, the server will assume the identity of the certificate, which may cause unexpected results
    /// @seealso useDoubleHopAuthWithPKI
    public string actual_username = String.Empty;
    protected override System.Net.WebRequest GetWebRequest(Uri uri)
    {
        HttpWebRequest r = (HttpWebRequest)base.GetWebRequest(uri);
        if (useDoubleHopAuthWithPKI && !String.IsNullOrEmpty(actual_username) && this.ClientCertificates.Count > 0)
        {
            r.Headers.Add("FGSMS.authorization", actual_username);
        }
        return r;
        //return base.GetWebRequest(uri);
    }
}

