<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Permissions</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        FGSMS's permission structure allows for fine grained access control to all data contained within. The permission structure is a Role and Access Control List hybrid.
        Roles can be provided by either an external role provider (such as Active Directory or LDAP group membership) or internally within FGSMS. The following roles exist globally, meaning the apply to everything in FGSMS.
        Role are processed in the following order:
        <ol>
            <li>Externally Provided Global Roles</li>
            <li>Internally Provided Global Roles</li>
            <li>Per Service Access Control Lists</li>
            <li>Everyone</li>
        </ol>

        <h2>Global Roles</h2>
        Externally Provided Global Roles
        <ul>
            <li>fgsms_GLOBAL_ADMINISTRATOR - The user can read, make changes to the service policy, view message transaction logs and delegate and change permissions for everything and delegate permissions.</li>
            <li>fgsms_GLOBAL_AGENT - This user account can be used as an agent. It can create service policies if they don't exist already and add data to the system. </li>
            <li>fgsms_GLOBAL_AUDITOR - The user can read, make changes to the service policy for every service, view message transaction logs and view FGSMS's audit log.</li>
            <li>fgsms_GLOBAL_WRITE - The user can read and make changes to the service policy for every service.</li>
            <li>Ofgsms_GLOBAL_READ - The user can read permissions and performance statistics for every service.</li>
        </ul>

        <h2>Internally Provided Global Roles</h2>
        <ul>
            <li>admin - The user can read, make changes to the service policy, view message transaction logs and delegate and change permissions for everything and delegate permissions.</li>
            <li>agent - This user account can be used as an agent. It can create service policies if they don't exist already and add data to the system.</li>
            <li>audit -  The user can read, make changes to the service policy for every service, view message transaction logs and view FGSMS's audit log.</li>
        </ul>

        <h2>Per Service ACLs</h2>
        The following table defines what each permission level means as far as access control within FGSMS.<br>
        <table border="1">
            <tr><th>Level</th><th>Rights</th></tr>
            <tr><td>Read</td><td>The user can read permissions and performance statistics for a service.</td></tr>
            <tr><td>Write</td><td>The user can read and make changes to the service policy for a service.</td></tr>
            <tr><td>Audit</td><td>The user can read, make changes to the service policy and view message transaction logs and view FGSMS's audit log</td></tr>
            <tr><td>Administer</td><td>The user can read, make changes to the service policy, view message transaction logs and delegate and change permissions for this service and delegate permissions.</td></tr>
        </table>

        <h2>Granting everyone access</h2>
        The username "everyone" is a special username and will enable all authenticated users the chosen ACL privilege, but only for read, write, and audit. 
        
        <h2>Special Cases</h2>
        In certain situations, specific functions are restricted to administrators that could potentially cause harm to systems. These special cases include the following:
        <ul>
            <li>Using the SLA Action, Run a Script.</li>
            <li>Using the SLA Action, Restart.</li>
        </ul>
    </div><!--/span-->
</div><!--/row-->
