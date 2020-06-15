package net.itdiandi.flink.stream;

import com.google.common.collect.Lists;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.windowing.AggregateApplyAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//这个类是测试source产生流数据,然后做一些通用操作
public class NumberStreamDemo {
    public static void main(String[] args) throws Exception {

        //no paralleSource 流中的wordcount窗口
        test1();

        //paralleSource
        test2();

        //richParalleSource
        // test3();

        //from Collection
        //test4();

        //filter
        //test5();

        //multi stream source union
        //test6();

        //two stream source connect
        //test7();

        // split 根据规则把一个数据流切分为多个流，select和split配合使用，选择切分后的流
        // test8();

        // 自定义分区需要实现Partitioner接口
        // test9();


    }

    private static void test9() throws Exception {

        //dataStream.partitionCustom(partitioner, “someKey”) 针对对象
        //dataStream.partitionCustom(partitioner, 0) 针对Tuple

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource());

        //对数据进行转换，把long类型转成tuple1类型
        DataStream<Tuple1<Long>> tupleData = text.map(new MapFunction<Long, Tuple1<Long>>() {
            @Override

            public Tuple1<Long> map(Long value) throws Exception {
                return new Tuple1<>(value);
            }
        });

        //分区之后的数据
        //一条线程一个task，分别处理奇数，偶数
        DataStream<Tuple1<Long>> partitionData = tupleData.partitionCustom(new MyPartition(), 0);
        DataStream<Long> result = partitionData.map(new MapFunction<Tuple1<Long>, Long>() {
            @Override
            public Long map(Tuple1<Long> value) throws Exception {
                System.out.println("当前线程id：" + Thread.currentThread().getId() + ",value: " + value);
                return value.getField(0);
            }
        });

