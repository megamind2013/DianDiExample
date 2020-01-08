//package gps
//
//import com.opera.adx.domain.Transformer
//import com.opera.adx.utils.DateUtils
//import com.opera.adx.utils.UDFTools.{shape, toTuple2}
//import org.apache.spark.sql.expressions.Window
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.{SaveMode, SparkSession}
//import org.apache.spark.sql.functions.udf
//
//import scala.reflect.runtime.universe.{TypeTag, typeTag}
//import org.apache.spark.sql.types._
//
//import scala.collection.mutable.Set
//
///**
//  *
//  * spark-shell --master yarn --driver-memory 8G --jars ./adx_stat_2.11-0.2-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar --conf spark.ui.port=5041
//  *
//  * import gps.GPSStatistics._
//  *
//  * var array1= Array("adx_request","20191101","day")
//  *
//  * gDensity(spark,array1)
//  *
//  */
//
//
//
//object GPSStatistics {
//
//  /**
//    *
//    * @param args
//    */
//    def gDensity(spark: SparkSession,args: Array[String]) = {
//      val beginTimeMs = System.currentTimeMillis
//      val project = args(0)
//      val day = args(1)
//      val cycle = args(2)
//
//      val month = day.substring(0, 6)
//      val week =  DateUtils.getWeekOfYearWithYear(day)
//      val transformer = Transformer(project)
//
//
//      val beginDay =
//        if(cycle == "range"){
//          args(3)
//        } else{
//          ""
//        }
//
//      var baseDF= {
//                if(cycle=="range"){
//        DateUtils.getRangeDays(beginDay, day).map{
//          d => spark.read.orc(transformer.getStatInPath(transformer.project, d, "day"):_*).withColumn("day", lit(d))
//        }.reduce{(x,y)=>x.union(y)}
//                }else{
//                  spark.read.orc(transformer.getStatInPath(transformer.project, day, cycle):_*)
//                }
//      }
//
//      import spark.implicits._
//
//      /**
//        * def nameAlias:Map[String,String] = Map[String,String]("countryCode"->"",
//        * "slotId"->"slot_id","bucketId"->"bucket_id","materialId"->"mid","adId"->"ad_id","aid"->"ad_id",
//        * "publisherId" -> "publisher_id", "appId"->"app_id",
//        * "advertiserId"->"advertiser_id","orderId"->"order_id",
//        * "trafficSource"->"traffic_source","eventName"->"event_name","siteId" -> "site_id",
//        * "agencyId" -> "agency_id","advIndustry" -> "advertiser_industry")
//        */
//
//
//      baseDF = baseDF.select($"test", $"countryCode".name("country_code"),$"slotId".name("slot_id"),$"bucketId".name("bucket_id"),$"publisherId".name("publisher_id"),
//        $"appId".name("app_id"),$"trafficSource".name("traffic_source"),$"sessionId",$"hashedOperaId",$"adReqCount",$"longitude",$"latitude",$"ip")
//
//      var schema = ArrayType(
//        StructType(
//          List(
//            StructField("longitude", DoubleType),
//            StructField("latitude", DoubleType)
//          )
//        )
//      )
//
//      val curDF = baseDF
//        .filter($"country_code" === "NG" && $"longitude".between(2.73333,14.21731) && $"latitude".between(4.31506,13.72918))
//        .withColumn("geoTuple",toTuple2[Double, Double].apply($"longitude",$"latitude"))
//        .groupBy($"test",$"country_code",$"ip").agg(
//        collect_set($"geoTuple").name("geoTuples")
//      ).withColumn("geoNumber",size($"geoTuples"))
//        .withColumn("shape",shape(schema)($"geoTuples"))
//        .withColumn("area",when($"shape".isNotNull && size($"shape") ===4 ,($"shape"(1).getField("latitude") - $"shape"(0).getField("latitude")) * ($"shape"(3).getField("longitude") - $"shape"(0).getField("longitude")) ))
//        .withColumn("midpoint",
//          when($"shape".isNotNull && size($"shape") ===4 ,toTuple2[Double, Double].apply(($"shape"(2).getField("longitude") + $"shape"(0).getField("longitude"))/2,($"shape"(0).getField("latitude") + $"shape"(2).getField("latitude"))/2))
//            .when($"shape".isNotNull && size($"shape") ===2,toTuple2[Double, Double].apply(($"shape"(0).getField("longitude") + $"shape"(1).getField("longitude"))/2,($"shape"(0).getField("latitude") + $"shape"(1).getField("latitude"))/2))
//            .when($"shape".isNotNull && size($"shape") ===1,toTuple2[Double, Double].apply($"shape"(0).getField("longitude"),$"shape"(0).getField("latitude")))
//        )
//        .withColumn("density",$"geoNumber"/$"area")
//        .select($"test",$"country_code",$"ip",format_string("%s", $"shape").name("shape"),format_string("%s", $"midpoint").name("midpoint"),$"geoNumber",$"area",$"density")
//
//      curDF
////      curDF.repartition(1)
////        .write.mode(SaveMode.Overwrite).format("csv").save("/user/sdev/ng.txt")
//    }
//
//  def toTuple2[S: TypeTag, T: TypeTag]=udf[(S, T), S, T]((x: S, y: T) => (x,y))
//
//  var schema = ArrayType(StructType(
//    List(
//      StructField("longitude", DoubleType),
//      StructField("latitude", DoubleType)
//    )
//  ))
//
//
//  def rectangle = udf((inputArray:Seq[org.apache.spark.sql.Row]) => {  // udf function
//    var xShaft:Set[Double] = Set()
//    var yShaft:Set[Double] = Set()
//
//    var iterator = inputArray.iterator
//    while (iterator.hasNext){
//      var row = iterator.next()
//      xShaft += (row.getDouble(0))
//      yShaft += (row.getDouble(1))
//    }
//
//    if(xShaft.size >=2 && yShaft.size >= 2){
//      List((xShaft.min,yShaft.min),(xShaft.min,yShaft.max),(xShaft.max,yShaft.max),(xShaft.max,yShaft.min))
//    }
//    else
//      None
//  }, schema)
//
//  def to_json = udf((inputArray:Seq[org.apache.spark.sql.Row]) => {
////    s"""[${inputArray.mkString(",")}]"""
//        val str = inputArray match {
//          case null => None
//          case seq: Seq[_] => seq.map( r => {
//
//          })
//        }
//      str
//
//  },schema)
//}
//
