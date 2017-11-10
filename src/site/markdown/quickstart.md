# Quick Start

1. Install Java JDK 7 or 8
2. Install Java Cryptographic Extension (JCE) if you haven't already
3. Set an environment variable for JAVA_HOME to be where you installed the JDK to
4. Download the FGSMS Distribution (when it becomes available)
5. Unzip
6. Setup postgres
  1. Setup databases
    * Windows -  `psql -U postgres -f database/dbcurrent.sql`
    * Linux/Mac/Ubuntu - 
         1. `sudo su`
         2. `su postgres`
         3. `psql -U postgres -f database/dbcurrent.sql`
7. Start tomcat 
  * Windows - `tomcat/bin/catalina.bat run`
  * Linux/Mac/Ubuntu - `tomcat/bin/catalina.sh run`
8. Browser to http://localhost:8888/fgsmsBootstrap
	* Username = fgsmsAdmin
	* Password = da_Password!
