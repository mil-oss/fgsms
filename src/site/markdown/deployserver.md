# Deploying FGSMS's Server

This article will discuss how to deploy the FGSMS core services into an application container, such as Jboss, Tomcat, etc.
Make sure you read the [Deployment planning](deployment-planning.html) article. 

## Database setup

Follow the same steps that are in the [Quick Start](quickstart.html) guide.

## Deploying to Tomcat 7

Easiest path since we test and integrate on Tomcat 7. 

1. Stop Tomcat if it's running
2. From the FGSMS distribution, copy fgsmsServices.war and fgsmsBootstrap.war into the `tomcat/webapps` folder.
3. Unzip them into their corresponding folders, you should have a `tomcat/webapps/fgsmsServices/` and a `tomcat/webapps/fgsmsBootstrap/` folders, (the war archives can be discarded).
4. Edit the `tomcat/webapps/fgsmsServices/WEB-INF/context.xml` file. This is where you configure your database credentials.
5. Edit the `tomcat/webapps/fgsmsServices/WEB-INF/web.xml` file. This is where you configure security settings. See especially the setup for requiring SSL and examples for username/password or PKI authentication.
6. Edit the `tomcat/webapps/fgsmsBootstrap/WEB-INF/config.properties`. This is how the user interface connects to the `fgsmsServices.war` deployment. Specifically, make sure the URL's match what you need, certificate information etc. 
7. If PKI is your desired authentication mechanism, also edit `tomcat/webapps/fgsmsBootstrap/WEB-INF/web.xml` to enable CLIENT-CERT

## Deploying to Jboss/Wildfly

Not tested, but the steps for Tomcat should work.

## Deploying the Sharepoint Web parts

FGSMS includes a SharePoint web part (a user interface component) which can be used to present availability indicators on a SharePoint based web site.

The web part provides access to the status service and status information of services that are running. Useful for providing a dash board of service availability.

NOTICE: the FGSMS webpart was designed for SharePoint 2007 and is dated. No idea if it will work on a newer build.

### Building

See notes on building the .NET components

### Installation

1. Add the solution
```
Stadm –o addsolution FGSMWebparts.wsp
```
### Enable it for the farm

2. Login to Central Administrator
3. Solution Management
4. Deploy to the web applications of choice
5. Login to the chosen web application. Site Settings > Site Features
6. Active the feature “FGSMS Web Parts”

The available web parts will now be available for addition to any web part page. Each instance needs to be configured with URLs and credentials



## Working with password encryption

`java -jar fgsms.Common.jar -help`

