To build the databases, first modify dbcurrent.sql. Adjust password if necessary

For tomcat deployments (default), you'll need to update the context.xml file.

If changing the password, make sure to update the postgres-ds.xml file for Jboss deployments (see example xml docs)

(Windows) psql -U posgres -f [pathto]dbcurrent.sql
(Linux) su – postgres –c “psql –f /tmp/dbcurrent.sql”

