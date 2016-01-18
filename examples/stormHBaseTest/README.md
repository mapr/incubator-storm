Build project:
========================
```
$ mvn clean package
```

WARN:
If cluster security is Kerberos, then follow next four steps: <br />
1.``` cp /opt/mapr/hbase/hbase-VERSION/conf/hbase-site.xml /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/``` <br />
2. <br />On YARN:<br /> ``` cp /opt/mapr/hadoop/hadoop-VERSION/etc/hadoop/core-site.xml /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/ ``` <br />
   On Classic: <br /> ```cp /opt/mapr/hadoop/hadoop-VERSION/conf/core-site.xml /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/ ``` <br />
3. Add <br />
```
<property>
  <name>hadoop.login</name>
  <value>kerberos</value>
</property>
```
to /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/core-site.xml<br />
4.Add HBase xml config to topology jar<br />
```
zip -j /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/target/storm-maprdbtable-tests-1.0-jar-with-dependencies.jar /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/core-site.xml
zip -j /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/target/storm-maprdbtable-tests-1.0-jar-with-dependencies.jar /opt/mapr/storm/storm-VERSION/examples/stormHBaseTest/hbase-site.xml
```
<br />
Use the `hbase shell` command to create the schema:
```
> create 'WordCount', 'cf'
```

Run the 
```
storm jar storm-maprdbtable-tests-1.0-jar-with-dependencies.jar storm.example.PersistentWordCount WordCount localhost hbase-example
```
Where localhost stands for Zookeeper hostname.

Use the `hbase shell` to check data at table.

```
hbase(main):012:0> scan 'WordCount'
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
 
 
 HBase and MaprDB spout examples:
 ==========================================

 1. Generate dummy data and store to the table(hbase or maprdb):
 ```
 java -cp storm-maprdbtable-tests-1.0-jar-with-dependencies.jar storm.example.TableValuesGenerator /WordCount localhost
 ```
 Where localhost stands for Zookeeper hostname.
 2. Run:
 ```
 storm jar storm-maprdbtable-tests-1.0-jar-with-dependencies.jar storm.example.SpoutHbaseTopology /WordCount localhost maprdb-spout
 ```
Where localhost stands for Zookeeper hostname.
Work with HBase 0.94
=====================

In `pom.xml` uncomment following lines:
```
        <!--=== For HBase 0.94 ====-->
        <!--      <dependency>
                  <groupId>org.apache.hbase</groupId>
                  <artifactId>hbase</artifactId>
                  <version>0.94.17-mapr-1403-m7-3.1.1</version>
                  <exclusions>
                      <exclusion>
                          <groupId>org.slf4j</groupId>
                          <artifactId>slf4j-log4j12</artifactId>
                      </exclusion>
                  </exclusions>
              </dependency>
        
              <dependency>
                  <groupId>org.json</groupId>
                  <artifactId>json</artifactId>
                  <version>20090211</version>
              </dependency>-->
        <!--===========-->
```
Work with HBase 1.1
=====================

In `pom.xml` uncomment following lines:
```
        <!--=== For HBase 1.1 ====-->
        <!--      <dependency>
                  <groupId>org.apache.hbase</groupId>
                  <artifactId>hbase-client</artifactId>
                  <version>1.1.1-mapr-1508-SNAPSHOT</version>
                  <exclusions>
                      <exclusion>
                          <groupId>org.slf4j</groupId>
                          <artifactId>slf4j-log4j12</artifactId>
                      </exclusion>
                  </exclusions>
              </dependency>

              <dependency>
                  <groupId>org.json</groupId>
                  <artifactId>json</artifactId>
                  <version>20090211</version>
              </dependency>-->

              change  <url>http://repository.mapr.com/maven/</url>
              to <url>http://repository_cv.mapr.com/maven/</url>
        <!--===========-->
```


And comment out the following lines:
```
      <dependency>
          <groupId>org.apache.hbase</groupId>
          <artifactId>hbase-client</artifactId>
          <version>0.98.4-mapr-1408-m7-4.0.1</version>
          <exclusions>
              <exclusion>
                  <groupId>org.slf4j</groupId>
                  <artifactId>slf4j-log4j12</artifactId>
              </exclusion>
          </exclusions>
      </dependency>
```

