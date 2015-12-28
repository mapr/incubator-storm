Build project:
========================
```
$ mvn clean package
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



