package storm.example;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MysqlJDBCTest {

    private JdbcClient client;

    private static final String tableName = "user_details";
    private String dataSourceUrl;
    private String userName;
    private String password;
    @Before
    public void setup() {
        dataSourceUrl = System.getProperty("dataSourceUrl");
        userName = System.getProperty("userName");
        password = System.getProperty("password");
        Map map = Maps.newHashMap();
        map.put("dataSourceClassName","com.mysql.jdbc.jdbc2.optional.MysqlDataSource");//com.mysql.jdbc.jdbc2.optional.MysqlDataSource
        map.put("dataSource.url", dataSourceUrl);//jdbc:mysql://localhost/test
        map.put("dataSource.user", userName);//root
        map.put("dataSource.password", password );//password
        ConnectionProvider connectionProvider = new HikariCPConnectionProvider(map);
        connectionProvider.prepare();

        int queryTimeoutSecs = 60;
        this.client = new JdbcClient(connectionProvider, queryTimeoutSecs);
        client.executeSql("create table user_details (ID integer, USER_NAME varchar(100), SALARY integer)");
    }

    @Test
    public void testInsertAndSelect() {

        List<Column> row1 = createRow(1, "bob");
        List<Column> row2 = createRow(2, "alice");

        List<List<Column>> rows = Lists.newArrayList(row1, row2);
        client.insert(tableName, rows);

        List<List<Column>> selectedRows = client.select("select * from user_details where ID = ?", Lists.newArrayList(new Column("ID", 1, Types.INTEGER)));
        List<List<Column>> expectedRows = Lists.newArrayList();
        expectedRows.add(row1);
        Assert.assertEquals(expectedRows, selectedRows);

        List<Column> row3 = createRow(3, "frank");
        List<List<Column>> moreRows  = new ArrayList<List<Column>>();
        moreRows.add(row3);
        client.executeInsertQuery("insert into user_details values(?,?,?)", moreRows);

        selectedRows = client.select("select * from user_details where ID = ?", Lists.newArrayList(new Column("ID", 3, Types.INTEGER)));
        expectedRows = Lists.newArrayList();
        expectedRows.add(row3);
        Assert.assertEquals(expectedRows, selectedRows);


        selectedRows = client.select("select * from user_details order by ID", Lists.<Column>newArrayList());
        rows.add(row3);
        Assert.assertEquals(rows, selectedRows);
    }

    private List<Column> createRow(int id, String name) {
        return Lists.newArrayList(
                new Column("ID", id, Types.INTEGER),
                new Column("USER_NAME", name, Types.VARCHAR),
                new Column("SALARY",  200 , Types.INTEGER));
    }

    @After
    public void cleanup() {
        client.executeSql("drop table " + tableName);
    }
}