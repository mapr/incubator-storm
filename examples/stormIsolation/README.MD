Build project:
========================
```
$ mvn clean package
```

Run topology:
```
$ storm jar storm-isolation-test-1.0.jar storm.example.IsolationWordCountTopology [topologyName] [userName] <filelocation>
```