        result.print().setParallelism(1);
        env.execute("NumberStreamDemo");
    }

    private static void test8() throws Exception {

        // split 根据规则把一个数据流切分为多个流，select和split配合使用，选择切分后的流

        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取数据源
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource()).setParallelism(1);//注意：针对此source，并行度只能设置为1

        //对流进行切分，按照数据的奇偶性进行区分
        SplitStream<Long> splitStream = text.split(new OutputSelector<Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Iterable<String> select(Long value) {
                List<String> outPut = Lists.newArrayList();
                if (value % 2 == 0) {
                    outPut.add("even"); //偶数
                } else {
                    outPut.add("odd"); //奇数
                }
                return outPut;
            }
        });

        //选择一个或者多个切分后的流
        DataStream<Long> evenStream = splitStream.select("even");
        DataStream<Long> oddStream = splitStream.select("odd");

        DataStream<Long> moreStream = splitStream.select("odd", "even");

        //打印结果
        evenStream.print().setParallelism(1);
        String jobName = NumberStreamDemo.class.getSimpleName();
        env.execute(jobName);
    }

    private static void test7() throws Exception {
        //Connect：和union类似，但是只能连接两个流，两个流的数据类型可以不同，会对两个流中的数据应用不同的处理方法。
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //获取数据源
        DataStreamSource<Long> text1 = env.addSource(new MyNoParalleSource()).setParallelism(1);//注意：针对此source，并行度只能设置为1
        DataStreamSource<Long> text2 = env.addSource(new MyNoParalleSource()).setParallelism(1);

        SingleOutputStreamOperator<String> text2_str = text2.map(new MapFunction<Long, String>() {
			private static final long serialVersionUID = 1L;

			@Override
            public String map(Long value) throws Exception {
                return "str_" + value;
            }
        });

        ConnectedStreams<Long, String> connectedStreams = text1.connect(text2_str);
        SingleOutputStreamOperator<Object> result = connectedStreams.map(new CoMapFunction<Long, String, Object>() {

			private static final long serialVersionUID = 1L;

			@Override
            public Object map1(Long value) throws Exception {
                return value;
            }

            @Override
            public Object map2(String value) throws Exception {
                return value;
            }
        });

        //打印结果
        result.print().setParallelism(1);
        String jobName = NumberStreamDemo.class.getSimpleName();
        env.execute(jobName);
    }

    private static void test6() throws Exception {
        // Union：合并多个流，新的流会包含所有流中的数据，但是union是一个限制，就是所有合并的流类型必须是一致的。
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取数据源
        DataStreamSource<Long> text1 = env.addSource(new MyNoParalleSource()).setParallelism(1);//注意：针对此source，并行度只能设置为1

        DataStreamSource<Long> text2 = env.addSource(new MyNoParalleSource()).setParallelism(1);

        //把text1和text2组装到一起
        DataStream<Long> text = text1.union(text2);

        DataStream<Long> num = text.map(new MapFunction<Long, Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Long map(Long value) throws Exception {
                System.out.println("原始接收到数据：" + value);
                return value;
            }
        });

        //每2秒钟处理一次数据
        DataStream<Long> sum = num.timeWindowAll(Time.seconds(2)).sum(0);
        //打印结果
        sum.print().setParallelism(1);
        String jobName = NumberStreamDemo.class.getSimpleName();
        env.execute(jobName);
    }

    private static void test5() throws Exception {
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //获取数据源
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource()).setParallelism(1);//注意：针对此source，并行度只能设置为1
        DataStream<Long> num = text.map(new MapFunction<Long, Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Long map(Long value) throws Exception {
                System.out.println("原始接收到数据：" + value);
                return value;
            }
        });
        //执行filter过滤，满足条件的数据会被留下
        DataStream<Long> filterData = num.filter(new FilterFunction<Long>() {
			private static final long serialVersionUID = 1L;

			//把所有的奇数过滤掉
            @Override
            public boolean filter(Long value) throws Exception {
                return value % 2 == 0;
            }
        });

        DataStream<Long> resultData = filterData.map(new MapFunction<Long, Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Long map(Long value) throws Exception {
                System.out.println("过滤之后的数据：" + value);
                return value;
            }
        });

        //每2秒钟处理一次数据
        DataStream<Long> sum = resultData.timeWindowAll(Time.seconds(2)).sum(0);

        //打印结果
        sum.print().setParallelism(1);

        String jobName = NumberStreamDemo.class.getSimpleName();
        env.execute(jobName);

    }


    private static void test4() throws Exception {
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        ArrayList<Integer> data = new ArrayList<>();
        data.add(10);
        data.add(15);
        data.add(20);

        //指定数据源
        DataStreamSource<Integer> collectionData = env.fromCollection(data);
        //通map对数据进行处理
        DataStream<Integer> num = collectionData.map(new MapFunction<Integer, Integer>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Integer map(Integer value) throws Exception {
                return value + 1;
            }
        });

        num.print().setParallelism(1);
        env.execute("Streaming From Collection");
    }

    private static void test3() throws Exception {
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取数据源
        DataStreamSource<Long> text = env.addSource(new MyRichParalleSource()).setParallelism(1);

        DataStream<Long> num = text.map(new MapFunction<Long, Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Long map(Long value) throws Exception {
                System.out.println("接收到数据：" + value);
                return value;
            }
        });

        //每2秒钟处理一次数据
        DataStream<Long> sum = num.timeWindowAll(Time.seconds(2)).sum(0);

        //打印结果
        sum.print().setParallelism(1);

        String jobName = NumberStreamDemo.class.getSimpleName();
        env.execute(jobName);

    }

    private static void test2() throws Exception {
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //获取数据源
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource()).setParallelism(1); //注意：针对此source，并行度只能设置为1
        DataStream<Long> num = text.map(new MapFunction<Long, Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Long map(Long value) throws Exception {
                System.out.println("接收到数据：" + value);
                return value;
            }
        });
        //每2秒钟处理一次数据
        DataStream<Long> sum = num.timeWindowAll(Time.seconds(2)).sum(0);
        //打印结果
        sum.print().setParallelism(1);
        String jobName = NumberStreamDemo.class.getSimpleName();
        env.execute(jobName);
    }

    private static void test1() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //DataStream<Long> someIntegers = env.generateSequence(0, 1000);
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource()).setParallelism(1);
        DataStream<Long> num = text.map(new MapFunction<Long, Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public Long map(Long value) throws Exception {
                return value;
            }
        });

        DataStream<TestBean> testBeanStream = num.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long value) throws Exception {
                if (value % 2 == 0) {
                    return true;
                }
                return false;
            }
        }).map(new MapFunction<Long, TestBean>() {
			private static final long serialVersionUID = 1L;

			@Override
            public TestBean map(Long value) throws Exception {
                System.out.println("接受到数据：" + new TestBean("a", value, 1L).toString());
                return new TestBean("a", value, 1L);
            }
        });
        DataStream<TestBean> testBeanStream2 = num.filter(new FilterFunction<Long>() {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean filter(Long value) throws Exception {
                if (value % 2 != 0) {
                    return true;
                }
                return false;
            }
        }).map(new MapFunction<Long, TestBean>() {
			private static final long serialVersionUID = 1L;

			@Override
            public TestBean map(Long value) throws Exception {
                System.out.println("接受到数据：" + new TestBean("b", value, 1L).toString());
                return new TestBean("b", value, 1L);
            }
        });

        testBeanStream.union(testBeanStream2).keyBy("word").timeWindow(Time.seconds(2), Time.seconds(10))
                .reduce(new ReduceFunction<TestBean>() {
					private static final long serialVersionUID = 1L;

					@Override
                    public TestBean reduce(TestBean first, TestBean second) throws Exception {
                        return new TestBean(first.word, (first.value + second.value) / (first.count + second.count), first.count + second.count);
                    }
                })
//                .sum("value")
                .print();

        String jobName = NumberStreamDemo.class.getSimpleName();
        env.execute(jobName);
    }

    public static class TestBean {

        public String word;
        public long value;
        public long count;//1

        public TestBean() {
        }

        public TestBean(String word, long value, long count) {
            this.word = word;
            this.count = count;
            this.value = value;
        }

        @Override
        public String toString() {
            return "TestBean{" +
                    "word='" + word + '\'' +
                    ", value=" + value +
                    ", count=" + count +
                    '}';
        }

    }

}
