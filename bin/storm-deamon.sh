#!/usr/bin/env bash
usage="Usage: stormSERVICEs.sh [--config <conf-dir>]\
 (start|stop|restart|status) <storm> \
 <args...>"

PATH_BIN=`dirname "${BASH_SOURCE-$0}"`

STORM_PATH=$PATH_BIN/..
PATH_LOG=$STORM_PATH/logs
if [ $# -le 1 ]; then
	echo $usage
	exit 1
fi

COMMAND=$1
SERVICE=$2

case $COMMAND in
	status)
    	PROCESS_ID=`jps | grep $SERVICE | awk -F " " '{print $1}' | grep -v '^$'`
		if [ -n "$PROCESS_ID" ];then
			echo $SERVICE running as process $PROCESS_ID.
#			echo " `date +%m.%d-%H:%M:%S` INFO : Process checked for $SERVICE : TRUE" >> $PATH_LOG/daemons.txt
			exit 0;
		else
			echo $SERVICE not running.
#			echo " `date +%m.%d-%H:%M:%S` INFO : Process checked for $SERVICE : FALSE" >> $PATH_LOG/daemons.txt
			exit 1;
    	fi
		;;
	stop)
		PROCES_ID=`jps | grep $SERVICE | awk -F " " '{print $1}'`
		maprcli alarm delete -alarm NODE_ALARM_SERVICE_${SERVICE^^}_DOWN
		kill -9 $PROCES_ID
		;;
	start)
#		echo " `date +%m.%d-%H:%M:%S` INFO : Process starting for $SERVICE " >> $PATH_LOG/daemons.txt
		$PATH_BIN/storm $SERVICE
#		echo " `date +%d-%H.%M.%S` INFO : Process FFstarted for $SERVICE " >> $PATH_LOG/daemons.txt
esac
