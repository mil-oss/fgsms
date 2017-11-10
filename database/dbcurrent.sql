--SQL Build Script for fgsms

-- This is for fgsms web Services to use to connect to the database
-- if you alter this, the file at 
-- jboss: server/default/deploy/postgres-ds.xml NEEDS VERIFICATION
-- tomcat: tomcat/webapps/fgsmsServices/META-INF/context.xml

drop database if exists fgsms_performance
;
drop database if exists fgsms_config
;
drop database if exists fgsms_quartz
;
drop role if exists fgsms
;

CREATE ROLE fgsms LOGIN
  --ENCRYPTED PASSWORD 'md5c13851fc51b916e074817e99b45aa222'
  PASSWORD 'PAssword1234!!!!'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE
  ;
  
  CREATE ROLE fgsmsqtz LOGIN
  --ENCRYPTED PASSWORD 'md5c13851fc51b916e074817e99b45aa222'
  PASSWORD 'PAssword1234!!!!'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE
  ;
  

CREATE DATABASE fgsms_performance
  WITH OWNER = fgsms
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1
	   ;



-- switch to the performance database 
\c fgsms_performance
;

CREATE TABLE statusext
(
  uri text NOT NULL,
  utcdatetime bigint NOT NULL,
  dataname text NOT NULL,
  datavalue text NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE statusext OWNER TO fgsms
;
COMMENT ON TABLE statusext IS 'fgsms version 6.3 and up'
;



--quick access for web service dependency injection
CREATE TABLE dependencies
(
   sourcesoapaction text NOT NULL,
  destinationsoapaction text NOT NULL,
  destinationurl text NOT NULL,
  sourceurl text NOT NULL,
  CONSTRAINT dependencies_pkey PRIMARY KEY (sourceurl, destinationurl, destinationsoapaction, sourcesoapaction)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE dependencies OWNER TO fgsms
;



CREATE TABLE arsjobs
(
  lastranat bigint,
  jobid text NOT NULL,
  reportdef bytea NOT NULL,
  owninguser text NOT NULL,
  hasextrapermissions boolean DEFAULT false,
  enabled boolean NOT NULL DEFAULT true,
  CONSTRAINT arsjobs_pkey PRIMARY KEY (jobid)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE arsjobs OWNER TO fgsms
;


CREATE TABLE arsreports
(
  reportid text NOT NULL,
  jobid text NOT NULL,
  datetime bigint NOT NULL,
  reportcontents bytea NOT NULL,
  CONSTRAINT arsreports_pkey PRIMARY KEY (reportid),
  CONSTRAINT jobcheck FOREIGN KEY (jobid)
      REFERENCES arsjobs (jobid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE arsreports OWNER TO fgsms
;

-- Index: fki_jobcheck

-- DROP INDEX fki_jobcheck;

CREATE INDEX fki_jobcheck
  ON arsreports
  USING btree
  (jobid)
;


  

CREATE TABLE rawdatadrives
(
  hostname text,
  driveidentifier text NOT NULL,
  freespace bigint,
  utcdatetime bigint,
  id text NOT NULL,
  writekbs bigint,
  readkbs bigint,
  deviceidentifier text,
  status boolean,
  statusmsg text,
  domainname text,
  uri text,
  CONSTRAINT rawdatadrives_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE,autovacuum_enabled=true
)
;
ALTER TABLE rawdatadrives OWNER TO fgsms
;



CREATE TABLE rawdatamachineprocess
(
  uri text,
  memoryused bigint,
  percentcpu integer,
  id text NOT NULL,
  utcdatetime bigint NOT NULL,
  threads bigint,
  openfiles bigint,
  startedat bigint,
  CONSTRAINT rawdatamachineprocess_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE,autovacuum_enabled=true
)
;
ALTER TABLE rawdatamachineprocess OWNER TO fgsms
;


	   
CREATE TABLE actionlist
(
  uri text NOT NULL,
  soapaction text NOT NULL,
  CONSTRAINT actionlist_pkey PRIMARY KEY (uri, soapaction)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
  
)
;
ALTER TABLE actionlist OWNER TO fgsms
;



CREATE TABLE agg2
(
  success bigint NOT NULL DEFAULT (-1),
  failure bigint NOT NULL DEFAULT (-1),
  avgres bigint NOT NULL DEFAULT (-1),
  timestampepoch bigint NOT NULL,
  avail double precision NOT NULL DEFAULT (-1),
  uri text NOT NULL,
  sla bigint NOT NULL DEFAULT (-1),
  soapaction text NOT NULL,
  mtbf bigint NOT NULL DEFAULT (-1),
  maxreq bigint NOT NULL DEFAULT (-1),
  maxres bigint NOT NULL DEFAULT (-1),
  maxresponsetime bigint NOT NULL DEFAULT (-1),
  timerange bigint NOT NULL,
  
  avgcpu double precision NOT NULL DEFAULT (-1),
  avgmem bigint NOT NULL DEFAULT (-1),
  avgthread bigint NOT NULL DEFAULT (-1),
  avgfile bigint NOT NULL DEFAULT (-1),
  
  avgchan bigint NOT NULL DEFAULT (-1),
  maxqueuedepth bigint NOT NULL DEFAULT (-1),
  avgmsgin bigint NOT NULL DEFAULT (-1),
  avgmsgout bigint NOT NULL DEFAULT (-1),
  avgmsgdropped bigint NOT NULL DEFAULT (-1),
  CONSTRAINT agg2_pkey PRIMARY KEY (uri, soapaction, timerange)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE agg2 OWNER TO fgsms
;






CREATE TABLE alternateurls
(
  uri text NOT NULL,
  alturi text NOT NULL,
  CONSTRAINT alternateurls_pkey PRIMARY KEY (uri, alturi)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
  
)
;
ALTER TABLE alternateurls OWNER TO fgsms
;



CREATE TABLE availability
(
  uri text,
  utcdatetime bigint,
  id text NOT NULL,
  message text,
  status boolean,
  CONSTRAINT availability_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE availability OWNER TO fgsms
;





CREATE TABLE brokerhistory
(
  host text,
  utcdatetime bigint,
  namecol text,
  messagecount bigint,
  consumercount bigint,
  typecol text,
  agenttype text,
  recievedmessagecount bigint,
  activeconsumercount bigint,
  queuedepth bigint,
  canonicalname text,
  messagedropcount bigint,
  bytesdropcount bigint,
  bytesin bigint,
  bytesout bigint
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE brokerhistory OWNER TO fgsms
;

-- Index: brokerhistory_host_idx

-- DROP INDEX brokerhistory_host_idx;

CREATE INDEX brokerhistory_host_idx
  ON brokerhistory
  USING btree
  (host)
;


  
  
  
CREATE TABLE brokerrawdata
(
  host text,
  utcdatetime bigint,
  namecol text,
  messagecount bigint,
  consumercount bigint,
  typecol text,
  agenttype text,
  recievedmessagecount bigint,
  activeconsumercount bigint,
  queuedepth bigint,
  canonicalname text,
  messagedropcount bigint,
  bytesdropcount bigint,
  bytesin bigint,
  bytesout bigint
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE brokerrawdata OWNER TO fgsms
;

-- Index: brokerrawdata_host_idx

-- DROP INDEX brokerrawdata_host_idx;

CREATE INDEX brokerrawdata_host_idx
  ON brokerrawdata
  USING btree
  (host)
;

  
  
CREATE TABLE rawdata
(
  uri text NOT NULL,
  responsetimems integer NOT NULL,
  monitorsource text NOT NULL,
  hostingsource text NOT NULL,
  requestxml bytea,
  responsexml bytea,
  consumeridentity text,
  transactionid text NOT NULL,
  soapaction text NOT NULL,
  responsesize integer NOT NULL,
  requestsize integer NOT NULL,
  utcdatetime bigint NOT NULL,
  success boolean DEFAULT true,
  slafault text,
  agenttype text,
  originalurl text,
  message bytea,
  relatedtransactionid text,
  threadid text,
  requestheaders bytea,
  responseheaders bytea,
  CONSTRAINT rawdata_pkey PRIMARY KEY (transactionid)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE rawdata OWNER TO fgsms
;

-- Index: rawdata_slafault_uri_action_utcdatetime_idx

-- DROP INDEX rawdata_slafault_uri_action_utcdatetime_idx;

CREATE INDEX rawdata_slafault_uri_action_utcdatetime_idx
  ON rawdata
  USING btree
  (slafault, uri, soapaction, utcdatetime)
;

-- Index: rawdata_time_index

-- DROP INDEX rawdata_time_index;

CREATE INDEX rawdata_time_index
  ON rawdata
  USING btree
  (utcdatetime)
;

-- Index: rawdata_uri_action_success_idx

-- DROP INDEX rawdata_uri_action_success_idx;

CREATE INDEX rawdata_uri_action_success_idx
  ON rawdata
  USING btree
  (uri, soapaction, success)
;

-- Index: rawdata_uri_index

-- DROP INDEX rawdata_uri_index;

CREATE INDEX rawdata_uri_index
  ON rawdata
  USING hash
  (uri)
;

-- Index: rawdata_uri_success

-- DROP INDEX rawdata_uri_success;

CREATE INDEX rawdata_uri_success
  ON rawdata
  USING btree
  (uri, success)
;

-- Index: rawdata_uri_utcdatetime_success_idx

-- DROP INDEX rawdata_uri_utcdatetime_success_idx;

CREATE INDEX rawdata_uri_utcdatetime_success_idx
  ON rawdata
  USING btree
  (uri, utcdatetime, success)
;

-- Index: rawdataindex

-- DROP INDEX rawdataindex;

CREATE INDEX rawdataindex
  ON rawdata
  USING hash
  (transactionid)
;

-- Index: threadindex

-- DROP INDEX threadindex;

CREATE INDEX threadindex
  ON rawdata
  USING btree
  (threadid)
;


CREATE TABLE rawdatanic
(
  nicid text NOT NULL,
  utcdatetime bigint NOT NULL,
  sendkbs bigint,
  receivekbs bigint,
  hostname text NOT NULL,
  domainname text,
  id text NOT NULL,
  uri text,
  CONSTRAINT rawdatanic_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE,
  autovacuum_enabled=true
)
;
ALTER TABLE rawdatanic OWNER TO fgsms
;




CREATE TABLE rawdatatally
(
  uri text NOT NULL,
  success bigint DEFAULT 0,
  faults bigint DEFAULT 0,
  slafault bigint DEFAULT 0,
  CONSTRAINT rawdatatally_pkey PRIMARY KEY (uri)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE rawdatatally OWNER TO fgsms
;
  
  
CREATE OR REPLACE FUNCTION updatetally()
  RETURNS trigger AS
$BODY$
    BEGIN
       IF (TG_OP = 'INSERT') THEN
		
		
		--update tally count
		if (NEW.success) then
                    UPDATE rawdatatally
	            SET success = success + 1
	            WHERE uri = NEW.uri;
		else 
		  UPDATE rawdatatally
	            SET faults = faults + 1
	            WHERE uri = NEW.uri;

		end if;
		if (NEW.slafault is not null) then
			UPDATE rawdatatally
	            SET slafault = slafault + 1
	            WHERE uri = NEW.uri;
		end if;
		
            
            RETURN NEW;
        END IF;
          IF (TG_OP = 'UPDATE') THEN
		
		
		--update tally count
		if (NEW.success) then
                    UPDATE rawdatatally
	            SET success = success + 1
	            WHERE uri = NEW.uri;
		else 
		  UPDATE rawdatatally
	            SET faults = faults + 1
	            WHERE uri = NEW.uri;

		end if;
		if (NEW.slafault is not null) then
			UPDATE rawdatatally
	            SET slafault = slafault + 1
	            WHERE uri = NEW.uri;
		end if;
		
            
            RETURN NEW;
        END IF;

        IF (TG_OP = 'DELETE') THEN
		
		
		--update tally count
		if (OLD.success) then
                    UPDATE rawdatatally
	            SET success = success -1
	            WHERE uri = OLD.uri;
		else 
		  UPDATE rawdatatally
	            SET faults = faults - 1
	            WHERE uri = OLD.uri;

		end if;
		if (OLD.slafault is not null) then
			UPDATE rawdatatally
	            SET slafault = slafault - 1
	            WHERE uri = OLD.uri;
		end if;
		
            
            RETURN NEW;
        END IF;
        
        RETURN NULL; -- result is ignored since this is an AFTER trigger
    END;
    $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION updatetally() OWNER TO fgsms
;


-- Trigger: updatetally2 on rawdata

-- DROP TRIGGER updatetally2 ON rawdata;

CREATE TRIGGER updatetally2
  AFTER INSERT OR DELETE
  ON rawdata
  FOR EACH ROW
  EXECUTE PROCEDURE updatetally()
;


  
  
CREATE TABLE slaviolations
(
  utcdatetime bigint,
  msg bytea,
  uri text,
  relatedtransaction text,
  incidentid text NOT NULL,
  CONSTRAINT slaviolations_pkey PRIMARY KEY (incidentid)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE slaviolations OWNER TO fgsms
;

CREATE INDEX rawdatanic_uri_timestamp_idx
  ON rawdatanic
  USING btree
  (uri, utcdatetime)
;


CREATE INDEX rawdatadrives_uri_timestamp_idx
  ON rawdatadrives
  USING btree
  (uri, utcdatetime)
;

CREATE INDEX rawdatamachineprocess_timestamp_uri_idx
  ON rawdatamachineprocess
  USING btree
  (utcdatetime, uri)
;



CREATE TABLE rawdatamachinesensor
(
  uri text NOT NULL,
  id text NOT NULL,
  utcdatetime bigint NOT NULL,
  dataname text,
  datavalue text,
  CONSTRAINT rawdatamachinesensor_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE,
  autovacuum_enabled=true
)
;
ALTER TABLE rawdatamachinesensor OWNER TO fgsms
;

-- Index: rawdatamachinesensor_uri_utcdatetime_idx

-- DROP INDEX rawdatamachinesensor_uri_utcdatetime_idx;

CREATE INDEX rawdatamachinesensor_uri_utcdatetime_idx
  ON rawdatamachinesensor
  USING btree
  (uri, utcdatetime)
;







CREATE DATABASE fgsms_config
  WITH OWNER = fgsms
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1
;

\c fgsms_config
;	   
	   
CREATE TABLE agents
(
  agenttype text NOT NULL,
  CONSTRAINT agents_pkey PRIMARY KEY (agenttype)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE agents OWNER TO fgsms
;

-- 6.3 and up
CREATE TABLE agentmailbox
(
  uri text NOT NULL,
  utcdatetime bigint NOT NULL,
  state integer NOT NULL DEFAULT 0,
  processingtime bigint NOT NULL DEFAULT 0,
  result bytea
)
WITH (
  OIDS=FALSE
)
;
ALTER TABLE agentmailbox OWNER TO fgsms
;

CREATE TABLE auditlog
(
  utcdatetime bigint,
  username text,
  classname text,
  method text,
  memo bytea,
  classification text,
  ipaddress text
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE auditlog OWNER TO fgsms
;

-- Index: auditlog_timestamp_idx

-- DROP INDEX auditlog_timestamp_idx;

CREATE INDEX auditlog_timestamp_idx
  ON auditlog
  USING btree
  (utcdatetime)
;


  
-- Table: plugins

-- DROP TABLE plugins;

CREATE TABLE plugins
(
  classname text NOT NULL,
  displayname text NOT NULL,
  appliesto text NOT NULL,
  CONSTRAINT plugins_classname_key UNIQUE (classname)
)
WITH (
  OIDS=FALSE,
  autovacuum_enabled=true
)
;
ALTER TABLE plugins OWNER TO fgsms
;

INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.actions.AMQPAlerter', 'Apache Qpid/Redhat MRG Alert','SLA_ACTION')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.actions.EmailAlerter', 'Email Alert','SLA_ACTION')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.actions.JMSAlerter', 'JMS Alert','SLA_ACTION')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.actions.SimpleMulticastAlerter', 'Multicast Alert','SLA_ACTION')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.actions.SLAActionLog', 'Log','SLA_ACTION')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.actions.SLAActionRunScript', 'Run a program or script','SLA_ACTION')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.actions.WSNotificationAlerter', 'WS-Notification Alert','SLA_ACTION')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ActionContainsIgnoreCase', 'Action Contains','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ActionEqualsIgnoreCase', 'Action Equals','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.AllFaults', 'All Faults','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.AllSuccesses', 'All Successes','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.BrokerQueueSizeGreaterThan', 'Broker Queue Size Greater than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus', 'Change In Availability','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ConsumerContainsIgnoreCase', 'Consumer Contains','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ConsumerEqualsIgnoreCase', 'Consumer Equals','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.FaultsOverTimeGreaterThan', 'Faults over time greater than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.FaultsOverTimeLessThan', 'Faults over time less than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighCPUUsage', 'High CPU','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighCPUUsageOverTime', 'High CPU over time','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighDiskUsageOverTime', 'High Disk Usage over time','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighMemoryUsage', 'High Memory Usage','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighMemoryUsageOverTime', 'High Memory Usage over time','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighNetworkUsageOverTime', 'High Network usage over time','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighOpenFileHandles', 'High Open File Handles','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.HighThreadCount', 'High Thread Count','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.InvocationsOverTimeGreatThan', 'Invocations over time greater than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.InvocationsOverTimeLessThan', 'Invocation over time less than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.LowDiskSpace', 'Low disk space','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.MeanTimeBetweenFailureGreatThan', 'MTBF greater than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.MeanTimeBetweenFailureLessThan', 'MTBF less than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.QueueOrTopicDoesNotExist', 'Topic or Queue does not eixst','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.RequestContentContainsIgnoreCase', 'Request content contains','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.RequestMessageSizeGreaterThan', 'Request message is greater than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.RequestMessageSizeLessThan', 'Request message is less than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ResponseContentContainsIgnoreCase', 'Respone content contains','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ResponseMessageSizeGreaterThan', 'Response message is greater than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ResponseMessageSizeLessThan', 'Response message is less than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ResponseTimeGreaterThan', 'Response time is greater than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.ResponseTimeLessThan', 'Response time is less than','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.StaleData', 'Stale Data','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.TransactionalAgentMemoContainsIgnoreCase', 'Agent Memo contains','SLA_RULE')
;
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.sla.rules.XPathExpression', 'XPath expression','SLA_RULE')
;

--INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.plugins.slauddi.UpdateUddiRegistration', 'Update UDDI Registration','SLA_ACTION');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.uddipub.UddiPublisher', 'UDDI Metrics Publisher','FEDERATION_PUBLISH');
-- INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.snmppublisher.SNMPAlerter', 'SNMP Publisher','FEDERATION_PUBLISH');

INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.AvailabilityByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ServiceLevelAgreementReport', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.broker.ConsumersByQueueOrTopic', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.broker.QueueDepth', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.broker.QueueTopicCountByBroker', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.os.CpuUsageReport', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.os.DiskIOReport', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.os.FreeDiskSpace', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.os.MemoryUsageReport', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.os.NetworkIOReport', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.os.OpenFilesByProcess', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.os.ThreadCount', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.AverageMessageSizeByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.AverageMessageSizeByServiceByMethod', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.AverageResponseTimeByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.AverageResponseTimeByServiceByMethod', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.InvocationsByConsumer', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.InvocationsByConsumerByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.InvocationsByConsumerByServiceByMethod', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.InvocationsByDataCollector', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.InvocationsByHostingServer', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.InvocationsByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.InvocationsByServiceByMethod', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.MeanTimeBetweenFailureByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.ResponseTimeOverTime', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.SuccessFailureCountByHostingServer', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.SuccessFailureCountByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.SuccessFailureCountByServiceByMethod', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.ThroughputByHostingServer', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.ThroughputByService', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.ThroughputByServiceByMethod', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.TotalMessageSizeByServiceByMethod', 'na','REPORTING');
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.services.rs.impl.reports.ws.TotalMessageSizesByService', 'na','REPORTING');
  
CREATE TABLE dcsservicehosts
(
  hostname text NOT NULL,
  records bigint NOT NULL DEFAULT 0,
  CONSTRAINT dcsservicehosts_pkey PRIMARY KEY (hostname)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE dcsservicehosts OWNER TO fgsms
;






CREATE TABLE globalpolicies
(
  policyrefreshrate bigint NOT NULL,
  recordedmessagecap integer NOT NULL,
  classification text DEFAULT 'U'::text,
  agentsenable boolean DEFAULT true,
  caveat text,
  uddipublishrate bigint
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE globalpolicies OWNER TO fgsms
;



CREATE TABLE mail
(
  property text NOT NULL,
  valuecol text,
  CONSTRAINT a PRIMARY KEY (property)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE mail OWNER TO fgsms
;



CREATE TABLE servicehosts
(
  hostname text NOT NULL,
  records bigint NOT NULL DEFAULT 0,
  CONSTRAINT servicehosts_pkey PRIMARY KEY (hostname)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE servicehosts OWNER TO fgsms
;



CREATE TABLE servicepolicies
(
  uri text NOT NULL,
  datattl bigint,
  buellerenabled boolean DEFAULT true,
  latitude double precision,
  longitude double precision,
  displayname text,
  policytype integer,
  hostname text,
  domaincol text,
  xmlpolicy bytea,
  hassla boolean,	--has an sla policy
  hasfederation boolean, --has a federation policy
  bucket text,
  lastchanged bigint,
  lastchangedby text,
  healthstatusenabled boolean DEFAULT false,
  parenturi text ,
  CONSTRAINT polkey PRIMARY KEY (uri)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE servicepolicies OWNER TO fgsms
;

-- Index: sevicepolicyindex

-- DROP INDEX sevicepolicyindex;

CREATE INDEX sevicepolicyindex
  ON servicepolicies
  USING hash
  (uri)
;


CREATE TABLE slasub
(
  slaid text NOT NULL,
  username text NOT NULL,
  uri text NOT NULL
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE slasub OWNER TO fgsms
;



CREATE TABLE status
(
  uri text NOT NULL,
  utcdatetime bigint,
  message text,
  status boolean,
  monitored boolean NOT NULL DEFAULT false,
  CONSTRAINT status_pkey PRIMARY KEY (uri),
  CONSTRAINT statusuri UNIQUE (uri)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE status OWNER TO fgsms
;

-- Index: statusindex

-- DROP INDEX statusindex;

CREATE INDEX statusindex
  ON status
  USING hash
  (uri)
;


  
  

CREATE TABLE userpermissions
(
  username text NOT NULL,
  objecturi text NOT NULL,
  auditobject boolean DEFAULT false,
  administerobject boolean DEFAULT false,
  writeobject boolean DEFAULT false,
  readobject boolean DEFAULT false
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE userpermissions OWNER TO fgsms
;


CREATE TABLE users
(
  username text NOT NULL,
  displayname text,
  email text,
  rolecol text,
  email2 text,
  email3 text,
  email1 text,
  CONSTRAINT usersc UNIQUE (username)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE users OWNER TO fgsms
;




CREATE TABLE settings
(
  namecol text NOT NULL,
  keycol text NOT NULL,
  valuecol bytea NOT NULL,
  isencrypted boolean DEFAULT false,
  CONSTRAINT settings_pkey PRIMARY KEY (keycol, namecol)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE settings OWNER TO fgsms
;
ALTER TABLE settings ADD UNIQUE (namecol, keycol)
;



CREATE TABLE bueller
(
  uri text NOT NULL,
  username text NOT NULL,
  pwdcol bytea NOT NULL,
  authtype integer,
  CONSTRAINT bueller_pkey PRIMARY KEY (uri)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE bueller OWNER TO fgsms
;


CREATE TABLE machineinfo
(
  xmlcol bytea,
  hostname text NOT NULL,
  domaincol text,
  lastchanged bigint,
  CONSTRAINT machineinfo_pkey PRIMARY KEY (hostname)
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE machineinfo OWNER TO fgsms
;


CREATE TABLE agentconf
(
  config bytea NOT NULL,
  utcdatetime bigint NOT NULL,
   agent text NOT NULL
)
WITH (
  OIDS=FALSE,  autovacuum_enabled=true
)
;
ALTER TABLE agentconf OWNER TO fgsms
;






--##################################################################################################
-- This section is used to setup the initial administrator user account

-- You also need to setup an initial account to use for agents
-- Note, this is case sensitive

-- If the ' symbol is in somneone's name, be sure to escape it as '' 

--If using fgsms in a devleopement, demo or testing environment and authentication  is set to None, use this statement
--INSERT INTO users(username, displayname, email, "role")   VALUES ('anonymous', 'anonymous', 'anonymous', 'admin');

--Root user   - When authentication  is set to Username/Password, use this statement as an example
INSERT INTO users(username, displayname, email, rolecol)   VALUES ('fgsmsadmin', 'Admin', 'fgsms@localhost.localdomain', 'admin')
;

--Agent user   - When authentication  is set to Username/Password, use this statement as an example
INSERT INTO users(username, rolecol)   VALUES ('fgsmsagent' , 'agent')
;	


--When authentication  is set to PKI/CAC, use this statement as an example
-- INSERT INTO users(username, displayname, email, "role")   VALUES ('anonymous', 'CN=DOE.M.JOHN.1234567890, OU=USA OU=PKI, OU=DoD, O=U.S. Government, C=US', 'anonymous', 'admin');	



--insert default policy, unclassified
INSERT INTO globalpolicies(    policyrefreshrate, recordedmessagecap, classification,  agentsenable, caveat)
    VALUES (180000, 1024000, 'U',   true, 'None')
;
	
--default mail settings
INSERT INTO mail(property, valuecol)  VALUES ('mail.smtp.host','localhost')
;
INSERT INTO mail(property, valuecol)  VALUES ('mail.smtp.port','25')
;
INSERT INTO mail(property, valuecol)  VALUES ('mail.smtp.auth','false')
;
INSERT INTO mail(property, valuecol)  VALUES ('fgsms.GUI.URL','http://localhost:8888/fgsmsBootstrap')
;
INSERT INTO mail(property, valuecol)  VALUES ('defaultReplyAddress','fgsms@localhost.localdomain')
;


-- default 'settings' settings

INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('QpidJMXAgent', 'Interval', '30000', false) --30 sec
;
	
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Bueller', 'Interval', '10000', false)	--10 sec
;

INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Bueller', 'defaultUser', 'fgsmsagent', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Bueller', 'defaultPassword', 'rdWSbPjbjm/bwV7hBnPDIw==:agnj4VYJgRFeSarqXJRVmA7TWeIBzm5BsgXfNQtcLqY=:X68+6lU3rLvHFtsOBD+/AA==', true)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Bueller', 'Enabled', 'true', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('DataPruner', 'Interval', '86400000', false) --1 day

;

INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('StatisticsAggregator', 'Periods', '60000', false)	--adds 1 min to the required period of 5,15,60,1440 minues
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('StatisticsAggregator', 'Interval', '60000', false) --1 min - run every x ms
;	


INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('NTSLAProcessor', 'Interval', '30000', false) 	--5min
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('NTSLAProcessor', 'StaleDataThreshold', '30000', false)	--5min
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('FederationScheduler', 'Interval', '30000', false) --5 min
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('FederationScheduler', 'Enabled', 'true', false)
; 
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('UddiPublisher', 'Enabled', 'false', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('UddiPublisher', 'Username', 'administrator', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('UddiPublisher', 'AuthMode', 'Http', false) --Http, Uddi;
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('UddiPublisher', 'InquiryURL', 'http://localhost/uddi/inquiry', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('UddiPublisher', 'PublishURL', 'http://localhost/uddi/publish', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('UddiPublisher', 'SecurityURL', 'http://localhost/uddi/security', false);

INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Status', 'defaultRetention', '2592000000', false) --1 month
;	
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Process', 'defaultRetention', '2592000000', false) 	--1 month
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Machine', 'defaultRetention', '2592000000', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Statistics', 'defaultRetention', '2592000000', false)
;	
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Transactional', 'defaultRetention', '2592000000', false)
;

INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Transactional', 'defaultRecordRequest', 'false', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Transactional', 'defaultRecordResponse', 'false', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Transactional', 'defaultRecordFaults', 'true', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Policy.Transactional', 'defaultRecordHeaders', 'false', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('Agents.Process', 'ReportingFrequency', '30000', false)
;

INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('defaults', 'truststore', 'truststore.jks', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('defaults', 'truststorepass', 'KmrOe/PgHsr7OsMP9ZfNMQ==:/A9fG+uJ7ma+YVht/Z7gnyZyPI2YS/xP4VzKZzO0yNQ=:pSftLqnYolBaCTGLFdK34g==', true)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('defaults', 'keystore', 'keystore.jks', false)
;
INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('defaults', 'keystorepass', 'KmrOe/PgHsr7OsMP9ZfNMQ==:/A9fG+uJ7ma+YVht/Z7gnyZyPI2YS/xP4VzKZzO0yNQ=:pSftLqnYolBaCTGLFdK34g==', true)
;



--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('JMSAlerts','ContextProviderUrl','jnp://localhost:1199', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('JMSAlerts','DestinationType','topic', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('JMSAlerts','Destination','topic/fgsmsAlerts', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('JMSAlerts','ConnectionFactoryLookup','/ConnectionFactory', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('JMSAlerts','INITIAL_CONTEXT_FACTORY','org.jnp.interfaces.NamingContextFactory', false);
--INSERT INTO settings(keycol, namecol, valuecol, isencrypted) VALUES ('JMSAlerts','URL_PKG_PREFIXES','org.jboss.naming:org.jnp.interfaces', false);





           
				
				
				
				
				

CREATE DATABASE fgsms_quartz
  WITH OWNER = fgsms
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
 --      LC_COLLATE = 'English_United States.1252' //this doesn't work in Linux
--       LC_CTYPE = 'English_United States.1252' //this doesn't work in Linux
       CONNECTION LIMIT = -1
;

	   
\c fgsms_quartz				
;				
-- The following is for supporting clustered/failover
-- Thanks to Patrick Lightbody for submitting this...
--
-- In your Quartz properties file, you'll need to set 
-- org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.PostgreSQLDelegate



drop table qrtz_job_listeners
;
drop table qrtz_trigger_listeners
;
drop table qrtz_fired_triggers
;
DROP TABLE QRTZ_PAUSED_TRIGGER_GRPS
;
DROP TABLE QRTZ_SCHEDULER_STATE
;
DROP TABLE QRTZ_LOCKS
;
drop table qrtz_simple_triggers
;
drop table qrtz_cron_triggers
;
DROP TABLE QRTZ_BLOB_TRIGGERS
;
drop table qrtz_triggers
;
drop table qrtz_job_details
;
drop table qrtz_calendars
;


CREATE TABLE qrtz_job_details
  (
    JOB_NAME  VARCHAR(200) NOT NULL,
    JOB_GROUP VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    JOB_CLASS_NAME   VARCHAR(250) NOT NULL, 
    IS_DURABLE BOOL NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    IS_STATEFUL BOOL NOT NULL,
    REQUESTS_RECOVERY BOOL NOT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
)
;
ALTER TABLE qrtz_job_details OWNER TO fgsms
;
CREATE TABLE qrtz_job_listeners
  (
    JOB_NAME  VARCHAR(200) NOT NULL, 
    JOB_GROUP VARCHAR(200) NOT NULL,
    JOB_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
)
;
ALTER TABLE qrtz_job_listeners OWNER TO fgsms
;

CREATE TABLE qrtz_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    JOB_NAME  VARCHAR(200) NOT NULL, 
    JOB_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    DESCRIPTION VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT NULL,
    PREV_FIRE_TIME BIGINT NULL,
    PRIORITY INTEGER NULL,
    TRIGGER_STATE VARCHAR(16) NOT NULL,
    TRIGGER_TYPE VARCHAR(8) NOT NULL,
    START_TIME BIGINT NOT NULL,
    END_TIME BIGINT NULL,
    CALENDAR_NAME VARCHAR(200) NULL,
    MISFIRE_INSTR SMALLINT NULL,
    JOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
)
;
ALTER TABLE qrtz_triggers OWNER TO fgsms
;


CREATE TABLE qrtz_simple_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    REPEAT_COUNT BIGINT NOT NULL,
    REPEAT_INTERVAL BIGINT NOT NULL,
    TIMES_TRIGGERED BIGINT NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
)
;
ALTER TABLE qrtz_simple_triggers OWNER TO fgsms
;
CREATE TABLE qrtz_cron_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID VARCHAR(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
ALTER TABLE qrtz_cron_triggers OWNER TO fgsms
;
CREATE TABLE qrtz_blob_triggers
  (
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    BLOB_DATA BYTEA NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
)
;
ALTER TABLE qrtz_blob_triggers OWNER TO fgsms
;
CREATE TABLE qrtz_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR(200) NOT NULL, 
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
)
;
ALTER TABLE qrtz_trigger_listeners OWNER TO fgsms
;

CREATE TABLE qrtz_calendars
  (
    CALENDAR_NAME  VARCHAR(200) NOT NULL, 
    CALENDAR BYTEA NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
)
;
ALTER TABLE qrtz_calendars OWNER TO fgsms
;

CREATE TABLE qrtz_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR(200) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
)
;
ALTER TABLE qrtz_paused_trigger_grps OWNER TO fgsms
;

CREATE TABLE qrtz_fired_triggers 
  (
    ENTRY_ID VARCHAR(95) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    IS_VOLATILE BOOL NOT NULL,
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    FIRED_TIME BIGINT NOT NULL,
    PRIORITY INTEGER NOT NULL,
    STATE VARCHAR(16) NOT NULL,
    JOB_NAME VARCHAR(200) NULL,
    JOB_GROUP VARCHAR(200) NULL,
    IS_STATEFUL BOOL NULL,
    REQUESTS_RECOVERY BOOL NULL,
    PRIMARY KEY (ENTRY_ID)
)
;
ALTER TABLE qrtz_fired_triggers OWNER TO fgsms
;


CREATE TABLE qrtz_scheduler_state 
  (
    INSTANCE_NAME VARCHAR(200) NOT NULL,
    LAST_CHECKIN_TIME BIGINT NOT NULL,
    CHECKIN_INTERVAL BIGINT NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
)
;
ALTER TABLE qrtz_scheduler_state OWNER TO fgsms
;
CREATE TABLE qrtz_locks
  (
    LOCK_NAME  VARCHAR(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
)
;
ALTER TABLE qrtz_locks OWNER TO fgsms
;

INSERT INTO qrtz_locks values('TRIGGER_ACCESS')
;
INSERT INTO qrtz_locks values('JOB_ACCESS')
;
INSERT INTO qrtz_locks values('CALENDAR_ACCESS')
;
INSERT INTO qrtz_locks values('STATE_ACCESS')
;
INSERT INTO qrtz_locks values('MISFIRE_ACCESS')
;

create index idx_qrtz_j_req_recovery on qrtz_job_details(REQUESTS_RECOVERY)
;
create index idx_qrtz_t_next_fire_time on qrtz_triggers(NEXT_FIRE_TIME)
;
create index idx_qrtz_t_state on qrtz_triggers(TRIGGER_STATE)
;
create index idx_qrtz_t_nft_st on qrtz_triggers(NEXT_FIRE_TIME,TRIGGER_STATE)
;
create index idx_qrtz_t_volatile on qrtz_triggers(IS_VOLATILE)
;
create index idx_qrtz_ft_trig_name on qrtz_fired_triggers(TRIGGER_NAME)
;
create index idx_qrtz_ft_trig_group on qrtz_fired_triggers(TRIGGER_GROUP)
;
create index idx_qrtz_ft_trig_nm_gp on qrtz_fired_triggers(TRIGGER_NAME,TRIGGER_GROUP)
;
create index idx_qrtz_ft_trig_volatile on qrtz_fired_triggers(IS_VOLATILE)
;
create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(INSTANCE_NAME)
;
create index idx_qrtz_ft_job_name on qrtz_fired_triggers(JOB_NAME)
;
create index idx_qrtz_ft_job_group on qrtz_fired_triggers(JOB_GROUP)
;
create index idx_qrtz_ft_job_stateful on qrtz_fired_triggers(IS_STATEFUL)
;
create index idx_qrtz_ft_job_req_recovery on qrtz_fired_triggers(REQUESTS_RECOVERY)
;

