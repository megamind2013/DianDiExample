package org.apache.spark.sql.types

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}

import net.itdiandi.spark.sql.udf.roaring.WrapperRoaringBitMap

class BitMapType extends UserDefinedType[WrapperRoaringBitMap]{

  override def sqlType: DataType = BinaryType

  override def serialize(obj: WrapperRoaringBitMap): Array[Byte] = {
    obj.runOptimize()
    val bos = new ByteArrayOutputStream()
    val out = new DataOutputStream(bos)
    obj.serialize(out)
    bos.toByteArray
  }

  override def deserialize(datum: Any): WrapperRoaringBitMap = {
    val bis = new ByteArrayInputStream(datum.asInstanceOf[Array[Byte]])
    val dis = new DataInputStream(bis)
    val roaringBitmap = new WrapperRoaringBitMap
    roaringBitmap.deserialize(dis)
    roaringBitmap
  }

  override def userClass: Class[WrapperRoaringBitMap] = classOf[WrapperRoaringBitMap]
}

case object  BitMapType extends  BitMapType

