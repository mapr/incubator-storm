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
$ storm jar stormRabbitMqTest-1.0-SNAPSHOT-jar-with-dependencies.jar storm.example.RabbitMqTestTopology
```

Run the program that put the message 'Hello World!' in message queue:
```
$ java -cp stormRabbitMqTest-1.0-SNAPSHOT-jar-with-dependencies.jar storm.example.Send
```

or

Run the program that put the user defined message in message queue:
```
$ java -cp stormRabbitMqTest-1.0-SNAPSHOT-jar-with-dependencies.jar storm.example.Send Hike!
```
