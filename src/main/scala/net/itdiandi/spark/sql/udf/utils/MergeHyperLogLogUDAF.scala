package net.itdiandi.spark.sql.udf.utils

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{BinaryType, _}


class MergeHyperLogLogUDAF extends UserDefinedAggregateFunction{
  override def inputSchema: StructType =
    new StructType().add("hll",BinaryType)
  override def bufferSchema: StructType =
    new StructType().add("hll",BinaryType)
  override def dataType: DataType = DataTypes.createStructType(
    Array(StructField("count",DataTypes.LongType),StructField("hll",BinaryType)))

  override def deterministic: Boolean = true
  override def initialize(buffer: MutableAggregationBuffer): Unit =
    buffer.update(0,null)

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    var  bufferHll:HyperLogLogPlus = null
    var  inputHll:HyperLogLogPlus = null
    if(buffer(0) != null){
      bufferHll = HyperLogLogPlus.Builder.build(buffer(0).asInstanceOf[Array[Byte]])
    }
    if(input(0) != null){
      inputHll = HyperLogLogPlus.Builder.build(input(0).asInstanceOf[Array[Byte]])
    }

    buffer(0) = if(bufferHll==null && inputHll==null) null
    else if(inputHll==null) bufferHll.getBytes
    else if(bufferHll==null) inputHll.getBytes
    else {bufferHll.addAll(inputHll)
      bufferHll.getBytes
    }
  }

  override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    var  hll1:HyperLogLogPlus = null
    var  hll2:HyperLogLogPlus = null
    if(buffer1(0) != null){
      hll1 = HyperLogLogPlus.Builder.build(buffer1(0).asInstanceOf[Array[Byte]])
    }
    if(buffer2(0) != null){
      hll2 = HyperLogLogPlus.Builder.build(buffer2(0).asInstanceOf[Array[Byte]])
    }

    buffer1(0) = if(hll1==null && hll2==null) null
    else if(hll2==null) hll1.getBytes
    else if(hll1==null) hll2.getBytes
    else {hll1.addAll(hll2)
      hll1.getBytes
    }
  }
  override def evaluate(buffer: Row): Any = {
    if(buffer(0) == null) {
      (0,null)
    }else{
      val hll = HyperLogLogPlus.Builder.build(buffer(0).asInstanceOf[Array[Byte]])
      (hll.cardinality(),hll.getBytes)
    }
  }


}

