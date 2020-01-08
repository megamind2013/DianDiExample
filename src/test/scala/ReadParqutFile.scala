//package com.opera.adx
//
//import org.apache.spark.sql.{DataFrame, SparkSession}
//import com.opera.adx.utils.UDFTools._
//import org.apache.spark.sql.DataFrame
//import org.apache.spark.sql.functions._
//import java.util.Properties
//
//import com.opera.adx.domain.WithRowNumberColumn
//import org.apache.spark.sql.SaveMode
//import org.apache.spark.sql.SparkSession
//
//
//object ReadParqutFile {
//  def main(args: Array[String]): Unit = {
//
//    val spark = SparkSession.builder().appName("ReadOrcFile").getOrCreate()
//
//    import spark.implicits._
//
//    var baseDF:DataFrame = spark.read.parquet("/apps/hive/warehouse/adx.db/log_adx_request/year=2019/month=07/week=29/day=20190722/*")
//
//    var requestDF = baseDF.withColumn("ipAddress",getAddressByIp($"ip"))
//      .where($"countryCode" === "IN").groupBy("countryCode","ipAddress.province").agg(count($"hashedOperaId").name("provinceNumber")).select($"countryCode",$"province",$"provinceNumber")
//
////    baseDF.show(10000,false)
////    requestDF.write.save("/user/sdev/ReadParqutFileqq/"+System.currentTimeMillis())
//
//    val testProp = new Properties
//    testProp.put("user", "adx")
//    testProp.put("password","adx")
//
//    requestDF.write.mode(SaveMode.Overwrite).option("driver","com.mysql.jdbc.Driver").jdbc("jdbc:mysql://172.17.31.251:3306/ad_bi?autoReconnect=true&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=UTF-8&useOldUTF8Behavior=true","tmp_in_ipaddress", testProp)
//
//  }
//
//
//}
