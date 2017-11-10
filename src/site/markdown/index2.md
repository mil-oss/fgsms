# Fine Grain Service Monitoring System
A centralized performance and monitoring system for web services, message brokers, ESBs, and more!
[![Build Status](https://travis-ci.org/mil-oss/fgsms.svg?branch=master)](https://travis-ci.org/mil-oss/fgsms)


## NOTICE:
You may run into build issues. Site generation current fails on JDK8 due to javadocs.

# What's the high level overview?

What is it?

It's an open source solution for monitoring the performance and availability of stuff.
What stuff? Your stuff. IT stuff, software, process, operating systems, message brokers,
and web services, especially web services. It tracks statistics and provides
alerting based on criteria you define and a mechanism that you define.

Why would I want to use it?

You have the ability to monitor commercial, closed source or open source applications.
This gives you the ability to monitor stuff at the application level and the ability
to collect data (and message payloads) in the clear. This provides an advantage
over packet captures in which you need to correlate message traffic on busy networks.
Often, these packets are encrypted. FGSMS is different and monitors data transactions
at the application level.

How does it work?

A variety of different agents collect data based on the products you have to work.
Deployments vary, but in most cases, agents are embedded with your applications,
usually with no code changes. Copy some files, change some config files and test.

Who owns the collected data?

You do. We don't want it, it's yours. That's why we went out of our way to make
sure that you have access to the data. Reports? Done. CSV exports? No problem.
SDK, check. Web services data queries? Yup. Easy to understand SQL structures? check.



# Features
* Centralized monitoring
* Group, Role, or ACL based access to everything
* 20+ agents for a variety of different systems (.NET, Java and more)
* Full data access to all collected data
* Reporting (on demand or scheduled)
* SOAP access to data (REST coming soon)
* Bootstrap based UI
* Support for poor networks and intermit connectivity
* UDDI integration
* Plugin system for a number of different functions with more being added.
* Username/password or PKI authentication
* Role/Group/ACL based authorization
* Alerting system
* Extensible Service Level Agreements rule engine
* Dependency tracking
* Database encryption


# Prereq's

## Building FGSMS
* Maven 3.0.8+
* JDK7+
* Graphviz (needed for site and javadocs)
* PostgreSQL installed (or at least available), at least version v8.4 (you can also build to a remote database via resources/test-database.properties)
* Dot Net compilation tools (msbuild, fxcop, nunit)

## Running FGSMS
* JDK7+
* PostgreSQL
* Unlimited Strength Java Crypto Extension (optional)

## Before Building (important!)
* Install and configure postgres (must at least listen on localhost tcp
* Create the databases, tables, users, using the included script

## Setup PostgreSQL
 > cd database
 > psql -U postgres -f dbcurrent.sql
 > cd ..


# Build
* Tomcat with Apache CXF:
	* > mvn clean install 
* JBoss
	* Coming soon


PS. There's lots of documentation that will be pushed soon
