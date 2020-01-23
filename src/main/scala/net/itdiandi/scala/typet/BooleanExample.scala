package net.itdiandi.scala.typet

object BooleanExample {
  def main(args: Array[String]): Unit = {
    // 将从获取到参数只转换boolean
//    var extraData:Map[String,String]=Map("save.hive"->"true")
    var extraData:Map[String,String]=Map("save.hive"->"true")

    val saveHive:Boolean = extraData.getOrElse("save.hive","false").toBoolean
    println(saveHive)
  }
}
