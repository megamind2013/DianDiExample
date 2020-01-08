package net.itdiandi.spark.sql.udf.udaf

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._

class UserDefinedAggregateFunctionHyperLogLogExample(relativeSD: Double=0.05) extends UserDefinedAggregateFunction{
  override def inputSchema: StructType = new StructType().add("id",DataTypes.StringType)


  override def bufferSchema: StructType = new StructType().add("hll",BinaryType)

  override def dataType: DataType = DataTypes.createStructType(
    Array(StructField("count",DataTypes.LongType),StructField("hll",BinaryType)))

  override def deterministic: Boolean = true

  val p:Int = Math.ceil(2.0d * Math.log(1.106d / relativeSD) / Math.log(2.0d)).toInt

  override def initialize(buffer: MutableAggregationBuffer): Unit = buffer.update(0,new HyperLogLogPlus(p).getBytes)

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    val in = input(0)
    val hll = HyperLogLogPlus.Builder.build(buffer(0).asInstanceOf[Array[Byte]])
    hll.offer(in)
    buffer(0) = hll.getBytes
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    val hll1 =  HyperLogLogPlus.Builder.build(buffer1(0).asInstanceOf[Array[Byte]])
    val hll2 = HyperLogLogPlus.Builder.build(buffer2(0).asInstanceOf[Array[Byte]])
    hll1.addAll(hll2)

    buffer1(0) = hll1.getBytes
  }
  override def evaluate(buffer: Row): Any = {
    val hll = HyperLogLogPlus.Builder.build(buffer(0).asInstanceOf[Array[Byte]])
    (hll.cardinality(),hll.getBytes)
  }

}