package net.itdiandi.sql.udf.udaf

import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import net.itdiandi.spark.sql.udf.udaf._
import net.itdiandi.spark.sql.udf.utils._
/**
  *
  */
object Main {
  def main(args: Array[String]): Unit = {
    import org.apache.log4j.PropertyConfigurator
//    PropertyConfigurator.configure("/Workspace/Learn/Flink/SparkDemo/src/main/resources/log4j.properties")

    val spark:SparkSession = SparkSession.builder().appName("udafDemo").master("local[1]")
//      .enableHiveSupport()
      .getOrCreate()

    import spark.implicits._

    /**
      * UserDefinedAggregateFunctionExample简单示例
      * 求平均值
      */
//    spark.read.json("src/main/resources/data/user").createOrReplaceTempView("v_user")
//    spark.udf.register("u_avg", UserDefinedAggregateFunctionExample)
//    // 将整张表看做是一个分组对求所有人的平均年龄
//    spark.sql("select count(1) as count, u_avg(age) as avg_age from v_user").show()
//    // 按照性别分组求平均年龄
//    spark.sql("select sex, count(1) as count, u_avg(age) as avg_age from v_user group by sex").show()


    /**
      * 简单示例
      * 实现带类型udap  有问题，没测通
      */
//
//    val user = spark.read.json("src/main/resources/data/user").as[User]
//
//    user.printSchema()
//    user.show(false)
//    user.select(AggregatorExample.toColumn.name("avg")).show()




    /**
      * UserDefinedAggregateFunctionBitMapExample结合BitMap一起使用
      * 统计uv
      */
//    val dataframe = spark.range(start = 0, end = 1000, step = 1, numPartitions = 1).withColumn("mod", 'id % 2)
//    dataframe.show(100, false)
//
//    val mycount = UserDefinedAggregateFunctionBitMapExample
//    var newDF = dataframe.groupBy($"mod").agg(mycount($"id").as("res"))
//
//    newDF.printSchema()
//    newDF.show(false)
//    //     newDF = newDF.select($"mod", $"res" ("count").as("count"),$"res" ("bitmap").as("bitmap"))
//    newDF = newDF.select($"mod", $"res.count".as("count"),$"res.bitmap".as("bitmap"))
//
//    newDF.printSchema()
//
//    newDF.show(false)


    /**
      * UserDefinedAggregateFunctionHyperLogLogExample结合hyperloglog一起使用
      * 统计uv
      */
    def hyperLogLog = new UserDefinedAggregateFunctionHyperLogLogExample(0.01)

    var user = spark.read.json("src/main/resources/data/user")

//    user
//      .groupBy("name")
//      .agg(hyperLogLog($"id").name("res"))
//      .select($"name",$"res" ("count").as("uv_count"), $"res" ("hll").as("hll"))
//      .write.save("/Data/hyperLogLog/user")

    def mergeHyperLogLog = new MergeHyperLogLogUDAF

    var oldDF = spark.read.parquet("/Data/hyperLogLog/user")

    var newDF = user
      .groupBy("name")
      .agg(hyperLogLog($"id").name("res"))
      .select($"name",$"res" ("count").as("uv_count"), $"res" ("hll").as("hll"))

    var tempDF = newDF.join(oldDF,oldDF("name")===newDF("name"),"full")
    tempDF.show()
  }
}
