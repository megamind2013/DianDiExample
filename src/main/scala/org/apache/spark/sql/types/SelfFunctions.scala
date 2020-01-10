package org.apache.spark.sql

import org.apache.spark.sql.catalyst.{InternalRow}
import org.apache.spark.sql.catalyst.expressions._
import org.apache.spark.sql.catalyst.expressions.aggregate.{ImperativeAggregate, Kurtosis}
import org.apache.spark.sql.types._
import org.roaringbitmap.longlong.Roaring64NavigableMap


object SelfFunctions {
  def roaringBitMap(e: Column): Column = new Column(RoaringBitMapFunction(e.expr))

}

case class RoaringBitMapFunction(child: Expression,
                                 mutableAggBufferOffset: Int = 0,
                                 inputAggBufferOffset: Int = 0)extends ImperativeAggregate {


  override def children: Seq[Expression] = Seq(child)

  override def prettyName: String = "count_bitmap"

  override def withNewMutableAggBufferOffset(newMutableAggBufferOffset: Int): ImperativeAggregate =
    copy(mutableAggBufferOffset = newMutableAggBufferOffset)

  override val inputAggBufferAttributes: Seq[AttributeReference] = {
    println(aggBufferAttributes)
    aggBufferAttributes.map(_.newInstance())

  }

  /** Allocate enough words to store all registers. */
  override val aggBufferAttributes: Seq[AttributeReference] = Seq.tabulate(2) { i =>
    AttributeReference(s"MS[$i]", BitMapType)()
  }
  override def initialize(buffer: MutableRow): Unit = {
    buffer.update(0, new Roaring64NavigableMap)
  }

  override def withNewInputAggBufferOffset(newInputAggBufferOffset: Int): ImperativeAggregate =
    copy(inputAggBufferOffset = newInputAggBufferOffset)

  override def nullable: Boolean = false

  override def inputTypes: Seq[LongType] = Seq(LongType)

  override def dataType: DataType = DataTypes.createStructType(
    Array(StructField("count",DataTypes.LongType),StructField("bitmap",BitMapType)))

  override def aggBufferSchema: StructType = new StructType().add("bitmap", BitMapType)

  override def update(buffer: MutableRow, input: InternalRow): Unit = {
    val v = child.eval(input)
    if (v != null) {
      val bitMap = buffer.get(0, BitMapType).asInstanceOf[Roaring64NavigableMap]
      bitMap.addLong(v.asInstanceOf[Long])
      buffer(0) = bitMap
    }
  }

  override def merge(buffer1: MutableRow, buffer2: InternalRow): Unit = {
    val bitMap1 = buffer1.get(0, BitMapType).asInstanceOf[Roaring64NavigableMap]

    if (buffer2.get(0, BitMapType) != null) {
      val bitMap2 = buffer2.get(0, BitMapType).asInstanceOf[Roaring64NavigableMap]
      bitMap1.or(bitMap2)
    }

    buffer1(0) = bitMap1

  }

  override def eval(buffer: InternalRow): Any = {
    val bitMap = buffer.get(0,BitMapType).asInstanceOf[Roaring64NavigableMap]
    (bitMap,bitMap.getLongCardinality)
  }
}