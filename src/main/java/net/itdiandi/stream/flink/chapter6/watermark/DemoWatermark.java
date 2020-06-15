package net.itdiandi.stream.flink.chapter6.watermark;

import net.itdiandi.stream.flink.chapter5.kursk.ElecMeterReading;
import net.itdiandi.stream.flink.chapter5.kursk.ElecMeterSource;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DemoWatermark {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(5000);

        DataStream<ElecMeterReading> elecReadings = env
                .addSource(new ElecMeterSource())
                .assignTimestampsAndWatermarks(new MyNewWatermark());

        env.execute();

    }

    public static class MyWater implements AssignerWithPeriodicWatermarks<ElecMeterReading>{

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println("watermark " +dtf.format(now) + " and watermark's ts is ");
            return null;
        }

        @Override
        public long extractTimestamp(ElecMeterReading element, long previousElementTimestamp) {
            return 0;
        }
    }
}
