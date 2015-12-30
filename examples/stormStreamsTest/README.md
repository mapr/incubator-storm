Build project:
========================
```
$ mvn clean package
```

 MapR Streams spout and bolt example example:
 ==========================================


 Download marlin-jar-with-dependencies.jar

 Do

```
From user mapr:

 maprcli volume create -name stream -replicationtype low_latency -path /streaming_data

 maprcli stream create -path /streaming_data/marlin

 maprcli stream topic create -path /streaming_data/marlin -topic events

 maprcli stream topic create -path /streaming_data/marlin -topic trash

 maprcli stream topic create -path /streaming_data/marlin -topic average

 maprcli stream edit -path /streaming_data/marlin -produceperm u:mapr g:mapr -consumeperm u:mapr g:mapr
```

 ```
 export MARLIN_CLASSPATH=`mapr classpath`
 ```
 Submit topology

 ```
 storm jar storm-streams-test-1.0-jar-with-dependencies.jar storm.example.DemoTopology localhost:5181 true
 ```

 Run Streams producer
 ```
 java -cp "$MARLIN_CLASSPATH:marlin-jar-with-dependencies.jar" -Djava.library.path=/opt/mapr/lib Main /streaming_data/marlin:events producer
 ```
 Run Streams consumer
 ```
 java -cp "$MARLIN_CLASSPATH:marlin-jar-with-dependencies.jar" -Djava.library.path=/opt/mapr/lib Main /streaming_data/marlin:average consumer
 ```


 Print any data into producer shell.

 Look to consumer shell, verify that it receives number of message sent.

 Delay between producer and consumer must be ~5 sec


 To work with Kafka create read-topic and write-topic in Kafka. And run topology as previously.
 For Kafka version <0.9 change "true" in topology run code to "false"
