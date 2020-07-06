package net.itdiandi.flink;

import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;

public class ProducerTestData {
    public static void main(String[] args) throws Exception {
        // Correct to your local path.
        String sourceData = "/Workspace/Learn/DianDiExample/flink1.10/src/main/java/net/itdiandi/flink/kafka/id_cnt_data.csv";
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(1000, CheckpointingMode.AT_LEAST_ONCE);

        // using blink planner due to https://issues.apache.org/jira/browse/FLINK-16693
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        String sourceDDL = "CREATE TABLE csvSource (" +
                "  id VARCHAR," +
                "  cnt INT," +
                "  ts VARCHAR" +
                ") WITH (" +
                "'connector.type' = 'filesystem'," +
                "'connector.path' = '" + sourceData + "'," +
                "'format.type' = 'csv'" +
                ")";

        String sinkDDL = "CREATE TABLE kafkaSink (" +
                "  id VARCHAR," +
                "  cnt INT, " +
                "  ts TIMESTAMP(3)" +
                ") WITH (" +
                "'connector.type' = 'kafka'," +
                "'connector.version' = '0.10'," +
                "'connector.topic' = 'checkIdle'," +
                "'connector.properties.bootstrap.servers' = '10.7.6.25:9092'," +
                "'connector.sink-partitioner' = 'custom',"+
                "'connector.sink-partitioner-class' = 'net.itdiandi.flink.kafka.MakeIdlePartitioner'," +
                "'format.type' = 'json')";

        tEnv.sqlUpdate(sourceDDL);
        tEnv.sqlUpdate(sinkDDL);

        String sql = "INSERT INTO kafkaSink SELECT id, cnt, TO_TIMESTAMP(ts) as ts FROM csvSource";
        tEnv.sqlUpdate(sql);

        env.execute("IdleKafka");
    }
}
