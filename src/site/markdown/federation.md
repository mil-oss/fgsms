# Federation targets

On a per service basis, FGSMS supports the ability to add a `Federation Target` to
a service policy. A `federation target` is simply a pluggable interface for periodically
publishing metrics (that you choose) on a service.

## What's out of the box?

As of right now, only the available component is the UDDI publisher.

## How often does it publish?

The publication frequency is administrator defined via `General Settings`. The default
is every 5 minutes. 

### The UDDI Publisher 

FGSMS provides a [Universal Description Discovery & Integration](http://uddi.org/pubs/uddi_v3.htm) publisher.
When the publish job fires, it will modify the content of an already published 
[UDDI Binding Template](http://www.uddi.org/pubs/uddi_v3.htm#_Toc85908020) by appending
[tModel Instance Infos](http://www.uddi.org/pubs/uddi_v3.htm#_Toc515847044) that represent 
the metrics over the selected period of time.

### What can it publish?

The following can be published on a per service basis for user selected time periods:

- Average response time, request size, response size
- Maximum response time, request size, response size
- Successful transactions
- Failing transactions
- MTBF
- SLA violations
- Availability - % of the time a service was available

In addition, status information can also be published.

 - Status - Publishes last known status and the time stamp.
 - Last time stamp the status changed.

<a href="images/uddi-federatiion-publish.png" target="_blank">Apache jUDDI screen shot with FGSMS data</a>


# SDK stufff

Below the section is all about how to make your own federation plugin. If you're
not a developer, your time is better spent else where

## How to make a custom Federation Target

First off, make a new Java project in your IDE of choice and reference the 
following maven artifact:

    groupId `org.mil-oss`
    artifactId `fgsms-common-interfaces`


### Implementing the interface

Make a class and have it implement `org.miloss.fgsms.plugins.federation.FederationInterface`
The rest is filling in the blanks.

### Configuration settings

There's a few ways to provide configuration options to your plugin. If you want
only administrator defined settings that apply to everything, you'll want to
check out the `DBSettingsLoader` class.

If you want users/operators to be able to have service specific settings, then
make sure you override `GetRequiredParameters`, `GetOptionalParameters`, and
`ValidateConfiguration` which helps guide users towards a successful configuration. 

### Deploying and Registering your plugin

See the [SDK](sdk.html) guide for deploying SLA agents for the guide.

