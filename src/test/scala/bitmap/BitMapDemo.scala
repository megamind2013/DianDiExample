//package bitmap
//
//object BitMapDemo {
//
//  /**
//    * 使用自定义函数解析bitmap
//    *
//    * @param sparkSession
//    * @return
//    */
//  private static void udafBitmap(SparkSession sparkSession) {
//    try {
//      Properties prop = PropUtil.loadProp(DB_PHOENIX_CONF_FILE);
//      // JDBC连接属性
//      Properties connProp = new Properties();
//      connProp.put("driver", prop.getProperty(DB_PHOENIX_DRIVER));
//      connProp.put("user", prop.getProperty(DB_PHOENIX_USER));
//      connProp.put("password", prop.getProperty(DB_PHOENIX_PASS));
//      connProp.put("fetchsize", prop.getProperty(DB_PHOENIX_FETCHSIZE));
//      // 注册自定义聚合函数
//      sparkSession.udf().register("bitmap",new UdafBitMap());
//      sparkSession
//        .read()
//        .jdbc(prop.getProperty(DB_PHOENIX_URL), "test_binary", connProp)
//        // sql中必须使用global_temp.表名，否则找不到
//        .createOrReplaceGlobalTempView("test_binary");
//      //sparkSession.sql("select YEAR(TO_DATE(date)) year,bitmap(dist_mem) memNum from global_temp.test_binary group by YEAR(TO_DATE(date))").show();
//      sparkSession.sql("select date,bitmap(dist_mem) memNum from global_temp.test_binary group by date").show();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//}
