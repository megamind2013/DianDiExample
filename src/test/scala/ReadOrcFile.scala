//import com.opera.adx.service.AdxRequestStat
//import org.apache.spark.sql.expressions.Window
//import org.apache.spark.sql.{Column, DataFrame, SparkSession}
//import org.apache.spark.sql.functions._
//import org.apache.spark.sql.functions.col
//import org.apache.spark.sql.types.DataTypes
//import java.util.regex.Pattern
//
//import com.opera.adx.utils.DateUtils
//import org.apache.commons.lang.StringUtils
//
//import scala.reflect.runtime.universe
//import scala.reflect.runtime.universe.{Type, typeOf}
//
//object ReadOrcFile {
//  def nameAlias:Map[String,String] = Map[String,String](
//    "languageCode"->"language",
//    "connType"->"network",
//    "countryCode"->"country")
//
//
//
//
//
//
//
//
//
//  def getSub(str1:String,str2:String,times:Int = 1):Int  = {
//    if(str1 != null && times > 0){
//      var sub = str1.indexOf(str2)
//
//      for(i <- 2 to times if sub != -1) {
//        sub = str1.indexOf(str2,sub+1)
//      }
//
//      if (sub == -1) str1.length else sub
//    } else str1.length
//  }
//
//  def getOmitVersion(version:String,keepPointNum:Int = -1):String  = {
//    var firstPointIndex = version.indexOf(".")
//    if (firstPointIndex >= 0){
//      //  first point behind content
//      var firstPointBehindContent:String = version.substring(firstPointIndex)
//      // keep sub of point
//      var keepSubOfPoint = if(keepPointNum== -1)  firstPointBehindContent.length() else getSub(firstPointBehindContent,".",keepPointNum)
//      // replace point with blank
//      firstPointBehindContent = firstPointBehindContent.substring(0,keepSubOfPoint).replaceAll("\\.", "").trim
//      if(StringUtils.isNotBlank(firstPointBehindContent)){
//        StringUtils.defaultIfBlank(version.substring(0, firstPointIndex).trim, "0") + "." + firstPointBehindContent
//      }else{
//        StringUtils.defaultIfBlank(version.substring(0, firstPointIndex).trim,"0")
//      }
//    }
//    else version
//  }
//
//
//
//  def main(args: Array[String]): Unit = {
//
////    println(getOmitVersion("12.1"))
////
////    println(getOmitVersion("12"))
////
////    println(getOmitVersion("12.1.2"))
////
////    println(getOmitVersion("12.1.2.43"))
////
////    println(getOmitVersion("12.1.2",-1))
////
////    println(getOmitVersion("12.1.2",1))
////
////    println(getOmitVersion("12.1.2.43",1))
////
////    println( BigDecimal(+0))
////    println( BigDecimal(-.11))
////
////    println(getOmitVersion("12.1.2.43",2))
////
////    println(getOmitVersion("12.123455",2))
////
////    println(getOmitVersion(".12.123455"))
//
////    val spark = SparkSession.builder().appName("ReadOrcFile").master("local[1]").getOrCreate()
////    import spark.implicits._
//
////    var baseDF = spark.read.orc("/Data/part-r-00299-6e480e66-fd9e-49c9-983c-09e07dcc7a2c.orc")
//
//
////    var clazz: Class[AdxRequestStat] = classOf[AdxRequestStat]
////
////    val methods = clazz.getDeclaredMethods.filter { x => x.getName=="statRequestFiled"}
////    methods.foreach {
////      method => {
////       println( method.getReturnType)
////      }
////    }
//
////    println(DateUtils.getWeekOfYear("20190708"))
//
//        println(Math.PI)
//
//
//    //.filter { x => x.getName==job._1}
//
////    baseDF = baseDF.select($"hashedOperaId",$"appVersion",$"appPackageName")
////
////    baseDF.where("hashedOperaId in ('4a02623cb816f697','e822ca98abd1ec07','53f980f744e09275')").show(1000,false)
//
//
//
////    baseDF.where($"countryCode"==="RE" or $"countryCode"==="MK" or $"countryCode"==="MX").select($"countryCode",$"ipAddress.provinceCode").show(10, false)
////
////    baseDF = baseDF.withColumn("firstNotNullProvinceCode",
////      first("ipAddress.provinceCode", ignoreNulls = true)
////        over(Window.partitionBy("countryCode").orderBy($"timeMs").rangeBetween(Window.unboundedPreceding, Window.unboundedFollowing)))
////
////    baseDF = baseDF.withColumn("seqNum", row_number() over (Window.partitionBy( "countryCode").orderBy($"ipAddress.provinceCode"))).filter($"seqNum" === 1)
////
////    baseDF.where($"countryCode"==="RE" or $"countryCode"==="MK" or $"countryCode"==="MX")
////      .select($"countryCode",$"ipAddress.provinceCode",$"firstNotNullProvinceCode").show(1000, false)
////    var aaa = baseDF.select($"ipAddress.province", $"ipAddress.provinceCode", $"ipAddress.city",
////      col("countryCode") as "country",
////      rename("languageCode"),
////      rename("connType"),
////      $"operator", $"hashedOperaId",$"deviceModel",$"osVersion")
////
////    aaa.show(10, false)
////    // 国家
////    var countryUserData = aaa.groupBy("country")day=20190620
////      .agg(
////        countDistinct("hashedOperaId") as "countryNumber"
////      );
//
//    // 省
////    var resultData = countryUserData.join(
////      aaa
////        .groupBy("country", "province")
////        .agg(
////          countDistinct("hashedOperaId") as "provinceNumber"
////        )
////        .withColumn("rank", row_number().over(Window.partitionBy("country").orderBy($"provinceNumber".desc)))
////        .filter($"rank" <= 6),
////      Seq("country")
////      // 处理科学计数法
////    ).withColumn("ratio", ($"provinceNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
////      .withColumn("expression", concat($"provinceNumber", lit("/"), $"countryNumber"))
////      .select(col("country"), lit("state").name("field"), col("province") as "field_value", $"ratio", $"expression",lit("") as "explain")
////
////    // 城市
////    resultData = resultData.union(
////      countryUserData.join(
////        aaa
////          .groupBy("country", "city")
////          .agg(
////            countDistinct("hashedOperaId") as "cityNumber"
////          )
////          .withColumn("rank", row_number().over(Window.partitionBy("country").orderBy($"cityNumber".desc)))
////          .filter($"rank" <= 11),
////        Seq("country")
////      ).withColumn("ratio", ($"cityNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
////        .withColumn("expression", concat($"cityNumber", lit("/"), $"countryNumber"))
////        .select(col("country"), lit("city").name("field"), col("city") as "field_value", $"ratio", $"expression")
////    )
////
////    // 语言
////    resultData = resultData.union(
////      countryUserData.join(
////        aaa
////          .groupBy("country", "language")
////          .agg(
////            countDistinct("hashedOperaId") as "languageNumber"
////          ),
////        Seq("country")
////      ).withColumn("ratio", ($"languageNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
////        .withColumn("expression", concat($"languageNumber", lit("/"), $"countryNumber"))
////        .select(col("country"), lit("language").name("field"), col("language") as "field_value", $"ratio", $"expression")
////    )
////
////    // 网络
////    resultData = resultData.union(
////      countryUserData.join(
////        aaa.groupBy("country", "network")
////          .agg(
////            countDistinct("hashedOperaId") as "networkNumber"
////          ),
////        Seq("country")
////      ).withColumn("ratio", ($"networkNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
////        .withColumn("expression", concat($"networkNumber", lit("/"), $"countryNumber"))
////        .select(col("country"), lit("network").name("field"), col("network") as "field_value", $"ratio", $"expression")
////    )
////
////    // 运营商
////    resultData = resultData.union(
////      countryUserData.join(
////        aaa.groupBy("country", "operator")
////          .agg(
////            countDistinct("hashedOperaId") as "operatorNumber"
////          ),
////        Seq("country")
////      ).withColumn("ratio", ($"operatorNumber" / $"countryNumber").cast(DataTypes.createDecimalType(24, 5)))
////        .withColumn("expression", concat($"operatorNumber", lit("/"), $"countryNumber"))
////        .select(col("country"), lit("operator").name("field"), col("operator") as "field_value", $"ratio", $"expression")
////    )
//
//    // device model
////    resultData = resultData.union(
////      var resultData = countryUserData.join(
////        aaa.withColumn("devicePrice",getPriceIndexByDeviceModel($"deviceModel"))
////          .groupBy("country","devicePrice")
////          .agg(
////            countDistinct("hashedOperaId") as "deviceNumber",
////            concat_ws(",",collect_set($"deviceModel")) as "deviceModel"
////          ),
////        Seq("country")
////      ).withColumn("ratio",($"deviceNumber"/$"countryNumber").cast(DataTypes.createDecimalType(24,5)))
////        .withColumn("expression",concat($"deviceNumber",lit("/"),$"countryNumber"))
////        .select(col("country"),lit("devicePrice").name("field"),col("devicePrice") as "field_value",$"ratio",$"expression",col("deviceModel") as "explain")
////    )
//
////    resultData = resultData.union(
////    var resultData = countryUserData.join(
////      // 根据hashedOperaId、osVersion去重
////      aaa.withColumn("seqNum",row_number() over(Window.partitionBy("hashedOperaId","osVersion").orderBy("hashedOperaId"))).filter($"seqNum" === 1)
////        // 根据osVersion获得osVersionAbove
////        .withColumn("osVersionRange",getOSVersionRange($"osVersion"))
////        // osVersion展开
////        .select(explode(split($"osVersionRange",",")).as("osVersion"),$"country",$"hashedOperaId")
////        // 统计country、osVersion人数
////        .groupBy("country","osVersion")
////        .agg(
////          countDistinct("hashedOperaId") as "osVersionNumber"
////        ),
////      Seq("country")
////    ).withColumn("ratio",($"osVersionNumber"/$"countryNumber").cast(DataTypes.createDecimalType(24,5)))
////      .withColumn("expression",concat($"osVersionNumber",lit("/"),$"countryNumber"))
////      .select(col("country"),lit("osVersion").name("field"),col("osVersion") as "field_value",$"ratio",$"expression",lit("") as "explain")
//    //    )
//
//
////    resultData = resultData.withColumn("test", lit("1")).select(explode(split($"test", ",")).as("test"), $"country", $"field", $"field_value", $"ratio", $"expression",$"convert")
////    //    split(data("b"), ","   explode($"test").as("test")
////    resultData.show(100000000, false)
////
////    println(resultData.getClass)
//
//    //  aaa.select($"country",
//    //    count(collect_set("hashedOperaId")) over (Window.partitionBy("country","province"))
//    //  ).show(1000,false)
//
//  }
//  def conversionUVResult(df: DataFrame, userIdCol: Column): Column =
//    when(df("isConversion") === 1, userIdCol)
//
//  def rename(colName: String): Column = col(colName).as(nameAlias.getOrElse(colName, colName))
//
//
//  val deviceModelPriceIndex:Map[String,Int] = Map[String,Int](
//    "tecno-y2" -> 1,
//    "itel a11" -> 1,
//    "infinix x507" -> 1,
//    "tecno w1" -> 1,
//    "itel a12" -> 1,
//    "fero a4502" -> 1,
//    "itel a16" -> 1,
//    "itel a14" -> 1,
//    "tecno-y3" -> 1,
//    "huawei g750-t01" -> 1,
//    "huawei lua-l01" -> 1,
//    "tecno m3" -> 1,
//    "fero a4001 plus" -> 1,
//    "fero a4001" -> 1,
//    "letv x500" -> 1,
//    "cubot_j3" -> 1,
//    "tecno wx3p" -> 2,
//    "itel p32" -> 2,
//    "infinix-x551" -> 2,
//    "tecno f1" -> 2,
//    "tecno f3" -> 2,
//    "infinix x510" -> 2,
//    "tecno wx3" -> 2,
//    "tecno-w3" -> 2,
//    "tecno w2" -> 2,
//    "itel p31" -> 2,
//    "tecno f2" -> 2,
//    "itel p12" -> 2,
//    "itel a32f" -> 2,
//    "itel s12" -> 2,
//    "itel it1556 plus" -> 2,
//    "tecno p701" -> 2,
//    "itel p13" -> 2,
//    "sm-g532f" -> 2,
//    "itel it1516 plus" -> 2,
//    "infinix x5010" -> 2,
//    "itel p51" -> 2,
//    "nokia 2" -> 2,
//    "itel it1508" -> 2,
//    "itel it1408" -> 2,
//    "gn5001s" -> 2,
//    "itel a31" -> 2,
//    "tecno b1" -> 2,
//    "itel s13" -> 2,
//    "p5 mini" -> 2,
//    "tecno-y6" -> 2,
//    "tecno b1p" -> 2,
//    "tecno lb6" -> 2,
//    "tecno-l8plus" -> 2,
//    "kiicaa power" -> 2,
//    "nokia 2.1" -> 2,
//    "tecno la6" -> 2,
//    "tecno-l8" -> 2,
//    "tecno_w4" -> 2,
//    "tecno w4" -> 2,
//    "p5w" -> 2,
//    "itel a15" -> 2,
//    "itel s11" -> 2,
//    "itel_it1503" -> 2,
//    "nokia 1" -> 2,
//    "tecno f2lte" -> 2,
//    "itel it1409" -> 2,
//    "infinix hot-v2" -> 2,
//    "pace 2 lite" -> 2,
//    "gt-i9060i" -> 2,
//    "vivo x3t" -> 2,
//    "itel s31" -> 2,
//    "tecno-y4" -> 2,
//    "tecno 7c" -> 2,
//    "itel s33" -> 2,
//    "gionee f205 lite" -> 2,
//    "p8m" -> 2,
//    "m2" -> 2,
//    "itel it1507" -> 2,
//    "m6 mini" -> 2,
//    "itel a51" -> 2,
//    "sm-g530h" -> 2,
//    "sm-g531h" -> 2,
//    "sm-j260f" -> 2,
//    "gt-i9082" -> 2,
//    "dp7cpro" -> 2,
//    "gt-i9500" -> 2,
//    "sm-j200h" -> 2,
//    "royale a1" -> 2,
//    "z10" -> 2,
//    "lenovo a1000" -> 2,
//    "sm-j110h" -> 2,
//    "gionee s10 lite" -> 2,
//    "itel it1506" -> 2,
//    "power 3" -> 2,
//    "c8" -> 2,
//    "halo plus" -> 2,
//    "fero l100" -> 2,
//    "power 2" -> 2,
//    "c12 pro" -> 2,
//    "s16" -> 2,
//    "a5500" -> 2,
//    "lg-h811" -> 2,
//    "le x526" -> 2,
//    "moto e (4)" -> 2,
//    "cubot r9" -> 2,
//    "cubot note plus" -> 2,
//    "lex626" -> 2,
//    "halo2 3g" -> 2,
//    "max2_plus_3g" -> 2,
//    "tecno k7" -> 3,
//    "infinix x559c" -> 3,
//    "tecno ka7" -> 3,
//    "tecno la7" -> 3,
//    "infinix x606d" -> 3,
//    "infinix x606c" -> 3,
//    "infinix x559" -> 3,
//    "tecno ka7o" -> 3,
//    "infinix hot 4 lite" -> 3,
//    "infinix hot 4" -> 3,
//    "tecno ca7" -> 3,
//    "infinix x606" -> 3,
//    "tecno ca6" -> 3,
//    "tecno-c8" -> 3,
//    "m5 mini" -> 3,
//    "tecno camon cx" -> 3,
//    "infinix x606b" -> 3,
//    "tecno cx air" -> 3,
//    "tecno k9" -> 3,
//    "infinix hot 4 pro" -> 3,
//    "itel s32" -> 3,
//    "infinix x5515" -> 3,
//    "tecno w5 lite" -> 3,
//    "p8w" -> 3,
//    "f103" -> 3,
//    "m5" -> 3,
//    "f100" -> 3,
//    "tecno cf7" -> 3,
//    "tecno-j8" -> 3,
//    "sm-j610f" -> 3,
//    "gn5001" -> 3,
//    "sm-g900f" -> 3,
//    "sm-j400f" -> 3,
//    "ta-1032" -> 3,
//    "infinix hot 3" -> 3,
//    "m6" -> 3,
//    "ta-1053" -> 3,
//    "tecno-c7" -> 3,
//    "sm-j500h" -> 3,
//    "sm-j415f" -> 3,
//    "htc one_m8" -> 3,
//    "gionee s11 lite" -> 3,
//    "infinix x5514d" -> 3,
//    "a5002" -> 3,
//    "nokia 3.1" -> 3,
//    "tecno k8" -> 3,
//    "sm-g900h" -> 3,
//    "sm-j320fn" -> 3,
//    "royale_x2" -> 3,
//    "tecno dp7cpro" -> 3,
//    "f103 pro" -> 3,
//    "sm-j320h" -> 3,
//    "q10" -> 3,
//    "sm-j410f" -> 3,
//    "huawei tit-u02" -> 3,
//    "moto c" -> 3,
//    "royale y2 lite" -> 3,
//    "nokia 3.1 plus" -> 3,
//    "royale y2" -> 3,
//    "max3_3g" -> 3,
//    "lg-k350" -> 3,
//    "ht50" -> 3,
//    "moto c plus" -> 3,
//    "fero_y1" -> 3,
//    "fero_j1" -> 3,
//    "innjoo 2" -> 3,
//    "infinix x608" -> 4,
//    "infinix x572" -> 4,
//    "infinix x623" -> 4,
//    "tecno l8 lite" -> 4,
//    "infinix x604" -> 4,
//    "tecno la7 pro" -> 4,
//    "infinix x571" -> 4,
//    "infinix note 3" -> 4,
//    "infinix-x600" -> 4,
//    "sm-g570f" -> 4,
//    "sm-t561" -> 4,
//    "tecno-c9" -> 4,
//    "sm-g920f" -> 4,
//    "infinix x622" -> 4,
//    "infinix x605" -> 4,
//    "a1 lite" -> 4,
//    "infinix x573" -> 4,
//    "x1s" -> 4,
//    "sm-j250f" -> 4,
//    "tecno p904" -> 4,
//    "sm-j600f" -> 4,
//    "infinix_x521" -> 4,
//    "tecno cf7k" -> 4,
//    "ta-1021" -> 4,
//    "m6 mirror" -> 4,
//    "sm-j530f" -> 4,
//    "infinix note 2" -> 4,
//    "nokia 5.1 plus" -> 4,
//    "nokia 6.1" -> 4,
//    "nokia 5.1" -> 4,
//    "k6000 plus" -> 4,
//    "tecno l9 plus" -> 5,
//    "sm-g935f" -> 5,
//    "sm-g610f" -> 5,
//    "tecno ca8" -> 5,
//    "sm-j700h" -> 5,
//    "tecno l9" -> 5,
//    "tecno cf8" -> 5,
//    "sm-g925f" -> 5,
//    "sm-j701f" -> 5,
//    "sm-j730f" -> 5,
//    "a1" -> 5,
//    "sm-g930f" -> 5,
//    "s6s" -> 5,
//    "phantom 8" -> 5,
//    "htc one" -> 5,
//    "tecno ax8" -> 5,
//    "nokia 6.1 plus" -> 5,
//    "ta-1004" -> 5,
//    "sm-g955f" -> 6,
//    "sm-g950f" -> 6,
//    "infinix x603" -> 6,
//    "sm-a520f" -> 6,
//    "vivo s7i(t)" -> 6,
//    "sm-g965f" -> 6,
//    "sm-g935t" -> 6,
//    "sm-a750f" -> 6,
//    "gt-i9300" -> 6,
//    "sm-n950f" -> 6,
//    "nokia 7 plus" -> 6,
//    "nokia 7.1" -> 6
//  )
//
//  def getPriceIndexByDeviceModel = udf{
//    (deviceModel: String) => deviceModelPriceIndex.getOrElse(deviceModel.toLowerCase(), 0)
//  }
//
//  def getOSVersionRange = udf{
//    (osVersion: String) => {
//
//      println("--------------------------")
//      println(osVersion)
//      println("===========================")
//
//      var firstPointIndex = osVersion.indexOf(".")
//      println(firstPointIndex)
//      var version = if(firstPointIndex > 0) osVersion.substring(0,firstPointIndex) + "." + osVersion.substring(firstPointIndex).replaceAll("\\.",""); else  osVersion
//      println(version)
//
//      val pattern = Pattern.compile("^[-\\+]?[.\\d]*$")
//      println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//
//      if(pattern.matcher(version).matches()){
//        BigDecimal(version) match {
//          case x if x >= 9.0 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0,5.1,6.0,7.0,7.1,8.0,8.1,9.0"
//          case x if x >= 8.1 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0,5.1,6.0,7.0,7.1,8.0,8.1"
//          case x if x >= 8.0 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0,5.1,6.0,7.0,7.1,8.0"
//          case x if x >= 7.1 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0,5.1,6.0,7.0,7.1"
//          case x if x >= 7.0 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0,5.1,6.0,7.0"
//          case x if x >= 6.0 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0,5.1,6.0"
//          case x if x >= 5.1 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0,5.1"
//          case x if x >= 5.0 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4,5.0"
//          case x if x >= 4.4 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3,4.4"
//          case x if x >= 4.3 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2,4.3"
//          case x if x >= 4.2 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1,4.2"
//          case x if x >= 4.1 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0,4.1"
//          case x if x >= 4.0 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2,4.0"
//          case x if x >= 3.2 => "2.0,2.1,2.2,2.3,3.0,3.1,3.2"
//          case x if x >= 3.1 => "2.0,2.1,2.2,2.3,3.0,3.1"
//          case x if x >= 3.0 => "2.0,2.1,2.2,2.3,3.0"
//          case x if x >= 2.3 => "2.0,2.1,2.2,2.3"
//          case x if x >= 2.2 => "2.0,2.1,2.2"
//          case x if x >= 2.1 => "2.0,2.1"
//          case x if x >= 2.0 => "2.0"
//          case _ => "0"
//        }
//      }else{
//        "-1"
//      }
//
//    }
//  }
//}