Build project:
```
$ mvn clean package
```

Run topology:
```
$ storm jar stormIngestionTest-1.0-SNAPSHOT.jar storm.example.StormIngestionTestTopology
```

In $STORM_HOME/logs look at `metrics.log` file:
```
$ tail -f metrics.log
```

You might see something like that:
```
....
2014-09-26 14:39:33,439 11178 1411731573 server-pc:6701 4:byteBolt execBytesReceiverBolt 480717
2014-09-26 14:39:34,439 12178 1411731574 server-pc:6701 4:byteBolt execBytesReceiverBolt 482857
2014-09-26 14:39:35,441 13180 1411731575 server-pc:6701 4:byteBolt execBytesReceiverBolt 469177
....
```

Last number is count of call execute() method in execBytesReceiverBolt.