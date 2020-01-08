//package annotation
//
//import scala.reflect.runtime.universe._
//
//object Stat {
//  def main(args: Array[String]): Unit = {
//    /**
//      * cycleName: day  week  month  range
//      *
//      * adx_pixel_event 20191010  range  official --job_names=statGpsDensity --begin_day=20191001
//      */
//
//    var cycleName = "day"
//
//    var typ = typeOf[PixelStat]
//    println(typ)
//    var decls = typ.decls.filter {
//      x => x.isMethod && x.asMethod.annotations.exists(
//        _.tree.tpe =:= typeOf[Cycle]
//      ) && x.asMethod.annotations.find(
//        _.tree.tpe =:= typeOf[Cycle]
//      ).get.tree.children.exists {
//        _.tpe == CycleType.getTyp(cycleName)
//      }
//    }
//    println(decls)
//
//  }
//
//  def getCyclAnnnotationData(cycleName: String, tree: Tree) = {
//    var list = tree.children.map(_.toString().replaceAll("\"","")).filter(!_.contains("."))
//
//    var tablename = list(0)
//    var categoryDBs = list(1)
//    var projectname = if(list.size>2) list(2) else ""
//    var path = if(list.size>3) list(3) else "/home/adx/token/export_data"
//
//    CycleType(cycleName, tablename, categoryDBs,projectname,path)
//  }
//}
