//import com.opera.adx.annotations.{Cycle, Day, Month, Week}
//import com.opera.adx.domain.StatContext
//import com.opera.adx.utils.UDFTools._
//import org.apache.spark.sql.DataFrame
//import org.apache.spark.sql.expressions.Window
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.types.DataTypes
//
//class AdxRequestStat(statContext:StatContext) {
//  val spark = statContext.spark
//  import spark.implicits._
//  import statContext._
//
//  val baseDF = statContext.baseDF.select(
//    $"test",
//    rename("countryCode"),
//    rename("slotId"),
//    rename("bucketId"),
//    rename("publisherId"),
//    rename("appId"),
//    rename("trafficSource"),
//    $"sessionId",$"hashedOperaId",$"adReqCount")
//
//  @Cycle(Day("r_day_stat_request","test,official"),
//    Week("r_week_stat_request","test,official"),
//    Month("r_month_stat_request","test,official"))
//  def statRequest(insertToDB:DataFrame=>Unit)={
//    adFillRateDimensionColumnArray.map(baseDF.withColumn("dim",_).select($"dim", $"sessionId",$"hashedOperaId",$"adReqCount")
//      .groupBy($"dim")
//      .agg( countDistinct(col(USER_ID)).as("request_uv"),
//        count($"sessionId").as("request_count"),
//        sum($"adReqCount").as("request_ad_num")
//      ).select( col("dim")("test").as("test"),
//      col("dim")("country_code").as("country_code"),
//      col("dim")("slot_id").as("slot_id"),col("dim")("bucket_id").as("bucket_id"),
//      col("dim")("publisher_id").as("publisher_id"),
//      col("dim")("app_id").as("app_id"),
//      col("dim")("traffic_source").as("traffic_source"),
//      $"request_count",$"request_ad_num",$"request_uv")
//    ).foreach(insertToDB(_))
//  }
//
//  @Cycle(Week("r_week_stat_request_filed","official"))
//  def statRequestFiled(insertToDB:DataFrame=>Unit)= {
//
//    var requestDF = statContext.baseDF.filter($"test" === 0).select($"ipAddress.province", $"ipAddress.provinceCode", $"ipAddress.city",
//      $"countryCode".name("country"),
//      $"languageCode".name("language"),
//      $"connType".name("network"),
//      $"operator", $"hashedOperaId", $"deviceModel", $"osVersion",$"appVersion",$"deviceVendor",$"timeMs",$"appPackageName")
////      .persist(StorageLevel.MEMORY_ONLY)
//      .cache()
//
//    // country data
//    var countryUserData = requestDF
//      // 过滤掉重复数据
//      .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","country").orderBy($"country".desc))).filter($"seqNum" === 1)
//      .groupBy("country")
//      .agg(
////        countDistinct("hashedOperaId").name("countryNumber")
//        count("hashedOperaId").name("countryNumber")
//      )
//      .withColumn("test", lit("0"))
////      .persist(StorageLevel.MEMORY_ONLY)
//      .cache()
//
//    // province data
//    var resultData = countryUserData.join(
//      requestDF
//        // 过滤掉重复数据
//        // .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","province").orderBy($"province".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "province")
//        .agg(
////          countDistinct("hashedOperaId").name("provinceNumber")
//          count("hashedOperaId").name("provinceNumber")
//        )
//        .withColumn("rank", row_number().over(Window.partitionBy("country").orderBy($"provinceNumber".desc)))
//        .filter($"rank" <= 6),
//      Seq("country")
//    ).withColumn("ratio", ($"provinceNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"provinceNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("state").name("field"), $"province".name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//
//    // city data
//    resultData = countryUserData.join(
//      requestDF
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","province","city").orderBy($"city".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "province", "city")
//        .agg(
////          countDistinct("hashedOperaId").name("cityNumber")
//          count("hashedOperaId").name("cityNumber")
//        )
//        .withColumn("rank", row_number().over(Window.partitionBy("country").orderBy($"cityNumber".desc)))
//        .filter($"rank" <= 11),
//      Seq("country")
//    ).withColumn("ratio", ($"cityNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"cityNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("city").name("field"), concat($"province", lit("/"), $"city").name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//
//    // language data
//    resultData = countryUserData.join(
//      requestDF
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","language").orderBy($"language".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "language")
//        .agg(
////          countDistinct("hashedOperaId").name("languageNumber")
//          count("hashedOperaId").name("languageNumber")
//        ),
//      Seq("country")
//    ).withColumn("ratio", ($"languageNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"languageNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("language").name("field"), $"language".name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//
//    // network data
//    resultData = countryUserData.join(
//      requestDF
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","network").orderBy($"network".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "network")
//        .agg(
////          countDistinct("hashedOperaId").name( "networkNumber")
//          count("hashedOperaId").name( "networkNumber")
//        ),
//      Seq("country")
//    ).withColumn("ratio", ($"networkNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"networkNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("network").name("field"), $"network".name("field_value"), $"ratio", $"expression")
//    // insert into data
//    insertToDB(resultData)
//
//    // operator
//    resultData = countryUserData.join(
//        // 先用逗号拆分
//      requestDF.select(explode(split($"operator", ",")).name("operatorCode"), $"country", $"hashedOperaId")
//       // .withColumn("operatorCode",getOperatorCode($"operatorCode"))
//        // 过滤掉空
//        .filter($"operatorCode" !== "")
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","operatorCode").orderBy($"operatorCode".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "operatorCode")
//        .agg(
////          countDistinct("hashedOperaId").name( "operatorNumber")
//          count("hashedOperaId").name( "operatorNumber")
//        ),
//      Seq("country")
//    ).withColumn("ratio", ($"operatorNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"operatorNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("operator").name("field"), $"operatorCode".name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//
//    // devicePrice
//    resultData = countryUserData.join(
//      requestDF
//        .withColumn("devicePrice", getPriceIndexByDeviceModel($"deviceModel"))
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","devicePrice").orderBy($"devicePrice".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "devicePrice")
//        .agg(
////          countDistinct("hashedOperaId").name("deviceNumber")
//          count("hashedOperaId").name("deviceNumber")
//        ),
//      Seq("country")
//    ).withColumn("ratio", ($"deviceNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"deviceNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("devicePrice").name("field"), $"devicePrice".name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//
//    // osVersion
//    resultData = countryUserData.join(
//      requestDF
//        // 根据osVersion获得osVersionAbove
//        .withColumn("osVersionRange", getOSVersionRange($"osVersion"))
//        // osVersionAbove展开
//        .select(explode(split($"osVersionRange", ",")).name("osVersion"), $"country", $"hashedOperaId")
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","osVersion").orderBy($"osVersion".desc))).filter($"seqNum" === 1)
//        // 统计country、osVersion人数
//        .groupBy("country", "osVersion")
//        .agg(
////          countDistinct("hashedOperaId").name("osVersionNumber")
//          count("hashedOperaId").name("osVersionNumber")
//        ),
//      Seq("country")
//    ).withColumn("ratio", ($"osVersionNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"osVersionNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("osVersion").name("field"), $"osVersion".name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//
//    // appVersion
//    resultData = countryUserData.join(
//      requestDF
//        .withColumn("appOmitVersion",getAppOmitVersion($"appVersion"))
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","appPackageName","appOmitVersion").orderBy($"appOmitVersion".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "appPackageName", "appOmitVersion")
//        .agg(
////          countDistinct("hashedOperaId").name("appVersionNumber")
//          count("hashedOperaId").name("appVersionNumber")
//        ),
//      Seq("country")
//    ).withColumn("ratio", ($"appVersionNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"appVersionNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", $"appPackageName".name("field"), $"appOmitVersion".name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//
//    // deviceVendor
//    resultData = countryUserData.join(
//      requestDF
//        // 过滤掉重复数据
//        .withColumn("seqNum", row_number() over (Window.partitionBy("hashedOperaId","deviceVendor").orderBy($"deviceVendor".desc))).filter($"seqNum" === 1)
//        .groupBy("country", "deviceVendor")
//        .agg(
////          countDistinct("hashedOperaId").name("deviceVendorNumber")
//          count("hashedOperaId").name("deviceVendorNumber")
//        ),
//      Seq("country")
//    ).withColumn("ratio", ($"deviceVendorNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
//      .withColumn("expression", concat($"deviceVendorNumber", lit("/"), $"countryNumber"))
//      .select($"test",$"country", lit("deviceVendor").name("field"), $"deviceVendor".name("field_value"), $"ratio", $"expression")
//
//    // insert into data
//    insertToDB(resultData)
//  }
//}
