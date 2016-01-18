Build project:
========================
```
$ mvn clean package
```

WARN: <br/ >
If cluster security is Kerberos, then follow next four steps: <br />
1. ```cp /opt/mapr/hive/hive-<version>/conf/hive-site.xml /opt/mapr/storm/storm-<version>/examples/stormHiveTest/``` <br />
2. On YARN: <br /> ```cp /opt/mapr/hadoop/hadoop-<version>/etc/hadoop/core-site.xml /opt/mapr/storm/storm-<version>/examples/stormHiveTest/``` <br />
   On Classic: <br /> ```cp /opt/mapr/hadoop/hadoop-<version>/conf/core-site.xml /opt/mapr/storm/storm-<version>/examples/stormHiveTest/``` <br />
3. Add <br />
```
<property>
  <name>hadoop.login</name>
  <value>kerberos</value>
</property>
```
<br />
to ```/opt/mapr/storm/storm-<version>/stormHiveTest/examples/core-site.xml``` <br />
4. Add Hive config xml to topology jar<br />
```
zip -j /opt/mapr/storm/storm-<version>/examples/stormHiveTest/target/storm-hive-test-1.0-jar-with-dependencies.jar /opt/mapr/storm/storm-<version>/examples/stormHiveTest/core-site.xml
zip -j /opt/mapr/storm/storm-<version>/examples/stormHiveTest/target/storm-hive-test-1.0-jar-with-dependencies.jar /opt/mapr/storm/storm-<version>/examples/stormHiveTest/hbase-site.xml
```

Use the `hive shell` command to create the table:

```
use default;
```

```
create table test_table ( id INT, name STRING, phone STRING, street STRING) partitioned by (city STRING, state STRING) clustered by (id) into 2 buckets stored as orc tblproperties ("orc.compress"="NONE");
```

Run the 

```
storm jar target/storm-hive-test-1.0-jar-with-dependencies.jar storm.example.HiveTopology <metaStoreURI> <databaseName> <tableName> <topologyName> [path to Kerberos keytab] [Kerberos principal] 
```

**example for none Kerberos**

```
storm jar target/storm-hive-test-1.0-jar-with-dependencies.jar storm.example.HiveTopology thrift://localhost:9083 default test_table hive-topology
```

Use the `hive shell` to check data at table.

```
use default;
```

```
select * from test_table;
```

OK
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca
4	user4	123456	street4	san jose	ca
3	user3	123456	street3	san jose	ca



