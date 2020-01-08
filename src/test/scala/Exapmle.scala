import org.apache.spark.sql.SparkSession

object Exapmle {
  /** paramChecked */
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Example").master("local[1]").getOrCreate()

    spark.read.json("student.json").createOrReplaceTempView("student")

    spark.sql("select name from student where age > 18").show()
  }

  def isNumeric(str:String): Boolean = {
    if (str == null) {
      return false;
    }

    for (ch <- str) {
      if (!(ch == '.' || ( ch >= '0' && ch <= '9'))) {
        return false
      }
    }
    return true
  }
}