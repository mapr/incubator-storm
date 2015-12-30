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

