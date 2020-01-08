//package fetchdata
//import com.opera.adx.annotations.ReflectionTools._
//import com.opera.adx.domain.{StatContext, Transformer, WithRowNumberColumn}
//import com.opera.adx.metrics.{CommonMetrics, InfluxMetrics, Job}
//import com.opera.adx.utils.CommonUtils.{getOmitString, getOsVersionIndex, isNumeric}
//import com.opera.adx.utils.UDFTools.{getAddressByIp, getOSVersionRange}
//import com.opera.adx.utils.{CategoryDB, DBUtils, DateUtils, Tools}
//import org.apache.spark.sql.expressions.Window
//import org.apache.spark.sql.functions.{broadcast, concat, count, countDistinct, explode, lit, row_number, split, udf, when}
//import org.apache.spark.sql.types.DataTypes
//import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
//
///**
//
//spark-shell --master yarn --jars ./adx_stat_2.11-0.2-SNAPSHOT.jar,./lib/async-http-client-1.9.21.jar,./lib/build-link-2.4.3.jar,./lib/fastjson-1.2.58.jar,./lib/flink-connector-kafka-0.9_2.11-1.7.0.jar,./lib/flink-connector-kafka-base_2.11-1.7.0.jar,./lib/flink-core-1.7.0.jar,./lib/flink-scala_2.11-1.7.0.jar,./lib/flink-streaming-java_2.11-1.7.0.jar,./lib/flink-streaming-scala_2.11-1.7.0.jar,./lib/geoip2-2.5.0.jar,./lib/jedis-3.0.0.jar,./lib/kafka-clients-0.9.0.1.jar,./lib/mariadb-java-client-2.2.3.jar,./lib/mariadb-java-client-2.4.0.jar,./lib/maxmind-db-1.1.0.jar,./lib/mysql-connector-java-5.1.8.jar,./lib/opera-s3o-2.8.2.jar,./lib/play_2.11-2.4.3.jar,./lib/play-datacommons_2.11-2.4.3.jar,./lib/play-exceptions-2.4.3.jar,./lib/play-functional_2.11-2.4.3.jar,./lib/play-iteratees_2.11-2.4.3.jar,./lib/play-json_2.11-2.4.3.jar,./lib/play-netty-utils-2.4.3.jar,./lib/play-ws_2.11-2.4.3.jar,./lib/RoaringBitmap-0.8.6.jar,./lib/spark-redis-0.3.2.jar,./lib/tikv-client-1.2-jar-with-dependencies.jar,./lib/tispark-core-1.2-jar-with-dependencies.jar,./lib/twirl-api_2.11-1.1.1.jar,./lib/zero-allocation-hashing-0.8.jar --conf spark.ui.port=5041
//
//
//import fetchdata.AudienceNumber._
//
//  * // day
//  * var array1= Array("adx_request","20190818","day","-1")
//  *
//  * audience1(spark,array1).show(1000,false)
//
//7.19-8.18
////range
//var array1= Array("adx_request","20190818","range","20190719")
//
//audience1(spark,array1).show(10000,false)
//
//
//8.20-8.26
////range
//var array1= Array("adx_request","20191222","range","20191216")
//
//audience1(spark,array1).show(10000,false)
//
//
//audience2(spark,array1).show(10000,false)
//
//
//var array1= Array("adx_request","20191222","range","20191216")
//
//var array1= Array("adx_request","20191216","range","20191216")
//
//
//audience3(spark,array1).show(10000,false)
//
//# val transformer = Transformer("adx_request")
//
//  */
//object AudienceNumber {
//  /**
//    * 计算NG/ID/IN/KE/GH/ZA在network和os version定向(Wi-Fi)/(2G)/(2G,3G)分别与android版本4.4交集的精确用户数
//    *
//    * @param args
//    */
//    def audience1(spark: SparkSession,args: Array[String]): DataFrame = {
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
////        if(cycle=="range"){
//          DateUtils.getRangeDays(beginDay, day).map{
//            d => spark.read.orc(transformer.getStatInPath(transformer.project, d, "day"):_*).withColumn("day", lit(d))
//          }
//            .reduce{(x,y)=> {
//              x.select("sessionId","hashedOperaId","advertisingId","hashedImei","androidId","channel","countryCode","slotId","timeMs","adReqCount","ip","connType","userAgent","floorPriceInLi","availableServices","languageCode","appPackageName","appVersion","deviceType","deviceVendor","deviceModel","deviceScreenWidth","deviceScreenHeight","osType","osVersion","longitude","latitude","positionTimestamp","bucketId","test","publisherId","appId","operator","rawOperaId","trafficSource","ipAddress","day")
//                .union(y.select("sessionId","hashedOperaId","advertisingId","hashedImei","androidId","channel","countryCode","slotId","timeMs","adReqCount","ip","connType","userAgent","floorPriceInLi","availableServices","languageCode","appPackageName","appVersion","deviceType","deviceVendor","deviceModel","deviceScreenWidth","deviceScreenHeight","osType","osVersion","longitude","latitude","positionTimestamp","bucketId","test","publisherId","appId","operator","rawOperaId","trafficSource","ipAddress","day"))
//            }}
////        }else{
////          spark.read.orc(transformer.getStatInPath(transformer.project, day, cycle):_*)
////        }
//      }
//
//      import spark.implicits._
//      // NG/ID/IN/KE/GH/ZA
////      var requestDF = baseDF.filter($"countryCode".isin("NG","ID","IN","KE","GH","ZA"))
////        .withColumn("osVersion44",getOSVersion44($"osVersion")).filter($"osVersion44" !== "-1")
////        .groupBy($"countryCode",$"connType")
////        .agg(
////          countDistinct("hashedOperaId").name("userNumber")
////        )
////        .select($"countryCode",$"connType",$"userNumber")
//
//      baseDF.filter($"countryCode" === "IN" && $"connType".isin("WIFI","CELLULAR_4G"))
//              .withColumn("osVersion40",getOSVersion40($"osVersion")).filter($"osVersion40" !== "-1")
//              .groupBy($"countryCode",$"connType")
//              .agg(
//                countDistinct("hashedOperaId").name("userNumber")
//              )
//              .select($"countryCode",$"connType",$"userNumber")
//
//    }
//
//
//  def audience2(spark: SparkSession,args: Array[String]):DataFrame = {
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
//      //        if(cycle=="range"){
//      DateUtils.getRangeDays(beginDay, day).map{
//        d => spark.read.orc(transformer.getStatInPath(transformer.project, d, "day"):_*).withColumn("day", lit(d))
//      }
//        .reduce{(x,y)=> {
//          x.select("sessionId","hashedOperaId","advertisingId","hashedImei","androidId","channel","countryCode","slotId","timeMs","adReqCount","ip","connType","userAgent","floorPriceInLi","availableServices","languageCode","appPackageName","appVersion","deviceType","deviceVendor","deviceModel","deviceScreenWidth","deviceScreenHeight","osType","osVersion","longitude","latitude","positionTimestamp","bucketId","test","publisherId","appId","operator","rawOperaId","trafficSource","ipAddress","day")
//            .union(y.select("sessionId","hashedOperaId","advertisingId","hashedImei","androidId","channel","countryCode","slotId","timeMs","adReqCount","ip","connType","userAgent","floorPriceInLi","availableServices","languageCode","appPackageName","appVersion","deviceType","deviceVendor","deviceModel","deviceScreenWidth","deviceScreenHeight","osType","osVersion","longitude","latitude","positionTimestamp","bucketId","test","publisherId","appId","operator","rawOperaId","trafficSource","ipAddress","day"))
//        }}
//      //        }else{
//      //          spark.read.orc(transformer.getStatInPath(transformer.project, day, cycle):_*)
//      //        }
//    }
//
//    import spark.implicits._
//    // NG/ID/IN/KE/GH/ZA
//    //      var requestDF = baseDF.filter($"countryCode".isin("NG","ID","IN","KE","GH","ZA"))
//    //        .withColumn("osVersion44",getOSVersion44($"osVersion")).filter($"osVersion44" !== "-1")
//    //        .groupBy($"countryCode",$"connType")
//    //        .agg(
//    //          countDistinct("hashedOperaId").name("userNumber")
//    //        )
//    //        .select($"countryCode",$"connType",$"userNumber")
//
//    baseDF.filter($"countryCode" === "ID" && $"apppackagename" === "com.opera.app.news")
//      .withColumn("osVersion40",getOSVersion40($"osVersion")).filter($"osVersion40" !== "-1")
//      .groupBy($"countryCode",$"apppackagename")
//      .agg(
//        countDistinct("hashedOperaId").name("userNumber")
//      )
//      .select($"countryCode",$"apppackagename",$"userNumber")
//
//
//
//    //      var requestDF = baseDF.filter($"countryCode" === "ID" && $"appPackageName" === "")
//    //        .withColumn("osVersion40",getOSVersion40($"osVersion")).filter($"osVersion40" !== "-1")
//    //        .groupBy($"countryCode",$"connType")
//    //        .agg(
//    //          countDistinct("hashedOperaId").name("userNumber")
//    //        )
//    //        .select($"countryCode",$"connType",$"userNumber")
//
//  }
//
//
//  def audience3(spark: SparkSession,args: Array[String]):DataFrame = {
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
//      //        if(cycle=="range"){
//      DateUtils.getRangeDays(beginDay, day).map{
//        d => spark.read.orc(transformer.getStatInPath(transformer.project, d, "day"):_*).withColumn("day", lit(d))
//      }
//        .reduce{(x,y)=> {
//          x.select("sessionId","hashedOperaId","advertisingId","hashedImei","androidId","channel","countryCode","slotId","timeMs","adReqCount","ip","connType","userAgent","floorPriceInLi","availableServices","languageCode","appPackageName","appVersion","deviceType","deviceVendor","deviceModel","deviceScreenWidth","deviceScreenHeight","osType","osVersion","longitude","latitude","positionTimestamp","bucketId","test","publisherId","appId","operator","rawOperaId","trafficSource","ipAddress","day")
//            .union(y.select("sessionId","hashedOperaId","advertisingId","hashedImei","androidId","channel","countryCode","slotId","timeMs","adReqCount","ip","connType","userAgent","floorPriceInLi","availableServices","languageCode","appPackageName","appVersion","deviceType","deviceVendor","deviceModel","deviceScreenWidth","deviceScreenHeight","osType","osVersion","longitude","latitude","positionTimestamp","bucketId","test","publisherId","appId","operator","rawOperaId","trafficSource","ipAddress","day"))
//        }}
//      //        }else{
//      //          spark.read.orc(transformer.getStatInPath(transformer.project, day, cycle):_*)
//      //        }
//    }
//
//    import spark.implicits._
//    // NG/ID/IN/KE/GH/ZA
//    //      var requestDF = baseDF.filter($"countryCode".isin("NG","ID","IN","KE","GH","ZA"))
//    //        .withColumn("osVersion44",getOSVersion44($"osVersion")).filter($"osVersion44" !== "-1")
//    //        .groupBy($"countryCode",$"connType")
//    //        .agg(
//    //          countDistinct("hashedOperaId").name("userNumber")
//    //        )
//    //        .select($"countryCode",$"connType",$"userNumber")
//
//
//    baseDF.filter($"countryCode" === "NG")
//      .groupBy("countryCode", "deviceVendor")
//      .agg(
//        count("hashedOperaId").name("deviceVendorNumber")
//      )
//      .select(explode(split(lit("0,2"),",")).name("test"),$"countryCode", lit("deviceVendor").name("field"), $"deviceVendor",$"deviceVendorNumber")
//
//
//    //      var requestDF = baseDF.filter($"countryCode" === "ID" && $"appPackageName" === "")
//    //        .withColumn("osVersion40",getOSVersion40($"osVersion")).filter($"osVersion40" !== "-1")
//    //        .groupBy($"countryCode",$"connType")
//    //        .agg(
//    //          countDistinct("hashedOperaId").name("userNumber")
//    //        )
//    //        .select($"countryCode",$"connType",$"userNumber")
//
//  }
//
//  def getOSVersion44 = udf {
//    (osVersion: String) => {
//      var version:String = getOmitString(osVersion);
//
//      if(isNumeric(version) && BigDecimal(version) >= 4.4){
//        version
//      } else {
//        "-1"
//      }
//    }
//  }
//
//  def getOSVersion40 = udf {
//    (osVersion: String) => {
//      var version:String = getOmitString(osVersion);
//
//      if(isNumeric(version) && BigDecimal(version) >= 4.0){
//        version
//      } else {
//        "-1"
//      }
//    }
//  }
//}
