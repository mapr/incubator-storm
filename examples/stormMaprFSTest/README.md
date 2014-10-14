Build project:
========================
```
$ mvn clean package
```
 
 MaprFS spout example:
 ==========================================
 ```
$ /opt/mapr/storm/storm-0.9.3/bin/storm jar /opt/mapr/storm/storm-0.9.3/examples/stormMaprFSTest/target/storm-maprfs-tests-1.0-jar-with-dependencies.jar main.java.storm.example.MaprFSTopology TestSpout
 ```