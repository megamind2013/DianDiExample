import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

object MultiLineToOneLine {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("ReadOrcFile").master("local[1]").getOrCreate()
    import spark.implicits._

    val df=spark.sparkContext.parallelize(Array(
      (15,399,2),
      (15,1401,5),
      (15,1608,4),
      (15,20,4),
      (18,100,3),
      (18,1401,3),
      (18,399,1)
    )).toDF("userID","movieID","rating")

    /** pivot 多行转多列*/
    val resultDF = df
      .groupBy($"userID")
      .pivot("movieID")
      .sum("rating")
      //.na.fill(0)

    /**结果*/
    resultDF.show(false)



    df.createOrReplaceTempView("user")
    val sql_content = "select * from user pivot (sum(`rating`) for `userID` in (15,18))"
    var df_pivot = spark.sql(sql_content)
    df_pivot.show()
  }
}