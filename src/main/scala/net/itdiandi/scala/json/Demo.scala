package net.itdiandi.scala.json

import com.alibaba.fastjson.JSON

object Demo {
  def main(args: Array[String]): Unit = {
    var inputJson = "{\"utmSource\":\"opera\",\"utmMedium\":\"\",\"utmCampaign\":\"\",\"utmTerm\":\"\",\"utmContent\":\"\",\"clickId\":\"MDAwMDIyNDI3OWMxY2FhN2Q0MCxzMTE5ODI4MjUwMTY5NixhMjMxNTc5ODYwNzA0MCxtMjMxNTc5ODYwNzA0MSxjNWM3Yjc3MWQzNjMwODBmLE5HLHBoYXNlPTI7eyI0NTUwIjoiMTIwNzkifSwzNTk4M2UxOWEyZWZmZmYz\",\"ipAddress\":\"105.112.105.97\",\"networkUserId\":\"a5969d65-06fe-4f51-91b6-88566a30e811\",\"pixelTrackerId\":\"sme2234018687296_st2234127740160\",\"seId\":\"0000224279c1caa7d40\",\"userId\":\"c5c7b771d363080f\",\"country\":\"NG\",\"sid\":\"s1198282501696\",\"trafficSource\":0,\"appId\":\"app496319027008\",\"publisherId\":\"pub236088034304\",\"mid\":\"m2315798607041\",\"aid\":\"a2315798607040\",\"cm\":2,\"adBeginTimeMs\":1578480093000,\"adEndTimeMs\":1580428799000,\"oid\":\"o2267070124992\",\"advertiserId\":\"sme2234018687296\",\"agencyId\":\"agency1600482249024\",\"advIndustry\":\"MedicalHealth_HealthCareProducts\",\"phase\":2,\"eventName\":\"pageview\",\"eventData\":\"null\",\"payout\":\"0\",\"isConversion\":0,\"tsMs\":1583127941737,\"siteId\":\"st2234127740160\",\"siteAdvId\":\"sme2234018687296\",\"pageSizeY\":494,\"viewMaxY\":454,\"clientTsMs\":1579083669932,\"pxSeId\":\"93107de5-0253-45af-863f-b45d114b8ae7\",\"label\":\"\",\"ua\":\"\",\"test\":0}"
    val jsonObj = JSON.parseObject(inputJson)

    println(getStringVal(jsonObj.getString("clickId")))
  }

  def getStringVal(input: Any, defaultVal: String = "", maxLimit: Int = -1, doubleToInt: Boolean = true): String = {
    if (input == null || input == None) {
      defaultVal
    } else {
      try {

        input match {
          case _: Double if doubleToInt => input.asInstanceOf[Double].toLong.toString
          case _: Option[Any] => input.asInstanceOf[Option[Any]].get.toString()
          case _ => if (maxLimit <= 0 || input.toString.length <= maxLimit) input.toString else input.toString.substring(0, maxLimit)
        }

      } catch {
        case ex: Exception => {
          ex.printStackTrace()
          defaultVal
        }
      }
    }
  }

}
