//package fetchdata
//
//import com.opera.adx.domain.{StatContext, Transformer}
//import com.opera.adx.utils.CommonUtils._
//import com.opera.adx.utils.DateUtils
//import org.apache.spark.sql.{Column, SaveMode, SparkSession}
//import org.apache.spark.sql.expressions.Window
//import org.apache.spark.sql.functions.{array, col, count, countDistinct, get_json_object, last, lit, max, min, row_number, struct, substring_index, sum, udf, when}
//
///**
//  * 产品付佳楠提
//  *
//  * spark-shell --master yarn --jars ./adx_stat_2.11-0.2-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar --conf spark.ui.port=5041
//  *
//  * import fetchdata.OsiteStatistics._
//  *
//  * var array1= Array("adx_pixel_event","20190814","day")
//  *
//  * osite1(spark,array1)
//  *
//  *
//  * var array1= Array("adx_pixel_event","20191126","day")
//  *
//  * osite2(spark,array1)
//  *
//  * spark-shell --master yarn --jars ./adx_stat_2.11-0.3-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar --conf spark.ui.port=5041
//  *
//  *
//  *
//  *
//  *
//  *
//  * spark-shell --master yarn --jars ./adx_stat_2.11-0.2-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar --conf spark.ui.port=5041
//  *
//  * var array1= Array("adx_pixel_event","20191126","day")
//  *
//  * import fetchdata.OsiteStatistics._
//  * osite3(spark,array1)
//  *
//  *
//  *
//  */
//object OsiteStatistics {
//  def TOTAL_TAG = "total"
//  def USER_ID = "hashedOperaId"
//
//  /**
//    * 1.活跃站点单天的浏览时长数据（单个用户产生的数据集合）
//    * 2.活跃站点单天的加载时长数据（单个用户产生的数据集合）
//    *
//    * @param args
//    */
//    def osite1(spark: SparkSession,args: Array[String]) = {
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
//      val baseDF= {
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
//      // NG/ID/IN/KE/GH/ZA
//      val curDF = baseDF
//        // calculate view_rate
//        .withColumn("view_rate",$"viewMaxY"/$"pageSizeY")
//        .withColumn("site_id",$"siteId")
//        .withColumn("event_name",$"eventName")
//
//      val windowSpecOrder = Window.partitionBy($"site_id",$"pxSessionId").orderBy($"timeMs")
//
//      val windowSpec = Window.partitionBy($"site_id",$"pxSessionId").orderBy($"timeMs").rowsBetween(Long.MinValue,Long.MaxValue)
//
//      var res =curDF
//        .withColumn("advertiser_id",max($"siteAdvId").over(windowSpec))
//        .withColumn("beginPageViewTime",min(when($"event_name" === "pageview",$"timeMs")).over(windowSpec))
//        .withColumn("beginPagePingTime",min(when($"event_name" === "pageping",$"timeMs")).over(windowSpec))
//        .withColumn("endPingTime",max(when($"event_name" === "pageping",$"timeMs")).over(windowSpec))
//        .withColumn("pageLoadingTotalTime",last(when($"event_name" === "pageping" && $"pageLoadingTotalTime" > 0,$"pageLoadingTotalTime"),true).over(windowSpec))
//        .withColumn("pageLoadingFetchTime",last(when($"event_name" === "pageping" && $"pageLoadingFetchTime" > 0,$"pageLoadingFetchTime"),true).over(windowSpec))
//        .withColumn("pageLoadingTcpConnTime",last(when($"event_name" === "pageping" && $"pageLoadingTcpConnTime" > 0,$"pageLoadingTcpConnTime"),true).over(windowSpec))
//        .withColumn("pageLoadingDnsTime",last(when($"event_name" === "pageping" && $"pageLoadingDnsTime" > 0,$"pageLoadingDnsTime"),true).over(windowSpec))
//        .withColumn("pageLoadingOnloadTime",last(when($"event_name" === "pageping" && $"pageLoadingOnloadTime" > 0,$"pageLoadingOnloadTime"),true).over(windowSpec))
//        .withColumn("pageLoadingProcessingTime",last(when($"event_name" === "pageping" && $"pageLoadingProcessingTime" > 0,$"pageLoadingProcessingTime"),true).over(windowSpec))
//        .withColumn("form_number",sum(when($"event_name" === "submit" ,1).otherwise(0)).over(windowSpec))
//        .withColumn("whatsApp_number",sum(when($"event_name" === "contact" and $"eventDataType" === "whatsApp",1).otherwise(0)).over(windowSpec))
//        .withColumn("call_number",sum(when($"event_name" === "contact" and $"eventDataType" === "phone",1).otherwise(0)).over(windowSpec))
//
//        .withColumn("sequence",row_number().over(windowSpecOrder))
//        .filter($"sequence"===1 && $"site_id"==="st1501915421760" && ($"test"===0  || $"test"===1))
//
//        .select($"advertiser_id",$"site_id", $"pxSessionId",$"pageLoadingTotalTime",
//          (when($"beginPageViewTime".isNotNull && $"endPingTime".isNotNull &&  $"endPingTime">=$"beginPageViewTime", $"endPingTime"-$"beginPageViewTime" )
//            .when($"beginPagePingTime".isNotNull && $"endPingTime".isNotNull &&  $"endPingTime">=$"beginPagePingTime", $"endPingTime"-$"beginPagePingTime")
//            .when($"beginPageViewTime".isNotNull && $"endPingTime".isNull,0)
//            ).name("browsing_time")
//        )
//
//      res.cache.repartition(1)
//          .write.mode(SaveMode.Overwrite).format("csv").save("/user/sdev/st1501915421760.csv")
//    }
//
//
//
//  def osite2(spark: SparkSession,args: Array[String]) = {
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
//    val baseDF= {
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
//    // NG/ID/IN/KE/GH/ZA
//
//    val curDF = baseDF
//      // calculate view_rate
//      .withColumn("view_rate",$"viewMaxY"/$"pageSizeY")
//      .withColumn("site_id",$"siteId")
//      .withColumn("event_name",$"eventName")
//
//      .withColumn("country_code",$"countryCode")
//      .withColumn("slot_id",$"slotId")
//      .withColumn("bucket_id",$"bucketId")
//      .withColumn("ad_id",$"adId")
//      .withColumn("mid",$"materialId")
//      .withColumn("publisher_id",$"publisherId")
//      .withColumn("app_id",$"appId")
//      .withColumn("advertiser_id",$"advertiserId")
//      .withColumn("order_id",$"orderId")
//      .withColumn("traffic_source",$"trafficSource")
//      .withColumn("event_name",$"eventName")
//      .withColumn("site_id",$"siteId")
//      .withColumn("agency_id",$"agencyId")
//      .withColumn("advertiser_industry",$"advIndustry")
//      .filter($"site_id"=== "st1927766425792")
//
//    // session dimension
//    val windowSpecOrder = Window.partitionBy($"site_id",$"pxSessionId").orderBy($"timeMs")
//
//    val windowSpec = Window.partitionBy($"site_id",$"pxSessionId").orderBy($"timeMs").rowsBetween(Long.MinValue,Long.MaxValue)
//
//    var res =curDF
//      // .withColumn("advertiser_id",max($"siteAdvId").over(windowSpec))
//      // use advertiser_id
//      //      .withColumn("advertiser_id",max($"advertiser_id").over(windowSpec))
//      // if advertiser_id is null,use siteAdvId
//      //      .withColumn("advertiser_id",when($"advertiser_id".isNull,max($"siteAdvId").over(windowSpec)).otherwise($"advertiser_id"))
////      .withColumn("advertiser_id",when((max($"advertiser_id").over(windowSpec)).isNotNull,max($"advertiser_id").over(windowSpec)).otherwise(max($"siteAdvId").over(windowSpec)))
//      .withColumn("advertiser_id",max($"advertiser_id").over(windowSpec))
//      .withColumn("siteAdvId",max($"siteAdvId").over(windowSpec))
//      .withColumn("advertiser_id",when($"advertiser_id".isNull || $"advertiser_id" === "",$"siteAdvId").otherwise($"advertiser_id"))
//
//      .withColumn("beginPageViewTime",min(when($"event_name" === "pageview",$"timeMs")).over(windowSpec))
//      .withColumn("beginPagePingTime",min(when($"event_name" === "pageping",$"timeMs")).over(windowSpec))
//      .withColumn("endPingTime",max(when($"event_name" === "pageping",$"timeMs")).over(windowSpec))
//      .withColumn("pageLoadingTotalTime",last(when($"event_name" === "pageping" && $"pageLoadingTotalTime" > 0,$"pageLoadingTotalTime"),true).over(windowSpec))
//      .withColumn("pageLoadingFetchTime",last(when($"event_name" === "pageping" && $"pageLoadingFetchTime" > 0,$"pageLoadingFetchTime"),true).over(windowSpec))
//      .withColumn("pageLoadingTcpConnTime",last(when($"event_name" === "pageping" && $"pageLoadingTcpConnTime" > 0,$"pageLoadingTcpConnTime"),true).over(windowSpec))
//      .withColumn("pageLoadingDnsTime",last(when($"event_name" === "pageping" && $"pageLoadingDnsTime" > 0,$"pageLoadingDnsTime"),true).over(windowSpec))
//      .withColumn("pageLoadingOnloadTime",last(when($"event_name" === "pageping" && $"pageLoadingOnloadTime" > 0,$"pageLoadingOnloadTime"),true).over(windowSpec))
//      .withColumn("pageLoadingProcessingTime",last(when($"event_name" === "pageping" && $"pageLoadingProcessingTime" > 0,$"pageLoadingProcessingTime"),true).over(windowSpec))
//      .withColumn("form_number",max(when(substring_index($"event_name","_",1) === "submit" ,1).otherwise(0)).over(windowSpec))
//      .withColumn("whatsApp_number",max(when($"event_name" === "contact_whatsApp",1).otherwise(0)).over(windowSpec))
//      .withColumn("call_number",max(when($"event_name" === "contact_phone",1).otherwise(0)).over(windowSpec))
//
//      .withColumn("pageViewNumber",count(when($"event_name" === "pageview",$"event_name")).over(windowSpec))
//      .withColumn("staticPageViewNumber",count(when($"event_name" === "staticpageview",$"event_name")).over(windowSpec))
//
//      .withColumn("sequence",row_number().over(windowSpecOrder))
////      .filter($"sequence"===1)
////
////      .select($"test",$"advertiser_id",$"site_id", $"pxSessionId",$"view_rate",$"form_number",$"whatsApp_number",$"call_number",$"networkUserId",
////        (when($"pageLoadingTotalTime" <= 60000, $"pageLoadingTotalTime")).name("pageLoadingTotalTime"),
////        $"pageLoadingFetchTime",$"pageLoadingTcpConnTime",$"pageLoadingDnsTime",$"pageLoadingOnloadTime",$"pageLoadingProcessingTime",$"pageViewNumber",$"staticPageViewNumber",
////        (when($"beginPageViewTime".isNotNull && $"endPingTime".isNotNull &&  $"endPingTime">=$"beginPageViewTime", when(($"endPingTime"-$"beginPageViewTime") <= 600000, $"endPingTime"-$"beginPageViewTime") )
////          .when($"beginPagePingTime".isNotNull && $"endPingTime".isNotNull &&  $"endPingTime">=$"beginPagePingTime", when(($"endPingTime"-$"beginPagePingTime") <= 600000, $"endPingTime"-$"beginPagePingTime"))
////          .when($"beginPageViewTime".isNotNull && $"endPingTime".isNull,0)
////          ).name("browsing_time")
////      )
//
//
//    res.show(false)
//  }
//
//
//
//  def osite3(spark: SparkSession,args: Array[String]) = {
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
//    val baseDF= {
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
//    // NG/ID/IN/KE/GH/ZA
//    val curDF = baseDF
//      // calculate view_rate
//      .withColumn("view_rate",$"viewMaxY"/$"pageSizeY")
//      .withColumn("site_id",$"siteId")
//      .withColumn("event_name",$"eventName")
//
//      .withColumn("country_code",$"countryCode")
//      .withColumn("slot_id",$"slotId")
//      .withColumn("bucket_id",$"bucketId")
//      .withColumn("ad_id",$"adId")
//      .withColumn("mid",$"materialId")
//      .withColumn("publisher_id",$"publisherId")
//      .withColumn("app_id",$"appId")
//      .withColumn("advertiser_id",$"advertiserId")
//      .withColumn("order_id",$"orderId")
//      .withColumn("traffic_source",$"trafficSource")
//      .withColumn("event_name",$"eventName")
//      .withColumn("site_id",$"siteId")
//      .withColumn("agency_id",$"agencyId")
//      .withColumn("advertiser_industry",$"advIndustry")
//
//    // session dimension
//    val windowSpecOrder = Window.partitionBy($"site_id",$"pxSessionId").orderBy($"timeMs")
//
//    val windowSpec = Window.partitionBy($"site_id",$"pxSessionId").orderBy($"timeMs").rowsBetween(Long.MinValue,Long.MaxValue)
//
//    var res =curDF
//      //      .withColumn("advertiser_id",max($"siteAdvId").over(windowSpec))
//      // use advertiser_id
//      //      .withColumn("advertiser_id",max($"advertiser_id").over(windowSpec))
//      // if advertiser_id is null,use siteAdvId
//      //      .withColumn("advertiser_id",when($"advertiser_id".isNull,max($"siteAdvId").over(windowSpec)).otherwise($"advertiser_id"))
//      .withColumn("advertiser_id",when((max($"advertiser_id").over(windowSpec)).isNotNull,max($"advertiser_id").over(windowSpec)).otherwise(max($"siteAdvId").over(windowSpec)))
//
//
//      .withColumn("beginPageViewTime",min(when($"event_name" === "pageview",$"timeMs")).over(windowSpec))
//      .withColumn("beginPagePingTime",min(when($"event_name" === "pageping",$"timeMs")).over(windowSpec))
//      .withColumn("endPingTime",max(when($"event_name" === "pageping",$"timeMs")).over(windowSpec))
//      .withColumn("pageLoadingTotalTime",last(when($"event_name" === "pageping" && $"pageLoadingTotalTime" > 0,$"pageLoadingTotalTime"),true).over(windowSpec))
//      .withColumn("pageLoadingFetchTime",last(when($"event_name" === "pageping" && $"pageLoadingFetchTime" > 0,$"pageLoadingFetchTime"),true).over(windowSpec))
//      .withColumn("pageLoadingTcpConnTime",last(when($"event_name" === "pageping" && $"pageLoadingTcpConnTime" > 0,$"pageLoadingTcpConnTime"),true).over(windowSpec))
//      .withColumn("pageLoadingDnsTime",last(when($"event_name" === "pageping" && $"pageLoadingDnsTime" > 0,$"pageLoadingDnsTime"),true).over(windowSpec))
//      .withColumn("pageLoadingOnloadTime",last(when($"event_name" === "pageping" && $"pageLoadingOnloadTime" > 0,$"pageLoadingOnloadTime"),true).over(windowSpec))
//      .withColumn("pageLoadingProcessingTime",last(when($"event_name" === "pageping" && $"pageLoadingProcessingTime" > 0,$"pageLoadingProcessingTime"),true).over(windowSpec))
//      .withColumn("form_number",max(when(substring_index($"event_name","_",1) === "submit",1).otherwise(0)).over(windowSpec))
//      .withColumn("whatsApp_number",max(when($"event_name" === "contact_whatsApp",1).otherwise(0)).over(windowSpec))
//      .withColumn("call_number",max(when($"event_name" === "contact_phone",1).otherwise(0)).over(windowSpec))
//
//      .withColumn("pageViewNumber",count(when($"event_name" === "pageview",$"event_name")).over(windowSpec))
//      .withColumn("staticPageViewNumber",count(when($"event_name" === "staticpageview",$"event_name")).over(windowSpec))
//
//      .withColumn("sequence",row_number().over(windowSpecOrder))
//      .filter($"sequence"===1 && $"site_id"=== "st1927766425792")
//
//      .select($"test",$"advertiser_id",$"site_id", $"pxSessionId",$"view_rate",$"form_number",$"whatsApp_number",$"call_number",$"networkUserId",$"agency_id",$"source",$"ad_id",$"mid",$"advertiser_industry",$"phase",$"country_code",
//        (when($"pageLoadingTotalTime" <= 60000, $"pageLoadingTotalTime")).name("pageLoadingTotalTime"),
//        $"pageLoadingFetchTime",$"pageLoadingTcpConnTime",$"pageLoadingDnsTime",$"pageLoadingOnloadTime",$"pageLoadingProcessingTime",$"pageViewNumber",$"staticPageViewNumber",
//        (when($"beginPageViewTime".isNotNull && $"endPingTime".isNotNull &&  $"endPingTime">=$"beginPageViewTime", when(($"endPingTime"-$"beginPageViewTime") <= 600000, $"endPingTime"-$"beginPageViewTime") )
//          .when($"beginPagePingTime".isNotNull && $"endPingTime".isNotNull &&  $"endPingTime">=$"beginPagePingTime", when(($"endPingTime"-$"beginPagePingTime") <= 600000, $"endPingTime"-$"beginPagePingTime"))
//          .when($"beginPageViewTime".isNotNull && $"endPingTime".isNull,0)
//          ).name("browsing_time")
//      )
//
//    pixelSiteEventDimensionColumnArray.map(
//      res.withColumn("dim",_).groupBy("dim")
//        .agg(
//          sum($"pageViewNumber").name("site_pv"),
//          countDistinct(when($"pageViewNumber" > 0,$"networkUserId")).name("site_uv"),
//          sum($"staticPageViewNumber").name("site_pv_2"),
//          countDistinct(when($"staticPageViewNumber" > 0,$"networkUserId")).name("site_uv_2"),
//          (sum(when($"browsing_time".isNotNull,$"browsing_time").otherwise(0))/count($"browsing_time")).as("browsing_time"),
//          (sum(when($"view_rate".isNotNull,$"view_rate").otherwise(0))/count($"view_rate")).as("view_rate"),
//          (sum(when($"pageLoadingTotalTime".isNotNull,$"pageLoadingTotalTime").otherwise(0))/count($"pageLoadingTotalTime")).as("pl_total_time"),
//          (sum(when($"pageLoadingFetchTime".isNotNull,$"pageLoadingFetchTime").otherwise(0))/count($"pageLoadingFetchTime")).as("pl_fetch_time"),
//          (sum(when($"pageLoadingTcpConnTime".isNotNull,$"pageLoadingTcpConnTime").otherwise(0))/count($"pageLoadingTcpConnTime")).as("pl_tcp_conn_time"),
//          (sum(when($"pageLoadingDnsTime".isNotNull,$"pageLoadingDnsTime").otherwise(0))/count($"pageLoadingDnsTime")).as("pl_dns_time"),
//          (sum(when($"pageLoadingOnloadTime".isNotNull,$"pageLoadingOnloadTime").otherwise(0))/count($"pageLoadingOnloadTime")).as("pl_onload_time"),
//          (sum(when($"pageLoadingProcessingTime".isNotNull,$"pageLoadingProcessingTime").otherwise(0))/count($"pageLoadingOnloadTime")).as("pl_processing_time"),
//          sum($"form_number").name("form_number"),
//          sum($"whatsApp_number").name("whatsApp_number"),
//          sum($"call_number").name("call_number")
//        ).select(
//        col("dim")("test").as("test"),
//        col("dim")("site_id").as("site_id"),
//        col("dim")("agency_id").as("agency_id"),
//        col("dim")("advertiser_id").as("advertiser_id"),
//        col("dim")("advertiser_industry").as("advertiser_industry"),
//        col("dim")("country_code").as("country_code"),
//        col("dim")("phase").as("phase"),
//        col("dim")("source").as("source"),
//        col("dim")("ad_id").as("ad_id"),
//        col("dim")("mid").as("mid"),
//        $"site_pv",$"site_uv",$"site_pv_2",$"site_uv_2",$"browsing_time",$"view_rate",$"pl_total_time",$"pl_fetch_time",$"pl_tcp_conn_time",
//        $"pl_dns_time",$"pl_onload_time",$"pl_processing_time",$"form_number",$"whatsApp_number",$"call_number")
//    ).foreach(_.show(1000,false))
//  }
//
//  def constructDimensionStruct(dimensionArray:Array[Array[String]],columnNames:Array[String]):Array[Column] = {
//    dimensionArray.map(f=>
//      struct( f.zipWithIndex.map(f=> if(f._1==TOTAL_TAG) lit(TOTAL_TAG).name(columnNames(f._2))
//      else {if(columnNames(f._2)=="traffic_source") col(f._1).cast("string").name(columnNames(f._2))
//      else col(f._1).name(columnNames(f._2))
//      }
//      ):_*
//      )
//    )
//  }
//
////  def validFormEvent() = {
////    when(
////      get_json_object($"eventData","$.data.userName").isNotNull ||
////        get_json_object($"eventData","$.data.phoneNumber").isNotNull ||
////        get_json_object($"eventData","$.data.nickname").isNotNull ||
////        get_json_object($"eventData","$.data.email").isNotNull ||
////        get_json_object($"eventData","$.data.address").isNotNull ||
////        get_json_object($"eventData","$.data.age").isNotNull ||
////        get_json_object($"eventData","$.data.gender").isNotNull ||
////        get_json_object($"eventData","$.data.remarks").isNotNull
////      , true)
////      .otherwise(false)
////  }
//
////  val adPixelDimensions:String = """test,site_id,advertiser_id,agency_id,total,total,source,ad_id,mid
////                                    test,site_id,advertiser_id,agency_id,advertiser_industry,total,source,ad_id,mid
////                                    test,site_id,advertiser_id,agency_id,total,phase,source,ad_id,mid
////                                    test,site_id,advertiser_id,agency_id,advertiser_industry,phase,source,ad_id,mid"""
////
////  val pixelSiteEventColumnNames:Array[String] =  Array[String]("test","site_id","advertiser_id","agency_id","advertiser_industry","phase","source","ad_id","mid")
////  val pixelSiteEventDimensionArray = adPixelDimensions.split("\n").map(f=>f.trim.split(",").map(_.trim))
////
////  val pixelSiteEventDimensionColumnArray = constructDimensionStruct(pixelSiteEventDimensionArray,pixelSiteEventColumnNames)
//
//  val adPixelDimensions:String = """test,site_id,advertiser_id,agency_id,total,total,total,source,ad_id,mid
//                                    test,site_id,advertiser_id,agency_id,advertiser_industry,total,total,source,ad_id,mid
//                                    test,site_id,advertiser_id,agency_id,total,total,phase,source,ad_id,mid
//                                    test,site_id,advertiser_id,agency_id,advertiser_industry,country_code,phase,source,ad_id,mid"""
//
//  val pixelSiteEventColumnNames:Array[String] =  Array[String]("test","site_id","advertiser_id","agency_id","advertiser_industry","country_code","phase","source","ad_id","mid")
//  val pixelSiteEventDimensionArray = adPixelDimensions.split("\n").map(f=>f.trim.split(",").map(_.trim))
//
//  val pixelSiteEventDimensionColumnArray = constructDimensionStruct(pixelSiteEventDimensionArray,pixelSiteEventColumnNames)
//
//}
