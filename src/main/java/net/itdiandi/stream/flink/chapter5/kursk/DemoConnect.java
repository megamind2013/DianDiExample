package net.itdiandi.stream.flink.chapter5.kursk;

import net.itdiandi.stream.flink.chapter5.MultiStreamTransformations;
import net.itdiandi.stream.flink.chapter5.util.Alert;
import net.itdiandi.stream.flink.chapter5.util.SmokeLevel;
import net.itdiandi.stream.flink.util.SensorReading;
import net.itdiandi.stream.flink.util.SensorSource;
import net.itdiandi.stream.flink.util.SensorTimeAssigner;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;

public class DemoConnect {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<ElecMeterReading> elecReadings = env
                .addSource(new ElecMeterSource())
                .assignTimestampsAndWatermarks(new ElecMeterTimeAssigner());

        DataStream<SensorReading> tempReadings = env
                // SensorSource generates random temperature readings
                .addSource(new SensorSource())
                // assign timestamps and watermarks which are required for event time
                .assignTimestampsAndWatermarks(new SensorTimeAssigner());


        KeyedStream<ElecMeterReading, String> keyedElecReadings = elecReadings.keyBy(r -> r.getId());

        DataStream<Alert> alerts = keyedElecReadings
                .connect(tempReadings.broadcast())
                .flatMap(new RaiseElecAlertFlatMap());

        alerts.print();

        env.execute();
    }

    public static class RaiseElecAlertFlatMap implements CoFlatMapFunction<ElecMeterReading, SensorReading, Alert> {

        @Override
        public void flatMap1(ElecMeterReading value, Collector<Alert> out) throws Exception {
            if (value.getDayElecValue() > 90) {
                out.collect(new Alert(  value.getDayElecValue()+ "Day Elec value too high", value.getTimestamp()));
            }
        }

        @Override
        public void flatMap2(SensorReading value, Collector<Alert> out) throws Exception {

        }
    }
}


