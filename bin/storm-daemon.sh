#!/usr/bin/env bash
usage="Usage: stormSERVICEs.sh [--config <conf-dir>]\
 (start|stop|restart|status) <storm> \
 <args...>"

PATH_BIN=`dirname "${BASH_SOURCE-$0}"`

STORM_PATH=$PATH_BIN/..
PATH_LOG=$STORM_PATH/logs
STORM_YARN_PATH=$STORM_PATH/../storm-yarn-master
#default place for config storm yarn
STORM_YARN_CONF_FILE=$STORM_YARN_PATH/storm.yaml
if [ $# -le 1 ]; then
	echo $usage
	exit 1
fi

COMMAND=$1
SERVICE=$2

case $COMMAND in
	status)

		if [ $SERVICE == "storm-yarn" ];then
			PROCESS_ID=` ps -ax | grep "n.MasterServer" | grep  -v  -m 1 "grep n.MasterServer" | awk -F "/" '{print $13}'`
			exit 0;
		else
			PROCESS_ID=`jps | grep $SERVICE | awk -F " " '{print $1}' | grep -v '^$'`
			exit 1;
		fi
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

		if [ $SERVICE == "storm-yarn" ];then
			PROCESS_ID=` ps -ax | grep "n.MasterServer" | grep  -v  -m 1 "grep n.MasterServer" | awk -F "/" '{print $13}'`
			$STORM_YARN_PATH/bin/storm-yarn shutdown -appId $PROCESS_ID
			maprcli alarm delete -alarm NODE_ALARM_SERVICE_STORMYARN_DOWN
		else
			PROCES_ID=`jps | grep $SERVICE | awk -F " " '{print $1}'`
			maprcli alarm delete -alarm NODE_ALARM_SERVICE_${SERVICE^^}_DOWN
			kill -9 $PROCES_ID
		fi
		;;
	start)
		if [ $SERVICE == "storm-yarn" ];then
			$STORM_YARN_PATH/bin/storm-yarn launch $STORM_YARN_CONF_FILE
		else
#		echo " `date +%m.%d-%H:%M:%S` INFO : Process starting for $SERVICE " >> $PATH_LOG/daemons.txt
			$PATH_BIN/storm $SERVICE
#		echo " `date +%d-%H.%M.%S` INFO : Process FFstarted for $SERVICE " >> $PATH_LOG/daemons.txt
		fi
esac
