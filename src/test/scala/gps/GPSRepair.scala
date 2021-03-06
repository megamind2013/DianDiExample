//package gps
//
//import com.opera.adx.domain.Transformer
////import com.opera.adx.infra.{JedisClusterHelper, JedisClusterPlus}
//import com.opera.adx.sinks.RedisSink
//import com.opera.adx.utils.{DateUtils, RedisUtils}
//import org.apache.spark.sql._
//import org.apache.spark.sql.expressions.Window
//import org.apache.spark.sql.functions._
//import scala.util.parsing.json.JSONObject
//
///**
//  *
//  * spark-shell --master yarn --driver-memory 2G --jars ./adx_stat_2.11-0.2-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar
//  *
//  * import gps.GPSRepair._
//  *
//  * var array1= Array("adx_request","20191112","day")
//  *
//  * gRepair(spark,array1)
//  *
//  */
//
//
//
//object GPSRepair {
//
//  /**
//    *
//    * @param args
//    */
//  def main(args: Array[String]): Unit = {
//      val beginTimeMs = System.currentTimeMillis
//      val project = args(0)
//      val day = args(1)
//      val cycle = args(2)
//
//      val spark = SparkSession.builder().appName(s"${cycle}_stat_${project}_${day}").getOrCreate()
//
//    gRepair(spark,args)
//
////          redisConn.close()
//
//
//
////      var dataArray:ArrayBuffer[(String,Any)] = baseDF
////    res.addSink(new RedisSink(
////      Transformer.getRedisSinkBuilder("dmp").setExpirePeroid(30*24*3600).setPipelineSize(1000).finish
////    )).setParallelism(sinkParallelism)
//
////      curDF.repartition(1)
////        .write.mode(SaveMode.Overwrite).format("csv").save("/user/sdev/ng.txt")
//    }
//
//def gRepair(spark: SparkSession,args: Array[String]){
//    val beginTimeMs = System.currentTimeMillis
//    val project = args(0)
//    val day = args(1)
//    val cycle = args(2)
//
//    val month = day.substring(0, 6)
//    val week =  DateUtils.getWeekOfYearWithYear(day)
//    val transformer = Transformer(project)
//
//
//    val beginDay =
//      if(cycle == "range"){
//        args(3)
//      } else{
//        ""
//      }
//
//    val spark = SparkSession.builder().appName(s"${cycle}_stat_${project}_${day}").getOrCreate()
//
//    var baseDF= {
//      if(cycle=="range"){
//        DateUtils.getRangeDays(beginDay, day).map{
//          d => spark.read.orc(transformer.getStatInPath(transformer.project, d, "day"):_*).withColumn("day", lit(d))
//        }.reduce{(x,y)=>x.union(y)}
//      }else{
//        spark.read.orc(transformer.getStatInPath(transformer.project, day, cycle):_*)
//      }
//    }
//
//    import spark.implicits._
//
//    var result = baseDF
//        .filter($"longitude".notEqual(0.0) && $"latitude".notEqual(0.0))
//      .withColumn("seq",row_number() over(Window.partitionBy($"hashedOperaId").orderBy($"timeMs".desc)))
//      .filter($"seq" === 1)
//      .select($"hashedOperaId",$"longitude", $"latitude")
////      .withColumn("value", )
//    .map(row => {
//      ("up:gps:"+row.get(0).toString(), convertRowToJSON(row))
//    })
////      .select(concat(lit("up:gps:"),$"hashedOperaId").name("key"),to_json(struct($"longitude", $"latitude")))
//    .repartition(30)
//
////  result.foreachPartition(
////      f => {
////        var redisConn: JedisClusterPlus = RedisUtils.getRedisClusterConn("dmp")()
////
////        f.foreach(
////          rows => {
////            var flag = redisConn.setnx(rows._1,rows._2)
//////            if (redisConn.ttl(rows._1) == -1){
////              redisConn.expire(rows._1,60 * 60 *24 * 30)
//////            }
////
////            println("======================")
////            if(flag !=0){
////              println(rows)
////              println("11111111111111111111111")
////            }
////
////            println("~~~~~~~~~~~~~~~~~~~~~")
////
////          }
////        )
////        redisConn.close()
////      }
////    )
//
//  }
//
////
//  def convertRowToJSON(row: Row): String = {
//    val m = row.getValuesMap(row.schema.fieldNames.drop(1))
//    JSONObject(m).toString()
//  }
//}
//
