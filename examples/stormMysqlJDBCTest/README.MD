Build project:
========================

Use the `mysql shell` command to create database and table:
> create database test;

> use test;

> create table if not exists user (user_id integer, user_name varchar(100), dept_name varchar(100), create_date date);

> create table if not exists department (dept_id integer, dept_name varchar(100));

> create table if not exists user_department (user_id integer, dept_id integer);

> insert into department values (1, 'R&D');

> insert into department values (2, 'Finance');

> insert into department values (3, 'HR');

> insert into department values (4, 'Sales');

> insert into user_department values (1, 1);

> insert into user_department values (2, 2);

> insert into user_department values (3, 3);

> insert into user_department values (4, 4);

**!!!IMPORTANT!!!** create database before mvn package

```
$ mvn clean package -DdatabaseName="test"
```

Run the 
```
storm jar target/storm-JDBC-test-1.0-SNAPSHOT-jar-with-dependencies.jar storm.example.MysqlJDBCTopology [datasource url] [user] [password] [remote]
```

**use your user and password**

***Example*** `storm jar storm-JDBC-test-1.0-SNAPSHOT-jar-with-dependencies.jar storm.example.MysqlJDBCTopology jdbc:mysql://localhost/test root 123456`


Use the `mysql shell` to check data at table.

> use test;

> select * from user;


| user_id   | user_name  | dept_name   | create_date   |
| --------- | -----------| ----------- | ------------- |
|       1   | peter      | R&D         | 2015-12-23    |
|       1   | peter      | R&D         | 2015-12-23    |
|       1   | peter      | R&D         | 2015-12-23    |
|       1   | peter      | R&D         | 2015-12-23    |
|       2   | bob        | Finance     | 2015-12-23    |
|       1   | peter      | R&D         | 2015-12-23    |
|       2   | bob        | Finance     | 2015-12-23    |
|       2   | bob        | Finance     | 2015-12-23    |
|       2   | bob        | Finance     | 2015-12-23    |
|       2   | bob        | Finance     | 2015-12-23    |
|       1   | peter      | R&D         | 2015-12-23    |
|     ...   | ...        | ...         | ...           |




