package net.itdiandi.sql.udf.udaf

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types._
import net.itdiandi.spark.sql.udf.roaring._
import net.itdiandi.utils.BitmapUtil._

object UserDefinedAggregateFunctionBitMapExample extends UserDefinedAggregateFunction {

  // 聚合函数的输入数据结构
//  override def inputSchema: StructType = StructType(StructField("id", LongType) :: Nil)
  override def inputSchema: StructType = new StructType().add("id",LongType)

  // 缓存区数据结构
  // override def bufferSchema: StructType = StructType(StructField("bitmap", BitMapType) :: Nil)
  override def bufferSchema: StructType = new StructType().add("bitmap",BitMapType)

  // 聚合函数返回值数据结构
//  override def dataType: DataType = DoubleType
   override def dataType: DataType = DataTypes.createStructType(Array(StructField("count",DataTypes.LongType),StructField("bitmap",DataTypes.BinaryType)))

  // 聚合函数是否是幂等的，即相同输入是否总是能得到相同输出
  override def deterministic: Boolean = true

  // 初始化缓冲区
  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer.update(0,new WrapperRoaringBitMap())
  }

  // 给聚合函数传入一条新数据进行处理
  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    val in = input(0)
    // TODO input must be Long/Int Type
    val inLong = in.asInstanceOf[Long]
    val roaringBitMap = buffer(0).asInstanceOf[WrapperRoaringBitMap]
    roaringBitMap.add(inLong)
    buffer(0) = roaringBitMap
  }

  // 合并聚合函数缓冲区
  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    val roaringBitMap1 = buffer1(0).asInstanceOf[WrapperRoaringBitMap]
    val inputBuffer2 = buffer2(0)

    if (inputBuffer2 != null) {
      val roaringBitmap2 = inputBuffer2.asInstanceOf[WrapperRoaringBitMap]
      roaringBitMap1.or(roaringBitmap2)
    }
    buffer1(0) = roaringBitMap1
  }

  // 计算最终结果
  override def evaluate(buffer: Row): Any = {
    //根据Buffer计算结果
    val input = buffer.get(0).asInstanceOf[WrapperRoaringBitMap]
    val inBytes: Array[Byte] = serializeToByteArray(input)
    (input.getLongCardinality,inBytes)
  }
}