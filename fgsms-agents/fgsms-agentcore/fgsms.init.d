#!/bin/sh
#
# $Id: jboss_init_redhat.sh 81068 2008-11-14 15:14:35Z dimitris@jboss.org $
#

# fgsms Control Script
# To use this script run it as root - it will switch to the specified user
#
# Here is a little (and extremely primitive) startup/shutdown script
# for RedHat systems. It assumes that JBoss lives in /usr/local/jboss,
# it's run by user 'jboss' and JDK binaries are in /usr/local/jdk/bin.
# All this can be changed in the script itself. 
#
# Either modify this script for your requirements or just ensure that
# the following variables are set correctly before calling the script.

#define where fgsms folder containing the OS agent
fgsms_HOME=${JBOSS_HOME:-"/opt/fgsms"}

#define the user under which jboss will run, or use 'RUNASIS' to run as the current user
JBOSS_USER=${JBOSS_USER:-"root"}

#make sure java is in your path
JAVAPTH=${JAVAPTH:-"/usr/local/jdk/bin"}


case "$1" in
start)

		java -Dorg.miloss.fgsms.agentConfigFileOverride=$fgsms_HOME/fgsms-agent.properties -jar $fgsms_HOME/fgsms.AgentCore.jar > $fgsms_HOME/persistentagent.log 2>&1 &

    ;;
stop)


    JBOSS_PROC_ID=`ps xa | grep 'fgsms.AgentCore.jar' | grep java | grep -v grep | awk -F= 'BEGIN {FS=" "}; {print $1 }'`
    if [[ -n "$JBOSS_PROC_ID" ]]; then
	kill $JBOSS_PROC_ID
    fi
    ;;
 


status)
     JBOSS_PROC_ID=`ps xa | grep 'fgsms.AgentCore.jar' | grep java | grep -v grep | awk -F= 'BEGIN {FS=" "}; {print $1 }'`
    if [[ -n "$JBOSS_PROC_ID" ]]; then
        echo -e 'fgsms Persistent is running with process id: [\033[1;32m' $JBOSS_PROC_ID '\033[0m]'
        RETVAL=0
    else
        echo -e 'fgsms Persistent Agent  is \033[1;31m'stopped'\033[0m'
        RETVAL=1
    fi
;;

*)



    echo "usage: $0 (start|stop|status)"
esac

exit $RETVAL

