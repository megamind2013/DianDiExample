package net.itdiandi.scala.list

object CaseClassToList {
  def coToMap(cc: AnyRef) =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        a + (f.getName -> f.get(cc))
    }

  // Usage

  case class Column(name: String,
                    typeCode: Int,
                    typeName: String,
                    size: Int = 0)


  def ccToList(cc: Class[_]) =
    (List[Any]() /: cc.getDeclaredFields) {
      (a, f) =>
        f.setAccessible(true)
        f.getName :: a
    }

  def main(args: Array[String]): Unit = {
    val c1 = Column("id", 0, "INT", 11)
    c1.getClass
    println(coToMap(c1))
    val c2 = Column("id", 0, "INT")
    println(coToMap(c1))

    println(ccToList(classOf[Column]))
    // List(size, typeName, typeCode, name)
  }

}
