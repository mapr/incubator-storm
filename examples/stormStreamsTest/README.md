Build project:
========================
```
$ mvn clean package
```

 MapR Streams spout and bolt example example:
 ==========================================


 Download marlin-jar-with-dependencies.jar from http://redmine.cybervisiontech.com/projects/mapr/wiki/MARLIN
 Do
 ```
 export MARLIN_CLASSPATH=`mapr classpath`
 ```
 Submit topology

 Run shell command:
 ```
 $ for ((i=0;;i++)); do (echo `date` " message $i" | hadoop fs -appendToFile - /test999.txt); sleep .5; done;
 ```

And submit topology:
 ```
$ storm jar /tmp/storm-maprfs-tests-1.0-jar-with-dependencies.jar storm.example.MaprFSTopology /test999.txt maprf-topology
 ```