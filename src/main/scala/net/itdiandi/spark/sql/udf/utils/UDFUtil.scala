package net.itdiandi.spark.sql.udf.utils

import net.itdiandi.spark.sql.udf.roaring.WrapperRoaringBitMap
import net.itdiandi.utils.BitmapUtil.{deserializeToBitmap, serializeToByteArray}
import org.apache.spark.sql.functions

object UDFUtil {
  val bitMapCombiner = functions.udf((a:Array[Byte],b:Array[Byte])=>{
    val roaringBitmap = new WrapperRoaringBitMap
    if (a != null) {
      val wrr1: WrapperRoaringBitMap = deserializeToBitmap(a)
      roaringBitmap.or(wrr1)
    }
    if (b != null) {
      val wrr2: WrapperRoaringBitMap = deserializeToBitmap(b)
      roaringBitmap.or(wrr2)
    }
    val bitmapBytes: Array[Byte] = serializeToByteArray(roaringBitmap)
    (roaringBitmap.getLongCardinality, bitmapBytes)
  })

}
