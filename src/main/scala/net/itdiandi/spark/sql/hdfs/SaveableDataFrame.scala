package net.itdiandi.sql.hdfs

import org.apache.spark.sql.{DataFrame, SaveMode}


trait Saveable{
  def save
}

class HdfsSinkDataFrame(val df:DataFrame,val etlOutPath:String,val repartitionNum:Int=1,val format:String="orc",val compression:String="none",val prop:Map[String,String]=Map()) extends Saveable{
  def save= {
    prop.foldLeft(df.repartition(repartitionNum).write
      .mode(SaveMode.Overwrite).format(format).option("compression", compression)
    )((newDF,item)=>{ newDF.option(item._1,item._2); newDF })
     .save(etlOutPath)
  }
}
class BackUpSinkDataFrame(val df:DataFrame,val etlOutPath:String,val repartitionNum:Int=1,val format:String="orc",val compression:String="none",val prop:Map[String,String]=Map()) extends Saveable {
  def save = {
    prop.foldLeft(df.repartition(repartitionNum).write.partitionBy("hour")
      .mode(SaveMode.Overwrite).format(format).option("compression", compression)
    )((newDF, item) => {
      newDF.option(item._1, item._2); newDF
    })
      .save(etlOutPath)
  }
}
