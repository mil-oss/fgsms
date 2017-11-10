# Sample SLA Plugin

Here's a quick howto for deploying this (or any) custom SLA Rule or Action

Reference FGSMS.Common.jar and FGSMS.Common.Interfaces.jar

Implement the interface
	org.miloss.fgsms.plugins.sla.SLARuleInterface
OR
	org.miloss.fgsms.plugins.sla.SLAActionInterface

Build your jar file.

Stop Tomcat

Copy the jar, along with any other dependencies to the following location
    `tomcat/webapps/FGSMSServices.war/WEB-INF/lib`

Start Tomcat

Register your plugin in the database

`psql -U fgsms`
`INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('your.package.Classname', 'A title for your plugin','type')`

where `type` MUST be one of the following
 - SLA_ACTION
 - SLA_RULE
 - FEDERATION_PUBLISH

Edit the policy for a specific service by accessing the "Manage Policy" link in the web interace.
Add an SLA and select "Plugin" from the rule list or select Plugin from the Action list
Specify your implementing class name.
Add the SLA, then Save the policy

Test it out and monitor the server log for warnings or errors.
