package net.itdiandi.spark.sql.hdfs
import scala.collection.mutable.ListBuffer
object RegularReadHDFS {
  def main(args: Array[String]): Unit = {
    val warehousePath:String = "/user/sdev/liujichao/*/*/*/day="
    val project = "adx_request"

    var day="20200101"
    var prefixString = new StringBuilder
    prefixString.append(day.substring(0,2))

    var intArray:Array[Int] = day.substring(prefixString.length).toCharArray.map(f => f.toInt-48)

    var list = new ListBuffer[String]()
    for (i <- intArray){
      for (e <- 0 until i)
        list += prefixString.toString()+e+"*"
      prefixString.append(i)
    }
    list += prefixString.toString()

    println(warehousePath+list.mkString("{",",","}"))

//
//  生成下面这种格式，hdfs使用这种格式访问有问题、spark-shell使用这种格式访问没问题，具体问题描述见https://myclusterbox.com/view/1457
// /user/sdev/liujichao/*/*/*/day={200*,2010*,2011*,2012*,2013*,2014*,2015*,2016*,2017*,2018*,201900*,201901*,20190200*,20190201*,20190202}
//



    prefixString = new StringBuilder
    prefixString.append(day.substring(0,2))

    intArray = day.substring(prefixString.length).toCharArray.map(f => f.toInt-48)

    list = new ListBuffer[String]()
    for (i <- intArray){
      list.append((0 to i).mkString("[","","]"))
    }
    println(warehousePath+prefixString.toString+list.mkString(""))

//
//    println(result)

  }
}
