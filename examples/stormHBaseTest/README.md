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
storm jar storm-maprdbtable-tests-1.0-jar-with-dependencies.jar storm.example.PersistentWordCount WordCount hbase-example
```

Use the `hbase shell` to check data at table.

```
hbase(main):012:0> scan 'z'
ROW                                                                  COLUMN+CELL
 apple                                                               column=cf:count, timestamp=1412767233223, value=\x00\x00\x00\x00\x00\x00\x0C\xD3
 apple                                                               column=cf:name, timestamp=1412767233222, value=apple
 banana                                                              column=cf:count, timestamp=1412767233227, value=\x00\x00\x00\x00\x00\x00\x0C\xA6
 banana                                                              column=cf:name, timestamp=1412767233223, value=banana
 orange                                                              column=cf:count, timestamp=1412767233216, value=\x00\x00\x00\x00\x00\x00\x0C\x98
 orange                                                              column=cf:name, timestamp=1412767233216, value=orange
 pineapple                                                           column=cf:count, timestamp=1412767233210, value=\x00\x00\x00\x00\x00\x00\x0C\xBB
 pineapple                                                           column=cf:name, timestamp=1412767233210, value=pineapple
 watermelon                                                          column=cf:count, timestamp=1412767233221, value=\x00\x00\x00\x00\x00\x00\x0C\xB7
 watermelon
 ```
 Please do the same for MaprDB tables. Please replace table name 'WordCount' to '/WordCount'.
 
 ```
 HBase and MaprDB spout examples:
 1. Generate dummy data and store to the table(hbase or maprdb):
 ```
 java -cp /tmp/storm-maprdbtable-tests-1.0-jar-with-dependencies.jar storm.example.TableValuesGenerator /WordCount
 ```
 
 2. Run:
 ```
 storm jar storm-maprdbtable-tests-1.0-jar-with-dependencies.jar storm.example.SpoutHbaseTopology /WordCount maprdb-spout
 ```
