<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Email Alerting</h1>
</div>
<div class="row-fluid">
    <div class="span12">
          Configuring email is easy and just three simple steps.<br>
            <ol>
                <li>The system administrator first has to configure the ability for FGSMS to send email via Administration > E-mail settings.</li>
                <li>Each service that you which to receive email alerts from needs to have a Service Level Alert configured with an Action defined for sending email alerts. By default, all services have a Change In Status alert defined with an email action defined.</li>
                <li>For each SLA (with an email action defined) that you are interested in, you must subscribe for email alerts. Click on My Settings, My Account to verify your email address. Then click on My Settings > Alerting. All available SLA's with email actions will be listed. To subscribe to a particular alert, check the box, then click Submit.</li>
            </ol>
        
    </div><!--/span-->
</div><!--/row-->
