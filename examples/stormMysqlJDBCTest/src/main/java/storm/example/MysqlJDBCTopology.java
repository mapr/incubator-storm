package storm.example;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.SubmitOptions;
import backtype.storm.generated.TopologyInitialStatus;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.bolt.JdbcLookupBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcLookupMapper;
import backtype.storm.LocalCluster;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class MysqlJDBCTopology {

    private static final String USER_SPOUT = "USER_SPOUT";
    private static final String LOOKUP_BOLT = "LOOKUP_BOLT";
    private static final String PERSISTANCE_BOLT = "PERSISTANCE_BOLT";


    protected static final String TABLE_NAME = "user";
    protected static final String JDBC_CONF = "jdbc.conf";
    protected static final String SELECT_QUERY = "select dept_name from department, user_department where department.dept_id = user_department.dept_id" +
            " and user_department.user_id = ?";

    public static void main(String[] args) throws Exception {
        if (args.length != 3 && args.length < 4) {
            System.out.println("Usage: " + " <dataSource.url> "
                    + "<user> <password> [topology name]");
            System.exit(-1);
        }
        Map map = Maps.newHashMap();
        map.put("dataSourceClassName","com.mysql.jdbc.jdbc2.optional.MysqlDataSource");//com.mysql.jdbc.jdbc2.optional.MysqlDataSource
        map.put("dataSource.url", args[0]);//jdbc:mysql://localhost/test
        map.put("dataSource.user",args[1]);//root
        map.put("dataSource.password",args[2]);//password
        ConnectionProvider connectionProvider = new HikariCPConnectionProvider(map);
        connectionProvider.prepare();

        int queryTimeoutSecs = 60;
        JdbcClient jdbcClient = new JdbcClient(connectionProvider, queryTimeoutSecs);

        Config config = new Config();
        config.put(JDBC_CONF, map);

        UserSpout userSpout = new UserSpout();
        SimpleJdbcMapper jdbcMapper = new SimpleJdbcMapper(TABLE_NAME, connectionProvider);
        connectionProvider.cleanup();

        Fields outputFields = new Fields("user_id", "user_name", "dept_name", "create_date");
        List<Column> queryParamColumns = Lists.newArrayList(new Column("user_id", Types.INTEGER));
        SimpleJdbcLookupMapper jdbcLookupMapper = new SimpleJdbcLookupMapper(outputFields, queryParamColumns);
        JdbcLookupBolt departmentLookupBolt = new JdbcLookupBolt(connectionProvider, SELECT_QUERY, jdbcLookupMapper);

        //must specify column schema when providing custom query.
        List<Column> schemaColumns = Lists.newArrayList(new Column("create_date", Types.DATE),
                new Column("dept_name", Types.VARCHAR), new Column("user_id", Types.INTEGER), new Column("user_name", Types.VARCHAR));
        JdbcMapper mapper = new SimpleJdbcMapper(schemaColumns);

        JdbcInsertBolt userPersistanceBolt = new JdbcInsertBolt(connectionProvider, mapper)
                .withInsertQuery("insert into user (create_date, dept_name, user_id, user_name) values (?,?,?,?)");

        // userSpout ==> jdbcBolt
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(USER_SPOUT, userSpout, 1);
        builder.setBolt(LOOKUP_BOLT, departmentLookupBolt, 1).shuffleGrouping(USER_SPOUT);
        builder.setBolt(PERSISTANCE_BOLT, userPersistanceBolt, 1).shuffleGrouping(LOOKUP_BOLT);
        if (args.length <= 3) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("test", config, builder.createTopology());
            Thread.sleep(30000);
            cluster.killTopology("test");
            cluster.shutdown();
            System.exit(0);
        } else {
            StormSubmitter.submitTopologyAs(args[3], config, builder.createTopology(), new SubmitOptions(TopologyInitialStatus.ACTIVE), null, args[4]);
        }
    }
}