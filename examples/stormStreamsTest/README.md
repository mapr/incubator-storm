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

 Make sure that kafka-clients-0.9.0.0 MapR library is placed under ${STORM_HOME}/lib/ folder. If not, make a symbolic link:

 ```
 ln -s /opt/mapr/lib/kafka-clients-0.9.0.0* ${STORM_HOME}/lib/
 ```

 Submit topology

 ```
 usage: storm jar [zookeeperHost:port] [bootstrap server host:port] [use kafka 09 API: true/false] [topic to read from] [topic to write count] [topic to send duplicated source data]
 ```
 Example:
 ```
 storm jar storm-streams-test-1.0-jar-with-dependencies.jar storm.example.DemoTopology localhost:5181 localhost:9092 true /streaming_data/marlin:events /streaming_data/marlin:average /streaming_data/marlin:trash
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


 To work with Kafka create read-topic and write-topics in Kafka.

  Kafka 0.9+:
  Run topology as previously.

  Kafka 0.9 or less:

  Remove 0.9 Kafka library from ${STORM_HOME}/lib directory (kafka-clients-0.9.0.0*mapr*.jar)

  Change
 ```
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka_2.10</artifactId>
  <version>0.9.0.0</version>
  ...
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>0.9.0.0</version>
 ```
   to
```
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka_2.10</artifactId>
  <version>0.8.2.2</version>
  ...
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>0.8.2.2</version>
```

 Run
 ```
 mvn package
 ```

  Change "true" in topology run signature run code to "false":

```
storm jar storm-streams-test-1.0-jar-with-dependencies.jar storm.example.DemoTopology localhost:5181 localhost:9092 false events average trash
```