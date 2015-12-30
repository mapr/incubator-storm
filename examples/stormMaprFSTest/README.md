Build project:
========================
```
$ mvn clean package
```
 
 MaprFS spout example:
 ==========================================
 
 Run shell command:
 ```
 $ for ((i=0;;i++)); do (echo `date` " message $i" | hadoop fs -appendToFile - /test999.txt); sleep .5; done;
 ```
 
And submit topology:
 ```
$ storm jar storm-maprfs-tests-1.0-jar-with-dependencies.jar storm.example.MaprFSTopology /test999.txt maprf-topology
 ```