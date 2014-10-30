Pre-requisites
==============

You will need install rabbitmq-server and run it.

Test Storm with RabbitMQ
========================

Build project:
```
$ mvn clean package
```

Run storm topology, which waiting for messages and when they appear, displays the contents:
```
$ storm jar storm-rabbitmq-test-1.0-SNAPSHOT-jar-with-dependencies.jar storm.example.RabbitMqTestTopology ololo
```

Run the program that put the message 'Hello World!' in message queue:
```
$ java -cp storm-rabbitmq-test-1.0-SNAPSHOT-jar-with-dependencies.jar storm.example.Send localhost ololo Hello\ World!    
```
