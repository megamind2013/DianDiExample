package annotation

import java.text.SimpleDateFormat
import java.util.Calendar

import scala.collection.mutable.ArrayBuffer

object DateUtils {
  val sdf = new SimpleDateFormat("yyyyMMdd")
  val sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def getWeekOfYear(day:String):Int={
    val cal = getCalendarByDate(day)
    val timeInMillis = cal.getTimeInMillis
    val year = cal.get(Calendar.YEAR)
    
    cal.setTime(sdf.parse(s"${year}0101"))
    val firstDayTimeInMillis = cal.getTimeInMillis
    val firstDayWeek = getDayOfWeek(cal)
    
    val days = ((timeInMillis-firstDayTimeInMillis)/(24 * 60 * 60 * 1000)).toInt
    if(firstDayWeek == 1){
      days/7+1
    }else{
      (firstDayWeek-1+days)/7
    }
     
  }

  
  def getWeekBeginDayAndEndDay(day:String):(String,String) = {
    val cal = getCalendarByDate(day)
    val week = getDayOfWeek(cal)
    cal.add(Calendar.DATE,-(week-1))
    val begin = sdf.format(cal.getTime)
    cal.add(Calendar.DATE,6)
    val end = sdf.format(cal.getTime)
    (begin,end)
  }
  
  def getRangeDays(beginDay:String,endDay:String):Array[String] = {
     val beginCal = getCalendarByDate(beginDay)
     var beginDate =  beginCal.getTime
     val endCal = getCalendarByDate(endDay)
     val endDate =  endCal.getTime 
     val rangDays = ArrayBuffer[String]()
     while(beginDate.compareTo(endDate) <= 0){
       rangDays += sdf.format(beginCal.getTime)
       beginCal.add(Calendar.DATE, 1)
       beginDate = beginCal.getTime
     }
     rangDays.toArray
  }

  def getWeekOfYearWithYear(day:String):String = {
     val week = getWeekOfYear(day)
     return day.substring(0,4)+ (if(week<10) s"0${week}"else s"${week}")
  }
  
    /**
     * 得到指定日期是星期几
     *
     * @param c 日期
     * @return
     */
    def getDayOfWeek(c:Calendar):Int ={
        var week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 0) {
            week = 7
        }
        week
    }
    
    def getCalendarByDate(day:String,format:String="yyyyMMdd"):Calendar = {
       val cal = Calendar.getInstance
       cal.setTime(new SimpleDateFormat(format).parse(day))
       cal
    }
    def getNDays(day:String,format:String="yyyyMMdd",offset:Int = -1):String={
      val cal = getCalendarByDate(day,format)
      cal.add(Calendar.DATE, offset)
      sdf.format(cal.getTime)
    }

    def getNDaySecondTime(day:String,format:String = "yyyyMMdd",offset: Int = -1):Long = {
      val cal = getCalendarByDate(day, format)
      cal.add(Calendar.DATE, offset)
      cal.getTimeInMillis/1000
    }
    def getNDayMilliSecondTime(day:String,format:String = "yyyyMMdd",offset: Int = -1):Long = {
      val cal = getCalendarByDate(day, format)
      cal.add(Calendar.DATE, offset)
      cal.getTimeInMillis
    }

    def getDayNanoTime(day:String,format:String = "yyyyMMdd"):Long = {
      val cal = getCalendarByDate(day, format)
      cal.getTimeInMillis*1000*1000

    }


   def getDateDiff(day1:String,day2:String,format:String="yyyyMMdd"):Int ={
      val cal1 = getCalendarByDate(day1,format)
      val cal2 = getCalendarByDate(day2,format)
      
      ((cal2.getTimeInMillis -cal1.getTimeInMillis)/(24 * 60 * 60 * 1000)).toInt

    } 
    def getDateStr(timestamp:Long):String = {
        val cal = Calendar.getInstance
         cal.setTimeInMillis(timestamp)
         sdf1.format(cal.getTime)
    }

  def getDetailTuple(timestamp:Long):(String,String,String,String) = {
    val cal = Calendar.getInstance
    cal.setTimeInMillis(timestamp)
    val day = sdf.format(cal.getTime)
    val hour = cal.get(Calendar.HOUR_OF_DAY).toString
    val minute = cal.get(Calendar.MINUTE).toString
    val second = cal.get(Calendar.SECOND).toString
    (day,hour,minute,second)
  }

  def main(args: Array[String]): Unit = {
    for(i<- 2000 to 2020){
      if(getDayOfWeek(getCalendarByDate(s"${i}0101")) == 1)
          println(i,getWeekOfYearWithYear(s"${i}0101"))
    }
  }
}