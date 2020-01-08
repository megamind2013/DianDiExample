package net.itdiandi.utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}
import net.itdiandi.spark.sql.udf.roaring.WrapperRoaringBitMap

object BitmapUtil {
  def deserializeToBitmap(a:Array[Byte]): WrapperRoaringBitMap = {
    val wrr1 = new WrapperRoaringBitMap()
    val bis = new ByteArrayInputStream(a)
    val dis = new DataInputStream(bis)
    wrr1.deserialize(dis)
    wrr1
  }

  def serializeToByteArray(wrr:WrapperRoaringBitMap)={
    wrr.runOptimize()
    val bos = new ByteArrayOutputStream()
    val out = new DataOutputStream(bos)
    wrr.serialize(out)
    bos.toByteArray
  }

}