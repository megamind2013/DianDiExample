package dim

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, SaveMode, SparkSession}

/**
  * 按维度展开统计指定维度组合的数据
  */
object DimensionExpansion {

//  org.apache.log4j.LogManager.resetConfiguration();

//  PropertyConfigurator.configure("/Workspace/adx/stat/stat/src/main/resources/log4j.properties");

  /** paramChecked */
  def main(args: Array[String]): Unit = {
    // 维度
    val dimensions:String = """test,total,app_id,total
                               test,publisher_id,total,total"""

    val columnNames = Array[String]("test","publisher_id","app_id","slot_id")

    val dimensionArray:Array[Array[String]] =  dimensions.split("\n").map(f=>f.trim.split(",").map(_.trim))

     var adEffectDimensionColumnArray = dimensionArray.map(f=> struct(f.zipWithIndex.map(f=>
       if(f._1=="total")
         lit("total").name(columnNames(f._2))
       else
         col(f._1).name(columnNames(f._2))
     ):_*))

    var adEffectDimensionColumn:Column=array(adEffectDimensionColumnArray:_*)

    val confa = SparkSession.builder().appName("Dimension").master("local[1]")

    confa.config("hive.execution.engine","tez")

    val spark = confa.getOrCreate()
    import spark.implicits._



    spark.sql("create database adx_app")
    spark.sql("create table aa(test varchar(128), publisher_id varchar(128), app_id varchar(128),slot_id varchar(128))")

    // 模拟数据
    val df = Seq(
      (0, "pub236088034304", "app258216147648","s258223597568"),
      (0, "pub236088034304", "app258216147648","s308112478528"),
      (0, "pub236088034304", "app258216147648","s347247264384"),

      (0, "pub236088034304", "app263722909504","s347248513408"),
      (0, "pub236088034304", "app263722909504","s347249271424"),
      (0, "pub236088034304", "app263722909504","s374393761984"),

      (0, "pub236088034305", "app258216147649","s258223597569"),
      (0, "pub236088034305", "app258216147649","s308112478529"),
      (0, "pub236088034305", "app258216147649","s347247264385"),

      (0, "pub236088034305", "app263722909505","s347248513409"),
      (0, "pub236088034305", "app263722909505","s347249271425"),
      (0, "pub236088034305", "app263722909505","s374393761985")
    ).toDF("test", "publisher_id", "app_id","slot_id")

    var df1=df.withColumn("dims",adEffectDimensionColumn)

    df1.show(100,false)

    var df2 = df1.select(explode($"dims").as("dim"),$"publisher_id",$"app_id",$"slot_id")

    df2.show(100,false)

    df2.groupBy("dim").agg(
      count("*").as("countNumber")
    ).select(
      col("dim")("test").as("test"),
      col("dim")("publisher_id").as("publisher_id"),
      col("dim")("app_id").as("app_id"),
      col("dim")("slot_id").as("slot_id"),
      $"countNumber"
    )
      .write.format("hive").mode(SaveMode.Append).saveAsTable("adx_app.aa")
  }
}
