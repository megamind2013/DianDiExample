package net.itdiandi.sql.basic

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row

object StructExample {
  def main(args: Array[String]): Unit = {
    val schema = StructType(List(
      StructField("integer_column", IntegerType, nullable = false),
      StructField("string_column", StringType, nullable = true)
//      ,StructField("res", StructType(
//        StructField("name",StringType,true),
//        StructField("age",IntegerType,true)
//      ),nullable = true)
    ))

    val spark:SparkSession = SparkSession.builder().appName("udafDemo").master("local[1]")
      //      .enableHiveSupport()
      .getOrCreate()

//    val rdd = spark.sparkContext.parallelize(Seq(
//      Row(1, "First Value", java.sql.Date.valueOf("2010-01-01")),
//      Row(2, "Second Value", java.sql.Date.valueOf("2010-02-01"))
//    ))
//    val df = spark.createDataFrame(rdd, schema)

    // newDF = newDF.select($"mod", $"res" ("count").as("count"),$"res" ("bitmap").as("bitmap"))
    //    newDF = newDF.select($"mod", $"res.count".as("count"),$"res.bitmap".as("bitmap"))

//    df.printSchema()
  }
}
