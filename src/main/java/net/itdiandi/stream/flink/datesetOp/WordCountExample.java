package net.itdiandi.flink.datesetOp;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCountExample {
    public static void main(String[] args) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(100);

        DataSet<String> text = env.fromElements(
                "Who's there?",
                "I think I hear them. Stand, ho! Who's there?",
                "I think I hear them. Stand, ho! Who's there?");

        DataSet<Tuple2<String, Integer>> wordCounts = text
                .flatMap(new LineSplitter())
                .groupBy(0)
                .sum(1);

        wordCounts.print();
        
        System.out.println(text.flatMap(new LineSplitter()).collect());
    }

    public static class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
        public void flatMap(String line, Collector<Tuple2<String, Integer>> out) {
            for (String word : line.split(" ")) {
              //  for (String word2 : line.split(",")) {
                    out.collect(new Tuple2<>(word, 1));
           //     }
            }
        }
    }
}