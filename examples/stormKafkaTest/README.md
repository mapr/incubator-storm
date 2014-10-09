Pre-requisites
==============

You should install kafka and run it with queue ololo.

Run producer with stream of data: 
```
(for ((i=0; ; i++)); do sleep 1; echo "message #"$i; done;) | bin/kafka-console-producer.sh --broker-list localhost:9092 --topic ololo
```

Test Storm with RabbitMQ
========================

Build project:
```
$ mvn package
```

Run storm topology, which waiting for messages and when they appear, displays the contents:
```
$ storm jar storm-kafka-test-1.0-jar-with-dependencies.jar storm.example.KafkaTestTopology kafka-topology
```
