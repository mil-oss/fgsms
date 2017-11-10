-- used during CI tests only, 
-- creates alternate the databases to test the db export's validity
  

CREATE DATABASE fgsms_performance_test
  WITH OWNER = fgsms
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1
	   ;



CREATE DATABASE fgsms_config_test
  WITH OWNER = fgsms
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1
;

-- switch to the performance database 
\c fgsms_performance_test
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





\c fgsms_config_test
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
INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.uddipub.UddiPublisher', 'UDDI Metrics Publisher','FEDERATION_PUBLISH')
;
-- INSERT INTO plugins(  classname, displayname,appliesto)    VALUES ('org.miloss.fgsms.snmppublisher.SNMPAlerter', 'SNMP Publisher','FEDERATION_PUBLISH');




  
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




