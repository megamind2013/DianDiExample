//package scala
//
//import java.util.Properties
//
//import com.opera.adx.annotations.ReflectionTools._
//import com.opera.adx.domain.{StatContext, Transformer}
//import com.opera.adx.metrics.{CommonMetrics, InfluxMetrics, Job}
//import com.opera.adx.utils.{CategoryDB, DBUtils, DateUtils, Tools}
//import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
//
//object Annotate {
//  /** paramChecked */
//  def main(args: Array[String]): Unit = {
////    val spark = SparkSession.builder().appName("Annotate").master("local[1]").getOrCreate()
//
//      print(((-1).toString).getClass.getSimpleName)
//
////    println(isNumeric("12s"))
//
//  }
//
//  def isNumeric(str:String): Boolean = {
//    if (str == null) {
//      return false;
//    }
//
//    for (ch <- str) {
//      if (!(ch == '.' || ( ch >= '0' && ch <= '9'))) {
//        return false
//      }
//    }
//    return true
//  }
//}