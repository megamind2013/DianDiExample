package net.itdiandi.spark.sql.udf.roaring

import java.io.IOException

import com.esotericsoftware.kryo.io.{Input, KryoDataInput, KryoDataOutput, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import org.roaringbitmap.RoaringBitmap

class RoaringSerializer  extends  Serializer[RoaringBitmap]{

  override def write(kryo:Kryo,output:Output, bitmap:RoaringBitmap) ={

    try
      bitmap.serialize(new KryoDataOutput(output))
    catch {
      case e: IOException =>
        e.printStackTrace()
        throw new RuntimeException
    }
  }
  override def read(kryo: Kryo, input: Input, clazz: Class[RoaringBitmap]): RoaringBitmap = {
    val bitmap = new RoaringBitmap
    try
      bitmap.deserialize(new KryoDataInput(input))
    catch {
      case e: IOException =>
        e.printStackTrace()
        throw new RuntimeException
    }
    bitmap
  }
}
