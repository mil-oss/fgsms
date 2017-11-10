# Apache Axis 1.4 Example WAR (monitored by FGSMS)

This package deserves some explaination.

First off, Apache Axis 1.4 is a ancient technology. If you're still using it, you really 
should consider upgrading.


## Reproducing this setup

The sources in the project are right from Apache Axis 1.4 source code. Steps to reproduce...

1. Download and install Java 1.4 JDK (1.5 won't do)
2. Download and install Apache Ant 1.8.3
3. `svn co http://svn.apache.org/repos/asf/axis/axis1/java/tags/1.4/`
4. Read the docs/build-axis.html and download as many of the dependencies as you can find. I found most in maven central but not all. Dump them in the lib folder.
5. `ant compile`
6. Now the slightly painful part. Go into the `samples` folder. Everywhere you find a `build.xml` file, run `ant`.
7. Start a new war project and copy the directory `samples` in the `src/main/java` folder
8. Copy the content from `build/work/samples` into `src/main/java/samples`
9. Copy the content from `webapps/axis/` into 'src/main/webapps`
10. Delete all *.class files in `src/main/webapps` if there are any
11. Move `src/main/webapps/WEB-INF/classes` (should only be some properties files) into `src/main/resources`
12. Lastly, if you want all of the sample services to deploy, you'll have to make an xml file in `src/main/webapp/WEB-INF/` called `server-config.wsdd`. Then merge the contents
of all `src/main/java/samples/**/deploy.wsdd` into it.
13. Finally, there was a required handler missing that prevents wsdl generation which is below

	<transport name="http">
		<requestFlow>
			<handler type="java:org.apache.axis.handlers.http.URLMapper"/>
		</requestFlow>
	</transport>

Dependencies on the new war project

	<dependency>
		<groupId>org.apache.axis</groupId>
		<artifactId>axis</artifactId>
	</dependency>
	<dependency>
		<groupId>junit</groupId>
		<artifactId>junit</artifactId>
	</dependency>
	<dependency>
		<groupId>org.apache.axis</groupId>
		<artifactId>axis-jaxrpc</artifactId>
	</dependency>
	<dependency>
		<groupId>org.apache.axis</groupId>
		<artifactId>axis-saaj</artifactId>
		<version>1.4</version>
	</dependency>
	<dependency>
		<groupId>commons-logging</groupId>
		<artifactId>commons-logging</artifactId>
		<version>1.2</version>
	</dependency>
	<dependency>
		<groupId>commons-discovery</groupId>
		<artifactId>commons-discovery</artifactId>
		<version>0.5</version>
	</dependency>
	<dependency>
		<groupId>axis</groupId>
		<artifactId>axis-wsdl4j</artifactId>
		<version>1.5.1</version>
	</dependency>
	<dependency>
		<groupId>javax.mail</groupId>
		<artifactId>mail</artifactId>
	</dependency>
	
	
## Was it worth it?

Ehh it was worth getting a automated test rig setup for the Axis handler. I felt more like a digital archaeologist, sifting through the remains of yesteryear, but yes.

