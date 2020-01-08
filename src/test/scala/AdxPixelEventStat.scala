//import com.opera.adx.domain.{StatContext, Transformer}
//import com.opera.adx.utils.DateUtils
//import org.apache.spark.sql.{DataFrame, SparkSession}
//import org.apache.spark.sql.expressions.Window
//import org.apache.spark.sql.functions._
//
///**
//  * spark-shell --master yarn --jars ./adx_stat_2.11-0.3-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar --conf spark.ui.port=5041
//  *
//  * import AdxPixelEventStat._
//  *
//  * var array1= Array("adx_pixel_event","20190922","day")
//  *
//  * statPixelSiteEvent(spark,array1)
//  */
//object AdxPixelEventStat {
//
//  def validFormEvent(spark: SparkSession) = {
//    import spark.implicits._
//    when(
//      get_json_object($"eventData","$.data.userName").isNotNull ||
//        get_json_object($"eventData","$.data.phoneNumber").isNotNull ||
//        get_json_object($"eventData","$.data.nickname").isNotNull ||
//        get_json_object($"eventData","$.data.email").isNotNull ||
//        get_json_object($"eventData","$.data.address").isNotNull ||
//        get_json_object($"eventData","$.data.age").isNotNull ||
//        get_json_object($"eventData","$.data.gender").isNotNull ||
//        get_json_object($"eventData","$.data.remarks").isNotNull
//      , true)
//      .otherwise(false)
//  }
//
//  def statPixelSiteEvent(spark1: SparkSession,args: Array[String]) = {
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
//          d => spark1.read.orc(transformer.getStatInPath(transformer.project, d, "day"):_*).withColumn("day", lit(d))
//        }.reduce{(x,y)=>x.union(y)}
//      }else{
//        spark1.read.orc(transformer.getStatInPath(transformer.project, day, cycle):_*)
//      }
//    }
//
////    var baseDF:DataFrame = spark.read.parquet("/apps/hive/warehouse/adx.db/log_adx_pixel_event/year=2019/month=09/*/day=20190922/*")
//    import spark1.implicits._
//
//    val statContext = new StatContext(spark1,day,project,transformer,cycle,1,beginDay)
//
//    import statContext._
//
//    val curDF = baseDF.select(
//      $"test",
//      rename("countryCode"),
//      rename("slotId"),
//      rename("bucketId"),
//      rename("adId"),
//      rename("materialId"),
//      rename("publisherId"),
//      rename("appId"),
//      rename("advertiserId"),
//      rename("orderId"),
//      rename("trafficSource"),
//      rename("eventName"),
//      rename("siteId"),
//      rename("agencyId"),
//      $"userId",$"sessionId",$"isConversion",$"payout",$"eventDataType",$"networkUserId",$"timeMs",$"pageLoadingTotalTime",$"pageLoadingFetchTime",
//      $"pageLoadingTcpConnTime",$"pageLoadingDnsTime",$"pageLoadingOnloadTime",$"pageLoadingProcessingTime",$"viewMaxY",$"pageSizeY",$"siteAdvId",
//      $"pxSessionId",$"eventData",$"source"
//    )
//        .filter($"site_id"==="st1695872210048")
//      // calculate view_rate
//      .withColumn("view_rate", $"viewMaxY" / $"pageSizeY")
//
//    // session dimension
//    val windowSpecOrder = Window.partitionBy($"site_id", $"pxSessionId").orderBy($"timeMs")
//
//    val windowSpec = Window.partitionBy($"site_id", $"pxSessionId").orderBy($"timeMs").rowsBetween(Long.MinValue, Long.MaxValue)
//
//    var res = curDF
//      .withColumn("advertiser_id", max($"siteAdvId").over(windowSpec))
//      .withColumn("beginPageViewTime", min(when($"event_name" === "pageview", $"timeMs")).over(windowSpec))
//      .withColumn("beginPagePingTime", min(when($"event_name" === "pageping", $"timeMs")).over(windowSpec))
//      .withColumn("endPingTime", max(when($"event_name" === "pageping", $"timeMs")).over(windowSpec))
//      .withColumn("pageLoadingTotalTime", last(when($"event_name" === "pageping" && $"pageLoadingTotalTime" > 0, $"pageLoadingTotalTime"), true).over(windowSpec))
//      .withColumn("pageLoadingFetchTime", last(when($"event_name" === "pageping" && $"pageLoadingFetchTime" > 0, $"pageLoadingFetchTime"), true).over(windowSpec))
//      .withColumn("pageLoadingTcpConnTime", last(when($"event_name" === "pageping" && $"pageLoadingTcpConnTime" > 0, $"pageLoadingTcpConnTime"), true).over(windowSpec))
//      .withColumn("pageLoadingDnsTime", last(when($"event_name" === "pageping" && $"pageLoadingDnsTime" > 0, $"pageLoadingDnsTime"), true).over(windowSpec))
//      .withColumn("pageLoadingOnloadTime", last(when($"event_name" === "pageping" && $"pageLoadingOnloadTime" > 0, $"pageLoadingOnloadTime"), true).over(windowSpec))
//      .withColumn("pageLoadingProcessingTime", last(when($"event_name" === "pageping" && $"pageLoadingProcessingTime" > 0, $"pageLoadingProcessingTime"), true).over(windowSpec))
//      .withColumn("form_number", sum(when(substring_index($"event_name", "_", 1) === "submit" && validFormEvent(spark), 1).otherwise(0)).over(windowSpec))
//      //      .withColumn("whatsApp_number",sum(when($"event_name" === "contact_whatsApp",1).otherwise(0)).over(windowSpec))
//      //      .withColumn("call_number",sum(when($"event_name" === "contact_phone",1).otherwise(0)).over(windowSpec))
//      .withColumn("whatsApp_number", when(count(when($"event_name" === "contact_whatsApp", $"event_name")).over(windowSpec) > 0, 1).otherwise(0))
//      .withColumn("call_number", when(count(when($"event_name" === "contact_phone", $"event_name")).over(windowSpec) > 0, 1).otherwise(0))
//
//      .withColumn("pageViewNumber", count(when($"event_name" === "pageview", $"event_name")).over(windowSpec))
//      .withColumn("staticPageViewNumber", count(when($"event_name" === "staticpageview", $"event_name")).over(windowSpec))
//
//      .withColumn("sequence", row_number().over(windowSpecOrder))
//      .filter($"sequence" === 1)
//
//      .select($"test", $"advertiser_id", $"site_id", $"pxSessionId", $"view_rate", $"form_number", $"whatsApp_number", $"call_number", $"networkUserId", $"agency_id", $"source", $"ad_id", $"mid",
//        (when($"pageLoadingTotalTime" <= 60000, $"pageLoadingTotalTime")).name("pageLoadingTotalTime"),
//        $"pageLoadingFetchTime", $"pageLoadingTcpConnTime", $"pageLoadingDnsTime", $"pageLoadingOnloadTime", $"pageLoadingProcessingTime", $"pageViewNumber", $"staticPageViewNumber",
//        (when($"beginPageViewTime".isNotNull && $"endPingTime".isNotNull && $"endPingTime" >= $"beginPageViewTime", when(($"endPingTime" - $"beginPageViewTime") <= 600000, $"endPingTime" - $"beginPageViewTime"))
//          .when($"beginPagePingTime".isNotNull && $"endPingTime".isNotNull && $"endPingTime" >= $"beginPagePingTime", when(($"endPingTime" - $"beginPagePingTime") <= 600000, $"endPingTime" - $"beginPagePingTime"))
//          .when($"beginPageViewTime".isNotNull && $"endPingTime".isNull, 0)
//          ).name("browsing_time")
//      )
//
//    pixelSiteEventDimensionColumnArray.map(
//      res.withColumn("dim", _).groupBy("dim")
//        .agg(
//          sum($"pageViewNumber").name("site_pv"),
//          countDistinct(when($"pageViewNumber" > 0, $"networkUserId")).name("site_uv"),
//          sum($"staticPageViewNumber").name("site_pv_2"),
//          countDistinct(when($"staticPageViewNumber" > 0, $"networkUserId")).name("site_uv_2"),
//          (sum(when($"browsing_time".isNotNull, $"browsing_time").otherwise(0)) / count($"browsing_time")).as("browsing_time"),
//          (sum(when($"view_rate".isNotNull, $"view_rate").otherwise(0)) / count($"view_rate")).as("view_rate"),
//          (sum(when($"pageLoadingTotalTime".isNotNull, $"pageLoadingTotalTime").otherwise(0)) / count($"pageLoadingTotalTime")).as("pl_total_time"),
//          (sum(when($"pageLoadingFetchTime".isNotNull, $"pageLoadingFetchTime").otherwise(0)) / count($"pageLoadingFetchTime")).as("pl_fetch_time"),
//          (sum(when($"pageLoadingTcpConnTime".isNotNull, $"pageLoadingTcpConnTime").otherwise(0)) / count($"pageLoadingTcpConnTime")).as("pl_tcp_conn_time"),
//          (sum(when($"pageLoadingDnsTime".isNotNull, $"pageLoadingDnsTime").otherwise(0)) / count($"pageLoadingDnsTime")).as("pl_dns_time"),
//          (sum(when($"pageLoadingOnloadTime".isNotNull, $"pageLoadingOnloadTime").otherwise(0)) / count($"pageLoadingOnloadTime")).as("pl_onload_time"),
//          (sum(when($"pageLoadingProcessingTime".isNotNull, $"pageLoadingProcessingTime").otherwise(0)) / count($"pageLoadingOnloadTime")).as("pl_processing_time"),
//          sum($"form_number").name("form_number"),
//          sum($"whatsApp_number").name("whatsApp_number"),
//          sum($"call_number").name("call_number")
//        ).select(
//        col("dim")("test").as("test"),
//        col("dim")("site_id").as("site_id"),
//        col("dim")("agency_id").as("agency_id"),
//        col("dim")("advertiser_id").as("advertiser_id"),
//        col("dim")("source").as("source"),
//        col("dim")("ad_id").as("ad_id"),
//        col("dim")("mid").as("mid"),
//        $"site_pv", $"site_uv", $"site_pv_2", $"site_uv_2", $"browsing_time", $"view_rate", $"pl_total_time", $"pl_fetch_time", $"pl_tcp_conn_time",
//        $"pl_dns_time", $"pl_onload_time", $"pl_processing_time", $"form_number", $"whatsApp_number", $"call_number")
//    ).foreach(_.show(100,false))
//  }
//}
