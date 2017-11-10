\c fgsmsPerformance
--removes all transactions for transactional services
delete from rawdata;

--removes all historical status info
delete from availability;

--removes all historical statistical broker data
delete from brokerhistory;

--removes all current statistic broker data
delete from brokerrawdata;

--removes all sla violations
delete from slaviolations;

--removes all aggregated statistics
delete from agg;

--removes all actions for all services, primarily used by DAS
delete from actionlist;

--removes all alternate urls for web services, used by bueller only
delete from alternateurls;

--removes count values of all services
delete from rawdatatally;
delete from actionlist;


\c fgsmsConfig
delete from dcsservicehosts;
delete from federationpolicies;

delete from servicehosts;
delete from auditlog;
delete from agents;
delete from dcsservicehosts;
delete from servicehosts;
-- Warning, uncommenting the following will reset the security classification level
--delete from globalpolicies

delete from servicepolicies;

--The following removes all service level agreement defitions for all registered services
delete from sla2;

--The following removes all user email subscriptions to defined SLAs, obviously removing it from the database will not notify users of this action
delete from slaSub;

--removes the current status of all services
delete from status;

delete from useridentification_http;
delete from useridentification_xpaths;

--Uncommenting the following line will erase all global admins and authorized agent acounts. 
--delete from users;

--Uncommenting the following line will erase all service level permissions granted to users
--delete from userpermissions;