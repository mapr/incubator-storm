Build storm-hbase jar from https://github.com/ptgoetz/storm-hbase and add to project (check the version)
```
mvn install:install-file -Dfile=storm-hbase-0.1.3-SNAPSHOT.jar -DgroupId=com.github.ptgoetz -DartifactId=storm-hbase -Dversion=0.1.3 -Dpackaging=jar
```

Build project:
```
$ mvn clean package
```

Use the `hbase shell` command to create the schema:
```
> create 'WordCount', 'cf'
```

Run the 
```
storm jar stormHBaseTest-1.0-SNAPSHOT-jar-with-dependencies.jar -cp "stormHBaseTest-1.0-SNAPSHOT-jar-with-dependencies.jar:/opt/mapr/lib/*" storm.example.PersistentWordCount maprfs:///hbase
```
(it will run the topology for 10 seconds, then exit).

After (or while) the word count topology is running, run the 
```
java -cp 'stormHBaseTest-1.0-SNAPSHOT-jar-with-dependencies.jar' storm.example.WordCountClient
```
to view the counter values stored in HBase. You should see something like to following:
```
Word: 'apple', Count: 6867
Word: 'orange', Count: 6645
Word: 'pineapple', Count: 6954
Word: 'banana', Count: 6787
Word: 'watermelon', Count: 6806
```
