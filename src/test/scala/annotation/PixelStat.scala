//package annotation
//
//import com.opera.adx.domain.StatContext
//import org.apache.spark.sql._
//
//class PixelStat(statContext: StatContext) {
//  val spark = statContext.spark
//  import spark.implicits._
//
//  var baseDF = statContext.baseDF.select($"test",$"eventData",$"source",$"phase").cache()
//
//  /**
//    * 下面这几个方法主要为测试注解用的，没什么实际意义
//    */
//  @Cycle(Month("r_day_stat_pixel_event","test,official"))
//  def statPixelEvent(insertToDB:DataFrame=>Unit) = {
//    print("-------statPixelEvent---------")
//    insertToDB(baseDF)
//  }
//
//  @Cycle(Week("r_day_stat_pixel_web_event","test,official"))
//  def statPixelWebEvent(insertToDB:DataFrame=>Unit) = {
//    println("----------statPixelWebEvent------------")
//    insertToDB(baseDF)
//  }
//
//  @Cycle(Day("r_day_stat_pixel_site_event","test,official"))
//  def statPixelSiteEvent(insertToDB:DataFrame=>Unit) = {
//    println("---------------statPixelSiteEvent------------------")
//    insertToDB(baseDF)
//  }
//
//  @Cycle(Range("r_day_stat_pixel_dim_event","test,official"))
//  def statPixelDimEvent(insertToDB:DataFrame=>Unit) = {
//    println("------statPixelDimEvent---------")
//    insertToDB(baseDF)
//  }
//
//
//}
