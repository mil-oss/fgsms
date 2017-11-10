# Fine Grained Service Monitoring System
A centralized performance and monitoring system for web services, message brokers, ESBs, and more!

[![Build Status](https://travis-ci.org/mil-oss/fgsms.svg?branch=master)](https://travis-ci.org/mil-oss/fgsms)

# What's the high level overview?

## What is it?

It's an open source solution for monitoring the performance and availability of other software
including, processes, operating systems, message brokers, SOAP and REST and web services, Java 
EARs, WARs, EJBs, .NET WCF services and more. It tracks statistics and provides
alerting based on criteria you define and a mechanism that you define.

## What's the relationship with MIL-OSS

FGSMS `was` a U.S. Army research and development project that end and was released as 
free open source software. The MIL-OSS group was the most logical choice at release time.

## What's the status of this project?

Right now: under development, beta-ish and probably unstable. The backstory is that
the process for open sourcing required a number of changes that all happened at the same
time, namely:

- Pluginize as much as possible
- Switch build systems
- Switch application servers
- Switch core service SOAP stack
- Switch a number of dependencies to license compatable eqivalents. 
- Refactor all packages, namespaces, and artifacts (several times)
- Product renaming

As such, take this under consideration. It's not exactly production stable at the moment.
Some of the documentation is probably wrong or at the least inaccurate. There's probably
security holes that need to be patched. Use at your own risk.


## Why would I want to use it?

You have the ability to monitor commercial, closed source or open source applications.
This gives you the ability to monitor stuff at the application level and the ability
to collect data (and message payloads) in the clear. This provides an advantage
over packet captures in which you need to correlate message traffic on busy networks.
Often, these packets are encrypted. FGSMS is different and monitors data transactions
at the application level.

## How does it work?

A variety of different agents collect data based on the products you have to work.
Deployments vary, but in most cases, agents are embedded with your applications,
usually with no code changes. Copy some files, change some config files and test.

## Who owns the collected data?

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
* Maven 3.2.1+
* JDK7+
* Unlimited Strength Java Crypto Extension
* PostgreSQL installed (or at least available), at least version v8.4 (you can also build to a remote database via resources/test-database.properties)
* Optional - Graphviz (needed for site and javadocs)
* Optional - Dot Net compilation tools (msbuild, fxcop, nunit)

## Running FGSMS
* JDK7+
* PostgreSQL
* Unlimited Strength Java Crypto Extension

## Quick steps to build FGSMS

### Before Building (important!)
* Install and configure postgres (must at least listen on localhost tcp)
* Install Java JDK (7 or 8 will do)
* Install Java Cryptographic Extensions
* Create the databases, tables, users, using the included script
	* Setup PostgreSQL
	 > cd database
	 > psql -U postgres -f dbcurrent.sql
	 > cd ..

### `mvn install`

### Start Tomcat 

Start the server, Tomcat with Apache CXF:
	* > cd fgsms-server/fgsms-tomcat/target/tomcat/apache-tomcat-7.0.75/bin
	* > catalina run

### Launch browser

To http://localhost:8888/fgsmsBootstrap

	
# Contributing

Pull requests are welcome, but before spending your time on something, create an issue
to describe what you intend to do. We'll be more than happy to give you feedback on the
feasibility and possibly even advice on how to accomplish your goal.

Bug reports are valueable and welcome.
