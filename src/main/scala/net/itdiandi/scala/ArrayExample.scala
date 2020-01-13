package net.itdiandi.scala

import scala.collection.mutable.ArrayBuffer

object ArrayExample {
  def main(args: Array[String]): Unit = {
    var day :String = "20200101"
    var startDay:Int = 20190101
    val results=ArrayBuffer[String]()
    var warehousePath:String = "/usr/warehouse/%s"
    var project:String = "adx_request"
    for (d <- startDay to Integer.parseInt(day)) {
      println("---------------")
      results += warehousePath.format(project) + s"/*/*/*/day=${day}/"
    }
    println(results.mkString("\n"))
  }
}
