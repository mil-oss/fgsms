# Reporting and Exporting Data

FGSMS provides a number of ways to get data out of system. It's your data
after all.

## Built in Reporting

FGSMS provides a wide array of stylized HTML reports out of the box that
you can use to generate reports. These reports are can be generated on
demand or in a periodic basic via the automated report generator service.
Both of these functions can be accessed via the web user interface.

To generate a report, click on `Data` then `Reporting`

!['Reporting'](images/reporting-html.png)

### Report Types

The reporting system is plugin based.
[Click Here for the current list of reporting tasks](reportTypes.html)


## Exporting Data via CSV

Data can be exported via the web user interface via the `Data` link on
the navigation menu. This allows you to export and filter information
based on what you need. Output is Excel/Spreadsheet friendly CSV formats
that loosely matches the database schema.

!['Reporting'](images/reporting-csv.png)

## Exporting Data via Web Service calls

Check out the [SDK](sdk.html) and some the sample projects, and the 
Command Line Interface.

## Exporting Data via direct SQL access

FGSMS's database schemas were designed to be simple and easy to understand.
There are two database `Configuration` and `Performance`. The Config database
contains a minimal set of information related to service policies, machine
level information from the OS agent, SLA subscriptions, emails, permissions,
etc. The Performance database has all of the raw data that you're probably
looking for. There is a third database for Quartz jobs, which basically
enables a number of timed jobs to execute concurrently and pooled across
multiple servers. This helps enable FGSMS to scale.

Here's a general description of the tables what's in them

| Table         | Description
| -----         | ------------
| rawdata       | all web service transactions logs
| actionlist    | all unique actions seen by a web service agent
| agg2          | aggregated statistics for web service transactions
| alternateurls | a collection of URLs that a given service has been access by, such as via load balancing scenarios, alternate URLs, DNS entries, etc
| arsjobs       | automated reporting jobs
| arsreports    | outputs from the automated reporting service
| availability  | availability logs for all services
| broker history| broker historical data
| brokerrawdata | current broker data
| dependencies  | web service chaining by action type and url
| rawdata       | raw web service performance data
| rawdatadrives | raw os agent data for drives
| rawdatamachienprocess | raw data for os agent system and process data
| rawdatanic    | raw os agent data for network cards
| rawdatatally  | a quick lookup table for raw success/fail counts for web service transactions, updated by stored SQL procedures and triggers.
| slaviolatiosn | all SLA rule violations
| statusext     | extend status information, reserved for future use
