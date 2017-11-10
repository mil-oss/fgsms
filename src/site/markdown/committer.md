# Committers Notes

# Building FGSMS

Note: this is still being refined.

## Setup the database first: 

`psql -u postgres -f database/dbcurrent.sql`

## Building all the Java agents and server

### Standard developer build:

`mvn clean install`

### Running the integration tests

These tests are primarily to test or to simulate various conditions related to remote agents reporting in information or to test edge cases for web service configurations.

1. First build everything `mvn install`
2. Start the server 
   `cd fgsms.Tomcat/target/tomcat/apache-tomcat-7.0.73/bin`
   `./catalina.sh run`
3. Run the tests (from the source root directory)
   `mvn install -Dintegration`

   
### Building the Java components in a disconnected or isolated network (no internet)

1. On an internet connected computer, check out the fgsms code base, `git clone https://github.com/mil-oss/fgsms.git`
2. Download a copy of [Maven](http://maven.apache.org/download.cgi) if you don't have it already, then run, `mvn dependency:go-offline 
   dependency:sources dependency:resolve -Dclassifier=javadoc dependency:resolve-plugins -fn`. 
   This will download all dependencies and plugins required to build the Java components. 
   If this is not your build machine, then you can copy the contents of `~/.m2/repository` onto
   the actual build environment or on to a Nexus server or something similiar.
3. You also need to download a few other things to build. while internet connected, this is 
   automatic but unfortunately these artifacts are not published anywhere.
    - [Apache jUDDI](https://dist.apache.org/repos/dist/release/juddi/juddi/3.3.3/juddi-distro-3.3.3.zip)
    - [Apache Tomcat for windows](https://dist.apache.org/repos/dist/release/tomcat/tomcat-7/v7.0.75/bin/apache-tomcat-7.0.75-windows-x64.zip)
    - [Jboss ESB](http://download.jboss.org/jbossesb/4.12/binary/jbossesb-server-4.12.zip)
4. Once offline, you'll have to get those 3 files in on to a web server that will serve up those 
   files and then have some kind of DNS forward, such as editing your host file. 
5. Assuming you don't have SSL certificates that match those download urls and that your local web 
   server is hosting those files with just plain old http, build with `mvn install -DforceHttp`. If 
   you do have local certs that match the hostnames for apache and jboss, then the regular build should 
   be just fine with `mvn install`
   
   

## Building the .NET Components

Prerequisites:
 
 - Windows SDK for your operating system
 - [Nugget command line executable](https://dist.nuget.org/index.html)
 - .NET Framework of at least v4.0   
 - Visual Studio (community edition is generally good enough)
 - Optionally FXCop
 - Optionally Nunit

You can also run the .net build with maven by switching to the .net directory and
executing `mvn clean install`
 

## Useful tools

 - Postgres's PG Admin tool
 - Xmlspy or any program capable of validating XML, such as Notepad++ with the XML plugin
 - Apache TcpMon

# Releases

TODO

# Testing

TODO This section needs to be updated in a big way.


Building FGSMS isn't really that bad of a problem. In fact, all you really need to build everything is a Windows box (because of the .NET Code). Don't have a windows box? No problem, the .NET build is skipped automatically.

Integration testing of FGSMS is a challenge due to numerous interactions
with other servers, services, clients and agents. Some of it is automated
with the [Maven](http://maven.apache.org) build system
be automated with <a href="http://maven.apache.org/">Maven</a>, It's truely a <a href="https://en.wikipedia.org/wiki/Continuous_integration">CI</a> nightmare. It's a perfect task for something like <a href="https://jenkins-ci.org/">Jenkins</a> or <a href="https://travis-ci.org/">Travis</a>.

You'll need the following:

* Postgres 9.x and a running instance of FGSMS
* CentOS/Redhat VM (build rpm + test)
   * VM Running Apache Qpid C++ broker
 * Windows VM (build + test)
   * IIS Web Server hosting
     * ASP.NET Web service
     * ASP.NET web site that calls a ASP.NET web service
     * WCF.NET web service in IIS
     * WCF.NET web service running standalone
     * Any old web site with NTLM v1 authentication
     * Any old web site with NTLM v2 authentication
   * WCF.NET client running in IIS
   * WCF.NET client running standlone
   * Any old web site with DIGEST authentication
   * Any old web site with Basic authentication
 * The following can be ran anywhere, so long as the test instance of FGSMS can reach it
   * ~~Apache Qpid Java broker~~  incorporated into the Maven build
   * Web server hosting an Apache Axis 1.4 service
   * Web server hosting an Apache Axis 2.x service
   * Web server hosting an Apache CXF 2.x service
   * Web server hosting an Apache CXF 3.x service
   * Web server hosting a fgsms's WS Notifcation broker service
   * SMTP Mail server
   * ~~HornetQ server~~ incorporated into the Maven build
   * Syslog server

* Federation target tests
 * ~~Apache jUDDI server 3.x~~  incorporated into the Maven build
 * SNMP plugin (future)
 * Plus an potential future plugins

1. Delete your local maven repo, located at ~/.m2/repository
2. mvn clean install -Pall
3. Start the server
