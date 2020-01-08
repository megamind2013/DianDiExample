//package annotation
//
//import org.apache.commons.lang.StringUtils
//import org.apache.spark.sql._
//import org.apache.spark.sql.functions._
//
//import scala.annotation.StaticAnnotation
//import scala.reflect.runtime.universe._
//
//class Cycle(cycleType: CycleType*)  extends StaticAnnotation
//
//object CycleType {
//  def apply(cycleName: String, tablename:String,categoryNames:String,projectname:String,path:String ): CycleType = cycleName.toLowerCase match {
//    case "day" => Day(tablename,categoryNames,projectname,path)
//    case "week" => Week(tablename,categoryNames,projectname,path)
//    case "month" => Month(tablename,categoryNames,projectname,path)
//    case "range" => Range(tablename,categoryNames,projectname,path)
//    case _ =>
//      val supported = Seq(
//        "day",
//        "week",
//        "month",
//        "range")
//      throw new IllegalArgumentException(s"Unsupported cycle type '$cycleName'. " +
//        "Supported  cycle names include: " + supported.mkString("'", "', '", "'") + ".")
//  }
//
//  def getTyp(cycleName:String):Type = cycleName.toLowerCase match {
//     case "day" => typeOf[Day]
//     case "week" => typeOf[Week]
//     case "month" => typeOf[Month]
//     case "range" => typeOf[Range]
//     case _ =>
//        val supported = Seq(
//        "day",
//        "week",
//        "month",
//        "range")
//      throw new IllegalArgumentException(s"Unsupported cycle type '$cycleName'. " +
//        "Supported  cycle names include: " + supported.mkString("'", "', '", "'") + ".")
//  }
//}
//
//sealed abstract class CycleType(val categoryNames:String) extends CycleColumn{
//  def id:Int
//  def name:String
//  def tableName:String
//  def projectname:String
//  def path:String
//}
//
//trait CycleColumn{
//  def addColumns(day:String)(inputDF:DataFrame):DataFrame
//  def ifAddToken:Boolean
//  def createToken(spark:SparkSession,day:String,token:String="")
//}
//
//case class Day(tablename:String,override val categoryNames:String="bet",projectname:String="",path:String="")extends CycleType(categoryNames) {
//  def id:Int = 1
//  def name:String = "day"
//  def tableName:String = tablename
//  def projectName:String = projectname
//  def ifAddToken = if(StringUtils.isNotBlank(projectName)) true else false
//  def defaultPath = path
//
//
//  def addColumns(day:String)(inputDF:DataFrame): DataFrame = {
//    inputDF.withColumn("day",lit(day))
//  }
//
//  def createToken(spark:SparkSession,day:String,token:String="") = {
//    if(ifAddToken){
//      var folderPath:String = s"%s/%s/date=%s".format(defaultPath,projectName,day)
//
//      spark.emptyDataFrame
//        .write.mode(SaveMode.Overwrite)
//        // avoid generate success
//        .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//        .save(folderPath)
//
//      if(StringUtils.isNotBlank(token)){
//        var filePath:String = folderPath + "/%s".format(token)
//
//        spark.emptyDataFrame
//          .write.mode(SaveMode.Overwrite)
//          // avoid generate success
//          .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//          .csv(filePath)
//      }
//    }
//  }
//}
//case class Week(tablename:String,override val categoryNames:String="bet",projectname:String="",path:String="") extends CycleType(categoryNames) {
//  def id:Int = 2
//  def name:String = "week"
//  def tableName:String = tablename
//  def projectName:String = projectname
//  def ifAddToken = if(StringUtils.isNotBlank(projectName)) true else false
//  def defaultPath = path
//
//
//  def addColumns(day:String)(inputDF:DataFrame): DataFrame = {
//    val (begin,end)=DateUtils.getWeekBeginDayAndEndDay(day)
//    val year = begin.substring(0,4)
//    inputDF.withColumn("week_index",lit(DateUtils.getWeekOfYearWithYear(begin)))
//      .withColumn("begin_day",lit(begin))
//      .withColumn("end_day",lit(end))
//  }
//
//  def createToken(spark:SparkSession,day:String,token:String="") = {
//    if(ifAddToken){
//      var folderPath:String = s"%s/%s/date=%s".format(defaultPath,projectName,day)
//
//      spark.emptyDataFrame
//        .write.mode(SaveMode.Overwrite)
//        // avoid generate success
//        .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//        .save(folderPath)
//
//      if(StringUtils.isNotBlank(token)){
//        var filePath:String = folderPath + "/%s".format(token)
//
//        spark.emptyDataFrame
//          .write.mode(SaveMode.Overwrite)
//          // avoid generate success
//          .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//          .csv(filePath)
//      }
//    }
//  }
//}
//case class Month(tablename:String,override val categoryNames:String= "bet",projectname:String="",path:String="") extends CycleType(categoryNames) {
//  def id:Int = 3
//  def name:String = "month"
//  def tableName:String = tablename
//  def projectName:String = projectname
//  def ifAddToken = if(StringUtils.isNotBlank(projectName)) true else false
//  def defaultPath = path
//
//
//  def addColumns(day:String)(inputDF:DataFrame): DataFrame = {
//    inputDF.withColumn("month",lit(day.substring(0,6)))
//  }
//
//  def createToken(spark:SparkSession,day:String,token:String="") = {
//    if(ifAddToken){
//      var folderPath:String = s"%s/%s/date=%s".format(defaultPath,projectName,day)
//
//      spark.emptyDataFrame
//        .write.mode(SaveMode.Overwrite)
//        // avoid generate success
//        .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//        .save(folderPath)
//
//      if(StringUtils.isNotBlank(token)){
//        var filePath:String = folderPath + "/%s".format(token)
//
//        spark.emptyDataFrame
//          .write.mode(SaveMode.Overwrite)
//          // avoid generate success
//          .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//          .csv(filePath)
//      }
//    }
//  }
//}
//case class Range(tablename:String,override val categoryNames:String="bet",projectname:String="",path:String="")extends CycleType(categoryNames) {
//  def id:Int = 4
//  def name:String = "range"
//  def tableName:String = tablename
//  def projectName:String = projectname
//  def ifAddToken = if(StringUtils.isNotBlank(projectName)) true else false
//  def defaultPath = path
//
//  def addColumns(day:String)(inputDF:DataFrame): DataFrame = {
//    inputDF.withColumn("day",lit(day))
//  }
//
//  def createToken(spark:SparkSession,day:String,token:String="") = {
//    if(ifAddToken){
//      var folderPath:String = s"%s/%s/date=%s".format(defaultPath,projectName,day)
//
//      spark.emptyDataFrame
//        .write.mode(SaveMode.Overwrite)
//        // avoid generate success
//        .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//        .save(folderPath)
//
//      if(StringUtils.isNotBlank(token)){
//        var filePath:String = folderPath + "/%s".format(token)
//
//        spark.emptyDataFrame
//          .write.mode(SaveMode.Overwrite)
//          // avoid generate success
//          .option("mapreduce.fileoutputcommitter.marksuccessfuljobs","false")
//          .csv(filePath)
//      }
//    }
//  }
//}
