#!/usr/bin/env bash
usage="Usage: storm-deamon.sh \
 (start|stop|restart|status) <storm service> \
 <args...>"

PATH_BIN=`dirname "${BASH_SOURCE-$0}"`

STORM_PATH=$PATH_BIN/..
if [ $# -le 1 ]; then
	echo $usage
	exit 1
fi

COMMAND=$1
SERVICE=$2

case $COMMAND in
	status)
		PROCESS_ID=`jps | grep $SERVICE | awk '{print $1}'`
		if [ -n "$PROCESS_ID" ];then
			echo $SERVICE running as process $PROCESS_ID.
			exit 0;
		else
			echo $SERVICE not running.
			exit 1;
    	fi
		;;
	stop)
        jps | grep $SERVICE | awk '{print "kill -9 " $1}' | sh
		;;
	start)
		$PATH_BIN/storm $SERVICE
esac
