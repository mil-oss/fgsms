﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.5485
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Web.Services;
using System.Web.Services.Protocols;
using System.Xml.Serialization;

// 
// This source code was auto-generated by wsdl, Version=2.0.50727.3038.
// 


/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
[System.Web.Services.WebServiceBindingAttribute(Name="BasicHttpBinding_IService1", Namespace="http://tempuri.org/")]
public partial class Service1 : System.Web.Services.Protocols.SoapHttpClientProtocol {
    
    private System.Threading.SendOrPostCallback WorkingGetDataOperationCompleted;
    
    private System.Threading.SendOrPostCallback CallDependantServiceOperationCompleted;
    
    private System.Threading.SendOrPostCallback CallWCFDependantServiceOperationCompleted;
    
    private System.Threading.SendOrPostCallback FailingGetDataOperationCompleted;
    
    private System.Threading.SendOrPostCallback LongRunningGetDataOperationCompleted;
    
    private System.Threading.SendOrPostCallback RandomWorkingMethodOperationCompleted;
    
    private System.Threading.SendOrPostCallback OneWayMethodOperationCompleted;
    
    /// <remarks/>
    public Service1() {
        this.Url = "http://FGSMSdev1/HelloWorldESMTester/HelloWorldESMWCF.svc";
    }
    
    /// <remarks/>
    public event WorkingGetDataCompletedEventHandler WorkingGetDataCompleted;
    
    /// <remarks/>
    public event CallDependantServiceCompletedEventHandler CallDependantServiceCompleted;
    
    /// <remarks/>
    public event CallWCFDependantServiceCompletedEventHandler CallWCFDependantServiceCompleted;
    
    /// <remarks/>
    public event FailingGetDataCompletedEventHandler FailingGetDataCompleted;
    
    /// <remarks/>
    public event LongRunningGetDataCompletedEventHandler LongRunningGetDataCompleted;
    
    /// <remarks/>
    public event RandomWorkingMethodCompletedEventHandler RandomWorkingMethodCompleted;
    
