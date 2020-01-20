package net.itdiandi.spark.sql.basic

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row

object WordCount {
  def main(args: Array[String]): Unit = {

//    val schema = StructType(Seq(
//      StructField("line", StringType)
//    ))
//
//    val spark = SparkSession.builder().appName(this.getClass.getSimpleName).master("local").getOrCreate()
//    import spark.implicits._
//    val linesDF = spark.sparkContext.textFile("src/main/resources/data/words").toDF("line")
//    linesDF.show(false)
//    linesDF.printSchema()
//    //将一行数据展开
////    val wordsDF = linesDF.explode("line", "word")((line: String) => line.split(" "))
//    val wordsDF = linesDF.flatMap((line: String) => line.split(" "))
//    // ds.select(explode(split('words, " ")).as("word"))
//    wordsDF.printSchema()
//    wordsDF.show(200,false)
//    //对 "word"列进行聚合逻辑并使用count算子计算每个分组元素的个数
//    val wordCoungDF = wordsDF.groupBy("word").count()
//    wordCoungDF.show(false)
//    wordCoungDF.printSchema()
//    println(wordCoungDF.count() + "----------")
  }
}
