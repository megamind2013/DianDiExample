//
//import collection.JavaConversions._
//import com.opera.adx.utils.CommonUtils._
//
//object Main {
//  /**
//    *
//    * spark-shell --master yarn --driver-memory 8G --jars ./adx_stat_2.11-0.2-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar --conf spark.ui.port=5041
//    *
//    */
//  def main(args: Array[String]): Unit = {
////    val foo = """a
////                 bc
////                 d"""
////    println(foo)
////
////    val speech = """abc
////                   |def""".stripMargin
////    println(speech)
////
////    var sql = """
////      |  SELECT  lang,
////      |  COUNT(1) AS pv,
////      |  COUNT(DISTINCT did) AS uv,
////      |
////      |  COUNT(XXX) AS return_pv,
////      |  COUNT(XXX) AS return_uv,
////      |
////      |  FROM resultDf
////      |  where XXX >= 0 and len(XXX) > 0
////      |
////      |  GROUP BY XXX,XXX, lang
////    """.stripMargin
////
////    val spark = SparkSession.builder().appName("Dimension").master("local[1]").getOrCreate()
////    println(sql)
////    spark.sql(sql)
//
////    val s = Set(2, 3, 5)
////    def func(i: Int) = "" + i + i
////    val func2 = (r: Map[Int,String], i: Int) => r + (i -> func(i))
////    println(s.foldLeft(Map.empty[Int,String])(func2))
//
//    var countryCode:String = "IN"
//    var operator: String = "40417"
//
//    var tempmap = OPERATORCODEMAP.getOrDefault(countryCode,Map())
//
////    println(tempmap.flatten(f => (f._1.split(","),f._2)))
//
////    val arr = Array(("a1", "b1", Array("1", "2", "3")),("a1", "b1", Array("1", "2", "3")))
////    val result = rdd.flatMap {
////      case (first, second, third) => {
////        third.map(x => (first, second, x))
////      }}
////
////    println("Results: " + result.collect.mkString("|"))
//
//    var keys = tempmap.keySet
//    var values = tempmap.values
//
//    var ss = keys.zipWithIndex.map{case(v,i) => (v, i+1)}.toMap
//
//    println(ss)
//  }
//}