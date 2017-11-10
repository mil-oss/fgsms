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
using System.Text;
using System.Web.Services.Protocols;
using System.ServiceModel.Channels;
using System.Xml.Serialization;
using System.Xml;

namespace org.miloss.fgsms.agent
{
    [SoapType(IncludeInSchema = false, Namespace = "org.miloss.FGSMS.headers")]
    [XmlType(IncludeInSchema = false, Namespace = "org.miloss.FGSMS.headers")]
    // [XmlElement(ElementName="FGSMS.relatedmessage", Namespace= "org.miloss.FGSMS.headers")]
    //[System.Xml.Serialization.XmlTypeAttribute(Namespace = "org.miloss.FGSMS.headers")]
    [System.Xml.Serialization.XmlRootAttribute("FGSMS.relatedmessage", Namespace = "org.miloss.FGSMS.headers", IsNullable = false)]
    public class FGSMSSoapHeaderRelatedMessageASPNET : System.Web.Services.Protocols.SoapHeader
    {
        [System.Xml.Serialization.XmlText()]
        public string id;

        public FGSMSSoapHeaderRelatedMessageASPNET() { }
      

        public FGSMSSoapHeaderRelatedMessageASPNET(string id)
        {
            this.id = id;
        }

        
        //  [XmlText(Type=typeof(string))]
        public override string ToString()
        {
            return id;
        }
        /*
        public string EncodedMustUnderstand
        {
            get { return "0"; }
            set { return; }
        }

        
        public string EncodedMustUnderstand12
        {
            get { return "0"; }
            set { return; }
        }

        public string EncodedRelay
        {
            get { return "0"; }
            set { return; }
        }

        public bool MustUnderstand
        {
            get { return false; }
            set { return; }
        }


        public bool Relay
        {
            get { return false; }
            set { return; }
        }

        public string Role
        {
            get { return String.Empty; }
            set { return; }
        }*/
    }

    [SoapType(IncludeInSchema = false, Namespace = "org.miloss.FGSMS.headers")]
    [XmlType(IncludeInSchema = false, Namespace = "org.miloss.FGSMS.headers")]
    // [XmlElement(ElementName = "FGSMS.threadid", Namespace = "org.miloss.FGSMS.headers")]
    //   [System.Xml.Serialization.XmlTypeAttribute(Namespace = "org.miloss.FGSMS.headers")]
    [System.Xml.Serialization.XmlRootAttribute("FGSMS.threadid", Namespace = "org.miloss.FGSMS.headers", IsNullable = false)]
    public class FGSMSSoapHeaderTransactionThreadIdASPNET : SoapHeader
    {
        [System.Xml.Serialization.XmlText()]
        public string id;

        public FGSMSSoapHeaderTransactionThreadIdASPNET() { }

        public FGSMSSoapHeaderTransactionThreadIdASPNET(string id)
        {
            this.id = id;
        }
        //  [XmlText(Type = typeof(string))]


        public override string ToString()
        {
            return id;
        }
        /*
        public string EncodedMustUnderstand
        {
            get { return "0"; }
            set { return; }
        }

        
        public string EncodedMustUnderstand12
        {
            get { return "0"; }
            set { return; }
        }

        public string EncodedRelay
        {
            get { return "0"; }
            set { return; }
        }

        public bool MustUnderstand
        {
            get { return false; }
            set { return; }
        }


        public bool Relay
        {
            get { return false; }
            set { return; }
        }

        public string Role
        {
            get { return String.Empty; }
            set { return; }
        }*/

    }

    [Serializable()]
    //   [System.Runtime.Serialization.DataMember(Name="FGSMS.relatedmessage")]
    public class FGSMSSoapHeaderRelatedMessageIdWCF : MessageHeader
    {
        public FGSMSSoapHeaderRelatedMessageIdWCF() { }
        public FGSMSSoapHeaderRelatedMessageIdWCF(string msgid)
        {
            this.id = msgid;
        }


        private string id = String.Empty;

        public override bool MustUnderstand
        {
            get
            {
                return false;
            }
        }
        public static FGSMSSoapHeaderRelatedMessageIdWCF ReadHeader(XmlDictionaryReader reader)
        {
            string t = reader.ReadString();
            if (!String.IsNullOrEmpty(t))
            {
                FGSMSSoapHeaderRelatedMessageIdWCF item = new FGSMSSoapHeaderRelatedMessageIdWCF();
                item.id = t;
                return item;
            }
            return null;
        }
        protected override void OnWriteHeaderContents(System.Xml.XmlDictionaryWriter writer, MessageVersion messageVersion)
        {
            //  writer.WriteStartElement(this.Name, this.Namespace);
            writer.WriteValue(this.id);
            // writer.WriteEndElement();
        }

        public override string Name
        {
            get { return "FGSMS.relatedmessage"; }
        }

        public override string Namespace
        {
            get { return "org.miloss.FGSMS.headers"; }
        }

        public static string Name2
        {
            get { return "FGSMS.relatedmessage"; }
        }

        public static string Namespace2
        {
            get { return "org.miloss.FGSMS.headers"; }
        }
        public string Id
        {
            get { return id; }
        }
    }

    public class FGSMSSoapHeaderTransactionThreadIdWCF : MessageHeader
    {
        public FGSMSSoapHeaderTransactionThreadIdWCF() { }
        public FGSMSSoapHeaderTransactionThreadIdWCF(string msgid)
        {
            this.id = msgid;
        }
        public static FGSMSSoapHeaderTransactionThreadIdWCF ReadHeader(XmlDictionaryReader reader)
        {
            string t = reader.ReadString();
            if (!String.IsNullOrEmpty(t))
            {
                FGSMSSoapHeaderTransactionThreadIdWCF item = new FGSMSSoapHeaderTransactionThreadIdWCF();
                item.id = t;
                return item;
            }
            return null;
        }


        private string id = String.Empty;
        public string Id
        {
            get { return id; }
        }
        public override bool MustUnderstand
        {
            get
            {
                return false;
            }


        }
        protected override void OnWriteHeaderContents(System.Xml.XmlDictionaryWriter writer, MessageVersion messageVersion)
        {
            //writer.WriteStartElement(this.Name, this.Namespace);
            writer.WriteValue(this.id);
            //writer.WriteEndElement();
        }

        public static string Name2
        {
            get { return "FGSMS.threadid"; }
        }

        public static string Namespace2
        {
            get { return "org.miloss.FGSMS.headers"; }
        }

        public override string Name
        {
            get { return "FGSMS.threadid"; }
        }

        public override string Namespace
        {
            get { return "org.miloss.FGSMS.headers"; }
        }
    }
}