    /// <remarks/>
    public event OneWayMethodCompletedEventHandler OneWayMethodCompleted;
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://tempuri.org/IService1/WorkingGetData", RequestNamespace="http://tempuri.org/", ResponseNamespace="http://tempuri.org/", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
    [return: System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
    public string WorkingGetData(int value, [System.Xml.Serialization.XmlIgnoreAttribute()] bool valueSpecified) {
        object[] results = this.Invoke("WorkingGetData", new object[] {
                    value,
                    valueSpecified});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginWorkingGetData(int value, bool valueSpecified, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("WorkingGetData", new object[] {
                    value,
                    valueSpecified}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndWorkingGetData(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void WorkingGetDataAsync(int value, bool valueSpecified) {
        this.WorkingGetDataAsync(value, valueSpecified, null);
    }
    
    /// <remarks/>
    public void WorkingGetDataAsync(int value, bool valueSpecified, object userState) {
        if ((this.WorkingGetDataOperationCompleted == null)) {
            this.WorkingGetDataOperationCompleted = new System.Threading.SendOrPostCallback(this.OnWorkingGetDataOperationCompleted);
        }
        this.InvokeAsync("WorkingGetData", new object[] {
                    value,
                    valueSpecified}, this.WorkingGetDataOperationCompleted, userState);
    }
    
    private void OnWorkingGetDataOperationCompleted(object arg) {
        if ((this.WorkingGetDataCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.WorkingGetDataCompleted(this, new WorkingGetDataCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://tempuri.org/IService1/CallDependantService", RequestNamespace="http://tempuri.org/", ResponseNamespace="http://tempuri.org/", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
    [return: System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
    public string CallDependantService(int value, [System.Xml.Serialization.XmlIgnoreAttribute()] bool valueSpecified) {
        object[] results = this.Invoke("CallDependantService", new object[] {
                    value,
                    valueSpecified});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginCallDependantService(int value, bool valueSpecified, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("CallDependantService", new object[] {
                    value,
                    valueSpecified}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndCallDependantService(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void CallDependantServiceAsync(int value, bool valueSpecified) {
        this.CallDependantServiceAsync(value, valueSpecified, null);
    }
    
    /// <remarks/>
    public void CallDependantServiceAsync(int value, bool valueSpecified, object userState) {
        if ((this.CallDependantServiceOperationCompleted == null)) {
            this.CallDependantServiceOperationCompleted = new System.Threading.SendOrPostCallback(this.OnCallDependantServiceOperationCompleted);
        }
        this.InvokeAsync("CallDependantService", new object[] {
                    value,
                    valueSpecified}, this.CallDependantServiceOperationCompleted, userState);
    }
    
    private void OnCallDependantServiceOperationCompleted(object arg) {
        if ((this.CallDependantServiceCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.CallDependantServiceCompleted(this, new CallDependantServiceCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://tempuri.org/IService1/CallWCFDependantService", RequestNamespace="http://tempuri.org/", ResponseNamespace="http://tempuri.org/", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
    [return: System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
    public string CallWCFDependantService(int value, [System.Xml.Serialization.XmlIgnoreAttribute()] bool valueSpecified) {
        object[] results = this.Invoke("CallWCFDependantService", new object[] {
                    value,
                    valueSpecified});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginCallWCFDependantService(int value, bool valueSpecified, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("CallWCFDependantService", new object[] {
                    value,
                    valueSpecified}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndCallWCFDependantService(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void CallWCFDependantServiceAsync(int value, bool valueSpecified) {
        this.CallWCFDependantServiceAsync(value, valueSpecified, null);
    }
    
    /// <remarks/>
    public void CallWCFDependantServiceAsync(int value, bool valueSpecified, object userState) {
        if ((this.CallWCFDependantServiceOperationCompleted == null)) {
            this.CallWCFDependantServiceOperationCompleted = new System.Threading.SendOrPostCallback(this.OnCallWCFDependantServiceOperationCompleted);
        }
        this.InvokeAsync("CallWCFDependantService", new object[] {
                    value,
                    valueSpecified}, this.CallWCFDependantServiceOperationCompleted, userState);
    }
    
    private void OnCallWCFDependantServiceOperationCompleted(object arg) {
        if ((this.CallWCFDependantServiceCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.CallWCFDependantServiceCompleted(this, new CallWCFDependantServiceCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://tempuri.org/IService1/FailingGetData", RequestNamespace="http://tempuri.org/", ResponseNamespace="http://tempuri.org/", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
    [return: System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
    public string FailingGetData(int value, [System.Xml.Serialization.XmlIgnoreAttribute()] bool valueSpecified) {
        object[] results = this.Invoke("FailingGetData", new object[] {
                    value,
                    valueSpecified});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginFailingGetData(int value, bool valueSpecified, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("FailingGetData", new object[] {
                    value,
                    valueSpecified}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndFailingGetData(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void FailingGetDataAsync(int value, bool valueSpecified) {
        this.FailingGetDataAsync(value, valueSpecified, null);
    }
    
    /// <remarks/>
    public void FailingGetDataAsync(int value, bool valueSpecified, object userState) {
        if ((this.FailingGetDataOperationCompleted == null)) {
            this.FailingGetDataOperationCompleted = new System.Threading.SendOrPostCallback(this.OnFailingGetDataOperationCompleted);
        }
        this.InvokeAsync("FailingGetData", new object[] {
                    value,
                    valueSpecified}, this.FailingGetDataOperationCompleted, userState);
    }
    
    private void OnFailingGetDataOperationCompleted(object arg) {
        if ((this.FailingGetDataCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.FailingGetDataCompleted(this, new FailingGetDataCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://tempuri.org/IService1/LongRunningGetData", RequestNamespace="http://tempuri.org/", ResponseNamespace="http://tempuri.org/", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
    [return: System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
    public string LongRunningGetData(int value, [System.Xml.Serialization.XmlIgnoreAttribute()] bool valueSpecified) {
        object[] results = this.Invoke("LongRunningGetData", new object[] {
                    value,
                    valueSpecified});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginLongRunningGetData(int value, bool valueSpecified, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("LongRunningGetData", new object[] {
                    value,
                    valueSpecified}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndLongRunningGetData(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void LongRunningGetDataAsync(int value, bool valueSpecified) {
        this.LongRunningGetDataAsync(value, valueSpecified, null);
    }
    
    /// <remarks/>
    public void LongRunningGetDataAsync(int value, bool valueSpecified, object userState) {
        if ((this.LongRunningGetDataOperationCompleted == null)) {
            this.LongRunningGetDataOperationCompleted = new System.Threading.SendOrPostCallback(this.OnLongRunningGetDataOperationCompleted);
        }
        this.InvokeAsync("LongRunningGetData", new object[] {
                    value,
                    valueSpecified}, this.LongRunningGetDataOperationCompleted, userState);
    }
    
    private void OnLongRunningGetDataOperationCompleted(object arg) {
        if ((this.LongRunningGetDataCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.LongRunningGetDataCompleted(this, new LongRunningGetDataCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://tempuri.org/IService1/RandomWorkingMethod", RequestNamespace="http://tempuri.org/", ResponseNamespace="http://tempuri.org/", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
    [return: System.Xml.Serialization.XmlElementAttribute(IsNullable=true)]
    public string RandomWorkingMethod(int value, [System.Xml.Serialization.XmlIgnoreAttribute()] bool valueSpecified) {
        object[] results = this.Invoke("RandomWorkingMethod", new object[] {
                    value,
                    valueSpecified});
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginRandomWorkingMethod(int value, bool valueSpecified, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("RandomWorkingMethod", new object[] {
                    value,
                    valueSpecified}, callback, asyncState);
    }
    
    /// <remarks/>
    public string EndRandomWorkingMethod(System.IAsyncResult asyncResult) {
        object[] results = this.EndInvoke(asyncResult);
        return ((string)(results[0]));
    }
    
    /// <remarks/>
    public void RandomWorkingMethodAsync(int value, bool valueSpecified) {
        this.RandomWorkingMethodAsync(value, valueSpecified, null);
    }
    
    /// <remarks/>
    public void RandomWorkingMethodAsync(int value, bool valueSpecified, object userState) {
        if ((this.RandomWorkingMethodOperationCompleted == null)) {
            this.RandomWorkingMethodOperationCompleted = new System.Threading.SendOrPostCallback(this.OnRandomWorkingMethodOperationCompleted);
        }
        this.InvokeAsync("RandomWorkingMethod", new object[] {
                    value,
                    valueSpecified}, this.RandomWorkingMethodOperationCompleted, userState);
    }
    
    private void OnRandomWorkingMethodOperationCompleted(object arg) {
        if ((this.RandomWorkingMethodCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.RandomWorkingMethodCompleted(this, new RandomWorkingMethodCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://tempuri.org/IService1/OneWayMethod", RequestElementName="OneWayMethodMethod", RequestNamespace="http://tempuri.org/", OneWay=true, Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
    public void OneWayMethod(int value, [System.Xml.Serialization.XmlIgnoreAttribute()] bool valueSpecified) {
        this.Invoke("OneWayMethod", new object[] {
                    value,
                    valueSpecified});
    }
    
    /// <remarks/>
    public System.IAsyncResult BeginOneWayMethod(int value, bool valueSpecified, System.AsyncCallback callback, object asyncState) {
        return this.BeginInvoke("OneWayMethod", new object[] {
                    value,
                    valueSpecified}, callback, asyncState);
    }
    
    /// <remarks/>
    public void EndOneWayMethod(System.IAsyncResult asyncResult) {
        this.EndInvoke(asyncResult);
    }
    
    /// <remarks/>
    public void OneWayMethodAsync(int value, bool valueSpecified) {
        this.OneWayMethodAsync(value, valueSpecified, null);
    }
    
    /// <remarks/>
    public void OneWayMethodAsync(int value, bool valueSpecified, object userState) {
        if ((this.OneWayMethodOperationCompleted == null)) {
            this.OneWayMethodOperationCompleted = new System.Threading.SendOrPostCallback(this.OnOneWayMethodOperationCompleted);
        }
        this.InvokeAsync("OneWayMethod", new object[] {
                    value,
                    valueSpecified}, this.OneWayMethodOperationCompleted, userState);
    }
    
    private void OnOneWayMethodOperationCompleted(object arg) {
        if ((this.OneWayMethodCompleted != null)) {
            System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
            this.OneWayMethodCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
        }
    }
    
    /// <remarks/>
    public new void CancelAsync(object userState) {
        base.CancelAsync(userState);
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
public delegate void WorkingGetDataCompletedEventHandler(object sender, WorkingGetDataCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class WorkingGetDataCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal WorkingGetDataCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
public delegate void CallDependantServiceCompletedEventHandler(object sender, CallDependantServiceCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class CallDependantServiceCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal CallDependantServiceCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
public delegate void CallWCFDependantServiceCompletedEventHandler(object sender, CallWCFDependantServiceCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class CallWCFDependantServiceCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal CallWCFDependantServiceCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
public delegate void FailingGetDataCompletedEventHandler(object sender, FailingGetDataCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class FailingGetDataCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal FailingGetDataCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
public delegate void LongRunningGetDataCompletedEventHandler(object sender, LongRunningGetDataCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class LongRunningGetDataCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal LongRunningGetDataCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
public delegate void RandomWorkingMethodCompletedEventHandler(object sender, RandomWorkingMethodCompletedEventArgs e);

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
public partial class RandomWorkingMethodCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
    
    private object[] results;
    
    internal RandomWorkingMethodCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) : 
            base(exception, cancelled, userState) {
        this.results = results;
    }
    
    /// <remarks/>
    public string Result {
        get {
            this.RaiseExceptionIfNecessary();
            return ((string)(this.results[0]));
        }
    }
}

/// <remarks/>
[System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "2.0.50727.3038")]
public delegate void OneWayMethodCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);
