package net.itdiandi.spark.sql.udf.roaring

import org.apache.spark.sql.types.{BitMapType, SQLUserDefinedType}
import org.roaringbitmap.longlong.Roaring64NavigableMap

@SQLUserDefinedType(udt = classOf[BitMapType])
class WrapperRoaringBitMap extends  Roaring64NavigableMap